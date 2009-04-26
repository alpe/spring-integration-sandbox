package org.springframework.integration.test.matcher;

import static org.hamcrest.CoreMatchers.anything;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.IsEqual;
import org.junit.matchers.TypeSafeMatcher;

/**
 * <h2>Is the Map containing any or multiple entry that match?</h2> <br />
 * <h3>Match a single entry by value or matcher. For example:</h3>
 * 
 * <pre>
 * assertThat(map, hasEntry(ANY_KEY, is(ANY_VALUE)));
 * assertThat(map, hasEntry(ANY_KEY, is(String.class)));
 * assertThat(map, hasEntry(ANY_KEY, notNullValue()));
 * </pre>
 * 
 * <h3>Match multiple entries in map:</h3>
 * 
 * <pre>
 * Map&lt;String, Object&gt; expectedInHeaderMap = new HashMap&lt;String, Object&gt;();
 * expectedInHeaderMap.put(ANY_KEY, ANY_VALUE);
 * expectedInHeaderMap.put(OTHER_KEY, is(OTHER_VALUE));
 * assertThat(map, hasAllEntries(expectedInHeaderMap));
 * </pre>
 * 
 * <h3>Has single key:</h3>
 * 
 * <pre>
 * assertThat(map, hasKey(ANY_KEY));
 * </pre>
 * 
 * @author Alex Peters
 * 
 */
public class MapContains<T, V> extends TypeSafeMatcher<Map<? super T, ? super V>> {

	private final T key;

	private final Matcher<V> valueMatcher;

	/**
	 * @param key
	 * @param value
	 */
	MapContains(T key, V value) {
		this(key, IsEqual.equalTo(value));
	}

	/**
	 * @param key
	 * @param valueMatcher
	 */
	MapContains(T key, Matcher<V> valueMatcher) {
		super();
		this.key = key;
		this.valueMatcher = valueMatcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesSafely(Map<? super T, ? super V> item) {
		return item.containsKey(key) && valueMatcher.matches(item.get(key));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("a Map entry with key ").appendValue(key).appendText(
				" and value matching ").appendDescriptionOf(valueMatcher);

	}

	@Factory
	public static <T, V> Matcher<Map<? super T, ? super V>> hasEntry(T key, V value) {
		return new MapContains<T, V>(key, value);
	}

	@Factory
	public static <T, V> Matcher<Map<? super T, ? super V>> hasEntry(T key, Matcher<V> valueMatcher) {
		return new MapContains<T, V>(key, valueMatcher);
	}

	@Factory
	@SuppressWarnings("unchecked")
	public static <T, V> Matcher<Map<? super T, ? super V>> hasKey(T key) {
		return new MapContains<T, V>(key, (Matcher<V>) anything("any Value"));
	}

	@Factory
	@SuppressWarnings("unchecked")
	public static <T, V> Matcher<Map<? super T, ? super V>> hasAllEntries(Map<T, V> entries) {
		List<Matcher<? extends Map<? super T, ? super V>>> matchers = new ArrayList<Matcher<? extends Map<? super T, ? super V>>>(
				entries.size());
		for (Map.Entry<T, V> entry : entries.entrySet()) {
			final V value = entry.getValue();
			if (value instanceof Matcher<?>) {
				matchers.add(hasEntry(entry.getKey(), (Matcher<V>) value));
			}
			else {
				matchers.add(hasEntry(entry.getKey(), value));
			}
		}
		return AllOf.allOf(matchers);
	}
}
