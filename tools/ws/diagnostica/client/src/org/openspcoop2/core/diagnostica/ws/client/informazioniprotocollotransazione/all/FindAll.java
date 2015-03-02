
package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for findAll complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAll">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/diagnostica/management}search-filter-informazioni-protocollo-transazione"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findAll", propOrder = {
    "filter"
})
public class FindAll {

    @XmlElement(required = true)
    protected SearchFilterInformazioniProtocolloTransazione filter;

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link SearchFilterInformazioniProtocolloTransazione }
     *     
     */
    public SearchFilterInformazioniProtocolloTransazione getFilter() {
        return this.filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchFilterInformazioniProtocolloTransazione }
     *     
     */
    public void setFilter(SearchFilterInformazioniProtocolloTransazione value) {
        this.filter = value;
    }

}
