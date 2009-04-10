package org.springframework.integration.test.matcher;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.matchers.TypeSafeMatcher;
import org.springframework.integration.core.Message;

/**
 * @author Alex Peters
 *
 */
public class HeaderMatcher extends TypeSafeMatcher<Message<?>> {

	private final Matcher<?> matcher;

	/**
	 * @param matcher
	 */
	HeaderMatcher(Matcher<?> matcher) {
		super();
		this.matcher = matcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesSafely(Message<?> item) {
		return matcher.matches(item.getHeaders());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("a message containing payload: ").appendDescriptionOf(matcher);

	}

	@Factory
	public static Matcher<Message<?>> hasEntry(String key, Object value) {
		return new HeaderMatcher(MapContains.hasEntry(key, value));
	}

	@Factory
	public static Matcher<Message<?>> hasEntry(String key, Matcher<?> valueMatcher) {
		return new HeaderMatcher(MapContains.hasEntry(key, valueMatcher));
	}

	@Factory
	public static Matcher<?> hasEntries(Map<String, ?> entries) {
		return new HeaderMatcher(MapContains.hasEntries(entries));
	}

}