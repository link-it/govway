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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourPropertiesUtils;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;

/**
 * ConditionalUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StickyUtils  {
	
	public static StickyResult getStickyResult(PortaApplicativa pa, OpenSPCoop2Message message, Busta busta, 
			RequestInfo requestInfo, PdDContext pddContext, 
			MsgDiagnostico msgDiag, Logger log,
			IState state) throws BehaviourException, BehaviourEmitDiagnosticException {
		
		if(isConfigurazioneSticky(pa, log)==false) {
			return null; // non vi è da fare alcun filtro condizionale
		}
		
		StickyResult result = new StickyResult();
		
		StickyConfigurazione config = read(pa, log);

		StickyTipoSelettore tipoSelettore = config.getTipoSelettore();
		String patternSelettore = config.getPattern();
		
		String pattern = "";
		try {
			Map<String, List<String>> pTrasporto = null;
			String urlInvocazione = null;
			Map<String, List<String>> pQuery = null;
			Map<String, List<String>> pForm = null;
			if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
				pTrasporto = requestInfo.getProtocolContext().getHeaders();
				urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
				pQuery = requestInfo.getProtocolContext().getParameters();
				if(requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
					HttpServletTransportRequestContext httpServletContext = (HttpServletTransportRequestContext) requestInfo.getProtocolContext();
					HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
					if(httpServletRequest!=null && httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
						FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
						if(formServlet.getFormUrlEncodedParametersValues()!=null &&
								!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
							pForm = formServlet.getFormUrlEncodedParametersValues();
						}
					}
				}
			}
			MessageContent messageContent = null;
			boolean bufferMessage_readOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			if(StickyTipoSelettore.CONTENT_BASED.equals(tipoSelettore) || tipoSelettore.isTemplate()) {
				if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
					messageContent = new MessageContent(message.castAsSoap(), bufferMessage_readOnly, pddContext);
				}
				else{
					if(MessageType.XML.equals(message.getMessageType())){
						messageContent = new MessageContent(message.castAsRestXml(), bufferMessage_readOnly, pddContext);
					}
					else if(MessageType.JSON.equals(message.getMessageType())){
						messageContent = new MessageContent(message.castAsRestJson(), bufferMessage_readOnly, pddContext);
					}
					else{
						if(StickyTipoSelettore.CONTENT_BASED.equals(tipoSelettore) 
								// Nei template potrei utilizzare gli header o altre informazioni che non entrano nel merito del contenuto //|| tipoSelettore.isTemplate()
								) {
							throw new Exception("Selettore '"+tipoSelettore.getValue()+"' non supportato per il message-type '"+message.getMessageType()+"'");
						}
					}
				}
			}
			
			msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SELETTORE, tipoSelettore.getValue());
			msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern); // per eliminare @@ dove non serve
			
			String condition = null;
			switch (tipoSelettore) {
			
			case COOKIE_BASED:
				pattern = " (Cookie: "+patternSelettore+")";
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				StickyCookieConfig stickyCookie = new StickyCookieConfig(patternSelettore);
				if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getHttpServletRequest()!=null) {
					Cookie [] cookies = requestInfo.getProtocolContext().getHttpServletRequest().getCookies();
					if(cookies!=null && cookies.length>0) {
						for (Cookie cookie : cookies) {
							if(cookie.getName().equalsIgnoreCase(stickyCookie.getName())) {
								
								// NOTA: Nelle richieste il domani e il path non dovrebbero mai esserci. Lascio comunque il codice.
								
								if(!StringUtils.isEmpty(stickyCookie.getDomain())) {
									if(!stickyCookie.getDomain().equals(cookie.getDomain())) {
										continue; // il cookie non ha un match sul domain
									}
								}
								if(!StringUtils.isEmpty(stickyCookie.getPath())) {
									if(!stickyCookie.getPath().equals(cookie.getPath())) {
										continue; // il cookie non ha un match sul domain
									}
								}
								
								condition = cookie.getValue();
								
								if(cookie.getMaxAge()>0) {
									result.setMaxAgeSeconds(cookie.getMaxAge());
								}
								
								break;
							}
						}
					}
				}
				if(condition==null) {
					throw new Exception("cookie non presente");
				}
				break;
			
			case HEADER_BASED:
				pattern = " (Header HTTP: "+patternSelettore+")";
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				condition = TransportUtils.getFirstValue(pTrasporto, patternSelettore);
				if(condition==null) {
					throw new Exception("header non presente");
				}
				break;
				
			case URLBASED:
				pattern = " (Espressione Regolare: "+patternSelettore+")";
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				try{
					condition = RegularExpressionEngine.getStringMatchPattern(urlInvocazione, patternSelettore);
				}catch(RegExpNotFoundException notFound){}
				break;
				
			case FORM_BASED:
				pattern = " (Parametro URL: "+patternSelettore+")";
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				condition = TransportUtils.getFirstValue(pQuery, patternSelettore);
				if(condition==null) {
					throw new Exception("parametro della url non presente");
				}
				break;
				
			case CONTENT_BASED:
				AbstractXPathExpressionEngine xPathEngine = null;
				if(messageContent==null) {
					throw new Exception("messaggio non presente");
				}
				if(messageContent.isXml()) {
					pattern = " (xPath: "+patternSelettore+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
					condition = AbstractXPathExpressionEngine.extractAndConvertResultAsString(messageContent.getElement(), xPathEngine, patternSelettore,  log);
				}
				else {
					pattern = " (jsonPath: "+patternSelettore+")";
					msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
					condition = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(messageContent.getElementJson(), patternSelettore, log);
				}
				break;
				
			case INDIRIZZO_IP:
				if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS)) {
					condition = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS);
				}
				break;
				
			case INDIRIZZO_IP_FORWARDED:
				if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS)) {
					condition = (String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
				}
				break;
				
			case TEMPLATE:
				if(patternSelettore.length()<50) {
					pattern = " ("+patternSelettore+")";
				}
				else {
					pattern = "";
				}
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				Map<String, Object> dynamicMap = new HashMap<>();
				ErrorHandler errorHandler = new ErrorHandler();
				DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
						message,
						messageContent, 
						busta, 
						pTrasporto, 
						pQuery,
						pForm,
						errorHandler);
				condition = DynamicUtils.convertDynamicPropertyValue("ConditionalConfig.gwt", patternSelettore, dynamicMap, pddContext);
				if(condition!=null) {
					condition = ConditionalUtils.normalizeTemplateResult(condition);
				}
				break;
				
			case FREEMARKER_TEMPLATE:
				if(patternSelettore.length()<50) {
					pattern = " ("+patternSelettore+")";
				}
				else {
					pattern = "";
				}
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				dynamicMap = new HashMap<>();
				errorHandler = new ErrorHandler();
				DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
						message,
						messageContent, 
						busta, 
						pTrasporto, 
						pQuery,
						pForm,
						errorHandler);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(pa.getNome());
				Template template = configurazionePdDManager.getTemplateConnettoreMultiploSticky(idPA, patternSelettore.getBytes(), requestInfo);
				DynamicUtils.convertFreeMarkerTemplate(template, dynamicMap, bout);
				bout.flush();
				bout.close();
				condition = bout.toString();
				if(condition!=null) {
					condition = ConditionalUtils.normalizeTemplateResult(condition);
				}
				break;
				
			case VELOCITY_TEMPLATE:
				if(patternSelettore.length()<50) {
					pattern = " ("+patternSelettore+")";
				}
				else {
					pattern = "";
				}
				msgDiag.addKeyword(CostantiPdD.KEY_PATTERN_SELETTORE, pattern);
				dynamicMap = new HashMap<>();
				errorHandler = new ErrorHandler();
				DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
						message,
						messageContent, 
						busta, 
						pTrasporto, 
						pQuery,
						pForm,
						errorHandler);
				bout = new ByteArrayOutputStream();
				configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
				idPA = new IDPortaApplicativa();
				idPA.setNome(pa.getNome());
				template = configurazionePdDManager.getTemplateConnettoreMultiploSticky(idPA, patternSelettore.getBytes(), requestInfo);
				DynamicUtils.convertVelocityTemplate(template, dynamicMap, bout);
				bout.flush();
				bout.close();
				condition = bout.toString();
				if(condition!=null) {
					condition = ConditionalUtils.normalizeTemplateResult(condition);
				}
				break;
			}
		
			if(condition==null || "".equals(condition)) {
				throw new Exception("Nessuna condizione estratta");
			}
			else {
				result.setCondition(condition);
				result.setFound(true);				
				
				msgDiag.addKeyword(CostantiPdD.KEY_CONDIZIONE_STICKY, condition);
				
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
						"connettoriMultipli.loadBalancer.sticky.identificazioneRiuscita");
				
			}
			
		}catch(Exception e) {
			
			msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
			
			msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_CONSEGNA_CONTENUTI_APPLICATIVI, 
							"connettoriMultipli.loadBalancer.sticky.identificazioneFallita");
		}
		
		
		if(result.isFound()) {
			if(config.getMaxAgeSeconds()!=null) {
				if(result.getMaxAgeSeconds()==null) {
					result.setMaxAgeSeconds(config.getMaxAgeSeconds());
				}
			}
		}
		
		return result;
	}
	
	
	public static boolean isConfigurazioneSticky(PortaApplicativa pa, Logger log) {
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			return false;
		}
		String type = null;
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			if(StickyCostanti.STICKY.equals(p.getNome())) {
				type = p.getValore();
				break;
			}
		}
		if(type==null) {
			return false;
		}
		return "true".equals(type);
	}
		
	public static StickyConfigurazione read(PortaApplicativa pa, Logger log) throws BehaviourException {
		StickyConfigurazione config = new StickyConfigurazione();
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			throw new BehaviourException("Configurazione sticky non disponibile");
		}
		
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			
			String nome = p.getNome();
			String valore = p.getValore().trim();
			
			try {
				if(StickyCostanti.STICKY.equals(nome)) {
					config.setStickyEnabled("true".equals(valore));
				}
				else if(StickyCostanti.STICKY_TIPO_SELETTORE.equals(nome)) {
					config.setTipoSelettore(StickyTipoSelettore.toEnumConstant(valore, true));
				}
				else if(StickyCostanti.STICKY_PATTERN.equals(nome)) {
					config.setPattern(valore);
				}
				else if(StickyCostanti.STICKY_EXPIRE.equals(nome)) {
					config.setMaxAgeSeconds(Integer.valueOf(valore));
				}
			}catch(Exception e) {
				throw new BehaviourException("Configurazione sticky non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
			}
			
		}

		return config;
	}
	

	public static void save(PortaApplicativa pa, StickyConfigurazione configurazione) throws BehaviourException {
		
		if(pa.getBehaviour()==null) {
			throw new BehaviourException("Configurazione behaviour non abilitata");
		}
		if(configurazione==null) {
			throw new BehaviourException("Configurazione condizionale non fornita");
		}
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),StickyCostanti.STICKY, configurazione.isStickyEnabled()+"");
		
		BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),StickyCostanti.STICKY_TIPO_SELETTORE);
		BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),StickyCostanti.STICKY_PATTERN);
		BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),StickyCostanti.STICKY_EXPIRE);
		
		if(configurazione.isStickyEnabled()) {
			if(configurazione.getTipoSelettore()!=null) {
				BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),StickyCostanti.STICKY_TIPO_SELETTORE, configurazione.getTipoSelettore().getValue());
			}
	
			if(StringUtils.isNotEmpty(configurazione.getPattern())) {
				BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),StickyCostanti.STICKY_PATTERN, configurazione.getPattern());
			}
			
			if(configurazione.getMaxAgeSeconds()!=null && configurazione.getMaxAgeSeconds().intValue()>0) {
				BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),StickyCostanti.STICKY_EXPIRE, configurazione.getMaxAgeSeconds().intValue()+"");
			}
		}
		
	}
	
}
