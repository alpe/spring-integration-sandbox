/**
 * 
 */
package org.springframework.integration.ext.samples.cafe.esper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.integration.ext.samples.cafe.Delivery;
import org.springframework.integration.ext.samples.cafe.Order;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * Annotation based spring config.
 * 
 * @author ap
 * 
 */
@Configuration
public class EsperStatementsInitialization {

    @Autowired
    EsperOrderDurationGateway gateway;

    @Bean
    public EPServiceProvider getEpService() {
	return EPServiceProviderManager.getDefaultProvider();
    }

    /**
     * Sample EPL statement to fetch orderNumber and duration from ordering to
     * delivery with a subscriber attached who submits the result into spring
     * integration via gateway.
     * 
     * @return statement
     */
    @Bean
    public EPStatement getDurationSelectWithRegistredSubscriber() {
	String expression = String
		.format(
			"select cOrder.number as number, (cDelivery.dateCreated - cOrder.dateCreated) as duration from `%s`.win:time(30 min) as cOrder, "
				+ "%s.std:lastevent() as cDelivery where cDelivery.orderNumber = cOrder.number",
				Order.class.getName(), Delivery.class.getName());
	EPStatement statement = getEpService().getEPAdministrator().createEPL(
		expression);
	statement.setSubscriber(gateway);
	return statement;
    }

}
