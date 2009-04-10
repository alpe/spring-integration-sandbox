/**
 * 
 */
package org.springframework.integration.ext.samples.onlinemq.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractConsumerEndpointParser;
import org.springframework.integration.ext.samples.onlinemq.OnlineMQOutboundGateway;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Alex Peters
 * 
 */
public class OnlineMQOutboundGatewayParser extends AbstractConsumerEndpointParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getInputChannelAttributeName() {
		return "request-channel";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BeanDefinitionBuilder parseHandler(Element element, ParserContext context) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(OnlineMQOutboundGateway.class);

		String template = element.getAttribute("template");
		String queueName = element.getAttribute("queueName");
		if (!StringUtils.hasText(template) || !StringUtils.hasText(queueName)) {
			throw new BeanCreationException(String.format(
					"Failed to create Bean with template bean ref=%s and queueName=%s", template,
					queueName));
		}

		builder.addPropertyReference("template", template);
		builder.addPropertyValue("queueName", queueName);
		String extractPayloadValue = element.getAttribute("extract-payload");
		if (StringUtils.hasText(extractPayloadValue)) {
			builder.addPropertyValue("extractPayload", extractPayloadValue);
		}
		return builder;

	}

}
