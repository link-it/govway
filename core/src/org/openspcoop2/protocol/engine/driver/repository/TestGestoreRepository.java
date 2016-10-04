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
package org.openspcoop2.protocol.engine.driver.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * TestGestoreRepository
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestGestoreRepository {

	private static Logger log = null;
	
	public static void main(String[] args) throws Exception{
		
		/**
		 * Tabelle
		 * 
		 * ---------------------------
		 * postgresql
		 * CREATE TABLE prova_bytewise (
		 *      descrizione VARCHAR(255) NOT NULL,
		 *      REPOSITORY_ACCESS INT NOT NULL DEFAULT 0
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * mysql
		 * CREATE TABLE prova_bytewise (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		REPOSITORY_ACCESS INT NOT NULL DEFAULT 0
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * oracle
		 * CREATE TABLE prova_bytewise (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		REPOSITORY_ACCESS RAW(8) DEFAULT 'C101' NOT NULL
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * hsql
		 * CREATE TABLE prova_bytewise (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		REPOSITORY_ACCESS INT NOT NULL
		 * );
		 * ALTER TABLE prova_bytewise ALTER COLUMN REPOSITORY_ACCESS SET DEFAULT 0;
		 * ---------------------------
		 * 
		 * ---------------------------
		 * sqlserver
		 * CREATE TABLE prova_bytewise (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		REPOSITORY_ACCESS INT NOT NULL DEFAULT 0
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * db2
		 * CREATE TABLE prova_bytewise (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		REPOSITORY_ACCESS INT NOT NULL DEFAULT 0
		 * );
		 * ---------------------------
		 */
		
		
		File logFile = File.createTempFile("runGestoreRepositoryTest_", ".log");
		System.out.println("LogMessages write in "+logFile.getAbsolutePath());
		LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%m %n");
		log = LoggerWrapperFactory.getLogger(TestGestoreRepository.class);
		
		DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), new Properties(), log);
		
		String url = null;
		String driver = null;
		String userName = null;
		String password = null;
		TipiDatabase tipoDatabase = TipiDatabase.toEnumConstant(args[0].trim());
		
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
		try{
			con = DriverManager.getConnection(url, userName, password);
			
			test(con, tipoDatabase);
						
	    }finally{
	    	try{
	    		con.close();
	    	}catch(Exception eClose){}
	    }
	}

	
	@SuppressWarnings("resource")
	private static void test(Connection con,TipiDatabase tipoDatabase) throws Exception{
		
	    Statement stmtDelete = null;
	    PreparedStatement stmtInsert = null;
	    Statement stmtQuery = null;
	    ResultSet rsQuery = null;
	    try{
	    	
	    	// step1. Elimino tutte le entries
	    	String delete = "delete from prova_bytewise";
	    	stmtDelete = con.createStatement();
	    	stmtDelete.execute(delete);
	    		    	
	    	
	    	IGestoreRepository gestoreRepository = GestoreRepositoryFactory.createRepositoryBuste(tipoDatabase);
	    	String tipo = GestoreRepositoryFactory.getTipoRepositoryBuste(tipoDatabase);
	    	log.info("Creato IGestoreRepository["+tipo+"] di tipo "+gestoreRepository.getClass().getName());
	    	
	    	
	    	String colonna = gestoreRepository.createSQLFields();
	    	log.info("createSQLFields: "+colonna);
	    	if("REPOSITORY_ACCESS".equals(colonna)==false){
	    		throw new Exception("createSQLFields ha ritornato un valore differente da quello atteso (REPOSITORY_ACCESS): "+colonna);
	    	}
	    	
	    	// Inserisco entry
	    	ISQLQueryObject sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
	    	sqlQuery.addInsertTable("prova_bytewise");
	    	sqlQuery.addInsertField("descrizione", "?");
	    	stmtInsert = con.prepareStatement(sqlQuery.createSQLInsert());
	    	stmtInsert.setString(1, "descrizione di esempio");
	    	int row = stmtInsert.executeUpdate();
	    	log.info("\n\ninserita riga: "+row);
	    	
	    	// Verifico default (history)
	    	String query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(false);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 1. history=false). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 1. history=false)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico default (history+profilo)
	    	query = query + " and "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(false);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 2. history=false and profilo=false). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 2. history=false and profilo=false)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico default (history+profilo+pdd)
	    	query = query + " and "+gestoreRepository.createSQLCondition_PdD(false);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 3. history=false and profilo=false and pdd=false). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 3. history=false and profilo=false and pdd=false)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	// Imposto History
	    	String update = "UPDATE prova_bytewise set "+gestoreRepository.createSQLSet_History(true);
	    	stmtInsert = con.prepareStatement(update);
	    	log.info("\n\nUpdate ["+update+"]");
	    	row = stmtInsert.executeUpdate();
	    	log.info("aggiornata riga: "+row);
	    	
	    	// Verifico (history=false)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(false);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 4. history=false)");
	    	}else{
	    		log.info("(test 4. history=false). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 5. history=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 5. history=true)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	// Imposto anche Profilo
	    	update = "UPDATE prova_bytewise set "+gestoreRepository.createSQLSet_ProfiloCollaborazione(true);
	    	stmtInsert = con.prepareStatement(update);
	    	log.info("\n\nUpdate ["+update+"]");
	    	row = stmtInsert.executeUpdate();
	    	log.info("aggiornata riga: "+row);
	    	
	    	// Verifico (history=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 6. history=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 6. history=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true and profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true)+
	    			 "and "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 7. history=true and profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 7. history=true and profilo=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true or profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true)+
	    			 "or "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 8. history=true or profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 8. history=true or profilo=true)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	// Imposto anche PdD
	    	update = "UPDATE prova_bytewise set "+gestoreRepository.createSQLSet_PdD(true);
	    	stmtInsert = con.prepareStatement(update);
	    	log.info("\n\nUpdate ["+update+"]");
	    	row = stmtInsert.executeUpdate();
	    	log.info("aggiornata riga: "+row);
	    	
	    	// Verifico (history=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 9. history=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 9. history=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true and profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true)+
	    			 "and "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 10. history=true and profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 10. history=true and profilo=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true or profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true)+
	    			 "or "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 1. history=true or profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 11. history=true or profilo=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true and profilo=true and pdd=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true)+
	    			 "and "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true)+
	    			 "and "+gestoreRepository.createSQLCondition_PdD(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 12. history=true and profilo=true and pdd=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 12. history=true and profilo=true and pdd=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (history=true or profilo=true or pdd=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true)+
	    			 "or "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true)+
	    			 "or "+gestoreRepository.createSQLCondition_PdD(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 13. history=true or profilo=true or pdd=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 13. history=true or profilo=true or pdd=true)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	
	    	// test
	    	
	    	// Solo History
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyHistory();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 14. onlyHistory)");
	    	}else{
	    		log.info("(test 14. onlyHistory). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo Profilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 15. onlyProfilo)");
	    	}else{
	    		log.info("(test 15. onlyProfilo). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo PdD
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPdd();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 16. onlyPdd)");
	    	}else{
	    		log.info("(test 16. onlyPdd). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo PdD e Profilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPddAndProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 17. onlyPddAndProfilo)");
	    	}else{
	    		log.info("(test 17. onlyPddAndProfilo). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// DisabledAll
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_disabledAll();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 18. disableAll)");
	    	}else{
	    		log.info("(test 18. disableAll). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	// disabilito history
	    	
	    	update = "UPDATE prova_bytewise set "+gestoreRepository.createSQLSet_History(false);
	    	stmtInsert = con.prepareStatement(update);
	    	log.info("\n\nUpdate ["+update+"]");
	    	row = stmtInsert.executeUpdate();
	    	log.info("aggiornata riga: "+row);
	    	
	    	// Verifico (history=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 19. history=true)");
	    	}else{
	    		log.info("(test 19. history=true). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (pdd=true or profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_PdD(true)+
	    			 "or "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 20. pdd=true or profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 20. pdd=true or profilo=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo Profilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 21. onlyProfilo)");
	    	}else{
	    		log.info("(test 21. onlyProfilo). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo PdD
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPdd();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 22. onlyPdd)");
	    	}else{
	    		log.info("(test 22. onlyPdd). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico Solo PddAndProfilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPddAndProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 23. pddAndProfilo). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 20. pddAndProfilo)");
	    	}
	    	rsQuery.close();
	    	
	    	// DisabledAll
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_disabledAll();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 24. disableAll)");
	    	}else{
	    		log.info("(test 24. disableAll). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	

	    	
	    	// disabilito pdd
	    	
	    	update = "UPDATE prova_bytewise set "+gestoreRepository.createSQLSet_PdD(false);
	    	stmtInsert = con.prepareStatement(update);
	    	log.info("\n\nUpdate ["+update+"]");
	    	row = stmtInsert.executeUpdate();
	    	log.info("aggiornata riga: "+row);
	    	
	    	// Verifico (history=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 25. history=true)");
	    	}else{
	    		log.info("(test 25. history=true). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (pdd=true or profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_PdD(true)+
	    			 "or "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 26. pdd=true or profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 26. pdd=true or profilo=true)");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo Profilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 27. profilo=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 27. onlyProfilo)");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo PdD
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPdd();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 28. onlyPdd)");
	    	}else{
	    		log.info("(test 28. onlyPdd). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico Solo PddAndProfilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPddAndProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 29. pddAndProfilo)");
	    	}else{
	    		log.info("(test 29. pddAndProfilo). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// DisabledAll
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_disabledAll();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 30. disableAll)");
	    	}else{
	    		log.info("(test 30. disableAll). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	// disabilito profilo
	    	
	    	update = "UPDATE prova_bytewise set "+gestoreRepository.createSQLSet_ProfiloCollaborazione(false);
	    	stmtInsert = con.prepareStatement(update);
	    	log.info("\n\nUpdate ["+update+"]");
	    	row = stmtInsert.executeUpdate();
	    	log.info("aggiornata riga: "+row);
	    	
	    	// Verifico (history=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 31. history=true)");
	    	}else{
	    		log.info("(test 31. history=true). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico (pdd=true or profilo=true)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_PdD(true)+
	    			 "or "+gestoreRepository.createSQLCondition_ProfiloCollaborazione(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 32. pdd=true or profilo=true)");
	    	}else{
	    		log.info("(test 32. pdd=true or profilo=true). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo Profilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 33. onlyProfilo)");
	    	}else{
	    		log.info("(test 33. onlyProfilo). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Solo PdD
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPdd();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 34. onlyPdd)");
	    	}else{
	    		log.info("(test 34. onlyPdd). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// Verifico Solo PddAndProfilo
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_enableOnlyPddAndProfilo();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		throw new Exception("trovata Riga non attesa (valore:"+toStringEngine(rsQuery,1)+") (test 35. pddAndProfilo)");
	    	}else{
	    		log.info("(test 35. pddAndProfilo). Non e' stata trovata correttamente una entry");
	    	}
	    	rsQuery.close();
	    	
	    	// DisabledAll
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_disabledAll();
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 36. disableAll). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 36. disableAll)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	delete = "delete from prova_bytewise";
	    	stmtDelete = con.createStatement();
	    	stmtDelete.execute(delete);
	    	
	    	// Inserisco entry
	    	sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
	    	sqlQuery.addInsertTable("prova_bytewise");
	    	sqlQuery.addInsertField("descrizione", "?");
	    	sqlQuery.addInsertField(gestoreRepository.createSQLFieldHistory(), gestoreRepository.getSQLValueHistory(true));
	    	stmtInsert = con.prepareStatement(sqlQuery.createSQLInsert());
	    	stmtInsert.setString(1, "descrizione di esempio");
	    	row = stmtInsert.executeUpdate();
	    	log.info("\n\ninserita riga: "+row);
	    	
	    	// Verifico default (history)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(true);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 37. history=true). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 37. history=true)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	delete = "delete from prova_bytewise";
	    	stmtDelete = con.createStatement();
	    	stmtDelete.execute(delete);
	    	
	    	// Inserisco entry
	    	sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
	    	sqlQuery.addInsertTable("prova_bytewise");
	    	sqlQuery.addInsertField("descrizione", "?");
	    	sqlQuery.addInsertField(gestoreRepository.createSQLFieldHistory(), gestoreRepository.getSQLValueHistory(false));
	    	stmtInsert = con.prepareStatement(sqlQuery.createSQLInsert());
	    	stmtInsert.setString(1, "descrizione di esempio");
	    	row = stmtInsert.executeUpdate();
	    	log.info("\n\ninserita riga: "+row);
	    	
	    	// Verifico default (history)
	    	query = "select "+colonna+" from prova_bytewise where "+gestoreRepository.createSQLCondition_History(false);
	    	stmtQuery = con.createStatement();
	    	log.info("\n\nQuery ["+query+"]");
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		log.info("(test 38. history=false). Trovata entry con valore: "+toStringEngine(rsQuery,1));
	    	}else{
	    		throw new Exception("Riga attesa non trovata (test 38. history=false)");
	    	}
	    	rsQuery.close();
	    	
	    	
	    	
	    	
	    	
	    }finally{
	    	try{
	    		stmtDelete.close();
	    	}catch(Exception eClose){}
	    	try{
	    		stmtInsert.close();
	    	}catch(Exception eClose){}
	    	try{
	    		rsQuery.close();
	    	}catch(Exception eClose){}
	    	try{
	    		stmtQuery.close();
	    	}catch(Exception eClose){}
	    }
	}
	

	public static String toStringEngine(ResultSet rs, int index) throws SQLException{
		//Object o = rs.getObject(index);
		Object o = rs.getString(index);
		//log.info("Classe: "+o.getClass().getName());
//		if(o instanceof byte[]){
//			return new String((byte[])o);
//		}
		return o.toString();
	}
	
}
