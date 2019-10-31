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
package org.openspcoop2.core.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for transazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transazione">
 * 		&lt;sequence>
 * 			&lt;element name="id-transazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="ruolo-transazione" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="esito" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="consegne-multiple-in-corso" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="esito-contesto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tipo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="codice-risposta-ingresso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="codice-risposta-uscita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-accettazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-ingresso-richiesta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-uscita-richiesta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-accettazione-risposta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-ingresso-risposta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-uscita-risposta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="richiesta-ingresso-bytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="richiesta-uscita-bytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="risposta-ingresso-bytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="risposta-uscita-bytes" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="pdd-codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="pdd-tipo-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="pdd-nome-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="pdd-ruolo" type="{http://www.openspcoop2.org/core/transazioni}pdd-ruolo" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="fault-integrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="formato-fault-integrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="fault-cooperazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="formato-fault-cooperazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="idporta-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="indirizzo-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="idporta-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="indirizzo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-messaggio-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-messaggio-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-id-msg-richiesta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="data-id-msg-risposta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="profilo-collaborazione-op2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="profilo-collaborazione-prot" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="uri-accordo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-asincrono" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-servizio-correlato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-servizio-correlato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="header-protocollo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="digest-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocollo-ext-info-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="header-protocollo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="digest-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocollo-ext-info-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="traccia-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="traccia-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="diagnostici" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="diagnostici-list1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="diagnostici-list2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="diagnostici-list-ext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="diagnostici-ext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-correlazione-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="id-correlazione-applicativa-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="operazione-im" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="location-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="location-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="credenziali" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="location-connettore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="url-invocazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="trasporto-mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="token-issuer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="token-client-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="token-subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="token-username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="token-mail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="token-info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tempi-elaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="duplicati-richiesta" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" default="0"/>
 * 			&lt;element name="duplicati-risposta" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1" default="0"/>
 * 			&lt;element name="cluster-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="socket-client-address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="transport-client-address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="client-address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="eventi-gestione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo-api" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="gruppi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="dump-messaggio" type="{http://www.openspcoop2.org/core/transazioni}dump-messaggio" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="transazione-applicativo-server" type="{http://www.openspcoop2.org/core/transazioni}transazione-applicativo-server" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="transazione-extended-info" type="{http://www.openspcoop2.org/core/transazioni}transazione-extended-info" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transazione", 
  propOrder = {
  	"idTransazione",
  	"stato",
  	"ruoloTransazione",
  	"esito",
  	"consegneMultipleInCorso",
  	"esitoContesto",
  	"protocollo",
  	"tipoRichiesta",
  	"codiceRispostaIngresso",
  	"codiceRispostaUscita",
  	"dataAccettazioneRichiesta",
  	"dataIngressoRichiesta",
  	"dataUscitaRichiesta",
  	"dataAccettazioneRisposta",
  	"dataIngressoRisposta",
  	"dataUscitaRisposta",
  	"richiestaIngressoBytes",
  	"richiestaUscitaBytes",
  	"rispostaIngressoBytes",
  	"rispostaUscitaBytes",
  	"pddCodice",
  	"pddTipoSoggetto",
  	"pddNomeSoggetto",
  	"pddRuolo",
  	"faultIntegrazione",
  	"formatoFaultIntegrazione",
  	"faultCooperazione",
  	"formatoFaultCooperazione",
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
  	"dataIdMsgRichiesta",
  	"dataIdMsgRisposta",
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
  	"headerProtocolloRichiesta",
  	"digestRichiesta",
  	"protocolloExtInfoRichiesta",
  	"headerProtocolloRisposta",
  	"digestRisposta",
  	"protocolloExtInfoRisposta",
  	"tracciaRichiesta",
  	"tracciaRisposta",
  	"diagnostici",
  	"diagnosticiList1",
  	"diagnosticiList2",
  	"diagnosticiListExt",
  	"diagnosticiExt",
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
  	"trasportoMittente",
  	"tokenIssuer",
  	"tokenClientId",
  	"tokenSubject",
  	"tokenUsername",
  	"tokenMail",
  	"tokenInfo",
  	"tempiElaborazione",
  	"duplicatiRichiesta",
  	"duplicatiRisposta",
  	"clusterId",
  	"socketClientAddress",
  	"transportClientAddress",
  	"clientAddress",
  	"eventiGestione",
  	"tipoApi",
  	"gruppi",
  	"dumpMessaggio",
  	"transazioneApplicativoServer",
  	"transazioneExtendedInfo"
  }
)

@XmlRootElement(name = "transazione")

