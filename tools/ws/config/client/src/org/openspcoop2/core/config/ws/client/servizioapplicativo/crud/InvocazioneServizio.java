/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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

package org.openspcoop2.core.config.ws.client.servizioapplicativo.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for invocazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-servizio"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="credenziali" type="{http://www.openspcoop2.org/core/config/management}invocazione-credenziali" minOccurs="0"/&gt;
 *         &lt;element name="connettore" type="{http://www.openspcoop2.org/core/config/management}connettore" minOccurs="0"/&gt;
 *         &lt;element name="sbustamento-soap" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="sbustamento-informazioni-protocollo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="get-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="autenticazione" type="{http://www.openspcoop2.org/core/config}InvocazioneServizioTipoAutenticazione" minOccurs="0"/&gt;
 *         &lt;element name="invio-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="risposta-per-riferimento" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocazione-servizio", propOrder = {
    "credenziali",
    "connettore",
    "sbustamentoSoap",
    "sbustamentoInformazioniProtocollo",
    "getMessage",
    "autenticazione",
    "invioPerRiferimento",
    "rispostaPerRiferimento"
})
public class InvocazioneServizio {

    protected InvocazioneCredenziali credenziali;
    protected Connettore connettore;
    @XmlElement(name = "sbustamento-soap")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita sbustamentoSoap;
    @XmlElement(name = "sbustamento-informazioni-protocollo")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita sbustamentoInformazioniProtocollo;
    @XmlElement(name = "get-message")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita getMessage;
    @XmlSchemaType(name = "string")
    protected InvocazioneServizioTipoAutenticazione autenticazione;
    @XmlElement(name = "invio-per-riferimento")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita invioPerRiferimento;
    @XmlElement(name = "risposta-per-riferimento")
    @XmlSchemaType(name = "string")
    protected StatoFunzionalita rispostaPerRiferimento;

    /**
     * Gets the value of the credenziali property.
     * 
     * @return
     *     possible object is
     *     {@link InvocazioneCredenziali }
     *     
     */
    public InvocazioneCredenziali getCredenziali() {
        return this.credenziali;
    }

    /**
     * Sets the value of the credenziali property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvocazioneCredenziali }
     *     
     */
    public void setCredenziali(InvocazioneCredenziali value) {
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
