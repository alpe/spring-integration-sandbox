package org.springframework.integration.test.matcher;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

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

	// see HeaderMatcherTests

	@Test
	public void hasKey_validKey_matching() throws Exception {
		assertThat(map, MapContains.hasKey(ANY_KEY));
	}

	@Test
	public void hasKey_unknownKey_notMatching() throws Exception {
		assertThat(map, not(MapContains.hasKey(UNKNOWN_KEY)));
	}
}
