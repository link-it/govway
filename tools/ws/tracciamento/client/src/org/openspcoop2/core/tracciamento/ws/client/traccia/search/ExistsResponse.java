
package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for existsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="existsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exists" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "existsResponse", propOrder = {
    "exists"
})
public class ExistsResponse {

    protected boolean exists;

    /**
     * Gets the value of the exists property.
     * 
     */
    public boolean isExists() {
        return this.exists;
    }

    /**
     * Sets the value of the exists property.
     * 
     */
    public void setExists(boolean value) {
        this.exists = value;
    }

}
