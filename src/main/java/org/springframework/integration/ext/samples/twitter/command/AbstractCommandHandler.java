package org.springframework.integration.ext.samples.twitter.command;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 * @param <T>
 */
public abstract class AbstractCommandHandler extends AbstractReplyProducingMessageHandler implements
		CommandHandler<String> {

	private static final Logger log = Logger.getLogger(AbstractCommandHandler.class);

	public static final String COMMAND_PREFIX = "~";

	public final Pattern commandPattern;

	private boolean active = true;

	/**
	 * @param commandPattern
	 */
	public AbstractCommandHandler(Pattern commandPattern) {
		super();
		Assert.notNull(commandPattern);
		this.commandPattern = commandPattern;
	}

	/**
	 * @param pattern regExp to identify command pattern in any text.
	 */
	public AbstractCommandHandler(String pattern) {
		this(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param processManager
	 * @return
	 */
	@Autowired
	public boolean register(TwitterCommandManager processManager) {
		return processManager.addHandler(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleRequestMessage(Message<?> message, ReplyMessageHolder replyHolder) {
		String resultPayload = null;
		Object payload = message.getPayload();
		if (payload != null && payload instanceof ControlCommand) {
			ControlCommand incomingDM = (ControlCommand) payload;
			if (canHandle(incomingDM)) {
				resultPayload = handle(incomingDM);
				replyHolder.set(resultPayload).build();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(ControlCommand cmd) {
		if (cmd == null || StringUtils.isBlank(cmd.getText())) {
			return false;
		}
		if (!active) {
			log.debug("Handler " + getClass() + " is not active. Ignoring command.");
			return false;
		}

		boolean result = commandPattern.matcher(cmd.getText()).matches();
		log.trace("return " + result + " for source: " + cmd.getText());
		return result;
	}

	static String createCommandRegExp(String word) {
		return ".*" + COMMAND_PREFIX + word + ".*";
	}
}