package org.openspcoop2.pdd.core.transazioni;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;


public class TransactionContext {

	private static Boolean gestioneStateful = null;
	private static synchronized void initGestioneStateful() throws Exception{
		if(gestioneStateful==null){
			gestioneStateful = OpenSPCoop2Properties.getInstance().isTransazioniStatefulEnabled();
		}
	}
	
	
	private static Hashtable<String, Transaction> transactionContext = 
		new Hashtable<String, Transaction>();
	
	public static List<String> getTransactionKeys() {
		// Lo clono per non incorrere in errori di modifica durante il runtime
		List<String> keys = new ArrayList<>();
		keys.addAll(transactionContext.keySet());
		return keys;
	}
			
	public static void createTransaction(String id) throws TransactionNotExistsException{
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
