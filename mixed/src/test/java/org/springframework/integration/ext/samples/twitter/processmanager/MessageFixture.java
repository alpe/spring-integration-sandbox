package org.springframework.integration.ext.samples.twitter.processmanager;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 * 
 */
public class MessageFixture {

	Object anyPayload = "foobar";

	public Message<Object> anyMessage() {
		return MessageBuilder.withPayload(anyPayload).build();
	}

}
