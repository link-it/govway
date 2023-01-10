/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.TipologiaServizio;


/**
 * <p>Java class for search-filter-accordo-servizio-parte-specifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-accordo-servizio-parte-specifica"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="configurazione-servizio" type="{http://www.openspcoop2.org/core/registry/management}configurazione-servizio" minOccurs="0"/&gt;
 *         &lt;element name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="privato" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
 *         &lt;element name="accordo-servizio-parte-comune" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipologia-servizio" type="{http://www.openspcoop2.org/core/registry}TipologiaServizio" minOccurs="0"/&gt;
 *         &lt;element name="port-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="versione-protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" minOccurs="0"/&gt;
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-filter-accordo-servizio-parte-specifica", propOrder = {
    "configurazioneServizio",
    "statoPackage",
    "privato",
    "tipoSoggettoErogatore",
    "nomeSoggettoErogatore",
    "tipo",
    "nome",
    "versione",
    "accordoServizioParteComune",
    "tipologiaServizio",
    "portType",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "versioneProtocollo",
    "descrizione",
    "messageType",
    "orCondition",
    "limit",
    "offset"
})
public class SearchFilterAccordoServizioParteSpecifica {

    @XmlElement(name = "configurazione-servizio")
    protected ConfigurazioneServizio configurazioneServizio;
    @XmlElement(name = "stato-package")
    protected String statoPackage;
    protected Boolean privato;
    @XmlElement(name = "tipo-soggetto-erogatore")
    protected String tipoSoggettoErogatore;
    @XmlElement(name = "nome-soggetto-erogatore")
    protected String nomeSoggettoErogatore;
    protected String tipo;
    protected String nome;
    @XmlSchemaType(name = "unsignedInt")
    protected Long versione;
    @XmlElement(name = "accordo-servizio-parte-comune")
    protected String accordoServizioParteComune;
    @XmlElement(name = "tipologia-servizio")
    @XmlSchemaType(name = "string")
    protected TipologiaServizio tipologiaServizio;
    @XmlElement(name = "port-type")
    protected String portType;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    @XmlElement(name = "versione-protocollo")
    protected String versioneProtocollo;
    protected String descrizione;
    @XmlElement(name = "message-type")
    @XmlSchemaType(name = "string")
    protected MessageType messageType;
    protected Boolean orCondition;
    protected BigInteger limit;
    protected BigInteger offset;

    /**
     * Gets the value of the configurazioneServizio property.
     * 
     * @return
     *     possible object is
     *     {@link ConfigurazioneServizio }
     *     
     */
    public ConfigurazioneServizio getConfigurazioneServizio() {
        return this.configurazioneServizio;
    }

    /**
     * Sets the value of the configurazioneServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfigurazioneServizio }
     *     
     */
    public void setConfigurazioneServizio(ConfigurazioneServizio value) {
        this.configurazioneServizio = value;
    }

    /**
     * Gets the value of the statoPackage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatoPackage() {
        return this.statoPackage;
    }

    /**
     * Sets the value of the statoPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatoPackage(String value) {
        this.statoPackage = value;
    }

    /**
     * Gets the value of the privato property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPrivato() {
        return this.privato;
    }

    /**
     * Sets the value of the privato property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrivato(Boolean value) {
        this.privato = value;
    }

    /**
     * Gets the value of the tipoSoggettoErogatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoSoggettoErogatore() {
        return this.tipoSoggettoErogatore;
    }

    /**
     * Sets the value of the tipoSoggettoErogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoSoggettoErogatore(String value) {
        this.tipoSoggettoErogatore = value;
    }

    /**
     * Gets the value of the nomeSoggettoErogatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeSoggettoErogatore() {
        return this.nomeSoggettoErogatore;
    }

    /**
     * Sets the value of the nomeSoggettoErogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeSoggettoErogatore(String value) {
        this.nomeSoggettoErogatore = value;
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
     * Gets the value of the versione property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getVersione() {
        return this.versione;
    }

    /**
     * Sets the value of the versione property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setVersione(Long value) {
        this.versione = value;
    }

    /**
     * Gets the value of the accordoServizioParteComune property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccordoServizioParteComune() {
        return this.accordoServizioParteComune;
    }

    /**
     * Sets the value of the accordoServizioParteComune property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccordoServizioParteComune(String value) {
        this.accordoServizioParteComune = value;
    }

    /**
     * Gets the value of the tipologiaServizio property.
     * 
     * @return
     *     possible object is
     *     {@link TipologiaServizio }
     *     
     */
    public TipologiaServizio getTipologiaServizio() {
        return this.tipologiaServizio;
    }

    /**
     * Sets the value of the tipologiaServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipologiaServizio }
     *     
     */
    public void setTipologiaServizio(TipologiaServizio value) {
        this.tipologiaServizio = value;
    }

    /**
     * Gets the value of the portType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortType() {
        return this.portType;
    }

    /**
     * Sets the value of the portType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortType(String value) {
        this.portType = value;
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
     * Gets the value of the versioneProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersioneProtocollo() {
        return this.versioneProtocollo;
    }

    /**
     * Sets the value of the versioneProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersioneProtocollo(String value) {
        this.versioneProtocollo = value;
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
     * Gets the value of the messageType property.
     * 
     * @return
     *     possible object is
     *     {@link MessageType }
     *     
     */
    public MessageType getMessageType() {
        return this.messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageType }
     *     
     */
    public void setMessageType(MessageType value) {
        this.messageType = value;
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
