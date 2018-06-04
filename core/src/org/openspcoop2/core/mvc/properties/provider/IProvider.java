package org.openspcoop2.core.mvc.properties.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IProvider {

	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException;
	
	public List<String> getValues(String id) throws ProviderException;
	public List<String> getLabels(String id) throws ProviderException;
	
	public String getDefault(String id) throws ProviderException;
	
}
