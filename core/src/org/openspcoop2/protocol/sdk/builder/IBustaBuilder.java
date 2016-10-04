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



package org.openspcoop2.protocol.sdk.builder;

import java.util.Date;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.state.IState;


/**
 * Le Porte di Dominio comunicano tra loro scambiandosi messaggi contenenti
 * informazioni di cooperazione. Le modalit&agrave; con cui la Porta di Dominio modifica i 
 * messaggi ricevuti dai servizi applicativi per inserire i dati di cooperazione, sono 
 * definite dal protocollo e gestite da questa classe.
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IBustaBuilder {

	public IProtocolFactory getProtocolFactory();
	
	/**
	 * Metodo che si occupa di costruire una stringa formata da un identificativo
	 * conforme alla specifica del protocollo in uso.
	 *
	 * @param idSoggetto Soggetto per cui stiamo gestendo il messaggio.
	 * @return la stringa contenente l'identificativo secondo specifica.
	 * @throws ProtocolException
	 */

	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta) throws ProtocolException;
	
	/**
	 * Se l'identificativo di protocollo contiene una data, il metodo la restituisce, altrimenti ritorna null.
	 * 
	 * @param id identificativo da cui leggere la data
	 * @return eventuale data presente all'interno dell'identificativo di protocollo
	 * @throws ProtocolException
	 */
	public Date extractDateFromID(String id) throws ProtocolException;
	
	/**
	 * Ritorna l'elemento che rappresenta la busta
	 * 
	 * @param busta Busta
	 * @return elemento che rappresenta la busta
	 * @throws ProtocolException
	 */
	public SOAPElement toElement(Busta busta,boolean isRichiesta) throws ProtocolException;
	
	/**
	 * Ritorna la rappresentazione in String della busta
	 * 
	 * @param busta Busta
	 * @return rappresentazione in String della busta
	 * @throws ProtocolException
	 */
	public String toString(Busta busta,boolean isRichiesta) throws ProtocolException;
	
	/**
	 * Ritorna la rappresentazione in byte[] della busta
	 * 
	 * @param busta Busta
	 * @return rappresentazione in byte[] della busta
	 * @throws ProtocolException
	 */
	public byte[] toByteArray(Busta busta,boolean isRichiesta) throws ProtocolException;
	
	/**
	 * Modifica il messaggio applicativo inserendo i metadati di cooperazione secondo
	 * le specifiche del protocollo in uso. 
	 *  
	 * @param msg Messaggio in cui inserire le informazioni di cooperazione.
	 * @param busta Busta contenente i metadati di cooperazione
	 * @param isRichiesta Indicazione se il messaggio da modificare &egrave; di richiesta o di risposta
	 * @param proprietaManifestAttachments Propriet&agrave; necessarie per la generazione del manifest degli attachments
	 * @return SOAPElement header della busta
	 * @throws ProtocolException
	 */
	
	public SOAPElement imbustamento(IState state, OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, 
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException;

	/**
	 * Modifica il messaggio di cooperazione (quindi gi&agrave; imbustato) aggiungendo le informazioni di trasmissione.
	 * 
	 * @param message Messaggio di cooperazione
	 * @param trasmissione Trasmissione da aggiungere
	 * @return SOAPElement header della busta
	 * @throws ProtocolException
	 */
	public SOAPElement addTrasmissione(OpenSPCoop2Message message, Trasmissione trasmissione) throws ProtocolException;

	/**
	 * Rimuove le informazioni di cooperazione dal messaggio. Il messaggio cosi ottenuto 
	 * sar&agrave; quello consegnato al servizio applicativo di destinazione.
	 * <p>
	 * L'elemento SOAP restituito dal metodo raccoglie i metadati di cooperazione.
	 *  
	 * @param msg Messaggio da cui devono essere estratte le informazioni di cooperazione.
	 * @param busta Busta contenente i metadati di cooperazione
	 * @param isRichiesta Indicazione se il messaggio da modificare &egrave; di richiesta o di risposta
	 * @param proprietaManifestAttachments Indicazioni per la generazione del manifest
	 */
	public SOAPElement sbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta, ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException;
}





