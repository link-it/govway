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

package org.openspcoop2.core.controllo_traffico.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoApplicabilita;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.dao.IDBAttivazionePolicyServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IDBConfigurazionePolicyServiceSearch;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.JDBCParameterUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * ControlloTrafficoDriverUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ControlloTrafficoDriverUtils {

	public static ConfigurazioneGenerale getConfigurazioneControlloTraffico(Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException {
		String nomeMetodo = "getConfigurazioneControlloTraffico";
		// ritorna la configurazione controllo del traffico della PdD
		
		ConfigurazioneGenerale config = null;
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			try {
				config = serviceManager.getConfigurazioneGeneraleServiceSearch().get();
			}catch (NotFoundException e) {
				throw new NotFoundException("["+ nomeMetodo + "] Configurazione non presente.");
			}
		}catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} 

		return config;
	}

	/***
	 * Aggiorna la configurazione del controllo del traffico
	 * 
	 * @param configurazioneControlloTraffico
	 * @throws ServiceException
	 */
	public static void updateConfigurazioneControlloTraffico(ConfigurazioneGenerale configurazioneControlloTraffico, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "updateConfigurazioneControlloTraffico";
		
		try {

			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			serviceManager.getConfigurazioneGeneraleService().update(configurazioneControlloTraffico);
		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		}
		
	}
	
	/**
	 * Restituisce il numero di Configurazione Policy
	 * 
	 * @return Configurazione
	 * 
	 */
	public static long countConfigurazioneControlloTrafficoConfigurazionePolicy(ISearch ricerca, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "countConfigurazioneControlloTrafficoConfigurazionePolicy";
		// ritorna la configurazione controllo del traffico della PdD
		int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY;
		String search = null;
		String filterTipoPolicy = null;
		if(ricerca != null) {
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
	
			filterTipoPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_POLICY);
			
			log.debug("search : " + search);
			log.debug("filterTipoPolicy : " + filterTipoPolicy);
		}

		long count = 0;
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getConfigurazionePolicyServiceSearch().newExpression();
			
			if(Filtri.FILTRO_TIPO_POLICY_BUILT_IN.equals(filterTipoPolicy)) {
				expr.equals(ConfigurazionePolicy.model().BUILT_IN, true);
			}
			else if(Filtri.FILTRO_TIPO_POLICY_UTENTE.equals(filterTipoPolicy)) {
				expr.equals(ConfigurazionePolicy.model().BUILT_IN, false);
			}
			
			if(search != null  && !"".equals(search)){
				expr.ilike(ConfigurazionePolicy.model().ID_POLICY, search, LikeMode.ANYWHERE);
			}
			
			NonNegativeNumber nnn = serviceManager.getConfigurazionePolicyServiceSearch().count(expr);
			
			count = nnn != null ? nnn.longValue() : 0;
		}catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} 

		return count;
	}
	
	/**
	 * Restituisce il numero di Attivazione Policy
	 * 
	 * @return Configurazione
	 * 
	 */
	public static long countConfigurazioneControlloTrafficoAttivazionePolicy(ISearch ricerca, RuoloPolicy ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "countConfigurazioneControlloTrafficoAttivazionePolicy";
		// ritorna la configurazione controllo del traffico della PdD
		int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
		String search = null;

		if(ricerca != null) {
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		}

		long count = 0;
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expr.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
			}
			
			if(search != null  && !"".equals(search)){
				expr.ilike(AttivazionePolicy.model().ID_POLICY, search, LikeMode.ANYWHERE);
			}
			NonNegativeNumber nnn = serviceManager.getAttivazionePolicyServiceSearch().count(expr);
			
			count = nnn != null ? nnn.longValue() : 0;
		}catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} 

		return count;
	}

	public static List<ConfigurazionePolicy> configurazioneControlloTrafficoConfigurazionePolicyList(ISearch ricerca, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "configurazioneControlloTrafficoConfigurazionePolicyList";
		// ritorna la configurazione controllo del traffico della PdD
		int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY;
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
		
		String filterTipoPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_POLICY);
		
		log.debug("search : " + search);
		log.debug("filterTipoPolicy : " + filterTipoPolicy);
		
		List<ConfigurazionePolicy> listaPolicy = new ArrayList<ConfigurazionePolicy>();
		
		long count = 0;
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getConfigurazionePolicyServiceSearch().newExpression();
			
			if(Filtri.FILTRO_TIPO_POLICY_BUILT_IN.equals(filterTipoPolicy)) {
				expr.equals(ConfigurazionePolicy.model().BUILT_IN, true);
			}
			else if(Filtri.FILTRO_TIPO_POLICY_UTENTE.equals(filterTipoPolicy)) {
				expr.equals(ConfigurazionePolicy.model().BUILT_IN, false);
			}
			
			if(search != null  && !"".equals(search)){
				expr.ilike(ConfigurazionePolicy.model().ID_POLICY, search, LikeMode.ANYWHERE);
			}
			
			if(ricerca!=null) {
				NonNegativeNumber nn = serviceManager.getConfigurazionePolicyServiceSearch().count(expr);
				if(nn!=null) {
					count = nn.longValue();
				}
			}
			
			IPaginatedExpression pagExpr = serviceManager.getConfigurazionePolicyServiceSearch().toPaginatedExpression(expr);
			pagExpr.offset(offset).limit(limit);
			pagExpr.sortOrder(SortOrder.ASC);
			pagExpr.addOrder(ConfigurazionePolicy.model().BUILT_IN);
			pagExpr.addOrder(ConfigurazionePolicy.model().ID_POLICY);
			
			listaPolicy = serviceManager.getConfigurazionePolicyServiceSearch().findAll(pagExpr);
						
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
		
		if(ricerca!=null) {
			ricerca.setNumEntries(idLista,(int)count);
		}
		
		return listaPolicy;
	}

	@SuppressWarnings("unchecked")
	public static List<AttivazionePolicy> configurazioneControlloTrafficoAttivazionePolicyList(ISearch ricerca, RuoloPolicy ruoloPorta, String nomePorta, 
			Connection con, Logger log, String tipoDB) throws ServiceException{
		return (List<AttivazionePolicy>) _configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta,
				con, log, tipoDB,
				false, "configurazioneControlloTrafficoAttivazionePolicyList",
				null, null, null,
				null, null,
				null, null);
	}
	@SuppressWarnings("unchecked")
	public static List<AttivazionePolicy> configurazioneControlloTrafficoAttivazionePolicyListByFilter(ISearch ricerca, RuoloPolicy ruoloPorta, String nomePorta, 
			Connection con, Logger log, String tipoDB,
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws ServiceException{
		return (List<AttivazionePolicy>) _configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta, 
				con, log, tipoDB,
				false, "configurazioneControlloTrafficoAttivazionePolicyListByFilter",
				filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
				filtroSoggettoErogatore, filtroRuoloErogatore,
				filtroServizioAzione, filtroRuolo);
	}
	@SuppressWarnings("unchecked")
	public static List<TipoRisorsaPolicyAttiva> configurazioneControlloTrafficoAttivazionePolicyTipoRisorsaList(ISearch ricerca, RuoloPolicy ruoloPorta, String nomePorta, 
			Connection con, Logger log, String tipoDB) throws ServiceException{
		return (List<TipoRisorsaPolicyAttiva>) _configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta, 
				con, log, tipoDB,
				true, "configurazioneControlloTrafficoAttivazionePolicyTipoRisorsaList",
				null, null, null,
				null, null,
				null, null);
	}
	public static Object _configurazioneControlloTrafficoAttivazionePolicyList(ISearch ricerca, RuoloPolicy ruoloPorta, String nomePorta, 
			Connection con, Logger log, String tipoDB,
			boolean tipoRisorsa, String nomeMetodo, 
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws ServiceException{
		// ritorna la configurazione controllo del traffico della PdD
		int idLista = Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY;
		String search = null;
		int offset = 0;
		int limit =0 ;

		if(ricerca != null) {
			if(!tipoRisorsa) {
				search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			}
			limit = ricerca.getPageSize(idLista);
			offset = ricerca.getIndexIniziale(idLista);
		}
		if (limit == 0) // con limit
			limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
		
		TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = null;
		if(!tipoRisorsa && ricerca != null) {
			String filterTipoRisorsaPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_RISORSA_POLICY);
			if(filterTipoRisorsaPolicy!=null && !"".equals(filterTipoRisorsaPolicy)) {
				try {
					tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.toEnumConstant(filterTipoRisorsaPolicy, true);
				}catch (Exception e) {
					throw new ServiceException(e.getMessage(),e);
				}
			}
		}
		
		log.debug("search : " + search);
		log.debug("filterTipoRisorsaPolicy : " + tipoRisorsaPolicyAttiva);
		
		List<AttivazionePolicy> listaPolicy = new ArrayList<AttivazionePolicy>();
		List<TipoRisorsaPolicyAttiva> listaTipoRisorsa = new ArrayList<>();
		
		long count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if(ricerca!=null) {

				ISQLQueryObject sqlQueryObject = _prepareSqlQueryObjectPolicyAttive(tipoDB,ruoloPorta, nomePorta, search, tipoRisorsaPolicyAttiva,
						filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
						filtroSoggettoErogatore, filtroRuoloErogatore,
						filtroServizioAzione, filtroRuolo);
				sqlQueryObject.addSelectCountField("active_policy_id", "policies");
				String query = sqlQueryObject.createSQLQuery();
				pstmt = con.prepareStatement(query);
				_prepareStatementPolicyAttive(tipoDB,pstmt, ruoloPorta, nomePorta, search, tipoRisorsaPolicyAttiva,
						filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
						filtroSoggettoErogatore, filtroRuoloErogatore,
						filtroServizioAzione, filtroRuolo);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					count = rs.getLong("policies");
				}
				rs.close();
				pstmt.close();
				
			}
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			
			ISQLQueryObject sqlQueryObject = _prepareSqlQueryObjectPolicyAttive(tipoDB,ruoloPorta, nomePorta, search, tipoRisorsaPolicyAttiva,
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
			sqlQueryObject.addSelectField(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY, "active_policy_id");
			sqlQueryObject.addSelectField(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY, "policy_posizione");
			sqlQueryObject.addSelectField(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY, "rt_risorsa");
			sqlQueryObject.addSelectField(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY, "rt_simultanee");
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);
			//sqlQueryObject.addOrderBy("policy_alias", true);
			//sqlQueryObject.addOrderBy("policy_id", true);
			sqlQueryObject.addOrderBy(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY+".rt_risorsa", true);
			sqlQueryObject.addOrderBy(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY+".rt_simultanee", true);
			sqlQueryObject.addOrderBy(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".policy_posizione", true);
			String query = sqlQueryObject.createSQLQuery();
			pstmt = con.prepareStatement(query);
			_prepareStatementPolicyAttive(tipoDB,pstmt, ruoloPorta, nomePorta, search, tipoRisorsaPolicyAttiva,
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				
				if(tipoRisorsa) {
					
					String risorsa = rs.getString("rt_risorsa");
					JDBCParameterUtilities utils = new JDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDB));
					boolean simultanee = utils.readBooleanParameter(rs, "rt_simultanee");
					TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttivaActivePolicy =  TipoRisorsaPolicyAttiva.getTipo(risorsa, simultanee);
					if(listaTipoRisorsa.contains(tipoRisorsaPolicyAttivaActivePolicy)==false) {
						listaTipoRisorsa.add(tipoRisorsaPolicyAttivaActivePolicy);
					}
					
				}
				else {
				
					String idActivePolicy = rs.getString("active_policy_id");
					
					IdActivePolicy idActivePolicyAsObject = new IdActivePolicy();
					idActivePolicyAsObject.setNome(idActivePolicy);
					AttivazionePolicy policy = serviceManager.getAttivazionePolicyServiceSearch().get(idActivePolicyAsObject);
					listaPolicy.add(policy);
					
				}
				
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
		
		if(tipoRisorsa) {
			return listaTipoRisorsa;
		}
		else {
			return listaPolicy;
		}
	}
	private static ISQLQueryObject _prepareSqlQueryObjectPolicyAttive(
			String tipoDB,
			RuoloPolicy ruoloPorta, String nomePorta,
			String search, TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva,
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo) throws Exception {
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
		sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY);
		sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".policy_id="+CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY+".policy_id");
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
					sqlQueryObject.getWhereLikeCondition("policy_alias", search, false, true, true));
		}
		if(tipoRisorsaPolicyAttiva!=null) {
			sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY+".rt_risorsa=?");
			sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_CONFIG_POLICY+".rt_simultanee=?");
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
	private static void _prepareStatementPolicyAttive(
			String tipoDB,
			PreparedStatement pstmt,
			RuoloPolicy ruoloPorta, String nomePorta,
			String search, TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva,
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
		if(tipoRisorsaPolicyAttiva!=null) {
			JDBCParameterUtilities utils = new JDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDB));
			if(TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE.equals(tipoRisorsaPolicyAttiva)) {
				pstmt.setString(index++, TipoRisorsa.NUMERO_RICHIESTE.getValue());
				utils.setParameter(pstmt, index++, true, boolean.class);
			}
			else {
				pstmt.setString(index++, tipoRisorsaPolicyAttiva.getValue());
				utils.setParameter(pstmt, index++, false, boolean.class);
			}
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
	
	public static AttivazionePolicy getAttivazionePolicy(String alias, RuoloPolicy ruoloPorta, String nomePorta,Connection con, Logger log, String tipoDB) throws ServiceException, NotFoundException{
		String nomeMetodo = "configurazioneControlloTrafficoAttivazionePolicyList";
		// ritorna la configurazione controllo del traffico della PdD
		
		AttivazionePolicy policy = null;
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
			expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
			expr.equals(AttivazionePolicy.model().ALIAS, alias);
						
			policy = serviceManager.getAttivazionePolicyServiceSearch().find(expr);
			if(policy==null) {
				throw new NotFoundException("Not Found");
			}
		} catch(NotFoundException notFound) {
			throw notFound;
		} 
		catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
		
		return policy;
	}
	
	
	public static List<InfoPolicy> getInfoPolicyList(Boolean builtIn, String idPolicyParam,Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "getInfoPolicyList";
		// ritorna la configurazione controllo del traffico della PdD
		int offset = 0;
		int limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
		
		List<InfoPolicy> listaPolicy = new ArrayList<InfoPolicy>();
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getConfigurazionePolicyServiceSearch().newExpression();
			
			if(idPolicyParam != null  && !"".equals(idPolicyParam)){
				expr.equals(ConfigurazionePolicy.model().ID_POLICY, idPolicyParam);
			}
			if(builtIn!=null) {
				expr.equals(ConfigurazionePolicy.model().BUILT_IN, builtIn);
			}
			
			IPaginatedExpression pagExpr = serviceManager.getConfigurazionePolicyServiceSearch().toPaginatedExpression(expr);
			pagExpr.offset(offset).limit(limit);
			pagExpr.sortOrder(SortOrder.ASC);
			pagExpr.addOrder(ConfigurazionePolicy.model().ID_POLICY);
			
			List<java.util.Map<String, Object>> mapList = null;
			try{
				mapList = serviceManager.getConfigurazionePolicyServiceSearch().select(pagExpr, 
						ConfigurazionePolicy.model().ID_POLICY,
						ConfigurazionePolicy.model().RISORSA,
						ConfigurazionePolicy.model().BUILT_IN,
						ConfigurazionePolicy.model().DESCRIZIONE,
						ConfigurazionePolicy.model().VALORE,
						ConfigurazionePolicy.model().SIMULTANEE,
						ConfigurazionePolicy.model().MODALITA_CONTROLLO,
						ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME,
						ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO,
						ConfigurazionePolicy.model().TIPO_APPLICABILITA,
						ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE,
						ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE,
						ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO
						);
			}catch(NotFoundException notFound){}
			if(mapList!=null){
				// caso get puntuale
				if(idPolicyParam != null  && !"".equals(idPolicyParam) && mapList.size()>1){
					throw new Exception("More than one results ("+mapList.size()+")");
				}
				
				
				for (java.util.Map<String, Object> map : mapList) {
					String idPolicy = (String) map.get(ConfigurazionePolicy.model().ID_POLICY.getFieldName());
					String resource = (String) map.get(ConfigurazionePolicy.model().RISORSA.getFieldName());
					InfoPolicy info = new InfoPolicy();
					info.setIdPolicy(idPolicy);
					info.setTipoRisorsa(TipoRisorsa.toEnumConstant(resource, true));
					
					Object builtInValue =  map.get(ConfigurazionePolicy.model().BUILT_IN.getFieldName());
					if(builtInValue!=null && builtInValue instanceof Boolean){
						info.setBuiltIn((Boolean)builtInValue);
					}
					
					Object descr =  map.get(ConfigurazionePolicy.model().DESCRIZIONE.getFieldName());
					if(descr!=null && descr instanceof String){
						info.setDescrizione((String)descr);
					}
					
					Object v =  map.get(ConfigurazionePolicy.model().VALORE.getFieldName());
					if(v!=null && v instanceof Long){
						info.setValore((Long)v);
					}
					
					Object simultanee =  map.get(ConfigurazionePolicy.model().SIMULTANEE.getFieldName());
					if(simultanee!=null && simultanee instanceof Boolean){
						boolean simultaneeBoolean = (Boolean) simultanee;
						info.setCheckRichiesteSimultanee(simultaneeBoolean);
						if(!simultaneeBoolean){
							Object controllo =  map.get(ConfigurazionePolicy.model().MODALITA_CONTROLLO.getFieldName());
                        	if(controllo!=null && controllo instanceof String){
                        		TipoControlloPeriodo tipo = TipoControlloPeriodo.toEnumConstant((String)controllo, true);
                        		info.setIntervalloUtilizzaRisorseStatistiche(TipoControlloPeriodo.STATISTIC.equals(tipo));
                        		info.setIntervalloUtilizzaRisorseRealtime(TipoControlloPeriodo.REALTIME.equals(tipo));
                        		if(info.isIntervalloUtilizzaRisorseStatistiche()) {
                        			Object tipoPeriodo =  map.get(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO.getFieldName());
                        			if(tipoPeriodo!=null) {
                        				info.setIntervalloUtilizzaRisorseStatisticheTipoPeriodo(TipoPeriodoStatistico.toEnumConstant((String)tipoPeriodo,true));
                        			}
                        		}
                        		if(info.isIntervalloUtilizzaRisorseRealtime()) {
                        			Object tipoPeriodo =  map.get(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME.getFieldName());
                        			if(tipoPeriodo!=null) {
                        				info.setIntervalloUtilizzaRisorseRealtimeTipoPeriodo(TipoPeriodoRealtime.toEnumConstant((String)tipoPeriodo,true));
                        			}
                        		}
                        	}
						}
					}
										
						
					// verifico anche degrado prestazionale
                    Object app =  map.get(ConfigurazionePolicy.model().TIPO_APPLICABILITA.getFieldName());
                    if(app!=null && app instanceof String){
                        TipoApplicabilita tipoApplicabilita = TipoApplicabilita.toEnumConstant((String)app, true);
                        if(TipoApplicabilita.CONDIZIONALE.equals(tipoApplicabilita)){
                        	
                        	Object enabledCongestione =  map.get(ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE.getFieldName());
                            boolean congestione = false;   
                            if(enabledCongestione!=null && (enabledCongestione instanceof Boolean)){
                            	congestione = (Boolean)enabledCongestione;
                            }
                            if(congestione){
                            	info.setControlloCongestione(true);
                            }
                        	
                        	Object enabled =  map.get(ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE.getFieldName());
                            boolean degrado = false;   
                            if(enabled!=null && (enabled instanceof Boolean)){
                            	degrado = (Boolean)enabled;
                            }
                            if(degrado){
                            	Object controllo =  map.get(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO.getFieldName());
                            	if(controllo!=null && controllo instanceof String){
                            		TipoControlloPeriodo tipo = TipoControlloPeriodo.toEnumConstant((String)controllo, true);
                            		info.setDegradoPrestazionaleUtilizzaRisorseStatistiche(TipoControlloPeriodo.STATISTIC.equals(tipo));
                            		info.setDegradoPrestazionaleUtilizzaRisorseRealtime(TipoControlloPeriodo.REALTIME.equals(tipo));
                            		info.setDegradoPrestazione(true);
                            	}
                            }
                        }
                    }

					
                    listaPolicy.add(info);
				}
			}
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
		
		return listaPolicy;
	}
	
	public static List<AttivazionePolicy> findInUseAttivazioni(String idPolicy, boolean escludiDisabilitate, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "findInUseAttivazioni";
		// ritorna la configurazione controllo del traffico della PdD
		int offset = 0;
		int limit = 10000 ;  // valore altissimo che non dovrebbe accadare
		
		
		List<AttivazionePolicy> listaPolicy = new ArrayList<AttivazionePolicy>();
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			expr.equals(AttivazionePolicy.model().ID_POLICY, idPolicy);
			if(escludiDisabilitate) {
				expr.equals(AttivazionePolicy.model().ENABLED, true);
			}
		
			IPaginatedExpression pagExpr = serviceManager.getAttivazionePolicyServiceSearch().toPaginatedExpression(expr);
			pagExpr.offset(offset).limit(limit);
			pagExpr.sortOrder(SortOrder.ASC);
			pagExpr.addOrder(AttivazionePolicy.model().ALIAS);
			pagExpr.addOrder(AttivazionePolicy.model().ID_POLICY);
			
			listaPolicy = serviceManager.getAttivazionePolicyServiceSearch().findAll(pagExpr);
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
		
		return listaPolicy;
	}
	
	public static long countInUseAttivazioni(String idPolicy, boolean escludiDisabilitate, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "countInUseAttivazioni";
		// ritorna la configurazione controllo del traffico della PdD
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			expr.equals(AttivazionePolicy.model().ID_POLICY, idPolicy);
			if(escludiDisabilitate) {
				expr.equals(AttivazionePolicy.model().ENABLED, true);
			}
			
			NonNegativeNumber nnn = serviceManager.getAttivazionePolicyServiceSearch().count(expr);
			return nnn != null ? nnn.longValue(): 0;
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static ConfigurazionePolicy getConfigurazionePolicy(long id, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getConfigurazionePolicy";

		ConfigurazionePolicy policy = null;
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			policy = ((IDBConfigurazionePolicyServiceSearch)serviceManager.getConfigurazionePolicyServiceSearch()).get(id);
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
		
		return policy;
	}
	
	public static ConfigurazionePolicy getConfigurazionePolicy(String nomePolicy, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getConfigurazionePolicy";
		
		ConfigurazionePolicy policy = null;
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IdPolicy id = new IdPolicy();
			id.setNome(nomePolicy);
			policy = serviceManager.getConfigurazionePolicyServiceSearch().get(id);
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
		
		return policy;
	}

	public static void createConfigurazionePolicy(ConfigurazionePolicy policy, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "createConfigurazionePolicy";
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			serviceManager.getConfigurazionePolicyService().create(policy); 
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}

	public static void createAttivazionePolicy(AttivazionePolicy policy, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "createAttivazionePolicy";

		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			serviceManager.getAttivazionePolicyService().create(policy);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}

	public static void updateConfigurazionePolicy(ConfigurazionePolicy policy, Connection con, Logger log, String tipoDB)throws ServiceException,NotFoundException{
		String nomeMetodo = "updateConfigurazionePolicy";
		
		try {

			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IdPolicy idToUpdate = new IdPolicy();
			
			if(policy.getOldIdPolicy()!=null){
				idToUpdate.setNome(policy.getOldIdPolicy().getNome());
			}
			else{
				idToUpdate.setNome(policy.getIdPolicy());
			}
			
			// ElencoPolicyModificareInSeguitoModificaSogliaPolicy
			List<AttivazionePolicy> listPolicyAttiveConStatoDisabilitato = findInUseAttivazioni(idToUpdate.getNome(), true, con, log, tipoDB);
			
			if(listPolicyAttiveConStatoDisabilitato !=null && listPolicyAttiveConStatoDisabilitato.size()>0) {
				//System.out.println("UPDATEEEEEEE SOGLIA ("+listPolicyAttiveConStatoDisabilitato.size()+") DISABILITO");
				Hashtable<String, Boolean> mapStati = new Hashtable<String, Boolean>();
				// NOTA: Devo fare la dobbia modifica perche' c'e' un controllo sul livello db layer che verifica se ci sono state modifiche
				for (AttivazionePolicy ap : listPolicyAttiveConStatoDisabilitato) {
					IdActivePolicy idActivePolicy = new IdActivePolicy();
					idActivePolicy.setNome(ap.getIdActivePolicy());
					mapStati.put(ap.getIdActivePolicy(), ap.isEnabled());
					//System.out.println("SALVATO ["+ap.getIdActivePolicy()+"] = ["+ap.isEnabled()+"]");
					ap.setEnabled(false);
					//System.out.println("UPDATEEEEEEE SOGLIA ("+ap.getIdActivePolicy()+") CON STATO=FALSE");
					serviceManager.getAttivazionePolicyService().update(idActivePolicy,ap);
				}
				
				for (AttivazionePolicy ap : listPolicyAttiveConStatoDisabilitato) {
					IdActivePolicy idActivePolicy = new IdActivePolicy();
					idActivePolicy.setNome(ap.getIdActivePolicy());
					//System.out.println("RIPRISTINO ["+ap.getIdActivePolicy()+"] = ["+mapStati.get(ap.getIdActivePolicy())+"]");
					ap.setEnabled(mapStati.get(ap.getIdActivePolicy()));
					//System.out.println("UPDATE SOGLIA ("+ap.getIdActivePolicy()+") CON STATO=true");
					serviceManager.getAttivazionePolicyService().update(idActivePolicy,ap);
				}
			}
			
			serviceManager.getConfigurazionePolicyService().update(idToUpdate, policy); 
			
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}

	public static void updateAttivazionePolicy(AttivazionePolicy policy, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "updateAttivazionePolicy";
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IdActivePolicy id = new IdActivePolicy();
			id.setNome(policy.getIdActivePolicy()); 
			serviceManager.getAttivazionePolicyService().update(id, policy); 
			
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}

	public static void deleteConfigurazionePolicy(ConfigurazionePolicy policy, Connection con, Logger log, String tipoDB) throws ServiceException {
		String nomeMetodo = "deleteConfigurazionePolicy";
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			serviceManager.getConfigurazionePolicyService().delete(policy); 
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}

	public static void deleteAttivazionePolicy(AttivazionePolicy policy, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "deleteAttivazionePolicy";
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			serviceManager.getAttivazionePolicyService().delete(policy);
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static AttivazionePolicy getAttivazionePolicy(long id, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getAttivazionePolicy"; 
		
		AttivazionePolicy policy = null;
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			policy = ((IDBAttivazionePolicyServiceSearch)serviceManager.getAttivazionePolicyServiceSearch()).get(id);
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
		
		return policy;
	}
	
	public static AttivazionePolicy getAttivazionePolicy(String nomePolicy, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getAttivazionePolicy"; 
		
		AttivazionePolicy policy = null;
		
		try {
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			IdActivePolicy idPolicy = new IdActivePolicy();
			idPolicy.setNome(nomePolicy);
			policy = serviceManager.getAttivazionePolicyServiceSearch().get(idPolicy);
			
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Configurazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
		
		return policy;
	}
	
	public static Integer getFreeCounterForGlobalPolicy(String policyId, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "getFreeCounterForGlobalPolicy"; 
		
		
		try{
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IPaginatedExpression pagExpr = serviceManager.getAttivazionePolicyServiceSearch().newPaginatedExpression();
			pagExpr.and();
			pagExpr.equals(AttivazionePolicy.model().ID_POLICY, policyId);
			
			// non funziona perchè :10 viene ordinato prima di :9
			//pagExpr.addOrder(AttivazionePolicy.model().ID_ACTIVE_POLICY, SortOrder.DESC);
			//pagExpr.limit(1);
			// devo scorrerle tutte
			
			try{
				List<Object> list = serviceManager.getAttivazionePolicyServiceSearch().select(pagExpr, AttivazionePolicy.model().ID_ACTIVE_POLICY);
				if(list!=null && list.size()>0){
					int found = -1;
					for (Object r : list) {
						if(r instanceof String){
							String s = (String)r;
							if(s.contains(":")){
								int last = s.lastIndexOf(":");
								if(last<(s.length()-1)){
									int value = Integer.parseInt(s.substring(s.lastIndexOf(":")+1,s.length()));
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
			return 1;
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
	}
	
	public static AttivazionePolicy getPolicy(String policyId, AttivazionePolicyFiltro filtroParam, AttivazionePolicyRaggruppamento groupBy,
			RuoloPolicy ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getPolicy"; 
		
		try{
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			IExpression expression = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			expression.and();
			expression.equals(AttivazionePolicy.model().ID_POLICY, policyId);
			
			AttivazionePolicyFiltro filtro = filtroParam;
			if(ruoloPorta!=null && nomePorta!=null) {
				filtro.setEnabled(true);
				filtro.setRuoloPorta(ruoloPorta);
				filtro.setNomePorta(nomePorta);
			}
			else {
				expression.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
			}
			
			expression.equals(AttivazionePolicy.model().FILTRO.ENABLED, filtro.isEnabled());
			if(filtro.isEnabled()){
				if(filtro.getRuoloPorta()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, filtro.getRuoloPorta());
				}
				else{
					expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, RuoloPolicy.ENTRAMBI);
				}
				
				if(filtro.getNomePorta()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, filtro.getNomePorta());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
				}
					
				if(filtro.getProtocollo()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.PROTOCOLLO, filtro.getProtocollo());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.PROTOCOLLO);
				}
				
				if(filtro.getRuoloErogatore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE, filtro.getRuoloErogatore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE);
				}
				
				if(filtro.getTipoErogatore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE, filtro.getTipoErogatore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE);
				}
				if(filtro.getNomeErogatore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.NOME_EROGATORE, filtro.getNomeErogatore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.NOME_EROGATORE);
				}
								
				if(filtro.getTipoServizio()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO, filtro.getTipoServizio());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO);
				}
				if(filtro.getNomeServizio()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO, filtro.getNomeServizio());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO);
				}
				if(filtro.getVersioneServizio()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO, filtro.getVersioneServizio());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO);
				}
				
				if(filtro.getAzione()!=null && !"".equals(filtro.getAzione())){
					expression.like(AttivazionePolicy.model().FILTRO.AZIONE, filtro.getAzione(), LikeMode.EXACT); // Colonna CLOB
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.AZIONE);
				}
				
				if(filtro.getServizioApplicativoErogatore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE, filtro.getServizioApplicativoErogatore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE);
				}
				
				if(filtro.getRuoloFruitore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE, filtro.getRuoloFruitore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE);
				}
				
				if(filtro.getTipoFruitore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE, filtro.getTipoFruitore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE);
				}
				if(filtro.getNomeFruitore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.NOME_FRUITORE, filtro.getNomeFruitore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.NOME_FRUITORE);
				}
				
				if(filtro.getServizioApplicativoFruitore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE, filtro.getServizioApplicativoFruitore());
				}
				else{
					expression.isNull(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE);
				}
				
				expression.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED, filtro.isInformazioneApplicativaEnabled());
				if(filtro.isInformazioneApplicativaEnabled() &&
						filtro.getInformazioneApplicativaTipo()!=null &&
						filtro.getInformazioneApplicativaNome()!=null &&
						filtro.getInformazioneApplicativaValore()!=null){
					expression.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO, filtro.getInformazioneApplicativaTipo());
					expression.like(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME, filtro.getInformazioneApplicativaNome(), LikeMode.EXACT); // Colonna CLOB
					expression.like(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE, filtro.getInformazioneApplicativaValore(), LikeMode.EXACT);  // Colonna CLOB
				}
			}
			
			expression.equals(AttivazionePolicy.model().GROUP_BY.ENABLED, groupBy.isEnabled());
			if(groupBy.isEnabled()){
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA, groupBy.isRuoloPorta());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.PROTOCOLLO, groupBy.isProtocollo());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.EROGATORE, groupBy.isErogatore());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO, groupBy.isServizio());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.AZIONE, groupBy.isAzione());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE, groupBy.isServizioApplicativoErogatore());
					
				expression.equals(AttivazionePolicy.model().GROUP_BY.FRUITORE, groupBy.isFruitore());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE, groupBy.isServizioApplicativoFruitore());
				
				expression.equals(AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO, groupBy.isIdentificativoAutenticato());
				
				if(groupBy.getToken()==null) {
					expression.isNull(AttivazionePolicy.model().GROUP_BY.TOKEN);	
				}
				else {
					expression.like(AttivazionePolicy.model().GROUP_BY.TOKEN, groupBy.getToken(), LikeMode.EXACT); // Colonna CLOB
				}

				expression.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED, groupBy.isInformazioneApplicativaEnabled());
				if(groupBy.isInformazioneApplicativaEnabled() &&
						groupBy.getInformazioneApplicativaTipo()!=null &&
						groupBy.getInformazioneApplicativaNome()!=null){
					expression.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO, groupBy.getInformazioneApplicativaTipo());
					expression.like(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME, groupBy.getInformazioneApplicativaNome(), LikeMode.EXACT); // Colonna CLOB
				}
			}
			
			return serviceManager.getAttivazionePolicyServiceSearch().find(expression);
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Attivazione Policy non presente.");	
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static AttivazionePolicy getPolicyByAlias(String alias,
			RuoloPolicy ruoloPorta, String nomePorta, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getPolicyByAlias"; 
		
		try{
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			IExpression expression = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			expression.and();
			expression.ilike(AttivazionePolicy.model().ALIAS, alias, LikeMode.EXACT);
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expression.equals(AttivazionePolicy.model().FILTRO.ENABLED, true);
				expression.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expression.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				expression.isNull(AttivazionePolicy.model().FILTRO.NOME_PORTA);
			}
		
			return serviceManager.getAttivazionePolicyServiceSearch().find(expression);
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Attivazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static List<AttivazionePolicy> getPolicyByServizioApplicativo(IDServizioApplicativo idServizioApplicativo, Connection con, Logger log, String tipoDB) throws ServiceException,NotFoundException{
		String nomeMetodo = "getPolicyByServizioApplicativo"; 
		
		try{
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			IPaginatedExpression expression = serviceManager.getAttivazionePolicyServiceSearch().newPaginatedExpression();

			expression.limit(100000); // non ne esisteranno mai cosi tante.
			
			expression.or();
			
			Map<IField, Object> propertyNameValuesFruitore = new HashMap<IField, Object>();
			propertyNameValuesFruitore.put(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
			propertyNameValuesFruitore.put(AttivazionePolicy.model().FILTRO.NOME_FRUITORE, idServizioApplicativo.getIdSoggettoProprietario().getNome());
			propertyNameValuesFruitore.put(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE, idServizioApplicativo.getNome());
			expression.allEquals(propertyNameValuesFruitore, true);
			
			Map<IField, Object> propertyNameValuesErogatore = new HashMap<IField, Object>();
			propertyNameValuesErogatore.put(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
			propertyNameValuesErogatore.put(AttivazionePolicy.model().FILTRO.NOME_EROGATORE, idServizioApplicativo.getIdSoggettoProprietario().getNome());
			propertyNameValuesErogatore.put(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE, idServizioApplicativo.getNome());
			expression.allEquals(propertyNameValuesErogatore, true);
			
			List<AttivazionePolicy> l = serviceManager.getAttivazionePolicyServiceSearch().findAll(expression);
			if(l==null || l.isEmpty()) {
				throw new NotFoundException("Non presenti");
			}
			return l;
			
		}catch (NotFoundException e) {
			throw new NotFoundException("[" + nomeMetodo + "] Attivazione Policy non presente.");
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		} 
	}
	
	public static boolean usedInConfigurazioneControlloTrafficoAttivazionePolicy(RuoloPolicy ruoloPorta, String nomePorta, String azione, Connection con, Logger log, String tipoDB) throws ServiceException{
		String nomeMetodo = "usedInConfigurazioneControlloTrafficoAttivazionePolicy";
		// ritorna la configurazione controllo del traffico della PdD
		
		
		try {
			
			ServiceManagerProperties properties = new ServiceManagerProperties();
			properties.setDatabaseType(tipoDB);
			properties.setShowSql(true);
			org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager serviceManager = new org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager(con, properties, log);
			
			IExpression expr = serviceManager.getAttivazionePolicyServiceSearch().newExpression();
			
			if(ruoloPorta!=null && nomePorta!=null) {
				expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
				expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
			}
			else {
				//throw new Exception("Metodo non invocabile senza il parametro nomePorta e ruoloPorta");
				// si viene usato per il check delle azioni rimosse a livello API
			}
			
			//expr.equals(AttivazionePolicy.model().FILTRO.AZIONE, azione); // e' diventata una lista
			expr.like(AttivazionePolicy.model().FILTRO.AZIONE, azione, LikeMode.ANYWHERE);
			
			IPaginatedExpression pagExpr = serviceManager.getAttivazionePolicyServiceSearch().toPaginatedExpression(expr);
			pagExpr.offset(0).limit(1000); // per un controllo di presenza per l'azione è sufficiente
			pagExpr.sortOrder(SortOrder.ASC);
			pagExpr.addOrder(AttivazionePolicy.model().ALIAS);
			pagExpr.addOrder(AttivazionePolicy.model().ID_POLICY);
			
			List<AttivazionePolicy> list = serviceManager.getAttivazionePolicyServiceSearch().findAll(pagExpr);
			if(list==null || list.isEmpty()) {
				return false;
			}
			else {
				for (AttivazionePolicy attivazionePolicy : list) {
					if(attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().getAzione()!=null) {
						String checkAz = attivazionePolicy.getFiltro().getAzione();
						if(azione.equals(checkAz)) {
							return true;
						}
						if(checkAz.contains(",")) {
							String [] tmp = checkAz.split(",");
							if(tmp!=null && tmp.length>0) {
								for (int i = 0; i < tmp.length; i++) {
									if(azione.equals(tmp[i])) {
										return true;
									}
								}
							}
						}
					}
				}
				return false;
			}
			
		} catch (Exception qe) {
			throw new ServiceException("[" + nomeMetodo +"] Errore : " + qe.getMessage(),qe);
		}
		
	}
	
}