public class Transazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Transazione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.lang.String getIdTransazione() {
    return this.idTransazione;
  }

  public void setIdTransazione(java.lang.String idTransazione) {
    this.idTransazione = idTransazione;
  }

  public java.lang.String getStato() {
    return this.stato;
  }

  public void setStato(java.lang.String stato) {
    this.stato = stato;
  }

  public int getRuoloTransazione() {
    return this.ruoloTransazione;
  }

  public void setRuoloTransazione(int ruoloTransazione) {
    this.ruoloTransazione = ruoloTransazione;
  }

  public int getEsito() {
    return this.esito;
  }

  public void setEsito(int esito) {
    this.esito = esito;
  }

  public int getConsegneMultipleInCorso() {
    return this.consegneMultipleInCorso;
  }

  public void setConsegneMultipleInCorso(int consegneMultipleInCorso) {
    this.consegneMultipleInCorso = consegneMultipleInCorso;
  }

  public java.lang.String getEsitoContesto() {
    return this.esitoContesto;
  }

  public void setEsitoContesto(java.lang.String esitoContesto) {
    this.esitoContesto = esitoContesto;
  }

  public java.lang.String getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(java.lang.String protocollo) {
    this.protocollo = protocollo;
  }

  public java.lang.String getTipoRichiesta() {
    return this.tipoRichiesta;
  }

  public void setTipoRichiesta(java.lang.String tipoRichiesta) {
    this.tipoRichiesta = tipoRichiesta;
  }

  public java.lang.String getCodiceRispostaIngresso() {
    return this.codiceRispostaIngresso;
  }

  public void setCodiceRispostaIngresso(java.lang.String codiceRispostaIngresso) {
    this.codiceRispostaIngresso = codiceRispostaIngresso;
  }

  public java.lang.String getCodiceRispostaUscita() {
    return this.codiceRispostaUscita;
  }

  public void setCodiceRispostaUscita(java.lang.String codiceRispostaUscita) {
    this.codiceRispostaUscita = codiceRispostaUscita;
  }

  public java.util.Date getDataAccettazioneRichiesta() {
    return this.dataAccettazioneRichiesta;
  }

  public void setDataAccettazioneRichiesta(java.util.Date dataAccettazioneRichiesta) {
    this.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
  }

  public java.util.Date getDataIngressoRichiesta() {
    return this.dataIngressoRichiesta;
  }

  public void setDataIngressoRichiesta(java.util.Date dataIngressoRichiesta) {
    this.dataIngressoRichiesta = dataIngressoRichiesta;
  }

  public java.util.Date getDataUscitaRichiesta() {
    return this.dataUscitaRichiesta;
  }

  public void setDataUscitaRichiesta(java.util.Date dataUscitaRichiesta) {
    this.dataUscitaRichiesta = dataUscitaRichiesta;
  }

  public java.util.Date getDataAccettazioneRisposta() {
    return this.dataAccettazioneRisposta;
  }

  public void setDataAccettazioneRisposta(java.util.Date dataAccettazioneRisposta) {
    this.dataAccettazioneRisposta = dataAccettazioneRisposta;
  }

  public java.util.Date getDataIngressoRisposta() {
    return this.dataIngressoRisposta;
  }

  public void setDataIngressoRisposta(java.util.Date dataIngressoRisposta) {
    this.dataIngressoRisposta = dataIngressoRisposta;
  }

  public java.util.Date getDataUscitaRisposta() {
    return this.dataUscitaRisposta;
  }

  public void setDataUscitaRisposta(java.util.Date dataUscitaRisposta) {
    this.dataUscitaRisposta = dataUscitaRisposta;
  }

  public java.lang.Long getRichiestaIngressoBytes() {
    return this.richiestaIngressoBytes;
  }

  public void setRichiestaIngressoBytes(java.lang.Long richiestaIngressoBytes) {
    this.richiestaIngressoBytes = richiestaIngressoBytes;
  }

  public java.lang.Long getRichiestaUscitaBytes() {
    return this.richiestaUscitaBytes;
  }

  public void setRichiestaUscitaBytes(java.lang.Long richiestaUscitaBytes) {
    this.richiestaUscitaBytes = richiestaUscitaBytes;
  }

  public java.lang.Long getRispostaIngressoBytes() {
    return this.rispostaIngressoBytes;
  }

  public void setRispostaIngressoBytes(java.lang.Long rispostaIngressoBytes) {
    this.rispostaIngressoBytes = rispostaIngressoBytes;
  }

  public java.lang.Long getRispostaUscitaBytes() {
    return this.rispostaUscitaBytes;
  }

  public void setRispostaUscitaBytes(java.lang.Long rispostaUscitaBytes) {
    this.rispostaUscitaBytes = rispostaUscitaBytes;
  }

  public java.lang.String getPddCodice() {
    return this.pddCodice;
  }

  public void setPddCodice(java.lang.String pddCodice) {
    this.pddCodice = pddCodice;
  }

  public java.lang.String getPddTipoSoggetto() {
    return this.pddTipoSoggetto;
  }

  public void setPddTipoSoggetto(java.lang.String pddTipoSoggetto) {
    this.pddTipoSoggetto = pddTipoSoggetto;
  }

  public java.lang.String getPddNomeSoggetto() {
    return this.pddNomeSoggetto;
  }

  public void setPddNomeSoggetto(java.lang.String pddNomeSoggetto) {
    this.pddNomeSoggetto = pddNomeSoggetto;
  }

  public void set_value_pddRuolo(String value) {
    this.pddRuolo = (PddRuolo) PddRuolo.toEnumConstantFromString(value);
  }

  public String get_value_pddRuolo() {
    if(this.pddRuolo == null){
    	return null;
    }else{
    	return this.pddRuolo.toString();
    }
  }

  public org.openspcoop2.core.transazioni.constants.PddRuolo getPddRuolo() {
    return this.pddRuolo;
  }

  public void setPddRuolo(org.openspcoop2.core.transazioni.constants.PddRuolo pddRuolo) {
    this.pddRuolo = pddRuolo;
  }

  public java.lang.String getFaultIntegrazione() {
    return this.faultIntegrazione;
  }

  public void setFaultIntegrazione(java.lang.String faultIntegrazione) {
    this.faultIntegrazione = faultIntegrazione;
  }

  public java.lang.String getFormatoFaultIntegrazione() {
    return this.formatoFaultIntegrazione;
  }

  public void setFormatoFaultIntegrazione(java.lang.String formatoFaultIntegrazione) {
    this.formatoFaultIntegrazione = formatoFaultIntegrazione;
  }

  public java.lang.String getFaultCooperazione() {
    return this.faultCooperazione;
  }

  public void setFaultCooperazione(java.lang.String faultCooperazione) {
    this.faultCooperazione = faultCooperazione;
  }

  public java.lang.String getFormatoFaultCooperazione() {
    return this.formatoFaultCooperazione;
  }

  public void setFormatoFaultCooperazione(java.lang.String formatoFaultCooperazione) {
    this.formatoFaultCooperazione = formatoFaultCooperazione;
  }

  public java.lang.String getTipoSoggettoFruitore() {
    return this.tipoSoggettoFruitore;
  }

  public void setTipoSoggettoFruitore(java.lang.String tipoSoggettoFruitore) {
    this.tipoSoggettoFruitore = tipoSoggettoFruitore;
  }

  public java.lang.String getNomeSoggettoFruitore() {
    return this.nomeSoggettoFruitore;
  }

  public void setNomeSoggettoFruitore(java.lang.String nomeSoggettoFruitore) {
    this.nomeSoggettoFruitore = nomeSoggettoFruitore;
  }

  public java.lang.String getIdportaSoggettoFruitore() {
    return this.idportaSoggettoFruitore;
  }

  public void setIdportaSoggettoFruitore(java.lang.String idportaSoggettoFruitore) {
    this.idportaSoggettoFruitore = idportaSoggettoFruitore;
  }

  public java.lang.String getIndirizzoSoggettoFruitore() {
    return this.indirizzoSoggettoFruitore;
  }

  public void setIndirizzoSoggettoFruitore(java.lang.String indirizzoSoggettoFruitore) {
    this.indirizzoSoggettoFruitore = indirizzoSoggettoFruitore;
  }

  public java.lang.String getTipoSoggettoErogatore() {
    return this.tipoSoggettoErogatore;
  }

  public void setTipoSoggettoErogatore(java.lang.String tipoSoggettoErogatore) {
    this.tipoSoggettoErogatore = tipoSoggettoErogatore;
  }

  public java.lang.String getNomeSoggettoErogatore() {
    return this.nomeSoggettoErogatore;
  }

  public void setNomeSoggettoErogatore(java.lang.String nomeSoggettoErogatore) {
    this.nomeSoggettoErogatore = nomeSoggettoErogatore;
  }

  public java.lang.String getIdportaSoggettoErogatore() {
    return this.idportaSoggettoErogatore;
  }

  public void setIdportaSoggettoErogatore(java.lang.String idportaSoggettoErogatore) {
    this.idportaSoggettoErogatore = idportaSoggettoErogatore;
  }

  public java.lang.String getIndirizzoSoggettoErogatore() {
    return this.indirizzoSoggettoErogatore;
  }

  public void setIndirizzoSoggettoErogatore(java.lang.String indirizzoSoggettoErogatore) {
    this.indirizzoSoggettoErogatore = indirizzoSoggettoErogatore;
  }

  public java.lang.String getIdMessaggioRichiesta() {
    return this.idMessaggioRichiesta;
  }

  public void setIdMessaggioRichiesta(java.lang.String idMessaggioRichiesta) {
    this.idMessaggioRichiesta = idMessaggioRichiesta;
  }

  public java.lang.String getIdMessaggioRisposta() {
    return this.idMessaggioRisposta;
  }

  public void setIdMessaggioRisposta(java.lang.String idMessaggioRisposta) {
    this.idMessaggioRisposta = idMessaggioRisposta;
  }

  public java.util.Date getDataIdMsgRichiesta() {
    return this.dataIdMsgRichiesta;
  }

  public void setDataIdMsgRichiesta(java.util.Date dataIdMsgRichiesta) {
    this.dataIdMsgRichiesta = dataIdMsgRichiesta;
  }

  public java.util.Date getDataIdMsgRisposta() {
    return this.dataIdMsgRisposta;
  }

  public void setDataIdMsgRisposta(java.util.Date dataIdMsgRisposta) {
    this.dataIdMsgRisposta = dataIdMsgRisposta;
  }

  public java.lang.String getProfiloCollaborazioneOp2() {
    return this.profiloCollaborazioneOp2;
  }

  public void setProfiloCollaborazioneOp2(java.lang.String profiloCollaborazioneOp2) {
    this.profiloCollaborazioneOp2 = profiloCollaborazioneOp2;
  }

  public java.lang.String getProfiloCollaborazioneProt() {
    return this.profiloCollaborazioneProt;
  }

  public void setProfiloCollaborazioneProt(java.lang.String profiloCollaborazioneProt) {
    this.profiloCollaborazioneProt = profiloCollaborazioneProt;
  }

  public java.lang.String getIdCollaborazione() {
    return this.idCollaborazione;
  }

  public void setIdCollaborazione(java.lang.String idCollaborazione) {
    this.idCollaborazione = idCollaborazione;
  }

  public java.lang.String getUriAccordoServizio() {
    return this.uriAccordoServizio;
  }

  public void setUriAccordoServizio(java.lang.String uriAccordoServizio) {
    this.uriAccordoServizio = uriAccordoServizio;
  }

  public java.lang.String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(java.lang.String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public java.lang.String getNomeServizio() {
    return this.nomeServizio;
  }

  public void setNomeServizio(java.lang.String nomeServizio) {
    this.nomeServizio = nomeServizio;
  }

  public int getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(int versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public java.lang.String getIdAsincrono() {
    return this.idAsincrono;
  }

  public void setIdAsincrono(java.lang.String idAsincrono) {
    this.idAsincrono = idAsincrono;
  }

  public java.lang.String getTipoServizioCorrelato() {
    return this.tipoServizioCorrelato;
  }

  public void setTipoServizioCorrelato(java.lang.String tipoServizioCorrelato) {
    this.tipoServizioCorrelato = tipoServizioCorrelato;
  }

  public java.lang.String getNomeServizioCorrelato() {
    return this.nomeServizioCorrelato;
  }

  public void setNomeServizioCorrelato(java.lang.String nomeServizioCorrelato) {
    this.nomeServizioCorrelato = nomeServizioCorrelato;
  }

  public java.lang.String getHeaderProtocolloRichiesta() {
    return this.headerProtocolloRichiesta;
  }

  public void setHeaderProtocolloRichiesta(java.lang.String headerProtocolloRichiesta) {
    this.headerProtocolloRichiesta = headerProtocolloRichiesta;
  }

  public java.lang.String getDigestRichiesta() {
    return this.digestRichiesta;
  }

  public void setDigestRichiesta(java.lang.String digestRichiesta) {
    this.digestRichiesta = digestRichiesta;
  }

  public java.lang.String getProtocolloExtInfoRichiesta() {
    return this.protocolloExtInfoRichiesta;
  }

  public void setProtocolloExtInfoRichiesta(java.lang.String protocolloExtInfoRichiesta) {
    this.protocolloExtInfoRichiesta = protocolloExtInfoRichiesta;
  }

  public java.lang.String getHeaderProtocolloRisposta() {
    return this.headerProtocolloRisposta;
  }

  public void setHeaderProtocolloRisposta(java.lang.String headerProtocolloRisposta) {
    this.headerProtocolloRisposta = headerProtocolloRisposta;
  }

  public java.lang.String getDigestRisposta() {
    return this.digestRisposta;
  }

  public void setDigestRisposta(java.lang.String digestRisposta) {
    this.digestRisposta = digestRisposta;
  }

  public java.lang.String getProtocolloExtInfoRisposta() {
    return this.protocolloExtInfoRisposta;
  }

  public void setProtocolloExtInfoRisposta(java.lang.String protocolloExtInfoRisposta) {
    this.protocolloExtInfoRisposta = protocolloExtInfoRisposta;
  }

  public java.lang.String getTracciaRichiesta() {
    return this.tracciaRichiesta;
  }

  public void setTracciaRichiesta(java.lang.String tracciaRichiesta) {
    this.tracciaRichiesta = tracciaRichiesta;
  }

  public java.lang.String getTracciaRisposta() {
    return this.tracciaRisposta;
  }

  public void setTracciaRisposta(java.lang.String tracciaRisposta) {
    this.tracciaRisposta = tracciaRisposta;
  }

  public java.lang.String getDiagnostici() {
    return this.diagnostici;
  }

  public void setDiagnostici(java.lang.String diagnostici) {
    this.diagnostici = diagnostici;
  }

  public java.lang.String getDiagnosticiList1() {
    return this.diagnosticiList1;
  }

  public void setDiagnosticiList1(java.lang.String diagnosticiList1) {
    this.diagnosticiList1 = diagnosticiList1;
  }

  public java.lang.String getDiagnosticiList2() {
    return this.diagnosticiList2;
  }

  public void setDiagnosticiList2(java.lang.String diagnosticiList2) {
    this.diagnosticiList2 = diagnosticiList2;
  }

  public java.lang.String getDiagnosticiListExt() {
    return this.diagnosticiListExt;
  }

  public void setDiagnosticiListExt(java.lang.String diagnosticiListExt) {
    this.diagnosticiListExt = diagnosticiListExt;
  }

  public java.lang.String getDiagnosticiExt() {
    return this.diagnosticiExt;
  }

  public void setDiagnosticiExt(java.lang.String diagnosticiExt) {
    this.diagnosticiExt = diagnosticiExt;
  }

  public java.lang.String getIdCorrelazioneApplicativa() {
    return this.idCorrelazioneApplicativa;
  }

  public void setIdCorrelazioneApplicativa(java.lang.String idCorrelazioneApplicativa) {
    this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
  }

  public java.lang.String getIdCorrelazioneApplicativaRisposta() {
    return this.idCorrelazioneApplicativaRisposta;
  }

  public void setIdCorrelazioneApplicativaRisposta(java.lang.String idCorrelazioneApplicativaRisposta) {
    this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
  }

  public java.lang.String getServizioApplicativoFruitore() {
    return this.servizioApplicativoFruitore;
  }

  public void setServizioApplicativoFruitore(java.lang.String servizioApplicativoFruitore) {
    this.servizioApplicativoFruitore = servizioApplicativoFruitore;
  }

  public java.lang.String getServizioApplicativoErogatore() {
    return this.servizioApplicativoErogatore;
  }

  public void setServizioApplicativoErogatore(java.lang.String servizioApplicativoErogatore) {
    this.servizioApplicativoErogatore = servizioApplicativoErogatore;
  }

  public java.lang.String getOperazioneIm() {
    return this.operazioneIm;
  }

  public void setOperazioneIm(java.lang.String operazioneIm) {
    this.operazioneIm = operazioneIm;
  }

  public java.lang.String getLocationRichiesta() {
    return this.locationRichiesta;
  }

  public void setLocationRichiesta(java.lang.String locationRichiesta) {
    this.locationRichiesta = locationRichiesta;
  }

  public java.lang.String getLocationRisposta() {
    return this.locationRisposta;
  }

  public void setLocationRisposta(java.lang.String locationRisposta) {
    this.locationRisposta = locationRisposta;
  }

  public java.lang.String getNomePorta() {
    return this.nomePorta;
  }

  public void setNomePorta(java.lang.String nomePorta) {
    this.nomePorta = nomePorta;
  }

  public java.lang.String getCredenziali() {
    return this.credenziali;
  }

  public void setCredenziali(java.lang.String credenziali) {
    this.credenziali = credenziali;
  }

  public java.lang.String getLocationConnettore() {
    return this.locationConnettore;
  }

  public void setLocationConnettore(java.lang.String locationConnettore) {
    this.locationConnettore = locationConnettore;
  }

  public java.lang.String getUrlInvocazione() {
    return this.urlInvocazione;
  }

  public void setUrlInvocazione(java.lang.String urlInvocazione) {
    this.urlInvocazione = urlInvocazione;
  }

  public java.lang.String getTrasportoMittente() {
    return this.trasportoMittente;
  }

  public void setTrasportoMittente(java.lang.String trasportoMittente) {
    this.trasportoMittente = trasportoMittente;
  }

  public java.lang.String getTokenIssuer() {
    return this.tokenIssuer;
  }

  public void setTokenIssuer(java.lang.String tokenIssuer) {
    this.tokenIssuer = tokenIssuer;
  }

  public java.lang.String getTokenClientId() {
    return this.tokenClientId;
  }

  public void setTokenClientId(java.lang.String tokenClientId) {
    this.tokenClientId = tokenClientId;
  }

  public java.lang.String getTokenSubject() {
    return this.tokenSubject;
  }

  public void setTokenSubject(java.lang.String tokenSubject) {
    this.tokenSubject = tokenSubject;
  }

  public java.lang.String getTokenUsername() {
    return this.tokenUsername;
  }

  public void setTokenUsername(java.lang.String tokenUsername) {
    this.tokenUsername = tokenUsername;
  }

  public java.lang.String getTokenMail() {
    return this.tokenMail;
  }

  public void setTokenMail(java.lang.String tokenMail) {
    this.tokenMail = tokenMail;
  }

  public java.lang.String getTokenInfo() {
    return this.tokenInfo;
  }

  public void setTokenInfo(java.lang.String tokenInfo) {
    this.tokenInfo = tokenInfo;
  }

  public java.lang.String getTempiElaborazione() {
    return this.tempiElaborazione;
  }

  public void setTempiElaborazione(java.lang.String tempiElaborazione) {
    this.tempiElaborazione = tempiElaborazione;
  }

  public int getDuplicatiRichiesta() {
    return this.duplicatiRichiesta;
  }

  public void setDuplicatiRichiesta(int duplicatiRichiesta) {
    this.duplicatiRichiesta = duplicatiRichiesta;
  }

  public int getDuplicatiRisposta() {
    return this.duplicatiRisposta;
  }

  public void setDuplicatiRisposta(int duplicatiRisposta) {
    this.duplicatiRisposta = duplicatiRisposta;
  }

  public java.lang.String getClusterId() {
    return this.clusterId;
  }

  public void setClusterId(java.lang.String clusterId) {
    this.clusterId = clusterId;
  }

  public java.lang.String getSocketClientAddress() {
    return this.socketClientAddress;
  }

  public void setSocketClientAddress(java.lang.String socketClientAddress) {
    this.socketClientAddress = socketClientAddress;
  }

  public java.lang.String getTransportClientAddress() {
    return this.transportClientAddress;
  }

  public void setTransportClientAddress(java.lang.String transportClientAddress) {
    this.transportClientAddress = transportClientAddress;
  }

  public java.lang.String getClientAddress() {
    return this.clientAddress;
  }

  public void setClientAddress(java.lang.String clientAddress) {
    this.clientAddress = clientAddress;
  }

  public java.lang.String getEventiGestione() {
    return this.eventiGestione;
  }

  public void setEventiGestione(java.lang.String eventiGestione) {
    this.eventiGestione = eventiGestione;
  }

  public int getTipoApi() {
    return this.tipoApi;
  }

  public void setTipoApi(int tipoApi) {
    this.tipoApi = tipoApi;
  }

  public java.lang.String getGruppi() {
    return this.gruppi;
  }

  public void setGruppi(java.lang.String gruppi) {
    this.gruppi = gruppi;
  }

  public void addDumpMessaggio(DumpMessaggio dumpMessaggio) {
    this.dumpMessaggio.add(dumpMessaggio);
  }

  public DumpMessaggio getDumpMessaggio(int index) {
    return this.dumpMessaggio.get( index );
  }

  public DumpMessaggio removeDumpMessaggio(int index) {
    return this.dumpMessaggio.remove( index );
  }

  public List<DumpMessaggio> getDumpMessaggioList() {
    return this.dumpMessaggio;
  }

  public void setDumpMessaggioList(List<DumpMessaggio> dumpMessaggio) {
    this.dumpMessaggio=dumpMessaggio;
  }

  public int sizeDumpMessaggioList() {
    return this.dumpMessaggio.size();
  }

  public void addTransazioneApplicativoServer(TransazioneApplicativoServer transazioneApplicativoServer) {
    this.transazioneApplicativoServer.add(transazioneApplicativoServer);
  }

  public TransazioneApplicativoServer getTransazioneApplicativoServer(int index) {
    return this.transazioneApplicativoServer.get( index );
  }

  public TransazioneApplicativoServer removeTransazioneApplicativoServer(int index) {
    return this.transazioneApplicativoServer.remove( index );
  }

  public List<TransazioneApplicativoServer> getTransazioneApplicativoServerList() {
    return this.transazioneApplicativoServer;
  }

  public void setTransazioneApplicativoServerList(List<TransazioneApplicativoServer> transazioneApplicativoServer) {
    this.transazioneApplicativoServer=transazioneApplicativoServer;
  }

  public int sizeTransazioneApplicativoServerList() {
    return this.transazioneApplicativoServer.size();
  }

  public void addTransazioneExtendedInfo(TransazioneExtendedInfo transazioneExtendedInfo) {
    this.transazioneExtendedInfo.add(transazioneExtendedInfo);
  }

  public TransazioneExtendedInfo getTransazioneExtendedInfo(int index) {
    return this.transazioneExtendedInfo.get( index );
  }

  public TransazioneExtendedInfo removeTransazioneExtendedInfo(int index) {
    return this.transazioneExtendedInfo.remove( index );
  }

  public List<TransazioneExtendedInfo> getTransazioneExtendedInfoList() {
    return this.transazioneExtendedInfo;
  }

  public void setTransazioneExtendedInfoList(List<TransazioneExtendedInfo> transazioneExtendedInfo) {
    this.transazioneExtendedInfo=transazioneExtendedInfo;
  }

  public int sizeTransazioneExtendedInfoList() {
    return this.transazioneExtendedInfo.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.transazioni.model.TransazioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.transazioni.Transazione.modelStaticInstance==null){
  			org.openspcoop2.core.transazioni.Transazione.modelStaticInstance = new org.openspcoop2.core.transazioni.model.TransazioneModel();
	  }
  }
  public static org.openspcoop2.core.transazioni.model.TransazioneModel model(){
	  if(org.openspcoop2.core.transazioni.Transazione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.transazioni.Transazione.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-transazione",required=true,nillable=false)
  protected java.lang.String idTransazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="stato",required=false,nillable=false)
  protected java.lang.String stato;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="ruolo-transazione",required=true,nillable=false)
  protected int ruoloTransazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="esito",required=false,nillable=false)
  protected int esito;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="consegne-multiple-in-corso",required=false,nillable=false)
  protected int consegneMultipleInCorso;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="esito-contesto",required=false,nillable=false)
  protected java.lang.String esitoContesto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo",required=true,nillable=false)
  protected java.lang.String protocollo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-richiesta",required=false,nillable=false)
  protected java.lang.String tipoRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-risposta-ingresso",required=false,nillable=false)
  protected java.lang.String codiceRispostaIngresso;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice-risposta-uscita",required=false,nillable=false)
  protected java.lang.String codiceRispostaUscita;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-richiesta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataAccettazioneRichiesta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ingresso-richiesta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataIngressoRichiesta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-uscita-richiesta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataUscitaRichiesta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-accettazione-risposta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataAccettazioneRisposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ingresso-risposta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataIngressoRisposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-uscita-risposta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataUscitaRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="richiesta-ingresso-bytes",required=false,nillable=false)
  protected java.lang.Long richiestaIngressoBytes;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="richiesta-uscita-bytes",required=false,nillable=false)
  protected java.lang.Long richiestaUscitaBytes;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="risposta-ingresso-bytes",required=false,nillable=false)
  protected java.lang.Long rispostaIngressoBytes;

  @javax.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="risposta-uscita-bytes",required=false,nillable=false)
  protected java.lang.Long rispostaUscitaBytes;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pdd-codice",required=false,nillable=false)
  protected java.lang.String pddCodice;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pdd-tipo-soggetto",required=false,nillable=false)
  protected java.lang.String pddTipoSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pdd-nome-soggetto",required=false,nillable=false)
  protected java.lang.String pddNomeSoggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_pddRuolo;

  @XmlElement(name="pdd-ruolo",required=false,nillable=false)
  protected PddRuolo pddRuolo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="fault-integrazione",required=false,nillable=false)
  protected java.lang.String faultIntegrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="formato-fault-integrazione",required=false,nillable=false)
  protected java.lang.String formatoFaultIntegrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="fault-cooperazione",required=false,nillable=false)
  protected java.lang.String faultCooperazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="formato-fault-cooperazione",required=false,nillable=false)
  protected java.lang.String formatoFaultCooperazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto-fruitore",required=false,nillable=false)
  protected java.lang.String tipoSoggettoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto-fruitore",required=false,nillable=false)
  protected java.lang.String nomeSoggettoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="idporta-soggetto-fruitore",required=false,nillable=false)
  protected java.lang.String idportaSoggettoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="indirizzo-soggetto-fruitore",required=false,nillable=false)
  protected java.lang.String indirizzoSoggettoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto-erogatore",required=false,nillable=false)
  protected java.lang.String tipoSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto-erogatore",required=false,nillable=false)
  protected java.lang.String nomeSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="idporta-soggetto-erogatore",required=false,nillable=false)
  protected java.lang.String idportaSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="indirizzo-soggetto-erogatore",required=false,nillable=false)
  protected java.lang.String indirizzoSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-messaggio-richiesta",required=false,nillable=false)
  protected java.lang.String idMessaggioRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-messaggio-risposta",required=false,nillable=false)
  protected java.lang.String idMessaggioRisposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-id-msg-richiesta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataIdMsgRichiesta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-id-msg-risposta",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataIdMsgRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="profilo-collaborazione-op2",required=false,nillable=false)
  protected java.lang.String profiloCollaborazioneOp2;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="profilo-collaborazione-prot",required=false,nillable=false)
  protected java.lang.String profiloCollaborazioneProt;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-collaborazione",required=false,nillable=false)
  protected java.lang.String idCollaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="uri-accordo-servizio",required=false,nillable=false)
  protected java.lang.String uriAccordoServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-servizio",required=false,nillable=false)
  protected java.lang.String tipoServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-servizio",required=false,nillable=false)
  protected java.lang.String nomeServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="versione-servizio",required=false,nillable=false)
  protected int versioneServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-asincrono",required=false,nillable=false)
  protected java.lang.String idAsincrono;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-servizio-correlato",required=false,nillable=false)
  protected java.lang.String tipoServizioCorrelato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-servizio-correlato",required=false,nillable=false)
  protected java.lang.String nomeServizioCorrelato;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header-protocollo-richiesta",required=false,nillable=false)
  protected java.lang.String headerProtocolloRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="digest-richiesta",required=false,nillable=false)
  protected java.lang.String digestRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo-ext-info-richiesta",required=false,nillable=false)
  protected java.lang.String protocolloExtInfoRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header-protocollo-risposta",required=false,nillable=false)
  protected java.lang.String headerProtocolloRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="digest-risposta",required=false,nillable=false)
  protected java.lang.String digestRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocollo-ext-info-risposta",required=false,nillable=false)
  protected java.lang.String protocolloExtInfoRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="traccia-richiesta",required=false,nillable=false)
  protected java.lang.String tracciaRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="traccia-risposta",required=false,nillable=false)
  protected java.lang.String tracciaRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="diagnostici",required=false,nillable=false)
  protected java.lang.String diagnostici;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="diagnostici-list1",required=false,nillable=false)
  protected java.lang.String diagnosticiList1;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="diagnostici-list2",required=false,nillable=false)
  protected java.lang.String diagnosticiList2;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="diagnostici-list-ext",required=false,nillable=false)
  protected java.lang.String diagnosticiListExt;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="diagnostici-ext",required=false,nillable=false)
  protected java.lang.String diagnosticiExt;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-correlazione-applicativa",required=false,nillable=false)
  protected java.lang.String idCorrelazioneApplicativa;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-correlazione-applicativa-risposta",required=false,nillable=false)
  protected java.lang.String idCorrelazioneApplicativaRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-fruitore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=false,nillable=false)
  protected java.lang.String servizioApplicativoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="operazione-im",required=false,nillable=false)
  protected java.lang.String operazioneIm;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="location-richiesta",required=false,nillable=false)
  protected java.lang.String locationRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="location-risposta",required=false,nillable=false)
  protected java.lang.String locationRisposta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=false,nillable=false)
  protected java.lang.String nomePorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="credenziali",required=false,nillable=false)
  protected java.lang.String credenziali;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="location-connettore",required=false,nillable=false)
  protected java.lang.String locationConnettore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="url-invocazione",required=false,nillable=false)
  protected java.lang.String urlInvocazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="trasporto-mittente",required=false,nillable=false)
  protected java.lang.String trasportoMittente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-issuer",required=false,nillable=false)
  protected java.lang.String tokenIssuer;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-client-id",required=false,nillable=false)
  protected java.lang.String tokenClientId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-subject",required=false,nillable=false)
  protected java.lang.String tokenSubject;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-username",required=false,nillable=false)
  protected java.lang.String tokenUsername;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-mail",required=false,nillable=false)
  protected java.lang.String tokenMail;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="token-info",required=false,nillable=false)
  protected java.lang.String tokenInfo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tempi-elaborazione",required=false,nillable=false)
  protected java.lang.String tempiElaborazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="duplicati-richiesta",required=false,nillable=false,defaultValue="0")
  protected int duplicatiRichiesta = 0;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="duplicati-risposta",required=false,nillable=false,defaultValue="0")
  protected int duplicatiRisposta = 0;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cluster-id",required=false,nillable=false)
  protected java.lang.String clusterId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="socket-client-address",required=false,nillable=false)
  protected java.lang.String socketClientAddress;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="transport-client-address",required=false,nillable=false)
  protected java.lang.String transportClientAddress;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="client-address",required=false,nillable=false)
  protected java.lang.String clientAddress;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="eventi-gestione",required=false,nillable=false)
  protected java.lang.String eventiGestione;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="tipo-api",required=false,nillable=false)
  protected int tipoApi;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="gruppi",required=false,nillable=false)
  protected java.lang.String gruppi;

  @XmlElement(name="dump-messaggio",required=true,nillable=false)
  protected List<DumpMessaggio> dumpMessaggio = new ArrayList<DumpMessaggio>();

  /**
   * @deprecated Use method getDumpMessaggioList
   * @return List<DumpMessaggio>
  */
  @Deprecated
  public List<DumpMessaggio> getDumpMessaggio() {
  	return this.dumpMessaggio;
  }

  /**
   * @deprecated Use method setDumpMessaggioList
   * @param dumpMessaggio List<DumpMessaggio>
  */
  @Deprecated
  public void setDumpMessaggio(List<DumpMessaggio> dumpMessaggio) {
  	this.dumpMessaggio=dumpMessaggio;
  }

  /**
   * @deprecated Use method sizeDumpMessaggioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDumpMessaggio() {
  	return this.dumpMessaggio.size();
  }

  @XmlElement(name="transazione-applicativo-server",required=true,nillable=false)
  protected List<TransazioneApplicativoServer> transazioneApplicativoServer = new ArrayList<TransazioneApplicativoServer>();

  /**
   * @deprecated Use method getTransazioneApplicativoServerList
   * @return List<TransazioneApplicativoServer>
  */
  @Deprecated
  public List<TransazioneApplicativoServer> getTransazioneApplicativoServer() {
  	return this.transazioneApplicativoServer;
  }

  /**
   * @deprecated Use method setTransazioneApplicativoServerList
   * @param transazioneApplicativoServer List<TransazioneApplicativoServer>
  */
  @Deprecated
  public void setTransazioneApplicativoServer(List<TransazioneApplicativoServer> transazioneApplicativoServer) {
  	this.transazioneApplicativoServer=transazioneApplicativoServer;
  }

  /**
   * @deprecated Use method sizeTransazioneApplicativoServerList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeTransazioneApplicativoServer() {
  	return this.transazioneApplicativoServer.size();
  }

  @XmlElement(name="transazione-extended-info",required=true,nillable=false)
  protected List<TransazioneExtendedInfo> transazioneExtendedInfo = new ArrayList<TransazioneExtendedInfo>();

  /**
   * @deprecated Use method getTransazioneExtendedInfoList
   * @return List<TransazioneExtendedInfo>
  */
  @Deprecated
  public List<TransazioneExtendedInfo> getTransazioneExtendedInfo() {
  	return this.transazioneExtendedInfo;
  }

  /**
   * @deprecated Use method setTransazioneExtendedInfoList
   * @param transazioneExtendedInfo List<TransazioneExtendedInfo>
  */
  @Deprecated
  public void setTransazioneExtendedInfo(List<TransazioneExtendedInfo> transazioneExtendedInfo) {
  	this.transazioneExtendedInfo=transazioneExtendedInfo;
  }

  /**
   * @deprecated Use method sizeTransazioneExtendedInfoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeTransazioneExtendedInfo() {
  	return this.transazioneExtendedInfo.size();
  }

}
