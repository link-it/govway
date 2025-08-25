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
package org.openspcoop2.pdd.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ExternalResourceUtils;
import org.slf4j.Logger;

/**     
 * PDNDConfigUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PDNDConfigUtilities {
	
	private PDNDConfigUtilities() {}

	public static List<PDNDConfig> getRemoteStoreConfig(OpenSPCoop2Properties propertiesReader) throws ProtocolException, CoreException {
		List<RemoteStoreConfig> listRSC = ModIUtils.getRemoteStoreConfig();
		List<PDNDConfig> l = null;
		if(listRSC!=null && !listRSC.isEmpty()) {
			List<String> pdndNames = propertiesReader.getGestoreChiaviPDNDRemoteStoreName();
			boolean all = propertiesReader.isGestoreChiaviPDNDEventiCheckAllStores();
			for (RemoteStoreConfig r : listRSC) {
				if(all || pdndNames.contains(r.getStoreName())) {
					String pdndName = r.getStoreName();
					PDNDConfig c = new PDNDConfig();
					c.setRemoteKeyType(ModIUtils.getRemoteKeyType(pdndName));
					c.setRemoteStoreConfig(r);
					if(l==null) {
						l = new ArrayList<>();
					}
					l.add(c);
				}
			}
		}
		if(l!=null && !l.isEmpty()) {
			return l;
		}
		else {
			 l = null;
		}
		return l;
	}
	
	private static final String URL_CHAR_DELIMITER = "/"; 
	private static String buildBaseUrlPDND(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader) throws CoreException {
		
		String baseUrl = remoteStore.getBaseUrl();
		
		// elimino path keys dalla url
		String pathKeys = propertiesReader.getGestoreChiaviPDNDkeysPath();	
		if(!pathKeys.startsWith(URL_CHAR_DELIMITER)) {
			pathKeys = URL_CHAR_DELIMITER + pathKeys;
		}
		if(baseUrl.endsWith(pathKeys)) {
			baseUrl = baseUrl.substring(0,baseUrl.length()-pathKeys.length());
		}
		else {
			if(pathKeys.endsWith(URL_CHAR_DELIMITER)) {
				// provo senza
				pathKeys = pathKeys.substring(0, (pathKeys.length()-1));
			}
			else {
				// provo con
				pathKeys = pathKeys + URL_CHAR_DELIMITER;
			}
			if(baseUrl.endsWith(pathKeys)) {
				baseUrl = baseUrl.substring(0,baseUrl.length()-pathKeys.length());
			}
		}
			
		return baseUrl;
	}
	
	public static String buildUrlCheckEventi(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader) throws CoreException {
		
		String urlCheckEventi = buildBaseUrlPDND(remoteStore, propertiesReader);
		
		// aggiungo event keys
		
		String pathEventKeys = propertiesReader.getGestoreChiaviPDNDeventsKeysPath();
		if(!pathEventKeys.startsWith(URL_CHAR_DELIMITER)) {
			pathEventKeys = URL_CHAR_DELIMITER + pathEventKeys;
		}
		return urlCheckEventi + pathEventKeys;
		
	}
	
	private static String buildUrlClientId(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader, String clientId) throws CoreException {
		String path = propertiesReader.getGestoreChiaviPDNDclientsPath();
		return buildUrlByResourceId(remoteStore, propertiesReader, clientId, path);
	}
	private static String buildUrlOrganizationId(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader, String organizationId) throws CoreException {
		String path = propertiesReader.getGestoreChiaviPDNDorganizationsPath();
		return buildUrlByResourceId(remoteStore, propertiesReader, organizationId, path);
	}
	private static String buildUrlByResourceId(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader, String valueId, String path) throws CoreException {
		
		String baseUrl = buildBaseUrlPDND(remoteStore, propertiesReader);
		
		// aggiungo event keys
		
		if(!path.startsWith(URL_CHAR_DELIMITER)) {
			path = URL_CHAR_DELIMITER + path;
		}
		int indexOf = path.indexOf("{");
		if(indexOf>0) {
			path = path.substring(0, indexOf);
			try {
				path = path + TransportUtils.urlEncodePath(valueId, Charset.UTF_8.getValue());
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		return baseUrl + path;
		
	}
	
	
	public static String readClientDetails(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader, org.openspcoop2.utils.Map<Object> context, String clientId, Logger log) throws CoreException {
		
		String responseJson = null;
		try {
		
			String url = buildUrlClientId(remoteStore, propertiesReader, clientId);
			
			byte[] response = ExternalResourceUtils.readResource(url, remoteStore);
			responseJson = new String(response);
			
		}catch(Exception e) {
			
			if(abortTransaction(false, propertiesReader, context, log)) {
				throw new CoreException(e.getMessage(),e);
			}
			else {
				String msgError = "Raccolta informazioni tramite PDND sul client con id '"+clientId+"' fallita: "+e.getMessage();
				log.error(msgError);
			}
			
		}
		
		return responseJson;
	}
	
	public static String readOrganizationId(OpenSPCoop2Properties propertiesReader, org.openspcoop2.utils.Map<Object> context, String clientDetails, Logger log) throws CoreException {
		String jsonPath = propertiesReader.getGestoreChiaviPDNDclientsOrganizationJsonPath();
		boolean readErrorAbortTransaction = abortTransaction(true, propertiesReader, context, log);
		return readOrganizationId(jsonPath, readErrorAbortTransaction, clientDetails, log);
	}
	public static String readOrganizationId(String jsonPath, boolean readErrorAbortTransaction, String clientDetails, Logger log) throws CoreException {
		if(clientDetails!=null) {
			try {
				return JsonPathExpressionEngine.extractAndConvertResultAsString(clientDetails, jsonPath, log);
			}catch(Exception e) {
				if(readErrorAbortTransaction) {
					throw new CoreException(e.getMessage(),e);
				}
				else {
					String msgError = "Estrazione identificativo organizzazione tramite jsonPath '"+jsonPath+"' fallita (clientDetails: "+clientDetails+"): "+e.getMessage();
					log.error(msgError);
				}
			}
		}
		return null;
	}
	
	public static String readOrganizationDetails(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader, org.openspcoop2.utils.Map<Object> context, String organizationId, Logger log) throws CoreException {
		
		String responseJson = null;
		try {
		
			String url = buildUrlOrganizationId(remoteStore, propertiesReader, organizationId);
			
			byte[] response = ExternalResourceUtils.readResource(url, remoteStore);
			responseJson = new String(response);
			
		}catch(Exception e) {
			if(abortTransaction(true, propertiesReader, context, log)) {
				throw new CoreException(e.getMessage(),e);
			}
			else {
				String msgError = "Raccolta informazioni tramite PDND sull'organizzazione con id '"+organizationId+"' fallita: "+e.getMessage();
				log.error(msgError);
			}
		}
		
		return responseJson;
	}
	
	private static boolean abortTransaction(boolean organization, OpenSPCoop2Properties propertiesReader, org.openspcoop2.utils.Map<Object> context, Logger log) throws CoreException {
		
		boolean abort = organization ? propertiesReader.isGestoreChiaviPDNDorganizationsErrorAbortTransaction() : propertiesReader.isGestoreChiaviPDNDclientsErrorAbortTransaction();
		
		RequestInfo requestInfo = null;
		if(context!=null && context.containsKey(Costanti.REQUEST_INFO)) {
			Object o = context.get(Costanti.REQUEST_INFO);
			if(o instanceof RequestInfo) {
				requestInfo = (RequestInfo) o;
			}
		}
		
		if(requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getInterfaceName()!=null && 
				StringUtils.isNotEmpty(requestInfo.getProtocolContext().getInterfaceName())) {
			IDPortaApplicativa idPA = new IDPortaApplicativa();
			idPA.setNome(requestInfo.getProtocolContext().getInterfaceName());
			try {
				PortaApplicativa pa = ConfigurazionePdDManager.getInstance().getPortaApplicativaSafeMethod(idPA, requestInfo);
				if(pa!=null && pa.sizeProprieta()>0) {
					abort = organization ? 
							CostantiProprieta.isPdndReadByApiInteropOrganizationFailedAbortTransaction(pa.getProprieta(), abort)
							:
							CostantiProprieta.isPdndReadByApiInteropClientFailedAbortTransaction(pa.getProprieta(), abort);
				}
			}catch(Exception e) {
				log.error("Accesso porta applicativa ["+requestInfo.getProtocolContext().getInterfaceName()+"] fallito: "+e.getMessage(),e);
			}
		}
		return abort;
		
	}
}
