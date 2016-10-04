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

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseHandler;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponse implements PostOutResponseHandler {

	@Override
	public void invoke(PostOutResponseContext context) throws HandlerException {
		
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
			
			// Messaggio
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				Utilities.verificaMessaggioRisposta(context.getMessaggio(), test.isRispostaVuotaSA_PD());
			}else{
				Utilities.verificaMessaggioRisposta(context.getMessaggio(), test.isRispostaVuotaPA_PD());
			}
			
			// esito
			if(context.getEsito()==null){
				throw new HandlerException("Esito is null");
			}
			if(test.getEsito()==null){
				throw new HandlerException("Esito da verificare is null ???");
			}
			if(!context.getEsito().getName().equals(test.getEsito())){
				throw new HandlerException("Esito della Porta di Dominio ["+context.getEsito().getName().name()+"] diverso da quello atteso per il test ["+test.getEsito().name()+"]");
			}
			
			// Dimensioni
			Utilities.verificaLunghezza(context.getInputRequestMessageSize(), "InputRequestMessage");
			Utilities.verificaLunghezza(context.getOutputRequestMessageSize(), "OutputRequestMessage");
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				Utilities.verificaLunghezza(context.getInputResponseMessageSize(), "InputResponseMessage", !test.isRispostaVuotaPD_PA());	
				Utilities.verificaLunghezza(context.getOutputResponseMessageSize(), "OutputResponseMessage", !test.isRispostaVuotaSA_PD());
			}else{
				if(EsitoTransazioneName.OK.equals(test.getEsito()) && test.getEgovContext()!=null && org.openspcoop2.protocol.engine.constants.Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(test.getEgovContext().getScenarioCooperazione())){
					// Puo' essere o non essere presente la InputResponse a seconda se viene ritornato o meno un messaggio
				}else{
					Utilities.verificaLunghezza(context.getInputResponseMessageSize(), "InputResponseMessage" , !test.isRispostaVuotaPA_SA());
				}
				Utilities.verificaLunghezza(context.getOutputResponseMessageSize(), "OutputResponseMessage", !test.isRispostaVuotaPA_PD());
			}			
						
			// return code
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				if(test.getReturnCodePDRes()==null){
					throw new HandlerException("ReturnCode da verificare is null ???");
				}
				if(context.getReturnCode() != test.getReturnCodePDRes()){
					throw new HandlerException("Return-code della Porta di Dominio ["+context.getReturnCode()+"] diverso da quello atteso per il test ["+test.getReturnCodePDRes()+"]");
				}
			}else{
				if(test.getReturnCodePARes()==null){
					throw new HandlerException("ReturnCode da verificare is null ???");
				}
				if(context.getReturnCode() != test.getReturnCodePARes()){
					throw new HandlerException("Return-code della Porta di Dominio ["+context.getReturnCode()+"] diverso da quello atteso per il test ["+test.getReturnCodePARes()+"]");
				}
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
