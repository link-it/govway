package org.openspcoop2.pdd.core.transazioni;

import java.util.Hashtable;
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
	
//	private static Hashtable<String, String> mappingIDBustaIdTransazione = 
//		new Hashtable<String, String>();
//	
//	public static void saveMapping(String idBusta,boolean delegata,String idTransazione){
//		String key = idBusta;
//		if(delegata){
//			key = key+"_DELEGATA";
//		}else{
//			key = key+"_APPLICATIVA";
//		}
//		mappingIDBustaIdTransazione.put(key, idTransazione);
//	}
//	public static String getIdTransazione(String idBusta,boolean delegata){
//		String key = idBusta;
//		if(delegata){
//			key = key+"_DELEGATA";
//		}else{
//			key = key+"_APPLICATIVA";
//		}
//		return mappingIDBustaIdTransazione.get(key);
//	}
//	public static String removeIdTransazione(String idBusta,boolean delegata){
//		String key = idBusta;
//		if(delegata){
//			key = key+"_DELEGATA";
//		}else{
//			key = key+"_APPLICATIVA";
//		}
//		return mappingIDBustaIdTransazione.remove(key);
//	}

	
	private static Vector<String> idBustaFiltroDuplicati = new Vector<String>();
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
