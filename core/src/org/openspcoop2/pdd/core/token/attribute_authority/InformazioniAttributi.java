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


package org.openspcoop2.pdd.core.token.attribute_authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * InformazioniAttributi
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniAttributi extends org.openspcoop2.utils.beans.BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniAttributi() {} // per serializzatore
	public InformazioniAttributi(String sourceAttributeAuthority) {
		this.sourceAttributeAuthority = sourceAttributeAuthority;
	} // per test
	public InformazioniAttributi(String sourceAttributeAuthority, String rawResponse, IRetrieveAttributeAuthorityResponseParser responseParser) throws Exception {
		this(null,sourceAttributeAuthority, rawResponse,responseParser);
	}
	public InformazioniAttributi(Integer httpResponseCode, String sourceAttributeAuthority, String rawResponse, IRetrieveAttributeAuthorityResponseParser responseParser) throws Exception {
		init(httpResponseCode, sourceAttributeAuthority, 
				null, rawResponse, responseParser);
	}
	public InformazioniAttributi(String sourceAttributeAuthority, byte[]raw, IRetrieveAttributeAuthorityResponseParser responseParser) throws Exception {
		this(null,sourceAttributeAuthority, raw,responseParser);
	}
	public InformazioniAttributi(Integer httpResponseCode, String sourceAttributeAuthority, byte[]raw, IRetrieveAttributeAuthorityResponseParser responseParser) throws Exception {
		init(httpResponseCode, sourceAttributeAuthority, 
				raw, null, responseParser);
	}
	private void init(Integer httpResponseCode, String sourceAttributeAuthority, 
			byte[]raw, String rawResponse, IRetrieveAttributeAuthorityResponseParser responseParser) throws Exception {
		this.raw = raw;
		this.rawResponse = rawResponse;
		this.sourceAttributeAuthority = sourceAttributeAuthority;

		if(this.raw!=null) {
			responseParser.init(this.raw);
			String rawString = responseParser.getContentAsString();
			if(rawString!=null && !"".equals(rawString)) {
				this.rawResponse = rawString;
			}
		}
		else {
			JSONUtils jsonUtils = JSONUtils.getInstance();
			if(jsonUtils.isJson(this.rawResponse)) {
				JsonNode root = jsonUtils.getAsNode(this.rawResponse);
				Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
				if(readClaims!=null && readClaims.size()>0) {
					this.claims.putAll(readClaims);
				}
			}
			responseParser.init(this.rawResponse, this.claims);
		}
		if(httpResponseCode!=null) {
			responseParser.checkHttpTransaction(httpResponseCode);
		}
		this.valid = responseParser.isValid();
		this.attributes = responseParser.getAttributes();
		this.iss = responseParser.getIssuer();
		this.sub = responseParser.getSubject();
		List<String> a = responseParser.getAudience();
		if(a!=null && a.size()>0) {
			if(this.aud == null) {
				this.aud = new ArrayList<>();
			}
			this.aud.addAll(a);
		}
		this.exp = responseParser.getExpired();
		this.iat = responseParser.getIssuedAt();
		this.nbf = responseParser.getNotToBeUsedBefore();
		this.identifier = responseParser.getIdentifier();
	}
	public InformazioniAttributi(boolean saveSourceAttributeResponseInfo, InformazioniAttributi ... informazioniTokens ) throws Exception {
		if(informazioniTokens!=null && informazioniTokens.length>0) {
			
			this.multipleAttributeAuthorities = true;
			for (int i = 0; i < informazioniTokens.length; i++) {
				this.attributeAuthorities.add(informazioniTokens[i].getSourceAttributeAuthority());
			}
			
			if(saveSourceAttributeResponseInfo) {
				this.sourcesAttributeInfo = new HashMap<>();
				for (int i = 0; i < informazioniTokens.length; i++) {
					if(informazioniTokens[i].getRawResponse()!=null) {
						this.sourcesAttributeInfo.put(informazioniTokens[i].getSourceAttributeAuthority(),
								informazioniTokens[i].getRawResponse());
					}
					else {
						this.sourcesAttributeInfo.put(informazioniTokens[i].getSourceAttributeAuthority(),
								"N.D.");
					}
				}
			}
			else {
				this.sourceAttributeAuthorities = new ArrayList<>();
				for (int i = 0; i < informazioniTokens.length; i++) {
					this.sourceAttributeAuthorities.add(informazioniTokens[i].getSourceAttributeAuthority());
				}
			}
			
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getClaims().size()>0) {
					this.claims.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getClaims());
				}
			}
			
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getAttributes()!=null && informazioniTokens[i].getAttributes().size()>0) {
					if(this.attributes==null) {
						this.attributes = new HashMap<String, Object>();
					}
					this.attributes.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getAttributes());
				}
			}
			
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getIss()!=null) {
					if(this.aaIss==null) {
						this.aaIss = new HashMap<String, String>();
					}
					this.aaIss.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getIss());
				}
			}
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getSub()!=null) {
					if(this.aaSub==null) {
						this.aaSub = new HashMap<String, String>();
					}
					this.aaSub.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getSub());
				}
			}
			
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getAud()!=null && !informazioniTokens[i].getAud().isEmpty()) {
					if(this.aaAud==null) {
						this.aaAud = new HashMap<String, List<String>>();
					}
					this.aaAud.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getAud());
				}
			}

			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getExp()!=null) {
					if(this.aaExp==null) {
						this.aaExp = new HashMap<String, Date>();
					}
					this.aaExp.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getExp());
				}
			}
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getIat()!=null) {
					if(this.aaIat==null) {
						this.aaIat = new HashMap<String, Date>();
					}
					this.aaIat.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getIat());
				}
			}
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getNbf()!=null) {
					if(this.aaNbf==null) {
						this.aaNbf = new HashMap<String, Date>();
					}
					this.aaNbf.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getNbf());
				}
			}
			
			for (int i = 0; i < informazioniTokens.length; i++) {
				if(informazioniTokens[i].getIdentifier()!=null) {
					if(this.aaIdentifier==null) {
						this.aaIdentifier = new HashMap<String, String>();
					}
					this.aaIdentifier.put(informazioniTokens[i].getSourceAttributeAuthority(),informazioniTokens[i].getIdentifier());
				}
			}
		}
	}
	

		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	// Indicazione se il token e' valido
	private boolean valid;
	
	// Attributi
	private Map<String, Object> attributes;

	// String representing the issuer for this attribute response
	private String iss; 
	private Map<String, String> aaIss;
	
	// String representing the Subject of this attribute response
	private String sub; 
	private Map<String, String> aaSub;
		
	// Service-specific string identifier or list of string identifiers representing the intended audience for this attribute response
	private List<String> aud;
	private Map<String, List<String>> aaAud;
	
	// Indicate when this attribute response will expire
	private Date exp;
	private Map<String, Date> aaExp;
	
	// Indicate when this attribute response was originally issued
	private Date iat;
	private Map<String, Date> aaIat;
	
	// Indicate when this attribute response is not to be used before.
	private Date nbf;
	private Map<String, Date> aaNbf;
	
	// String representing the unique identifier for the attribute response
	private String identifier;
	private Map<String, String> aaIdentifier;
	
	// Claims
	private Map<String,Object> claims = new HashMap<String,Object>();
	
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	private Boolean multipleAttributeAuthorities = false;
	private List<String> attributeAuthorities = new ArrayList<String>();
	
	// RawResponse
	private byte[] raw;
	private String rawResponse;
	
	// Nome dell'Attribute Authority dove sono stati reperiti gli attributi
	private String sourceAttributeAuthority;
	
	// Multiple Source
	private List<String> sourceAttributeAuthorities = null;
	private Map<String,String> sourcesAttributeInfo = null;
	
	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public Boolean isMultipleAttributeAuthorities() {
		if(this.multipleAttributeAuthorities) {
			return this.multipleAttributeAuthorities;
		}
		return null;
	}
	public List<String> getAttributeAuthorities() {
		if(this.attributeAuthorities!=null && !this.attributeAuthorities.isEmpty()) {
			return this.attributeAuthorities;
		}
		return null;
	}
	
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public List<String> getAttributesNames(){
		if(this.attributes!=null && !this.attributes.isEmpty()) {
			if(this.multipleAttributeAuthorities!=null && this.multipleAttributeAuthorities) {
				// informazioni normalizzate, devo scendere di un livello, senno al primo degli attributi ci sono le attribute authority
				List<String> attributesNames = new ArrayList<String>();
				for (String attrAuthName : this.attributes.keySet()) {
					Object o = this.attributes.get(attrAuthName);
					if(o instanceof Map) {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> attributes = (Map<String, Object>) o;
							if(attributes!=null && !attributes.isEmpty()) {
								for (String attrName : attributes.keySet()) {
									if(!attributesNames.contains(attrName)) {
										attributesNames.add(attrName);
									}
								}
							}
						}catch(Throwable t) {
							OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("getAttributesNames failed (A.A. "+attrAuthName+"): "+t.getMessage(),t);
						}
					}
				}
				Collections.sort(attributesNames);
				return attributesNames;
			}
			else {
				List<String> attributesNames = new ArrayList<String>();
				for (String attrName : this.attributes.keySet()) {
					attributesNames.add(attrName);
				}
				Collections.sort(attributesNames);
				return attributesNames;
			}
		}
		return null;
	}
	
	public String getIss() {
		return this.iss;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	public Map<String, String> getAaIss() {
		return this.aaIss;
	}
	public void setAaIss(Map<String, String> aaIss) {
		this.aaIss = aaIss;
	}

	public String getSub() {
		return this.sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public Map<String, String> getAaSub() {
		return this.aaSub;
	}
	public void setAaSub(Map<String, String> aaSub) {
		this.aaSub = aaSub;
	}

	public List<String> getAud() {
		return this.aud;
	}
	public void setAud(List<String> aud) {
		this.aud = aud;
	}
	public Map<String, List<String>> getAaAud() {
		return this.aaAud;
	}
	public void setAaAud(Map<String, List<String>> aaAud) {
		this.aaAud = aaAud;
	}

	public Date getExp() {
		return this.exp;
	}
	public void setExp(Date exp) {
		this.exp = exp;
	}
	public Map<String, Date> getAaExp() {
		return this.aaExp;
	}
	public void setAaExp(Map<String, Date> aaExp) {
		this.aaExp = aaExp;
	}

	public Date getIat() {
		return this.iat;
	}
	public void setIat(Date iat) {
		this.iat = iat;
	}
	public Map<String, Date> getAaIat() {
		return this.aaIat;
	}
	public void setAaIat(Map<String, Date> aaIat) {
		this.aaIat = aaIat;
	}

	public Date getNbf() {
		return this.nbf;
	}
	public void setNbf(Date nbf) {
		this.nbf = nbf;
	}
	public Map<String, Date> getAaNbf() {
		return this.aaNbf;
	}
	public void setAaNbf(Map<String, Date> aaNbf) {
		this.aaNbf = aaNbf;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	public void setIdentifier(String id) {
		this.identifier = id;
	}
	public Map<String, String> getAaIdentifier() {
		return this.aaIdentifier;
	}
	public void setAaIdentifier(Map<String, String> aaIdentifier) {
		this.aaIdentifier = aaIdentifier;
	}
	
	public Map<String, Object> getClaims() {
		return this.claims;
	}

	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}
			
	public String getRawResponse() {
		return this.rawResponse;
	}
	
	public String getSourceAttributeAuthority() {
		return this.sourceAttributeAuthority;
	}
	
	public Map<String, String> getSourcesAttributeInfo() {
		return this.sourcesAttributeInfo;
	}

	public List<String> getSourceAttributeAuthorities() {
		return this.sourceAttributeAuthorities;
	}
	
}