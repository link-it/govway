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

package org.openspcoop2.pdd.core.integrazione;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.LoggerWrapperFactory;



/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePATrasporto extends AbstractCore implements IGestoreIntegrazionePA{

	/** Utility per l'integrazione */
	UtilitiesIntegrazione utilitiesRequest = null;
	UtilitiesIntegrazione utilitiesResponse = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	public GestoreIntegrazionePATrasporto(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePATrasporto.class);
		}
		try{
			this.utilitiesRequest = UtilitiesIntegrazione.getInstancePARequest(this.log);
			this.utilitiesResponse = UtilitiesIntegrazione.getInstancePAResponse(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException {
		try{
			this.utilitiesRequest.readTransportProperties(inRequestPAMessage.getUrlProtocolContext().getHeaders(), 
					integrazione);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
	}
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			this.utilitiesRequest.setTransportProperties(integrazione, outRequestPAMessage.getHeaders(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPAMessage.getBustaRichiesta(), true, TipoIntegrazione.TRASPORTO));
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
	}
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			this.utilitiesResponse.readTransportProperties(inResponsePAMessage.getHeaders(), integrazione);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
	}
		
	// OUT - Response

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		
		try{
			// Vogliamo aggiungere solo informazioni sulla Pdd nella risposta verso la porta mittente.
			// Per questo non forniamo integrazione.
			// Per il protocollo trasparente vien apoosta definita la classe WithResponseOut che estende questa,
			// ridefinisce questo metodo e fornisce anche integrazione.
			this.utilitiesResponse.setTransportProperties(null, outResponsePAMessage.getHeaders(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePAMessage.getBustaRichiesta(), false, TipoIntegrazione.TRASPORTO));
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
		
	}
}