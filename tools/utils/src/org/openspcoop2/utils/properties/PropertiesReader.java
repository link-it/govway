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

import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.MapReader;

/**
 * Lettore di file properties, che permette di interpretare anche le variabili di sistema 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertiesReader extends MapReader<Object, Object> {
	
	public PropertiesReader(Properties properties, boolean readCallsNotSynchronized){
		super(properties, readCallsNotSynchronized);
	}
	
	public String getValue(String key) throws UtilsException{
		Object valueObject = super.getValue(key);
		if(valueObject!=null){
			if(!(valueObject instanceof String)){
				throw new UtilsException("Proprieta '"+key+"' non e' di tipo java.lang.String (trovato tipo: ["+valueObject.getClass().getName()+"] valore: ["+valueObject+"])");
			}
			String value = (String) valueObject;
			return value.trim();
		}
		else{
			return null;
		}
	}
	
	public String convertEnvProperties(String value)throws UtilsException{
		return this.convertEnvProperties(value, false);
	}
	public String convertEnvProperties(String value, boolean convertKeyEnvProperties)throws UtilsException{
		String label = "del valore";
		if(convertKeyEnvProperties){
			label = "della chiave";
		}
		while (value.indexOf("${")!=-1){
			int indexStart = value.indexOf("${");
			int indexEnd = value.indexOf("}");
			if(indexEnd==-1){
				throw new UtilsException("Errore durante l'interpretazione "+label+" ["+value+"]: ${ utilizzato senza la rispettiva chiusura }");
			}
			String nameSystemProperty = value.substring(indexStart+"${".length(),indexEnd);
			String valueSystemProperty = System.getenv(nameSystemProperty); // sistema
			if(valueSystemProperty==null) {
				valueSystemProperty = System.getProperty(nameSystemProperty); // java
			}
			if(valueSystemProperty==null){
				throw new UtilsException("Errore durante l'interpretazione "+label+" ["+value+"]: variabile di sistema o java ${"+nameSystemProperty+"} non esistente");
			}
			value = value.replace("${"+nameSystemProperty+"}", valueSystemProperty);
		}
		return value;
	}
	
	public String getValue_convertEnvProperties(String key)throws UtilsException{
		String value = this.getValue(key);
		if(value!=null)
			value = this.convertEnvProperties(value);
		return value;
		//}

	}
	
	/**
	 * Legge le proprieta' che possiedono un nome che inizia con un determinato prefisso
	 * 
	 * @param prefix
	 * @return java.util.Properties
	 * @throws UtilsException
	 */
	public java.util.Properties readProperties (String prefix)throws UtilsException{
		java.util.Properties prop = new java.util.Properties();
		try{
			
			java.util.Enumeration<?> keys = this.keys();
			while (keys.hasMoreElements()) {
				Object keyIt = keys.nextElement();
				if(keyIt instanceof String){
					String property = (String) keyIt;
					if(property.startsWith(prefix)){
						String key = (property.substring(prefix.length()));
						if(key != null)
							key = key.trim();
						String value = this.getValue(property);
						if(value!=null)
							value = (value).trim();
						if(key!=null && value!=null){
							prop.setProperty(key,value);
						}
					}
				}
			}
			return prop;
		}catch(java.lang.Exception e) {
			throw new UtilsException("readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
	
	/**
	 * Legge le proprieta' che possiedono un nome che inizia con un determinato prefisso
	 * 
	 * @param prefix
	 * @return java.util.Properties
	 * @throws UtilsException
	 */
	public java.util.Properties readProperties_convertEnvProperties (String prefix)throws UtilsException{
		return this.readProperties_convertEnvProperties(prefix,false);
	}
	public java.util.Properties readProperties_convertEnvProperties (String prefix, boolean convertKeyEnvProperties)throws UtilsException{
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Properties propTmp = this.readProperties(prefix);
			java.util.Enumeration<?> en = propTmp.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				String value = null;
				if(property!=null) {
					value = propTmp.getProperty(property);
				}
				if(value!=null){
					value = value.trim();
					value = this.convertEnvProperties(value);
				}
				if(property!=null && value!=null){
					if(convertKeyEnvProperties){
						prop.setProperty(this.convertEnvProperties(property,true),value);
					}
					else{
						prop.setProperty(property,value);
					}
				}
			}
			return prop;
		}catch(java.lang.Exception e) {
			throw new UtilsException("readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
	
	public java.util.Enumeration<?> propertyNames(){
		return this.keys();
	}
	
}
