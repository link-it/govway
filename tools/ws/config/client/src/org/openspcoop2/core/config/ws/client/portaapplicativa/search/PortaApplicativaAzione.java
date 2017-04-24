
package org.openspcoop2.core.config.ws.client.portaapplicativa.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for porta-applicativa-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-azione"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="identificazione" type="{http://www.openspcoop2.org/core/config}PortaApplicativaAzioneIdentificazione" minOccurs="0"/&gt;
 *         &lt;element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="force-wsdl-based" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa-azione", propOrder = {
    "identificazione",
    "pattern",
    "nome",
    "forceWsdlBased"
})
public class PortaApplicativaAzione {

    @XmlSchemaType(name = "string")
    protected PortaApplicativaAzioneIdentificazione identificazione;
    protected String pattern;
    protected String nome;
    @XmlElement(name = "force-wsdl-based")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita forceWsdlBased;

    /**
     * Gets the value of the identificazione property.
     * 
     * @return
     *     possible object is
     *     {@link PortaApplicativaAzioneIdentificazione }
     *     
     */
    public PortaApplicativaAzioneIdentificazione getIdentificazione() {
        return identificazione;
    }

    /**
     * Sets the value of the identificazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaApplicativaAzioneIdentificazione }
     *     
     */
    public void setIdentificazione(PortaApplicativaAzioneIdentificazione value) {
        this.identificazione = value;
    }

    /**
     * Gets the value of the pattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the value of the pattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPattern(String value) {
        this.pattern = value;
    }

    /**
     * Gets the value of the nome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the value of the nome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNome(String value) {
        this.nome = value;
    }

    /**
     * Gets the value of the forceWsdlBased property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getForceWsdlBased() {
        return forceWsdlBased;
    }

    /**
     * Sets the value of the forceWsdlBased property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setForceWsdlBased(StatoFunzionalita value) {
        this.forceWsdlBased = value;
    }

}
