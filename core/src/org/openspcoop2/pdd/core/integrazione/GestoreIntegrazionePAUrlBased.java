/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

package org.openspcoop2.pdd.core.integrazione;

import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;




/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePAUrlBased extends AbstractCore implements IGestoreIntegrazionePA{

	/** Utility per l'integrazione */
	UtilitiesIntegrazione utilities = UtilitiesIntegrazione.getInstance();
	
	
	// IN - Request
	
	@Override
	public void readInRequestHeader(HeaderIntegrazione integrazione,
			InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException {
		try{
			this.utilities.readUrlProperties(inRequestPAMessage.getUrlProtocolContext().getParametersFormBased(), 
					integrazione);	
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePAUrlBased, "+e.getMessage(),e);
		}
	}
	
	// OUT - Request
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPAMessage outRequestPAMessage) throws HeaderIntegrazioneException{
		try{
			this.utilities.setRequestUrlProperties(integrazione, outRequestPAMessage.getProprietaUrlBased(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPAMessage.getBustaRichiesta(), true, TipoIntegrazione.URL));			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePAUrlBased, "+e.getMessage(),e);
		}
	}
	
	// IN - Response
	
	@Override
	public void readInResponseHeader(HeaderIntegrazione integrazione,
			InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException{
		// NOP
		// Non esiste un header di integrazione basato sulla url per la risposta
	}

	// OUT - Response

	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		
		// nop;
		
	}
}