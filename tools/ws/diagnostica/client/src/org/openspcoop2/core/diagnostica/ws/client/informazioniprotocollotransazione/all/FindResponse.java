
package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;


/**
 * <p>Java class for findResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="informazioniProtocolloTransazione" type="{http://www.openspcoop2.org/core/diagnostica}informazioni-protocollo-transazione"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findResponse", propOrder = {
    "informazioniProtocolloTransazione"
})
public class FindResponse {

    @XmlElement(required = true)
    protected InformazioniProtocolloTransazione informazioniProtocolloTransazione;

    /**
     * Gets the value of the informazioniProtocolloTransazione property.
     * 
     * @return
     *     possible object is
     *     {@link InformazioniProtocolloTransazione }
     *     
     */
    public InformazioniProtocolloTransazione getInformazioniProtocolloTransazione() {
        return this.informazioniProtocolloTransazione;
    }

    /**
     * Sets the value of the informazioniProtocolloTransazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformazioniProtocolloTransazione }
     *     
     */
    public void setInformazioniProtocolloTransazione(InformazioniProtocolloTransazione value) {
        this.informazioniProtocolloTransazione = value;
    }

}
