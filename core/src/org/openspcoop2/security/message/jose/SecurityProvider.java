package org.openspcoop2.security.message.jose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
public class SecurityProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {

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
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		List<String> l = this.getValues(id);
		if(JOSECostanti.ID_SIGNATURE_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id) ||
				JOSECostanti.ID_ENCRYPT_CONTENT_ALGORITHM.equals(id)) {
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
		return l;
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
		return null;
	}

}
