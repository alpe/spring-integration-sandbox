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
public class PayloadMatcher extends TypeSafeMatcher<Message<?>> {

	private final Matcher<?> matcher;

	/**
	 * @param matcher
	 */
	PayloadMatcher(Matcher<?> matcher) {
		super();
		this.matcher = matcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesSafely(Message<?> message) {
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
	public static <T> Matcher<Message<?>> hasPayload(T payload) {
		return new PayloadMatcher(IsEqual.equalTo(payload));
	}

	@Factory
	public static Matcher<Message<?>> hasPayload(Matcher<?> payloadMatcher) {
		return new PayloadMatcher(payloadMatcher);
	}
}