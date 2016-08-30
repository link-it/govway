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

package org.openspcoop2.pdd.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;

/**
 * SystemPropertiesManager
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SystemPropertiesManager {

	public static final String SYSTEM_PROPERTIES = "OpenSPCoop2SystemProperties";
	
	private ConfigurazionePdDManager configPdDManager;
	private Logger log;
	
	public SystemPropertiesManager(ConfigurazionePdDManager configPdDManager,Logger log){
		this.configPdDManager = configPdDManager;
		this.log = log;
	}

	public void updateSystemProperties() throws DriverConfigurazioneException{
		
		// remove old properties
		String oldProperties = System.getProperty(SYSTEM_PROPERTIES);
		if(oldProperties!=null && oldProperties.length()>0){
			this.log.info("Remove old properties: ["+oldProperties+"]");
			String [] names = oldProperties.split(",");
			for (int i = 0; i < names.length; i++) {
				this.log.info("RemoveProperty ["+names[i]+"]");
				System.clearProperty(names[i]);
			}
		}
		System.clearProperty(SYSTEM_PROPERTIES);
		
		SystemProperties sps = this.configPdDManager.getSystemPropertiesPdD();
		if(sps!=null && sps.sizeSystemPropertyList()>0){
						
			// set new propertis
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < sps.sizeSystemPropertyList(); i++) {
				Property sp = sps.getSystemProperty(i);
				String nome = sp.getNome();
				String valore = sp.getValore();
				
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(nome);
				this.log.info("SetProperty ["+nome+"]=["+valore+"]");
				System.setProperty(nome, valore);
			}
			
			// Storico
			this.log.info("SetStoricoProperty ["+SYSTEM_PROPERTIES+"]=["+bf.toString()+"]");
			System.setProperty(SYSTEM_PROPERTIES, bf.toString());
			
		}
		else{
			this.log.info("Non sono state rilevate proprietà di sistema da impostare");
		}
		
	}
	
	
	public String getPropertyValue(String key){
		return System.getProperty(key);
	}
	
	public String readAllSystemProperties(String separator){
		return this.readAllSystemProperties(separator, false);
	}
	private String readAllSystemProperties(String separator,boolean onlyOpenSPCoop2){
		Properties p = System.getProperties();
		if(p!=null){
			
			StringBuffer bf = new StringBuffer();
			
			Enumeration<Object> keys = p.keys();
			java.util.ArrayList<String> listKeys = new ArrayList<String>();
			while (keys.hasMoreElements()) {
				Object object = (Object) keys.nextElement();
				if(object instanceof String){
					listKeys.add((String)object);
				}
			}
			java.util.Collections.sort(listKeys);
			
			for (int i = 0; i < listKeys.size(); i++) {
				String key = listKeys.get(i);
				
				boolean isOpenSPCoop2SystemProperty = false;
				String oldProperties = System.getProperty(SYSTEM_PROPERTIES);
				if(oldProperties!=null && oldProperties.length()>0){
					String [] names = oldProperties.split(",");
					for (int j = 0; j < names.length; j++) {
						if(key.equals(names[j])){
							isOpenSPCoop2SystemProperty = true;
							break;
						}
					}
				}
				if(!isOpenSPCoop2SystemProperty){
					if(onlyOpenSPCoop2){
						continue;
					}
				}
				
				if(bf.length()>0){
					bf.append(separator);
				}
				bf.append("["+key+"]=["+p.getProperty(key)+"]");
			}
			
			return bf.toString();
			
		}else{
			return null;
		}
	}
	
	public String readOpenSPCoop2SystemProperties(String separator){
		return this.readAllSystemProperties(separator, true);
	}

	public void removeProperty(String key) throws OpenSPCoop2ConfigurationException{
		if(System.getProperties().containsKey(key)==false){
			throw new OpenSPCoop2ConfigurationException("Property["+key+"] not found");
		}
		System.clearProperty(key);
		String oldProperties = System.getProperty(SYSTEM_PROPERTIES);
		StringBuffer bf = new StringBuffer();
		if(oldProperties!=null && oldProperties.length()>0){
			String [] names = oldProperties.split(",");
			for (int j = 0; j < names.length; j++) {
				if(key.equals(names[j])){
					continue;
				}
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(names[j]);
			}
			System.clearProperty(SYSTEM_PROPERTIES);
			System.setProperty(SYSTEM_PROPERTIES, bf.toString());
		}
	}
	
	public void updateProperty(String key,String value) throws OpenSPCoop2ConfigurationException{
		if(System.getProperties().containsKey(key)==false){
			throw new OpenSPCoop2ConfigurationException("Property["+key+"] not found");
		}
		System.clearProperty(key);
		System.setProperty(key,value);
	}
	
	public void insertProperty(String key,String value) throws OpenSPCoop2ConfigurationException{
		if(System.getProperties().containsKey(key)){
			throw new OpenSPCoop2ConfigurationException("Property["+key+"] already exists (actual value: "+System.getProperties().getProperty(key)+")");
		}
		System.setProperty(key,value);
		
		String props = System.getProperty(SYSTEM_PROPERTIES);
		if(props!=null && props.length()>0){
			props = props + "," + key;
		}
		else{
			props = key;
		}
		
		System.clearProperty(SYSTEM_PROPERTIES);
		System.setProperty(SYSTEM_PROPERTIES, props);
	}
}
