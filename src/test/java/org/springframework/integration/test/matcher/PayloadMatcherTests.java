package org.springframework.integration.test.matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 * 
 */
public class PayloadMatcherTests {

	static final BigDecimal ANY_PAYLOAD = new BigDecimal("1.123");

	Message<BigDecimal> message;

	@Before
	public void setUp() {
		message = MessageBuilder.withPayload(ANY_PAYLOAD).build();
	}

	@Test
	public void hasPayload_withEqualValue_matches() throws Exception {
		assertThat(message, PayloadMatcher.hasPayload(new BigDecimal("1.123")));
	}

	@Test
	public void hasPayload_withNotEqualValue_notMatching() throws Exception {
		assertThat(message, not(PayloadMatcher.hasPayload(new BigDecimal("456"))));
	}

	@Test
	public void hasPayload_withMatcher_matches() throws Exception {
		assertThat(message, PayloadMatcher.hasPayload(is(BigDecimal.class)));
		assertThat(message, PayloadMatcher.hasPayload(notNullValue()));
	}

	@Test
	public void hasPayload_withNotMatchingMatcher_notMatching() throws Exception {
		assertThat(message, not((PayloadMatcher.hasPayload(is(String.class)))));
	}
}
