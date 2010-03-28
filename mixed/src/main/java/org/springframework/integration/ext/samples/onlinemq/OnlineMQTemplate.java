package org.springframework.integration.ext.samples.onlinemq;

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
	 * @return result code
	 * @throws OnlineMQException
	 */
	public abstract int send(final String queueName, Object payload) throws OnlineMQException;

	/**
	 * @param queueName
	 * @return {@link OMQMessage}
	 * @throws OnlineMQException
	 */
	public abstract OMQMessage receive(final String queueName) throws OnlineMQException;

	/**
	 * @param queueName
	 * @param payload
	 * @return result code
	 * @throws OnlineMQException
	 */
	public int send(final String queueName, byte[] payload) throws OnlineMQException;

}