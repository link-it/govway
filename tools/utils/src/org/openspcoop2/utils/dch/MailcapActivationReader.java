/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.utils.dch;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;


/**
 * Classe che permette di leggere il file mailcap presente nel MANIFEST di OpenSPCoop.ear
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MailcapActivationReader {

	private MailcapActivationReader() {}

	private static Map<String, String> mimeTypes = new HashMap<>();

	private static final String MAILCAP = "/META-INF/mailcap";
	
	private static void logInfo(Logger log, String msg) {
		log.info(msg);
	}
	private static void logError(Logger log, String msg) {
		log.error(msg);
	}
	
	private static boolean debug = true;
	public static void setDebug(boolean debug) {
		MailcapActivationReader.debug = debug;
	}


	public static void initDataContentHandler(Logger log,boolean forceLoadMailcap) throws UtilsException{
		java.io.InputStream is = null;
		try
		{
			DataContentHandlerManager dchManager = new DataContentHandlerManager(log);
			List<String> typesRegistrati = dchManager.readMimeTypesRegistrati(true);

			URL urlDebug = MailcapActivationReader.class.getResource(MAILCAP);
			logInfo(log, "Posizione '"+MAILCAP+"' in classloader: "+urlDebug);
			
			is = MailcapActivationReader.class.getResourceAsStream(MAILCAP);
			if(debug) {
				String s = org.openspcoop2.utils.Utilities.getAsString(is, Charset.UTF_8.getValue());
				is.close();
				logInfo(log, "Contenuto '"+MAILCAP+"': "+s);
				is = new java.io.ByteArrayInputStream(s.getBytes());
			}
			
			java.util.Properties prop= new java.util.Properties();
			prop.load(is);
			Enumeration<?> en =  prop.keys();
			while (en.hasMoreElements()){
				String key = (String) en.nextElement();
				checkType(key, prop, typesRegistrati,
						log,forceLoadMailcap,
						dchManager);
			}
		}
		catch(Exception e)
		{
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private static void checkType(String key, java.util.Properties prop, List<String> typesRegistrati,
			Logger log,boolean forceLoadMailcap,
			DataContentHandlerManager dchManager) throws UtilsException, IOException {

		int index = key.indexOf(";;");
		if(index==-1){
			throw new UtilsException("Entry "+key+(String) prop.get(key)+ " definita con un formato scorretto [;;] (vedi jakarta.activation.MailcapCommandMap api)");
		}
		String keyMime = key.substring(0,index);
		keyMime = keyMime.trim();

		String value = (String) prop.get(key);
		index = value.indexOf("=");
		if(index==-1){
			throw new UtilsException("Entry "+key+(String) prop.get(key)+ " definita con un formato scorretto [=] (vedi jakarta.activation.MailcapCommandMap api)");
		}
		value=value.substring(index+1,value.length());
		value=value.trim();

		MailcapActivationReader.mimeTypes.put(keyMime, value);

		if(!typesRegistrati.contains(keyMime)){
			if(forceLoadMailcap){
				logInfo(log, "DataContentHandler per mime-type "+keyMime+" non risulta registrato, caricamento in corso ...");    		
				/** La chiave non possiede il valore originale. Mancano i caratteri dopo la spazio: dchManager.addMimeTypeIntoMailcap(key+"="+value); */
				URL url = MailcapActivationReader.class.getResource(MAILCAP);
				logInfo(log, "Posizione 'forceLoadMailcap' '"+MAILCAP+"' in classloader: "+url);
				byte [] mailcap = Utilities.getAsByteArray(url);
				if(debug) {
					logInfo(log, "Contenuto 'forceLoadMailcap' '"+MAILCAP+"': "+new String(mailcap));
				}
				dchManager.addMimeTypesIntoMailcap(mailcap);
			}
			else{
				logError(log,"DataContentHandler per mime-type "+keyMime+" non risulta registrato");    			  
			}
		}

		logInfo(log, "Caricato nel core di OpenSPCoop (!!differente dal MailcapEngine!!) DataContentHandler per mime-type "+keyMime+" gestito tramite la classe "+value);
	}

	public static boolean existsDataContentHandler(String mimeType){
		return MailcapActivationReader.mimeTypes.containsKey(mimeType);
	}
}





