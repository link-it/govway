
package org.openspcoop2.core.config.ws.client.portadelegata.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.PortaDelegata;


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
 *         &lt;element name="portaDelegata" type="{http://www.openspcoop2.org/core/config}porta-delegata" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "portaDelegata"
})
public class FindAllResponse {

    protected List<PortaDelegata> portaDelegata;

    /**
     * Gets the value of the portaDelegata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the portaDelegata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPortaDelegata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PortaDelegata }
     * 
     * 
     */
    public List<PortaDelegata> getPortaDelegata() {
        if (this.portaDelegata == null) {
            this.portaDelegata = new ArrayList<PortaDelegata>();
        }
        return this.portaDelegata;
    }

}
