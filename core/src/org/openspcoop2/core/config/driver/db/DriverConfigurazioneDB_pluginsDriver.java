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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.constants.PluginSorgenteArchivio;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_pluginsDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_pluginsDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_pluginsDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected RegistroPlugins getRegistroPlugins() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRegistroPlugins");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getRegistroPlugins] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		RegistroPlugins registro = null;
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("posizione", true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			List<String> nomi = new ArrayList<>();
			while(rs.next()) {
				nomi.add(rs.getString("nome"));
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			if(!nomi.isEmpty()) {
				if(registro==null) {
					registro = new RegistroPlugins();
				}
				for (String nome : nomi) {
					registro.addPlugin(this.getRegistroPlugin(con, nome));
				}
			}
			
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getRegistroPlugins]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getRegistroPlugins]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		if(registro==null || registro.sizePluginList()<=0) {
			throw new DriverConfigurazioneNotFound("Nessun plugin registrato");
		}
		return registro;
		
	}
	
	
	
	protected RegistroPlugin getRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getRegistroPlugin(nome, false);
	}
	protected RegistroPlugin getDatiRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getRegistroPlugin(nome, true);
	}
	private RegistroPlugin getRegistroPlugin(String nome, boolean soloDati) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Connection con = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRegistroPlugin");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

			try {
			
			return getRegistroPlugin(con, nome, soloDati);
			
		} finally {
			this.driver.closeConnection(con);
		}

	}

	protected RegistroPlugin getRegistroPlugin(Connection conParam, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getRegistroPlugin(conParam, nome, false);
	}
	protected RegistroPlugin getDatiRegistroPlugin(Connection conParam, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getRegistroPlugin(conParam, nome, true);
	}
	private RegistroPlugin getRegistroPlugin(Connection conParam, String nome, boolean soloDati) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		RegistroPlugin plugin = null;
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = conParam.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if(rs.next()) {
				
				plugin = new RegistroPlugin();
				
				plugin.setId(rs.getLong("id"));
				plugin.setNome(rs.getString("nome"));
				plugin.setPosizione(rs.getInt("posizione"));
				StatoFunzionalita stato = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato"));
				plugin.setStato(stato);
				plugin.setDescrizione(rs.getString("descrizione"));
				plugin.setData(rs.getTimestamp("data"));
				
				String compatibilita = rs.getString("compatibilita");
				if( (compatibilita!=null && !"".equals(compatibilita)) ) {
					if(compatibilita.contains(",")) {
						String [] tmp = compatibilita.split(",");
						for (int i = 0; i < tmp.length; i++) {
							plugin.addCompatibilita(tmp[i].trim());
						}
					}
					else {
						plugin.addCompatibilita(compatibilita);
					}
				}
				
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			if(plugin!=null && !soloDati) {
				
				// archive jar
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
				sqlQueryObject.addWhereCondition("id_plugin=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				 
				stm = conParam.prepareStatement(sqlQuery);
				stm.setLong(1, plugin.getId());
				rs = stm.executeQuery();
				while(rs.next()) {
					
					RegistroPluginArchivio archivio = new RegistroPluginArchivio();
					archivio.setNome(rs.getString("nome"));
					archivio.setData(rs.getTimestamp("data"));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
					archivio.setSorgente( DriverConfigurazioneDB_LIB.getEnumPluginSorgenteArchivio(rs.getString("sorgente")));
					switch (archivio.getSorgente()) {
					case JAR:
						archivio.setContenuto(jdbcAdapter.getBinaryData(rs, "contenuto"));
						break;
					case URL:
						archivio.setUrl(rs.getString("url"));
						break;
					case DIR:
						archivio.setDir(rs.getString("dir"));
						break;
					}
					plugin.addArchivio(archivio);
					
				}
				rs.close();
				stm.close();
				rs=null;
				stm=null;

			}
			
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getRegistroPlugin]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getRegistroPlugin]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}

		if(plugin!=null) {
			return plugin;
		}
		throw new DriverConfigurazioneNotFound("Plugin '"+nome+"' non esistente");
	}
	
	protected RegistroPlugin getRegistroPluginFromPosizione(int posizione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getRegistroPluginFromPosizione(posizione, false);
	}
	protected RegistroPlugin getDatiRegistroPluginFromPosizione(int posizione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return getRegistroPluginFromPosizione(posizione, true);
	}
	private RegistroPlugin getRegistroPluginFromPosizione(int posizione, boolean soloDati) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRegistroPlugin");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		RegistroPlugin plugin = null;
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addWhereCondition("posizione=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			stm.setInt(1, posizione);
			rs = stm.executeQuery();
			if(rs.next()) {
				
				plugin = new RegistroPlugin();
				
				plugin.setId(rs.getLong("id"));
				plugin.setNome(rs.getString("nome"));
				plugin.setPosizione(rs.getInt("posizione"));
				StatoFunzionalita stato = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato"));
				plugin.setStato(stato);
				plugin.setDescrizione(rs.getString("descrizione"));
				plugin.setData(rs.getTimestamp("data"));
				
				String compatibilita = rs.getString("compatibilita");
				if( (compatibilita!=null && !"".equals(compatibilita)) ) {
					if(compatibilita.contains(",")) {
						String [] tmp = compatibilita.split(",");
						for (int i = 0; i < tmp.length; i++) {
							plugin.addCompatibilita(tmp[i].trim());
						}
					}
					else {
						plugin.addCompatibilita(compatibilita);
					}
				}
				
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			if(plugin!=null && !soloDati) {
				
				// archive jar
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
				sqlQueryObject.addWhereCondition("id_plugin=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				 
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, plugin.getId());
				rs = stm.executeQuery();
				while(rs.next()) {
					
					RegistroPluginArchivio archivio = new RegistroPluginArchivio();
					archivio.setNome(rs.getString("nome"));
					archivio.setData(rs.getTimestamp("data"));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
					archivio.setSorgente( DriverConfigurazioneDB_LIB.getEnumPluginSorgenteArchivio(rs.getString("sorgente")));
					switch (archivio.getSorgente()) {
					case JAR:
						archivio.setContenuto(jdbcAdapter.getBinaryData(rs, "contenuto"));
						break;
					case URL:
						archivio.setUrl(rs.getString("url"));
						break;
					case DIR:
						archivio.setDir(rs.getString("dir"));
						break;
					}
					plugin.addArchivio(archivio);
					
				}
				rs.close();
				stm.close();
				rs=null;
				stm=null;

			}
			
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getRegistroPlugin]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getRegistroPlugin]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		if(plugin!=null) {
			return plugin;
		}
		throw new DriverConfigurazioneNotFound("Plugin in posizione '"+posizione+"' non esistente");
	}
	
	protected boolean existsRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsRegistroPlugin");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[existsRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		int count = -1;
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectCountField(CostantiDB.REGISTRO_PLUGINS+".id", "cont");
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);
			}
			
			return count > 0;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[existsRegistroPlugin]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[existsRegistroPlugin]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	
	protected int getMaxPosizioneRegistroPlugin() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getMaxPosizioneRegistroPlugin");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getMaxPosizioneRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectMaxField(CostantiDB.REGISTRO_PLUGINS+".posizione", "max");
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			
			if(rs.next()) {
				return rs.getInt(1);
			}
			
			return 0;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getMaxPosizioneRegistroPlugin]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getMaxPosizioneRegistroPlugin]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	
	protected int getNumeroArchiviJarRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getNumeroArchiviJarRegistroPlugin");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[existsRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		int count = -1;
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectCountField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE+".id", "cont");
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addWhereCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".id_plugin = " + CostantiDB.REGISTRO_PLUGINS + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.REGISTRO_PLUGINS + ".nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);
			}
			
			return count;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getNumeroArchiviJarRegistroPlugin]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getNumeroArchiviJarRegistroPlugin]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	
	protected void createRegistroPlugin(RegistroPlugin plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createRegistroPlugin");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRegistroPlugin type = 1");
			DriverConfigurazioneDB_pluginsLIB.CRUDRegistroPlugin(1, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRegistroPlugin] Errore durante la createRegistroPlugin : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void updateRegistroPlugin(RegistroPlugin plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateRegistroPlugin");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRegistroPlugin type = 2");
			DriverConfigurazioneDB_pluginsLIB.CRUDRegistroPlugin(2, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRegistroPlugin] Errore durante la updateRegistroPlugin : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void deleteRegistroPlugin(RegistroPlugin plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteRegistroPlugin");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRegistroPlugin type = 3");
			DriverConfigurazioneDB_pluginsLIB.CRUDRegistroPlugin(3, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRegistroPlugin] Errore durante la deleteRegistroPlugin : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void updateDatiRegistroPlugin(String nomePlugin, RegistroPlugin plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateDatiRegistroPlugin");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateDatiRegistroPlugin] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("updateDatiRegistroPlugin");
			DriverConfigurazioneDB_pluginsLIB.updateDatiRegistroPlugin(nomePlugin, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateDatiRegistroPlugin] Errore durante la updateDatiRegistroPlugin : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected RegistroPluginArchivio getRegistroPluginArchivio(String nomePlugin, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRegistroPluginArchivio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getRegistroPluginArchivio] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		RegistroPluginArchivio archivio = null;
		
		try {
			
			long idPlugin = DBUtils.getIdRegistroPlugin(nomePlugin, con, this.driver.tipoDB);
			if(idPlugin<=0) {
				throw new DriverConfigurazioneNotFound("Plugin '"+nomePlugin+"' non esistente");
			}
			
			if(nome==null) {
				throw new DriverConfigurazioneException("Nome archivio non indicato");
			}
			
			// archive jar
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
			sqlQueryObject.addWhereCondition("id_plugin=?");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPlugin);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if(rs.next()) {
					
				archivio = new RegistroPluginArchivio();
				archivio.setNome(rs.getString("nome"));
				archivio.setData(rs.getTimestamp("data"));
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
				archivio.setSorgente( DriverConfigurazioneDB_LIB.getEnumPluginSorgenteArchivio(rs.getString("sorgente")));
				switch (archivio.getSorgente()) {
				case JAR:
					archivio.setContenuto(jdbcAdapter.getBinaryData(rs, "contenuto"));
					break;
				case URL:
					archivio.setUrl(rs.getString("url"));
					break;
				case DIR:
					archivio.setDir(rs.getString("dir"));
					break;
				}
	
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;

	
		}catch (DriverConfigurazioneNotFound notFound) {
			throw notFound;
		}catch (SQLException se) {
			throw new DriverConfigurazioneException("[getRegistroPluginArchivio]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getRegistroPluginArchivio]  Exception: " + se.getMessage(),se);
		}
		finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		if(archivio!=null) {
			archivio.setNomePlugin(nomePlugin);
			return archivio;
		}
		throw new DriverConfigurazioneNotFound("Archivio '"+nome+"' non esistente nel plugin '"+nomePlugin+"'");
	}
	
	protected boolean existsRegistroPluginArchivio(String nomePlugin, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsRegistroPluginArchivio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[existsRegistroPluginArchivio] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		int count = -1;
		
		try {
			long idPlugin = DBUtils.getIdRegistroPlugin(nomePlugin, con, this.driver.tipoDB);
			if(idPlugin<=0) {
				throw new DriverConfigurazioneNotFound("Plugin '"+nomePlugin+"' non esistente");
			}
			
			if(nome==null) {
				throw new DriverConfigurazioneException("Nome archivio non indicato");
			}
			
			// archive jar
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectCountField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE+".id", "cont");
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
			sqlQueryObject.addWhereCondition("id_plugin=?");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPlugin);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if(rs.next()) {
					
				count = rs.getInt(1);
	
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			return count > 0;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[existsRegistroPluginArchivio]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[existsRegistroPluginArchivio]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	
	protected boolean existsRegistroPluginArchivio(String nomePlugin, PluginSorgenteArchivio tipoSorgente, String sorgente) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsRegistroPluginArchivio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[existsRegistroPluginArchivio] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		int count = -1;
		
		try {
			long idPlugin = DBUtils.getIdRegistroPlugin(nomePlugin, con, this.driver.tipoDB);
			if(idPlugin<=0) {
				throw new DriverConfigurazioneNotFound("Plugin '"+nomePlugin+"' non esistente");
			}
			
			if(tipoSorgente==null) {
				throw new DriverConfigurazioneException("TipoSorgente non indicata");
			}
			if(sorgente==null) {
				throw new DriverConfigurazioneException("Sorgente non indicata");
			}
			
			// archive jar
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectCountField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE+".id", "cont");
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
			sqlQueryObject.addWhereCondition("id_plugin=?");
			switch (tipoSorgente) {
			case JAR:
				sqlQueryObject.addWhereCondition("nome=?");
				break;
			case DIR:
				sqlQueryObject.addWhereCondition("dir=?");
				break;
			case URL:
				sqlQueryObject.addWhereCondition("url=?");
				break;
			default:
				break;
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPlugin);
			stm.setString(2, sorgente);
			rs = stm.executeQuery();
			if(rs.next()) {
					
				count = rs.getInt(1);
	
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			return count > 0;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[existsRegistroPluginArchivio]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[existsRegistroPluginArchivio]  Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}
	
	protected void createRegistroPluginArchivio(String nomePlugin, RegistroPluginArchivio plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createRegistroPluginArchivio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRegistroPluginArchivio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRegistroPlugin type = 1");
			DriverConfigurazioneDB_pluginsLIB.CRUDRegistroPluginArchivio(1, nomePlugin, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRegistroPluginArchivio] Errore durante la createRegistroPluginArchivio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void updateRegistroPluginArchivio(String nomePlugin, RegistroPluginArchivio plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateRegistroPluginArchivio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRegistroPluginArchivio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRegistroPlugin type = 2");
			DriverConfigurazioneDB_pluginsLIB.CRUDRegistroPluginArchivio(2, nomePlugin, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRegistroPluginArchivio] Errore durante la updateRegistroPluginArchivio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void deleteRegistroPluginArchivio(String nomePlugin, RegistroPluginArchivio plugin) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteRegistroPluginArchivio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRegistroPluginArchivio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRegistroPlugin type = 3");
			DriverConfigurazioneDB_pluginsLIB.CRUDRegistroPluginArchivio(3, nomePlugin, plugin, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRegistroPluginArchivio] Errore durante la deleteRegistroPluginArchivio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<RegistroPlugin> pluginsArchiviList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "pluginsArchiviList";
		int idLista = Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI;
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<RegistroPlugin> lista = new ArrayList<RegistroPlugin>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("pluginsArchiviList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addSelectCountField("id", "cont");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");            
			sqlQueryObject.addSelectField("posizione");       
			sqlQueryObject.addSelectField("stato");           
			sqlQueryObject.addSelectField("descrizione");     
			sqlQueryObject.addSelectField("data");         
			sqlQueryObject.addSelectField("compatibilita");          
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy("posizione");
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				RegistroPlugin regola = new RegistroPlugin();
				regola.setId(risultato.getLong("id"));
				regola.setNome(risultato.getString("nome"));
				regola.setPosizione(risultato.getInt("posizione"));
				regola.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(risultato.getString("stato")));
				regola.setDescrizione(risultato.getString("descrizione"));
				Timestamp timestamp = risultato.getTimestamp("data");
				regola.setData(new Date(timestamp.getTime()));
				
				String compatibilita = risultato.getString("compatibilita");
				if(compatibilita != null)
					regola.setCompatibilitaList(Arrays.asList(compatibilita.split(",")));
				
				lista.add(regola);
			
			}
			risultato.close();
			stmt.close();
			
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	protected List<RegistroPluginArchivio> pluginsArchiviJarList(String nome, ISearch ricerca) throws DriverConfigurazioneException {
		return this.pluginsArchiviJarList(nome, ricerca, true);
	}
	
	private List<RegistroPluginArchivio> pluginsArchiviJarList(String nome, ISearch ricerca, boolean escludiContenuto) throws DriverConfigurazioneException {
		String nomeMetodo = "pluginsArchiviList";
		int idLista = Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI_JAR;
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<RegistroPluginArchivio> lista = new ArrayList<RegistroPluginArchivio>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("pluginsArchiviList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObjectOr = null;
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObjectJar = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectJar.addWhereLikeCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".nome", search, true, true);
				sqlQueryObjectJar.addWhereCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".sorgente = ?");
				sqlQueryObjectJar.setANDLogicOperator(true);	
				
				ISQLQueryObject sqlQueryObjectUrl = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUrl.addWhereLikeCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".url", search, true, true);
				sqlQueryObjectUrl.addWhereCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".sorgente = ?");
				sqlQueryObjectUrl.setANDLogicOperator(true);	
				
				ISQLQueryObject sqlQueryObjectDir = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectDir.addWhereLikeCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".dir", search, true, true);
				sqlQueryObjectDir.addWhereCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".sorgente = ?");
				sqlQueryObjectDir.setANDLogicOperator(true);	
				
				sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectOr.setANDLogicOperator(false);	
				sqlQueryObjectOr.addWhereCondition(sqlQueryObjectJar.createSQLConditions());
				sqlQueryObjectOr.addWhereCondition(sqlQueryObjectUrl.createSQLConditions());
				sqlQueryObjectOr.addWhereCondition(sqlQueryObjectDir.createSQLConditions());
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectCountField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE+".id", "cont");
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addWhereCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".id_plugin = " + CostantiDB.REGISTRO_PLUGINS + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.REGISTRO_PLUGINS + ".nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			if (sqlQueryObjectOr!=null) {
				sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setString(parameterIndex ++, nome);
			if (sqlQueryObjectOr!=null) {
				stmt.setString(parameterIndex ++, DriverConfigurazioneDB_LIB.getValue(PluginSorgenteArchivio.JAR));
				stmt.setString(parameterIndex ++, DriverConfigurazioneDB_LIB.getValue(PluginSorgenteArchivio.URL));
				stmt.setString(parameterIndex ++, DriverConfigurazioneDB_LIB.getValue(PluginSorgenteArchivio.DIR));
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addWhereCondition(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".id_plugin = " + CostantiDB.REGISTRO_PLUGINS + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.REGISTRO_PLUGINS + ".nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			if (sqlQueryObjectOr!=null) {
				sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
			}
			
			sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".id");
			sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".nome");            
			sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".data");       
			sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".sorgente");   
			
			if(!escludiContenuto) {
				sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".contenuto");     
			}
			sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".url");         
			sqlQueryObject.addSelectField(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".dir");
						
			sqlQueryObject.addOrderBy(CostantiDB.REGISTRO_PLUGINS_ARCHIVE + ".data");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			parameterIndex = 1;
			stmt.setString(parameterIndex ++, nome);
			if (sqlQueryObjectOr!=null) {
				stmt.setString(parameterIndex ++, DriverConfigurazioneDB_LIB.getValue(PluginSorgenteArchivio.JAR));
				stmt.setString(parameterIndex ++, DriverConfigurazioneDB_LIB.getValue(PluginSorgenteArchivio.URL));
				stmt.setString(parameterIndex ++, DriverConfigurazioneDB_LIB.getValue(PluginSorgenteArchivio.DIR));
			}
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				RegistroPluginArchivio archivio = new RegistroPluginArchivio();
				archivio.setNome(risultato.getString("nome"));
				archivio.setNomePlugin(nome);
				Timestamp timestamp = risultato.getTimestamp("data");
				archivio.setData(new Date(timestamp.getTime()));
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
				archivio.setSorgente( DriverConfigurazioneDB_LIB.getEnumPluginSorgenteArchivio(risultato.getString("sorgente")));
				switch (archivio.getSorgente()) {
				case JAR:
					if(!escludiContenuto) {
						archivio.setContenuto(jdbcAdapter.getBinaryData(risultato, "contenuto"));
					}
					break;
				case URL:
					archivio.setUrl(risultato.getString("url"));
					break;
				case DIR:
					archivio.setDir(risultato.getString("dir"));
					break;
				}
				lista.add(archivio);
			}
			risultato.close();
			stmt.close();
			
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
}
