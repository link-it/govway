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

package org.openspcoop2.utils.certificate;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkReaderWriter;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.Algorithm;
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
	private String jwkJson;
	private String jwkJsonPretty;
	private JsonWebKey jwkCxf;
	private com.nimbusds.jose.jwk.JWK jwkNimbusds;
	private JsonNode jwkNode;
	
	public JWK(String json) {
		this.jwkJson = json;
	}
	
	public JWK(JsonWebKey jwk) {
		this.jwkCxf = jwk;
	}
	
	public JWK(com.nimbusds.jose.jwk.JWK jwk) {
		this.jwkNimbusds = jwk;
	}
	
	public JWK(KeyStore keystore, String alias) throws UtilsException {
		this(keystore, alias, null, null, true);
	}
	public JWK(KeyStore keystore, String alias, KeyUse use) throws UtilsException {
		this(keystore, alias, null, use, true);
	}
	public JWK(KeyStore keystore, String alias, boolean kid) throws UtilsException {
		this(keystore, alias, null, null, kid);
	}
	public JWK(KeyStore keystore, String alias, KeyUse use, boolean kid) throws UtilsException {
		this(keystore, alias, null, use, kid);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException {
		this(keystore, alias, passwordPrivateKey, null, true);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey, KeyUse use) throws UtilsException {
		this(keystore, alias, passwordPrivateKey, use, true);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey, boolean kid) throws UtilsException {
		this(keystore, alias, passwordPrivateKey, null, kid);
	}
	public JWK(KeyStore keystore, String alias, String passwordPrivateKey, KeyUse use, boolean kid) throws UtilsException {
		try {
			if(!keystore.existsAlias(alias)) {
				throw new UtilsException("Alias '"+alias+"' undefined");
			}
			PublicKey publicKey = keystore.getPublicKey(alias);
			if(publicKey instanceof java.security.interfaces.RSAPublicKey) {
				PrivateKey privateKey = null;
				if(passwordPrivateKey!=null) {
					privateKey = keystore.getPrivateKey(alias, passwordPrivateKey);
				}
				String aliasP = alias;
				if(!kid) {
					aliasP = null;
				}
				initEngine(publicKey, privateKey, aliasP, use);
			}
			else {
				throw new UtilsException("Unsupported type '"+publicKey.getClass().getName()+"'");
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
	public JWK(PublicKey publicKey, String kid, KeyUse use) throws UtilsException {
		this(publicKey, null, kid, use);
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
		initEngine(publicKey, privateKey, kid, use);
	}
	private void initEngine(PublicKey publicKey, PrivateKey privateKey, String kid, KeyUse use) throws UtilsException {
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
				this.jwkNimbusds = builder.build();
			}
			else {
				if(publicKey==null) {
					throw new UtilsException("PublicKey undefined");
				}
				else {
					throw new UtilsException("Unsupported type '"+publicKey.getClass().getName()+"'");
				}
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public JWK(javax.crypto.SecretKey secretKey) throws UtilsException {
		this(secretKey, null, null, null);
	}
	public JWK(javax.crypto.SecretKey secretKey, String kid) throws UtilsException {
		this(secretKey, kid, null, null);
	}
	public JWK(javax.crypto.SecretKey secretKey, String kid, String algorithm) throws UtilsException {
		this(secretKey, kid, null, algorithm);
	}
	public JWK(javax.crypto.SecretKey secretKey, KeyUse use) throws UtilsException {
		this(secretKey, null, use, null);
	}
	public JWK(javax.crypto.SecretKey secretKey, KeyUse use, String algorithm) throws UtilsException {
		this(secretKey, null, use, algorithm);
	}
	public JWK(javax.crypto.SecretKey secretKey, String kid, KeyUse use) throws UtilsException {
		this(secretKey, kid, use, null);
	}
	public JWK(javax.crypto.SecretKey secretKey, String kid, KeyUse use, String algorithm) throws UtilsException {
		try {
			OctetSequenceKey.Builder builder = new OctetSequenceKey.Builder(secretKey);
			if(algorithm!=null) {
				builder = builder.algorithm(Algorithm.parse(algorithm));
			}
			if(kid!=null) {
				builder = builder.keyID(kid);
			}
			if(use!=null) {
				builder = builder.keyUse(use);
			}
			this.jwkNimbusds = builder.build();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	private synchronized void initCxf() throws UtilsException {
		if(this.jwkCxf==null) {
			if(this.jwkJson==null){
				throw new UtilsException("Json not defined");
			}
			this.jwkCxf = this.engineCxf.jsonToJwk(this.jwkJson);
		}
	}
	public JsonWebKey getJsonWebKey() throws UtilsException {
		if(this.jwkCxf==null) {
			this.initCxf();
		}
		return this.jwkCxf;
	}
	
	private synchronized void initNimbusds() throws UtilsException {
		if(this.jwkNimbusds==null) {
			if(this.jwkJson==null){
				throw new UtilsException("Json not defined");
			}
			try {
				this.jwkNimbusds = RSAKey.parse(this.jwkJson);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public com.nimbusds.jose.jwk.JWK getJWK() throws UtilsException {
		if(this.jwkNimbusds==null) {
			this.initNimbusds();
		}
		return this.jwkNimbusds;
	}
	
	private synchronized void initJson() throws UtilsException {
		if(this.jwkJson==null) {
			if(this.jwkCxf==null && this.jwkNimbusds==null){
				throw new UtilsException("JWK not defined");
			}
			if(this.jwkCxf!=null) {
				try {
					this.jwkJson = this.engineCxf.jwkToJson(this.jwkCxf);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			else {
				try {
					this.jwkJson = this.jwkNimbusds.toJSONString();
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
	}
	public String getJson() throws UtilsException {
		if(this.jwkJson==null) {
			this.initJson();
		}
		return this.jwkJson;
	}
	
	private synchronized void initJsonPretty() throws UtilsException {
		if(this.jwkJsonPretty==null) {
			try {
				if(this.jwkNode==null) {
					initNode();
				}
				this.jwkJsonPretty = JSONUtils.getInstance(true).toString(this.jwkNode);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public String getJsonPretty() throws UtilsException {
		if(this.jwkJsonPretty==null) {
			this.initJsonPretty();
		}
		return this.jwkJsonPretty;
	}
	
	private synchronized void initNode() throws UtilsException {
		if(this.jwkNode==null) {
			try {
				if(this.jwkJson==null) {
					initJson();
				}
				this.jwkNode = JSONUtils.getInstance().getAsNode(this.jwkJson);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public JsonNode getNode() throws UtilsException {
		if(this.jwkNode==null) {
			this.initNode();
		}
		return this.jwkNode;
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
