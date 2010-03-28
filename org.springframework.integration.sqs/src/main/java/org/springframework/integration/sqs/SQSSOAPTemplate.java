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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.apache.log4j.Logger;

import com.amazonaws.queue.doc._2009_02_01.Attribute;
import com.amazonaws.queue.doc._2009_02_01.CreateQueueResult;
import com.amazonaws.queue.doc._2009_02_01.Message;
import com.amazonaws.queue.doc._2009_02_01.MessageQueue;
import com.amazonaws.queue.doc._2009_02_01.MessageQueuePortType;
import com.amazonaws.queue.doc._2009_02_01.QueueService;
import com.amazonaws.queue.doc._2009_02_01.QueueServicePortType;
import com.amazonaws.queue.doc._2009_02_01.ReceiveMessageResult;
import com.amazonaws.queue.doc._2009_02_01.ResponseMetadata;
import com.amazonaws.queue.doc._2009_02_01.SendMessageResult;

/**
 * <p>
 * Template class that simplifies basic SQS operations.
 * </p>
 * Visit <a href="http://aws.amazon.com/sqs/faqs/" > AWS for more details.</a>
 * 
 * @author Alex Peters
 * 
 */
public class SQSSOAPTemplate {

	private static final Logger log = Logger.getLogger(SQSSOAPTemplate.class);

	private int visibilityTimeoutInSeconds = 15 * 60; // 12 hours maximum

	private final MessageQueue messageQueue;

	private final AWSSecurityHandler securityHandler;

	/**
	 * @param awsKey credentials
	 * @param awsSecret credentials
	 */
	public SQSSOAPTemplate(String awsKey, String awsSecret) {
		super();
		securityHandler = new AWSSecurityHandler(awsKey, awsSecret);
		messageQueue = new MessageQueue();
	}

	/**
	 * @return the visibilityTimeoutInSeconds
	 */
	public int getVisibilityTimeoutInSeconds() {
		return visibilityTimeoutInSeconds;
	}

	/**
	 * A value greater than transaction timeout. The current supported max is 12
	 * hours. See <a href="http://aws.amazon.com/sqs/faqs/#What_is_the_maximum_limit_for_message_visibility"
	 * >AWS.</a>
	 * 
	 * @param visibilityTimeoutInSeconds value
	 */
	public void setVisibilityTimeoutInSeconds(int visibilityTimeoutInSeconds) {
		this.visibilityTimeoutInSeconds = visibilityTimeoutInSeconds;
	}

	/**
	 * {@link #receive(String, int)} with
	 * {@link #getVisibilityTimeoutInSeconds()}.
	 * @param queueName the name of the SQS message queue
	 * @return message or <code>null</code> when none exists or is visible.
	 */
	public Message receive(String queueName) {
		return receive(queueName, visibilityTimeoutInSeconds);
	}

	/**
	 * Poll SQS queue with given name for a single message. The messages is not
	 * removed but will be invisible for another poll until the timeout.
	 * 
	 * See <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/index.html?AboutVT.html"
	 * > AWS for more details.</a>
	 * 
	 * To remove the message use {@link #commitReceive(String, Message)}.
	 * 
	 * @param queueName the name of the SQS message queue
	 * @param visibilityTimeoutInSeconds timeout value
	 * @return message or <code>null</code> when none exists or is visible.
	 */
	public Message receive(String queueName, int visibilityTimeoutInSeconds) {
		MessageQueuePortType port = getMessagePort(queueName);
		BigInteger maxNumberOfMessages = BigInteger.valueOf(1L); // max 10
		Holder<ReceiveMessageResult> receiveMessageResult = new Holder<ReceiveMessageResult>();
		Holder<ResponseMetadata> responseMetadata = new Holder<ResponseMetadata>();
		List<String> attributes = Collections.emptyList();
		String unused = ""; // aws, what is this for?
		port.receiveMessage(maxNumberOfMessages, BigInteger.valueOf(visibilityTimeoutInSeconds), attributes, unused,
				receiveMessageResult, responseMetadata);
		List<Message> messages = receiveMessageResult.value.getMessage();
		if (messages.isEmpty()) {
			return null;
		}
		return messages.get(0);
	}

	/**
	 * @param queueName the name of the SQS message queue
	 * @param message the message. must not be <code>null</code>.
	 */
	public void commitReceive(String queueName, Message message) {
		commitReceive(queueName, message.getReceiptHandle());
	}

	/**
	 * @param queueName the name of the SQS message queue
	 * @param receiptHandle the receipt handle. must not be <code>null</code>.
	 */
	public void commitReceive(String queueName, String receiptHandle) {
		log.debug("Deleting message with handle: " + receiptHandle);
		MessageQueuePortType port = getMessagePort(queueName);
		List<Attribute> attributes = Collections.emptyList();
		port.deleteMessage(receiptHandle, attributes);
	}

	/**
	 * @param queueName the name of the SQS message queue
	 * @param payload must not be >8kb
	 * @return sqs message id
	 */
	public String sendMessage(String queueName, String payload) {
		MessageQueuePortType port = getMessagePort(queueName);
		Holder<SendMessageResult> sendMessageResult = new Holder<SendMessageResult>();
		Holder<ResponseMetadata> responseMetadata = new Holder<ResponseMetadata>();
		List<Attribute> attributes = Collections.emptyList();
		port.sendMessage(payload, attributes, sendMessageResult, responseMetadata);
		log.debug("Sent message-id: " + sendMessageResult.value.getMessageId());
		return sendMessageResult.value.getMessageId();

	}

	/**
	 * @param queueName the name of the SQS message queue
	 */
	public void createQueue(String queueName) {
		QueueServicePortType port = new QueueService().getQueueServiceHttpsPort();
		addSecurityHandler((BindingProvider) port);
		Holder<CreateQueueResult> createQueueResult = new Holder<CreateQueueResult>();
		Holder<ResponseMetadata> responseMetadata = new Holder<ResponseMetadata>();
		List<Attribute> attributes = Collections.emptyList();
		port.createQueue(queueName, BigInteger.valueOf(visibilityTimeoutInSeconds), attributes, createQueueResult,
				responseMetadata);
		CreateQueueResult createResult = createQueueResult.value;
		log.debug("Created queue: " + createResult.getQueueUrl());
	}

	/**
	 * Get Soap Port. For internal usage only.
	 * @param queueName the name of the SQS message queue
	 * @return instance.
	 */
	MessageQueuePortType getMessagePort(String queueName) {
		MessageQueuePortType port = messageQueue.getMessageQueueHttpsPort();
		addSecurityHandler((BindingProvider) port);
		Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
		try {
			String baseURL = (String) requestContext.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, baseURL + '/'
					+ URLEncoder.encode(queueName, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		return port;
	}

	/**
	 * Bind security handler to port instance.
	 * @param port source
	 */
	@SuppressWarnings("unchecked")
	void addSecurityHandler(BindingProvider port) {
		Binding binding = port.getBinding();
		List<Handler> handlerChain = binding.getHandlerChain();
		handlerChain.add(securityHandler);
		binding.setHandlerChain(handlerChain);
	}

}
