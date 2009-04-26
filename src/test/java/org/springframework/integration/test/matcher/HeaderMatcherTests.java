package org.springframework.integration.test.matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 * 
 */
public class HeaderMatcherTests {

	static final String UNKNOWN_KEY = "unknownKey";

	static final String ANY_HEADER_VALUE = "bar";

	static final String ANY_HEADER_KEY = "test.foo";

	static final String ANY_PAYLOAD = "bla";

	static final String OTHER_HEADER_KEY = "test.number";

	static final Integer OTHER_HEADER_VALUE = Integer.valueOf(123);

	Message<?> message;

	@Before
	public void setUp() {
		message = MessageBuilder.withPayload(ANY_PAYLOAD).setHeader(ANY_HEADER_KEY,
				ANY_HEADER_VALUE).setHeader(OTHER_HEADER_KEY, OTHER_HEADER_VALUE).build();
	}

	@Test
	public void hasEntry_withValidKeyValue_matches() throws Exception {
		assertThat(message, HeaderMatcher.hasEntry(ANY_HEADER_KEY, ANY_HEADER_VALUE));
		assertThat(message, HeaderMatcher.hasEntry(OTHER_HEADER_KEY, OTHER_HEADER_VALUE));
	}

	@Test
	public void hasEntry_withUnknownKey_notMatching() throws Exception {
		assertThat(message, not(HeaderMatcher.hasEntry("test.unknown", ANY_HEADER_VALUE)));
	}

	@Test
	public void hasEntry_withValidKeyAndMatcherValue_matches() throws Exception {
		assertThat(message, HeaderMatcher.hasEntry(ANY_HEADER_KEY, is(String.class)));
		assertThat(message, HeaderMatcher.hasEntry(ANY_HEADER_KEY, notNullValue()));
		assertThat(message, HeaderMatcher.hasEntry(ANY_HEADER_KEY, is(ANY_HEADER_VALUE)));
	}

	@Test
	public void hasEntry_withValidKeyAndMatcherValue_notMatching() throws Exception {
		assertThat(message, not(HeaderMatcher.hasEntry(ANY_HEADER_KEY, is(Integer.class))));
	}

	@Test
	public void hasKey_withValidKey_matches() throws Exception {
		assertThat(message, HeaderMatcher.hasKey(ANY_HEADER_KEY));
		assertThat(message, HeaderMatcher.hasKey(OTHER_HEADER_KEY));
	}

	@Test
	public void hasKey_withInvalidKey_notMatching() throws Exception {
		assertThat(message, not(HeaderMatcher.hasKey(UNKNOWN_KEY)));
	}

	@Test
	public void hasAllEntries_withMessageHeader_matches() throws Exception {
		Map<String, Object> expectedInHeaderMap = message.getHeaders();
		assertThat(message, HeaderMatcher.hasAllEntries(expectedInHeaderMap));
	}

	@Test
	public void hasAllEntries_withValidKeyValueOrMatcherValue_matches() throws Exception {
		Map<String, Object> expectedInHeaderMap = new HashMap<String, Object>();
		expectedInHeaderMap.put(ANY_HEADER_KEY, ANY_HEADER_VALUE);
		expectedInHeaderMap.put(OTHER_HEADER_KEY, is(OTHER_HEADER_VALUE));
		assertThat(message, HeaderMatcher.hasAllEntries(expectedInHeaderMap));
	}

	@Test
	public void hasAllEntries_withInvalidValidKeyValueOrMatcherValue_notMatching() throws Exception {
		Map<String, Object> expectedInHeaderMap = new HashMap<String, Object>();
		expectedInHeaderMap.put(ANY_HEADER_KEY, ANY_HEADER_VALUE); // valid
		expectedInHeaderMap.put(UNKNOWN_KEY, not(nullValue())); // fails
		assertThat(message, not(HeaderMatcher.hasAllEntries(expectedInHeaderMap)));
		expectedInHeaderMap.remove(UNKNOWN_KEY);
		expectedInHeaderMap.put(OTHER_HEADER_KEY, ANY_HEADER_VALUE); // fails
	}

}
