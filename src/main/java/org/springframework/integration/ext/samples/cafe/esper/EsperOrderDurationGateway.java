package org.springframework.integration.ext.samples.cafe.esper;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.Header;

/**
 * Abstract gateway into spring integration.
 * 
 * @author ap
 * 
 */
public interface EsperOrderDurationGateway {

    /**
     * 
     * @param orderNumber
     *            order number
     * @param duration
     *            endTime-startTime of order
     */
    @Gateway(requestChannel = "esperOrderDuration")
    public void update(@Header("orderNumber") int orderNumber, long duration);
}
