package org.springframework.integration.ext.samples.twitter.gateway;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 */
@Component("directMessageOutChannelAdapter")
public class DirectMessageOutboundGateway implements InitializingBean {

	private static final Logger log = Logger.getLogger(DirectMessageOutboundGateway.class);

	@Autowired
	private TwitterTemplate template;

	public DirectMessageOutboundGateway() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(template);
	}

	/**
	 * @param message
	 */
	public void sendMessage(Message<OutgoingMessage> message) {
		log.debug("sending message: " + message);
		try {
			OutgoingMessage payload = message.getPayload();
			template.sendDirectMessage(payload.getRecipientIdentifier(), payload.getText());
		}
		catch (Exception e) {
			throw new MessageTransformationException(message,
					"Failed to transform message/payload", e);
		}
	}
}
