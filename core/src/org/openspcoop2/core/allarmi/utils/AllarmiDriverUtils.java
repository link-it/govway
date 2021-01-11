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

package org.openspcoop2.core.allarmi.utils;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.allarmi.dao.jdbc.converter.AllarmeFieldConverter;
import org.openspcoop2.core.allarmi.dao.jdbc.converter.AllarmeHistoryFieldConverter;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**
 * AllarmiDriverUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmiDriverUtils {

	public static List<Allarme> allarmiList(ISearch ricerca, RuoloPorta ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "allarmiList";
		int idLista = Liste.CONFIGURAZIONE_ALLARMI;
		int offset;
		int limit;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

//		String filterTipoPlugin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_PLUGIN_CLASSI);
		
		log.debug("search : " + search);
				
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			boolean addAnd = false;
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(Allarme.model().FILTRO.RUOLO_PORTA, ruoloPorta.getValue()).and().equals(Allarme.model().FILTRO.NOME_PORTA, nomePorta);
				addAnd = true;
			}
			else {
				expr.isNull(Allarme.model().FILTRO.NOME_PORTA);
				addAnd = true;
			}
			
			if (!search.equals("")) {
				if(addAnd)
					expr.and();
				
				expr.ilike(Allarme.model().NOME, search, LikeMode.ANYWHERE);
				
				addAnd = true;
			}
			
			NonNegativeNumber count = allarmiServiceSearch.count(expr);
			ricerca.setNumEntries(idLista,(int) count.longValue());
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			IPaginatedExpression pagExpr = allarmiServiceSearch.toPaginatedExpression(expr);
			
			pagExpr.limit(limit).offset(offset);
			pagExpr.addOrder(Allarme.model().NOME, SortOrder.ASC);

			return allarmiServiceSearch.findAll(pagExpr);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	
	public static void createAllarme(Allarme allarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "createAllarme";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeService allarmiService = jdbcServiceManager.getAllarmeService();
			
			allarmiService.create(allarme);
			
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static void updateAllarme(Allarme allarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "updateAllarme";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeService allarmiService = jdbcServiceManager.getAllarmeService();
			
			IdAllarme idAll = new IdAllarme();
			idAll.setNome(allarme.getNome());
			allarmiService.update(idAll,allarme);
			
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static void deleteAllarme(Allarme allarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "deleteAllarme";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeService allarmiService = jdbcServiceManager.getAllarmeService();
			
			allarmiService.delete(allarme);
			
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}

	public static boolean existsAllarme(String nome, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "existsAllarme";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			expr.equals(Allarme.model().NOME, nome);
			
			NonNegativeNumber count = allarmiServiceSearch.count(expr);
			
			return count.longValue() > 0;
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static Allarme getAllarme(Long id, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "getAllarme";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			AllarmeFieldConverter converter = new AllarmeFieldConverter(tipoDB);
			expr.equals(new CustomField("id", Long.class, "id", converter.toTable(Allarme.model())), id);
			
			return allarmiServiceSearch.find(expr);
			
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	
	public static void createHistoryAllarme(AllarmeHistory allarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "createHistoryAllarme";

		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeHistoryService allarmiService = jdbcServiceManager.getAllarmeHistoryService();
			
			allarmiService.create(allarme);
			
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static List<AllarmeHistory> allarmiHistoryList(ISearch ricerca, Long idAllarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "allarmiHistoryList";
		int idLista = Liste.CONFIGURAZIONE_ALLARMI_HISTORY;
		int offset;
		int limit;
//		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
//		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

//		String filterTipoPlugin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_PLUGIN_CLASSI);
		
//		this.log.debug("search : " + search);
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeHistoryServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeHistoryServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			AllarmeHistoryFieldConverter converter = new AllarmeHistoryFieldConverter(tipoDB);
			CustomField cf = new CustomField("id_allarme", Long.class, "id_allarme", converter.toTable(AllarmeHistory.model()));
			expr.equals(cf, idAllarme);
			
//			boolean addAnd = false;
			
//			if (!search.equals("")) {
//				expr.ilike(Allarme.model().NOME, search, LikeMode.ANYWHERE);
				
//				addAnd = true;
//			}
			
			NonNegativeNumber count = allarmiServiceSearch.count(expr);
			ricerca.setNumEntries(idLista,(int) count.longValue());
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			IPaginatedExpression pagExpr = allarmiServiceSearch.toPaginatedExpression(expr);
			
			pagExpr.limit(limit).offset(offset);
			pagExpr.addOrder(AllarmeHistory.model().TIMESTAMP_UPDATE, SortOrder.DESC);

			return allarmiServiceSearch.findAll(pagExpr);
			
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
}
