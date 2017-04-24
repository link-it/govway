
package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.AccordoServizioParteComune;


/**
 * <p>Java class for findAllResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accordoServizioParteComune" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findAllResponse", propOrder = {
    "accordoServizioParteComune"
})
public class FindAllResponse {

    protected List<AccordoServizioParteComune> accordoServizioParteComune;

    /**
     * Gets the value of the accordoServizioParteComune property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accordoServizioParteComune property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccordoServizioParteComune().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccordoServizioParteComune }
     * 
     * 
     */
    public List<AccordoServizioParteComune> getAccordoServizioParteComune() {
        if (this.accordoServizioParteComune == null) {
            this.accordoServizioParteComune = new ArrayList<AccordoServizioParteComune>();
        }
        return this.accordoServizioParteComune;
    }

}
