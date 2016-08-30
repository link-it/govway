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


package org.openspcoop2.utils.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;


/**
* PropertiesUtilities
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/

public class PropertiesUtilities {

	/*
	 * // RICERCA
	 	// 1. VARIABILE DI SISTEMA che identifica il singolo file di properties (es. OPENSPCOOP_PROPERTIES o OPENSPCOOP_LOGGER_PROPERTIES)
	 	// 2. PROPRIETA' JAVA che identifica il singolo file di properties (es. OPENSPCOOP_PROPERTIES o OPENSPCOOP_LOGGER_PROPERTIES)
		// 3. VARIABILE DI SISTEMA: OPENSPCOOP_HOME dove deve essere specificata una directory in cui verra' cercato il file path
		// 4. PROPRIETA' JAVA (es. ApplicationServer o Java con -D): OPENSPCOOP_HOME dove deve essere specificata una directory in cui verra' cercato il file path
		// 5. CLASSPATH con nome path
		// 6. (DIRECTORY DI CONFIGURAZIONE)/path
	*/
	public static CollectionProperties searchLocalImplementation(String OPENSPCOOP2_LOCAL_HOME, Logger log,String variabile,String path,String confDirectory){	
		return searchLocalImplementation(OPENSPCOOP2_LOCAL_HOME, log, variabile, path, confDirectory, true);
	}
	public static CollectionProperties searchLocalImplementation(String OPENSPCOOP2_LOCAL_HOME, Logger log,String variabile,String path,String confDirectory,boolean readCallsNotSynchronized){	
		
		CollectionProperties cp = new CollectionProperties();
		
		Properties p1 = PropertiesUtilities.examineStep1(log,variabile);
		if(p1!=null){
			cp.setSystemVariable(new PropertiesReader(p1,readCallsNotSynchronized));
		}
		
		Properties p2 = PropertiesUtilities.examineStep2(log,variabile);
		if(p2!=null){
			cp.setJavaVariable(new PropertiesReader(p2,readCallsNotSynchronized));
		}
		
		Properties p3 = PropertiesUtilities.examineStep3(OPENSPCOOP2_LOCAL_HOME,log,path);
		if(p3!=null){
			cp.setSystemOpenSPCoopHome(new PropertiesReader(p3,readCallsNotSynchronized));
		}
		
		Properties p4 = PropertiesUtilities.examineStep4(OPENSPCOOP2_LOCAL_HOME,log,path);
		if(p4!=null){
			cp.setJavaOpenSPCoopHome(new PropertiesReader(p4,readCallsNotSynchronized));
		}
			
		Properties p5 = PropertiesUtilities.examineStep5(log,path);
		if(p5!=null){
			cp.setClasspath(new PropertiesReader(p5,readCallsNotSynchronized));
		}
				
		File fConfDirectory = null;
		if(confDirectory!=null){
			fConfDirectory = new File(confDirectory);
		}
		if(fConfDirectory!=null && fConfDirectory.exists() && fConfDirectory.isDirectory() ){
			Properties p6 = PropertiesUtilities.examineStep6(log,path,fConfDirectory);
			if(p6!=null){
				cp.setConfigDir(new PropertiesReader(p6,readCallsNotSynchronized));
			}
		}
			
		return cp;
	}
	
	private static Properties examineStep1(Logger log,String variabile){
		/*
		System.out.println("--------- ENV --------------");
		java.util.Map<String, String> env = System.getenv();
		for (java.util.Iterator<String> iterator = env.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			System.out.println("\t["+key+"]=["+env.get(key)+"]");			
		}
		System.out.println("--------- ENV --------------");
		*/
		String file = System.getenv(variabile);
		String subject =  "Variabile di sistema "+variabile;
		if(file!=null){
			File ffile = new File(file);
			if(ffile.exists()==false){
				log.error("["+subject+"] File non esistente: "+ffile.getAbsolutePath());
				return null;
			}
			if(ffile.canRead()==false){
				log.error("["+subject+"] File non accessibile: "+ffile.getAbsolutePath());
				return null;
			}
			InputStream is = null;
			try{
				is = new FileInputStream(ffile);
				return PropertiesUtilities.getPropertiesReader(log,is,subject);
			}catch(java.io.IOException e) {
				log.error("["+subject+"] file di properties non utilizzabile: "+e.getMessage(),e);
			}finally{
				try{
					is.close();
				}catch(Exception eClose){}
			}
		}
		return null;
	}
	
	private static Properties examineStep2(Logger log,String variabile){
		/*
		System.out.println("--------- ENV --------------");
		java.util.Map<String, String> env = System.getenv();
		for (java.util.Iterator<String> iterator = env.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			System.out.println("\t["+key+"]=["+env.get(key)+"]");			
		}
		System.out.println("--------- ENV --------------");
		*/
		String file = System.getProperty(variabile);
		String subject =  "Proprieta' di sistema "+variabile;
		if(file!=null){
			File ffile = new File(file);
			if(ffile.exists()==false){
				log.error("["+subject+"] File non esistente: "+ffile.getAbsolutePath());
				return null;
			}
			if(ffile.canRead()==false){
				log.error("["+subject+"] File non accessibile: "+ffile.getAbsolutePath());
				return null;
			}
			InputStream is = null;
			try{
				is = new FileInputStream(ffile);
				return PropertiesUtilities.getPropertiesReader(log,is,subject);
			}catch(java.io.IOException e) {
				log.error("["+subject+"] file di properties non utilizzabile: "+e.getMessage(),e);
			}finally{
				try{
					is.close();
				}catch(Exception eClose){}
			}
		}
		return null;
	}
	
	private static Properties examineStep3(String OPENSPCOOP2_LOCAL_HOME,Logger log,String path){
		/*
		System.out.println("--------- ENV --------------");
		java.util.Map<String, String> env = System.getenv();
		for (java.util.Iterator<String> iterator = env.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			System.out.println("\t["+key+"]=["+env.get(key)+"]");			
		}
		System.out.println("--------- ENV --------------");
		*/
		String dir = System.getenv(OPENSPCOOP2_LOCAL_HOME);
		String subject =  "Variabile di sistema "+OPENSPCOOP2_LOCAL_HOME;
		if(dir!=null){
			File fDir = new File(dir);
			if(fDir.exists()==false){
				log.error("["+subject+"] Directory non esistente: "+fDir.getAbsolutePath());
				return null;
			}
			if(fDir.canRead()==false){
				log.error("["+subject+"] Directory non accessibile: "+fDir.getAbsolutePath());
				return null;
			}
			return PropertiesUtilities.getPropertiesReader(log,fDir.getAbsolutePath()+File.separatorChar+path,subject);
		}
		return null;
	}
	
	private static Properties examineStep4(String OPENSPCOOP2_LOCAL_HOME,Logger log,String path){
		/*	
		System.out.println("--------- PROPERTIES --------------");
		java.util.Properties properties = System.getProperties();
		java.util.Enumeration<?> enumProperties = properties.keys();
		while (enumProperties.hasMoreElements()) {
			Object object = (Object) enumProperties.nextElement();
			System.out.println("\t["+object+"]=["+properties.get(object)+"]");			
		}
		System.out.println("--------- PROPERTIES --------------");
		*/
		String dir = System.getProperty(OPENSPCOOP2_LOCAL_HOME);
		String subject =  "Proprieta' di sistema "+OPENSPCOOP2_LOCAL_HOME;
		if(dir!=null){
			File fDir = new File(dir);
			if(fDir.exists()==false){
				log.error("["+subject+"] Directory non esistente: "+fDir.getAbsolutePath());
				return null;
			}
			if(fDir.canRead()==false){
				log.error("["+subject+"] Directory non accessibile: "+fDir.getAbsolutePath());
				return null;
			}
			return PropertiesUtilities.getPropertiesReader(log,fDir.getAbsolutePath()+File.separatorChar+path,subject);
		}
		return null;
	}
	
	private static Properties examineStep5(Logger log,String path){
		return PropertiesUtilities.getPropertiesReader(log, PropertiesUtilities.class.getResourceAsStream("/"+path), "CLASSPATH: "+path);
	}
	
	private static Properties examineStep6(Logger log,String path,File fConfDirectory){
		File f = new File(fConfDirectory,path);
		if(f.exists()){
			return PropertiesUtilities.getPropertiesReader(log,f.getAbsolutePath(), "CONFIG_DIR_OPENSPCOOP/"+path);
		}
		return null;
	}
	
	private static Properties getPropertiesReader(Logger log,String path,String subject){
		if(path!=null){
			File f = new File(path);
			if(!f.exists()){
				// NON DEVO SEGNALARE L'ERRORE log.error("["+subject+"] file di properties non esistente: "+f.getAbsolutePath());
				return null;
			}
			if(!f.canRead()){
				log.error("["+subject+"] file di properties non accessibile: "+f.getAbsolutePath());
				return null;
			}
			InputStream is = null;
			try{
				is = new FileInputStream(f);
				return PropertiesUtilities.getPropertiesReader(log,is,subject);
			}catch(java.io.IOException e) {
				log.error("["+subject+"] file di properties non utilizzabile: "+e.getMessage(),e);
			}finally{
				try{
					is.close();
				}catch(Exception eClose){}
			}
		}
		return null;
	}
	private static Properties getPropertiesReader(Logger log,InputStream is,String subject){
		if(is!=null){
			Properties propertiesReader = null;
			try{  
				propertiesReader = new Properties();
				propertiesReader.load(is);
			}catch(java.io.IOException e) {
				propertiesReader = null;
				log.error("["+subject+"] file di properties non utilizzabile: "+e.getMessage(),e);
			}finally{
				try{
					is.close();
				}catch(Exception eClose){}
			}
				
			if(propertiesReader!=null){
				return propertiesReader;
			}
			return null;
		}
		return null;
	}
	
}
