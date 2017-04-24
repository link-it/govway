
package org.openspcoop2.core.config.ws.client.portadelegata.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.PortaDelegata;


/**
 * <p>Java class for findResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
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
@XmlType(name = "findResponse", propOrder = {
    "portaDelegata"
})
public class FindResponse {

    @XmlElement(required = true)
    protected PortaDelegata portaDelegata;

    /**
     * Gets the value of the portaDelegata property.
     * 
     * @return
     *     possible object is
     *     {@link PortaDelegata }
     *     
     */
    public PortaDelegata getPortaDelegata() {
        return portaDelegata;
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
