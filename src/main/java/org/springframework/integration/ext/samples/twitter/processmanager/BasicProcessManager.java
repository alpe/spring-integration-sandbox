package org.springframework.integration.ext.samples.twitter.processmanager;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.MessageChannelTemplate;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.ext.samples.twitter.util.MessageChannelUtils;
import org.springframework.integration.handler.MessageHandlerChain;
import org.springframework.integration.message.MessageHandler;
import org.springframework.util.Assert;

/**
 * <p>
 * A Basic process manager receiving messages and delegating to a
 * {@link ProcessInstance} managing the flow and message transformation.
 * ProcessInstances are created and managed by the {@link ProcessInstanceStore}.
 * <p>
 * For a straight chain please see {@link MessageHandlerChain}.
 * @author Alex Peters
 * 
 */
public class BasicProcessManager extends AbstractEndpoint implements MessageHandler {

	private static final String OUTPUT_CHANNEL_PROPERTY = "outputChannel";

	private static final AtomicInteger channelCounter = new AtomicInteger(0);

	private volatile boolean initialized;

	private final ProcessInstanceStore<?, ? extends ProcessInstance<?>> processStore;

	private final DirectChannel internalFlowChannel;

	private volatile MessageChannel outputChannel;

	private final MessageChannelTemplate channelTemplate = new MessageChannelTemplate();

	private final EventDrivenConsumer eventDrivenConsumer;

	private final Set<MessageHandler> handlers;

	/**
	 * @param processStore
	 */
	public BasicProcessManager(ProcessInstanceStore<?, ? extends ProcessInstance<?>> processStore) {
		super();
		this.processStore = processStore;
		handlers = new CopyOnWriteArraySet<MessageHandler>();
		internalFlowChannel = new DirectChannel();
		internalFlowChannel
				.setBeanName("_" + this + ".channel#" + channelCounter.getAndIncrement());
		eventDrivenConsumer = new EventDrivenConsumer(internalFlowChannel, this);
		setAutoStartup(true);
	}

	/**
	 * @param outputChannel
	 */
	public void setOutputChannel(MessageChannel outputChannel) {
		this.outputChannel = outputChannel;
	}

	/**
	 * @return
	 */
	protected MessageChannel getOutputChannel() {
		return outputChannel;
	}

	/**
	 * @param sendTimeout
	 */
	public void setSendTimeout(long sendTimeout) {
		this.channelTemplate.setSendTimeout(sendTimeout);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onInit() {
		// sychronized by parent class
		initialized = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(Message<?> message) {
		if (!this.initialized) {
			this.afterPropertiesSet();
		}
		processMessage(message);
	}

	/**
	 * @param message
	 */
	void processMessage(Message<?> message) {
		ProcessInstance<?> process = processStore.find(message);
		if (process == null) {
			process = processStore.createAndStore(message, handlers);
		}
		process.update(message);
		Message<?> nextMessage = process.nextMessage();
		boolean hasNextHandler = process.hasNextHandler();
		if (hasNextHandler) {
			try {
				MessageHandler handler = process.nextHandler();
				handler.handleMessage(nextMessage);
			}
			catch (Exception e) {
				process.handleException(nextMessage, e);
			}
		}
		else {
			MessageChannel replyChannel = (getOutputChannel() != null) ? getOutputChannel()
					: MessageChannelUtils.fetchReplyChannel(message, getChannelResolver());
			channelTemplate.send(nextMessage, replyChannel);
			process.close();
			processStore.removeByPid(process.getPid());
		}
	}

	/**
	 * @param handler
	 * @return
	 */
	public boolean addHandler(MessageHandler handler) {
		Assert.notNull(handler);
		PropertyAccessor accessor = new BeanWrapperImpl(handler);
		Assert.notNull(accessor.getPropertyType(OUTPUT_CHANNEL_PROPERTY),
				"All handlers in the chain must implement property '" + OUTPUT_CHANNEL_PROPERTY
						+ "' of type 'MessageChannel'");
		accessor.setPropertyValue(OUTPUT_CHANNEL_PROPERTY, internalFlowChannel);
		return handlers.add(handler);
	}

	/**
	 * @param handler
	 * @return
	 */
	public boolean removeHandler(MessageHandler handler) {
		return handlers.remove(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStart() {
		eventDrivenConsumer.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStop() {
	}

}
