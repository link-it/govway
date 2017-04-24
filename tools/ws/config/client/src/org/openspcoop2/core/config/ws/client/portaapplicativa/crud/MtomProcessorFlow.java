
package org.openspcoop2.core.config.ws.client.portaapplicativa.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.MTOMProcessorType;


/**
 * <p>Java class for mtom-processor-flow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mtom-processor-flow"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mode" type="{http://www.openspcoop2.org/core/config}MTOMProcessorType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mtom-processor-flow", propOrder = {
    "mode"
})
public class MtomProcessorFlow {

    @XmlSchemaType(name = "string")
    protected MTOMProcessorType mode;

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link MTOMProcessorType }
     *     
     */
    public MTOMProcessorType getMode() {
        return this.mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link MTOMProcessorType }
     *     
     */
    public void setMode(MTOMProcessorType value) {
        this.mode = value;
    }

}
