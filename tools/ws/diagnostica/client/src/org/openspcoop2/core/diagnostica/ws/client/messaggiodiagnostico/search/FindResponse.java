
package org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;


/**
 * <p>Java class for findResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="messaggioDiagnostico" type="{http://www.openspcoop2.org/core/diagnostica}messaggio-diagnostico"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findResponse", propOrder = {
    "messaggioDiagnostico"
})
public class FindResponse {

    @XmlElement(required = true)
    protected MessaggioDiagnostico messaggioDiagnostico;

    /**
     * Gets the value of the messaggioDiagnostico property.
     * 
     * @return
     *     possible object is
     *     {@link MessaggioDiagnostico }
     *     
     */
    public MessaggioDiagnostico getMessaggioDiagnostico() {
        return this.messaggioDiagnostico;
    }

    /**
     * Sets the value of the messaggioDiagnostico property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessaggioDiagnostico }
     *     
     */
    public void setMessaggioDiagnostico(MessaggioDiagnostico value) {
        this.messaggioDiagnostico = value;
    }

}
