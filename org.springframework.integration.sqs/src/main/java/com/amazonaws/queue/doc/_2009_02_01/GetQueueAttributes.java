
package com.amazonaws.queue.doc._2009_02_01;

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
 *         &lt;element name="AttributeName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Unused" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "attributeName",
    "unused"
})
@XmlRootElement(name = "GetQueueAttributes")
public class GetQueueAttributes {

    @XmlElement(name = "AttributeName")
    protected List<String> attributeName;
    @XmlElement(name = "Unused")
    protected String unused;

    /**
     * Gets the value of the attributeName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAttributeName() {
        if (attributeName == null) {
            attributeName = new ArrayList<String>();
        }
        return this.attributeName;
    }

    /**
     * Gets the value of the unused property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnused() {
        return unused;
    }

    /**
     * Sets the value of the unused property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnused(String value) {
        this.unused = value;
    }

}
