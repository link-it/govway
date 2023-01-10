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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.utils.AbstractSecurityProvider;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWTOptions;


/**     
 * SecurityProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityProvider extends AbstractSecurityProvider {

	private boolean addSecretKey = false;
	public void addSecretKey() {
		this.addSecretKey = true;
	}

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {

		JWTOptions jwtOptions = new JWTOptions(JOSESerialization.COMPACT); // una qualsaisi per la validazione
		boolean useHeaders = JOSEUtils.useJwtHeaders_mapProperties(mapProperties, jwtOptions);
		if(useHeaders) {
			if(!jwtOptions.isPermitUseHeaderJKU() &&
					!jwtOptions.isPermitUseHeaderJWK() &&
					!jwtOptions.isPermitUseHeaderX5C() &&
					!jwtOptions.isPermitUseHeaderX5U() &&
					!jwtOptions.isPermitUseHeaderX5T() &&
					!jwtOptions.isPermitUseHeaderX5T_256() &&
					!jwtOptions.isPermitUseHeaderKID()
					){
						throw new ProviderValidationException("Selezionare almeno un header");
					}
		}
		
		super.validate(mapProperties);
	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(JOSECostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(SecurityConstants.KEYSTORE_TYPE.equals(id) || 
				SecurityConstants.SECRETKEYSTORE_TYPE.equals(id) ||
				SecurityConstants.TRUSTSTORE_TYPE.equals(id)) {
			List<String> l = super.getValues(id);
			l.add(0, SecurityConstants.KEYSTORE_TYPE_JWK_VALUE);
			if(SecurityConstants.TRUSTSTORE_TYPE.equals(id) ||
					(SecurityConstants.KEYSTORE_TYPE.equals(id) && (this.isAsTruststore() || this.addSecretKey))) {
				// aggiunto info mancanti come secret
				List<String> lSecret = super.getValues(SecurityConstants.SECRETKEYSTORE_TYPE);
				if(lSecret!=null && !lSecret.isEmpty()) {
					for (String type : lSecret) {
						if(!l.contains(type)) {
							l.add(type);
						}
					}
				}
			}
			return l;
		}
		else {
			return super.getValues(id);
		}
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(JOSECostanti.ID_SIGNATURE_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			List<String> l = this.getValues(id);
			List<String> labels = new ArrayList<>();
			for (String value : l) {
				if(value.contains("_")) {
					String t = new String(value);
					while(t.contains("_")) {
						t = t.replace("_", "-");
					}
					labels.add(t);
				}
				else {
					labels.add(value);
				}
			}
			return labels;
		}
		else if(SecurityConstants.KEYSTORE_TYPE.equals(id) || 
				SecurityConstants.SECRETKEYSTORE_TYPE.equals(id) ||
				SecurityConstants.TRUSTSTORE_TYPE.equals(id)) {
			List<String> l = super.getLabels(id);
			l.add(0, SecurityConstants.KEYSTORE_TYPE_JWK_LABEL);
			if(SecurityConstants.TRUSTSTORE_TYPE.equals(id) ||
					(SecurityConstants.KEYSTORE_TYPE.equals(id) && (this.isAsTruststore() || this.addSecretKey))) {
				// aggiunto info mancanti come secret
				List<String> lSecret = super.getLabels(SecurityConstants.SECRETKEYSTORE_TYPE);
				if(lSecret!=null && !lSecret.isEmpty()) {
					for (String type : lSecret) {
						if(!l.contains(type)) {
							l.add(type);
						}
					}
				}
			}
			return l;
		}
		else {
			return super.getLabels(id);
		}
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if(JOSECostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.RS256.name();
		}
		else if(JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.KeyAlgorithm.RSA_OAEP_256.name();
		}
		else if(JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
			return org.apache.cxf.rs.security.jose.jwa.ContentAlgorithm.A256GCM.name();
		}
		else {
			return super.getDefault(id);
		}
	}

}
