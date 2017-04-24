
package org.openspcoop2.core.config.ws.client.servizioapplicativo.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.ServizioApplicativo;


/**
 * <p>Java class for getResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="servizioApplicativo" type="{http://www.openspcoop2.org/core/config}servizio-applicativo"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getResponse", propOrder = {
    "servizioApplicativo"
})
public class GetResponse {

    @XmlElement(required = true)
    protected ServizioApplicativo servizioApplicativo;

    /**
     * Gets the value of the servizioApplicativo property.
     * 
     * @return
     *     possible object is
     *     {@link ServizioApplicativo }
     *     
     */
    public ServizioApplicativo getServizioApplicativo() {
        return this.servizioApplicativo;
    }

    /**
     * Sets the value of the servizioApplicativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServizioApplicativo }
     *     
     */
    public void setServizioApplicativo(ServizioApplicativo value) {
        this.servizioApplicativo = value;
    }

}
