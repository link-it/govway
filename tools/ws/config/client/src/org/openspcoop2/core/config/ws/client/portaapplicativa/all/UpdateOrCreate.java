
package org.openspcoop2.core.config.ws.client.portaapplicativa.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdPortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativa;


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
 *         &lt;element name="oldIdPortaApplicativa" type="{http://www.openspcoop2.org/core/config}id-porta-applicativa"/&gt;
 *         &lt;element name="portaApplicativa" type="{http://www.openspcoop2.org/core/config}porta-applicativa"/&gt;
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
    "oldIdPortaApplicativa",
    "portaApplicativa"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdPortaApplicativa oldIdPortaApplicativa;
    @XmlElement(required = true)
    protected PortaApplicativa portaApplicativa;

    /**
     * Gets the value of the oldIdPortaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaApplicativa }
     *     
     */
    public IdPortaApplicativa getOldIdPortaApplicativa() {
        return oldIdPortaApplicativa;
    }

    /**
     * Sets the value of the oldIdPortaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaApplicativa }
     *     
     */
    public void setOldIdPortaApplicativa(IdPortaApplicativa value) {
        this.oldIdPortaApplicativa = value;
    }

    /**
     * Gets the value of the portaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link PortaApplicativa }
     *     
     */
    public PortaApplicativa getPortaApplicativa() {
        return portaApplicativa;
    }

    /**
     * Sets the value of the portaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaApplicativa }
     *     
     */
    public void setPortaApplicativa(PortaApplicativa value) {
        this.portaApplicativa = value;
    }

}
