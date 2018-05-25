package org.openspcoop2.core.mvc.properties.plugins;

import java.util.Map;
import java.util.Properties;

public interface IPlugin {

	public void validate(Map<String, Properties> mapProperties) throws PluginException, PluginValidationException;
	
}
