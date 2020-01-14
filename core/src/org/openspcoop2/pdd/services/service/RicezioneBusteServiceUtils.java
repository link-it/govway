/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.services.service;

import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.CachedConfigIntegrationReader;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.transazioni.PreInRequestHandler;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.core.RicezioneBusteContext;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaApplicativa;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * RicezioneBusteServiceUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteServiceUtils {

	
	public static ConnectorDispatcherInfo updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, PdDContext pddContextNullable) throws ConnectorException{
		
		URLProtocolContext protocolContext = requestInfo.getProtocolContext();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();

						
		
		
		IDPortaApplicativa idPA = null;
		try{
			idPA = serviceIdentificationReader.findPortaApplicativa(protocolContext, true);
		}catch(RegistryNotFound notFound){
			if(bindingConfig.existsContextUrlMapping()==false){
				logCore.error("Porta Applicativa non trovata: "+notFound.getMessage(),notFound);
				msgDiag.addKeywordErroreProcessamento(notFound);
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
				return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
						IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
			}
		}
		if(idPA!=null){
			return updatePortaApplicativaRequestInfo(requestInfo, logCore, 
					res, generatoreErrore, 
					serviceIdentificationReader, 
					msgDiag,
					protocolContext,idPA, pddContextNullable);
		}
		
		return null;
	}
	
	public static ConnectorDispatcherInfo updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, 
			URLProtocolContext protocolContext, IDPortaApplicativa idPA, PdDContext pddContextNullable) throws ConnectorException{
		return updatePortaApplicativaRequestInfo(requestInfo, logCore, 
				null, generatoreErrore, 
				serviceIdentificationReader, 
				msgDiag, 
				protocolContext, idPA, pddContextNullable);
	}
	private static ConnectorDispatcherInfo updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, 
			URLProtocolContext protocolContext, IDPortaApplicativa idPA, PdDContext pddContextNullable) throws ConnectorException{

			
		// da ora in avanti, avendo localizzato una PA, se avviene un errore genero l'errore stesso
		protocolContext.setInterfaceName(idPA.getNome());
		if(protocolContext.getFunctionParameters()==null) {
			protocolContext.setFunctionParameters(idPA.getNome());
		}
		
		IProtocolFactory<?> pf = requestInfo.getProtocolFactory();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
		ServiceBinding integrationServiceBinding = requestInfo.getIntegrationServiceBinding();
		ServiceBinding protocolServiceBinding = requestInfo.getProtocolServiceBinding();
		
		IDServizio idServizio = null;
		if(idPA.getIdentificativiErogazione()!=null){
			if(idPA.getIdentificativiErogazione().getIdServizio()!=null) {
				idServizio = idPA.getIdentificativiErogazione().getIdServizio().clone(); // effettuo clone altrimenti nella cache viene memorizzata l'azione impostata dopo!
			}
		}
		
		if(idServizio==null){
			try{
				idServizio = serviceIdentificationReader.convertToIDServizio(idPA);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Conversione Dati PortaDelegata in identificativo servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}
		}
		
		PortaApplicativa paDefault = null;
		if(idServizio!=null){
			
			CachedConfigIntegrationReader configIntegrationReader = (CachedConfigIntegrationReader) serviceIdentificationReader.getConfigIntegrationReader();
			IRegistryReader registryReader = serviceIdentificationReader.getRegistryReader();
						
			// provo a leggere anche l'azione
			// l'eventuale errore lo genero dopo
			try{
				idServizio.setAzione(configIntegrationReader.getAzione(configIntegrationReader.getPortaApplicativa(idPA), protocolContext, requestInfo.getProtocolFactory()));
			}catch(Exception e){
				logCore.debug("Azione non trovata: "+e.getMessage(),e);
			}
			
			// PortaApplicativa Default
			try{
				paDefault = ConfigurazionePdDManager.getInstance().getPortaApplicativa_SafeMethod(idPA);
			}catch(Exception e){
				logCore.debug("Recupero porta default fallita: "+e.getMessage(),e);
			}
			
			// Lettura eventuale MessageFactory da utilizzare
			try {
				if(paDefault!=null && paDefault.getOptions()!=null && !StringUtils.isEmpty(paDefault.getOptions())) {
					Hashtable<String, String> props = PropertiesSerializator.convertoFromDBColumnValue(paDefault.getOptions());
					if(props!=null && props.size()>0) {
						String msgFactory = props.get(CostantiPdD.OPTIONS_MESSAGE_FACTORY);
						if(msgFactory!=null) {
							requestInfo.setMessageFactory(msgFactory);
						}
					}
				}
			}catch(Throwable e){
				logCore.debug("Lettura Message Factory fallita: "+e.getMessage(),e);
			}
			
			// Lettura Azione
			try{
				if(idServizio.getAzione()!=null) {
					
					IdentificazionePortaApplicativa identificazione = new IdentificazionePortaApplicativa(logCore, pf, null, paDefault);
					if(identificazione.find(idServizio.getAzione())) {
						IDPortaApplicativa idPA_action = identificazione.getIDPortaApplicativa(idServizio.getAzione());
						if(idPA_action!=null) {
							
							if(pddContextNullable!=null) {
								pddContextNullable.addObject(CostantiPdD.NOME_PORTA_INVOCATA, idPA.getNome()); // prima di aggiornare la porta applicativa
							}
							
							msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA, idPA_action.getNome());
							msgDiag.updatePorta(idPA_action.getNome());
							protocolContext.setInterfaceName(idPA_action.getNome());
							idPA = idPA_action;
						}
					}
				}
			}catch(Exception e){
				logCore.debug("Gestione porta specifica per azione fallita: "+e.getMessage(),e);
			}
			
			// updateInformazioniCooperazione
			generatoreErrore.updateInformazioniCooperazione(null, idServizio);
			requestInfo.setIdServizio(idServizio);
			
			// Aggiorno service binding rispetto al servizio localizzato
			try{
				integrationServiceBinding = pf.createProtocolConfiguration().getIntegrationServiceBinding(idServizio, registryReader);
				requestInfo.setIntegrationServiceBinding(integrationServiceBinding);
				
				protocolServiceBinding = pf.createProtocolConfiguration().getProtocolServiceBinding(integrationServiceBinding, protocolContext);
				requestInfo.setProtocolServiceBinding(protocolServiceBinding);
				
				generatoreErrore.updateServiceBinding(protocolServiceBinding);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Lettura ServiceBinding fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Lettura ServiceBinding fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura ServiceBinding fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
			}
			
			// Aggiorno service binding configuration rispetto al servizio localizzato
			try{
				bindingConfig = pf.createProtocolConfiguration().getServiceBindingConfiguration(protocolContext, integrationServiceBinding, idServizio, registryReader);
				requestInfo.setBindingConfig(bindingConfig);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Lettura Configurazione Servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Lettura Configurazione Servizio fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura Configurazione Servizio fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
			}
			
			// Aggiorno message type
			try{
				MessageType requestMessageTypeIntegration = bindingConfig.getRequestMessageType(integrationServiceBinding, 
						protocolContext, protocolContext.getContentType());
				requestInfo.setIntegrationRequestMessageType(requestMessageTypeIntegration);
				
				MessageType requestMessageTypeProtocol = bindingConfig.getRequestMessageType(protocolServiceBinding, 
						protocolContext, protocolContext.getContentType());
				requestInfo.setProtocolRequestMessageType(requestMessageTypeProtocol);
				
				generatoreErrore.updateRequestMessageType(requestMessageTypeProtocol);
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Comprensione MessageType fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Comprensione MessageType fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
			}
			
			// Controllo Service Binding rispetto alla url e al tipo di servizio
			if(requestInfo.getProtocolContext()!=null && bindingConfig.isServiceBindingContextEnabled(protocolServiceBinding, requestInfo.getProtocolContext().getProtocolWebContext())==false) {
				if(res!=null) {
					String url = requestInfo.getProtocolContext().getRequestURI();
					logCore.error("L'API invocata possiede un service binding '"+protocolServiceBinding+"' non abilitato sul contesto utilizzato: "+url);
					msgDiag.logErroreGenerico("L'API invocata possiede un service binding '"+protocolServiceBinding+"' non abilitato sul contesto utilizzato", "CheckServiceBinding");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, 
							ErroriIntegrazione.ERRORE_447_API_NON_INVOCABILE_CONTESTO_UTILIZZATO.getErroreIntegrazione(),
							IntegrationError.BAD_REQUEST, null, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}
			if(bindingConfig.isServiceBindingServiceTypeEnabled(protocolServiceBinding, idServizio.getTipo())==false) {
				if(res!=null) {
					logCore.error("L'API invocata possiede un service binding '"+protocolServiceBinding+"' non abilitato per il tipo di servizio '"+idServizio.getTipo()+"'");
					msgDiag.logErroreGenerico("L'API invocata possiede un service binding '"+protocolServiceBinding+"' non abilitato per il tipo di servizio '"+idServizio.getTipo(), "CheckServiceBinding");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, 
							ErroriIntegrazione.ERRORE_448_API_NON_INVOCABILE_TIPO_SERVIZIO_UTILIZZATO.getErroreIntegrazione(),
							IntegrationError.BAD_REQUEST, null, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
				return null;
			}
		}
	
		if(requestInfo!=null && requestInfo.getProtocolServiceBinding()!=null && 
				ServiceBinding.SOAP.equals(requestInfo.getProtocolServiceBinding()) && 
				requestInfo.getProtocolContext()!=null &&
				HttpRequestMethod.OPTIONS.name().equalsIgnoreCase(requestInfo.getProtocolContext().getRequestType())) {
						
			// Gestione CORS
			try{
				CorsConfigurazione cors = null;
				HttpServletRequest httpServletRequest = null;
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
					httpServletRequest = requestInfo.getProtocolContext().getHttpServletRequest();	
				}
				
				if(httpServletRequest!=null) {
					ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
					if(paDefault!=null) {
						cors = configurazionePdDManager.getConfigurazioneCORS(paDefault);
					}
					else {
						cors = configurazionePdDManager.getConfigurazioneCORS();
					}
				}
				else {
					cors = new CorsConfigurazione();
					cors.setStato(StatoFunzionalita.DISABILITATO);
				}
				
				if(StatoFunzionalita.ABILITATO.equals(cors.getStato())) {
					if(TipoGestioneCORS.GATEWAY.equals(cors.getTipo())) {
						
						/*
						
						org.openspcoop2.pdd.core.CORSFilter corsFilter = new org.openspcoop2.pdd.core.CORSFilter(logCore, cors);
						org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse resCORS = new org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse(true);
						corsFilter.doCORS(httpServletRequest, resCORS, org.openspcoop2.utils.transport.http.CORSRequestType.PRE_FLIGHT, true);
						org.openspcoop2.message.OpenSPCoop2Message msgCORSResponse = resCORS.buildMessage();
						org.openspcoop2.protocol.sdk.builder.EsitoTransazione esito = 
								requestInfo.getProtocolFactory().createEsitoBuilder().getEsito(requestInfo.getProtocolContext(),
										org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY);
						ConnectorDispatcherInfo c = ConnectorDispatcherInfo.getGeneric(msgCORSResponse, 
								resCORS.getStatus(), null, resCORS.getHeader(), esito);
						if(pddContextNullable!=null) {
							pddContextNullable.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY, "true");
						}
						ConnectorDispatcherUtils.doInfo(c, requestInfo, res, logCore, false);
						return c;
						
						*/
						
						// Fix per avere il dump anche nelle comunicazioni soap, converto in REST
						requestInfo.setIntegrationServiceBinding(ServiceBinding.REST);
						requestInfo.setProtocolServiceBinding(ServiceBinding.REST);
						generatoreErrore.updateServiceBinding(ServiceBinding.REST);
						
						requestInfo.setIntegrationRequestMessageType(MessageType.BINARY);
						requestInfo.setProtocolRequestMessageType(MessageType.BINARY);
						generatoreErrore.updateRequestMessageType(MessageType.BINARY);
						
						if(pddContextNullable!=null) {
							pddContextNullable.addObject(CostantiPdD.CORS_PREFLIGHT_REQUEST_SOAP, "true");
						}
						
					}
					else {
						
						if(pddContextNullable!=null) {
							pddContextNullable.addObject(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_TRASPARENTE, "true");
						}
						
						requestInfo.setIntegrationServiceBinding(ServiceBinding.REST);
						requestInfo.setProtocolServiceBinding(ServiceBinding.REST);
						generatoreErrore.updateServiceBinding(ServiceBinding.REST);
						
						requestInfo.setIntegrationRequestMessageType(MessageType.BINARY);
						requestInfo.setProtocolRequestMessageType(MessageType.BINARY);
						generatoreErrore.updateRequestMessageType(MessageType.BINARY);
					}
				}
				
			}catch(Exception error){
				
				if(res!=null) {
					logCore.error("Gestione CORS fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Gestione CORS fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				return null;
				
			}

		}
		
		return null;
	}
	
	
	
	
	public static void emitTransaction(Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherInfo info) {
		emitTransaction(null, logCore, req, pddContext, dataAccettazioneRichiesta, info);
	}
	public static void emitTransaction(RicezioneBusteContext context,Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherInfo info) {
		try {
			String idModulo = req.getIdModulo();
			IDService idModuloAsService = req.getIdModuloAsIDService();
			IProtocolFactory<?> protocolFactory = req.getProtocolFactory();
			RequestInfo requestInfo = req.getRequestInfo();
			
			if(context!=null && context.getPddContext()!=null) {
				PreInRequestHandler.readClientAddress(logCore, req, context.getPddContext());
				if(OpenSPCoop2Properties.getInstance().isTransazioniEnabled()) {
					try {
						String idTransazione = (String) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
						if(idTransazione!=null) {
							Transaction tr = TransactionContext.getTransaction(idTransazione);
							PreInRequestHandler.setInfoInvocation(tr, requestInfo, req);
						}
					}catch(Throwable e){
						logCore.error("postOutResponse, lettura parametri fallita: "+e.getMessage(),e);
					}
				}
			}
			
			emitTransaction(context,logCore, idModulo, idModuloAsService, protocolFactory, requestInfo, pddContext, dataAccettazioneRichiesta, info);
		}catch(Throwable e){
			logCore.error("postOutResponse, registrazione fallita: "+e.getMessage(),e);
		}
	}
	
	public static void emitTransaction(RicezioneBusteContext context,Logger logCore,String idModulo,IDService idModuloAsService, IProtocolFactory<?> protocolFactory, RequestInfo requestInfo,
			PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherInfo info) {
		
		try {
		
			if(context==null) {
				context = new RicezioneBusteContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
			}
			
			PostOutResponseContext postOutResponseContext = new PostOutResponseContext(logCore,protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.APPLICATIVA);
			postOutResponseContext.setIdModulo(idModulo);
			
			postOutResponseContext.setPddContext(pddContext);
			if(pddContext==null) {
				postOutResponseContext.setPddContext(context.getPddContext());
			}
			else {
				pddContext.addAll(context.getPddContext(), true);
			}
			postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA, dataAccettazioneRichiesta);

			if(OpenSPCoop2Properties.getInstance().isTransazioniEnabled()) {
				// NOTA: se gia' esiste con l'id di transazione, non viene ricreata
				TransactionContext.createTransaction((String)postOutResponseContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE));
			}
			
			postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			postOutResponseContext.setEsito(info.getEsitoTransazione());
			postOutResponseContext.setReturnCode(info.getStatus());
			postOutResponseContext.setPropertiesRispostaTrasporto(info.getTrasporto());
			if(info.getContentType()!=null) {
				postOutResponseContext.setPropertiesRispostaTrasporto(new Properties());
				postOutResponseContext.getPropertiesRispostaTrasporto().put(HttpConstants.CONTENT_TYPE, info.getContentType());
			}
					
			if(info.getMessage()!=null){
				
				postOutResponseContext.setOutputResponseMessageSize(info.getMessage().getOutgoingMessageContentLength());
				
				postOutResponseContext.setMessaggio(info.getMessage());
			}
	
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.APPLICATIVA,idModulo);
			msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, logCore);
			
		}catch(Throwable e){
			logCore.error("postOutResponse, registrazione fallita: "+e.getMessage(),e);
		}
	
	}
}
