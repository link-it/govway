package org.openspcoop2.generic_project.web.config;

import java.util.Properties;

import org.openspcoop2.utils.resources.InstanceProperties;
import org.slf4j.Logger;


/**
 * @author Poli Andrea (apoli@link.it)
 */
public class ApplicationInstanceProperties extends InstanceProperties {


   
	
	ApplicationInstanceProperties(String OPENSPCOOP2_LOCAL_HOME,Properties reader,Logger log,String localPropertyName,String localPropertiesPath) throws Exception{
		super(OPENSPCOOP2_LOCAL_HOME,reader, log);
			
		// Leggo directory di configurazione
		String confDir = super.getValue("confDirectory");
		
		super.setLocalFileImplementation(localPropertyName,localPropertiesPath, confDir);
		
	}
	
}
