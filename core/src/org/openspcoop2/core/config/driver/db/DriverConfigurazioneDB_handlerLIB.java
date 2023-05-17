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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.ConfigurazioneMessageHandlers;
import org.openspcoop2.core.config.ConfigurazioneServiceHandlers;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_handlerLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_handlerLIB {



	protected static ConfigurazioneMessageHandlers readConfigurazioneMessageHandlers(Connection con, Long idPortaDelegata, Long idPortaApplicatva, boolean request) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			
			ConfigurazioneMessageHandlers config = null;
			
			String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
			if(idPortaDelegata!=null) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(idPortaApplicatva!=null) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
			
			List<String> tipologie = new ArrayList<>();
			String suffix = request ? CostantiDB.HANDLER_REQUEST_SUFFIX : CostantiDB.HANDLER_RESPONSE_SUFFIX;
			tipologie.add(CostantiDB.HANDLER_PRE_IN+suffix);
			tipologie.add(CostantiDB.HANDLER_IN+suffix);
			if(request) {
				tipologie.add(CostantiDB.HANDLER_IN_PROTOCOL+suffix);
			}
			tipologie.add(CostantiDB.HANDLER_OUT+suffix);
			tipologie.add(CostantiDB.HANDLER_POST_OUT+suffix);
			
			for (String tipologia : tipologie) {
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addSelectField("*");
				if(idPortaDelegata!=null || idPortaApplicatva!=null) {
					sqlQueryObject.addWhereCondition("id_porta=?");
				}
				sqlQueryObject.addWhereCondition("tipologia=?");
				sqlQueryObject.addOrderBy("posizione");
				
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				int index = 1;
				if(idPortaDelegata!=null) {
					stm1.setLong(index++, idPortaDelegata);
				}
				else if(idPortaApplicatva!=null) {
					stm1.setLong(index++, idPortaApplicatva);
				}
				stm1.setString(index++, tipologia);
				rs1 = stm1.executeQuery();
				List<ConfigurazioneHandler> list = new ArrayList<ConfigurazioneHandler>();
				while(rs1.next()){
					
					ConfigurazioneHandler handler = new ConfigurazioneHandler();
					handler.setId(rs1.getLong("id"));
					handler.setTipo(rs1.getString("tipo"));
					handler.setPosizione(rs1.getInt("posizione"));
					String stato = rs1.getString("stato");
					if(stato!=null && !"".equals(stato)) {
						handler.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(stato));
					}
					list.add(handler);
					
				}
				rs1.close();
				stm1.close();
				
				if(!list.isEmpty()) {
					if(config==null) {
						config = new ConfigurazioneMessageHandlers();
					}
					if(tipologia.startsWith(CostantiDB.HANDLER_PRE_IN)) {
						config.setPreInList(list);
					}
					else if(tipologia.startsWith(CostantiDB.HANDLER_IN_PROTOCOL)) { // prima di in senno c'e' il bu
						config.setInProtocolInfoList(list);
					}
					else if(tipologia.startsWith(CostantiDB.HANDLER_IN)) {
						config.setInList(list);
					}
					else if(tipologia.startsWith(CostantiDB.HANDLER_OUT)) {
						config.setOutList(list);
					}
					else if(tipologia.startsWith(CostantiDB.HANDLER_POST_OUT)) {
						config.setPostOutList(list);
					}
				}
			}
			
			return config;
			
		}finally {
			JDBCUtilities.closeResources(rs1, stm1);
		}
	}
	
	protected static ConfigurazioneServiceHandlers readConfigurazioneServiceHandlers(Connection con, Long idPortaDelegata, Long idPortaApplicatva, boolean request) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			
			ConfigurazioneServiceHandlers config = null;
			
			String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
			if(idPortaDelegata!=null) {
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			}
			else if(idPortaApplicatva!=null) {
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			}
			
			List<String> tipologie = new ArrayList<>();
			tipologie.add(CostantiDB.HANDLER_INIT);
			tipologie.add(CostantiDB.HANDLER_EXIT);
			tipologie.add(CostantiDB.HANDLER_INTEGRATION_MANAGER_REQUEST);
			tipologie.add(CostantiDB.HANDLER_INTEGRATION_MANAGER_RESPONSE);
			
			for (String tipologia : tipologie) {
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addSelectField("*");
				if(idPortaDelegata!=null || idPortaApplicatva!=null) {
					sqlQueryObject.addWhereCondition("id_porta=?");
				}
				sqlQueryObject.addWhereCondition("tipologia=?");
				sqlQueryObject.addOrderBy("posizione");
				
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				int index = 1;
				if(idPortaDelegata!=null) {
					stm1.setLong(index++, idPortaDelegata);
				}
				else if(idPortaApplicatva!=null) {
					stm1.setLong(index++, idPortaApplicatva);
				}
				stm1.setString(index++, tipologia);
				rs1 = stm1.executeQuery();
				List<ConfigurazioneHandler> list = new ArrayList<ConfigurazioneHandler>();
				while(rs1.next()){
					
					ConfigurazioneHandler handler = new ConfigurazioneHandler();
					handler.setId(rs1.getLong("id"));
					handler.setTipo(rs1.getString("tipo"));
					handler.setPosizione(rs1.getInt("posizione"));
					String stato = rs1.getString("stato");
					if(stato!=null && !"".equals(stato)) {
						handler.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(stato));
					}
					list.add(handler);
					
				}
				rs1.close();
				stm1.close();
				
				if(!list.isEmpty()) {
					if(config==null) {
						config = new ConfigurazioneServiceHandlers();
					}
					if(tipologia.equals(CostantiDB.HANDLER_INIT)) {
						config.setInitList(list);
					}
					else if(tipologia.equals(CostantiDB.HANDLER_EXIT)) {
						config.setExitList(list);
					}
					else if(tipologia.equals(CostantiDB.HANDLER_INTEGRATION_MANAGER_REQUEST)) {
						config.setIntegrationManagerRequestList(list);
					}
					else if(tipologia.equals(CostantiDB.HANDLER_INTEGRATION_MANAGER_RESPONSE)) {
						config.setIntegrationManagerResponseList(list);
					}
				}
			}
			
			return config;
			
		}finally {
			JDBCUtilities.closeResources(rs1, stm1);
		}
	}
	
	static void CRUDConfigurazioneMessageHandlers(int type, Connection con, Long idPortaDelegata, Long idPortaApplicatva, boolean request, ConfigurazioneMessageHandlers config) throws DriverConfigurazioneException {
		
		Long idPorta = null;
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(idPortaDelegata!=null) {
			tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			idPorta = idPortaDelegata;
		}
		else if(idPortaApplicatva!=null) {
			tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			idPorta = idPortaApplicatva;
		}
		
		String suffix = request ? CostantiDB.HANDLER_REQUEST_SUFFIX : CostantiDB.HANDLER_RESPONSE_SUFFIX;
				
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(config==null) {
					break;
				}
				
				createConfigurazioneHandlers(con, config.getPreInList(), tabella, CostantiDB.HANDLER_PRE_IN+suffix, idPorta);
				createConfigurazioneHandlers(con, config.getInList(), tabella, CostantiDB.HANDLER_IN+suffix, idPorta);
				if(request) {
					createConfigurazioneHandlers(con, config.getInProtocolInfoList(), tabella, CostantiDB.HANDLER_IN_PROTOCOL+suffix, idPorta);
				}
				createConfigurazioneHandlers(con, config.getOutList(), tabella, CostantiDB.HANDLER_OUT+suffix, idPorta);
				createConfigurazioneHandlers(con, config.getPostOutList(), tabella, CostantiDB.HANDLER_POST_OUT+suffix, idPorta);

				break;
				
			case UPDATE:
				
				// Faccio prima delete
				CRUDConfigurazioneMessageHandlers(DELETE, con, idPortaDelegata, idPortaApplicatva, request, null);
				
				// Creo la nuova immagine
				if(config!=null) {
					CRUDConfigurazioneMessageHandlers(CREATE, con, idPortaDelegata, idPortaApplicatva, request, config);
				}
				break;
				
			case DELETE:
				
				List<String> tipologie = new ArrayList<>();
				tipologie.add(CostantiDB.HANDLER_PRE_IN+suffix);
				tipologie.add(CostantiDB.HANDLER_IN+suffix);
				if(request) {
					tipologie.add(CostantiDB.HANDLER_IN_PROTOCOL+suffix);
				}
				tipologie.add(CostantiDB.HANDLER_OUT+suffix);
				tipologie.add(CostantiDB.HANDLER_POST_OUT+suffix);
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(tabella);
				sqlQueryObject.setANDLogicOperator(true);
				if(idPorta!=null) {
					sqlQueryObject.addWhereCondition("id_porta=?");
				}
				sqlQueryObject.addWhereINCondition("tipologia", true, tipologie.toArray(new String[1]));
				String updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				if(idPorta!=null) {
					updateStmt.setLong(index++, idPorta);
				}
				updateStmt.executeUpdate();
				updateStmt.close();

				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneMessageHandlers] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneMessageHandlers] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			JDBCUtilities.closeResources(updateStmt);
			
		}
	}
	
	static void CRUDConfigurazioneServiceHandlers(int type, Connection con, Long idPortaDelegata, Long idPortaApplicatva, boolean request, ConfigurazioneServiceHandlers config) throws DriverConfigurazioneException {
		
		Long idPorta = null;
		String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
		if(idPortaDelegata!=null) {
			tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
			idPorta = idPortaDelegata;
		}
		else if(idPortaApplicatva!=null) {
			tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
			idPorta = idPortaApplicatva;
		}
		
		@SuppressWarnings("unused")
		String suffix = request ? CostantiDB.HANDLER_REQUEST_SUFFIX : CostantiDB.HANDLER_RESPONSE_SUFFIX;
				
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(config==null) {
					break;
				}
				
				createConfigurazioneHandlers(con, config.getInitList(), tabella, CostantiDB.HANDLER_INIT, idPorta);
				createConfigurazioneHandlers(con, config.getExitList(), tabella, CostantiDB.HANDLER_EXIT, idPorta);
				createConfigurazioneHandlers(con, config.getIntegrationManagerRequestList(), tabella, CostantiDB.HANDLER_INTEGRATION_MANAGER_REQUEST, idPorta);
				createConfigurazioneHandlers(con, config.getIntegrationManagerResponseList(), tabella, CostantiDB.HANDLER_INTEGRATION_MANAGER_RESPONSE, idPorta);		

				break;
				
			case UPDATE:
				
				// Faccio prima delete
				CRUDConfigurazioneServiceHandlers(DELETE, con, idPortaDelegata, idPortaApplicatva, request, null);
				
				// Creo la nuova immagine
				if(config!=null) {
					CRUDConfigurazioneServiceHandlers(CREATE, con, idPortaDelegata, idPortaApplicatva, request, config);
				}
				break;
				
			case DELETE:
				
				List<String> tipologie = new ArrayList<>();
				tipologie.add(CostantiDB.HANDLER_INIT);
				tipologie.add(CostantiDB.HANDLER_EXIT);
				tipologie.add(CostantiDB.HANDLER_INTEGRATION_MANAGER_REQUEST);
				tipologie.add(CostantiDB.HANDLER_INTEGRATION_MANAGER_RESPONSE);
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(tabella);
				sqlQueryObject.setANDLogicOperator(true);
				if(idPorta!=null) {
					sqlQueryObject.addWhereCondition("id_porta=?");
				}
				sqlQueryObject.addWhereINCondition("tipologia", true, tipologie.toArray(new String[1]));
				String updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				if(idPorta!=null) {
					updateStmt.setLong(index++, idPorta);
				}
				updateStmt.executeUpdate();
				updateStmt.close();

				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneMessageHandlers] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneMessageHandlers] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			JDBCUtilities.closeResources(updateStmt);
			
		}
	}
	
	private static void createConfigurazioneHandlers(Connection con, List<ConfigurazioneHandler> list, String tabella, String tipologia, Long idPorta) throws Exception {
		if(list!=null && !list.isEmpty()) {
			for (ConfigurazioneHandler handler : list) {
				PreparedStatement updateStmt = null;
				try {
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(tabella);
					if(idPorta!=null) {
						sqlQueryObject.addInsertField("id_porta", "?");
					}
					sqlQueryObject.addInsertField("tipologia", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("posizione", "?");
					sqlQueryObject.addInsertField("stato", "?");
					String updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					if(idPorta!=null) {
						updateStmt.setLong(index++, idPorta);
					}
					updateStmt.setString(index++, tipologia);
					updateStmt.setString(index++, handler.getTipo());
					updateStmt.setInt(index++, handler.getPosizione());
					updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(handler.getStato()));
					updateStmt.executeUpdate();
					updateStmt.close();
					updateStmt = null;
				} finally {
					JDBCUtilities.closeResources(updateStmt);	
				}
			}
		}
	}

}
