
package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;


/**
 * <p>Java class for get complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idDumpMessaggio" type="{http://www.openspcoop2.org/core/transazioni}id-dump-messaggio"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get", propOrder = {
    "idDumpMessaggio"
})
public class Get {

    @XmlElement(required = true)
    protected IdDumpMessaggio idDumpMessaggio;

    /**
     * Gets the value of the idDumpMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link IdDumpMessaggio }
     *     
     */
    public IdDumpMessaggio getIdDumpMessaggio() {
        return this.idDumpMessaggio;
    }

    /**
     * Sets the value of the idDumpMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdDumpMessaggio }
     *     
     */
    public void setIdDumpMessaggio(IdDumpMessaggio value) {
        this.idDumpMessaggio = value;
    }

}
