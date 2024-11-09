/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.slf4j.Logger;

/**
 * DynamicFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicFactory {

	private static DynamicFactory basicLoaderFactory;
	private static synchronized void initialize() {
		if(basicLoaderFactory==null){
			basicLoaderFactory = new DynamicFactory();
		}
	}
	public static DynamicFactory getInstance() {
		if(basicLoaderFactory==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (DynamicFactory.class) {
				initialize();
			}
		}
		return basicLoaderFactory;
	}
	
	
	private PluginLoader pluginLoader = null;
	private DynamicFactory() {
		this.pluginLoader = PluginLoader.getInstance();
	}
	
	public IDynamicLoader newDynamicLoader(TipoPlugin tipoPlugin, String tipo, String className,Logger log) throws SearchException{
		return newDynamicLoader(tipoPlugin.getValue(), tipo, className, log);
	}
	public IDynamicLoader newDynamicLoader(String tipoPlugin, String tipo, String className,Logger log) throws SearchException{
		
		try {
			return new BasicLoader(tipoPlugin, tipo, className, this.pluginLoader.getDynamicClass(className, tipoPlugin, tipo), log);
		}catch(Exception e) {
			throw new SearchException(e.getMessage(),e);
		}
		
	}
	
	public IDynamicFilter newDynamicFilter(TipoPlugin tipoPlugin, String tipo, String className,Logger log) {
		return newDynamicFilter(tipoPlugin.getValue(), tipo, className, log);
	}
	public IDynamicFilter newDynamicFilter(String tipoPlugin, String tipo, String className,Logger log) {
		if(log!=null) {
			// unused
		}
		return new BasicFilter(tipoPlugin, tipo, className);
	}
	
	public IDynamicValidator newDynamicValidator(TipoPlugin tipoPlugin, String tipo, String className,Logger log) {
		return newDynamicValidator(tipoPlugin.getValue(), tipo, className, log);
	}
	public IDynamicValidator newDynamicValidator(String tipoPlugin, String tipo, String className,Logger log) {
		if(log!=null) {
			// unused
		}
		return new BasicValidator(tipoPlugin, tipo, className);
	}
}
