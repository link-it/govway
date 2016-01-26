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
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for risposta-asincrona complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risposta-asincrona">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config/management}credenziali" minOccurs="0"/>
 *         &lt;element name="connettore" type="{http://www.openspcoop2.org/core/config/management}connettore" minOccurs="0"/>
 *         &lt;element name="sbustamento-soap" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *         &lt;element name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *         &lt;element name="get-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *         &lt;element name="autenticazione" type="{http://www.openspcoop2.org/core/config}InvocazioneServizioTipoAutenticazione" minOccurs="0"/>
 *         &lt;element name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *         &lt;element name="risposta-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risposta-asincrona", propOrder = {
    "credenziali",
    "connettore",
    "sbustamentoSoap",
    "sbustamentoInformazioniProtocollo",
    "getMessage",
    "autenticazione",
    "invioPerRiferimento",
    "rispostaPerRiferimento"
})
public class RispostaAsincrona {

    protected Credenziali credenziali;
    protected Connettore connettore;
    @XmlElement(name = "sbustamento-soap")
    protected StatoFunzionalita sbustamentoSoap;
    @XmlElement(name = "sbustamento-informazioni-protocollo")
    protected StatoFunzionalita sbustamentoInformazioniProtocollo;
    @XmlElement(name = "get-message")
    protected StatoFunzionalita getMessage;
    protected InvocazioneServizioTipoAutenticazione autenticazione;
    @XmlElement(name = "invio-per-riferimento")
    protected StatoFunzionalita invioPerRiferimento;
    @XmlElement(name = "risposta-per-riferimento")
    protected StatoFunzionalita rispostaPerRiferimento;

    /**
     * Gets the value of the credenziali property.
     * 
     * @return
     *     possible object is
     *     {@link Credenziali }
     *     
     */
    public Credenziali getCredenziali() {
        return this.credenziali;
    }

    /**
     * Sets the value of the credenziali property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credenziali }
     *     
     */
    public void setCredenziali(Credenziali value) {
        this.credenziali = value;
    }

    /**
     * Gets the value of the connettore property.
     * 
     * @return
     *     possible object is
     *     {@link Connettore }
     *     
     */
    public Connettore getConnettore() {
        return this.connettore;
    }

    /**
     * Sets the value of the connettore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Connettore }
     *     
     */
    public void setConnettore(Connettore value) {
        this.connettore = value;
    }

    /**
     * Gets the value of the sbustamentoSoap property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getSbustamentoSoap() {
        return this.sbustamentoSoap;
    }

    /**
     * Sets the value of the sbustamentoSoap property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setSbustamentoSoap(StatoFunzionalita value) {
        this.sbustamentoSoap = value;
    }

    /**
     * Gets the value of the sbustamentoInformazioniProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getSbustamentoInformazioniProtocollo() {
        return this.sbustamentoInformazioniProtocollo;
    }

    /**
     * Sets the value of the sbustamentoInformazioniProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setSbustamentoInformazioniProtocollo(StatoFunzionalita value) {
        this.sbustamentoInformazioniProtocollo = value;
    }

    /**
     * Gets the value of the getMessage property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getGetMessage() {
        return this.getMessage;
    }

    /**
     * Sets the value of the getMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setGetMessage(StatoFunzionalita value) {
        this.getMessage = value;
    }

    /**
     * Gets the value of the autenticazione property.
     * 
     * @return
     *     possible object is
     *     {@link InvocazioneServizioTipoAutenticazione }
     *     
     */
    public InvocazioneServizioTipoAutenticazione getAutenticazione() {
        return this.autenticazione;
    }

    /**
     * Sets the value of the autenticazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvocazioneServizioTipoAutenticazione }
     *     
     */
    public void setAutenticazione(InvocazioneServizioTipoAutenticazione value) {
        this.autenticazione = value;
    }

    /**
     * Gets the value of the invioPerRiferimento property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getInvioPerRiferimento() {
        return this.invioPerRiferimento;
    }

    /**
     * Sets the value of the invioPerRiferimento property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setInvioPerRiferimento(StatoFunzionalita value) {
        this.invioPerRiferimento = value;
    }

    /**
     * Gets the value of the rispostaPerRiferimento property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getRispostaPerRiferimento() {
        return this.rispostaPerRiferimento;
    }

    /**
     * Sets the value of the rispostaPerRiferimento property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setRispostaPerRiferimento(StatoFunzionalita value) {
        this.rispostaPerRiferimento = value;
    }

}
