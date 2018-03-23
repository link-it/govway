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

import org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm;
import org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm;
import org.apache.cxf.rs.security.jose.jwe.JweEncryptionProvider;
import org.apache.cxf.rs.security.jose.jwe.JweHeaders;
import org.apache.cxf.rs.security.jose.jwe.JweJsonProducer;
import org.apache.cxf.rs.security.jose.jwe.JweUtils;
import org.openspcoop2.utils.UtilsException;

/**	
 * Encrypt
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */
public class JsonEncrypt {

	private JweEncryptionProvider provider;
	private JOSERepresentation representation;
	
	private ContentAlgorithm contentAlgorithm;
	private KeyAlgorithm keyAlgorithm;
	
	public JsonEncrypt(Properties props, JOSERepresentation representation) throws UtilsException{
		try {
			this.provider = JweUtils.loadEncryptionProvider(props, new JweHeaders(), false);
			this.representation=representation;
			
			if(JOSERepresentation.SELF_CONTAINED.equals(representation)) {
				this.contentAlgorithm = JweUtils.getContentEncryptionAlgorithm(props, ContentAlgorithm.A256GCM);
				this.keyAlgorithm = JweUtils.getKeyEncryptionAlgorithm(props, null);
				if(this.keyAlgorithm==null) {
					throw new Exception("KeyAlgorithm undefined");
				}
			}
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
	}


	public String encrypt(String jsonString) throws UtilsException{
		try {
			switch(this.representation) {
				case SELF_CONTAINED: return encryptSelfContained(jsonString);
				case COMPACT: return encryptCompact(jsonString);
				default: throw new UtilsException("Unsupported representation ["+this.representation+"]");
			}
		}
		catch(UtilsException t) {
			throw t;
		}
		catch(Throwable t) {
			throw new UtilsException("Error occurs during encrypt (representation "+this.representation+"): "+t.getMessage(),t);
		}
	}


	private String encryptCompact(String jsonString) {
		return this.provider.encrypt(jsonString.getBytes(), new JweHeaders());
	}


	private String encryptSelfContained(String jsonString) {
		
		JweHeaders sharedUnprotectedHeaders = new JweHeaders();
		sharedUnprotectedHeaders.setKeyEncryptionAlgorithm(this.keyAlgorithm);

		JweHeaders protectedHeaders = new JweHeaders(this.contentAlgorithm);
		
		JweJsonProducer producer = new JweJsonProducer(protectedHeaders, sharedUnprotectedHeaders, jsonString.getBytes());
		return producer.encryptWith(this.provider);
	}

}
