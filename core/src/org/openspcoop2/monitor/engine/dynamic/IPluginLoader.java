/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * IPluginLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IPluginLoader {

	public void init(Loader loader, Logger log);
	public void init(Loader loader, Logger log, IRegistroPluginsReader registroPluginsReader, int expireSeconds);
	
	public Class<?> getDynamicClass(String className, TipoPlugin tipoPlugin, String tipo) throws Exception;
	public Class<?> getDynamicClass(String className, String tipoPlugin, String tipo) throws Exception;
	
	public <T> T newInstance(Class<T> c, TipoPlugin tipoPlugin, String tipo) throws Exception;
	public <T> T newInstance(Class<T> c, String tipoPlugin, String tipo) throws Exception;
	
	public Object newInstance(String customTipoClasse, String tipo) throws Exception;
	
}
