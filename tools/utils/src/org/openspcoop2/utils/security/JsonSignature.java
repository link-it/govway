/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.security;

import java.util.Iterator;
import java.util.Properties;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsCompactProducer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJsonProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * JsonSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonSignature {

	private JwsSignatureProvider provider;
	private JOSERepresentation representation;
	private JwsHeaders headers;
	private JwtHeaders jwtHeaders;
	
	public JsonSignature(Properties props, JOSERepresentation representation) throws UtilsException{
		this(props, null, representation);
	}
	public JsonSignature(Properties props, JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			this.headers = new JwsHeaders(props);
			this.provider = JwsUtils.loadSignatureProvider(JsonUtils.newMessage(), props, this.headers);
			this.representation=representation;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	public JsonSignature(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, signatureAlgorithm, 
				null, representation);
	}
	public JsonSignature(KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(keystore, alias, passwordPrivateKey, signatureAlgorithm, 
				null, representation);
	}
	public JsonSignature(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, signatureAlgorithm, jwtHeaders, representation);
	}
	public JsonSignature(KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPrivateKeySignatureProvider(keystore.getPrivateKey(alias, passwordPrivateKey), algo);
			this.representation=representation;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}
	
	public JsonSignature(JsonWebKeys jsonWebKeys, String alias, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(jsonWebKeys, alias, signatureAlgorithm, 
				null, representation);
	}
	public JsonSignature(JsonWebKeys jsonWebKeys, String alias, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias),signatureAlgorithm,jwtHeaders,representation);
	}
	
	public JsonSignature(JsonWebKey jsonWebKey, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(jsonWebKey, signatureAlgorithm, 
				null, representation);
	}
	public JsonSignature(JsonWebKey jsonWebKey, String signatureAlgorithm, 
			JwtHeaders jwtHeaders, JOSERepresentation representation) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPrivateKeySignatureProvider(JwkUtils.toRSAPrivateKey(jsonWebKey), algo);
			this.representation=representation;
			this.jwtHeaders = jwtHeaders;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}


	public String sign(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: return signSelfContained(jsonString);
				case COMPACT: return signCompact(jsonString);
				case DETACHED:  return signDetached(jsonString);
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.representation, JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	private String signDetached(String jsonString) {
		boolean detached = true;
		JwsJsonProducer jwsProducer = new JwsJsonProducer(jsonString, false, detached);
		jwsProducer.signWith(this.provider);
		return jwsProducer.getJwsJsonSignedDocument();
	}

	private String signCompact(String jsonString) throws Exception {
		JwsCompactProducer jwsProducer = new JwsCompactProducer(new JwsHeaders(), jsonString);
		fillJwtHeaders(jwsProducer.getJwsHeaders(), this.provider.getAlgorithm());
		return jwsProducer.signWith(this.provider);
	}


	private String signSelfContained(String jsonString) {
		JwsJsonProducer jwsProducer = new JwsJsonProducer(jsonString);
		return jwsProducer.signWith(this.provider);
	}

	
	private void fillJwtHeaders(JwsHeaders headers,
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm signatureAlgo) throws Exception {
		if(this.headers!=null) {
			if(this.headers.asMap()!=null && !this.headers.asMap().isEmpty()) {
				Iterator<String> itKeys = this.headers.asMap().keySet().iterator();
				while (itKeys.hasNext()) {
					String key = (String) itKeys.next();
					if(!headers.containsHeader(key)) {
						headers.setHeader(key, this.headers.getHeader(key));
					}
				}
			}
		}
		if(this.jwtHeaders!=null) {
			this.jwtHeaders.fillJwsHeaders(headers, false, signatureAlgo.getJwaName());
		}
	}
}
