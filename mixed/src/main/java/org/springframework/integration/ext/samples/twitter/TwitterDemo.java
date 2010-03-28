package org.springframework.integration.ext.samples.twitter;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.BeanFactoryChannelResolver;
import org.springframework.integration.channel.ChannelResolver;

/**
 * @author Alex Peters
 * 
 */
public class TwitterDemo {

	public static void main(String[] args) throws InterruptedException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"twitterDemo.xml", TwitterDemo.class);
		ChannelResolver channelResolver = new BeanFactoryChannelResolver(context);
		// MessageChannel channel =
		// channelResolver.resolveChannelName("sqsQueue1");

		Thread.sleep(120 * 1000L);
		System.exit(0);
	}
}
