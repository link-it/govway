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


package org.openspcoop2.pdd.core;

import java.util.Vector;

import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;

/**
 * IntegrationContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationContext {

	/** Identificativo di correlazione applicativa */
	private String idCorrelazioneApplicativa;
	/** Identificativo di correlazione applicativa risposta */
	private String idCorrelazioneApplicativaRisposta;
	
	/** Identita' del servizio applicativo fruitore */
	private String servizioApplicativoFruitore = null;
	/** Identita' dei servizi applicativi erogatori */
	private Vector<String> serviziApplicativiErogatori = new Vector<String>();
	
	/** Indicazione se la gestione e' stateless/stateful (Null se non ancora definita) */
	private Boolean gestioneStateless;
	
	/**
	 * Tipo di autenticazione utilizzato
	 */
	private String tipoAutenticazione;
	
	/**
	 * Tipo di autorizzazione utilizzato
	 */
	private String tipoAutorizzazione;
	
	/**
	 * Tipo di autorizzazione per contenuto utilizzato
	 */
	private String tipoAutorizzazioneContenuto;
	
	/**
	 * Tipo di validazione contenuti
	 */
	private String tipoValidazioneContenuti;
	
	/**
	 * Identificativo della Porta Applicativa
	 */
	private IDPortaApplicativaByNome idPA;
	
	/**
	 * Identificativo della Porta Delegata
	 */
	private IDPortaDelegata idPD;
	
	public String getTipoAutorizzazione() {
		return this.tipoAutorizzazione;
	}
	public void setTipoAutorizzazione(String tipoAutorizzazione) {
		this.tipoAutorizzazione = tipoAutorizzazione;
	}
	public String getTipoAutenticazione() {
		return this.tipoAutenticazione;
	}
	public void setTipoAutenticazione(String tipoAutenticazione) {
		this.tipoAutenticazione = tipoAutenticazione;
	}
	
	public String getTipoAutorizzazioneContenuto() {
		return this.tipoAutorizzazioneContenuto;
	}
	public void setTipoAutorizzazioneContenuto(String tipoAutorizzazioneContenuto) {
		this.tipoAutorizzazioneContenuto = tipoAutorizzazioneContenuto;
	}
	
	public String getTipoValidazioneContenuti() {
		return this.tipoValidazioneContenuti;
	}
	public void setTipoValidazioneContenuti(String tipoValidazioneContenuti) {
		this.tipoValidazioneContenuti = tipoValidazioneContenuti;
	}
	
	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}
	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}
	public String getIdCorrelazioneApplicativaRisposta() {
		return this.idCorrelazioneApplicativaRisposta;
	}
	public void setIdCorrelazioneApplicativaRisposta(
			String idCorrelazioneApplicativaRisposta) {
		this.idCorrelazioneApplicativaRisposta = idCorrelazioneApplicativaRisposta;
	}
	
	public void addServizioApplicativoErogatore(String servizioApplicativo){
		this.serviziApplicativiErogatori.add(servizioApplicativo);
	}
	public int sizeServiziApplicativiErogatori(){
		return this.serviziApplicativiErogatori.size();
	}
	public String getServizioApplicativoErogatore(int index){
		return this.serviziApplicativiErogatori.get(index);
	}
	public String removeServizioApplicativoErogatore(int index){
		return this.serviziApplicativiErogatori.remove(index);
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public Boolean isGestioneStateless() {
		return this.gestioneStateless;
	}
	public void setGestioneStateless(Boolean gestioneStateless) {
		this.gestioneStateless = gestioneStateless;
	}
	
	public IDPortaApplicativaByNome getIdPA() {
		return this.idPA;
	}
	public void setIdPA(IDPortaApplicativaByNome idPA) {
		this.idPA = idPA;
	}
	public IDPortaDelegata getIdPD() {
		return this.idPD;
	}
	public void setIdPD(IDPortaDelegata idPD) {
		this.idPD = idPD;
	}
}
