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

import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolHandler;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InProtocolRequest implements InRequestProtocolHandler {

	
	@Override
	public void invoke(InRequestProtocolContext context) throws HandlerException {
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
				throw new HandlerException("ERRORE RICHIESTO DA TESTSUITE HANDLER");
			}
			
			// Data Elaborazione
			Utilities.verificaData(test.getDataInizioTest(), context.getDataElaborazioneMessaggio());
			
			// Messaggio/Dimensione
			Utilities.verificaMessaggioRichiesta(context.getMessaggio());
			
			// egov context
			if(test.getEgovContext()!=null){
				ProtocolContext egovContext = test.getEgovContext();
				// forzo a null il controllo sullo scenario e su profilo di collaborazione
				// in questo handler non sono presenti
				ProfiloDiCollaborazione profiloCollaborazione = egovContext.getProfiloCollaborazione();
				String scenario = egovContext.getScenarioCooperazione();
				egovContext.setProfiloCollaborazione(null,null);
				egovContext.setScenarioCooperazione(null);
				// verifica
				Utilities.verificaEGovContext(context.getProtocollo(),egovContext,false,context.getTipoPorta());		
				// ripristino valori
				egovContext.setProfiloCollaborazione(profiloCollaborazione,egovContext.getProfiloCollaborazioneValue());
				egovContext.setScenarioCooperazione(scenario);
			}
			
			// integrationContext
			// forzo a null il controllo sullo stateless
			// in questo handler non sono presenti
			Boolean statelessPD = test.getStatelessPD();
			Boolean statelessPA = test.getStatelessPA();
			test.setStatelessPD(null);
			test.setStatelessPA(null);
			// verifica
			Utilities.verificaIntegrationContext(test, context.getIntegrazione(), context.getTipoPorta());
			// ripristino valori
			test.setStatelessPD(statelessPD);
			test.setStatelessPA(statelessPA);
			
		}catch(Exception e){
			throw new HandlerException(Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": "+e.getMessage(),e);
		}
	}
}
