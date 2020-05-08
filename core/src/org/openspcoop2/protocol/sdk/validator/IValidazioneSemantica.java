/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.validator;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.utils.digest.IDigestReader;


/**
 * I messaggi ricevuti dalla Porta di Dominio dalle
 * altre porte sono arricchiti con le informazioni di cooperazione
 * secondo le specifiche del protocollo. 
 * <p>
 * Questa classe verifica che
 * i valori assunti dai dati di cooperazione siano coerenti alla
 * configurazione del Registro dei Servizi.  
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IValidazioneSemantica extends IComponentFactory {

	public void setContext(Context context);

	/**
	 * Verifica che l'identificativo del messaggio rispetti le specifiche di protocollo.
	 * <p>
	 * Il codice Amministrazione contiene l'identificativo parte del mittente, quando deve essere validato
	 * un identificativo del messaggio od una collaborazione di una richiesta 
	 * (oneWay, richiestaSincrona , richiesta/ricevutaRisposta AsincronaSimmetrica , 
	 *  richiesta/richiestaStato AsincronaAsimmetrica), poiche' e' lui che lo ha spedito 
	 * e che ha quindi creato l'identificatore.
	 * <p>
	 * Deve contenere invece il codice del destinatario, quando deve essere validato un RiferimentoMessaggio, od una
	 * collaborazione di una risposta (Sincrona , ricevutaRichiesta/Risposta AsincronaSimmetrica ,
	 * ricevutaRichiesta/ricevutaRichiestaStato AsincronaAsimmetrica),
	 * poiche' la creazione dell'identificatore venne fatta dal destinatario quando creo' la richiesta.
	 * 
	 * @param id Identificativo da validare
	 * @param dominio Dominio del soggetto che emette la busta.
	 * @param proprietaValidazione Contiene alcune indicazione sulla modalità di validazione del messaggio
	 * @return true se l'identificativo &egrave; valido
	 */
	public boolean validazioneID(String id, IDSoggetto dominio, ProprietaValidazione proprietaValidazione);

	/**
	 * Metodo che verifica la validit&agrave; semantica dei metadati di cooperazione,
	 * controllandone la compatibilit&agrave; con la configurazione del Registro dei Servizi.
	 * <p>
	 * I metadati sono raccolti in una Busta la busta ha un RuoloBusta che 
	 * pu&ograve; assumere questi valori
	 * <p>
	 * Per il profilo OneWay :
	 * <ul>
	 * <li> Richiesta
	 * </ul>
	 * Per il profilo Sincrono :
	 * <ul>
	 * <li> Richiesta
	 * <li> Risposta
	 * </ul>
	 * Per il profilo Asincrono :
	 * <ul>
	 * <li> Richiesta
	 * <li> Risposta
	 * <li> RicevutaRichiesta
	 * <li> RicevutaRisposta
	 * </ul>
	 * Altrimenti (&egrave; sempre cos&igrave; se non &egrave; presente il servizio):
	 * <ul>
	 * <li> BustaDiServizio
	 * </ul>
	 *
	 * @param msg Messaggio su cui effettuare la validazione semantica
	 * @param busta Busta con i dati di cooperazione da validare
	 * @param proprietaValidazione Contiene alcune indicazione sulla modalità di validazione del messaggio
	 * @param tipoBusta Ruolo della busta da validare
	 * @return ValidazioneSemanticaResult cotenente i risultati della validazione.
	 * @throws ProtocolException
	 */
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			ProprietaValidazione proprietaValidazione, 
			RuoloBusta tipoBusta) throws ProtocolException;
	
	/**
	 * Informazioni sulla sicurezza estratta dal messaggio
	 * 
	 * @param digestReader interfaccia da utilizzare per estrarre le informazioni sulla sicurezza
	 * @param msg Messaggio da analizzare
	 * @return Informazioni sulla sicurezza estratta dal messaggio
	 * @throws ProtocolException
	 */
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg) throws ProtocolException;
}
