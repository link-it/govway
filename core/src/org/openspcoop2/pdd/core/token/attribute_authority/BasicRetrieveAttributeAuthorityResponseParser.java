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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.TokenUtils;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JSONUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**     
 * BasicRetrieveAttributeAuthorityResponseParser
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicRetrieveAttributeAuthorityResponseParser implements IRetrieveAttributeAuthorityResponseParser {

	protected Integer httpResponseCode;
	protected String raw;
	protected Map<String, Object> claims;
	protected TipologiaResponseAttributeAuthority parser;
	protected Date now;
	protected List<String> attributesClaims;
	
	protected String attributeAuthority;
	protected Logger log;
	
	public BasicRetrieveAttributeAuthorityResponseParser(String attributeAuthority, Logger log, TipologiaResponseAttributeAuthority parser, List<String> attributesClaims) {
		this.attributeAuthority = attributeAuthority;
		this.log = log;
		this.parser = parser;
		this.attributesClaims = attributesClaims;
	}
	
	@Override
	public void init(String raw, Map<String, Object> claims) {
		this.raw = raw;
		this.claims = claims;
		this.now = DateManager.getDate();
	}
	@Override
	public void init(byte[] content) {
		throw new RuntimeException("unsupported");
	}
	
	@Override
	public String getContentAsString() {
		throw new RuntimeException("unsupported");
	}

	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws Exception{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case jws:
		case json:
		case custom:
			if(this.httpResponseCode!=null && 
				(this.httpResponseCode.intValue() < 200 || this.httpResponseCode.intValue()>299)) {
				String msgError = "Connessione terminata con errore (codice trasporto: "+this.httpResponseCode.intValue()+")";
				throw new Exception(msgError+": "+this.raw);
			}
			break;
		}
	}
	
	@Override
	public boolean isValid() {
		
		if(this.claims==null || this.claims.size()<=0) {
			return false;
		}
		
		switch (this.parser) {
		case custom:
		case json:
		case jws:
			return true;
		}
		
		return false;
	}
	
	@Override
	public Map<String, Object> getAttributes() {
		
		if(TipologiaResponseAttributeAuthority.custom.equals(this.parser)) {
			return null;
		}
		
		if(TipologiaResponseAttributeAuthority.json.equals(this.parser) &&
				(this.attributesClaims==null || this.attributesClaims.isEmpty())) {
			return this.claims;
		}
		
		if(TipologiaResponseAttributeAuthority.jws.equals(this.parser)) {
			if(this.attributesClaims==null || this.attributesClaims.isEmpty()) {
				return null;
			}			
		}
		
		Map<String, Object> attributes = new HashMap<String, Object>();
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		if(jsonUtils.isJson(this.raw)) {
			try {
				JsonNode jsonResponse = jsonUtils.getAsNode(this.raw);
				if(jsonResponse instanceof ObjectNode) {
					
					Iterator<String> iterator = jsonResponse.fieldNames();
					while(iterator.hasNext()) {
						String field = iterator.next();
						if(this.attributesClaims.contains(field)) {
							try {
								JsonNode selectedClaim = jsonResponse.get(field);
								if(selectedClaim instanceof ValueNode) {
									attributes.put(field, selectedClaim.asText());
								}
								else if(selectedClaim instanceof ArrayNode) {
									ArrayNode array = (ArrayNode) selectedClaim;
									if(array.size()>0) {
										JsonNode arrayFirstChildNode = array.get(0);
										if(arrayFirstChildNode instanceof ValueNode) {
											List<Object> l = new ArrayList<Object>();
											for (int i = 0; i < array.size(); i++) {
												JsonNode arrayChildNode = array.get(i);
												Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(arrayChildNode);
												if(readClaims!=null && readClaims.size()>0) {
													if(readClaims.size()==1) {
														for (String claim : readClaims.keySet()) {
															Object value = readClaims.get(claim);
															l.add(value);
														}
													}
													else {
														for (String claim : readClaims.keySet()) {
															Object value = readClaims.get(claim);
															_addAttribute(attributes, claim==null ? field : claim, value);
														}
													}
												}
											}
											if(!l.isEmpty()) {
												_addAttribute(attributes, field, l);
											}
										}
										else {
											for (int i = 0; i < array.size(); i++) {
												JsonNode arrayChildNode = array.get(i);
												Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(arrayChildNode);
												if(readClaims!=null && readClaims.size()>0) {
													for (String claim : readClaims.keySet()) {
														Object value = readClaims.get(claim);
														_addAttribute(attributes, claim, value);
													}
												}
											}
										}
									}
								}
								else {
									Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(selectedClaim);
									if(readClaims!=null && readClaims.size()>0) {
										for (String claim : readClaims.keySet()) {
											Object value = readClaims.get(claim);
											_addAttribute(attributes, claim, value);
										}
									}
								}
							}catch(Throwable e) {
								this.log.error("Errore durante la conversione in mappa del claim '"+field+"' (Attribute Authority: "+this.attributeAuthority+"): "+e.getMessage(),e);
							}
						}
					}
					
				}
			}catch(Exception e) {}
		}
		
		return attributes;
	}
	private void _addAttribute(Map<String, Object> attributes, String claim, Object value) {
		if(attributes.containsKey(claim)) {
			for (int j = 1; j < 100; j++) {
				String newKeyClaim = "_"+claim+"_"+j;
				if(!attributes.containsKey(newKeyClaim)) {
					attributes.put(newKeyClaim, attributes.get(claim));
					break;
				}
			}
		}
		attributes.put(claim,value); // sovrascrive claim gia' esistente (e' stato salvato con un nome speciale)
	}
	
	// String representing the issuer for this attribute response
	@Override
	public String getIssuer() {
		String tmp = null;
		switch (this.parser) {
		case jws:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_ISSUER);
			break;
		case custom:
		case json:
			return null;
		}
		return tmp;
	}
	
	// String representing the Subject of this attribute response
	@Override
	public String getSubject() {
		String tmp = null;
		switch (this.parser) {
		case jws:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_SUBJECT);
			break;
		case custom:
		case json:
			return null;
		}
		return tmp;
	}
	
	// Service-specific string identifier or list of string identifiers representing the intended audience for this attribute response
	@Override
	public List<String> getAudience() {
		switch (this.parser) {
		case jws:
			return TokenUtilities.getClaimAsList(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
		case custom:
		case json:
		default:
			return null;
		}
	}
	
	// Indicate when this attribute response will expire
	@Override
	public Date getExpired() {
		String tmp = null;
		switch (this.parser) {
		case jws:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_EXPIRED);
			break;
		case custom:
		case json:
			return null;
		}
		if(tmp!=null) {
			return TokenUtils.parseTimeInSecond(tmp);
		}
		return null;
	}
	
	// Indicate when this attribute response was originally issued
	@Override
	public Date getIssuedAt() {
		String tmp = null;
		switch (this.parser) {
		case jws:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_ISSUED_AT);
			break;
		case custom:
		case json:
			return null;
		}
		if(tmp!=null) {
			return TokenUtils.parseTimeInSecond(tmp);
		}
		return null;
	}
	
	// Indicate when this attribute response is not to be used before.
	@Override
	public Date getNotToBeUsedBefore() {
		String tmp = null;
		switch (this.parser) {
		case jws:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_NOT_TO_BE_USED_BEFORE);
			break;
		case custom:
		case json:
			return null;
		}
		if(tmp!=null) {
			return TokenUtils.parseTimeInSecond(tmp);
		}
		return null;
	}
	
	// String representing the unique identifier for the attribute response
	@Override
	public String getIdentifier() {
		String tmp = null;
		switch (this.parser) {
		case jws:
			tmp =  TokenUtilities.getClaimAsString(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_JWT_ID);
			break;
		case custom:
		case json:
			return null;
		}
		return tmp;
	}
	
	



	
}
