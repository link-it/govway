
package org.openspcoop2.core.config.ws.client.portaapplicativa.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdPortaApplicativa;


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
 *         &lt;element name="idPortaApplicativa" type="{http://www.openspcoop2.org/core/config}id-porta-applicativa"/&gt;
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
    "idPortaApplicativa"
})
public class Exists {

    @XmlElement(required = true)
    protected IdPortaApplicativa idPortaApplicativa;

    /**
     * Gets the value of the idPortaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaApplicativa }
     *     
     */
    public IdPortaApplicativa getIdPortaApplicativa() {
        return this.idPortaApplicativa;
    }

    /**
     * Sets the value of the idPortaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaApplicativa }
     *     
     */
    public void setIdPortaApplicativa(IdPortaApplicativa value) {
        this.idPortaApplicativa = value;
    }

}
