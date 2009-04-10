package org.springframework.integration.ext.samples.sqs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import javax.xml.ws.BindingProvider;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.queue.doc._2009_02_01.MessageQueuePortType;

/**
 * @author Alex Peters
 * 
 */
public class SQSSOAPTemplateTests {

	SQSSOAPTemplate template;

	@Before
	public void setUp() {
		template = new SQSSOAPTemplate("anyKey", "anySecret");
	}

	@Test
	public void getPort_isThreadsafe() throws Exception {
		MessageQueuePortType port1 = template.getMessagePort("anyQueue");
		MessageQueuePortType port2 = template.getMessagePort("anyQueue2");
		assertThat(port1, not(sameInstance(port2)));
	}

	@Test
	public void getPort_destinationAddressContainsQueuName() throws Exception {
		final String anyQueueName = "anyQueue";
		MessageQueuePortType port = template.getMessagePort(anyQueueName);
		String destAddress = (String) ((BindingProvider) port).getRequestContext().get(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
		assertThat(destAddress, is("https://queue.amazonaws.com/anyQueue"));
	}
}
