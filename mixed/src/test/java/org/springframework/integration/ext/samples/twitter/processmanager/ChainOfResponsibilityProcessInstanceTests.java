package org.springframework.integration.ext.samples.twitter.processmanager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.test.core.TestMessageFactory;

/**
 * @author Alex Peters
 * 
 */
@RunWith(MockitoJUnit44Runner.class)
public class ChainOfResponsibilityProcessInstanceTests {

	ChainOfResponsibilityProcessInstance<String> instance;

	String anyPid = "foobar";

	@Mock
	MessageHandler handler1;

	@Mock
	MessageHandler handler2;

	@Mock
	MessageHandler handler3;

	Message<?> anyMessage;

	@Before
	public void setUp() {
		anyMessage = TestMessageFactory.createAnyMessage();
		instance = ChainOfResponsibilityProcessInstance.create(anyPid, anyMessage, Arrays.asList(
				handler1, handler2, handler3));
	}

	@Test
	public void nextHandler_withoutMessageUpdates_allHandlersReturnedInCorrectOrder() {
		assertThat(instance.nextHandler(), is(handler1));
		assertThat(instance.nextHandler(), is(handler2));
		assertThat(instance.nextHandler(), is(handler3));
		try {
			instance.nextHandler();
			fail("Expected no more handler");
		}
		catch (NoSuchElementException e) {
		}
		;
	}

	@Test
	public void hasNextHandler_withMessageUpdate_noHandlerReturnedAfterFirst() {
		instance.update(TestMessageFactory.createAnyMessage());
		assertThat(instance.hasNextHandler(), is(false));

	}
}
