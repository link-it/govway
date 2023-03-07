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

package org.openspcoop2.utils.id.test;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierV1Generator;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierV4Generator;
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierV4GeneratorSecureRandom;
import org.slf4j.Logger;

import com.fasterxml.uuid.EthernetAddress;

/**
 * TestUUID
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UUIDTest {

	private static boolean systemOut = true; // utile per maven test 
	
	static int ID_GENERATI_PER_THREAD = 10000;
	static int THREADS = 200;
	static boolean DEBUG = false;
	
	static Logger log = null;
	
	public static void main(String[] args) throws Exception {
				
		if(args!=null && args.length>0){
			String numThreads = args[0].trim();
			if(!"${threads}".equals(numThreads)){
				try{
					THREADS = Integer.parseInt(numThreads);
				}catch(Exception e){
					throw new Exception("Parameter 'threads' with wrong format (value:"+numThreads+"): "+e.getMessage(),e);
				}
			}
		}
		if(args!=null && args.length>1){
			String numIdsForThread = args[1].trim();
			if(!"${idsForThread}".equals(numIdsForThread)){
				try{
					ID_GENERATI_PER_THREAD = Integer.parseInt(numIdsForThread);
				}catch(Exception e){
					throw new Exception("Parameter 'idsForThread' with wrong format (value:"+numIdsForThread+"): "+e.getMessage(),e);
				}
			}
		}
		if(args!=null && args.length>2){
			String debugParam = args[2].trim();
			if(!"${printDebug}".equals(debugParam)){
				try{
					DEBUG = Boolean.parseBoolean(debugParam);
				}catch(Exception e){
					throw new Exception("Parameter 'printDebug' with wrong format (value:"+debugParam+"): "+e.getMessage(),e);
				}
			}
		}
	
		test();
	}
	
	public static void test() throws Exception {
		
		File logFile = File.createTempFile("runTestUUID_", ".log");
		System.out.println("LogMessages write in "+logFile.getAbsolutePath());
		LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%m %n");
		log = LoggerWrapperFactory.getLogger(UUIDTest.class);
		
		DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), new Properties(), log);
		
		System.out.println("Threads:"+THREADS);
		System.out.println("Id generati per thread:"+ID_GENERATI_PER_THREAD);
		System.out.println("Debug:"+DEBUG);
		
		// Internet Address: utilizzo il primo mac address che trovo per i test
		EthernetAddress ethAddrFounded = null;
		Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (enumNetworkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = (NetworkInterface) enumNetworkInterfaces.nextElement();
			System.out.println("\n============");
			System.out.println("Network: "+networkInterface.getDisplayName());
			if(networkInterface.getHardwareAddress()!=null) {
				EthernetAddress ethAddr = new EthernetAddress(networkInterface.getHardwareAddress());
				if(ethAddrFounded==null) {
					ethAddrFounded = ethAddr;
				}
				System.out.println("ethAddr: "+ethAddr.toString());
			}
			Enumeration<InetAddress> enumInetAddresses = networkInterface.getInetAddresses();
			while (enumInetAddresses.hasMoreElements()) {
				InetAddress inetAddress = (InetAddress) enumInetAddresses.nextElement();
				System.out.println("getInetAddresses: "+inetAddress);		
			}
		}
		

	
		
		boolean USE_THREAD_LOCAL = true;
		
		boolean v4 = true;
		boolean v1 = true;
		
		
		// ****** V4 *********
		
		if(v4) {
		
			/** TEST  UUID.randomUUID() (NoThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv4 UUID.randomUUID() (NoThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, !USE_THREAD_LOCAL, UniversallyUniqueIdentifierGenerator.class.getName());
			test(THREADS, log, DEBUG);
					
			/** TEST  UUID.randomUUID() (ThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv4 UUID.randomUUID() (ThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, USE_THREAD_LOCAL, UniversallyUniqueIdentifierGenerator.class.getName());
			test(THREADS, log, DEBUG);
			
			
			
			/** TEST  com.fasterxml.uuid.impl.RandomBasedGenerator (NoThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv4 com.fasterxml.uuid.impl.RandomBasedGenerator (NoThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, !USE_THREAD_LOCAL, UniversallyUniqueIdentifierV4Generator.class.getName());
			test(THREADS, log, DEBUG);
					
			/** TEST  com.fasterxml.uuid.impl.RandomBasedGenerator (ThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv4 com.fasterxml.uuid.impl.RandomBasedGenerator (ThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, USE_THREAD_LOCAL, UniversallyUniqueIdentifierV4Generator.class.getName());
			test(THREADS, log, DEBUG);
			
			
			
			/** TEST  com.fasterxml.uuid.impl.RandomBasedGenerator con SecureRandom (NoThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv4 com.fasterxml.uuid.impl.RandomBasedGenerator con SecureRandom (NoThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, !USE_THREAD_LOCAL, UniversallyUniqueIdentifierV4GeneratorSecureRandom.class.getName());
			test(THREADS, log, DEBUG);
					
			/** TEST  com.fasterxml.uuid.impl.RandomBasedGenerator con SecureRandom (ThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv4 com.fasterxml.uuid.impl.RandomBasedGenerator con SecureRandom (ThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, USE_THREAD_LOCAL, UniversallyUniqueIdentifierV4GeneratorSecureRandom.class.getName());
			test(THREADS, log, DEBUG);
			
		}
		
		
		
		// ****** V1 *********
		
		if(v1) {
		
			/** TEST  com.fasterxml.uuid.impl.TimeBasedGenerator (NoThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv1 com.fasterxml.uuid.impl.TimeBasedGenerator (NoThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, !USE_THREAD_LOCAL, UniversallyUniqueIdentifierV1Generator.class.getName());
			test(THREADS, log, DEBUG);
					
			/** TEST  com.fasterxml.uuid.impl.TimeBasedGenerator (ThreadLocal) */
			
			info(log,systemOut,"\n\n==========================================");
			info(log,systemOut,"Test UUIDv1 com.fasterxml.uuid.impl.TimeBasedGenerator (ThreadLocal)");
			UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, USE_THREAD_LOCAL, UniversallyUniqueIdentifierV1Generator.class.getName());
			test(THREADS, log, DEBUG);
	
			
			if(ethAddrFounded!=null) {
			
				/** TEST  com.fasterxml.uuid.impl.TimeBasedGenerator (NoThreadLocal) */
				
				info(log,systemOut,"\n\n==========================================");
				info(log,systemOut,"Test UUIDv1 com.fasterxml.uuid.impl.TimeBasedGenerator (NoThreadLocal) con ethernet card '"+ethAddrFounded.toString()+"'");
				UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, !USE_THREAD_LOCAL, UniversallyUniqueIdentifierV1Generator.class.getName(), ethAddrFounded.toString());
				test(THREADS, log, DEBUG);
						
				/** TEST  com.fasterxml.uuid.impl.TimeBasedGenerator (ThreadLocal) */
				
				info(log,systemOut,"\n\n==========================================");
				info(log,systemOut,"Test UUIDv1 com.fasterxml.uuid.impl.TimeBasedGenerator (ThreadLocal) con ethernet card '"+ethAddrFounded.toString()+"'");
				UniqueIdentifierManager.inizializzaUniqueIdentifierManager(true, USE_THREAD_LOCAL, UniversallyUniqueIdentifierV1Generator.class.getName(), ethAddrFounded.toString());
				test(THREADS, log, DEBUG);
				
			}
			
		}
		
	}

	
	private static void info(Logger log, boolean systemOut, String msg) {
		log.info(msg);
		if(systemOut) {
			System.out.println(msg);
		}
	}
	
	public static void test(int threadsNum, Logger log, boolean debug) throws Exception{
		
		Date inizio = DateManager.getDate();
		
		ExecutorService threadsPool = Executors.newFixedThreadPool(threadsNum);
		Map<String, ClientTestThread> threads = new HashMap<String, ClientTestThread>();
		
		boolean error = false;
		Exception exception = null;
		try {
		
			for (int i = 0; i < threadsNum; i++) {
				
				ClientTestThread c = new ClientTestThread(i,debug);
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
				for (int i = 0; i < threadsNum; i++) {
					
					ClientTestThread c = threads.get("Thread-"+i);
					if(c.isError()){
						error = true;
						exception = c.getException();
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
		info(log, true, "Tempo impiegato: "+Utilities.convertSystemTimeIntoString_millisecondi(diff, true));
		
		boolean isDuplicati = false;
		HashSet<String> identificativi = new HashSet<String>();
		for (int i = 0; i < threadsNum; i++) {
			ClientTestThread c = threads.get("Thread-"+i);
			if(c.getValoriGenerati().size()>0) {
				log.info("[Thread-"+i+"] ha generato "+c.getValoriGenerati().size()+" id (duplicati: "+c.isValoriDuplicati()+"). Ultimo: "+c.getValoriGenerati().get(c.getValoriGenerati().size()-1));
				if(c.isValoriDuplicati()) {
					isDuplicati = true;
				}
				else {
					//System.out.println("i["+i+"] prima: "+identificativi.size());
					identificativi.addAll(c.getValoriGenerati());
					//System.out.println("i["+i+"] dopo: "+identificativi.size());
				}
			}
			else
				log.info("[Thread-"+i+"] non ha generato id.");
			if(debug){
				log.info("[Thread-"+i+"] ids generati: "+c.getValoriGenerati().toString());
			}
		}
		int attesi = (ID_GENERATI_PER_THREAD*THREADS);
		if(identificativi.size()!=attesi) {
			isDuplicati = true;
			log.info("Identificativi generati '"+identificativi.size()+"', attesi '"+attesi+"'");
		}
		info(log, true, "Duplicati rilevati: "+isDuplicati);
		info(log, true, "Identificativi distinti complessivi generati: "+identificativi.size());
		if(identificativi.size()>0) {
			Iterator<String> it = identificativi.iterator();
			int index = 0;
			while(it.hasNext() && index<=3) {
				info(log, true, "Esempio di un uuid generato: "+it.next());
				index++;
			}
		}
		//log.info("Identificativi: "+identificativi);
		
		if(error){
			throw new Exception("Error occurs in threads: "+exception.getMessage(),exception);
		}
		else if(isDuplicati){
			throw new Exception("Duplicate ids occurs in threads: "+exception.getMessage(),exception);
		}
	}
}

class ClientTestThread implements Runnable{

	private List<String> check = new ArrayList<String>();
	private boolean valoriDuplicati = false;
	
	private void put(String v,boolean rilevaValoriDuplicati) throws Exception{
		if(!this.check.contains(v)){
			this.check.add(v);
		}else{
			if(rilevaValoriDuplicati){
				throw new Exception("Valore ["+v+"] gia generato");
			}
			else{
				this.valoriDuplicati = true;
			}
		}
	}
	
	public List<String> getValoriDistintiGenerati() {
		return this.check;
	}

	public void reset(){
		this.check.clear();
		this.valoriDuplicati = false;
	}
	
	public int count(){
		return this.check.size();
	}
	
	public boolean isValoriDuplicati() {
		return this.valoriDuplicati;
	}
	
	private List<String> valoriGenerati;
	private int index;
	private boolean debug;
	public List<String> getValoriGenerati() {
		return this.valoriGenerati;
	}

	private boolean finished = false;
	private boolean error = false;
	
	private Exception exception = null;
	public Exception getException() {
		return this.exception;
	}

	public boolean isError() {
		return this.error;
	}

	public boolean isFinished() {
		return this.finished;
	}

	public ClientTestThread(int index, boolean debug){
		this.valoriGenerati = new ArrayList<String>();
		this.index = index;
		this.debug = debug;
	}
	
	@Override
	public void run() {
		
		try{
		
			String v = null;
			int i = 0;
			boolean rilevaValoriDuplicati = true;
	
			for (; i < UUIDTest.ID_GENERATI_PER_THREAD; i++) {
				if(this.debug){
					if(i%10==0){
						UUIDTest.log.info("[Thread-"+this.index+"] Generati "+i+" ids");
					}
				}
				v = UniqueIdentifierManager.newUniqueIdentifier().getAsString();
				this.valoriGenerati.add(v);
				put(v, rilevaValoriDuplicati);
			}
			
		}catch(Exception e){
			this.error = true;
			this.exception = e;
			// Se si lancia l'eccezione, nell'output viene loggato e si sporcano i test. Comunque lo stato errore viene rilevato.
			//throw new RuntimeException(e.getMessage(),e);
		}
		finally{
			this.finished = true;
		}
	}
	
}

