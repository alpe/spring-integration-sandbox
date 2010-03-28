package org.springframework.integration.ext.samples.twitter.command;

import java.util.Collection;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.core.MessageHeaders;
import org.springframework.integration.ext.samples.twitter.gateway.IncomingDirectMessage;
import org.springframework.integration.ext.samples.twitter.gateway.TwitterMessageRepository;
import org.springframework.integration.ext.samples.twitter.processmanager.AbstractProcessInstanceStore;
import org.springframework.integration.ext.samples.twitter.processmanager.BasicProcessManager;
import org.springframework.integration.ext.samples.twitter.processmanager.ChainOfResponsibilityProcessInstance;
import org.springframework.integration.message.MessageHandler;
import org.springframework.stereotype.Component;

/**
 * @author Alex Peters
 * 
 */
@Component
public class TwitterCommandManager extends BasicProcessManager {

	private static final Logger log = Logger.getLogger(TwitterCommandManager.class);

	private static final String PREFIX = MessageHeaders.PREFIX + "processManager_";

	private static final String MESSAGE_HANDLE_HEADER_KEY = PREFIX + "messageHandle";

	@Autowired
	private TwitterMessageRepository messageRepository;

	@Override
	@Autowired
	public void setOutputChannel(@Qualifier("commandResponses") MessageChannel outputChannel) {
		super.setOutputChannel(outputChannel);
	};

	@Qualifier(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
	private MessageChannel errorChannel;

	public TwitterCommandManager() {
		super(new CommandProcessStore());
	}

	@ServiceActivator(inputChannel = "unprocessedCommands")
	public void handleCommand(Message<IncomingDirectMessage> message) {
		processMessage(message);
	}

	/**
	 * @param message
	 */
	// @Transactional(propagation = Propagation.REQUIRED)
	public void processMessage(Message<IncomingDirectMessage> message) {
		IncomingDirectMessage payload = message.getPayload();
		// messageRepository.store(payload);
		handleMessage(message);
	}

	/**
	 * @param handler
	 * @return
	 */
	public boolean addHandler(AbstractCommandHandler handler) {
		return super.addHandler(handler);
	}

	/**
	 * @author Alex Peters
	 * 
	 */
	private static class CommandProcessStore extends
			AbstractProcessInstanceStore<UUID, ChainOfResponsibilityProcessInstance<UUID>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChainOfResponsibilityProcessInstance<UUID> create(Message<?> message,
				Collection<MessageHandler> handlers) {
			UUID pid = (UUID) message.getHeaders().getId();
			return ChainOfResponsibilityProcessInstance.create(pid, message, handlers);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChainOfResponsibilityProcessInstance<UUID> find(Message<?> message) {
			UUID pid = (UUID) message.getHeaders().getId();
			ChainOfResponsibilityProcessInstance<UUID> result = get(pid);
			if (result != null) {
				return result;
			}
			pid = (UUID) message.getHeaders().getCorrelationId();
			return get(pid) != null ? result : null;
		}

	}

}
