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

package org.openspcoop2.protocol.sdk.validator;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * La Porta di Dominio verifica che le informazioni di cooperazione
 * contenute nei messaggi scambiati con le altre porte, siano sintatticamente
 * corrette e le raccoglie per future operazioni.
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IValidazioneSintattica<BustaRawType> extends IComponentFactory {

	/**
	 * Esegue la validazione sintattica delle informazioni di cooperazione
	 * 
	 * @param state Stato delle risorse utilizzate durante la gestione della richiesta dalla PdD
	 * @param msg Messaggio da validare
	 * @param datiBustaLettiURLMappingProperties Eventuale busta costruita con i parametri letti tramite url Mapping Properties (Porta Applicativa)
	 * @param proprietaValidazioneErrori Contiene alcune indicazione sulla modalità di validazione del messaggio
	 * @return ValidazioneSintatticaResult con i risultati del processo di validazione
	 * @throws ProtocolException
	 */
	public ValidazioneSintatticaResult<BustaRawType> validaRichiesta(IState state, OpenSPCoop2Message msg, 
			Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException;
	
	/**
	 * Esegue la validazione sintattica delle informazioni di cooperazione
	 * 
	 * @param state Stato delle risorse utilizzate durante la gestione della risposta dalla PdD
	 * @param msg Messaggio da validare
	 * @param bustaRichiesta Busta di richiesta
	 * @param proprietaValidazioneErrori Contiene alcune indicazione sulla modalità di validazione del messaggio
	 * @return ValidazioneSintatticaResult con i risultati del processo di validazione
	 * @throws ProtocolException
	 */
	public ValidazioneSintatticaResult<BustaRawType> validaRisposta(IState state, OpenSPCoop2Message msg, 
			Busta bustaRichiesta, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException;
		
	/**
	 * Verifica se il messaggio che sta transitando presenta o meno le informazioni di cooperazione. 
	 * Deve rendere true se e solo se il messaggio ha le informazioni di cooperazione
	 * 
	 * @param tipoPdD Tipo della PdD
	 * @param profilo profilo di collaborazione
	 * @param isRichiesta indicazione se il messaggio da controllare e' una richiesta o risposta 
	 * @param msg Messaggio da controllare
	 * @return rende true se e' presente.
	 * @throws ProtocolException
	 */
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, boolean isRichiesta,
			OpenSPCoop2Message msg) throws ProtocolException;
	
	/**
	 * Metodo che si occupa di validare il Fault presente in un Messaggio di Cooperazione contenente un Errore.
	 *
	 * @param msg Messaggio contenente il Fault.
	 * @return ValidazioneSintatticaResult con i risultati del processo di validazione
	 */
	
	public ValidazioneSintatticaResult<BustaRawType> validazioneFault(OpenSPCoop2Message msg);
	
	/**
	 * Metodo che si occupa di eseguire la validazione del manifest quando gestito dalla Porta di Dominio.
	 * 
	 * @param msg Messaggio di Cooperazione contenente il manifest da validare
	 * @param proprietaManifestAttachments Propriet&agrave; del manifest che ne determinano la struttura.
	 * @return ValidazioneSintatticaResult con i risultati del processo di validazione
	 */
	public ValidazioneSintatticaResult<BustaRawType> validazioneManifestAttachments(OpenSPCoop2Message msg, 
			ProprietaManifestAttachments proprietaManifestAttachments);
	
	/**
	 * Questo metodo ritorna l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo)
	 * 
	 * @param msg Messaggio da cui estrarre le informazioni di cooperazione
	 * @return Oggetto che contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo)
	 * @throws ProtocolException
	 */
	public BustaRawContent<BustaRawType> getBustaRawContent_senzaControlli(OpenSPCoop2Message msg) throws ProtocolException;
	
	/**
	 * Questo metodo ritorna la busta che contiene le informazioni di cooperazione.
	 * 
	 * @param msg Messaggio da cui estrarre le informazioni di cooperazione
	 * @return Busta contenenete le informazioni di cooperazione
	 * @throws ProtocolException
	 */
	public Busta getBusta_senzaControlli(OpenSPCoop2Message msg) throws ProtocolException;
	
	
}