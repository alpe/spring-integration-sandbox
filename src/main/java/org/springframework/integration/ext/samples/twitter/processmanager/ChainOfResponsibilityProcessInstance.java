/**
 * 
 */
package org.springframework.integration.ext.samples.twitter.processmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageRejectedException;

/**
 * A chain of responsibility {@link ProcessInstance} implementation. All message
 * handlers are sequentially called to handle the source message until a
 * response is received. When the last handler is called without sending a
 * response a {@link MessageRejectedException} is thrown.
 * 
 * @author Alex Peters
 * 
 */
public class ChainOfResponsibilityProcessInstance<T> implements ProcessInstance<T> {

	private final T pid;

	private volatile Message<?> value;

	private final Iterator<MessageHandler> iterator;

	private final AtomicBoolean resultMsg = new AtomicBoolean(false);

	/**
	 * @param pid
	 * @param value
	 * @param iterator
	 */
	ChainOfResponsibilityProcessInstance(T pid, Message<?> value,
			Collection<MessageHandler> handlers) {
		super();
		this.pid = pid;
		this.value = value;
		this.iterator = new ArrayList<MessageHandler>(handlers).iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getPid() {
		return pid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNextHandler() {
		return !resultMsg.get() && iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageHandler nextHandler() {
		return iterator.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message<?> nextMessage() {
		if (hasNextHandler() || resultMsg.get()) {
			return value;
		}
		throw new MessageRejectedException(value, "Message rejected by all handlers.");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Message<?> message) {
		Object payload = message.getPayload();
		if (!resultMsg.get() && payload != null && !value.equals(message)) {
			resultMsg.set(true);
			value = message;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleException(Message<?> nextMessage, Exception e) {
		throw new MessageHandlingException(nextMessage, e);

	}

	/**
	 * @param T
	 * @param message
	 * @param handlers
	 * @return
	 */
	public static <T> ChainOfResponsibilityProcessInstance<T> create(T pid, Message<?> message,
			Collection<MessageHandler> handlers) {
		return new ChainOfResponsibilityProcessInstance<T>(pid, message, handlers);
	}

}
