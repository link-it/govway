/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.message.rest;

import java.util.Properties;

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
	
	public static String buildUrl(String url,Properties p,TransportRequestContext requestContext, String normalizedInterfaceName){
		
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
		
		StringBuffer newUrl = new StringBuffer();
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
					p = new Properties();
				}
				for (int i = 0; i < split.length; i++) {
					if(split[i].contains("=")){
						String [] splitNomeValore = split[i].split("=");
						if(splitNomeValore!=null){
							String nome = splitNomeValore[0];
							if(nome!=null){
								nome = nome.trim();
							}
							String valore = splitNomeValore[1];
							if(valore!=null){
								valore = valore.trim();
							}
							if(nome!=null && valore!=null){
								p.put(nome,valore);
							}
						}
					}
				}
			}
		}
		
		return TransportUtils.buildLocationWithURLBasedParameter(p, newUrl.toString());
		
	}
	
	public static String buildPassReverseUrl(TransportRequestContext transportRequestContext, String baseUrl, String redirectLocationUrl) {
               
		String r = baseUrl;
		if(r.contains("?")) {
			r = baseUrl.split("\\?")[0];
		}
               
		if(redirectLocationUrl.startsWith(r)) {
			
			// Context path rappresenta il prefisso della richiesta contenente il contesto dell'applicazione (es. /openspcoop2),
			// il protocollo e il servizio. Ad esempio /openspcoop2/spcoop/PD
			String contextPath = transportRequestContext.getWebContext();
			if(contextPath.endsWith("/")==false) {
				contextPath = contextPath + "/";
			}
			String protocollo = transportRequestContext.getProtocolWebContext();
			if(Costanti.CONTEXT_EMPTY.equals(protocollo)==false) {
				contextPath = contextPath + protocollo + "/";
			}
			contextPath = contextPath + transportRequestContext.getFunction();

			// Il suffisso contiene la parte della url ritornata dalla redirect senza la parte iniziale rappresentata dalla base url del servizio
			String suffix = "";
			if(redirectLocationUrl.equals(r)==false) {
				suffix=redirectLocationUrl.substring(r.length());
			}
			
			// Viene costruita una nuova url contenente la richiesta iniziale e il nuovo suffisso,
			// in modo che la url ritornata tramite la redirect possa contenere una nuova url che viene veicolata nuovamente sulla PdD
			StringBuffer bf = new StringBuffer();
			bf.append(contextPath);
			if(contextPath.endsWith("/")==false) {
				bf.append("/");
			}
			bf.append(transportRequestContext.getInterfaceName());
			if(suffix.startsWith("/")==false) {
				bf.append("/");
			}
			bf.append(suffix);
			return bf.toString();
		}
		else {
			return redirectLocationUrl;
		}
	}

}
