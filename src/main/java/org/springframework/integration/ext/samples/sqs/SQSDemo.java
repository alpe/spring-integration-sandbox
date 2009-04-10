package org.springframework.integration.ext.samples.sqs;

import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.BeanFactoryChannelResolver;
import org.springframework.integration.channel.ChannelResolver;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 */
public class SQSDemo {

	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("sqsDemo.xml",
				SQSDemo.class);
		ChannelResolver channelResolver = new BeanFactoryChannelResolver(context);
		MessageChannel channel = channelResolver.resolveChannelName("sqsQueue1");
		// send some sample messages
		for (int i = 0; i < 10; i++) {
			String requestMsg = "any message text: '" + new Date() + "'...";
			Message<String> message = MessageBuilder.withPayload(requestMsg).build();
			channel.send(message);
			// wait
			Thread.sleep(1 * 1000L);
		}
		System.exit(0);
	}

}
