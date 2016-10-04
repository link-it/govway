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
import org.openspcoop2.pdd.core.handlers.InResponseContext;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InResponse implements InResponseHandler {

	@Override
	public void invoke(InResponseContext context) throws HandlerException {
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
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				Utilities.verificaMessaggioRisposta(context.getMessaggio(), test.isRispostaVuotaPD_PA());
			}else{
				Utilities.verificaMessaggioRisposta(context.getMessaggio(), test.isRispostaVuotaPA_SA());
			}
			
			// return code
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				if(test.getReturnCodePDReq()==null){
					throw new HandlerException("ReturnCode da verificare is null ???");
				}
				if(context.getReturnCode() != test.getReturnCodePDReq()){
					throw new HandlerException("Return-code della Porta di Dominio ["+context.getReturnCode()+"] diverso da quello atteso per il test ["+test.getReturnCodePDReq()+"]");
				}
			}else{
				if(test.getReturnCodePAReq()==null){
					throw new HandlerException("ReturnCode da verificare is null ???");
				}
				if(context.getReturnCode() != test.getReturnCodePAReq()){
					throw new HandlerException("Return-code della Porta di Dominio ["+context.getReturnCode()+"] diverso da quello atteso per il test ["+test.getReturnCodePAReq()+"]");
				}
			}
			
			// location e tipo Connettore
			Utilities.verificaLocationTipoConnettore(context.getTipoPorta(), test.getLocationPD(), test.getLocationPA(),
					test.getTipoConnettorePD(), test.getTipoConnettorePA(), context.getConnettore());
			
			// egov context
			if(test.getEgovContext()!=null){
				ProtocolContext egovContext = test.getEgovContext();
				// L'id della risposta e' non e' presente in questo handler nell'InResponse
				// - ConsegnaContenutiApplicativi, poiche' l'id potrebbe cambiare in seguito ad un errore
				// - InoltroBusteEGov, la busta deve essere sbustata per sapere l'id
				Utilities.verificaEGovContext(context.getProtocollo(),egovContext,false,context.getTipoPorta());				
			}
			
			// integrationContext
			Utilities.verificaIntegrationContext(test, context.getIntegrazione(), context.getTipoPorta());
			
		}catch(Exception e){
			throw new HandlerException(Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": "+e.getMessage(),e);
		}
	}
	
}
