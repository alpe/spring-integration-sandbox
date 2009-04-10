package org.springframework.integration.ext.samples.onlinemq.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.ext.samples.onlinemq.OnlineMQInboundGateway;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Alex Peters
 * 
 */
public class OnlineMQInboundGatewayParser extends AbstractPollingInboundChannelAdapterParser {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseSource(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(OnlineMQInboundGateway.class);
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
		return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(),
				parserContext.getRegistry());
	}

}
