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
package org.openspcoop2.pdd.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.keystore.KeystoreException;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProvider;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.PDNDTokenInfo;
import org.openspcoop2.protocol.sdk.PDNDTokenInfoDetails;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.remote.RemoteKeyType;
import org.openspcoop2.utils.certificate.remote.RemoteStoreClientInfo;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * PDNDResolver
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PDNDResolver {
	
	private Context context;
	private List<RemoteStoreConfig> remoteStores;
	
	public PDNDResolver(Context context, List<RemoteStoreConfig> remoteStores) {
		this.context = context;
		this.remoteStores = remoteStores;
	}
	
	
	public boolean isRemoteStore(String name) {
		return isRemoteStore(name, this.remoteStores);
	}
	public static boolean isRemoteStore(String name, List<RemoteStoreConfig> remoteStores) {
		for (RemoteStoreConfig rsc : remoteStores) {
			if(name.equals(rsc.getStoreName())) {
				return true;
			}
		}
		return false;
	}
	
	public RemoteStoreConfig getRemoteStoreConfig(String name, IDSoggetto idDominio) throws ProtocolException {
		return getRemoteStoreConfig(name, idDominio, this.remoteStores);
	}
	public static RemoteStoreConfig getRemoteStoreConfig(String name, IDSoggetto idDominio, List<RemoteStoreConfig> remoteStores) throws ProtocolException {
		for (RemoteStoreConfig rsc : remoteStores) {
			if(name.equals(rsc.getStoreName())) {
				if(rsc.isMultitenant() && idDominio!=null && idDominio.getNome()!=null) {
					try {
						return rsc.newInstanceMultitenant(idDominio.getNome());
					}catch(Exception e){
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				return rsc;
			}
		}
		return null;
	}
	
	public RemoteStoreConfig getRemoteStoreConfigByTokenPolicy(String name, IDSoggetto idDominio) throws ProtocolException {
		return getRemoteStoreConfigByTokenPolicy(name, idDominio, this.remoteStores);
	}
	public static RemoteStoreConfig getRemoteStoreConfigByTokenPolicy(String name, IDSoggetto idDominio, List<RemoteStoreConfig> remoteStores) throws ProtocolException {
		for (RemoteStoreConfig rsc : remoteStores) {
			if(name.equals(rsc.getTokenPolicy())) {
				if(rsc.isMultitenant() && idDominio!=null && idDominio.getNome()!=null) {
					try {
						return rsc.newInstanceMultitenant(idDominio.getNome());
					}catch(Exception e){
						throw new ProtocolException(e.getMessage(),e);
					}
				}
				return rsc;
			}
		}
		return null;
	}
	
	public RemoteStoreConfig enrichTokenInfo(RequestInfo requestInfo, IDSoggetto idSoggetto,
			InformazioniToken informazioniToken, SecurityToken securityTokenForContext) throws ProtocolException {
		return enrichTokenInfo(requestInfo, false, false, idSoggetto, 
				informazioniToken, securityTokenForContext);
	}
	public RemoteStoreConfig enrichTokenInfo(RequestInfo requestInfo, boolean sicurezzaMessaggio, boolean sicurezzaAudit, IDSoggetto idSoggetto) throws ProtocolException {
		Object oInformazioniTokenNormalizzate = null;
		if(this.context!=null) {
			oInformazioniTokenNormalizzate = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
		}
		InformazioniToken informazioniTokenNormalizzate = null;
		if(oInformazioniTokenNormalizzate instanceof InformazioniToken) {
			informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
		}
		
		SecurityToken securityTokenForContext = SecurityTokenUtilities.readSecurityToken(this.context);
		
		return enrichTokenInfo(requestInfo, sicurezzaMessaggio, sicurezzaAudit, idSoggetto, 
				informazioniTokenNormalizzate, securityTokenForContext);
	}
	private RemoteStoreConfig enrichTokenInfo(RequestInfo requestInfo, boolean sicurezzaMessaggio, boolean sicurezzaAudit, IDSoggetto idSoggetto, 
			InformazioniToken informazioniToken, SecurityToken securityTokenForContext) throws ProtocolException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		RemoteStoreConfig rsc = null;
		try {
			if(op2Properties.isGestoreChiaviPDNDclientInfoEnabled()) {
			
				rsc = getRemoteStoreConfig(idSoggetto);
				if(rsc==null) {
					return rsc;
				}
				
				String clientId = null;
				if(informazioniToken!=null) {
					clientId = informazioniToken.getClientId();
				}
				if(clientId==null) {
					return rsc;
				}
				
				// NOTA: il kid DEVE essere preso dall'eventuale token di integrità, poichè il kid nell'access token è sempre uguale ed è quello della PDND
				String kid = readKid(sicurezzaMessaggio, sicurezzaAudit, securityTokenForContext, clientId);
				
				enrichTokenInfo(requestInfo, idSoggetto, 
						informazioniToken, securityTokenForContext,
						rsc, clientId, kid);
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
		return rsc;
	}
	
	private void enrichTokenInfo(RequestInfo requestInfo, IDSoggetto idSoggetto, 
			InformazioniToken informazioniToken, SecurityToken securityTokenForContext,
			RemoteStoreConfig rsc, String clientId, String kid) throws KeystoreException, SecurityException, UtilsException, TransactionNotExistsException, CoreException, TransactionDeletedException {
		/**if(kid.startsWith(REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID)) {*/
		Object oInfoPDND = null;
		if(this.context!=null) {
			oInfoPDND = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_PDND_CLIENT_READ);
		}
		if(oInfoPDND instanceof String) {
			String s = (String) oInfoPDND;
			if("true".equals(s)) {
				// chiamata fatta dalla validazione semantica dopo che la raccolta delle informazioni sulla PDND è già stata chiamata in seguito alla validazione del token
				// poiche' il kid non e' presente, e' inutile ri-effettuare la medesima invocazione 
				return;
			}
		}
		/**}*/
		
		enrichTokenInfo(securityTokenForContext, informazioniToken, requestInfo, rsc,
				kid, clientId);
		
		if(kid.startsWith(REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID)) {
			this.context.put(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_PDND_CLIENT_READ, "true");
		}
		
		updateCredenzialiTokenPDND(requestInfo, idSoggetto, informazioniToken);
	}
	
	private void updateCredenzialiTokenPDND(RequestInfo requestInfo, IDSoggetto idSoggetto, InformazioniToken informazioniToken) throws TransactionNotExistsException, CoreException, TransactionDeletedException {
		String idTransazione = null;
		if(this.context!=null) {
			idTransazione = (String) this.context.getObject(Costanti.ID_TRANSAZIONE);
		}
		Transaction transaction = TransactionContext.getTransaction(idTransazione);
		
		CredenzialiMittente credenzialiMittente = transaction.getCredenzialiMittente();
		if(credenzialiMittente==null) {
			credenzialiMittente = new CredenzialiMittente();
			transaction.setCredenzialiMittente(credenzialiMittente);
		}
		
		try {
			GestoreAutenticazione.updateCredenzialiTokenPDND(idSoggetto, "ModIValidator", idTransazione, 
				informazioniToken, credenzialiMittente,
	    		null, "ModIValidator.credenzialiPDND", requestInfo,
	    		this.context);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	
	private RemoteStoreConfig getRemoteStoreConfig(IDSoggetto idSoggetto) throws ProtocolException {
		Object oTokenPolicy = null;
		if(this.context!=null) {
			oTokenPolicy = this.context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_POLICY);
		}
		String tokenPolicy = null;
		if(oTokenPolicy instanceof String) {
			tokenPolicy = (String) oTokenPolicy;
		}
		if(tokenPolicy==null) {
			return null;
		}
		
		return this.getRemoteStoreConfigByTokenPolicy(tokenPolicy, idSoggetto);
	}
	
	public static final String REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID = "ClientId--";
	private String readKid(boolean sicurezzaMessaggio, boolean sicurezzaAudit, SecurityToken securityTokenForContext, String clientId) throws UtilsException {
		// NOTA: il kid DEVE essere preso dall'eventuale token di integrità, poichè il kid nell'access token è sempre uguale ed è quello della PDND
		String kid = null;
		if(sicurezzaMessaggio) {
			kid = readKidFromTokenIntegrity(securityTokenForContext);
		}
		if(kid==null && sicurezzaAudit) {
			kid = readKidFromTokenAudit(securityTokenForContext);
		}
		if(kid==null) {
			// Altrimenti utilizzo la struttura dati per ospitare le informazioni sul clientId
			kid = REMOTE_STORE_KEY_KID_STARTS_WITH_CLIENT_ID+clientId;
		}
		return kid;
	}
	
	private String readKidFromTokenIntegrity(SecurityToken securityTokenForContext) throws UtilsException {
		String kid = null;
		if(securityTokenForContext!=null && securityTokenForContext.getIntegrity()!=null) {
			kid = securityTokenForContext.getIntegrity().getKid();
			if(kid==null) {
				kid = securityTokenForContext.getIntegrity().getHeaderClaim("kid");
			}
		}
		return kid;
	}
	private String readKidFromTokenAudit(SecurityToken securityTokenForContext) throws UtilsException {
		String kid = null;
		if(securityTokenForContext!=null && securityTokenForContext.getAudit()!=null) {
			kid = securityTokenForContext.getAudit().getKid();
			if(kid==null) {
				kid = securityTokenForContext.getAudit().getHeaderClaim("kid");
			}
		}
		return kid;
	}
	
	
	private void enrichTokenInfo(SecurityToken securityTokenForContext, InformazioniToken informazioniTokenNormalizzate, RequestInfo requestInfo, RemoteStoreConfig rsc,
			String kid, String clientId) throws KeystoreException, SecurityException, UtilsException {
		RemoteKeyType keyType = RemoteKeyType.JWK; // ignored
		RemoteStoreProvider remoteStoreProvider = new RemoteStoreProvider(requestInfo, keyType);
		RemoteStoreClientInfo rsci = GestoreKeystoreCache.getRemoteStoreClientInfo(requestInfo, kid, clientId, rsc, remoteStoreProvider);
		if(rsci!=null &&
			(rsci.getClientDetails()!=null || rsci.getOrganizationId()!=null || rsci.getOrganizationDetails()!=null) 
			){
			if(informazioniTokenNormalizzate.getPdnd()==null) {
				informazioniTokenNormalizzate.setPdnd(new HashMap<>());
			}
			if(rsci.getClientDetails()!=null) {
				JSONUtils jsonUtils = JSONUtils.getInstance();
				if(jsonUtils.isJson(rsci.getClientDetails())) {
					JsonNode root = jsonUtils.getAsNode(rsci.getClientDetails());
					PDNDTokenInfoDetails info = new PDNDTokenInfoDetails();
					info.setId(rsci.getClientId());
					info.setDetails(rsci.getClientDetails());
					enrichTokenInfoAddInClaims(jsonUtils, securityTokenForContext, informazioniTokenNormalizzate, root, PDNDTokenInfo.CLIENT_INFO, info);
				}
			}
			if(rsci.getOrganizationDetails()!=null) {
				JSONUtils jsonUtils = JSONUtils.getInstance();
				if(jsonUtils.isJson(rsci.getOrganizationDetails())) {
					JsonNode root = jsonUtils.getAsNode(rsci.getOrganizationDetails());
					PDNDTokenInfoDetails info = new PDNDTokenInfoDetails();
					info.setId(rsci.getOrganizationId());
					info.setDetails(rsci.getOrganizationDetails());
					enrichTokenInfoAddInClaims(jsonUtils, securityTokenForContext, informazioniTokenNormalizzate, root, PDNDTokenInfo.ORGANIZATION_INFO, info);
				}
			}
		}
	}
	private void enrichTokenInfoAddInClaims(JSONUtils jsonUtils, SecurityToken securityTokenForContext, InformazioniToken informazioniTokenNormalizzate, JsonNode root, String type,
			PDNDTokenInfoDetails info) {
		Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
		if(!readClaims.isEmpty()) {
			enrichTokenInfoAddInClaims(securityTokenForContext, informazioniTokenNormalizzate,
					type, info,
					readClaims);
		}
	}

	private void enrichTokenInfoAddInClaims(SecurityToken securityTokenForContext, InformazioniToken informazioniTokenNormalizzate,
			String type, PDNDTokenInfoDetails info,
			Map<String, Object> readClaims) {
		informazioniTokenNormalizzate.getPdnd().put(type,readClaims);
		String prefix = PDNDTokenInfo.TOKEN_INFO_PREFIX_PDND+type+".";
		Map<String, Serializable> readClaimsSerializable = new HashMap<>(); 
		if(informazioniTokenNormalizzate.getClaims()!=null) {
			for (Map.Entry<String,Object> entry : readClaims.entrySet()) {
				String key = prefix+entry.getKey();
				if(!informazioniTokenNormalizzate.getClaims().containsKey(key)) {
					informazioniTokenNormalizzate.getClaims().put(key, entry.getValue());
				}
				if(entry.getValue() instanceof Serializable) {
					readClaimsSerializable.put(entry.getKey(), (Serializable) entry.getValue());
				}
			}
		}
		
		info.setClaims(readClaimsSerializable);
		if(securityTokenForContext!=null) {
			if(securityTokenForContext.getPdnd()==null) {
				securityTokenForContext.setPdnd(new PDNDTokenInfo());
			}
			securityTokenForContext.getPdnd().setInfo(type, info);
		}
	}
	
}
