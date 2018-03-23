
package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;


/**
 * <p>Java class for search-filter-dump-messaggio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-dump-messaggio"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipo-messaggio" type="{http://www.openspcoop2.org/core/transazioni}tipo-messaggio" minOccurs="0"/&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-filter-dump-messaggio", propOrder = {
    "idTransazione",
    "tipoMessaggio",
    "limit",
    "offset",
    "descOrder"
})
public class SearchFilterDumpMessaggio {

    @XmlElement(name = "id-transazione")
    protected String idTransazione;
    @XmlElement(name = "tipo-messaggio")
    @XmlSchemaType(name = "string")
    protected TipoMessaggio tipoMessaggio;
    protected BigInteger limit;
    protected BigInteger offset;
    protected Boolean descOrder;

    /**
     * Gets the value of the idTransazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTransazione() {
        return this.idTransazione;
    }

    /**
     * Sets the value of the idTransazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTransazione(String value) {
        this.idTransazione = value;
    }

    /**
     * Gets the value of the tipoMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link TipoMessaggio }
     *     
     */
    public TipoMessaggio getTipoMessaggio() {
        return this.tipoMessaggio;
    }

    /**
     * Sets the value of the tipoMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoMessaggio }
     *     
     */
    public void setTipoMessaggio(TipoMessaggio value) {
        this.tipoMessaggio = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLimit() {
        return this.limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLimit(BigInteger value) {
        this.limit = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOffset() {
        return this.offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOffset(BigInteger value) {
        this.offset = value;
    }

    /**
     * Gets the value of the descOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDescOrder() {
        return this.descOrder;
    }

    /**
     * Sets the value of the descOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDescOrder(Boolean value) {
        this.descOrder = value;
    }

}
