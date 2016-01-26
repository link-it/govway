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

package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;


/**
 * <p>Java class for search-filter-informazioni-protocollo-transazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-informazioni-protocollo-transazione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipoPdD" type="{http://www.openspcoop2.org/core/diagnostica}TipoPdD" minOccurs="0"/>
 *         &lt;element name="identificativo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dominio" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-transazione" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fruitore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0"/>
 *         &lt;element name="erogatore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0"/>
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/diagnostica/management}servizio" minOccurs="0"/>
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="protocollo" type="{http://www.openspcoop2.org/core/diagnostica/management}protocollo" minOccurs="0"/>
 *         &lt;element name="filtro-servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="filtro-informazioni-diagnostici" type="{http://www.openspcoop2.org/core/diagnostica/management}filtro-informazioni-diagnostici" minOccurs="0"/>
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
@XmlType(name = "search-filter-informazioni-protocollo-transazione", propOrder = {
    "tipoPdD",
    "identificativoRichiesta",
    "dominio",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "nomePorta",
    "fruitore",
    "erogatore",
    "servizio",
    "azione",
    "identificativoCorrelazioneRichiesta",
    "identificativoCorrelazioneRisposta",
    "correlazioneApplicativaAndMatch",
    "protocollo",
    "filtroServizioApplicativo",
    "filtroInformazioniDiagnostici",
    "limit",
    "offset",
    "descOrder"
})
public class SearchFilterInformazioniProtocolloTransazione {

    protected TipoPdD tipoPdD;
    @XmlElement(name = "identificativo-richiesta")
    protected String identificativoRichiesta;
    protected DominioTransazione dominio;
    @XmlElement(name = "ora-registrazione-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMin;
    @XmlElement(name = "ora-registrazione-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar oraRegistrazioneMax;
    @XmlElement(name = "nome-porta")
    protected String nomePorta;
    protected Soggetto fruitore;
    protected Soggetto erogatore;
    protected Servizio servizio;
    protected String azione;
    @XmlElement(name = "identificativo-correlazione-richiesta")
    protected String identificativoCorrelazioneRichiesta;
    @XmlElement(name = "identificativo-correlazione-risposta")
    protected String identificativoCorrelazioneRisposta;
    @XmlElement(name = "correlazione-applicativa-and-match")
    protected Boolean correlazioneApplicativaAndMatch;
    protected Protocollo protocollo;
    @XmlElement(name = "filtro-servizio-applicativo")
    protected String filtroServizioApplicativo;
    @XmlElement(name = "filtro-informazioni-diagnostici")
    protected FiltroInformazioniDiagnostici filtroInformazioniDiagnostici;
    protected BigInteger limit;
    protected BigInteger offset;
    protected Boolean descOrder;

    /**
     * Gets the value of the tipoPdD property.
     * 
     * @return
     *     possible object is
     *     {@link TipoPdD }
     *     
     */
    public TipoPdD getTipoPdD() {
        return this.tipoPdD;
    }

    /**
     * Sets the value of the tipoPdD property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPdD }
     *     
     */
    public void setTipoPdD(TipoPdD value) {
        this.tipoPdD = value;
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
     * Gets the value of the dominio property.
     * 
     * @return
     *     possible object is
     *     {@link DominioTransazione }
     *     
     */
    public DominioTransazione getDominio() {
        return this.dominio;
    }

    /**
     * Sets the value of the dominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link DominioTransazione }
     *     
     */
    public void setDominio(DominioTransazione value) {
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
     * Gets the value of the nomePorta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomePorta() {
        return this.nomePorta;
    }

    /**
     * Sets the value of the nomePorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomePorta(String value) {
        this.nomePorta = value;
    }

    /**
     * Gets the value of the fruitore property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getFruitore() {
        return this.fruitore;
    }

    /**
     * Sets the value of the fruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setFruitore(Soggetto value) {
        this.fruitore = value;
    }

    /**
     * Gets the value of the erogatore property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getErogatore() {
        return this.erogatore;
    }

    /**
     * Sets the value of the erogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setErogatore(Soggetto value) {
        this.erogatore = value;
    }

    /**
     * Gets the value of the servizio property.
     * 
     * @return
     *     possible object is
     *     {@link Servizio }
     *     
     */
    public Servizio getServizio() {
        return this.servizio;
    }

    /**
     * Sets the value of the servizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Servizio }
     *     
     */
    public void setServizio(Servizio value) {
        this.servizio = value;
    }

    /**
     * Gets the value of the azione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAzione() {
        return this.azione;
    }

    /**
     * Sets the value of the azione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAzione(String value) {
        this.azione = value;
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
     * Gets the value of the filtroServizioApplicativo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFiltroServizioApplicativo() {
        return this.filtroServizioApplicativo;
    }

    /**
     * Sets the value of the filtroServizioApplicativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFiltroServizioApplicativo(String value) {
        this.filtroServizioApplicativo = value;
    }

    /**
     * Gets the value of the filtroInformazioniDiagnostici property.
     * 
     * @return
     *     possible object is
     *     {@link FiltroInformazioniDiagnostici }
     *     
     */
    public FiltroInformazioniDiagnostici getFiltroInformazioniDiagnostici() {
        return this.filtroInformazioniDiagnostici;
    }

    /**
     * Sets the value of the filtroInformazioniDiagnostici property.
     * 
     * @param value
     *     allowed object is
     *     {@link FiltroInformazioniDiagnostici }
     *     
     */
    public void setFiltroInformazioniDiagnostici(FiltroInformazioniDiagnostici value) {
        this.filtroInformazioniDiagnostici = value;
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
