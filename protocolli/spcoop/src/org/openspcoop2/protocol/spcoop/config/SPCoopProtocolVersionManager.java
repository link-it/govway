/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.spcoop.config;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolVersionManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopProtocolVersionManager extends SPCoopProtocolManager implements IProtocolVersionManager {
	
	protected String versione;
	public SPCoopProtocolVersionManager(IProtocolFactory<?> protocolFactory,String versione) throws ProtocolException{
		super(protocolFactory);
		this.versione = versione;
	}
	
	
	
	
	/* *********** FUNZIONALITA' OFFERTE DALLA Porta di Dominio ******************* */
	
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Busta busta){
		return this._getFiltroDuplicati();
	}
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Servizio infoServizio){
		return this._getFiltroDuplicati();
	}
	private StatoFunzionalitaProtocollo _getFiltroDuplicati(){

		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return StatoFunzionalitaProtocollo.ABILITATA;
		} else {
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
	}
	
	@Override
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(Busta busta){
		return this._getConsegnaAffidabile();
	}
	@Override
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(Servizio infoServizio){
		return this._getConsegnaAffidabile();
	}
	private StatoFunzionalitaProtocollo _getConsegnaAffidabile(){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return StatoFunzionalitaProtocollo.DISABILITATA;
		} else {
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
	}
	
	@Override
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(Busta busta){
		return this._getConsegnaInOrdine();
	}
	@Override
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(Servizio infoServizio){
		return this._getConsegnaInOrdine();
	}
	private StatoFunzionalitaProtocollo _getConsegnaInOrdine(){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return StatoFunzionalitaProtocollo.DISABILITATA;
		} else {
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
	}
	
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Busta busta){
		return this._getCollaborazione(busta.getProfiloDiCollaborazione());
	}
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Servizio infoServizio){
		return this._getCollaborazione(infoServizio.getProfiloDiCollaborazione());
	}
	private StatoFunzionalitaProtocollo _getCollaborazione(ProfiloDiCollaborazione profiloCollaborazione){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			if(profiloCollaborazione==null || ProfiloDiCollaborazione.UNKNOWN.equals(profiloCollaborazione)){
				return StatoFunzionalitaProtocollo.REGISTRO;
			}
			else if(profiloCollaborazione.equals(ProfiloDiCollaborazione.ONEWAY) || profiloCollaborazione.equals(ProfiloDiCollaborazione.SINCRONO))
				return StatoFunzionalitaProtocollo.DISABILITATA;
			else {
				return StatoFunzionalitaProtocollo.ABILITATA;
			}	
		}
		else{
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
		
	}
	
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Busta busta){
		return this._getIdRiferimentoRichiesta();
	}
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Servizio infoServizio){
		return this._getIdRiferimentoRichiesta();
	}
	private StatoFunzionalitaProtocollo _getIdRiferimentoRichiesta(){
		return StatoFunzionalitaProtocollo.DISABILITATA; // non supportato in spcoop la relazione tra piu invocazioni oneway
	}

	
	
	
	/* *********** PROFILI ASINCRONI ******************* */

	/**
	 * Ritorna l'indicazione se sulla busta di risposta del profilo asincrono simmetrico 
	 * debba essere inserito l'identificatore del messaggio della richiesta asincrona.
	 * 
	 * @return Indicazione se l'id della richiesta debba essere inserito o meno nella busta di risposta
	 */
	@Override
	public boolean isCorrelazioneRichiestaPresenteRispostaAsincronaSimmetrica(){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return false;
		} else {
			return true;
		}
	}
	/**
	 * Ritorna l'indicazione se sulla busta di richiesta stato del profilo asincrono asimmetrico 
	 * debba essere inserito l'identificatore del messaggio della richiesta asincrona.
	 * 
	 * @return Indicazione se l'id della richiesta debba essere inserito o meno nella busta di richiesta stato
	 */
	@Override
	public boolean isCorrelazioneRichiestaPresenteRichiestaStatoAsincronaAsimmetrica(){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Indicazione, per il profilo asincrono simmetrico,
	 * se deve essere riportato o meno nella busta di richiesta il servizio correlato dove deve essere inviata la risposta
	 * 
	 * @return indicazione se il servizio correlato deve essere indicato o meno nella busta
	 */
	@Override
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoSimmetrico(){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return false;
		} else {
			return true;
		}
	}
	/**
	 * Indicazione, per il profilo asincrono asimmetrico,
	 * se deve essere riportato o meno nella busta di ricevuta alla richiesta il servizio correlato dove deve essere effettuato il polling
	 * 
	 * @return indicazione se il servizio correlato deve essere indicato o meno nella busta
	 */
	@Override
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico(){
		return !SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione);
	}
	
	/**
	 * Ritorna l'identificatore da utilizzare per la correlazione asincrona scelto tra i valori della busta
	 * 
	 * @param richiesta Busta
	 * @return identificatore da utilizzare per la correlazione asincrona
	 */
	@Override
	public String getIdCorrelazioneAsincrona(Busta richiesta){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return richiesta.getCollaborazione();
		} else {
			return richiesta.getRiferimentoMessaggio();
		}
	}
	
	/**
	 * Ritorna l'identificatore da utilizzare per la correlazione asincrona scelto tra i due parametri
	 * 
	 * @param rifMsg riferimento messaggio 
	 * @param collaborazione id collaborazione
	 * @return identificatore da utilizzare per la correlazione asincrona
	 */
	@Override
	public String getIdCorrelazioneAsincrona(String rifMsg,String collaborazione){
		if (SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equalsIgnoreCase(this.versione)) {
			// Se presente riferimentoMessaggio utilizzo quello.
			if (rifMsg != null) {
				return rifMsg;
			} else if (collaborazione != null) {
				// Utilizzo Collaborazione come riferimentoServizioCorrelato
				// Tanto nelle linee guida non possono esistere piu' istanze con la stessa collaborazione, e' stata deprecata.
				// Per igni istanza asincrona (richiesta/risposta) la richiesta genera una collaborazione a capostipite
				return collaborazione;
			} 
		} else {
			return rifMsg;
		}
		return null;
	}
	
	
	
	
	

	
	/* *********** ECCEZIONI ******************* */

	/**
	 * Indicazione se devono essere gestite eccezioni di livello INFO
	 * 
	 * @return Indicazione se devono essere gestite eccezioni di livello INFO
	 */
	@Override
	public boolean isEccezioniLivelloInfoAbilitato(){
		return SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione);
	}

	
	/**
	 * Indicazione se devono essere ignorate eventuali eccezioni di livello diverso da GRAVE
	 * 
	 * @return Indicazione se devono essere ignorate eventuali eccezioni di livello diverso da GRAVE
	 */
	@Override
	public boolean isIgnoraEccezioniLivelloNonGrave(){
		return SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione);
	}
	
	
	
	
	
	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	/**
	 * Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 */
	@Override
	public boolean isGenerazioneBusteErrore_strutturaMalformataHeaderProtocollo(){
		return this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo();
	}
	
	/**
	 * Indicazione se in caso di messaggio con profilo oneway duplicato, debba essere generato un errore di protocollo o meno
	 * 
	 * @return Indicazione se in caso di messaggio con profilo oneway duplicato, debba essere generato un errore di protocollo o meno
	 */
	@Override
	public boolean isGenerazioneErroreMessaggioOnewayDuplicato(){
		return SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione);
	}

	
	
	
	
	
	/* *********** ALTRO ******************* */
    
	/**
	 * Indicazione se l'indirizzo telematico deve essere interpretato dalla Porta di Dominio
	 * 
	 * @return Indicazione se l'indirizzo telematico deve essere interpretato dalla Porta di Dominio
	 */
	@Override
	public boolean isUtilizzoIndirizzoSoggettoPresenteBusta(){
		return !SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equalsIgnoreCase(this.versione);
	}



}
