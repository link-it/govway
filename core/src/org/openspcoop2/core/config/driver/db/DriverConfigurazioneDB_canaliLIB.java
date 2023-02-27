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
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_canaliLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_canaliLIB {


	protected static void readCanaliConfigurazione(Connection con, CanaliConfigurazione configCanali, boolean readNodi) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm1 = con.prepareStatement(sqlQuery);
			rs1 = stm1.executeQuery();
			while(rs1.next()){
				
				CanaleConfigurazione canale = new CanaleConfigurazione();
				canale.setId(rs1.getLong("id"));
				canale.setNome(rs1.getString("nome"));
				canale.setDescrizione(rs1.getString("descrizione"));
				int v = rs1.getInt("canale_default");
				if(v == CostantiDB.TRUE) {
					canale.setCanaleDefault(true);
				}
				else {
					canale.setCanaleDefault(false);
				}
				configCanali.addCanale(canale);
				
			}
			rs1.close();
			stm1.close();

			if(readNodi) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				while(rs1.next()){
					
					CanaleConfigurazioneNodo nodo = new CanaleConfigurazioneNodo();
					nodo.setId(rs1.getLong("id"));
					nodo.setNome(rs1.getString("nome"));
					nodo.setDescrizione(rs1.getString("descrizione"));
					List<String> l = DBUtils.convertToList(rs1.getString("canali"));
					nodo.setCanaleList(l);
					configCanali.addNodo(nodo);
					
				}
			}

		}finally {
			JDBCUtilities.closeResources(rs1, stm1);
		}
	}
	
	static void CRUDCanaliConfigurazione(int type, Connection con, CanaliConfigurazione canaliConfigurazione) throws DriverConfigurazioneException {
		
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(canaliConfigurazione==null) {
					break;
				}
				
				if(canaliConfigurazione.sizeCanaleList()>0) {
					for (CanaleConfigurazione canale : canaliConfigurazione.getCanaleList()) {
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE_CANALI);
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("descrizione", "?");
						sqlQueryObject.addInsertField("canale_default", "?");
						String updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						int index = 1;
						updateStmt.setString(index++, canale.getNome());
						updateStmt.setString(index++, canale.getDescrizione());
						updateStmt.setInt(index++, canale.isCanaleDefault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}
				
				if(canaliConfigurazione.sizeNodoList()>0) {
					for (CanaleConfigurazioneNodo nodo : canaliConfigurazione.getNodoList()) {
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("descrizione", "?");
						sqlQueryObject.addInsertField("canali", "?");
						String updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						int index = 1;
						updateStmt.setString(index++, nodo.getNome());
						updateStmt.setString(index++, nodo.getDescrizione());
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < nodo.sizeCanaleList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(nodo.getCanale(i));
						}
						String canali = bf.toString();
						updateStmt.setString(index++, canali);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}
				
				break;
				
			case UPDATE:
				
				// Faccio prima delete
				CRUDCanaliConfigurazione(DELETE, con, canaliConfigurazione);
				
				// Creo la nuova immagine
				if(canaliConfigurazione!=null) {
					CRUDCanaliConfigurazione(CREATE, con, canaliConfigurazione);
				}
				break;
				
			case DELETE:
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CANALI);
				sqlQueryObject.setANDLogicOperator(true);
				String updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.executeUpdate();
				updateStmt.close();

				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDCanaliConfigurazione] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDCanaliConfigurazione] Exception [" + se.getMessage() + "].",se);
		} finally {
			JDBCUtilities.closeResources(updateStmt);			
		}
	}
	
	
	
}
