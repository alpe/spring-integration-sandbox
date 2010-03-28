package org.springframework.integration.ext.samples.cafe.esper;

import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;

/**
 * Transforming Esper message to printable string.
 * 
 * @author Alex Peters
 */
@MessageEndpoint
public class DurationToStringTransformer {

	/**
	 * Transform message.
	 * 
	 * @param orderNumber order number
	 * @param duration duration in ms
	 * @return string representation
	 */
	@Transformer(inputChannel = "esperOrderDuration", outputChannel = "esperOut")
	public String transform(@Header("orderNumber") int orderNumber, long duration) {
		return String.format("[ESPER]Transformed: order number %s had a duration of %s ms.\n",
				orderNumber, duration);
	}
}
