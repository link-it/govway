
package org.openspcoop2.core.registry.ws.client.accordocooperazione.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.AccordoCooperazione;


/**
 * <p>Java class for delete complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="delete"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accordoCooperazione" type="{http://www.openspcoop2.org/core/registry}accordo-cooperazione"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delete", propOrder = {
    "accordoCooperazione"
})
public class Delete {

    @XmlElement(required = true)
    protected AccordoCooperazione accordoCooperazione;

    /**
     * Gets the value of the accordoCooperazione property.
     * 
     * @return
     *     possible object is
     *     {@link AccordoCooperazione }
     *     
     */
    public AccordoCooperazione getAccordoCooperazione() {
        return accordoCooperazione;
    }

    /**
     * Sets the value of the accordoCooperazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccordoCooperazione }
     *     
     */
    public void setAccordoCooperazione(AccordoCooperazione value) {
        this.accordoCooperazione = value;
    }

}
