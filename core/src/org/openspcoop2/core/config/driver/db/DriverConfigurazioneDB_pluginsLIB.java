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

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.constants.PluginSorgenteArchivio;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_pluginLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_pluginsLIB {


	
	
	
	public static void CRUDRegistroPlugin(int type, RegistroPlugin plugin, Connection con) throws DriverConfigurazioneException {
		if (plugin == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] Il plugin non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			if(plugin.getNome()==null) {
				throw new DriverConfigurazioneException("Nome non fornito");
			}
			
			// Recupero id generic properties
			long idParent = -1;
			if(type == CostantiDB.UPDATE || type == CostantiDB.DELETE) {
				
				idParent = DBUtils.getIdRegistroPlugin(plugin.getNome(), con, DriverConfigurazioneDB_LIB.tipoDB);
				if(idParent<=0) {
					throw new DriverConfigurazioneException("Plugin con nome '"+plugin.getNome()+"' non trovato");
				}
				
				// Elimino anche gli archivi (nell'update le ricreo)
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
				sqlQueryObject.addWhereCondition("id_plugin=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();

				// delete
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRO_PLUGINS);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idParent);
				updateStmt.executeUpdate();
				updateStmt.close();
			}
			

			switch (type) {
			case CREATE:
			case UPDATE:
		
				// insert

				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<>();
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("nome", plugin.getNome() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("posizione", plugin.getPosizione() , InsertAndGeneratedKeyJDBCType.INT) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("stato",  DriverConfigurazioneDB_LIB.getValue(plugin.getStato()) , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("descrizione", plugin.getDescrizione() , InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("data", plugin.getData() , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
				String compatibilita = null;
				if(plugin.sizeCompatibilitaList()>0) {
					StringBuilder bf = new StringBuilder();
					for (int i = 0; i < plugin.sizeCompatibilitaList(); i++) {
						if(i>0) {
							bf.append(",");
						}
						bf.append(plugin.getCompatibilita(i));
					}
					if(bf.length()>0) {
						compatibilita = bf.toString();
					}
				}
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("compatibilita", compatibilita , InsertAndGeneratedKeyJDBCType.STRING) );
		       				
				long idPlugin = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
						new CustomKeyGeneratorObject(CostantiDB.REGISTRO_PLUGINS, CostantiDB.REGISTRO_PLUGINS_COLUMN_ID, 
								CostantiDB.REGISTRO_PLUGINS_SEQUENCE, CostantiDB.REGISTRO_PLUGINS_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(idPlugin<=0){
					throw new Exception("ID (RegistroPlugins) autoincrementale non ottenuto");
				}

				for(int l=0; l<plugin.sizeArchivioList();l++){
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
					sqlQueryObject.addInsertField("id_plugin", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("data", "?");
					sqlQueryObject.addInsertField("sorgente", "?");
					sqlQueryObject.addInsertField("contenuto", "?");
					sqlQueryObject.addInsertField("url", "?");
					sqlQueryObject.addInsertField("dir", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idPlugin);
					updateStmt.setString(index++, plugin.getArchivio(l).getNome());
					
					Timestamp t = DateManager.getTimestamp();
					if( plugin.getArchivio(l).getData()!=null ) {
						t = new Timestamp( plugin.getArchivio(l).getData().getTime() );
					}
					updateStmt.setTimestamp(index++, t);
					
					PluginSorgenteArchivio sorgente = plugin.getArchivio(l).getSorgente();
					updateStmt.setString(index++, DriverConfigurazioneDB_LIB.getValue(sorgente));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDB_LIB.tipoDB);
					byte[] archivio = null;
					String url = null;
					String dir = null;
					switch (sorgente) {
					case JAR:
						archivio = plugin.getArchivio(l).getContenuto();
						break;
					case URL:
						url = plugin.getArchivio(l).getUrl();
						break;
					case DIR:
						dir = plugin.getArchivio(l).getDir();
						break;
					}
					jdbcAdapter.setBinaryData(updateStmt, index++, archivio);
					updateStmt.setString(index++, url);
					updateStmt.setString(index++, dir);
					
					updateStmt.executeUpdate();
					updateStmt.close();
				}

				break;
			case DELETE:
				// non rimuovo in quanto gia fatto sopra.
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}

	}
	
	public static void updateDatiRegistroPlugin(String nome, RegistroPlugin plugin, Connection con) throws DriverConfigurazioneException{
		if (plugin == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] Il plugin non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
	
		try {
			
			long idParent = DBUtils.getIdRegistroPlugin(nome, con, DriverConfigurazioneDB_LIB.tipoDB);
			if(idParent<=0) {
				throw new DriverConfigurazioneException("Plugin con nome '"+nome+"' non trovato");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addUpdateField("nome", "?");
			sqlQueryObject.addUpdateField("posizione", "?");
			sqlQueryObject.addUpdateField("stato", "?");
			sqlQueryObject.addUpdateField("descrizione", "?");
			sqlQueryObject.addUpdateField("data", "?");
			sqlQueryObject.addUpdateField("compatibilita", "?");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setString(index++, plugin.getNome());
			updateStmt.setInt(index++, plugin.getPosizione());
			updateStmt.setString(index++, DriverConfigurazioneDB_LIB.getValue(plugin.getStato()));
			updateStmt.setString(index++, plugin.getDescrizione());
			
			Timestamp t = DateManager.getTimestamp();
			if( plugin.getData()!=null ) {
				t = new Timestamp( plugin.getData().getTime() );
			}
			updateStmt.setTimestamp(index++, t);
			
			String compatibilita = null;
			if(plugin.sizeCompatibilitaList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < plugin.sizeCompatibilitaList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(plugin.getCompatibilita(i));
				}
				if(bf.length()>0) {
					compatibilita = bf.toString();
				}
			}
			updateStmt.setString(index++, compatibilita);
	       			
			updateStmt.setLong(index++, idParent);
	
			updateStmt.executeUpdate();
			updateStmt.close();
			updateStmt=null;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPlugin] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	public static void CRUDRegistroPluginArchivio(int type, String nomePlugin, RegistroPluginArchivio plugin, Connection con) throws DriverConfigurazioneException {
		if (plugin == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPluginArchivio] Il plugin non possono essere NULL");
		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		//String selectQuery = "";
		ResultSet selectRS = null;
		

		try {
			
			long idParent = DBUtils.getIdRegistroPlugin(nomePlugin, con, DriverConfigurazioneDB_LIB.tipoDB);
			if(idParent<=0) {
				throw new DriverConfigurazioneException("Plugin con nome '"+nomePlugin+"' non trovato");
			}
			
			switch (type) {
			case CREATE:
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
				sqlQueryObject.addInsertField("id_plugin", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("data", "?");
				sqlQueryObject.addInsertField("sorgente", "?");
				sqlQueryObject.addInsertField("contenuto", "?");
				sqlQueryObject.addInsertField("url", "?");
				sqlQueryObject.addInsertField("dir", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idParent);
				updateStmt.setString(index++, plugin.getNome());
				
				Timestamp t = DateManager.getTimestamp();
				if( plugin.getData()!=null ) {
					t = new Timestamp( plugin.getData().getTime() );
				}
				updateStmt.setTimestamp(index++, t);
				
				PluginSorgenteArchivio sorgente = plugin.getSorgente();
				updateStmt.setString(index++, DriverConfigurazioneDB_LIB.getValue(sorgente));
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDB_LIB.tipoDB);
				byte[] archivio = null;
				String url = null;
				String dir = null;
				switch (sorgente) {
				case JAR:
					archivio = plugin.getContenuto();
					break;
				case URL:
					url = plugin.getUrl();
					break;
				case DIR:
					dir = plugin.getDir();
					break;
				}
				jdbcAdapter.setBinaryData(updateStmt, index++, archivio);
				updateStmt.setString(index++, url);
				updateStmt.setString(index++, dir);
				
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
				
			case UPDATE:
		
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
				sqlQueryObject.addUpdateField("data", "?");
				sqlQueryObject.addUpdateField("sorgente", "?");
				sqlQueryObject.addUpdateField("contenuto", "?");
				sqlQueryObject.addUpdateField("url", "?");
				sqlQueryObject.addUpdateField("dir", "?");
				sqlQueryObject.addWhereCondition("id_plugin = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				
				t = DateManager.getTimestamp();
				updateStmt.setTimestamp(index++, t);
				
				sorgente = plugin.getSorgente();
				updateStmt.setString(index++, DriverConfigurazioneDB_LIB.getValue(sorgente));
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDB_LIB.tipoDB);
				archivio = null;
				url = null;
				dir = null;
				switch (sorgente) {
				case JAR:
					archivio = plugin.getContenuto();
					break;
				case URL:
					url = plugin.getUrl();
					break;
				case DIR:
					dir = plugin.getDir();
					break;
				}
				jdbcAdapter.setBinaryData(updateStmt, index++, archivio);
				updateStmt.setString(index++, url);
				updateStmt.setString(index++, dir);

				updateStmt.setLong(index++, idParent);
				updateStmt.setString(index++, plugin.getNome());
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
			case DELETE:
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRO_PLUGINS_ARCHIVE);
				sqlQueryObject.addWhereCondition("id_plugin = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setLong(index++, idParent);
				updateStmt.setString(index++, plugin.getNome());
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
			}


		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPluginArchivio] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRegistroPluginArchivio] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}

	}
	
}
