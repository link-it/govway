
package org.openspcoop2.core.registry.ws.client.portadominio.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.PortaDominio;


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
 *         &lt;element name="portaDominio" type="{http://www.openspcoop2.org/core/registry}porta-dominio"/&gt;
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
    "portaDominio"
})
public class GetResponse {

    @XmlElement(required = true)
    protected PortaDominio portaDominio;

    /**
     * Gets the value of the portaDominio property.
     * 
     * @return
     *     possible object is
     *     {@link PortaDominio }
     *     
     */
    public PortaDominio getPortaDominio() {
        return this.portaDominio;
    }

    /**
     * Sets the value of the portaDominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaDominio }
     *     
     */
    public void setPortaDominio(PortaDominio value) {
        this.portaDominio = value;
    }

}
