/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.spcoop.testsuite.handler;

import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.core.handlers.OutRequestHandler;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutRequest implements OutRequestHandler {

	@Override
	public void invoke(OutRequestContext context) throws HandlerException {
		
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
			
			// oggetto testsuite
			TestContext test = null;
			if(context.getPddContext()!=null){
				Object o = context.getPddContext().getObject(Costanti.TEST_CONTEXT);
				if(o!=null){
					if(o instanceof TestContext){
						test = (TestContext) o;
					}
				}
			}
			if(test==null){
				return;
			}
			
			if(test.isGeneraErroreVerificaInstallazioneArchivioTest()){
				return; // generato errore da handler in-egov
			}
			
			// Data Elaborazione
			Utilities.verificaData(test.getDataInizioTest(), context.getDataElaborazioneMessaggio());
			
			// Messaggio/Dimensione
			Utilities.verificaMessaggioRichiesta(context.getMessaggio());
			
			// check forward properties
			if(test.getForwardProperties()!=null){
				if(context.getConnettore()!=null && context.getConnettore().getPropertiesTrasporto()!=null){
					context.getConnettore().getPropertiesTrasporto().putAll(test.getForwardProperties());
				}
			}
			
			// location e tipo Connettore
			Utilities.verificaLocationTipoConnettore(context.getTipoPorta(), test.getLocationPD(), test.getLocationPA(),
					test.getTipoConnettorePD(), test.getTipoConnettorePA(), context.getConnettore());
			
			// egov context
			if(test.getEgovContext()!=null){
				ProtocolContext egovContext = test.getEgovContext();
				Utilities.verificaEGovContext(context.getProtocollo(),egovContext,false,context.getTipoPorta());				
			}
			
			// integrationContext
			Utilities.verificaIntegrationContext(test, context.getIntegrazione(), context.getTipoPorta());
			
		}catch(Exception e){
			throw new HandlerException(Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": "+e.getMessage(),e);
		}
		
	}

}
