
package org.openspcoop2.core.registry.ws.client.portadominio.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdPortaDominio;


/**
 * <p>Java class for get complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idPortaDominio" type="{http://www.openspcoop2.org/core/registry}id-porta-dominio"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get", propOrder = {
    "idPortaDominio"
})
public class Get {

    @XmlElement(required = true)
    protected IdPortaDominio idPortaDominio;

    /**
     * Gets the value of the idPortaDominio property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaDominio }
     *     
     */
    public IdPortaDominio getIdPortaDominio() {
        return idPortaDominio;
    }

    /**
     * Sets the value of the idPortaDominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaDominio }
     *     
     */
    public void setIdPortaDominio(IdPortaDominio value) {
        this.idPortaDominio = value;
    }

}
