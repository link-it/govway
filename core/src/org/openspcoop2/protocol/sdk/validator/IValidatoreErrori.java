/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IComponentFactory;


/**
 * Questa classe si occupa di verificare se il messaggio e' o meno di errore applicativo
 * o di cooperazione
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IValidatoreErrori extends IComponentFactory {

	/**
	 * Controlla se il messaggio fornito e' da considerarsi di errore
	 * 
	 * @param busta Informazioni di cooperazione
	 * @param msg Messaggio da analizzare
	 * @param proprietaValidazioneErrori Contiene alcune indicazione sulla modalità di validazione del messaggio
	 * @return true se il messaggio contiene un errore di protocollo
	 */
	public boolean isBustaErrore(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori);
	
	/**
	 * Controlla se il messaggio contiene un errore di processamento
	 * 
	 * @param busta Informazioni di cooperazione
	 * @param msg Messaggio da analizzare
	 * @param proprietaValidazioneErrori Contiene alcune indicazione sulla modalità di validazione del messaggio
	 * @return true se il messaggio è di tipologia errore di processamento
	 */
	public boolean isBustaErroreProcessamento(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori);
	
	/**
	 * Controlla se il messaggio fornito contiene un errore di intestazione
	 * 
	 * @param busta Informazioni di cooperazione
	 * @param msg Messaggio da analizzare
	 * @param proprietaValidazioneErrori Contiene alcune indicazione sulla modalità di validazione del messaggio 
	 * @return true se il messaggio è di tipologia errore di intestazione
	 */
	public boolean isBustaErroreIntestazione(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori);
}
