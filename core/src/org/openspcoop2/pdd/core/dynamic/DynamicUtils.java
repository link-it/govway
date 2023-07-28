/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.ArchiveType;
import org.openspcoop2.utils.io.CompressorUtilities;
import org.openspcoop2.utils.io.Entry;
import org.openspcoop2.utils.resources.FreemarkerTemplateLoader;
import org.openspcoop2.utils.resources.TemplateUtils;
import org.openspcoop2.utils.resources.VelocityTemplateUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

/**
 * DynamicUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicUtils {
	
	private DynamicUtils() {}

	// *** DYNAMIC MAP ***
	
	// NOTA: uso volutamente le stesse costanti del connettore File
	
	public static void fillDynamicMapRequest(Logger log, Map<String, Object> dynamicMap, Context pddContext, String urlInvocazione,
			OpenSPCoop2Message message,
			MessageContent messageContent,
			Busta busta, 
			Map<String, List<String>> trasporto, 
			Map<String, List<String>> url, 
			Map<String, List<String>> form,
			ErrorHandler errorHandler) {
		fillDynamicMapEngine(log, dynamicMap, pddContext, urlInvocazione, 
				message,
				messageContent,
				busta, 
				trasporto, 
				url,
				form,
				errorHandler);	
    }
	public static void fillDynamicMapResponse(Logger log, Map<String, Object> dynamicMap, Map<String, Object> dynamicMapRequest, Context pddContext,
			OpenSPCoop2Message message,
			MessageContent messageContent,
			Busta busta, Map<String, List<String>> trasporto,
			ErrorHandler errorHandler) {
		fillDynamicMapResponse(log, dynamicMap, dynamicMapRequest, pddContext,
				message,
				messageContent,
				busta, trasporto,
				errorHandler,
				false);
	}
	public static void fillDynamicMapResponse(Logger log, Map<String, Object> dynamicMap, Map<String, Object> dynamicMapRequest, Context pddContext,
			OpenSPCoop2Message message,
			MessageContent messageContent,
			Busta busta, Map<String, List<String>> trasporto,
			ErrorHandler errorHandler,
			boolean preserveRequest) {
		Map<String, Object> dynamicMapResponse = new HashMap<>();
		fillDynamicMapEngine(log, dynamicMapResponse, pddContext, null, 
				message,
				messageContent,
				busta, 
				trasporto, 
				null, 
				null,
				errorHandler);
		if(!dynamicMapResponse.isEmpty()) {
			Iterator<String> it = dynamicMapResponse.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object o = dynamicMapResponse.get(key);
				if(Costanti.MAP_ERROR_HANDLER_OBJECT.equalsIgnoreCase(key) 
						|| 
					Costanti.MAP_RESPONSE.equalsIgnoreCase(key)
						||
					Costanti.MAP_INTEGRATION.equalsIgnoreCase(key)
						||
					(Costanti.MAP_INTEGRATION+Costanti.MAP_SUFFIX_RESPONSE).equalsIgnoreCase(key)
					){
					dynamicMap.put(key, o);
				}
				else {
					String keyResponse = key+Costanti.MAP_SUFFIX_RESPONSE;
					dynamicMap.put(keyResponse, o);
					dynamicMap.put(keyResponse.toLowerCase(), o);
					if(Costanti.MAP_HEADER_VALUES.equals(key)) {
						// scritta piu' corretta, pero' lascio anche la precedente
						dynamicMap.put(Costanti.MAP_HEADER_RESPONSE_VALUES, o);
						dynamicMap.put(Costanti.MAP_HEADER_RESPONSE_VALUES.toLowerCase(), o);
					}
				}
			}
		}
		if(dynamicMapRequest!=null && !dynamicMapRequest.isEmpty()) {
			Iterator<String> it = dynamicMapRequest.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if(Costanti.MAP_ERROR_HANDLER_OBJECT.equalsIgnoreCase(key)
						|| 
					(Costanti.MAP_REQUEST.equalsIgnoreCase(key) && !preserveRequest)
						){
					continue; // error handler viene usato quello istanziato per la risposta; mentre la richiesta è già stata consumata.
				}
				Object o = dynamicMapRequest.get(key);
				if(o instanceof PatternExtractor) {
					PatternExtractor pe = (PatternExtractor) o;
					pe.refreshContent();
				}
				dynamicMap.put(key, o);
			}
		}
	}
	private static void fillDynamicMapEngine(Logger log, Map<String, Object> dynamicMap, Context pddContext, String urlInvocazione,
			OpenSPCoop2Message message,
			MessageContent messageContent,
			Busta busta, 
			Map<String, List<String>> trasporto, 
			Map<String, List<String>> url,
			Map<String, List<String>> form,
			ErrorHandler errorHandler) {
		DynamicInfo dInfo = new DynamicInfo();
		dInfo.setBusta(busta);
		dInfo.setPddContext(pddContext);
		if(trasporto!=null) { 
			/**	&& !trasporto.isEmpty()) {
			//Map<String, String> pNew = new HashMap<>();
			//pNew.putAll(trasporto);
			//dInfo.setTrasporto(pNew);
			// Fix per permettere la modifica degli header direttamente dentro la trasformazione */
			dInfo.setHeaders(trasporto);
		}
		if(url!=null) {
			/** && !url.isEmpty()) {
			//Map<String, String> pNew = new HashMap<>();
			//pNew.putAll(url);
			//dInfo.setQueryParameters(pNew);
			// Fix per permettere la modifica dei parametri direttamente dentro la trasformazione */
			dInfo.setParameters(url);
		}
		if(form!=null) {
			/** && !url.isEmpty()) {
			//Map<String, String> pNew = new HashMap<>();
			//pNew.putAll(url);
			//dInfo.setQueryParameters(pNew);
			// Fix per permettere la modifica dei parametri direttamente dentro la trasformazione */
			dInfo.setFormParameters(form);
		}
		if(urlInvocazione!=null) {
			dInfo.setUrl(urlInvocazione);
		}
		if(messageContent!=null) {
			dInfo.setMessageContent(messageContent);
		}
		if(message!=null) {
			dInfo.setMessage(message);
		}
		dInfo.setErrorHandler(errorHandler);
		DynamicUtils.fillDynamicMap(log, dynamicMap, dInfo);		
    }
	
	@SuppressWarnings("unchecked")
	public static void fillDynamicMap(Logger log, Map<String, Object> dynamicMap, DynamicInfo dynamicInfo) {
		if(!dynamicMap.containsKey(Costanti.MAP_DATE_OBJECT)) {
			dynamicMap.put(Costanti.MAP_DATE_OBJECT, DateManager.getDate());
		}
		
		RequestInfo requestInfo = null;
		
		if(dynamicInfo!=null && dynamicInfo.getPddContext()!=null) {
			if(!dynamicMap.containsKey(Costanti.MAP_CTX_OBJECT)) {
				dynamicMap.put(Costanti.MAP_CTX_OBJECT, dynamicInfo.getPddContext());
			}
			if(!dynamicMap.containsKey(Costanti.MAP_SYNC_CTX_OBJECT) &&
					dynamicInfo.getPddContext().containsKey(CostantiPdD.CONTESTO_RICHIESTA_MESSAGGIO_NOTIFICA)) {
				Context transactionSyncContext = (Context)dynamicInfo.getPddContext().getObject(CostantiPdD.CONTESTO_RICHIESTA_MESSAGGIO_NOTIFICA);
				dynamicMap.put(Costanti.MAP_SYNC_CTX_OBJECT, transactionSyncContext);
			}
			if(!dynamicMap.containsKey(Costanti.MAP_TRANSACTION_ID_OBJECT) &&
				dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
				String idTransazione = (String)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
				dynamicMap.put(Costanti.MAP_TRANSACTION_ID_OBJECT, idTransazione);
			}
			if(dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			if(!dynamicMap.containsKey(Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT) &&
				requestInfo!=null) {
				if(requestInfo.getProtocolContext()!=null) {
					dynamicMap.put(Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT, requestInfo.getProtocolContext());
					dynamicMap.put(Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT.toLowerCase(), requestInfo.getProtocolContext());
				}
				if(requestInfo.getIdServizio()!=null) {
					AttachmentsReader aReader = new AttachmentsReader(requestInfo.getIdServizio(), requestInfo);
					dynamicMap.put(Costanti.MAP_ATTACHMENTS_OBJECT, aReader);
				}
			}
			if(!dynamicMap.containsKey(Costanti.MAP_INTEGRATION)) {
				Object oInformazioniIntegrazione = dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.INFORMAZIONI_INTEGRAZIONE);
	    		if(oInformazioniIntegrazione!=null) {
	    			InformazioniIntegrazione informazioniIntegrazione = (InformazioniIntegrazione) oInformazioniIntegrazione;
	    			dynamicMap.put(Costanti.MAP_INTEGRATION, informazioniIntegrazione);
	    		}
			}
			if(!dynamicMap.containsKey((Costanti.MAP_INTEGRATION+Costanti.MAP_SUFFIX_RESPONSE))) {
				Object oInformazioniIntegrazione = dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.INFORMAZIONI_INTEGRAZIONE_RISPOSTA);
	    		if(oInformazioniIntegrazione!=null) {
	    			InformazioniIntegrazione informazioniIntegrazione = (InformazioniIntegrazione) oInformazioniIntegrazione;
	    			dynamicMap.put((Costanti.MAP_INTEGRATION+Costanti.MAP_SUFFIX_RESPONSE), informazioniIntegrazione);
	    			dynamicMap.put((Costanti.MAP_INTEGRATION+Costanti.MAP_SUFFIX_RESPONSE).toLowerCase(), informazioniIntegrazione);
	    		}
			}
			if(!dynamicMap.containsKey(Costanti.MAP_TOKEN_INFO)) {
				Object oInformazioniTokenNormalizzate = dynamicInfo.getPddContext().getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniTokenNormalizzate!=null) {
	    			InformazioniToken informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
	    			dynamicMap.put(Costanti.MAP_TOKEN_INFO, informazioniTokenNormalizzate);
	    			dynamicMap.put(Costanti.MAP_TOKEN_INFO.toLowerCase(), informazioniTokenNormalizzate);
	    		}
			}
			if(!dynamicMap.containsKey(Costanti.MAP_ATTRIBUTES)) {
				Object oInformazioniAttributiNormalizzati = dynamicInfo.getPddContext().getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniAttributiNormalizzati!=null) {
	    			InformazioniAttributi informazioniAttributiNormalizzati = (InformazioniAttributi) oInformazioniAttributiNormalizzati;
	    			dynamicMap.put(Costanti.MAP_ATTRIBUTES, informazioniAttributiNormalizzati);
	    		}
			}
			if(!dynamicMap.containsKey(Costanti.MAP_SECURITY_TOKEN)) {
				Object oSecToken = dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SECURITY_TOKEN);
	    		if(oSecToken!=null) {
	    			SecurityToken securityToken = (SecurityToken) oSecToken;
	    			dynamicMap.put(Costanti.MAP_SECURITY_TOKEN, securityToken);
	    		}
			}
			if (!dynamicMap.containsKey(Costanti.MAP_API_IMPL_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_CONFIGURAZIONE);
				}
				if(configProperties==null) {
					configProperties = new HashMap<>();
				}
				dynamicMap.put(Costanti.MAP_API_IMPL_CONFIG_PROPERTY, configProperties);
			}
			if (!dynamicMap.containsKey(Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO);
				}
				if(configProperties==null) {
					configProperties = new HashMap<>();
				}
				dynamicMap.put(Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY, configProperties);
				dynamicMap.put(Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY.toLowerCase(), configProperties);
			}
			if (!dynamicMap.containsKey(Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE);
				}
				if(configProperties==null) {
					configProperties = new HashMap<>();
				}
				dynamicMap.put(Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY, configProperties);
				dynamicMap.put(Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY.toLowerCase(), configProperties);
			}
			if (!dynamicMap.containsKey(Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_EROGATORE)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_EROGATORE);
				}
				if(configProperties==null) {
					configProperties = new HashMap<>();
				}
				dynamicMap.put(Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY, configProperties);
				dynamicMap.put(Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY.toLowerCase(), configProperties);
			}
			if (!dynamicMap.containsKey(Costanti.MAP_APPLICATIVO_TOKEN)) {
				IDServizioApplicativo idApplicativoToken = null;
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
					idApplicativoToken = (IDServizioApplicativo)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
				}
				if(idApplicativoToken!=null) {
					dynamicMap.put(Costanti.MAP_APPLICATIVO_TOKEN, idApplicativoToken);
					dynamicMap.put(Costanti.MAP_APPLICATIVO_TOKEN.toLowerCase(), idApplicativoToken);
				}
			}
			if (!dynamicMap.containsKey(Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO_TOKEN)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO_TOKEN);
				}
				if(configProperties==null) {
					configProperties = new HashMap<>();
				}
				dynamicMap.put(Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY, configProperties);
				dynamicMap.put(Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY.toLowerCase(), configProperties);
			}
			if (!dynamicMap.containsKey(Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN);
				}
				if(configProperties==null) {
					configProperties = new HashMap<>();
				}
				dynamicMap.put(Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY, configProperties);
				dynamicMap.put(Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY.toLowerCase(), configProperties);
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_BUSTA_OBJECT) && dynamicInfo!=null && dynamicInfo.getBusta()!=null) {
			dynamicMap.put(Costanti.MAP_BUSTA_OBJECT, dynamicInfo.getBusta());
		}
		if(!dynamicMap.containsKey(Costanti.MAP_BUSTA_PROPERTY) && dynamicInfo!=null && 
				dynamicInfo.getBusta()!=null && dynamicInfo.getBusta().sizeProperties()>0) {
			Map<String, String> propertiesBusta = new HashMap<>();
			String[] pNames = dynamicInfo.getBusta().getPropertiesNames();
			if(pNames!=null && pNames.length>0) {
				for (int j = 0; j < pNames.length; j++) {
					String pName = pNames[j];
					String pValue = dynamicInfo.getBusta().getProperty(pName);
					if(pValue!=null) {
						propertiesBusta.put(pName, pValue);
					}
				}
			}
			if(!propertiesBusta.isEmpty()) {
				dynamicMap.put(Costanti.MAP_BUSTA_PROPERTY, propertiesBusta);
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_HEADER)) {
			if(dynamicInfo!=null && dynamicInfo.getHeaders()!=null && !dynamicInfo.getHeaders().isEmpty()) {
				dynamicMap.put(Costanti.MAP_HEADER, TransportUtils.convertToMapSingleValue(dynamicInfo.getHeaders()));
			}
			else {
				dynamicMap.put(Costanti.MAP_HEADER, new HashMap<>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_HEADER_VALUES)) {
			if(dynamicInfo!=null && dynamicInfo.getHeaders()!=null) {
				dynamicMap.put(Costanti.MAP_HEADER_VALUES, dynamicInfo.getHeaders());
			}
			else {
				dynamicMap.put(Costanti.MAP_HEADER_VALUES, new HashMap<>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_QUERY_PARAMETER)) {
			if(dynamicInfo!=null && dynamicInfo.getParameters()!=null && !dynamicInfo.getParameters().isEmpty()) {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER, TransportUtils.convertToMapSingleValue(dynamicInfo.getParameters()));
			}
			else {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER, new HashMap<>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_QUERY_PARAMETER_VALUES)) {
			if(dynamicInfo!=null && dynamicInfo.getParameters()!=null) {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER_VALUES, dynamicInfo.getParameters());
			}
			else {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER_VALUES, new HashMap<>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_FORM_PARAMETER)) {
			if(dynamicInfo!=null && dynamicInfo.getFormParameters()!=null && !dynamicInfo.getFormParameters().isEmpty()) {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER, TransportUtils.convertToMapSingleValue(dynamicInfo.getFormParameters()));
			}
			else {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER, new HashMap<>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_FORM_PARAMETER_VALUES)) {
			if(dynamicInfo!=null && dynamicInfo.getFormParameters()!=null) {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER_VALUES, dynamicInfo.getFormParameters());
			}
			else {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER_VALUES, new HashMap<>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		try {
			SystemPropertiesReader systemPropertiesReader = new SystemPropertiesReader(log, requestInfo);
			dynamicMap.put(Costanti.MAP_SYSTEM_PROPERTY, systemPropertiesReader);
			dynamicMap.put(Costanti.MAP_SYSTEM_PROPERTY.toLowerCase(), systemPropertiesReader);
		}
		catch(Exception e) {
			log.error("Creazione system properties reader fallita: "+e.getMessage(),e);
		}
		
		EnvironmentPropertiesReader environmentPropertiesReader = new EnvironmentPropertiesReader(log);
		dynamicMap.put(Costanti.MAP_ENV_PROPERTY, environmentPropertiesReader);
		dynamicMap.put(Costanti.MAP_ENV_PROPERTY.toLowerCase(), environmentPropertiesReader);
		
		JavaPropertiesReader javaPropertiesReader = new JavaPropertiesReader(log);
		dynamicMap.put(Costanti.MAP_JAVA_PROPERTY, javaPropertiesReader);
		dynamicMap.put(Costanti.MAP_JAVA_PROPERTY.toLowerCase(), javaPropertiesReader);
		
		// questi sottostanti, non sono disponnibili sul connettore
		if(dynamicInfo!=null && dynamicInfo.getUrl()!=null) {
			URLRegExpExtractor urle = new URLRegExpExtractor(dynamicInfo.getUrl(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_URL_REGEXP, urle);
			dynamicMap.put(Costanti.MAP_ELEMENT_URL_REGEXP.toLowerCase(), urle);
		}
		if(dynamicInfo!=null && dynamicInfo.getMessageContent()!=null && 
				(dynamicInfo.getMessageContent().isXml() || dynamicInfo.getMessageContent().isRestMultipart())) {
			OpenSPCoop2MessageFactory messageFactory = dynamicInfo.getMessage()!=null ? dynamicInfo.getMessage().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			PatternExtractor pe = new PatternExtractor(messageFactory, dynamicInfo.getMessageContent(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_XML_XPATH, pe);
			dynamicMap.put(Costanti.MAP_ELEMENT_XML_XPATH.toLowerCase(), pe);
		}
		if(dynamicInfo!=null && dynamicInfo.getMessageContent()!=null && 
				(dynamicInfo.getMessageContent().isJson() || dynamicInfo.getMessageContent().isRestMultipart())) {
			OpenSPCoop2MessageFactory messageFactory = dynamicInfo.getMessage()!=null ? dynamicInfo.getMessage().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			PatternExtractor pe = new PatternExtractor(messageFactory, dynamicInfo.getMessageContent(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_JSON_PATH, pe);
			dynamicMap.put(Costanti.MAP_ELEMENT_JSON_PATH.toLowerCase(), pe);
		}
		if(dynamicInfo!=null && dynamicInfo.getMessage()!=null) {
			ContentExtractor content = new ContentExtractor(dynamicInfo.getMessage(), dynamicInfo.getPddContext(), log);
			if(MessageRole.REQUEST.equals(dynamicInfo.getMessage().getMessageRole())) {
				dynamicMap.put(Costanti.MAP_REQUEST, content);
			}
			else {
				dynamicMap.put(Costanti.MAP_RESPONSE, content);
			}
		}
		if(dynamicInfo!=null && dynamicInfo.getErrorHandler()!=null) {
			dynamicMap.put(Costanti.MAP_ERROR_HANDLER_OBJECT, dynamicInfo.getErrorHandler());
			dynamicMap.put(Costanti.MAP_ERROR_HANDLER_OBJECT.toLowerCase(), dynamicInfo.getErrorHandler());
		}
		
		if(!dynamicMap.containsKey(Costanti.MAP_DYNAMIC_CONFIG_PROPERTY)) {
			dynamicMap.put(Costanti.MAP_DYNAMIC_CONFIG_PROPERTY, new DynamicConfig(log, dynamicMap, requestInfo, 
					dynamicInfo!=null ? dynamicInfo.getBusta() : null));
		}
		
	}
	
	
	
	// DYNAMIC MAP
	
	// Mappa che non contiene 'response' field
	@Deprecated
	// Cercare sempre di passare l'oggetto busta
	public static Map<String, Object> buildDynamicMap(OpenSPCoop2Message msg, Context context, Logger log, 
			boolean bufferMessageReadOnly) throws DynamicException {
		return buildDynamicMap(msg, context, null, log, 
				bufferMessageReadOnly);
	}
	public static Map<String, Object> buildDynamicMap(OpenSPCoop2Message msg, Context context, Busta busta, Logger log, 
			boolean bufferMessageReadOnly) throws DynamicException {
		return buildDynamicMapEngine(msg, context, busta, log, 
				bufferMessageReadOnly, 
				null);
	}
	
	// Mappa che contiene 'response' field
	public static Map<String, Object> buildDynamicMapResponse(OpenSPCoop2Message msg, Context context, Busta busta, Logger log, 
			boolean bufferMessageReadOnly,
			Map<String, Object>  dynamicMapRequest) throws DynamicException {
		return buildDynamicMapEngine(msg, context, busta, log, 
				bufferMessageReadOnly, 
				dynamicMapRequest);
	}
	
	private static Map<String, Object> buildDynamicMapEngine(OpenSPCoop2Message msg, Context context, Busta busta, Logger log, 
			boolean bufferMessageReadOnly, 
			Map<String, Object>  dynamicMapRequest) throws DynamicException {
		
		/* Costruisco dynamic Map */
		
		DynamicInfo dInfo = DynamicUtils.readDynamicInfo(msg, 
				bufferMessageReadOnly, context);
		MessageContent messageContent = dInfo.getMessageContent();
		Map<String, List<String>> parametriTrasporto = dInfo.getHeaders();
		Map<String, List<String>> parametriUrl = dInfo.getParameters();
		Map<String, List<String>> parametriForm = dInfo.getFormParameters();
		String urlInvocazione = dInfo.getUrl();
		Map<String, Object> dynamicMap = new HashMap<>();
		ErrorHandler errorHandler = new ErrorHandler();
		
		if(dynamicMapRequest!=null) {
			fillDynamicMapResponse(log, dynamicMap, dynamicMapRequest, context,
					msg,
					messageContent,
					busta, 
					parametriTrasporto,
					errorHandler); 
			return dynamicMap;
		}
	
		DynamicUtils.fillDynamicMapRequest(log, dynamicMap, context, urlInvocazione,
				msg,
				messageContent,
				busta, 
				parametriTrasporto, 
				parametriUrl,
				parametriForm,
				errorHandler);
		return dynamicMap;

	}
	
	
	
	
	
	
	// READ DYNAMIC INFO
	
	public static DynamicInfo readDynamicInfo(OpenSPCoop2Message message, boolean bufferMessageReadOnly, Context context) throws DynamicException {
		MessageContent content = null;
		Map<String, List<String>> parametriTrasporto = null;
		Map<String, List<String>> parametriUrl = null;
		Map<String, List<String>> parametriForm = null;
		String urlInvocazione = null;
		
		try{
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
				content = new MessageContent(soapMessage, bufferMessageReadOnly, context);
			}
			else{
				if(MessageType.XML.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestXmlMessage xml = message.castAsRestXml();
					content = new MessageContent(xml, bufferMessageReadOnly, context);
				}
				else if(MessageType.JSON.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestJsonMessage json = message.castAsRestJson();
					content = new MessageContent(json, bufferMessageReadOnly, context);
				}
			}
			

			if(message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getHeaders()!=null &&
					!message.getTransportRequestContext().getHeaders().isEmpty()) {
					parametriTrasporto = message.getTransportRequestContext().getHeaders();
				}
				if(message.getTransportRequestContext().getParameters()!=null &&
						!message.getTransportRequestContext().getParameters().isEmpty()) {
					parametriUrl = message.getTransportRequestContext().getParameters();
				}
				if(message.getTransportRequestContext() instanceof HttpServletTransportRequestContext) {
					HttpServletTransportRequestContext httpServletContext = (HttpServletTransportRequestContext) message.getTransportRequestContext();
					HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
					if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
						FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
						if(formServlet.getFormUrlEncodedParametersValues()!=null &&
								!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
							parametriForm = formServlet.getFormUrlEncodedParametersValues();
						}
					}
				}
				urlInvocazione = message.getTransportRequestContext().getUrlInvocazione_formBased();
			}
			//else
			// se c'e' la risposta devo usare quello come parametri della risposta
				if(message.getTransportResponseContext()!=null &&
						message.getTransportResponseContext().getHeaders()!=null &&
						!message.getTransportResponseContext().getHeaders().isEmpty()) {
					parametriTrasporto = message.getTransportResponseContext().getHeaders();
			}
			
		}catch(Exception e){
			throw new DynamicException(e.getMessage(),e);
		}
		
		DynamicInfo dInfo = new DynamicInfo();
		dInfo.setMessage(message);
		dInfo.setMessageContent(content);
		dInfo.setHeaders(parametriTrasporto);
		dInfo.setParameters(parametriUrl);
		dInfo.setFormParameters(parametriForm);
		dInfo.setUrl(urlInvocazione);
		return dInfo;
	}
	
	
	
	
	// *** TEMPLATE GOVWAY ***
	
	private static String initTemplateValue(String tmpParam, boolean forceStartWithDollaro,Context pddContext) {
		String tmp = tmpParam;
		if(!forceStartWithDollaro) {
			// per retrocompatibilità nel connettore file gestisco entrambi
			while(tmp.contains("${")) {
				tmp = tmp.replace("${", "{");
			}
		}
		
		String transactionIdConstant = Costanti.MAP_TRANSACTION_ID;
		if(forceStartWithDollaro) {
			transactionIdConstant = "$"+transactionIdConstant;
		}
		if(tmp.contains(transactionIdConstant)){
			String idTransazione = (String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			while(tmp.contains(transactionIdConstant)){
				tmp = tmp.replace(transactionIdConstant, idTransazione);
			}
		}
		
		if(forceStartWithDollaro) {
			transactionIdConstant = Costanti.MAP_TRANSACTION_ID;
			transactionIdConstant = "?"+transactionIdConstant;
			if(tmp.contains(transactionIdConstant)){
				String idTransazione = (String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
				while(tmp.contains(transactionIdConstant)){
					tmp = tmp.replace(transactionIdConstant, idTransazione);
				}
			}
		}
				
		return tmp;
	}
	
	public static void validate(String name,String tmpParam, boolean addPrefixError) throws DynamicException{
		validate(name, tmpParam, true, addPrefixError);
	}
	public static void validate(String name,String tmpParam, boolean forceStartWithDollaro, boolean addPrefixError) throws DynamicException{
		Context pddContext = new Context();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, "fakeId");
		String tmp = initTemplateValue(tmpParam, forceStartWithDollaro, pddContext);
		
		boolean onlyValidate = true;
		tmp = processTemplateValueUrlXpathJsonpath(tmp, onlyValidate, null, forceStartWithDollaro);
		
		try{
			DynamicStringReplace.validate(tmp, forceStartWithDollaro);
		}catch(Exception e){
			String prefix = addPrefixError ? "Proprieta' '"+name+"' contiene un valore non corretto: " : "";
			throw new DynamicException(prefix+e.getMessage(),e);
		}
	}
	
	public static String convertDynamicPropertyValue(String name,String tmpParam,Map<String,Object> dynamicMap,Context pddContext) throws DynamicException{
		return convertDynamicPropertyValue(name, tmpParam, dynamicMap, pddContext, true);
	}
	public static String convertDynamicPropertyValue(String name,String tmpParam,Map<String,Object> dynamicMap,Context pddContext, boolean forceStartWithDollaro) throws DynamicException{

		String tmp = initTemplateValue(tmpParam, forceStartWithDollaro, pddContext);
				
		boolean onlyValidate = false;
		tmp = processTemplateValueUrlXpathJsonpath(tmp, onlyValidate, dynamicMap, forceStartWithDollaro);
		
		try{
			tmp = DynamicStringReplace.replace(tmp, dynamicMap, forceStartWithDollaro);
		}catch(Exception e){
			throw new DynamicException("Proprieta' '"+name+"' contiene un valore non corretto: "+e.getMessage(),e);
		}
		return tmp;
	}
	
	private static String processTemplateValueUrlXpathJsonpath(String tmpParam, boolean onlyValidate, Map<String,Object> dynamicMap,
			boolean forceStartWithDollaro) throws DynamicException {
		
		String tmp = tmpParam;
		boolean request = false;
		boolean response = true;
		
		// conversione url
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.URL,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.URL,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione xpath
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.XML,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.XML,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione jsonpath
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.JSON,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.JSON,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione system properties
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.SYSTEM,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.SYSTEM,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione env properties
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.ENV,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.ENV,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione java properties
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.JAVA,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.JAVA,
				forceStartWithDollaro, response,
				onlyValidate);
		
		return tmp;
	}
	
	private static String convertDynamicPropertyContent(String tmpOriginal, Map<String,Object> dynamicMap, 
			TemplateType templateType, 
			boolean forceStartWithDollaro, boolean response,
			boolean onlyValidate) throws DynamicException {
		
		if(tmpOriginal==null) {
			return null; // precedente replace ha causato un null tramite opzione ?
		}
		
		String istruzione = null;
		String prefix = null;
		switch (templateType) {
		case XML:
			istruzione = Costanti.MAP_ELEMENT_XML_XPATH;
			prefix = Costanti.MAP_ELEMENT_XML_XPATH_PREFIX;
			break;
		case JSON:
			istruzione = Costanti.MAP_ELEMENT_JSON_PATH;
			prefix = Costanti.MAP_ELEMENT_JSON_PATH_PREFIX;
			break;
		case URL:
			istruzione = Costanti.MAP_ELEMENT_URL_REGEXP;
			prefix = Costanti.MAP_ELEMENT_URL_REGEXP_PREFIX;
			break;
		case SYSTEM:
			istruzione = Costanti.MAP_SYSTEM_PROPERTY;
			prefix = Costanti.MAP_SYSTEM_PROPERTY_PREFIX;
			break;
		case ENV:
			istruzione = Costanti.MAP_ENV_PROPERTY;
			prefix = Costanti.MAP_ENV_PROPERTY_PREFIX;
			break;
		case JAVA:
			istruzione = Costanti.MAP_JAVA_PROPERTY;
			prefix = Costanti.MAP_JAVA_PROPERTY_PREFIX;
			break;
		}
		

		String tmp = tmpOriginal;
		if(forceStartWithDollaro) {
			String prefixDollaro = "$"+prefix;
			tmp = convertDynamicPropertyContentEngine(tmp, dynamicMap, 
					templateType, 
					response,
					onlyValidate,
					istruzione, prefixDollaro);
			
			String prefixOptional = "?"+prefix;
			tmp = convertDynamicPropertyContentEngine(tmp, dynamicMap, 
					templateType, 
					response,
					onlyValidate,
					istruzione, prefixOptional);
		}
		else {
			tmp = convertDynamicPropertyContentEngine(tmp, dynamicMap, 
					templateType, 
					response,
					onlyValidate,
					istruzione, prefix);
		}

		if(tmpOriginal.trim().startsWith("?{") && tmp!=null && StringUtils.isEmpty(tmp)) {
			return null;
		}
		
		return tmp;
	}
	private static String convertDynamicPropertyContentEngine(String tmp, Map<String,Object> dynamicMap, 
			TemplateType templateType, 
			boolean response,
			boolean onlyValidate,
			String istruzione, String prefix) throws DynamicException {
		if(response) {
			istruzione = istruzione+Costanti.MAP_SUFFIX_RESPONSE;
			prefix = prefix.substring(0,prefix.length()-1);
			prefix = prefix + Costanti.MAP_SUFFIX_RESPONSE + ":";
		}
		
		String tmpLowerCase = tmp.toLowerCase();
		String prefixLowerCase = prefix.toLowerCase();
		
		if(tmpLowerCase.contains(prefixLowerCase)){
			int maxIteration = 100;
			while (maxIteration>0 && tmpLowerCase.contains(prefixLowerCase)) {
				int indexOfStart = tmpLowerCase.indexOf(prefixLowerCase);
				String pattern = tmp.substring(indexOfStart+prefix.length(),tmp.length());
				if(!pattern.contains("}")) {
					throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+"non correttamente formata (chiusura '}' non trovata)");
				}
				
				// cerco chiusura, all'interno ci potrebbero essere altre aperture di { per le regole xpath
				char [] patternChars = pattern.toCharArray();
				int numAperture = 0;
				int positionChiusura = -1;
				for (int i = 0; i < patternChars.length; i++) {
					if(patternChars[i] == '{') {
						numAperture++;
					}
					if(patternChars[i] == '}') {
						if(numAperture==0) {
							positionChiusura = i;
							break;
						}
						else {
							numAperture--;
						}
					}
				}
				if(positionChiusura<=0) {
					throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+"non correttamente formata (chiusura '}' non trovata)");
				}
				
				pattern = pattern.substring(0,positionChiusura);
				
				String complete = tmp.substring(indexOfStart, positionChiusura+indexOfStart+prefix.length()+1);
				String value = null;
				if(!onlyValidate) {
					Object o = dynamicMap.get(istruzione);
					if(o==null) {
						throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+"non utilizzabile in questo contesto");
					}
					switch (templateType) {
					case XML:
					case JSON:
						if( !(o instanceof PatternExtractor) ) {
							throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+NON_UTILIZZABILE_IN_QUESTO_CONTESTO+buildExtractorWrongClassMsg(o));
						}
						PatternExtractor patternExtractor = (PatternExtractor) o;
						value = patternExtractor.read(pattern);
						break;
					case URL:
						if( !(o instanceof URLRegExpExtractor) ) {
							throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+NON_UTILIZZABILE_IN_QUESTO_CONTESTO+buildExtractorWrongClassMsg(o));
						}
						URLRegExpExtractor urlExtractor = (URLRegExpExtractor) o;
						value = urlExtractor.read(pattern);
						break;
					case SYSTEM:
						if( !(o instanceof SystemPropertiesReader) ) {
							throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+NON_UTILIZZABILE_IN_QUESTO_CONTESTO+buildReaderWrongClassMsg(o));
						}
						SystemPropertiesReader systemPropertiesReader = (SystemPropertiesReader) o;
						value = systemPropertiesReader.read(pattern);
						break;
					case ENV:
						if( !(o instanceof EnvironmentPropertiesReader) ) {
							throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+NON_UTILIZZABILE_IN_QUESTO_CONTESTO+buildReaderWrongClassMsg(o));
						}
						EnvironmentPropertiesReader environmentPropertiesReader = (EnvironmentPropertiesReader) o;
						value = environmentPropertiesReader.read(pattern);
						break;
					case JAVA:
						if( !(o instanceof JavaPropertiesReader) ) {
							throw new DynamicException(buildPrefixIstruzioneMsgError(istruzione)+NON_UTILIZZABILE_IN_QUESTO_CONTESTO+buildReaderWrongClassMsg(o));
						}
						JavaPropertiesReader javaPropertiesReader = (JavaPropertiesReader) o;
						value = javaPropertiesReader.read(pattern);
						break;
					}
				}
				if(value==null) {
					value = "";
				}
				tmp = tmp.replace(complete, value);
				tmpLowerCase = tmp.toLowerCase();
				maxIteration--;
			}
		}
		
		return tmp;
	}
	
	private static String buildPrefixIstruzioneMsgError(String istruzione) {
		return "Trovata istruzione '"+istruzione+"' ";
	}
	private static final String NON_UTILIZZABILE_IN_QUESTO_CONTESTO = "non utilizzabile in questo contesto ";
	private static String buildReaderWrongClassMsg(Object o) {
		return "(reader wrong class: "+o.getClass().getName()+")";
	}
	private static String buildExtractorWrongClassMsg(Object o) {
		return "(extractor wrong class: "+o.getClass().getName()+")";
	}
	
	// *** FREEMARKER ***
	
	public static void convertFreeMarkerTemplate(Template template, 
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertFreeMarkerTemplate(template, 
				dynamicMap, out, null);
	}
	public static void convertFreeMarkerTemplate(Template template, 
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {			
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			convertFreeMarkerTemplateEngine(template, 
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public static void convertFreeMarkerTemplate(Template template, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		convertFreeMarkerTemplateEngine(template, 
				dynamicMap, writer);
	}
	
	public static void convertZipFreeMarkerTemplate(ZipTemplate zipTemplate,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertZipFreeMarkerTemplate(zipTemplate,
				dynamicMap, out, null);
	}
	public static void convertZipFreeMarkerTemplate(ZipTemplate zipTemplate,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {			
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			convertZipFreeMarkerTemplate(zipTemplate,  
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static void convertZipFreeMarkerTemplate(ZipTemplate zipTemplate,
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		
		Template template = zipTemplate.getTemplateFreeMarker();
		
		convertFreeMarkerTemplateEngine(template, 
				dynamicMap, writer);
	}
	
	private static void convertFreeMarkerTemplateEngine(Template template, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		try {
			// ** Aggiungo utility per usare metodi statici ed istanziare oggetti
			
			// statici
			BeansWrapper wrapper = new BeansWrapper(Configuration.VERSION_2_3_23);
			TemplateModel classModel = wrapper.getStaticModels();
			dynamicMap.put(Costanti.MAP_CLASS_LOAD_STATIC, classModel);
			
			// newObject
			dynamicMap.put(Costanti.MAP_CLASS_NEW_INSTANCE, new freemarker.template.utility.ObjectConstructor());
			
			// ** costruisco template
			freemarker.template.Template templateFTL = template.getTemplateFreeMarker();
			
			// ** applico trasformazione
			templateFTL.process(dynamicMap, writer);
			writer.flush();
			
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public static freemarker.template.Template buildFreeMarkerTemplate(Template template) throws DynamicException {
		try {
			String name = template.getName();
			byte[] templateBin = template.getTemplate();
			Map<String, byte[]> templateIncludes = template.getTemplateIncludes();
			
			// Configurazione
			freemarker.template.Configuration config = TemplateUtils.newTemplateEngine();
			config.setAPIBuiltinEnabled(true); // serve per modificare le mappe in freemarker
			
			// template includes
			if(templateIncludes!=null && !templateIncludes.isEmpty()) {
				config.setTemplateLoader(new FreemarkerTemplateLoader(templateIncludes));
			}
			
			// costruisco template
			return TemplateUtils.buildTemplate(config, name, templateBin);
			
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	
	
	// *** VELOCITY ***
	
	public static void convertVelocityTemplate(Template template,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertVelocityTemplate(template,
				dynamicMap, out, null);
	}
	public static void convertVelocityTemplate(Template template,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			convertVelocityTemplateEngine(template, 
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public static void convertVelocityTemplate(Template template,
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		convertVelocityTemplateEngine(template,
				dynamicMap, writer);
	}
	
	public static void convertZipVelocityTemplate(ZipTemplate zipTemplate,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertZipVelocityTemplate(zipTemplate,
				dynamicMap, out, null);
	}
	public static void convertZipVelocityTemplate(ZipTemplate zipTemplate,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {			
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			convertZipVelocityTemplate(zipTemplate,  
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static void convertZipVelocityTemplate(ZipTemplate zipTemplate,
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		
		Template template = zipTemplate.getTemplateFreeMarker();
		
		convertVelocityTemplateEngine(template, 
				dynamicMap, writer);
	}
	
	private static void convertVelocityTemplateEngine(Template template, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		try {
			// ** Aggiungo utility per usare metodi statici ed istanziare oggetti
			
			// statici
			dynamicMap.put(Costanti.MAP_CLASS_LOAD_STATIC, "".getClass());
			
			// newObject
			dynamicMap.put(Costanti.MAP_CLASS_NEW_INSTANCE, new ObjectConstructor());
			
			// ** costruisco template
			org.apache.velocity.Template templateVelocity = template.getTemplateVelocity();
			
			// ** applico trasformazione
			templateVelocity.merge(VelocityTemplateUtils.toVelocityContext(dynamicMap), writer);
			writer.flush();
			
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public static org.apache.velocity.Template buildVelocityTemplate(Template template) throws DynamicException {
		try {
			String name = template.getName();
			byte[] templateBin = template.getTemplate();
			Map<String, byte[]> templateIncludes = template.getTemplateIncludes();
			
			// Configurazione
			return VelocityTemplateUtils.buildTemplate(name, templateBin, templateIncludes);
					
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
		
	
	
	// *** XSLT ***
	
	private static boolean xsltProcessAsDomSource = true;
	
	public static boolean isXsltProcessAsDomSource() {
		return xsltProcessAsDomSource;
	}
	public static void setXsltProcessAsDomSource(boolean xsltProcessAsDomSource) {
		DynamicUtils.xsltProcessAsDomSource = xsltProcessAsDomSource;
	}
	public static void convertXSLTTemplate(String name, byte[] template, Element element, OutputStream out) throws DynamicException {
		try {
			Source xsltSource = null;
			if(xsltProcessAsDomSource) {
				xsltSource = new DOMSource(org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(template)); 	
			}
			else {
				xsltSource = new StreamSource(new ByteArrayInputStream(template));
			}
			Source xmlSource = new DOMSource(element);
			Transformer trans = MessageXMLUtils.DEFAULT.getTransformerFactory().newTransformer(xsltSource);
			trans.transform(xmlSource, new StreamResult(out));
			out.flush();
		}catch(Exception e) {
			throw new DynamicException("["+name+"] "+e.getMessage(),e);
		}
	}
	
	
	
	// *** COMPRESS ***
	
	public static void convertCompressorTemplate(String name,byte[] template,Map<String,Object> dynamicMap,Context pddContext, 
			ArchiveType archiveType, OutputStream out) throws DynamicException{
		try {
			try(ByteArrayInputStream bin = new ByteArrayInputStream(template)){
				java.util.Properties p = new java.util.Properties();
				p.load(bin);
				
				ContentExtractor contentExtractor = null;
				String ruolo = null;
				if(dynamicMap.containsKey(Costanti.MAP_REQUEST)) {
					contentExtractor = (ContentExtractor) dynamicMap.get(Costanti.MAP_REQUEST);
					ruolo = "messaggio di richiesta";
				}
				else if(dynamicMap.containsKey(Costanti.MAP_RESPONSE)) {
					contentExtractor = (ContentExtractor) dynamicMap.get(Costanti.MAP_RESPONSE);
					ruolo = "messaggio di risposta";
				}
				
				List<Entry> listEntries = new ArrayList<>();
				
				Enumeration<?> keys = p.keys();
				while (keys.hasMoreElements()) {
					String keyP = (String) keys.nextElement();
					keyP = keyP.trim();
					
					String oggetto = "property-"+keyP;
					String entryName = convertCompressorTemplateReadDynamicValueEntryName(oggetto, keyP, dynamicMap, pddContext);
					String prefixError = "["+keyP+"] ";
					
					String valoreP = p.getProperty(keyP);
					if(valoreP==null) {
						throw new DynamicException(prefixError+"Nessun valore fornito per la proprietà");
					}
					valoreP = valoreP.trim();
					
					byte[] content = converCompressTemplateReadContent(prefixError, keyP, valoreP, contentExtractor, ruolo,
							dynamicMap, pddContext);
					
					listEntries.add(new Entry(entryName, content));
				}
				
				out.write(CompressorUtilities.archive(listEntries, archiveType));
			}
		}catch(Exception e) {
			throw new DynamicException("["+name+"] "+e.getMessage(),e);
		}
		
	}
	private static byte[] converCompressTemplateReadContent(String prefixError, String keyP, String valoreP, ContentExtractor contentExtractor, String ruolo,
			Map<String,Object> dynamicMap,Context pddContext) throws DynamicException {
		byte[] content = null;
		if(Costanti.COMPRESS_CONTENT.equals(valoreP)) {
			if(contentExtractor==null || !contentExtractor.hasContent()) {
				throw new DynamicException(prefixError+"Il "+ruolo+" non possiede un payload");
			}
			content = contentExtractor.getContent();
		}
		else if(Costanti.COMPRESS_ENVELOPE.equals(valoreP) ||
				Costanti.COMPRESS_BODY.equals(valoreP) ||
				Costanti.COMPRESS_ENVELOPE.equalsIgnoreCase(valoreP) ||
				Costanti.COMPRESS_BODY.equalsIgnoreCase(valoreP)) {
			content = convertCompressorTemplateReadEnvelope(prefixError, valoreP, contentExtractor, ruolo);
		}
		else if(valoreP.startsWith(Costanti.COMPRESS_ATTACH_PREFIX) ||
				valoreP.startsWith(Costanti.COMPRESS_ATTACH_BY_ID_PREFIX) &&
				valoreP.endsWith(Costanti.COMPRESS_SUFFIX)) {
			content = convertCompressorTemplateReadAttachContent(prefixError, valoreP, contentExtractor, ruolo);
		}
		else {
			String oggettoV = "valore-"+keyP;
			content = convertCompressorTemplateReadDynamicContent(prefixError, oggettoV, valoreP, dynamicMap, pddContext);
		}
		
		if(content==null || content.length==0) {
			throw new DynamicException(prefixError+"Nessun contenuto da associare alla entry trovato");
		}
		return content;
	}
	private static int converCompressTemplateReadAttachmentIndex(String prefixError, String valoreInterno) throws DynamicException {
		int index = -1;
		try {
			index = Integer.valueOf(valoreInterno);
		}catch(Exception e) {
			throw new DynamicException(prefixError+"L'indice definito per l'attachment non è un numero intero: "+e.getMessage(),e);
		}
		return index;
	}
	private static String convertCompressorTemplateReadDynamicValueEntryName(String oggetto, String key, Map<String,Object> dynamicMap,Context pddContext) throws DynamicException {
		try {
			return DynamicUtils.convertDynamicPropertyValue(oggetto, key, dynamicMap, pddContext);
		}catch(Exception e) {
			throw new DynamicException("["+oggetto+"] Conversione valore per entry name '"+key+"' non riuscita: "+e.getMessage(),e);
		}
	}
	private static byte[] convertCompressorTemplateReadEnvelope(String prefixError, String valoreP, ContentExtractor contentExtractor, String ruolo) throws DynamicException {
		byte[] content = null;
		if(contentExtractor==null || !contentExtractor.hasContent()) {
			throw new DynamicException(prefixError+"Il "+ruolo+" non possiede un payload");
		}
		if(!contentExtractor.isSoap()) {
			throw new DynamicException(prefixError+"Il "+ruolo+" non è un messaggio soap");
		}
		if(Costanti.COMPRESS_ENVELOPE.equals(valoreP) ||
				Costanti.COMPRESS_ENVELOPE.equalsIgnoreCase(valoreP)) {
			DumpMessaggio dump = contentExtractor.dumpMessage();
			if(dump==null) {
				throw new DynamicException(prefixError+"Dump del "+ruolo+" non disponibile");
			}
			content = dump.getBody();
		}
		else {
			content = contentExtractor.getContentSoapBody();
		}
		return content;
	}
	private static byte[] convertCompressorTemplateReadDynamicContent(String prefixError, String oggettoV, String valoreP, Map<String,Object> dynamicMap,Context pddContext) throws DynamicException {
		try {
			String v = DynamicUtils.convertDynamicPropertyValue(oggettoV, valoreP, dynamicMap, pddContext);
			if(v!=null) {
				return v.getBytes();
			}
		}catch(Exception e) {
			throw new DynamicException(prefixError+"["+oggettoV+"] Conversione valore non riuscita: "+e.getMessage(),e);
		}
		return new byte[0];
	}
	private static byte[] convertCompressorTemplateReadAttachContent(String prefixError, String valoreP, ContentExtractor contentExtractor, String ruolo) throws DynamicException {
		String valoreInterno = valoreP.substring(Costanti.COMPRESS_ATTACH_PREFIX.length(), valoreP.length()-1);
		if(valoreInterno==null || "".equals(valoreInterno)) {
			throw new DynamicException(prefixError+"Non è stato definito un indice per l'attachment");
		}
		DumpMessaggio dump = contentExtractor.dumpMessage();
		if(dump==null) {
			throw new DynamicException(prefixError+"Dump del "+ruolo+" non disponibile");
		}
		
		DumpAttachment attach = null;
		boolean attachAtteso = true;
		byte[] content = null;
		if(valoreP.startsWith(Costanti.COMPRESS_ATTACH_PREFIX)) {
			int index = converCompressTemplateReadAttachmentIndex(prefixError, valoreInterno);
			if(contentExtractor.isRest() && index==0) {
				content = dump.getBody();
				attachAtteso = false;
			}
			else {
				attach = convertCompressorTemplateReadDumpAttachment(dump, contentExtractor, index);
			}
		}
		else {
			if(contentExtractor.isRest() &&
					dump.getMultipartInfoBody()!=null &&
					valoreInterno.equals(dump.getMultipartInfoBody().getContentId())) {
				content = dump.getBody();
				attachAtteso = false;
			}
			else {
				attach = dump.getAttachment(valoreInterno);
			}
		}
		
		if(attachAtteso) {
			content = convertCompressorTemplateReadAttachContent(prefixError, attach);
		}
		
		return content;
	}
	private static DumpAttachment convertCompressorTemplateReadDumpAttachment(DumpMessaggio dump, ContentExtractor contentExtractor, int index) {
		DumpAttachment attach = null;
		if(contentExtractor.isRest()) {
			attach = dump.getAttachment((index-1));
		}
		else {
			attach = dump.getAttachment(index);
		}
		return attach;
	}
	private static byte[] convertCompressorTemplateReadAttachContent(String prefixError, DumpAttachment attach) throws DynamicException{
		byte[] content = null;
		if(attach==null) {
			throw new DynamicException(prefixError+"L'indice definito per l'attachment non ha identificato alcun attachment");
		}
		content = attach.getContent();
		return content;
	}

}

enum TemplateType {
	
	XML, JSON, URL, SYSTEM, ENV, JAVA; 
	
}
