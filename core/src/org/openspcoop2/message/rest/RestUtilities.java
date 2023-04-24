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

package org.openspcoop2.message.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;

/**
 * RestUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestUtilities {

	public static String getUrlWithoutInterface(TransportRequestContext requestContext, String normalizedInterfaceName) {
		String resourcePath = requestContext.getFunctionParameters();
		if(resourcePath!=null){
			if(resourcePath.startsWith("/")){
				resourcePath = resourcePath.substring(1);
			}
			if(requestContext.getInterfaceName()!=null) {
				if(resourcePath.startsWith(requestContext.getInterfaceName())){
					resourcePath = resourcePath.substring(requestContext.getInterfaceName().length());
				}		
				else if(normalizedInterfaceName!=null && resourcePath.startsWith(normalizedInterfaceName)){
					resourcePath = resourcePath.substring(normalizedInterfaceName.length());
				}
			}
		}
		return resourcePath;
	}
	
	public static String buildUrl(Logger log, String url,Map<String, List<String>>  p,TransportRequestContext requestContext, String normalizedInterfaceName){
		
		String baseUrl = url;
		String parameterOriginalUrl = null;
		if(url.contains("?")){
			baseUrl = url.split("\\?")[0];
			if(baseUrl!=null){
				baseUrl = baseUrl.trim();
			}
			parameterOriginalUrl = url.split("\\?")[1]; 
			if(parameterOriginalUrl!=null){
				parameterOriginalUrl = parameterOriginalUrl.trim();
			}
		}
		
		StringBuilder newUrl = new StringBuilder();
		newUrl.append(baseUrl);
		
		if(requestContext!=null){
			String resourcePath = getUrlWithoutInterface(requestContext,normalizedInterfaceName);
			if(resourcePath!=null){
				boolean extraPathApplicativoStartWithSlash = false;
				if(resourcePath.startsWith("/")){
					extraPathApplicativoStartWithSlash = true;
					// Lo elimino in modo da evitare doppi '//' se anche il baseUrl finisce con '/'
					resourcePath = resourcePath.substring(1);
				}
				if(resourcePath!=null && !"".equals(resourcePath)){
					// // extra path contiene ulteriori dati da aggiungere alla url
					if(baseUrl.endsWith("/")==false){
						newUrl.append("/");
					}
					newUrl.append(resourcePath);
				}
				else {
					if(extraPathApplicativoStartWithSlash) {
						if(baseUrl.endsWith("/")==false){
							// extra path terminava semplicemente con '/'. Questo slash va ripristinato poiche' e' importante per l'index
							newUrl.append("/");
						}
					}
				}
			}
		}
				
		if(parameterOriginalUrl!=null){
			String [] split = parameterOriginalUrl.split("&");
			if(split!=null){
				if(p==null){
					p = new HashMap<> ();
				}
				
				// elimino tutte le proprieta' che sono poi indicate nella base url
				for (int i = 0; i < split.length; i++) {
					if(split[i].contains("=")){
						int indexOf = split[i].indexOf("=");
						
						String nome = null;
						if(indexOf>0) {
							nome = split[i].substring(0, indexOf);
						}
						if(nome!=null){
							nome = nome.trim();
						}
						
						if(nome!=null){
							TransportUtils.removeRawObject(p, nome);
						}
					}
				}
				
				// adesso le aggiungo
				for (int i = 0; i < split.length; i++) {
					if(split[i].contains("=")){
						int indexOf = split[i].indexOf("=");
						
						String nome = null;
						if(indexOf>0) {
							nome = split[i].substring(0, indexOf);
						}
						if(nome!=null){
							nome = nome.trim();
						}
						
						String valore = null;
						if((indexOf+1)<=((split[i].length()-1))) {
							valore = split[i].substring((indexOf+1));
						}
						if(valore!=null){
							valore = valore.trim();
						}
						else {
							valore = "";
						}
						
						if(nome!=null && valore!=null){
							TransportUtils.addParameter(p, nome, valore);
						}
						
					}
				}
			}
		}
		
		return TransportUtils.buildUrlWithParameters(p, newUrl.toString(), log);
		
	}
	
	public static String buildPassReverseUrl(TransportRequestContext transportRequestContext, String baseUrl, String redirectLocationUrlParam, String prefixGatewayUrl,
			String interfaceName) throws MalformedURLException {
               
		String r = baseUrl;
		if(r.contains("?")) {
			r = baseUrl.split("\\?")[0];
		}
         
		URL uri = new URL(r);
		String rRelative= uri.getPath();
		
		String redirectLocationUrl = null;
		String redirectLocationUrlParameters = null;
		if(redirectLocationUrlParam.contains("?")) {
			String [] tmp = redirectLocationUrlParam.split("\\?");
			redirectLocationUrl = tmp[0];
			if(tmp.length>1) {
				StringBuilder sbc = new StringBuilder();
				for (int i = 1; i < tmp.length; i++) {
					if(sbc.length()>0) {
						sbc.append("?");
					}
					sbc.append(tmp[i]);
				}
				redirectLocationUrlParameters = sbc.toString();
			}
		}
		else {
			redirectLocationUrl = redirectLocationUrlParam;
		}
		if(redirectLocationUrl.endsWith("/")) {
			if(!r.endsWith("/")) {
				r = r + "/";
			}
			if(!rRelative.endsWith("/")) {
				rRelative = rRelative + "/";
			}
		}
		else {
			if(r.endsWith("/")) {
				r = r.length()<=1 ? "" : r.substring(0,r.length()-1);
			}
			if(rRelative.endsWith("/")) {
				rRelative = rRelative.length()<=1 ? "" : rRelative.substring(0,rRelative.length()-1);
			}
		}
		
		if(redirectLocationUrl.startsWith(r) || redirectLocationUrl.startsWith(rRelative)) {
			
//			if(redirectLocationUrl.startsWith(r)) {
//				System.out.println("AbsoluteURL redirect["+redirectLocationUrl+"]");
//			}
//			else {
//				System.out.println("RelativeURL redirect["+redirectLocationUrl+"]");
//			}
			
			String contextPath = null;
			if(prefixGatewayUrl==null) {
				// Context path rappresenta il prefisso della richiesta contenente il contesto dell'applicazione (es. /openspcoop2),
				// il protocollo e il servizio. Ad esempio /govway/spcoop/out
				contextPath = transportRequestContext.getWebContext();
				if(contextPath.endsWith("/")==false) {
					contextPath = contextPath + "/";
				}
				String protocollo = transportRequestContext.getProtocolWebContext();
				if(Costanti.CONTEXT_EMPTY.equals(protocollo)==false) {
					contextPath = contextPath + protocollo + "/";
				}
				contextPath = contextPath + transportRequestContext.getFunction();
			}
			
			// Il suffisso contiene la parte della url ritornata dalla redirect senza la parte iniziale rappresentata dalla base url del servizio
			String suffix = "";
			if(redirectLocationUrl.startsWith(r)) {
				if(redirectLocationUrl.equals(r)==false) {
					suffix=redirectLocationUrl.substring(r.length());
				}
			}
			else {
				if(redirectLocationUrl.equals(rRelative)==false) {
					suffix=redirectLocationUrl.substring(rRelative.length());
				}
			}
			
			// Viene costruita una nuova url contenente la richiesta iniziale e il nuovo suffisso,
			// in modo che la url ritornata tramite la redirect possa contenere una nuova url che viene veicolata nuovamente sulla PdD
			StringBuilder bf = new StringBuilder();
			if(redirectLocationUrl.startsWith(r)) {
				// absolute
				if(prefixGatewayUrl!=null) {
					prefixGatewayUrl = prefixGatewayUrl.trim();
					bf.append(prefixGatewayUrl);
					if(prefixGatewayUrl.endsWith("/")==false) {
						bf.append("/");
					}
				}
				else {
					bf.append(contextPath);
					if(contextPath.endsWith("/")==false) {
						bf.append("/");
					}
				}
			}
			else {
				// relative
				if(prefixGatewayUrl!=null) {
					URL urlPrefixGatewayUrl = new URL(prefixGatewayUrl);
					String urlPrefixGatewayUrlAsString = urlPrefixGatewayUrl.getPath();
					bf.append(urlPrefixGatewayUrlAsString);
					if(urlPrefixGatewayUrlAsString.endsWith("/")==false) {
						bf.append("/");
					}
				}
				else {
					bf.append(contextPath);
					if(contextPath.endsWith("/")==false) {
						bf.append("/");
					}
				}
			}
			String interfaceNameTmp = null;
			if(interfaceName!=null) {
				interfaceNameTmp = interfaceName;
			}
			else {
				interfaceNameTmp = transportRequestContext.getInterfaceName();
			}
			if(interfaceNameTmp!=null) {
				if(interfaceNameTmp.startsWith("/")) {
					if(interfaceNameTmp.length()>1) {
						bf.append(interfaceNameTmp.substring(1));
					}
				}
				else {
					bf.append(interfaceNameTmp);
				}
			}
			
			
			if(suffix.equals("")) {
				if(redirectLocationUrl.endsWith("/")) {
					if(!bf.toString().endsWith("/")) {
						bf.append("/");
					}
				}
			}
			else {
				if(suffix.startsWith("/")==false) {
					bf.append("/");
				}
				bf.append(suffix);
			}
			
//			if(redirectLocationUrl.startsWith(r)) {
//				System.out.println("AbsoluteURL new["+bf.toString()+"]");
//			}
//			else {
//				System.out.println("RelativeURL new["+bf.toString()+"]");
//			}		
			
			String url = bf.toString();
			if(redirectLocationUrlParameters!=null && !"".equals(redirectLocationUrlParameters)) {
				if(url.contains("?")) {
					url = url + "&";
				}
				else {
					url = url + "?";
				}
				url = url + redirectLocationUrlParameters;
			}
			
			return url;
			
		}
		else {
			
//			System.out.println("ProxyPass nop redirect["+redirectLocationUrl+"] relative["+rRelative+"] absolute["+r+"]");
			
			return redirectLocationUrl;
		}
	}

	public static String buildCookiePassReversePath(TransportRequestContext transportRequestContext, String baseUrl, String cookiePathParam, String prefixGatewayUrl,
			String interfaceName) throws MalformedURLException {
        
		String r = baseUrl;
		if(r.contains("?")) {
			r = baseUrl.split("\\?")[0];
		}
         
		URL uri = new URL(r);
		String rRelative= uri.getPath();
		
		String cookiePath = null;
		String cookieParameters = null;
		if(cookiePathParam.contains("?")) {
			String [] tmp = cookiePathParam.split("\\?");
			cookiePath = tmp[0];
			if(tmp.length>1) {
				StringBuilder sbc = new StringBuilder();
				for (int i = 1; i < tmp.length; i++) {
					if(sbc.length()>0) {
						sbc.append("?");
					}
					sbc.append(tmp[i]);
				}
				cookieParameters = sbc.toString();
			}
		}
		else {
			cookiePath = cookiePathParam;
		}
		if(cookiePath.endsWith("/")) {
			if(!rRelative.endsWith("/")) {
				rRelative = rRelative + "/";
			}
		}
		else {
			if(rRelative.endsWith("/")) {
				rRelative = rRelative.length()<=1 ? "" : rRelative.substring(0,rRelative.length()-1);
			}
		}
		
		if(cookiePath.startsWith(rRelative)) {
			
			//System.out.println("Cookie - RelativeURL redirect["+cookiePath+"]");
			
			String contextPath = null;
			if(prefixGatewayUrl==null) {
				// Context path rappresenta il prefisso della richiesta contenente il contesto dell'applicazione (es. /openspcoop2),
				// il protocollo e il servizio. Ad esempio /govway/spcoop/out
				contextPath = transportRequestContext.getWebContext();
				if(contextPath.endsWith("/")==false) {
					contextPath = contextPath + "/";
				}
				String protocollo = transportRequestContext.getProtocolWebContext();
				if(Costanti.CONTEXT_EMPTY.equals(protocollo)==false) {
					contextPath = contextPath + protocollo + "/";
				}
				contextPath = contextPath + transportRequestContext.getFunction();
			}
			
			// Il suffisso contiene la parte della url ritornata dalla redirect senza la parte iniziale rappresentata dalla base url del servizio
			String suffix = "";
			if(cookiePath.equals(rRelative)==false) {
				suffix=cookiePath.substring(rRelative.length());
			}
			
			// Viene costruita una nuova url contenente la richiesta iniziale e il nuovo suffisso,
			// in modo che la url ritornata tramite la redirect possa contenere una nuova url che viene veicolata nuovamente sulla PdD
			StringBuilder bf = new StringBuilder();
			// relative
			if(prefixGatewayUrl!=null) {
				URL urlPrefixGatewayUrl = new URL(prefixGatewayUrl);
				String urlPrefixGatewayUrlAsString = urlPrefixGatewayUrl.getPath();
				bf.append(urlPrefixGatewayUrlAsString);
				if(urlPrefixGatewayUrlAsString.endsWith("/")==false) {
					bf.append("/");
				}
			}
			else {
				bf.append(contextPath);
				if(contextPath.endsWith("/")==false) {
					bf.append("/");
				}
			}

			String interfaceNameTmp = null;
			if(interfaceName!=null) {
				interfaceNameTmp = interfaceName;
			}
			else {
				interfaceNameTmp = transportRequestContext.getInterfaceName();
			}
			if(interfaceNameTmp!=null) {
				if(interfaceNameTmp.startsWith("/")) {
					if(interfaceNameTmp.length()>1) {
						bf.append(interfaceNameTmp.substring(1));
					}
				}
				else {
					bf.append(interfaceNameTmp);
				}
			}
			
			if(suffix.equals("")) {
				if(cookiePath.endsWith("/")) {
					if(!bf.toString().endsWith("/")) {
						bf.append("/");
					}
				}
			}
			else {
				if(suffix.startsWith("/")==false) {
					bf.append("/");
				}
				bf.append(suffix);
			}
			
			//System.out.println("Cookie relativeURL new["+bf.toString()+"]");
				
			String url = bf.toString();
			if(cookieParameters!=null && !"".equals(cookieParameters)) {
				if(url.contains("?")) {
					url = url + "&";
				}
				else {
					url = url + "?";
				}
				url = url + cookieParameters;
			}
			
			return url;
			
		}
		else {
			
//			System.out.println("ProxyPass nop cookiePath["+cookiePath+"] relative["+rRelative+"] absolute["+r+"]");
			
			return cookiePath;
		}
	}
	
	public static String buildCookiePassReverseDomain(TransportRequestContext transportRequestContext, String baseUrl, String cookieDomain, String prefixGatewayUrl) throws MalformedURLException {
               
		String r = baseUrl;
		if(r.contains("?")) {
			r = baseUrl.split("\\?")[0];
		}
         
		URL uri = new URL(r);
		String rDomain= uri.getHost();
		
		if(cookieDomain!=null && cookieDomain.equalsIgnoreCase(rDomain)) {
			
			//System.out.println("CookieDomain cookieDomain["+cookieDomain+"]");
			
			String newDomain = null;
			if(prefixGatewayUrl==null) {
				if(transportRequestContext!=null && transportRequestContext instanceof HttpServletTransportRequestContext) {
					HttpServletTransportRequestContext http = (HttpServletTransportRequestContext) transportRequestContext;
					if(http.getHttpServletRequest()!=null) {
						String requestUrl = http.getHttpServletRequest().getRequestURL().toString();
						URL uriRequestUrl = new URL(requestUrl);
						newDomain = uriRequestUrl.getHost();
					}
				}
			}
			else {
				URL uriRequestUrl = new URL(prefixGatewayUrl);
				newDomain = uriRequestUrl.getHost();
			}

			if(newDomain!=null) {
				
				//System.out.println("ProxyPass cookieDomain["+cookieDomain+"] newCookieDomain["+newDomain+"]");
				
				return newDomain;
			}
			else {
				
//				System.out.println("ProxyPass nop cookieDomain["+cookieDomain+"]");
			
				return cookieDomain;
			}
			
		}
		else {
			
//			System.out.println("ProxyPass nop cookieDomain["+cookieDomain+"]");
			
			return cookieDomain;
		}
	}
}
