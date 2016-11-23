package org.openspcoop2.pdd.services.service;

import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.RegistryReader;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.pdd.services.connector.ConnectorDispatcherUtils;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.slf4j.Logger;

public class RicezioneContenutiApplicativiServiceUtils {

	public static boolean updatePortaDelegataRequestInfo(RequestInfo requestInfo, Logger logCore, 
			ConnectorOutMessage res, RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore,
			RegistryReader registryReader, ServiceIdentificationReader serviceIdentificationReader) throws ConnectorException{
		
		URLProtocolContext protocolContext = requestInfo.getProtocolContext();
		IProtocolFactory<?> pf = requestInfo.getProtocolFactory();
		ServiceBindingConfiguration bindingConfig = requestInfo.getBindingConfig();
		ServiceBinding serviceBinding = requestInfo.getServiceBinding();
		MessageType requestMessageType = requestInfo.getRequestMessageType();
						
		IDPortaDelegata idPD = null;
		try{
			idPD = serviceIdentificationReader.findPortaDelegata(protocolContext, true);
		}catch(RegistryNotFound notFound){
			if(bindingConfig.existsContextUrlMapping()==false){
				logCore.error("Porta Delegata non trovata: "+notFound.getMessage(),notFound);
				ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
						IntegrationError.NOT_FOUND, notFound, null, res, logCore);
				return false;
			}
		}
		if(idPD!=null){
			
			// da ora in avanti, avendo localizzato una PD, se avviene un errore genero l'errore stesso
			protocolContext.setInterfaceName(idPD.getNome());
			
			IDSoggetto idSoggettoFruitore = null;
			IDServizio idServizio = null;
			if(idPD.getIdentificativiFruizione()!=null){
				idSoggettoFruitore = idPD.getIdentificativiFruizione().getSoggettoFruitore();
				idServizio = idPD.getIdentificativiFruizione().getIdServizio();
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
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, serviceIdentificationReader.getErroreIntegrazioneNotFound(), 
							IntegrationError.NOT_FOUND, notFound, null, res, logCore);
					return false;
				}
			}
			if(idServizio!=null){
				
				// provo a leggere anche l'azione
				// l'eventuale errore lo genero dopo
				try{
					idServizio.setAzione(registryReader.getAzione(registryReader.getPortaDelegata(idPD), protocolContext, requestInfo.getProtocolFactory()));
				}catch(Exception e){
					logCore.debug("Azione non trovata: "+e.getMessage(),e);
				}
				
				// updateInformazioniCooperazione
				if(generatoreErrore!=null){
					generatoreErrore.updateInformazioniCooperazione(idSoggettoFruitore, idServizio);
				}
				
				// Aggiorno service binding rispetto al servizio localizzato
				try{
					serviceBinding = pf.createProtocolConfiguration().getServiceBinding(idServizio, registryReader);
					requestInfo.setServiceBinding(serviceBinding);
				}catch(RegistryNotFound notFound){
					logCore.debug("Lettura ServiceBinding fallita (notFound): "+notFound.getMessage(),notFound);
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore);
					return false;
				}catch(Exception error){
					logCore.error("Lettura ServiceBinding fallita: "+error.getMessage(),error);
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura ServiceBinding fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore);
					return false;
				}
				
				// Aggiorno service binding configuration rispetto al servizio localizzato
				try{
					bindingConfig = pf.createProtocolConfiguration().getServiceBindingConfiguration(protocolContext, serviceBinding, idServizio, registryReader);
					requestInfo.setBindingConfig(bindingConfig);
				}catch(RegistryNotFound notFound){
					logCore.debug("Lettura Configurazione Servizio fallita (notFound): "+notFound.getMessage(),notFound);
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione(),
							IntegrationError.NOT_FOUND, notFound, null, res, logCore);
					return false;
				}catch(Exception error){
					logCore.error("Lettura Configurazione Servizio fallita: "+error.getMessage(),error);
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Lettura Configurazione Servizio fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore);
					return false;
				}
				
				// Aggiorno message type
				try{
					requestMessageType = bindingConfig.getMessageType(serviceBinding, MessageRole.REQUEST, 
							protocolContext, protocolContext.getContentType());
					requestInfo.setRequestMessageType(requestMessageType);
				}catch(Exception error){
					logCore.error("Comprensione MessageType fallita: "+error.getMessage(),error);
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore,
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento("Comprensione MessageType fallita"),
							IntegrationError.INTERNAL_ERROR, error, null, res, logCore);
					return false;
				}
			}
		}
		
		return true;
	}
	
}
