package org.springframework.integration.ext.samples.twitter.processmanager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;

/**
 * @author Alex Peters
 * 
 */
public abstract class AbstractProcessInstanceStore<K, T extends ProcessInstance<? extends K>>
		implements ProcessInstanceStore<K, T> {

	private final ConcurrentHashMap<? super K, T> content = new ConcurrentHashMap<K, T>();

	/**
	 * @param key
	 * @return
	 */
	protected T get(K key) {
		return content.get(key);
	}

	/**
	 * @param key
	 * @param value
	 */
	protected void add(K key, T value) {
		content.put(key, value);
	}

	protected void add(T value) {
		add(value.getPid(), value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(T process) {
		removeByPid(process.getPid());
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeByPid(Object pid) {
		content.remove(pid);
	}

	/**
	 * @param message
	 * @param handlers
	 * @return
	 */
	public T createAndStore(Message<?> message, Collection<MessageHandler> handlers) {
		T result = create(message, handlers);
		add(result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract T find(Message<?> message);

	/**
	 * {@inheritDoc}
	 */
	public abstract T create(Message<?> message, Collection<MessageHandler> handlers);
}
