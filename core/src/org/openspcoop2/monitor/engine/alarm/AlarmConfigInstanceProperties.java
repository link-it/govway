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


package org.openspcoop2.monitor.engine.alarm;

import java.util.Properties;

import org.openspcoop2.utils.properties.InstanceProperties;
import org.slf4j.Logger;

/**
* AlarmConfigInstanceProperties
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

class AlarmConfigInstanceProperties extends InstanceProperties {

    public final static String ALARM_PROPERTIES_LOCAL_PATH = "allarmi_configurazione_local.properties";
    public final static String ALARM_PROPERTIES = "ALLARMI_CONFIGURAZIONE_PROPERTIES";
	
	AlarmConfigInstanceProperties(Properties reader,Logger log,String confDir) throws Exception{
		super(org.openspcoop2.utils.Costanti.OPENSPCOOP2_LOCAL_HOME,reader, log);
		super.setLocalFileImplementation(
				ALARM_PROPERTIES, ALARM_PROPERTIES_LOCAL_PATH,
				confDir);
	}
}