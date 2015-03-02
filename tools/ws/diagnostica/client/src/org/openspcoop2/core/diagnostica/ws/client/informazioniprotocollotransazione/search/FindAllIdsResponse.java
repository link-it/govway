
package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.IdInformazioniProtocolloTransazione;


/**
 * <p>Java class for findAllIdsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllIdsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idInformazioniProtocolloTransazione" type="{http://www.openspcoop2.org/core/diagnostica}id-informazioni-protocollo-transazione" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findAllIdsResponse", propOrder = {
    "idInformazioniProtocolloTransazione"
})
public class FindAllIdsResponse {

    protected List<IdInformazioniProtocolloTransazione> idInformazioniProtocolloTransazione;

    /**
     * Gets the value of the idInformazioniProtocolloTransazione property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idInformazioniProtocolloTransazione property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdInformazioniProtocolloTransazione().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdInformazioniProtocolloTransazione }
     * 
     * 
     */
    public List<IdInformazioniProtocolloTransazione> getIdInformazioniProtocolloTransazione() {
        if (this.idInformazioniProtocolloTransazione == null) {
            this.idInformazioniProtocolloTransazione = new ArrayList<IdInformazioniProtocolloTransazione>();
        }
        return this.idInformazioniProtocolloTransazione;
    }

}
