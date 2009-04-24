package org.springframework.integration.test.message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageRejectedException;

/**
 * @author Alex Peters
 * 
 */
public class TestMessageHandlerFactory {

	/**
	 * Stub given handler with an output Channel to send any handled message to.
	 * Message is delegated to handler before.
	 * @param handler {@link MessageHandler}
	 * @return handler
	 */
	public static ProducingHandlerStub stubAsProducingHandler(MessageHandler handler) {
		return new ProducingHandlerStub(handler);
	}

	/**
	 * Stub given handler to count all received messages and delegate to given
	 * handler.
	 * @param handler {@link MessageHandler}
	 * @return handler
	 */
	public static CountingTestEndpoint stubAsCountingHandler(MessageHandler handler) {
		return new CountingTestEndpoint(handler);
	}

	/**
	 * Create a {@link MessageHandler} that extracts the current thread name.
	 * @return handler
	 */
	public static ThreadNameExtractingTestTarget threadNameExtractinHandler() {
		return new ThreadNameExtractingTestTarget();
	}

	/**
	 * Create a {@link MessageHandler} that extracts the current thread name and
	 * is controlled by the given latch.
	 * @param latch {@link CountDownLatch}
	 * @return handler
	 */
	public static ThreadNameExtractingTestTarget threadNameExtractinHandler(CountDownLatch latch) {
		return new ThreadNameExtractingTestTarget(latch);
	}

	/**
	 * Create a {@link MessageHandler} that rejects all messages.
	 * @return handler
	 */
	public static RejectingMessageHandler rejectingHandler() {
		return new RejectingMessageHandler();
	}

	public static class ProducingHandlerStub implements MessageHandler {
		private MessageChannel output;

		private final MessageHandler handler;

		public ProducingHandlerStub(MessageHandler handler) {
			this.handler = handler;
		}

		public void setOutputChannel(MessageChannel channel) {
			this.output = channel;

		}

		public void handleMessage(Message<?> message) {
			if (this.handler != null) {
				this.handler.handleMessage(message);
			}
			output.send(message);
		}
	}

	public static class ThreadNameExtractingTestTarget implements MessageHandler {

		private String threadName;

		private final CountDownLatch latch;

		/**
		 * @return the threadName
		 */
		public String getThreadName() {
			return threadName;
		}

		ThreadNameExtractingTestTarget() {
			this(null);
		}

		ThreadNameExtractingTestTarget(CountDownLatch latch) {
			this.latch = latch;
		}

		public void handleMessage(Message<?> message) {
			this.threadName = Thread.currentThread().getName();
			if (this.latch != null) {
				this.latch.countDown();
			}
		}
	}

	public static class CountingTestEndpoint implements MessageHandler {

		private final AtomicInteger counter;

		private final MessageHandler handler;

		CountingTestEndpoint(MessageHandler handler) {
			this(new AtomicInteger(), handler);
		}

		CountingTestEndpoint(AtomicInteger counter, MessageHandler handler) {
			this.counter = counter;
			this.handler = handler;
		}

		public int getCount() {
			return counter.intValue();
		}

		public void handleMessage(Message<?> message) {
			this.counter.incrementAndGet();
			if (handler != null) {
				handler.handleMessage(message);
			}

		}
	}

	public static class RejectingMessageHandler implements MessageHandler {

		public void handleMessage(Message<?> message) {
			throw new MessageRejectedException(message, "intentional test failure");
		}
	}

}
