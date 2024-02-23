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
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.core.config.TracciamentoConfigurazione;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletrace;
import org.openspcoop2.core.config.TracciamentoConfigurazioneFiletraceConnector;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverConfigurazioneDBTracciamentoLIB
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDBTracciamentoLIB {
	
	private DriverConfigurazioneDBTracciamentoLIB() {}


	static void crudTracciamentoConfigurazione(int type, Connection con, TracciamentoConfigurazione tracciamentoConfig, 
			Long idProprietario, String tipoProprietario, String tipoConfigurazione) throws DriverConfigurazioneException {
		
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(tracciamentoConfig==null) {
					break;
				}
				
				createTracciamentoConfigurazione(con, tracciamentoConfig, 
						idProprietario, tipoProprietario, tipoConfigurazione);
				
				break;
				
			case UPDATE:
				
				// Per la delete recupero l'immagine attuale del config
				TracciamentoConfigurazione tracciamentoConfigOld = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazione(con, idProprietario, tipoProprietario, tipoConfigurazione);
				if(tracciamentoConfigOld!=null) {
					crudTracciamentoConfigurazione(DELETE, con, tracciamentoConfigOld, idProprietario, tipoProprietario, tipoConfigurazione);
				}
				
				// Creo la nuova immagine
				if(tracciamentoConfig!=null) {
					crudTracciamentoConfigurazione(CREATE, con, tracciamentoConfig, idProprietario, tipoProprietario, tipoConfigurazione);
				}
				break;
				
			case DELETE:
				
				if(tracciamentoConfig==null) {
					break;
				}
								
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE);
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_PROPRIETARIO+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_TIPO+"=?");
				if(idProprietario!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_ID_PROPRIETARIO+"=?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				String updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setString(index++, tipoProprietario);
				updateStmt.setString(index++, tipoConfigurazione);
				if(idProprietario!=null) {
					updateStmt.setLong(index++, idProprietario);
				}
				updateStmt.executeUpdate();
				
				break;
				
			default: 
				break;
			
			}
					
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::crudTracciamentoConfigurazione] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::crudTracciamentoConfigurazione] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			JDBCUtilities.closeResources(updateStmt);
			
		}
	}
		
	private static void createTracciamentoConfigurazione(Connection con, TracciamentoConfigurazione tracciamentoConfig, 
			Long idProprietario, String tipoProprietario, String tipoConfigurazione) throws SQLException, SQLQueryObjectException {
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
		sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE);
		sqlQueryObject.addInsertField(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_PROPRIETARIO, "?");
		sqlQueryObject.addInsertField(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_TIPO, "?");
		sqlQueryObject.addInsertField(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_ID_PROPRIETARIO, "?");
		sqlQueryObject.addInsertField("stato", "?");
		sqlQueryObject.addInsertField("filtro_esiti", "?");
		sqlQueryObject.addInsertField("request_in", "?");
		sqlQueryObject.addInsertField("request_out", "?");
		sqlQueryObject.addInsertField("response_out", "?");
		sqlQueryObject.addInsertField("response_out_complete", "?");
		
		PreparedStatement updateStmt = null;
		try {
			String updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setString(index++, tipoProprietario);
			updateStmt.setString(index++, tipoConfigurazione);
			updateStmt.setLong(index++, idProprietario!=null ? idProprietario : -1);
			updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getStato()));
			updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getFiltroEsiti()));
			updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getRequestIn()));
			updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getRequestOut()));
			updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getResponseOut()));
			updateStmt.setString(index++, DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getResponseOutComplete()));
			updateStmt.executeUpdate();
			updateStmt.close();
			updateStmt=null;
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	protected static TracciamentoConfigurazione readTracciamentoConfigurazione(Connection con, Long idProprietario, String tipoProprietario, String tipoConfigurazione) throws DriverConfigurazioneException {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_PROPRIETARIO+"=?");
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_TIPO+"=?");
			if(idProprietario!=null) {
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_COLUMN_ID_PROPRIETARIO+"=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm1 = con.prepareStatement(sqlQuery);
			int index = 1;
			stm1.setString(index++, tipoProprietario);
			stm1.setString(index++, tipoConfigurazione);
			if(idProprietario!=null) {
				stm1.setLong(index++, idProprietario);
			}
			rs1 = stm1.executeQuery();
			
			TracciamentoConfigurazione tracciamentoConfig = null;
			if(rs1.next()){
				
				tracciamentoConfig = new TracciamentoConfigurazione();
				
				tracciamentoConfig.setId(rs1.getLong("id"));
				
				tracciamentoConfig.setStato(DriverConfigurazioneDBLib.getEnumStatoFunzionalitaConPersonalizzazione(rs1.getString("stato")));
				tracciamentoConfig.setFiltroEsiti(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("filtro_esiti")));
				tracciamentoConfig.setRequestIn(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("request_in")));
				tracciamentoConfig.setRequestOut(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("request_out")));
				tracciamentoConfig.setResponseOut(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("response_out")));
				tracciamentoConfig.setResponseOutComplete(DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("response_out_complete")));
				
			}
			return tracciamentoConfig;

		}catch (Exception se) {
			throw new DriverConfigurazioneException(se.getMessage(),se);
		}finally {
			JDBCUtilities.closeResources(rs1, stm1);
		}
	}
	
	
	
	
	
	
	static void crudTracciamentoConfigurazioneFiletrace(int type, Connection con, TracciamentoConfigurazioneFiletrace tracciamentoConfig, 
			Long idProprietario, String tipoProprietario) throws DriverConfigurazioneException {
		
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(tracciamentoConfig==null) {
					break;
				}
				
				createTracciamentoConfigurazioneFiletrace(con, tracciamentoConfig, 
						idProprietario, tipoProprietario);
				
				break;
				
			case UPDATE:
				
				// Per la delete recupero l'immagine attuale del config
				TracciamentoConfigurazioneFiletrace tracciamentoConfigOld = DriverConfigurazioneDBTracciamentoLIB.readTracciamentoConfigurazioneFiletrace(con, idProprietario, tipoProprietario);
				if(tracciamentoConfigOld!=null) {
					crudTracciamentoConfigurazioneFiletrace(DELETE, con, tracciamentoConfigOld, idProprietario, tipoProprietario);
				}
				
				// Creo la nuova immagine
				if(tracciamentoConfig!=null) {
					crudTracciamentoConfigurazioneFiletrace(CREATE, con, tracciamentoConfig, idProprietario, tipoProprietario);
				}
				break;
				
			case DELETE:
				
				if(tracciamentoConfig==null) {
					break;
				}
								
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE);
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE_COLUMN_PROPRIETARIO+"=?");
				if(idProprietario!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE_COLUMN_ID_PROPRIETARIO+"=?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				String updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setString(index++, tipoProprietario);
				if(idProprietario!=null) {
					updateStmt.setLong(index++, idProprietario);
				}
				updateStmt.executeUpdate();
				
				break;
				
			default: 
				break;
			
			}
					
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::crudTracciamentoConfigurazioneFiletrace] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::crudTracciamentoConfigurazioneFiletrace] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			JDBCUtilities.closeResources(updateStmt);
			
		}
	}
	
	private static void createTracciamentoConfigurazioneFiletrace(Connection con, TracciamentoConfigurazioneFiletrace tracciamentoConfig, 
			Long idProprietario, String tipoProprietario) throws SQLException, SQLQueryObjectException {
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
		sqlQueryObject.addInsertTable(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE);
		sqlQueryObject.addInsertField(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE_COLUMN_PROPRIETARIO, "?");
		sqlQueryObject.addInsertField(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE_COLUMN_ID_PROPRIETARIO, "?");
		sqlQueryObject.addInsertField("config", "?");
		sqlQueryObject.addInsertField("dump_in_stato", "?");
		sqlQueryObject.addInsertField("dump_in_stato_hdr", "?");
		sqlQueryObject.addInsertField("dump_in_stato_body", "?");
		sqlQueryObject.addInsertField("dump_out_stato", "?");
		sqlQueryObject.addInsertField("dump_out_stato_hdr", "?");
		sqlQueryObject.addInsertField("dump_out_stato_body", "?");
		
		PreparedStatement updateStmt = null;
		try {
			String updateQuery = sqlQueryObject.createSQLInsert();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setString(index++, tipoProprietario);
			updateStmt.setLong(index++, idProprietario!=null ? idProprietario : -1);
			updateStmt.setString(index++, tracciamentoConfig.getConfig());
			updateStmt.setString(index++, tracciamentoConfig.getDumpIn()!=null ? DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getDumpIn().getStato()) : null);
			updateStmt.setString(index++, tracciamentoConfig.getDumpIn()!=null ? DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getDumpIn().getHeader()) : null);
			updateStmt.setString(index++, tracciamentoConfig.getDumpIn()!=null ? DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getDumpIn().getPayload()) : null);
			updateStmt.setString(index++, tracciamentoConfig.getDumpOut()!=null ? DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getDumpOut().getStato()) : null);
			updateStmt.setString(index++, tracciamentoConfig.getDumpOut()!=null ? DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getDumpOut().getHeader()) : null);
			updateStmt.setString(index++, tracciamentoConfig.getDumpOut()!=null ? DriverConfigurazioneDBLib.getValue(tracciamentoConfig.getDumpOut().getPayload()) : null);
			
			updateStmt.executeUpdate();
			updateStmt.close();
			updateStmt=null;
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
		
	protected static TracciamentoConfigurazioneFiletrace readTracciamentoConfigurazioneFiletrace(Connection con, Long idProprietario, String tipoProprietario) throws DriverConfigurazioneException {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE_COLUMN_PROPRIETARIO+"=?");
			if(idProprietario!=null) {
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCIAMENTO_CONFIGURAZIONE_FILETRACE_COLUMN_ID_PROPRIETARIO+"=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm1 = con.prepareStatement(sqlQuery);
			int index = 1;
			stm1.setString(index++, tipoProprietario);
			if(idProprietario!=null) {
				stm1.setLong(index++, idProprietario);
			}
			rs1 = stm1.executeQuery();
			
			TracciamentoConfigurazioneFiletrace tracciamentoConfig = null;
			if(rs1.next()){
				
				tracciamentoConfig = new TracciamentoConfigurazioneFiletrace();
				
				tracciamentoConfig.setId(rs1.getLong("id"));
				
				tracciamentoConfig.setConfig(rs1.getString("config"));
				
				StatoFunzionalita inStato = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("dump_in_stato"));
				StatoFunzionalita inStatoHdr = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("dump_in_stato_hdr"));
				StatoFunzionalita inStatoBody = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("dump_in_stato_body"));
				if(inStato!=null || inStatoHdr!=null || inStatoBody!=null) {
					tracciamentoConfig.setDumpIn(new TracciamentoConfigurazioneFiletraceConnector());
					tracciamentoConfig.getDumpIn().setStato(inStato);
					tracciamentoConfig.getDumpIn().setHeader(inStatoHdr);
					tracciamentoConfig.getDumpIn().setPayload(inStatoBody);
				}
				
				StatoFunzionalita outStato = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("dump_out_stato"));
				StatoFunzionalita outStatoHdr = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("dump_out_stato_hdr"));
				StatoFunzionalita outStatoBody = DriverConfigurazioneDBLib.getEnumStatoFunzionalita(rs1.getString("dump_out_stato_body"));
				if(outStato!=null || outStatoHdr!=null || outStatoBody!=null) {
					tracciamentoConfig.setDumpOut(new TracciamentoConfigurazioneFiletraceConnector());
					tracciamentoConfig.getDumpOut().setStato(outStato);
					tracciamentoConfig.getDumpOut().setHeader(outStatoHdr);
					tracciamentoConfig.getDumpOut().setPayload(outStatoBody);
				}

			}
			return tracciamentoConfig;

		}catch (Exception se) {
			throw new DriverConfigurazioneException(se.getMessage(),se);
		}finally {
			JDBCUtilities.closeResources(rs1, stm1);
		}
	}

	
}
