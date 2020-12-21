/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.monitor.engine.dynamic.IRegistroPluginsReader;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**     
 * ConfigurazionePdD_registroPlugin
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD_registroPlugins extends AbstractConfigurazionePdDConnectionResourceManager implements IRegistroPluginsReader {

	public ConfigurazionePdD_registroPlugins(OpenSPCoop2Properties openspcoopProperties, DriverConfigurazioneDB driver, boolean useConnectionPdD) {
		super(openspcoopProperties, driver, useConnectionPdD, OpenSPCoop2Logger.getLoggerOpenSPCoopPluginsSql(openspcoopProperties.isConfigurazionePluginsDebug()));
	}
	
	@Override
	public RegistroPlugins getRegistroPlugins() throws NotFoundException, CoreException {
		try {
			return this.driver.getRegistroPlugins();
		}catch(DriverConfigurazioneNotFound notFound) {
			throw new NotFoundException(notFound.getMessage(), notFound);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	
}
