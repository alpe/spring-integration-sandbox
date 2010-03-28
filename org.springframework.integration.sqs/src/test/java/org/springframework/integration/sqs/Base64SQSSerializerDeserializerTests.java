package org.springframework.integration.sqs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;
import org.springframework.integration.sqs.SQSInboundGateway.Base64SQSPayloadDeserializer;
import org.springframework.integration.sqs.SQSOutboundGateway.Base64SQSPayloadSerializer;

/**
 * @author Alex Peters
 * 
 */
public class Base64SQSSerializerDeserializerTests {

	@Test
	public void base64SerializerDeserializer() throws Exception {
		Object[] sources = { "anyPayload", 567L, Collections.singleton("otherPayload") };
		for (Object source : sources) {
			String serializedPayload = new Base64SQSPayloadSerializer().serialize(source);
			Object deserialize = new Base64SQSPayloadDeserializer().deserialize(serializedPayload);
			assertThat("failed with object:" + source, deserialize, is(source));
		}
	}
}
