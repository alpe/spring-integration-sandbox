package org.springframework.integration.test.matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.integration.test.matcher.MapContains.hasAllEntries;
import static org.springframework.integration.test.matcher.MapContains.hasEntry;
import static org.springframework.integration.test.matcher.MapContains.hasKey;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alex Peters
 * 
 */
public class MapContainsTests {

	static final String UNKNOWN_KEY = "unknownKey";

	static final String ANY_VALUE = "bar";

	static final String ANY_KEY = "test.foo";

	static final String OTHER_KEY = "test.number";

	static final Integer OTHER_VALUE = Integer.valueOf(123);

	private HashMap<String, Object> map;

	@Before
	public void setUp() {
		map = new HashMap<String, Object>();
		map.put(ANY_KEY, ANY_VALUE);
		map.put(OTHER_KEY, OTHER_VALUE);
	}

	@Test
	public void hasKey_validKey_matching() throws Exception {
		assertThat(map, hasKey(ANY_KEY));

	}

	@Test
	public void hasKey_unknownKey_notMatching() throws Exception {
		assertThat(map, not(hasKey(UNKNOWN_KEY)));
	}

	@Test
	public void hasEntry_withValidKeyValue_matches() throws Exception {
		assertThat(map, hasEntry(ANY_KEY, ANY_VALUE));
		assertThat(map, hasEntry(OTHER_KEY, OTHER_VALUE));
	}

	@Test
	public void hasEntry_withUnknownKey_notMatching() throws Exception {
		assertThat(map, not(hasEntry("test.unknown", ANY_VALUE)));
	}

	@Test
	public void hasEntry_withValidKeyAndMatcherValue_matches() throws Exception {
		assertThat(map, hasEntry(ANY_KEY, is(String.class)));
		assertThat(map, hasEntry(ANY_KEY, notNullValue()));
		assertThat(map, hasEntry(ANY_KEY, is(ANY_VALUE)));
	}

	@Test
	public void hasEntry_withValidKeyAndMatcherValue_notMatching() throws Exception {
		assertThat(map, not(hasEntry(ANY_KEY, is(Integer.class))));
	}

	@Test
	public void hasEntry_withTypedValueMap_matches() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", "b");
		map.put("c", "d");
		assertThat(map, hasEntry("a", "b"));
		assertThat(map, not(hasEntry(ANY_KEY, is("a"))));
		assertThat(map, hasAllEntries(map));
	}

	@Test
	public void hasAllEntries_withValidKeyValueOrMatcherValue_matches() throws Exception {
		Map<String, Object> expectedInHeaderMap = new HashMap<String, Object>();
		expectedInHeaderMap.put(ANY_KEY, ANY_VALUE);
		expectedInHeaderMap.put(OTHER_KEY, is(OTHER_VALUE));
		assertThat(map, hasAllEntries(expectedInHeaderMap));
	}

	@Test
	public void hasAllEntries_withInvalidValidKeyValueOrMatcherValue_notMatching() throws Exception {
		Map<String, Object> expectedInHeaderMap = new HashMap<String, Object>();
		expectedInHeaderMap.put(ANY_KEY, ANY_VALUE); // valid
		expectedInHeaderMap.put(UNKNOWN_KEY, not(nullValue())); // fails
		assertThat(map, not(hasAllEntries(expectedInHeaderMap)));
		expectedInHeaderMap.remove(UNKNOWN_KEY);
		expectedInHeaderMap.put(OTHER_KEY, ANY_VALUE); // fails
	}
}
