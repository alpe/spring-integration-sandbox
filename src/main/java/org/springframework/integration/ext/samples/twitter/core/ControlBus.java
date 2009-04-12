package org.springframework.integration.ext.samples.twitter.core;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * @author Alex Peters
 * 
 */
@Component
public class ControlBus {

	private final Date startDate;

	public ControlBus() {
		startDate = new Date();
	}

	/**
	 * @return
	 */
	public Date getStartDate() {
		return startDate;
	}
}
