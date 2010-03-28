package org.springframework.integration.ext.samples.twitter.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.ext.samples.twitter.util.DateAdapter;

public class DateAdapterTests {

	DateAdapter adapter;

	Calendar cal;

	@Before
	public void setUp() {
		adapter = new DateAdapter();
		cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.DAY_OF_MONTH, 24);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2009);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 14);
		cal.set(Calendar.SECOND, 29);
		cal.set(Calendar.MILLISECOND, 0);

	}

	@Test
	public void marshall() throws Exception {
		assertThat(adapter.marshal(cal.getTime()), is("Sat Jan 24 22:14:29 +0000 2009"));
	}

	@Test
	public void unmarshall() throws Exception {
		assertThat(adapter.unmarshal("Sat Jan 24 22:14:29 +0000 2009"), is(cal.getTime()));
	}
}
