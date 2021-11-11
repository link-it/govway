/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


/**
 * Classe che permette di leggere il file mailcap presente nel MANIFEST di OpenSPCoop.ear
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MailcapActivationReader {
   
	 private static Map<String, String> mimeTypes = new HashMap<String, String>();
	
	 public static void initDataContentHandler(Logger log,boolean forceLoadMailcap) throws UtilsException{
	      try
	        {
    		  DataContentHandlerManager dchManager = new DataContentHandlerManager(log);
    		  List<String> typesRegistrati = dchManager.readMimeTypesRegistrati(true);
	    	  
	    	  java.io.InputStream is = MailcapActivationReader.class.getResourceAsStream("/META-INF/mailcap");
	    	  java.util.Properties prop= new java.util.Properties();
	    	  prop.load(is);
	    	  Enumeration<?> en =  prop.keys();
	    	  while (en.hasMoreElements()){
	    		  String key = (String) en.nextElement();
	    		  int index = key.indexOf(";;");
	    		  if(index==-1){
	    			  throw new Exception("Entry "+key+(String) prop.get(key)+ " definita con un formato scorretto [;;] (vedi javax/activation/MailcapCommandMap api)");
	    		  }
	    		  String keyMime = key.substring(0,index);
	    		  keyMime = keyMime.trim();
	    		 
	    		  String value = (String) prop.get(key);
	    		  index = value.indexOf("=");
	    		  if(index==-1){
	    			  throw new Exception("Entry "+key+(String) prop.get(key)+ " definita con un formato scorretto [=] (vedi javax/activation/MailcapCommandMap api)");
	    		  }
	    		  value=value.substring(index+1,value.length());
	    		  value=value.trim();
	    		  
	    		  MailcapActivationReader.mimeTypes.put(keyMime, value);
	    		  
	    		  if(typesRegistrati.contains(keyMime)==false){
	    			  if(forceLoadMailcap){
	    				 log.info("DataContentHandler per mime-type "+keyMime+" non risulta registrato, caricamento in corso ...");	    		
		    			 // La chiave non possiede il valore originale. Mancano i caratteri dopo la spazio: dchManager.addMimeTypeIntoMailcap(key+"="+value);
		    			 URL url = MailcapActivationReader.class.getResource("/META-INF/mailcap");
		    			 byte [] mailcap = Utilities.getAsByteArray(url);
		    			 dchManager.addMimeTypesIntoMailcap(mailcap);
	    			  }
	    			  else{
	    				  log.error("DataContentHandler per mime-type "+keyMime+" non risulta registrato");	    			  
	    			  }
	    		  }
	    		  
	    		  log.info("Caricato nel core di OpenSPCoop (!!differente dal MailcapEngine!!) DataContentHandler per mime-type "+keyMime+" gestito tramite la classe "+value);
	    	  }
	        }
	        catch(Exception e)
	        {
	        	throw new UtilsException(e.getMessage(),e);
	        }
	    }

   
	 public static boolean existsDataContentHandler(String mimeType){
		 return MailcapActivationReader.mimeTypes.containsKey(mimeType);
	 }
}





