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

import java.io.File;
import java.security.PrivateKey;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.apache.commons.lang.NotImplementedException;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionOutput;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweJsonConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonDecrypt {

	private JweDecryptionProvider provider;
	private JOSERepresentation representation;
	private Properties properties;
	private boolean dynamicProvider;
	
	private String decodedPayload;
	private byte[] decodedPayloadAsByte;
	
	public JsonDecrypt(Properties props, JOSERepresentation representation) throws UtilsException{
		try {
			this.dynamicProvider = JsonUtils.isDynamicProvider(props); // rimuove l'alias
			if(JOSERepresentation.COMPACT.equals(representation) && this.dynamicProvider) {
				this.properties = props;
			}
			else {
				this.provider = this.loadProviderFromProperties(props);
			}
			this.representation=representation;		
		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	private JweDecryptionProvider loadProviderFromProperties(Properties props) throws Exception {
		File fTmp = null;
		try {
			fTmp = JsonUtils.normalizeProperties(props); // in caso di url http viene letta la risorsa remota e salvata in tmp
			/*java.util.Enumeration<?> en = props.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				System.out.println("- ["+key+"] ["+props.getProperty(key)+"]");
			}*/
			return JweUtils.loadDecryptionProvider(props, JsonUtils.newMessage(), null); // lasciare null come secondo parametro senno non funziona il decrypt senza keyEncoding
		}finally {
			try {
				if(fTmp!=null) {
					fTmp.delete();
				}
			}catch(Throwable t) {}
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
	
	
	public JsonDecrypt(JsonWebKeys jsonWebKeys, String alias, String keyAlgorithm, String contentAlgorithm, JOSERepresentation representation) throws UtilsException{
		this(JsonUtils.readKey(jsonWebKeys, alias), keyAlgorithm, contentAlgorithm, representation);
	}
	public JsonDecrypt(JsonWebKey jsonWebKey, String keyAlgorithm, String contentAlgorithm, JOSERepresentation representation) throws UtilsException{
		try {
			this.representation=representation;
			
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.getAlgorithm(keyAlgorithm);
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.getAlgorithm(contentAlgorithm);
			this.provider = JweUtils.createJweDecryptionProvider( JwkUtils.toRSAPrivateKey(jsonWebKey), keyAlgo, contentAlgo);

		}catch(Throwable t) {
			throw JsonUtils.convert(representation, JsonUtils.DECRYPT,JsonUtils.RECEIVER,t);
		}
	}
	
	
	// Non implementati: vedi metodo decryptCompact
//	public JsonDecrypt() throws UtilsException{
//		// verra usato l'header per validare ed ottenere il certificato
//		this.representation=JOSERepresentation.COMPACT;
//	}
//	public JsonDecrypt(Properties props) throws UtilsException{
//		// verra usato l'header per validare ed ottenere il certificato
//		this.representation=JOSERepresentation.COMPACT;
//		this.properties = props; // le proprieta' servono per risolvere le url https
//	}
//	
	


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
		
		JweDecryptionProvider provider = this.provider;
		if(this.dynamicProvider) {
			String alias = JsonUtils.readAlias(jsonString);
			Properties pNew = new Properties();
			pNew.putAll(this.properties);
			//System.out.println("ALIAS ["+alias+"]");
			pNew.put(JoseConstants.RSSEC_KEY_STORE_ALIAS, alias);
			provider = loadProviderFromProperties(pNew);
		}

		if(provider==null) {
			JweJsonConsumer consumer = new JweJsonConsumer(jsonString);
			JweHeaders jewHeaders = consumer.getProtectedHeader();
			@SuppressWarnings("unused")
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm keyAlgo  = jewHeaders.getKeyEncryptionAlgorithm();
			@SuppressWarnings("unused")
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm contentAlgo = jewHeaders.getContentEncryptionAlgorithm();
			if(jewHeaders.getX509Chain()!=null && !jewHeaders.getX509Chain().isEmpty()) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "x5c" Header Parameter defined in Section 4.1.6 of [JWS], except
				//   that the X.509 public key certificate or certificate chain [RFC5280]
				//   contains the public key to which the JWE was encrypted; this can be
				//   used to determine the private key needed to decrypt the JWE.
				// TODO: cercare nel keystore la chiave privata associata.
				throw new NotImplementedException();
			}
			else if(jewHeaders.getJsonWebKey()!=null) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "jwk" Header Parameter defined in Section 4.1.3 of [JWS], except
				//   that the key is the public key to which the JWE was encrypted; this
				//   can be used to determine the private key needed to decrypt the JWE.
				// TODO: cercare nel keystore la chiave privata associata.
				throw new NotImplementedException();
			}
			else if(jewHeaders.getX509Url()!=null) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "x5u" Header Parameter defined in Section 4.1.5 of [JWS], except
				//   that the X.509 public key certificate or certificate chain [RFC5280]
				//   contains the public key to which the JWE was encrypted; this can be
				//   used to determine the private key needed to decrypt the JWE.
				// TODO: cercare nel keystore la chiave privata associata.
				throw new NotImplementedException();
			}
			else if(jewHeaders.getJsonWebKeysUrl()!=null) {
				// This parameter has the same meaning, syntax, and processing rules as
				//   the "jku" Header Parameter defined in Section 4.1.2 of [JWS], except
				//   that the JWK Set resource contains the public key to which the JWE
				//   was encrypted; this can be used to determine the private key needed
				//   to decrypt the JWE.
				// TODO: cercare nel keystore la chiave privata associata.
				throw new NotImplementedException();
			}
			else {
				throw new Exception("Non è stato trovato alcun header che consentisse di recuperare il certificato per effettuare la validazione");
			}
		}
		
		JweDecryptionOutput output =  provider.decrypt(jsonString);
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
