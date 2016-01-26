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

package org.openspcoop2.protocol.sdk.validator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.state.IState;
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

public interface IValidazioneSemantica {

	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
	public IProtocolFactory getProtocolFactory();

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
	 * @param proprietaValidazione Indica il grado di validazione
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
	 * @param busta Busta con i dati di cooperazione da validare
	 * @param state Rappresentazione dello stato della busta
	 * @param proprietaValidazione Contiene le informazioni sul tipo di validazione da effettuare
	 * @param tipoBusta Ruolo della busta da validare
	 * @return ValidazioneSemanticaResult cotenente i risultati della validazione.
	 * @throws ProtocolException
	 */
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			IState state, ProprietaValidazione proprietaValidazione, 
			RuoloBusta tipoBusta) throws ProtocolException;
	
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg,SOAPEnvelope soapEnvelope,SOAPElement protocolHeader) throws ProtocolException;
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg) throws ProtocolException;
}
