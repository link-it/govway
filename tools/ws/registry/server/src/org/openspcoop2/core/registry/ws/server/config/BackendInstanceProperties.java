/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.registry.ws.server.config;

import java.util.Properties;

import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;

/**
* BackendInstanceProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/

class BackendInstanceProperties extends InstanceProperties {

	BackendInstanceProperties(Properties reader,Logger log,String confDir) throws Exception{
		super(ConstantsEnv.OPENSPCOOP2_LOCAL_HOME,reader, log);
		super.setLocalFileImplementation(ConstantsEnv.OPENSPCOOP2_BACKEND_PROPERTIES, ConstantsEnv.OPENSPCOOP2_BACKEND_LOCAL_PATH, confDir);
	}
}
