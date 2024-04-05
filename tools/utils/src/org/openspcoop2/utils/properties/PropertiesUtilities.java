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


package org.openspcoop2.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.openspcoop2.utils.Costanti;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;


/**
* PropertiesUtilities
*
* @author Andrea Poli (apoli@link.it)
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
		
		String envP = System.getenv(Costanti.OPENSPCOOP2_FORCE_CONFIG_FILE);
		if(envP!=null && "true".equalsIgnoreCase(envP)) {
			cp.setForceConfigDir(true);
		}
		else {
			String javaP = System.getProperty(Costanti.OPENSPCOOP2_FORCE_CONFIG_FILE);
			if(javaP!=null && "true".equalsIgnoreCase(javaP)) {
				cp.setForceConfigDir(true);
			}
		}
		
		
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
					if(is!=null) {
						is.close();
					}
				}catch(Exception eClose){
					// close
				}
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
					if(is!=null) {
						is.close();
					}
				}catch(Exception eClose){
					// close
				}
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
					if(is!=null) {
						is.close();
					}
				}catch(Exception eClose){
					// close
				}
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
					if(is!=null) {
						is.close();
					}
				}catch(Exception eClose){
					// close
				}
			}
				
			if(propertiesReader!=null){
				return propertiesReader;
			}
			return null;
		}
		return null;
	}

	
	
	public static Properties convertTextToProperties(String text) {
		Scanner scanner = new Scanner(text);
		Properties properties = new Properties();
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line==null || line.trim().equals("")) {
					continue;
				}
				line = line.trim();
				if(line!=null && line.startsWith("#")) {
					continue;
				}
				if(line!=null && line.contains("=") && !line.startsWith("=")) {
					String key = line.split("=")[0];
					key = key.trim();
					int valueIndex = line.indexOf("=");
					String value = "";
					if(valueIndex<line.length()) {
						value = line.substring(valueIndex+1);
						value = value.trim();
					}
					properties.put(key, value);
				}
			}
		}finally {
			scanner.close();
		}
		return properties;
	}
	
	private static String EMPTY_COMMENT_VALUE = " "; // non uso "" senno su oracle non viene serializzato essendo null
	private static String KEY_COMMENT_UNIQUE = "#C_ID#_";
	private static String getKeyComment(int index) {
		return KEY_COMMENT_UNIQUE.replace("ID", index+"");
	}
	private static String removeKeyComment(String keyComment, int index) {
		String prefix = KEY_COMMENT_UNIQUE.replace("ID", index+"");
		return keyComment.replace(prefix,"");
	}
	public static SortedMap<String> convertTextToSortedMap(String text) throws UtilsException {
		return convertTextToSortedMap(text, false);
	}
	@SuppressWarnings("unchecked")
	public static SortedMap<String> convertTextToSortedMap(String text, boolean addCommentAsEntry) throws UtilsException {
		return (SortedMap<String>) _convertTextToSortedMap(text, addCommentAsEntry, false);
	}
	public static SortedMap<List<String>> convertTextToSortedListMap(String text) throws UtilsException {
		return convertTextToSortedListMap(text, false);
	}
	@SuppressWarnings("unchecked")
	public static SortedMap<List<String>> convertTextToSortedListMap(String text, boolean addCommentAsEntry) throws UtilsException {
		return (SortedMap<List<String>>) _convertTextToSortedMap(text, addCommentAsEntry, true);
	}
	private static SortedMap<?> _convertTextToSortedMap(String text, boolean addCommentAsEntry, boolean listEnabled) throws UtilsException {
		Scanner scanner = new Scanner(text);
		SortedMap<String> propertiesString = null;
		SortedMap<List<String>> propertiesList = null;
		if(listEnabled) {
			propertiesList = new SortedMap<List<String>>();
		}
		else {
			propertiesString = new SortedMap<String>();
		}
		try {
			int index = 0;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line==null || line.trim().equals("")) {
					continue;
				}
				line = line.trim();
				if(line.startsWith("#")) {
					if(addCommentAsEntry) {
						String key = getKeyComment(index)+line;
						//System.out.println("ADD COMMENT '"+key+"'");
						if(listEnabled) {
							List<String> l = new ArrayList<>();
							l.add(EMPTY_COMMENT_VALUE);
							propertiesList.add(key, l);
						}
						else {
							propertiesString.add(key, EMPTY_COMMENT_VALUE);
						}
						index++;
					}
					continue;
				}
				if(line.contains("=")) {
					String key = line.split("=")[0];
					key = key.trim();
					int valueIndex = line.indexOf("=");
					String value = "";
					if(valueIndex<line.length()) {
						value = line.substring(valueIndex+1);
						value = value.trim();
					}
					if(listEnabled) {
						List<String> l = propertiesList.get(key);
						if(l==null) {
							l = new ArrayList<>();
							propertiesList.add(key, l);
						}
						l.add(value);
					}
					else {
						propertiesString.add(key, value);
					}
				}
			}
		}finally {
			scanner.close();
		}
		return listEnabled ? propertiesList : propertiesString;
	}
	
	public static String convertSortedMapToText(SortedMap<String> map) throws UtilsException {
		return convertSortedMapToText(map, false);
	}
	public static String convertSortedMapToText(SortedMap<String> map, boolean commentAsEntry) throws UtilsException {
		return _convertSortedMapToText(map, commentAsEntry, false);
	}
	public static String convertSorteListdMapToText(SortedMap<List<String>> map) throws UtilsException {
		return convertSortedListMapToText(map, false);
	}
	public static String convertSortedListMapToText(SortedMap<List<String>> map, boolean commentAsEntry) throws UtilsException {
		return _convertSortedMapToText(map, commentAsEntry, true);
	}
	private static String _convertSortedMapToText(SortedMap<?> map, boolean commentAsEntry, boolean listEnabled) throws UtilsException {
		StringBuilder sb = new StringBuilder();
		if(map!=null && !map.isEmpty()) {
			int index = 0;
			for (String key : map.keys()) {
				
				if(key==null) {
					continue;
				}
								
				if(key.startsWith("#") && commentAsEntry) {
					
					if(sb.length() >0)
						sb.append("\n");
					
					//System.out.println("READ COMMENT '"+key+"'");
					String keyComment = getKeyComment(index);
					if(key.startsWith(keyComment)) {
						String line = removeKeyComment(key, index);
						//System.out.println("READ ADD COMMENT '"+line+"'");
						sb.append(line);
						index++;
					}
					else {
						//System.out.println("READ ADD ORIGINAL '"+key+"'");
						sb.append(key);
					}
					continue;
				}
								
				if(listEnabled) {
					@SuppressWarnings("unchecked")
					List<String> values = (List<String>) map.get(key);
					if(values!=null && !values.isEmpty()) {
						for (String value : values) {
							if(sb.length() >0)
								sb.append("\n");
							sb.append(key).append("=").append(value);
						}
					}
				}else {
					if(sb.length() >0)
						sb.append("\n");
					String value = (String) map.get(key);
					sb.append(key).append("=").append(value);
				}
			}	
		}
		return sb.toString();
	}
}
