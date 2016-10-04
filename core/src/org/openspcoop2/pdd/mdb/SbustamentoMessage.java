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



package org.openspcoop2.pdd.mdb;

import java.util.Vector;

import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link SbustamentoMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SbustamentoMessage implements GenericMessage {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore della Porta Applicativa Richiesta */
	private RichiestaApplicativa richiestaApplicativa;
	/** Busta di richiesta. */
	private Busta busta;
	/** Eventuali errori riscontrati durante la validazione della Busta ricevuta. 
        Se non sono presenti errori, questa variabile conterra' un vector vuoto (size = 0) */
	private Vector<Eccezione> errors;
	/** Indicazione se la busta associata a questo messaggio e' un messaggio od un messaggioErrore */
	private boolean isMessaggioErroreProtocollo;
	/** Indicazione se la busta associata a questo messaggio e' un messaggio senza carico applicativo */
	private boolean isBustaDiServizio;
	/** Indicazione se e' abilitato il filtro duplicati */
	private boolean filtroDuplicatiRichiestoAccordo;
	/** Indicazione se e' abilitato la conferma ricezione */
	private boolean confermaRicezioneRichiestoAccordo;
	/** Indicazione se e' abilitato la consegna in ordine */
	private boolean consegnaOrdineRichiestoAccordo;
	/** ServizioCorrelato necessario per la richiesta Asincrona Asimmetrica */
	private String servizioCorrelato;
	/** Tipo ServizioCorrelato necessario per la richiesta Asincrona Asimmetrica*/
	private String tipoServizioCorrelato;
	/** RuoloBusta */
	private RuoloBusta ruoloBustaRicevuta = null;

	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11;
	/** Indicazione se la porta delegata/applicativa richiesta una modalita stateless (oneway o sincrono) 
	 *  Il caso oneway11 possiedera questo booleano con il valore false.
	 * */
	private boolean stateless;

	/** Tipologia di porta di domino del soggetto mittente */
	private String implementazionePdDSoggettoMittente;
	/** Tipologia di porta di domino del soggetto destinatario */
	private String implementazionePdDSoggettoDestinatario;
	
	/** PdDContext */
	private PdDContext pddContext;
	
	/** DettaglioEccezione */
	private DettaglioEccezione dettaglioEccezione;
	

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */    
	public SbustamentoMessage(){
	}





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'identificatore della porta applicativa richiesta.
	 *
	 * @param idpa Identificatore della porta applicativa.
	 * 
	 */
	public void setRichiestaApplicativa(RichiestaApplicativa idpa){
		this.richiestaApplicativa = idpa;
	}
	/**
	 * Imposta la Busta ricevuta.
	 *
	 * @param aBusta Busta ricevuta.
	 * 
	 */
	public void setBusta(Busta aBusta){
		this.busta = aBusta;
	}
	/**
	 * Imposta eventuali errori riscontrati durante la validazione della Busta ricevuta. 
	 * Se non sono presenti errori, questa variabile conterra' un vector vuoto (size = 0)
	 *
	 * @param aErrors Errori riscontrati durante la validazione della busta.
	 * 
	 */
	public void setErrors(Vector<Eccezione> aErrors){
		this.errors = aErrors;
	}
	/**
	 * Imposta l'indicazione se la busta associata a questo messaggio e' un messaggio od un messaggioErrore
	 *
	 * @param isErrore indicazione se la busta associata a questo messaggio e' un messaggio (false) 
	 *                 od un messaggioErrore (true).
	 * 
	 */
	public void setMessaggioErroreProtocollo(boolean isErrore){
		this.isMessaggioErroreProtocollo = isErrore;
	}
	/**
	 * Imposta l'indicazione se la busta associata a questo messaggio e' un messaggio senza carico applicativo
	 *
	 * @param isDiServizio indicazione se la busta associata a questo messaggio e' un messaggio senza carico applicativo (true).
	 * 
	 */
	public void setIsBustaDiServizio(boolean isDiServizio){
		this.isBustaDiServizio = isDiServizio;
	}
	/**
	 * Eventuale servizio correlato associato al servizio presente nella busta.
	 *
	 * @param ser Eventuale servizio correlato associato al servizio presente nella busta.
	 * 
	 */
	public void setServizioCorrelato(String ser) {
		this.servizioCorrelato = ser;
	}

	/**
	 * Eventuale tipo di servizio correlato associato al servizio presente nella busta.
	 *
	 * @param tipo Eventuale tipo di servizio correlato associato al servizio presente nella busta.
	 * 
	 */
	public void setTipoServizioCorrelato(String tipo) {
		this.tipoServizioCorrelato = tipo;
	}

	public void setRuoloBustaRicevuta(RuoloBusta ruoloBustaRicevuta) {
		this.ruoloBustaRicevuta = ruoloBustaRicevuta;
	}

	/**
	 * @param oneWayVersione11
	 */
	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}
	
	
	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}




	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'identificatore della porta applicativa richiesta.
	 *
	 * @return Identificatore della porta applicativa.
	 * 
	 */
	public RichiestaApplicativa getRichiestaApplicativa(){
		return this.richiestaApplicativa;
	}
	/**
	 * Ritorna la Busta ricevuta come risposta.
	 *
	 * @return Busta ricevuta.
	 * 
	 */
	public Busta getBusta(){
		return this.busta;
	}
	/**
	 * Ritorna eventuali errori riscontrati durante la validazione della Busta ricevuta. 
	 * Se non sono presenti errori, questa variabile conterra' un vector vuoto (size = 0)
	 *
	 * @return Errori riscontrati durante la validazione della busta se presenti, un vector vuoto (size = 0) altrimenti.
	 * 
	 */
	public Vector<Eccezione> getErrors(){
		return this.errors;
	}
	/**
	 * Ritorna l'indicazione se la busta associata a questo messaggio e' un messaggio od un messaggioErrore
	 *
	 * @return indicazione se la busta associata a questo messaggio e' un messaggio (false) 
	 *                 od un messaggioErrore (true).
	 * 
	 */
	public boolean isMessaggioErroreProtocollo(){
		return this.isMessaggioErroreProtocollo;
	}
	/**
	 * Ritorna l'indicazione se la busta associata a questo messaggio e' un messaggio senza carico applicativo
	 *
	 * @return indicazione se la busta associata a questo messaggio e' un messaggio senza carico applicativo (true).
	 * 
	 */
	public boolean getIsBustaDiServizio(){
		return this.isBustaDiServizio;
	}


	/**
	 * Eventuale servizio correlato associato al servizio presente nella busta.
	 *
	 * @return Eventuale servizio correlato associato al servizio presente nella busta.
	 * 
	 */
	public String getServizioCorrelato() {
		return this.servizioCorrelato;
	}

	/**
	 * Eventuale tipo di servizio correlato associato al servizio presente nella busta.
	 *
	 * @return Eventuale tipo di servizio correlato associato al servizio presente nella busta.
	 * 
	 */
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}

	public RuoloBusta getRuoloBustaRicevuta() {
		return this.ruoloBustaRicevuta;
	}

	/**
	 * @return indicazione se stiamo gestendo oneway in modalita stateless 1.4
	 */
	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}

	public boolean isStateless() {
		return this.stateless;
	}

	public boolean isFiltroDuplicatiRichiestoAccordo() {
		return this.filtroDuplicatiRichiestoAccordo;
	}

	public void setFiltroDuplicatiRichiestoAccordo(
			boolean filtroDuplicatiRichiestoAccordo) {
		this.filtroDuplicatiRichiestoAccordo = filtroDuplicatiRichiestoAccordo;
	}

	public boolean isConfermaRicezioneRichiestoAccordo() {
		return this.confermaRicezioneRichiestoAccordo;
	}
	public void setConfermaRicezioneRichiestoAccordo(
			boolean confermaRicezioneRichiestoAccordo) {
		this.confermaRicezioneRichiestoAccordo = confermaRicezioneRichiestoAccordo;
	}

	public boolean isConsegnaOrdineRichiestoAccordo() {
		return this.consegnaOrdineRichiestoAccordo;
	}
	public void setConsegnaOrdineRichiestoAccordo(
			boolean consegnaOrdineRichiestoAccordo) {
		this.consegnaOrdineRichiestoAccordo = consegnaOrdineRichiestoAccordo;
	}


	
	
	public String getImplementazionePdDSoggettoMittente() {
		return this.implementazionePdDSoggettoMittente;
	}

	public void setImplementazionePdDSoggettoMittente(
			String implementazionePdDSoggettoMittente) {
		this.implementazionePdDSoggettoMittente = implementazionePdDSoggettoMittente;
	}

	public String getImplementazionePdDSoggettoDestinatario() {
		return this.implementazionePdDSoggettoDestinatario;
	}

	public void setImplementazionePdDSoggettoDestinatario(
			String implementazionePdDSoggettoDestinatario) {
		this.implementazionePdDSoggettoDestinatario = implementazionePdDSoggettoDestinatario;
	}
	
	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public DettaglioEccezione getDettaglioEccezione() {
		return this.dettaglioEccezione;
	}
	
	public void setDettaglioEccezione(DettaglioEccezione dettaglioEccezione) {
		this.dettaglioEccezione = dettaglioEccezione;
	}
}





