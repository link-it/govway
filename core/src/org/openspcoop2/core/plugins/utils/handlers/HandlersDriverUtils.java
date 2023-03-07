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
package org.openspcoop2.core.plugins.utils.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.dao.jdbc.converter.PluginFieldConverter;
import org.openspcoop2.core.plugins.utils.PluginsDriverUtils;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * HandlersDriverUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HandlersDriverUtils {

	public static int numeroHandlerList(String tipologia, TipoPdD ruoloPorta, Long idPorta, TipoPlugin tipoPlugin, String nomeMetodo, Connection con, Logger log, String tipoDB) throws ServiceException {
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(ruoloPorta !=null) {
			if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
		}		
		
		String queryString;

		PreparedStatement stmt=null;
		ResultSet risultato=null;

		try {
			PluginFieldConverter converter = new PluginFieldConverter(tipoDB);
			String tabellaPlugin = converter.toTable(Plugin.model());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addFromTable(tabellaPlugin);
			sqlQueryObject.addSelectCountField(tabella + ".id", "cont");
			
			sqlQueryObject.addWhereCondition(tabellaPlugin +".tipo=" + tabella + ".tipo");
			if(idPorta !=null) {
				sqlQueryObject.addWhereCondition(tabella + ".id_porta=?");
			}
			sqlQueryObject.addWhereCondition(tabella + ".tipologia=?");
			sqlQueryObject.addWhereCondition(tabellaPlugin + ".tipo_plugin=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(idPorta!=null) {
				stmt.setLong(index++, idPorta);
			}
			stmt.setString(index++, tipologia);
			stmt.setString(index++, tipoPlugin.getValue());
			
			risultato = stmt.executeQuery();
			int res = 0;
			if (risultato.next()) {
				res = risultato.getInt(1);
			}
			risultato.close();
			stmt.close();

			return res;
		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	public static List<ConfigurazioneHandlerBean> handlerList(ISearch ricerca, String tipologia, TipoPdD ruoloPorta,
			Long idPorta, String nomeMetodo, int idLista, TipoPlugin tipoPlugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(ruoloPorta !=null) {
			if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
		}		
		
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ConfigurazioneHandlerBean> lista = new ArrayList<ConfigurazioneHandlerBean>();

		try {
			PluginFieldConverter converter = new PluginFieldConverter(tipoDB);
			String tabellaPlugin = converter.toTable(Plugin.model());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addFromTable(tabellaPlugin);
			
			sqlQueryObject.addSelectCountField(tabella + ".id", "cont");
			sqlQueryObject.addWhereCondition(tabellaPlugin +".tipo=" + tabella + ".tipo");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(tabellaPlugin +".label", search, true, true);
			}
			if(idPorta !=null) {
				sqlQueryObject.addWhereCondition(tabella + ".id_porta=?");
			}
			sqlQueryObject.addWhereCondition(tabella + ".tipologia=?");
			sqlQueryObject.addWhereCondition(tabellaPlugin + ".tipo_plugin=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(idPorta!=null) {
				stmt.setLong(index++, idPorta);
			}
			stmt.setString(index++, tipologia);
			stmt.setString(index++, tipoPlugin.getValue());
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addFromTable(tabellaPlugin);
			sqlQueryObject.addSelectAliasField(tabella + ".id","IdHandler");
			sqlQueryObject.addSelectField(tabella + ".tipologia"); 
			sqlQueryObject.addSelectField(tabella + ".tipo");         
			sqlQueryObject.addSelectField(tabella + ".posizione");       
			sqlQueryObject.addSelectField(tabella + ".stato");
			if(idPorta !=null) {
				sqlQueryObject.addSelectField(tabella + ".id_porta");
			}
			
			sqlQueryObject.addWhereCondition(tabellaPlugin +".tipo=" + tabella + ".tipo");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(tabellaPlugin +".label", search, true, true);
			}
			if(idPorta !=null) {
				sqlQueryObject.addWhereCondition(tabella + ".id_porta=?");
			}
			sqlQueryObject.addWhereCondition(tabella + ".tipologia=?");
			sqlQueryObject.addWhereCondition(tabellaPlugin + ".tipo_plugin=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(tabella + ".posizione");
			sqlQueryObject.addOrderBy(tabella + ".tipo");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			index = 1;
			if(idPorta!=null) {
				stmt.setLong(index++, idPorta);
			}
			stmt.setString(index++, tipologia);
			stmt.setString(index++, tipoPlugin.getValue());
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				ConfigurazioneHandler handler = new ConfigurazioneHandler();
				handler.setId(risultato.getLong("IdHandler"));
				handler.setPosizione(risultato.getInt("posizione"));
				handler.setStato(getEnumStatoFunzionalita(risultato.getString("stato")));
				handler.setTipo(risultato.getString("tipo"));
				
				Plugin plugin = PluginsDriverUtils.getPlugin(tipoPlugin.getValue(), handler.getTipo(), true, con, log, tipoDB);
				
				ConfigurazioneHandlerBean bean = new ConfigurazioneHandlerBean(handler, plugin);
				
				lista.add(bean);
			
			}
			risultato.close();
			stmt.close();
			
			return lista;

		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	public static int getMaxPosizioneHandlers(String tipologia, TipoPdD ruoloPorta, Long idPorta, String nomeMetodo, TipoPlugin tipoPlugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(ruoloPorta !=null) {
			if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
		}		
		
		String queryString;

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		int posizione = 0;

		try {
			PluginFieldConverter converter = new PluginFieldConverter(tipoDB);
			String tabellaPlugin = converter.toTable(Plugin.model());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addFromTable(tabellaPlugin);
			sqlQueryObject.addWhereCondition(tabellaPlugin +".tipo=" + tabella + ".tipo");
			
			sqlQueryObject.addSelectMaxField(tabella + ".posizione", "posizione");       

			if(idPorta !=null) {
				sqlQueryObject.addWhereCondition(tabella + ".id_porta=?");
			}
			sqlQueryObject.addWhereCondition(tabella + ".tipologia=?");
			sqlQueryObject.addWhereCondition(tabellaPlugin + ".tipo_plugin=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(idPorta!=null) {
				stmt.setLong(index++, idPorta);
			}
			stmt.setString(index++, tipologia);
			stmt.setString(index++, tipoPlugin.getValue());
			risultato = stmt.executeQuery();

			if(risultato.next()) {
				posizione = risultato.getInt("posizione");
			}
			
			risultato.close();
			stmt.close();
			
			return posizione;

		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	public static boolean existsHandler(String tipologia, TipoPdD ruoloPorta, Long idPorta, String nomeMetodo, TipoPlugin tipoPlugin, String tipo, Connection con, Logger log, String tipoDB) throws ServiceException {
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(ruoloPorta !=null) {
			if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
		}		
		
		String queryString;

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean exists = false;

		try {
			PluginFieldConverter converter = new PluginFieldConverter(tipoDB);
			String tabellaPlugin = converter.toTable(Plugin.model());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addFromTable(tabellaPlugin);
			sqlQueryObject.addWhereCondition(tabellaPlugin +".tipo=" + tabella + ".tipo");
			
			sqlQueryObject.addSelectCountField(tabella + ".id", "cont");  

			if(idPorta !=null) {
				sqlQueryObject.addWhereCondition(tabella + ".id_porta=?");
			}
			sqlQueryObject.addWhereCondition(tabella + ".tipologia=?");
			sqlQueryObject.addWhereCondition(tabella + ".tipo=?");
			sqlQueryObject.addWhereCondition(tabellaPlugin + ".tipo_plugin=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(idPorta!=null) {
				stmt.setLong(index++, idPorta);
			}
			stmt.setString(index++, tipologia);
			stmt.setString(index++, tipo);
			stmt.setString(index++, tipoPlugin.getValue());
			risultato = stmt.executeQuery();

			if(risultato.next()) {
				exists = risultato.getInt(1) > 0;
			}
			
			risultato.close();
			stmt.close();
			
			return exists;

		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	public static ConfigurazioneHandlerBean getHandler(String tipologia, TipoPdD ruoloPorta,
			Long idPorta, Long idHandler, String nomeMetodo, TipoPlugin tipoPlugin, Connection con, Logger log, String tipoDB) throws ServiceException {
		
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(ruoloPorta !=null) {
			if(ruoloPorta.equals(TipoPdD.DELEGATA)) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(ruoloPorta.equals(TipoPdD.APPLICATIVA)) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
		}		
		
		String queryString;

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ConfigurazioneHandlerBean bean = null;

		try {
			PluginFieldConverter converter = new PluginFieldConverter(tipoDB);
			String tabellaPlugin = converter.toTable(Plugin.model());
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addFromTable(tabellaPlugin);
			sqlQueryObject.addSelectField(tabella + ".id");
			sqlQueryObject.addSelectField(tabella + ".tipologia"); 
			sqlQueryObject.addSelectField(tabella + ".tipo");         
			sqlQueryObject.addSelectField(tabella + ".posizione");       
			sqlQueryObject.addSelectField(tabella + ".stato");
			if(idPorta !=null) {
				sqlQueryObject.addSelectField(tabella + ".id_porta");
			}
			
			sqlQueryObject.addWhereCondition(tabellaPlugin +".tipo=" + tabella + ".tipo");
			
			sqlQueryObject.addWhereCondition(tabella + ".id=?");
			if(idPorta !=null) {
				sqlQueryObject.addWhereCondition(tabella + ".id_porta=?");
			}
			sqlQueryObject.addWhereCondition(tabella + ".tipologia=?");
			sqlQueryObject.addWhereCondition(tabellaPlugin + ".tipo_plugin=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			
			stmt.setLong(index++, idHandler);
			if(idPorta!=null) {
				stmt.setLong(index++, idPorta);
			}
			stmt.setString(index++, tipologia);
			stmt.setString(index++, tipoPlugin.getValue());
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				ConfigurazioneHandler handler = new ConfigurazioneHandler();
				handler.setId(risultato.getLong("id"));
				handler.setPosizione(risultato.getInt("posizione"));
				handler.setStato(getEnumStatoFunzionalita(risultato.getString("stato")));
				handler.setTipo(risultato.getString("tipo"));
				
				Plugin plugin = PluginsDriverUtils.getPlugin(tipoPlugin.getValue(), handler.getTipo(), true, con, log, tipoDB);
				
				bean = new ConfigurazioneHandlerBean(handler, plugin);
			
			}
			risultato.close();
			stmt.close();
			
			return bean;

		} catch (Exception se) {
			throw new ServiceException("[" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	private static StatoFunzionalita getEnumStatoFunzionalita(String value){
		if(value==null){
			return null;
		}
		else{
			return StatoFunzionalita.toEnumConstant(value);
		}
	}
}
