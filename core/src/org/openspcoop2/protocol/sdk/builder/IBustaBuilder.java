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



package org.openspcoop2.protocol.sdk.builder;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;


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
	 * @param idSoggetto Soggetto che stà gestendo il messaggio
	 * @param idTransazione Identificativo della transazione in corso sulla PdD
	 * @param ruoloMessaggio Indicazione se l'identificativo deve essere generato per un messaggio di richiesta o risposta
	 * @return la stringa contenente l'identificativo secondo specifica.
	 * @throws ProtocolException
	 */

	public String newID(IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException;
	
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
	 * @param msg Messaggio in cui inserire le informazioni di cooperazione.
	 * @param busta Busta contenente i metadati di cooperazione da convertire in informazione raw del protocollo
	 * @param bustaRichiesta Busta contenente i metadati di cooperazione della richiesta (presente solo nella risposta)
	 * @param ruoloMessaggio Indicazione se la busta appartiene ad un messaggio di richiesta o di risposta
	 * @param proprietaManifestAttachments Propriet&agrave; necessarie per la generazione del manifest degli attachments (se presenti e la funzionalità è abilitata e supportata dal protocollo)
	 * @param faseImbustamento Indicazione sul momento in cui viene invocato il metodo durante il trattamento del messaggio
	 * @return Oggetto che contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo) e l'eventuale messaggio modificato
	 * @throws ProtocolException
	 */
	
	public ProtocolMessage imbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta, Busta bustaRichiesta, 
			RuoloMessaggio ruoloMessaggio, 
			ProprietaManifestAttachments proprietaManifestAttachments,
			FaseImbustamento faseImbustamento) throws ProtocolException;

	/**
	 * Modifica il messaggio di cooperazione (gi&agrave; imbustato) aggiungendo le informazioni di trasmissione.
	 * 
	 * @param message Messaggio di cooperazione in cui inserire le informazioni di trasmissione.
	 * @param trasmissione Trasmissione da aggiungere
	 * @return Oggetto che contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo) e l'eventuale messaggio modificato
	 * @throws ProtocolException
	 */
	public ProtocolMessage addTrasmissione(OpenSPCoop2Message message, Trasmissione trasmissione,
			FaseImbustamento faseImbustamento) throws ProtocolException;

	/**
	 * Rimuove le informazioni di cooperazione dal messaggio. 
	 * 
	 * @param msg Messaggio da cui devono essere estratte le informazioni di cooperazione.
	 * @param busta Busta contenente i metadati di cooperazione
	 * @param ruoloMessaggio Indicazione se la busta appartiene ad un messaggio di richiesta o di risposta
	 * @param proprietaManifestAttachments Propriet&agrave; necessarie per la gestione del manifest degli attachments (se la funzionalità è abilitata e supportata dal protocollo)
	 * @param faseSbustamento Indicazione sul momento in cui viene invocato il metodo durante il trattamento del messaggio
	 * @return Oggetto che contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo) e l'eventuale messaggio modificato
	 * @throws ProtocolException
	 */
	public ProtocolMessage sbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta,
			RuoloMessaggio ruoloMessaggio, ProprietaManifestAttachments proprietaManifestAttachments,
			FaseSbustamento faseSbustamento, 
			ServiceBinding integrationServiceBinding, ServiceBindingConfiguration serviceBindingConfiguration) throws ProtocolException;
}





