/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
	private String jwk_set_json;
	private String jwk_set_json_pretty;
	private JsonWebKeys jwk_set_cxf;
	private com.nimbusds.jose.jwk.JWKSet jwk_set_nimbusds;
	private JsonNode jwk_set_node;
	private List<JWK> jwk_set = new ArrayList<>();
	
	public JWKSet(String json) {
		this.jwk_set_json = json;
	}
	
	public JWKSet(JsonWebKeys jwk) {
		this.jwk_set_cxf = jwk;
	}
	
	public JWKSet(com.nimbusds.jose.jwk.JWKSet jwk) {
		this.jwk_set_nimbusds = jwk;
	}
	
	public JWKSet(List<JWK> list) {
		this.jwk_set = list;
	}
	
	public JWKSet() {

	}
	
	
	public void addJwk(JWK jwk) {
		this.jwk_set.add(jwk);
		
		// forzo rebuild
		this.jwk_set_json = null;
		this.jwk_set_json_pretty = null;
		this.jwk_set_cxf = null;
		this.jwk_set_nimbusds = null;
		this.jwk_set_node = null;
				
	}
	
	private synchronized void initJwks() throws UtilsException {
		if(this.jwk_set==null || this.jwk_set.isEmpty()) {
			if(this.getJsonWebKeys()==null && this.getJWKSet()==null){
				throw new UtilsException("JWK Set not defined");
			}
			if(this.jwk_set_cxf!=null) {
				try {
					Iterator<JsonWebKey> it = this.jwk_set_cxf.getKeys().iterator();
					while (it.hasNext()) {
						JsonWebKey jsonWebKey = (JsonWebKey) it.next();
						this.jwk_set.add(new JWK(jsonWebKey));
					}
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			else {
				try {
					Iterator<com.nimbusds.jose.jwk.JWK> it = this.jwk_set_nimbusds.getKeys().iterator();
					while (it.hasNext()) {
						com.nimbusds.jose.jwk.JWK jwk = (com.nimbusds.jose.jwk.JWK) it.next();
						this.jwk_set.add(new JWK(jwk));
					}
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
	}
	public List<JWK> getJwks() throws UtilsException {
		if(this.jwk_set==null || this.jwk_set.isEmpty()) {
			this.initJwks();
		}
		return this.jwk_set;
	}
	
	
	
	private synchronized void initCxf() throws UtilsException {
		if(this.jwk_set_cxf==null) {
			if(this.jwk_set_json==null){
				throw new UtilsException("Json not defined");
			}
			this.jwk_set_cxf = this.engineCxf.jsonToJwkSet(this.jwk_set_json);
		}
	}
	public JsonWebKeys getJsonWebKeys() throws UtilsException {
		if(this.jwk_set_cxf==null) {
			this.initCxf();
		}
		return this.jwk_set_cxf;
	}
	
	private synchronized void initNimbusds() throws UtilsException {
		if(this.jwk_set_nimbusds==null) {
			if(this.jwk_set_json==null){
				throw new UtilsException("Json not defined");
			}
			try {
				this.jwk_set_nimbusds = com.nimbusds.jose.jwk.JWKSet.parse(this.jwk_set_json);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public com.nimbusds.jose.jwk.JWKSet getJWKSet() throws UtilsException {
		if(this.jwk_set_nimbusds==null) {
			this.initNimbusds();
		}
		return this.jwk_set_nimbusds;
	}
	
	private synchronized void initJson() throws UtilsException {
		if(this.jwk_set_json==null) {
			if( (this.jwk_set==null || this.jwk_set.isEmpty()) && this.jwk_set_cxf==null && this.jwk_set_nimbusds==null){
				throw new UtilsException("JWK Set not defined");
			}
			if(this.jwk_set!=null && !this.jwk_set.isEmpty()) {
				List<com.nimbusds.jose.jwk.JWK> list = new ArrayList<>();
				for (JWK jwkOp : this.jwk_set) {
					list.add(jwkOp.getJWK());
				}
				com.nimbusds.jose.jwk.JWKSet set = new com.nimbusds.jose.jwk.JWKSet(list);
				this.jwk_set_json = set.toString();
			}
			else if(this.jwk_set_cxf!=null) {
				try {
					this.jwk_set_json = this.engineCxf.jwkSetToJson(this.jwk_set_cxf);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			else {
				try {
					this.jwk_set_json = this.jwk_set_nimbusds.toString();
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
	}
	public String getJson() throws UtilsException {
		if(this.jwk_set_json==null) {
			this.initJson();
		}
		return this.jwk_set_json;
	}
	
	private synchronized void initJsonPretty() throws UtilsException {
		if(this.jwk_set_json_pretty==null) {
			try {
				if(this.jwk_set_node==null) {
					initNode();
				}
				this.jwk_set_json_pretty = JSONUtils.getInstance(true).toString(this.jwk_set_node);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public String getJsonPretty() throws UtilsException {
		if(this.jwk_set_json_pretty==null) {
			this.initJsonPretty();
		}
		return this.jwk_set_json_pretty;
	}
	
	private synchronized void initNode() throws UtilsException {
		if(this.jwk_set_node==null) {
			try {
				if(this.jwk_set_json==null) {
					initJson();
				}
				this.jwk_set_node = JSONUtils.getInstance().getAsNode(this.jwk_set_json);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public JsonNode getNode() throws UtilsException {
		if(this.jwk_set_node==null) {
			this.initNode();
		}
		return this.jwk_set_node;
	}

	@Override
	public String toString() {
		try {
			return this.getJsonPretty();
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
}
