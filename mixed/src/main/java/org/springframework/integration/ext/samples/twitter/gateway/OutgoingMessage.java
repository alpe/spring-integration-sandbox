package org.springframework.integration.ext.samples.twitter.gateway;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Alex Peters
 * 
 */
public class OutgoingMessage {

	private final String recipientIdentifier;

	private final String text;

	/**
	 * @param recipientIdentifier
	 * @param text
	 */
	private OutgoingMessage(String recipientIdentifier, String text) {
		super();
		this.recipientIdentifier = recipientIdentifier;
		this.text = text;
	}

	public static OutgoingMessage create(String recipientIdentifier, String text) {
		Assert.isTrue(StringUtils.hasText(text));
		Assert.isTrue(text.length() < 141);
		return new OutgoingMessage(recipientIdentifier, text);
	}

	/**
	 * @return
	 */
	public String getRecipientIdentifier() {
		return recipientIdentifier;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("recipient", recipientIdentifier).append("text",
				text).toString();
	}
}
