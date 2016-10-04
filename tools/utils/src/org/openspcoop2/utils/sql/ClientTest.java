/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Vector;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * TEST
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {

	/**
	 * Tabelle
	 * 
	 * ---------------------------
	 * postgresql
	 * CREATE SEQUENCE seq_tracce start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT DEFAULT nextval('seq_tracce') NOT NULL
	 * );
	 * CREATE SEQUENCE seq_msgdiagnostici start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT DEFAULT nextval('seq_msgdiagnostici') NOT NULL
	 * );
	 * ---------------------------
	 * 
	 * ---------------------------
	 * mysql
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	 *      gdo2 TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT AUTO_INCREMENT,
	 * 		CONSTRAINT pk_tracce PRIMARY KEY (id)
	 * );
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	 *      gdo2 TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT AUTO_INCREMENT,
	 * 		CONSTRAINT pk_msgdiagnostici PRIMARY KEY (id)
	 * );
	 * ---------------------------
	 * 
	 * ---------------------------
	 * oracle
	 * CREATE SEQUENCE seq_tracce MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id NUMBER
	 * );
	 * CREATE TRIGGER trg_tracce
	 * BEFORE
	 * insert on tracce
	 * for each row
	 * begin
	 * 	IF (:new.id IS NULL) THEN
	 * 		SELECT seq_tracce.nextval INTO :new.id
	 * 			FROM DUAL;
	 * 	END IF;
	 * end;
	 * /
	 * CREATE SEQUENCE seq_msgdiagnostici MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 INCREMENT BY 1 CACHE 2 NOCYCLE;
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id NUMBER
	 * );
	 * CREATE TRIGGER trg_msgdiagnostici
	 * BEFORE
	 * insert on msgdiagnostici
	 * for each row
	 * begin
	 * 	IF (:new.id IS NULL) THEN
	 * 		SELECT seq_msgdiagnostici.nextval INTO :new.id
	 * 			FROM DUAL;
	 * 	END IF;
	 * end;
	 * /
	 * ---------------------------
	 * 
	 * ---------------------------
	 * hsql
	 * CREATE SEQUENCE seq_tracce AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1)
	 * );
	 * CREATE TABLE tracce_init_seq (id BIGINT);
	 * INSERT INTO tracce_init_seq VALUES (NEXT VALUE FOR seq_tracce);
	 * CREATE SEQUENCE seq_msgdiagnostici AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1)
	 * );
	 * CREATE TABLE msgdiagnostici_init_seq (id BIGINT);
	 * INSERT INTO msgdiagnostici_init_seq VALUES (NEXT VALUE FOR seq_msgdiagnostici);
	 * ---------------------------
	 * 
	 * ---------------------------
	 * sqlserver
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT IDENTITY
	 * );
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 DATETIME2 DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT IDENTITY
	 * );
	 * ---------------------------
	 * 
	 * ---------------------------
	 * db2
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE)
	 * );
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 * 		id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE)
	 * );
	 * ---------------------------
	 */

	
	private static int ROW = 21;

	private static Logger log = null;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		File logFile = File.createTempFile("runSQLQueryObjectTest_", ".log");
		System.out.println("LogMessages write in "+logFile.getAbsolutePath());
		LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%m %n");
		log = LoggerWrapperFactory.getLogger(ClientTest.class);
		
		DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), new Properties(), log);
		
		TipiDatabase tipoDatabase = null;
		if(args.length>0){
			if(!"${tipoDatabase}".equals(args[0].trim())){
				tipoDatabase = TipiDatabase.toEnumConstant(args[0].trim());
			}
		}
		
		String url = null;
		String driver = null;
		String userName = null;
		String password = null;

		switch (tipoDatabase) {
		case POSTGRESQL:
			url = "jdbc:postgresql://localhost/prova";
			driver = "org.postgresql.Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case MYSQL:
			url = "jdbc:mysql://localhost/prova";
			driver = "com.mysql.jdbc.Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case ORACLE:
			url = "jdbc:oracle:thin:@localhost:1521:XE";
			driver = "oracle.jdbc.OracleDriver";
			userName = "prova";
			password = "prova";
			break;
		case HSQL:
			url = "jdbc:hsqldb:hsql://localhost:9001/";
			driver = "org.hsqldb.jdbcDriver";
			userName = "sa";
			password = "";
			break;
		case SQLSERVER:
			url = "jdbc:sqlserver://localhost:1433;databaseName=prova";
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case DB2:
			url = "jdbc:db2://127.0.0.1:50000/prova";
			driver = "com.ibm.db2.jcc.DB2Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		default:
			break;
		}
		if(args.length>1){
			String urlCustom = args[1].trim();
			if(!"${url}".equals(urlCustom)){
				url = urlCustom;
			}
		}
		if(args.length>2){
			String usernameCustom = args[2].trim();
			if(!"${username}".equals(usernameCustom)){
				userName = usernameCustom;
			}
		}
		if(args.length>3){
			String passwordCustom = args[3].trim();
			if(!"${password}".equals(passwordCustom)){
				password = passwordCustom;
			}
		}


		Class.forName(driver).newInstance();
		Connection con = null;
		Statement stmtDelete = null;
		PreparedStatement stmtInsert = null;
		try{
			con = DriverManager.getConnection(url, userName, password);

			// step1. Elimino tutte le entries

			String delete = "delete from msgdiagnostici";
			stmtDelete = con.createStatement();
			stmtDelete.execute(delete);
			stmtDelete.close();

			delete = "delete from tracce";
			stmtDelete = con.createStatement();
			stmtDelete.execute(delete);
			stmtDelete.close();


			// step2. Aggiungo entry
			for (int i = 0; i < ROW; i++) {
				String insertMsgDiagnosticoWithGdo = "INSERT INTO msgdiagnostici (descrizione,gdo,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario) VALUES (?,?,?,?,?,?,?)";
				String insertMsgDiagnosticoWithoutGdo = "INSERT INTO msgdiagnostici (descrizione,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario) VALUES (?,?,?,?,?,?)";
				String descrizione = "descrizione esempio "+i;
				if(i>9){
					// genero stessa descrizione per groupby
					descrizione =  "descrizione esempio uguale";
				}
				if(i==20){
					// genero entry per caratteri speciali like
					descrizione =  "descrizione con caratteri particolari finisce con ---- [ _ % ^ ] -----";
				}
				if(i%2==0){
					stmtInsert = con.prepareStatement(insertMsgDiagnosticoWithGdo);
				}else{
					stmtInsert = con.prepareStatement(insertMsgDiagnosticoWithoutGdo);
				}
				int index=1;
				stmtInsert.setString(index++, descrizione);
				if(i%2==0){
					stmtInsert.setTimestamp(index++, DateManager.getTimestamp());
					long increment = (long)((long)1000*(long)60*(long)60*(long)60*(long)24*(long)2*((long)(i+1)));
					stmtInsert.setTimestamp(index++, new Timestamp(DateManager.getTimeMillis()+increment+(200101*(i+1))));
				}
				else{
					stmtInsert.setTimestamp(index++, new Timestamp(DateManager.getTimeMillis()+(23*(i+1))));
				}
				stmtInsert.setString(index++, "SPC");
				stmtInsert.setString(index++, "SoggettoMittente"+(i/2));
				stmtInsert.setString(index++, "SPC");
				stmtInsert.setString(index++, "SoggettoDestinatario"+(i/2));
				stmtInsert.execute();
				stmtInsert.close();
			}

			for (int i = 0; i < ROW; i++) {
				String insertTracciaWithGdo = "INSERT INTO tracce (descrizione,gdo,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario) VALUES (?,?,?,?,?,?,?)";
				String insertTracciaWithoutGdo = "INSERT INTO tracce (descrizione,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario) VALUES (?,?,?,?,?,?)";
				String descrizione = "descrizione esempio "+i;
				if(i>9){
					// genero stessa descrizione per groupby
					descrizione =  "descrizione esempio uguale";
				}
				if(i==20){
					// genero entry per caratteri speciali like
					descrizione =  "descrizione con caratteri particolari finisce con ---- [ _ % ^ ] -----";
				}
				if(i%2==0){
					stmtInsert = con.prepareStatement(insertTracciaWithGdo);
				}else{
					stmtInsert = con.prepareStatement(insertTracciaWithoutGdo);
				}
				int index=1;
				stmtInsert.setString(index++, descrizione);
				if(i%2==0){
					stmtInsert.setTimestamp(index++, DateManager.getTimestamp());
					long increment = (long)((long)1000*(long)60*(long)60*(long)60*(long)24*(long)2*((long)(i+1)));
					stmtInsert.setTimestamp(index++, new Timestamp(DateManager.getTimeMillis()+increment+(200101*(i+1))));
				}
				else{
					stmtInsert.setTimestamp(index++, new Timestamp(DateManager.getTimeMillis()+(23*(i+1))));
				}
				stmtInsert.setString(index++, "SPC");
				stmtInsert.setString(index++, "SoggettoMittente"+(i/2));
				stmtInsert.setString(index++, "SPC");
				stmtInsert.setString(index++, "SoggettoDestinatario"+(i/2));
				stmtInsert.execute();
				stmtInsert.close();
			}


			for (int i = 0; i < 2; i++) {
				
				boolean selectForUpdate = false;
				if(i==1){
					selectForUpdate = true;
				}
				
				log.info("\n\n@@@ SELECT FOR UPDATE: "+selectForUpdate);
				
			
			
				// step3. Test UnixTime
				// a. verifico con msgdiagnostici dove e' stato inserita la data applicativamente
				testUnixTime_engine(tipoDatabase, "msgdiagnostici",con,selectForUpdate);
				// b. verifico con tracce dove la data e' stata generata tramite current_timestamp
				testUnixTime_engine(tipoDatabase, "tracce",con,selectForUpdate);		
				
				
				// step4 . Test fromTable
				testFromTable_engine(tipoDatabase, "msgdiagnostici", con,selectForUpdate);
				
				
				// step5. Test escape char like
				testLikeEscapeChar_engine(tipoDatabase,  "msgdiagnostici", con,selectForUpdate);
				
				
				// step6. Test query
				test0_engine(tipoDatabase, con,selectForUpdate);
				
				
				// step7. Test query complesse senza distinct
				test1_engine(tipoDatabase, false, con,selectForUpdate);
				
				
				// step8. Test query complesse con distinct
				test1_engine(tipoDatabase, true, con,selectForUpdate);
				
				
				// step9. Test query con unionAll senza count
				testUnion_engine(tipoDatabase, false, true, con,selectForUpdate);
				
				
				// step10. Test query con unionAll con count
				testUnion_engine(tipoDatabase, true, true, con,selectForUpdate);
				
				
				// step11. Test query con union senza count
				testUnion_engine(tipoDatabase, false, false, con,selectForUpdate);
				
				
				// step12. Test query con union con count
				testUnion_engine(tipoDatabase, true, false, con,selectForUpdate);
				
				
				// step13. Test query con unionAllWithGroupBy senza count
				testUnionWithGroupBy_engine(tipoDatabase, false, true, con,selectForUpdate);
				
				
				// step14. Test query con unionAllWithGroupBy con count
				testUnionWithGroupBy_engine(tipoDatabase, true, true, con,selectForUpdate);	
				
				
				// step15. Test query con unionWithGroupBy senza count
				testUnionWithGroupBy_engine(tipoDatabase, false, false, con,selectForUpdate);
				
				
				// step16. Test query con unionWithGroupBy con count
				testUnionWithGroupBy_engine(tipoDatabase, true, false, con,selectForUpdate);			

			}
			
		}finally{
			try{
				stmtDelete.close();
			}catch(Exception eClose){}
			try{
				stmtInsert.close();
			}catch(Exception eClose){}
			try{
				con.close();
			}catch(Exception eClose){}
		}


	}


	private static SQLQueryObjectCore createSQLQueryObjectCore(TipiDatabase tipo, boolean selectForUpdate) throws Exception{
		SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
		if(selectForUpdate){
			sqlQueryObject.setSelectForUpdate(true);
		}
		return sqlQueryObject;
	}




	private static void testUnixTime_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate) throws Exception {
		PreparedStatement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectAliasField(sqlQueryObject.getUnixTimestampConversion("gdo"),"unixtime");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			
			String testUnixTime = sqlQueryObject.createSQLQuery();
			log.info("\ntest1-"+table+" unixtime:\n\t"+testUnixTime);
			try{
				stmtQuery = con.prepareStatement(testUnixTime);
				rs = stmtQuery.executeQuery();
				int index = 0;
				if(rs.next()){
					long timeStampValue = rs.getTimestamp("gdo").getTime();
					long convertValue = rs.getLong("unixtime");
					log.info("riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+" timeValue:"+timeStampValue+") ("+convertValue+")");
					if(timeStampValue!=convertValue){
						// NOTA alcune volte oracle differisce di 1000. Non sempre, non ho capito il motivo
						if(TipiDatabase.ORACLE.equals(tipo)){
							if(timeStampValue!=(convertValue-1000)){
								throw new Exception("UnixTime test failed diff("+(convertValue-timeStampValue)+")");	
							}
						}
						else{
							throw new Exception("UnixTime test failed diff("+(convertValue-timeStampValue)+")");
						}
					}
				}
				else{
					throw new Exception("UnixTime test1 failed"); 
				}
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
			// TEST 2.
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso in group by
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectAvgTimestampField("gdo", "unixtimeavg");
			sqlQueryObject.addSelectMinTimestampField("gdo", "unixtimemin");
			sqlQueryObject.addSelectMaxTimestampField("gdo", "unixtimemax");
			sqlQueryObject.addSelectSumTimestampField("gdo", "unixtimesum");
			sqlQueryObject.addGroupBy("descrizione");
			
			testUnixTime = sqlQueryObject.createSQLQuery();
			log.info("\ntest2-"+table+" unixtime:\n\t"+testUnixTime);
			try{
				stmtQuery = con.prepareStatement(testUnixTime);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){
					log.info("riga["+(index++)+"]="+rs.getString("descrizione")+
							" [min:"+rs.getLong("unixtimemin")+" max:"+
							rs.getLong("unixtimemax")+" avg:"+
							rs.getLong("unixtimeavg")+" sum:"+
							rs.getLong("unixtimesum")+"]");
				}
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 3.
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate);
			
			String format = "yyyy-MM-dd_HH:mm:ss.SSS";
			SimpleDateFormat dateformat = new SimpleDateFormat (format);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("gdo2");
			sqlQueryObject.addSelectAliasField(sqlQueryObject.getDiffUnixTimestamp("gdo2", "gdo"),"latenza");
			sqlQueryObject.addOrderBy(sqlQueryObject.getDiffUnixTimestamp("gdo2", "gdo"));
			sqlQueryObject.setSortType(true);
			
			testUnixTime = sqlQueryObject.createSQLQuery();
			log.info("\ntest3-"+table+" unixtime:\n\t"+testUnixTime);
			long oldLatenza = 0;
			try{
				stmtQuery = con.prepareStatement(testUnixTime);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){
					long latenza = rs.getLong("latenza");
					log.info("riga["+(index++)+"]="+rs.getString("descrizione")+
							" gdo["+dateformat.format(rs.getTimestamp("gdo"))+"] gdo2["+dateformat.format(rs.getTimestamp("gdo2"))+
								"] [msLatenza:"+latenza+" humanReadable:"+
							Utilities.convertSystemTimeIntoString_millisecondi(latenza,true)+"]");
					if(latenza<=oldLatenza){
						throw new Exception("Attesa latenza con ordinamento crescente. Latenza della entry precedente aveva ["+oldLatenza+"]");
					}
					if(index>10){ 
						if(latenza<10000000000l){ 
							throw new Exception("Attesa latenza enorme, riscontro non avvenuto");
						}
					}
					oldLatenza = latenza;
				}
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}

		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	private static void testFromTable_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate) throws Exception {
		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			
			SQLQueryObjectCore sqlQueryObjectExternal = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false);
			sqlQueryObjectExternal.addFromTable(sqlQueryObject);
			
			String test = sqlQueryObjectExternal.createSQLQuery();
			log.info("\ntest-"+table+" fromTable:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					log.info("riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	private static void testLikeEscapeChar_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate) throws Exception {
		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.addWhereLikeCondition("descrizione", "[ _ % ^ ]", true, true);
			
			String test = sqlQueryObject.createSQLQuery();
			log.info("\ntest-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					log.info("riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
	}
	
	



	private static void test0_engine(TipiDatabase tipo, Connection con, boolean selectForUpdate) throws Exception {

		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			
			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso in group by

			sqlQueryObject.addFromTable("tracce");
			sqlQueryObject.addFromTable("msgdiagnostici","aliasMSG");

			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectCountField("aliasMSG","id", "cont", true);
			sqlQueryObject.addSelectAvgField("aliasMSG","id", "avgMedio");
			sqlQueryObject.addSelectField("aliasMSG","mittente");
			sqlQueryObject.addSelectAliasField("aliasMSG","destinatario", "ALIASDEST");

			sqlQueryObject.addWhereIsNotNullCondition("tracce.tipo_mittente");

			sqlQueryObject.addGroupBy("aliasMSG.mittente");
			sqlQueryObject.addGroupBy("ALIASDEST");
			sqlQueryObject.addOrderBy("cont",false);
			sqlQueryObject.addOrderBy("avgMedio");
			sqlQueryObject.addOrderBy("mittente",true);
			sqlQueryObject.addOrderBy("ALIASDEST",false);
			sqlQueryObject.setSortType(true);

			//log.info("["+tipo.toString()+"] getField: \t"+sqlQueryObject.getFields());
			Vector<String> trovato = sqlQueryObject.getFieldsName();
			log.info("(test0_engine) ["+tipo.toString()+"] getFieldsName: \t"+trovato);
			String atteso = "cont, mittente, ALIASDEST";
			if(atteso.equals(trovato.toString())){
				throw new Exception("Test failed (getFieldsName) trovato["+trovato.toString()+"] atteso["+atteso+"]");
			}
			
			//log.info("["+tipo.toString()+"] getTables: \t"+sqlQueryObject.getTables());
			trovato = sqlQueryObject.getTablesName();
			log.info("(test0_engine) ["+tipo.toString()+"] getTablesName: \t"+trovato);
			atteso = "tracce, aliasMSG";
			if(atteso.equals(trovato.toString())){
				throw new Exception("Test failed (getTablesName) trovato["+trovato.toString()+"] atteso["+atteso+"]");
			}
			
			log.info("");

			
			String test = sqlQueryObject.createSQLQuery();
			log.info("\ntest0_engine:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+
							"->"+rs.getString("ALIASDEST")+") (count:"+rs.getLong("cont")+")");
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+
								"->"+rs.getString("ALIASDEST")+") (count:"+rs.getLong("cont")+")");
						
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
	}



	@SuppressWarnings("resource")
	private static void test1_engine(TipiDatabase tipo, boolean distinct, Connection con, boolean selectForUpdate) throws Exception {


		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			
			log.info("\n\na. ** Query normale");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,(selectForUpdate && !distinct)); // distinct non supporta forUpdate

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField("tracce","tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("tracce.destinatario");
			sqlQueryObject.addSelectField("mittente");

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QueryNormale]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");			
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				if(distinct){
					if(index!=(((ROW/2)+1))){
						throw new Exception("Test failed (expected "+(((ROW/2)+1))+" rows, found:"+(index)+")"); 
					}
				}else{
					if(index!=(ROW)){
						throw new Exception("Test failed (expected "+ROW+" rows, found:"+(index)+")"); 
					}
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 2.
			
			log.info("\n\nb. ** Query con limit/offset");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField("tracce","tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("tracce.destinatario");
			sqlQueryObject.addSelectField("mittente");

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			int limit = 10;
			
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(0);

			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QueryLimitOffset]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(index == 0 || index==1){
						// verifico che l'offset=0 abbia funzionato
						String mittente = rs.getString("mittente");
						if(mittente.endsWith("0")==false){
							throw new Exception("Test failed (atteso un nome di mittente che termina con 0)"); 
						}
					}
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");					
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				
				if(index!=(limit)){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
			
					
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 2b (con unixTimestamp).
			
			log.info("\n\nb. ** Query con limit/offset (unixTimestamp)");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipo);

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField("tracce","tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("tracce.destinatario");
			sqlQueryObject.addSelectField("mittente");
			
			sqlQueryObject.addSelectAliasField(sqlQueryObject.getDiffUnixTimestamp("gdo2", "gdo"),"latenza");

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.addOrderBy(sqlQueryObject.getDiffUnixTimestamp("gdo2", "gdo"));
			sqlQueryObject.setSortType(true);

			int limit = 10;
			
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(0);

			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QueryLimitOffset]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(index == 0 || index==1){
						// verifico che l'offset=0 abbia funzionato
						String mittente = rs.getString("mittente");
						if(mittente.endsWith("0")==false){
							throw new Exception("Test failed (atteso un nome di mittente che termina con 0)"); 
						}
					}
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")("+rs.getLong("latenza")+")");
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")("+rs.getLong("latenza")+")");					
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				
				if(index!=(limit)){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
			
					
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 3.
			
			log.info("\n\nc. ** Query con limit");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField("tracce","tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("tracce.destinatario");
			sqlQueryObject.addSelectField("mittente");

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			int limit = 10;
			
			sqlQueryObject.setLimit(limit);

			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QueryLimit]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(index == 0 || index==1){
						// verifico che l'offset=0 abbia funzionato
						String mittente = rs.getString("mittente");
						if(mittente.endsWith("0")==false){
							throw new Exception("Test failed (atteso un nome di mittente che termina con 0)"); 
						}
					}
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");						
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
			
				if(index!=(limit)){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
				
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 4.
			
			log.info("\n\nd. ** Query con offset");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField("tracce","tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("tracce.destinatario");
			sqlQueryObject.addSelectField("mittente");

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setOffset(2);
			
			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QueryOffset]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(distinct){
						if(index == 0){
							// verifico che l'offset=0 abbia funzionato
							String mittente = rs.getString("mittente");
							if(mittente.endsWith("10")==false){
								throw new Exception("Test failed (atteso un nome di mittente che termina con 10)"); 
							}
						}
						if(index == 1){
							// verifico che l'offset=0 abbia funzionato
							String mittente = rs.getString("mittente");
							if(mittente.endsWith("2")==false){
								throw new Exception("Test failed (atteso un nome di mittente che termina con 2)"); 
							}
						}
					}
					else{
						if(index == 0 || index==1){
							// verifico che l'offset=0 abbia funzionato
							String mittente = rs.getString("mittente");
							if(mittente.endsWith("1")==false){
								throw new Exception("Test failed (atteso un nome di mittente che termina con 1)"); 
							}
						}
					}
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");					
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				
				if(distinct){
					if(index!=((((ROW-2)/2)))){
						throw new Exception("Test failed (expected "+((((ROW-2)/2)))+" rows, found:"+(index)+")"); 
					}
				}else{
					if(index!=(ROW-2)){
						throw new Exception("Test failed (expected "+(ROW-2)+" rows, found:"+(index)+")"); 
					}
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 5.
			
			log.info("\n\ne. ** Query groupBy");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);  // forUpdate non permesso in group by

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectCountField("id", "cont", true);
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectAliasField("tracce.tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("destinatario");

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("TIPODEST");
			sqlQueryObject.addGroupBy("tracce.destinatario");

			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [GroupBy]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");
					if(2!=rs.getLong("cont")){
						throw new Exception("Test failed row["+(index)+"] found:"+rs.getLong("cont")+" expected:2"); 
					}
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");		
						if(rs.getString("mittente").endsWith("10")){
							if(1!=rs.getLong("cont")){
								throw new Exception("Test failed row["+(index)+"] found:"+rs.getLong("cont")+" expected:1"); 
							}
						}
						else{
							if(2!=rs.getLong("cont")){
								throw new Exception("Test failed row["+(index)+"] found:"+rs.getLong("cont")+" expected:2"); 
							}
						}
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				if(index!=((ROW/2)+1)){
					throw new Exception("Test failed (expected "+((ROW/2)+1)+" rows, found:"+(index)+")"); 
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}

		
		
		
		
			
		
		try{
			
			// TEST 6.
			
			log.info("\n\nf. ** Query groupBy con limit/offset");
		
			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);  // forUpdate non permesso in group by
		
			sqlQueryObject.addFromTable("tracce");
		
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectCountField("id", "cont", true);
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectAliasField("tracce.tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("destinatario");
		
			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");
		
			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("TIPODEST",false);
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);
		
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("TIPODEST");
			sqlQueryObject.addGroupBy("tracce.destinatario");
		
			int limit = 5;
			
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(1);

			String test = sqlQueryObject.createSQLQuery();
			log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [GroupByLimitOffset]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");
					if(2!=rs.getLong("cont")){
						throw new Exception("Test failed row["+((index-1))+"] found:"+rs.getLong("cont")+" expected:2"); 
					}
					while(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");		
						if(rs.getString("mittente").endsWith("10")){
							if(1!=rs.getLong("cont")){
								throw new Exception("Test failed row["+((index-1))+"] found:"+rs.getLong("cont")+" expected:1"); 
							}
						}
						else{
							if(2!=rs.getLong("cont")){
								throw new Exception("Test failed row["+((index-1))+"] found:"+rs.getLong("cont")+" expected:2"); 
							}
						}
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				if(index!=limit){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 7.
			

			log.info("\n\ng. ** Query con select*");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,(selectForUpdate && !distinct));

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("tipo_destinatario");
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			String test = null;
			try{
				test = sqlQueryObject.createSQLQuery();
				log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QuerySelect*]:\n\t"+test);
				if(distinct)
					throw new Exception("Attesa eccezione: Per usare la select distinct devono essere indicati dei select field");
			}catch(SQLQueryObjectException s){
				if(distinct){
					if(!s.getMessage().equals("Per usare la select distinct devono essere indicati dei select field"))
						throw new Exception("Attesa eccezione: Per usare la select distinct devono essere indicati dei select field. Trovata: "+s.getMessage());
				}
				else{
					throw s;
				}
			}

			if(!distinct){
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")");
						while(rs.next()){
							log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")");						
						}
					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(ROW)){
						throw new Exception("Test failed (expected "+ROW+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
				}catch(Exception e){
					
					throw e;
				}
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		

		try{
			
			// TEST 8.
			
			log.info("\n\nh. ** Query con select* e offset/limit");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("tipo_destinatario");
			sqlQueryObject.addOrderBy("tracce.destinatario",false);
			sqlQueryObject.setSortType(true);

			int limit = 10;
			
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(0);

			String test = null;
			try{
				test = sqlQueryObject.createSQLQuery();
				log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QuerySelect*LimitOffset*]:\n\t"+test);
				if(distinct){
					throw new Exception("Attesa eccezione: Per usare la select distinct devono essere indicati dei select field");
				}
			}catch(SQLQueryObjectException s){
				if(distinct){
					if(!s.getMessage().equals("Per usare la select distinct devono essere indicati dei select field"))
						throw new Exception("Attesa eccezione: Per usare la select distinct devono essere indicati dei select field. Trovata: "+s.getMessage());
				}
				else{
					throw s;
				}
			}

			if(!distinct){
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						if(index == 0 || index==1){
							// verifico che l'offset=0 abbia funzionato
							String mittente = rs.getString("mittente");
							if(mittente.endsWith("0")==false){
								throw new Exception("Test failed (atteso un nome di mittente che termina con 0)"); 
							}
						}
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")");
						while(rs.next()){
							log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")");						
						}
					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limit)){
						throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
				}catch(Exception e){
					
					throw e;
				}
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 9.
			
			log.info("\n\ni. ** Query con select* e offset/limit e alias");

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit

			sqlQueryObject.addFromTable("tracce","tr");

			sqlQueryObject.setSelectDistinct(distinct);

			sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("tipo_destinatario");
			sqlQueryObject.addOrderBy("tr.destinatario",false);
			sqlQueryObject.setSortType(true);


			int limit = 10;
			
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(0);

			String test = null;
			try{
				test = sqlQueryObject.createSQLQuery();
				log.info("\nTest1("+distinct+") ["+tipo.toString()+"] [QuerySelect*LimitOffsetAlias*]:\n\t"+test);
				if(distinct){
					throw new Exception("Attesa eccezione: Per usare la select distinct devono essere indicati dei select field");
				}
			}catch(SQLQueryObjectException s){
				if(distinct){
					if(!s.getMessage().equals("Per usare la select distinct devono essere indicati dei select field"))
						throw new Exception("Attesa eccezione: Per usare la select distinct devono essere indicati dei select field. Trovata: "+s.getMessage());
				}
				else{
					throw s;
				}
			}

			if(!distinct){
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						if(index == 0 || index==1){
							// verifico che l'offset=0 abbia funzionato
							String mittente = rs.getString("mittente");
							if(mittente.endsWith("0")==false){
								throw new Exception("Test failed (atteso un nome di mittente che termina con 0)"); 
							}
						}
						log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")");
						while(rs.next()){
							log.info("riga["+(index++)+"]= ("+rs.getString("mittente")+")");						
						}
					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limit)){
						throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
				}catch(Exception e){
					
					throw e;
				}
			}
			
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}

	}




	
	
	
	

	// UNION TEST
	
	private static ISQLQueryObject prepareForUnion(ISQLQueryObject sqlQueryObject,int limit) throws SQLQueryObjectException {

		sqlQueryObject.addFromTable("tracce");

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.addSelectCountField("id", "cont", true);
		sqlQueryObject.addSelectField("mittente");
		sqlQueryObject.addSelectField("destinatario");

		sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

		sqlQueryObject.addOrderBy("mittente");
		sqlQueryObject.addOrderBy("destinatario",false);
		sqlQueryObject.setSortType(true);

		sqlQueryObject.addGroupBy("mittente");
		sqlQueryObject.addGroupBy("destinatario");

		sqlQueryObject.setLimit(limit);
		sqlQueryObject.setOffset(0);

		// selectForUpdate non permesso in group by
		sqlQueryObject.setSelectForUpdate(false);
		
		return sqlQueryObject;
	}
	private static ISQLQueryObject prepareForUnionSelectForUpdate(ISQLQueryObject sqlQueryObject,int limit) throws SQLQueryObjectException {

		sqlQueryObject.addFromTable("tracce");

		sqlQueryObject.addSelectField("mittente");
		sqlQueryObject.addSelectField("destinatario");

		sqlQueryObject.addWhereIsNotNullCondition("tipo_mittente");

		sqlQueryObject.addOrderBy("mittente");
		sqlQueryObject.addOrderBy("destinatario",false);
		sqlQueryObject.setSortType(true);

		sqlQueryObject.setLimit(limit);
		sqlQueryObject.setOffset(0);

		return sqlQueryObject;
	}
	@SuppressWarnings("resource")
	private static void testUnion_engine(TipiDatabase tipo, boolean count, boolean unionAll, Connection con, boolean selectForUpdate) throws Exception {


		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			log.info("\n\na. ** Query Union (unionCount:"+count+" unionAll:"+unionAll+")");
			
			int limit = 5;
			ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
			ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union

			sqlQueryObject.addSelectAliasField("mittente", "mit");
			sqlQueryObject.addSelectField("cont");

			if(count==false){
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
			}

			String test = null;
			if(count){
				test = sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2);
			}else{
				test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);
			}
			log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(count){
						long cont = rs.getLong("aliasUnion");
						log.info("riga["+(index++)+"]= ["+cont+"]");
						if(unionAll){
							if(cont!=(limit*2)){
								throw new Exception("Expected "+(limit*2)+", found "+cont);
							}
						}
						else{
							if(cont!=(limit)){
								throw new Exception("Expected "+(limit)+", found "+cont);
							}
						}
					}
					else{
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(cont!=1){
								throw new Exception("Expected 1, found "+cont);
							}
						}
						else{
							if(cont!=2){
								throw new Exception("Expected 2, found "+cont);
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("cont");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}			
						}
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				if(count){
					if(index!=1){
						throw new Exception("Test failed (expected "+1+" rows, found:"+(index)+")"); 
					}
				}else{
					if(unionAll){
						if(index!=(limit*2)){
							throw new Exception("Test failed (expected "+(limit*2)+" rows, found:"+(index)+")"); 
						}
					}
					else{
						if(index!=(limit)){
							throw new Exception("Test failed (expected "+(limit)+" rows, found:"+(index)+")"); 
						}
					}	
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		
		try{
			
			// TEST 2.
			
			if(count==false){
				log.info("\n\nb. ** Query Union OffSetLimit (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectField("cont");
	
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);

				int limitEsterno = 6;
				int offset = 2;
				if(unionAll==false){
					limitEsterno = limitEsterno/2;
					offset = offset/2;
				}
				sqlQueryObject.setLimit(limitEsterno);
				sqlQueryObject.setOffset(offset);
	
				String test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);

				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSetLimit UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(cont!=1){
								throw new Exception("Expected 1, found "+cont);
							}
						}
						else{
							if(cont!=2){
								throw new Exception("Expected 2, found "+cont);
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("cont");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}			
						}

					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limitEsterno)){
						throw new Exception("Test failed (expected "+(limitEsterno)+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		try{
			
			// TEST 3.
						
			if(count==false){
				log.info("\n\nc. ** Query Union OffSet (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectField("cont");
	
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);

				int offset = 2;
				if(unionAll==false){
					offset = offset/2;
				}
				int numeroEntriesRisultato = (limit*2)-offset;
				if(unionAll==false){
					numeroEntriesRisultato = (limit)-offset;
				}
				sqlQueryObject.setOffset(offset);
	
				String test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);

				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(cont!=1){
								throw new Exception("Expected 1, found "+cont);
							}
						}
						else{
							if(cont!=2){
								throw new Exception("Expected 2, found "+cont);
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("cont");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}			
						}

					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(numeroEntriesRisultato)){
						throw new Exception("Test failed (expected "+(numeroEntriesRisultato)+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 4.
						
			if(count==false){
				log.info("\n\nd. ** Query Union Limit (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectField("cont");
	
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);

				int limitEsterno = 4;
				if(unionAll==false){
					limitEsterno = limitEsterno/2;
				}
				sqlQueryObject.setLimit(limitEsterno);
	
				String test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);

				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					boolean foundEndsWith10 = false;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							foundEndsWith10 = true;
							if(cont!=1){
								throw new Exception("Expected 1, found "+cont);
							}
						}
						else{
							if(cont!=2){
								throw new Exception("Expected 2, found "+cont);
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("cont");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}			
						}

					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limitEsterno)){
						throw new Exception("Test failed (expected "+(limitEsterno)+" rows, found:"+(index)+")"); 
					}
					if(foundEndsWith10){
						throw new Exception("Test failed, unexpected element ends with 10");	
					}
					
					rs.close();
					stmtQuery.close();
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		
		
		
		
		try{
			
			// TEST 5.
			log.info("\n\ne. ** Query Union con select* (unionCount:"+count+" unionAll:"+unionAll+")");
			
			int limit = 5;
			ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
			ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union

			if(count==false){
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
			}

			String test = null;
			if(count){
				test = sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2);
			}else{
				test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);
			}
			log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(count){
						long cont = rs.getLong("aliasUnion");
						log.info("riga["+(index++)+"]= ["+cont+"]");
						if(unionAll){
							if(cont!=(limit*2)){
								throw new Exception("Expected "+(limit*2)+", found "+cont);
							}
						}
						else{
							if(cont!=(limit)){
								throw new Exception("Expected "+(limit)+", found "+cont);
							}
						}
					}
					else{
						String mit = rs.getString("mittente");
						long cont = rs.getLong("cont");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(cont!=1){
								throw new Exception("Expected 1, found "+cont);
							}
						}
						else{
							if(cont!=2){
								throw new Exception("Expected 2, found "+cont);
							}
						}
						while(rs.next()){
							mit = rs.getString("mittente");
							cont = rs.getLong("cont");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}			
						}
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				if(count){
					if(index!=1){
						throw new Exception("Test failed (expected "+1+" rows, found:"+(index)+")"); 
					}
				}else{
					if(unionAll){
						if(index!=(limit*2)){
							throw new Exception("Test failed (expected "+(limit*2)+" rows, found:"+(index)+")"); 
						}
					}
					else{
						if(index!=limit){
							throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
						}
					}
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		
		
		if(count==false){

			String msgOffset = "Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union";
			String msgLimit = "Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union";
			
			
			// TEST 6.
			log.info("\n\nf. ** Query Union con select* e LimitOffset (unionCount:"+count+" unionAll:"+unionAll+")");
			
			int limit = 5;
			ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
			ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setLimit(10);
			sqlQueryObject.setOffset(2);

			try{
				if(count){
					log.info("["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{
					log.info("["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}



			// TEST 7.
			log.info("\n\ng. ** Query Union con select* e Offset (unionCount:"+count+" unionAll:"+unionAll+")");
						
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setOffset(2);
			
			try{
				if(count){
					log.info("["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{
					log.info("["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

			

			// TEST 8.
			log.info("\n\nh. ** Query Union con select* e Limit (unionCount:"+count+" unionAll:"+unionAll+")");
									
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setLimit(10);
			
			try{
				if(count){
					log.info("["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{
					log.info("["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				}
				throw new Exception("Attesa eccezione: Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgLimit) && !s.getMessage().equals(msgOffset)) // alcune implementazione forzano l'offset a 0 se si imposta il limit e non l'offset
					throw new Exception("Attesa eccezione: ["+msgLimit+"] oppure ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

		}
		
		
		
		if(selectForUpdate){
			boolean findError = false;
			try{
				
				// TEST 9.
				log.info("\n\ni. ** Query Union per test SelectForUpdate (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnionSelectForUpdate(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnionSelectForUpdate(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // selectForUpdate non permesso in union
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectAliasField("destinatario", "dest");
	
				if(count==false){
					sqlQueryObject.addOrderBy("mittente");
					sqlQueryObject.addOrderBy("destinatario",false);
					sqlQueryObject.setSortType(true);
				}
	
				@SuppressWarnings("unused")
				String test = null;
				if(count){
					test = sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2);
				}else{
					test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);
				}
//				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
//				try{
//					stmtQuery = con.createStatement();
//					rs = stmtQuery.executeQuery(test);
//
//					int index = 0;
//					if(rs.next()){
//						if(count){
//							long cont = rs.getLong("aliasUnion");
//							log.info("riga["+(index++)+"]= ["+cont+"]");
//							if(unionAll){
//								if(cont!=(limit*2)){
//									throw new Exception("Expected "+(limit*2)+", found "+cont);
//								}
//							}
//							else{
//								if(cont!=(limit)){
//									throw new Exception("Expected "+(limit)+", found "+cont);
//								}
//							}
//						}
//						else{
//							String mit = rs.getString("mit");
//							String dest = rs.getString("dest");
//							log.info("riga["+(index++)+"]= ("+mit+"):("+dest+")");
//							while(rs.next()){
//								mit = rs.getString("mit");
//								dest = rs.getString("dest");
//								log.info("riga["+(index++)+"]= ("+mit+"):("+dest+")");		
//							}
//						}
//					}
//					else{
//						throw new Exception("Test failed"); 
//					}
//					
//					if(count){
//						if(index!=1){
//							throw new Exception("Test failed (expected "+1+" rows, found:"+(index)+")"); 
//						}
//					}else{
//						if(unionAll){
//							if(index!=(limit*2)){
//								throw new Exception("Test failed (expected "+(limit*2)+" rows, found:"+(index)+")"); 
//							}
//						}
//						else{
//							if(index!=(limit)){
//								throw new Exception("Test failed (expected "+(limit)+" rows, found:"+(index)+")"); 
//							}
//						}	
//					}
//					
//					rs.close();
//					stmtQuery.close();
				
			}catch(Exception e){
				findError = true;
				log.info("ERRORE ATTESO: "+e.getMessage());
			}finally{
				try{
					rs.close();
				}catch(Exception eClose){}
				try{
					stmtQuery.close();
				}catch(Exception eClose){}
			}
			if(findError==false){
				throw new Exception("Atteso errore utilizzo select for update non permesso in union");
			}
		}
	}
	



	
	
	
	
	
	
	@SuppressWarnings("resource")
	private static void testUnionWithGroupBy_engine(TipiDatabase tipo, boolean count, boolean unionAll, Connection con, boolean selectForUpdate) throws Exception {


		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
						
			// TEST 1.
			log.info("\n\na. ** Query UnionGroupBy (unionCount:"+count+" unionAll:"+unionAll+")");
			
			int limit = 5;
			ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
			ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addSelectAliasField("mittente", "mit");
			sqlQueryObject.addSelectSumField("cont", "contRisultatoGroupBy");

			if(count==false){
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
			}
			
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");

			String test = null;
			if(count){
				test = sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2);
			}else{
				test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);
			}
			log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(count){
						long cont = rs.getLong("aliasUnion");
						log.info("riga["+(index++)+"]= ["+cont+"]");
						if(cont!=(limit)){
							throw new Exception("Expected "+(limit)+", found "+cont);
						}
					}
					else{
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(unionAll){
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
							else{
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
						}
						else{
							if(unionAll){
								if(cont!=4){
									throw new Exception("Expected 4, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("contRisultatoGroupBy");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
							}			
						}
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				if(count){
					if(index!=1){
						throw new Exception("Test failed (expected "+1+" rows, found:"+(index)+")"); 
					}
				}else{
					if(index!=(limit)){
						throw new Exception("Test failed (expected "+(limit)+" rows, found:"+(index)+")"); 
					}	
				}
				
				rs.close();
				stmtQuery.close();
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		
		
		try{
			
			// TEST 2.
			
			if(count==false){
				log.info("\n\nb. ** Query UnionGroupBy OffSetLimit (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectSumField("cont", "contRisultatoGroupBy");
	
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
				
				sqlQueryObject.addGroupBy("mittente");
				sqlQueryObject.addGroupBy("destinatario");

				int limitEsterno = 6;
				int offset = 2;
				limitEsterno = limitEsterno/2;  // groupBy
				offset = offset/2;  // groupBy
				sqlQueryObject.setLimit(limitEsterno);
				sqlQueryObject.setOffset(offset);
	
				String test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);

				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSetLimit UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(unionAll){
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
							else{
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
						}
						else{
							if(unionAll){
								if(cont!=4){
									throw new Exception("Expected 4, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("contRisultatoGroupBy");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
							}	
						}

					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limitEsterno)){
						throw new Exception("Test failed (expected "+(limitEsterno)+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		
		try{
			
			// TEST 3.
						
			if(count==false){
				log.info("\n\nc. ** Query UnionGroupBy  OffSet (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectSumField("cont", "contRisultatoGroupBy");
	
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
				
				sqlQueryObject.addGroupBy("mittente");
				sqlQueryObject.addGroupBy("destinatario");

				int offset = 2;
				offset = offset/2; // groupBy
				int numeroEntriesRisultato = (limit*2)-offset;
				numeroEntriesRisultato = (limit)-offset;  // groupBy
				sqlQueryObject.setOffset(offset);
	
				String test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);

				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(unionAll){
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
							else{
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
						}
						else{
							if(unionAll){
								if(cont!=4){
									throw new Exception("Expected 4, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
						}
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("contRisultatoGroupBy");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
							}		
						}

					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(numeroEntriesRisultato)){
						throw new Exception("Test failed (expected "+(numeroEntriesRisultato)+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					stmtQuery.close();
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 4.
						
			if(count==false){
				log.info("\n\nd. ** Query UnionGroupBy Limit (unionCount:"+count+" unionAll:"+unionAll+")");
				
				int limit = 5;
				ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
				ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
	
				ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);
	
				sqlQueryObject.addSelectAliasField("mittente", "mit");
				sqlQueryObject.addSelectSumField("cont", "contRisultatoGroupBy");
	
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
				
				sqlQueryObject.addGroupBy("mittente");
				sqlQueryObject.addGroupBy("destinatario");

				int limitEsterno = 4;
				limitEsterno = limitEsterno/2; // groupBy
				sqlQueryObject.setLimit(limitEsterno);
	
				String test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);

				log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					boolean foundEndsWith10 = false;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
						if(mit.endsWith("10")){
							if(unionAll){
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
							else{
								if(cont!=1){
									throw new Exception("Expected 1, found "+cont);
								}
							}
						}
						else{
							if(unionAll){
								if(cont!=4){
									throw new Exception("Expected 4, found "+cont);
								}
							}
							else{
								if(cont!=2){
									throw new Exception("Expected 2, found "+cont);
								}
							}
						}		
						while(rs.next()){
							mit = rs.getString("mit");
							cont = rs.getLong("contRisultatoGroupBy");
							log.info("riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										throw new Exception("Expected 2, found "+cont);
									}
								}
							}				
						}

					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limitEsterno)){
						throw new Exception("Test failed (expected "+(limitEsterno)+" rows, found:"+(index)+")"); 
					}
					if(foundEndsWith10){
						throw new Exception("Test failed, unexpected element ends with 10");	
					}
					
					rs.close();
					stmtQuery.close();
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmtQuery.close();
			}catch(Exception eClose){}
		}
		
		
		
		
		try{
			
			// TEST 5.
			log.info("\n\ne. ** Query UnionGroupBy con select* (unionCount:"+count+" unionAll:"+unionAll+")");
			
			int limit = 5;
			ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
			ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			if(count==false){
				sqlQueryObject.addOrderBy("mittente");
				sqlQueryObject.addOrderBy("destinatario",false);
				sqlQueryObject.setSortType(true);
			}

			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");
			
			String test = null;
			if(count){
				test = sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2);
			}else{
				test = sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2);
			}
			log.info("\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			throw new Exception("Attesa eccezione: Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field");
		}catch(SQLQueryObjectException s){
			if(!s.getMessage().equals("Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field"))
				throw new Exception("Attesa eccezione: Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field. Trovata: "+s.getMessage());
		}
		
		
		
		
		if(count==false){

			String msgOffset = "Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union";
			String msgLimit = "Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union";
			
			
			// TEST 6.
			log.info("\n\nf. ** Query UnionGroupBy con select* e LimitOffset (unionCount:"+count+" unionAll:"+unionAll+")");
			
			int limit = 5;
			ISQLQueryObject prepare1 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);
			ISQLQueryObject prepare2 = prepareForUnion(createSQLQueryObjectCore(tipo,selectForUpdate),limit);

			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setLimit(10);
			sqlQueryObject.setOffset(2);
			
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");

			try{
				if(count){
					log.info("["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{
					log.info("["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}



			// TEST 7.
			log.info("\n\ng. ** Query UnionGroupBy con select* e Offset (unionCount:"+count+" unionAll:"+unionAll+")");
						
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);
			
			sqlQueryObject.setOffset(2);
			
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");

			try{
				if(count){
					log.info("["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{
					log.info("["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

			

			// TEST 8.
			log.info("\n\nh. ** Query UnionGroupBy con select* e Limit (unionCount:"+count+" unionAll:"+unionAll+")");
									
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setLimit(10);
			
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");

			try{
				if(count){
					log.info("["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{
					log.info("["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				}
				throw new Exception("Attesa eccezione: Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgLimit) && !s.getMessage().equals(msgOffset)) // alcune implementazione forzano l'offset a 0 se si imposta il limit e non l'offset
					throw new Exception("Attesa eccezione: ["+msgLimit+"] oppure ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

		}
		
	}
	
	
}
