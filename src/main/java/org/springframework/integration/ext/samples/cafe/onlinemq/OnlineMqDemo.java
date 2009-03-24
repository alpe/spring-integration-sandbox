package org.springframework.integration.ext.samples.cafe.onlinemq;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.BeanFactoryChannelResolver;
import org.springframework.integration.channel.ChannelResolver;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Alex Peters
 */
public class OnlineMqDemo {

	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"onlinemqDemo.xml", OnlineMqDemo.class);
		ChannelResolver channelResolver = new BeanFactoryChannelResolver(context);
		
		String requestXml = "irgend ein message text";

		// Create the Message object
		Message<String> message = MessageBuilder.withPayload(requestXml).build();

		// Send the Message to the handler's input channel
		MessageChannel channel = channelResolver.resolveChannelName("sampleQueue1");
		channel.send(message);
		OnlineMQInboundGateway inGateway = (OnlineMQInboundGateway) context
				.getBean("inboundGateway");
		// wait
		Thread.sleep(60*1000L);
		System.exit(0);
	}

}
