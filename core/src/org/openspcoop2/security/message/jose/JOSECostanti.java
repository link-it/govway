/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.security.message.jose;

/**     
 * JOSECostanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSECostanti {

	public static final String JOSE_ENGINE_DESCRIPTION = "JOSE Engine";
	public static final String JOSE_ENGINE_SIGNATURE_DESCRIPTION = "JOSE Signature Engine";
	public static final String JOSE_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION = "JOSE SignatureVerifier Engine";
	public static final String JOSE_ENGINE_ENCRYPT_DESCRIPTION = "JOSE Encrypt Engine";
	public static final String JOSE_ENGINE_DECRYPT_DESCRIPTION = "JOSE Decrypt Engine";
	
	// SELECT LIST ID
	
	public final static String ID_SIGNATURE_ALGORITHM = "signatureAlgorithm";
	
	public final static String ID_ENCRYPT_KEY_ALGORITHM = "encryptionKeyAlgorithm";
	
	public final static String ID_ENCRYPT_CONTENT_ALGORITHM = "encryptionContentAlgorithm";

}
