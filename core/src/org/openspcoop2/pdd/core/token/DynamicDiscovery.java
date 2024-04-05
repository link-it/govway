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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.IDynamicDiscoveryParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * DynamicDiscovery
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicDiscovery extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DynamicDiscovery() {} // per serializzatore
	public DynamicDiscovery(Integer httpResponseCode, TipologiaClaims tipo, String rawResponse, IDynamicDiscoveryParser parser) throws UtilsException {
		this.rawResponse = rawResponse;
		this.type = tipo;
		JSONUtils jsonUtils = JSONUtils.getInstance();
		if(jsonUtils.isJson(this.rawResponse)) {
			JsonNode root = jsonUtils.getAsNode(this.rawResponse);
			Map<String, Serializable> readClaims = jsonUtils.convertToSimpleMap(root);
			if(readClaims!=null && readClaims.size()>0) {
				this.claims.putAll(readClaims);
			}
		}
		parser.init(this.rawResponse, this.claims);
		if(httpResponseCode!=null) {
			parser.checkHttpTransaction(httpResponseCode);
		}
		this.jwksUri = parser.getJwksUri();
		this.introspectionEndpoint = parser.getIntrospectionEndpoint();
		this.userinfoEndpoint = parser.getUserInfoEndpoint();
		
		this.valid = true;
	}
	
	public DynamicDiscovery(String errorDetails, Integer httpResponseCode, byte[] rawResponse, TipologiaClaims tipo) {
		this.valid = false;
		this.claims = null;
		this.type = tipo;
		if(httpResponseCode!=null) {
			this.httpResponseCode = httpResponseCode+"";
		}
		if(rawResponse!=null) {
			this.rawResponse = new String(rawResponse);
		}
		this.errorDetails = errorDetails;
	}
		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	// Indicazione se il token e' valido
	private boolean valid;
	
	// jwk
	private String jwksUri;
	
	// introspection
	private String introspectionEndpoint;
	
	// userInfo
	private String userinfoEndpoint;
			
	// Claims
	private Map<String,Serializable> claims = new HashMap<>();
		
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json

	// RawResponse
	private String rawResponse;
	
	// TipologiaClaims
	private TipologiaClaims type;
	
	// HttpCode (nel caso di errori)
	private String httpResponseCode;
	
	// Failed
	private String errorDetails;
		
	public boolean isValid() {
		return this.valid;
	}
	public boolean getValid() { // clone
		return this.valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public String getJwksUri() {
		return this.jwksUri;
	}
	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}
	
	public String getIntrospectionEndpoint() {
		return this.introspectionEndpoint;
	}
	public void setIntrospectionEndpoint(String introspectionEndpoint) {
		this.introspectionEndpoint = introspectionEndpoint;
	}
	
	public String getUserinfoEndpoint() {
		return this.userinfoEndpoint;
	}
	public void setUserinfoEndpoint(String userinfoEndpoint) {
		this.userinfoEndpoint = userinfoEndpoint;
	}
	
	public Map<String, Serializable> getClaims() {
		return this.claims;
	}
	public void setClaims(Map<String, Serializable> claims) {
		this.claims = claims;
	}
			
	public String getRawResponse() {
		return this.rawResponse;
	}
	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}
	
	public TipologiaClaims getType() {
		return this.type;
	}
	public void setType(TipologiaClaims type) {
		this.type = type;
	}
	
	public String getHttpResponseCode() {
		return this.httpResponseCode;
	}
	public void setHttpResponseCode(String httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}
	
	public String getErrorDetails() {
		return this.errorDetails;
	}
	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}
	
}
