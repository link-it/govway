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


package org.openspcoop2.protocol.spcoop.testsuite.handler;

import java.util.Map;

import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.handlers.InRequestHandler;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequest implements InRequestHandler {

	@Override
	public void invoke(InRequestContext context) throws HandlerException {
		
		try{
		
			if(context==null){
				throw new HandlerException("Context is null");
			}
			if(context.getLogCore()==null){
				throw new HandlerException("Logger is null");
			}
			if(context.getTipoPorta()==null){
				throw new HandlerException("TipoPdD is null");
			}
			if(context.getPddContext()==null){
				throw new HandlerException("PddContext is null");
			}
						
			if(context.getConnettore()!=null){
				
				if(!Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED.equals(context.getConnettore().getSoapAction())){
					return;
				}
				
				Map<String,String> p = context.getConnettore().getUrlProtocolContext().getParametersTrasporto();
				if(p!=null){
					TestContext testContext = new TestContext(context.getTipoPorta(),p);
					context.getPddContext().addObject(Costanti.TEST_CONTEXT, testContext);
					
					// Data Elaborazione
					Utilities.verificaData(testContext.getDataInizioTest(), context.getDataElaborazioneMessaggio());
					
					// Messaggio/Dimensione
					Utilities.verificaMessaggioRichiesta(context.getMessaggio());
				}
			}
			
		}catch(Exception e){
			throw new HandlerException(Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": "+e.getMessage(),e);
		}
		
	}
	
}
