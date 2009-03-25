package org.springframework.integration.ext.samples.cafe.onlinemq;

import omq.api.OMQMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.message.MessageSource;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 */
public class OnlineMQInboundGateway implements MessageSource<Object>, InitializingBean {

	private static final Logger log = Logger.getLogger(OnlineMQInboundGateway.class);

	private OnlineMQTemplate onlineMQTemplate;

	private String queueName;

	boolean extractPayload = true;

	/**
	 * @param onlineMQTemplate the onlineMQTemplate to set
	 */
	public void setTemplate(OnlineMQTemplate onlineMQTemplate) {
		this.onlineMQTemplate = onlineMQTemplate;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setExtractPayload(boolean extractPayload) {
		this.extractPayload = extractPayload;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(onlineMQTemplate);
		Assert.hasText(queueName);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Message<Object> receive() {
		log.debug("Recieve called");
		try {
			OMQMessage message;
			message = onlineMQTemplate.recieve(queueName);
			if (message == null) {
				return null;
			}
			log.debug("Recieved message: " + message);
			if (extractPayload) {
				Object receivedObject = message.getMsgBodyAsObject();
				if (receivedObject instanceof Message) {
					return (Message<Object>) receivedObject;
				}
				return new GenericMessage<Object>(receivedObject);
			}
			return new GenericMessage<Object>(message);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to retrieve Message.", e);
		}

	}

}
