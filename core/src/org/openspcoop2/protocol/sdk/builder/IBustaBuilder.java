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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
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

public interface IBustaBuilder<BustaRawType> extends IComponentFactory {
	
	/**
	 * Metodo che si occupa di costruire una stringa formata da un identificativo
	 * conforme alla specifica del protocollo in uso.
	 *
	 * @param state Stato delle risorse utilizzate durante la gestione dalla PdD
	 * @param idSoggetto Soggetto che stà gestendo il messaggio
	 * @param idTransazione Identificativo della transazione in corso sulla PdD
	 * @param ruoloMessaggio Indicazione se l'identificativo deve essere generato per un messaggio di richiesta o risposta
	 * @return la stringa contenente l'identificativo secondo specifica.
	 * @throws ProtocolException
	 */

	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException;
	
	/**
	 * Se l'identificativo di protocollo contiene una data, il metodo la restituisce, altrimenti ritorna null.
	 * 
	 * @param id identificativo da cui leggere la data
	 * @return eventuale data presente all'interno dell'identificativo di protocollo
	 * @throws ProtocolException
	 */
	public Date extractDateFromID(String id) throws ProtocolException;
		
	/**
	 * Modifica il messaggio applicativo inserendo i metadati di cooperazione secondo le specifiche del protocollo in uso. 
	 *  
	 * @param state Stato delle risorse utilizzate durante la gestione dalla PdD
	 * @param msg Messaggio in cui inserire le informazioni di cooperazione.
	 * @param busta Busta contenente i metadati di cooperazione da convertire in informazione raw del protocollo
	 * @param ruoloMessaggio Indicazione se la busta appartiene ad un messaggio di richiesta o di risposta
	 * @param proprietaManifestAttachments Propriet&agrave; necessarie per la generazione del manifest degli attachments (se presenti e la funzionalità è abilitata e supportata dal protocollo)
	 * @return Oggetto che contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo)
	 * @throws ProtocolException
	 */
	
	public BustaRawContent<BustaRawType> imbustamento(IState state, OpenSPCoop2Message msg, Busta busta, RuoloMessaggio ruoloMessaggio, 
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException;

	/**
	 * Modifica il messaggio di cooperazione (gi&agrave; imbustato) aggiungendo le informazioni di trasmissione.
	 * 
	 * @param message Messaggio di cooperazione in cui inserire le informazioni di trasmissione.
	 * @param trasmissione Trasmissione da aggiungere
	 * @return Oggetto che contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo)
	 * @throws ProtocolException
	 */
	public BustaRawContent<BustaRawType> addTrasmissione(OpenSPCoop2Message message, Trasmissione trasmissione) throws ProtocolException;

	/**
	 * Rimuove le informazioni di cooperazione dal messaggio. 
	 * 
	 * @param state Stato delle risorse utilizzate durante la gestione dalla PdD
	 * @param msg Messaggio da cui devono essere estratte le informazioni di cooperazione.
	 * @param busta Busta contenente i metadati di cooperazione
	 * @param ruoloMessaggio Indicazione se la busta appartiene ad un messaggio di richiesta o di risposta
	 * @param proprietaManifestAttachments Propriet&agrave; necessarie per la gestione del manifest degli attachments (se la funzionalità è abilitata e supportata dal protocollo)
	 */
	public BustaRawContent<BustaRawType> sbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio, ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException;
}





