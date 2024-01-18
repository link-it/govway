/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.spcoop.builder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * Classe che contiene l'informazione sul contatore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopStaticCounter {

	private static Map<String, SPCoopStaticCounter> mapCounter = new HashMap<String, SPCoopStaticCounter>();
	private static synchronized void initCounter(Date now, String formatLastDate, String key) {
		if(!mapCounter.containsKey(key)) {
			mapCounter.put(key, new SPCoopStaticCounter(now, formatLastDate, key));
		}
	}
	public static int getNextSerialCounter(Date now, String formatLastDate, String codAmm, String idPdd) throws ProtocolException {
		String key = codAmm+"_"+idPdd;
		if(!mapCounter.containsKey(key)) {
			initCounter(now, formatLastDate, key);
		}
		return mapCounter.get(key).getNextSerialCounter(now, formatLastDate);
	}
	
	// Usato nel tipo di identificativo static
	/** Contatore seriale */
	@SuppressWarnings("unused")
	private String identificativo;
	private int serialCounter;
	@SuppressWarnings("unused")
	private Date lastDate; // serve per azzerare il contatore ogni minuto
	private String formatLastDate;
	
	public SPCoopStaticCounter(Date now, String formatLastDate, String identificativo) {
		this.identificativo = identificativo;
		this.serialCounter = 0;
		this.lastDate = now;
		this.formatLastDate = formatLastDate;
	}
	
	public synchronized int getNextSerialCounter(Date now, String formatNow) throws ProtocolException{

		//System.out.println("["+this.identificativo+"] getNextSerialCounter ...");
		
		try {
			
			/*
			long millisecondiTrascorsi = now.getTime() - this.lastDate.getTime();
			long secondiTrascorsi = 0;
			if(millisecondiTrascorsi>999) {
				secondiTrascorsi = millisecondiTrascorsi/1000;
			}
			if(secondiTrascorsi>60) {
				//System.out.println("AZZERO PER 60 SECONDI PASSATI! ["+this.identificativo+"]");
			*/
			// FIX: il controllo precedente creava identificativi uguali
			if(!this.formatLastDate.equals(formatNow)) {
				//System.out.println("AZZERO PERCHE SIAMO NEL PROSSIMO MINUTO ["+this.identificativo+"]");
				this.serialCounter = 0;
				this.lastDate = now;
				this.formatLastDate = formatNow;
			}
					
			if((this.serialCounter+1) > SPCoopImbustamento.maxSeriale){
				throw new ProtocolException("Numero massimo del seriale ("+SPCoopImbustamento.maxSeriale+") associabile all'identificato nel minuto raggiunto");
			} 
			
			this.serialCounter=this.serialCounter+1;
			return this.serialCounter;
		}finally {
			//System.out.println("["+this.identificativo+"] getNextSerialCounter: "+this.serialCounter);
		}
	}
	
}
