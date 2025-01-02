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

package org.openspcoop2.monitor.engine.dynamic;

import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * CorePluginLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CorePluginLoader {

	private static IPluginLoader staticInstance;
	public static synchronized void initialize(Loader loader, Logger log, Class<?> c) throws Exception {
		staticInstance = (IPluginLoader) loader.newInstance(c);
		staticInstance.init(loader, log);
	}
	public static synchronized void initialize(Loader loader, Logger log, Class<?> c, IRegistroPluginsReader registroPluginsReader, int expireSeconds) throws Exception {
		staticInstance = (IPluginLoader) loader.newInstance(c);
		staticInstance.init(loader, log, registroPluginsReader, expireSeconds);
	}
	public static IPluginLoader getInstance() {
		return staticInstance;
	}
	public static synchronized void close(Logger log) {
		if(staticInstance!=null) {
			staticInstance.close(log);
		}
	}
}
