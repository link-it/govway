/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
	
	public static String buildUrl(String url,Map<String, List<String>>  p,TransportRequestContext requestContext, String normalizedInterfaceName){
		
		String baseUrl = url;
		String parameterOriginalUrl = null;
		if(url.contains("?")){
			baseUrl = url.split("\\?")[0];
			if(baseUrl!=null){
				baseUrl.trim();
			}
			parameterOriginalUrl = url.split("\\?")[1]; 
			if(parameterOriginalUrl!=null){
				parameterOriginalUrl.trim();
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
					p = new HashMap<String, List<String>> ();
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
		
		return TransportUtils.buildUrlWithParameters(p, newUrl.toString());
		
	}
	
	public static String buildPassReverseUrl(TransportRequestContext transportRequestContext, String baseUrl, String redirectLocationUrl, String prefixGatewayUrl,
			String interfaceName) throws MalformedURLException {
               
		String r = baseUrl;
		if(r.contains("?")) {
			r = baseUrl.split("\\?")[0];
		}
         
		URL uri = new URL(r);
		String rRelative= uri.getPath();
		
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
			
			if(suffix.startsWith("/")==false) {
				bf.append("/");
			}
			bf.append(suffix);
			
//			if(redirectLocationUrl.startsWith(r)) {
//				System.out.println("AbsoluteURL new["+bf.toString()+"]");
//			}
//			else {
//				System.out.println("RelativeURL new["+bf.toString()+"]");
//			}
				
			return bf.toString();
			
		}
		else {
			
//			System.out.println("ProxyPass nop redirect["+redirectLocationUrl+"] relative["+rRelative+"] absolute["+r+"]");
			
			return redirectLocationUrl;
		}
	}

}
