package org.springframework.integration.ext.samples.twitter.command;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ext.samples.twitter.core.ControlBus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 */
@Component
public class StatusCommandHandler extends AbstractCommandHandler implements InitializingBean {

	private static final String COMMAND_PATTERN = createCommandRegExp("status");

	@Autowired
	private ControlBus identity;

	public StatusCommandHandler() {
		super(COMMAND_PATTERN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(identity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handle(ControlCommand cmd) {
		return "Up and Running since: " + identity.getStartDate().toString();
	}
}