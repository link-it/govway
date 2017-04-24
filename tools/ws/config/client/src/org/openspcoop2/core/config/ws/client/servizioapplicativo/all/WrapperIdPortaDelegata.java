
package org.openspcoop2.core.config.ws.client.servizioapplicativo.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdPortaDelegata;


/**
 * <p>Java class for wrapperIdPortaDelegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wrapperIdPortaDelegata"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/config}id-porta-delegata"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wrapperIdPortaDelegata", propOrder = {
    "id"
})
public class WrapperIdPortaDelegata {

    @XmlElement(required = true)
    protected IdPortaDelegata id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaDelegata }
     *     
     */
    public IdPortaDelegata getId() {
        return this.id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaDelegata }
     *     
     */
    public void setId(IdPortaDelegata value) {
        this.id = value;
    }

}
