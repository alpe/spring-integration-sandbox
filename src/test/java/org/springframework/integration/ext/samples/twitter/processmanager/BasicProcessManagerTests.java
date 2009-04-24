package org.springframework.integration.ext.samples.twitter.processmanager;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.test.message.TestMessageHandlerFactory;

/**
 * @author Alex Peters
 * 
 */
@RunWith(MockitoJUnit44Runner.class)
public class BasicProcessManagerTests {

	BasicProcessManager manager;

	MessageFixture messageFixture;

	@Mock
	ProcessInstanceStore<?, ? super ProcessInstance<?>> store;

	@Mock
	ProcessInstance<Object> anyProcess;

	@Mock
	MessageChannel outputChannel;

	@Mock
	MessageHandler anyHandler;

	Message anyMessage;

	@Before
	public void setup() {
		manager = new BasicProcessManager(store);
		manager.setOutputChannel(outputChannel);
		manager.afterPropertiesSet();
		messageFixture = new MessageFixture();
		anyMessage = messageFixture.anyMessage();
		when(store.find(anyMessage)).thenReturn(anyProcess);
	}

	@Test
	public void processMessage_withResultMsg_sendToOutput() {
		when(anyProcess.nextMessage()).thenReturn(anyMessage);
		callProcessManager();
		verify(outputChannel).send(anyMessage);
	}

	@Test
	public void processMessage_withResultMsg_removedFromStore() {
		Object anyPid = new Object();
		when(anyProcess.getPid()).thenReturn(anyPid);
		callProcessManager();
		verify(store).removeByPid(anyPid);
	}

	@Ignore
	@Test
	public void processMessage_withException_() {
		when(anyProcess.nextMessage()).thenThrow(new IllegalArgumentException("Test, ignore..."));
		callProcessManager();

	}

	@Test
	public void processMessage_withMultipleProcessingSteps_allHandled() {
		MessageHandler anyHandler1 = TestMessageHandlerFactory.stubAsProducingHandler(anyHandler);
		manager.addHandler(anyHandler1);
		MessageHandler anyHandler2 = TestMessageHandlerFactory.stubAsProducingHandler(anyHandler);
		manager.addHandler(anyHandler2);
		MessageHandler anyHandler3 = TestMessageHandlerFactory.stubAsProducingHandler(anyHandler);
		manager.addHandler(anyHandler3);
		MessageHandler anyHandler4 = TestMessageHandlerFactory.stubAsProducingHandler(anyHandler);
		manager.addHandler(anyHandler4);

		when(anyProcess.nextMessage()).thenReturn(anyMessage);
		when(anyProcess.hasNextHandler()).thenReturn(true, true, true, true, false);
		when(anyProcess.nextHandler()).thenReturn(anyHandler1, anyHandler2, anyHandler3,
				anyHandler4);
		callProcessManager();
		verify(anyHandler, times(4)).handleMessage(anyMessage);
		verify(outputChannel).send(anyMessage);
	}

	protected void callProcessManager() {
		manager.processMessage(anyMessage);
	}
}
