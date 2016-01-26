/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.openspcoop2.utils.UtilsException;

/**
 * Classe per la produzione di date utilizzando un server NTP
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NTPDate implements IDate {

	private static InetAddress server = null;
	private static NTPUDPClient udpClient = null;
	private static int defaultTimeout = -1;
	private static Hashtable<String,Date> time = null;
	private static boolean cacheEnabled = true;
	private static int cacheRefresh = 100;
	
	private static Date getDateCached() throws Exception{
		
		if(NTPDate.cacheEnabled==false){
			TimeInfo tInfo = NTPDate.udpClient.getTime(NTPDate.server);
			return tInfo.getMessage().getReceiveTimeStamp().getDate();
		}
		else{
			String key = (System.currentTimeMillis() / (NTPDate.cacheRefresh)) + ""; // al massimo vengono eseguite 10 richieste al secondo.
			if(NTPDate.time.containsKey(key)){
				//System.out.println("NOW ["+key+"] from cache");
				return NTPDate.time.get(key);
			}else{
				synchronized(NTPDate.time){
					if(NTPDate.time.containsKey(key)){
						//System.out.println("NOW ["+key+"] from cache sync");
						return NTPDate.time.get(key);
					}else{
						//System.out.println("NOW ["+key+"] no cache");
						TimeInfo tInfo = NTPDate.udpClient.getTime(NTPDate.server);
						Date d = tInfo.getMessage().getReceiveTimeStamp().getDate();
						NTPDate.time.clear();
						NTPDate.time.put(key, d);
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
			NTPDate.udpClient = new NTPUDPClient();
			
			// timeout
			String timeoutS = properties.getProperty("ntp.timeout");
			if(timeoutS!=null){
				try{
					NTPDate.defaultTimeout = Integer.parseInt(timeoutS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.ntp.timeout value non valido: "+e.getMessage(),e);
				}
			}
			if(NTPDate.defaultTimeout!=-1)
				NTPDate.udpClient.setDefaultTimeout(NTPDate.defaultTimeout);
			
			// ipaddress 
			String ipaddressS = properties.getProperty("ntp.server");
			if(ipaddressS==null)
				throw new Exception("org.openspcoop.pdd.date.property.ntp.server value non definito");
			if(ipaddressS!=null){
				try{
					NTPDate.server = InetAddress.getByName(ipaddressS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.ntp.server value non valido: "+e.getMessage(),e);
				}
			}
			
			// cache
			String cacheS = properties.getProperty("ntp.cache.enable");
			if(cacheS!=null){
				try{
					NTPDate.cacheEnabled = Boolean.parseBoolean(cacheS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.ntp.cache.enable non valido: "+e.getMessage(),e);
				}
			}
			
			// intervallo cache refresh
			String cacheRefreshS = properties.getProperty("ntp.cache.refresh");
			if(cacheRefreshS!=null){
				try{
					int value = Integer.parseInt(cacheRefreshS);
					if(value>1000)
						throw new Exception("Valore deve essere minore di 1000");
					NTPDate.cacheRefresh = 1000 / value;
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.ntp.cache.refresh non valido: "+e.getMessage(),e);
				}
			}
			
			NTPDate.udpClient.open();
			
			NTPDate.time = new Hashtable<String,Date>(); 
			
		}catch(Exception e){
			NTPDate.udpClient=null;
			throw new UtilsException("Inizializzazione NTP Client (properties) non riuscita: "+e.getMessage(),e);
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
			if(NTPDate.udpClient!=null)
				NTPDate.udpClient.close();
		}catch(Exception e){
			throw new UtilsException("Chiusura NTP Client non riuscita: "+e.getMessage(),e);
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
			if(NTPDate.udpClient==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("NTP getDate ("+NTPDate.getDateCached()+")");
			return NTPDate.getDateCached();
						
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
			if(NTPDate.udpClient==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("NTP getTime ("+NTPDate.getDateCached().getTime()+")");
			return NTPDate.getDateCached().getTime();
						
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
			if(NTPDate.udpClient==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("NTP getTimestamp ("+new Timestamp(NTPDate.getDateCached().getTime())+")");
			return new Timestamp(NTPDate.getDateCached().getTime());
						
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
