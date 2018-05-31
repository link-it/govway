/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Hashtable;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * IEsitoBuilder.
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IEsitoBuilder extends IComponentFactory {
	
	/**
	 * Calcola l'esito della transazione utilizzando le informazioni fornite
	 * 
	 * @param transportRequestContext Informazioni di trasporto riguardanti la richiesta
	 * @param name Nome dell'esisto
	 * @return Esito della transazione
	 */
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, EsitoTransazioneName name); // non deve lanciare eccezione //throws ProtocolException;
	
	/**
	 * Calcola l'esito della transazione utilizzando le informazioni fornite
	 * 
	 * @param transportRequestContext Informazioni di trasporto riguardanti la richiesta
	 * @param message Messaggio di risposta
	 * @param informazioniErroriInfrastrutturali Eventuali errori infrastrutturali occorsi durante la gestione
	 * @param context Contesto della PdD arricchito durante la gestione del messaggio
	 * @return Esito della transazione
	 * @throws ProtocolException
	 */
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, 
			int returnCode, ServiceBinding serviceBinding,			
			OpenSPCoop2Message message,
			InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, Hashtable<String, Object> context) throws ProtocolException;
		
	/**
	 * Calcola l'esito della transazione utilizzando le informazioni fornite
	 * 
	 * @param transportRequestContext Informazioni di trasporto riguardanti la richiesta
	 * @param message Messaggio di risposta
	 * @param erroreApplicativo Indicazione sulla modalità di generazione di un errore applicativo
	 * @param informazioniErroriInfrastrutturali Eventuali errori infrastrutturali occorsi durante la gestione
	 * @param context Contesto della PdD arricchito durante la gestione del messaggio
	 * @return Esito della transazione
	 * @throws ProtocolException
	 */
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext,
			int returnCode, ServiceBinding serviceBinding,
			OpenSPCoop2Message message,
			ProprietaErroreApplicativo erroreApplicativo,
			InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, Hashtable<String, Object> context) throws ProtocolException;

}
