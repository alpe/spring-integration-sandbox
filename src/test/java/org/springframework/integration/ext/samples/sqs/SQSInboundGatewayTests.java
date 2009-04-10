package org.springframework.integration.ext.samples.sqs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.integration.test.matcher.PayloadMatcher.hasPayload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;
import org.springframework.integration.ext.samples.sqs.SQSInboundGateway.SQSPayloadDeserializingTransformer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Alex Peters
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:org/springframework/integration/ext/samples/sqs/SQSInboundGatewayTests.xml", inheritLocations = true)
public class SQSInboundGatewayTests {

	private static final String anyQueueName = "anyQueueName";

	@Autowired
	TransactionTemplate transactionTemplate;

	SQSInboundGateway inGateway;

	SQSSOAPTemplate template;

	com.amazonaws.queue.doc._2009_02_01.Message anyMessage;

	SQSPayloadDeserializingTransformer deserializer;

	Object anyPayload;

	String anyReceiptHandle = "anyReceiptHandle";

	@Before
	public void setUp() {
		template = mock(SQSSOAPTemplate.class);
		deserializer = mock(SQSPayloadDeserializingTransformer.class);
		anyMessage = mock(com.amazonaws.queue.doc._2009_02_01.Message.class);
		inGateway = new SQSInboundGateway();
		inGateway.setQueueName(anyQueueName);
		inGateway.setTemplate(template);
		inGateway.setDeserializer(deserializer);
		anyPayload = "hallo Welt!";
		anyReceiptHandle = "blabla";
		when(anyMessage.getReceiptHandle()).thenReturn(anyReceiptHandle);
	}

	@Test
	public void receive_transactionCommited_messageDeletedAtSQS() throws Exception {
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				try {
					when(template.receive(anyQueueName)).thenReturn(anyMessage);
					when(deserializer.deserialize((String) anyObject())).thenReturn(anyPayload);
					Message<Object> result = inGateway.receive();
					assertThat(result, is(notNullValue()));
					assertThat(result, hasPayload(anyPayload));
				}
				catch (Exception e) {
					fail(e.getMessage());
				}
				return status;
			}
		});
		verify(template).comitReceive(anyQueueName, anyReceiptHandle);
	}

	@Test
	public void recieve_transactionRollback_messageNotDeletedAtSQS() throws Exception {
		transactionTemplate.execute(new TransactionCallback() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				try {
					when(template.receive(anyQueueName)).thenReturn(anyMessage);
					when(deserializer.deserialize((String) anyObject())).thenReturn(anyPayload);
					Message<Object> result = inGateway.receive();
					// now out of receive method. values ok?
					assertThat(result, is(notNullValue()));
					assertThat(result, hasPayload(anyPayload));
					// -- finally rollback
					status.setRollbackOnly();
				}
				catch (Exception e) {
					fail(e.getMessage());
				}
				return status;
			}
		});
		// message not deleted at sqs
		verify(template, never()).comitReceive((String) anyObject(), (String) anyObject());
	}
}
