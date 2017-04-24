
package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;


/**
 * <p>Java class for deleteById complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteById"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idAccordoServizioParteSpecifica" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-specifica"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteById", propOrder = {
    "idAccordoServizioParteSpecifica"
})
public class DeleteById {

    @XmlElement(required = true)
    protected IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica;

    /**
     * Gets the value of the idAccordoServizioParteSpecifica property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoServizioParteSpecifica }
     *     
     */
    public IdAccordoServizioParteSpecifica getIdAccordoServizioParteSpecifica() {
        return idAccordoServizioParteSpecifica;
    }

    /**
     * Sets the value of the idAccordoServizioParteSpecifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoServizioParteSpecifica }
     *     
     */
    public void setIdAccordoServizioParteSpecifica(IdAccordoServizioParteSpecifica value) {
        this.idAccordoServizioParteSpecifica = value;
    }

}
