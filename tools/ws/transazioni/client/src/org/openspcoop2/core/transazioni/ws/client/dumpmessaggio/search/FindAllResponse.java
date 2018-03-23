
package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.DumpMessaggio;


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
 *         &lt;element ref="{http://www.openspcoop2.org/core/transazioni}dump-messaggio" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "dumpMessaggio"
})
public class FindAllResponse {

    @XmlElement(name = "dump-messaggio", namespace = "http://www.openspcoop2.org/core/transazioni")
    protected List<DumpMessaggio> dumpMessaggio;

    /**
     * Gets the value of the dumpMessaggio property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dumpMessaggio property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDumpMessaggio().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DumpMessaggio }
     * 
     * 
     */
    public List<DumpMessaggio> getDumpMessaggio() {
        if (this.dumpMessaggio == null) {
            this.dumpMessaggio = new ArrayList<DumpMessaggio>();
        }
        return this.dumpMessaggio;
    }

}
