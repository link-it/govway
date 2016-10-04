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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;


import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Loader;


/**
 * Classe per la produzione di date utilizzando
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateManager {

	/** Istanza */
	private static IDate dataManager;
	/** Logger */
	private static Logger log;
	
	/**
	 * Inizializzazione DataManager
	 * 
	 */
	public static synchronized void initializeDataManager(String className,java.util.Properties properties,Logger log)throws UtilsException{
		
		if(DateManager.dataManager==null){
			try{
				if(log!=null)
					DateManager.log = log;
				else
					DateManager.log = LoggerWrapperFactory.getLogger(DateManager.class);
				DateManager.dataManager = (IDate) Loader.getInstance().newInstance(className);
				DateManager.dataManager.init(properties);
			}catch(Exception e){
				throw new UtilsException("Riscontrato errore durante il caricamento del data manager specificato [class:"+className+"]: "+e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Close il DataManager
	 * 
	 * @throws UtilsException
	 */
	public static void close() {
		if(DateManager.dataManager!=null){
			try{
				DateManager.dataManager.close();
			}catch(Exception e){
				DateManager.log.error("Riscontrato errore durante la chiusura del data manager: "+e.getMessage(),e);
			}
		}
	}
	
	
	
	/**
	 * Ritorna la data corrente.
	 * 
	 * @return Data corrente
	 */
	public static Date getDate() {
		try{
			if(DateManager.dataManager==null){
				Logger log = LoggerWrapperFactory.getLogger(DateManager.class);
				log.warn("DateManager non inizializzato");
				DateManager.initializeDataManager("org.openspcoop2.utils.date.SystemDate", new Properties(), log);
			}
			return DateManager.dataManager.getDate();
		}catch(Exception e){
			DateManager.log.error("DateManager.getDate() non riuscita",e);
			return new java.util.Date();
		}
	}
	
	/**
	 * Ritorna la data corrente in millisecondi da Gennaio 1.4970.
	 * 
	 * @return Data corrente
	 */
	public static long getTimeMillis() {
		try{
			if(DateManager.dataManager==null){
				Logger log = LoggerWrapperFactory.getLogger(DateManager.class);
				log.warn("DateManager non inizializzato");
				DateManager.initializeDataManager("org.openspcoop2.utils.date.SystemDate", new Properties(), log);
			}
			return DateManager.dataManager.getTimeMillis();
		}catch(Exception e){
			DateManager.log.error("DateManager.getTimeMillis() non riuscita",e);
			return new java.util.Date().getTime();
		}
	}
	
	/**
	 * Ritorna la data corrente come timestamp SQL.
	 * 
	 * @return Data corrente
	 */
	public static Timestamp getTimestamp() {
		try{
			if(DateManager.dataManager==null){
				Logger log = LoggerWrapperFactory.getLogger(DateManager.class);
				log.warn("DateManager non inizializzato");
				DateManager.initializeDataManager("org.openspcoop2.utils.date.SystemDate", new Properties(), log);
			}
			return DateManager.dataManager.getTimestamp();
		}catch(Exception e){
			DateManager.log.error("DateManager.getTimestamp() non riuscita",e);
			return new Timestamp(new java.util.Date().getTime());
		}
	}
	
	/**
	 * Ritorna la data corrente come Calendar
	 * 
	 * @return Data corrente
	 */
	public static Calendar getCalendar() throws UtilsException{
		try{
			if(DateManager.dataManager==null){
				Logger log = LoggerWrapperFactory.getLogger(DateManager.class);
				log.warn("DateManager non inizializzato");
				DateManager.initializeDataManager("org.openspcoop2.utils.date.SystemDate", new Properties(), log);
			}
			return DateManager.dataManager.getCalendar();
		}catch(Exception e){
			DateManager.log.error("DateManager.getCalendar() non riuscita",e);
			Calendar c = new GregorianCalendar();
			c.setTime(new java.util.Date());
			return c;
		}
	}
}
