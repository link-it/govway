
package org.openspcoop2.core.config.ws.client.portadelegata.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdPortaDelegata;
import org.openspcoop2.core.config.PortaDelegata;


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
 *         &lt;element name="oldIdPortaDelegata" type="{http://www.openspcoop2.org/core/config}id-porta-delegata"/&gt;
 *         &lt;element name="portaDelegata" type="{http://www.openspcoop2.org/core/config}porta-delegata"/&gt;
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
    "oldIdPortaDelegata",
    "portaDelegata"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdPortaDelegata oldIdPortaDelegata;
    @XmlElement(required = true)
    protected PortaDelegata portaDelegata;

    /**
     * Gets the value of the oldIdPortaDelegata property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaDelegata }
     *     
     */
    public IdPortaDelegata getOldIdPortaDelegata() {
        return this.oldIdPortaDelegata;
    }

    /**
     * Sets the value of the oldIdPortaDelegata property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaDelegata }
     *     
     */
    public void setOldIdPortaDelegata(IdPortaDelegata value) {
        this.oldIdPortaDelegata = value;
    }

    /**
     * Gets the value of the portaDelegata property.
     * 
     * @return
     *     possible object is
     *     {@link PortaDelegata }
     *     
     */
    public PortaDelegata getPortaDelegata() {
        return this.portaDelegata;
    }

    /**
     * Sets the value of the portaDelegata property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaDelegata }
     *     
     */
    public void setPortaDelegata(PortaDelegata value) {
        this.portaDelegata = value;
    }

}
