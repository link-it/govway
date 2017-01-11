/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
package org.openspcoop2.pdd.services.error;

import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.protocol.engine.builder.ImbustamentoErrore;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.slf4j.Logger;

/**
 * RicezioneBusteExternalErrorGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteExternalErrorGenerator extends AbstractErrorGenerator {

	private ImbustamentoErrore imbustamentoErrore;
	public ImbustamentoErrore getImbustamentoErrore() {
		return this.imbustamentoErrore;
	}

	private boolean forceSoapPrefixCompatibilitOpenSPCoopV1 = false;
	
	public RicezioneBusteExternalErrorGenerator(Logger log, String idModulo, RequestInfo requestInfo) throws ProtocolException{
		
		super(log, idModulo, requestInfo, TipoPdD.APPLICATIVA, false);
			
		this.imbustamentoErrore = new ImbustamentoErrore(this.log, this.protocolFactory, this.serviceBinding);
		
		this.forceSoapPrefixCompatibilitOpenSPCoopV1 = this.openspcoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1();
	}
	
	public OpenSPCoop2Message buildErroreProcessamento(IntegrationError integrationError, ErroreIntegrazione erroreIntegrazione) {
		return this.buildErroreProcessamento(integrationError, erroreIntegrazione, null);
	}
	public OpenSPCoop2Message buildErroreProcessamento(IntegrationError integrationError, ErroreIntegrazione erroreIntegrazione, Throwable eProcessamento) {
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_processamento(this.identitaPdD,this.tipoPdD,this.idModulo, 
					erroreIntegrazione,eProcessamento,msgTypeErrorResponse,this.forceSoapPrefixCompatibilitOpenSPCoopV1);			
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreProcessamento(IntegrationError integrationError, DettaglioEccezione dettaglioEccezione){	
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_processamento(dettaglioEccezione,
					this.protocolFactory.createProtocolManager().isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(),
					msgTypeErrorResponse,this.forceSoapPrefixCompatibilitOpenSPCoopV1);			
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreIntestazione(IntegrationError integrationError)  {
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_intestazione(msgTypeErrorResponse,this.forceSoapPrefixCompatibilitOpenSPCoopV1);			
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreIntestazione(IntegrationError integrationError, ErroreIntegrazione erroreIntegrazione) {
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_intestazione(this.identitaPdD,this.tipoPdD,this.idModulo, 
					erroreIntegrazione,msgTypeErrorResponse,this.forceSoapPrefixCompatibilitOpenSPCoopV1);			
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	public OpenSPCoop2Message buildErroreIntestazione(IntegrationError integrationError, CodiceErroreCooperazione codiceErroreCooperazione, String descrizione) {
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_intestazione(this.identitaPdD,this.tipoPdD,this.idModulo, 
					codiceErroreCooperazione,descrizione,msgTypeErrorResponse,this.forceSoapPrefixCompatibilitOpenSPCoopV1);			
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	
	
	public OpenSPCoop2Message buildErroreProtocollo_Processamento(IntegrationError integrationError,
			IState state, Busta busta,Integrazione integrazione, String idTransazione,
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,Exception eProcessamento){
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Processamento(state,this.identitaPdD,this.tipoPdD,this.idModulo, 
					busta, integrazione, idTransazione, errori,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					eProcessamento, msgTypeErrorResponse, this.forceSoapPrefixCompatibilitOpenSPCoopV1);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
		
	}
	
	public OpenSPCoop2Message buildErroreProtocollo_Processamento(IntegrationError integrationError,
			IState state, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento){ 
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Processamento(state,this.identitaPdD,this.tipoPdD,this.idModulo, 
					busta, integrazione, idTransazione, erroreCooperazione,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					eProcessamento, msgTypeErrorResponse, this.forceSoapPrefixCompatibilitOpenSPCoopV1);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
		
	}
	
	public OpenSPCoop2Message buildErroreProtocollo_Processamento(IntegrationError integrationError,
			IState state, Busta busta,Integrazione integrazione, String idTransazione,
			ErroreIntegrazione erroreIntegrazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento){ 
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Processamento(state,this.identitaPdD,this.tipoPdD,this.idModulo, 
					busta, integrazione, idTransazione, erroreIntegrazione,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					eProcessamento, msgTypeErrorResponse, this.forceSoapPrefixCompatibilitOpenSPCoopV1);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
		
	}
	
	public  OpenSPCoop2Message buildErroreProtocollo_Intestazione(IntegrationError integrationError,
			IState state, Busta busta,Integrazione integrazione, String idTransazione,		
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni){
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Intestazione(state,this.identitaPdD,this.tipoPdD,this.idModulo, 
					busta, integrazione, idTransazione, errori,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					msgTypeErrorResponse, this.forceSoapPrefixCompatibilitOpenSPCoopV1);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreProtocollo_Intestazione(IntegrationError integrationError,
			IState state, Busta busta,Integrazione integrazione, 
			String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, 
			long attesaAttiva, 
			int checkInterval, 
			String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni){
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{		
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Intestazione(state,this.identitaPdD,this.tipoPdD,this.idModulo, 
					busta, integrazione, idTransazione, erroreCooperazione,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					msgTypeErrorResponse, this.forceSoapPrefixCompatibilitOpenSPCoopV1);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
		
	}
	
}
