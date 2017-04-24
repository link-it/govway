
package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteAllByFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteAllByFilter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/registry/management}search-filter-accordo-servizio-parte-specifica"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteAllByFilter", propOrder = {
    "filter"
})
public class DeleteAllByFilter {

    @XmlElement(required = true)
    protected SearchFilterAccordoServizioParteSpecifica filter;

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link SearchFilterAccordoServizioParteSpecifica }
     *     
     */
    public SearchFilterAccordoServizioParteSpecifica getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchFilterAccordoServizioParteSpecifica }
     *     
     */
    public void setFilter(SearchFilterAccordoServizioParteSpecifica value) {
        this.filter = value;
    }

}
