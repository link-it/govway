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
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100(ven, 26 gen 2018) $
 */
public class JsonVerifySignature {

	private JwsSignatureVerifier provider;
	private JOSERepresentation representation;

	public JsonVerifySignature(Properties props, JOSERepresentation representation) throws UtilsException{
		this.provider = JwsUtils.loadSignatureVerifier(props, new JwsHeaders());
		this.representation = representation;
	}


	public boolean verify(String jsonString) throws UtilsException{
		switch(this.representation) {
		case SELF_CONTAINED: return verifySelfContained(jsonString);
		case COMPACT: return verifyCompact(jsonString);
		case DETACHED:   throw new UtilsException("Usare il metodo verify(String, String)");
		default: throw new UtilsException();
		}
	}

	public boolean verify(String jsonDetachedSignature, String jsonDetachedPayload) throws UtilsException{
		switch(this.representation) {
		case SELF_CONTAINED: throw new UtilsException("Usare il metodo verify(String)");
		case COMPACT: throw new UtilsException("Usare il metodo verify(String)");
		case DETACHED:  return verifyDetached(jsonDetachedSignature, jsonDetachedPayload);
		default: throw new UtilsException();
		}
	}

	private boolean verifyDetached(String jsonDetachedSignature, String jsonDetachedPayload) {
		JwsJsonProducer producer = new JwsJsonProducer(jsonDetachedPayload);
		
		JwsJsonConsumer consumer = new JwsJsonConsumer(jsonDetachedSignature, producer.getUnsignedEncodedPayload());
		return consumer.verifySignatureWith(this.provider);
	}

	private boolean verifyCompact(String jsonString) {
		JwsCompactConsumer producer = new JwsCompactConsumer(jsonString);
		return producer.verifySignatureWith(this.provider);
	}


	private boolean verifySelfContained(String jsonString) {
		JwsJsonConsumer producer = new JwsJsonConsumer(jsonString);
		return producer.verifySignatureWith(this.provider);
	}

}
