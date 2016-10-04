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


package org.openspcoop2.pdd.core;

import java.io.Serializable;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Informazioni protocollo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ProtocolContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** IdentificativoPorta.
	 * L'identificativo viene istanziato SOLO dopo che la PdD ha compreso l'identita' del soggetto impersonificato.
	 * Quindi dopo la validazione sintattica della busta (ricezione buste)
	 * o dopo la lettura della porta delegata (ricezione contenuti applicativi)
	 *  */
	private IDSoggetto dominio; 
	
	/** Identificativo del Soggetto Fruitore */
	private IDSoggetto fruitore;
	private String indirizzoFruitore;
	
	/** Identificativo del Soggetto Erogatore */
	private IDSoggetto erogatore;
	private String indirizzoErogatore;
	
	/** Identificativo messaggio */
	private String idRichiesta;
	/** Identificativo messaggio risposta */
	private String idRisposta;
	
	/** Identificativo collaborazione */
	private String collaborazione;

	/** Informazioni sul servizio e azione */
	private IDAccordo idAccordo;
	private String tipoServizio;
	private String servizio;
	private int versioneServizio;
	private String azione;
	
	/** Profilo di Collaborazione */
	private ProfiloDiCollaborazione profiloCollaborazione;
	private String profiloCollaborazioneValue;
	
	/** Scenario di cooperazione */
	private String scenarioCooperazione;
	
	/** Costruttore necessario per la serializzazione */
	public ProtocolContext(){}
	public ProtocolContext(IDSoggetto fruitore,String indirizzoFruitore, 
			IDSoggetto erogatore,String indirizzoErogatore,
			String idRichiesta, 
			IDAccordo idAccordo,String tipoServizio, String servizio, String azione) {
		this(fruitore, indirizzoFruitore,
				erogatore, indirizzoErogatore,
				idRichiesta, null,
				null,
				idAccordo,tipoServizio, servizio, azione,null);	
	}
	public ProtocolContext(IDSoggetto fruitore,String indirizzoFruitore, 
			IDSoggetto erogatore,String indirizzoErogatore,
			String idRichiesta, 
			String idRisposta,
			String collaborazione, 
			IDAccordo idAccordo,
			String tipoServizio, 
			String servizio, 
			String azione,
			ProfiloDiCollaborazione profiloCollaborazione) {
		
		this.fruitore = fruitore;
		this.indirizzoFruitore = indirizzoFruitore;
		
		this.erogatore = erogatore;
		this.indirizzoErogatore = indirizzoErogatore;
		
		this.idRichiesta = idRichiesta;
		this.idRisposta = idRisposta;
		
		this.collaborazione = collaborazione;
		this.idAccordo = idAccordo;
		this.tipoServizio = tipoServizio;
		this.servizio = servizio;
		this.azione = azione;
		this.profiloCollaborazione = profiloCollaborazione;
	}
	
	
	
	public IDSoggetto getFruitore() {
		return this.fruitore;
	}
	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}
	public IDSoggetto getErogatore() {
		return this.erogatore;
	}
	public void setErogatore(IDSoggetto erogatore) {
		this.erogatore = erogatore;
	}
	public String getIndirizzoFruitore() {
		return this.indirizzoFruitore;
	}
	public void setIndirizzoFruitore(String indirizzoFruitore) {
		this.indirizzoFruitore = indirizzoFruitore;
	}
	public String getIndirizzoErogatore() {
		return this.indirizzoErogatore;
	}
	public void setIndirizzoErogatore(String indirizzoErogatore) {
		this.indirizzoErogatore = indirizzoErogatore;
	}
	public String getIdRichiesta() {
		return this.idRichiesta;
	}
	public void setIdRichiesta(String idRichiesta) {
		this.idRichiesta = idRichiesta;
	}
	public String getIdRisposta() {
		return this.idRisposta;
	}
	public void setIdRisposta(String idRisposta) {
		this.idRisposta = idRisposta;
	}
	public String getCollaborazione() {
		return this.collaborazione;
	}
	public void setCollaborazione(String collaborazione) {
		this.collaborazione = collaborazione;
	}
	public String getTipoServizio() {
		return this.tipoServizio;
	}
	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}
	public String getServizio() {
		return this.servizio;
	}
	public void setServizio(String servizio) {
		this.servizio = servizio;
	}
	public int getVersioneServizio() {
		return this.versioneServizio;
	}
	public void setVersioneServizio(int versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public ProfiloDiCollaborazione getProfiloCollaborazione() {
		return this.profiloCollaborazione;
	}
	public void setProfiloCollaborazione(ProfiloDiCollaborazione profiloCollaborazione, String value) {
		// vengono effettuati i controlli al fine di non sovrascrivere eventuali valori corretti gia impostati
		// con null value, se la risposta non possiede il profilo (es. riscontro)
		if(profiloCollaborazione!=null) {
			this.profiloCollaborazione = profiloCollaborazione;
		}
		if(value!=null){
			this.profiloCollaborazioneValue = value;
		}
	}
	public String getScenarioCooperazione() {
		return this.scenarioCooperazione;
	}
	public String getProfiloCollaborazioneValue() {
		return this.profiloCollaborazioneValue;
	}
	public void setScenarioCooperazione(String scenarioCooperazione) {
		this.scenarioCooperazione = scenarioCooperazione;
	}
	public IDSoggetto getDominio() {
		return this.dominio;
	}
	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}
	public IDAccordo getIdAccordo() {
		return this.idAccordo;
	}
	public void setIdAccordo(IDAccordo idAccordo) {
		this.idAccordo = idAccordo;
	}
}
