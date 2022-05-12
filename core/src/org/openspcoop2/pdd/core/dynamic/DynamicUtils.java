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

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.ArchiveType;
import org.openspcoop2.utils.io.CompressorUtilities;
import org.openspcoop2.utils.io.Entry;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FreemarkerTemplateLoader;
import org.openspcoop2.utils.resources.TemplateUtils;
import org.openspcoop2.utils.resources.VelocityTemplateLoader;
import org.openspcoop2.utils.resources.VelocityTemplateUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

/**
 * DynamicUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicUtils {

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
		_fillDynamicMap(log, dynamicMap, pddContext, urlInvocazione, 
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
		Map<String, Object> dynamicMapResponse = new HashMap<>();
		_fillDynamicMap(log, dynamicMapResponse, pddContext, null, 
				message,
				messageContent,
				busta, 
				trasporto, 
				null, 
				null,
				errorHandler);
		if(dynamicMapResponse!=null && !dynamicMapResponse.isEmpty()) {
			Iterator<String> it = dynamicMapResponse.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object o = dynamicMapResponse.get(key);
				if(Costanti.MAP_ERROR_HANDLER_OBJECT.toLowerCase().equals(key.toLowerCase()) 
						|| 
					Costanti.MAP_RESPONSE.toLowerCase().equals(key.toLowerCase())){
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
				String key = (String) it.next();
				if(Costanti.MAP_ERROR_HANDLER_OBJECT.toLowerCase().equals(key.toLowerCase())
						|| 
					Costanti.MAP_REQUEST.toLowerCase().equals(key.toLowerCase())){
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
	public static void _fillDynamicMap(Logger log, Map<String, Object> dynamicMap, Context pddContext, String urlInvocazione,
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
			//	&& !trasporto.isEmpty()) {
			//Map<String, String> pNew = new HashMap<String, String>();
			//pNew.putAll(trasporto);
			//dInfo.setTrasporto(pNew);
			// Fix per permettere la modifica degli header direttamente dentro la trasformazione
			dInfo.setHeaders(trasporto);
		}
		if(url!=null) {
			//&& !url.isEmpty()) {
			//Map<String, String> pNew = new HashMap<String, String>();
			//pNew.putAll(url);
			//dInfo.setQueryParameters(pNew);
			// Fix per permettere la modifica dei parametri direttamente dentro la trasformazione
			dInfo.setParameters(url);
		}
		if(form!=null) {
			//&& !url.isEmpty()) {
			//Map<String, String> pNew = new HashMap<String, String>();
			//pNew.putAll(url);
			//dInfo.setQueryParameters(pNew);
			// Fix per permettere la modifica dei parametri direttamente dentro la trasformazione
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
		if(dynamicMap.containsKey(Costanti.MAP_DATE_OBJECT)==false) {
			dynamicMap.put(Costanti.MAP_DATE_OBJECT, DateManager.getDate());
		}
		
		if(dynamicInfo!=null && dynamicInfo.getPddContext()!=null && dynamicInfo.getPddContext().getContext()!=null) {
			if(dynamicMap.containsKey(Costanti.MAP_CTX_OBJECT)==false) {
				dynamicMap.put(Costanti.MAP_CTX_OBJECT, dynamicInfo.getPddContext().getContext());
			}
			if(dynamicMap.containsKey(Costanti.MAP_TRANSACTION_ID_OBJECT)==false) {
				if(dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
					String idTransazione = (String)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					dynamicMap.put(Costanti.MAP_TRANSACTION_ID_OBJECT, idTransazione);
				}
			}
			if(dynamicMap.containsKey(Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT)==false) {
				if(dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
					RequestInfo requestInfo = (RequestInfo)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
					if(requestInfo.getProtocolContext()!=null) {
						dynamicMap.put(Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT, requestInfo.getProtocolContext());
						dynamicMap.put(Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT.toLowerCase(), requestInfo.getProtocolContext());
					}
					if(requestInfo.getIdServizio()!=null) {
						AttachmentsReader aReader = new AttachmentsReader(requestInfo.getIdServizio());
						dynamicMap.put(Costanti.MAP_ATTACHMENTS_OBJECT, aReader);
					}
				}
			}
			if(dynamicMap.containsKey(Costanti.MAP_TOKEN_INFO)==false) {
				Object oInformazioniTokenNormalizzate = dynamicInfo.getPddContext().getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniTokenNormalizzate!=null) {
	    			InformazioniToken informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
	    			dynamicMap.put(Costanti.MAP_TOKEN_INFO, informazioniTokenNormalizzate);
	    			dynamicMap.put(Costanti.MAP_TOKEN_INFO.toLowerCase(), informazioniTokenNormalizzate);
	    		}
			}
			if(dynamicMap.containsKey(Costanti.MAP_ATTRIBUTES)==false) {
				Object oInformazioniAttributiNormalizzati = dynamicInfo.getPddContext().getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE);
	    		if(oInformazioniAttributiNormalizzati!=null) {
	    			InformazioniAttributi informazioniAttributiNormalizzati = (InformazioniAttributi) oInformazioniAttributiNormalizzati;
	    			dynamicMap.put(Costanti.MAP_ATTRIBUTES, informazioniAttributiNormalizzati);
	    		}
			}
			if(dynamicMap.containsKey(Costanti.MAP_SECURITY_TOKEN)==false) {
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
					configProperties = new HashMap<String, String>();
				}
				dynamicMap.put(Costanti.MAP_API_IMPL_CONFIG_PROPERTY, configProperties);
			}
			if (!dynamicMap.containsKey(Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY)) {
				Map<String, String> configProperties = null; // aggiungo sempre, piu' pratico il controllo nei template engine
				if (dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO)) {
					configProperties = (Map<String, String>)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO);
				}
				if(configProperties==null) {
					configProperties = new HashMap<String, String>();
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
					configProperties = new HashMap<String, String>();
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
					configProperties = new HashMap<String, String>();
				}
				dynamicMap.put(Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY, configProperties);
				dynamicMap.put(Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY.toLowerCase(), configProperties);
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_BUSTA_OBJECT)==false && dynamicInfo!=null && dynamicInfo.getBusta()!=null) {
			dynamicMap.put(Costanti.MAP_BUSTA_OBJECT, dynamicInfo.getBusta());
		}
		if(dynamicMap.containsKey(Costanti.MAP_BUSTA_PROPERTY)==false && dynamicInfo!=null && 
				dynamicInfo.getBusta()!=null && dynamicInfo.getBusta().sizeProperties()>0) {
			Map<String, String> propertiesBusta = new HashMap<String, String>();
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
		
		if(dynamicMap.containsKey(Costanti.MAP_HEADER)==false) {
			if(dynamicInfo!=null && dynamicInfo.getHeaders()!=null && !dynamicInfo.getHeaders().isEmpty()) {
				dynamicMap.put(Costanti.MAP_HEADER, TransportUtils.convertToMapSingleValue(dynamicInfo.getHeaders()));
			}
			else {
				dynamicMap.put(Costanti.MAP_HEADER, new HashMap<String, String>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_HEADER_VALUES)==false) {
			if(dynamicInfo!=null && dynamicInfo.getHeaders()!=null) {
				dynamicMap.put(Costanti.MAP_HEADER_VALUES, dynamicInfo.getHeaders());
			}
			else {
				dynamicMap.put(Costanti.MAP_HEADER_VALUES, new HashMap<String, List<String>>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_QUERY_PARAMETER)==false) {
			if(dynamicInfo!=null && dynamicInfo.getParameters()!=null && !dynamicInfo.getParameters().isEmpty()) {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER, TransportUtils.convertToMapSingleValue(dynamicInfo.getParameters()));
			}
			else {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER, new HashMap<String, String>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_QUERY_PARAMETER_VALUES)==false) {
			if(dynamicInfo!=null && dynamicInfo.getParameters()!=null) {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER_VALUES, dynamicInfo.getParameters());
			}
			else {
				dynamicMap.put(Costanti.MAP_QUERY_PARAMETER_VALUES, new HashMap<String, List<String>>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_FORM_PARAMETER)==false) {
			if(dynamicInfo!=null && dynamicInfo.getFormParameters()!=null && !dynamicInfo.getFormParameters().isEmpty()) {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER, TransportUtils.convertToMapSingleValue(dynamicInfo.getFormParameters()));
			}
			else {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER, new HashMap<String, String>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_FORM_PARAMETER_VALUES)==false) {
			if(dynamicInfo!=null && dynamicInfo.getFormParameters()!=null) {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER_VALUES, dynamicInfo.getFormParameters());
			}
			else {
				dynamicMap.put(Costanti.MAP_FORM_PARAMETER_VALUES, new HashMap<String, List<String>>()); // aggiungo sempre, piu' pratico il controllo nei template engine
			}
		}
		
		try {
			SystemPropertiesReader systemPropertiesReader = new SystemPropertiesReader(log);
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
		if(dynamicInfo!=null && dynamicInfo.getMessageContent()!=null && dynamicInfo.getMessageContent().isXml()) {
			OpenSPCoop2MessageFactory messageFactory = dynamicInfo.getMessage()!=null ? dynamicInfo.getMessage().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			PatternExtractor pe = new PatternExtractor(messageFactory, dynamicInfo.getMessageContent(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_XML_XPATH, pe);
			dynamicMap.put(Costanti.MAP_ELEMENT_XML_XPATH.toLowerCase(), pe);
		}
		if(dynamicInfo!=null && dynamicInfo.getMessageContent()!=null && dynamicInfo.getMessageContent().isJson()) {
			OpenSPCoop2MessageFactory messageFactory = dynamicInfo.getMessage()!=null ? dynamicInfo.getMessage().getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			PatternExtractor pe = new PatternExtractor(messageFactory, dynamicInfo.getMessageContent(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_JSON_PATH, pe);
			dynamicMap.put(Costanti.MAP_ELEMENT_JSON_PATH.toLowerCase(), pe);
		}
		if(dynamicInfo!=null && dynamicInfo.getMessage()!=null) {
			ContentExtractor content = new ContentExtractor(dynamicInfo.getMessage(), log);
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
	}

	
	
	// DYNAMIC MAP
	
	// Mappa che non contiene 'response' field
	@Deprecated
	// Cercare sempre di passare l'oggetto busta
	public static Map<String, Object> buildDynamicMap(OpenSPCoop2Message msg, Context context, Logger log, 
			boolean bufferMessage_readOnly) throws DynamicException {
		return buildDynamicMap(msg, context, null, log, 
				bufferMessage_readOnly);
	}
	public static Map<String, Object> buildDynamicMap(OpenSPCoop2Message msg, Context context, Busta busta, Logger log, 
			boolean bufferMessage_readOnly) throws DynamicException {
		return _buildDynamicMap(msg, context, busta, log, 
				bufferMessage_readOnly, 
				null);
	}
	
	// Mappa che contiene 'response' field
	public static Map<String, Object> buildDynamicMapResponse(OpenSPCoop2Message msg, Context context, Busta busta, Logger log, 
			boolean bufferMessage_readOnly,
			Map<String, Object>  dynamicMapRequest) throws DynamicException {
		return _buildDynamicMap(msg, context, busta, log, 
				bufferMessage_readOnly, 
				dynamicMapRequest);
	}
	
	private static Map<String, Object> _buildDynamicMap(OpenSPCoop2Message msg, Context context, Busta busta, Logger log, 
			boolean bufferMessage_readOnly, 
			Map<String, Object>  dynamicMapRequest) throws DynamicException {
		
		/* Costruisco dynamic Map */
		
		DynamicInfo dInfo = DynamicUtils.readDynamicInfo(msg, 
				bufferMessage_readOnly, context);
		MessageContent messageContent = dInfo.getMessageContent();
		Map<String, List<String>> parametriTrasporto = dInfo.getHeaders();
		Map<String, List<String>> parametriUrl = dInfo.getParameters();
		Map<String, List<String>> parametriForm = dInfo.getFormParameters();
		String urlInvocazione = dInfo.getUrl();
		Map<String, Object> dynamicMap = new HashMap<String, Object>();
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
	
	public static DynamicInfo readDynamicInfo(OpenSPCoop2Message message, boolean bufferMessage_readOnly, Context context) throws DynamicException {
		MessageContent content = null;
		Map<String, List<String>> parametriTrasporto = null;
		Map<String, List<String>> parametriUrl = null;
		Map<String, List<String>> parametriForm = null;
		String urlInvocazione = null;
		
		try{
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
				content = new MessageContent(soapMessage, bufferMessage_readOnly, context);
			}
			else{
				if(MessageType.XML.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestXmlMessage xml = message.castAsRestXml();
					content = new MessageContent(xml, bufferMessage_readOnly, context);
				}
				else if(MessageType.JSON.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestJsonMessage json = message.castAsRestJson();
					content = new MessageContent(json, bufferMessage_readOnly, context);
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
					if(httpServletRequest!=null && httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
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
				if(message.getTransportResponseContext()!=null) {
				if(message.getTransportResponseContext().getHeaders()!=null &&
						!message.getTransportResponseContext().getHeaders().isEmpty()) {
					parametriTrasporto = message.getTransportResponseContext().getHeaders();
				}
			}
			
		}catch(Throwable e){
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
		return tmp;
	}
	
	public static void validate(String name,String tmpParam, boolean forceStartWithDollaro, boolean addPrefixError) throws DynamicException{
		Context pddContext = new Context();
		pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, "fakeId");
		String tmp = initTemplateValue(tmpParam, forceStartWithDollaro, pddContext);
		
		boolean onlyValidate = true;
		tmp = processTemplateValue_url_xpath_jsonpath(tmp, onlyValidate, null, forceStartWithDollaro);
		
		try{
			DynamicStringReplace.validate(tmp, forceStartWithDollaro);
		}catch(Exception e){
			String prefix = addPrefixError ? "Proprieta' '"+name+"' contiene un valore non corretto: " : "";
			throw new DynamicException(prefix+e.getMessage(),e);
		}
	}
	
	public static String convertDynamicPropertyValue(String name,String tmpParam,Map<String,Object> dynamicMap,Context pddContext, boolean forceStartWithDollaro) throws DynamicException{

		String tmp = initTemplateValue(tmpParam, forceStartWithDollaro, pddContext);
				
		boolean onlyValidate = false;
		tmp = processTemplateValue_url_xpath_jsonpath(tmp, onlyValidate, dynamicMap, forceStartWithDollaro);
		
		try{
			tmp = DynamicStringReplace.replace(tmp, dynamicMap, forceStartWithDollaro);
		}catch(Exception e){
			throw new DynamicException("Proprieta' '"+name+"' contiene un valore non corretto: "+e.getMessage(),e);
		}
		return tmp;
	}
	
	private static String processTemplateValue_url_xpath_jsonpath(String tmpParam, boolean onlyValidate, Map<String,Object> dynamicMap,
			boolean forceStartWithDollaro) throws DynamicException {
		
		String tmp = tmpParam;
		boolean request = false;
		boolean response = true;
		
		// conversione url
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.url,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.url,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione xpath
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.xml,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.xml,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione jsonpath
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.json,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.json,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione system properties
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.system,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.system,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione env properties
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.env,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.env,
				forceStartWithDollaro, response,
				onlyValidate);
		
		// conversione java properties
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.java,
				forceStartWithDollaro, request,
				onlyValidate);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				TemplateType.java,
				forceStartWithDollaro, response,
				onlyValidate);
		
		return tmp;
	}
	
	private static String convertDynamicPropertyContent(String tmp, Map<String,Object> dynamicMap, 
			TemplateType templateType, 
			boolean forceStartWithDollaro, boolean response,
			boolean onlyValidate) throws DynamicException {
		
		String istruzione = null;
		String prefix = null;
		switch (templateType) {
		case xml:
			istruzione = Costanti.MAP_ELEMENT_XML_XPATH;
			prefix = Costanti.MAP_ELEMENT_XML_XPATH_PREFIX;
			break;
		case json:
			istruzione = Costanti.MAP_ELEMENT_JSON_PATH;
			prefix = Costanti.MAP_ELEMENT_JSON_PATH_PREFIX;
			break;
		case url:
			istruzione = Costanti.MAP_ELEMENT_URL_REGEXP;
			prefix = Costanti.MAP_ELEMENT_URL_REGEXP_PREFIX;
			break;
		case system:
			istruzione = Costanti.MAP_SYSTEM_PROPERTY;
			prefix = Costanti.MAP_SYSTEM_PROPERTY_PREFIX;
			break;
		case env:
			istruzione = Costanti.MAP_ENV_PROPERTY;
			prefix = Costanti.MAP_ENV_PROPERTY_PREFIX;
			break;
		case java:
			istruzione = Costanti.MAP_JAVA_PROPERTY;
			prefix = Costanti.MAP_JAVA_PROPERTY_PREFIX;
			break;
		}
		

		if(forceStartWithDollaro) {
			prefix = "$"+prefix;
		}
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
				if(pattern.contains("}")==false) {
					throw new DynamicException("Trovata istruzione '"+istruzione+"' non correttamente formata (chiusura '}' non trovata)");
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
					throw new DynamicException("Trovata istruzione '"+istruzione+"' non correttamente formata (chiusura '}' non trovata)");
				}
				
				pattern = pattern.substring(0,positionChiusura);
				
				String complete = tmp.substring(indexOfStart, positionChiusura+indexOfStart+prefix.length()+1);
				String value = null;
				if(!onlyValidate) {
					Object o = dynamicMap.get(istruzione);
					if(o==null) {
						throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto");
					}
					switch (templateType) {
					case xml:
					case json:
						if( !(o instanceof PatternExtractor) ) {
							throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (extractor wrong class: "+o.getClass().getName()+")");
						}
						PatternExtractor patternExtractor = (PatternExtractor) o;
						value = patternExtractor.read(pattern);
						break;
					case url:
						if( !(o instanceof URLRegExpExtractor) ) {
							throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (extractor wrong class: "+o.getClass().getName()+")");
						}
						URLRegExpExtractor urlExtractor = (URLRegExpExtractor) o;
						value = urlExtractor.read(pattern);
						break;
					case system:
						if( !(o instanceof SystemPropertiesReader) ) {
							throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (reader wrong class: "+o.getClass().getName()+")");
						}
						SystemPropertiesReader systemPropertiesReader = (SystemPropertiesReader) o;
						value = systemPropertiesReader.read(pattern);
						break;
					case env:
						if( !(o instanceof EnvironmentPropertiesReader) ) {
							throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (reader wrong class: "+o.getClass().getName()+")");
						}
						EnvironmentPropertiesReader environmentPropertiesReader = (EnvironmentPropertiesReader) o;
						value = environmentPropertiesReader.read(pattern);
						break;
					case java:
						if( !(o instanceof JavaPropertiesReader) ) {
							throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (reader wrong class: "+o.getClass().getName()+")");
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
	
	
	// *** FREEMARKER ***
	
	public static void convertFreeMarkerTemplate(String name, 
			byte[] template,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertFreeMarkerTemplate(name, 
				template,
				dynamicMap, out, null);
	}
	public static void convertFreeMarkerTemplate(String name, 
			byte[] template,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		convertFreeMarkerTemplate(name,
				template, null,
				dynamicMap,out, charset);
	}
	public static void convertFreeMarkerTemplate(String name, 
			byte[] template, Map<String, byte[]> templateIncludes, 
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertFreeMarkerTemplate(name, 
				template, templateIncludes, 
				dynamicMap, out, null);
	}
	public static void convertFreeMarkerTemplate(String name, 
			byte[] template, Map<String, byte[]> templateIncludes, 
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {			
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			_convertFreeMarkerTemplate(name, 
					template, templateIncludes, 
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public static void convertFreeMarkerTemplate(String name, 
			byte[] template, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		convertFreeMarkerTemplate(name,
				template, null,
				dynamicMap,writer);
	}
	public static void convertFreeMarkerTemplate(String name, 
			byte[] template, Map<String, byte[]> templateIncludes, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		_convertFreeMarkerTemplate(name, 
				template, templateIncludes, 
				dynamicMap, writer);
	}
	
	public static void convertZipFreeMarkerTemplate(String name, 
			byte[] zip,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertZipFreeMarkerTemplate(name, 
				zip,
				dynamicMap, out, null);
	}
	public static void convertZipFreeMarkerTemplate(String name, 
			byte[] zip,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {			
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			convertZipFreeMarkerTemplate(name, 
					zip,  
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static void convertZipFreeMarkerTemplate(String name, 
			byte[] zip,
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		
		List<Entry> entries = null;
		try {
			entries = ZipUtilities.read(zip);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
		if(entries.isEmpty()) {
			throw new DynamicException("Entries not found");
		}
		byte[] template = null;
		Map<String, byte[]> templateIncludes = new HashMap<>();
		for (Entry entry : entries) {
			if(Costanti.ZIP_INDEX_ENTRY_FREEMARKER.equals(entry.getName())) {
				template = entry.getContent();
			}
			else if(!entry.getName().contains("/") && !entry.getName().contains("\\") && template==null) {
				// prende il primo
				template = entry.getContent();
			}
			else {
				templateIncludes.put(entry.getName(), entry.getContent());
			}
		}
		
		_convertFreeMarkerTemplate(name, 
				template, templateIncludes, 
				dynamicMap, writer);
	}
	
	private static void _convertFreeMarkerTemplate(String name, 
			byte[] template,
			Map<String, byte[]> templateIncludes, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		try {
			// ** Aggiungo utility per usare metodi statici ed istanziare oggetti
			
			// statici
			BeansWrapper wrapper = new BeansWrapper(Configuration.VERSION_2_3_23);
			TemplateModel classModel = wrapper.getStaticModels();
			dynamicMap.put(Costanti.MAP_CLASS_LOAD_STATIC, classModel);
			
			// newObject
			dynamicMap.put(Costanti.MAP_CLASS_NEW_INSTANCE, new freemarker.template.utility.ObjectConstructor());
			
			// Configurazione
			freemarker.template.Configuration config = TemplateUtils.newTemplateEngine();
			config.setAPIBuiltinEnabled(true); // serve per modificare le mappe in freemarker
			
			// template includes
			if(templateIncludes!=null && !templateIncludes.isEmpty()) {
				config.setTemplateLoader(new FreemarkerTemplateLoader(templateIncludes));
			}
			
			// ** costruisco template
			Template templateFTL = TemplateUtils.buildTemplate(config, name, template);
			templateFTL.process(dynamicMap, writer);
			writer.flush();
			
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	
	
	// *** VELOCITY ***
	
	public static void convertVelocityTemplate(String name, 
			byte[] template, 
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertVelocityTemplate(name, 
				template, 
				dynamicMap, out, null);
	}
	public static void convertVelocityTemplate(String name, 
			byte[] template, 
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		convertVelocityTemplate(name, 
				template, null, 
				dynamicMap, out, charset);
	}
	public static void convertVelocityTemplate(String name, 
			byte[] template, Map<String, byte[]> templateIncludes,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertVelocityTemplate(name, 
				template, templateIncludes,
				dynamicMap, out, null);
	}
	public static void convertVelocityTemplate(String name, 
			byte[] template, Map<String, byte[]> templateIncludes,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			_convertVelocityTemplate(name, 
					template, templateIncludes, 
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	public static void convertVelocityTemplate(String name, byte[] template, Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		convertVelocityTemplate(name, 
				template, null, 
				dynamicMap, writer);
	}
	public static void convertVelocityTemplate(String name, 
			byte[] template, Map<String, byte[]> templateIncludes,
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		_convertVelocityTemplate(name, 
				template, templateIncludes,
				dynamicMap, writer);
	}
	
	public static void convertZipVelocityTemplate(String name, 
			byte[] zip,
			Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		convertZipVelocityTemplate(name, 
				zip,
				dynamicMap, out, null);
	}
	public static void convertZipVelocityTemplate(String name, 
			byte[] zip,
			Map<String,Object> dynamicMap, OutputStream out, String charset) throws DynamicException {
		try {			
			OutputStreamWriter oow = null;
			if(charset!=null) {
				oow = new OutputStreamWriter(out, charset);
			}
			else {
				oow = new OutputStreamWriter(out);
			}
			convertZipVelocityTemplate(name, 
					zip,  
					dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static void convertZipVelocityTemplate(String name, 
			byte[] zip,
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		
		List<Entry> entries = null;
		try {
			entries = ZipUtilities.read(zip);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
		if(entries.isEmpty()) {
			throw new DynamicException("Entries not found");
		}
		byte[] template = null;
		Map<String, byte[]> templateIncludes = new HashMap<>();
		for (Entry entry : entries) {
			if(Costanti.ZIP_INDEX_ENTRY_VELOCITY.equals(entry.getName())) {
				template = entry.getContent();
			}
			else if(!entry.getName().contains("/") && !entry.getName().contains("\\") && template==null) {
				// prende il primo
				template = entry.getContent();
			}
			else {
				templateIncludes.put(entry.getName(), entry.getContent());
			}
		}
		
		_convertVelocityTemplate(name, 
				template, templateIncludes, 
				dynamicMap, writer);
	}
	
	private static void _convertVelocityTemplate(String name, 
			byte[] template, 
			Map<String, byte[]> templateIncludes, 
			Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		try {
			// ** Aggiungo utility per usare metodi statici ed istanziare oggetti
			
			// statici
			dynamicMap.put(Costanti.MAP_CLASS_LOAD_STATIC, "".getClass());
			
			// newObject
			dynamicMap.put(Costanti.MAP_CLASS_NEW_INSTANCE, new ObjectConstructor());
			
			// Configurazione
			org.apache.velocity.Template templateVelocity = VelocityTemplateUtils.buildTemplate(name, template);
			
			// template includes
			if(templateIncludes!=null && !templateIncludes.isEmpty()) {
				templateVelocity.setResourceLoader(new VelocityTemplateLoader(templateIncludes));
			}
			
			// ** costruisco template
			templateVelocity.merge(VelocityTemplateUtils.toVelocityContext(dynamicMap), writer);
			writer.flush();
			
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
		
	
	
	// *** XSLT ***
	
	public static void convertXSLTTemplate(String name, byte[] template, Element element, OutputStream out) throws DynamicException {
		try {
			Source xsltSource = new StreamSource(new ByteArrayInputStream(template));
			Source xmlSource = new DOMSource(element);
			Transformer trans = XMLUtils.DEFAULT.getTransformerFactory().newTransformer(xsltSource);
			trans.transform(xmlSource, new StreamResult(out));
			out.flush();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
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
					String entryName = null;
					try {
						entryName = DynamicUtils.convertDynamicPropertyValue(oggetto, keyP, dynamicMap, pddContext, true);
					}catch(Exception e) {
						throw new Exception("["+oggetto+"] Conversione valore per entry name '"+keyP+"' non riuscita: "+e.getMessage(),e);
					}
					String prefixError = "["+keyP+"] ";
					
					String valoreP = p.getProperty(keyP);
					if(valoreP==null) {
						throw new Exception(prefixError+"Nessun valore fornito per la proprietà");
					}
					valoreP = valoreP.trim();
					byte[] content = null;
					if(Costanti.COMPRESS_CONTENT.equals(valoreP)) {
						if(contentExtractor==null || !contentExtractor.hasContent()) {
							throw new Exception(prefixError+"Il "+ruolo+" non possiede un payload");
						}
						content = contentExtractor.getContent();
					}
					else if(Costanti.COMPRESS_ENVELOPE.equals(valoreP) ||
							Costanti.COMPRESS_BODY.equals(valoreP) ||
							Costanti.COMPRESS_ENVELOPE.toLowerCase().equals(valoreP.toLowerCase()) ||
							Costanti.COMPRESS_BODY.toLowerCase().equals(valoreP.toLowerCase())) {
						if(contentExtractor==null || !contentExtractor.hasContent()) {
							throw new Exception(prefixError+"Il "+ruolo+" non possiede un payload");
						}
						if(!contentExtractor.isSoap()) {
							throw new Exception(prefixError+"Il "+ruolo+" non è un messaggio soap");
						}
						if(Costanti.COMPRESS_ENVELOPE.equals(valoreP) ||
								Costanti.COMPRESS_ENVELOPE.toLowerCase().equals(valoreP.toLowerCase())) {
							DumpMessaggio dump = contentExtractor.dumpMessage();
							if(dump==null) {
								throw new Exception(prefixError+"Dump del "+ruolo+" non disponibile");
							}
							content = dump.getBody();
						}
						else {
							content = contentExtractor.getContentSoapBody();
						}
					}
					else if(valoreP.startsWith(Costanti.COMPRESS_ATTACH_PREFIX) ||
							valoreP.startsWith(Costanti.COMPRESS_ATTACH_BY_ID_PREFIX) &&
							valoreP.endsWith(Costanti.COMPRESS_SUFFIX)) {
						String valoreInterno = valoreP.substring(Costanti.COMPRESS_ATTACH_PREFIX.length(), valoreP.length()-1);
						if(valoreInterno==null || "".equals(valoreInterno)) {
							throw new Exception(prefixError+"Non è stato definito un indice per l'attachment");
						}
						DumpMessaggio dump = contentExtractor.dumpMessage();
						if(dump==null) {
							throw new Exception(prefixError+"Dump del "+ruolo+" non disponibile");
						}
						
						DumpAttachment attach = null;
						boolean attachAtteso = true;
						if(valoreP.startsWith(Costanti.COMPRESS_ATTACH_PREFIX)) {
							int index = -1;
							try {
								index = Integer.valueOf(valoreInterno);
							}catch(Exception e) {
								throw new Exception(prefixError+"L'indice definito per l'attachment non è un numero intero: "+e.getMessage(),e);
							}
							if(contentExtractor.isRest() && index==0) {
								content = dump.getBody();
								attachAtteso = false;
							}
							else {
								if(contentExtractor.isRest()) {
									attach = dump.getAttachment((index-1));
								}
								else {
									attach = dump.getAttachment(index);
								}
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
							if(attach==null) {
								throw new Exception(prefixError+"L'indice definito per l'attachment non ha identificato alcun attachment");
							}
							content = attach.getContent();
						}
					}
					else {
						String oggettoV = "valore-"+keyP;
						try {
							String v = DynamicUtils.convertDynamicPropertyValue(oggettoV, valoreP, dynamicMap, pddContext, true);
							if(v!=null) {
								content = v.getBytes();
							}
						}catch(Exception e) {
							throw new Exception(prefixError+"["+oggettoV+"] Conversione valore non riuscita: "+e.getMessage(),e);
						}
					}
					
					if(content==null) {
						throw new Exception(prefixError+"Nessun contenuto da associare alla entry trovato");
					}
					
					listEntries.add(new Entry(entryName, content));
				}
				
				out.write(CompressorUtilities.archive(listEntries, archiveType));
			}
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
		
	}
}

enum TemplateType {
	
	xml, json, url, system, env, java; 
	
}