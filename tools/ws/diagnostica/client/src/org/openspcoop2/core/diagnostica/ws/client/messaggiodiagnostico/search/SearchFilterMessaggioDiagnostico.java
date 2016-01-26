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

package org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for search-filter-messaggio-diagnostico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-messaggio-diagnostico">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dominio" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-diagnostico" minOccurs="0"/>
 *         &lt;element name="identificativo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severita" type="{http://www.openspcoop2.org/core/diagnostica}LivelloDiSeveritaType" minOccurs="0"/>
 *         &lt;element name="protocollo" type="{http://www.openspcoop2.org/core/diagnostica/management}protocollo" minOccurs="0"/>
 *         &lt;element name="filtro-informazione-protocollo" type="{http://www.openspcoop2.org/core/diagnostica/management}filtro-informazione-protocollo" minOccurs="0"/>
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
@XmlType(name = "search-filter-messaggio-diagnostico", propOrder = {
    "dominio",
    "identificativoRichiesta",
    "identificativoRisposta",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "codice",
    "messaggio",
    "severita",
    "protocollo",
    "filtroInformazioneProtocollo",
    "limit",
    "offset",
    "descOrder"
})
public class SearchFilterMessaggioDiagnostico {

    protected DominioDiagnostico dominio;
    @XmlElement(name = "identificativo-richiesta")
    protected String identificativoRichiesta;
    @XmlElement(name = "identificativo-risposta")
    protected String identificativoRisposta;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    protected String codice;
    protected String messaggio;
    protected BigInteger severita;
    protected Protocollo protocollo;
    @XmlElement(name = "filtro-informazione-protocollo")
    protected FiltroInformazioneProtocollo filtroInformazioneProtocollo;
    protected BigInteger limit;
    protected BigInteger offset;
    protected Boolean descOrder;

    /**
     * Gets the value of the dominio property.
     * 
     * @return
     *     possible object is
     *     {@link DominioDiagnostico }
     *     
     */
    public DominioDiagnostico getDominio() {
        return this.dominio;
    }

    /**
     * Sets the value of the dominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link DominioDiagnostico }
     *     
     */
    public void setDominio(DominioDiagnostico value) {
        this.dominio = value;
    }

    /**
     * Gets the value of the identificativoRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoRichiesta() {
        return this.identificativoRichiesta;
    }

    /**
     * Sets the value of the identificativoRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoRichiesta(String value) {
        this.identificativoRichiesta = value;
    }

    /**
     * Gets the value of the identificativoRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoRisposta() {
        return this.identificativoRisposta;
    }

    /**
     * Sets the value of the identificativoRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoRisposta(String value) {
        this.identificativoRisposta = value;
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
     * Gets the value of the codice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodice() {
        return this.codice;
    }

    /**
     * Sets the value of the codice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodice(String value) {
        this.codice = value;
    }

    /**
     * Gets the value of the messaggio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessaggio() {
        return this.messaggio;
    }

    /**
     * Sets the value of the messaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessaggio(String value) {
        this.messaggio = value;
    }

    /**
     * Gets the value of the severita property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeverita() {
        return this.severita;
    }

    /**
     * Sets the value of the severita property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeverita(BigInteger value) {
        this.severita = value;
    }

    /**
     * Gets the value of the protocollo property.
     * 
     * @return
     *     possible object is
     *     {@link Protocollo }
     *     
     */
    public Protocollo getProtocollo() {
        return this.protocollo;
    }

    /**
     * Sets the value of the protocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Protocollo }
     *     
     */
    public void setProtocollo(Protocollo value) {
        this.protocollo = value;
    }

    /**
     * Gets the value of the filtroInformazioneProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link FiltroInformazioneProtocollo }
     *     
     */
    public FiltroInformazioneProtocollo getFiltroInformazioneProtocollo() {
        return this.filtroInformazioneProtocollo;
    }

    /**
     * Sets the value of the filtroInformazioneProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FiltroInformazioneProtocollo }
     *     
     */
    public void setFiltroInformazioneProtocollo(FiltroInformazioneProtocollo value) {
        this.filtroInformazioneProtocollo = value;
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
