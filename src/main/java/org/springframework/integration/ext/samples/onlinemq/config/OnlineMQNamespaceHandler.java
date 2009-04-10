package org.springframework.integration.ext.samples.onlinemq.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Spring namespace handler for onlinemq namespace.
 * @author Alex Peters
 *
 */
public class OnlineMQNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		this.registerBeanDefinitionParser("inbound-channel-adapter", new OnlineMQInboundGatewayParser());
		this.registerBeanDefinitionParser("outbound-gateway", new OnlineMQOutboundGatewayParser());
	}

}
