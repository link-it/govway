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
import org.openspcoop2.utils.LoggerWrapperFactory;




/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AbstractGestoreIntegrazionePAUrlBasedWithOnlyReadBC extends AbstractCore implements IGestoreIntegrazionePA{

	/** Utility per l'integrazione */
	UtilitiesIntegrazioneBC utilities = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	public AbstractGestoreIntegrazionePAUrlBasedWithOnlyReadBC(boolean openspcoop2){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(AbstractGestoreIntegrazionePAUrlBasedWithOnlyReadBC.class);
		}
		try{
			this.utilities = UtilitiesIntegrazioneBC.getInstancePARequest(this.log, openspcoop2, false);
		}catch(Exception e){
			this.log.error("Errore durante l'inizializzazione delle UtilitiesIntegrazione: "+e.getMessage(),e);
		}
	}
	
	
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
		
		// nop;
		
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