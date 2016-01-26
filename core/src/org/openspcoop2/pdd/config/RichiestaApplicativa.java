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

import java.util.Hashtable;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * Classe contenente i dati di una richiesta applicativa. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RichiestaApplicativa implements java.io.Serializable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore del Soggetto che sta' richiedendo il servizio */
	private IDSoggetto soggettoFruitore;
	/** IDAccordo */
	private IDAccordo idAccordo;
	/** IDServizio */
	private IDServizio idServizio;
	/** Identificatore della PortaApplicativa */
	private IDPortaApplicativaByNome idPAbyNome;
	/** Nome del Servizio Applicativo che eroga il servizio */
	private String servizioApplicativo;
	/** Identificatore del modulo OpenSPCoop che ha gestito la richiesta, e che sta aspettando una risposta */
	private String idModuloInAttesa;
	/** Dominio di gestione */
	private IDSoggetto dominio;
	/** Indica il tipo di scenario di cooperazione da intraprendere. */
	private String scenario;
	/** Indica se deve essere attesa o meno una ricevuta asincrona, in caso di profili asincroni */
	private boolean ricevutaAsincrona;
	/** Filtri per identificazione PA */
	Hashtable<String, String> filtroProprietaPorteApplicative;
	 /** Profilo di Gestione */
	private String profiloGestione = null;
	/** Nome del Servizio Applicativo che sta' richiedendo il servizio */
	private String identitaServizioApplicativoFruitore;
	/** ID di CorrelazioneApplicativa */
	private String idCorrelazioneApplicativa;
	/** ID di CorrelazioneApplicativaRisposta */
	private String idCorrelazioneApplicativaRisposta;
	/** LocalForward */
	private boolean localForward;



	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 * @param sog Identificatore del Soggetto che sta' richiedendo il servizio
	 * @param idServ Identificativo del Servizio richiesto
	 * @param idModulo Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * @param dominio Dominio di gestione, puo' essere differente dal soggetto fruitore (es. Router)
	 * 
	 */
	public RichiestaApplicativa(IDSoggetto sog,IDServizio idServ, String idModulo, IDSoggetto dominio, IDPortaApplicativaByNome idByNome){
		this.soggettoFruitore = sog;
		this.idServizio = idServ;
		this.idModuloInAttesa = idModulo;
		this.dominio = dominio;
		this.idPAbyNome = idByNome;
	}
	/**
	 * Costruttore. 
	 *
	 * @param sog Identificatore del Soggetto che sta' richiedendo il servizio
	 * @param idServ Identificativo del Servizio richiesto
	 * @param dominio Dominio di gestione, puo' essere differente dal soggetto fruitore (es. Router)
	 * 
	 */
	public RichiestaApplicativa(IDSoggetto sog,IDServizio idServ,IDSoggetto dominio, IDPortaApplicativaByNome idByNome){
		this.soggettoFruitore = sog;
		this.idServizio = idServ;
		this.dominio = dominio;
		this.idPAbyNome = idByNome;
	}




	/* ********  S E T T E R   ******** */
	/**
	 * Imposta l'identificatore del Soggetto che sta' richiedendo il servizio
	 *
	 * @param idSoggetto Identificatore del Soggetto.
	 * 
	 */
	public void setSoggettoFruitore(IDSoggetto idSoggetto){
		this.soggettoFruitore = idSoggetto;
	}
	/**
	 * Imposta il nome del Servizio Applicativo che eroga il servizio
	 *
	 * @param idServizio Nome del Servizio Applicativo.
	 * 
	 */
	public void setServizioApplicativo(String idServizio){
		this.servizioApplicativo = idServizio;
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
	 * Indicazione sull'attesa di una ricevuta asincrona
	 * 
	 * @param ricevutaAsincrona Indicazione  sull'attesa di una ricevuta asincrona
	 */
	public void setRicevutaAsincrona(boolean ricevutaAsincrona) {
		this.ricevutaAsincrona = ricevutaAsincrona;
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
		return this.soggettoFruitore;
	}
	/**
	 * Ritorna l'identificativo del servizio all'interno del registro dei servizi.
	 *
	 * @return Identificativo del servizio
	 * 
	 */
	public IDServizio getIDServizio(){
		return this.idServizio;
	}
	public IDPortaApplicativa getIdPA(){
		if(this.idServizio!=null){
			IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
			idPortaApplicativa.setIDServizio(this.idServizio);
			return idPortaApplicativa;
		}
		else{
			return null;
		}
	}
	/**
	 * Ritorna il nome del Servizio Applicativo che eroga il servizio
	 *
	 * @return Nome del Servizio Applicativo.
	 * 
	 */
	public String getServizioApplicativo(){
		return this.servizioApplicativo;
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
	 * Indicazione sull'attesa di una ricevuta asincrona
	 * 
	 * @return Indicazione  sull'attesa di una ricevuta asincrona
	 */
	public boolean isRicevutaAsincrona() {
		return this.ricevutaAsincrona;
	}
	public Hashtable<String, String> getFiltroProprietaPorteApplicative() {
		return this.filtroProprietaPorteApplicative;
	}
	public void setFiltroProprietaPorteApplicative(
			Hashtable<String, String> filtroProprietaPorteApplicative) {
		this.filtroProprietaPorteApplicative = filtroProprietaPorteApplicative;
	}
	
	public String getProfiloGestione() {
		return this.profiloGestione;
	}
	public String getIdentitaServizioApplicativoFruitore() {
		return this.identitaServizioApplicativoFruitore;
	}
	public void setIdentitaServizioApplicativoFruitore(
			String identitaServizioApplicativoFruitore) {
		this.identitaServizioApplicativoFruitore = identitaServizioApplicativoFruitore;
	}
	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
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
	
	public boolean isLocalForward() {
		return this.localForward;
	}
	public void setLocalForward(boolean localForward) {
		this.localForward = localForward;
	}
	
	public IDPortaApplicativaByNome getIdPAbyNome() {
		return this.idPAbyNome;
	}
	public void setIdPAbyNome(IDPortaApplicativaByNome idPAbyNome) {
		this.idPAbyNome = idPAbyNome;
	}
}






