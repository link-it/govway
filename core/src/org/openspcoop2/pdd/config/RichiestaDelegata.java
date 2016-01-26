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



package org.openspcoop2.pdd.config;


import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;


/**
 * Classe contenente i dati di una richiesta delegata. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RichiestaDelegata implements java.io.Serializable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore della porta delegata */
	private IDPortaDelegata idPortaDelegata;
	/** Nome del Servizio Applicativo che sta' richiedendo il servizio */
	private String servizioApplicativo;
	/** IDAccordo */
	private IDAccordo idAccordo;
	/** Identificatore del ServizioRichiesto */
	private IDServizio idServizio;
	/** Identificatore del modulo OpenSPCoop che ha gestito la richiesta, e che sta aspettando una risposta */
	private String idModuloInAttesa;
	/** Indicazione sulla modalita' di costruzione di un eventuale msg di errore */
	private ProprietaErroreApplicativo fault;
	/** Dominio di gestione */
	private IDSoggetto dominio;
	/** Indica il tipo di scenario di cooperazione da intraprendere. */
	private String scenario;
	/** Indica il profilo di collaborazione utilizzato */
	private ProfiloDiCollaborazione profiloCollaborazione;
	private String profiloCollaborazioneValue;
	/** Utilizzo Consegna Asincrona */
	private boolean utilizzoConsegnaAsincrona;
	/** Indica se deve essere attesa o meno una ricevuta asincrona, in caso di profili asincroni */
	private boolean ricevutaAsincrona;
	/** Indicazione sulla eventuale collaborazione intrapresa */
	private String idCollaborazione;
	/** ID di CorrelazioneApplicativa */
	private String idCorrelazioneApplicativa;
	/** ID di CorrelazioneApplicativaRisposta */
	private String idCorrelazioneApplicativaRisposta;
	 /** Profilo di Gestione */
	private String profiloGestione = null;



	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param aSoggetto Identificatore del Soggetto che sta' richiedendo il servizio
	 * 
	 */
	public RichiestaDelegata(IDSoggetto aSoggetto){
		this.idPortaDelegata = new IDPortaDelegata();
		this.idPortaDelegata.setSoggettoFruitore(aSoggetto);
	}
	
	/**
	 * Costruttore. 
	 *
	 * @param aSoggetto Identificatore del Soggetto che sta' richiedendo il servizio
	 * @param aLocationPD Location della Porta Delegata Richiesta
	 * 
	 */
	public RichiestaDelegata(IDSoggetto aSoggetto, String aLocationPD){
		this.idPortaDelegata = new IDPortaDelegata();
		this.idPortaDelegata.setLocationPD(aLocationPD);
		this.idPortaDelegata.setSoggettoFruitore(aSoggetto);
	}
	
	/**
	 * Costruttore. 
	 *
	 * @param aSoggetto Identificatore del Soggetto che sta' richiedendo il servizio
	 * @param aLocationPD Location della Porta Delegata Richiesta
	 * @param aServizioApplicativo Nome del Servizio Applicativo che sta' richiedendo il servizio
	 * @param idS Identificativo del servizio richiesto
	 * @param idModulo Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * @param fault Proprieta' di un eventuale fault
	 * @param dominio Dominio di gestione, puo' essere differente dal soggetto fruitore (es. Router)
	 * 
	 */
	public RichiestaDelegata(IDSoggetto aSoggetto, String aLocationPD, 
			String aServizioApplicativo,IDServizio idS,
			String idModulo,ProprietaErroreApplicativo fault,IDSoggetto dominio){
		this.idPortaDelegata = new IDPortaDelegata();
		this.idPortaDelegata.setLocationPD(aLocationPD);
		this.idPortaDelegata.setSoggettoFruitore(aSoggetto);
		this.servizioApplicativo = aServizioApplicativo;
		this.idServizio = idS;
		this.idModuloInAttesa = idModulo;
		this.fault = fault;
		this.dominio = dominio;
	}
	/**
	 * Costruttore. 
	 *
	 * @param aSoggetto Identificatore del Soggetto che sta' richiedendo il servizio
	 * @param aLocationPD Location della Porta Delegata Richiesta
	 * @param aServizioApplicativo Nome del Servizio Applicativo che sta' richiedendo il servizio
	 * @param idModulo Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * @param fault Proprieta' di un eventuale fault
	 * @param dominio Dominio di gestione, puo' essere differente dal soggetto fruitore (es. Router)
	 * 
	 */
	public RichiestaDelegata(IDSoggetto aSoggetto, String aLocationPD, 
			String aServizioApplicativo,String idModulo,ProprietaErroreApplicativo fault,IDSoggetto dominio){
		this(aSoggetto,aLocationPD,aServizioApplicativo,null,idModulo,fault,dominio);
	}
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public RichiestaDelegata(){}




	/* ********  S E T T E R   ******** */


	/**
	 * Imposta il nome del Servizio Applicativo che sta' richiedendo il servizio
	 *
	 * @param idServizio Nome del Servizio Applicativo.
	 * 
	 */
	public void setServizioApplicativo(String idServizio){
		this.servizioApplicativo = idServizio;
	} 
	/**
	 * Imposta l'identificatore del servizio richiesto all'interno del registro dei servizi.
	 *
	 * @param idservice Identificatore del servizio all'interno del registro.
	 * 
	 */
	public void setIdServizio(IDServizio idservice){
		this.idServizio = idservice;
	}
	/**
	 * Identificatore del modulo OpenSPCoop che ha gestito la richiesta, 
	 * e che sta aspettando una risposta.
	 *
	 * @param idModulo Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * 
	 */
	public void setIdModuloInAttesa(String idModulo){
		this.idModuloInAttesa = idModulo;
	} 
	/**
	 * Imposta l'indicazione sulla modalita' di costruzione di un eventuale msg di errore.
	 * 
	 * @param fault Proprieta' di un eventuale fault 
	 * 
	 */
	public void setFault(ProprietaErroreApplicativo fault) {
		this.fault = fault;
	} 

	/**
	 * Imposta il dominio di gestione
	 * 
	 * @param dominio Dominio di gestione
	 * 
	 */
	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}
	/**
	 * Imposta il tipo di scenario di cooperazione da intraprendere
	 * 
	 * @param scenario tipo di scenario di cooperazione da intraprendere
	 * 
	 */
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
	/**
	 * Indicazione sull'utilizzo della consegna asincrona
	 * 
	 * @param utilizzoConsegnaAsincrona Indicazione sull'utilizzo della consegna asincrona
	 */
	public void setUtilizzoConsegnaAsincrona(boolean utilizzoConsegnaAsincrona) {
		this.utilizzoConsegnaAsincrona = utilizzoConsegnaAsincrona;
	}

	/**
	 * Indicazione sull'attesa di una ricevuta asincrona
	 * 
	 * @param ricevutaAsincrona Indicazione  sull'attesa di una ricevuta asincrona
	 */
	public void setRicevutaAsincrona(boolean ricevutaAsincrona) {
		this.ricevutaAsincrona = ricevutaAsincrona;
	}
	
	/**
	 * Identificatore della porta delegata
	 * 
	 * @param idPortaDelegata Identificatore della porta delegata
	 */
	public void setIdPortaDelegata(IDPortaDelegata idPortaDelegata) {
		this.idPortaDelegata = idPortaDelegata;
	}
	
	public void setProfiloGestione(String profiloGestione) {
		this.profiloGestione = profiloGestione;
	}
	

	/* ********  G E T T E R   ******** */
	/**
	 * Ritorna l'identificatore del Soggetto che sta' richiedendo il servizio
	 *
	 * @return Identificatore del Soggetto.
	 * 
	 */
	public IDSoggetto getSoggettoFruitore(){
		if(this.idPortaDelegata!=null)
			return this.idPortaDelegata.getSoggettoFruitore();
		else
			return null;
	}
	/**
	 * Ritorna la Location della Porta Delegata Richiesta
	 *
	 * @return Location della Porta Delegata.
	 * 
	 */
	public String getLocationPD(){
		if(this.idPortaDelegata!=null)
			return this.idPortaDelegata.getLocationPD();
		else
			return null;
	}
	
	/**
	 * Ritorna il nome del Servizio Applicativo che sta' richiedendo il servizio
	 *
	 * @return Nome del Servizio Applicativo.
	 * 
	 */
	public String getServizioApplicativo(){
		return this.servizioApplicativo;
	} 
	/**
	 * Ritorna l'identificatore del servizio richiesto all'interno del registro dei servizi.
	 *
	 * @return Identificatore del servizio all'interno del registro.
	 * 
	 */
	public IDServizio getIdServizio(){
		return this.idServizio;
	}
	/**
	 * Ritorna l'identificatore del modulo OpenSPCoop che ha gestito la richiesta, 
	 * e che sta aspettando una risposta.
	 *
	 * @return Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * 
	 */
	public String getIdModuloInAttesa(){
		return this.idModuloInAttesa;
	}

	/**
	 * Ritorna l'indicazione sulla modalita' di costruzione di un eventuale msg di errore.
	 * 
	 * @return Proprieta' di un eventuale fault 
	 * 
	 */
	public ProprietaErroreApplicativo getFault() {
		return this.fault;
	} 
	
	/**
	 *  Ritorna il dominio di gestione
	 *  
	 * @return dominio di gestione.
	 * 
	 */
	public IDSoggetto getDominio() {
		return this.dominio;
	}
	/**
	 * Ritorna il tipo di scenario di cooperazione da intraprendere
	 * 
	 * @return tipo di scenario di cooperazione da intraprendere
	 * 
	 */
	public String getScenario() {
		return this.scenario;
	}
	/**
	 * Indicazione sull'utilizzo della consegna asincrona
	 * 
	 * @return Indicazione sull'utilizzo della consegna asincrona
	 */
	public boolean getUtilizzoConsegnaAsincrona() {
		return this.utilizzoConsegnaAsincrona;
	}
	/**
	 * Indicazione sull'attesa di una ricevuta asincrona
	 * 
	 * @return Indicazione  sull'attesa di una ricevuta asincrona
	 */
	public boolean isRicevutaAsincrona() {
		return this.ricevutaAsincrona;
	}
	/**
	 * Ritorna l'identificatore della porta delegata
	 * 
	 * @return identificatore della porta delegata
	 */
	public IDPortaDelegata getIdPortaDelegata() {
		return this.idPortaDelegata;
	}

	/**
	 * @return the idCollaborazione
	 */
	public String getIdCollaborazione() {
		return this.idCollaborazione;
	}

	/**
	 * @param idCollaborazione the idCollaborazione to set
	 */
	public void setIdCollaborazione(String idCollaborazione) {
		this.idCollaborazione = idCollaborazione;
	}

	/**
	 * @return the profiloCollaborazione
	 */
	public ProfiloDiCollaborazione getProfiloCollaborazione() {
		return this.profiloCollaborazione;
	}

	/**
	 * @param profiloCollaborazione the profiloCollaborazione to set
	 */
	public void setProfiloCollaborazione(ProfiloDiCollaborazione profiloCollaborazione, String profiloCollaborazioneValue) {
		this.profiloCollaborazione = profiloCollaborazione;
		this.profiloCollaborazioneValue = profiloCollaborazioneValue;
	}
	
	public String getProfiloCollaborazioneValue() {
		return this.profiloCollaborazioneValue;
	}

	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public String getProfiloGestione() {
		return this.profiloGestione;
	}
	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}

	public IDAccordo getIdAccordo() {
		return this.idAccordo;
	}

	public void setIdAccordo(IDAccordo idAccordo) {
		this.idAccordo = idAccordo;
	}

	public String getIdCorrelazioneApplicativaRisposta() {
		return this.idCorrelazioneApplicativaRisposta;
	}

	public void setIdCorrelazioneApplicativaRisposta(
			String idCorrelazioneApplicativaRisposta) {
		this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
	}
	
}
