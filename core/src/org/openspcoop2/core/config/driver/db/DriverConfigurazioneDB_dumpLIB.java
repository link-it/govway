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

import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_configDumpLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_dumpLIB {


	
	static void CRUDDumpConfigurazione(int type, Connection con, DumpConfigurazione dumpConfig, 
			Long idProprietario, String tipoProprietario) throws DriverConfigurazioneException {
		
		PreparedStatement updateStmt = null;
		try {
			switch (type) {
			case CREATE:
		
				if(dumpConfig==null) {
					break;
				}
				
				long idRequestIn = -1;
				if(dumpConfig.getRichiestaIngresso()!=null) {
					idRequestIn = createDumpConfigurazioneRegola(dumpConfig.getRichiestaIngresso(), con);
				}
				
				long idRequestOut = -1;
				if(dumpConfig.getRichiestaUscita()!=null) {
					idRequestOut = createDumpConfigurazioneRegola(dumpConfig.getRichiestaUscita(), con);
				}
				
				long idResponseIn = -1;
				if(dumpConfig.getRispostaIngresso()!=null) {
					idResponseIn = createDumpConfigurazioneRegola(dumpConfig.getRispostaIngresso(), con);
				}
				
				long idResponseOut = -1;
				if(dumpConfig.getRispostaUscita()!=null) {
					idResponseOut = createDumpConfigurazioneRegola(dumpConfig.getRispostaUscita(), con);
				}
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.DUMP_CONFIGURAZIONE);
				sqlQueryObject.addInsertField("proprietario", "?");
				sqlQueryObject.addInsertField("id_proprietario", "?");
				sqlQueryObject.addInsertField("dump_realtime", "?");
				sqlQueryObject.addInsertField("id_richiesta_ingresso", "?");
				sqlQueryObject.addInsertField("id_richiesta_uscita", "?");
				sqlQueryObject.addInsertField("id_risposta_ingresso", "?");
				sqlQueryObject.addInsertField("id_risposta_uscita", "?");
				String updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setString(index++, tipoProprietario);
				updateStmt.setLong(index++, idProprietario!=null ? idProprietario : -1);
				updateStmt.setString(index++, DriverConfigurazioneDB_LIB.getValue(dumpConfig.getRealtime()));
				updateStmt.setLong(index++, idRequestIn);
				updateStmt.setLong(index++, idRequestOut);
				updateStmt.setLong(index++, idResponseIn);
				updateStmt.setLong(index++, idResponseOut);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				break;
				
			case UPDATE:
				
				// Per la delete recupero l'immagine attuale del confi
				DumpConfigurazione dumpConfigOld = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, idProprietario, tipoProprietario);
				if(dumpConfigOld!=null) {
					CRUDDumpConfigurazione(DELETE, con, dumpConfigOld, idProprietario, tipoProprietario);
				}
				
				// Creo la nuova immagine
				if(dumpConfig!=null) {
					CRUDDumpConfigurazione(CREATE, con, dumpConfig, idProprietario, tipoProprietario);
				}
				break;
				
			case DELETE:
				
				if(dumpConfig==null) {
					break;
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				
				if(dumpConfig.getRichiestaIngresso()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRichiestaIngresso().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				if(dumpConfig.getRichiestaUscita()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRichiestaUscita().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				if(dumpConfig.getRispostaIngresso()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRispostaIngresso().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				if(dumpConfig.getRispostaUscita()!=null) {
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, dumpConfig.getRispostaUscita().getId());
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DUMP_CONFIGURAZIONE);
				if(!CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario) &&
						!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD.equals(tipoProprietario) &&
						!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA.equals(tipoProprietario)) {
					sqlQueryObject.addWhereCondition("id_proprietario=?");
				}
				sqlQueryObject.addWhereCondition("proprietario=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				if(!CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario) &&
						!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD.equals(tipoProprietario) &&
						!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA.equals(tipoProprietario)) {
					updateStmt.setLong(index++, idProprietario);
				}
				updateStmt.setString(index++, tipoProprietario);
				updateStmt.executeUpdate();
				
				break;
			}
		
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConfigurazioneGenerale] Exception [" + se.getMessage() + "].",se);
		} finally {
	
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
			
		}
	}
	
	private static long createDumpConfigurazioneRegola(DumpConfigurazioneRegola dumpRegola, Connection con) throws Exception {
		List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("body", DriverConfigurazioneDB_LIB.getValue(dumpRegola.getBody()) , InsertAndGeneratedKeyJDBCType.STRING) );
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("payload", DriverConfigurazioneDB_LIB.getValue(dumpRegola.getPayload()) , InsertAndGeneratedKeyJDBCType.STRING) );
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("payload_parsing", DriverConfigurazioneDB_LIB.getValue(dumpRegola.getPayloadParsing()) , InsertAndGeneratedKeyJDBCType.STRING) );
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("attachments", DriverConfigurazioneDB_LIB.getValue(dumpRegola.getAttachments()) , InsertAndGeneratedKeyJDBCType.STRING) );
		listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("headers", DriverConfigurazioneDB_LIB.getValue(dumpRegola.getHeaders()) , InsertAndGeneratedKeyJDBCType.STRING) );
		
		long idDumpconfigurazioneRegola = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(DriverConfigurazioneDB_LIB.tipoDB), 
				new CustomKeyGeneratorObject(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA, CostantiDB.DUMP_CONFIGURAZIONE_REGOLA_COLUMN_ID, 
						CostantiDB.DUMP_CONFIGURAZIONE_REGOLA_SEQUENCE, CostantiDB.DUMP_CONFIGURAZIONE_REGOLA_TABLE_FOR_ID),
				listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
		if(idDumpconfigurazioneRegola<=0){
			throw new Exception("ID (dump configurazione regola) autoincrementale non ottenuto");
		}
		return idDumpconfigurazioneRegola;
	}
	
	protected static DumpConfigurazione readDumpConfigurazione(Connection con, Long idProprietario, String tipoProprietario) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DUMP_CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			if(!CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario) &&
					!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD.equals(tipoProprietario) &&
					!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA.equals(tipoProprietario)) {
				sqlQueryObject.addWhereCondition("id_proprietario=?");
			}
			sqlQueryObject.addWhereCondition("proprietario=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm1 = con.prepareStatement(sqlQuery);
			int index = 1;
			if(!CostantiDB._DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG.equals(tipoProprietario) &&
					!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PD.equals(tipoProprietario) &&
					!CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG_PA.equals(tipoProprietario)) {
				stm1.setLong(index++, idProprietario);
			}
			stm1.setString(index++, tipoProprietario);
			rs1 = stm1.executeQuery();
			
			//recuper tutti gli appender e le prop di ogni appender
			DumpConfigurazione dumpConfig = null;
			if(rs1.next()){
				
				dumpConfig = new DumpConfigurazione();
				
				dumpConfig.setId(rs1.getLong("id"));
				
				dumpConfig.setRealtime(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("dump_realtime")));
				
				long idRequestIn = rs1.getLong("id_richiesta_ingresso");
				if(idRequestIn>0) {
					dumpConfig.setRichiestaIngresso(readDumpConfigurazioneRegola(con, idRequestIn));
				}
				
				long idRequestOut = rs1.getLong("id_richiesta_uscita");
				if(idRequestOut>0) {
					dumpConfig.setRichiestaUscita(readDumpConfigurazioneRegola(con, idRequestOut));
				}
				
				long idResponseIn = rs1.getLong("id_risposta_ingresso");
				if(idResponseIn>0) {
					dumpConfig.setRispostaIngresso(readDumpConfigurazioneRegola(con, idResponseIn));
				}
				
				long idResponseOut = rs1.getLong("id_risposta_uscita");
				if(idResponseOut>0) {
					dumpConfig.setRispostaUscita(readDumpConfigurazioneRegola(con, idResponseOut));
				}
				
			}
			return dumpConfig;

		}finally {
			try {
				if(rs1!=null) {
					rs1.close();
				}
			}catch(Exception e) {
				// close
			}
			try {
				if(stm1!=null) {
					stm1.close();
				}
			}catch(Exception e) {
				// close
			}
		}
	}
	
	private static DumpConfigurazioneRegola readDumpConfigurazioneRegola(Connection con, long idRegola) throws Exception {
		PreparedStatement stm1=null;
		ResultSet rs1= null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			
			stm1 = con.prepareStatement(sqlQuery);
			stm1.setLong(1, idRegola);
			rs1 = stm1.executeQuery();
			
			//recuper tutti gli appender e le prop di ogni appender
			DumpConfigurazioneRegola dumpConfig = new DumpConfigurazioneRegola(); // ci sono i default
			if(rs1.next()){
				
				dumpConfig.setId(rs1.getLong("id"));
				dumpConfig.setBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("body")));
				dumpConfig.setPayload(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("payload")));
				dumpConfig.setPayloadParsing(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("payload_parsing")));
				dumpConfig.setAttachments(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("attachments")));
				dumpConfig.setHeaders(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("headers")));
				
			}

			return dumpConfig;

		}finally {
			try {
				if(rs1!=null) {
					rs1.close();
				}
			}catch(Exception e) {
				// close
			}
			try {
				if(stm1!=null) {
					stm1.close();
				}
			}catch(Exception e) {
				// close
			}
		}
	}
	

	
}
