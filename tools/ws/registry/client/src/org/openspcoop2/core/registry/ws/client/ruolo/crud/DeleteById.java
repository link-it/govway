
package org.openspcoop2.core.registry.ws.client.ruolo.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdRuolo;


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
 *         &lt;element name="idRuolo" type="{http://www.openspcoop2.org/core/registry}id-ruolo"/&gt;
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
    "idRuolo"
})
public class DeleteById {

    @XmlElement(required = true)
    protected IdRuolo idRuolo;

    /**
     * Gets the value of the idRuolo property.
     * 
     * @return
     *     possible object is
     *     {@link IdRuolo }
     *     
     */
    public IdRuolo getIdRuolo() {
        return this.idRuolo;
    }

    /**
     * Sets the value of the idRuolo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdRuolo }
     *     
     */
    public void setIdRuolo(IdRuolo value) {
        this.idRuolo = value;
    }

}
