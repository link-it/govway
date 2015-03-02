
package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.IdTraccia;


/**
 * <p>Java class for get complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idTraccia" type="{http://www.openspcoop2.org/core/tracciamento}id-traccia"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get", propOrder = {
    "idTraccia"
})
public class Get {

    @XmlElement(required = true)
    protected IdTraccia idTraccia;

    /**
     * Gets the value of the idTraccia property.
     * 
     * @return
     *     possible object is
     *     {@link IdTraccia }
     *     
     */
    public IdTraccia getIdTraccia() {
        return this.idTraccia;
    }

    /**
     * Sets the value of the idTraccia property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdTraccia }
     *     
     */
    public void setIdTraccia(IdTraccia value) {
        this.idTraccia = value;
    }

}
