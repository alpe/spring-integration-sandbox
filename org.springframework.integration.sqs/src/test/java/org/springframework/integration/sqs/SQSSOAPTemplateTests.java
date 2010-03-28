/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.sqs;

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
