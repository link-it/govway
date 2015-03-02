
package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione;


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
 *         &lt;element name="idInformazioniProtocolloTransazione" type="{http://www.openspcoop2.org/core/diagnostica}id-informazioni-protocollo-transazione"/>
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
    "idInformazioniProtocolloTransazione"
})
public class Get {

    @XmlElement(required = true)
    protected IdInformazioniProtocolloTransazione idInformazioniProtocolloTransazione;

    /**
     * Gets the value of the idInformazioniProtocolloTransazione property.
     * 
     * @return
     *     possible object is
     *     {@link IdInformazioniProtocolloTransazione }
     *     
     */
    public IdInformazioniProtocolloTransazione getIdInformazioniProtocolloTransazione() {
        return this.idInformazioniProtocolloTransazione;
    }

    /**
     * Sets the value of the idInformazioniProtocolloTransazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdInformazioniProtocolloTransazione }
     *     
     */
    public void setIdInformazioniProtocolloTransazione(IdInformazioniProtocolloTransazione value) {
        this.idInformazioniProtocolloTransazione = value;
    }

}
