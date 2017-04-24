
package org.openspcoop2.core.config.ws.client.portaapplicativa.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;


/**
 * <p>Java class for validazione-contenuti-applicativi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" minOccurs="0"/&gt;
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/config}ValidazioneContenutiApplicativiTipo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi", propOrder = {
    "stato",
    "tipo"
})
public class ValidazioneContenutiApplicativi {

    @XmlSchemaType(name = "string")
    protected StatoFunzionalitaConWarning stato;
    @XmlSchemaType(name = "string")
    protected ValidazioneContenutiApplicativiTipo tipo;

    /**
     * Gets the value of the stato property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalitaConWarning }
     *     
     */
    public StatoFunzionalitaConWarning getStato() {
        return this.stato;
    }

    /**
     * Sets the value of the stato property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalitaConWarning }
     *     
     */
    public void setStato(StatoFunzionalitaConWarning value) {
        this.stato = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link ValidazioneContenutiApplicativiTipo }
     *     
     */
    public ValidazioneContenutiApplicativiTipo getTipo() {
        return this.tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidazioneContenutiApplicativiTipo }
     *     
     */
    public void setTipo(ValidazioneContenutiApplicativiTipo value) {
        this.tipo = value;
    }

}
