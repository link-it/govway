
package org.openspcoop2.core.config.ws.client.portadelegata.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;


/**
 * <p>Java class for autorizzazione-ruoli complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="autorizzazione-ruoli"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="match" type="{http://www.openspcoop2.org/core/config}RuoloTipoMatch" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizzazione-ruoli", propOrder = {
    "match"
})
public class AutorizzazioneRuoli {

    @XmlSchemaType(name = "string")
    protected RuoloTipoMatch match;

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link RuoloTipoMatch }
     *     
     */
    public RuoloTipoMatch getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuoloTipoMatch }
     *     
     */
    public void setMatch(RuoloTipoMatch value) {
        this.match = value;
    }

}
