/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
