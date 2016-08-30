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

import java.util.Vector;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;

/**
* CollectionProperties
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/
public class CollectionProperties {

	/*
	 * // RICERCA
	 	// 1. VARIABILE DI SISTEMA che identifica il singolo file di properties (es. OPENSPCOOP_PROPERTIES o OPENSPCOOP_LOGGER_PROPERTIES)
	 	// 2. PROPRIETA' JAVA che identifica il singolo file di properties (es. OPENSPCOOP_PROPERTIES o OPENSPCOOP_LOGGER_PROPERTIES)
		// 3. VARIABILE DI SISTEMA: OPENSPCOOP_HOME dove deve essere specificata una directory in cui verra' cercato il file path
		// 4. PROPRIETA' JAVA (es. ApplicationServer o Java con -D): OPENSPCOOP_HOME dove deve essere specificata una directory in cui verra' cercato il file path
		// 5. CLASSPATH con nome path
		// 6. (DIRECTORY DI CONFIGURAZIONE)/path
	*/
	
	// NOTA: La logica di append "+," vale solo per le proprietà che sono definite dentro InstanceProperties e cioè viene usato per aggiungere nuovi valori
	//	     rispetto agli elementi PropertiesOriginale, CollectionProperties, PropertiesObject
	//		 Mentre il valore indicato per una proprietà dei files locale (Properties definiti in questa classi) viene identificato secondo l'algoritmo 1-6 
	//	     e non va in append, ma viene utilizzato solamente il primo incontrato.
	
	private PropertiesReader systemVariable;
	private PropertiesReader javaVariable;
	private PropertiesReader systemOpenSPCoopHome;
	private PropertiesReader javaOpenSPCoopHome;
	private PropertiesReader classpath;
	private PropertiesReader configDir;
	
	public PropertiesReader getSystemVariable() {
		return this.systemVariable;
	}
	public void setSystemVariable(PropertiesReader systemVariable) {
		this.systemVariable = systemVariable;
	}
	public PropertiesReader getJavaVariable() {
		return this.javaVariable;
	}
	public void setJavaVariable(PropertiesReader javaVariable) {
		this.javaVariable = javaVariable;
	}
	public PropertiesReader getSystemOpenSPCoopHome() {
		return this.systemOpenSPCoopHome;
	}
	public void setSystemOpenSPCoopHome(PropertiesReader systemOpenSPCoopHome) {
		this.systemOpenSPCoopHome = systemOpenSPCoopHome;
	}
	public PropertiesReader getJavaOpenSPCoopHome() {
		return this.javaOpenSPCoopHome;
	}
	public void setJavaOpenSPCoopHome(PropertiesReader javaOpenSPCoopHome) {
		this.javaOpenSPCoopHome = javaOpenSPCoopHome;
	}
	public PropertiesReader getClasspath() {
		return this.classpath;
	}
	public void setClasspath(PropertiesReader classpath) {
		this.classpath = classpath;
	}
	public PropertiesReader getConfigDir() {
		return this.configDir;
	}
	public void setConfigDir(PropertiesReader configDir) {
		this.configDir = configDir;
	}
	
	public java.util.Enumeration<?> propertyNames(){
		return this._keys().elements();
	}
	public java.util.Enumeration<?> keys(){ // WRAPPER per java.util.Properties.
		return this._keys().elements();
	}
	public int size(){ // WRAPPER per java.util.Properties.
		return this._keys().size();
	}
	
	private Vector<String> _keys(){
	
		Vector<String> keys = new Vector<String>();
		
		if(this.systemVariable!=null){
			java.util.Enumeration<?> enumProp = this.systemVariable.propertyNames();
			while(enumProp.hasMoreElements()){
				String key = (String)enumProp.nextElement();
				if(keys.contains(key)==false)
					keys.add(key);		
			}
		}
		
		if(this.javaVariable!=null){
			java.util.Enumeration<?> enumProp = this.javaVariable.propertyNames();
			while(enumProp.hasMoreElements()){
				String key = (String)enumProp.nextElement();
				if(keys.contains(key)==false)
					keys.add(key);		
			}
		}
		
		if(this.systemOpenSPCoopHome!=null){
			java.util.Enumeration<?> enumProp = this.systemOpenSPCoopHome.propertyNames();
			while(enumProp.hasMoreElements()){
				String key = (String)enumProp.nextElement();
				if(keys.contains(key)==false)
					keys.add(key);		
			}
		}
		
		if(this.javaOpenSPCoopHome!=null){
			java.util.Enumeration<?> enumProp = this.javaOpenSPCoopHome.propertyNames();
			while(enumProp.hasMoreElements()){
				String key = (String)enumProp.nextElement();
				if(keys.contains(key)==false)
					keys.add(key);		
			}
		}
		
		if(this.classpath!=null){
			java.util.Enumeration<?> enumProp = this.classpath.propertyNames();
			while(enumProp.hasMoreElements()){
				String key = (String)enumProp.nextElement();
				if(keys.contains(key)==false)
					keys.add(key);			
			}
		}
		
		if(this.configDir!=null){
			java.util.Enumeration<?> enumProp = this.configDir.propertyNames();
			while(enumProp.hasMoreElements()){
				String key = (String)enumProp.nextElement();
				if(keys.contains(key)==false)
					keys.add(key);		
			}
		}
		
		
		
		return keys;
	}
	
	public String getValue_convertEnvProperties(String key)throws UtilsException{
	
		if(this.systemVariable!=null){
			String v = this.systemVariable.getValue_convertEnvProperties(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.javaVariable!=null){
			String v = this.javaVariable.getValue_convertEnvProperties(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.systemOpenSPCoopHome!=null){
			String v = this.systemOpenSPCoopHome.getValue_convertEnvProperties(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.javaOpenSPCoopHome!=null){
			String v = this.javaOpenSPCoopHome.getValue_convertEnvProperties(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.classpath!=null){
			String v = this.classpath.getValue_convertEnvProperties(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.configDir!=null){
			String v = this.configDir.getValue_convertEnvProperties(key);
			if(v!=null){
				return v;
			}
		}
		
		return null;
		
	}
	
	public String getProperty(String key) { // WRAPPER per java.util.Properties. Non deve lanciare eccezione
		return this.get(key);
	}
	public String get(String key) { // WRAPPER per java.util.Properties. Non deve lanciare eccezione
		try{
			return this.getValue(key);
		}catch(Exception e){
			LoggerWrapperFactory.getLogger(CollectionProperties.class).error("Lettura proprietà ["+key+"] ha generato un errore: "+e.getMessage(),e);
			return null;
		}
	}
	public String getValue(String key) throws UtilsException{
		
		if(this.systemVariable!=null){
			String v = this.systemVariable.getValue(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.javaVariable!=null){
			String v = this.javaVariable.getValue(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.systemOpenSPCoopHome!=null){
			String v = this.systemOpenSPCoopHome.getValue(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.javaOpenSPCoopHome!=null){
			String v = this.javaOpenSPCoopHome.getValue(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.classpath!=null){
			String v = this.classpath.getValue(key);
			if(v!=null){
				return v;
			}
		}
		
		if(this.configDir!=null){
			String v = this.configDir.getValue(key);
			if(v!=null){
				return v;
			}
		}
		
		return null;
		
	}
	
	public java.util.Properties readProperties_convertEnvProperties(String prefix)throws UtilsException{
		java.util.Properties prop = new java.util.Properties();
		try{
			
			java.util.Enumeration<?> keys = this.propertyNames(); // property names di questa classe colleziona tutti i nomi di tutti i files esterni
			if(keys!=null){
				while (keys.hasMoreElements()) {
					Object keyIt = (Object) keys.nextElement();
					if(keyIt instanceof String){
						String property = (String) keyIt;
						if(property.startsWith(prefix)){
							String key = (property.substring(prefix.length()));
							if(key != null)
								key = key.trim();
							String value = this.getValue_convertEnvProperties(property);
							if(value!=null)
								value = ((String)value).trim();
							if(key!=null && value!=null){
								prop.setProperty(key,(String) value);
							}
						}
					}
				}
			}
			
			return prop;
		}catch(java.lang.Exception e) {
			throw new UtilsException("readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
	
	public java.util.Properties readProperties(String prefix)throws UtilsException{
		java.util.Properties prop = new java.util.Properties();
		try{
			
			java.util.Enumeration<?> keys = this.propertyNames(); // property names di questa classe colleziona tutti i nomi di tutti i files esterni
			if(keys!=null){
				while (keys.hasMoreElements()) {
					Object keyIt = (Object) keys.nextElement();
					if(keyIt instanceof String){
						String property = (String) keyIt;
						if(property.startsWith(prefix)){
							String key = (property.substring(prefix.length()));
							if(key != null)
								key = key.trim();
							String value = this.getValue(property);
							if(value!=null)
								value = ((String)value).trim();
							if(key!=null && value!=null){
								prop.setProperty(key,(String) value);
							}
						}
					}
				}
			}
			
			return prop;
		}catch(java.lang.Exception e) {
			throw new UtilsException("readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
}
