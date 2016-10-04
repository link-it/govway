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

package org.openspcoop2.utils.id.serial;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * ClientTest
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {

	static int ID_GENERATI_PER_THREAD = 100;
	static int THREADS = 10;
	static boolean DEBUG = false;
	
	static Logger log = null;
	
	public static void main(String[] args) throws Exception {
		
		File logFile = File.createTempFile("runIdSerialTest_", ".log");
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
		int sizeBuffer = 1;

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
			String numThreads = args[4].trim();
			if(!"${threads}".equals(numThreads)){
				try{
					THREADS = Integer.parseInt(numThreads);
				}catch(Exception e){
					throw new Exception("Parameter 'threads' with wrong format (value:"+numThreads+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>5){
			String numIdsForThread = args[5].trim();
			if(!"${idsForThread}".equals(numIdsForThread)){
				try{
					ID_GENERATI_PER_THREAD = Integer.parseInt(numIdsForThread);
				}catch(Exception e){
					throw new Exception("Parameter 'idsForThread' with wrong format (value:"+numIdsForThread+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>6){
			String debugParam = args[6].trim();
			if(!"${printDebug}".equals(debugParam)){
				try{
					DEBUG = Boolean.parseBoolean(debugParam);
				}catch(Exception e){
					throw new Exception("Parameter 'printDebug' with wrong format (value:"+debugParam+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>7){
			String timeWait = args[7].trim();
			if(!"${timeWaitMs}".equals(timeWait)){
				try{
					timeWaitMs = Integer.parseInt(timeWait);
				}catch(Exception e){
					throw new Exception("Parameter 'timeWaitMs' with wrong format (value:"+timeWait+"): "+e.getMessage(),e);
				}
			}
		}
		if(args.length>8){
			String sizeBufferParam = args[8].trim();
			if(!"${sizeBuffer}".equals(sizeBufferParam)){
				try{
					sizeBuffer = Integer.parseInt(sizeBufferParam);
				}catch(Exception e){
					throw new Exception("Parameter 'sizeBuffer' with wrong format (value:"+sizeBufferParam+"): "+e.getMessage(),e);
				}
			}
		}
		
		Class.forName(driver).newInstance();
		Connection con = null;
		List<Connection> conThreads = new ArrayList<Connection>();
		Statement stmtDelete = null;
		PreparedStatement stmtInsert = null;
		try{
			con = DriverManager.getConnection(url, userName, password);

			InfoStatistics infoStat = new InfoStatistics();
			clear(infoStat, con, stmtDelete);
			
			for (int i = 0; i < THREADS; i++) {
				conThreads.add(DriverManager.getConnection(url, userName, password));
			}
						
			IDSerialGenerator serialGenerator = new IDSerialGenerator(infoStat);
			
			IDSerialGeneratorParameter serialGeneratorParameter = new IDSerialGeneratorParameter("ApplicationContext");
			
			// Tempo di attesa jdbc
			serialGeneratorParameter.setSerializableTimeWaitMs(timeWaitMs);
			//serialGeneratorParameter.setSerializableNextIntervalTimeMs(100);
			serialGeneratorParameter.setSizeBuffer(sizeBuffer);
				
			
			/** TEST N.1 PROGRESSIVO */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(false);			
			log.info("\n\n==========================================");
			log.info("Test 1. Progressivo numerico");
			test(serialGenerator, serialGeneratorParameter, conThreads, log, true, DEBUG, tipoDatabase);
			printInfos(infoStat);
			
			
			/** TEST N.2 PROGRESSIVO con MAX */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setMaxValue(10l);
			log.info("\n\n==========================================");
			log.info("Test 2. Progressivo numerico con max 10");
			boolean foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, conThreads, log, true, DEBUG, tipoDatabase);
			}catch(Exception e){
				foundError = true;
				log.info("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			printInfos(infoStat);
			
			
			/** TEST N.3 PROGRESSIVO con MAX e Wrap */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setMaxValue(10l);
			log.info("\n\n==========================================");
			log.info("Test 3. Progressivo numerico con max 10 e wrap");
			test(serialGenerator, serialGeneratorParameter, conThreads, log, true, DEBUG, tipoDatabase);
			printInfos(infoStat);
			
			
			
			/** TEST N.4 PROGRESSIVO (InfoAssociata) */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setMaxValue(Long.MAX_VALUE);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("Associata");
			log.info("\n\n==========================================");
			log.info("Test 4. Progressivo numerico (InfoAssociata)");
			test(serialGenerator, serialGeneratorParameter, conThreads, log, true, DEBUG, tipoDatabase);
			printInfos(infoStat);
			
			
			/** TEST N.5 PROGRESSIVO con MAX (InfoAssociata) */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);

			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setMaxValue(10l);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("Associata");
			log.info("\n\n==========================================");
			log.info("Test 5. Progressivo numerico con max 10 (InfoAssociata)");
			foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, conThreads, log, true, DEBUG, tipoDatabase);
			}catch(Exception e){
				foundError = true;
				log.info("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			printInfos(infoStat);
			
			
			/** TEST N.6 PROGRESSIVO con MAX e Wrap (InfoAssociata)  */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setMaxValue(10l);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("Associata");
			log.info("\n\n==========================================");
			log.info("Test 6. Progressivo numerico con max 10 e wrap");
			test(serialGenerator, serialGeneratorParameter, conThreads, log, true, DEBUG, tipoDatabase);
			printInfos(infoStat);
			
			
			
			
			/** TEST N.7 PROGRESSIVO con MAX */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
	
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setSize(1);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo(null); // annullo il precedente assegnamento
			log.info("\n\n==========================================");
			log.info("Test 7. Progressivo alfanumerico con size 1");
			foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, conThreads, log, false, DEBUG, tipoDatabase);
			}catch(Exception e){
				foundError = true;
				log.info("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			printInfos(infoStat);
			
			
			
			/** TEST N.8 PROGRESSIVO con MAX e Wrap */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setSize(1);
			log.info("\n\n==========================================");
			log.info("Test 8. Progressivo numerico con size 1 e wrap");
			test(serialGenerator, serialGeneratorParameter, conThreads, log, false, DEBUG, tipoDatabase);
			printInfos(infoStat);
			
			
			/** TEST N.9 PROGRESSIVO con MAX (InfoAssociata) */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
		
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setSize(1);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("InfoAssociata");
			log.info("\n\n==========================================");
			log.info("Test 9. Progressivo alfanumerico con size 1 (InfoAssociata)");
			foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, conThreads, log, false, DEBUG, tipoDatabase);
			}catch(Exception e){
				foundError = true;
				log.info("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			printInfos(infoStat);
			
			
			
			/** TEST N.10 PROGRESSIVO con MAX e Wrap (InfoAssociata) */
			
			ClientTestThread.reset();
			clear(infoStat, con, stmtDelete);
		
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGenerator.clearBuffer(serialGeneratorParameter);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setSize(1);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("InfoAssociata");
			log.info("\n\n==========================================");
			log.info("Test 10. Progressivo numerico con size 1 e wrap (InfoAssociata)");
			test(serialGenerator, serialGeneratorParameter, conThreads, log, false, DEBUG, tipoDatabase);
			printInfos(infoStat);
			
			
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
			for (int i = 0; i < THREADS; i++) {
				try{
					conThreads.get(i).close();
				}catch(Exception eClose){}
			}
		}


	}

	private static void printInfos(InfoStatistics infoStat){
		log.info("Numero di errori 'access serializable': "+infoStat.getErrorSerializableAccess());
		for (int i=0; i<infoStat.getExceptionOccurs().size(); i++) {
			Throwable e = infoStat.getExceptionOccurs().get(i);
			log.info("Errore-"+(i+1)+" (occurs:"+infoStat.getNumber(e)+"): "+e.getMessage());
		}
	}
	
	private static void clear(InfoStatistics infoStat, Connection con, Statement stmtDelete) throws Exception{
		
		infoStat.clear();
		
		String delete = "delete from "+Constants.TABELLA_ID_RELATIVO_AS_LONG;
		stmtDelete = con.createStatement();
		stmtDelete.execute(delete);
		stmtDelete.close();
		
		delete = "delete from "+Constants.TABELLA_ID_AS_LONG;
		stmtDelete = con.createStatement();
		stmtDelete.execute(delete);
		stmtDelete.close();
		
		delete = "delete from "+Constants.TABELLA_ID_AS_STRING;
		stmtDelete = con.createStatement();
		stmtDelete.execute(delete);
		stmtDelete.close();
		
		delete = "delete from "+Constants.TABELLA_ID_RELATIVO_AS_STRING;
		stmtDelete = con.createStatement();
		stmtDelete.execute(delete);
		stmtDelete.close();
	}
	
	public static void test(IDSerialGenerator serialGenerator, IDSerialGeneratorParameter param,
			List<Connection> conThreads, Logger log, boolean isNumber, boolean debug, TipiDatabase tipoDatabase) throws Exception{
		
		ExecutorService threadsPool = Executors.newFixedThreadPool(conThreads.size());
		Hashtable<String, ClientTestThread> threads = new Hashtable<String, ClientTestThread>();
		
		boolean error = false;
		try {
		
			for (int i = 0; i < conThreads.size(); i++) {
				
				ClientTestThread c = new ClientTestThread(serialGenerator, param, conThreads.get(i), isNumber,i,debug, tipoDatabase);
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
					try{
						Thread.sleep(250);
					}catch(Exception e){}
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
		
		
		for (int i = 0; i < conThreads.size(); i++) {
			ClientTestThread c = threads.get("Thread-"+i);
			if(c.getValoriGenerati().size()>0)
				log.info("[Thread-"+i+"] ha generato "+c.getValoriGenerati().size()+" id. Ultimo: "+c.getValoriGenerati().get(c.getValoriGenerati().size()-1));
			else
				log.info("[Thread-"+i+"] non ha generato id.");
			if(debug){
				log.info("[Thread-"+i+"] ids generati: "+c.getValoriGenerati().toString());
			}
		}
		log.info("Duplicati rilevati: "+ClientTestThread.isValoriDuplicati());
		log.info("Identificativi distinti complessivi generati: "+ClientTestThread.count());
		Collections.sort(ClientTestThread.getValoriDistintiGenerati());
		log.info("Identificativi: "+ClientTestThread.getValoriDistintiGenerati());
		
		if(error){
			throw new Exception("Error occurs in threads");
		}
	}
	
}


class ClientTestThread implements Runnable{

	private static List<String> check = new ArrayList<String>();
	private static boolean valoriDuplicati = false;
	
	private static synchronized void put(String v,boolean rilevaValoriDuplicati) throws Exception{
		if(!check.contains(v)){
			check.add(v);
		}else{
			if(rilevaValoriDuplicati){
				throw new Exception("Valore ["+v+"] gia generato");
			}
			else{
				valoriDuplicati = true;
			}
		}
	}
	
	public static List<String> getValoriDistintiGenerati() {
		return check;
	}

	public static void reset(){
		check.clear();
		valoriDuplicati = false;
	}
	
	public static int count(){
		return check.size();
	}
	
	public static boolean isValoriDuplicati() {
		return valoriDuplicati;
	}
	
	private IDSerialGenerator serialGenerator;
	private IDSerialGeneratorParameter param;
	private Connection con;
	private boolean isNumber;
	private List<String> valoriGenerati;
	private int index;
	private boolean debug;
	private TipiDatabase tipoDatabase;
	public List<String> getValoriGenerati() {
		return this.valoriGenerati;
	}

	private boolean finished = false;
	private boolean error = false;
	
	public boolean isError() {
		return this.error;
	}

	public boolean isFinished() {
		return this.finished;
	}

	public ClientTestThread(IDSerialGenerator serialGenerator, IDSerialGeneratorParameter param, Connection con, 
			boolean isNumber, int index, boolean debug, TipiDatabase tipoDatabase){
		this.serialGenerator = serialGenerator;
		this.param = param;
		this.con = con;
		this.isNumber = isNumber;
		this.valoriGenerati = new ArrayList<String>();
		this.index = index;
		this.debug = debug;
		this.tipoDatabase = tipoDatabase;
	}
	
	@Override
	public void run() {
		
		try{
		
			String v = null;
			int i = 0;
			boolean rilevaValoriDuplicati = false;
			if(this.param.isWrap()==false){
				rilevaValoriDuplicati = true;
			}
	
			for (; i < ClientTest.ID_GENERATI_PER_THREAD; i++) {
				if(this.debug){
					if(i%10==0){
						ClientTest.log.info("[Thread-"+this.index+"] Generati "+i+" ids");
					}
				}
				v = null;
				if(this.isNumber){
					long l = this.serialGenerator.buildIDAsNumber(this.param, this.con, this.tipoDatabase, ClientTest.log);
					v = l + "";
				}
				else{
					v = this.serialGenerator.buildID(this.param, this.con, this.tipoDatabase, ClientTest.log);
					if(!RegularExpressionEngine.isMatch((v+""),"^[a-zA-Z0-9]*$")){
						throw new UtilsException("Deve essere fornito [a-zA-Z0-9] trovato ["+v+"]");
					}
				}
				this.valoriGenerati.add(v);
				put(v, rilevaValoriDuplicati);
			}
			
		}catch(Exception e){
			this.error = true;
			throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			this.finished = true;
		}
	}
	
}
