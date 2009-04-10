package org.springframework.integration.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.junit.matchers.TypeSafeMatcher;
import org.springframework.integration.core.Message;

/**
 * @author Alex Peters
 * 
 */
public class PayloadMatcher<T> extends TypeSafeMatcher<Message<T>> {

	private final Matcher<T> matcher;

	/**
	 * @param matcher
	 */
	PayloadMatcher(Matcher<T> matcher) {
		super();
		this.matcher = matcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesSafely(Message<T> message) {
		return matcher.matches(message.getPayload());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("a message containing payload: ").appendDescriptionOf(matcher);

	}

	@Factory
	public static <T> Matcher<Message<T>> hasPayload(T payload) {
		return new PayloadMatcher<T>(IsEqual.equalTo(payload));
	}

	@Factory
	public static <T> Matcher<Message<T>> hasPayload(Matcher<T> payloadMatcher) {
		return new PayloadMatcher<T>(payloadMatcher);
	}
}