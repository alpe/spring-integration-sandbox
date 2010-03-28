package org.springframework.integration.ext.samples.twitter.processmanager;

import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.core.Message;

/**
 * @author Alex Peters
 * 
 */
public class AsyncProcessManager extends BasicProcessManager {

	private TaskExecutor taskExecutor;

	/**
	 * @param processStore
	 */
	public AsyncProcessManager(ProcessInstanceStore<?, ? extends ProcessInstance<?>> processStore) {
		super(processStore);
	}

	/**
	 * @param taskExecutor the taskExecutor to set
	 */
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * @return
	 */
	TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onInit() {
		if (taskExecutor == null) {
			taskExecutor = IntegrationContextUtils.createThreadPoolTaskExecutor(1, 10, 10,
					"async-managedprocess-");
		}
		super.onInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void processMessage(final Message<?> message) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				AsyncProcessManager.super.processMessage(message);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStop() {
		super.doStop();
	}

}
