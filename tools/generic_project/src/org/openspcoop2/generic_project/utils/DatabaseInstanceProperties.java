/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.utils;

import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;


/**
 * DatabaseInstanceProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatabaseInstanceProperties extends InstanceProperties {

    public static final String OPENSPCOOP2_LOCAL_HOME = "GOVWAY_HOME";
	
	DatabaseInstanceProperties(String propertiesLocalPath, String propertiesName, Properties reader,Logger log) throws UtilsException {
		super(OPENSPCOOP2_LOCAL_HOME,reader, log);
			
		// Leggo directory di configurazione
		String confDir = super.getValue("confDirectory");
		
		super.setLocalFileImplementation(propertiesName,propertiesLocalPath, confDir);
		
	}
	
}
