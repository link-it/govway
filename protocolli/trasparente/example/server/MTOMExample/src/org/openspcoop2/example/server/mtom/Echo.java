
package org.openspcoop2.example.server.mtom;

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
 *         &lt;element name="richiesta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ImageData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
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
    "richiesta",
    "imageData"
})
@XmlRootElement(name = "echo")
public class Echo {

    @XmlElement(required = true)
    protected String richiesta;
    @XmlElement(name = "ImageData", required = true)
    @XmlMimeType("text/xml")
    protected Source imageData;

    /**
     * Gets the value of the richiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRichiesta() {
        return this.richiesta;
    }

    /**
     * Sets the value of the richiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRichiesta(String value) {
        this.richiesta = value;
    }

    /**
     * Gets the value of the imageData property.
     * 
     * @return
     *     possible object is
     *     {@link Source }
     *     
     */
    public Source getImageData() {
        return this.imageData;
    }

    /**
     * Sets the value of the imageData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Source }
     *     
     */
    public void setImageData(Source value) {
        this.imageData = value;
    }

}
