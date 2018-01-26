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



package org.openspcoop2.protocol.sdk.validator;

import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * Classe utilizzata per effettuare una validazione con schema xsd.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IValidazioneConSchema  extends IComponentFactory {

	/**
	 * Metodo che viene chiamato in fase di startup della PdD per l'eventuale inizializzazione di validatori
	 * 
	 * @return true se l'inizializzazione va a buon fine
	 */
	public boolean initialize();
		
	/**
	 * Effettua la validazione utilizzando gli schemi formali che definiscono i dati raw del protocollo
	 * 
	 * @param message Messaggio su cui effettuare la validazione
	 * @param isErroreProcessamento Indicazione se il messaggio da validare è un errore protocollo di processamento
	 * @param isErroreIntestazione Indicazione se il messaggio da validare è un errore protocollo di intestazione
	 * @param isMessaggioConAttachments Indicazione se il messaggio contiene attachments
	 * @param validazioneManifestAttachments Indicazione se deve essere attuata o meno la validazione del manifest degli attachments (se supportata dal protocollo)
	 * @throws ProtocolException
	 */
	public void valida(OpenSPCoop2Message message, boolean isErroreProcessamento, boolean isErroreIntestazione,
			boolean isMessaggioConAttachments, boolean validazioneManifestAttachments) throws ProtocolException;

	/**
	 * Ritorna eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	
	public List<Eccezione> getEccezioniValidazione();
	
	/**
	 * Ritorna eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public List<Eccezione> getEccezioniProcessamento();
	
}
