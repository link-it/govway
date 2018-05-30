
package org.openspcoop2.core.registry.ws.client.accordocooperazione.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdAccordoCooperazione;


/**
 * <p>Java class for exists complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exists"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idAccordoCooperazione" type="{http://www.openspcoop2.org/core/registry}id-accordo-cooperazione"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exists", propOrder = {
    "idAccordoCooperazione"
})
public class Exists {

    @XmlElement(required = true)
    protected IdAccordoCooperazione idAccordoCooperazione;

    /**
     * Gets the value of the idAccordoCooperazione property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoCooperazione }
     *     
     */
    public IdAccordoCooperazione getIdAccordoCooperazione() {
        return this.idAccordoCooperazione;
    }

    /**
     * Sets the value of the idAccordoCooperazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoCooperazione }
     *     
     */
    public void setIdAccordoCooperazione(IdAccordoCooperazione value) {
        this.idAccordoCooperazione = value;
    }

}
