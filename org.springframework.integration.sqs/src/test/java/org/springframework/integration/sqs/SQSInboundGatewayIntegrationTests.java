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
import org.springframework.integration.sqs.SQSInboundGateway.SQSPayloadDeserializer;
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
@ContextConfiguration
public class SQSInboundGatewayIntegrationTests {

	private static final String anyQueueName = "anyQueueName";

	@Autowired
	TransactionTemplate transactionTemplate;

	SQSInboundGateway inGateway;

	SQSSOAPTemplate template;

	com.amazonaws.queue.doc._2009_02_01.Message anyMessage;

	SQSPayloadDeserializer deserializer;

	Object anyPayload;

	String anyReceiptHandle = "anyReceiptHandle";

	@Before
	public void setUp() {
		template = mock(SQSSOAPTemplate.class);
		deserializer = mock(SQSPayloadDeserializer.class);
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
	@SuppressWarnings("unchecked")
	public void receive_transactionCommited_messageDeletedAtSQS() throws Exception {
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(final TransactionStatus status) {
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
		verify(template).commitReceive(anyQueueName, anyReceiptHandle);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void recieve_transactionRollback_messageNotDeletedAtSQS() throws Exception {
		transactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(final TransactionStatus status) {
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
		verify(template, never()).commitReceive((String) anyObject(), (String) anyObject());
	}
}