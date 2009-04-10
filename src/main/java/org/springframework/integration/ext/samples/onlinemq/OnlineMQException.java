package org.springframework.integration.ext.samples.onlinemq;

/**
 * @author Alex Peters
 * 
 */
public class OnlineMQException extends Exception {

	private static final long serialVersionUID = -2001636405427545455L;

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
