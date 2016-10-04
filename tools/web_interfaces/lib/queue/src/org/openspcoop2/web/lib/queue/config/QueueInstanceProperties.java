/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.lib.queue.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.resources.InstanceProperties;
import org.openspcoop2.web.lib.queue.costanti.Costanti;

/**
* BackwardCompatibilityInstanceProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/

class QueueInstanceProperties extends InstanceProperties {

	QueueInstanceProperties(Properties reader,Logger log,String confDir) throws Exception{
		super(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,reader, log);
		super.setLocalFileImplementation(Costanti.OPENSPCOOP2_QUEUE_PROPERTIES, Costanti.OPENSPCOOP2_QUEUE_LOCAL_PATH, confDir);
	}
}
