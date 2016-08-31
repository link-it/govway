/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.utils.jdbc;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * TestJDBCAdapter
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestJDBCAdapter {

	private static Logger log = null;
	
	public static void main(String[] args) throws Exception{
		
		/**
		 * Tabelle
		 * 
		 * ---------------------------
		 * postgresql
		 * CREATE TABLE prova_bytes (
		 *      descrizione VARCHAR(255) NOT NULL,
		 *      contenuto BYTEA NOT NULL
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * mysql
		 * CREATE TABLE prova_bytes (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		contenuto MEDIUMBLOB NOT NULL
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * oracle
		 * CREATE TABLE prova_bytes (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		contenuto BLOB NOT NULL
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * hsql
		 * CREATE TABLE prova_bytes (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		contenuto VARBINARY(16777215) NOT NULL
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * sqlserver
		 * CREATE TABLE prova_bytes (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		contenuto VARBINARY(MAX) NOT NULL
		 * );
		 * ---------------------------
		 * 
		 * ---------------------------
		 * db2
		 * CREATE TABLE prova_bytes (
		 *      descrizione VARCHAR(255) NOT NULL,
		 * 		contenuto BLOB NOT NULL
		 * );
		 * ---------------------------
		 */
		
		File logFile = File.createTempFile("runJDBCAdapterTest_", ".log");
		System.out.println("LogMessages write in "+logFile.getAbsolutePath());
		LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%m %n");
		log = LoggerWrapperFactory.getLogger(TestJDBCAdapter.class);
		
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
			
			testBytes(con, tipoDatabase);
			
			testInputStream(con, tipoDatabase, true, false);
			
			testInputStream(con, tipoDatabase, false, true);
			
			testInputStream(con, tipoDatabase, false, false);
			
	    }finally{
	    	try{
	    		con.close();
	    	}catch(Exception eClose){}
	    }
	}

	
	private static void testBytes(Connection con,TipiDatabase tipoDatabase) throws Exception{
		
	    Statement stmtDelete = null;
	    PreparedStatement stmtInsert = null;
	    Statement stmtQuery = null;
	    ResultSet rsQuery = null;
	    InputStream pdfIs = null;
	    try{
	    	log.info("\n\n*** Test bytes[] ***");
	    	
	    	// step0. Recuper il documento
	    	pdfIs = TestJDBCAdapter.class.getResourceAsStream("/org/openspcoop2/utils/jdbc/test.pdf");
	    	if(pdfIs==null){
	    		throw new Exception("test.pdf resource not found");
	    	}
	    	byte[]data = Utilities.getAsByteArray(pdfIs);
	    	
	    	
	    	// step1. Elimino tutte le entries
	    	String delete = "delete from prova_bytes";
	    	stmtDelete = con.createStatement();
	    	stmtDelete.execute(delete);
	    		    	
	    	// step2. Creo IJDBCResource
	    	IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
	    	log.info("Creato IJDBCAdapter di tipo "+jdbcAdapter.getClass().getName());
	    	
	    	// step3. Inserisco documento
	    	ISQLQueryObject sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
	    	sqlQuery.addInsertTable("prova_bytes");
	    	sqlQuery.addInsertField("descrizione", "?");
	    	sqlQuery.addInsertField("contenuto", "?");
	    	
	    	stmtInsert = con.prepareStatement(sqlQuery.createSQLInsert());
	    	stmtInsert.setString(1, "descrizione di esempio");
	    	jdbcAdapter.setBinaryData(stmtInsert, 2, data);
	    	int row = stmtInsert.executeUpdate();
	    	log.info("Documento PDF inserito: "+row);
	    	
	    	// step4. Lettura per alias
	    	String query = "select * from prova_bytes";
	    	stmtQuery = con.createStatement();
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		
	    		// per nome
	    		byte[] read = jdbcAdapter.getBinaryData(rsQuery, "contenuto");
	    		for (int i = 0; i < data.length; i++) {
					if(data[i]!=read[i]){
						File fTmpOriginale = File.createTempFile("orig", "tmp");
						File fTmpLettoDaDB = File.createTempFile("lettoDaDB", "tmp");
						FileSystemUtilities.writeFile(fTmpOriginale, data);
						FileSystemUtilities.writeFile(fTmpLettoDaDB, read);
						throw new Exception("Documento salvato su database e riletto risulta differente. Salvate le immagini nei seguenti file temporanei: originale["+
								fTmpOriginale.getAbsolutePath()+"] prelevatoDaDatabase["+
								fTmpLettoDaDB.getAbsolutePath()+"]");
					}
				}
	    		
	    	}else{
	    		throw new Exception("Nessuna insert effettuata");
	    	}
	    	log.info("Documento PDF inserito e riletto dal database tramite columnName sono equivalenti" );
	    	rsQuery.close();
	    		    	
	    	// step5. Lettura per index
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		
	    		// per posizione
	    		byte[] read = jdbcAdapter.getBinaryData(rsQuery, 2);
	    		for (int i = 0; i < data.length; i++) {
					if(data[i]!=read[i]){
						File fTmpOriginale = File.createTempFile("orig", "tmp");
						File fTmpLettoDaDB = File.createTempFile("lettoDaDB", "tmp");
						FileSystemUtilities.writeFile(fTmpOriginale, data);
						FileSystemUtilities.writeFile(fTmpLettoDaDB, read);
						throw new Exception("Documento salvato su database e riletto risulta differente (read by index). Salvate le immagini nei seguenti file temporanei: originale["+
								fTmpOriginale.getAbsolutePath()+"] prelevatoDaDatabase["+
								fTmpLettoDaDB.getAbsolutePath()+"]");
					}
				}
	    		
	    	}else{
	    		throw new Exception("Nessuna insert effettuata");
	    	}
	    	log.info("Documento PDF inserito e riletto dal database tramite index sono equivalenti" );
	    	
	    	
	    }finally{
	    	try{
	    		pdfIs.close();
	    	}catch(Exception eClose){}
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
	
	
	
	private static void testInputStream(Connection con,TipiDatabase tipoDatabase,boolean withLength,boolean bufferEnabled) throws Exception{
		
	    Statement stmtDelete = null;
	    PreparedStatement stmtInsert = null;
	    Statement stmtQuery = null;
	    ResultSet rsQuery = null;
	    InputStream pdfIs = null;
	    try{
	    	log.info("\n\n*** Test InputStream (lenght:"+withLength+" buffer:"+bufferEnabled+") ***");
	    	
	    	// step0. Recuper il documento
	    	pdfIs = TestJDBCAdapter.class.getResourceAsStream("/org/openspcoop2/utils/jdbc/test.pdf");
	    	if(pdfIs==null){
	    		throw new Exception("test.pdf resource not found");
	    	}
	    	byte[]data = Utilities.getAsByteArray(pdfIs);
	    	pdfIs.close();
    		pdfIs = TestJDBCAdapter.class.getResourceAsStream("/org/openspcoop2/utils/jdbc/test.pdf");
    		
	    	int lenght = -1;
	    	if(withLength){
	    		lenght = data.length;
	    	}
	    	
	    	
	    	// step1. Elimino tutte le entries
	    	String delete = "delete from prova_bytes";
	    	stmtDelete = con.createStatement();
	    	stmtDelete.execute(delete);
	    		    	
	    	// step2. Creo IJDBCResource
	    	IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
	    	log.info("Creato IJDBCAdapter di tipo "+jdbcAdapter.getClass().getName());
	    	
	    	// step3. Inserisco documento
	    	ISQLQueryObject sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
	    	sqlQuery.addInsertTable("prova_bytes");
	    	sqlQuery.addInsertField("descrizione", "?");
	    	sqlQuery.addInsertField("contenuto", "?");
	    	
	    	stmtInsert = con.prepareStatement(sqlQuery.createSQLInsert());
	    	stmtInsert.setString(1, "descrizione di esempio");
	    	Exception eNotSupported = null;
	    	if(withLength){
	    		jdbcAdapter.setBinaryData(stmtInsert, 2, pdfIs, lenght);
	    	}
	    	else{
	    		String msgEccezioneAtteso = "SetBinaryStream non supportata dal tipo di database utilizzato";
	    		try{
	    			jdbcAdapter.setBinaryData(stmtInsert, 2, pdfIs, bufferEnabled);
	    		}catch(BinaryStreamNotSupportedException e){
	    			if(!bufferEnabled){
	    				if(!e.getMessage().startsWith(msgEccezioneAtteso)){
	    					throw new Exception("Ottenuta eccezione ["+e.getMessage()+"] differente da quella attesa ["+msgEccezioneAtteso+"...]");
	    				}
	    				else{
	    					eNotSupported = e;
	    				}
	    			}
	    			else{
	    				throw e;
	    			}
	    		}
	    	}
	    	
	    	if(!withLength && !bufferEnabled && eNotSupported!=null){
	    		//log.info(eNotSupported.getMessage());
	    		eNotSupported.printStackTrace(System.out);
	    		return;
	    	}
	    	int row = stmtInsert.executeUpdate();
	    	log.info("Documento PDF inserito: "+row);
	    	
	    	// step4. Lettura per alias
	    	String query = "select * from prova_bytes";
	    	stmtQuery = con.createStatement();
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		
	    		// per nome
	    		InputStream isRead = jdbcAdapter.getBinaryStream(rsQuery, "contenuto");
	    		byte[] read = Utilities.getAsByteArray(isRead);
	    		for (int i = 0; i < data.length; i++) {
					if(data[i]!=read[i]){
						File fTmpOriginale = File.createTempFile("orig", "tmp");
						File fTmpLettoDaDB = File.createTempFile("lettoDaDB", "tmp");
						FileSystemUtilities.writeFile(fTmpOriginale, data);
						FileSystemUtilities.writeFile(fTmpLettoDaDB, read);
						throw new Exception("Documento salvato su database e riletto risulta differente. Salvate le immagini nei seguenti file temporanei: originale["+
								fTmpOriginale.getAbsolutePath()+"] prelevatoDaDatabase["+
								fTmpLettoDaDB.getAbsolutePath()+"]");
					}
				}
	    		
	    	}else{
	    		throw new Exception("Nessuna insert effettuata");
	    	}
	    	log.info("Documento PDF inserito e riletto dal database tramite columnName sono equivalenti" );
	    	rsQuery.close();
	    		    	
	    	// step5. Lettura per index
	    	rsQuery = stmtQuery.executeQuery(query);
	    	if(rsQuery.next()){
	    		
	    		// per posizione
	    		InputStream isRead = jdbcAdapter.getBinaryStream(rsQuery, 2);
	    		byte[] read = Utilities.getAsByteArray(isRead);
	    		for (int i = 0; i < data.length; i++) {
					if(data[i]!=read[i]){
						File fTmpOriginale = File.createTempFile("orig", "tmp");
						File fTmpLettoDaDB = File.createTempFile("lettoDaDB", "tmp");
						FileSystemUtilities.writeFile(fTmpOriginale, data);
						FileSystemUtilities.writeFile(fTmpLettoDaDB, read);
						throw new Exception("Documento salvato su database e riletto risulta differente (read by index). Salvate le immagini nei seguenti file temporanei: originale["+
								fTmpOriginale.getAbsolutePath()+"] prelevatoDaDatabase["+
								fTmpLettoDaDB.getAbsolutePath()+"]");
					}
				}
	    		
	    	}else{
	    		throw new Exception("Nessuna insert effettuata");
	    	}
	    	log.info("Documento PDF inserito e riletto dal database tramite index sono equivalenti" );
	    	
	    	
	    }finally{
	    	try{
	    		pdfIs.close();
	    	}catch(Exception eClose){}
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
}
