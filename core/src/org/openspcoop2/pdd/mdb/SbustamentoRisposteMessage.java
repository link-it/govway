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



package org.openspcoop2.pdd.mdb;

import java.sql.Timestamp;
import java.util.Vector;

import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link SbustamentoRisposteMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SbustamentoRisposteMessage implements GenericMessage {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore della Porta Delegata Richiesta */
	private RichiestaDelegata richiestaDelegata;
	/** Busta ricevuta come risposta. */
	private Busta busta;
	/** Eventuali errori riscontrati durante la validazione della Busta ricevuta come risposta. 
        Se non sono presenti errori, questa variabile conterra' un vector vuoto (size = 0) */
	private Vector<Eccezione> errors;
	/** Indicazione se la busta associata a questo messaggio e' un messaggioProtocollo od un messaggioErroreProtocollo */
	private boolean isMessaggioErroreProtocollo;
	/** Indicazione se la busta associata a questo messaggio e' un messaggioProtocollo senza carico applicativo */
	private boolean isBustaDiServizio;
	/** Indicazione se e' abilitato il filtro duplicati */
    private boolean isFiltroDuplicati;
    /** RuoloBusta */
	private RuoloBusta ruoloBustaRicevuta = null;
	
	/** Gestione tempi di attraversamento */
	private Timestamp spedizioneMsgIngresso;
	private Timestamp ricezioneMsgRisposta;
		
	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11;
	
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
	public SbustamentoRisposteMessage(){
	}




	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'identificatore della porta delegata richiesta.
	 *
	 * @param idpd Identificatore della porta delegata.
	 * 
	 */
	public void setRichiestaDelegata(RichiestaDelegata idpd){
		this.richiestaDelegata = idpd;
	}
	/**
	 * Imposta la Busta ricevuta come risposta.
	 *
	 * @param aBusta Busta ricevuta.
	 * 
	 */
	public void setBusta(Busta aBusta){
		this.busta = aBusta;
	}
	/**
	 * Imposta eventuali errori riscontrati durante la validazione della Busta ricevuta come risposta. 
	 * Se non sono presenti errori, questa variabile conterra' un vector vuoto (size = 0)
	 *
	 * @param aErrors Errori riscontrati durante la validazione della busta.
	 * 
	 */
	public void setErrors(Vector<Eccezione> aErrors){
		this.errors = aErrors;
	}
	/**
	 * Imposta l'indicazione se la busta associata a questo messaggio e' un messaggioProtocollo od un messaggioErroreProtocollo
	 *
	 * @param isErrore indicazione se la busta associata a questo messaggio e' un messaggioProtocollo (false) 
	 *                 od un messaggioErroreProtocollo (true).
	 * 
	 */
	public void setMessaggioErroreProtocollo(boolean isErrore){
		this.isMessaggioErroreProtocollo = isErrore;
	}
	/**
	 * Imposta l'indicazione se la busta associata a questo messaggio e' un messaggioProtocollo senza carico applicativo
	 *
	 * @param isDiServizio indicazione se la busta associata a questo messaggio e' un messaggioProtocollo senza carico applicativo (true).
	 * 
	 */
	public void setIsBustaDiServizio(boolean isDiServizio){
		this.isBustaDiServizio = isDiServizio;
	}

	public void setRuoloBustaRicevuta(RuoloBusta ruoloBustaRicevuta) {
		this.ruoloBustaRicevuta = ruoloBustaRicevuta;
	}

	public void setRicezioneMsgRisposta(Timestamp ricezioneMsgRisposta) {
		this.ricezioneMsgRisposta = ricezioneMsgRisposta;
	}

	public void setSpedizioneMsgIngresso(Timestamp spedizioneMsgIngresso) {
		this.spedizioneMsgIngresso = spedizioneMsgIngresso;
	}

	/**
	 * @param oneWayVersione11
	 */
	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}
	
	



	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'identificatore della porta delegata richiesta.
	 *
	 * @return Identificatore della porta delegata.
	 * 
	 */
	public RichiestaDelegata getRichiestaDelegata(){
		return this.richiestaDelegata;
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
	 * Ritorna eventuali errori riscontrati durante la validazione della Busta ricevuta come risposta. 
	 * Se non sono presenti errori, questa variabile conterra' un vector vuoto (size = 0)
	 *
	 * @return Errori riscontrati durante la validazione della busta se presenti, un vector vuoto (size = 0) altrimenti.
	 * 
	 */
	public Vector<Eccezione> getErrors(){
		return this.errors;
	}
	/**
	 * Ritorna l'indicazione se la busta associata a questo messaggio e' un messaggioProtocollo od un messaggioErroreProtocollo
	 *
	 * @return indicazione se la busta associata a questo messaggio e' un messaggioProtocollo (false) 
	 *                 od un messaggioErroreProtocollo (true).
	 * 
	 */
	public boolean isMessaggioErroreProtocollo(){
		return this.isMessaggioErroreProtocollo;
	}
	/**
	 * Ritorna l'indicazione se la busta associata a questo messaggio e' un messaggioProtocollo senza carico applicativo
	 *
	 * @return indicazione se la busta associata a questo messaggio e' un messaggioProtocollo senza carico applicativo (true).
	 * 
	 */
	public boolean getIsBustaDiServizio(){
		return this.isBustaDiServizio;
	}

	public boolean isFiltroDuplicati() {
		return this.isFiltroDuplicati;
	}

	public void setFiltroDuplicati(boolean isFiltroDuplicati) {
		this.isFiltroDuplicati = isFiltroDuplicati;
	}

	public RuoloBusta getRuoloBustaRicevuta() {
		return this.ruoloBustaRicevuta;
	}

	public Timestamp getRicezioneMsgRisposta() {
		return this.ricezioneMsgRisposta;
	}

	public Timestamp getSpedizioneMsgIngresso() {
		return this.spedizioneMsgIngresso;
	}

	/**
	 * @return indicazione se stiamo gestendo oneway in modalita stateless 1.4
	 */
	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
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





