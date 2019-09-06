/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.security.message.constants.EncryptionAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionC14NAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionDigestAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionKeyTransportAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionSymmetricKeyWrapAlgorithm;
import org.openspcoop2.security.message.constants.KeyAlgorithm;
import org.openspcoop2.security.message.constants.SignatureAlgorithm;
import org.openspcoop2.security.message.constants.SignatureC14NAlgorithm;
import org.openspcoop2.security.message.constants.SignatureDigestAlgorithm;

/**     
 * SecurityProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {

	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(XMLCostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureAlgorithm [] tmp = SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureDigestAlgorithm [] tmp = SignatureDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureC14NAlgorithm [] tmp = SignatureC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM_EXCLUSIVE.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureC14NAlgorithm [] tmp = SignatureC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				if(tmp[i].name().startsWith("EXCLUSIVE")) {
					l.add(tmp[i].getUri());
				}
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM_INCLUSIVE.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureC14NAlgorithm [] tmp = SignatureC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				if(tmp[i].name().startsWith("INCLUSIVE")) {
					l.add(tmp[i].getUri());
				}
			}
			return l;
		}
		
		else if(XMLCostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			KeyAlgorithm [] tmp = KeyAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionSymmetricKeyWrapAlgorithm [] tmp = EncryptionSymmetricKeyWrapAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionKeyTransportAlgorithm [] tmp = EncryptionKeyTransportAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionAlgorithm [] tmp = EncryptionAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionDigestAlgorithm [] tmp = EncryptionDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionC14NAlgorithm [] tmp = EncryptionC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM_EXCLUSIVE.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionC14NAlgorithm [] tmp = EncryptionC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				if(tmp[i].name().startsWith("EXCLUSIVE")) {
					l.add(tmp[i].getUri());
				}
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM_INCLUSIVE.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionC14NAlgorithm [] tmp = EncryptionC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				if(tmp[i].name().startsWith("INCLUSIVE")) {
					l.add(tmp[i].getUri());
				}
			}
			return l;
		}
		
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(XMLCostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureAlgorithm [] tmp = SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureDigestAlgorithm [] tmp = SignatureDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureC14NAlgorithm [] tmp = SignatureC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getLabel());
			}
			return l;
		}
		
		else if(XMLCostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			return this.getValues(id);
		}
		else if(XMLCostanti.ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionSymmetricKeyWrapAlgorithm [] tmp = EncryptionSymmetricKeyWrapAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionKeyTransportAlgorithm [] tmp = EncryptionKeyTransportAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionAlgorithm [] tmp = EncryptionAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionDigestAlgorithm [] tmp = EncryptionDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionC14NAlgorithm [] tmp = EncryptionC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getLabel());
			}
			return l;
		}

		return this.getValues(id);
	}
	
	private String convertEnumName(String name) {
		if(name.contains("_")) {
			String t = new String(name);
			while(t.contains("_")) {
				t = t.replace("_", "-");
			}
			return t;
		}
		else {
			return name;
		}
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if(XMLCostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			return SignatureAlgorithm.RSA_SHA256.getUri();
		}
		else if(XMLCostanti.ID_SIGNATURE_DIGEST_ALGORITHM.equals(id)) {
			return SignatureDigestAlgorithm.SHA256.getUri();
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM.equals(id)) {
			return SignatureC14NAlgorithm.EXCLUSIVE_C14N_10_OMITS_COMMENTS.getUri(); // richiesto da WSI-BasicProfile
		}
		
		else if(XMLCostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			return KeyAlgorithm.AES.name();
		}
		else if(XMLCostanti.ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM.equals(id)) {
			return EncryptionSymmetricKeyWrapAlgorithm.AES_256.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_ALGORITHM.equals(id)) {
			return EncryptionAlgorithm.AES_256.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			return EncryptionKeyTransportAlgorithm.RSA_v1dot5.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_DIGEST_ALGORITHM.equals(id)) {
			return EncryptionDigestAlgorithm.SHA256.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM.equals(id)) {
			return EncryptionC14NAlgorithm.INCLUSIVE_C14N_10_WITH_COMMENTS.getUri();
		}
		
		return null;
	}

}
