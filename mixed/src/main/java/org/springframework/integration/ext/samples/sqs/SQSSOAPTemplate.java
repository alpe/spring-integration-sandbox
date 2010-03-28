package org.springframework.integration.ext.samples.sqs;

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
 * @author Alex Peters
 * 
 */
public class SQSSOAPTemplate {

	private static final Logger log = Logger.getLogger(SQSSOAPTemplate.class);

	/** should be greater than transaction timeout */
	private final int visibilityTimeoutInSeconds = 15 * 60; // max7200

	private final MessageQueue messageQueue;

	private final AWSSecurityHandler securityHandler;

	// 7200

	/**
	 * @param awsKey
	 * @param awsSecret
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
	 * @param queueName
	 * @return
	 */
	public Message receive(String queueName) {
		return receive(queueName, visibilityTimeoutInSeconds);
	}

	/**
	 * @param queueName
	 * @param visibilityTimeoutInSeconds
	 * @return
	 */
	public Message receive(String queueName, int visibilityTimeoutInSeconds) {
		MessageQueuePortType port = getMessagePort(queueName);
		BigInteger maxNumberOfMessages = BigInteger.valueOf(1L); // max 10
		Holder<ReceiveMessageResult> receiveMessageResult = new Holder<ReceiveMessageResult>();
		Holder<ResponseMetadata> responseMetadata = new Holder<ResponseMetadata>();
		List<String> attributes = Collections.emptyList();
		String unused = ""; // aws, what is this for?
		port.receiveMessage(maxNumberOfMessages, BigInteger.valueOf(visibilityTimeoutInSeconds),
				attributes, unused, receiveMessageResult, responseMetadata);
		List<Message> messages = receiveMessageResult.value.getMessage();
		if (messages.isEmpty()) {
			return null;
		}
		return messages.get(0);
	}

	/**
	 * @param queueName
	 * @param message
	 */
	protected void comitReceive(String queueName, Message message) {
		comitReceive(queueName, message.getReceiptHandle());
	}

	/**
	 * @param queueName
	 * @param receiptHandle
	 */
	protected void comitReceive(String queueName, String receiptHandle) {
		log.debug("Deleting message with handle: " + receiptHandle);
		MessageQueuePortType port = getMessagePort(queueName);
		List<Attribute> attributes = Collections.emptyList();
		port.deleteMessage(receiptHandle, attributes);
	}

	/**
	 * @param queueName
	 * @param payload must not be >8kb
	 */
	public void sendMessage(String queueName, String payload) {
		MessageQueuePortType port = getMessagePort(queueName);
		Holder<SendMessageResult> sendMessageResult = new Holder<SendMessageResult>();
		Holder<ResponseMetadata> responseMetadata = new Holder<ResponseMetadata>();
		List<Attribute> attributes = Collections.emptyList();
		port.sendMessage(payload, attributes, sendMessageResult, responseMetadata);
		log.debug("Sent message-id: " + sendMessageResult.value.getMessageId());
	}

	/**
	 * @param queueName
	 */
	public void createQueue(String queueName) {
		QueueServicePortType port = new QueueService().getQueueServiceHttpsPort();
		addSecurityHandler((BindingProvider) port);
		Holder<CreateQueueResult> createQueueResult = new Holder<CreateQueueResult>();
		Holder<ResponseMetadata> responseMetadata = new Holder<ResponseMetadata>();
		List<Attribute> attributes = Collections.emptyList();
		port.createQueue(queueName, BigInteger.valueOf(visibilityTimeoutInSeconds), attributes,
				createQueueResult, responseMetadata);
		CreateQueueResult createResult = createQueueResult.value;
		log.debug("Created queue: " + createResult.getQueueUrl());
	}

	/**
	 * @param queueName
	 * @return
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
	 * @param port
	 */
	@SuppressWarnings("unchecked")
	void addSecurityHandler(BindingProvider port) {
		Binding binding = port.getBinding();
		List<Handler> handlerChain = binding.getHandlerChain();
		handlerChain.add(securityHandler);
		binding.setHandlerChain(handlerChain);
	}

}
