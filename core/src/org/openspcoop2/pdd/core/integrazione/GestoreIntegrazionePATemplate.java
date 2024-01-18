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

package org.openspcoop2.pdd.core.integrazione;

import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;



/**
 * Classe utilizzata per avere una trasformazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePATemplate extends AbstractCore implements IGestoreIntegrazionePA{

	private boolean transformRequest = true;
	private boolean transformResponse = true;
	
	protected GestoreIntegrazionePATemplate( boolean transformRequest, boolean transformResponse ){
		this.transformRequest = transformRequest;
		this.transformResponse = transformResponse;
	}
	public GestoreIntegrazionePATemplate(){
	}
	
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException {
		// nop
	}
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		if(this.transformRequest) {
			UtilitiesTemplate utilities = new UtilitiesTemplate("template-request", integrazione, outRequestPAMessage, 
					this.getPddContext(), OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			utilities.process(true);
		}
	}
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		// nop
	}
		
	// OUT - Response

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		if(this.transformResponse) {
			UtilitiesTemplate utilities = new UtilitiesTemplate("template-response", integrazione, outResponsePAMessage, 
					this.getPddContext(), OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			utilities.process(false);
		}
	}
}