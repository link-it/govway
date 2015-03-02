
package org.openspcoop2.core.tracciamento.ws.client.traccia.all;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.IdTraccia;


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
 *         &lt;element name="idTraccia" type="{http://www.openspcoop2.org/core/tracciamento}id-traccia" maxOccurs="unbounded" minOccurs="0"/>
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
    "idTraccia"
})
public class FindAllIdsResponse {

    protected List<IdTraccia> idTraccia;

    /**
     * Gets the value of the idTraccia property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idTraccia property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdTraccia().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdTraccia }
     * 
     * 
     */
    public List<IdTraccia> getIdTraccia() {
        if (this.idTraccia == null) {
            this.idTraccia = new ArrayList<IdTraccia>();
        }
        return this.idTraccia;
    }

}
