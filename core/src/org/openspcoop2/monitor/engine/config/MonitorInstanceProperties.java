package org.openspcoop2.monitor.engine.config;

import java.util.Properties;

import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;


/**
 * MonitorInstanceProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitorInstanceProperties extends InstanceProperties {
	
	MonitorInstanceProperties(Properties reader,Logger log) throws Exception{
		super(Costanti.OPENSPCOOP2_LOCAL_HOME,reader, log);
			
		// Leggo directory di configurazione
		String confDir = super.getValue("confDirectory");
		
		super.setLocalFileImplementation(CostantiConfigurazione.PROPERTIES,CostantiConfigurazione.PROPERTIES_LOCAL_PATH, confDir);
		
	}
	
}
