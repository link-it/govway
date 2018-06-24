/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.CachedConfigIntegrationReader;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.slf4j.Logger;

/**
 * RicezioneBusteServiceUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteServiceUtils {

	
	public static boolean updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag) throws ConnectorException{
		
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
				ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
						IntegrationError.NOT_FOUND, notFound, null, res, logCore);
				return false;
			}
		}
		if(idPA!=null){
			return updatePortaApplicativaRequestInfo(requestInfo, logCore, 
					res, generatoreErrore, 
					serviceIdentificationReader, 
					msgDiag,
					protocolContext,idPA);
		}
		
		return true;
	}
	
	public static boolean updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, 
			URLProtocolContext protocolContext, IDPortaApplicativa idPA) throws ConnectorException{
		return updatePortaApplicativaRequestInfo(requestInfo, logCore, 
				null, generatoreErrore, 
				serviceIdentificationReader, 
				msgDiag, 
				protocolContext, idPA);
	}
	public static boolean updatePortaApplicativaRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneBusteExternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, 
			URLProtocolContext protocolContext, IDPortaApplicativa idPA) throws ConnectorException{

			
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
			idServizio = idPA.getIdentificativiErogazione().getIdServizio();
		}
		
		if(idServizio==null){
			try{
				idServizio = serviceIdentificationReader.convertToIDServizio(idPA);
			}catch(RegistryNotFound notFound){
				if(res!=null) {
					logCore.debug("Conversione Dati PortaDelegata in identificativo servizio fallita (notFound): "+notFound.getMessage(),notFound);
					msgDiag.addKeywordErroreProcessamento(notFound);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
							IntegrationError.NOT_FOUND, notFound, null, res, logCore);
				}
				return false;
			}
		}
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
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore);
				}
				return false;
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Lettura ServiceBinding fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura ServiceBinding fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore);
				}
				return false;
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
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore);
				}
				return false;
			}catch(Exception error){
				if(res!=null) {
					logCore.error("Lettura Configurazione Servizio fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_SBUSTAMENTO,"portaApplicativaNonEsistente");
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura Configurazione Servizio fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore);
				}
				return false;
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
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Comprensione MessageType fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore);
				}
				return false;
			}
		}
	
		return true;
	}
}
