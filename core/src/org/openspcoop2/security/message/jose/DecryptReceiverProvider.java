/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.rs.security.jose.common.JoseConstants;
import org.apache.cxf.rt.security.rs.RSSecurityConstants;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;

/**     
 * DecryptReceiverProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DecryptReceiverProvider extends KeyStoreWithSecretKeySecurityProvider {

	public DecryptReceiverProvider() {
		super();
	}

	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		super.validate(mapProperties);
		
		Properties defaultP = MultiPropertiesUtilities.getDefaultProperties(mapProperties);
		
		Properties p = mapProperties.get("decryptionPropRefId");
		if(p!=null && p.size()>0) {
			if(!p.containsKey(RSSecurityConstants.RSSEC_KEY_STORE) && !p.containsKey(JoseConstants.RSSEC_KEY_STORE_JWKSET)) {
				// altrimenti è stato fatto inject del keystore
				String file = p.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE);
				if(file!=null && StringUtils.isNotEmpty(file)) {
					InputValidationUtils.validateTextAreaInput(file, "Decryption - KeyStore - File");
				}
			}
		}
		
		String file = defaultP.getProperty(RSSecurityConstants.RSSEC_KEY_STORE_FILE+".ssl");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Configurazione HTTPS (jku/x5u) - TrustStore - File");
		}
		
		file = defaultP.getProperty(RSSecurityConstants.RSSEC_KEY_STORE+".ssl.crl");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Configurazione HTTPS (jku/x5u) - TrustStore - CRL File(s)");
		}
		
		file = defaultP.getProperty("joseUseHeaders.keystore.file");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			String fieldName = "Certificati X.509 (x5c/x5u) - KeyStore - File";
			if (MultiPropertiesUtilities.isEnabled(defaultP, "joseUseHeaders.jwk") ||
					MultiPropertiesUtilities.isEnabled(defaultP, "joseUseHeaders.jku")) {
				fieldName = "Certificati JWK (jwk/jku) - KeyStore - File"; 
			}
			InputValidationUtils.validateTextAreaInput(file, fieldName);
		}
		
		file = defaultP.getProperty("joseUseHeaders.keystore.file");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Certificati JWK (jwk/jku) - KeyStore - File");
		}
		
		file = defaultP.getProperty("joseUseHeaders.truststore.file");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Validazione Certificati X.509 (x5c/x5u) - TrustStore - File");
		}
		
		file = defaultP.getProperty("joseUseHeaders.truststore.crl");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Validazione Certificati X.509 (x5c/x5u) - TrustStore - CRL File(s)");
		}
		
		
	}
	
}
