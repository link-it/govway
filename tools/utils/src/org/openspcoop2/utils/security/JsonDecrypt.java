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

import org.apache.cxf.rs.security.jose.jwe.JweDecryptionOutput;
import org.apache.cxf.rs.security.jose.jwe.JweDecryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweJsonConsumer;
import org.apache.cxf.rs.security.jose.jwe.JweJsonEncryptionEntry;
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
			this.provider = JweUtils.loadDecryptionProvider(props, null, false); // lasciare null come secondo parametro senno non funziona il decrypt senza keyEncoding
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
			throw new UtilsException(t.getMessage(),t);
		}
	}


	public void decrypt(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: decryptSelfContained(jsonString); break;
				case COMPACT: decryptCompact(jsonString); break;
				default: throw new UtilsException("Unsupported representation ["+this.representation+"]");
			}
		}
		catch(UtilsException t) {
			throw t;
		}
		catch(Throwable t) {
			throw new UtilsException("Error occurs during decrypt (representation "+this.representation+"): "+t.getMessage(),t);
		}
	}
	

	private void decryptCompact(String jsonString) throws Exception {
		JweDecryptionOutput output =  this.provider.decrypt(jsonString);
		this.decodedPayload = output.getContentText();
		this.decodedPayloadAsByte = output.getContent();
	}


	private void decryptSelfContained(String jsonString) throws Exception {
		
		JweJsonConsumer consumer = new JweJsonConsumer(jsonString);
		
		// nuovo
		JweJsonEncryptionEntry entry = consumer.getRecipients().get(0);
		System.out.println("ENTRY: "+entry);
		JweDecryptionOutput output = consumer.decryptWith(this.provider, entry);
		// nuovo
		
		// vecchio
		//JweDecryptionOutput output = consumer.decryptWith(this.provider);
		
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
