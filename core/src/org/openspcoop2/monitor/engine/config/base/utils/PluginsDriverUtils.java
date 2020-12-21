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

package org.openspcoop2.monitor.engine.config.base.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.generic_project.beans.AliasTableComplexField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginService;
import org.openspcoop2.monitor.engine.config.base.dao.IPluginServiceSearch;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.converter.PluginFieldConverter;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**
 * PluginsDriverUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginsDriverUtils {

	public static int numeroPluginsClassiList(Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "numeroPluginsClassiList";
		int val = 0;

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginServiceSearch pluginServiceSearch = jdbcServiceManagerMonitorEngineConfig.getPluginServiceSearch();
			
			IExpression expr = pluginServiceSearch.newExpression();
			
			NonNegativeNumber count = pluginServiceSearch.count(expr);
			if(count!= null)
				val = (int) count.longValue();
			
			return val;

		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static List<Plugin> pluginsClassiList(ISearch ricerca, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "pluginsClassiList";
		int idLista = Liste.CONFIGURAZIONE_PLUGINS_CLASSI;
		int offset;
		int limit;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterTipoPlugin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_PLUGIN_CLASSI);
		
		log.debug("search : " + search);
		
		List<Plugin> lista = new ArrayList<Plugin>();

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginServiceSearch pluginServiceSearch = jdbcServiceManagerMonitorEngineConfig.getPluginServiceSearch();
			
			IExpression expr = pluginServiceSearch.newExpression();
			
			boolean addAnd = false;
			
			if (!search.equals("")) {
				
				IExpression exprLabel = pluginServiceSearch.newExpression();
				exprLabel.ilike(Plugin.model().LABEL, search, LikeMode.ANYWHERE);
				
				IExpression exprTipo = pluginServiceSearch.newExpression();
				exprTipo.ilike(Plugin.model().TIPO, search, LikeMode.ANYWHERE);
				
				expr.or(exprLabel, exprTipo);
				
				addAnd = true;
			}
			
			if(!filterTipoPlugin.equals("")) {
				if(addAnd) {
					expr.and();
				}
				
				expr.equals(Plugin.model().TIPO_PLUGIN, filterTipoPlugin);
				
				TipoPlugin tipoPlugin = TipoPlugin.toEnumConstant(filterTipoPlugin);
				switch (tipoPlugin) {
				case AUTENTICAZIONE:
				case AUTORIZZAZIONE:
				case AUTORIZZAZIONE_CONTENUTI:
				case INTEGRAZIONE:
					String filtroRuolo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_RUOLO_NOME);
				
					if(!filtroRuolo.equals("")) {
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_RUOLO_NOME)
							.and().equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroRuolo);
					}
					break;
				case SERVICE_HANDLER:
					String filtroShTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME);
				
					if(!filtroShTipo.equals("")) {
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME)
							.and().equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroShTipo);
					}
					break;
				case MESSAGE_HANDLER:
					// message handler e ruolo messa ge handler
					String filtroMhRuolo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME);
					boolean ruoloDefined = !filtroMhRuolo.equals("");
					
					String filtroMhTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME);
					boolean tipoDefined = !filtroMhTipo.equals("");
										
					if(tipoDefined && ruoloDefined) {
						
						IAliasTableField ruolo_name = new AliasTableComplexField((ComplexField)Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, FilterUtils.getNextAliasPluginsTable());
						IAliasTableField ruolo_valore = new AliasTableComplexField((ComplexField)Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, ruolo_name.getAliasTable());
						
						IAliasTableField tipo_name = new AliasTableComplexField((ComplexField)Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, FilterUtils.getNextAliasPluginsTable());
						IAliasTableField tipo_valore = new AliasTableComplexField((ComplexField)Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, tipo_name.getAliasTable());
						
						expr.
							and().
							equals(ruolo_name, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME).
							equals(ruolo_valore, filtroMhRuolo).
							equals(tipo_name, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME).
							equals(tipo_valore, filtroMhTipo);
					}
					else if(tipoDefined) {
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME)
						.and().equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroMhTipo);
					}
					else if(ruoloDefined) {
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME)
						.and().equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroMhRuolo);
					}
										
					break;
				case ALLARME:
				case BEHAVIOUR:
				case CONNETTORE:
				case RATE_LIMITING:
				case RICERCA:
				case STATISTICA:
				case TRANSAZIONE:
					break;
				}
			}
			
			NonNegativeNumber count = pluginServiceSearch.count(expr);
			ricerca.setNumEntries(idLista,(int) count.longValue());
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			IPaginatedExpression pagExpr = pluginServiceSearch.toPaginatedExpression(expr);
			
			pagExpr.limit(limit).offset(offset);
			
			pagExpr.addOrder(Plugin.model().TIPO_PLUGIN, SortOrder.ASC);
			pagExpr.addOrder(Plugin.model().LABEL, SortOrder.ASC);

			lista = pluginServiceSearch.findAll(pagExpr);

			return lista;

		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static void createPluginClassi(Plugin plugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "createPluginClassi";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginService pluginService = jdbcServiceManagerMonitorEngineConfig.getPluginService();
			
			pluginService.create(plugin);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static void updatePluginClassi(Plugin plugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "updatePluginClassi";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginService pluginService = jdbcServiceManagerMonitorEngineConfig.getPluginService();
			
			pluginService.update(plugin.getOldIdPlugin(), plugin);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static void deletePluginClassi(Plugin plugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "deletePluginClassi";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginService pluginService = jdbcServiceManagerMonitorEngineConfig.getPluginService();
			
			pluginService.delete(plugin);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}

	public static boolean existsPlugin(TipoPlugin tipoPlugin, String tipo, String label, String className, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "existsPlugin";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginServiceSearch pluginServiceSearch = jdbcServiceManagerMonitorEngineConfig.getPluginServiceSearch();
			
			IExpression expr = pluginServiceSearch.newExpression();
			
			IExpression tipoExp = pluginServiceSearch.newExpression();
			tipoExp.equals(Plugin.model().TIPO_PLUGIN, tipoPlugin.toString()).and().equals(Plugin.model().TIPO, tipo);
			IExpression labelExp = pluginServiceSearch.newExpression();
			labelExp.equals(Plugin.model().TIPO_PLUGIN, tipoPlugin.toString()).and().equals(Plugin.model().LABEL, label);
			IExpression classExp = pluginServiceSearch.newExpression();
			classExp.equals(Plugin.model().TIPO_PLUGIN, tipoPlugin.toString()).and().equals(Plugin.model().CLASS_NAME, className);
			
			expr.or(tipoExp, labelExp, classExp);
			
			NonNegativeNumber count = pluginServiceSearch.count(expr);
			
			return count.longValue() > 0;
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}

	public static Plugin getPlugin(long idPlugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "getPlugin";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginServiceSearch pluginServiceSearch = jdbcServiceManagerMonitorEngineConfig.getPluginServiceSearch();
			
			IExpression expr = pluginServiceSearch.newExpression();
			
			PluginFieldConverter converter = new PluginFieldConverter(tipoDB);
			expr.equals(new CustomField("id", Long.class, "id", converter.toTable(Plugin.model())), idPlugin);

			Plugin plugin = pluginServiceSearch.find(expr);
			
			IdPlugin idPluginObj = pluginServiceSearch.convertToId(plugin);
			plugin.setOldIdPlugin(idPluginObj);
			
			return plugin;
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static Plugin getPlugin(TipoPlugin tipoPlugin, String tipo, Connection con, Logger log, String tipoDB) throws ServiceException {
		return getPlugin(tipoPlugin.getValue(), tipo, con, log, tipoDB);
	}
	public static Plugin getPlugin(String tipoPlugin, String tipo, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "getPlugin";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginServiceSearch pluginServiceSearch = jdbcServiceManagerMonitorEngineConfig.getPluginServiceSearch();
			
			IdPlugin idPlugin = new IdPlugin();
			idPlugin.setTipoPlugin(tipoPlugin);
			idPlugin.setTipo(tipo);
			
			Plugin plugin = pluginServiceSearch.get(idPlugin);
			
			IdPlugin idPluginObj = pluginServiceSearch.convertToId(plugin);
			plugin.setOldIdPlugin(idPluginObj);
			
			return plugin;
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static boolean isPluginInUso(String className, String label, String tipoPlugin, String tipo,
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, 
			Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "isPluginInUso";

		try {

			return DBOggettiInUsoUtils.isPluginInUso(con, tipoDB, className, label, tipoPlugin, tipo, whereIsInUso, normalizeObjectIds);
			
		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} 
	}
	
}
