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

package org.openspcoop2.core.registry.ws.client.scope.crud;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.registry.constants.ScopeContesto;


/**
 * <p>Java class for search-filter-scope complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-scope"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipologia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-esterno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="contesto-utilizzo" type="{http://www.openspcoop2.org/core/registry}ScopeContesto" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
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
@XmlType(name = "search-filter-scope", propOrder = {
    "nome",
    "descrizione",
    "tipologia",
    "nomeEsterno",
    "contestoUtilizzo",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "orCondition",
    "limit",
    "offset"
})
public class SearchFilterScope {

    protected String nome;
    protected String descrizione;
    protected String tipologia;
    @XmlElement(name = "nome-esterno")
    protected String nomeEsterno;
    @XmlElement(name = "contesto-utilizzo")
    @XmlSchemaType(name = "string")
    protected ScopeContesto contestoUtilizzo;
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
     * Gets the value of the tipologia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipologia() {
        return this.tipologia;
    }

    /**
     * Sets the value of the tipologia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipologia(String value) {
        this.tipologia = value;
    }

    /**
     * Gets the value of the nomeEsterno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeEsterno() {
        return this.nomeEsterno;
    }

    /**
     * Sets the value of the nomeEsterno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeEsterno(String value) {
        this.nomeEsterno = value;
    }

    /**
     * Gets the value of the contestoUtilizzo property.
     * 
     * @return
     *     possible object is
     *     {@link ScopeContesto }
     *     
     */
    public ScopeContesto getContestoUtilizzo() {
        return this.contestoUtilizzo;
    }

    /**
     * Sets the value of the contestoUtilizzo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScopeContesto }
     *     
     */
    public void setContestoUtilizzo(ScopeContesto value) {
        this.contestoUtilizzo = value;
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
