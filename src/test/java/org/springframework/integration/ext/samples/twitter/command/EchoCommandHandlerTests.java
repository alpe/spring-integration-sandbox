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
public class EchoCommandHandlerTests {

	EchoCommandHandler handler;

	@Mock
	ControlCommand cmd;

	@Before
	public void setUp() {
		handler = new EchoCommandHandler();
	}

	@Test
	public void canHandle_validText_returnsTrue() {
		when(cmd.getText()).thenReturn("~echo");
		assertThat(handler.canHandle(cmd), is(true));
		when(cmd.getText()).thenReturn("~echo hallo");
		assertThat(handler.canHandle(cmd), is(true));
		when(cmd.getText()).thenReturn("blabla ~echo hallo");
		assertThat(handler.canHandle(cmd), is(true));
		when(cmd.getText()).thenReturn("~Echo");
		assertThat(handler.canHandle(cmd), is(true));
	}

	@Test
	public void canHandle_invalidText_returnsFalse() {
		when(cmd.getText()).thenReturn("~ech");
		assertThat(handler.canHandle(cmd), is(false));
		when(cmd.getText()).thenReturn("echo");
		assertThat(handler.canHandle(cmd), is(false));
	}

	@Test
	public void handle_withValidString_returnsAllAfterCommand() throws Exception {
		when(cmd.getText()).thenReturn("~echo fooBar with ketchup");
		assertThat(handler.handle(cmd), is("fooBar with ketchup"));
		when(cmd.getText()).thenReturn("blabla ~echo fooBar with ketchup");
		assertThat(handler.handle(cmd), is("fooBar with ketchup"));
	}
}
