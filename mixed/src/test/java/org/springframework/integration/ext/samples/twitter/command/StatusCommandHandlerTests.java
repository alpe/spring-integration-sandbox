package org.springframework.integration.ext.samples.twitter.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

/**
 * @author Alex Peters
 * 
 */
@RunWith(MockitoJUnit44Runner.class)
public class StatusCommandHandlerTests {

	StatusCommandHandler handler;

	@Mock
	ControlCommand cmd;

	@Before
	public void setUp() {
		handler = new StatusCommandHandler();
	}

	@Test
	public void canHandle_validText_returnsTrue() {
		when(cmd.getText()).thenReturn("~status");
		assertThat(handler.canHandle(cmd), is(true));

		when(cmd.getText()).thenReturn("~status hallo");
		assertThat(handler.canHandle(cmd), is(true));
		when(cmd.getText()).thenReturn("blabla ~status hallo");
		assertThat(handler.canHandle(cmd), is(true));
		when(cmd.getText()).thenReturn("~Status");
		assertThat(handler.canHandle(cmd), is(true));
	}
}
