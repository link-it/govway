/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione.backward_compatibility;

import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.InRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePDMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePDMessage;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;



/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractGestoreIntegrazionePDTrasportoBC extends AbstractCore implements IGestoreIntegrazionePD{

	
	/** Utility per l'integrazione */
	UtilitiesIntegrazioneBC utilitiesRequestBC = null;
	UtilitiesIntegrazioneBC utilitiesResponseBC = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	public AbstractGestoreIntegrazionePDTrasportoBC(boolean openspcoop2, boolean x_prefix){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(AbstractGestoreIntegrazionePDTrasportoBC.class);
		}
		try{
			this.utilitiesRequestBC = UtilitiesIntegrazioneBC.getInstancePDRequest(this.log, openspcoop2, x_prefix);
			this.utilitiesResponseBC = UtilitiesIntegrazioneBC.getInstancePDResponse(this.log, openspcoop2, x_prefix);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	

	// IN - Request

	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		try{
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesRequestBC.readTransportProperties(inRequestPDMessage.getUrlProtocolContext().getHeaders(), integrazione,
					protocollo);	
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
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesResponseBC.setTransportProperties(integrazione, outResponsePDMessage.getHeaders(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePDMessage.getBustaRichiesta(), false, TipoIntegrazione.TRASPORTO),
					protocollo);		
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDTrasporto, "+e.getMessage(),e);
		}
	}
}