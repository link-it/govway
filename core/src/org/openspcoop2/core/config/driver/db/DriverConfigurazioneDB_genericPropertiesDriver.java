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
package org.openspcoop2.core.config.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_genericPropertiesDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_genericPropertiesDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_genericPropertiesDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	/**
	 * Restituisce le proprieta' generiche di una tipologia utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	protected List<GenericProperties> getGenericProperties() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGenericProperties(null);
	}
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	protected List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		List<String> listTipologia = new ArrayList<>();
		if(tipologia!=null) {
			listTipologia.add(tipologia);
		}
		return getGenericProperties(listTipologia, null, null,true, null);
	}
	
	protected GenericProperties getGenericProperties(String tipologia, String name) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		List<String> listTipologia = new ArrayList<>();
		if(tipologia!=null) {
			listTipologia.add(tipologia);
		}
		List<GenericProperties> l = getGenericProperties(listTipologia, null, null,true, name);
		if(l==null || l.isEmpty()) {
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti con tipologia '"+tipologia+"' e nome '"+name+"'");
		}
		else if(l.size()>1) {
			throw new DriverConfigurazioneException("[getGenericProperties] Trovata più di una collezione di proprietà con tipologia '"+tipologia+"' e nome '"+name+"'");
		}
		return l.get(0);
	}
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	protected List<GenericProperties> getGenericProperties(List<String> tipologia, Integer idLista, ISearch ricerca, boolean throwNotFoundException) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGenericProperties(tipologia, idLista, ricerca, throwNotFoundException, null);
	}
	private List<GenericProperties> getGenericProperties(List<String> tipologia, Integer idLista, ISearch ricerca, boolean throwNotFoundException, String nomeEsatto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		Integer offset = null;
		Integer limit =null;
		String search = "";
		String filterTipoTokenPolicy = null;
		if(idLista != null && ricerca != null) {
			limit = ricerca.getPageSize(idLista);
			offset = ricerca.getIndexIniziale(idLista);
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			
			filterTipoTokenPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_TOKEN_POLICY);
			
			this.driver.logDebug("search : " + search);
			this.driver.logDebug("filterTipoTokenPolicy : " + filterTipoTokenPolicy);
		}
		
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getGenericProperties");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<Long> listIdLong = new ArrayList<>();
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectCountField("*", "cont");
			if(tipologia!=null && !tipologia.isEmpty()) {
				sqlQueryObject.addWhereINCondition("tipologia", true, tipologia.toArray(new String[1]));
			}
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				sqlQueryObject.addWhereCondition("tipo=?");
			}
			if(nomeEsatto!=null) {
				sqlQueryObject.addWhereCondition("nome=?");
			}
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				stm.setString(index++, filterTipoTokenPolicy);
			}
			if(nomeEsatto!=null) {
				stm.setString(index++, nomeEsatto);
			}
			rs = stm.executeQuery();
			if (rs.next() && ricerca != null)
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit!= null && limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			if(tipologia!=null && !tipologia.isEmpty()) {
				sqlQueryObject.addWhereINCondition("tipologia", true, tipologia.toArray(new String[1]));
			}
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				sqlQueryObject.addWhereCondition("tipo=?");
			}
			if(nomeEsatto!=null) {
				sqlQueryObject.addWhereCondition("nome=?");
			}
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			if(limit!= null)
				sqlQueryObject.setLimit(limit);
			if(offset != null)
				sqlQueryObject.setOffset(offset);
			
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			index = 1;
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				stm.setString(index++, filterTipoTokenPolicy);
			}
			if(nomeEsatto!=null) {
				stm.setString(index++, nomeEsatto);
			}
			rs = stm.executeQuery();
			
			while(rs.next()){
				
				long idP = rs.getLong("id");
				listIdLong.add(idP);
				
			}
			rs.close();
			stm.close();
			
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		
		List<GenericProperties> genericPropertiesList = new ArrayList<>();
		
		if(listIdLong.size()>0) {
			for (Long id : listIdLong) {
				genericPropertiesList.add(this.getGenericProperties(id));
			}
		}

		if((genericPropertiesList==null || genericPropertiesList.size()<=0) && throwNotFoundException)
			throw new DriverConfigurazioneNotFound("Generic Properties non presenti");
		
		return genericPropertiesList;
	}
	
	protected void createGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createGenericProperties");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGenericPropertiesPdD type = 1");
			DriverConfigurazioneDB_configLIB.CRUDGenericProperties(1, genericProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGenericProperties] Errore durante la createSystemPropertiesPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Aggiorna le informazioni sulle proprieta' generiche della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	protected void updateGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateGenericProperties");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("updateGenericProperties type = 2");
			DriverConfigurazioneDB_configLIB.CRUDGenericProperties(2, genericProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGenericProperties] Errore durante la updateSystemPropertiesPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}


	/**
	 * Elimina le informazioni sulle proprieta' generiche della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	protected void deleteGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteGenericProperties");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("deleteGenericProperties type = 3");
			DriverConfigurazioneDB_configLIB.CRUDGenericProperties(3, genericProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGenericProperties] Errore durante la deleteSystemPropertiesPdD : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected GenericProperties getGenericProperties(long idGenericProperties) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getGenericProperties");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);

		try {
			GenericProperties genericProperties = null;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idGenericProperties);
			rs = stm.executeQuery();

			if(rs.next()){
				
				genericProperties = new GenericProperties();
				genericProperties.setNome(rs.getString("nome"));
				genericProperties.setDescrizione(rs.getString("descrizione"));
				genericProperties.setTipologia(rs.getString("tipologia"));
				genericProperties.setTipo(rs.getString("tipo"));
				
				long idP = rs.getLong("id");
				genericProperties.setId(idP);
				
				//prendo le proprieta
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_props = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm2 = con.prepareStatement(sqlQuery);
				stm2.setLong(1, idP);
				rs2 = stm2.executeQuery();
				Property genericProperty = null;
				while(rs2.next())
				{
					genericProperty = new Property();
					//proprieta
					genericProperty.setId(rs2.getLong("id"));
					genericProperty.setNome(rs2.getString("nome"));
					genericProperty.setValore(rs2.getString("valore"));
					genericProperties.addProperty(genericProperty);
				}
				rs2.close();
				stm2.close();
				
			}
			rs.close();
			stm.close();

			if(genericProperties==null )
				throw new DriverConfigurazioneNotFound("Generic Properties non presenti");
			
			return genericProperties;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs2, stm2);
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
}
