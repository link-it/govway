/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.security.message.wss4j;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**     
 * SignatureReceiverProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignatureReceiverProvider extends TrustStoreSecurityProvider {

	public SignatureReceiverProvider() {
		super();
	}

	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		super.validate(mapProperties);
		
		//Properties defaultP = MultiPropertiesUtilities.getDefaultProperties(mapProperties);
		
		Properties p = mapProperties.get("signaturePropRefId");
		if(p!=null && p.size()>0) {
			String file = p.getProperty("org.apache.ws.security.crypto.merlin.file");
			if(file!=null && StringUtils.isNotEmpty(file)) {
				InputValidationUtils.validateTextAreaInput(file, "Signature - TrustStore - Path");
			}
			
			file = p.getProperty("org.apache.ws.security.crypto.merlin.x509crl.file");
			if(file!=null && StringUtils.isNotEmpty(file)) {
				InputValidationUtils.validateTextAreaInput(file, "Signature - TrustStore - CRL File(s)");
			}
		}	
		
		validateAlgoritmiAccettati(mapProperties);
	}
	
	/**
	 * Verifica gli elenchi degli algoritmi accettati in ricezione ('Algoritmi accettati' = 'Elenco'):
	 * URI separati da ',' senza elementi vuoti.
	 */
	static void validateAlgoritmiAccettati(Map<String, Properties> mapProperties) throws ProviderValidationException {
		Properties defaultP = MultiPropertiesUtilities.getDefaultProperties(mapProperties);
		if(defaultP==null) {
			return;
		}
		validateAlgoritmiAccettati(defaultP.getProperty(SecurityConstants.SIGNATURE_ALGORITHM), "Signature - Algoritmi accettati - Signature Algorithm");
		validateAlgoritmiAccettati(defaultP.getProperty(SecurityConstants.SIGNATURE_DIGEST_ALGORITHM), "Signature - Algoritmi accettati - Digest Algorithm");
	}
	private static void validateAlgoritmiAccettati(String value, String fieldName) throws ProviderValidationException {
		if(value==null || StringUtils.isEmpty(value)) {
			return;
		}
		InputValidationUtils.validateTextAreaInput(value, fieldName);
		for (String algoritmo : value.split(WSS4JInInterceptorAlgorithmList.SEPARATORE, -1)) {
			if(StringUtils.isBlank(algoritmo)) {
				throw new ProviderValidationException("Il campo '"+fieldName+"' contiene un algoritmo vuoto");
			}
		}
	}
	
}
