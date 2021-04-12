package org.openspcoop2.protocol.spcoop.builder;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.date.DateManager;

/**
 * Classe che contiene l'informazione sul contatore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopStaticCounter {

	private static Map<String, SPCoopStaticCounter> mapCounter = new HashMap<String, SPCoopStaticCounter>();
	private static synchronized void initCounter(String key) {
		if(!mapCounter.containsKey(key)) {
			mapCounter.put(key, new SPCoopStaticCounter());
		}
	}
	public static int getNextSerialCounter(String codAmm, String idPdd) throws ProtocolException {
		String key = codAmm+"_"+idPdd;
		if(!mapCounter.containsKey(key)) {
			initCounter(key);
		}
		return mapCounter.get(key).getNextSerialCounter();
	}
	
	// Usato nel tipo di identificativo static
	/** Contatore seriale */
	private int serialCounter;
	private long lastDate; // serve per azzerare il contatore ogni minuto
	
	public SPCoopStaticCounter() {
		this.serialCounter = 0;
		this.lastDate = DateManager.getTimeMillis();
	}
	
	public synchronized int getNextSerialCounter() throws ProtocolException{

		long millisecondiTrascorsi = DateManager.getTimeMillis() - this.lastDate;
		long secondiTrascorsi = 0;
		if(millisecondiTrascorsi>999) {
			secondiTrascorsi = millisecondiTrascorsi/1000;
		}
		if(secondiTrascorsi>60) {
			//System.out.println("AZZERO PER 60 SECONDI PASSATI!");
			this.serialCounter = 0;
			this.lastDate = DateManager.getTimeMillis();
		}
				
		if((this.serialCounter+1) > SPCoopImbustamento.maxSeriale){
			throw new ProtocolException("Numero massimo del seriale ("+SPCoopImbustamento.maxSeriale+") associabile all'identificato nel minuto raggiunto");
		} 
		
		this.serialCounter++;
		return this.serialCounter;
	}
	
}
