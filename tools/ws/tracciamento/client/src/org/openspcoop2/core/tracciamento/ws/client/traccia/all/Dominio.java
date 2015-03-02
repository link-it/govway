
package org.openspcoop2.core.tracciamento.ws.client.traccia.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.constants.TipoPdD;


/**
 * <p>Java class for dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="soggetto" type="{http://www.openspcoop2.org/core/tracciamento/management}dominio-soggetto" minOccurs="0"/>
 *         &lt;element name="funzione" type="{http://www.openspcoop2.org/core/tracciamento}TipoPdD" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dominio", propOrder = {
    "identificativoPorta",
    "soggetto",
    "funzione"
})
public class Dominio {

    @XmlElement(name = "identificativo-porta")
    protected String identificativoPorta;
    protected DominioSoggetto soggetto;
    protected TipoPdD funzione;

    /**
     * Gets the value of the identificativoPorta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoPorta() {
        return this.identificativoPorta;
    }

    /**
     * Sets the value of the identificativoPorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoPorta(String value) {
        this.identificativoPorta = value;
    }

    /**
     * Gets the value of the soggetto property.
     * 
     * @return
     *     possible object is
     *     {@link DominioSoggetto }
     *     
     */
    public DominioSoggetto getSoggetto() {
        return this.soggetto;
    }

    /**
     * Sets the value of the soggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link DominioSoggetto }
     *     
     */
    public void setSoggetto(DominioSoggetto value) {
        this.soggetto = value;
    }

    /**
     * Gets the value of the funzione property.
     * 
     * @return
     *     possible object is
     *     {@link TipoPdD }
     *     
     */
    public TipoPdD getFunzione() {
        return this.funzione;
    }

    /**
     * Sets the value of the funzione property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPdD }
     *     
     */
    public void setFunzione(TipoPdD value) {
        this.funzione = value;
    }

}
