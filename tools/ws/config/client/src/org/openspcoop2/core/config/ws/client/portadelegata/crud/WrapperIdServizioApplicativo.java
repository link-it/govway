
package org.openspcoop2.core.config.ws.client.portadelegata.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdServizioApplicativo;


/**
 * <p>Java class for wrapperIdServizioApplicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wrapperIdServizioApplicativo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/config}id-servizio-applicativo"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wrapperIdServizioApplicativo", propOrder = {
    "id"
})
public class WrapperIdServizioApplicativo {

    @XmlElement(required = true)
    protected IdServizioApplicativo id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link IdServizioApplicativo }
     *     
     */
    public IdServizioApplicativo getId() {
        return this.id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdServizioApplicativo }
     *     
     */
    public void setId(IdServizioApplicativo value) {
        this.id = value;
    }

}
