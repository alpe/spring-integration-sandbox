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

import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.BeanFactoryChannelResolver;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 */
public class SQSDemo {

	public static void main(final String[] args) throws InterruptedException {
		System.out.println("started");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/sqsDemo.xml", SQSDemo.class);
		MessageChannel channel = new BeanFactoryChannelResolver(context).resolveChannelName("sqsQueue1");
		// send some sample messages
		for (int i = 0; i < 10; i++) {
			System.out.println("sending message: " + i);
			String requestMsg = "" + i + " any message text: '" + new Date() + "'...";
			Message<String> message = MessageBuilder.withPayload(requestMsg).build();
			channel.send(message);
			// wait
			Thread.sleep(1 * 1000L);
		}
		System.out.println("completed");
		System.exit(0);
	}

}
