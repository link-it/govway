
package org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.all;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;


/**
 * <p>Java class for findAllResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messaggioDiagnostico" type="{http://www.openspcoop2.org/core/diagnostica}messaggio-diagnostico" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findAllResponse", propOrder = {
    "messaggioDiagnostico"
})
public class FindAllResponse {

    protected List<MessaggioDiagnostico> messaggioDiagnostico;

    /**
     * Gets the value of the messaggioDiagnostico property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messaggioDiagnostico property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessaggioDiagnostico().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MessaggioDiagnostico }
     * 
     * 
     */
    public List<MessaggioDiagnostico> getMessaggioDiagnostico() {
        if (this.messaggioDiagnostico == null) {
            this.messaggioDiagnostico = new ArrayList<MessaggioDiagnostico>();
        }
        return this.messaggioDiagnostico;
    }

}
