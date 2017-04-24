
package org.openspcoop2.core.config.ws.client.portadelegata.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mtom-processor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mtom-processor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="request-flow" type="{http://www.openspcoop2.org/core/config/management}mtom-processor-flow" minOccurs="0"/&gt;
 *         &lt;element name="response-flow" type="{http://www.openspcoop2.org/core/config/management}mtom-processor-flow" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mtom-processor", propOrder = {
    "requestFlow",
    "responseFlow"
})
public class MtomProcessor {

    @XmlElement(name = "request-flow")
    protected MtomProcessorFlow requestFlow;
    @XmlElement(name = "response-flow")
    protected MtomProcessorFlow responseFlow;

    /**
     * Gets the value of the requestFlow property.
     * 
     * @return
     *     possible object is
     *     {@link MtomProcessorFlow }
     *     
     */
    public MtomProcessorFlow getRequestFlow() {
        return requestFlow;
    }

    /**
     * Sets the value of the requestFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link MtomProcessorFlow }
     *     
     */
    public void setRequestFlow(MtomProcessorFlow value) {
        this.requestFlow = value;
    }

    /**
     * Gets the value of the responseFlow property.
     * 
     * @return
     *     possible object is
     *     {@link MtomProcessorFlow }
     *     
     */
    public MtomProcessorFlow getResponseFlow() {
        return responseFlow;
    }

    /**
     * Sets the value of the responseFlow property.
     * 
     * @param value
     *     allowed object is
     *     {@link MtomProcessorFlow }
     *     
     */
    public void setResponseFlow(MtomProcessorFlow value) {
        this.responseFlow = value;
    }

}
