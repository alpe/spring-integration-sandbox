/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.sqs;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.integration.transformer.PayloadDeserializingTransformer;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * <p>
 * Inbound message gateway to poll a Amazon SQS hosted queue.
 * </p>
 * <p>
 * With transaction support enabled (default) the message receipt is confirmed
 * at transaction commit only. With a rollback the message can be polled again
 * (after visibilityTimeout).
 * </p>
 * 
 * @author Alex Peters
 * 
 */
public class SQSInboundGateway extends AbstractSQSGateway implements MessageSource<Object> {

	private static final Logger log = Logger.getLogger(SQSInboundGateway.class);

	private volatile boolean transaction = true;

	private volatile SQSPayloadDeserializer deserializer;

	public SQSInboundGateway() {
		super();
		deserializer = new Base64SQSPayloadDeserializer();
	}

	/**
	 * @return the transaction
	 */
	public boolean isTransaction() {
		return transaction;
	}

	/**
	 * @param transaction <code>true</code> to enable transactional behavior on
	 * message receive. The message is removed at SQS when the transaction
	 * commits.
	 */
	public void setTransaction(final boolean transaction) {
		this.transaction = transaction;
	}

	public void setDeserializer(final SQSPayloadDeserializer deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * {@inheritDoc}
	 */
	public Message<Object> receive() {
		if (transaction
				&& (!TransactionSynchronizationManager.isActualTransactionActive() || TransactionSynchronizationManager
						.isCurrentTransactionReadOnly())) {
			throw new IllegalStateException("An active non read only transaction is required!");
		}
		String queueName = getQueueName();
		final com.amazonaws.queue.doc._2009_02_01.Message message = template.receive(queueName);
		log.debug("Pulled message: " + message);
		if (message == null) {
			return null;
		}
		Message<Object> result = handleReceivedMessage(message);
		postReceive(queueName, message);
		return result;
	}

	/**
	 * SQS message to SI message transformation.
	 * @param message source
	 * @return the SI message
	 */
	private Message<Object> handleReceivedMessage(final com.amazonaws.queue.doc._2009_02_01.Message message) {
		Message<Object> result;
		if (isExtractPayload()) {
			try {
				result = extractPayload(message);
			}
			catch (Exception e) {
				throw new MessageTransformationException("Failed to convert payload of sqs message: " + message, e);
			}
		}
		else {
			result = new GenericMessage<Object>(message);
		}
		log.debug("Received as: " + result);
		return result;
	}

	private void postReceive(final String queueName, final com.amazonaws.queue.doc._2009_02_01.Message message) {
		if (!transaction) {
			template.commitReceive(queueName, message);
		}
		else {
			final String receiptHandle = message.getReceiptHandle();
			// add commit/delete message to transaction callback
			TransactionSynchronization value = new TransactionSynchronizationAdapter() {
				@Override
				public void beforeCommit(final boolean readOnly) {
					if (readOnly) {
						log.warn("Skipping commit receive in read only transaction.");
					}
					else {
						template.commitReceive(getQueueName(), receiptHandle);
					}
				}
			};
			TransactionSynchronizationManager.registerSynchronization(value);
		}
	}

	@SuppressWarnings("unchecked")
	private Message<Object> extractPayload(final com.amazonaws.queue.doc._2009_02_01.Message message) throws Exception {
		String payload = message.getBody();
		log.debug("Extracting payload: " + payload);
		Object receivedObject = deserializer.deserialize(payload);
		if (receivedObject instanceof Message<?>) {
			return (Message<Object>) receivedObject;
		}
		MessageBuilder<Object> builder = MessageBuilder.withPayload(receivedObject);
		builder.setHeader("sqs_message_id", message.getMessageId());
		builder.setHeader("sqs_receipt_handle", message.getReceiptHandle());
		return builder.build();
	}

	/**
	 * Decode base64 encoded payload.
	 * @author Alex Peters
	 * 
	 */
	public static class Base64SQSPayloadDeserializer extends PayloadDeserializingTransformer implements
			SQSPayloadDeserializer {
		/**
		 * {@inheritDoc}
		 */
		public Object deserialize(final String payload) throws Exception {
			byte[] transformPayload = payload.getBytes(DEFAULT_ENCODING);
			transformPayload = Base64.decodeBase64(transformPayload);
			return transformPayload(transformPayload);
		}
	}

	/**
	 * SQS message payload to SI object payload deserializer.
	 * @author Alex Peters
	 * 
	 */
	public static interface SQSPayloadDeserializer {

		/**
		 * @param payload to decode.
		 * @return result object
		 * @throws Exception any exception
		 */
		public abstract Object deserialize(String payload) throws Exception;

	}

}
