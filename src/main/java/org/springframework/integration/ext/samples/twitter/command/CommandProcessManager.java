package org.springframework.integration.ext.samples.twitter.command;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ext.samples.twitter.gateway.OutgoingMessage;
import org.springframework.stereotype.Component;

/**
 * @author Alex Peters
 * 
 */
@Component
public class CommandProcessManager {

	private final Set<CommandHandler<String>> cmdChain = new CopyOnWriteArraySet<CommandHandler<String>>();

	public CommandProcessManager() {
	}

	public boolean addCommandHandler(CommandHandler<String> handler) {
		return cmdChain.add(handler);
	}

	@ServiceActivator(inputChannel = "unprocessedCommands", outputChannel = "commandResponses")
	public OutgoingMessage handleCommand(ControlCommand cmd) {
		if (isProcessed(cmd)) {
			throw new IllegalStateException("Already processed Command with identity: "
					+ cmd.getMessageId());
		}
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

	private boolean isProcessed(ControlCommand cmd) {
		// TODO: check storage for messageid
		return false;
	}

}
