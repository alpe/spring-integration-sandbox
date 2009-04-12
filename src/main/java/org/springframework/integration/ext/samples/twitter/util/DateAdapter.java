package org.springframework.integration.ext.samples.twitter.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Jaxb Date parsing/ formatting adapter to handle twitter date and timezone.
 * @author Alex Peters
 * 
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	private static final String DATE_PATTERN = "EEE MMM dd HH:mm:ss Z yyyy";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(Date v) throws Exception {
		return getDateFormat().format(v);
	}

	private SimpleDateFormat getDateFormat() {
		// remind, simpledateformat is not thread safe
		SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date unmarshal(String v) throws Exception {
		return getDateFormat().parse(v);
	}
}
