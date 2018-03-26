
package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.Traccia;


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
 *         &lt;element name="traccia" type="{http://www.openspcoop2.org/core/tracciamento}traccia"/&gt;
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
    "traccia"
})
public class GetResponse {

    @XmlElement(required = true)
    protected Traccia traccia;

    /**
     * Gets the value of the traccia property.
     * 
     * @return
     *     possible object is
     *     {@link Traccia }
     *     
     */
    public Traccia getTraccia() {
        return this.traccia;
    }

    /**
     * Sets the value of the traccia property.
     * 
     * @param value
     *     allowed object is
     *     {@link Traccia }
     *     
     */
    public void setTraccia(Traccia value) {
        this.traccia = value;
    }

}
