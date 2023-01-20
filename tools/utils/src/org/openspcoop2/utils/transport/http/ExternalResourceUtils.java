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
package org.openspcoop2.utils.transport.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * ExternalResourceUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalResourceUtils {

	public static byte[] readResource(String resource, ExternalResourceConfig externalConfig) throws UtilsException {
		
		try {
			if(!resource.trim().startsWith("http") && !resource.trim().startsWith("file")) {
				try {
					File f = new File(resource);
					if(f.exists()) {
						return FileSystemUtilities.readBytesFromFile(f);
					}
				}catch(Throwable t) {
					// ignore
				}
				throw new Exception("Unsupported protocol");
			}
			
			HttpRequest req = new HttpRequest();
			req.setMethod(HttpRequestMethod.GET);
			
			resource = resource.trim();
			if(externalConfig.getForwardProxy_url()!=null && StringUtils.isNotEmpty(externalConfig.getForwardProxy_url())) {
				String forwardProxyUrl = externalConfig.getForwardProxy_url();
				String remoteLocation = externalConfig.isForwardProxy_base64() ? Base64Utilities.encodeAsString(resource.getBytes()) : resource;
				if(externalConfig.getForwardProxy_header()!=null && StringUtils.isNotEmpty(externalConfig.getForwardProxy_header())) {
					req.addHeader(externalConfig.getForwardProxy_header(), remoteLocation);
				}
				else if(externalConfig.getForwardProxy_queryParameter()!=null && StringUtils.isNotEmpty(externalConfig.getForwardProxy_queryParameter())) {
					Map<String, List<String>> queryParameters = new HashMap<String, List<String>>();
					TransportUtils.addParameter(queryParameters,externalConfig.getForwardProxy_queryParameter(), remoteLocation);
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

			HttpResponse res = HttpUtilities.httpInvoke(req);

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
				if(response!=null && response.length>0) {
					return res.getContent();
				}
				else {
					throw new Exception("Empty response (http code: "+res.getResultHTTPOperation()+")");
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
				throw new Exception("(http code: "+res.getResultHTTPOperation()+")"+error);
			}

		}catch(Throwable t) {
			throw new UtilsException("Retrieve external resource '"+resource+"' failed: "+t.getMessage(),t);
		}

	}

}
