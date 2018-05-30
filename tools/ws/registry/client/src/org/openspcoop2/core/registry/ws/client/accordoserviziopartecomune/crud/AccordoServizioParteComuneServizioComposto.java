
package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for accordo-servizio-parte-comune-servizio-composto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-servizio-composto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accordo-cooperazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordo-servizio-parte-comune-servizio-composto", propOrder = {
    "accordoCooperazione"
})
public class AccordoServizioParteComuneServizioComposto {

    @XmlElement(name = "accordo-cooperazione")
    protected String accordoCooperazione;

    /**
     * Gets the value of the accordoCooperazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccordoCooperazione() {
        return this.accordoCooperazione;
    }

    /**
     * Sets the value of the accordoCooperazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccordoCooperazione(String value) {
        this.accordoCooperazione = value;
    }

}
