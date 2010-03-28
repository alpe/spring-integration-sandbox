/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.sqs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Generic SQS Gateway with common methods.
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
	public void setQueueName(final String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(final SQSSOAPTemplate template) {
		this.template = template;
	}

	/**
	 * @param extractPayload the extractPayload to set
	 */
	public void setExtractPayload(final boolean extractPayload) {
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
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(queueName);
		Assert.notNull(template);
	}

}