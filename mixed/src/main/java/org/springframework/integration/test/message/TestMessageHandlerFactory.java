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
	public static ProducingTestMessageHandler stubAsProducingHandler(MessageHandler handler) {
		return new ProducingTestMessageHandler(handler);
	}

	/**
	 * Create {@link MessageHandler} that counts all received messages.
	 * @return handler
	 */
	public static CountingTestMessageHandler countingHandler() {
		return new CountingTestMessageHandler(null);
	}

	/**
	 * Stub given handler to count all received messages and delegate to given
	 * handler.
	 * @param handler {@link MessageHandler}
	 * @return handler
	 */
	public static CountingTestMessageHandler stubAsCountingHandler(MessageHandler handler) {
		return new CountingTestMessageHandler(handler);
	}

	/**
	 * Create a {@link MessageHandler} that extracts the current thread name.
	 * @return handler
	 */
	public static ThreadNameExtractingTestMessageHandler threadNameExtractingHandler() {
		return new ThreadNameExtractingTestMessageHandler();
	}

	/**
	 * Create a {@link MessageHandler} that extracts the current thread name and
	 * is controlled by the given latch.
	 * @param latch {@link CountDownLatch}
	 * @return handler
	 */
	public static ThreadNameExtractingTestMessageHandler threadNameExtractinHandler(
			CountDownLatch latch) {
		return new ThreadNameExtractingTestMessageHandler(latch);
	}

	/**
	 * Create a {@link MessageHandler} that rejects all messages.
	 * @return handler
	 */
	public static RejectingTestMessageHandler rejectingHandler() {
		return new RejectingTestMessageHandler();
	}

	public static class ProducingTestMessageHandler implements MessageHandler {
		private MessageChannel output;

		private final MessageHandler handler;

		public ProducingTestMessageHandler(MessageHandler handler) {
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

	public static class ThreadNameExtractingTestMessageHandler implements MessageHandler {

		private String threadName;

		private final CountDownLatch latch;

		/**
		 * @return the threadName
		 */
		public String getThreadName() {
			return threadName;
		}

		ThreadNameExtractingTestMessageHandler() {
			this(null);
		}

		ThreadNameExtractingTestMessageHandler(CountDownLatch latch) {
			this.latch = latch;
		}

		public void handleMessage(Message<?> message) {
			this.threadName = Thread.currentThread().getName();
			if (this.latch != null) {
				this.latch.countDown();
			}
		}
	}

	public static class CountingTestMessageHandler implements MessageHandler {

		private final AtomicInteger counter;

		private final MessageHandler handler;

		CountingTestMessageHandler(MessageHandler handler) {
			this(new AtomicInteger(), handler);
		}

		CountingTestMessageHandler(AtomicInteger counter, MessageHandler handler) {
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

	public static class RejectingTestMessageHandler implements MessageHandler {

		public void handleMessage(Message<?> message) {
			throw new MessageRejectedException(message, "intentional test failure");
		}
	}

}
