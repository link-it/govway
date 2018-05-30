
package org.openspcoop2.core.registry.ws.client.ruolo.all;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdRuolo;


/**
 * <p>Java class for findAllIdsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllIdsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idRuolo" type="{http://www.openspcoop2.org/core/registry}id-ruolo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findAllIdsResponse", propOrder = {
    "idRuolo"
})
public class FindAllIdsResponse {

    protected List<IdRuolo> idRuolo;

    /**
     * Gets the value of the idRuolo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idRuolo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdRuolo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdRuolo }
     * 
     * 
     */
    public List<IdRuolo> getIdRuolo() {
        if (this.idRuolo == null) {
            this.idRuolo = new ArrayList<IdRuolo>();
        }
        return this.idRuolo;
    }

}
