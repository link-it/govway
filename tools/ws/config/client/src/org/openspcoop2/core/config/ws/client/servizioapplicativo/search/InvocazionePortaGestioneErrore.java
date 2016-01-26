/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.core.config.ws.client.servizioapplicativo.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for invocazione-porta-gestione-errore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta-gestione-errore">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fault" type="{http://www.openspcoop2.org/core/config}FaultIntegrazioneTipo" minOccurs="0"/>
 *         &lt;element name="fault-actor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="generic-fault-code" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *         &lt;element name="prefix-fault-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocazione-porta-gestione-errore", propOrder = {
    "fault",
    "faultActor",
    "genericFaultCode",
    "prefixFaultCode"
})
public class InvocazionePortaGestioneErrore {

    protected FaultIntegrazioneTipo fault;
    @XmlElement(name = "fault-actor")
    protected String faultActor;
    @XmlElement(name = "generic-fault-code")
    protected StatoFunzionalita genericFaultCode;
    @XmlElement(name = "prefix-fault-code")
    protected String prefixFaultCode;

    /**
     * Gets the value of the fault property.
     * 
     * @return
     *     possible object is
     *     {@link FaultIntegrazioneTipo }
     *     
     */
    public FaultIntegrazioneTipo getFault() {
        return this.fault;
    }

    /**
     * Sets the value of the fault property.
     * 
     * @param value
     *     allowed object is
     *     {@link FaultIntegrazioneTipo }
     *     
     */
    public void setFault(FaultIntegrazioneTipo value) {
        this.fault = value;
    }

    /**
     * Gets the value of the faultActor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaultActor() {
        return this.faultActor;
    }

    /**
     * Sets the value of the faultActor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaultActor(String value) {
        this.faultActor = value;
    }

    /**
     * Gets the value of the genericFaultCode property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getGenericFaultCode() {
        return this.genericFaultCode;
    }

    /**
     * Sets the value of the genericFaultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setGenericFaultCode(StatoFunzionalita value) {
        this.genericFaultCode = value;
    }

    /**
     * Gets the value of the prefixFaultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrefixFaultCode() {
        return this.prefixFaultCode;
    }

    /**
     * Sets the value of the prefixFaultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrefixFaultCode(String value) {
        this.prefixFaultCode = value;
    }

}
