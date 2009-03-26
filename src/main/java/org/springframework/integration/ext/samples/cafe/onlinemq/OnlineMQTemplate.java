package org.springframework.integration.ext.samples.cafe.onlinemq;

import java.io.IOException;

import omq.api.OMQMessage;

/**
 * Template style onlineMQ.com access.
 * 
 * @author Alex Peters
 * 
 */
public interface OnlineMQTemplate {

	/**
	 * @param payload
	 * @param queueName
	 * @return
	 * @throws OnlineMQException
	 */
	public abstract int send(final String queueName, Object payload) throws OnlineMQException;

	/**
	 * @param queueName
	 * @return
	 * @throws OnlineMQException
	 */
	public abstract OMQMessage recieve(final String queueName) throws OnlineMQException;

	/**
	 * @param queueName
	 * @param payload
	 * @return
	 * @throws OnlineMQException
	 */
	public int send(final String queueName, byte[] payload) throws OnlineMQException;

}