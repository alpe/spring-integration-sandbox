package org.springframework.integration.ext.samples.twitter.command;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ext.samples.twitter.gateway.IncomingDirectMessage;
import org.springframework.integration.ext.samples.twitter.gateway.OutgoingMessage;
import org.springframework.integration.ext.samples.twitter.gateway.TwitterMessageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alex Peters
 * 
 */
@Component
public class CommandProcessManager {

	private static final Logger log = Logger.getLogger(CommandProcessManager.class);

	private final Set<CommandHandler<String>> cmdChain = new CopyOnWriteArraySet<CommandHandler<String>>();

	@Autowired
	private TwitterMessageRepository messageRepository;

	public CommandProcessManager() {
	}

	public boolean addCommandHandler(CommandHandler<String> handler) {
		return cmdChain.add(handler);
	}

	@ServiceActivator(inputChannel = "unprocessedCommands", outputChannel = "commandResponses")
	@Transactional(propagation = Propagation.REQUIRED)
	public OutgoingMessage handleCommand(IncomingDirectMessage cmd) {
		if (isProcessed(cmd)) {
			log.debug(String.format("Skipping message %s. Alredy processed!", cmd.getMessageId()));
			return null;
		}
		messageRepository.store(cmd);
		String responseText = proccessByChain(cmd);
		if (responseText == null) {
			return null;
		}
		return OutgoingMessage.create(cmd.getSender().getIdentifier(), responseText);

	}

	/**
	 * @param cmd
	 * @return
	 */
	String proccessByChain(ControlCommand cmd) {
		// TODO: this will work but what about adding some channels?
		for (CommandHandler<String> handler : cmdChain) {
			if (handler.canHandle(cmd)) {
				return handler.handle(cmd);
			}
		}
		return "Unsupported command: " + cmd.getText();
	}

	private boolean isProcessed(IncomingDirectMessage cmd) {
		IncomingDirectMessage msg = messageRepository.findIncomingMessage(cmd.getMessageId());
		log.debug("Found message: " + msg);
		return msg != null;
	}

}
