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

import java.security.PrivateKey;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.jose.jwe.JweDecryptionOutput;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweJsonConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.openspcoop2.utils.UtilsException;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */
public class JsonDecrypt {

	private JweDecryptionProvider provider;
	
	private JOSERepresentation representation;
	
//	private org.apache.cxf.rs.security.jose.jwe.KeyDecryptionProvider keyDecriptionProvider;
//	private org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgorithm;
//	private org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgorithm;
//	private java.security.PrivateKey privateKey;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	public JsonDecrypt(Properties props, JOSERepresentation representation) throws UtilsException{
		try {
			this.provider = JweUtils.loadDecryptionProvider(JsonUtils.newMessage(), props, null, false); // lasciare null come secondo parametro senno non funziona il decrypt senza keyEncoding
			this.representation=representation;
			
//			if(JOSERepresentation.SELF_CONTAINED.equals(representation)) {
//				this.contentAlgorithm = JweUtils.getContentEncryptionAlgorithm(props, org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.A256GCM);
//				this.keyAlgorithm = JweUtils.getKeyEncryptionAlgorithm(props, null);
//				java.security.PrivateKey privateKey = org.apache.cxf.rs.security.jose.common.KeyManagementUtils.loadPrivateKey(null, props, org.apache.cxf.rs.security.jose.jwk.KeyOperation.DECRYPT);
//				if(this.keyAlgorithm==null) {
//					if (privateKey instanceof java.security.interfaces.RSAPrivateKey) {
//						this.keyAlgorithm = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.RSA_OAEP;
//					} else if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
//						this.keyAlgorithm = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.ECDH_ES_A128KW;
//					}
//					else {
//						throw new UtilsException("Unsupported Private Key ["+privateKey+"]");
//					}
//				}
//				this.keyDecriptionProvider = JweUtils.getPrivateKeyDecryptionProvider(privateKey, this.keyAlgorithm);
//			}
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	public JsonDecrypt(java.security.KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), false, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, representation);
	}
	public JsonDecrypt(KeyStore keystore, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(keystore, false, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, representation);
	}
	public JsonDecrypt(java.security.KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(new KeyStore(keystore), secretKey, alias, passwordPrivateKey, keyAlgorithm, contentAlgorithm, representation);
	}
	public JsonDecrypt(KeyStore keystore, boolean secretKey, String alias, String passwordPrivateKey, String keyAlgorithm, String contentAlgorithm, JOSERepresentation representation) throws UtilsException{
		try {
			this.representation=representation;
			
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			if(secretKey) {
				this.provider = JweUtils.createJweDecryptionProvider( (SecretKey) keystore.getSecretKey(alias, passwordPrivateKey), keyAlgo, contentAlgo);
			}else {
				this.provider = JweUtils.createJweDecryptionProvider( (PrivateKey) keystore.getPrivateKey(alias, passwordPrivateKey), keyAlgo, contentAlgo);
			}
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}


	public void decrypt(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: decryptSelfContained(jsonString); break;
				case COMPACT: decryptCompact(jsonString); break;
				default: throw new UtilsException("Unsupported representation '"+this.representation+"'");
			}
		}
		catch(Throwable t) {
			throw JsonUtils.convert(this.representation, JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	

	private void decryptCompact(String jsonString) throws Exception {
		JweDecryptionOutput output =  this.provider.decrypt(jsonString);
		this.decodedPayload = output.getContentText();
		this.decodedPayloadAsByte = output.getContent();
	}


	private void decryptSelfContained(String jsonString) throws Exception {
		
		JweJsonConsumer consumer = new JweJsonConsumer(jsonString);
		
		// con gestione recipients
//		org.apache.cxf.rs.security.jose.jwe.JweJsonEncryptionEntry entry = consumer.getRecipients().get(0);
//		JweDecryptionOutput output = consumer.decryptWith(this.provider, entry);
		
		// senza gestione recipients
		JweDecryptionOutput output = consumer.decryptWith(this.provider);
		
		this.decodedPayload = output.getContentText();
		this.decodedPayloadAsByte = output.getContent();
		
	}

	public String getDecodedPayload() {
		return this.decodedPayload;
	}

	public byte[] getDecodedPayloadAsByte() {
		return this.decodedPayloadAsByte;
	}
}
