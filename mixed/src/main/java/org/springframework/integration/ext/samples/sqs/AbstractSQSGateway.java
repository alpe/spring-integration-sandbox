package org.springframework.integration.ext.samples.sqs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 */
public abstract class AbstractSQSGateway implements InitializingBean {

	protected static final String DEFAULT_ENCODING = "US-ASCII"; // base 64

	protected volatile SQSSOAPTemplate template;

	private volatile boolean extractPayload = true;

	private volatile String queueName;

	public AbstractSQSGateway() {
		super();
	}

	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName the queueName to set
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(SQSSOAPTemplate template) {
		this.template = template;
	}

	/**
	 * @param extractPayload the extractPayload to set
	 */
	public void setExtractPayload(boolean extractPayload) {
		this.extractPayload = extractPayload;
	}

	/**
	 * @return the extractPayload
	 */
	public boolean isExtractPayload() {
		return extractPayload;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(queueName);
		Assert.notNull(template);
	}

}