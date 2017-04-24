
package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;


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
 *         &lt;element name="oldIdAccordoServizioParteSpecifica" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-specifica"/&gt;
 *         &lt;element name="accordoServizioParteSpecifica" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-specifica"/&gt;
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
    "oldIdAccordoServizioParteSpecifica",
    "accordoServizioParteSpecifica"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdAccordoServizioParteSpecifica oldIdAccordoServizioParteSpecifica;
    @XmlElement(required = true)
    protected AccordoServizioParteSpecifica accordoServizioParteSpecifica;

    /**
     * Gets the value of the oldIdAccordoServizioParteSpecifica property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoServizioParteSpecifica }
     *     
     */
    public IdAccordoServizioParteSpecifica getOldIdAccordoServizioParteSpecifica() {
        return oldIdAccordoServizioParteSpecifica;
    }

    /**
     * Sets the value of the oldIdAccordoServizioParteSpecifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoServizioParteSpecifica }
     *     
     */
    public void setOldIdAccordoServizioParteSpecifica(IdAccordoServizioParteSpecifica value) {
        this.oldIdAccordoServizioParteSpecifica = value;
    }

    /**
     * Gets the value of the accordoServizioParteSpecifica property.
     * 
     * @return
     *     possible object is
     *     {@link AccordoServizioParteSpecifica }
     *     
     */
    public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica() {
        return accordoServizioParteSpecifica;
    }

    /**
     * Sets the value of the accordoServizioParteSpecifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccordoServizioParteSpecifica }
     *     
     */
    public void setAccordoServizioParteSpecifica(AccordoServizioParteSpecifica value) {
        this.accordoServizioParteSpecifica = value;
    }

}
