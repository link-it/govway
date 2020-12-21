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
 * PluginLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginLoader implements IPluginLoader {

	public static PluginLoader getInstance() {
		return (PluginLoader) CorePluginLoader.getInstance();
	}
	
	private PluginManager pluginManager = null;
	private Loader loader = null;
	private Logger log;
	
	public PluginLoader() {
	}
	
	@Override
	public void init(Loader loader, Logger log) {
		this.loader = loader;
		this.log = log;
	}
	@Override
	public void init(Loader loader, Logger log, IRegistroPluginsReader registroPluginsReader, int expireSeconds) {
		init(loader, log);
		this.pluginManager = new PluginManager(registroPluginsReader, expireSeconds);
	}
	
	public boolean isPluginManagerEnabled() {
		return this.pluginManager!=null;
	}
	
	
	// UTILITY
	
	@Override
	public Class<?> getDynamicClass(String className, TipoPlugin tipoPlugin, String tipo) throws Exception {
		return getDynamicClass(className, tipoPlugin.getValue(), tipo);
	}
	@Override
	public Class<?> getDynamicClass(String className, String tipoPlugin, String tipo) throws Exception {
		if(className==null) {
			throw new Exception("Class not found ("+getObjectName(tipoPlugin)+" type '"+tipo+"')");
		}
		if(this.pluginManager==null) {
			throw new Exception("Plugin manager not initialized");
		}
		Class<?> c = null;
		try {
			c = this.pluginManager.findClass(this.log, tipoPlugin, className);
		}catch(Exception e) {
			throw new Exception("Class '"+className+"' not found in registry ("+getObjectName(tipoPlugin)+" type '"+tipo+"'): "+e.getMessage(),e);
		}
		if(c==null) {
			throw new Exception("Class '"+className+"' not found in registry ("+getObjectName(tipoPlugin)+" type '"+tipo+"')");
		}
		return c;
	}
	
	@Override
	public <T> T newInstance(Class<T> c, TipoPlugin tipoPlugin, String tipo) throws Exception{
		return newInstance(c, tipoPlugin.getValue(), tipo);
	}
	@Override
	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<T> c, String tipoPlugin, String tipo) throws Exception{
		try {
			return (T) this.loader.newInstance(c);
		}catch(Throwable e) {
			throw new Exception("Class '"+c.getClass().getName()+"' new instance error ("+getObjectName(tipoPlugin)+" type '"+tipo+"'): "+e.getMessage(),e);
		}
	}
	
	@Override
	public Object newInstance(String customTipoClasse, String tipo) throws Exception {
		Class<?> c = getDynamicClass(null, customTipoClasse, tipo);
		return newInstance(c, customTipoClasse, tipo);
	}
	
	protected String getObjectName(String tipoPluginParam) throws Exception {
		
		TipoPlugin tipoPlugin = TipoPlugin.toEnumConstant(tipoPluginParam);
		
		if(tipoPlugin==null) {
			return tipoPluginParam;
		}
		
		switch (tipoPlugin) {
		case CONNETTORE:
			return "connector";
		case AUTENTICAZIONE:
			return "authentication";
		case AUTORIZZAZIONE:
			return "authorization";
		case AUTORIZZAZIONE_CONTENUTI:
			return "authorization content";
		case INTEGRAZIONE:
			return "integration";
		case SERVICE_HANDLER:
			return "service handler";
		case MESSAGE_HANDLER:
			return "message handler";
		case BEHAVIOUR:
			return "behaviour";
		case RATE_LIMITING:
			return "policy rate limiting";
		case ALLARME:
			return "alarm";
		case TRANSAZIONE:
			return "transaction library";
		case RICERCA:
			return "search library";
		case STATISTICA:
			return "stats library";
		}
		throw new Exception("?? Type '"+tipoPlugin+"' unsupported ??");
	}
	
}
