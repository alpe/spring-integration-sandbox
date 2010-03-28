package org.springframework.integration.ext.samples.sqs;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.integration.transformer.PayloadSerializingTransformer;

/**
 * @author Alex Peters
 * 
 */
public class SQSOutboundGateway extends AbstractSQSGateway {

	private static final Logger log = Logger.getLogger(SQSOutboundGateway.class);

	/**
	 * @param message
	 */
	public void sendMessage(Message<?> message) {
		log.debug("sending message: " + message);
		Object payload = isExtractPayload() ? message.getPayload() : message;
		String serializedPayload;
		try {
			serializedPayload = serialize(payload);
		}
		catch (Exception e) {
			throw new MessageTransformationException(message,
					"Failed to transform message/payload", e);
		}
		template.sendMessage(getQueueName(), serializedPayload);
	}

	/**
	 * @param payload
	 * @return
	 * @throws Exception
	 */
	String serialize(Object payload) throws Exception {
		/** Impl to access transformPayload method only. */
		byte[] transformPayload = new PayloadSerializingTransformer() {
			@Override
			public byte[] transformPayload(Object obj) throws Exception {
				return super.transformPayload(obj);
			}

		}.transformPayload(payload);
		transformPayload = Base64.encodeBase64(transformPayload);
		return new String(transformPayload, DEFAULT_ENCODING);

	}
}
