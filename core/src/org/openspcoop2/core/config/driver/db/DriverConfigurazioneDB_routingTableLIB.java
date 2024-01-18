/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_routingTableLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_routingTableLIB {

	public static void CRUDRoutingTable(int type, RoutingTable aRT, Connection con) throws DriverConfigurazioneException {
		PreparedStatement updateStmt = null;
		PreparedStatement insertStmt = null;
		PreparedStatement updateStmtSelectRegistri = null;
		ResultSet rsSelectRegistri = null;
		String updateQuery = "";

		int i = 0;

		RoutingTableDestinazione rtd = null;
		Route route = null;
		RouteGateway gw = null;
		RouteRegistro rg = null;
		String tipo = null;
		String nome = null;
		long idRoute = 0;

		try {

			switch (type) {
			case CREATE:

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("routing_enabled", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				if(aRT.getAbilitata()!=null && aRT.getAbilitata())
					updateStmt.setString(1, CostantiConfigurazione.ABILITATO.toString());
				else
					updateStmt.setString(1, CostantiConfigurazione.DISABILITATO.toString());
				DriverConfigurazioneDBLib.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, (aRT.getAbilitata()!=null && aRT.getAbilitata())));
				updateStmt.executeUpdate();
				updateStmt.close();

				// CREATE
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ROUTING);
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tiporotta", "?");
				sqlQueryObject.addInsertField("tiposoggrotta", "?");
				sqlQueryObject.addInsertField("nomesoggrotta", "?");
				sqlQueryObject.addInsertField("registrorotta", "?");
				sqlQueryObject.addInsertField("is_default", "?");
				updateQuery = sqlQueryObject.createSQLInsert();

				i = 0;
				if(aRT.getDefault()!=null){
					RoutingTableDefault rtDefault = aRT.getDefault();
					for (i = 0; i < rtDefault.sizeRouteList(); i++) {
						route = rtDefault.getRoute(i);
	
						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro
	
						updateStmt = con.prepareStatement(updateQuery);
	
						updateStmt.setString(1, null);// nn ho tipo
						updateStmt.setString(2, null);// nn ho nome
						updateStmt.setString(3, (gw != null ? "gateway" : "registro"));
						updateStmt.setString(4, (gw != null ? gw.getTipo() : null));// se
						// rotta
						// gateway
						// setto
						// tiposoggrotta
						updateStmt.setString(5, (gw != null ? gw.getNome() : null));// se
						// rotta
						// gateway
						// setto
						// nomesoggrotta
						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									if(rsSelectRegistri!=null) {
										rsSelectRegistri.close();
									}
									updateStmtSelectRegistri.close();
								}
							}
						}
						updateStmt.setLong(6, registroRotta);
	
						updateStmt.setInt(7, CostantiDB.TRUE);
	
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				DriverConfigurazioneDBLib.log.debug("Inserted " + i + " Default route.");

				for (i = 0; i < aRT.sizeDestinazioneList(); i++) {
					rtd = aRT.getDestinazione(i);
					nome = rtd.getNome();
					tipo = rtd.getTipo();

					for (int j = 0; j < rtd.sizeRouteList(); j++) {
						route = rtd.getRoute(j);
						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro

						updateStmt = con.prepareStatement(updateQuery);

						updateStmt.setString(1, tipo);
						updateStmt.setString(2, nome);
						updateStmt.setString(3, (gw != null ? "gateway" : "registro"));
						updateStmt.setString(4, (gw != null ? gw.getTipo() : null));// se
						// rotta
						// gateway
						// setto
						// tiposoggrotta
						updateStmt.setString(5, (gw != null ? gw.getNome() : null));// se
						// rotta
						// gateway
						// setto
						// nomesoggrotta
						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}
						updateStmt.setLong(6, registroRotta);

						updateStmt.setInt(7, CostantiDB.FALSE);

						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				DriverConfigurazioneDBLib.log.debug("Inserted " + i + " Destination route.");

				break;

			case 2:
				// UPDATE

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
				sqlQueryObject.addUpdateField("routing_enabled", "?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				if(aRT.getAbilitata()!=null && aRT.getAbilitata())
					updateStmt.setString(1, CostantiConfigurazione.ABILITATO.toString());
				else
					updateStmt.setString(1, CostantiConfigurazione.DISABILITATO.toString());
				DriverConfigurazioneDBLib.log.debug("eseguo query :" + DBUtils.formatSQLString(updateQuery, aRT.getAbilitata()!=null && aRT.getAbilitata()));
				updateStmt.executeUpdate();
				updateStmt.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ROUTING);
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("tiporotta", "?");
				sqlQueryObject.addUpdateField("tiposoggrotta", "?");
				sqlQueryObject.addUpdateField("nomesoggrotta", "?");
				sqlQueryObject.addUpdateField("registrorotta", "?");
				sqlQueryObject.addUpdateField("is_default", "?");
				sqlQueryObject.addWhereCondition("id = ?");
				updateQuery = sqlQueryObject.createSQLUpdate();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ROUTING);
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tiporotta", "?");
				sqlQueryObject.addInsertField("tiposoggrotta", "?");
				sqlQueryObject.addInsertField("nomesoggrotta", "?");
				sqlQueryObject.addInsertField("registrorotta", "?");
				sqlQueryObject.addInsertField("is_default", "?");
				String insertQuery = sqlQueryObject.createSQLInsert();

				/**
				 * La lista contiene tutte e sole le entry necessarie
				 */
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addDeleteTable(CostantiDB.ROUTING);
				//sqlQueryObject.addWhereCondition("is_default<>?");//cancello le rotte non di default
				String queryDelete = sqlQueryObject.createSQLDelete();
				DriverConfigurazioneDBLib.log.debug("DELETING Destination Route : "+queryDelete);
				updateStmt = con.prepareStatement(queryDelete);
				//updateStmt.setInt(1, CostantiDB.TRUE);
				int n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " Destination route.");

				i = 0;
				if(aRT.getDefault()!=null){
					RoutingTableDefault rtDefault = aRT.getDefault();
					for (i = 0; i < rtDefault.sizeRouteList(); i++) {
						route = rtDefault.getRoute(i);

						idRoute = route.getId();
	
						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro
	
						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}
	
						insertStmt = con.prepareStatement(insertQuery);
	
						insertStmt.setString(1, null);// nn ho tipo
						insertStmt.setString(2, null);// nn ho nome
						insertStmt.setString(3, (gw != null ? "gateway" : "registro"));
						insertStmt.setString(4, (gw != null ? gw.getTipo() : null));// se
						// rotta
						// gateway
						// setto
						// tiposoggrotta
						insertStmt.setString(5, (gw != null ? gw.getNome() : null));// se
						// rotta
						// gateway
						// setto
						// nomesoggrotta
	
						insertStmt.setLong(6, registroRotta);
	
						insertStmt.setInt(7, CostantiDB.TRUE);
						insertStmt.executeUpdate();
						insertStmt.close();
	
					}
				}

				DriverConfigurazioneDBLib.log.debug("Updated " + i + " Default route.");

				for (i = 0; i < aRT.sizeDestinazioneList(); i++) {
					rtd = aRT.getDestinazione(i);
					nome = rtd.getNome();
					tipo = rtd.getTipo();

					for (int j = 0; j < rtd.sizeRouteList(); j++) {
						route = rtd.getRoute(j);

						idRoute = route.getId();

						gw = route.getGateway();// rotta gateway
						rg = route.getRegistro();// rotta registro

						long registroRotta = 0;
						if(rg!=null){
							if(rg.getId()<=0){
								if(rg.getNome()!=null && ("".equals(rg.getNome())==false)){
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
									sqlQueryObject.addSelectField("id");
									sqlQueryObject.addWhereCondition("nome = ?");
									String selectQuery = sqlQueryObject.createSQLQuery();
									updateStmtSelectRegistri = con.prepareStatement(selectQuery);
									updateStmtSelectRegistri.setString(1, rg.getNome());
									rsSelectRegistri = updateStmtSelectRegistri.executeQuery();
									if(rsSelectRegistri!=null && rsSelectRegistri.next()){
										registroRotta = rsSelectRegistri.getLong("id");
									}
									rsSelectRegistri.close();
									updateStmtSelectRegistri.close();
								}
							}
						}


						insertStmt = con.prepareStatement(insertQuery);

						insertStmt.setString(1, tipo);
						insertStmt.setString(2, nome);
						insertStmt.setString(3, (gw != null ? "gateway" : "registro"));
						insertStmt.setString(4, (gw != null ? gw.getTipo() : null));
						insertStmt.setString(5, (gw != null ? gw.getNome() : null));

						insertStmt.setLong(6, registroRotta);

						insertStmt.setInt(7, CostantiDB.FALSE);
						insertStmt.executeUpdate();
						insertStmt.close();

					}
				}

				DriverConfigurazioneDBLib.log.debug("Updated " + i + " Destination route.");



				break;

			case 3:
				// DELETE
				i = 0;
				if(aRT.getDefault()!=null){
					RoutingTableDefault rtDefault = aRT.getDefault();
					for (i = 0; i < rtDefault.sizeRouteList(); i++) {
						route = rtDefault.getRoute(i);

						if (route.getId() == null || route.getId() <= 0)
							throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRoutingTable(DELETE)] id route non valida.");
						idRoute = route.getId();
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.ROUTING);
						sqlQueryObject.addWhereCondition("id=?");
						String sqlQuery = sqlQueryObject.createSQLDelete();
						updateStmt = con.prepareStatement(sqlQuery);
						updateStmt.setLong(1, idRoute);
						updateStmt.executeUpdate();
						updateStmt.close();
	
						DriverConfigurazioneDBLib.log.debug("Deleted " + i + " Destination route.");
					}
				}
				break;
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRoutingTable] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDRoutingTable] Exception [" + se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(updateStmt);
			JDBCUtilities.closeResources(insertStmt);
			JDBCUtilities.closeResources(rsSelectRegistri, updateStmtSelectRegistri);
			
		}
	}
	
}
