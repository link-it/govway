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
	
	public JsonEncrypt(Properties props, JOSERepresentation representation) throws UtilsException{
		this.provider = JweUtils.loadEncryptionProvider(props, new JweHeaders(), false);
		this.representation=representation;
	}


	public String encrypt(String jsonString) throws UtilsException{

		switch(this.representation) {
		case SELF_CONTAINED: return encryptSelfContained(jsonString);
		case COMPACT: return encryptCompact(jsonString);
		case DETACHED:  return encryptDetached(jsonString);
		default: throw new UtilsException();
		}

	}


	private String encryptDetached(String jsonString) {
		JweJsonProducer producer = new JweJsonProducer(new JweHeaders(), jsonString.getBytes());
		return producer.encryptWith(this.provider);
	}


	private String encryptCompact(String jsonString) {
		JweJsonProducer producer = new JweJsonProducer(new JweHeaders(), jsonString.getBytes());
		return producer.encryptWith(this.provider);
	}


	private String encryptSelfContained(String jsonString) {
		JweJsonProducer producer = new JweJsonProducer(new JweHeaders(), jsonString.getBytes());
		return producer.encryptWith(this.provider);
	}

}
