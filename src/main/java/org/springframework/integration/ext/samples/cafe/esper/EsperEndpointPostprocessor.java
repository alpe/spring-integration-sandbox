package org.springframework.integration.ext.samples.cafe.esper;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.ChannelInterceptor;
import org.springframework.integration.channel.interceptor.ChannelInterceptorAdapter;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.w3c.dom.Node;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

/**
 * Spring Postprocessor to add Esper publishing method interceptor to channels.
 * 
 * @author ap
 * 
 */
public class EsperEndpointPostprocessor implements BeanPostProcessor {

	private static final Logger log = Logger.getLogger(EsperEndpointPostprocessor.class);

	/** publishing interceptor instance. */
	private final ChannelInterceptor interceptor;

	/** name of channels. */
	private Collection<String> channelsToIntercept;

	/**
	 * Default constructor.
	 */
	public EsperEndpointPostprocessor() {
		interceptor = new EsperChannelInterceptor();
	}

	/**
	 * @param channelsToIntercept the channelsToIntercept to set
	 */
	public void setChannelsToIntercept(Collection<String> channelsToIntercept) {
		this.channelsToIntercept = channelsToIntercept;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof AbstractMessageChannel) {
			if (channelsToIntercept.contains(beanName)) {
				log.debug("Adding esper interceptor to channel: " + beanName + ">"
						+ bean.toString());
				((AbstractMessageChannel) bean).addInterceptor(interceptor);
			}
		}

		if (bean instanceof ServiceActivatingHandler) {

		}
		return bean;
	}

	/**
	 * Channel interceptor to push channel messages into the esper runtime.
	 * 
	 * @author ap
	 * 
	 */
	@Configurable
	class EsperChannelInterceptor extends ChannelInterceptorAdapter {

		/** Runtime instance. */
		private final EPRuntime esperRuntime;

		/**
		 * Default constructor.
		 */
		public EsperChannelInterceptor() {
			super();
			EPServiceProvider esperServiceProvider = EPServiceProviderManager.getDefaultProvider();
			this.esperRuntime = esperServiceProvider.getEPRuntime();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Message<?> preSend(Message<?> message, MessageChannel channel) {
			Object payload = message.getPayload();
			if (payload instanceof Node) {
				esperRuntime.sendEvent((Node) payload);
			}
			else {
				esperRuntime.sendEvent(payload);
			}
			return message;
		}

	}

}
