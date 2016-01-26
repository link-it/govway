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

package org.openspcoop2.core.config.ws.client.servizioapplicativo.all;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for search-filter-servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-servizio-applicativo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="invocazione-porta" type="{http://www.openspcoop2.org/core/config/management}invocazione-porta" minOccurs="0"/>
 *         &lt;element name="invocazione-servizio" type="{http://www.openspcoop2.org/core/config/management}invocazione-servizio" minOccurs="0"/>
 *         &lt;element name="risposta-asincrona" type="{http://www.openspcoop2.org/core/config/management}risposta-asincrona" minOccurs="0"/>
 *         &lt;element name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipologia-fruizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipologia-erogazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-filter-servizio-applicativo", propOrder = {
    "invocazionePorta",
    "invocazioneServizio",
    "rispostaAsincrona",
    "tipoSoggettoProprietario",
    "nomeSoggettoProprietario",
    "tipologiaFruizione",
    "tipologiaErogazione",
    "nome",
    "descrizione",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "orCondition",
    "limit",
    "offset"
})
public class SearchFilterServizioApplicativo {

    @XmlElement(name = "invocazione-porta")
    protected InvocazionePorta invocazionePorta;
    @XmlElement(name = "invocazione-servizio")
    protected InvocazioneServizio invocazioneServizio;
    @XmlElement(name = "risposta-asincrona")
    protected RispostaAsincrona rispostaAsincrona;
    @XmlElement(name = "tipo-soggetto-proprietario")
    protected String tipoSoggettoProprietario;
    @XmlElement(name = "nome-soggetto-proprietario")
    protected String nomeSoggettoProprietario;
    @XmlElement(name = "tipologia-fruizione")
    protected String tipologiaFruizione;
    @XmlElement(name = "tipologia-erogazione")
    protected String tipologiaErogazione;
    protected String nome;
    protected String descrizione;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    protected Boolean orCondition;
    protected BigInteger limit;
    protected BigInteger offset;

    /**
     * Gets the value of the invocazionePorta property.
     * 
     * @return
     *     possible object is
     *     {@link InvocazionePorta }
     *     
     */
    public InvocazionePorta getInvocazionePorta() {
        return this.invocazionePorta;
    }

    /**
     * Sets the value of the invocazionePorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvocazionePorta }
     *     
     */
    public void setInvocazionePorta(InvocazionePorta value) {
        this.invocazionePorta = value;
    }

    /**
     * Gets the value of the invocazioneServizio property.
     * 
     * @return
     *     possible object is
     *     {@link InvocazioneServizio }
     *     
     */
    public InvocazioneServizio getInvocazioneServizio() {
        return this.invocazioneServizio;
    }

    /**
     * Sets the value of the invocazioneServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvocazioneServizio }
     *     
     */
    public void setInvocazioneServizio(InvocazioneServizio value) {
        this.invocazioneServizio = value;
    }

    /**
     * Gets the value of the rispostaAsincrona property.
     * 
     * @return
     *     possible object is
     *     {@link RispostaAsincrona }
     *     
     */
    public RispostaAsincrona getRispostaAsincrona() {
        return this.rispostaAsincrona;
    }

    /**
     * Sets the value of the rispostaAsincrona property.
     * 
     * @param value
     *     allowed object is
     *     {@link RispostaAsincrona }
     *     
     */
    public void setRispostaAsincrona(RispostaAsincrona value) {
        this.rispostaAsincrona = value;
    }

    /**
     * Gets the value of the tipoSoggettoProprietario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoSoggettoProprietario() {
        return this.tipoSoggettoProprietario;
    }

    /**
     * Sets the value of the tipoSoggettoProprietario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoSoggettoProprietario(String value) {
        this.tipoSoggettoProprietario = value;
    }

    /**
     * Gets the value of the nomeSoggettoProprietario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeSoggettoProprietario() {
        return this.nomeSoggettoProprietario;
    }

    /**
     * Sets the value of the nomeSoggettoProprietario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeSoggettoProprietario(String value) {
        this.nomeSoggettoProprietario = value;
    }

    /**
     * Gets the value of the tipologiaFruizione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipologiaFruizione() {
        return this.tipologiaFruizione;
    }

    /**
     * Sets the value of the tipologiaFruizione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipologiaFruizione(String value) {
        this.tipologiaFruizione = value;
    }

    /**
     * Gets the value of the tipologiaErogazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipologiaErogazione() {
        return this.tipologiaErogazione;
    }

    /**
     * Sets the value of the tipologiaErogazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipologiaErogazione(String value) {
        this.tipologiaErogazione = value;
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
     * Gets the value of the descrizione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizione() {
        return this.descrizione;
    }

    /**
     * Sets the value of the descrizione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

    /**
     * Gets the value of the oraRegistrazioneMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOraRegistrazioneMin() {
        return this.oraRegistrazioneMin;
    }

    /**
     * Sets the value of the oraRegistrazioneMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOraRegistrazioneMin(XMLGregorianCalendar value) {
        this.oraRegistrazioneMin = value;
    }

    /**
     * Gets the value of the oraRegistrazioneMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOraRegistrazioneMax() {
        return this.oraRegistrazioneMax;
    }

    /**
     * Sets the value of the oraRegistrazioneMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOraRegistrazioneMax(XMLGregorianCalendar value) {
        this.oraRegistrazioneMax = value;
    }

    /**
     * Gets the value of the orCondition property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOrCondition() {
        return this.orCondition;
    }

    /**
     * Sets the value of the orCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOrCondition(Boolean value) {
        this.orCondition = value;
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

}
