
package com.amazonaws.queue.doc._2009_02_01;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="ReceiptHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VisibilityTimeout" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element ref="{http://queue.amazonaws.com/doc/2009-02-01/}Attribute" maxOccurs="unbounded" minOccurs="0"/>
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
    "receiptHandle",
    "visibilityTimeout",
    "attribute"
})
@XmlRootElement(name = "ChangeMessageVisibility")
public class ChangeMessageVisibility {

    @XmlElement(name = "ReceiptHandle", required = true)
    protected String receiptHandle;
    @XmlElement(name = "VisibilityTimeout", required = true)
    protected BigInteger visibilityTimeout;
    @XmlElement(name = "Attribute")
    protected List<Attribute> attribute;

    /**
     * Gets the value of the receiptHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiptHandle() {
        return receiptHandle;
    }

    /**
     * Sets the value of the receiptHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiptHandle(String value) {
        this.receiptHandle = value;
    }

    /**
     * Gets the value of the visibilityTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * Sets the value of the visibilityTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVisibilityTimeout(BigInteger value) {
        this.visibilityTimeout = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

}
