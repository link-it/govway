/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.plugins.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.dao.IPluginService;
import org.openspcoop2.core.plugins.dao.IPluginServiceSearch;
import org.openspcoop2.core.plugins.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.plugins.dao.jdbc.converter.PluginFieldConverter;
import org.openspcoop2.generic_project.beans.AliasTableComplexField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
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
		
		String filterStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO);
		
		log.debug("search : " + search);
		log.debug("filterTipoPlugin : " + filterTipoPlugin);
		log.debug("filterStato : " + filterStato);
		
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
			
			if(filterStato!=null && !filterStato.equals("")) {
				if(Filtri.FILTRO_STATO_VALORE_ABILITATO.equals(filterStato) || Filtri.FILTRO_STATO_VALORE_DISABILITATO.equals(filterStato)) {
					
					if(addAnd) {
						expr.and();
					}
					addAnd = true;
					
					expr.equals(Plugin.model().STATO, Filtri.FILTRO_STATO_VALORE_ABILITATO.equals(filterStato));
					
				}
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
					log.debug("filtroRuolo : " + filtroRuolo);
					
					if(!filtroRuolo.equals("")) {
						
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_RUOLO_NOME);
						expr.and();
						
						IExpression exprOr = pluginServiceSearch.newExpression();
						exprOr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroRuolo);
						exprOr.or();
						exprOr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, PluginCostanti.FILTRO_RUOLO_VALORE_ENTRAMBI);
						expr.and(exprOr);
						
					}
					break;
				case SERVICE_HANDLER:
					String filtroShTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME);
					log.debug("filtroShTipo : " + filtroShTipo);
					
					if(!filtroShTipo.equals("")) {
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_SERVICE_HANDLER_NOME)
							.and().equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroShTipo);
					}
					break;
				case MESSAGE_HANDLER:
					// message handler e ruolo messa ge handler
					String filtroMhRuolo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME);
					log.debug("filtroMhRuolo : " + filtroMhRuolo);
					boolean ruoloDefined = !filtroMhRuolo.equals("");
					
					String filtroMhTipo = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME);
					log.debug("filtroMhTipo : " + filtroMhTipo);
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
					String filtroApplicabilita = SearchUtils.getFilter(ricerca, idLista, PluginCostanti.FILTRO_APPLICABILITA_NOME);
					log.debug("filtroApplicabilita : " + filtroApplicabilita);
					
					if(!filtroApplicabilita.equals("")) {
						
						expr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.NOME, PluginCostanti.FILTRO_APPLICABILITA_NOME);
						expr.and();
						
						IExpression exprOr = pluginServiceSearch.newExpression();
						exprOr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, filtroApplicabilita);
						exprOr.or();
						exprOr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, PluginCostanti.FILTRO_APPLICABILITA_VALORE_QUALSIASI);
						if(PluginCostanti.FILTRO_APPLICABILITA_VALORE_EROGAZIONE.equals(filtroApplicabilita) || PluginCostanti.FILTRO_APPLICABILITA_VALORE_FRUIZIONE.equals(filtroApplicabilita)) {
							exprOr.equals(Plugin.model().PLUGIN_PROPRIETA_COMPATIBILITA.VALORE, PluginCostanti.FILTRO_APPLICABILITA_VALORE_IMPLEMENTAZIONE_API);
						}
						expr.and(exprOr);
						
					}
					break;
				case BEHAVIOUR:
				case CONNETTORE:
				case RATE_LIMITING:
				case RICERCA:
				case STATISTICA:
				case TRANSAZIONE:
				case TOKEN_VALIDAZIONE:
				case TOKEN_NEGOZIAZIONE:
				case ATTRIBUTE_AUTHORITY:
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
		return _existsPlugin(tipoPlugin, tipo, label, className, con, log, tipoDB);
	}
	public static boolean existsPluginConTipo(TipoPlugin tipoPlugin, String tipo, Connection con, Logger log, String tipoDB) throws ServiceException {
		return _existsPlugin(tipoPlugin, tipo, null, null, con, log, tipoDB);
	}
	public static boolean existsPluginConLabel(TipoPlugin tipoPlugin, String label, Connection con, Logger log, String tipoDB) throws ServiceException {
		return _existsPlugin(tipoPlugin, null, label, null, con, log, tipoDB);
	}
	public static boolean existsPluginConClassName(TipoPlugin tipoPlugin, String className, Connection con, Logger log, String tipoDB) throws ServiceException {
		return _existsPlugin(tipoPlugin, null, null, className, con, log, tipoDB);
	}
	
	private static boolean _existsPlugin(TipoPlugin tipoPlugin, String tipo, String label, String className, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "existsPlugin";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManagerMonitorEngineConfig = new JDBCServiceManager(con, properties, log);
			
			IPluginServiceSearch pluginServiceSearch = jdbcServiceManagerMonitorEngineConfig.getPluginServiceSearch();
			
			IExpression expr = pluginServiceSearch.newExpression();
			
			List<IExpression> list = new ArrayList<IExpression>();
			
			if(tipo!=null) {
				IExpression tipoExp = pluginServiceSearch.newExpression();
				tipoExp.equals(Plugin.model().TIPO_PLUGIN, tipoPlugin.toString()).and().equals(Plugin.model().TIPO, tipo);
				list.add(tipoExp);
			}
			if(label!=null) {
				IExpression labelExp = pluginServiceSearch.newExpression();
				labelExp.equals(Plugin.model().TIPO_PLUGIN, tipoPlugin.toString()).and().equals(Plugin.model().LABEL, label);
				list.add(labelExp);
			}
			if(className!=null) {
				IExpression classExp = pluginServiceSearch.newExpression();
				classExp.equals(Plugin.model().TIPO_PLUGIN, tipoPlugin.toString()).and().equals(Plugin.model().CLASS_NAME, className);
				list.add(classExp);
			}
			
			expr.or(list.toArray(new IExpression[list.size()]));
			
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
	
	public static Plugin getPlugin(TipoPlugin tipoPlugin, String tipo, boolean throwNotFound, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException {
		return getPlugin(tipoPlugin.getValue(), tipo, throwNotFound, con, log, tipoDB);
	}
	public static Plugin getPlugin(String tipoPlugin, String tipo, boolean throwNotFound, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException {
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
			if(plugin==null) {
				throw new NotFoundException("NotFound");
			}
			
			IdPlugin idPluginObj = pluginServiceSearch.convertToId(plugin);
			plugin.setOldIdPlugin(idPluginObj);
			
			return plugin;
		} 
		catch (NotFoundException notFound) {
			if(throwNotFound) {
				throw new NotFoundException("[" + nomeMetodo + "] Errore : " + notFound.getMessage(),notFound);
			}
			return null;
		}
		catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
}
