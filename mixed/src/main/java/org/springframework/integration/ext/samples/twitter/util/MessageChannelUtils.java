package org.springframework.integration.ext.samples.twitter.util;

import org.springframework.integration.channel.ChannelResolver;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * @author Alex Peters
 * 
 */
public class MessageChannelUtils {

	/**
	 * @param message
	 * @return
	 * @throws MessageHandlingException
	 */
	public static MessageChannel fetchReplyChannel(Message<?> message,
			ChannelResolver channelResolver) {
		// copy/paste from MessageHandlerChain.ReplyForwardingMessageChannel
		Object replyChannelHeader = message.getHeaders().getReplyChannel();
		if (replyChannelHeader == null) {
			throw new MessageHandlingException(message, "no replyChannel header available");
		}
		MessageChannel replyChannel = null;
		if (replyChannelHeader instanceof MessageChannel) {
			replyChannel = (MessageChannel) replyChannelHeader;
		}
		else if (replyChannelHeader instanceof String) {
			Assert.notNull(channelResolver, "ChannelResolver is required");
			replyChannel = channelResolver.resolveChannelName((String) replyChannelHeader);
		}
		else {
			throw new MessageHandlingException(message, "invalid replyChannel type ["
					+ replyChannelHeader.getClass() + "]");
		}
		return replyChannel;
	}
}
