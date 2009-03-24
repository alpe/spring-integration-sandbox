package org.springframework.integration.ext.samples.cafe.onlinemq;

/**
 * @author Alex Peters
 *
 */
public class OnlineMQException extends Exception {

	/**
	 * 
	 */
	public OnlineMQException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OnlineMQException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OnlineMQException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OnlineMQException(Throwable cause) {
		super(cause);
	}

}
