package org.openspcoop2.core.commons.dao;

import java.util.Properties;

import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;


/**
 * DAOFactoryInstanceProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DAOFactoryInstanceProperties extends InstanceProperties {

	private final static String PROPERTIES_LOCAL_PATH = "daoFactory_local.properties";
	private final static String PROPERTIES = "DAO_FACTORY_PROPERTIES";
    public final static String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";
	
	DAOFactoryInstanceProperties(Properties reader,Logger log) throws Exception{
		super(OPENSPCOOP2_LOCAL_HOME,reader, log);
			
		// Leggo directory di configurazione
		String confDir = super.getValue("confDirectory");
		
		super.setLocalFileImplementation(PROPERTIES,PROPERTIES_LOCAL_PATH, confDir);
		
	}
	
}
