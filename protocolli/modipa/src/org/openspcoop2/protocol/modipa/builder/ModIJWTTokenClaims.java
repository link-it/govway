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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * ModIJWTTokenClaims
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIJWTTokenClaims implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean audit;
	private String idTransazione; // no cache key
	private boolean request;
	private String porta;
	private String operazione;
	private boolean sicurezzaRidefinitaOperazione;
	public ModIJWTTokenClaims(boolean audit, String idTransazione, boolean request, String porta, String operazione, boolean sicurezzaRidefinitaOperazione) {
		this.audit = audit;
		this.idTransazione = idTransazione;
		this.request = request;
		this.porta = porta;
		this.operazione = operazione;
		this.sicurezzaRidefinitaOperazione = sicurezzaRidefinitaOperazione;
	}
	
	// header
	
	private boolean addKid;
	private boolean addX5c;
	private boolean addX5cChain;
	private boolean addX5t;
	private boolean addX5u;
	
	private String kid;
	
	private String algorithm;
	
	private String x5uUrl;
	
	private String pem;
	private String jwk;
	
	
	// payload
	
	private Date exp; // no cache key
	private String expValue;
	private Date nbf; // no cache key
	private String nbfValue;
	private Date iat; // no cache key
	private String iatValue;
	
	private String jti; // no cache key
	
	private String audience;
	
	private String clientId;
	
	private String iss;
	
	private String sub;
	
	private Map<String, String> corniceSicurezzaAudit = new HashMap<>();
	
	private String purposeId;
	
	private String dNonce; // no cache key
	
	private Map<String, String> customClaims = new HashMap<>();
	
	
	// claims non cachable (integrita)
	
	private String requestDigest;
	

	public String toCacheKey() {
		
		StringBuilder sb = new StringBuilder("ModI-Token");
		if(this.audit) {
			sb.append("-Audit");
		}
		else {
			sb.append("-Authorization");
		}
		if(this.request) {
			sb.append("-PD-");
		}
		else {
			sb.append("-PA-");
		}
		sb.append(this.porta);
		
		if(this.sicurezzaRidefinitaOperazione) {
			sb.append(" ");
			sb.append(this.operazione);
		}
		
		// header
		addCacheKeyHeader(sb);
	
		// payload
		addCacheKeyPayload(sb);

		return sb.toString();
	}
	private void addCacheKeyHeader(StringBuilder sb) {
				
		sb.append(" add-kid:");
		sb.append(this.addKid);
		
		sb.append(" add-x5c:");
		sb.append(this.addX5c);
		
		sb.append(" add-x5c-chain:");
		sb.append(this.addX5cChain);
		
		sb.append(" add-x5t:");
		sb.append(this.addX5t);
		
		sb.append(" add-x5u:");
		sb.append(this.addX5u);
			
		if(this.kid!=null) {
			sb.append(" kid:");
			sb.append(this.kid);
		}
		
		if(this.algorithm!=null) {
			sb.append(" algorithm:");
			sb.append(this.algorithm);
		}
		
		if(this.x5uUrl!=null) {
			sb.append(" x5u-url:");
			sb.append(this.x5uUrl);
		}
		
		if(this.pem!=null) {
			sb.append(" pem:");
			sb.append(this.pem);
		}
		
		if(this.jwk!=null) {
			sb.append(" jwk:");
			sb.append(this.jwk);
		}
	}
	private void addCacheKeyPayload(StringBuilder sb) {
		if(this.audience!=null) {
			sb.append(" aud:");
			sb.append(this.audience);
		}
		
		if(this.clientId!=null) {
			sb.append(" client_id:");
			sb.append(this.clientId);
		}
		
		if(this.iss!=null) {
			sb.append(" iss:");
			sb.append(this.iss);
		}
		
		if(this.sub!=null) {
			sb.append(" sub:");
			sb.append(this.sub);
		}
		
		if(!this.corniceSicurezzaAudit.isEmpty()) {
			for (Map.Entry<String,String> entry : this.corniceSicurezzaAudit.entrySet()) {
				sb.append(" audit-").append(entry.getKey()).append(":");
				sb.append(entry.getValue());
			}
		}
		
		if(this.purposeId!=null) {
			sb.append(" purposeId:");
			sb.append(this.purposeId);
		}
		
		if(!this.customClaims.isEmpty()) {
			for (Map.Entry<String,String> entry : this.customClaims.entrySet()) {
				sb.append(" custom-").append(entry.getKey()).append(":");
				sb.append(entry.getValue());
			}
		}
		
	}

	public void setInfoNonCachableInBusta(Busta busta) {
		
		// id transazione
		if(this.idTransazione!=null) {
			if(this.audit) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_ORIGIN_TRANSACTION_ID, this.idTransazione);
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ORIGIN_TRANSACTION_ID, this.idTransazione);
			}
		}
		
		
		// iat, nbf, exp
		setDateNonCachableInBusta(busta);
		
		// jti
		if(this.jti!=null) {
			if(this.audit) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_ID, this.jti);
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_ID, this.jti);
			}
		}
		
		// dnonce
		// per adesso non viene registrato nella busta
	}
	private void setDateNonCachableInBusta(Busta busta) {
		// iat
		if(this.iatValue!=null) {
			if(this.audit) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_IAT, this.iatValue);
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT, this.iatValue);
			}
		}

		// nbf
		if(this.nbfValue!=null) {
			if(this.audit) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_NBF, this.nbfValue);
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF, this.nbfValue);
			}
		}
		
		// exp
		if(this.expValue!=null) {
			if(this.audit) {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_EXP, this.expValue);
			}
			else {
				busta.addProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP, this.expValue);
			}
		}
	}
	
	public String getExpValue() {
		return this.expValue;
	}
	public void setExpValue(String expValue) {
		this.expValue = expValue;
	}
	public String getNbfValue() {
		return this.nbfValue;
	}
	public void setNbfValue(String nbfValue) {
		this.nbfValue = nbfValue;
	}
	public String getIatValue() {
		return this.iatValue;
	}
	public void setIatValue(String iatValue) {
		this.iatValue = iatValue;
	}
	public Date getExp() {
		return this.exp;
	}
	public void setExp(Date exp) {
		this.exp = exp;
	}
	public Date getNbf() {
		return this.nbf;
	}
	public void setNbf(Date nbf) {
		this.nbf = nbf;
	}
	public Date getIat() {
		return this.iat;
	}
	public void setIat(Date iat) {
		this.iat = iat;
	}
	
	public String getJti() {
		return this.jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	
	public String getAudience() {
		return this.audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getKid() {
		return this.kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	
	public String getRequestDigest() {
		return this.requestDigest;
	}
	public void setRequestDigest(String requestDigest) {
		this.requestDigest = requestDigest;
	}
	
	public Map<String, String> getCorniceSicurezzaAudit() {
		return this.corniceSicurezzaAudit;
	}
	public void setCorniceSicurezzaAudit(Map<String, String> corniceSicurezzaAudit) {
		this.corniceSicurezzaAudit = corniceSicurezzaAudit;
	}
	public void addCorniceSicurezzaAudit(String key, String value) {
		this.corniceSicurezzaAudit.put(key, value);
	}
	
	public String getIss() {
		return this.iss;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	public String getSub() {
		return this.sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	
	public String getPurposeId() {
		return this.purposeId;
	}
	public void setPurposeId(String purposeId) {
		this.purposeId = purposeId;
	}
	
	public String getdNonce() {
		return this.dNonce;
	}
	public void setdNonce(String dNonce) {
		this.dNonce = dNonce;
	}
	
	public Map<String, String> getCustomClaims() {
		return this.customClaims;
	}
	public void setCustomClaims(Map<String, String> customClaims) {
		this.customClaims = customClaims;
	}
	public void addCustomClaim(String key, String value) {
		this.customClaims.put(key, value);
	}
	
	public String getAlgorithm() {
		return this.algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
	public boolean isAddKid() {
		return this.addKid;
	}
	public void setAddKid(boolean addKid) {
		this.addKid = addKid;
	}
	public boolean isAddX5c() {
		return this.addX5c;
	}
	public void setAddX5c(boolean addX5c) {
		this.addX5c = addX5c;
	}
	public boolean isAddX5cChain() {
		return this.addX5cChain;
	}
	public void setAddX5cChain(boolean addX5cChain) {
		this.addX5cChain = addX5cChain;
	}
	public boolean isAddX5t() {
		return this.addX5t;
	}
	public void setAddX5t(boolean addX5t) {
		this.addX5t = addX5t;
	}
	public boolean isAddX5u() {
		return this.addX5u;
	}
	public void setAddX5u(boolean addX5u) {
		this.addX5u = addX5u;
	}
	public String getX5uUrl() {
		return this.x5uUrl;
	}
	public void setX5uUrl(String x5uUrl) {
		this.x5uUrl = x5uUrl;
	}
	
	public String getPem() {
		return this.pem;
	}
	public void setPem(String pem) {
		this.pem = pem;
	}
	public String getJwk() {
		return this.jwk;
	}
	public void setJwk(String jwk) {
		this.jwk = jwk;
	}
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
}
