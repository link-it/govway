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

package org.openspcoop2.core.allarmi.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryService;
import org.openspcoop2.core.allarmi.dao.IAllarmeHistoryServiceSearch;
import org.openspcoop2.core.allarmi.dao.IAllarmeService;
import org.openspcoop2.core.allarmi.dao.IAllarmeServiceSearch;
import org.openspcoop2.core.allarmi.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.allarmi.dao.jdbc.converter.AllarmeFieldConverter;
import org.openspcoop2.core.allarmi.dao.jdbc.converter.AllarmeHistoryFieldConverter;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
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

		String filterStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO);
		
		log.debug("search : " + search);
		log.debug("filterStato : " + filterStato);
				
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
				
				expr.ilike(Allarme.model().ALIAS, search, LikeMode.ANYWHERE);
				
				addAnd = true;
			}
			
			if (filterStato!=null && !filterStato.equals("")) {
				if(addAnd)
					expr.and();
				
				if(Filtri.FILTRO_STATO_VALORE_ABILITATO.equals(filterStato)) {
					expr.equals(Allarme.model().ENABLED, 1);
					addAnd = true;
				}
				else if(Filtri.FILTRO_STATO_VALORE_DISABILITATO.equals(filterStato)) {
					expr.equals(Allarme.model().ENABLED, 0);
					addAnd = true;
				}
				else if(Filtri.FILTRO_STATO_VALORE_OK.equals(filterStato)) {
					expr.equals(Allarme.model().ENABLED, 1);
					expr.and();
					expr.equals(Allarme.model().STATO, AllarmiConverterUtils.toIntegerValue(StatoAllarme.OK));
					addAnd = true;
				}
				else if(Filtri.FILTRO_STATO_VALORE_WARNING.equals(filterStato)) {
					expr.equals(Allarme.model().ENABLED, 1);
					expr.and();
					expr.equals(Allarme.model().STATO, AllarmiConverterUtils.toIntegerValue(StatoAllarme.WARNING));
					addAnd = true;
				}
				else if(Filtri.FILTRO_STATO_VALORE_ERROR.equals(filterStato)) {
					expr.equals(Allarme.model().ENABLED, 1);
					expr.and();
					expr.equals(Allarme.model().STATO, AllarmiConverterUtils.toIntegerValue(StatoAllarme.ERROR));
					addAnd = true;
				}
				
				addAnd = true;
			}
			
			NonNegativeNumber count = allarmiServiceSearch.count(expr);
			ricerca.setNumEntries(idLista,(int) count.longValue());
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			IPaginatedExpression pagExpr = allarmiServiceSearch.toPaginatedExpression(expr);
			
			pagExpr.limit(limit).offset(offset);
			pagExpr.addOrder(Allarme.model().ALIAS, SortOrder.ASC);

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
	
	public static Allarme getAllarme(String nome, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "getAllarmeByNome";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IdAllarme id = new IdAllarme();
			id.setNome(nome);
			
			return allarmiServiceSearch.get(id);
			
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
	
	public static Allarme getAllarmeByAlias(String alias,
			RuoloPorta ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getAllarmeByAlias"; 
		
		try{
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager serviceManager = new JDBCServiceManager(con, properties, log);
			IExpression expression = serviceManager.getAllarmeServiceSearch().newExpression();
			
			expression.and();
			expression.ilike(Allarme.model().ALIAS, alias, LikeMode.EXACT);
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expression.equals(Allarme.model().FILTRO.ENABLED, true);
				expression.equals(Allarme.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expression.equals(Allarme.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expression.isNull(Allarme.model().FILTRO.NOME_PORTA);
			}
		
			return serviceManager.getAllarmeServiceSearch().find(expression);
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Allarme non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static boolean existsAllarmi(TipoAllarme tipoAllarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "existsAllarmi";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			if(tipoAllarme!=null) {
				expr.equals(Allarme.model().TIPO_ALLARME, tipoAllarme.getValue());
			}
			
			NonNegativeNumber count = allarmiServiceSearch.count(expr);
			
			return count.longValue() > 0;
		} catch (Exception qe) {
			throw new ServiceException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static long countAllarmi(TipoAllarme tipoAllarme, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "countAllarmi";
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			if(tipoAllarme!=null) {
				expr.equals(Allarme.model().TIPO_ALLARME, tipoAllarme.getValue());
			}
			
			NonNegativeNumber count = allarmiServiceSearch.count(expr);
			
			return count.longValue();
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
	
	private static String FREE_COUNTER_SEPARATOR_CHAR = "@"; 
	private static int FREE_COUNTER_SEPARATOR_CHAR_PAD = 19; // colonna che memorizza l'info ha dimensione 275
	public static String buildIdAlarm(String tipoPlugin, String serialId) {
		String idAlarm = tipoPlugin+AllarmiDriverUtils.FREE_COUNTER_SEPARATOR_CHAR+serialId;
		return idAlarm;
	}
	
	private static String normalizeAlarmInstanceSerialId(int value) {
		return StringUtils.leftPad(value+"", FREE_COUNTER_SEPARATOR_CHAR_PAD, "0");
	}
	public static String incrementAlarmInstanceSerialId(String value) {
		int valueInt = 0;
		if(value!=null && !"".equals(value)) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				if('0' == c && sb.length()<=0) {
					continue;
				}
				sb.append(c);
			}
			valueInt = Integer.valueOf(sb.toString());
		}
		valueInt++;
		return StringUtils.leftPad(valueInt+"", FREE_COUNTER_SEPARATOR_CHAR_PAD, "0");
	}
	
	//public static Integer getFreeCounterForAlarm(String tipoPlugin, Connection con, Logger log, String tipoDB) throws ServiceException{
	public static String getNextAlarmInstanceSerialId(String tipoPlugin, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "getNextAlarmInstanceSerialId"; 
		
		
		try{
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IExpression pagExpr = jdbcServiceManager.getAllarmeServiceSearch().newExpression();
			pagExpr.and();
			pagExpr.equals(Allarme.model().TIPO, tipoPlugin);
			
			// non funziona perchè :10 viene ordinato prima di :9
			//pagExpr.addOrder(AttivazionePolicy.model().ID_ACTIVE_POLICY, SortOrder.DESC);
			//pagExpr.limit(1);
			// devo scorrerle tutte
			
			/*
			try{
				// inefficiente
				List<Object> list = jdbcServiceManager.getAllarmeServiceSearch().select(pagExpr, Allarme.model().NOME);
				if(list!=null && !list.isEmpty()){
					int found = -1;
					for (Object r : list) {
						if(r instanceof String){
							String s = (String)r;
							if(s.contains(FREE_COUNTER_SEPARATOR_CHAR)){
								int last = s.lastIndexOf(FREE_COUNTER_SEPARATOR_CHAR);
								if(last<(s.length()-1)){
									int value = Integer.parseInt(s.substring(s.lastIndexOf(FREE_COUNTER_SEPARATOR_CHAR)+1,s.length()));
									if(value > found) {
										found = value;
									}
								}
							}
						}	
					}
					if(found>0) {
						return found+1;
					}
				}
			}catch(NotFoundException notF){
				
			}
			return 1;*/
			
			FunctionField ff = new FunctionField(Allarme.model().NOME, Function.MAX, "maxAlarmId");
			Object maxValue = null;
			try {
				maxValue = jdbcServiceManager.getAllarmeServiceSearch().aggregate(pagExpr, ff);
			}catch(NotFoundException notFound) {
			}
			if(maxValue!=null){
				if(maxValue instanceof String){
					String s = (String)maxValue;
					if(s.contains(FREE_COUNTER_SEPARATOR_CHAR)){
						int last = s.lastIndexOf(FREE_COUNTER_SEPARATOR_CHAR);
						if(last<(s.length()-1)){
							String actualMaxValue = s.substring(s.lastIndexOf(FREE_COUNTER_SEPARATOR_CHAR)+1,s.length());
							return incrementAlarmInstanceSerialId(actualMaxValue);
						}
					}
				}	
			}
			return normalizeAlarmInstanceSerialId(1);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static List<Allarme> configurazioneAllarmiList(ISearch ricerca, RuoloPorta ruoloPorta, String nomePorta, 
			Connection con, Logger log, String tipoDB,
			String nomeMetodo, 
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws ServiceException{
		// ritorna la configurazione allarmi
		int idLista = Liste.CONFIGURAZIONE_ALLARMI;
		String search = null;
		int offset = 0;
		int limit =0 ;

		if(ricerca != null) {
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			limit = ricerca.getPageSize(idLista);
			offset = ricerca.getIndexIniziale(idLista);
		}
		if (limit == 0) // con limit
			limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
		
		log.debug("search : " + search);
		
		List<Allarme> listaAllarmi = new ArrayList<Allarme>();
		
		long count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if(ricerca!=null) {

				ISQLQueryObject sqlQueryObject = _prepareSqlQueryObjectAllarmi(tipoDB,ruoloPorta, nomePorta, search, 
						filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
						filtroSoggettoErogatore, filtroRuoloErogatore,
						filtroServizioAzione, filtroRuolo);
				sqlQueryObject.addSelectCountField("nome", "allarmi");
				String query = sqlQueryObject.createSQLQuery();
				pstmt = con.prepareStatement(query);
				_prepareStatementAllarmi(tipoDB,pstmt, ruoloPorta, nomePorta, search, 
						filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
						filtroSoggettoErogatore, filtroRuoloErogatore,
						filtroServizioAzione, filtroRuolo);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					count = rs.getLong("allarmi");
				}
				rs.close();
				pstmt.close();
				
			}
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager serviceManager = new JDBCServiceManager(con, properties, log);
			
			
			ISQLQueryObject sqlQueryObject = _prepareSqlQueryObjectAllarmi(tipoDB,ruoloPorta, nomePorta, search, 
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
			sqlQueryObject.addSelectField(CostantiDB.ALLARMI, "nome");
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);
			//sqlQueryObject.addOrderBy("policy_alias", true);
			//sqlQueryObject.addOrderBy("policy_id", true);
			sqlQueryObject.addOrderBy(CostantiDB.ALLARMI+".nome", true);
			String query = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(query);
			_prepareStatementAllarmi(tipoDB,pstmt, ruoloPorta, nomePorta, search, 
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				
				String nomeAllarme = rs.getString("nome");
				
				IdAllarme idAllarmeAsObject = new IdAllarme();
				idAllarmeAsObject.setNome(nomeAllarme);
				Allarme allarme = serviceManager.getAllarmeServiceSearch().get(idAllarmeAsObject);
				listaAllarmi.add(allarme);
				
			}
			rs.close();
			pstmt.close();
						
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} finally {
			try {
				if(rs!=null) {
					rs.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
		
		if(ricerca!=null) {
			ricerca.setNumEntries(idLista,(int) count);
		}
		
		return listaAllarmi;
		
	}
	private static ISQLQueryObject _prepareSqlQueryObjectAllarmi(
			String tipoDB,
			RuoloPorta ruoloPorta, String nomePorta,
			String search, 
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws Exception {
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
		if(ruoloPorta!=null && nomePorta!=null) {
			sqlQueryObject.addWhereCondition("filtro_ruolo=?");
			sqlQueryObject.addWhereCondition("filtro_porta=?");
		}
		else if(ruoloPorta!=null) {
			sqlQueryObject.addWhereCondition("filtro_ruolo=?"); // se passo solo ruolo porta, voglio tutte le policy con quel ruolo. Altrimenti finisco nelle policy globali
		}
		else {
			sqlQueryObject.addWhereIsNullCondition("filtro_porta");
		}
		if(search != null  && !"".equals(search)){
			// fix: oramai l'alias e' obbligatorio
			sqlQueryObject.addWhereCondition(false, 
					//sqlQueryObject.getWhereLikeCondition("active_policy_id", search, false, true, true),
					sqlQueryObject.getWhereLikeCondition("alias", search, false, true, true));
		}
		if(filtroSoggettoFruitore!=null) {
			if(filtroSoggettoFruitore.getTipo()!=null) {
				sqlQueryObject.addWhereCondition("filtro_tipo_fruitore=?");
			}
			if(filtroSoggettoFruitore.getNome()!=null) {
				sqlQueryObject.addWhereCondition("filtro_nome_fruitore=?");
			}
		}
		if(filtroApplicativoFruitore!=null) {
			if(filtroApplicativoFruitore.getIdSoggettoProprietario()!=null) {
				if(filtroApplicativoFruitore.getIdSoggettoProprietario().getTipo()!=null) {
					sqlQueryObject.addWhereCondition("filtro_tipo_fruitore=?");
				}
				if(filtroApplicativoFruitore.getIdSoggettoProprietario().getNome()!=null) {
					sqlQueryObject.addWhereCondition("filtro_nome_fruitore=?");
				}
			}
			if(filtroApplicativoFruitore.getNome()!=null) {
				sqlQueryObject.addWhereCondition("filtro_sa_fruitore=?");
			}
		}
		if(filtroRuoloFruitore!=null) {
			sqlQueryObject.addWhereCondition("filtro_ruolo_fruitore=?");
		}
		if(filtroSoggettoErogatore!=null) {
			if(filtroSoggettoErogatore.getTipo()!=null) {
				sqlQueryObject.addWhereCondition("filtro_tipo_erogatore=?");
			}
			if(filtroSoggettoErogatore.getNome()!=null) {
				sqlQueryObject.addWhereCondition("filtro_nome_erogatore=?");
			}
		}
		if(filtroRuoloErogatore!=null) {
			sqlQueryObject.addWhereCondition("filtro_ruolo_erogatore=?");
		}
		if(filtroServizioAzione!=null) {
			if(filtroServizioAzione.getSoggettoErogatore()!=null) {
				if(filtroServizioAzione.getSoggettoErogatore().getTipo()!=null) {
					sqlQueryObject.addWhereCondition("filtro_tipo_erogatore=?");
				}
				if(filtroServizioAzione.getSoggettoErogatore().getNome()!=null) {
					sqlQueryObject.addWhereCondition("filtro_nome_erogatore=?");
				}
			}
			if(filtroServizioAzione.getTipo()!=null) {
				sqlQueryObject.addWhereCondition("filtro_tipo_servizio=?");
			}
			if(filtroServizioAzione.getNome()!=null) {
				sqlQueryObject.addWhereCondition("filtro_nome_servizio=?");
			}
			if(filtroServizioAzione.getVersione()!=null) {
				sqlQueryObject.addWhereCondition("filtro_versione_servizio=?");
			}
			if(filtroServizioAzione.getAzione()!=null) {
				// condizione di controllo
				ISQLQueryObject sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObjectOr.setANDLogicOperator(false);
				// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
				// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
				sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", filtroServizioAzione.getAzione(), false , false);
				sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", filtroServizioAzione.getAzione()+",", LikeConfig.startsWith(false));
				sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+filtroServizioAzione.getAzione(), LikeConfig.endsWith(false));
				sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+filtroServizioAzione.getAzione()+",", true , false);
				sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
			}
		}
		if(filtroRuolo!=null) {
			sqlQueryObject.addWhereCondition(false, "filtro_ruolo_fruitore=?", "filtro_ruolo_erogatore=?");
		}
		return sqlQueryObject;
	}
	private static void _prepareStatementAllarmi(
			String tipoDB,
			PreparedStatement pstmt,
			RuoloPorta ruoloPorta, String nomePorta,
			String search, 
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws Exception {
		int index = 1;
		if(ruoloPorta!=null && nomePorta!=null) {
			pstmt.setString(index++, ruoloPorta.getValue());
			pstmt.setString(index++, nomePorta);
		}
		else if(ruoloPorta!=null) {
			pstmt.setString(index++, ruoloPorta.getValue());
		}
		if(filtroSoggettoFruitore!=null) {
			if(filtroSoggettoFruitore.getTipo()!=null) {
				pstmt.setString(index++, filtroSoggettoFruitore.getTipo());
			}
			if(filtroSoggettoFruitore.getNome()!=null) {
				pstmt.setString(index++, filtroSoggettoFruitore.getNome());
			}
		}
		if(filtroApplicativoFruitore!=null) {
			if(filtroApplicativoFruitore.getIdSoggettoProprietario()!=null) {
				if(filtroApplicativoFruitore.getIdSoggettoProprietario().getTipo()!=null) {
					pstmt.setString(index++, filtroApplicativoFruitore.getIdSoggettoProprietario().getTipo());
				}
				if(filtroApplicativoFruitore.getIdSoggettoProprietario().getNome()!=null) {
					pstmt.setString(index++, filtroApplicativoFruitore.getIdSoggettoProprietario().getNome());
				}
			}
			if(filtroApplicativoFruitore.getNome()!=null) {
				pstmt.setString(index++, filtroApplicativoFruitore.getNome());
			}
		}
		if(filtroRuoloFruitore!=null) {
			pstmt.setString(index++, filtroRuoloFruitore);
		}
		if(filtroSoggettoErogatore!=null) {
			if(filtroSoggettoErogatore.getTipo()!=null) {
				pstmt.setString(index++, filtroSoggettoErogatore.getTipo());
			}
			if(filtroSoggettoErogatore.getNome()!=null) {
				pstmt.setString(index++, filtroSoggettoErogatore.getNome());
			}
		}
		if(filtroRuoloErogatore!=null) {
			pstmt.setString(index++, filtroRuoloErogatore);
		}
		if(filtroServizioAzione!=null) {
			if(filtroServizioAzione.getSoggettoErogatore()!=null) {
				if(filtroServizioAzione.getSoggettoErogatore().getTipo()!=null) {
					pstmt.setString(index++, filtroServizioAzione.getSoggettoErogatore().getTipo());
				}
				if(filtroServizioAzione.getSoggettoErogatore().getNome()!=null) {
					pstmt.setString(index++, filtroServizioAzione.getSoggettoErogatore().getNome());
				}
			}
			if(filtroServizioAzione.getTipo()!=null) {
				pstmt.setString(index++, filtroServizioAzione.getTipo());
			}
			if(filtroServizioAzione.getNome()!=null) {
				pstmt.setString(index++, filtroServizioAzione.getNome());
			}
			if(filtroServizioAzione.getVersione()!=null) {
				pstmt.setInt(index++, filtroServizioAzione.getVersione());
			}
			if(filtroServizioAzione.getAzione()!=null) {
				// inserito direttamente nella preparazione
			}
		}
		if(filtroRuolo!=null) {
			pstmt.setString(index++, filtroRuolo);
			pstmt.setString(index++, filtroRuolo);
		}
	}
	
	
	public static List<Allarme> allarmiForPolicyRateLimiting(String activeIdPolicy, RuoloPorta ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "allarmiForPolicyRateLimiting";
				
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, properties, log);
			
			IAllarmeServiceSearch allarmiServiceSearch = jdbcServiceManager.getAllarmeServiceSearch();
			
			IExpression expr = allarmiServiceSearch.newExpression();
			
			expr.and();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(Allarme.model().FILTRO.RUOLO_PORTA, ruoloPorta.getValue()).and().equals(Allarme.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expr.isNull(Allarme.model().FILTRO.NOME_PORTA);
			}
			
			expr.equals(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO, CostantiConfigurazione.PARAM_POLICY_ID);
			if(activeIdPolicy!=null) {
				expr.like(Allarme.model().ALLARME_PARAMETRO.VALORE, activeIdPolicy,LikeMode.EXACT);
			}
			else {
				expr.isNull(Allarme.model().ALLARME_PARAMETRO.VALORE);
			}

			IPaginatedExpression pagExpr = allarmiServiceSearch.toPaginatedExpression(expr);
			
			pagExpr.addOrder(Allarme.model().ALIAS, SortOrder.ASC);

			return allarmiServiceSearch.findAll(pagExpr);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} 
	}
	
}
