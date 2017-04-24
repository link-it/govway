
package org.openspcoop2.core.config.ws.client.servizioapplicativo.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdServizioApplicativo;


/**
 * <p>Java class for get complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idServizioApplicativo" type="{http://www.openspcoop2.org/core/config}id-servizio-applicativo"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get", propOrder = {
    "idServizioApplicativo"
})
public class Get {

    @XmlElement(required = true)
    protected IdServizioApplicativo idServizioApplicativo;

    /**
     * Gets the value of the idServizioApplicativo property.
     * 
     * @return
     *     possible object is
     *     {@link IdServizioApplicativo }
     *     
     */
    public IdServizioApplicativo getIdServizioApplicativo() {
        return idServizioApplicativo;
    }

    /**
     * Sets the value of the idServizioApplicativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdServizioApplicativo }
     *     
     */
    public void setIdServizioApplicativo(IdServizioApplicativo value) {
        this.idServizioApplicativo = value;
    }

}
