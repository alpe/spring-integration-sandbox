package org.springframework.integration.test.matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.integration.test.matcher.MockitoMessageMatchers.hasHeaderEntry;
import static org.springframework.integration.test.matcher.MockitoMessageMatchers.hasPayload;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockito.runners.MockitoJUnit44Runner;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageHandler;

/**
 * @author Alex Peters
 * 
 */
@RunWith(MockitoJUnit44Runner.class)
public class MockitoMessageMatchersTests {

	static final Date ANY_PAYLOAD = new Date();

	static final String UNKNOWN_KEY = "unknownKey";

	static final String ANY_HEADER_VALUE = "bar";

	static final String ANY_HEADER_KEY = "test.foo";

	@Mock
	MessageHandler handler;

	@Mock
	MessageChannel channel;

	Message<Date> message;

	@Before
	public void setUp() {
		message = MessageBuilder.withPayload(ANY_PAYLOAD).setHeader(ANY_HEADER_KEY,
				ANY_HEADER_VALUE).build();
	}

	@Test
	public void anyMatcher_withVerifyArgumentMatcherAndEqualPayload_matching() throws Exception {
		handler.handleMessage(message);
		verify(handler).handleMessage(hasPayload(ANY_PAYLOAD));
		verify(handler).handleMessage(hasPayload(is(Date.class)));
	}

	@Test(expected = ArgumentsAreDifferent.class)
	public void anyMatcher_withVerifyAndDifferentPayload_notMatching() throws Exception {
		handler.handleMessage(message);
		verify(handler).handleMessage(hasPayload(nullValue()));
	}

	@Test
	public void anyMatcher_withWhenArgumentMatcherAndEqualPayload_matching() throws Exception {
		when(channel.send(hasPayload(ANY_PAYLOAD))).thenReturn(true);
		assertThat(channel.send(message), is(true));
	}

	@Test
	public void anyMatcher_withWhenAndDifferentPayload_notMatching() throws Exception {
		when(channel.send(hasHeaderEntry(ANY_HEADER_KEY, is(Short.class)))).thenReturn(true);
		assertThat(channel.send(message), is(false));
	}

}
