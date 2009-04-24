package org.springframework.integration.ext.samples.twitter.processmanager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Alex Peters
 * 
 */
public class AsyncProcessManagerTests extends BasicProcessManagerTests {

	@Override
	@Before
	public void setup() {
		manager = new AsyncProcessManager(store);
		manager.setOutputChannel(outputChannel);
		manager.afterPropertiesSet();
		messageFixture = new MessageFixture();
		anyMessage = messageFixture.anyMessage();
		when(store.find(anyMessage)).thenReturn(anyProcess);
	}

	@Test
	public void processMessage_withMultipleConcurrentMsg_allSendToOutput() {
		when(anyProcess.nextMessage()).thenReturn(anyMessage);
		int quantity = 20;
		for (int i = 0; i < quantity; i++) {
			new Thread() {
				@Override
				public void run() {
					AsyncProcessManagerTests.super.callProcessManager();
				}
			}.start();

		}
		waitForAllTasksToComplete();
		verify(outputChannel, times(quantity)).send(anyMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void callProcessManager() {
		super.callProcessManager();
		waitForAllTasksToComplete();
	}

	/**
	 * 
	 */
	private void waitForAllTasksToComplete() {
		// wait until all executor threads are finished
		TaskExecutor taskExecutor = ((AsyncProcessManager) manager).getTaskExecutor();
		assertThat(taskExecutor, is(ThreadPoolTaskExecutor.class));
		ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) taskExecutor;
		try {
			while (threadPoolTaskExecutor.getActiveCount() != 0) {

				Thread.sleep(200);
			}
			// give the last thread time to compute
			Thread.sleep(200);
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
