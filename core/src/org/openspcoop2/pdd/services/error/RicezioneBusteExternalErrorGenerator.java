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
package org.openspcoop2.pdd.services.error;

import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.config.IntegrationErrorReturnConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.builder.ImbustamentoErrore;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
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
	
	private IState state;
	public void updateState(IState state) {
		this.state = state;
	}

	public RicezioneBusteExternalErrorGenerator(Logger log, String idModulo, RequestInfo requestInfo, IState state) throws ProtocolException{
		
		super(log, idModulo, requestInfo, TipoPdD.APPLICATIVA, false);

		this.state = state;
		
		this.imbustamentoErrore = new ImbustamentoErrore(this.log, this.protocolFactory, this.state, this.serviceBinding,
				this.openspcoopProperties.getProprietaGestioneErrorePD(this.protocolFactory.createProtocolManager()).getFaultActor(),
				requestInfo.getIdTransazione());
		
		this.forceSoapPrefixCompatibilitOpenSPCoopV1 = this.openspcoopProperties.isForceSoapPrefixCompatibilitaOpenSPCoopV1();

	}
	
	public OpenSPCoop2Message buildErroreProcessamento(Context context, IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione) {
		return this.buildErroreProcessamento(context, integrationFunctionError, erroreIntegrazione, null);
	}
	public OpenSPCoop2Message buildErroreProcessamento(Context context, IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione, Throwable eProcessamento) {
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_processamento(this.identitaPdD,this.tipoPdD,this.idModulo, 
					erroreIntegrazione,eProcessamento,
					msgTypeErrorResponse,rfc7807, returnConfig, integrationFunctionError, this.getInterfaceName(), 
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					context);	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse,useProblemRFC7807, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreProcessamento(Context context, IntegrationFunctionError integrationFunctionError, DettaglioEccezione dettaglioEccezione){	
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_processamento(dettaglioEccezione,
					this.protocolFactory.createProtocolManager().isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento(),
					msgTypeErrorResponse,rfc7807, returnConfig, integrationFunctionError, this.getInterfaceName(),
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					context);
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreIntestazione(Context context, IntegrationFunctionError integrationFunctionError)  {

		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			ErroreIntegrazione erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreIntegrazione();
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_intestazione(
					this.identitaPdD,this.tipoPdD,this.idModulo, 
					erroreIntegrazione, msgTypeErrorResponse,rfc7807, returnConfig, integrationFunctionError, this.getInterfaceName(),
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					context);	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreIntestazione(Context context, IntegrationFunctionError integrationFunctionError, ErroreIntegrazione erroreIntegrazione) {
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_intestazione(this.identitaPdD,this.tipoPdD,this.idModulo, 
					erroreIntegrazione,msgTypeErrorResponse,rfc7807, returnConfig, integrationFunctionError, this.getInterfaceName(),
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					context);	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
	}
	public OpenSPCoop2Message buildErroreIntestazione(Context context, IntegrationFunctionError integrationFunctionError, CodiceErroreCooperazione codiceErroreCooperazione, String descrizione) {
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.buildFaultProtocollo_intestazione(this.identitaPdD,this.tipoPdD,this.idModulo, 
					codiceErroreCooperazione,descrizione,
					msgTypeErrorResponse,rfc7807, returnConfig, integrationFunctionError, this.getInterfaceName(),
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					context);	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
	}
	
	
	
	public OpenSPCoop2Message buildErroreProtocollo_Processamento(IntegrationFunctionError integrationFunctionError,
			Busta busta,Integrazione integrazione, String idTransazione,
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,Exception eProcessamento,
			PdDContext pddContext){
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			Transaction transactionNullable = null;
			try{
				transactionNullable = TransactionContext.getTransaction(idTransazione);
			}catch(Exception e){
				// puo' non essere presente in comunicazioni stateful
			}
			
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Processamento(this.identitaPdD,this.tipoPdD,pddContext,
					this.idModulo, 
					busta, integrazione, idTransazione, errori,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					eProcessamento, msgTypeErrorResponse, rfc7807, returnConfig, integrationFunctionError, 
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null);	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
		
	}
	
	public OpenSPCoop2Message buildErroreProtocollo_Processamento(IntegrationFunctionError integrationFunctionError,
			Busta busta,Integrazione integrazione, String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento,
			PdDContext pddContext){ 
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			Transaction transactionNullable = null;
			try{
				transactionNullable = TransactionContext.getTransaction(idTransazione);
			}catch(Exception e){
				// puo' non essere presente in comunicazioni stateful
			}
			
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Processamento(this.identitaPdD,this.tipoPdD,pddContext,
					this.idModulo, 
					busta, integrazione, idTransazione, erroreCooperazione,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					eProcessamento, msgTypeErrorResponse, rfc7807, returnConfig, integrationFunctionError, 
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null);
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
		
	}
	
	public OpenSPCoop2Message buildErroreProtocollo_Processamento(IntegrationFunctionError integrationFunctionError,
			Busta busta,Integrazione integrazione, String idTransazione,
			ErroreIntegrazione erroreIntegrazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			Exception eProcessamento,
			PdDContext pddContext){ 
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			Transaction transactionNullable = null;
			try{
				transactionNullable = TransactionContext.getTransaction(idTransazione);
			}catch(Exception e){
				// puo' non essere presente in comunicazioni stateful
			}
			
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Processamento(this.identitaPdD,this.tipoPdD,pddContext,
					this.idModulo, 
					busta, integrazione, idTransazione, erroreIntegrazione,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					eProcessamento, msgTypeErrorResponse, rfc7807, returnConfig, integrationFunctionError, 
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null);
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
		
	}
	
	public  OpenSPCoop2Message buildErroreProtocollo_Intestazione(IntegrationFunctionError integrationFunctionError,
			Busta busta,Integrazione integrazione, String idTransazione,		
			List<Eccezione> errori,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext,long attesaAttiva,int checkInterval,String profiloGestione,
			TipoOraRegistrazione tipoTempo,boolean generazioneListaTrasmissioni,
			PdDContext pddContext){
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			Transaction transactionNullable = null;
			try{
				transactionNullable = TransactionContext.getTransaction(idTransazione);
			}catch(Exception e){
				// puo' non essere presente in comunicazioni stateful
			}
			
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Intestazione(this.identitaPdD,this.tipoPdD,pddContext,
					this.idModulo, 
					busta, integrazione, idTransazione, errori,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					msgTypeErrorResponse, rfc7807, returnConfig, integrationFunctionError, 
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null);
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
	}
	
	public OpenSPCoop2Message buildErroreProtocollo_Intestazione(IntegrationFunctionError integrationFunctionError,
			Busta busta,Integrazione integrazione, 
			String idTransazione,
			ErroreCooperazione erroreCooperazione,
			java.util.Hashtable<String,Object> messageSecurityPropertiesResponse,
			MessageSecurityContext messageSecurityContext, 
			long attesaAttiva, 
			int checkInterval, 
			String profiloGestione,
			TipoOraRegistrazione tipoTempo, boolean generazioneListaTrasmissioni,
			PdDContext pddContext){
		
		IntegrationError integrationError = convertToIntegrationError(integrationFunctionError);
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		ConfigurationRFC7807 rfc7807 = this.getRfc7807ForErrorSafeMode(integrationError);
		boolean useProblemRFC7807 = rfc7807!=null;
		try{		
			Transaction transactionNullable = null;
			try{
				transactionNullable = TransactionContext.getTransaction(idTransazione);
			}catch(Exception e){
				// puo' non essere presente in comunicazioni stateful
			}
			
			IntegrationErrorReturnConfiguration returnConfig = this.getReturnConfigForError(integrationError);
			boolean useInternalFault = this.isUseInternalFault(integrationError);
			OpenSPCoop2Message msg = this.imbustamentoErrore.msgErroreProtocollo_Intestazione(this.identitaPdD,this.tipoPdD,pddContext,
					this.idModulo, 
					busta, integrazione, idTransazione, erroreCooperazione,
					messageSecurityPropertiesResponse, messageSecurityContext,
					attesaAttiva, checkInterval, profiloGestione,
					tipoTempo, generazioneListaTrasmissioni,
					msgTypeErrorResponse, rfc7807, returnConfig, integrationFunctionError, 
					this.forceSoapPrefixCompatibilitOpenSPCoopV1, useInternalFault,
					transactionNullable!=null ? transactionNullable.getTempiElaborazione() : null);	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(msgTypeErrorResponse, useProblemRFC7807, e);
		}
		
	}
	
}
