package org.springframework.integration.sqs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.sqs.SQSOutboundGateway.SQSPayloadSerializer;

/**
 * @author Alex Peters
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class SQSOutboundGatewayTests {

	private static final String anyQueueName = "anyQueueName";

	SQSOutboundGateway gateway;

	@Mock
	SQSSOAPTemplate template;

	@Mock
	SQSPayloadSerializer serializer;

	Message<?> anyMessage;

	final String msgPayload = "fooBar";

	@Before
	public void setUp() {
		gateway = new SQSOutboundGateway();
		gateway.setQueueName(anyQueueName);
		gateway.setTemplate(template);
		gateway.setSerializer(serializer);
		anyMessage = MessageBuilder.withPayload(msgPayload).build();
	}

	@Test
	public void handleMessage_withPayloadExtracted_encodedPayloadSubmitted() throws Exception {
		String serializedPayload = msgPayload;
		when(serializer.serialize(msgPayload)).thenReturn(serializedPayload);
		when(template.sendMessage(anyQueueName, serializedPayload)).thenReturn("sqsConfirmationId");
		gateway.sendMessage(anyMessage);
		verify(serializer).serialize(msgPayload);
		verify(template).sendMessage(anyQueueName, serializedPayload);
	}

	@Test
	public void handleMessage_withoutPayloadExtracted_encodedMessageSubmitted() throws Exception {
		gateway.setExtractPayload(false);
		String serializedPayload = "payload encoded";
		when(serializer.serialize(anyMessage)).thenReturn(serializedPayload);
		when(template.sendMessage(anyQueueName, serializedPayload)).thenReturn("sqsConfirmationId");
		gateway.sendMessage(anyMessage);
		verify(serializer).serialize(anyMessage);
		verify(template).sendMessage(anyQueueName, serializedPayload);
	}

}
