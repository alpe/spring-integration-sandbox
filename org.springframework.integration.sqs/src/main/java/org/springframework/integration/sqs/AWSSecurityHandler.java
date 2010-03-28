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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * 
 * RPC Handler implementation that adds SOAP envelope header information
 * required by AWS's security schema. Here's a typical example of how to use
 * this class.
 * <p>
 * based on <a href="http://developer.amazonwebservices.com/connect/entry.jspa?externalID=1468&categoryID=114"
 * >http://developer.amazonwebservices.com/connect/entry.jspa?externalID=1468&
 * categoryID=114</a>
 * </p>
 * 
 * @author Scott Dixon (original author)
 * @author Alex Peters (minor modifications)
 */
public class AWSSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger log = Logger.getLogger(AWSSecurityHandler.class);

	/**
	 * Namespace prefix used to qualify all AWS security schema types.
	 */
	private static final String NAMESPACE_AWS_PREFIX = "aws";

	/**
	 * Namespace of the AWS security schema.
	 */
	private static final String NAMESPACE_AWS = "http://security.amazonaws.com/doc/2007-01-01/";

	/**
	 * Algorithm used to calculate string hashes.
	 */
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	/**
	 * Stores our secret key in a generic fashion.
	 */
	private SecretKeySpec s_secretkeySpec;

	/**
	 * Store our AWS key.
	 */
	private String s_key;

	/**
	 * 
	 * @param inAWSKey The Amazon Web Services key to use when providing
	 * authentication information for a SOAP request.
	 * @param inAWSSecrectKey The Amazon Web Services secret key to use when
	 * calculating message authentication signatures.
	 * 
	 */
	public void setKeys(final String inAWSKey, final String inAWSSecrectKey) {
		Assert.hasText(inAWSKey);
		Assert.hasText(inAWSSecrectKey);
		s_secretkeySpec = new SecretKeySpec(inAWSSecrectKey.getBytes(), HMAC_SHA1_ALGORITHM);
		s_key = inAWSKey;
	}

	/**
	 * @param inAWSKey The Amazon Web Services key to use when providing
	 * authentication information for a SOAP request.
	 * @param inAWSSecrectKey The Amazon Web Services secret key to use when
	 * calculating message authentication signatures.
	 */
	public AWSSecurityHandler(final String inAWSKey, final String inAWSSecrectKey) {
		setKeys(inAWSKey, inAWSSecrectKey);
	}

	/**
	 * {@inheritDoc}
	 */

	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close(final MessageContext context) {
		log.debug("close: " + context);

	}

	/**
	 * {@inheritDoc}
	 */

	public boolean handleFault(final SOAPMessageContext context) {
		logMessage(context);
		return false;
	}

	/*
	 * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
	 * outgoing or incoming message. Write a brief message to the print stream
	 * and output the message. The writeTo() method can throw SOAPException or
	 * IOException
	 */
	private void logMessage(final SOAPMessageContext smc) {
		Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		SOAPMessage message = smc.getMessage();
		if (outboundProperty.booleanValue()) {
			logMessage("Outbound message: ", message);
		}
		else {
			logMessage("Inbound message: ", message);
		}
	}

	/*
	 * Log Message.
	 */
	private void logMessage(final String text, final SOAPMessage message) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			message.writeTo(out);
			log.debug(text + out.toString()); // using system encoding
		}
		catch (Exception e) {
		} // ignore
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean handleMessage(final SOAPMessageContext context) {
		logMessage(context);
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!outboundProperty) {
			return true;
		}

		/*
		 * Example SOAP header from
		 * http://docs.amazonwebservices.com/AWSSimpleQueueService
		 * /2008-01-01/SQSDeveloperGuide
		 * /MakingRequests_MakingSOAPRequestsArticle.html
		 * 
		 * <soapenv:Header
		 * xmlns:aws="http://security.amazonaws.com/doc/2007-01-01/">
		 * <aws:AWSAccessKeyId>1D9FVRAYCP1VJS767E02EXAMPLE</aws:AWSAccessKeyId>
		 * <aws:Timestamp>2008-02-10T23:59:59Z</aws:Timestamp>
		 * <aws:Signature>SZf1CHmQ/nrZbsrC13hCZS061ywsEXAMPLE</aws:Signature>
		 * </soapenv:Header>
		 */

		SOAPMessage aSOAPMessage = context.getMessage();
		try {
			SOAPEnvelope aEnvelope = aSOAPMessage.getSOAPPart().getEnvelope();
			SOAPHeader aHeader = aEnvelope.addHeader();
			String aTimestampStr = this.getTimestamp();
			// ADD AWS SECURITY HEADER ----------------------------------------
			aHeader.addNamespaceDeclaration(NAMESPACE_AWS_PREFIX, NAMESPACE_AWS);

			// ADD ACCESS KEY -------------------------------------------------
			Name aKeyName = aEnvelope.createName("AWSAccessKeyId", NAMESPACE_AWS_PREFIX, NAMESPACE_AWS);
			SOAPHeaderElement aKey = aHeader.addHeaderElement(aKeyName);
			aKey.addTextNode(s_key);

			// ADD TIMESTAMP --------------------------------------------------
			Name aTimestampName = aEnvelope.createName("Timestamp", NAMESPACE_AWS_PREFIX, NAMESPACE_AWS);
			SOAPHeaderElement aTimestamp = aHeader.addHeaderElement(aTimestampName);
			aTimestamp.addTextNode(aTimestampStr);

			// ADD SIGNATURE --------------------------------------------------
			Name aSignatureName = aEnvelope.createName("Signature", NAMESPACE_AWS_PREFIX, NAMESPACE_AWS);
			SOAPHeaderElement aSignature = aHeader.addHeaderElement(aSignatureName);

			SOAPBody aBody = aEnvelope.getBody();
			Iterator<?> aChildren = aBody.getChildElements();
			SOAPBodyElement aAction = (SOAPBodyElement) aChildren.next();
			if (aChildren.hasNext()) {
				throw new IllegalStateException(
						"Unexpected number of actions in soap request. Cannot calculate signature.");
			}
			aSignature.addTextNode(this.calculateSignature(aAction.getLocalName(), aTimestampStr));
			aSOAPMessage.saveChanges();
			logMessage("Final out message: ", aSOAPMessage);
		}
		catch (Exception e) {
			throw new IllegalStateException("Failed to add aws headers!", e);
		}
		return true;
	}

	/**
	 * Calculates a time stamp from "now" in UTC and returns it in ISO8601
	 * string format. According to
	 * http://docs.amazonwebservices.com/AWSSimpleQueueService
	 * /2008-01-01/SQSDeveloperGuide/NotUsingWSSecurity.html the soap message
	 * expires 15 minutes after this time stamp. AWS only supports UTC and it's
	 * canonical representation as 'Z' in an ISO8601 time string. See
	 * http://www.w3.org/TR/xmlschema-2/#dateTime for more details.
	 * @return ISO8601 time stamp string for "now" in UTC.
	 */
	private String getTimestamp() {
		// <aws:Timestamp>2008-02-10T23:59:59Z</aws:Timestamp>
		Calendar aGregorian = Calendar.getInstance();
		SimpleTimeZone aUTC = new SimpleTimeZone(0, "UTC");
		SimpleDateFormat aISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		aISO8601.setTimeZone(aUTC);
		return aISO8601.format(aGregorian.getTime());
	}

	/**
	 * Method borrowed from AWS example code at
	 * http://developer.amazonwebservices.com/
	 * @param inSOAPAction The single SOAP body element that is the action the
	 * request is taking.
	 * @param inTimeStamp The time stamp string as provided in the
	 * &lt;aws:Timestamp&gt; header element.
	 * @return A hash calculated according to AWS security rules to be provided
	 * in the &lt;aws:signature&gt; header element.
	 * @throws Exception If there were errors or missing, required classes when
	 * trying to calculate the hash.
	 */
	public String calculateSignature(final String inSOAPAction, final String inTimeStamp) throws Exception {
		// FROM
		// http://docs.amazonwebservices.com/AWSSimpleQueueService/2008-01-01/SQSDeveloperGuide/NotUsingWSSecurity.html
		//
		// 1. Concatenate the values of the Action and Timestamp request
		// parameters, in that order.
		// 2. The string you've just created is the string you'll use when
		// generating the signature.
		// 3. Calculate an RFC 2104-compliant HMAC-SHA1 signature, using the
		// string you just created and your Secret Access Key as the key.
		// 4. Convert the resulting value to base64.
		String aConcatActionAndTs = (inSOAPAction + inTimeStamp);
		Mac aMessageAuthCode = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		aMessageAuthCode.init(s_secretkeySpec);
		byte[] aRawMessageAuthCode = aMessageAuthCode.doFinal(aConcatActionAndTs.getBytes());
		return new String(Base64.encodeBase64(aRawMessageAuthCode));
	}
};
