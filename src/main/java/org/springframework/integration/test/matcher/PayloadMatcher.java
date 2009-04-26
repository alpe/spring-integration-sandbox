package org.springframework.integration.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.matchers.TypeSafeMatcher;
import org.springframework.integration.core.Message;

/**
 * <h2>Is the payload of a {@link Message} equal to a given value or is matching
 * a given matcher?</h2>
 * 
 * <h3>
 * A Junit example using {@link Assert#assertThat(Object, Matcher)} could look
 * like this to test a payload value:</h3>
 * 
 * <pre>
 * ANY_PAYLOAD = new BigDecimal(&quot;1.123&quot;);
 * Message&lt;BigDecimal message = MessageBuilder.withPayload(ANY_PAYLOAD).build();
 * assertThat(message, hasPayload(ANY_PAYLOjAD));
 * </pre>
 * 
 * <h3>
 * An example using {@link Assert#assertThat(Object, Matcher)} delegating to
 * another {@link Matcher}.</h3>
 * 
 * <pre>
 * ANY_PAYLOAD = new BigDecimal(&quot;1.123&quot;);
 * assertThat(message, PayloadMatcher.hasPayload(is(BigDecimal.class)));
 * assertThat(message, PayloadMatcher.hasPayload(notNullValue()));
 * assertThat(message, not((PayloadMatcher.hasPayload(is(String.class))))); *
 * </pre>
 * 
 * 
 * 
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