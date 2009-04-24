package org.springframework.integration.ext.samples.twitter.gateway;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.selector.MessageSelector;
import org.springframework.stereotype.Component;

/**
 * @author Alex Peters
 * 
 */
@Component("preventDublicateMessagesSelector")
public class DuplicateMessageFilterFactory extends AbstractFactoryBean {

	private static final Logger log = Logger.getLogger(DublicateMessageSelector.class);

	public DuplicateMessageFilterFactory() {
		super();
		setSingleton(true);
	}

	@Autowired
	private TwitterMessageRepository messageRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object createInstance() throws Exception {
		return new DublicateMessageSelector();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class getObjectType() {
		return DublicateMessageSelector.class;
	}

	/**
	 * @author Alex Peters
	 * 
	 */
	private class DublicateMessageSelector implements MessageSelector {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(Message<?> message) {
			if (!(message.getPayload() instanceof IncomingDirectMessage)) {
				return false;
			}
			IncomingDirectMessage payload = (IncomingDirectMessage) message.getPayload();
			IncomingDirectMessage persistedPayload = messageRepository.findIncomingMessage(payload
					.getMessageId());
			boolean result = persistedPayload == null;
			if (log.isDebugEnabled() && !result) {
				log.debug(String.format("Rejecting message with twitterId %s!", payload
						.getMessageId()));
			}
			return result;
		}
	}

}
