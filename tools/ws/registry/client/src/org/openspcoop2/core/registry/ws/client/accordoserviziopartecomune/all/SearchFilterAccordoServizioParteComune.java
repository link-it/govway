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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.all;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.ServiceBinding;


/**
 * <p>Java class for search-filter-accordo-servizio-parte-comune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-accordo-servizio-parte-comune"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="soggetto-referente" type="{http://www.openspcoop2.org/core/registry/management}id-soggetto" minOccurs="0"/&gt;
 *         &lt;element name="servizio-composto" type="{http://www.openspcoop2.org/core/registry/management}accordo-servizio-parte-comune-servizio-composto" minOccurs="0"/&gt;
 *         &lt;element name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="privato" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="service-binding" type="{http://www.openspcoop2.org/core/registry}ServiceBinding" minOccurs="0"/&gt;
 *         &lt;element name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" minOccurs="0"/&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="formato-specifica" type="{http://www.openspcoop2.org/core/registry}FormatoSpecifica" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/&gt;
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
@XmlType(name = "search-filter-accordo-servizio-parte-comune", propOrder = {
    "soggettoReferente",
    "servizioComposto",
    "statoPackage",
    "privato",
    "serviceBinding",
    "messageType",
    "nome",
    "descrizione",
    "formatoSpecifica",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "versione",
    "orCondition",
    "limit",
    "offset"
})
public class SearchFilterAccordoServizioParteComune {

    @XmlElement(name = "soggetto-referente")
    protected IdSoggetto soggettoReferente;
    @XmlElement(name = "servizio-composto")
    protected AccordoServizioParteComuneServizioComposto servizioComposto;
    @XmlElement(name = "stato-package")
    protected String statoPackage;
    protected Boolean privato;
    @XmlElement(name = "service-binding")
    @XmlSchemaType(name = "string")
    protected ServiceBinding serviceBinding;
    @XmlElement(name = "message-type")
    @XmlSchemaType(name = "string")
    protected MessageType messageType;
    protected String nome;
    protected String descrizione;
    @XmlElement(name = "formato-specifica")
    @XmlSchemaType(name = "string")
    protected FormatoSpecifica formatoSpecifica;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    @XmlSchemaType(name = "unsignedInt")
    protected Long versione;
    protected Boolean orCondition;
    protected BigInteger limit;
    protected BigInteger offset;

    /**
     * Gets the value of the soggettoReferente property.
     * 
     * @return
     *     possible object is
     *     {@link IdSoggetto }
     *     
     */
    public IdSoggetto getSoggettoReferente() {
        return this.soggettoReferente;
    }

    /**
     * Sets the value of the soggettoReferente property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdSoggetto }
     *     
     */
    public void setSoggettoReferente(IdSoggetto value) {
        this.soggettoReferente = value;
    }

    /**
     * Gets the value of the servizioComposto property.
     * 
     * @return
     *     possible object is
     *     {@link AccordoServizioParteComuneServizioComposto }
     *     
     */
    public AccordoServizioParteComuneServizioComposto getServizioComposto() {
        return this.servizioComposto;
    }

    /**
     * Sets the value of the servizioComposto property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccordoServizioParteComuneServizioComposto }
     *     
     */
    public void setServizioComposto(AccordoServizioParteComuneServizioComposto value) {
        this.servizioComposto = value;
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
     * Gets the value of the serviceBinding property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceBinding }
     *     
     */
    public ServiceBinding getServiceBinding() {
        return this.serviceBinding;
    }

    /**
     * Sets the value of the serviceBinding property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceBinding }
     *     
     */
    public void setServiceBinding(ServiceBinding value) {
        this.serviceBinding = value;
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
     * Gets the value of the formatoSpecifica property.
     * 
     * @return
     *     possible object is
     *     {@link FormatoSpecifica }
     *     
     */
    public FormatoSpecifica getFormatoSpecifica() {
        return this.formatoSpecifica;
    }

    /**
     * Sets the value of the formatoSpecifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormatoSpecifica }
     *     
     */
    public void setFormatoSpecifica(FormatoSpecifica value) {
        this.formatoSpecifica = value;
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
