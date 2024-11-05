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
package org.openspcoop2.utils.transport.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.ldap.LdapClientFactory;
import org.openspcoop2.utils.transport.ldap.LdapClientInterface;
import org.openspcoop2.utils.transport.ldap.LdapEngineType;
import org.openspcoop2.utils.transport.ldap.LdapQuery;
import org.openspcoop2.utils.transport.ldap.LdapUtility;

/**
 * ExternalResourceUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalResourceUtils {
	
	private ExternalResourceUtils() {}

	public static byte[] readResource(String resource, ExternalResourceConfig externalConfig) throws UtilsException {
		return readResourceReturnHttpResponse(resource, externalConfig).getContent();
	}
	public static HttpResponse readResourceReturnHttpResponse(String resource, ExternalResourceConfig externalConfig) throws UtilsException {
		
		HttpResponse res = null;
		try {
			if(!TransportUtils.isRemoteResource(resource.trim())) {
				try {
					File f = new File(resource);
					if(f.exists()) {
						byte[] content = FileSystemUtilities.readBytesFromFile(f);
						HttpResponse response = new HttpResponse();
						response.setResultHTTPOperation(200);
						response.setContent(content);
						return response;
					}
				}catch(Exception t) {
					// ignore
				}
				throw new UtilsException("Unsupported protocol");
			}
			
			if(TransportUtils.isLdapResource(resource)) {
				return readLdapResource(resource, externalConfig);
			}
			
			HttpRequest req = new HttpRequest();
			req.setMethod(HttpRequestMethod.GET);
			
			if(externalConfig.getHeaders()!=null && !externalConfig.getHeaders().isEmpty()) {
				for (Map.Entry<String,String> entry : externalConfig.getHeaders().entrySet()) {
					if(entry.getKey()!=null && entry.getValue()!=null) {
						req.addHeader(entry.getKey(),entry.getValue());
					}
				}
			}
			
			resource = resource.trim();
			
			if(externalConfig.getQueryParameters()!=null && !externalConfig.getQueryParameters().isEmpty()) {
				resource = TransportUtils.buildUrlWithParameters(TransportUtils.convertToMapListValues(externalConfig.getQueryParameters()), resource);
			}
			
			if(externalConfig.getForwardProxyUrl()!=null && StringUtils.isNotEmpty(externalConfig.getForwardProxyUrl())) {
				String forwardProxyUrl = externalConfig.getForwardProxyUrl();
				String remoteLocation = externalConfig.isForwardProxyBase64() ? Base64Utilities.encodeAsString(resource.getBytes()) : resource;
				if(externalConfig.getForwardProxyHeader()!=null && StringUtils.isNotEmpty(externalConfig.getForwardProxyHeader())) {
					req.addHeader(externalConfig.getForwardProxyHeader(), remoteLocation);
				}
				else if(externalConfig.getForwardProxyQueryParameter()!=null && StringUtils.isNotEmpty(externalConfig.getForwardProxyQueryParameter())) {
					Map<String, List<String>> queryParameters = new HashMap<>();
					TransportUtils.addParameter(queryParameters,externalConfig.getForwardProxyQueryParameter(), remoteLocation);
					forwardProxyUrl = TransportUtils.buildUrlWithParameters(queryParameters, forwardProxyUrl, false, null);
				}
				else {
					throw new UtilsException("Forward Proxy configuration error: header and query parameter not found");
				}
				req.setUrl(forwardProxyUrl);
			}
			else {
				req.setUrl(resource);
			}
			
			if(req.getUrl().startsWith("https")) {
				req.setHostnameVerifier(externalConfig.isHostnameVerifier());
				req.setTrustAllCerts(externalConfig.isTrustAllCerts());
				if(externalConfig.getTrustStore()!=null) {
					req.setTrustStore(externalConfig.getTrustStore());
				}
				if(externalConfig.getKeyStore()!=null) {
					req.setKeyStore(externalConfig.getKeyStore());
					req.setKeyAlias(externalConfig.getKeyAlias());
					req.setKeyPassword(externalConfig.getKeyPassword());
				}
			}
			req.setConnectTimeout(externalConfig.getConnectTimeout());
			req.setReadTimeout(externalConfig.getReadTimeout());
			
			req.setUsername(externalConfig.getBasicUsername());
			req.setPassword(externalConfig.getBasicPassword());
			
			res = HttpUtilities.httpInvoke(req);

		}catch(Exception t) {
			throw new UtilsException("Retrieve external resource '"+resource+"' failed: "+t.getMessage(),t);
		}

		validateResponse(res, resource, externalConfig);
		
		return res;
	}
	
	private static HttpResponse readLdapResource(String resource, ExternalResourceConfig externalConfig) throws UtilsException {
		
		try {
		
			LdapClientInterface clientLdap = LdapClientFactory.getClient(LdapEngineType.SPRING_FRAMEWORK);
			clientLdap.uri(LdapUtility.getBaseUrlFromURI(resource));
			if(externalConfig.getBasicUsername()!=null && externalConfig.getBasicPassword()!=null) {
				clientLdap.username(externalConfig.getBasicUsername());
				clientLdap.password(externalConfig.getBasicPassword());
			}
			LdapQuery parsedQuery = LdapUtility.getQueryFromURI(resource);
			if(parsedQuery==null) {
				throw new UtilsException("parse failed");
			}
			String parsedAttribute = null;
			if(parsedQuery.getAttributes()!=null && !parsedQuery.getAttributes().isEmpty()) {
				parsedAttribute = parsedQuery.getAttributes().get(0);
			}
			if(parsedAttribute==null) {
				throw new UtilsException("parse attribute failed");
			}
			List<Attributes> list = clientLdap.search(parsedQuery);
			if(list == null || list.isEmpty()) {
				throw new UtilsException("results not found");
			}
			if(list.size()>1) {
				throw new UtilsException("more than one result found ("+list.size()+")");
			}
			Attributes attrs = list.get(0);
			if(attrs==null || attrs.size()<=0) {
				throw new UtilsException("attributes empty");
			}
			Attribute a = attrs.get(parsedAttribute);
			if(a==null) {
				throw new UtilsException("attribute '"+parsedAttribute+"' not found");
			}
			Object o = a.get();
			if(o==null) {
				throw new UtilsException("attribute '"+parsedAttribute+"' null");
			}
			byte [] content = null;
			if(o instanceof byte[]) {
				content = (byte[]) o; 
			}
			else if(o instanceof String) {
				content = ((String) o).getBytes(); 
			}
			else {
				throw new UtilsException("attribute '"+parsedAttribute+"' with unknown type: "+o.getClass().getName());
			}
			
			HttpResponse response = new HttpResponse();
			response.setResultHTTPOperation(200);
			response.setContent(content);
			return response;
		
		}catch(Exception t) {
			throw new UtilsException("Retrieve external resource '"+resource+"' failed: "+t.getMessage(),t);
		}
			
	}
	
	private static void validateResponse(HttpResponse res, String resource, ExternalResourceConfig externalConfig) throws UtilsException {
		try {
			List<Integer> returnCodeValid = externalConfig.getReturnCode();
			if(returnCodeValid==null) {
				returnCodeValid = new ArrayList<>();
			}
			if(returnCodeValid.isEmpty()) {
				returnCodeValid.add(200);
			}
			boolean isValid = false;
			for (Integer rt : returnCodeValid) {
				if(rt!=null && rt.intValue() == res.getResultHTTPOperation()) {
					isValid = true;
					break;
				}
			}
	
			byte[] response = res.getContent();
	
			if(isValid) {
				if(response==null || response.length<=0) {
					throw new UtilsException("Empty response (http code: "+res.getResultHTTPOperation()+")");
				}
			}
			else {
				String error = null;
				if(response.length<=(2048)) {
					error = Utilities.convertToPrintableText(response, 2048);
					if(error!=null && error.contains("Visualizzazione non riuscita")) {
						error = null;
					}
				}
				if(error==null) {
					error="";
				}
				else {
					error = ": "+error;
				}
				
				throw new UtilsException("(http code: "+res.getResultHTTPOperation()+")"+error);
			}
	
		}catch(Exception t) {
			throw new UtilsException("Retrieve external resource '"+resource+"' failed: "+t.getMessage(),t);
		}
	}

}
