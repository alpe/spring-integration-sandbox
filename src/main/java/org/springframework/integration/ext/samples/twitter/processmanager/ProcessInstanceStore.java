package org.springframework.integration.ext.samples.twitter.processmanager;

import java.util.Collection;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;

/**
 * @author Alex Peters
 * 
 * @param <K>
 * @param <T>
 */
public interface ProcessInstanceStore<K, T extends ProcessInstance<? extends K>> {

	/**
	 * @param message
	 * @return
	 */
	public abstract T find(Message<?> message);

	/**
	 * @param message
	 * @param handlers
	 * @return
	 */
	public T createAndStore(Message<?> message, Collection<MessageHandler> handlers);

	/**
	 * @param process
	 */
	public void remove(T process);

	/**
	 * @param pid
	 */
	public void removeByPid(Object pid);

}