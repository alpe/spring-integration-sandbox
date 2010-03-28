package org.springframework.integration.ext.samples.twitter.command;

import org.springframework.stereotype.Component;

/**
 * @author Alex Peters
 * 
 */
@Component
public class EchoCommandHandler extends AbstractCommandHandler {

	private static final String COMMAND_PATTERN = createCommandRegExp("echo");

	public EchoCommandHandler() {
		super(COMMAND_PATTERN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handle(ControlCommand cmd) {
		int pos = cmd.getText().toLowerCase().indexOf("echo");
		return cmd.getText().substring(pos + 5);
	}

}