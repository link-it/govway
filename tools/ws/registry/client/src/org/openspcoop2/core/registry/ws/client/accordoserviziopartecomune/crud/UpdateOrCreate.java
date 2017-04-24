
package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;


/**
 * <p>Java class for updateOrCreate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateOrCreate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="oldIdAccordoServizioParteComune" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-comune"/&gt;
 *         &lt;element name="accordoServizioParteComune" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateOrCreate", propOrder = {
    "oldIdAccordoServizioParteComune",
    "accordoServizioParteComune"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdAccordoServizioParteComune oldIdAccordoServizioParteComune;
    @XmlElement(required = true)
    protected AccordoServizioParteComune accordoServizioParteComune;

    /**
     * Gets the value of the oldIdAccordoServizioParteComune property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoServizioParteComune }
     *     
     */
    public IdAccordoServizioParteComune getOldIdAccordoServizioParteComune() {
        return this.oldIdAccordoServizioParteComune;
    }

    /**
     * Sets the value of the oldIdAccordoServizioParteComune property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoServizioParteComune }
     *     
     */
    public void setOldIdAccordoServizioParteComune(IdAccordoServizioParteComune value) {
        this.oldIdAccordoServizioParteComune = value;
    }

    /**
     * Gets the value of the accordoServizioParteComune property.
     * 
     * @return
     *     possible object is
     *     {@link AccordoServizioParteComune }
     *     
     */
    public AccordoServizioParteComune getAccordoServizioParteComune() {
        return this.accordoServizioParteComune;
    }

    /**
     * Sets the value of the accordoServizioParteComune property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccordoServizioParteComune }
     *     
     */
    public void setAccordoServizioParteComune(AccordoServizioParteComune value) {
        this.accordoServizioParteComune = value;
    }

}
