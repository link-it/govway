
package org.openspcoop2.core.registry.ws.client.scope.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdScope;
import org.openspcoop2.core.registry.Scope;


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
 *         &lt;element name="oldIdScope" type="{http://www.openspcoop2.org/core/registry}id-scope"/&gt;
 *         &lt;element name="scope" type="{http://www.openspcoop2.org/core/registry}scope"/&gt;
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
    "oldIdScope",
    "scope"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdScope oldIdScope;
    @XmlElement(required = true)
    protected Scope scope;

    /**
     * Gets the value of the oldIdScope property.
     * 
     * @return
     *     possible object is
     *     {@link IdScope }
     *     
     */
    public IdScope getOldIdScope() {
        return this.oldIdScope;
    }

    /**
     * Sets the value of the oldIdScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdScope }
     *     
     */
    public void setOldIdScope(IdScope value) {
        this.oldIdScope = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link Scope }
     *     
     */
    public Scope getScope() {
        return this.scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Scope }
     *     
     */
    public void setScope(Scope value) {
        this.scope = value;
    }

}
