/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
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
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativiContext;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaDelegata;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
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

	public static RicezionePropertiesConfig readPropertiesConfig(RequestInfo requestInfo, Logger logCore, IState state) {
		if (requestInfo != null && requestInfo.getProtocolContext() != null && requestInfo.getProtocolContext().getInterfaceName() != null && !"".equals(requestInfo.getProtocolContext().getInterfaceName())) {
			try {
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
				RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(state);
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(requestInfo.getProtocolContext().getInterfaceName());
				PortaDelegata pd = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD, requestInfo);
				if (pd != null) {
					RicezionePropertiesConfig config = new RicezionePropertiesConfig();
					
					config.setApiImplementation(configurazionePdDManager.getProprietaConfigurazione(pd));
					
					IDSoggetto idSoggettoProprietario = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
					Soggetto soggetto = registroServiziManager.getSoggetto(idSoggettoProprietario, null, requestInfo);
					config.setSoggettoFruitore(registroServiziManager.getProprietaConfigurazione(soggetto));
					
					IDSoggetto idSoggettoErogatore = new IDSoggetto(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome());
					Soggetto soggettoErogatore = registroServiziManager.getSoggetto(idSoggettoErogatore, null, requestInfo);
					config.setSoggettoErogatore(registroServiziManager.getProprietaConfigurazione(soggettoErogatore));
					
					return config;
				}
			} catch (Exception e) {
				logCore.error("Errore durante la lettura delle proprietà di configurazione della porta delegata [" + requestInfo.getProtocolContext().getInterfaceName() + "]: " + e.getMessage(), e);
			}

			return null;
		} else {
			return null;
		}
	}
	
	public static ConnectorDispatcherInfo updatePortaDelegataRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorInMessage req, ConnectorOutMessage res, RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore,
			ServiceIdentificationReader serviceIdentificationReader,
			MsgDiagnostico msgDiag, PdDContext pddContextNullable) throws ConnectorException{
		
		URLProtocolContext protocolContext = requestInfo.getProtocolContext();
		IProtocolFactory<?> pf = requestInfo.getProtocolFactory();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
		ServiceBinding integrationServiceBinding = requestInfo.getIntegrationServiceBinding();
		ServiceBinding protocolServiceBinding = requestInfo.getProtocolServiceBinding();
				
		IRegistryReader registryReader = serviceIdentificationReader.getRegistryReader();
		CachedConfigIntegrationReader configIntegrationReader = (CachedConfigIntegrationReader) serviceIdentificationReader.getConfigIntegrationReader();
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
		
		IDPortaDelegata idPD = null;
		PortaDelegata pdDefault = null;
		try{
			if(requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getIdPortaDelegataDefault()!=null) {
				idPD = requestInfo.getRequestConfig().getIdPortaDelegataDefault();
			}
			else {
				idPD = serviceIdentificationReader.findPortaDelegata(protocolContext, true);
				if(requestInfo.getRequestConfig()!=null) {
					requestInfo.getRequestConfig().setIdPortaDelegataDefault(idPD);
				}
			}
		}catch(RegistryNotFound notFound){
			if(pddContextNullable!=null) {
				pddContextNullable.addObject(org.openspcoop2.core.constants.Costanti.API_NON_INDIVIDUATA, "true");
			}
			// Non ha senso nel contesto di porta delegata
			//if(bindingConfig.existsContextUrlMapping()==false){
			logCore.error("Porta Delegata non trovata: "+notFound.getMessage(),notFound);
			msgDiag.addKeywordErroreProcessamento(notFound);
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
			ConnectorDispatcherInfo c = ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
					IntegrationFunctionError.API_OUT_UNKNOWN, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
			try {
				EsitoTransazione esito = requestInfo.getProtocolFactory().createEsitoBuilder().getEsito(requestInfo.getProtocolContext(),EsitoTransazioneName.API_NON_INDIVIDUATA);
				c.setEsitoTransazione(esito);
			}catch(Throwable t) {
				logCore.error("Errore durante l'impostazione dell'esito 'API_NON_INDIVIDUATA'");
			}
			return c;
			//}
		}
		if(idPD!=null){
			
			// da ora in avanti, avendo localizzato una PD, se avviene un errore genero l'errore stesso
			protocolContext.setInterfaceName(idPD.getNome());
			
			// read IDServizio e IDSoggettoFruitore
			IDSoggetto idSoggettoFruitore = null;
			IDServizio idServizio = null;
			if(requestInfo.getRequestConfig()!=null && 
					requestInfo.getRequestConfig().getIdServizio()!=null &&
					requestInfo.getRequestConfig().getIdFruitore()!=null) {
				idServizio = requestInfo.getRequestConfig().getIdServizio();
				idSoggettoFruitore = requestInfo.getRequestConfig().getIdFruitore();
			}
			else {
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
								IntegrationFunctionError.API_OUT_UNKNOWN, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
					}
				}
				if(idSoggettoFruitore==null){
					try{
						idSoggettoFruitore = serviceIdentificationReader.convertToIDSoggettoFruitore(idPD);
					}catch(RegistryNotFound notFound){
						logCore.debug("Conversione Dati PortaDelegata in identificativo soggetto fruitore fallita (notFound): "+notFound.getMessage(),notFound);
						msgDiag.addKeywordErroreProcessamento(notFound);
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
						return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
								IntegrationFunctionError.API_OUT_UNKNOWN, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
					}
				}
				
				if(requestInfo.getRequestConfig()!=null) {
					requestInfo.getRequestConfig().setIdServizio(idServizio);
					requestInfo.getRequestConfig().setIdFruitore(idSoggettoFruitore);
				}
			}
			
			if(idServizio!=null){
				
				RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance();
				
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
							IntegrationFunctionError.API_OUT_UNKNOWN, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}catch(Exception error){
					logCore.error("Lettura ServiceBinding fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura ServiceBinding fallita"),
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				
				// Provo ad ottenere il reader soap
				OpenSPCoop2MessageSoapStreamReader soapStreamReader = null;
				if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
					try{
						if(req!=null) {
							soapStreamReader = req.getSoapReader();
						}
					}catch(Exception e){
						logCore.debug("SOAPStream lettura non riuscita: "+e.getMessage(),e);
					}
				}
				
				// PortaDelegata Default
				try{
					if(requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getPortaDelegataDefault()!=null) {
						pdDefault = requestInfo.getRequestConfig().getPortaDelegataDefault();
					}
					else {
						pdDefault = configurazionePdDManager.getPortaDelegata(idPD, null); // passo null volutamente, per non utilizzare la configurazione
						if(requestInfo.getRequestConfig()!=null) {
							requestInfo.getRequestConfig().setPortaDelegataDefault(pdDefault);
						}
					}
				}catch(Exception e){
					logCore.debug("Recupero porta default fallita: "+e.getMessage(),e);
				}
				
				// Verifico che la modalità di riconoscimento dell'azione sia compatibile
				if(pdDefault!=null && pdDefault.getAzione()!=null && pdDefault.getAzione().getIdentificazione()!=null) {
					if(org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.CONTENT_BASED.equals(pdDefault.getAzione().getIdentificazione()) 
							||
						org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED.equals(pdDefault.getAzione().getIdentificazione())	
							||
						org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.INPUT_BASED.equals(pdDefault.getAzione().getIdentificazione())	
							||
						org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(pdDefault.getAzione().getIdentificazione())) {
						logCore.debug("Cache della richiesta non utilizzabile con la modalità di identificazione dell'azione '"+pdDefault.getAzione().getIdentificazione()+"'");
						requestInfo.setRequestConfig(null);
						requestInfo.setRequestRateLimitingConfig(null);
					}
				}
				
				// provo a leggere anche l'azione
				// l'eventuale errore lo genero dopo
				try{
					//PortaDelegata pdSearchAzione = configIntegrationReader.getPortaDelegata(idPD);
					PortaDelegata pdSearchAzione = pdDefault;
					idServizio.setAzione(configIntegrationReader.getAzione(pdSearchAzione, protocolContext, requestInfo, requestInfo.getProtocolFactory(),soapStreamReader));
				}catch(Exception e){
					logCore.debug("Azione non trovata: "+e.getMessage(),e);
				}
								
				// Lettura eventuale MessageFactory da utilizzare
				try {
					if(pdDefault!=null && pdDefault.getOptions()!=null && !StringUtils.isEmpty(pdDefault.getOptions())) {
						Map<String, List<String>> props = PropertiesSerializator.convertoFromDBColumnValue(pdDefault.getOptions());
						if(props!=null && props.size()>0) {
							String msgFactory = TransportUtils.getFirstValue(props,CostantiPdD.OPTIONS_MESSAGE_FACTORY);
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
					if(requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getIdPortaDelegata()!=null) {
						idPD = requestInfo.getRequestConfig().getIdPortaDelegata();
					}
					else {
						if(idServizio.getAzione()!=null) {
							IdentificazionePortaDelegata identificazione = new IdentificazionePortaDelegata(logCore, pf, 
									serviceIdentificationReader.getRegistryReader(), serviceIdentificationReader.getConfigIntegrationReader(), // questi reader sono gia' 'cached'
									pdDefault);
							if(identificazione.find(idServizio.getAzione())) {
								IDPortaDelegata idPD_action = identificazione.getIDPortaDelegata(idServizio.getAzione());
								if(idPD_action!=null) {
									
									if(pddContextNullable!=null) {
										pddContextNullable.addObject(CostantiPdD.NOME_PORTA_INVOCATA, idPD.getNome()); // prima di aggiornare la porta delegata
									}
									
									msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, idPD_action.getNome());
									msgDiag.updatePorta(idPD_action.getNome(),requestInfo);
									protocolContext.setInterfaceName(idPD_action.getNome());
									idPD = idPD_action;
								}
							}
						}
						if(idPD!=null && requestInfo.getRequestConfig()!=null) {
							requestInfo.getRequestConfig().setIdPortaDelegata(idPD);
						}
					}
				}catch(Exception e){
					logCore.debug("Gestione porta specifica per azione fallita: "+e.getMessage(),e);
				}
				
				// SetPD usato poi successivamente
				if(idPD!=null && requestInfo.getRequestConfig()!=null && requestInfo.getRequestConfig().getPortaDelegata()==null) {
					try{
						PortaDelegata pd = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD, null); // passo null volutamente, per non utilizzare la configurazione
						requestInfo.getRequestConfig().setPortaDelegata(pd);
					}catch(Exception e){
						logCore.debug("Recupero porta delegata fallito: "+e.getMessage(),e);
					}
				}
				
				// Set identità soggetti erogatori usati poi successivamente
				if(idServizio!=null && idServizio.getSoggettoErogatore()!=null && requestInfo.getRequestConfig()!=null) {
					if(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry()==null) {
						try{
							org.openspcoop2.core.registry.Soggetto soggettoRegistry = registroServiziManager.getSoggetto(idServizio.getSoggettoErogatore(), null, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoErogatoreRegistry(soggettoRegistry);
						}catch(Exception e){
							logCore.debug("Recupero soggetto erogatore dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoErogatoreConfig()==null) {
						try{
							org.openspcoop2.core.config.Soggetto soggettoConfig = configurazionePdDManager.getSoggetto(idServizio.getSoggettoErogatore(), null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoErogatoreConfig(soggettoConfig);
						}catch(Exception e){
							logCore.debug("Recupero soggetto erogatore dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoErogatoreIdentificativoPorta()==null) {
						try{
							String idPorta = configurazionePdDManager.getIdentificativoPorta(idServizio.getSoggettoErogatore(), pf, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoErogatoreIdentificativoPorta(idPorta);
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto erogatore (identificativoPorta) dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoErogatoreSoggettoVirtuale()==null) {
						try{
							boolean soggettoVirtuale = configurazionePdDManager.isSoggettoVirtuale(idServizio.getSoggettoErogatore(), null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoErogatoreSoggettoVirtuale(soggettoVirtuale);
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto erogatore (soggettoVirtuale) dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoErogatorePddReaded()==null) {
						try{
							if(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry()!=null) {
								if(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry().getPortaDominio()!=null &&
										StringUtils.isNotEmpty(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry().getPortaDominio())) {
									PortaDominio pdd = registroServiziManager.getPortaDominio(requestInfo.getRequestConfig().getSoggettoErogatoreRegistry().getPortaDominio(), null, null); // passo null volutamente, per accedere alla configurazione
									requestInfo.getRequestConfig().setSoggettoErogatorePddReaded(true);
									requestInfo.getRequestConfig().setSoggettoErogatorePdd(pdd);
								}
								else {
									requestInfo.getRequestConfig().setSoggettoErogatorePddReaded(true);
								}
							}
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto erogatore (pdd) dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoErogatoreImplementazionePdd()==null) {
						try{
							String impl = registroServiziManager.getImplementazionePdD(idServizio.getSoggettoErogatore(), null, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoErogatoreImplementazionePdd(impl);
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto erogatore (implementazione pdd) dal registro fallito: "+e.getMessage(),e);
						}
					}
				}
				
				// Set identità soggetti fruitori usati poi successivamente
				if(idSoggettoFruitore!=null && requestInfo.getRequestConfig()!=null) {
					if(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry()==null) {
						try{
							org.openspcoop2.core.registry.Soggetto soggettoRegistry = registroServiziManager.getSoggetto(idSoggettoFruitore, null, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoFruitoreRegistry(soggettoRegistry);
						}catch(Exception e){
							logCore.debug("Recupero soggetto fruitore dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoFruitoreConfig()==null) {
						try{
							org.openspcoop2.core.config.Soggetto soggettoConfig = configurazionePdDManager.getSoggetto(idSoggettoFruitore, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoFruitoreConfig(soggettoConfig);
						}catch(Exception e){
							logCore.debug("Recupero soggetto fruitore dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoFruitoreIdentificativoPorta()==null) {
						try{
							String idPorta = configurazionePdDManager.getIdentificativoPorta(idSoggettoFruitore, pf, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoFruitoreIdentificativoPorta(idPorta);
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto fruitore (identificativoPorta) dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoFruitoreSoggettoVirtuale()==null) {
						try{
							boolean soggettoVirtuale = configurazionePdDManager.isSoggettoVirtuale(idSoggettoFruitore, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoFruitoreSoggettoVirtuale(soggettoVirtuale);
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto fruitore (soggettoVirtuale) dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoFruitorePddReaded()==null) {
						try{
							if(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry()!=null) {
								if(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
										StringUtils.isNotEmpty(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry().getPortaDominio())) {
									PortaDominio pdd = registroServiziManager.getPortaDominio(requestInfo.getRequestConfig().getSoggettoFruitoreRegistry().getPortaDominio(), null, null); // passo null volutamente, per accedere alla configurazione
									requestInfo.getRequestConfig().setSoggettoFruitorePddReaded(true);
									requestInfo.getRequestConfig().setSoggettoFruitorePdd(pdd);
								}
								else {
									requestInfo.getRequestConfig().setSoggettoFruitorePddReaded(true);
								}
							}
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto fruitore (pdd) dal registro fallito: "+e.getMessage(),e);
						}
					}
					if(requestInfo.getRequestConfig().getSoggettoFruitoreImplementazionePdd()==null) {
						try{
							String impl = registroServiziManager.getImplementazionePdD(idSoggettoFruitore, null, null); // passo null volutamente, per accedere alla configurazione
							requestInfo.getRequestConfig().setSoggettoFruitoreImplementazionePdd(impl);
						}catch(Exception e){
							logCore.debug("Recupero dati soggetto fruitore (implementazione pdd) dal registro fallito: "+e.getMessage(),e);
						}
					}
				}
				
				// updateInformazioniCooperazione
				requestInfo.setFruitore(idSoggettoFruitore);
				requestInfo.setIdServizio(idServizio);
				if(generatoreErrore!=null){
					generatoreErrore.updateInformazioniCooperazione(idSoggettoFruitore, idServizio);
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
							IntegrationFunctionError.API_OUT_UNKNOWN, notFound, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
				}catch(Exception error){
					logCore.error("Lettura Configurazione Servizio fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura Configurazione Servizio fallita"),
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				
				// Verifico correttezza Content-Type
				String ct = null;
				try {
					ct = protocolContext.getContentType();
					if(ct!=null && !"".equals(ct)) {
						//new ContentType(ct).getBaseType();
						ContentTypeUtilities.validateContentType(ct);
					}
				}catch(Exception error){
					if(res!=null) {
						logCore.error("Lettura ContentType fallita: "+error.getMessage(),error);
						msgDiag.addKeywordErroreProcessamento(error);
						msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " Content-Type '"+ct+"' presente nella richiesta non valido;");
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"richiestaNonValida");
						ConnectorDispatcherInfo cdiError = ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
								ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.getErrore432_MessaggioRichiestaMalformato(error),
								IntegrationFunctionError.BAD_REQUEST, error, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
						try {
							cdiError.setEsitoTransazione(pf.createEsitoBuilder().getEsito(requestInfo.getProtocolContext(),EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO));
							if(pddContextNullable!=null) {
								pddContextNullable.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
							}
						}catch(Throwable t) {}
						return cdiError;
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
					
					generatoreErrore.updateRequestMessageType(requestMessageTypeIntegration);
				}catch(Exception error){
					logCore.error("Identificazione MessageType fallita: "+error.getMessage(),error);
					msgDiag.addKeywordErroreProcessamento(error);
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI,"portaDelegataNonEsistente");
					return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Identificazione MessageType fallita"),
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
				}
				
				// Controllo Service Binding rispetto alla url e al tipo di servizio
				if(requestInfo.getProtocolContext()!=null && bindingConfig.isServiceBindingContextEnabled(integrationServiceBinding, requestInfo.getProtocolContext().getProtocolWebContext())==false) {
					if(res!=null) {
						String url = requestInfo.getProtocolContext().getRequestURI();
						logCore.error("L'API invocata possiede un service binding '"+integrationServiceBinding+"' non abilitato sul contesto utilizzato: "+url);
						msgDiag.logErroreGenerico("L'API invocata possiede un service binding '"+integrationServiceBinding+"' non abilitato sul contesto utilizzato", "CheckServiceBinding");
						return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, 
								ErroriIntegrazione.ERRORE_447_API_NON_INVOCABILE_CONTESTO_UTILIZZATO.getErroreIntegrazione(),
								IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, null, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
					}
					return null;
				}
				if(bindingConfig.isServiceBindingServiceTypeEnabled(integrationServiceBinding, idServizio.getTipo())==false) {
					if(res!=null) {
						logCore.error("L'API invocata possiede un service binding '"+integrationServiceBinding+"' non abilitato per il tipo di servizio '"+idServizio.getTipo()+"'");
						msgDiag.logErroreGenerico("L'API invocata possiede un service binding '"+integrationServiceBinding+"' non abilitato per il tipo di servizio '"+idServizio.getTipo(), "CheckServiceBinding");
						return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, 
								ErroriIntegrazione.ERRORE_448_API_NON_INVOCABILE_TIPO_SERVIZIO_UTILIZZATO.getErroreIntegrazione(),
								IntegrationFunctionError.NOT_SUPPORTED_BY_PROTOCOL, null, null, res, logCore, ConnectorDispatcherUtils.CLIENT_ERROR);
					}
					return null;
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
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, error, null, res, logCore, ConnectorDispatcherUtils.GENERAL_ERROR);
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
			if (!OpenSPCoop2Startup.initialize || OpenSPCoop2Properties.getInstance() == null) {
				logCore.error("emitTransaction, registrazione non effettuata: inizializzazione govway non rilevata");
				return;
			}
			
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
			if (!OpenSPCoop2Startup.initialize || OpenSPCoop2Properties.getInstance() == null) {
				logCore.error("postOutResponse, registrazione non effettuata: inizializzazione govway non rilevata");
				return;
			}
		
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
				TransactionContext.createTransaction((String)postOutResponseContext.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE), "RicezioneContenutiApplicativi.3");
			}
			
			postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
			postOutResponseContext.setEsito(info.getEsitoTransazione());
			postOutResponseContext.setReturnCode(info.getStatus());
			postOutResponseContext.setResponseHeaders(info.getTrasporto());
			if(info.getContentType()!=null) {
				if(postOutResponseContext.getResponseHeaders()==null) {
					postOutResponseContext.setResponseHeaders(new HashMap<String, List<String>>());
				}
				TransportUtils.setHeader(postOutResponseContext.getResponseHeaders(),HttpConstants.CONTENT_TYPE, info.getContentType());
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
