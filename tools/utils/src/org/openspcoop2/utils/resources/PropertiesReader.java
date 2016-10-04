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


package org.openspcoop2.utils.resources;

import java.util.Properties;

import org.openspcoop2.utils.UtilsException;

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
		if(value.indexOf("${")!=-1){
			while (value.indexOf("${")!=-1){
				int indexStart = value.indexOf("${");
				int indexEnd = value.indexOf("}");
				if(indexEnd==-1){
					throw new UtilsException("Errore durante l'interpretazione del valore ["+value+"]: ${ utilizzato senza la rispettiva chiusura }");
				}
				String nameSystemProperty = value.substring(indexStart+"${".length(),indexEnd);
				String valueSystemProperty = System.getProperty(nameSystemProperty);
				if(valueSystemProperty==null){
					throw new UtilsException("Errore durante l'interpretazione del valore ["+value+"]: variabile di sistema ${"+nameSystemProperty+"} non esistente");
				}
				value = value.replace("${"+nameSystemProperty+"}", valueSystemProperty);
			}
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
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Properties prop_tmp = this.readProperties(prefix);
			java.util.Enumeration<?> en = prop_tmp.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				String value = prop_tmp.getProperty(property);
				if(value!=null){
					value = value.trim();
					value = this.convertEnvProperties(value);
				}
				if(property!=null && value!=null){
					prop.setProperty(property,value);
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
