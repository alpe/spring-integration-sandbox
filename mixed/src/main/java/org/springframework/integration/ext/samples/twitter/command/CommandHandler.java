package org.springframework.integration.ext.samples.twitter.command;

/**
 * @author Alex Peters
 * 
 * @param <T>
 */
public interface CommandHandler<T> {

	boolean canHandle(ControlCommand cmd);

	T handle(ControlCommand cmd);
}
