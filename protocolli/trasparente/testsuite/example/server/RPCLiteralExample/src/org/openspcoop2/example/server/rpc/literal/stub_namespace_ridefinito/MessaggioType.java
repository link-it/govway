
package org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessaggioType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessaggioType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dati" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessaggioType", propOrder = {
    "dati"
})
@XmlSeeAlso({
    FixedMessaggioType.class,
    Fixed2MessaggioType.class
})
public class MessaggioType {

    @XmlElement(required = true)
    protected String dati;

    /**
     * Gets the value of the dati property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDati() {
        return this.dati;
    }

    /**
     * Sets the value of the dati property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDati(String value) {
        this.dati = value;
    }

}
