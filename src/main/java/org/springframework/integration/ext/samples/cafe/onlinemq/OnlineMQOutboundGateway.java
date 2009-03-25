package org.springframework.integration.ext.samples.cafe.onlinemq;

import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.message.MessageHandlingException;

/**
 * @author Alex Peters
 * 
 */
public class OnlineMQOutboundGateway extends AbstractReplyProducingMessageHandler {

	private static final Logger log = Logger.getLogger(OnlineMQOutboundGateway.class);

	private String queueName;

	private OnlineMQTemplate onlineMQTemplate;

	private boolean extractPayload = true;

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
	protected void handleRequestMessage(Message<?> requestMessage,
			ReplyMessageHolder replyMessageHolder) {
		int returnCode;
		try {
			Object payload = extractPayload ? requestMessage.getPayload() : requestMessage;
			returnCode = onlineMQTemplate.send(queueName, payload);
		}
		catch (Exception e) {
			throw new MessageHandlingException(requestMessage, "failed to send OnlineMQ message", e);
		}
		if (returnCode == 0) {
			log.debug("Message sent to OnlineMQ");
		}
		else if (returnCode == 101)
		// example for handling specific error code 101 = Authentication failed
		{
			throw new MessageHandlingException(requestMessage, "Authentication failed");
		}
		else {
			throw new MessageHandlingException(requestMessage, "Authentication failed");
		}
		log.debug("finished message sending");
	}

}
