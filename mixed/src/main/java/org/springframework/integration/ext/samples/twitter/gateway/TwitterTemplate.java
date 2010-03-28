package org.springframework.integration.ext.samples.twitter.gateway;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.integration.http.CommonsHttpRequestExecutor;
import org.springframework.integration.http.HttpResponse;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Alex Peters
 * 
 */
@Component("twitterTemplate")
public class TwitterTemplate {

	private static final Logger log = Logger.getLogger(TwitterTemplate.class);

	private final CommonsHttpRequestExecutor executor;

	/**
	 * @param userName
	 * @param password
	 */
	public TwitterTemplate(String userName, String password) {
		executor = new CommonsHttpRequestExecutor();
		HttpClient httpClient = executor.getHttpClient();
		httpClient.getParams().setAuthenticationPreemptive(true);
		Credentials defaultcreds = new UsernamePasswordCredentials(userName,
				password);
		httpClient.getState().setCredentials(
				new AuthScope("twitter.com", 80, AuthScope.ANY_REALM),
				defaultcreds);

	}

	public Document fetchDirectMessages() throws Exception {
		log.debug("Looking for new direct messages at twitter.com");
		HttpResponse response = executor.executeRequest(SimpleGetRequest
				.create("http://twitter.com/direct_messages.xml")); // ?since_id=12345
		String contentType = response.getFirstHeader("Content-Type");
		if (contentType == null
				|| !contentType.toLowerCase().contains("application/xml")) {
			throw new IllegalStateException("Unexpected content type: "
					+ contentType);
		}
		if (!contentType.contains("charset=utf-8")) {
			throw new IllegalStateException("Unexcepted encoding: "
					+ contentType);
		}
		DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
		builder.setNamespaceAware(true);
		Document result = builder.newDocumentBuilder().parse(
				new InputSource(response.getBody()));
		return result;
	}

	/**
	 * @param recipient
	 * @param text
	 * @throws IOException
	 * @throws HttpException
	 * @throws Exception
	 */
	public void sendDirectMessage(String recipient, String text)
			throws HttpException, IOException {
		log.debug("sending a direct message to: " + recipient);
		PostMethod post = new PostMethod(
				"http://twitter.com/direct_messages/new.xml");
		NameValuePair[] data = { new NameValuePair("user", recipient),
				new NameValuePair("text", text) };
		post.setRequestBody(data);
		executor.getHttpClient().executeMethod(post);
	}
}
