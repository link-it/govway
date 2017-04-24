
package org.openspcoop2.core.config.ws.client.servizioapplicativo.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;


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
 *         &lt;element name="oldIdServizioApplicativo" type="{http://www.openspcoop2.org/core/config}id-servizio-applicativo"/&gt;
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
@XmlType(name = "updateOrCreate", propOrder = {
    "oldIdServizioApplicativo",
    "servizioApplicativo"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdServizioApplicativo oldIdServizioApplicativo;
    @XmlElement(required = true)
    protected ServizioApplicativo servizioApplicativo;

    /**
     * Gets the value of the oldIdServizioApplicativo property.
     * 
     * @return
     *     possible object is
     *     {@link IdServizioApplicativo }
     *     
     */
    public IdServizioApplicativo getOldIdServizioApplicativo() {
        return this.oldIdServizioApplicativo;
    }

    /**
     * Sets the value of the oldIdServizioApplicativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdServizioApplicativo }
     *     
     */
    public void setOldIdServizioApplicativo(IdServizioApplicativo value) {
        this.oldIdServizioApplicativo = value;
    }

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
