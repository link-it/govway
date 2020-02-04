/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

/**     
 * TransactionContext
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionContext {

	private static Boolean gestioneStateful = null;
	public static synchronized void initGestioneStateful() throws Exception{
		if(gestioneStateful==null){
			gestioneStateful = OpenSPCoop2Properties.getInstance().isTransazioniStatefulEnabled();
		}
	}
	
	
	private static Map<String, Transaction> transactionContext = null;
	public static synchronized void initResources() throws Exception{
		if(OpenSPCoop2Properties.getInstance().isConfigurazioneCache_transactionContext_accessiSynchronized()) {
			transactionContext = new Hashtable<>();
		}
		else {
			transactionContext = new ConcurrentHashMap<String, Transaction>();
		}
	}
	public static String getTransactionContextType() {
		return transactionContext.getClass().getName();
	}
	
	public static List<String> getTransactionKeys() {
		// Lo clono per non incorrere in errori di modifica durante il runtime
		List<String> keys = new ArrayList<>();
		keys.addAll(transactionContext.keySet());
		return keys;
	}
			
	public static void createTransaction(String id) throws TransactionNotExistsException{
		if(transactionContext.containsKey(id)==false) {
			try{
				if(gestioneStateful==null){
					initGestioneStateful();
				}
			}catch(Exception e){
				throw new TransactionNotExistsException("Indicazione sulla gestione stateful errata: "+e.getMessage(),e);
			}
			Transaction transaction = new Transaction(gestioneStateful);
			transactionContext.put(id, transaction);
		}
	}
	
	public static Transaction getTransaction(String id) throws TransactionNotExistsException{
		return getTransaction(id, false);
	}
	private static Transaction getTransaction(String id,boolean createIfNotExists) throws TransactionNotExistsException{
		//if(transactionContext==null){
		//	System.out.println("TX IS NULL??");
		//}
		//System.out.println("TX get ("+id+")");
		Transaction transaction = transactionContext.get(id);
		//System.out.println("TX get ("+id+") query fatta");
		if(transaction==null){
			if(createIfNotExists){
				createTransaction(id);
			}
			else{
				throw new TransactionNotExistsException("Transaction con id ["+id+"] non esiste"); 
			}
		}
		return transaction;
	}
	
	public static Transaction removeTransaction(String id){
		return transactionContext.remove(id);
	}
	
	
	private static Vector<String> idBustaFiltroDuplicati = new Vector<String>();
	
	public static List<String> getIdBustaKeys() {
		// Lo clono per non incorrere in errori di modifica durante il runtime
		List<String> keys = new ArrayList<>();
		keys.addAll(idBustaFiltroDuplicati);
		return keys;
	}
	
	public static synchronized void registraIdentificativoProtocollo(String idBusta) throws Exception{
		if(idBustaFiltroDuplicati.contains(idBusta)){
			throw new Exception("DUPLICATA");
		}
		idBustaFiltroDuplicati.add(idBusta);
	}
	public static boolean containsIdentificativoProtocollo(String idBusta){
		return idBustaFiltroDuplicati.contains(idBusta);
	}
	public static void removeIdentificativoProtocollo(String idBusta){
		//boolean a = 
		idBustaFiltroDuplicati.remove(idBusta);
		/*if(a){
			System.out.println("REMOVE ["+idBusta+"] size("+idBustaFiltroDuplicati.size()+")");
		}
		else{
			System.out.println("NOT REMOVE ["+idBusta+"] size("+idBustaFiltroDuplicati.size()+")");
		}*/
	}
}
