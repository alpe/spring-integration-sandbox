package org.springframework.integration.ext.samples.twitter.command;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 * @param <T>
 */
public abstract class AbstractCommandHandler implements CommandHandler<String> {

	private static final Logger log = Logger.getLogger(AbstractCommandHandler.class);

	public static final String COMMAND_PREFIX = "~";

	public final Pattern commandPattern;

	private boolean active = true;

	/**
	 * @param pattern regExp to identify command pattern in any text.
	 */
	public AbstractCommandHandler(String pattern) {
		this(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
	}

	/**
	 * @param commandPattern
	 */
	public AbstractCommandHandler(Pattern commandPattern) {
		super();
		Assert.notNull(commandPattern);
		this.commandPattern = commandPattern;

	}

	@Autowired
	public boolean register(CommandProcessManager processManager) {
		return processManager.addCommandHandler(this);
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
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(ControlCommand cmd) {
		if (cmd == null || cmd.getText() == null || cmd.getText().length() == 0) {
			return false;
		}
		if (!active) {
			log.debug("Can not handle command. Handler " + getClass() + " is not active.");
		}

		boolean result = commandPattern.matcher(cmd.getText()).matches();
		log.trace("return " + result + " for source: " + cmd.getText());
		return result;
	}

	static String createCommandRegExp(String word) {
		return ".*" + COMMAND_PREFIX + word + ".*";
	}
}