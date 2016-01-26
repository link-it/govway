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


package org.openspcoop2.protocol.spcoop.config;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
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
	public SPCoopProtocolVersionManager(IProtocolFactory protocolFactory,String versione) throws ProtocolException{
		super(protocolFactory);
		this.versione = versione;
	}
	
	
	
	
	/* *********** FUNZIONALITA' OFFERTE DALLA Porta di Dominio ******************* */
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per il filtro duplicati.
	 * 
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(ProfiloDiCollaborazione profiloCollaborazione){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return StatoFunzionalitaProtocollo.ABILITATA;
		} else {
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
	}
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per la consegna affidabile.
	 * 
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	@Override
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(ProfiloDiCollaborazione profiloCollaborazione){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return StatoFunzionalitaProtocollo.DISABILITATA;
		} else {
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
	}
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per la consegna in ordine.
	 * 
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	@Override
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(ProfiloDiCollaborazione profiloCollaborazione){
		if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(this.versione)){
			return StatoFunzionalitaProtocollo.DISABILITATA;
		} else {
			return StatoFunzionalitaProtocollo.REGISTRO;
		}
	}
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per inserire più transazioni in una unica collaborazione
	 * 
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(ProfiloDiCollaborazione profiloCollaborazione){
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
