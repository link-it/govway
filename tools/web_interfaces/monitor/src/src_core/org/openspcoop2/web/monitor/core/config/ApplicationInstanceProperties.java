package org.openspcoop2.web.monitor.core.config;

import java.util.Properties;

import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;


/**
 * @author Poli Andrea (apoli@link.it)
 */
public class ApplicationInstanceProperties extends InstanceProperties {


   
	
	ApplicationInstanceProperties(Properties reader,Logger log,String localPropertyName,String localPropertiesPath) throws Exception{
		super(Costanti.OPENSPCOOP2_LOCAL_HOME,reader, log);
			
		// Leggo directory di configurazione
		String confDir = super.getValue("confDirectory");
		
		super.setLocalFileImplementation(localPropertyName,localPropertiesPath, confDir);
		
	}
	
}
