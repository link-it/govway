
package org.openspcoop2.example.server.mtom;

import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Source;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="risposta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ImageDataResponse" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="otherResponse" type="{http://www.w3.org/2001/XMLSchema}base64Binary" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "risposta",
    "imageDataResponse",
    "otherResponse"
})
@XmlRootElement(name = "echoResponse")
public class EchoResponse {

    @XmlElement(required = true)
    protected String risposta;
    @XmlElement(name = "ImageDataResponse", required = true)
    @XmlMimeType("text/xml")
    protected Source imageDataResponse;
    @XmlMimeType("*/*")
    protected List<DataHandler> otherResponse;

    /**
     * Gets the value of the risposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRisposta() {
        return this.risposta;
    }

    /**
     * Sets the value of the risposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRisposta(String value) {
        this.risposta = value;
    }

    /**
     * Gets the value of the imageDataResponse property.
     * 
     * @return
     *     possible object is
     *     {@link Source }
     *     
     */
    public Source getImageDataResponse() {
        return this.imageDataResponse;
    }

    /**
     * Sets the value of the imageDataResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Source }
     *     
     */
    public void setImageDataResponse(Source value) {
        this.imageDataResponse = value;
    }

    /**
     * Gets the value of the otherResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataHandler }
     * 
     * 
     */
    public List<DataHandler> getOtherResponse() {
        if (this.otherResponse == null) {
            this.otherResponse = new ArrayList<DataHandler>();
        }
        return this.otherResponse;
    }

}
