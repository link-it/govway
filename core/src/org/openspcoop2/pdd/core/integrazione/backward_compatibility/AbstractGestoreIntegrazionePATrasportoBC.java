/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.InRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.InResponsePAMessage;
import org.openspcoop2.pdd.core.integrazione.OutRequestPAMessage;
import org.openspcoop2.pdd.core.integrazione.OutResponsePAMessage;
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
public abstract class AbstractGestoreIntegrazionePATrasportoBC extends AbstractCore implements IGestoreIntegrazionePA{

	/** Utility per l'integrazione */
	UtilitiesIntegrazioneBC utilitiesRequestBC = null;
	UtilitiesIntegrazioneBC utilitiesResponseBC = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	public AbstractGestoreIntegrazionePATrasportoBC(boolean openspcoop2, boolean x_prefix){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(AbstractGestoreIntegrazionePATrasportoBC.class);
		}
		try{
			this.utilitiesRequestBC = UtilitiesIntegrazioneBC.getInstancePARequest(this.log, openspcoop2, x_prefix);
			this.utilitiesResponseBC = UtilitiesIntegrazioneBC.getInstancePAResponse(this.log, openspcoop2, x_prefix);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException {
		try{
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesRequestBC.readTransportProperties(inRequestPAMessage.getUrlProtocolContext().getHeaders(), 
					integrazione, 
					protocollo);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
	}
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesRequestBC.setTransportProperties(integrazione, outRequestPAMessage.getHeaders(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPAMessage.getBustaRichiesta(), true, TipoIntegrazione.TRASPORTO),
					protocollo);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
	}
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		try{
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			this.utilitiesResponseBC.readTransportProperties(inResponsePAMessage.getHeaders(), integrazione,
					protocollo);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
	}
		
	// OUT - Response

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		
		try{
			String protocollo = this.getProtocolFactory()!=null ? this.getProtocolFactory().getProtocol() : null;
			
			// Vogliamo aggiungere solo informazioni sulla Pdd nella risposta verso la porta mittente.
			// Per questo non forniamo integrazione.
			// Per il protocollo trasparente vien apoosta definita la classe WithResponseOut che estende questa,
			// ridefinisce questo metodo e fornisce anche integrazione.
			this.utilitiesResponseBC.setTransportProperties(null, outResponsePAMessage.getHeaders(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePAMessage.getBustaRichiesta(), false, TipoIntegrazione.TRASPORTO),
					protocollo);
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePATrasporto, "+e.getMessage(),e);
		}
		
	}
}