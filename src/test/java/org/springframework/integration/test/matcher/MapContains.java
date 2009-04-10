package org.springframework.integration.test.matcher;

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
 * @author Alex Peters
 *
 */
public class MapContains<T, V> extends TypeSafeMatcher<Map<T, V>> {

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
	public boolean matchesSafely(Map<T, V> item) {
		return valueMatcher.matches(item.get(key));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("a Map entry with key ").appendValue(key).appendText(
				"' and value matching").appendDescriptionOf(valueMatcher);

	}

	@Factory
	public static <T, V> Matcher<Map<T, V>> hasEntry(T key, V value) {
		return new MapContains<T, V>(key, value);
	}

	@Factory
	public static <T, V> Matcher<Map<T, V>> hasEntry(T key, Matcher<V> valueMatcher) {
		return new MapContains<T, V>(key, valueMatcher);
	}

	@Factory
	public static <T, V> Matcher<?> hasEntries(Map<T, V> entries) {
		List<Matcher<?>> matchers = new ArrayList<Matcher<?>>(entries.size());
		for (Map.Entry<T, V> entry : entries.entrySet()) {
			final V value = entry.getValue();
			if (value instanceof Matcher<?>) {
				matchers.add(MapContains.hasEntry(entry.getKey(), (Matcher<?>) value));
			}
			else {
				matchers.add(MapContains.hasEntry(entry.getKey(), value));
			}
		}
		return AllOf.allOf(matchers);
	}

}
