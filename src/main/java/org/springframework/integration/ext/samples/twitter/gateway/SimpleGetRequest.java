package org.springframework.integration.ext.samples.twitter.gateway;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.integration.http.HttpRequest;

/**
 * @author Alex Peters
 * 
 */
public class SimpleGetRequest implements HttpRequest {

	private final URL targetUrl;

	/**
	 * @param url
	 */
	private SimpleGetRequest(String url) {
		super();
		try {
			this.targetUrl = new URL(url);
		}
		catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + url, e);
		}
	}

	public static SimpleGetRequest create(String url) {
		return new SimpleGetRequest(url);

	}

	@Override
	public ByteArrayOutputStream getBody() {
		return null;
	}

	@Override
	public Integer getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getRequestMethod() {
		return "GET";
	}

	@Override
	public URL getTargetUrl() {
		return targetUrl;
	}

}
