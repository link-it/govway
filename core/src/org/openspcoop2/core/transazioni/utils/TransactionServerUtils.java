/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.core.transazioni.utils;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
import org.openspcoop2.utils.date.DateManager;

/**     
 * TransactionServerUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionServerUtils {

	// *** SAVE ***
	
	public static void save(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam) throws Exception {
		
		String idTransazione = serverInfoParam.getIdTransazione();
		if(idTransazione==null) {
			throw new CoreException("IdTransazione non esistente nel contesto");
		}
		
		String servizioApplicativoErogatore = serverInfoParam.getServizioApplicativoErogatore();
		if(servizioApplicativoErogatore==null) {
			throw new CoreException("Id servizioApplicativoErogatore non esistente nel contesto");
		}
		
		String protocol = serverInfoParam.getProtocollo();
		if(protocol==null) {
			throw new CoreException("Protocollo non esistente nel contesto");
		}
		
		IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
		idTransazioneApplicativoServer.setIdTransazione(idTransazione);
		idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
		
		
		
		if(transazioneService.exists(idTransazioneApplicativoServer)) {
			
			TransazioneApplicativoServer transazioneApplicativoServer = transazioneService.get(idTransazioneApplicativoServer);
			
			// ** successivi invii **
			
			// id transazione, servizio applicativo erogatore e data registrazione non sono da aggiornare
						
			// protocollo
			transazioneApplicativoServer.setProtocollo(protocol);
			
			// esito
			transazioneApplicativoServer.setConsegnaSuccesso(serverInfoParam.isConsegnaSuccesso());
			transazioneApplicativoServer.setDettaglioEsito(serverInfoParam.getDettaglioEsito());
			
			// esito im
			transazioneApplicativoServer.setConsegnaIntegrationManager(serverInfoParam.isConsegnaIntegrationManager());
			
			// identificativo messaggio
			transazioneApplicativoServer.setIdentificativoMessaggio(serverInfoParam.getIdentificativoMessaggio());
			
			// date
			transazioneApplicativoServer.setDataAccettazioneRichiesta(serverInfoParam.getDataAccettazioneRichiesta());
			transazioneApplicativoServer.setDataUscitaRichiesta(serverInfoParam.getDataUscitaRichiesta());
			transazioneApplicativoServer.setDataAccettazioneRisposta(serverInfoParam.getDataAccettazioneRisposta());
			transazioneApplicativoServer.setDataIngressoRisposta(serverInfoParam.getDataIngressoRisposta());
			
			// dimensioni
			transazioneApplicativoServer.setRichiestaUscitaBytes(serverInfoParam.getRichiestaUscitaBytes());
			transazioneApplicativoServer.setRispostaIngressoBytes(serverInfoParam.getRispostaIngressoBytes());
		
			// location e codice risposta
			transazioneApplicativoServer.setLocationConnettore(serverInfoParam.getLocationConnettore());
			transazioneApplicativoServer.setCodiceRisposta(serverInfoParam.getCodiceRisposta());
			
			// fault
			transazioneApplicativoServer.setFault(serverInfoParam.getFault());
			transazioneApplicativoServer.setFormatoFault(serverInfoParam.getFormatoFault());
			
			// data primo tentativo
			if(transazioneApplicativoServer.getDataPrimoTentativo()==null) {
				if(serverInfoParam.getDataUscitaRichiesta()!=null) {
					transazioneApplicativoServer.setDataPrimoTentativo(serverInfoParam.getDataUscitaRichiesta()); // primo tentativo di consegna
				}
			}
			
			// numero tentativi
			transazioneApplicativoServer.setNumeroTentativi(transazioneApplicativoServer.getNumeroTentativi()+1);	

			// cluster id
			transazioneApplicativoServer.setClusterId(serverInfoParam.getClusterId());
			
			
			// aggiorno errore e fault se ho uno stato non ok
			
			if(!serverInfoParam.isConsegnaSuccesso()) {
				
				if(serverInfoParam.getDataUscitaRichiesta()!=null) {
					transazioneApplicativoServer.setDataUltimoErrore(serverInfoParam.getDataUscitaRichiesta());
				}
				else {
					transazioneApplicativoServer.setDataUltimoErrore(serverInfoParam.getDataAccettazioneRichiesta());
				}
				
				transazioneApplicativoServer.setDettaglioEsitoUltimoErrore(serverInfoParam.getDettaglioEsito());
				
				transazioneApplicativoServer.setCodiceRispostaUltimoErrore(serverInfoParam.getCodiceRisposta());
				
				transazioneApplicativoServer.setUltimoErrore(serverInfoParam.getUltimoErrore());
				
				transazioneApplicativoServer.setLocationUltimoErrore(serverInfoParam.getLocationConnettore());
				
				transazioneApplicativoServer.setClusterIdUltimoErrore(serverInfoParam.getClusterId());
								
				transazioneApplicativoServer.setFaultUltimoErrore(serverInfoParam.getFault());
				transazioneApplicativoServer.setFormatoFaultUltimoErrore(serverInfoParam.getFormatoFault());
			}
			
			// AGGIORNO
			transazioneService.update(idTransazioneApplicativoServer, transazioneApplicativoServer);
		}
		else {
			
			// ** primo invio **
			
			TransazioneApplicativoServer transazioneApplicativoServer = serverInfoParam;
			
			// data registrazione
			if(transazioneApplicativoServer.getDataRegistrazione()==null) {
				transazioneApplicativoServer.setDataRegistrazione(DateManager.getDate());
			}
			
			// data primo tentativo
			if(transazioneApplicativoServer.getDataUscitaRichiesta()!=null) {
				transazioneApplicativoServer.setDataPrimoTentativo(transazioneApplicativoServer.getDataUscitaRichiesta()); // primo tentativo di consegna
			}
			
			// numero tentativi
			transazioneApplicativoServer.setNumeroTentativi(1);
			
			// cluster id
			transazioneApplicativoServer.setClusterId(serverInfoParam.getClusterId());
							
			// CREO
			transazioneService.create(transazioneApplicativoServer);
		}
		
	}
	
}
