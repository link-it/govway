/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Properties;

import org.apache.cxf.rs.security.jose.jws.JwsCompactProducer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJsonProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.openspcoop2.utils.UtilsException;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */
public class JsonSignature {

	private JwsSignatureProvider provider;
	private JOSERepresentation representation;
	
	public JsonSignature(Properties props, JOSERepresentation representation) throws UtilsException{
		try {
			this.provider = JwsUtils.loadSignatureProvider(JsonUtils.newMessage(), props, new JwsHeaders(props), false);
			this.representation=representation;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.SENDER,t);
		}
	}

	public JsonSignature(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, passwordPrivateKey, signatureAlgorithm, representation);
	}
	public JsonSignature(KeyStore keystore, String alias, String passwordPrivateKey, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPrivateKeySignatureProvider(keystore.getPrivateKey(alias, passwordPrivateKey), algo);
			this.representation=representation;
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
		JwsJsonProducer jwsProducer = new JwsJsonProducer(jsonString);
		jwsProducer.signWith(this.provider);
		return jwsProducer.getJwsJsonSignedDocument(true);
	}

	private String signCompact(String jsonString) {
		JwsCompactProducer jwsProducer = new JwsCompactProducer(new JwsHeaders(), jsonString);
		return jwsProducer.signWith(this.provider);
	}


	private String signSelfContained(String jsonString) {
		JwsJsonProducer jwsProducer = new JwsJsonProducer(jsonString);
		return jwsProducer.signWith(this.provider);
	}

}
