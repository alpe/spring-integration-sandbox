package org.springframework.integration.ext.samples.twitter.command;

/**
 * @author Alex Peters
 * 
 */
public interface ControlCommand {

	/**
	 * @return the messageId
	 */
	public abstract long getMessageId();

	/**
	 * @return the text
	 */
	public abstract String getText();

	/**
	 * @return
	 */
	public Sender getSender();

	/**
	 * @author Alex Peters
	 * 
	 */
	public static interface Sender {

		/**
		 * Get unique identifier like database id or natural key.
		 * @return
		 */
		String getIdentifier();

		/**
		 * @return any displayable name.
		 */
		String getDisplayName();
	}

}