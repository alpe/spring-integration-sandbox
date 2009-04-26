package org.springframework.integration.test.matcher;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.matchers.TypeSafeMatcher;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageHeaders;

/**
 * <h2>Are the {@link MessageHeaders} of a {@link Message} containing any entry
 * or multiple that match?</h2>
 * 
 * 
 * <h3>
 * For example using {@link Assert#assertThat(Object, Matcher)} for a single
 * entry:</h3>
 * 
 * <pre>
 * ANY_HEADER_KEY = &quot;foo&quot;;
 * ANY_HEADER_VALUE = &quot;bar&quot;;
 * assertThat(message, hasEntry(ANY_HEADER_KEY, ANY_HEADER_VALUE));
 * assertThat(message, hasEntry(ANY_HEADER_KEY, is(String.class)));
 * assertThat(message, hasEntry(ANY_HEADER_KEY, notNullValue()));
 * assertThat(message, hasEntry(ANY_HEADER_KEY, is(ANY_HEADER_VALUE)));
 * </pre>
 * 
 * <h3>For multiple entries to match all:</h3>
 * 
 * <pre>
 * Map&lt;String, Object&gt; expectedInHeaderMap = new HashMap&lt;String, Object&gt;();
 * expectedInHeaderMap.put(ANY_HEADER_KEY, ANY_HEADER_VALUE);
 * expectedInHeaderMap.put(OTHER_HEADER_KEY, is(OTHER_HEADER_VALUE));
 * assertThat(message, HeaderMatcher.hasAllEntries(expectedInHeaderMap));
 * </pre>
 * 
 * <h3>
 * For a single key:</h3>
 * 
 * <pre>
 * ANY_HEADER_KEY = &quot;foo&quot;;
 * assertThat(message, HeaderMatcher.hasKey(ANY_HEADER_KEY));
 * </pre>
 * 
 * 
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
		description.appendText("a message containing a header with: ").appendDescriptionOf(matcher);

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
	public static Matcher<Message<?>> hasKey(String key) {
		return new HeaderMatcher(MapContains.hasKey(key));
	}

	@Factory
	public static Matcher<Message<?>> hasAllEntries(Map<String, ?> entries) {
		return new HeaderMatcher(MapContains.hasAllEntries(entries));
	}

}