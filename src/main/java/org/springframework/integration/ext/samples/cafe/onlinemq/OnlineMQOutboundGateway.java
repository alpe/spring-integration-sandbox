package org.springframework.integration.ext.samples.cafe.onlinemq;

import org.apache.log4j.Logger;
import org.springframework.integration.core.Message;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

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

	/**
	 * @param queueName name of queue at onlineMQ
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @param extractPayload
	 */
	public void setExtractPayload(boolean extractPayload) {
		this.extractPayload = extractPayload;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleRequestMessage(Message<?> requestMessage,
			ReplyMessageHolder replyMessageHolder) {
		Assert.notNull(requestMessage);
		log.debug("Sending message :" + requestMessage.getHeaders().getId());
		int returnCode = sendMessage(requestMessage);
		if (returnCode != 0) {
			throw new MessageHandlingException(requestMessage, getCodeDescription(returnCode));
		}
		log.debug("Message sent to O10nlineMQ");
	}

	/**
	 * @see http
	 * ://www.onlinemq.com/support/index.php/Online_MQ_API#Error_handling
	 * @param returnCode code
	 * @return description
	 */
	String getCodeDescription(int returnCode) {
		switch (returnCode) {
		case 101:
			return "Authentication failed";
		case 201:
			return "Unknown queue";
		case 202:
			return "Unknown queue for specified queue manager";
		case 203:
			return "Unknown queue manager";
		case 204:
			return "User is not authorized as viewer for queue";
		case 301:
			return "Message could not be found";
		case 401:
			return "Must be SuperAdmin for this action";
		default:
			return "Failure with code: " + returnCode;
		}
	}

	/**
	 * @param requestMessage
	 * @return return code.
	 */
	int sendMessage(Message<?> requestMessage) {
		try {
			Object payload = extractPayload ? requestMessage.getPayload() : requestMessage;
			return onlineMQTemplate.send(queueName, payload);
		}
		catch (Exception e) {
			throw new MessageHandlingException(requestMessage, "Failed to send OnlineMQ message", e);
		}
	}

}
