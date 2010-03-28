
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
 *         &lt;element ref="{http://queue.amazonaws.com/doc/2009-02-01/}CreateQueueResult"/>
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
    "createQueueResult",
    "responseMetadata"
})
@XmlRootElement(name = "CreateQueueResponse")
public class CreateQueueResponse {

    @XmlElement(name = "CreateQueueResult", required = true)
    protected CreateQueueResult createQueueResult;
    @XmlElement(name = "ResponseMetadata", required = true)
    protected ResponseMetadata responseMetadata;

    /**
     * Gets the value of the createQueueResult property.
     * 
     * @return
     *     possible object is
     *     {@link CreateQueueResult }
     *     
     */
    public CreateQueueResult getCreateQueueResult() {
        return createQueueResult;
    }

    /**
     * Sets the value of the createQueueResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateQueueResult }
     *     
     */
    public void setCreateQueueResult(CreateQueueResult value) {
        this.createQueueResult = value;
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
