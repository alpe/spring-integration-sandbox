package org.springframework.integration.ext.samples.twitter.processmanager;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;

/**
 * @author Alex Peters
 * 
 */
public interface ProcessInstance<T> {

	T getPid();

	boolean hasNextHandler();

	MessageHandler nextHandler();

	void update(Message<?> message);

	Message<?> nextMessage();

	void close();

	void handleException(Message<?> nextMessage, Exception e);
}
