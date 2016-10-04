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

package org.openspcoop2.pdd.monitor.ws.client.statopdd.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;


/**
 * <p>Java class for filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="correlazione-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="busta" type="{http://www.openspcoop2.org/pdd/monitor/management}busta" minOccurs="0"/>
 *         &lt;element name="id-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="message-pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="soglia" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/pdd/monitor}StatoMessaggio" minOccurs="0"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filtro", propOrder = {
    "correlazioneApplicativa",
    "busta",
    "idMessaggio",
    "messagePattern",
    "soglia",
    "stato",
    "tipo"
})
public class Filtro {

    @XmlElement(name = "correlazione-applicativa")
    protected String correlazioneApplicativa;
    protected Busta busta;
    @XmlElement(name = "id-messaggio")
    protected String idMessaggio;
    @XmlElement(name = "message-pattern")
    protected String messagePattern;
    protected Long soglia;
    protected StatoMessaggio stato;
    protected String tipo;

    /**
     * Gets the value of the correlazioneApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelazioneApplicativa() {
        return this.correlazioneApplicativa;
    }

    /**
     * Sets the value of the correlazioneApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelazioneApplicativa(String value) {
        this.correlazioneApplicativa = value;
    }

    /**
     * Gets the value of the busta property.
     * 
     * @return
     *     possible object is
     *     {@link Busta }
     *     
     */
    public Busta getBusta() {
        return this.busta;
    }

    /**
     * Sets the value of the busta property.
     * 
     * @param value
     *     allowed object is
     *     {@link Busta }
     *     
     */
    public void setBusta(Busta value) {
        this.busta = value;
    }

    /**
     * Gets the value of the idMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMessaggio() {
        return this.idMessaggio;
    }

    /**
     * Sets the value of the idMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMessaggio(String value) {
        this.idMessaggio = value;
    }

    /**
     * Gets the value of the messagePattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessagePattern() {
        return this.messagePattern;
    }

    /**
     * Sets the value of the messagePattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessagePattern(String value) {
        this.messagePattern = value;
    }

    /**
     * Gets the value of the soglia property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSoglia() {
        return this.soglia;
    }

    /**
     * Sets the value of the soglia property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSoglia(Long value) {
        this.soglia = value;
    }

    /**
     * Gets the value of the stato property.
     * 
     * @return
     *     possible object is
     *     {@link StatoMessaggio }
     *     
     */
    public StatoMessaggio getStato() {
        return this.stato;
    }

    /**
     * Sets the value of the stato property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoMessaggio }
     *     
     */
    public void setStato(StatoMessaggio value) {
        this.stato = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

}
