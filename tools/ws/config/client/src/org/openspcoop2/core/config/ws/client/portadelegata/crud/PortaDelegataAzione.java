/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.core.config.ws.client.portadelegata.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for porta-delegata-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata-azione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificazione" type="{http://www.openspcoop2.org/core/config}PortaDelegataAzioneIdentificazione" minOccurs="0"/>
 *         &lt;element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="force-wsdl-based" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-delegata-azione", propOrder = {
    "identificazione",
    "pattern",
    "nome",
    "forceWsdlBased"
})
public class PortaDelegataAzione {

    protected PortaDelegataAzioneIdentificazione identificazione;
    protected String pattern;
    protected String nome;
    @XmlElement(name = "force-wsdl-based")
    protected StatoFunzionalita forceWsdlBased;

    /**
     * Gets the value of the identificazione property.
     * 
     * @return
     *     possible object is
     *     {@link PortaDelegataAzioneIdentificazione }
     *     
     */
    public PortaDelegataAzioneIdentificazione getIdentificazione() {
        return this.identificazione;
    }

    /**
     * Sets the value of the identificazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaDelegataAzioneIdentificazione }
     *     
     */
    public void setIdentificazione(PortaDelegataAzioneIdentificazione value) {
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
        return this.pattern;
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
        return this.nome;
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
        return this.forceWsdlBased;
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
