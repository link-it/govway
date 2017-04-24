
package org.openspcoop2.core.config.ws.client.soggetto.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.Soggetto;


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
 *         &lt;element name="oldIdSoggetto" type="{http://www.openspcoop2.org/core/config}id-soggetto"/&gt;
 *         &lt;element name="soggetto" type="{http://www.openspcoop2.org/core/config}soggetto"/&gt;
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
    "oldIdSoggetto",
    "soggetto"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdSoggetto oldIdSoggetto;
    @XmlElement(required = true)
    protected Soggetto soggetto;

    /**
     * Gets the value of the oldIdSoggetto property.
     * 
     * @return
     *     possible object is
     *     {@link IdSoggetto }
     *     
     */
    public IdSoggetto getOldIdSoggetto() {
        return this.oldIdSoggetto;
    }

    /**
     * Sets the value of the oldIdSoggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdSoggetto }
     *     
     */
    public void setOldIdSoggetto(IdSoggetto value) {
        this.oldIdSoggetto = value;
    }

    /**
     * Gets the value of the soggetto property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getSoggetto() {
        return this.soggetto;
    }

    /**
     * Sets the value of the soggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setSoggetto(Soggetto value) {
        this.soggetto = value;
    }

}
