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


package org.openspcoop2.protocol.spcoop.testsuite.handler;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.handlers.OutResponseHandler;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutResponse implements OutResponseHandler {

	@Override
	public void invoke(OutResponseContext context) throws HandlerException {
		try{
			
			if(context==null){
				throw new HandlerException("context is null");
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
			// forzo a false il controllo di risposta vuota. La risposta vuota, casomai verrà fornita in PostOutHandler
			// Il motivo risiede nel fatto che un utente che implementa l'OutResponse può voler aggiungere qualcosa al messaggio.
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				Utilities.verificaMessaggioRisposta(context.getMessaggio(), false);
			}else{
				Utilities.verificaMessaggioRisposta(context.getMessaggio(), false);
			}
			
			// egov context
			if(test.getEgovContext()!=null){
				ProtocolContext egovContext = test.getEgovContext();
				// forzo a null il controllo sullo scenario
				// in questo handler non sono presenti
				String scenario = egovContext.getScenarioCooperazione();
				egovContext.setScenarioCooperazione(null);
				// verifica
				Utilities.verificaEGovContext(context.getProtocollo(),egovContext,true,context.getTipoPorta());		
				// ripristino valori
				egovContext.setScenarioCooperazione(scenario);			
			}
			
			// integrationContext
			Utilities.verificaIntegrationContext(test, context.getIntegrazione(), context.getTipoPorta());
			
		}catch(Exception e){
			throw new HandlerException(Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": "+e.getMessage(),e);
		}
	}

}
