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

package org.openspcoop2.pdd.services.service;

import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
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
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativiContext;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaDelegata;
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
 * RicezioneContenutiApplicativiServiceUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneContenutiApplicativiServiceUtils {

	public static ConnectorDispatcherInfo updatePortaDelegataRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, PdDContext pddContextNullable) throws ConnectorException{
		
		URLProtocolContext protocolContext = requestInfo.getProtocolContext();
		IProtocolFactory<?> pf = requestInfo.getProtocolFactory();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
		ServiceBinding integrationServiceBinding = requestInfo.getIntegrationServiceBinding();
		ServiceBinding protocolServiceBinding = requestInfo.getProtocolServiceBinding();
				
		IRegistryReader registryReader = serviceIdentificationReader.getRegistryReader();
		CachedConfigIntegrationReader configIntegrationReader = (CachedConfigIntegrationReader) serviceIdentificationReader.getConfigIntegrationReader();
		
		IDPortaDelegata idPD = null;
		PortaDelegata pdDefault = null;
		try{
			idPD = serviceIdentificationReader.findPortaDelegata(protocolContext, true);
		}catch(RegistryNotFound notFound){
			// Non ha senso nel contesto di porta delegata
			//if(bindingConfig.existsContextUrlMapping()==false){
			logCore.error("Porta Delegata non trovata: "+notFound.getMessage(),notFound);
			msgDiag.addKeywordErroreProcessamento(notFound);
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
			return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
					IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
			//}
		}
		if(idPD!=null){
			
			// da ora in avanti, avendo localizzato una PD, se avviene un errore genero l'errore stesso
			protocolContext.setInterfaceName(idPD.getNome());
			
			IDSoggetto idSoggettoFruitore = null;
			IDServizio idServizio = null;
			if(idPD.getIdentificativiFruizione()!=null){
				if(idPD.getIdentificativiFruizione().getSoggettoFruitore()!=null) {
					idSoggettoFruitore = idPD.getIdentificativiFruizione().getSoggettoFruitore().clone(); // effettuo clone altrimenti nella cache vengono memorizzate le informazioni impostate dopo!
				}
				if( idPD.getIdentificativiFruizione().getIdServizio()!=null) {
					idServizio = idPD.getIdentificativiFruizione().getIdServizio().clone(); // effettuo clone altrimenti nella cache viene memorizzata l'azione impostata dopo!
				}
			}
			
			if(generatoreErrore!=null && idSoggettoFruitore!=null){
				generatoreErrore.updateDominio(idSoggettoFruitore);
				generatoreErrore.updateInformazioniCooperazione(idSoggettoFruitore, null);
			}
			if(idServizio==null){
				try{
					idServizio = serviceIdentificationReader.convertToIDServizio(idPD);
				}catch(RegistryNotFound notFound){
					logCore.debug("Conversione Dati PortaDelegata in identificativo servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}
			}
			
			if(idServizio!=null){
				
				// provo a leggere anche l'azione
				// l'eventuale errore lo genero dopo
				try{
					idServizio.setAzione(configIntegrationReader.getAzione(configIntegrationReader.getPortaDelegata(idPD), protocolContext, requestInfo.getProtocolFactory()));
				}catch(Exception e){
					logCore.debug("Azione non trovata: "+e.getMessage(),e);
				}
				
				// PortaDelegata Default
				try{
					pdDefault = ConfigurazionePdDManager.getInstance().getPortaDelegata(idPD);
				}catch(Exception e){
					logCore.debug("Recupero porta default fallita: "+e.getMessage(),e);
				}
				try{
					if(idServizio.getAzione()!=null) {
						IdentificazionePortaDelegata identificazione = new IdentificazionePortaDelegata(logCore, pf, null, pdDefault);
						if(identificazione.find(idServizio.getAzione())) {
							IDPortaDelegata idPD_action = identificazione.getIDPortaDelegata(idServizio.getAzione());
							if(idPD_action!=null) {
								
								if(pddContextNullable!=null) {
									pddContextNullable.addObject(CostantiPdD.NOME_PORTA_INVOCATA, idPD.getNome()); // prima di aggiornare la porta delegata
								}
								
								msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, idPD_action.getNome());
								msgDiag.updatePorta(idPD_action.getNome());
								protocolContext.setInterfaceName(idPD_action.getNome());
								idPD = idPD_action;
							}
						}
					}
				}catch(Exception e){
					logCore.debug("Gestione porta specifica per azione fallita: "+e.getMessage(),e);
				}
				
				// updateInformazioniCooperazione
				requestInfo.setFruitore(idSoggettoFruitore);
				requestInfo.setIdServizio(idServizio);
				if(generatoreErrore!=null){
					generatoreErrore.updateInformazioniCooperazione(idSoggettoFruitore, idServizio);
				}
				
				// Aggiorno service binding rispetto al servizio localizzato
				try{
					
					integrationServiceBinding = pf.createProtocolConfiguration().getIntegrationServiceBinding(idServizio, registryReader);
					requestInfo.setIntegrationServiceBinding(integrationServiceBinding);
					
					protocolServiceBinding = pf.createProtocolConfiguration().getProtocolServiceBinding(integrationServiceBinding, protocolContext);
					requestInfo.setProtocolServiceBinding(protocolServiceBinding);
					
					generatoreErrore.updateServiceBinding(integrationServiceBinding);
				}catch(RegistryNotFound notFound){
					logCore.debug("Lettura ServiceBinding fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}catch(Exception error){
					logCore.error("Lettura ServiceBinding fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura ServiceBinding fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				
				// Aggiorno service binding configuration rispetto al servizio localizzato
				try{
					bindingConfig = pf.createProtocolConfiguration().getServiceBindingConfiguration(protocolContext, integrationServiceBinding, idServizio, registryReader);
					requestInfo.setBindingConfig(bindingConfig);
				}catch(RegistryNotFound notFound){
					logCore.debug("Lettura Configurazione Servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}catch(Exception error){
					logCore.error("Lettura Configurazione Servizio fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura Configurazione Servizio fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				
				// Aggiorno message type
				try{
					MessageType requestMessageTypeIntegration = bindingConfig.getRequestMessageType(integrationServiceBinding, 
							protocolContext, protocolContext.getContentType());
					requestInfo.setIntegrationRequestMessageType(requestMessageTypeIntegration);
					
					MessageType requestMessageTypeProtocol = bindingConfig.getRequestMessageType(protocolServiceBinding, 
							protocolContext, protocolContext.getContentType());
					requestInfo.setProtocolRequestMessageType(requestMessageTypeProtocol);
					
					generatoreErrore.updateRequestMessageType(requestMessageTypeIntegration);
				}catch(Exception error){
					logCore.error("Comprensione MessageType fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Comprensione MessageType fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
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
					if(pdDefault!=null) {
						cors = configurazionePdDManager.getConfigurazioneCORS(pdDefault);
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
						org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse resCORS = new org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse(false);
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
						ConnectorDispatcherUtils.doInfo(c, requestInfo, res, logCore, true);
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
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
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
	public static void emitTransaction(RicezioneContenutiApplicativiContext context,Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta,
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
	
	public static void emitTransaction(RicezioneContenutiApplicativiContext context,Logger logCore,String idModulo,IDService idModuloAsService, IProtocolFactory<?> protocolFactory, RequestInfo requestInfo,
			PdDContext pddContext, Date dataAccettazioneRichiesta,
			ConnectorDispatcherInfo info) {
		
		try {
		
			if(context==null) {
				context = new RicezioneContenutiApplicativiContext(idModuloAsService,dataAccettazioneRichiesta,requestInfo);
			}
			
			PostOutResponseContext postOutResponseContext = new PostOutResponseContext(logCore,protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
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
	
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA,idModulo);
			msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, logCore);
			
		}catch(Throwable e){
			logCore.error("postOutResponse, registrazione fallita: "+e.getMessage(),e);
		}
	
	}
}
