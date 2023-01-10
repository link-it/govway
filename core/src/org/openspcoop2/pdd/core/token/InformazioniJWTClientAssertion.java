/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * InformazioniClientAssertionJWT
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniJWTClientAssertion extends org.openspcoop2.utils.beans.BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniJWTClientAssertion() {} // per serializzatore
	public InformazioniJWTClientAssertion(Logger log, String base64, boolean infoNormalizzate) {
		this.token = base64;
		
		if(infoNormalizzate) {
			String [] split = this.token.split("\\.");
			if(split!=null && split.length>0) {
				if(split[0]!=null) {
					String hdrBase64 = split[0]; 
					try {
						this.jsonHeader = new String(Base64Utilities.decode(hdrBase64));
					}catch(Throwable t) {
						log.error("Decode header failed (hdr: "+hdrBase64+" assertion:"+base64+"): "+t.getMessage(),t);
					}
					if(this.jsonHeader!=null) {
						try {
							JSONUtils jsonUtils = JSONUtils.getInstance();
							if(jsonUtils.isJson(this.jsonHeader)) {
								JsonNode root = jsonUtils.getAsNode(this.jsonHeader);
								Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
								if(readClaims!=null && readClaims.size()>0) {
									this.header = new HashMap<String,Object>();
									this.header.putAll(readClaims);
								}
							}	
						}catch(Throwable t) {
							log.error("Decode header failed (json: "+this.jsonHeader+"): "+t.getMessage(),t);
						}
					}
				}
				if(split.length>1 && split[1]!=null) {
					String payloadBase64 = split[1]; 
					try {
						this.jsonPayload = new String(Base64Utilities.decode(payloadBase64));
					}catch(Throwable t) {
						log.error("Decode payload failed (payload: "+payloadBase64+" assertion:"+base64+"): "+t.getMessage(),t);
					}
					if(this.jsonPayload!=null) {
						try {
							JSONUtils jsonUtils = JSONUtils.getInstance();
							if(jsonUtils.isJson(this.jsonPayload)) {
								JsonNode root = jsonUtils.getAsNode(this.jsonPayload);
								Map<String, Object> readClaims = jsonUtils.convertToSimpleMap(root);
								if(readClaims!=null && readClaims.size()>0) {
									this.payload = new HashMap<String,Object>();
									this.payload.putAll(readClaims);
								}
							}	
						}catch(Throwable t) {
							log.error("Decode payload failed (json: "+this.jsonPayload+"): "+t.getMessage(),t);
						}
					}
				}
			}
		}
	} 
	
	// NOTA: l'ordine stabilisce come viene serializzato nell'oggetto json
	
	// RawResponse
	private String token;
	// Claims
	private Map<String,Object> header = null;
	private Map<String,Object> payload = null;
	private String jsonHeader;
	private String jsonPayload;

	public Map<String, Object> getHeader() {
		return this.header;
	}
	public void setHeader(Map<String, Object> header) {
		this.header = header;
	}
	public Map<String, Object> getPayload() {
		return this.payload;
	}
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}

	public String getToken() {
		return this.token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getJsonHeader() {
		return this.jsonHeader;
	}
	public void setJsonHeader(String jsonHeader) {
		this.jsonHeader = jsonHeader;
	}
	public String getJsonPayload() {
		return this.jsonPayload;
	}
	public void setJsonPayload(String jsonPayload) {
		this.jsonPayload = jsonPayload;
	}

}
