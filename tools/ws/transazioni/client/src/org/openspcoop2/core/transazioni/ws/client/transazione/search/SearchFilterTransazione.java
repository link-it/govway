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

package org.openspcoop2.core.transazioni.ws.client.transazione.search;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openspcoop2.core.transazioni.constants.PddRuolo;


/**
 * <p>Java class for search-filter-transazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-transazione"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="esito" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="esito-contesto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="data-accettazione-richiesta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-accettazione-richiesta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-ingresso-richiesta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-ingresso-richiesta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-uscita-richiesta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-uscita-richiesta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-accettazione-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-accettazione-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-ingresso-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-ingresso-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-uscita-risposta-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="data-uscita-risposta-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="richiesta-ingresso-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="richiesta-ingresso-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="richiesta-uscita-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="richiesta-uscita-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="risposta-ingresso-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="risposta-ingresso-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="risposta-uscita-bytes-min" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="risposta-uscita-bytes-max" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/&gt;
 *         &lt;element name="pdd-codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pdd-tipo-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pdd-nome-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pdd-ruolo" type="{http://www.openspcoop2.org/core/transazioni}pdd-ruolo" minOccurs="0"/&gt;
 *         &lt;element name="tipo-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idporta-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="indirizzo-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idporta-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="indirizzo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id-messaggio-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id-messaggio-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="profilo-collaborazione-op2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="profilo-collaborazione-prot" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id-collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="uri-accordo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id-asincrono" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipo-servizio-correlato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-servizio-correlato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id-correlazione-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id-correlazione-applicativa-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="operazione-im" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="location-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="location-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="credenziali" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="location-connettore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="url-invocazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cluster-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="socket-client-address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transport-client-address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-filter-transazione", propOrder = {
    "idTransazione",
    "stato",
    "esito",
    "esitoContesto",
    "protocollo",
    "dataAccettazioneRichiestaMin",
    "dataAccettazioneRichiestaMax",
    "dataIngressoRichiestaMin",
    "dataIngressoRichiestaMax",
    "dataUscitaRichiestaMin",
    "dataUscitaRichiestaMax",
    "dataAccettazioneRispostaMin",
    "dataAccettazioneRispostaMax",
    "dataIngressoRispostaMin",
    "dataIngressoRispostaMax",
    "dataUscitaRispostaMin",
    "dataUscitaRispostaMax",
    "richiestaIngressoBytesMin",
    "richiestaIngressoBytesMax",
    "richiestaUscitaBytesMin",
    "richiestaUscitaBytesMax",
    "rispostaIngressoBytesMin",
    "rispostaIngressoBytesMax",
    "rispostaUscitaBytesMin",
    "rispostaUscitaBytesMax",
    "pddCodice",
    "pddTipoSoggetto",
    "pddNomeSoggetto",
    "pddRuolo",
    "tipoSoggettoFruitore",
    "nomeSoggettoFruitore",
    "idportaSoggettoFruitore",
    "indirizzoSoggettoFruitore",
    "tipoSoggettoErogatore",
    "nomeSoggettoErogatore",
    "idportaSoggettoErogatore",
    "indirizzoSoggettoErogatore",
    "idMessaggioRichiesta",
    "idMessaggioRisposta",
    "profiloCollaborazioneOp2",
    "profiloCollaborazioneProt",
    "idCollaborazione",
    "uriAccordoServizio",
    "tipoServizio",
    "nomeServizio",
    "versioneServizio",
    "azione",
    "idAsincrono",
    "tipoServizioCorrelato",
    "nomeServizioCorrelato",
    "idCorrelazioneApplicativa",
    "idCorrelazioneApplicativaRisposta",
    "servizioApplicativoFruitore",
    "servizioApplicativoErogatore",
    "operazioneIm",
    "locationRichiesta",
    "locationRisposta",
    "nomePorta",
    "credenziali",
    "locationConnettore",
    "urlInvocazione",
    "clusterId",
    "socketClientAddress",
    "transportClientAddress",
    "limit",
    "offset",
    "descOrder"
})
public class SearchFilterTransazione {

    @XmlElement(name = "id-transazione")
    protected String idTransazione;
    protected String stato;
    protected Integer esito;
    @XmlElement(name = "esito-contesto")
    protected String esitoContesto;
    protected String protocollo;
    @XmlElement(name = "data-accettazione-richiesta-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataAccettazioneRichiestaMin;
    @XmlElement(name = "data-accettazione-richiesta-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataAccettazioneRichiestaMax;
    @XmlElement(name = "data-ingresso-richiesta-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataIngressoRichiestaMin;
    @XmlElement(name = "data-ingresso-richiesta-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataIngressoRichiestaMax;
    @XmlElement(name = "data-uscita-richiesta-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataUscitaRichiestaMin;
    @XmlElement(name = "data-uscita-richiesta-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataUscitaRichiestaMax;
    @XmlElement(name = "data-accettazione-risposta-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataAccettazioneRispostaMin;
    @XmlElement(name = "data-accettazione-risposta-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataAccettazioneRispostaMax;
    @XmlElement(name = "data-ingresso-risposta-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataIngressoRispostaMin;
    @XmlElement(name = "data-ingresso-risposta-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataIngressoRispostaMax;
    @XmlElement(name = "data-uscita-risposta-min")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataUscitaRispostaMin;
    @XmlElement(name = "data-uscita-risposta-max")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataUscitaRispostaMax;
    @XmlElement(name = "richiesta-ingresso-bytes-min")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger richiestaIngressoBytesMin;
    @XmlElement(name = "richiesta-ingresso-bytes-max")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger richiestaIngressoBytesMax;
    @XmlElement(name = "richiesta-uscita-bytes-min")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger richiestaUscitaBytesMin;
    @XmlElement(name = "richiesta-uscita-bytes-max")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger richiestaUscitaBytesMax;
    @XmlElement(name = "risposta-ingresso-bytes-min")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger rispostaIngressoBytesMin;
    @XmlElement(name = "risposta-ingresso-bytes-max")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger rispostaIngressoBytesMax;
    @XmlElement(name = "risposta-uscita-bytes-min")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger rispostaUscitaBytesMin;
    @XmlElement(name = "risposta-uscita-bytes-max")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger rispostaUscitaBytesMax;
    @XmlElement(name = "pdd-codice")
    protected String pddCodice;
    @XmlElement(name = "pdd-tipo-soggetto")
    protected String pddTipoSoggetto;
    @XmlElement(name = "pdd-nome-soggetto")
    protected String pddNomeSoggetto;
    @XmlElement(name = "pdd-ruolo")
    @XmlSchemaType(name = "string")
    protected PddRuolo pddRuolo;
    @XmlElement(name = "tipo-soggetto-fruitore")
    protected String tipoSoggettoFruitore;
    @XmlElement(name = "nome-soggetto-fruitore")
    protected String nomeSoggettoFruitore;
    @XmlElement(name = "idporta-soggetto-fruitore")
    protected String idportaSoggettoFruitore;
    @XmlElement(name = "indirizzo-soggetto-fruitore")
    protected String indirizzoSoggettoFruitore;
    @XmlElement(name = "tipo-soggetto-erogatore")
    protected String tipoSoggettoErogatore;
    @XmlElement(name = "nome-soggetto-erogatore")
    protected String nomeSoggettoErogatore;
    @XmlElement(name = "idporta-soggetto-erogatore")
    protected String idportaSoggettoErogatore;
    @XmlElement(name = "indirizzo-soggetto-erogatore")
    protected String indirizzoSoggettoErogatore;
    @XmlElement(name = "id-messaggio-richiesta")
    protected String idMessaggioRichiesta;
    @XmlElement(name = "id-messaggio-risposta")
    protected String idMessaggioRisposta;
    @XmlElement(name = "profilo-collaborazione-op2")
    protected String profiloCollaborazioneOp2;
    @XmlElement(name = "profilo-collaborazione-prot")
    protected String profiloCollaborazioneProt;
    @XmlElement(name = "id-collaborazione")
    protected String idCollaborazione;
    @XmlElement(name = "uri-accordo-servizio")
    protected String uriAccordoServizio;
    @XmlElement(name = "tipo-servizio")
    protected String tipoServizio;
    @XmlElement(name = "nome-servizio")
    protected String nomeServizio;
    @XmlElement(name = "versione-servizio")
    protected Integer versioneServizio;
    protected String azione;
    @XmlElement(name = "id-asincrono")
    protected String idAsincrono;
    @XmlElement(name = "tipo-servizio-correlato")
    protected String tipoServizioCorrelato;
    @XmlElement(name = "nome-servizio-correlato")
    protected String nomeServizioCorrelato;
    @XmlElement(name = "id-correlazione-applicativa")
    protected String idCorrelazioneApplicativa;
    @XmlElement(name = "id-correlazione-applicativa-risposta")
    protected String idCorrelazioneApplicativaRisposta;
    @XmlElement(name = "servizio-applicativo-fruitore")
    protected String servizioApplicativoFruitore;
    @XmlElement(name = "servizio-applicativo-erogatore")
    protected String servizioApplicativoErogatore;
    @XmlElement(name = "operazione-im")
    protected String operazioneIm;
    @XmlElement(name = "location-richiesta")
    protected String locationRichiesta;
    @XmlElement(name = "location-risposta")
    protected String locationRisposta;
    @XmlElement(name = "nome-porta")
    protected String nomePorta;
    protected String credenziali;
    @XmlElement(name = "location-connettore")
    protected String locationConnettore;
    @XmlElement(name = "url-invocazione")
    protected String urlInvocazione;
    @XmlElement(name = "cluster-id")
    protected String clusterId;
    @XmlElement(name = "socket-client-address")
    protected String socketClientAddress;
    @XmlElement(name = "transport-client-address")
    protected String transportClientAddress;
    protected BigInteger limit;
    protected BigInteger offset;
    protected Boolean descOrder;

    /**
     * Gets the value of the idTransazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTransazione() {
        return this.idTransazione;
    }

    /**
     * Sets the value of the idTransazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTransazione(String value) {
        this.idTransazione = value;
    }

    /**
     * Gets the value of the stato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStato() {
        return this.stato;
    }

    /**
     * Sets the value of the stato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStato(String value) {
        this.stato = value;
    }

    /**
     * Gets the value of the esito property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEsito() {
        return this.esito;
    }

    /**
     * Sets the value of the esito property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEsito(Integer value) {
        this.esito = value;
    }

    /**
     * Gets the value of the esitoContesto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsitoContesto() {
        return this.esitoContesto;
    }

    /**
     * Sets the value of the esitoContesto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsitoContesto(String value) {
        this.esitoContesto = value;
    }

    /**
     * Gets the value of the protocollo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocollo() {
        return this.protocollo;
    }

    /**
     * Sets the value of the protocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocollo(String value) {
        this.protocollo = value;
    }

    /**
     * Gets the value of the dataAccettazioneRichiestaMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAccettazioneRichiestaMin() {
        return this.dataAccettazioneRichiestaMin;
    }

    /**
     * Sets the value of the dataAccettazioneRichiestaMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAccettazioneRichiestaMin(XMLGregorianCalendar value) {
        this.dataAccettazioneRichiestaMin = value;
    }

    /**
     * Gets the value of the dataAccettazioneRichiestaMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAccettazioneRichiestaMax() {
        return this.dataAccettazioneRichiestaMax;
    }

    /**
     * Sets the value of the dataAccettazioneRichiestaMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAccettazioneRichiestaMax(XMLGregorianCalendar value) {
        this.dataAccettazioneRichiestaMax = value;
    }

    /**
     * Gets the value of the dataIngressoRichiestaMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataIngressoRichiestaMin() {
        return this.dataIngressoRichiestaMin;
    }

    /**
     * Sets the value of the dataIngressoRichiestaMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataIngressoRichiestaMin(XMLGregorianCalendar value) {
        this.dataIngressoRichiestaMin = value;
    }

    /**
     * Gets the value of the dataIngressoRichiestaMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataIngressoRichiestaMax() {
        return this.dataIngressoRichiestaMax;
    }

    /**
     * Sets the value of the dataIngressoRichiestaMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataIngressoRichiestaMax(XMLGregorianCalendar value) {
        this.dataIngressoRichiestaMax = value;
    }

    /**
     * Gets the value of the dataUscitaRichiestaMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataUscitaRichiestaMin() {
        return this.dataUscitaRichiestaMin;
    }

    /**
     * Sets the value of the dataUscitaRichiestaMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataUscitaRichiestaMin(XMLGregorianCalendar value) {
        this.dataUscitaRichiestaMin = value;
    }

    /**
     * Gets the value of the dataUscitaRichiestaMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataUscitaRichiestaMax() {
        return this.dataUscitaRichiestaMax;
    }

    /**
     * Sets the value of the dataUscitaRichiestaMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataUscitaRichiestaMax(XMLGregorianCalendar value) {
        this.dataUscitaRichiestaMax = value;
    }

    /**
     * Gets the value of the dataAccettazioneRispostaMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAccettazioneRispostaMin() {
        return this.dataAccettazioneRispostaMin;
    }

    /**
     * Sets the value of the dataAccettazioneRispostaMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAccettazioneRispostaMin(XMLGregorianCalendar value) {
        this.dataAccettazioneRispostaMin = value;
    }

    /**
     * Gets the value of the dataAccettazioneRispostaMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAccettazioneRispostaMax() {
        return this.dataAccettazioneRispostaMax;
    }

    /**
     * Sets the value of the dataAccettazioneRispostaMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAccettazioneRispostaMax(XMLGregorianCalendar value) {
        this.dataAccettazioneRispostaMax = value;
    }

    /**
     * Gets the value of the dataIngressoRispostaMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataIngressoRispostaMin() {
        return this.dataIngressoRispostaMin;
    }

    /**
     * Sets the value of the dataIngressoRispostaMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataIngressoRispostaMin(XMLGregorianCalendar value) {
        this.dataIngressoRispostaMin = value;
    }

    /**
     * Gets the value of the dataIngressoRispostaMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataIngressoRispostaMax() {
        return this.dataIngressoRispostaMax;
    }

    /**
     * Sets the value of the dataIngressoRispostaMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataIngressoRispostaMax(XMLGregorianCalendar value) {
        this.dataIngressoRispostaMax = value;
    }

    /**
     * Gets the value of the dataUscitaRispostaMin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataUscitaRispostaMin() {
        return this.dataUscitaRispostaMin;
    }

    /**
     * Sets the value of the dataUscitaRispostaMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataUscitaRispostaMin(XMLGregorianCalendar value) {
        this.dataUscitaRispostaMin = value;
    }

    /**
     * Gets the value of the dataUscitaRispostaMax property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataUscitaRispostaMax() {
        return this.dataUscitaRispostaMax;
    }

    /**
     * Sets the value of the dataUscitaRispostaMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataUscitaRispostaMax(XMLGregorianCalendar value) {
        this.dataUscitaRispostaMax = value;
    }

    /**
     * Gets the value of the richiestaIngressoBytesMin property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRichiestaIngressoBytesMin() {
        return this.richiestaIngressoBytesMin;
    }

    /**
     * Sets the value of the richiestaIngressoBytesMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRichiestaIngressoBytesMin(BigInteger value) {
        this.richiestaIngressoBytesMin = value;
    }

    /**
     * Gets the value of the richiestaIngressoBytesMax property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRichiestaIngressoBytesMax() {
        return this.richiestaIngressoBytesMax;
    }

    /**
     * Sets the value of the richiestaIngressoBytesMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRichiestaIngressoBytesMax(BigInteger value) {
        this.richiestaIngressoBytesMax = value;
    }

    /**
     * Gets the value of the richiestaUscitaBytesMin property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRichiestaUscitaBytesMin() {
        return this.richiestaUscitaBytesMin;
    }

    /**
     * Sets the value of the richiestaUscitaBytesMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRichiestaUscitaBytesMin(BigInteger value) {
        this.richiestaUscitaBytesMin = value;
    }

    /**
     * Gets the value of the richiestaUscitaBytesMax property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRichiestaUscitaBytesMax() {
        return this.richiestaUscitaBytesMax;
    }

    /**
     * Sets the value of the richiestaUscitaBytesMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRichiestaUscitaBytesMax(BigInteger value) {
        this.richiestaUscitaBytesMax = value;
    }

    /**
     * Gets the value of the rispostaIngressoBytesMin property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRispostaIngressoBytesMin() {
        return this.rispostaIngressoBytesMin;
    }

    /**
     * Sets the value of the rispostaIngressoBytesMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRispostaIngressoBytesMin(BigInteger value) {
        this.rispostaIngressoBytesMin = value;
    }

    /**
     * Gets the value of the rispostaIngressoBytesMax property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRispostaIngressoBytesMax() {
        return this.rispostaIngressoBytesMax;
    }

    /**
     * Sets the value of the rispostaIngressoBytesMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRispostaIngressoBytesMax(BigInteger value) {
        this.rispostaIngressoBytesMax = value;
    }

    /**
     * Gets the value of the rispostaUscitaBytesMin property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRispostaUscitaBytesMin() {
        return this.rispostaUscitaBytesMin;
    }

    /**
     * Sets the value of the rispostaUscitaBytesMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRispostaUscitaBytesMin(BigInteger value) {
        this.rispostaUscitaBytesMin = value;
    }

    /**
     * Gets the value of the rispostaUscitaBytesMax property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRispostaUscitaBytesMax() {
        return this.rispostaUscitaBytesMax;
    }

    /**
     * Sets the value of the rispostaUscitaBytesMax property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRispostaUscitaBytesMax(BigInteger value) {
        this.rispostaUscitaBytesMax = value;
    }

    /**
     * Gets the value of the pddCodice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPddCodice() {
        return this.pddCodice;
    }

    /**
     * Sets the value of the pddCodice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPddCodice(String value) {
        this.pddCodice = value;
    }

    /**
     * Gets the value of the pddTipoSoggetto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPddTipoSoggetto() {
        return this.pddTipoSoggetto;
    }

    /**
     * Sets the value of the pddTipoSoggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPddTipoSoggetto(String value) {
        this.pddTipoSoggetto = value;
    }

    /**
     * Gets the value of the pddNomeSoggetto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPddNomeSoggetto() {
        return this.pddNomeSoggetto;
    }

    /**
     * Sets the value of the pddNomeSoggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPddNomeSoggetto(String value) {
        this.pddNomeSoggetto = value;
    }

    /**
     * Gets the value of the pddRuolo property.
     * 
     * @return
     *     possible object is
     *     {@link PddRuolo }
     *     
     */
    public PddRuolo getPddRuolo() {
        return this.pddRuolo;
    }

    /**
     * Sets the value of the pddRuolo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PddRuolo }
     *     
     */
    public void setPddRuolo(PddRuolo value) {
        this.pddRuolo = value;
    }

    /**
     * Gets the value of the tipoSoggettoFruitore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoSoggettoFruitore() {
        return this.tipoSoggettoFruitore;
    }

    /**
     * Sets the value of the tipoSoggettoFruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoSoggettoFruitore(String value) {
        this.tipoSoggettoFruitore = value;
    }

    /**
     * Gets the value of the nomeSoggettoFruitore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeSoggettoFruitore() {
        return this.nomeSoggettoFruitore;
    }

    /**
     * Sets the value of the nomeSoggettoFruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeSoggettoFruitore(String value) {
        this.nomeSoggettoFruitore = value;
    }

    /**
     * Gets the value of the idportaSoggettoFruitore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdportaSoggettoFruitore() {
        return this.idportaSoggettoFruitore;
    }

    /**
     * Sets the value of the idportaSoggettoFruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdportaSoggettoFruitore(String value) {
        this.idportaSoggettoFruitore = value;
    }

    /**
     * Gets the value of the indirizzoSoggettoFruitore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzoSoggettoFruitore() {
        return this.indirizzoSoggettoFruitore;
    }

    /**
     * Sets the value of the indirizzoSoggettoFruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzoSoggettoFruitore(String value) {
        this.indirizzoSoggettoFruitore = value;
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
     * Gets the value of the idportaSoggettoErogatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdportaSoggettoErogatore() {
        return this.idportaSoggettoErogatore;
    }

    /**
     * Sets the value of the idportaSoggettoErogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdportaSoggettoErogatore(String value) {
        this.idportaSoggettoErogatore = value;
    }

    /**
     * Gets the value of the indirizzoSoggettoErogatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzoSoggettoErogatore() {
        return this.indirizzoSoggettoErogatore;
    }

    /**
     * Sets the value of the indirizzoSoggettoErogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzoSoggettoErogatore(String value) {
        this.indirizzoSoggettoErogatore = value;
    }

    /**
     * Gets the value of the idMessaggioRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMessaggioRichiesta() {
        return this.idMessaggioRichiesta;
    }

    /**
     * Sets the value of the idMessaggioRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMessaggioRichiesta(String value) {
        this.idMessaggioRichiesta = value;
    }

    /**
     * Gets the value of the idMessaggioRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMessaggioRisposta() {
        return this.idMessaggioRisposta;
    }

    /**
     * Sets the value of the idMessaggioRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMessaggioRisposta(String value) {
        this.idMessaggioRisposta = value;
    }

    /**
     * Gets the value of the profiloCollaborazioneOp2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfiloCollaborazioneOp2() {
        return this.profiloCollaborazioneOp2;
    }

    /**
     * Sets the value of the profiloCollaborazioneOp2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfiloCollaborazioneOp2(String value) {
        this.profiloCollaborazioneOp2 = value;
    }

    /**
     * Gets the value of the profiloCollaborazioneProt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfiloCollaborazioneProt() {
        return this.profiloCollaborazioneProt;
    }

    /**
     * Sets the value of the profiloCollaborazioneProt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfiloCollaborazioneProt(String value) {
        this.profiloCollaborazioneProt = value;
    }

    /**
     * Gets the value of the idCollaborazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCollaborazione() {
        return this.idCollaborazione;
    }

    /**
     * Sets the value of the idCollaborazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCollaborazione(String value) {
        this.idCollaborazione = value;
    }

    /**
     * Gets the value of the uriAccordoServizio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUriAccordoServizio() {
        return this.uriAccordoServizio;
    }

    /**
     * Sets the value of the uriAccordoServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUriAccordoServizio(String value) {
        this.uriAccordoServizio = value;
    }

    /**
     * Gets the value of the tipoServizio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoServizio() {
        return this.tipoServizio;
    }

    /**
     * Sets the value of the tipoServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoServizio(String value) {
        this.tipoServizio = value;
    }

    /**
     * Gets the value of the nomeServizio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeServizio() {
        return this.nomeServizio;
    }

    /**
     * Sets the value of the nomeServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeServizio(String value) {
        this.nomeServizio = value;
    }

    /**
     * Gets the value of the versioneServizio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVersioneServizio() {
        return this.versioneServizio;
    }

    /**
     * Sets the value of the versioneServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVersioneServizio(Integer value) {
        this.versioneServizio = value;
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
     * Gets the value of the idAsincrono property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdAsincrono() {
        return this.idAsincrono;
    }

    /**
     * Sets the value of the idAsincrono property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdAsincrono(String value) {
        this.idAsincrono = value;
    }

    /**
     * Gets the value of the tipoServizioCorrelato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoServizioCorrelato() {
        return this.tipoServizioCorrelato;
    }

    /**
     * Sets the value of the tipoServizioCorrelato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoServizioCorrelato(String value) {
        this.tipoServizioCorrelato = value;
    }

    /**
     * Gets the value of the nomeServizioCorrelato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeServizioCorrelato() {
        return this.nomeServizioCorrelato;
    }

    /**
     * Sets the value of the nomeServizioCorrelato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeServizioCorrelato(String value) {
        this.nomeServizioCorrelato = value;
    }

    /**
     * Gets the value of the idCorrelazioneApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCorrelazioneApplicativa() {
        return this.idCorrelazioneApplicativa;
    }

    /**
     * Sets the value of the idCorrelazioneApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCorrelazioneApplicativa(String value) {
        this.idCorrelazioneApplicativa = value;
    }

    /**
     * Gets the value of the idCorrelazioneApplicativaRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCorrelazioneApplicativaRisposta() {
        return this.idCorrelazioneApplicativaRisposta;
    }

    /**
     * Sets the value of the idCorrelazioneApplicativaRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCorrelazioneApplicativaRisposta(String value) {
        this.idCorrelazioneApplicativaRisposta = value;
    }

    /**
     * Gets the value of the servizioApplicativoFruitore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServizioApplicativoFruitore() {
        return this.servizioApplicativoFruitore;
    }

    /**
     * Sets the value of the servizioApplicativoFruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServizioApplicativoFruitore(String value) {
        this.servizioApplicativoFruitore = value;
    }

    /**
     * Gets the value of the servizioApplicativoErogatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServizioApplicativoErogatore() {
        return this.servizioApplicativoErogatore;
    }

    /**
     * Sets the value of the servizioApplicativoErogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServizioApplicativoErogatore(String value) {
        this.servizioApplicativoErogatore = value;
    }

    /**
     * Gets the value of the operazioneIm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperazioneIm() {
        return this.operazioneIm;
    }

    /**
     * Sets the value of the operazioneIm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperazioneIm(String value) {
        this.operazioneIm = value;
    }

    /**
     * Gets the value of the locationRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationRichiesta() {
        return this.locationRichiesta;
    }

    /**
     * Sets the value of the locationRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationRichiesta(String value) {
        this.locationRichiesta = value;
    }

    /**
     * Gets the value of the locationRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationRisposta() {
        return this.locationRisposta;
    }

    /**
     * Sets the value of the locationRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationRisposta(String value) {
        this.locationRisposta = value;
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
     * Gets the value of the credenziali property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCredenziali() {
        return this.credenziali;
    }

    /**
     * Sets the value of the credenziali property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCredenziali(String value) {
        this.credenziali = value;
    }

    /**
     * Gets the value of the locationConnettore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationConnettore() {
        return this.locationConnettore;
    }

    /**
     * Sets the value of the locationConnettore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationConnettore(String value) {
        this.locationConnettore = value;
    }

    /**
     * Gets the value of the urlInvocazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlInvocazione() {
        return this.urlInvocazione;
    }

    /**
     * Sets the value of the urlInvocazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlInvocazione(String value) {
        this.urlInvocazione = value;
    }

    /**
     * Gets the value of the clusterId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClusterId() {
        return this.clusterId;
    }

    /**
     * Sets the value of the clusterId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClusterId(String value) {
        this.clusterId = value;
    }

    /**
     * Gets the value of the socketClientAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocketClientAddress() {
        return this.socketClientAddress;
    }

    /**
     * Sets the value of the socketClientAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocketClientAddress(String value) {
        this.socketClientAddress = value;
    }

    /**
     * Gets the value of the transportClientAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportClientAddress() {
        return this.transportClientAddress;
    }

    /**
     * Sets the value of the transportClientAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportClientAddress(String value) {
        this.transportClientAddress = value;
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
