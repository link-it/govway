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

package org.openspcoop2.utils.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
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

	private static boolean systemOut = true; // utile per maven test 
		
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,      
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update NUMBER,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update NUMBER,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
	 * 		id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1)
	 * );
	 * CREATE TABLE msgdiagnostici_init_seq (id BIGINT);
	 * INSERT INTO msgdiagnostici_init_seq VALUES (NEXT VALUE FOR seq_msgdiagnostici);
	 * ---------------------------
	 * 
	 * ---------------------------
	 * derby
	 * CREATE SEQUENCE seq_tracce AS BIGINT START WITH 1 INCREMENT BY 1 ;
	 * CREATE TABLE tracce (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
	 * 		id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1)
	 * );
	 * CREATE TABLE tracce_init_seq (id BIGINT);
	 * INSERT INTO tracce_init_seq VALUES (NEXT VALUE FOR seq_tracce);
	 * CREATE SEQUENCE seq_msgdiagnostici AS BIGINT START WITH 1 INCREMENT BY 1 ;
	 * CREATE TABLE msgdiagnostici (
	 *      descrizione VARCHAR(255) NOT NULL,
	 *      gdo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      gdo2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	 *      tipo_mittente VARCHAR(255) NOT NULL,
	 *      mittente VARCHAR(255) NOT NULL,
	 *      tipo_destinatario VARCHAR(255) NOT NULL,
	 *      destinatario VARCHAR(255) NOT NULL,
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
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
	 *      campo_vuoto VARCHAR(255),
	 *      campo_update VARCHAR(255),
	 *      campo_int_update INT,
	 * 		id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1 NO CYCLE NO CACHE)
	 * );
	 * ---------------------------
	 */

	
	private static int ROW = 21;

	private static Logger log = null;

	private static String PRIMA_COLONNA = "Prima colonna valorizzata";
	private static String ULTIMA_COLONNA = "Ultima colonna valorizzata";
	
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
		if(tipoDatabase==null) {
			throw new Exception("TipoDatabase non fornito");
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
		case DERBY:
			url = "jdbc:derby:sample";
			driver = "org.apache.derby.jdbc.EmbeddedDriver";
			userName = "";
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
		if(args.length>4){
			String driverJdbcCustom = args[4].trim();
			if(!"${driverJdbc}".equals(driverJdbcCustom)){
				driver = driverJdbcCustom;
			}
		}

		System.out.println("URL:"+url);
		System.out.println("UserName:"+userName);
		System.out.println("Password:"+password);
		System.out.println("DriverJDBC:"+driver);		
		ClassLoaderUtilities.newInstance(driver);
		Connection con = null;
		Statement stmtDelete = null;
		PreparedStatement stmtInsert = null;
		try{
			con = DriverManager.getConnection(url, userName, password);

			String caratteriStraniParte1 = "- { ( [ _ % ^ \\ / # | & à è @ . : ; , < parolaCasualeMAIUSCOLA > é ù ì ^ \" ' ? ! ] ) } -";
			String caratteriStraniParte2 = "\n \t \r \b Un  po di parole Maiuscole Minuscole § ° ç £";
			String rigaCaratteriStrani = "------" + caratteriStraniParte1 + "-----" + caratteriStraniParte2  + "-----";
			
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
			String rigaCompletaCaratteriStrani = null;
			for (int i = 0; i < ROW; i++) {
				String insertMsgDiagnosticoWithGdo = "INSERT INTO msgdiagnostici (descrizione,gdo,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario,campo_vuoto) VALUES (?,?,?,?,?,?,?,?)";
				String insertMsgDiagnosticoWithoutGdo = "INSERT INTO msgdiagnostici (descrizione,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario,campo_vuoto) VALUES (?,?,?,?,?,?,?)";
				String descrizione = "descrizione esempio "+i;
				if(i>9){
					// genero stessa descrizione per groupby
					descrizione =  "descrizione esempio uguale";
				}
				if(i==20){
					// genero entry per caratteri speciali like
					descrizione =  "descrizione con caratteri particolari: "+rigaCaratteriStrani;
					rigaCompletaCaratteriStrani = descrizione;
				}
				String campoVuoto = null;
				if(i==0){
					campoVuoto = PRIMA_COLONNA;
				}
				if(i>10){
					campoVuoto = ""; // Valorizzato con stringa vuota
				}
				if(i==(ROW-1)){
					campoVuoto = ULTIMA_COLONNA;
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
				stmtInsert.setString(index++, campoVuoto);
				stmtInsert.execute();
				stmtInsert.close();
			}

			for (int i = 0; i < ROW; i++) {
				String insertTracciaWithGdo = "INSERT INTO tracce (descrizione,gdo,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario,campo_vuoto) VALUES (?,?,?,?,?,?,?,?)";
				String insertTracciaWithoutGdo = "INSERT INTO tracce (descrizione,gdo2,tipo_mittente,mittente,tipo_destinatario,destinatario,campo_vuoto) VALUES (?,?,?,?,?,?,?)";
				String descrizione = "descrizione esempio "+i;
				if(i>9){
					// genero stessa descrizione per groupby
					descrizione =  "descrizione esempio uguale";
				}
				if(i==20){
					// genero entry per caratteri speciali like
					descrizione =  "descrizione con caratteri particolari: "+rigaCaratteriStrani;
				}
				String campoVuoto = null;
				if(i==0){
					campoVuoto = PRIMA_COLONNA;
				}
				if(i>10){
					campoVuoto = ""; // Valorizzato con stringa vuota
				}
				if(i==(ROW-1)){
					campoVuoto = ULTIMA_COLONNA;
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
				stmtInsert.setString(index++, campoVuoto);
				stmtInsert.execute();
				stmtInsert.close();
			}


			for (int i = 0; i < 2; i++) {
				
				boolean selectForUpdate = false;
				if(i==1){
					selectForUpdate = true;
				}
				
				info(log,systemOut,"\n\n@@@ SELECT FOR UPDATE: "+selectForUpdate);
				
			
			
				// step3. Test UnixTime
				// a. verifico con msgdiagnostici dove e' stato inserita la data applicativamente
				testUnixTime_engine(tipoDatabase, "msgdiagnostici",con,selectForUpdate);
				// b. verifico con tracce dove la data e' stata generata tramite current_timestamp
				testUnixTime_engine(tipoDatabase, "tracce",con,selectForUpdate);		
				
				
				// step4 . Test fromTable
				testFromTable_engine(tipoDatabase, "msgdiagnostici", con,selectForUpdate);
				
				
				// step5. Test escape char like
				testLikeEscapeChar_engine(tipoDatabase,  "msgdiagnostici", con,selectForUpdate, caratteriStraniParte1, caratteriStraniParte2);
				testLikeEscapeChar_likeConfig_engine(tipoDatabase,  "msgdiagnostici", con,selectForUpdate, caratteriStraniParte1, caratteriStraniParte2, rigaCompletaCaratteriStrani);
				
				
				// step5b. Test coalesce/case
				test_coalesce_case(tipoDatabase, con, selectForUpdate);
				
				
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
				
				
				// step17. Test con condizioni null e empty
				testEmptyNull_engine(tipoDatabase, "msgdiagnostici", con, selectForUpdate);
				
				
				// step18. Test CASE UPDATE
				testCase_engine(tipoDatabase, i==0 ? "msgdiagnostici" : "tracce", con, false); // e' un update! selectForUpdate);

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
			}catch(Exception eClose){
				// close
			}
		}


	}

	
	private static void info(Logger log, boolean systemOut, String msg) {
		log.info(msg);
		if(systemOut) {
			System.out.println(msg);
		}
	}

	private static SQLQueryObjectCore createSQLQueryObjectCore(TipiDatabase tipo, boolean selectForUpdate) throws Exception{
		SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
		if(selectForUpdate){
			sqlQueryObject.setSelectForUpdate(true);
		}
		return sqlQueryObject;
	}




	@SuppressWarnings("deprecation")
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
			info(log,systemOut,"\ntest1-"+table+" unixtime:\n\t"+testUnixTime);
			try{
				stmtQuery = con.prepareStatement(testUnixTime);
				rs = stmtQuery.executeQuery();
				int index = 0;
				if(rs.next()){

					// Data Letta dal database
					String format = "yyyy-MM-dd_HH:mm:ss.SSSZ";
					SimpleDateFormat dateformat = new SimpleDateFormat (format);
					Timestamp timestamp = rs.getTimestamp("gdo");
					String s = dateformat.format(timestamp);
					long timeStampValue = timestamp.getTime();
					long timeStampValueWithTimeZone = -1;
					if(timestamp.getTimezoneOffset()!=0){
						// L'oggetto timestamp del database può avere una data diversa su cui deve essere applicato il timezone per poter poi su quell'oggetto
						// poter avere un epoc time corretto.
						if(timestamp.getTimezoneOffset()<0){
							timeStampValueWithTimeZone = timestamp.getTime() + ((-1*timestamp.getTimezoneOffset())*60*1000);
						}
						else{
							timeStampValueWithTimeZone = timestamp.getTime() + (timestamp.getTimezoneOffset()*60*1000);
						}
					}

					// UnixEpoque
					long unixAtteso = dateformat.parse(s).getTime()  - (dateformat.parse(s).getTimezoneOffset()*60*1000);
					long unixAttesoCalcolatoViaDB = rs.getLong("unixtime");

					info(log,systemOut,"Data["+s+"]");
					info(log,systemOut,"timeStampValue["+timeStampValue+"] offset["+timestamp.getTimezoneOffset()+"] timeStampValueWithTimeZone["+timeStampValueWithTimeZone+"]");
					info(log,systemOut,"unixEpoqueAtteso["+unixAtteso+"] unixEpoqueCalcolato["+unixAttesoCalcolatoViaDB+"]");
										

					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+" timeValue:"+timeStampValue+") ("+unixAttesoCalcolatoViaDB+")");
					
					// Questo controllo che aveva lo scopo di calcolare che lo unix timestamp dovesse corrispondere a quello atteso non funziona
					// poichè alcuni database generano un unix epoque considerando l'offset, altri no e non sono riuscito a trovare una soluzione definitiva
					// Comunque sia per le funzioni di latenza dovrebbe cmq funzionare visto che per tutte le date viene applicato lo stesso meccanismo
//					if(unixAtteso!=unixAttesoCalcolatoViaDB){
//						log.error("Query: "+sqlQueryObject.toString());
//						throw new Exception("UnixTime test failed diff Calcolato-Atteso:"+(unixAttesoCalcolatoViaDB-unixAtteso));
//					}
					
					boolean check = timeStampValue==unixAttesoCalcolatoViaDB;
					if(!check){
						if(timeStampValueWithTimeZone!=-1){
							check = timeStampValueWithTimeZone==unixAttesoCalcolatoViaDB;
						}
					}
					if(!check){
						// NOTA alcune volte oracle differisce di 1000. Non sempre, non ho capito il motivo
						if(TipiDatabase.ORACLE.equals(tipo)){
							if(timeStampValue!=(unixAttesoCalcolatoViaDB-1000)){
								log.error("Query: "+sqlQueryObject.toString());
								throw new Exception("UnixTime [checkDB] test failed diff("+(unixAttesoCalcolatoViaDB-timeStampValue)+")");	
							}
						}
						else{
							log.error("Query: "+sqlQueryObject.toString());
							throw new Exception("UnixTime [checkDB] test failed diff("+(unixAttesoCalcolatoViaDB-timeStampValue)+")");
						}
					}
				}
				else{
					log.error("Query: "+sqlQueryObject.toString());
					throw new Exception("UnixTime [checkDB] test1 failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
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
			info(log,systemOut,"\ntest2-"+table+" unixtime:\n\t"+testUnixTime);
			try{
				stmtQuery = con.prepareStatement(testUnixTime);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" [min:"+rs.getLong("unixtimemin")+" max:"+
							rs.getLong("unixtimemax")+" avg:"+
							rs.getLong("unixtimeavg")+" sum:"+
							rs.getLong("unixtimesum")+"]");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 3.
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate);
			
			SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("gdo2");
			sqlQueryObject.addSelectAliasField(sqlQueryObject.getDiffUnixTimestamp("gdo2", "gdo"),"latenza");
			sqlQueryObject.addOrderBy(sqlQueryObject.getDiffUnixTimestamp("gdo2", "gdo"));
			sqlQueryObject.setSortType(true);
			
			testUnixTime = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest3-"+table+" unixtime:\n\t"+testUnixTime);
			long oldLatenza = 0;
			try{
				stmtQuery = con.prepareStatement(testUnixTime);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){
					long latenza = rs.getLong("latenza");
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}

		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
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
			info(log,systemOut,"\ntest-"+table+" fromTable:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	
	
	
	
	private static void testLikeEscapeChar_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate, String caratteriStraniParte1, String caratteriStraniParte2) throws Exception {
		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			boolean escape = true;
			boolean contains = true;
			boolean caseInsensitive = true;
			
			
			// TEST 1.
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			String ricerca = caratteriStraniParte1;
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, caseInsensitive);
			
			String test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte1)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 1 (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte1.toLowerCase();
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte1)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			// TEST 1b.
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = caratteriStraniParte2;
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte2)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 1b (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte2.toLowerCase();
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte2)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			
			
			// TEST 2 (caseInsensistive=false).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = caratteriStraniParte1;
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, !caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte1)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 2 (caseInsensistive=false) (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte1.toLowerCase();
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, !caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte1)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			// TEST 2 (caseInsensistive=false).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = caratteriStraniParte2;
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, !caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte2)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 2 (caseInsensistive=false) (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte2.toLowerCase();
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, escape, contains, !caseInsensitive);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte2)-"+table+" escapeLike:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			

		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	

	
	private static void testLikeEscapeChar_likeConfig_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate, 
			String caratteriStraniParte1, String caratteriStraniParte2, String rigaCompletaCaratteriStrani) throws Exception {
		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			boolean escape = true;
			boolean contains = true;
			boolean caseInsensitive = true;
			
			
			// TEST 1.
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			String ricerca = caratteriStraniParte1;
			LikeConfig likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			String test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte1)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 1 (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte1.toLowerCase();
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte1)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			// TEST 1b.
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = caratteriStraniParte2;
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte2)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 1b (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte2.toLowerCase();
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte2)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			
			
			// TEST 2 (caseInsensistive=false).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = caratteriStraniParte1;
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte1)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 2 (caseInsensistive=false) (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte1.toLowerCase();
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte1)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			// TEST 2 (caseInsensistive=false).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = caratteriStraniParte2;
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte2)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 2 (caseInsensistive=false) (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = caratteriStraniParte2.toLowerCase();
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setContains(contains);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte2)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			// START WITH
			
			
			// TEST 3.
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = rigaCompletaCaratteriStrani.substring(0, 100); // primi 100 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setStartsWith(true);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte3)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 3 (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = rigaCompletaCaratteriStrani.substring(0, 100).toLowerCase(); // primi 100 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setStartsWith(true);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte3)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 4 (caseInsensistive=false).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = rigaCompletaCaratteriStrani.substring(0, 100); // primi 100 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setStartsWith(true);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte4)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 4 (caseInsensistive=false) (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = rigaCompletaCaratteriStrani.substring(0, 100).toLowerCase(); // primi 100 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setStartsWith(true);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte4)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 4 errore
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = rigaCompletaCaratteriStrani.substring(0, 100); // primi 100 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setEndsWith(true);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte4 errore)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			// ENDS WITH
			
			
			// TEST 5.
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,false); // forUpdate non permesso con limit
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = rigaCompletaCaratteriStrani.substring(50, rigaCompletaCaratteriStrani.length()); // escludo primi 50 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setEndsWith(true);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte5)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 5 (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = rigaCompletaCaratteriStrani.substring(50, rigaCompletaCaratteriStrani.length()).toLowerCase(); // escludo primi 50 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setEndsWith(true);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte5)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 6 (caseInsensistive=false).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			ricerca = rigaCompletaCaratteriStrani.substring(50, rigaCompletaCaratteriStrani.length()); // escludo primi 50 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setEndsWith(true);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest (Parte6)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+")");
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 6 (caseInsensistive=false) (toLower).
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = rigaCompletaCaratteriStrani.substring(50, rigaCompletaCaratteriStrani.length()).toLowerCase(); // escludo primi 50 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setEndsWith(true);
			likeConfig.setCaseInsensitive(!caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte6)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 6 errore
			
			sqlQueryObject = (SQLQueryObjectCore) SQLObjectFactory.createSQLQueryObject(tipo);
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.setLimit(1);
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			// essendo abilitato il caseInsensitive deve trovarla lo stesso anche se faccio il toLowerCase
			ricerca = rigaCompletaCaratteriStrani.substring(50, rigaCompletaCaratteriStrani.length()); // escludo primi 50 caratteri.
			likeConfig = new LikeConfig();
			likeConfig.setEscape(escape);
			likeConfig.setStartsWith(true);
			likeConfig.setCaseInsensitive(caseInsensitive);
			sqlQueryObject.addWhereLikeCondition("descrizione", ricerca, likeConfig);
			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntestLower (Parte6 errore)-"+table+" escapeLikeConfig:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(TipiDatabase.MYSQL.equals(tipo) || TipiDatabase.SQLSERVER.equals(tipo)){
						info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
								" (time:"+rs.getTimestamp("gdo")+")");
					}
					else{
						throw new Exception("Atteso fallimento del test");
					}
				}
				else{
					info(log,systemOut,"Nessuna riga trovata: risultato atteso");
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			

		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
	}
	

	private static void test_coalesce_case(TipiDatabase tipo, Connection con, boolean selectForUpdate) throws Exception {

		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1. (Case string)
			
			ISQLQueryObject sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso in group by

			sqlQueryObject.addFromTable("msgdiagnostici","aliasMSG");

			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectAliasField("aliasMSG", "campo_vuoto", "testCampoVuoto");
			sqlQueryObject.addSelectCoalesceField("aliasMSG", "campo_vuoto", "testCoalesce", "VALORE_DEFAULT_COALESCE");
			Case caseValue = new Case(CastColumnType.STRING, true, "CASE_DEFAULT");
			caseValue.addCase("aliasMSG.campo_vuoto='"+PRIMA_COLONNA+"'", "CASE_PRIMO");
			caseValue.addCase("aliasMSG.campo_vuoto='"+ULTIMA_COLONNA+"'", "CASE_ULTIMO");
			sqlQueryObject.addSelectCaseField(caseValue, "testCase");
			
			info(log,systemOut,"");

			
			String test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest0_engine:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				try {
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= (coalesce:"+rs.getString("testCoalesce")+") (case:"+rs.getString("testCase")+")");
							
						String campoVuoto = rs.getString("testCampoVuoto");
						String testCoalesce = rs.getString("testCoalesce");
						String testCase = rs.getString("testCase");

						if(campoVuoto==null) {
							if(!"VALORE_DEFAULT_COALESCE".equals(testCoalesce)) {
								throw new Exception("Test failed; expected testCoalesce '"+testCoalesce+"' con il valore di default 'VALORE_DEFAULT_COALESCE'"); 
							}
						}
						if(PRIMA_COLONNA.equals(campoVuoto)) {
							if(!"CASE_PRIMO".equals(testCase)) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con il valore 'CASE_PRIMO' per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
						else if(ULTIMA_COLONNA.equals(campoVuoto)) {
							if(!"CASE_ULTIMO".equals(testCase)) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con il valore 'CASE_ULTIMO' per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
						else {
							if(!"CASE_DEFAULT".equals(testCase)) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con il valore 'CASE_DEFAULT' per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
					}
				}finally {
					rs.close();
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				}
				
				if(index==0) {
					throw new Exception("Test failed"); 
				}
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			// TEST 2. (Case number)
			
			sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso in group by

			sqlQueryObject.addFromTable("msgdiagnostici","aliasMSG");

			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectAliasField("aliasMSG", "campo_vuoto", "testCampoVuoto");
			sqlQueryObject.addSelectCoalesceField("aliasMSG", "campo_vuoto", "testCoalesce", "VALORE_DEFAULT_COALESCE");
			caseValue = new Case(CastColumnType.LONG, false, "23");
			caseValue.addCase("aliasMSG.campo_vuoto='"+PRIMA_COLONNA+"'", "11");
			caseValue.addCase("aliasMSG.campo_vuoto='"+ULTIMA_COLONNA+"'", "99");
			sqlQueryObject.addSelectCaseField(caseValue, "testCase");
			
			info(log,systemOut,"");

			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest0_engine:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				try {
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= (coalesce:"+rs.getString("testCoalesce")+") (case:"+rs.getLong("testCase")+")");
							
						String campoVuoto = rs.getString("testCampoVuoto");
						String testCoalesce = rs.getString("testCoalesce");
						long testCase = rs.getLong("testCase");

						if(campoVuoto==null) {
							if(!"VALORE_DEFAULT_COALESCE".equals(testCoalesce)) {
								throw new Exception("Test failed; expected testCoalesce '"+testCoalesce+"' con il valore di default 'VALORE_DEFAULT_COALESCE'"); 
							}
						}
						if(PRIMA_COLONNA.equals(campoVuoto)) {
							if(testCase!=11l) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con il valore '11' per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
						else if(ULTIMA_COLONNA.equals(campoVuoto)) {
							if(testCase!=99l) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con il valore '99' per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
						else {
							if(testCase!=23l) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con il valore '23' per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
					}
				}finally {
					rs.close();
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				}
				
				if(index==0) {
					throw new Exception("Test failed"); 
				}
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			// TEST 3. (Column e null)
			
			sqlQueryObject = createSQLQueryObjectCore(tipo,false); // forUpdate non permesso in group by

			sqlQueryObject.addFromTable("msgdiagnostici","aliasMSG");

			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectAliasField("aliasMSG", "campo_vuoto", "testCampoVuoto");
			sqlQueryObject.addSelectCoalesceField("aliasMSG", "campo_vuoto", "testCoalesce", "VALORE_DEFAULT_COALESCE");
			caseValue = new Case(CastColumnType.STRING, false, "null");
			caseValue.addCase("aliasMSG.campo_vuoto='"+PRIMA_COLONNA+"'", "aliasMSG.campo_vuoto");
			caseValue.addCase("aliasMSG.campo_vuoto='"+ULTIMA_COLONNA+"'", "null");
			sqlQueryObject.addSelectCaseField(caseValue, "testCase");
			
			info(log,systemOut,"");

			
			test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest0_engine:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				try {
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= (coalesce:"+rs.getString("testCoalesce")+") (case:"+rs.getString("testCase")+")");
							
						String campoVuoto = rs.getString("testCampoVuoto");
						String testCoalesce = rs.getString("testCoalesce");
						String testCase = rs.getString("testCase");

						if(campoVuoto==null) {
							if(!"VALORE_DEFAULT_COALESCE".equals(testCoalesce)) {
								throw new Exception("Test failed; expected testCoalesce '"+testCoalesce+"' con il valore di default 'VALORE_DEFAULT_COALESCE'"); 
							}
						}
						if(PRIMA_COLONNA.equals(campoVuoto)) {
							if(!campoVuoto.equals(testCase)) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con lo stesso valore della colonna campo_vuoto:'"+campoVuoto+"'"); 
							}
						}
						else if(ULTIMA_COLONNA.equals(campoVuoto)) {
							if(testCase!=null) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con null value per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
						else {
							if(testCase!=null) {
								throw new Exception("Test failed; expected testCase '"+testCase+"' con null value per la colonna con valore '"+campoVuoto+"'"); 
							}
						}
					}
				}finally {
					rs.close();
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				}
				
				if(index==0) {
					throw new Exception("Test failed"); 
				}
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
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

			//info(log,systemOut,"["+tipo.toString()+"] getField: \t"+sqlQueryObject.getFields());
			List<String> trovato = sqlQueryObject.getFieldsName();
			info(log,systemOut,"(test0_engine) ["+tipo.toString()+"] getFieldsName: \t"+trovato);
			String atteso = "cont, mittente, ALIASDEST";
			if(atteso.equals(trovato.toString())){
				throw new Exception("Test failed (getFieldsName) trovato["+trovato.toString()+"] atteso["+atteso+"]");
			}
			
			//info(log,systemOut,"["+tipo.toString()+"] getTables: \t"+sqlQueryObject.getTables());
			trovato = sqlQueryObject.getTablesName();
			info(log,systemOut,"(test0_engine) ["+tipo.toString()+"] getTablesName: \t"+trovato);
			atteso = "tracce, aliasMSG";
			if(atteso.equals(trovato.toString())){
				throw new Exception("Test failed (getTablesName) trovato["+trovato.toString()+"] atteso["+atteso+"]");
			}
			
			info(log,systemOut,"");

			
			String test = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest0_engine:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+
							"->"+rs.getString("ALIASDEST")+") (count:"+rs.getLong("cont")+")");					
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+
								"->"+rs.getString("ALIASDEST")+") (count:"+rs.getLong("cont")+")");
						
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
	}



	private static void test1_engine(TipiDatabase tipo, boolean distinct, Connection con, boolean selectForUpdate) throws Exception {


		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			
			info(log,systemOut,"\n\na. ** Query normale");

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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QueryNormale]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");			
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
				rs = null;
				
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 2.
			
			info(log,systemOut,"\n\nb. ** Query con limit/offset");

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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QueryLimitOffset]:\n\t"+test);
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
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");					
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				
				if(index!=(limit)){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
			
					
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 2b (con unixTimestamp).
			
			info(log,systemOut,"\n\nb. ** Query con limit/offset (unixTimestamp)");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipo);

			sqlQueryObject.addFromTable("tracce");

			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField("tracce","tipo_destinatario", "TIPODEST");
			sqlQueryObject.addSelectField("tracce.destinatario");
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectField("gdo2");
			sqlQueryObject.addSelectField("gdo");
			
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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QueryLimitOffset]:\n\t"+test);
			try{
				
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
				
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
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")("+rs.getLong("latenza")+")");
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")("+rs.getLong("latenza")+")");					
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
				
				
				if(index!=(limit)){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
			
					
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 3.
			
			info(log,systemOut,"\n\nc. ** Query con limit");

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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QueryLimit]:\n\t"+test);
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
				
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
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");						
					}
				}
				else{
					throw new Exception("Test failed"); 
				}
			
				if(index!=(limit)){
					throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
				}
				
				
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 4.
			
			info(log,systemOut,"\n\nd. ** Query con offset");

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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QueryOffset]:\n\t"+test);
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
				
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
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")("+rs.getString("TIPODEST")+")("+rs.getString("destinatario")+")");					
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 5.
			
			info(log,systemOut,"\n\ne. ** Query groupBy");

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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [GroupBy]:\n\t"+test);
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
				
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");
					if(2!=rs.getLong("cont")){
						throw new Exception("Test failed row["+(index)+"] found:"+rs.getLong("cont")+" expected:2"); 
					}
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");		
						if(rs.getString("mittente").endsWith("10")){
							if(1!=rs.getLong("cont")){
								long cont = rs.getLong("cont");
								rs.close();
								rs = null;
								stmtQuery.close();
								stmtQuery = null;
								throw new Exception("Test failed row["+(index)+"] found:"+cont+" expected:1"); 
							}
						}
						else{
							if(2!=rs.getLong("cont")){
								long cont = rs.getLong("cont");
								rs.close();
								rs = null;
								stmtQuery.close();
								stmtQuery = null;
								throw new Exception("Test failed row["+(index)+"] found:"+cont+" expected:2"); 
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}

		
		
		
		
			
		
		try{
			
			// TEST 6.
			
			info(log,systemOut,"\n\nf. ** Query groupBy con limit/offset");
		
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
			info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [GroupByLimitOffset]:\n\t"+test);
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
				
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");
					if(2!=rs.getLong("cont")){
						throw new Exception("Test failed row["+((index-1))+"] found:"+rs.getLong("cont")+" expected:2"); 
					}
					while(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+") = "+rs.getLong("cont")+"");		
						if(rs.getString("mittente").endsWith("10")){
							if(1!=rs.getLong("cont")){
								long cont = rs.getLong("cont");
								rs.close();
								rs = null;
								stmtQuery.close();
								stmtQuery = null;
								throw new Exception("Test failed row["+((index-1))+"] found:"+cont+" expected:1"); 
							}
						}
						else{
							if(2!=rs.getLong("cont")){
								long cont = rs.getLong("cont");
								rs.close();
								rs = null;
								stmtQuery.close();
								stmtQuery = null;
								throw new Exception("Test failed row["+((index-1))+"] found:"+cont+" expected:2"); 
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 7.
			

			info(log,systemOut,"\n\ng. ** Query con select*");

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
				info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QuerySelect*]:\n\t"+test);
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
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")");
						while(rs.next()){
							info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")");						
						}
					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(ROW)){
						throw new Exception("Test failed (expected "+ROW+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
				}catch(Exception e){
					
					throw e;
				}
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		

		try{
			
			// TEST 8.
			
			info(log,systemOut,"\n\nh. ** Query con select* e offset/limit");

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
				info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QuerySelect*LimitOffset*]:\n\t"+test);
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
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
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
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")");
						while(rs.next()){
							info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")");						
						}
					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limit)){
						throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
				}catch(Exception e){
					
					throw e;
				}
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 9.
			
			info(log,systemOut,"\n\ni. ** Query con select* e offset/limit e alias");

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
				info(log,systemOut,"\nTest1("+distinct+") ["+tipo.toString()+"] [QuerySelect*LimitOffsetAlias*]:\n\t"+test);
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
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
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
						info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")");
						while(rs.next()){
							info(log,systemOut,"riga["+(index++)+"]= ("+rs.getString("mittente")+")");						
						}
					}
					else{
						throw new Exception("Test failed"); 
					}
					
					if(index!=(limit)){
						throw new Exception("Test failed (expected "+limit+" rows, found:"+(index)+")"); 
					}
					
					rs.close();
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
				}catch(Exception e){
					
					throw e;
				}
			}
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
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

	private static void testUnion_engine(TipiDatabase tipo, boolean count, boolean unionAll, Connection con, boolean selectForUpdate) throws Exception {


		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			// TEST 1.
			info(log,systemOut,"\n\na. ** Query Union (unionCount:"+count+" unionAll:"+unionAll+")");
			
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
			info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(count){
						long cont = rs.getLong("aliasUnion");
						info(log,systemOut,"riga["+(index++)+"]= ["+cont+"]");
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
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		
		try{
			
			// TEST 2.
			
			if(count==false){
				info(log,systemOut,"\n\nb. ** Query Union OffSetLimit (unionCount:"+count+" unionAll:"+unionAll+")");
				
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

				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSetLimit UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
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
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		try{
			
			// TEST 3.
						
			if(count==false){
				info(log,systemOut,"\n\nc. ** Query Union OffSet (unionCount:"+count+" unionAll:"+unionAll+")");
				
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

				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 4.
						
			if(count==false){
				info(log,systemOut,"\n\nd. ** Query Union Limit (unionCount:"+count+" unionAll:"+unionAll+")");
				
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

				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
					stmtQuery = con.createStatement();
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					boolean foundEndsWith10 = false;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("cont");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
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
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		
		
		
		
		try{
			
			// TEST 5.
			info(log,systemOut,"\n\ne. ** Query Union con select* (unionCount:"+count+" unionAll:"+unionAll+")");
			
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
			info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
				
				stmtQuery = con.createStatement();
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(count){
						long cont = rs.getLong("aliasUnion");
						info(log,systemOut,"riga["+(index++)+"]= ["+cont+"]");
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
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(cont!=1){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
									throw new Exception("Expected 1, found "+cont);
								}
							}
							else{
								if(cont!=2){
									rs.close();
									rs = null;
									stmtQuery.close();
									stmtQuery = null;
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		
		
		if(count==false){

			String msgOffset = "Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union";
			String msgLimit = "Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union";
			
			
			// TEST 6.
			info(log,systemOut,"\n\nf. ** Query Union con select* e LimitOffset (unionCount:"+count+" unionAll:"+unionAll+")");
			
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
				//if(count){
				//	info(log,systemOut,"["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				//}else{
				info(log,systemOut,"["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				//}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}



			// TEST 7.
			info(log,systemOut,"\n\ng. ** Query Union con select* e Offset (unionCount:"+count+" unionAll:"+unionAll+")");
						
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setOffset(2);
			
			try{
				//if(count){
				//	info(log,systemOut,"["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				//}else{
				info(log,systemOut,"["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				//}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

			

			// TEST 8.
			info(log,systemOut,"\n\nh. ** Query Union con select* e Limit (unionCount:"+count+" unionAll:"+unionAll+")");
									
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setLimit(10);
			
			try{
				//if(count){
				//	info(log,systemOut,"["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				//}else{
				info(log,systemOut,"["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				//}
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
				info(log,systemOut,"\n\ni. ** Query Union per test SelectForUpdate (unionCount:"+count+" unionAll:"+unionAll+")");
				
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
//				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
//				try{
//					stmtQuery = con.createStatement();
//					rs = stmtQuery.executeQuery(test);
//
//					int index = 0;
//					if(rs.next()){
//						if(count){
//							long cont = rs.getLong("aliasUnion");
//							info(log,systemOut,"riga["+(index++)+"]= ["+cont+"]");
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
//							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):("+dest+")");
//							while(rs.next()){
//								mit = rs.getString("mit");
//								dest = rs.getString("dest");
//								info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):("+dest+")");		
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
//					if(rs!=null){
//						rs.close();
//						rs = null;
//					}
//					if(stmtQuery!=null){
//						stmtQuery.close();
//						stmtQuery = null;
//					}
				
			}catch(Exception e){
				findError = true;
				info(log,systemOut,"ERRORE ATTESO: "+e.getMessage());
			}finally{
				try{
					if(rs!=null){
						rs.close();
						rs = null;
					}
				}catch(Exception eClose){}
				try{
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
				}catch(Exception eClose){
					// close
				}
			}
			if(findError==false){
				throw new Exception("Atteso errore utilizzo select for update non permesso in union");
			}
		}
	}
	



	
	
	
	
	
	
	private static void testUnionWithGroupBy_engine(TipiDatabase tipo, boolean count, boolean unionAll, Connection con, boolean selectForUpdate) throws Exception {


		Statement stmtQuery = null;
		ResultSet rs = null;
		try{
						
			// TEST 1.
			info(log,systemOut,"\n\na. ** Query UnionGroupBy (unionCount:"+count+" unionAll:"+unionAll+")");
			
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
			info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			try{
				stmtQuery = con.createStatement();
				if(stmtQuery==null) {
					throw new Exception("Statement is null"); 
				}
				rs = stmtQuery.executeQuery(test);
				int index = 0;
				if(rs.next()){
					if(count){
						long cont = rs.getLong("aliasUnion");
						info(log,systemOut,"riga["+(index++)+"]= ["+cont+"]");
						if(cont!=(limit)){
							throw new Exception("Expected "+(limit)+", found "+cont);
						}
					}
					else{
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
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
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
			}catch(Exception e){
				
				throw e;
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		
		
		try{
			
			// TEST 2.
			
			if(count==false){
				info(log,systemOut,"\n\nb. ** Query UnionGroupBy OffSetLimit (unionCount:"+count+" unionAll:"+unionAll+")");
				
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

				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSetLimit UnionAll]:\n\t"+test);
				try{
					stmtQuery = con.createStatement();
					if(stmtQuery==null) {
						throw new Exception("Statement is null"); 
					}
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
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
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		
		try{
			
			// TEST 3.
						
			if(count==false){
				info(log,systemOut,"\n\nc. ** Query UnionGroupBy  OffSet (unionCount:"+count+" unionAll:"+unionAll+")");
				
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

				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
					stmtQuery = con.createStatement();
					if(stmtQuery==null) {
						throw new Exception("Statement is null"); 
					}
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 4.
						
			if(count==false){
				info(log,systemOut,"\n\nd. ** Query UnionGroupBy Limit (unionCount:"+count+" unionAll:"+unionAll+")");
				
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

				info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [OffSet UnionAll]:\n\t"+test);
				try{
					if(rs!=null){
						rs.close();
						rs = null;
					}
					if(stmtQuery!=null){
						stmtQuery.close();
						stmtQuery = null;
					}
					
					stmtQuery = con.createStatement();
					if(stmtQuery==null) {
						throw new Exception("Statement is null"); 
					}
					rs = stmtQuery.executeQuery(test);
					int index = 0;
					boolean foundEndsWith10 = false;
					if(rs.next()){
						
						String mit = rs.getString("mit");
						long cont = rs.getLong("contRisultatoGroupBy");
						info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
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
							info(log,systemOut,"riga["+(index++)+"]= ("+mit+"):["+cont+"]");
							if(mit.endsWith("10")){
								if(unionAll){
									if(cont!=2){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 2, found "+cont);
									}
								}
								else{
									if(cont!=1){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 1, found "+cont);
									}
								}
							}
							else{
								if(unionAll){
									if(cont!=4){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
										throw new Exception("Expected 4, found "+cont);
									}
								}
								else{
									if(cont!=2){
										rs.close();
										rs = null;
										stmtQuery.close();
										stmtQuery = null;
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
					rs = null;
					stmtQuery.close();
					stmtQuery = null;
				
					
				}catch(Exception e){
					
					throw e;
				}
			}
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
		
		
		
		
		try{
			
			// TEST 5.
			info(log,systemOut,"\n\ne. ** Query UnionGroupBy con select* (unionCount:"+count+" unionAll:"+unionAll+")");
			
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
			info(log,systemOut,"\nTest(count:"+count+" unionAll:"+unionAll+") ["+tipo.toString()+"] [Normale UnionAll]:\n\t"+test);
			throw new Exception("Attesa eccezione: Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field");
		}catch(SQLQueryObjectException s){
			if(!s.getMessage().equals("Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field"))
				throw new Exception("Attesa eccezione: Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field. Trovata: "+s.getMessage());
		}
		
		
		
		
		if(count==false){

			String msgOffset = "Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union";
			String msgLimit = "Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union";
			
			
			// TEST 6.
			info(log,systemOut,"\n\nf. ** Query UnionGroupBy con select* e LimitOffset (unionCount:"+count+" unionAll:"+unionAll+")");
			
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
				/*if(count){
					info(log,systemOut,"["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{*/
				// count e' per forza false
				info(log,systemOut,"["+tipo.toString()+"] f. OffSetLimit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				//}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}



			// TEST 7.
			info(log,systemOut,"\n\ng. ** Query UnionGroupBy con select* e Offset (unionCount:"+count+" unionAll:"+unionAll+")");
						
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);
			
			sqlQueryObject.setOffset(2);
			
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");

			try{
				/*if(count){
					info(log,systemOut,"["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{*/
				// count e' per forza false
				info(log,systemOut,"["+tipo.toString()+"] g. OffSet *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				//}
				throw new Exception("Attesa eccezione: Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgOffset))
					throw new Exception("Attesa eccezione: ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

			

			// TEST 8.
			info(log,systemOut,"\n\nh. ** Query UnionGroupBy con select* e Limit (unionCount:"+count+" unionAll:"+unionAll+")");
									
			sqlQueryObject = createSQLQueryObjectCore(tipo,false);

			sqlQueryObject.addOrderBy("mittente");
			sqlQueryObject.addOrderBy("destinatario",false);
			sqlQueryObject.setSortType(true);

			sqlQueryObject.setLimit(10);
			
			sqlQueryObject.addGroupBy("mittente");
			sqlQueryObject.addGroupBy("destinatario");

			try{
				/*if(count){
					info(log,systemOut,"["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnionCount(unionAll,"aliasUnion", prepare1, prepare2));
				}else{*/
				// count e' per forza false
				info(log,systemOut,"["+tipo.toString()+"] h Limit *: \n\t"+sqlQueryObject.createSQLUnion(unionAll, prepare1, prepare2));
				//}
				throw new Exception("Attesa eccezione: Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union");
			}catch(SQLQueryObjectException s){
				if(!s.getMessage().equals(msgLimit) && !s.getMessage().equals(msgOffset)) // alcune implementazione forzano l'offset a 0 se si imposta il limit e non l'offset
					throw new Exception("Attesa eccezione: ["+msgLimit+"] oppure ["+msgOffset+"]. Trovata: ["+s.getMessage()+"]");
			}

		}
		
	}
	
	
	private static void testEmptyNull_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate) throws Exception {
		PreparedStatement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			info(log,systemOut,"\n\nh. ** Test IS NULL / IS EMPTY");
			
			
			// TEST 1. (IS NULL)
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addWhereIsNullCondition("campo_vuoto");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			
			String testIsNull = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest1-"+table+" isNull:\n\t"+testIsNull);
			try{
				stmtQuery = con.prepareStatement(testIsNull);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){

					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+" campo_vuoto:("+rs.getString("campo_vuoto")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = 10;
				if(TipiDatabase.ORACLE.equals(tipo)){
					attesi = 19; // in oracle le stringhe vuote sono trattate come null
				}
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			// TEST 2. (IS NOT NULL)
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addWhereIsNotNullCondition("campo_vuoto");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			
			String testIsNotNull = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest2-"+table+" isNotNull:\n\t"+testIsNotNull);
			try{
				stmtQuery = con.prepareStatement(testIsNotNull);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){

					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+" campo_vuoto:("+rs.getString("campo_vuoto")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = 11;
				if(TipiDatabase.ORACLE.equals(tipo)){
					attesi = 2; // in oracle le stringhe vuote sono trattate come null
				}
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			// TEST 3. (IS EMPTY)
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addWhereIsEmptyCondition("campo_vuoto");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			
			String testIsEmpty = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest3-"+table+" isEmpty:\n\t"+testIsEmpty);
			try{
				stmtQuery = con.prepareStatement(testIsEmpty);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){

					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+" campo_vuoto:("+rs.getString("campo_vuoto")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = 9;
				if(TipiDatabase.ORACLE.equals(tipo)){
					attesi = 19; // in oracle le stringhe vuote sono trattate come null
				}
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			// TEST 4. (IS NOT EMPTY)
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addWhereIsNotEmptyCondition("campo_vuoto");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(true);
			
			String testIsNotEmpty = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest4-"+table+" isNotEmpty:\n\t"+testIsNotEmpty);
			try{
				stmtQuery = con.prepareStatement(testIsNotEmpty);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){

					info(log,systemOut,"riga["+(index++)+"]="+rs.getString("descrizione")+
							" (time:"+rs.getTimestamp("gdo")+" campo_vuoto:("+rs.getString("campo_vuoto")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = 2;
				if(TipiDatabase.ORACLE.equals(tipo)){
					attesi = 2; // in oracle le stringhe vuote sono trattate come null
				}
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	private static void testCase_engine(TipiDatabase tipo, String table, Connection con, boolean selectForUpdate) throws Exception {
		PreparedStatement stmtQuery = null;
		ResultSet rs = null;
		try{
			
			info(log,systemOut,"\n\nh. ** Test CASE");
			
			
			// TEST 1. (CASE)
			
			SQLQueryObjectCore sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectField("destinatario");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.addWhereIsNullCondition("campo_update");
			sqlQueryObject.addWhereIsNullCondition("campo_int_update");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSortType(true);
			
			String testQuery = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest1-"+table+" case:\n\t"+testQuery);
			try{
				stmtQuery = con.prepareStatement(testQuery);
				rs = stmtQuery.executeQuery();
				int index = 0;
				while(rs.next()){

					info(log,systemOut,"riga["+(index++)+"] mittente:"+rs.getString("mittente")+" destinatario:"+rs.getString("destinatario")+
							" (time:"+rs.getTimestamp("gdo")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = ROW;
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			
			Case caseSql = new Case(CastColumnType.STRING, false, "?");
			caseSql.addCase("mittente=?", "?");
			caseSql.addCase("mittente=?", "?");
			sqlQueryObject.addUpdateField("campo_update", caseSql);
			
			Case caseSqlInt = new Case(CastColumnType.INT, false, "?");
			caseSqlInt.addCase("mittente=? OR destinatario=?", "?");
			caseSqlInt.addCase("mittente=? OR destinatario=?", "?");
			sqlQueryObject.addUpdateField("campo_int_update", caseSqlInt);
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "CASO_0");
				stmtQuery.setString(index++, "SoggettoMittente1");
				stmtQuery.setString(index++, "CASO_1");
				stmtQuery.setString(index++, "CASO_DEFAULT");
				
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "SoggettoDestinatario1");
				stmtQuery.setInt(index++, 22);
				stmtQuery.setString(index++, "SoggettoMittente2");
				stmtQuery.setString(index++, "SoggettoDestinatario3");
				stmtQuery.setInt(index++, 33);
				stmtQuery.setInt(index++, 11);
				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
		
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectField("destinatario");
			sqlQueryObject.addSelectField("campo_update");
			sqlQueryObject.addSelectField("campo_int_update");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.addWhereIsNotNullCondition("campo_update");
			sqlQueryObject.addWhereIsNotNullCondition("campo_int_update");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSortType(true);
			
			testQuery = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest1-"+table+" case:\n\t"+testQuery);
			try{
				stmtQuery = con.prepareStatement(testQuery);
				rs = stmtQuery.executeQuery();
				int index = 0;
				int stringCasoA = 0;
				int stringCasoB = 0;
				int stringCasoDefault = 0;
				int intCasoA = 0;
				int intCasoB = 0;
				int intCasoDefault = 0;
				while(rs.next()){

					String valoreString = rs.getString("campo_update");
					int valoreInt = rs.getInt("campo_int_update");
					
					if("CASO_0".equals(valoreString)) {
						stringCasoA++;
					}
					else if("CASO_1".equals(valoreString)) {
						stringCasoB++;
					}
					else if("CASO_DEFAULT".equals(valoreString)) {
						stringCasoDefault++;
					}
					
					if(valoreInt == 22) {
						intCasoA++;
					}
					else if(valoreInt == 33) {
						intCasoB++;
					}
					else if(valoreInt == 11) {
						intCasoDefault++;
					}
					
					info(log,systemOut,"riga["+(index++)+"] (string:"+valoreString+" int:"+valoreInt+") mittente:"+rs.getString("mittente")+" destinatario:"+rs.getString("destinatario")+
							" (time:"+rs.getTimestamp("gdo")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = ROW;
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
				attesi = 2;
				int trovati = stringCasoA;
				info(log,systemOut,"(stringCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 2;
				trovati = stringCasoB;
				info(log,systemOut,"(stringCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = ROW-4;
				trovati = stringCasoDefault;
				info(log,systemOut,"(stringCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoA;
				info(log,systemOut,"(intCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoB;
				info(log,systemOut,"(intCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = ROW-8;
				trovati = intCasoDefault;
				info(log,systemOut,"(intCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			info(log,systemOut,"\n\nh. ** Test CASE SENZA ELSE");
			
			// ripulisco
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			sqlQueryObject.addUpdateField("campo_update", "?");
			sqlQueryObject.addUpdateField("campo_int_update", "?");
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, null);
				stmtQuery.setInt(index++, -1);				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
			
			
			// TEST 2. (CASE SENZA ELSE)
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			
			caseSql = new Case(CastColumnType.STRING);
			caseSql.addCase("mittente=?", "?");
			caseSql.addCase("mittente=?", "?");
			sqlQueryObject.addUpdateField("campo_update", caseSql);
			
			caseSqlInt = new Case(CastColumnType.INT);
			caseSqlInt.addCase("mittente=? OR destinatario=?", "?");
			caseSqlInt.addCase("mittente=? OR destinatario=?", "?");
			sqlQueryObject.addUpdateField("campo_int_update", caseSqlInt);
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "CASO_0");
				stmtQuery.setString(index++, "SoggettoMittente1");
				stmtQuery.setString(index++, "CASO_1");
				
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "SoggettoDestinatario1");
				stmtQuery.setInt(index++, 22);
				stmtQuery.setString(index++, "SoggettoMittente2");
				stmtQuery.setString(index++, "SoggettoDestinatario3");
				stmtQuery.setInt(index++, 33);
				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
		
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectField("destinatario");
			sqlQueryObject.addSelectField("campo_update");
			sqlQueryObject.addSelectField("campo_int_update");
			sqlQueryObject.addOrderBy("gdo");
//			sqlQueryObject.addWhereIsNotNullCondition("campo_update");
//			sqlQueryObject.addWhereIsNotNullCondition("campo_int_update");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSortType(true);
			
			testQuery = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest1-"+table+" case:\n\t"+testQuery);
			try{
				stmtQuery = con.prepareStatement(testQuery);
				rs = stmtQuery.executeQuery();
				int index = 0;
				int stringCasoA = 0;
				int stringCasoB = 0;
				int stringCasoDefault = 0;
				int intCasoA = 0;
				int intCasoB = 0;
				int intCasoDefault = 0;
				while(rs.next()){

					String valoreString = rs.getString("campo_update");
					int valoreInt = rs.getInt("campo_int_update");
					
					if("CASO_0".equals(valoreString)) {
						stringCasoA++;
					}
					else if("CASO_1".equals(valoreString)) {
						stringCasoB++;
					}
					else if("CASO_DEFAULT".equals(valoreString)) {
						stringCasoDefault++;
					}
					
					if(valoreInt == 22) {
						intCasoA++;
					}
					else if(valoreInt == 33) {
						intCasoB++;
					}
					else if(valoreInt == 11) {
						intCasoDefault++;
					}
					
					info(log,systemOut,"riga["+(index++)+"] (string:"+valoreString+" int:"+valoreInt+") mittente:"+rs.getString("mittente")+" destinatario:"+rs.getString("destinatario")+
							" (time:"+rs.getTimestamp("gdo")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = ROW;
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
				attesi = 2;
				int trovati = stringCasoA;
				info(log,systemOut,"(stringCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 2;
				trovati = stringCasoB;
				info(log,systemOut,"(stringCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 0;
				trovati = stringCasoDefault;
				info(log,systemOut,"(stringCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoA;
				info(log,systemOut,"(intCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoB;
				info(log,systemOut,"(intCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 0;
				trovati = intCasoDefault;
				info(log,systemOut,"(intCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			info(log,systemOut,"\n\nh. ** Test CASE senza ?");
			
			// ripulisco
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			sqlQueryObject.addUpdateField("campo_update", "?");
			sqlQueryObject.addUpdateField("campo_int_update", "?");
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, null);
				stmtQuery.setInt(index++, -1);				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
			
			
			// TEST 2. (CASE senza ?)
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			
			caseSql = new Case(CastColumnType.STRING, true, "CASO_DEFAULT");
			caseSql.addCase("mittente=?", "CASO_0");
			caseSql.addCase("mittente=?", "CASO_1");
			sqlQueryObject.addUpdateField("campo_update", caseSql);
			
			caseSqlInt = new Case(CastColumnType.INT, false, "11");
			caseSqlInt.addCase("mittente=? OR destinatario=?", "22");
			caseSqlInt.addCase("mittente=? OR destinatario=?", "33");
			sqlQueryObject.addUpdateField("campo_int_update", caseSqlInt);
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "SoggettoMittente1");
				
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "SoggettoDestinatario1");
				stmtQuery.setString(index++, "SoggettoMittente2");
				stmtQuery.setString(index++, "SoggettoDestinatario3");
				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
		
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectField("destinatario");
			sqlQueryObject.addSelectField("campo_update");
			sqlQueryObject.addSelectField("campo_int_update");
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.addWhereIsNotNullCondition("campo_update");
			sqlQueryObject.addWhereIsNotNullCondition("campo_int_update");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSortType(true);
			
			testQuery = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest1-"+table+" case:\n\t"+testQuery);
			try{
				stmtQuery = con.prepareStatement(testQuery);
				rs = stmtQuery.executeQuery();
				int index = 0;
				int stringCasoA = 0;
				int stringCasoB = 0;
				int stringCasoDefault = 0;
				int intCasoA = 0;
				int intCasoB = 0;
				int intCasoDefault = 0;
				while(rs.next()){

					String valoreString = rs.getString("campo_update");
					int valoreInt = rs.getInt("campo_int_update");
					
					if("CASO_0".equals(valoreString)) {
						stringCasoA++;
					}
					else if("CASO_1".equals(valoreString)) {
						stringCasoB++;
					}
					else if("CASO_DEFAULT".equals(valoreString)) {
						stringCasoDefault++;
					}
					
					if(valoreInt == 22) {
						intCasoA++;
					}
					else if(valoreInt == 33) {
						intCasoB++;
					}
					else if(valoreInt == 11) {
						intCasoDefault++;
					}
					
					info(log,systemOut,"riga["+(index++)+"] (string:"+valoreString+" int:"+valoreInt+") mittente:"+rs.getString("mittente")+" destinatario:"+rs.getString("destinatario")+
							" (time:"+rs.getTimestamp("gdo")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = ROW;
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
				attesi = 2;
				int trovati = stringCasoA;
				info(log,systemOut,"(stringCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 2;
				trovati = stringCasoB;
				info(log,systemOut,"(stringCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = ROW-4;
				trovati = stringCasoDefault;
				info(log,systemOut,"(stringCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoA;
				info(log,systemOut,"(intCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoB;
				info(log,systemOut,"(intCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = ROW-8;
				trovati = intCasoDefault;
				info(log,systemOut,"(intCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
			
			
			
			
			
			
			info(log,systemOut,"\n\nh. ** Test CASE SENZA ELSE senza ?");
			
			// ripulisco
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			sqlQueryObject.addUpdateField("campo_update", "?");
			sqlQueryObject.addUpdateField("campo_int_update", "?");
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, null);
				stmtQuery.setInt(index++, -1);				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
			
			
			// TEST 2. (CASE SENZA ELSE senza ?)
			
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			sqlQueryObject.addUpdateTable(table);
			
			caseSql = new Case(CastColumnType.STRING);
			caseSql.setStringValueType(true);
			caseSql.addCase("mittente=?", "CASO_0");
			caseSql.addCase("mittente=?", "CASO_1");
			sqlQueryObject.addUpdateField("campo_update", caseSql);
			
			caseSqlInt = new Case(CastColumnType.INT);
			caseSqlInt.addCase("mittente=? OR destinatario=?", "22");
			caseSqlInt.addCase("mittente=? OR destinatario=?", "33");
			sqlQueryObject.addUpdateField("campo_int_update", caseSqlInt);
			try{
				String update = sqlQueryObject.createSQLUpdate();
				info(log,systemOut,"\ntest1-"+table+" case:\n\t"+update);
				stmtQuery = con.prepareStatement(update);
				int index = 1;
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "SoggettoMittente1");
				
				stmtQuery.setString(index++, "SoggettoMittente0");
				stmtQuery.setString(index++, "SoggettoDestinatario1");
				stmtQuery.setString(index++, "SoggettoMittente2");
				stmtQuery.setString(index++, "SoggettoDestinatario3");
				
				stmtQuery.executeUpdate();
			}catch(Exception e){
				throw e;
			}
		
			sqlQueryObject = (SQLQueryObjectCore) createSQLQueryObjectCore(tipo,selectForUpdate); 
			
			sqlQueryObject.addFromTable(table);
			sqlQueryObject.addSelectField("campo_vuoto");
			sqlQueryObject.addSelectField("descrizione");
			sqlQueryObject.addSelectField("gdo");
			sqlQueryObject.addSelectField("mittente");
			sqlQueryObject.addSelectField("destinatario");
			sqlQueryObject.addSelectField("campo_update");
			sqlQueryObject.addSelectField("campo_int_update");
			sqlQueryObject.addOrderBy("gdo");
//			sqlQueryObject.addWhereIsNotNullCondition("campo_update");
//			sqlQueryObject.addWhereIsNotNullCondition("campo_int_update");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSortType(true);
			
			testQuery = sqlQueryObject.createSQLQuery();
			info(log,systemOut,"\ntest1-"+table+" case:\n\t"+testQuery);
			try{
				stmtQuery = con.prepareStatement(testQuery);
				rs = stmtQuery.executeQuery();
				int index = 0;
				int stringCasoA = 0;
				int stringCasoB = 0;
				int stringCasoDefault = 0;
				int intCasoA = 0;
				int intCasoB = 0;
				int intCasoDefault = 0;
				while(rs.next()){

					String valoreString = rs.getString("campo_update");
					int valoreInt = rs.getInt("campo_int_update");
					
					if("CASO_0".equals(valoreString)) {
						stringCasoA++;
					}
					else if("CASO_1".equals(valoreString)) {
						stringCasoB++;
					}
					else if("CASO_DEFAULT".equals(valoreString)) {
						stringCasoDefault++;
					}
					
					if(valoreInt == 22) {
						intCasoA++;
					}
					else if(valoreInt == 33) {
						intCasoB++;
					}
					else if(valoreInt == 11) {
						intCasoDefault++;
					}
					
					info(log,systemOut,"riga["+(index++)+"] (string:"+valoreString+" int:"+valoreInt+") mittente:"+rs.getString("mittente")+" destinatario:"+rs.getString("destinatario")+
							" (time:"+rs.getTimestamp("gdo")+")");

				}
				rs.close();
				rs = null;
				stmtQuery.close();
				stmtQuery = null;
				
				int attesi = ROW;
				info(log,systemOut,"Attesi ["+attesi+"] trovati["+index+"]");
				if(attesi != index){
					throw new Exception("Attesi ["+attesi+"] trovati["+index+"]");
				}
				
				attesi = 2;
				int trovati = stringCasoA;
				info(log,systemOut,"(stringCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 2;
				trovati = stringCasoB;
				info(log,systemOut,"(stringCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 0;
				trovati = stringCasoDefault;
				info(log,systemOut,"(stringCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoA;
				info(log,systemOut,"(intCasoA) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 4;
				trovati = intCasoB;
				info(log,systemOut,"(intCasoB) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
				attesi = 0;
				trovati = intCasoDefault;
				info(log,systemOut,"(intCasoDefault) Attesi ["+attesi+"] trovati["+trovati+"]");
				if(attesi != trovati){
					throw new Exception("Attesi ["+attesi+"] trovati["+trovati+"]");
				}
				
			}catch(Exception e){
				
				throw e;
			}
			
			
			
		}finally{
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch(Exception eClose){}
			try{
				if(stmtQuery!=null){
					stmtQuery.close();
					stmtQuery = null;
				}
			}catch(Exception eClose){
				// close
			}
		}
	}

}
