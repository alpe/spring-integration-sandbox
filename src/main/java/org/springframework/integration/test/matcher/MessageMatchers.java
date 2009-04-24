package org.springframework.integration.test.matcher;

import java.util.Map;

import org.hamcrest.Matcher;
import org.mockito.internal.progress.HandyReturnValues;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.springframework.integration.core.Message;

/**
 * Mockito matcher factory.
 * 
 * @author Alex Peters
 * 
 */
public class MessageMatchers {

	public static <T> Message<T> hasPayload(Matcher<T> payloadMatcher) {
		return reportMatcher(PayloadMatcher.hasPayload(payloadMatcher)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasPayload(T payload) {
		return reportMatcher(PayloadMatcher.hasPayload(payload)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasHeaderEntry(String key, Object value) {
		return reportMatcher(HeaderMatcher.hasEntry(key, value)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasHeaderEntry(String key, Matcher<?> valueMatcher) {
		return reportMatcher(HeaderMatcher.hasEntry(key, valueMatcher)).<Message<T>> returnNull();
	}

	public static <T> Message<T> hasHeaderEntries(Map<String, ?> entries) {
		return reportMatcher(HeaderMatcher.hasEntries(entries)).<Message<T>> returnNull();
	}

	// copy/paste from mockito.Matchers internal api.
	private static MockingProgress mockingProgress = new ThreadSafeMockingProgress();

	// copy/paste from mockito.Matchers internal api.
	private static HandyReturnValues reportMatcher(Matcher<?> matcher) {
		return mockingProgress.getArgumentMatcherStorage().reportMatcher(matcher);
	}
}
