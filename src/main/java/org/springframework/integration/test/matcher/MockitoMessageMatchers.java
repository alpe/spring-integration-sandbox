package org.springframework.integration.test.matcher;

import java.util.Map;

import org.hamcrest.Matcher;
import org.mockito.Mockito;
import org.mockito.internal.progress.HandyReturnValues;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.springframework.integration.core.Message;

/**
 * <h2>Mockito matcher factory for simple {@link Message} matcher creation.</h2>
 * <p>
 * This factory is using an internal mockito API of version
 * <strong>1.7.0+</strong> so be aware when upgrading. You could also use any
 * Hamcrest matcher in Mockito with {@link Mockito#argThat(Matcher)}.
 * 
 * <h3>Example usage:</h3>
 * <p>
 * With {@link Mockito#verify(Object)}:
 * </p>
 * 
 * <pre>
 * &#064;Mock
 * MessageHandler handler;
 * ...
 * handler.handleMessage(message);
 * verify(handler).handleMessage(hasPayload(ANY_PAYLOAD));
 * verify(handler).handleMessage(hasPayload(is(Date.class)));
 * </pre>
 * <p>
 * With {@link Mockito#when(Object)}:
 * </p>
 * 
 * <pre>
 * ...
 * when(channel.send(hasPayload(ANY_PAYLOAD))).thenReturn(true);
 * assertThat(channel.send(message), is(true));
 * </pre>
 * 
 * @author Alex Peters
 * 
 */
public class MockitoMessageMatchers {

	public static <T> Message<T> hasPayload(Matcher<T> payloadMatcher) {
		return reportMatcher(PayloadMatcher.hasPayload(payloadMatcher)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasPayload(T payload) {
		return reportMatcher(PayloadMatcher.hasPayload(payload)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasHeaderEntry(String key, Object value) {
		return reportMatcher(HeaderMatcher.hasEntry(key, value)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasHeaderKey(String key) {
		return reportMatcher(HeaderMatcher.hasKey(key)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasHeaderEntry(String key, Matcher<?> valueMatcher) {
		return reportMatcher(HeaderMatcher.hasEntry(key, valueMatcher)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasAllHeaderEntries(Map<String, ?> entries) {
		return reportMatcher(HeaderMatcher.hasAllEntries(entries)).<Message<T>> returnNull();
	}

	// copy/paste from mockito.Matchers internal api.
	private static MockingProgress mockingProgress = new ThreadSafeMockingProgress();

	// copy/paste from mockito.Matchers internal api.
	private static HandyReturnValues reportMatcher(Matcher<?> matcher) {
		return mockingProgress.getArgumentMatcherStorage().reportMatcher(matcher);
	}
}
