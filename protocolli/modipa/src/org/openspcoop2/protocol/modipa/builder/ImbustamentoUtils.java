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

package org.openspcoop2.protocol.modipa.builder;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.GestoreTokenNegoziazioneUtilities;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.slf4j.Logger;

/**
 * ImbustamentoUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImbustamentoUtils {
	
	private ImbustamentoUtils() {}

	private static void logError(Logger log, String msg, Exception e) {
		log.error(msg,e);
	}
	
	// invocato in Imbustamento
	
	private static final String PREFIX = "l'assegnazione di una token policy di negoziazione al connettore";
	private static final String NO_SIGNED_JWT = "non è di tipo 'SignedJWT'";
	private static String getPolicyIndicata(String tokenPolicy) {
		return "; la policy indicata '"+tokenPolicy+"' ";
	}
	
	public static PolicyNegoziazioneToken readPolicyNegoziazioneToken(Logger log, IState state, IDSoggetto soggettoFruitore, IDServizio idServizio, String azione,
			RequestInfo requestInfo, StringBuilder sbRequired) throws ProtocolException {
		try {
			
			PolicyNegoziazioneToken policyNegoziazioneToken = null;
			
			IDServizio idServizioClone = idServizio.clone();
			idServizioClone.setAzione(azione);
			
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(state);
			boolean functionAsRouter = false;
			Connettore connettore = configurazionePdDManager.getForwardRoute(soggettoFruitore,idServizio, functionAsRouter, requestInfo);
			if(connettore==null) {
				throw new ProtocolException("Connettore non individuato");
			}
			
			if(connettore.sizePropertyList()<=0) {
				sbRequired.append(PREFIX); // prefisso sarà "... richiede "
				return policyNegoziazioneToken;
			}
			String tokenPolicy = null;
			for (Property p : connettore.getPropertyList()) {
				if(CostantiDB.CONNETTORE_TOKEN_POLICY.equals(p.getNome())) {
					tokenPolicy = p.getValore();
				}
			}
			if(tokenPolicy!=null) {
				tokenPolicy = tokenPolicy.trim();
			}
			if(tokenPolicy==null || StringUtils.isEmpty(tokenPolicy)) {
				sbRequired.append(PREFIX); // prefisso sarà "... richiede "
				return policyNegoziazioneToken;
			}
			
			GenericProperties gp = configurazionePdDManager.getGenericProperties(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, tokenPolicy);
			if(gp==null) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non esiste"); // prefisso sarà "... richiede "
				return policyNegoziazioneToken;
			}
			
			return TokenUtilities.convertTo(gp);
	    	
		}catch(Exception e) {
			logError(log, "Errore durante la lettura del connettore (tramite la sicurezza messaggio): "+e.getMessage(),e);
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static KeystoreParams readKeystoreParams(Logger log, PolicyNegoziazioneToken policyNegoziazioneToken, StringBuilder sbRequired) throws ProtocolException {
		try {
			
			KeystoreParams kp = null;
			
			String tokenPolicy = policyNegoziazioneToken.getName();
			
			if(!policyNegoziazioneToken.isRfc7523x509Grant()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+NO_SIGNED_JWT); // prefisso sarà "... richiede "
				return kp;
			}
			
			if(policyNegoziazioneToken.isJwtSignKeystoreApplicativoModI()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è utilizzabile essendo configurata con una modalità di keystore '"+Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL+"'"); // prefisso sarà "... richiede "
				return kp;
			}
			else if(policyNegoziazioneToken.isJwtSignKeystoreFruizioneModI()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è utilizzabile essendo configurata con una modalità di keystore '"+Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL+"'"); // prefisso sarà "... richiede "
				return kp;
			}
			else {
				kp = GestoreTokenNegoziazioneUtilities.readKeystoreParams(policyNegoziazioneToken);
				if(kp==null) {
					sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è configurata correttamente"); // prefisso sarà "... richiede "
					return kp;
				}
			}
			
			return kp;
	    	
		}catch(Exception e) {
			logError(log, "Errore durante la lettura del connettore (tramite la sicurezza messaggio): "+e.getMessage(),e);
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static String readClientId(Logger log, PolicyNegoziazioneToken policyNegoziazioneToken, 
			StringBuilder sbRequired) throws ProtocolException {
		try {
			
			String clientId = null;
			
			String tokenPolicy = policyNegoziazioneToken.getName();
			
			if(!policyNegoziazioneToken.isRfc7523x509Grant()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+NO_SIGNED_JWT); // prefisso sarà "... richiede "
				return clientId;
			}
			
			if(policyNegoziazioneToken.isJwtClientIdApplicativoModI()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è utilizzabile essendo configurato un identificativo client con modalità '"+Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL+"'"); // prefisso sarà "... richiede "
				return clientId;
			}
			else if(policyNegoziazioneToken.isJwtClientIdFruizioneModI()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è utilizzabile essendo configurata un identificativo client con modalità '"+Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL+"'"); // prefisso sarà "... richiede "
				return clientId;
			}
			else {
				clientId = policyNegoziazioneToken.getJwtClientId();
			}
			
			return clientId;
	    	
		}catch(Exception e) {
			logError(log, "Errore durante la lettura del kid presente nella token policy (tramite la sicurezza messaggio): "+e.getMessage(),e);
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static String readKID(Logger log, PolicyNegoziazioneToken policyNegoziazioneToken, 
			String clientId, KeystoreParams kp, StringBuilder sbRequired) throws ProtocolException {
		try {
			
			String kid = null;
			
			String tokenPolicy = policyNegoziazioneToken.getName();
			
			if(!policyNegoziazioneToken.isRfc7523x509Grant()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+NO_SIGNED_JWT); // prefisso sarà "... richiede "
				return kid;
			}
			
			if(policyNegoziazioneToken.isJwtSignIncludeKeyIdApplicativoModI()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è utilizzabile essendo configurato un identificativo kid con modalità '"+Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL+"'"); // prefisso sarà "... richiede "
				return kid;
			}
			else if(policyNegoziazioneToken.isJwtSignIncludeKeyIdFruizioneModI()) {
				sbRequired.append(PREFIX+getPolicyIndicata(tokenPolicy)+"non è utilizzabile essendo configurata un identificativo kid con modalità '"+Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL+"'"); // prefisso sarà "... richiede "
				return kid;
			}
			else if( policyNegoziazioneToken.isJwtSignIncludeKeyIdWithClientId() ) {
				kid = clientId;
			}
			else if( policyNegoziazioneToken.isJwtSignIncludeKeyIdWithKeyAlias() ) {
				kid = kp.getKeyAlias();
			}
			else if( policyNegoziazioneToken.isJwtSignIncludeKeyIdCustom()) {
				kid = policyNegoziazioneToken.getJwtSignIncludeKeyIdCustom();
			}
			
			return kid;
	    	
		}catch(Exception e) {
			logError(log, "Errore durante la lettura del kid presente nella token policy (tramite la sicurezza messaggio): "+e.getMessage(),e);
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
}
