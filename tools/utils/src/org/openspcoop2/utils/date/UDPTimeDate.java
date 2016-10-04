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



package org.openspcoop2.utils.date;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.apache.commons.net.time.TimeUDPClient;
import org.openspcoop2.utils.UtilsException;

/**
 * Classe per la produzione di date utilizzando un server TimeUDP
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UDPTimeDate implements IDate {

	private static InetAddress server = null;
	private static TimeUDPClient udpClient = null;
	private static int defaultTimeout = -1;
	private static Hashtable<String,Date> time = null;
	private static boolean cacheEnabled = true;
	private static int cacheRefresh = 100;
	
	private static Date getDateCached() throws Exception{
		
		if(UDPTimeDate.cacheEnabled==false){
			return UDPTimeDate.udpClient.getDate(UDPTimeDate.server);
		}
		else{
			String key = (System.currentTimeMillis() / (UDPTimeDate.cacheRefresh)) + ""; // al massimo vengono eseguite 10 richieste al secondo.
			if(UDPTimeDate.time.containsKey(key)){
				//System.out.println("NOW ["+key+"] from cache");
				return UDPTimeDate.time.get(key);
			}else{
				synchronized(UDPTimeDate.time){
					if(UDPTimeDate.time.containsKey(key)){
						//System.out.println("NOW ["+key+"] from cache sync");
						return UDPTimeDate.time.get(key);
					}else{
						//System.out.println("NOW ["+key+"] no cache");
						Date d = UDPTimeDate.udpClient.getDate(UDPTimeDate.server);
						UDPTimeDate.time.clear();
						UDPTimeDate.time.put(key, d);
						return d;
					}
				}
			}
		}
	}
	
	
	/**
	 * Impostazione delle proprieta' per il DateManager
	 */
	@Override
	public void init(java.util.Properties properties) throws UtilsException{
		try{
			UDPTimeDate.udpClient = new TimeUDPClient();
			
			// timeout
			String timeoutS = properties.getProperty("time.timeout");
			if(timeoutS!=null){
				try{
					UDPTimeDate.defaultTimeout = Integer.parseInt(timeoutS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.timeout value non valido: "+e.getMessage(),e);
				}
			}
			if(UDPTimeDate.defaultTimeout!=-1)
				UDPTimeDate.udpClient.setDefaultTimeout(UDPTimeDate.defaultTimeout);
			
			// ipaddress 
			String ipaddressS = properties.getProperty("time.server");
			if(ipaddressS==null)
				throw new Exception("org.openspcoop.pdd.date.property.time.server value non definito");
			if(ipaddressS!=null){
				try{
					UDPTimeDate.server = InetAddress.getByName(ipaddressS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.server value non valido: "+e.getMessage(),e);
				}
			}
	
			// cache
			String cacheS = properties.getProperty("time.cache.enable");
			if(cacheS!=null){
				try{
					UDPTimeDate.cacheEnabled = Boolean.parseBoolean(cacheS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.cache.enable non valido: "+e.getMessage(),e);
				}
			}
			
			// intervallo cache refresh
			String cacheRefreshS = properties.getProperty("time.cache.refresh");
			if(cacheRefreshS!=null){
				try{
					int value = Integer.parseInt(cacheRefreshS);
					if(value>1000)
						throw new Exception("Valore deve essere minore di 1000");
					UDPTimeDate.cacheRefresh = 1000 / value;
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.cache.refresh non valido: "+e.getMessage(),e);
				}
			}
			
			UDPTimeDate.udpClient.open();
			
			UDPTimeDate.time = new Hashtable<String,Date>(); 
			
		}catch(Exception e){
			UDPTimeDate.udpClient=null;
			throw new UtilsException("Inizializzazione TimeUDP Client (properties) non riuscita: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Close il DataManager
	 * 
	 * @throws UtilsException
	 */
	@Override
	public void close() throws UtilsException{
		try{
			if(UDPTimeDate.udpClient!=null)
				UDPTimeDate.udpClient.close();
		}catch(Exception e){
			throw new UtilsException("Chiusura TimeUDP Client non riuscita: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna la data corrente.
	 * 
	 * @return Data corrente
	 */
	@Override
	public Date getDate() throws UtilsException{
		try{
			if(UDPTimeDate.udpClient==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("UDP getDate ("+UDPTimeDate.getDateCached()+")");
			return UDPTimeDate.getDateCached();
						
		}catch(Exception e){
			throw new UtilsException("getDate error: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna la data corrente in millisecondi da Gennaio 1.4970.
	 * 
	 * @return Data corrente
	 */
	@Override
	public long getTimeMillis()throws UtilsException {
		try{
			if(UDPTimeDate.udpClient==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("UDP getTime ("+UDPTimeDate.getDateCached().getTime()+")");
			return UDPTimeDate.getDateCached().getTime();
						
		}catch(Exception e){
			throw new UtilsException("getDate error: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna la data corrente come timestamp SQL.
	 * 
	 * @return Data corrente
	 */
	@Override
	public Timestamp getTimestamp() throws UtilsException{
		try{
			if(UDPTimeDate.udpClient==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("UDP getTimestamp ("+new Timestamp(UDPTimeDate.getDateCached().getTime())+")");
			return new Timestamp(UDPTimeDate.getDateCached().getTime());
						
		}catch(Exception e){
			throw new UtilsException("getDate error: "+e.getMessage(),e);
		}	
	}
	
	/**
	 * Ritorna la data corrente come Calendar
	 * 
	 * @return Data corrente
	 */
	@Override
	public Calendar getCalendar() throws UtilsException{
		Calendar c = new GregorianCalendar();
		c.setTime(this.getDate());
		return c;
	}
}
