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


package org.openspcoop2.protocol.trasparente.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.protocol.trasparente.constants.TrasparenteCostanti;
import org.openspcoop2.utils.resources.InstanceProperties;


/**
* TrasparenteInstanceProperties
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class TrasparenteInstanceProperties extends InstanceProperties {

	TrasparenteInstanceProperties(Properties reader,Logger log) throws Exception{
		super(TrasparenteCostanti.OPENSPCOOP2_LOCAL_HOME,reader, log);
		
		// Leggo directory di configurazione
		String confDir = super.getValue("org.openspcoop2.protocol.trasparente.confDirectory");
		
		super.setLocalFileImplementation(TrasparenteCostanti.TRASPARENTE_PROPERTIES,TrasparenteCostanti.TRASPARENTE_PROPERTIES_LOCAL_PATH, confDir);
		
	}
}
