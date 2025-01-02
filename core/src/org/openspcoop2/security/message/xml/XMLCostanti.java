/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.security.message.xml;

/**     
 * XMLCostanti
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLCostanti {

	public static final String XML_ENGINE_DESCRIPTION = "XML Engine";
	public static final String XML_ENGINE_SIGNATURE_DESCRIPTION = "XML Signature Engine";
	public static final String XML_ENGINE_VERIFIER_SIGNATURE_DESCRIPTION = "XML SignatureVerifier Engine";
	public static final String XML_ENGINE_ENCRYPT_DESCRIPTION = "XML Encrypt Engine";
	public static final String XML_ENGINE_DECRYPT_DESCRIPTION = "XML Decrypt Engine";
	
	
	// SELECT LIST ID
	
	public static final String ID_SIGNATURE_ALGORITHM = "signatureAlgorithm";
	public static final String ID_SIGNATURE_DIGEST_ALGORITHM = "signatureDigestAlgorithm";
	public static final String ID_SIGNATURE_C14N_ALGORITHM = "signatureC14nAlgorithm";
	public static final String ID_SIGNATURE_C14N_ALGORITHM_EXCLUSIVE = "signatureC14nAlgorithmExclusive";
	public static final String ID_SIGNATURE_C14N_ALGORITHM_INCLUSIVE = "signatureC14nAlgorithmInclusive";
	
	public static final String ID_ENCRYPT_KEY_ALGORITHM = "encryptionKeyAlgorithm";
	public static final String ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM = "encryptionSymAlgorithm";
	public static final String ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM = "encryptionKeyTransportAlgorithm";
	public static final String ID_ENCRYPT_ALGORITHM = "encryptionAlgorithm";
	public static final String ID_ENCRYPT_DIGEST_ALGORITHM = "encryptionDigestAlgorithm";
	public static final String ID_ENCRYPT_C14N_ALGORITHM = "encryptionC14nAlgorithm";
	public static final String ID_ENCRYPT_C14N_ALGORITHM_EXCLUSIVE = "encryptionC14nAlgorithmOnlyExclusive";
	public static final String ID_ENCRYPT_C14N_ALGORITHM_INCLUSIVE = "encryptionC14nAlgorithmInclusive";

}
