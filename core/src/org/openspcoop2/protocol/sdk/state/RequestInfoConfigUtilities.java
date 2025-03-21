/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.sdk.state;

import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.protocol.sdk.Context;
import org.slf4j.Logger;

/**
 * RequestInfoConfigUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestInfoConfigUtilities {
	
	private RequestInfoConfigUtilities() {}

	private static final String CACHE_NON_UTILIZZABILE_AZIONE = "Cache della richiesta non utilizzabile con la modalità di identificazione dell'azione '";
	
	public static void checkRequestInfoConfig(PortaApplicativa paDefault, Logger logCore,
			ServiceBinding serviceBinding, OpenSPCoop2MessageSoapStreamReader soapStreamReader,
			RequestInfo requestInfo) {
		
		if(requestInfo==null || requestInfo.getRequestConfig()==null || requestInfo.getRequestRateLimitingConfig()==null) {
			if(requestInfo!=null) {
				requestInfo.setRequestConfig(null);
				requestInfo.setRequestRateLimitingConfig(null);
			}
			return;
		}
		
		// Verifico che la modalità di riconoscimento dell'azione sia compatibile
		boolean identificazioneByInterfaccia = false;
		boolean identificazioneByInterfacciaForceMode = false;
		if(paDefault!=null && paDefault.getAzione()!=null && paDefault.getAzione().getIdentificazione()!=null) {
			if(org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.CONTENT_BASED.equals(paDefault.getAzione().getIdentificazione()) 
					||
				org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED.equals(paDefault.getAzione().getIdentificazione())	
					||
				org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.INPUT_BASED.equals(paDefault.getAzione().getIdentificazione())	
					||
				org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(paDefault.getAzione().getIdentificazione())) {
				String msg = CACHE_NON_UTILIZZABILE_AZIONE+paDefault.getAzione().getIdentificazione()+"'";
				logCore.debug(msg);
				requestInfo.setRequestConfig(null);
				requestInfo.setRequestRateLimitingConfig(null);
				return;
			}
			
			identificazioneByInterfaccia = org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.INTERFACE_BASED.equals(paDefault.getAzione().getIdentificazione());
			
			identificazioneByInterfacciaForceMode = paDefault.getAzione().getForceInterfaceBased() == null || 
					org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(paDefault.getAzione().getForceInterfaceBased());
		}
		
		if(identificazioneByInterfaccia && ServiceBinding.SOAP.equals(serviceBinding) &&
			// Verifico che il soapStreamReader per il rootElement sia disponibile, utilizzato per riconoscere l'azione di default
			(soapStreamReader==null || soapStreamReader.getRootElementLocalName()==null || soapStreamReader.getRootElementNamespace()==null) 
			){
			String msg = CACHE_NON_UTILIZZABILE_AZIONE+paDefault.getAzione().getIdentificazione()+"' e soapStreamReader root element non disponibile";
			logCore.debug(msg);
			requestInfo.setRequestConfig(null);
			requestInfo.setRequestRateLimitingConfig(null);
			return;
		}
		if(identificazioneByInterfacciaForceMode && ServiceBinding.SOAP.equals(serviceBinding) &&
			// Verifico che il soapStreamReader per il rootElement sia disponibile, utilizzato per riconoscere l'azione di default
			(soapStreamReader==null || soapStreamReader.getRootElementLocalName()==null || soapStreamReader.getRootElementNamespace()==null) 
			){
			logCore.debug("Cache della richiesta non utilizzabile con la modalità di identificazione dell'azione basata sull'interfaccia (force) e soapStreamReader root element non disponibile");
			requestInfo.setRequestConfig(null);
			requestInfo.setRequestRateLimitingConfig(null);
		}
		
	}
	
	public static void checkRequestInfoConfig(PortaDelegata pdDefault, Logger logCore,
			ServiceBinding serviceBinding, OpenSPCoop2MessageSoapStreamReader soapStreamReader,
			RequestInfo requestInfo) {
		
		boolean identificazioneByInterfaccia = false;
		boolean identificazioneByInterfacciaForceMode = false;
		if(pdDefault!=null && pdDefault.getAzione()!=null && pdDefault.getAzione().getIdentificazione()!=null) {
			if(org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.CONTENT_BASED.equals(pdDefault.getAzione().getIdentificazione()) 
					||
				org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.INPUT_BASED.equals(pdDefault.getAzione().getIdentificazione())	
					||
				org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(pdDefault.getAzione().getIdentificazione())) {
				String msg = CACHE_NON_UTILIZZABILE_AZIONE+pdDefault.getAzione().getIdentificazione()+"'";
				logCore.debug(msg);
				requestInfo.setRequestConfig(null);
				requestInfo.setRequestRateLimitingConfig(null);
				return;
			}
			
			identificazioneByInterfaccia = org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.INTERFACE_BASED.equals(pdDefault.getAzione().getIdentificazione());
			
			identificazioneByInterfacciaForceMode = pdDefault.getAzione().getForceInterfaceBased() == null || 
					org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(pdDefault.getAzione().getForceInterfaceBased());
		}
		
		if(identificazioneByInterfaccia && ServiceBinding.SOAP.equals(serviceBinding) &&
			// Verifico che il soapStreamReader per il rootElement sia disponibile, utilizzato per riconoscere l'azione di default
			(soapStreamReader==null || soapStreamReader.getRootElementLocalName()==null || soapStreamReader.getRootElementNamespace()==null) 
				){
			String msg = CACHE_NON_UTILIZZABILE_AZIONE+pdDefault.getAzione().getIdentificazione()+"' e soapStreamReader root element non disponibile";
			logCore.debug(msg);
			requestInfo.setRequestConfig(null);
			requestInfo.setRequestRateLimitingConfig(null);
			return;
		}
		if(identificazioneByInterfacciaForceMode && ServiceBinding.SOAP.equals(serviceBinding) &&
			// Verifico che il soapStreamReader per il rootElement sia disponibile, utilizzato per riconoscere l'azione di default
			(soapStreamReader==null || soapStreamReader.getRootElementLocalName()==null || soapStreamReader.getRootElementNamespace()==null) 
			){
			logCore.debug("Cache della richiesta non utilizzabile con la modalità di identificazione dell'azione basata sull'interfaccia (force) e soapStreamReader root element non disponibile");
			requestInfo.setRequestConfig(null);
			requestInfo.setRequestRateLimitingConfig(null);
		}
		
	}
	
	
	public static RequestInfo normalizeRequestInfoBeforeSerialization(OpenSPCoop2Message msg) {
		Object oRequestInfo = msg.getContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(oRequestInfo instanceof RequestInfo) {
			RequestInfo requestInfo = (RequestInfo) oRequestInfo;
			return normalizeRequestInfoBeforeSerializationEngine(requestInfo);
		}
		return null;
	}
	public static RequestInfo normalizeRequestInfoBeforeSerialization(Context context) {
		Object oRequestInfo = context.get(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(oRequestInfo instanceof RequestInfo) {
			RequestInfo requestInfo = (RequestInfo) oRequestInfo;
			return normalizeRequestInfoBeforeSerializationEngine(requestInfo);
		}
		return null;
	}
	private static RequestInfo normalizeRequestInfoBeforeSerializationEngine(RequestInfo requestInfo) {
		
		RequestConfig preRequestConfig = requestInfo.getPreRequestConfig();
		requestInfo.setPreRequestConfig(null);
		RequestConfig requestConfig = requestInfo.getRequestConfig();
		requestInfo.setRequestConfig(null);
		RequestRateLimitingConfig requestRateLimitingConfig = requestInfo.getRequestRateLimitingConfig();
		requestInfo.setRequestRateLimitingConfig(null);
		RequestThreadContext requestThreadContext = requestInfo.getRequestThreadContext();
		requestInfo.setRequestThreadContext(null);
		Map<String, Object> dynamicMap = requestInfo.getDynamicMap();
		requestInfo.setDynamicMap(null);
		
		RequestInfo requestInfoBackup = new RequestInfo();
		requestInfoBackup.setPreRequestConfig(preRequestConfig);
		requestInfoBackup.setRequestConfig(requestConfig);
		requestInfoBackup.setRequestRateLimitingConfig(requestRateLimitingConfig);
		requestInfoBackup.setRequestThreadContext(requestThreadContext);
		requestInfoBackup.setDynamicMap(dynamicMap);
		return requestInfoBackup;

	}
	
	public static void restoreRequestInfoAfterSerialization(OpenSPCoop2Message msg, RequestInfo requestInfoBackup) {
		if(requestInfoBackup==null) {
			return;
		}
		
		Object oRequestInfo = msg.getContextProperty(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(oRequestInfo instanceof RequestInfo) {
			RequestInfo requestInfo = (RequestInfo) oRequestInfo;
			restoreRequestInfoAfterSerializationEngine(requestInfo, requestInfoBackup);
		}
	}
	public static void restoreRequestInfoAfterSerialization(Context context, RequestInfo requestInfoBackup) {
		if(requestInfoBackup==null) {
			return;
		}
		
		Object oRequestInfo = context.get(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		if(oRequestInfo instanceof RequestInfo) {
			RequestInfo requestInfo = (RequestInfo) oRequestInfo;
			restoreRequestInfoAfterSerializationEngine(requestInfo, requestInfoBackup);
		}
	}
	private static void restoreRequestInfoAfterSerializationEngine(RequestInfo requestInfo, RequestInfo requestInfoBackup) {
		requestInfo.setPreRequestConfig(requestInfoBackup.getPreRequestConfig());
		requestInfo.setRequestConfig(requestInfoBackup.getRequestConfig());
		requestInfo.setRequestRateLimitingConfig(requestInfoBackup.getRequestRateLimitingConfig());
		requestInfo.setRequestThreadContext(requestInfoBackup.getRequestThreadContext());
		requestInfo.setDynamicMap(requestInfoBackup.getDynamicMap());
	}
}
