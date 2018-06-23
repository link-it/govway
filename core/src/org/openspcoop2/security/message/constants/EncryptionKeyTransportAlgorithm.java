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

package org.openspcoop2.security.message.constants;

/**     
 * EncryptionKeyTransportAlgorithm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum EncryptionKeyTransportAlgorithm {

	// Di fatto è EncryptAsymmetricKeyWrapAlgorithm
	
	RSA_OAEP("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"),
	RSA_OAEP_11("http://www.w3.org/2009/xmlenc11#rsa-oaep"),
	RSA_v1dot5("http://www.w3.org/2001/04/xmlenc#rsa-1_5"),
	DIFFIE_HELLMAN("http://www.w3.org/2001/04/xmlenc#dh");
	
	private String uri;
	EncryptionKeyTransportAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
