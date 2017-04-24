
package org.openspcoop2.core.config.ws.client.portadelegata.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdPortaDelegata;


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
 *         &lt;element name="idPortaDelegata" type="{http://www.openspcoop2.org/core/config}id-porta-delegata"/&gt;
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
    "idPortaDelegata"
})
public class Exists {

    @XmlElement(required = true)
    protected IdPortaDelegata idPortaDelegata;

    /**
     * Gets the value of the idPortaDelegata property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaDelegata }
     *     
     */
    public IdPortaDelegata getIdPortaDelegata() {
        return idPortaDelegata;
    }

    /**
     * Sets the value of the idPortaDelegata property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaDelegata }
     *     
     */
    public void setIdPortaDelegata(IdPortaDelegata value) {
        this.idPortaDelegata = value;
    }

}
