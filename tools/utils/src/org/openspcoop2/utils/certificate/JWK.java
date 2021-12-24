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

import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkReaderWriter;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;

/**	
 * JWK
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JWK {

	private JwkReaderWriter engineCxf = new JwkReaderWriter();
	private String jwk_json;
	private String jwk_json_pretty;
	private JsonWebKey jwk_cxf;
	private com.nimbusds.jose.jwk.JWK jwk_nimbusds;
	private JsonNode jwk_node;
	
	public JWK(String json) {
		this.jwk_json = json;
	}
	
	public JWK(JsonWebKey jwk) {
		this.jwk_cxf = jwk;
	}
	
	public JWK(com.nimbusds.jose.jwk.JWK jwk) {
		this.jwk_nimbusds = jwk;
	}
	
	public JWK(KeyStore keystore, String alias) throws UtilsException {
		this(keystore, alias, null, null, true);
	}
	public JWK(KeyStore keystore, String alias, KeyUse use) throws UtilsException {
		this(keystore, alias, null, use, true);
	}
	public JWK(KeyStore keystore, String alias, boolean kid) throws UtilsException {
		this(keystore, alias, null, null, true);
	}
	public JWK(KeyStore keystore, String alias, KeyUse use, boolean kid) throws UtilsException {
		this(keystore, alias, null, use, true);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		this(keystore, alias, passwordPrivateKey, null, true);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey, KeyUse use) throws UtilsException {
		this(keystore, alias, passwordPrivateKey, use, true);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey, boolean kid) throws UtilsException {
		this(keystore, alias, passwordPrivateKey, null, true);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey, KeyUse use, boolean kid) throws UtilsException {
		try {
			if(keystore.existsAlias(alias)==false) {
				throw new Exception("Alias '"+alias+"' undefined");
			}
			PublicKey publicKey = keystore.getPublicKey(alias);
			if(publicKey instanceof java.security.interfaces.RSAPublicKey) {
				PrivateKey privateKey = null;
				if(passwordPrivateKey!=null) {
					privateKey = 
							(PrivateKey) keystore.getPrivateKey(alias, passwordPrivateKey);
				}
				String aliasP = alias;
				if(!kid) {
					aliasP = null;
				}
				_init(publicKey, privateKey, aliasP, use);
			}
			else {
				throw new Exception("Unsupported type '"+publicKey.getClass().getName()+"'");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public JWK(PublicKey publicKey) throws UtilsException {
		this(publicKey, null, null, null);
	}
	public JWK(PublicKey publicKey, String kid) throws UtilsException {
		this(publicKey, null, kid, null);
	}
	public JWK(PublicKey publicKey, KeyUse use) throws UtilsException {
		this(publicKey, null, null, use);
	}
	public JWK(PublicKey publicKey, PrivateKey privateKey) throws UtilsException {
		this(publicKey, privateKey, null, null);
	}
	public JWK(PublicKey publicKey, PrivateKey privateKey, String kid) throws UtilsException {
		this(publicKey, privateKey, kid, null);
	}
	public JWK(PublicKey publicKey, PrivateKey privateKey, KeyUse use) throws UtilsException {
		this(publicKey, privateKey, null, use);
	}
	public JWK(PublicKey publicKey, PrivateKey privateKey, String kid, KeyUse use) throws UtilsException {
		_init(publicKey, privateKey, kid, use);
	}
	private void _init(PublicKey publicKey, PrivateKey privateKey, String kid, KeyUse use) throws UtilsException {
		try {
			if(publicKey instanceof java.security.interfaces.RSAPublicKey) {
				java.security.interfaces.RSAPublicKey p = (java.security.interfaces.RSAPublicKey) publicKey;
				RSAKey.Builder builder = new RSAKey.Builder(p);
				if(privateKey!=null) {
					builder.privateKey(privateKey);
				}
				if(kid!=null) {
					builder.keyID(kid);
				}
				if(use!=null) {
					builder.keyUse(use);
				}
				this.jwk_nimbusds = builder.build();
			}
			else {
				throw new Exception("Unsupported type '"+publicKey.getClass().getName()+"'");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public JWK(javax.crypto.SecretKey secretKey) throws UtilsException {
		this(secretKey, null, null);
	}
	public JWK(javax.crypto.SecretKey secretKey, String kid) throws UtilsException {
		this(secretKey, kid, null);
	}
	public JWK(javax.crypto.SecretKey secretKey, KeyUse use) throws UtilsException {
		this(secretKey, null, use);
	}	
	public JWK(javax.crypto.SecretKey secretKey, String kid, KeyUse use) throws UtilsException {
		try {
			OctetSequenceKey.Builder builder = new OctetSequenceKey.Builder(secretKey);
			if(kid!=null) {
				builder.keyID(kid);
			}
			if(use!=null) {
				builder.keyUse(use);
			}
			this.jwk_nimbusds = builder.build();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private synchronized void initCxf() throws UtilsException {
		if(this.jwk_cxf==null) {
			if(this.jwk_json==null){
				throw new UtilsException("Json not defined");
			}
			this.jwk_cxf = this.engineCxf.jsonToJwk(this.jwk_json);
		}
	}
	public JsonWebKey getJsonWebKey() throws UtilsException {
		if(this.jwk_cxf==null) {
			this.initCxf();
		}
		return this.jwk_cxf;
	}
	
	private synchronized void initNimbusds() throws UtilsException {
		if(this.jwk_nimbusds==null) {
			if(this.jwk_json==null){
				throw new UtilsException("Json not defined");
			}
			try {
				this.jwk_nimbusds = RSAKey.parse(this.jwk_json);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public com.nimbusds.jose.jwk.JWK getJWK() throws UtilsException {
		if(this.jwk_nimbusds==null) {
			this.initNimbusds();
		}
		return this.jwk_nimbusds;
	}
	
	private synchronized void initJson() throws UtilsException {
		if(this.jwk_json==null) {
			if(this.jwk_cxf==null && this.jwk_nimbusds==null){
				throw new UtilsException("JWK not defined");
			}
			if(this.jwk_cxf!=null) {
				try {
					this.jwk_json = this.engineCxf.jwkToJson(this.jwk_cxf);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			else {
				try {
					this.jwk_json = this.jwk_nimbusds.toJSONString();
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
	}
	public String getJson() throws UtilsException {
		if(this.jwk_json==null) {
			this.initJson();
		}
		return this.jwk_json;
	}
	
	private synchronized void initJsonPretty() throws UtilsException {
		if(this.jwk_json_pretty==null) {
			try {
				if(this.jwk_node==null) {
					initNode();
				}
				this.jwk_json_pretty = JSONUtils.getInstance(true).toString(this.jwk_node);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public String getJsonPretty() throws UtilsException {
		if(this.jwk_json_pretty==null) {
			this.initJsonPretty();
		}
		return this.jwk_json_pretty;
	}
	
	private synchronized void initNode() throws UtilsException {
		if(this.jwk_node==null) {
			try {
				if(this.jwk_json==null) {
					initJson();
				}
				this.jwk_node = JSONUtils.getInstance().getAsNode(this.jwk_json);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public JsonNode getNode() throws UtilsException {
		if(this.jwk_node==null) {
			this.initNode();
		}
		return this.jwk_node;
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
