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

package org.openspcoop2.utils.certificate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkReaderWriter;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**	
 * JWKSet
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWKSet {

	private JwkReaderWriter engineCxf = new JwkReaderWriter();
	private String jwkSetJson;
	private String jwkSetJsonPretty;
	private JsonWebKeys jwkSetCxf;
	private com.nimbusds.jose.jwk.JWKSet jwkSetNimbusds;
	private JsonNode jwkSetNode;
	private List<JWK> jwkSetList = new ArrayList<>();
	
	public JWKSet(String json) {
		this.jwkSetJson = json;
	}
	
	public JWKSet(JsonWebKeys jwk) {
		this.jwkSetCxf = jwk;
	}
	
	public JWKSet(com.nimbusds.jose.jwk.JWKSet jwk) {
		this.jwkSetNimbusds = jwk;
	}
	
	public JWKSet(List<JWK> list) {
		this.jwkSetList = list;
	}
	
	public JWKSet() {

	}
	
	
	public void addJwk(JWK jwk) {
		this.jwkSetList.add(jwk);
		
		// forzo rebuild
		this.jwkSetJson = null;
		this.jwkSetJsonPretty = null;
		this.jwkSetCxf = null;
		this.jwkSetNimbusds = null;
		this.jwkSetNode = null;
				
	}
	
	private synchronized void initJwks() throws UtilsException {
		if(this.jwkSetList==null || this.jwkSetList.isEmpty()) {
			if(this.getJsonWebKeys()==null && this.getJWKSet()==null){
				throw new UtilsException("JWK Set not defined");
			}
			if(this.jwkSetCxf!=null) {
				initJwksFromSetCxf();
			}
			else {
				initJwksFromSetNimbusds();
			}
		}
	}
	private synchronized void initJwksFromSetCxf() throws UtilsException {
		try {
			Iterator<JsonWebKey> it = this.jwkSetCxf.getKeys().iterator();
			while (it.hasNext()) {
				JsonWebKey jsonWebKey = it.next();
				this.jwkSetList.add(new JWK(jsonWebKey));
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private synchronized void initJwksFromSetNimbusds() throws UtilsException {
		try {
			Iterator<com.nimbusds.jose.jwk.JWK> it = this.jwkSetNimbusds.getKeys().iterator();
			while (it.hasNext()) {
				com.nimbusds.jose.jwk.JWK jwk = it.next();
				this.jwkSetList.add(new JWK(jwk));
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public List<JWK> getJwks() throws UtilsException {
		if(this.jwkSetList==null || this.jwkSetList.isEmpty()) {
			this.initJwks();
		}
		return this.jwkSetList;
	}
	
	
	
	private synchronized void initCxf() throws UtilsException {
		if(this.jwkSetCxf==null) {
			if(this.jwkSetJson==null){
				throw new UtilsException("Json not defined");
			}
			this.jwkSetCxf = this.engineCxf.jsonToJwkSet(this.jwkSetJson);
		}
	}
	public JsonWebKeys getJsonWebKeys() throws UtilsException {
		if(this.jwkSetCxf==null) {
			this.initCxf();
		}
		return this.jwkSetCxf;
	}
	
	private synchronized void initNimbusds() throws UtilsException {
		if(this.jwkSetNimbusds==null) {
			if(this.jwkSetJson==null){
				throw new UtilsException("Json not defined");
			}
			try {
				this.jwkSetNimbusds = com.nimbusds.jose.jwk.JWKSet.parse(this.jwkSetJson);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public com.nimbusds.jose.jwk.JWKSet getJWKSet() throws UtilsException {
		if(this.jwkSetNimbusds==null) {
			this.initNimbusds();
		}
		return this.jwkSetNimbusds;
	}
	
	private synchronized void initJson() throws UtilsException {
		if(this.jwkSetJson==null) {
			if( (this.jwkSetList==null || this.jwkSetList.isEmpty()) && this.jwkSetCxf==null && this.jwkSetNimbusds==null){
				throw new UtilsException("JWK Set not defined");
			}
			if(this.jwkSetList!=null && !this.jwkSetList.isEmpty()) {
				initJsonFromJwkSetList();
			}
			else if(this.jwkSetCxf!=null) {
				initJsonFromJwkSetCxf();
			}
			else {
				initJsonFromJwkSetNimbusds();
			}
		}
	}
	private synchronized void initJsonFromJwkSetList() throws UtilsException {
		List<com.nimbusds.jose.jwk.JWK> list = new ArrayList<>();
		for (JWK jwkOp : this.jwkSetList) {
			list.add(jwkOp.getJWK());
		}
		com.nimbusds.jose.jwk.JWKSet set = new com.nimbusds.jose.jwk.JWKSet(list);
		this.jwkSetJson = set.toString();
	}
	private synchronized void initJsonFromJwkSetCxf() throws UtilsException {
		try {
			this.jwkSetJson = this.engineCxf.jwkSetToJson(this.jwkSetCxf);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private synchronized void initJsonFromJwkSetNimbusds() throws UtilsException {
		try {
			this.jwkSetJson = this.jwkSetNimbusds.toString();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public String getJson() throws UtilsException {
		if(this.jwkSetJson==null) {
			this.initJson();
		}
		return this.jwkSetJson;
	}
	
	private synchronized void initJsonPretty() throws UtilsException {
		if(this.jwkSetJsonPretty==null) {
			try {
				if(this.jwkSetNode==null) {
					initNode();
				}
				this.jwkSetJsonPretty = JSONUtils.getInstance(true).toString(this.jwkSetNode);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public String getJsonPretty() throws UtilsException {
		if(this.jwkSetJsonPretty==null) {
			this.initJsonPretty();
		}
		return this.jwkSetJsonPretty;
	}
	
	private synchronized void initNode() throws UtilsException {
		if(this.jwkSetNode==null) {
			try {
				if(this.jwkSetJson==null) {
					initJson();
				}
				this.jwkSetNode = JSONUtils.getInstance().getAsNode(this.jwkSetJson);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public JsonNode getNode() throws UtilsException {
		if(this.jwkSetNode==null) {
			this.initNode();
		}
		return this.jwkSetNode;
	}

	@Override
	public String toString() {
		try {
			return this.getJsonPretty();
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
}
