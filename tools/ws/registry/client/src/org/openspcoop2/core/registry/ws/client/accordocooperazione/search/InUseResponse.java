
package org.openspcoop2.core.registry.ws.client.accordocooperazione.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for inUseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inUseResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inUse" type="{http://www.openspcoop2.org/core/registry/management}use-info"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inUseResponse", propOrder = {
    "inUse"
})
public class InUseResponse {

    @XmlElement(required = true)
    protected UseInfo inUse;

    /**
     * Gets the value of the inUse property.
     * 
     * @return
     *     possible object is
     *     {@link UseInfo }
     *     
     */
    public UseInfo getInUse() {
        return this.inUse;
    }

    /**
     * Sets the value of the inUse property.
     * 
     * @param value
     *     allowed object is
     *     {@link UseInfo }
     *     
     */
    public void setInUse(UseInfo value) {
        this.inUse = value;
    }

}
