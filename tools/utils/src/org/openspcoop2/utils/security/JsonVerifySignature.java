/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jws.JwsCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJsonConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsJsonProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.openspcoop2.utils.UtilsException;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonVerifySignature {

	private JwsSignatureVerifier provider;
	private JOSERepresentation representation;
	private Properties dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;

	public JsonVerifySignature(Properties props, JOSERepresentation representation) throws UtilsException{
		try {
			if(JOSERepresentation.COMPACT.equals(representation) && JsonUtils.isDynamicProvider(props)) {
				this.dynamicProvider = props;
			}
			else {
				this.provider = loadProviderFromProperties(props);
			}
			this.representation = representation;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}
	
	private JwsSignatureVerifier loadProviderFromProperties(Properties props) throws Exception {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/*java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			return JwsUtils.loadSignatureVerifier(JsonUtils.newMessage(), props, new JwsHeaders());
		}finally {
			try {
				if(fTmp!=null) {
					fTmp.delete();
				}
			}catch(Throwable t) {}
		}
	}
	
	public JsonVerifySignature(java.security.KeyStore keystore, String alias, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), alias, signatureAlgorithm, representation);
	}
	public JsonVerifySignature(KeyStore keystore, String alias, String signatureAlgorithm, JOSERepresentation representation) throws UtilsException{
		try {
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm algo = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.getAlgorithm(signatureAlgorithm);
			this.provider = JwsUtils.getPublicKeySignatureVerifier((X509Certificate) keystore.getCertificate(alias), algo);
			this.representation=representation;
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}


	public boolean verify(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: return verifySelfContained(jsonString);
				case COMPACT: return verifyCompact(jsonString);
				case DETACHED:   throw new UtilsException("Use method verify(String, String) with representation '"+this.representation+"'");
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			t.printStackTrace(System.out);
			throw JsonUtils.convert(this.representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	public boolean verify(String jsonDetachedSignature, String jsonDetachedPayload) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: throw new UtilsException("Use method verify(String) with representation '"+this.representation+"'");
				case COMPACT: throw new UtilsException("UUse method verify(String) with representation '"+this.representation+"'");
				case DETACHED:  return verifyDetached(jsonDetachedSignature, jsonDetachedPayload);
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.representation, JsonUtils.SIGNATURE,JsonUtils.RECEIVER,t);
		}
	}

	private boolean verifyDetached(String jsonDetachedSignature, String jsonDetachedPayload) {
		JwsJsonProducer producer = new JwsJsonProducer(jsonDetachedPayload);
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonDetachedSignature, producer.getUnsignedEncodedPayload());
		return this._verify(consumer);
	}
	private boolean verifySelfContained(String jsonString) {
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonString);
		return this._verify(consumer);
	}	
	private boolean _verify(JwsJsonConsumer consumer) {
		boolean result = consumer.verifySignatureWith(this.provider);
		this.decodedPayload = consumer.getDecodedJwsPayload();
		this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		return result;
	}

	
	private boolean verifyCompact(String jsonString) throws Exception {
		
		JwsSignatureVerifier provider = this.provider;
		if(this.dynamicProvider!=null) {
			String alias = JsonUtils.readAlias(jsonString);
			Properties pNew = new Properties();
			pNew.putAll(this.dynamicProvider);
			//System.out.println("ALIAS ["+alias+"]");
			pNew.put(JoseConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew);
		}
		
		JwsCompactConsumer consumer = new JwsCompactConsumer(jsonString);
		boolean result = consumer.verifySignatureWith(provider);
		this.decodedPayload = consumer.getDecodedJwsPayload();
		this.decodedPayloadAsByte = consumer.getDecodedJwsPayloadBytes();
		return result;
	}
	
	public String getDecodedPayload() {
		return this.decodedPayload;
	}

	public byte[] getDecodedPayloadAsByte() {
		return this.decodedPayloadAsByte;
	}
}
