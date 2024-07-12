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
	
	private void logInfo(String msg) {
		if(this.log!=null) {
			this.log.info(msg);
		}
	}
	
	public SystemPropertiesManager(ConfigurazionePdDManager configPdDManager,Logger log){
		this.configPdDManager = configPdDManager;
		this.log = log;
	}

	private String getPrefixKey(String key) {
		return "Property["+key+"] ";
	}
	
	public void updateSystemProperties() throws DriverConfigurazioneException{
		
		// remove old properties
		String oldProperties = System.getProperty(SYSTEM_PROPERTIES);
		if(oldProperties!=null && oldProperties.length()>0){
			this.logInfo("Remove old properties: ["+oldProperties+"]");
			String [] names = oldProperties.split(",");
			for (int i = 0; i < names.length; i++) {
				this.logInfo("RemoveProperty ["+names[i]+"]");
				System.clearProperty(names[i]);
			}
		}
		System.clearProperty(SYSTEM_PROPERTIES);
		
		SystemProperties sps = this.configPdDManager.getSystemPropertiesPdD();
		if(sps!=null && sps.sizeSystemPropertyList()>0){
						
			// set new propertis
			StringBuilder bf = new StringBuilder();
			for (int i = 0; i < sps.sizeSystemPropertyList(); i++) {
				Property sp = sps.getSystemProperty(i);
				String nome = sp.getNome();
				String valore = sp.getValore();
				
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(nome);
				this.logInfo("SetProperty ["+nome+"]=["+valore+"]");
				System.setProperty(nome, valore);
			}
			
			// Storico
			this.logInfo("SetStoricoProperty ["+SYSTEM_PROPERTIES+"]=["+bf.toString()+"]");
			System.setProperty(SYSTEM_PROPERTIES, bf.toString());
			
		}
		else{
			this.logInfo("Non sono state rilevate proprietà di sistema da impostare");
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
			
			StringBuilder bf = new StringBuilder();
			
			Enumeration<Object> keys = p.keys();
			java.util.ArrayList<String> listKeys = new ArrayList<>();
			while (keys.hasMoreElements()) {
				Object object = keys.nextElement();
				if(object instanceof String){
					listKeys.add((String)object);
				}
			}
			java.util.Collections.sort(listKeys);
			
			addSystemProperties(p, listKeys, separator, onlyOpenSPCoop2, bf);
			
			return bf.toString();
			
		}else{
			return null;
		}
	}
	private void addSystemProperties(Properties p,java.util.ArrayList<String> listKeys,String separator,boolean onlyOpenSPCoop2,StringBuilder bf) {
		for (int i = 0; i < listKeys.size(); i++) {
			String key = listKeys.get(i);
			
			boolean isOpenSPCoop2SystemProperty = isOpenSPCoop2SystemProperty(key);
			if(!isOpenSPCoop2SystemProperty &&
				onlyOpenSPCoop2){
				continue;
			}
			
			if(bf.length()>0){
				bf.append(separator);
			}
			bf.append("["+key+"]=["+p.getProperty(key)+"]");
		}
	}
	private boolean isOpenSPCoop2SystemProperty(String key) {
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
		return isOpenSPCoop2SystemProperty;
	}
	
	public String readOpenSPCoop2SystemProperties(String separator){
		return this.readAllSystemProperties(separator, true);
	}

	public void removeProperty(String key) throws OpenSPCoop2ConfigurationException{
		if(!System.getProperties().containsKey(key)){
			throw new OpenSPCoop2ConfigurationException(getPrefixKey(key)+"not found");
		}
		System.clearProperty(key);
		String oldProperties = System.getProperty(SYSTEM_PROPERTIES);
		StringBuilder bf = new StringBuilder();
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
		if(!System.getProperties().containsKey(key)){
			throw new OpenSPCoop2ConfigurationException(getPrefixKey(key)+"not found");
		}
		System.clearProperty(key);
		System.setProperty(key,value);
	}
	
	public void insertProperty(String key,String value) throws OpenSPCoop2ConfigurationException{
		if(System.getProperties().containsKey(key)){
			throw new OpenSPCoop2ConfigurationException(getPrefixKey(key)+"already exists (actual value: "+System.getProperties().getProperty(key)+")");
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
