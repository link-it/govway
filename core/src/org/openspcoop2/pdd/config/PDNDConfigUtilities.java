/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
	
	public static String buildUrlCheckEventi(RemoteStoreConfig remoteStore) throws ProtocolException {
		return ModIUtils.extractInfoFromMetadati(remoteStore.getMetadati(), ModIUtils.API_PDND_EVENTS_KEYS_PATH, "Events keys path");		
	}
	
	private static String buildUrlClientId(RemoteStoreConfig remoteStore, String clientId) throws ProtocolException {
		String urlClients = ModIUtils.extractInfoFromMetadati(remoteStore.getMetadati(), ModIUtils.API_PDND_CLIENTS_PATH, "Clients path");
		return buildUrlByResourceId(urlClients, clientId);
	}
	private static String buildUrlOrganizationId(RemoteStoreConfig remoteStore, String organizationId) throws ProtocolException {
		String urlOrganizations = ModIUtils.extractInfoFromMetadati(remoteStore.getMetadati(), ModIUtils.API_PDND_ORGANIZATIONS_PATH, "Organizations path");
		return buildUrlByResourceId(urlOrganizations, organizationId);
	}
	private static String buildUrlByResourceId(String url, String valueId) throws ProtocolException {
		
		int indexOf = url.indexOf("{");
		if(indexOf>0) {
			url = url.substring(0, indexOf);
			try {
				url = url + TransportUtils.urlEncodePath(valueId, Charset.UTF_8.getValue());
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return url;
		
	}
	
	
	public static String readClientDetails(RemoteStoreConfig remoteStore, OpenSPCoop2Properties propertiesReader, org.openspcoop2.utils.Map<Object> context, String clientId, Logger log) throws CoreException {
		
		String responseJson = null;
		try {
		
			String url = buildUrlClientId(remoteStore, clientId);
			
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
	
	public static String readOrganizationId(RemoteStoreConfig remoteConfig, OpenSPCoop2Properties propertiesReader, org.openspcoop2.utils.Map<Object> context, String clientDetails, Logger log) throws CoreException, ProtocolException {
		String jsonPath = ModIUtils.extractInfoFromMetadati(remoteConfig.getMetadati(), ModIUtils.API_PDND_CLIENTS_ORGANIZATION_JSON_PATH, "Clients organization json path");
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
		
			String url = buildUrlOrganizationId(remoteStore, organizationId);
			
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
