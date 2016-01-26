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

import org.apache.commons.net.time.TimeTCPClient;
import org.openspcoop2.utils.UtilsException;

/**
 * Classe per la produzione di date utilizzando un server TimeTCP
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TCPTimeDate implements IDate {

	private static InetAddress server = null;
	private static int port = TimeTCPClient.DEFAULT_PORT;
	private static int defaultTimeout = -1;
	private static Hashtable<String,Date> time = null;
	private static boolean cacheEnabled = true;
	private static int cacheRefresh = 100;
	
	
	private static TimeTCPClient getTCPClient() throws Exception{
		if(TCPTimeDate.server==null)
			throw new Exception("Inizializzazione non effettuata, invocare metodo init");
		TimeTCPClient tcpClient = new TimeTCPClient();
		if(TCPTimeDate.defaultTimeout!=-1)
			tcpClient.setDefaultTimeout(TCPTimeDate.defaultTimeout);
		tcpClient.connect(TCPTimeDate.server,TCPTimeDate.port);
		return tcpClient;
	}
	
	private static Date getDateCached() throws Exception{
		
		TimeTCPClient tcpClient = null;
		try{
			if(TCPTimeDate.cacheEnabled==false){
				tcpClient = TCPTimeDate.getTCPClient();
				return tcpClient.getDate();
			}
			else{
				String key = (System.currentTimeMillis() / (TCPTimeDate.cacheRefresh)) + ""; // al massimo vengono eseguite 10 richieste al secondo.
				if(TCPTimeDate.time.containsKey(key)){
					//System.out.println("NOW ["+key+"] from cache");
					return TCPTimeDate.time.get(key);
				}else{
					synchronized(TCPTimeDate.time){
						if(TCPTimeDate.time.containsKey(key)){
							//System.out.println("NOW ["+key+"] from cache sync");
							return TCPTimeDate.time.get(key);
						}else{
							//System.out.println("NOW ["+key+"] no cache");
							tcpClient = TCPTimeDate.getTCPClient();
							Date d = tcpClient.getDate();
							TCPTimeDate.time.clear();
							TCPTimeDate.time.put(key, d);
							return d;
						}
					}
				}	
			}
		}catch(Exception e){
			throw new UtilsException("getDateCached error: "+e.getMessage(),e);
		}finally{
			try{
				if(tcpClient!=null && tcpClient.isConnected())
					tcpClient.disconnect();
			}catch(Exception e){}
		}
	}
	
	
	/**
	 * Impostazione delle proprieta' per il DateManager
	 */
	@Override
	public void init(java.util.Properties properties) throws UtilsException{
		try{
			
			// timeout
			String timeoutS = properties.getProperty("time.timeout");
			if(timeoutS!=null){
				try{
					TCPTimeDate.defaultTimeout = Integer.parseInt(timeoutS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.timeout value non valido: "+e.getMessage(),e);
				}
			}
			
			// porta 
			String portaS = properties.getProperty("time.porta");
			TCPTimeDate.port = TimeTCPClient.DEFAULT_PORT;
			if(timeoutS!=null){
				try{
					TCPTimeDate.port = Integer.parseInt(portaS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.porta value non valido: "+e.getMessage(),e);
				}
			}
			
			// ipaddress 
			String ipaddressS = properties.getProperty("time.server");
			if(ipaddressS==null)
				throw new Exception("org.openspcoop.pdd.date.property.time.server value non definito");
			if(ipaddressS!=null){
				try{
					TCPTimeDate.server = InetAddress.getByName(ipaddressS);
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.server value non valido: "+e.getMessage(),e);
				}
			}
			
			// cache
			String cacheS = properties.getProperty("time.cache.enable");
			if(cacheS!=null){
				try{
					TCPTimeDate.cacheEnabled = Boolean.parseBoolean(cacheS);
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
					TCPTimeDate.cacheRefresh = 1000 / value;
				}catch(Exception e){
					throw new Exception("org.openspcoop.pdd.date.property.time.cache.refresh non valido: "+e.getMessage(),e);
				}
			}
			
			TCPTimeDate.time = new Hashtable<String,Date>(); 
			
		}catch(Exception e){
			TCPTimeDate.server=null;
			throw new UtilsException("Inizializzazione TimeTCP Client (properties) non riuscita: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Close il DataManager
	 * 
	 * @throws UtilsException
	 */
	@Override
	public void close() throws UtilsException{}
	
	/**
	 * Ritorna la data corrente.
	 * 
	 * @return Data corrente
	 */
	@Override
	public Date getDate() throws UtilsException{
		try{
			if(TCPTimeDate.server==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("TCP getDate ("+TCPTimeDate.getDateCached()+")");
			return TCPTimeDate.getDateCached();
						
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
			if(TCPTimeDate.server==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("TCP getTimeMillis ("+TCPTimeDate.getDateCached().getTime()+")");
			return TCPTimeDate.getDateCached().getTime();
						
		}catch(Exception e){
			throw new UtilsException("getTimeMillis error: "+e.getMessage(),e);
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
			if(TCPTimeDate.server==null)
				throw new Exception("Inizializzazione non effettuata, invocare metodo init");
			
			//System.out.println("TCP getTimestamp ("+new Timestamp(TCPTimeDate.getDateCached().getTime())+")");
			return new Timestamp(TCPTimeDate.getDateCached().getTime());
						
		}catch(Exception e){
			throw new UtilsException("getTimestamp error: "+e.getMessage(),e);
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
