package org.springframework.integration.test.core;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 * 
 */
public class TestMessageFactory {

	/**
	 * @return
	 */
	public static Message<?> createAnyMessage() {
		return MessageBuilder.withPayload("fooBar").build();
	}
}
