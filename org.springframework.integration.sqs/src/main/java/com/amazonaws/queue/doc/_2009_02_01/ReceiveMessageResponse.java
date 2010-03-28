
package com.amazonaws.queue.doc._2009_02_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://queue.amazonaws.com/doc/2009-02-01/}ReceiveMessageResult"/>
 *         &lt;element ref="{http://queue.amazonaws.com/doc/2009-02-01/}ResponseMetadata"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "receiveMessageResult",
    "responseMetadata"
})
@XmlRootElement(name = "ReceiveMessageResponse")
public class ReceiveMessageResponse {

    @XmlElement(name = "ReceiveMessageResult", required = true)
    protected ReceiveMessageResult receiveMessageResult;
    @XmlElement(name = "ResponseMetadata", required = true)
    protected ResponseMetadata responseMetadata;

    /**
     * Gets the value of the receiveMessageResult property.
     * 
     * @return
     *     possible object is
     *     {@link ReceiveMessageResult }
     *     
     */
    public ReceiveMessageResult getReceiveMessageResult() {
        return receiveMessageResult;
    }

    /**
     * Sets the value of the receiveMessageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReceiveMessageResult }
     *     
     */
    public void setReceiveMessageResult(ReceiveMessageResult value) {
        this.receiveMessageResult = value;
    }

    /**
     * Gets the value of the responseMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseMetadata }
     *     
     */
    public ResponseMetadata getResponseMetadata() {
        return responseMetadata;
    }

    /**
     * Sets the value of the responseMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseMetadata }
     *     
     */
    public void setResponseMetadata(ResponseMetadata value) {
        this.responseMetadata = value;
    }

}
