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

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;



/**
 * Classe utilizzata per avere una trasformazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDAutenticazione extends AbstractCore implements IGestoreIntegrazionePD{

	private boolean rest = true;
	private boolean soap = true;
	
	protected GestoreIntegrazionePDAutenticazione( boolean rest, boolean soap ){
		this.rest = rest;
		this.soap = soap;
	}
	public GestoreIntegrazionePDAutenticazione(){
	}
	

	// IN - Request

	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException{
		// nop
	}

	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
		
		boolean process = false;
		if(outRequestPDMessage!=null && outRequestPDMessage.getMessage()!=null && outRequestPDMessage.getMessage().getServiceBinding()!=null) {
			if(ServiceBinding.REST.equals(outRequestPDMessage.getMessage().getServiceBinding())) {
				process = this.rest;
			}
			else {
				process = this.soap;
			}
		}
		
		if(process) {
			UtilitiesAutenticazione utilities = new UtilitiesAutenticazione(integrazione, outRequestPDMessage, 
					this.getPddContext(), OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			utilities.process();
		}
	}
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException{
		// nop
	}
		
	// OUT - Response

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePDMessage outResponsePDMessage) throws HeaderIntegrazioneException{
		// nop
	}
}