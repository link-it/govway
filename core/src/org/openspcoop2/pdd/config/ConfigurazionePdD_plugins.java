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

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.PluginProprietaCompatibilita;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginServiceSearch;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.NameValue;

/**     
 * ConfigurazionePdD_plugins
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdD_plugins extends AbstractConfigurazionePdDConnectionResourceManager {

	private ServiceManagerProperties smp;
	
	
	public ConfigurazionePdD_plugins(OpenSPCoop2Properties openspcoopProperties, DriverConfigurazioneDB driver, boolean useConnectionPdD) {
		super(openspcoopProperties, driver, useConnectionPdD, OpenSPCoop2Logger.getLoggerOpenSPCoopPluginsSql(openspcoopProperties.isConfigurazionePluginsDebug()));
		
		this.smp = new ServiceManagerProperties();
		this.smp.setShowSql(this.openspcoopProperties.isConfigurazionePluginsDebug());
		this.smp.setDatabaseType(this.driver.getTipoDB());
	}
	
	
	
	
	public int countPlugins(Connection connectionPdD) throws DriverConfigurazioneException{
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Plugins.countPlugins");
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager sm = 
					(org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			IPluginServiceSearch search =  sm.getPluginServiceSearch();
			IExpression expr = search.newExpression();
			return (int) search.count(expr).longValue();
		}
		catch(Exception e){
			String errorMsg = "Errore durante la conta dei plugins registrati: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	public List<IdPlugin> findAllPluginIds(Connection connectionPdD, int offset, int limit) throws DriverConfigurazioneException{
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Plugins.findAllPluginIds_"+offset+"_"+limit);
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager sm = 
					(org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			IPluginServiceSearch search =  sm.getPluginServiceSearch();
			IPaginatedExpression pagExpr = search.newPaginatedExpression();
			pagExpr.offset(offset);
			pagExpr.limit(limit);
			pagExpr.addOrder(Plugin.model().TIPO_PLUGIN);
			pagExpr.addOrder(Plugin.model().TIPO);
			return search.findAllIds(pagExpr);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la lettura degli idPlugin (offset:"+offset+" limit:"+limit+"): "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	
	
	public String getPluginClassName(Connection connectionPdD, String tipoPlugin, String tipo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getPluginClassNameByFilter(connectionPdD, tipoPlugin, tipo);
	}
	public String getPluginClassNameByFilter(Connection connectionPdD, String tipoPlugin, String tipo, NameValue ... filtri) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		ConfigurazionePdDConnectionResource cr = null;
		try{
			cr = this.getConnection(connectionPdD, "Plugins.getPluginClassName_"+tipoPlugin+"#"+tipo);
			org.openspcoop2.monitor.engine.config.base.dao.IServiceManager sm = 
					(org.openspcoop2.monitor.engine.config.base.dao.IServiceManager) DAOFactory.getInstance(this.log).
					getServiceManager(org.openspcoop2.monitor.engine.config.base.utils.ProjectInfo.getInstance(),
							cr.connectionDB,this.smp,this.log);
			
			IPluginServiceSearch search =  sm.getPluginServiceSearch();
			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(tipoPlugin);
			idPlugin.setTipo(tipo);
			
			Plugin plugin = search.get(idPlugin);
			
			if(filtri!=null && filtri.length>0) {
				for (int i = 0; i < filtri.length; i++) {
					NameValue filtro = filtri[i];
					
					if(plugin.sizePluginProprietaCompatibilitaList()>0) {
						for (int j = 0; j < plugin.sizePluginProprietaCompatibilitaList(); j++) {
							PluginProprietaCompatibilita ppc = plugin.getPluginProprietaCompatibilita(j);
							if(ppc.getNome().equals(filtro.getName())) {
								if(!ppc.getValore().equals(filtro.getValue())) {
									throw new NotFoundException("Filtro '"+ppc.getNome()+"' non soddisfatto (atteso:"+filtro.getValue()+" trovato:"+ppc.getValore()+")");
								}
							}
						}
					}
					
				}
			}
			
			return plugin.getClassName();

		}
		catch(NotFoundException e) {
			String errorMsg = "Plugin (tipologia:"+tipoPlugin+" tipo:"+tipo+") non trovato: "+e.getMessage();
			this.log.debug(errorMsg,e);
			throw new DriverConfigurazioneNotFound(errorMsg,e);
		}
		catch(Exception e){
			String errorMsg = "Errore durante la lettura del Plugin (tipologia:"+tipoPlugin+" tipo:"+tipo+"): "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new DriverConfigurazioneException(errorMsg,e);
		}
		finally {
			this.releaseConnection(cr);
		}

	}
	
	
}
