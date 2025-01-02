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

package org.openspcoop2.protocol.sdk.config;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;

/**
 * Interfaccia del Manager del Protocollo con filtro sulla versione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IProtocolVersionManager extends IProtocolManager {


	
	/* *********** FUNZIONALITA' OFFERTE DALLA Porta di Dominio ******************* */
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per il filtro duplicati.
	 * 
	 * @param busta Busta
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Busta busta);
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per il filtro duplicati.
	 * 
	 * @param infoServizio InfoServizio
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getFiltroDuplicati(Servizio infoServizio);
	
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per la consegna affidabile.
	 *
	 * @param busta Busta
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(Busta busta);
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per la consegna affidabile.
	 *
	 * @param infoServizio InfoServizio
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(Servizio infoServizio);
	
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per la consegna in ordine.
	 *
	 * @param busta Busta
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(Busta busta);
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per la consegna in ordine.
	 *
	 * @param infoServizio InfoServizio
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(Servizio infoServizio);
	
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per inserire più transazioni in una unica collaborazione
	 * 
	 * @param busta Busta
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getCollaborazione(Busta busta);
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio deve attuare per inserire più transazioni in una unica collaborazione
	 * 
	 * @param infoServizio InfoServizio
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getCollaborazione(Servizio infoServizio);
	
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio attua per gestire il riferimento di una precedente richiesta
	 * 
	 * @param busta Busta
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Busta busta);
	
	/**
	 * Ritorna l'indicazione sulla modalità di gestione che la Porta di Dominio attua per gestire il riferimento di una precedente richiesta
	 * 
	 * @param infoServizio InfoServizio
	 * @return Indicazione che la PdS deve intraprendere indicata tramite l'oggetto {link org.openspcoop.engine.modules.StatoFunzionalitaProtocollo}.
	 */
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Servizio infoServizio);
	
	
	
	
	
	/* *********** PROFILI ASINCRONI ******************* */
	
	/**
	 * Ritorna l'indicazione se sulla busta di risposta del profilo asincrono simmetrico 
	 * debba essere inserito l'identificatore del messaggio della richiesta asincrona.
	 * 
	 * @return Indicazione se l'id della richiesta debba essere inserito o meno nella busta di risposta
	 */
	public boolean isCorrelazioneRichiestaPresenteRispostaAsincronaSimmetrica();
	/**
	 * Ritorna l'indicazione se sulla busta di richiesta stato del profilo asincrono asimmetrico 
	 * debba essere inserito l'identificatore del messaggio della richiesta asincrona.
	 * 
	 * @return Indicazione se l'id della richiesta debba essere inserito o meno nella busta di richiesta stato
	 */
	public boolean isCorrelazioneRichiestaPresenteRichiestaStatoAsincronaAsimmetrica();
	
	/**
	 * Indicazione, per il profilo asincrono simmetrico,
	 * se deve essere riportato o meno nella busta di richiesta il servizio correlato dove deve essere inviata la risposta
	 * 
	 * @return indicazione se il servizio correlato deve essere indicato o meno nella busta
	 */
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoSimmetrico();	
	/**
	 * Indicazione, per il profilo asincrono asimmetrico,
	 * se deve essere riportato o meno nella busta di ricevuta alla richiesta il servizio correlato dove deve essere effettuato il polling
	 * 
	 * @return indicazione se il servizio correlato deve essere indicato o meno nella busta
	 */
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico();
	
	/**
	 * Ritorna l'identificatore da utilizzare per la correlazione asincrona scelto tra i valori della busta
	 * 
	 * @param richiesta Busta
	 * @return identificatore da utilizzare per la correlazione asincrona
	 */
	public String getIdCorrelazioneAsincrona(Busta richiesta);
	
	/**
	 * Ritorna l'identificatore da utilizzare per la correlazione asincrona scelto tra i due parametri
	 * 
	 * @param rifMsg riferimento messaggio 
	 * @param collaborazione id collaborazione
	 * @return identificatore da utilizzare per la correlazione asincrona
	 */
	public String getIdCorrelazioneAsincrona(String rifMsg,String collaborazione);
	
	
	

	
	
	/* *********** ECCEZIONI ******************* */

	/**
	 * Indicazione se devono essere gestite eccezioni di livello INFO
	 * 
	 * @return Indicazione se devono essere gestite eccezioni di livello INFO
	 */
	public boolean isEccezioniLivelloInfoAbilitato();

	
	/**
	 * Indicazione se devono essere ignorate eventuali eccezioni di livello diverso da GRAVE
	 * 
	 * @return Indicazione se devono essere ignorate eventuali eccezioni di livello diverso da GRAVE
	 */
	public boolean isIgnoraEccezioniLivelloNonGrave();
	
	
	
	
	
	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	/**
	 * Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 */
	public boolean isGenerazioneBusteErrore_strutturaMalformataHeaderProtocollo();
		
	/**
	 * Indicazione se in caso di messaggio con profilo oneway duplicato, debba essere generato un errore di protocollo o meno
	 * 
	 * @return Indicazione se in caso di messaggio con profilo oneway duplicato, debba essere generato un errore di protocollo o meno
	 */
	public boolean isGenerazioneErroreMessaggioOnewayDuplicato();

	
	
	
	
	
	/* *********** ALTRO ******************* */
    
	/**
	 * Indicazione se l'indirizzo presente nel soggetto della busta deve essere interpretato dalla Porta di Dominio
	 * 
	 * @return Indicazione se l'indirizzo presente nel soggetto della busta  deve essere interpretato dalla Porta di Dominio
	 */
	public boolean isUtilizzoIndirizzoSoggettoPresenteBusta();
	
}
