
package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;


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
 *         &lt;element name="idAccordoServizioParteComune" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-comune"/&gt;
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
    "idAccordoServizioParteComune"
})
public class Exists {

    @XmlElement(required = true)
    protected IdAccordoServizioParteComune idAccordoServizioParteComune;

    /**
     * Gets the value of the idAccordoServizioParteComune property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoServizioParteComune }
     *     
     */
    public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
        return this.idAccordoServizioParteComune;
    }

    /**
     * Sets the value of the idAccordoServizioParteComune property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoServizioParteComune }
     *     
     */
    public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune value) {
        this.idAccordoServizioParteComune = value;
    }

}
