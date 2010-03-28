package org.springframework.integration.ext.samples.twitter.gateway;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

/**
 * @author Alex Peters
 * 
 */
@Component("directMessageInChannelAdapter")
public class DirectMessageInboundGateway implements MessageSource<Document>, InitializingBean {

	private static final Logger log = Logger.getLogger(DirectMessageInboundGateway.class);

	@Autowired
	private TwitterTemplate template;

	public DirectMessageInboundGateway() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(template);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message<Document> receive() {
		Document payload;
		try {
			payload = template.fetchDirectMessages();
			return MessageBuilder.withPayload(payload).build();
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to pull direct messages.", e);
		}
	}
}
