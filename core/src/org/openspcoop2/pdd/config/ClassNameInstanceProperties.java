/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.properties.InstanceProperties;

/**
* ClassNameInstanceProperties
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

public class ClassNameInstanceProperties extends InstanceProperties {

	ClassNameInstanceProperties(Properties reader,Logger log) {
		super(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,reader, log);
	}
	
	public void searchLocalFileImplementation(String confDir){
		// Leggo directory di configurazione
		super.setLocalFileImplementation(CostantiPdD.OPENSPCOOP2_CLASSNAME_PROPERTIES,CostantiPdD.OPENSPCOOP2_CLASSNAME_LOCAL_PATH, confDir);
	}
	
}
