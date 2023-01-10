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

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;

/**     
 * EncryptSenderProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EncryptSenderProvider extends TrustStoreSecurityProvider {

	public EncryptSenderProvider() {
		super();
	}

	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		super.validate(mapProperties);
		
		Properties defaultP = MultiPropertiesUtilities.getDefaultProperties(mapProperties);
		
		Properties p = mapProperties.get("encryptionPropRefId");
		if(p!=null && p.size()>0) {
			if(!p.containsKey("rs.security.keystore") && !p.containsKey("rs.security.keystore.jwkset")) {
				// altrimenti è stato fatto inject del keystore
				String file = p.getProperty("rs.security.keystore.file");
				if(file!=null && StringUtils.isNotEmpty(file)) {
					InputValidationUtils.validateTextAreaInput(file, "Encryption - KeyStore - File");
				}
			}
		}
		
		String file = defaultP.getProperty("joseX509Url");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Encryption - X.509 Certificate - URL");
		}
		
		file = defaultP.getProperty("joseJWKSetUrl");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Encryption - Public Key - URL");
		}
		
		file = defaultP.getProperty("joseCriticalHeaders");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Encryption - Type (typ) - Critical Headers (crit)");
		}
		
	}
	
}
