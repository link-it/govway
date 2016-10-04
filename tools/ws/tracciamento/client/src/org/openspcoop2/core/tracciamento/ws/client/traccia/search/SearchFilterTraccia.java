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

package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.tracciamento.constants.TipoTraccia;


/**
 * <p>Java class for search-filter-traccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-traccia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dominio" type="{http://www.openspcoop2.org/core/tracciamento/management}dominio" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="busta" type="{http://www.openspcoop2.org/core/tracciamento/management}busta" minOccurs="0"/>
 *         &lt;element name="ricerca-solo-buste-errore" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/tracciamento}TipoTraccia" minOccurs="0"/>
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-filter-traccia", propOrder = {
    "dominio",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "identificativoCorrelazioneRichiesta",
    "identificativoCorrelazioneRisposta",
    "correlazioneApplicativaAndMatch",
    "busta",
    "ricercaSoloBusteErrore",
    "tipo",
    "limit",
    "offset",
    "descOrder"
})
public class SearchFilterTraccia {

    protected Dominio dominio;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    @XmlElement(name = "identificativo-correlazione-richiesta")
    protected String identificativoCorrelazioneRichiesta;
    @XmlElement(name = "identificativo-correlazione-risposta")
    protected String identificativoCorrelazioneRisposta;
    @XmlElement(name = "correlazione-applicativa-and-match")
    protected Boolean correlazioneApplicativaAndMatch;
    protected Busta busta;
    @XmlElement(name = "ricerca-solo-buste-errore")
    protected Boolean ricercaSoloBusteErrore;
    protected TipoTraccia tipo;
    protected BigInteger limit;
    protected BigInteger offset;
    protected Boolean descOrder;

    /**
     * Gets the value of the dominio property.
     * 
     * @return
     *     possible object is
     *     {@link Dominio }
     *     
     */
    public Dominio getDominio() {
        return this.dominio;
    }

    /**
     * Sets the value of the dominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dominio }
     *     
     */
    public void setDominio(Dominio value) {
        this.dominio = value;
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
     * Gets the value of the identificativoCorrelazioneRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoCorrelazioneRichiesta() {
        return this.identificativoCorrelazioneRichiesta;
    }

    /**
     * Sets the value of the identificativoCorrelazioneRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoCorrelazioneRichiesta(String value) {
        this.identificativoCorrelazioneRichiesta = value;
    }

    /**
     * Gets the value of the identificativoCorrelazioneRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoCorrelazioneRisposta() {
        return this.identificativoCorrelazioneRisposta;
    }

    /**
     * Sets the value of the identificativoCorrelazioneRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoCorrelazioneRisposta(String value) {
        this.identificativoCorrelazioneRisposta = value;
    }

    /**
     * Gets the value of the correlazioneApplicativaAndMatch property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCorrelazioneApplicativaAndMatch() {
        return this.correlazioneApplicativaAndMatch;
    }

    /**
     * Sets the value of the correlazioneApplicativaAndMatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCorrelazioneApplicativaAndMatch(Boolean value) {
        this.correlazioneApplicativaAndMatch = value;
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
     * Gets the value of the ricercaSoloBusteErrore property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRicercaSoloBusteErrore() {
        return this.ricercaSoloBusteErrore;
    }

    /**
     * Sets the value of the ricercaSoloBusteErrore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRicercaSoloBusteErrore(Boolean value) {
        this.ricercaSoloBusteErrore = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link TipoTraccia }
     *     
     */
    public TipoTraccia getTipo() {
        return this.tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoTraccia }
     *     
     */
    public void setTipo(TipoTraccia value) {
        this.tipo = value;
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

    /**
     * Gets the value of the descOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDescOrder() {
        return this.descOrder;
    }

    /**
     * Sets the value of the descOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDescOrder(Boolean value) {
        this.descOrder = value;
    }

}
