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
package org.openspcoop2.pdd.core.token.attribute_authority;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.core.token.parser.TokenUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JSONUtils;
import org.slf4j.Logger;

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
	protected Map<String, Serializable> claims;
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
	public void init(String raw, Map<String, Serializable> claims) {
		this.raw = raw;
		this.claims = claims;
		this.now = DateManager.getDate();
	}
	@Override
	public void init(byte[] content) {
		throw new UtilsRuntimeException("unsupported");
	}
	
	@Override
	public String getContentAsString() {
		throw new UtilsRuntimeException("unsupported");
	}

	@Override
	public void checkHttpTransaction(Integer httpResponseCode) throws UtilsException{
		this.httpResponseCode = httpResponseCode;
		switch (this.parser) {
		case jws:
		case json:
		case custom:
			if(this.httpResponseCode!=null && 
				(this.httpResponseCode.intValue() < 200 || this.httpResponseCode.intValue()>299)) {
				String msgError = "Connessione terminata con errore (codice trasporto: "+this.httpResponseCode.intValue()+")";
				throw new UtilsException(msgError+": "+this.raw);
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
	public Map<String, Serializable> getAttributes() {
		
		Map<String, Serializable> attributes = null;
		if(TipologiaResponseAttributeAuthority.custom.equals(this.parser)) {
			return attributes; // null voluto
		}
		
		if(TipologiaResponseAttributeAuthority.json.equals(this.parser) &&
				(this.attributesClaims==null || this.attributesClaims.isEmpty())) {
			return this.claims;
		}
		
		if(TipologiaResponseAttributeAuthority.jws.equals(this.parser) &&
			(this.attributesClaims==null || this.attributesClaims.isEmpty()) 
			){
			return attributes; // null voluto
		}
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		attributes = jsonUtils.convertToMap(this.log, ("Attribute Authority: "+this.attributeAuthority), this.raw, this.attributesClaims);
				
		return attributes;
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
		List<String> lNull = null;
		switch (this.parser) {
		case jws:
			return TokenUtilities.getClaimAsList(this.claims,Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE);
		case custom:
		case json:
		default:
			return lNull;
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
