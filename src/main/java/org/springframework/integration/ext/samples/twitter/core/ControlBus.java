package org.springframework.integration.ext.samples.twitter.core;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * @author Alex Peters
 * 
 */
@Component
public class ControlBus {

	enum BusStaus {
		UP_AND_RUNNING, SHUITTING_DOWN
	}

	private final Date startDate;

	private BusStaus status;

	private Date lastStatusChangedDate;

	public ControlBus() {
		startDate = new Date();
		setStatus(BusStaus.UP_AND_RUNNING);
	}

	/**
	 * @return the status
	 */
	public BusStaus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	void setStatus(BusStaus status) {
		if (this.status != status) {
			this.status = status;
			lastStatusChangedDate = new Date();
		}
	}

	/**
	 * @return
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return
	 */
	public Date getLastStatusChangedDate() {
		return lastStatusChangedDate;
	}
}
