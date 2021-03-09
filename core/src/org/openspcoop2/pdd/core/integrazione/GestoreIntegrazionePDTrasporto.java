/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDTrasporto extends AbstractCore implements IGestoreIntegrazionePD{

	
	/** Utility per l'integrazione */
	UtilitiesIntegrazione utilitiesRequest = null;
	UtilitiesIntegrazione utilitiesResponse = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	public GestoreIntegrazionePDTrasporto(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(GestoreIntegrazionePDTrasporto.class);
		}
		try{
			this.utilitiesRequest = UtilitiesIntegrazione.getInstancePDRequest(this.log);
			this.utilitiesResponse = UtilitiesIntegrazione.getInstancePDResponse(this.log);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	

	// IN - Request

	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		try{
			this.utilitiesRequest.readTransportProperties(inRequestPDMessage.getUrlProtocolContext().getHeaders(), integrazione);	
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDTrasporto, "+e.getMessage(),e);
		}
	}

	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
	
		// nop;
		
	}
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException{
		// NOP
		// Non esiste un header di integrazione basato sul trasporto sul canale
	}
		
	// OUT - Response

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePDMessage outResponsePDMessage) throws HeaderIntegrazioneException{
		try{
			this.utilitiesResponse.setTransportProperties(integrazione, outResponsePDMessage.getHeaders(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePDMessage.getBustaRichiesta(), false, TipoIntegrazione.TRASPORTO));		
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDTrasporto, "+e.getMessage(),e);
		}
	}
}