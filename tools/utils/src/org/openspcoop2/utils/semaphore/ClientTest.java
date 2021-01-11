/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.semaphore;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * ClientTest
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {
	
	private static boolean systemOut = true; // utile per maven test 
	
	static int CICLI_LOCK_PER_THREAD = 150;
	static int THREADS = 20;
	static boolean DEBUG = false;
	
	static Logger log = null;
	
	public static void main(String[] args) throws Exception {
		
		File logFile = File.createTempFile("runSemaphoreTest_", ".log");
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
		int timeWaitMs = 60000;
		boolean testIdle=true;

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
			// !!NOTA: SELECT FOR UPDATE is fixed in HSQLDB 2.3.3.
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
		if(args.length>4){
			String driverJdbcCustom = args[4].trim();
			if(!"${driverJdbc}".equals(driverJdbcCustom)){
				driver = driverJdbcCustom;
			}
		}
		if(args.length>5){
			String testIdleCustom = args[5].trim();
			if(!"${testIdle}".equals(testIdleCustom)){
				testIdle = "true".equalsIgnoreCase(testIdleCustom);
			}
		}
		if(args.length>6){
			String numThreads = args[6].trim();
			if(!"${threads}".equals(numThreads)){
				try{
					THREADS = Integer.parseInt(numThreads);
				}catch(Exception e){
					throw new Exception("Parameter 'threads' with wrong format (value:"+numThreads+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>7){
			String cicliLockPerThread = args[7].trim();
			if(!"${lockForThread}".equals(cicliLockPerThread)){
				try{
					CICLI_LOCK_PER_THREAD = Integer.parseInt(cicliLockPerThread);
				}catch(Exception e){
					throw new Exception("Parameter 'lockForThread' with wrong format (value:"+cicliLockPerThread+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>8){
			String debugParam = args[8].trim();
			if(!"${printDebug}".equals(debugParam)){
				try{
					DEBUG = Boolean.parseBoolean(debugParam);
				}catch(Exception e){
					throw new Exception("Parameter 'printDebug' with wrong format (value:"+debugParam+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>9){
			String timeWait = args[9].trim();
			if(!"${timeWaitMs}".equals(timeWait)){
				try{
					timeWaitMs = Integer.parseInt(timeWait);
				}catch(Exception e){
					throw new Exception("Parameter 'timeWaitMs' with wrong format (value:"+timeWait+"): "+e.getMessage(),e);
				}
			}
		}
		
		System.out.println("URL:"+url);
		System.out.println("UserName:"+userName);
		System.out.println("Password:"+password);
		System.out.println("DriverJDBC:"+driver);
		System.out.println("Threads:"+THREADS);
		System.out.println("Cicli lock per thread:"+CICLI_LOCK_PER_THREAD);
		System.out.println("Debug:"+DEBUG);
		System.out.println("TimeWaitMs:"+timeWaitMs);
		ClassLoaderUtilities.newInstance(driver);
		Connection con = null;
		List<Connection> conThreads = new ArrayList<Connection>();
		try{
			con = DriverManager.getConnection(url, userName, password);

			InfoStatistics infoStat = new InfoStatistics();
			infoStat.clear();
			clear(con);
			
			for (int i = 0; i < THREADS; i++) {
				conThreads.add(DriverManager.getConnection(url, userName, password));
			}
			

			List<String> listApplicativeId = new ArrayList<>();

			boolean SERIALIZABLE = true;
			boolean READ_COMMITTED = false;
			
			
			/** TEST N.1 Idle:Infinito MaxLife:Infinito */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 1. Idle:Infinito MaxLife:Infinito");
			String applicativeId = null;
			if(TipiDatabase.ORACLE.equals(tipoDatabase)) {
				init(con, tipoDatabase, applicativeId); // la init è necessaria in oracle anche con serializable. Altrimenti si ottiene più righe
			}
			test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, -1, SERIALIZABLE);
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			
		
			
			/** TEST N.2 Idle:Infinito MaxLife:Infinito con Applicative ID TestNumero2 */
			
			infoStat.clear();
						
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 2a. Idle:Infinito MaxLife:Infinito ApplicativeId:TestNumero2-NOSerializable");
			applicativeId = "TestNumero2-NOSerializable";
			init(con, tipoDatabase, applicativeId);
			test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, -1, READ_COMMITTED);
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 2b. Idle:Infinito MaxLife:Infinito ApplicativeId:TestNumero2-Serializable");
			applicativeId = "TestNumero2-Serializable";
			if(TipiDatabase.ORACLE.equals(tipoDatabase)) {
				init(con, tipoDatabase, applicativeId); // la init è necessaria in oracle anche con serializable. Altrimenti si ottiene più righe
			}
			test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, -1, SERIALIZABLE);
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			
			
			/** TEST N.3 Idle:Infinito MaxLife:Infinito con Applicative ID TestNumero3 */
			
			infoStat.clear();
						
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 3a. Idle:Infinito MaxLife:Infinito ApplicativeId:TestNumero3-NOSerializable");		
			applicativeId = "TestNumero3-NOSerializable";
			init(con, tipoDatabase, applicativeId);
			test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, -1, READ_COMMITTED);
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 3b. Idle:Infinito MaxLife:Infinito ApplicativeId:TestNumero3-Serializable");
			applicativeId = "TestNumero3-Serializable";
			if(TipiDatabase.ORACLE.equals(tipoDatabase)) {
				init(con, tipoDatabase, applicativeId); // la init è necessaria in oracle anche con serializable. Altrimenti si ottiene più righe
			}
			test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, -1, SERIALIZABLE);
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			
			/** TEST N.4 Idle:Infinito MaxLife:100ms con Applicative ID TestNumero4 */
			
			infoStat.clear();
						
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 4a. Idle:Infinito MaxLife:100ms ApplicativeId:TestNumero4-NoSerializable");
			applicativeId = "TestNumero4-NoSerializable";
			init(con, tipoDatabase, applicativeId);
			boolean foundError = false;
			try {
				test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, 100, -1, READ_COMMITTED);
			}catch(Throwable e) {
				foundError = true;
				info(log,systemOut,"Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max life, errore non rilevato");
			}
			String logContent = FileSystemUtilities.readFile(logFile);
			if(logContent.contains("Max Life Time (100ms) exceeded")==false) {
				throw new Exception("Atteso errore di max life, errore non rilevato nel log file");
			}
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test 4b. Idle:Infinito MaxLife:100ms ApplicativeId:TestNumero4-Serializable");
			applicativeId = "TestNumero4-Serializable";
			if(TipiDatabase.ORACLE.equals(tipoDatabase)) {
				init(con, tipoDatabase, applicativeId); // la init è necessaria in oracle anche con serializable. Altrimenti si ottiene più righe
			}
			foundError = false;
			try {
				test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, 105, -1, SERIALIZABLE);
			}catch(Throwable e) {
				foundError = true;
				info(log,systemOut,"Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max life, errore non rilevato");
			}
			logContent = FileSystemUtilities.readFile(logFile);
			if(logContent.contains("Max Life Time (105ms) exceeded")==false) {
				throw new Exception("Atteso errore di max life, errore non rilevato nel log file");
			}
			printInfos(infoStat);
			listApplicativeId.add(applicativeId);
			
			
			/** TEST N.5 Idle:133ms MaxLife:Infinito con Applicative ID TestNumero5 */
			
			if(testIdle) {
			
				infoStat.clear();
							
				info(log,systemOut,"\n\n==========================================");
				info(log,systemOut,"Test 5a. Idle:38ms MaxLife:Infinito ApplicativeId:TestNumero5-NOSerializable");			
				applicativeId = "TestNumero5-NOSerializable";
				init(con, tipoDatabase, applicativeId);
				foundError = false;
				try {
					test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, 38, READ_COMMITTED);
				}catch(Throwable e) {
					foundError = true;
					info(log,systemOut,"Errore Atteso: "+e.getMessage());
				}
				if(foundError==false){
					throw new Exception("Atteso errore di idle time, errore non rilevato");
				}
				logContent = FileSystemUtilities.readFile(logFile);
				if(logContent.contains("Idle Time (38ms) exceeded")==false) {
					throw new Exception("Atteso errore di idle time, errore non rilevato nel log file");
				}
				printInfos(infoStat);
				listApplicativeId.add(applicativeId);
				
				info(log,systemOut,"\n\n==========================================");
				info(log,systemOut,"Test 5b. Idle:42s MaxLife:Infinito ApplicativeId:TestNumero5-Serializable");			
				applicativeId = "TestNumero5-Serializable";
				if(TipiDatabase.ORACLE.equals(tipoDatabase)) {
					init(con, tipoDatabase, applicativeId); // la init è necessaria in oracle anche con serializable. Altrimenti si ottiene più righe
				}
				foundError = false;
				try {
					test(infoStat, SemaphoreMapping.newInstance(applicativeId), tipoDatabase, conThreads, log, DEBUG, timeWaitMs, -1, 42, SERIALIZABLE);
				}catch(Throwable e) {
					foundError = true;
					info(log,systemOut,"Errore Atteso: "+e.getMessage());
				}
				if(foundError==false){
					throw new Exception("Atteso errore di idle time, errore non rilevato");
				}
				logContent = FileSystemUtilities.readFile(logFile);
				if(logContent.contains("Idle Time (42ms) exceeded")==false) {
					throw new Exception("Atteso errore di idle time, errore non rilevato nel log file");
				}
				printInfos(infoStat);
				listApplicativeId.add(applicativeId);
				
			}
		
			
			
			
			// VERIFICA FINALE
			verificaFinale(con, tipoDatabase, listApplicativeId);
			
			
		}finally{
			try{
				con.close();
			}catch(Exception eClose){}
			for (int i = 0; i < THREADS; i++) {
				try{
					conThreads.get(i).close();
				}catch(Exception eClose){}
			}
		}


	}

	private static void info(Logger log, boolean systemOut, String msg) {
		log.info(msg);
		if(systemOut) {
			System.out.println(msg);
		}
	}
	
	private static void printInfos(InfoStatistics infoStat){
		info(log,systemOut,"Numero di errori 'access serializable': "+infoStat.getErrorSerializableAccess());
		for (int i=0; i<infoStat.getExceptionOccurs().size(); i++) {
			Throwable e = infoStat.getExceptionOccurs().get(i);
			info(log,systemOut,"Errore-"+(i+1)+" (occurs:"+infoStat.getNumber(e)+"): "+e.getMessage());
		}
	}

	private static void clear(Connection con) throws Exception{
		
		PreparedStatement pstmt = null;
		try{
		
			SemaphoreMapping mapping = SemaphoreMapping.newInstance(null);
			
			String delete = "delete from "+mapping.getTable();
			pstmt = con.prepareStatement(delete);
			pstmt.execute();
			pstmt.close();

		}finally {
			try{
				pstmt.close();
			}catch(Exception eClose){}
		}
	}
	
	private static void init(Connection con, TipiDatabase tipoDatabase,String applicativeId) throws Exception{
		
		PreparedStatement pstmt = null;
		try{
		
			SemaphoreMapping mapping = SemaphoreMapping.newInstance(null);
			
			ISQLQueryObject sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQuery.addInsertTable(mapping.getTable());
			if(applicativeId==null) {
				sqlQuery.addInsertField(mapping.getUniqueConditionColumnName(0), "null");
			}
			else {
				sqlQuery.addInsertField(mapping.getUniqueConditionColumnName(0), "?");
			}
			String insert = sqlQuery.createSQLInsert();
			pstmt = con.prepareStatement(insert);
			if(applicativeId!=null) {
				pstmt.setString(1, applicativeId);
			}
			pstmt.execute();
			pstmt.close();

		}finally {
			try{
				pstmt.close();
			}catch(Exception eClose){}
		}
	}
	
	private static void verificaFinale(Connection con, TipiDatabase tipoDatabase, List<String> listApplicativeId) throws Exception{
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try{
		
			SemaphoreMapping mapping = SemaphoreMapping.newInstance(null);
			
			ISQLQueryObject sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQuery.addFromTable(mapping.getTable());
			sqlQuery.setANDLogicOperator(true);
			sqlQuery.addWhereIsNullCondition(mapping.getIdNode());
			sqlQuery.addWhereIsNullCondition(mapping.getLockDate());
			sqlQuery.addWhereIsNullCondition(mapping.getUpdateDate());
			sqlQuery.addWhereIsNullCondition(mapping.getDetails());
			sqlQuery.addSelectCountField("verifica");
			
			pstmt = con.prepareStatement(sqlQuery.createSQLQuery());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int v = rs.getInt("verifica");
				if(v!=listApplicativeId.size()) {
					throw new Exception("Entries trovate nel database ("+v+") differenti da quelle attese ("+listApplicativeId.size()+")");
				}
			}else {
				throw new Exception("Entries non trovate nel database");
			}
			rs.close();
			pstmt.close();
			
			for (String applicativeId : listApplicativeId) {
				sqlQuery = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQuery.addFromTable(mapping.getTable());
				sqlQuery.setANDLogicOperator(true);
				sqlQuery.addWhereIsNullCondition(mapping.getIdNode());
				sqlQuery.addWhereIsNullCondition(mapping.getLockDate());
				sqlQuery.addWhereIsNullCondition(mapping.getUpdateDate());
				sqlQuery.addWhereIsNullCondition(mapping.getDetails());
				sqlQuery.addSelectCountField("verifica");
				if(applicativeId!=null) {
					sqlQuery.addWhereCondition(mapping.getUniqueConditionColumnName(0)+"=?");
				}
				else {
					sqlQuery.addWhereIsNullCondition(mapping.getUniqueConditionColumnName(0));
				}
				pstmt = con.prepareStatement(sqlQuery.createSQLQuery());
				StringBuilder bf = new StringBuilder(sqlQuery.createSQLQuery());
				if(applicativeId!=null) {
					pstmt.setString(1, applicativeId);
					String s = bf.toString();
					bf.delete(0, s.length());
					bf.append(s.replace("?", applicativeId));
				}
				rs = pstmt.executeQuery();
				if(rs.next()) {
					int v = rs.getInt("verifica");
					if(v>1) {
						throw new Exception("Entries trovate ("+v+") per l'id applicativo ("+applicativeId+") sono più di una\nQuery: "+bf.toString());
					}
					else if(v<1) {
						throw new Exception("Entries non trovate per l'id applicativo (\"+applicativeId+\")\nQuery: "+bf.toString());
					}
				}else {
					throw new Exception("Entries non trovate per l'id applicativo (\"+applicativeId+\")\nQuery: "+bf.toString());
				}
				rs.close();
				pstmt.close();
			}
		}finally {
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				pstmt.close();
			}catch(Exception eClose){}
		}
		
	}
	
	public static void test(InfoStatistics infoStat, SemaphoreMapping mapping, TipiDatabase tipoDatabase,
			List<Connection> conThreads, Logger log, boolean debug,
			long timeWaitMs, long maxLife, long idleTime, boolean serializableLevel) throws Exception{
		
		Date inizio = DateManager.getDate();
		
		EventGeneratorLog eventGenerator = new EventGeneratorLog(log);

		
		ExecutorService threadsPool = Executors.newFixedThreadPool(conThreads.size());
		Hashtable<String, ClientTestThread> threads = new Hashtable<String, ClientTestThread>();
		
		boolean error = false;
		try {
		
			for (int i = 0; i < conThreads.size(); i++) {
				
				SemaphoreConfiguration config = new SemaphoreConfiguration();
				config.setSerializableTimeWaitMs(timeWaitMs);
				config.setMaxLife(maxLife);
				config.setMaxIdleTime(idleTime);
				config.setEmitEvent(true);
				config.setEventGenerator(eventGenerator);
				config.setIdNode("Thread-"+i);
				config.setSerializableLevel(serializableLevel);
				
				ClientTestThread c = new ClientTestThread(infoStat, mapping, config, tipoDatabase, log, conThreads.get(i),i,debug);
				threadsPool.execute(c);
				if(debug)
					log.info("Lanciato thread "+i);
				threads.put("Thread-"+i, c);
				
			}
		
			boolean terminated = false;
			while(terminated == false){
				if(debug)
					log.info("Attendo terminazione ...");
				boolean tmpTerminated = true;
				for (int i = 0; i < conThreads.size(); i++) {
					
					ClientTestThread c = threads.get("Thread-"+i);
					if(c.isError()){
						error = true;
					}
					if(c.isFinished()==false){
						tmpTerminated = false;
						break;
					}
				}
				if(tmpTerminated==false){
					Utilities.sleep(250);
				}
				else{
					terminated = true;
				}
			}
			
		} finally{
			log.info("Shutdown pool ...");
			threadsPool.shutdown(); 
			log.info("Shutdown pool ok");
		}
		
		Date fine = DateManager.getDate();
		long diff = fine.getTime() - inizio.getTime();
		log.info("Tempo impiegato: "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
		
		int lockAcquisiti = 0;
		int lockAggiornati = 0;
		int lockRilasciati = 0;
		for (int i = 0; i < conThreads.size(); i++) {
			ClientTestThread c = threads.get("Thread-"+i);
			log.info("[Thread-"+i+"] acquisito:"+c.isLockAcquisito()+" aggiornamenti:"+c.getLockAggiornamenti()+" rilasciato:"+c.isLockRilasciato());
			lockAcquisiti = lockAcquisiti+(c.isLockAcquisito()?1:0);
			lockAggiornati = lockAggiornati + c.getLockAggiornamenti();
			lockRilasciati = lockRilasciati+(c.isLockRilasciato()?1:0);
		}
		
		if(error){
			throw new Exception("Error occurs in threads");
		}
		
		StringBuilder bf = new StringBuilder();
		if(lockAcquisiti!=THREADS) {
			String msg = "Riscontrata una differenza tra i lock acquisiti ("+lockAcquisiti+") ed i threads ("+THREADS+")";
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		if(lockAcquisiti!=lockRilasciati) {
			String msg = "Riscontrata una differenza tra i lock acquisiti ("+lockAcquisiti+") e quelli rilasciati ("+lockRilasciati+")";
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		if((lockAcquisiti*5)!=lockAggiornati) {
			String msg = "Riscontrata una differenza tra i lock acquisiti ("+lockAcquisiti+") e quelli aggiornati ("+lockAggiornati+") (dovrebbero essere 5 volte tanto)";
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		
		if(eventGenerator.getAcquisiti().size()!=lockAcquisiti) {
			String msg = "Riscontrata una differenza tra i lock acquisiti ("+lockAcquisiti+") e quelli notificati ("+eventGenerator.getAcquisiti().size()+")";
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		if(eventGenerator.getRilasciati().size()!=lockRilasciati) {
			String msg = "Riscontrata una differenza tra i lock rilasciati ("+lockRilasciati+") e quelli notificati ("+eventGenerator.getRilasciati().size()+")";
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		if(eventGenerator.getAggiornati().size()!=lockAggiornati) {
			String msg = "Riscontrata una differenza tra i lock aggiornati ("+lockAggiornati+") e quelli notificati ("+eventGenerator.getAggiornati().size()+")";
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		if(eventGenerator.getErrori().size()>0) {
			String msg = "Riscontrati errori durante la notifica: "+eventGenerator.getErrori().toString();
			log.error(msg);
			if(bf.length()>0) {
				bf.append("\n");
			}
			bf.append(msg);
		}
		
		if(bf.length()>0){
			throw new Exception(bf.toString());
		}
	}
	
}


class ClientTestThread implements Runnable{

	private Semaphore semaphore;
	private Connection con;
	
	private boolean lockAcquisito;
	private boolean lockRilasciato;
	private int lockAggiornamenti;
	public boolean isLockAcquisito() {
		return this.lockAcquisito;
	}
	public boolean isLockRilasciato() {
		return this.lockRilasciato;
	}
	public int getLockAggiornamenti() {
		return this.lockAggiornamenti;
	}

	private int index;
	@SuppressWarnings("unused")
	private boolean debug;

	private boolean finished = false;
	private boolean error = false;
	
	public boolean isError() {
		return this.error;
	}

	public boolean isFinished() {
		return this.finished;
	}

	public ClientTestThread(InfoStatistics infoStat, SemaphoreMapping mapping, SemaphoreConfiguration config, TipiDatabase tipoDatabase, Logger log, 
			Connection con, 
			int index, boolean debug) throws UtilsException{
		this.semaphore = new Semaphore(infoStat, mapping, config, tipoDatabase, log);
		this.con = con;
		this.lockAcquisito = false;
		this.lockRilasciato = false;
		this.lockAggiornamenti = 0;
		this.index = index;
		this.debug = debug;
	}
	
	@Override
	public void run() {
		
		try{
						
			boolean lock = false;
			for (int i=0; i < ClientTest.CICLI_LOCK_PER_THREAD; i++) {
				Utilities.sleep(50);
				lock = this.semaphore.newLock(this.con, "[Thread-"+this.index+"] new lock (tentativo:"+i+")..."); 
				if(lock) {
					break;
				}
			}
			if(lock==false) {
				//throw new UtilsException("Lock non acquisito");
				return;
			}
			this.lockAcquisito = true;
			
			for (int j = 0; j < 5; j++) {
				Utilities.sleep(50);
				lock = this.semaphore.updateLock(this.con, "[Thread-"+this.index+"] update lock iterazione-"+j+" ..."); 
				if(lock==false) {
					//throw new UtilsException("Lock non aggiornato (iterazione-"+j+")");
					return;
				}
				this.lockAggiornamenti++;
			}
			
			Utilities.sleep(50);
			
			lock = this.semaphore.releaseLock(this.con, "[Thread-"+this.index+"] new lock..."); 
			if(lock==false) {
				//throw new UtilsException("Lock non rilasciato");
				return;
			}
			this.lockRilasciato = true;
			
			
			
		}catch(Exception e){
			this.error = true;
			throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			this.finished = true;
		}
	}
	
}

class EventGeneratorLog implements ISemaphoreEventGenerator{

	private Logger log;
	
	private List<String> acquisiti = new ArrayList<>();
	
	private List<String> aggiornati = new ArrayList<>();
	
	private List<String> rilasciati = new ArrayList<>();
	
	private List<String> errori = new ArrayList<>();
	
	public List<String> getAcquisiti() {
		return this.acquisiti;
	}

	public List<String> getAggiornati() {
		return this.aggiornati;
	}
	
	public List<String> getRilasciati() {
		return this.rilasciati;
	}

	public List<String> getErrori() {
		return this.errori;
	}
	
	public EventGeneratorLog(Logger log) {
		this.log = log;
	}
	
	@Override
	public void emitEvent(Connection con, SemaphoreEvent event) {
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		this.log.info("["+event.getSeverity()+"] ["+event.getOperationType()+"] Date["+dateformat.format(event.getDate())+"] IdNode["+event.getIdNode()+"] [Lock:"+event.isLock()+"]: "+event.getDetails()+"\n");
		if(event.isLock()) {
			if(SemaphoreOperationType.NEW.equals(event.getOperationType())) {
				if(this.acquisiti.contains(event.getIdNode())) {
					this.errori.add("Riscontrato più di un lock per l'id node: "+event.getIdNode());
				}
				else {
					this.acquisiti.add(event.getIdNode());
				}
			}
			else if(SemaphoreOperationType.UPDATE.equals(event.getOperationType())) {
				this.aggiornati.add(event.getIdNode());
			}
			else if(SemaphoreOperationType.RELEASE.equals(event.getOperationType())) {
				if(this.acquisiti.contains(event.getIdNode())==false) {
					this.errori.add("Riscontrato un rilascio di lock per un nodo per cui non era stato ottenuto un lock: "+event.getIdNode());
				}
				else if(this.rilasciati.contains(event.getIdNode())) {
					this.errori.add("Riscontrato più di un rilascio di lock per l'id node: "+event.getIdNode());
				}
				else {
					this.rilasciati.add(event.getIdNode());
				}
			}
		}
	}
	
}