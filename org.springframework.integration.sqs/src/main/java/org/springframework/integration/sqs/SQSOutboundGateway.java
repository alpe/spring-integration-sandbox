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
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageRejectedException;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.integration.transformer.PayloadSerializingTransformer;

/**
 * <p>
 * Gateway to send SI Messages to a Amazon SQS based Queue.
 * </p>
 * @author Alex Peters
 * 
 */
public class SQSOutboundGateway extends AbstractSQSGateway implements MessageHandler {

	private static final Logger log = Logger.getLogger(SQSOutboundGateway.class);

	private SQSPayloadSerializer serializer;

	public SQSOutboundGateway() {
		super();
		serializer = new Base64SQSPayloadSerializer();
	}

	public void setSerializer(final SQSPayloadSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleMessage(final Message<?> message) throws MessageRejectedException, MessageHandlingException,
			MessageDeliveryException {
		sendMessage(message);
	}

	/**
	 * Send message to SQS queue.
	 * @param message the message
	 */
	void sendMessage(final Message<?> message) {
		log.debug("sending message: " + message);
		Object payload = isExtractPayload() ? message.getPayload() : message;
		String serializedPayload;
		try {
			serializedPayload = serializer.serialize(payload);
		}
		catch (Exception e) {
			throw new MessageTransformationException(message, "Failed to transform message/payload", e);
		}
		template.sendMessage(getQueueName(), serializedPayload);
	}

	/**
	 * Serialize and encode payload to base64 String.
	 * @author Alex Peters
	 * 
	 */
	public static class Base64SQSPayloadSerializer extends PayloadSerializingTransformer implements
			SQSPayloadSerializer {
		/**
		 * {@inheritDoc}
		 */
		public String serialize(final Object payload) throws Exception {
			byte[] transformPayload = transformPayload(payload);
			transformPayload = Base64.encodeBase64(transformPayload);
			return new String(transformPayload, DEFAULT_ENCODING);
		}
	}

	/**
	 * SI object payload to SQS message payload transformer.
	 * @author Alex Peters
	 * 
	 */
	public static interface SQSPayloadSerializer {

		/**
		 * Encode object to base64 string.
		 * @param payload source
		 * @return object as string
		 * @throws Exception any exception
		 */
		public abstract String serialize(Object payload) throws Exception;

	}
}
