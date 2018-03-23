
package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for count complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="count"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="filter" type="{http://www.openspcoop2.org/core/transazioni/management}search-filter-dump-messaggio"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "count", propOrder = {
    "filter"
})
public class Count {

    @XmlElement(required = true)
    protected SearchFilterDumpMessaggio filter;

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link SearchFilterDumpMessaggio }
     *     
     */
    public SearchFilterDumpMessaggio getFilter() {
        return this.filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchFilterDumpMessaggio }
     *     
     */
    public void setFilter(SearchFilterDumpMessaggio value) {
        this.filter = value;
    }

}
