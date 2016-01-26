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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link InoltroBusteMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class InoltroRisposteMessage implements GenericMessage {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Dominio di gestione */
	private IDSoggetto identitaPdD;

	/** Indicazione se la busta contiene la segnalazione di ricezione di una busta di risposta Malformata,
     o proprio una risposta da inoltrare*/
	private boolean inoltroSegnalazioneErrore;

	/** Busta  della risposta da inoltrare */
	private Busta bustaRisposta; 

	/** Profilo di Gestione (presente solo se inoltroSegnalazioneErrore=false) */
	private String profiloGestione = null;

	/** OpenSPCoop state */
	private OpenSPCoopStateless openspcoopstate = null;

	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11;
	
	/** Eventuale correlazioneApplicativa */
	private String idCorrelazioneApplicativa;
	
	/** Eventuale correlazioneApplicativa della risposta */
	private String idCorrelazioneApplicativaRisposta;
	
	/** Eventuale servizioApplicativoFruitore */
	private String servizioApplicativoFruitore;
	
	/** Indicazione se deve essere effettuato l'imbustamento */
	private boolean imbustamento=true;

	/** Tipologia di porta di domino del soggetto mittente */
	private String implementazionePdDSoggettoMittente;
	/** Tipologia di porta di domino del soggetto destinatario */
	private String implementazionePdDSoggettoDestinatario;
	
	/** PdDContext */
	private PdDContext pddContext;
	

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public InoltroRisposteMessage(){
	}





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta il dominio che deve gestire la busta di risposta.
	 *
	 * @param dominio Dominio
	 * 
	 */
	public void setDominio(IDSoggetto dominio){
		this.identitaPdD = dominio;
	}


	/**
	 * Imposta la indicazione se la busta e una segnalazione di errore
	 *
	 * @param segnalazione Indicazione se la busta e una segnalazione di errore
	 * 
	 */
	public void setInoltroSegnalazioneErrore(boolean segnalazione){
		this.inoltroSegnalazioneErrore = segnalazione;
	}


	/**
	 * Imposta la busta della risposta
	 *
	 * @param busta busta di risposta
	 * 
	 */
	public void setBustaRisposta(Busta busta){
		this.bustaRisposta = busta;
	}

	public void setProfiloGestione(String profiloGestione) {
		this.profiloGestione = profiloGestione;
	}

	public void setOpenspcoopstate(OpenSPCoopStateless openspcoopstate) {
		this.openspcoopstate = openspcoopstate;
	}

	/**
	 * @param oneWayVersione11
	 */
	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}

	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}

	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}

	public void setImbustamento(boolean imbustamento) {
		this.imbustamento = imbustamento;
	}
	
	
	/* ********  G E T T E R   ******** */


	/**
	 * Ritorna il dominio che deve gestire la busta di risposta.
	 *
	 * @return Dominio
	 * 
	 */
	public IDSoggetto getDominio(){
		return this.identitaPdD;
	}


	/**
	 * Ritorna la indicazione se la busta e una segnalazione di errore
	 *
	 * @return Indicazione se la busta e una segnalazione di errore
	 * 
	 */
	public boolean getInoltroSegnalazioneErrore(){
		return this.inoltroSegnalazioneErrore;
	}

	/**
	 * Ritorna la busta della risposta
	 *
	 * @return busta di risposta
	 * 
	 */
	public Busta getBustaRisposta(){
		return this.bustaRisposta;
	}

	public String getProfiloGestione() {
		return this.profiloGestione;
	}

	public OpenSPCoopStateless getOpenspcoopstate() {
		return this.openspcoopstate;
	}
	
	/**
	 * @return indicazione se stiamo gestendo oneway in modalita stateless 1.4
	 */
	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}


	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}

	public boolean isImbustamento() {
		return this.imbustamento;
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
	

	public String getIdCorrelazioneApplicativaRisposta() {
		return this.idCorrelazioneApplicativaRisposta;
	}

	public void setIdCorrelazioneApplicativaRisposta(
			String idCorrelazioneApplicativaRisposta) {
		this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
	}
}





