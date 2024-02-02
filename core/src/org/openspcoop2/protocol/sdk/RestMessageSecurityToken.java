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
package org.openspcoop2.protocol.sdk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * RestMessageSecurityToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$AuthorizationMessageSecurityToken
 */
public class RestMessageSecurityToken extends AbstractMessageSecurityToken<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String httpHeaderName;
	private String queryParameterName;
	private String formParameterName;

	public String getQueryParameterName() {
		return this.queryParameterName;
	}
	public void setQueryParameterName(String queryParameterName) {
		this.queryParameterName = queryParameterName;
	}
	public String getFormParameterName() {
		return this.formParameterName;
	}
	public void setFormParameterName(String formParameterName) {
		this.formParameterName = formParameterName;
	}
	public String getHttpHeaderName() {
		return this.httpHeaderName;
	}
	public void setHttpHeaderName(String httpHeaderName) {
		this.httpHeaderName = httpHeaderName;
	}
	
	public String getHeader() {
		if(this.token!=null) {
			String [] split = this.token.split("\\.");
			if(split!=null && split.length>0) {
				return split[0];
			}
		}
		return null;
	}
	public String getDecodedHeader() {
		String hdr = this.getHeader();
		if(hdr!=null) {
			return new String(Base64Utilities.decode(hdr));
		}
		return null;
	}
	public Map<String, String> getHeaderClaims() throws UtilsException {
		Map<String, String> m = null;
		if(this.token!=null) {
			if(this.readClaimsHeader==null) {
				this.initReadClaimsHeader();
			}
			return this.readClaimsHeader;
		}
		return m;
	}
	public String getHeaderClaim(String claim) throws UtilsException {
		if(this.token!=null) {
			if(this.readClaimsHeader==null) {
				this.initReadClaimsHeader();
			}
			if(this.readClaimsHeader!=null) {
				return this.readClaimsHeader.get(claim);
			}
		}
		return null;
	}
	
	public String getPayload() {
		if(this.token!=null) {
			String [] split = this.token.split("\\.");
			if(split!=null && split.length>1) {
				return split[1];
			}
		}
		return null;
	}
	public String getDecodedPayload() {
		String payload = this.getPayload();
		if(payload!=null) {
			return new String(Base64Utilities.decode(payload));
		}
		return null;
	}
	public Map<String, String> getPayloadClaims() throws UtilsException {
		Map<String, String> m = null;
		if(this.token!=null) {
			if(this.readClaimsPayload==null) {
				this.initReadClaimsPayload();
			}
			return this.readClaimsPayload;
		}
		return m;
	}
	public String getPayloadClaim(String claim) throws UtilsException {
		if(this.token!=null) {
			if(this.readClaimsPayload==null) {
				this.initReadClaimsPayload();
			}
			if(this.readClaimsPayload!=null) {
				return this.readClaimsPayload.get(claim);
			}
		}
		return null;
	}
	
	
	// -- Utilities
	
	private Map<String, String> readClaimsHeader = null;
	private synchronized void initReadClaimsHeader() throws UtilsException {
		if(this.readClaimsHeader==null) {
			this.readClaimsHeader = new HashMap<>();
			String hdr = getDecodedHeader();
			JSONUtils jsonUtils = JSONUtils.getInstance();
			if(jsonUtils.isJson(hdr)) {
				JsonNode root = jsonUtils.getAsNode(hdr);
				Map<String, Serializable> readClaims = jsonUtils.convertToSimpleMap(root);
				initReadClaimsHeader(readClaims);
			}
		}
	}
	private void initReadClaimsHeader(Map<String, Serializable> readClaims) {
		if(readClaims!=null && readClaims.size()>0) {
			for (Map.Entry<String,Serializable> entry: readClaims.entrySet()) {
				String claim = entry.getKey();
				Serializable o = readClaims.get(claim);
				putClaimHeader(claim, o);
			}
		}
	}
	private void putClaimHeader(String claim, Serializable o) {
		if(o!=null) {
			List<String> lClaimValues = getClaimValues(o);
			if(lClaimValues!=null && !lClaimValues.isEmpty()) {
				String v = getClaimValuesAsString(lClaimValues);
				if(v!=null) {
					this.readClaimsHeader.put(claim, v);
				}
			}
		}
	}
	
	private Map<String, String> readClaimsPayload = null;
	private synchronized void initReadClaimsPayload() throws UtilsException {
		if(this.readClaimsPayload==null) {
			this.readClaimsPayload = new HashMap<>();
			String hdr = getDecodedPayload();
			JSONUtils jsonUtils = JSONUtils.getInstance();
			if(jsonUtils.isJson(hdr)) {
				JsonNode root = jsonUtils.getAsNode(hdr);
				Map<String, Serializable> readClaims = jsonUtils.convertToSimpleMap(root);
				initReadClaimsPayload(readClaims);
			}
		}
	}
	private void initReadClaimsPayload(Map<String, Serializable> readClaims) {
		if(readClaims!=null && readClaims.size()>0) {
			for (Map.Entry<String,Serializable> entry: readClaims.entrySet()) {
				String claim = entry.getKey();
				Serializable o = readClaims.get(claim);
				putClaimPayload(claim, o);
			}
		}
	}
	private void putClaimPayload(String claim, Serializable o) {
		if(o!=null) {
			List<String> lClaimValues = getClaimValues(o);
			if(lClaimValues!=null && !lClaimValues.isEmpty()) {
				String v = getClaimValuesAsString(lClaimValues);
				if(v!=null) {
					this.readClaimsPayload.put(claim, v);
				}
			}
		}
	}

	public static List<String> getClaimValues(Object value) {
		List<String> lreturn = null;
		if(value!=null) {
			if(value instanceof List<?>) {
				List<?> l = (List<?>) value;
				List<String> lString = convertTo(l);
				if(lString!=null) {
					return lString;
				}
			}
			else {
				String sValue = value.toString();
				List<String> l = new ArrayList<>();
				l.add(sValue);
				return l;
			}
		}
		return lreturn;
	}
	private static List<String> convertTo(List<?> l){
		List<String> lreturn = null;
		if(!l.isEmpty()) {
			List<String> lString = new ArrayList<>();
			for (Object o : l) {
				if(o!=null) {
					lString.add(o.toString());
				}
			}
			if(!lString.isEmpty()) {
				return lString;
			}
		}
		return lreturn;
	}
	private static String getClaimValuesAsString(List<String> claimValues) {
		String claimValue = null;
		if(claimValues==null || claimValues.isEmpty()) {
			return null;
		}
		if(claimValues.size()>1) {
			StringBuilder sb = new StringBuilder();
			for (String c : claimValues) {
				if(sb.length()>0) {
					sb.append(",");
				}
				sb.append(c);
			}
			claimValue = sb.toString();
		}
		else {
			claimValue = claimValues.get(0);
		}
		return claimValue;
	}
	
}
