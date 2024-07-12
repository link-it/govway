/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;

/**
 * DynamicMapBuilderUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicMapBuilderUtils {
	
	private DynamicMapBuilderUtils() {}

	public static void injectDynamicMap(Busta busta, 
			RequestInfo requestInfo, org.openspcoop2.utils.Map<Object> contextParam, Logger log) {
		
		/**&& !context.containsKey(Costanti.DYNAMIC_MAP_CONTEXT)) { aggiorno */
		
		Context context = null;
		if(contextParam instanceof Context) {
			context = (Context) contextParam;
		}
			
		Map<String,Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(busta, requestInfo, context, log);
		
		if(context!=null) {
			context.put(Costanti.DYNAMIC_MAP_CONTEXT, dynamicMap);
		}
		if(requestInfo!=null) {
			requestInfo.setDynamicMap(dynamicMap);
		}
		
	}
	public static Map<String,Object> readDynamicMap(org.openspcoop2.utils.Map<Object> context){
		return Costanti.readDynamicMap(context);
	}
	
	public static Map<String, Object> buildDynamicMap(Busta busta, 
			RequestInfo requestInfo, Context pddContext, Logger log) {
	
		Map<String, Object> dynamicMap = new HashMap<>();
		
		Map<String, List<String>> pTrasporto = null;
		String urlInvocazione = null;
		Map<String, List<String>> pQuery = null;
		Map<String, List<String>> pForm = null;
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null) {
			pTrasporto = requestInfo.getProtocolContext().getHeaders();
			urlInvocazione = requestInfo.getProtocolContext().getUrlInvocazione_formBased();
			pQuery = requestInfo.getProtocolContext().getParameters();
			if(requestInfo.getProtocolContext() instanceof HttpServletTransportRequestContext) {
				HttpServletTransportRequestContext httpServletContext = requestInfo.getProtocolContext();
				HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
				if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
					FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
					if(formServlet.getFormUrlEncodedParametersValues()!=null &&
							!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
						pForm = formServlet.getFormUrlEncodedParametersValues();
					}
				}
			}
		}
				
		ErrorHandler errorHandler = new ErrorHandler();
		DynamicUtils.fillDynamicMapRequest(log, dynamicMap, pddContext, urlInvocazione,
				null,
				null, 
				busta, 
				pTrasporto, 
				pQuery,
				pForm,
				errorHandler);
		
		return dynamicMap;
	}
	public static String convertDynamicPropertyValue(String v, String nome, Map<String, Object> dynamicMap, Context context) throws TokenException {
		if(v!=null && !"".equals(v) && dynamicMap!=null) {
			try {
				v = DynamicUtils.convertDynamicPropertyValue(nome+".gwt", v, dynamicMap, context);
			}catch(Exception e) {
				throw new TokenException(e.getMessage(),e);
			}
		}
		return v;
	}
	
}
