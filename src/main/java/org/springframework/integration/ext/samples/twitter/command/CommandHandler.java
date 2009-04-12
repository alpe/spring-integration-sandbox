package org.springframework.integration.ext.samples.twitter.command;

/**
 * @author Alex Peters
 * 
 * @param <T>
 */
public interface CommandHandler<T> {

	public boolean canHandle(ControlCommand cmd);

	public T handle(ControlCommand cmd);
}
