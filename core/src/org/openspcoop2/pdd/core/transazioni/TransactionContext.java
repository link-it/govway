/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.transazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;

/**     
 * TransactionContext
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionContext {

	private TransactionContext() {}
	
	private static final String INDICAZIONE_GESTIONE_STATEFUL_ERRATA = "Indicazione sulla gestione stateful errata: ";
	
	private static Boolean gestioneStateful = null;
	public static synchronized void initGestioneStateful() {
		if(gestioneStateful==null){
			gestioneStateful = OpenSPCoop2Properties.getInstance().isTransazioniStatefulEnabled();
		}
	}
	
	private static boolean useThreadLocal = true;
	public static boolean isUseThreadLocal() {
		return useThreadLocal;
	}

	private static Map<String, TransactionInfo> setTransactionInfoThreadLocal = new ConcurrentHashMap<>(); // 1 sola insert per ogni thread, poi acceduta tramite getTransactionKeys
	private static final ThreadLocal<TransactionInfo> transactionContextThreadLocal = ThreadLocal.withInitial(() -> {
	    String tName = Thread.currentThread().getName();
	    TransactionInfo info = new TransactionInfo(tName);
	    /** System.out.println("ADD ["+tName+"]");*/ /* Ogni ThreadLocalContext comporterà la creazione di una entry */
	    setTransactionInfoThreadLocal.put(tName, info);
	    return info;
	});
       
	public static void removeTransactionContextThreadLocal() {
		if(transactionContextThreadLocal!=null) {
			transactionContextThreadLocal.remove();
		}
	}
	
	private static Map<String, Transaction> transactionContextShared = null;
	public static synchronized void initResources() {
		if(!useThreadLocal) {
			if(OpenSPCoop2Properties.getInstance().isConfigurazioneCache_transactionContext_accessiSynchronized()) {
				transactionContextShared = new java.util.Hashtable<>();
			}
			else {
				transactionContextShared = new ConcurrentHashMap<>();
			}
		}
	}
	public static String getTransactionContextType() {
		if(useThreadLocal) {
			return "ThreadLocal";
		}
		else {
			return transactionContextShared.getClass().getName();
		}
	}
	/**public static String getTransactionContextType() {
		return transactionContext.getClass().getName();
	}*/
	
	public static List<String> getTransactionKeys() {
		// Lo clono per non incorrere in errori di modifica durante il runtime
		List<String> keys = new ArrayList<>();
		if(useThreadLocal) {
			fillTransactionKeysThreadLocal(keys);
		}
		else {
			keys.addAll(transactionContextShared.keySet());
		}
		return keys;
	}
	private static void fillTransactionKeysThreadLocal(List<String> keys) {
		List<String> thNames = new ArrayList<>();
		thNames.addAll(setTransactionInfoThreadLocal.keySet());
		
		/**System.out.println("FILL ["+thNames.size()+"] ["+thNames+"]");*/
		
		List<String> threadsClientNio = new ArrayList<>();
		
		for (String tName : thNames) {
			fillTransactionKeysThreadLocal(tName, threadsClientNio, keys);
		}
		
		if(useThreadLocal) {
			fillTransactionKeysThreadLocalCleanThreadNio(threadsClientNio,thNames, keys);
		}
	}
	private static boolean isClientNio(String tName) {
		return tName.startsWith("httpclient-dispatch");
	}
	private static void fillTransactionKeysThreadLocal(String tName, List<String> threadsClientNio, List<String> keys) {
		TransactionInfo tInfo = setTransactionInfoThreadLocal.get(tName);
		if(tInfo!=null && tInfo.transaction!=null) {
			String id = null;
			try {
				id = tInfo.transaction.getId();
			}catch(Throwable t) {
				// potrebbe diventare null
			}
			/**System.out.println("tName id["+id+"]");*/
			if(id!=null) {
				if(useThreadLocal) {
					String threadNameId = formatThreadId(tName, id);
					if(isClientNio(tName) && !threadsClientNio.contains(threadNameId)) {
						threadsClientNio.add(threadNameId);
					}
					keys.add(threadNameId);
				}
				else {
					keys.add(id);
				}
			}
		}
	}
	private static String formatThreadId(String tName, String id) {
		return "Thread:"+tName+" id:"+id;
	}
	private static String extractIdFromFormatThreadId(String id) { 
		String f = " id:";
		if(id.contains(f)) {
			String [] split = id.split(f);
			if(split.length==2) {
				return split[1];
			}
		}
		return id;
	}
	private static void fillTransactionKeysThreadLocalCleanThreadNio(List<String> threadsClientNio, List<String> thNames, List<String> keys) {
		if(!threadsClientNio.isEmpty()) {
			for (String threadNameId : threadsClientNio) {
				String id = extractIdFromFormatThreadId(threadNameId);
				boolean find = existsAnotherThreadWithSameId(thNames, id);
				if(!find) {
					// rimane perchè la gestione (ConnettoreHTTPCORETransactionThreadContextSwitchEntityProducer<T extends AsyncEntityProducer> implements AsyncEntityProducer) funziona male
					// in caso di errori di timeout non viene chiamato ne il metodo 'releaseResources' ne il metodo 'failed'
					keys.remove(threadNameId);
					/**System.out.println("Remove ["+threadNameId+"]");*/
				}
			}
		}
	}
	private static boolean existsAnotherThreadWithSameId(List<String> thNames, String id) {
		boolean find = false;
		for (String tName : thNames) {
			if(!isClientNio(tName)) {
				TransactionInfo tInfo = setTransactionInfoThreadLocal.get(tName);
				String idAltroThread = null;
				if(tInfo!=null && tInfo.transaction!=null) {
					try {
						idAltroThread = tInfo.transaction.getId();
					}catch(Throwable t) {
						// potrebbe diventare null
					}
				}
				if(idAltroThread!=null && idAltroThread.equals(id)) {
					find = true;
					break;
				}
			}
		}
		return find;
	}
			
	
	public static void createTransaction(String id, String originator) throws TransactionNotExistsException{
		if(useThreadLocal) {
			createTransactionThreadLocal(id, originator);	
		}
		else {
			if(!transactionContextShared.containsKey(id)) {
				try{
					if(gestioneStateful==null){
						initGestioneStateful();
					}
				}catch(Exception e){
					throw new TransactionNotExistsException(INDICAZIONE_GESTIONE_STATEFUL_ERRATA+e.getMessage(),e);
				}
				Transaction transaction = new Transaction(id, originator, gestioneStateful);
				transactionContextShared.put(id, transaction);
			}
		}
	}
	private static void createTransactionThreadLocal(String id, String originator) throws TransactionNotExistsException {
		if(transactionContextThreadLocal.get().transaction==null || !id.equals(transactionContextThreadLocal.get().transaction.getId()) ) {
			try{
				if(gestioneStateful==null){
					initGestioneStateful();
				}
			}catch(Exception e){
				throw new TransactionNotExistsException(INDICAZIONE_GESTIONE_STATEFUL_ERRATA+e.getMessage(),e);
			}
			/**String tName = Thread.currentThread().getName();
			System.out.println("TX create ("+id+") per thread ["+tName+"]");**/
			transactionContextThreadLocal.get().transaction = new Transaction(id, originator, gestioneStateful);
		}
	}
	// usato per trasferire l'oggetto in un altro thread
	public static void setTransactionThreadLocal(String id, Transaction transaction) throws TransactionNotExistsException {
		if(useThreadLocal &&
				(transactionContextThreadLocal.get().transaction==null || !id.equals(transactionContextThreadLocal.get().transaction.getId()) ) 
			){
			try{
				if(gestioneStateful==null){
					initGestioneStateful();
				}
			}catch(Exception e){
				throw new TransactionNotExistsException(INDICAZIONE_GESTIONE_STATEFUL_ERRATA+e.getMessage(),e);
			}
			/**String tName = Thread.currentThread().getName();
			System.out.println("TX set ("+id+") per thread ["+tName+"]");*/
			transactionContextThreadLocal.get().transaction = transaction;
		}
	}
		
	public static Transaction getTransaction(String id) throws TransactionNotExistsException{
		return getTransaction(id, null, false);
	}
	private static Transaction getTransaction(String id,String originator, boolean createIfNotExists) throws TransactionNotExistsException{
		if(useThreadLocal) {
			if(transactionContextThreadLocal.get().transaction==null || !id.equals(transactionContextThreadLocal.get().transaction.getId()) ) {
				if(createIfNotExists){
					createTransaction(id, originator);
				}
				else{
					throw new TransactionNotExistsException("Transaction con id ["+id+"] non esiste"); 
				}
			}
			return transactionContextThreadLocal.get().transaction;
		}
		else {
			/**if(transactionContext==null){
			//	System.out.println("TX IS NULL??");
			//}
			//System.out.println("TX get ("+id+")");*/
			Transaction transaction = transactionContextShared.get(id);
			/**System.out.println("TX get ("+id+") query fatta");*/
			if(transaction==null){
				if(createIfNotExists){
					createTransaction(id, originator);
				}
				else{
					throw new TransactionNotExistsException("Transaction con id ["+id+"] non esiste"); 
				}
			}
			return transaction;
		}
	}
	
	public static Transaction removeTransaction(String id){
		if(useThreadLocal) {
			/**String tName = Thread.currentThread().getName();
			System.out.println("TX remove ("+id+") per thread ["+tName+"]");*/
			Transaction t = transactionContextThreadLocal.get().transaction;
			transactionContextThreadLocal.get().transaction = null;
			return t;
		}
		else {
			return transactionContextShared.remove(id);
		}
	}
	
	
	private static Set<String> idBustaFiltroDuplicati = ConcurrentHashMap.newKeySet();
	
	public static List<String> getIdBustaKeys() {
		// Lo clono per non incorrere in errori di modifica durante il runtime
		List<String> keys = new ArrayList<>();
		keys.addAll(idBustaFiltroDuplicati);
		return keys;
	}
	
	private static org.openspcoop2.utils.Semaphore semaphoreIdentificativoProtocollo = new org.openspcoop2.utils.Semaphore("TransactionContext.idProtocollo");
	public static void registraIdentificativoProtocollo(String idBusta, String idTransazione) throws UtilsException{
		SemaphoreLock lock = semaphoreIdentificativoProtocollo.acquire("registraIdentificativoProtocollo_"+idBusta, idTransazione);
		try {
			if(idBustaFiltroDuplicati.contains(idBusta)){
				throw new UtilsException("DUPLICATA");
			}
			idBustaFiltroDuplicati.add(idBusta);
		}finally{
			semaphoreIdentificativoProtocollo.release(lock, "registraIdentificativoProtocollo_"+idBusta, idTransazione);
		}
	}
	public static boolean containsIdentificativoProtocollo(String idBusta){
		return idBustaFiltroDuplicati.contains(idBusta);
	}
	public static void removeIdentificativoProtocollo(String idBusta){
		idBustaFiltroDuplicati.remove(idBusta);
	}
}

class TransactionInfo {
	
	Transaction transaction = null;
	
	public TransactionInfo(String threadName) {
		String msg = "ThreadLocal transaction context created for thread '"+threadName+"'";
		OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().debug(msg);
	}
	
}
