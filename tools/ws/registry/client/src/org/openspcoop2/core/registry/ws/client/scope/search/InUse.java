
package org.openspcoop2.core.registry.ws.client.scope.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdScope;


/**
 * <p>Java class for inUse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inUse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idScope" type="{http://www.openspcoop2.org/core/registry}id-scope"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inUse", propOrder = {
    "idScope"
})
public class InUse {

    @XmlElement(required = true)
    protected IdScope idScope;

    /**
     * Gets the value of the idScope property.
     * 
     * @return
     *     possible object is
     *     {@link IdScope }
     *     
     */
    public IdScope getIdScope() {
        return this.idScope;
    }

    /**
     * Sets the value of the idScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdScope }
     *     
     */
    public void setIdScope(IdScope value) {
        this.idScope = value;
    }

}
