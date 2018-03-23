
package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.DumpMessaggio;


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
 *         &lt;element ref="{http://www.openspcoop2.org/core/transazioni}dump-messaggio"/&gt;
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
    "dumpMessaggio"
})
public class FindResponse {

    @XmlElement(name = "dump-messaggio", namespace = "http://www.openspcoop2.org/core/transazioni", required = true)
    protected DumpMessaggio dumpMessaggio;

    /**
     * Gets the value of the dumpMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link DumpMessaggio }
     *     
     */
    public DumpMessaggio getDumpMessaggio() {
        return this.dumpMessaggio;
    }

    /**
     * Sets the value of the dumpMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link DumpMessaggio }
     *     
     */
    public void setDumpMessaggio(DumpMessaggio value) {
        this.dumpMessaggio = value;
    }

}
