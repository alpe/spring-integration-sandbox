package org.springframework.integration.ext.samples.sqs;

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
 * @author Alex Peters
 * 
 */
public class SQSInboundGateway extends AbstractSQSGateway implements MessageSource<Object> {

	private static final Logger log = Logger.getLogger(SQSInboundGateway.class);

	private volatile boolean transaction = true;

	private volatile SQSPayloadDeserializingTransformer deserializer;

	/**
	 * 
	 */
	public SQSInboundGateway() {
		super();
		deserializer = new SQSPayloadDeserializingTransformer();
	}

	/**
	 * Setter for testing purpose only.
	 * @param deserializer the deserializer to set
	 */
	void setDeserializer(SQSPayloadDeserializingTransformer deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * @return the transaction
	 */
	public boolean isTransaction() {
		return transaction;
	}

	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * @param message
	 * @return
	 */
	private Message<Object> handleReceivedMessage(
			final com.amazonaws.queue.doc._2009_02_01.Message message) {
		Message<Object> result;
		if (isExtractPayload()) {
			try {
				result = extractPayload(message);
			}
			catch (Exception e) {
				throw new MessageTransformationException(
						"Failed to convert payload of sqs message: " + message, e);
			}
		}
		else {
			result = new GenericMessage<Object>(message);
		}
		log.debug("Received as: " + result);
		return result;
	}

	/**
	 * @param message
	 */
	private void postReceive(final String queueName,
			final com.amazonaws.queue.doc._2009_02_01.Message message) {
		if (!transaction) {
			template.comitReceive(queueName, message);
		}
		else {

			final String receiptHandle = message.getReceiptHandle();
			// add commit/delete message to transaction callback
			TransactionSynchronization value = new TransactionSynchronizationAdapter() {
				@Override
				public void beforeCommit(boolean readOnly) {
					if (readOnly) {
						log.warn("Skipping commit receive in read only transaction.");
					}
					else {
						template.comitReceive(getQueueName(), receiptHandle);
					}
				}
			};
			TransactionSynchronizationManager.registerSynchronization(value);
		}
	}

	/**
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Message<Object> extractPayload(com.amazonaws.queue.doc._2009_02_01.Message message)
			throws Exception {
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
	 * 
	 * @author Alex Peters
	 * 
	 */
	public static class SQSPayloadDeserializingTransformer extends PayloadDeserializingTransformer {
		/**
		 * @param payload
		 * @return
		 * @throws Exception
		 */
		public Object deserialize(String payload) throws Exception {
			byte[] transformPayload = payload.getBytes(DEFAULT_ENCODING);
			transformPayload = Base64.decodeBase64(transformPayload);
			return transformPayload(transformPayload);
		}

	}
}
