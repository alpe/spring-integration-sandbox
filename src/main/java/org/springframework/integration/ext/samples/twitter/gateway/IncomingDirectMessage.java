package org.springframework.integration.ext.samples.twitter.gateway;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.core.style.ToStringCreator;
import org.springframework.integration.ext.samples.twitter.command.ControlCommand;
import org.springframework.integration.ext.samples.twitter.util.DateAdapter;

/**
 * @author Alex Peters
 * 
 */
@XmlRootElement(name = "direct_message")
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "IncomingDirectMessage", uniqueConstraints = { @UniqueConstraint(columnNames = { "messageId", }) })
@Entity
@NamedQuery(name = IncomingDirectMessage.QUERY_FIND_BY_TWITTER_ID, query = "from IncomingDirectMessage where messageId = ?")
public class IncomingDirectMessage implements ControlCommand, Serializable {

	static final String QUERY_FIND_BY_TWITTER_ID = "incomingDirectMessage.byTwitterId";

	@XmlTransient
	@GeneratedValue
	@Id
	private long id;

	/** unique twitter message id. natural key. */
	@XmlElement(name = "id")
	private long messageId;

	@XmlElement(name = "sender_id")
	private long senderId;

	@XmlElement(name = "sender_screen_name")
	private String senderScreenName;

	@XmlElement
	private String text;

	@XmlElement(name = "created_at")
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date messageCreated;

	@XmlElement(name = "recipient_screen_name")
	private String recipientScreenName;

	@XmlElement(name = "recipient_id")
	private long recipientId;

	Date dateCreated = new Date();

	/** Empty package private constructor for Jaxb. */
	IncomingDirectMessage() {
	}

	/**
	 * {@inheritDoc}
	 */
	public long getMessageId() {
		return messageId;
	}

	/**
	 * @return the senderScreenName
	 */
	public String getSenderScreenName() {
		return senderScreenName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sender getSender() {
		return new Sender() {
			@Override
			public String getDisplayName() {
				return getSenderScreenName();
			}

			@Override
			public String getIdentifier() {
				return String.valueOf(senderId);
			}
		};
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (messageId ^ (messageId >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncomingDirectMessage other = (IncomingDirectMessage) obj;
		if (messageId != other.messageId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("messageId", messageId).append("senderScreenName",
				senderScreenName).append("text", text).append("recipientScreenName",
				recipientScreenName).append("messageCreated", messageCreated).toString();
	}
}
