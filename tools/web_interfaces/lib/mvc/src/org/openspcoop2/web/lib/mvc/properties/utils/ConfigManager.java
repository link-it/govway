/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.lib.mvc.properties.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.web.lib.mvc.properties.Config;
import org.openspcoop2.web.lib.mvc.properties.utils.serializer.JaxbDeserializer;
import org.slf4j.Logger;

/***
 * 
 * Manager delle configurazioni disponibili.
 * 
 * @author pintori
 *
 */
public class ConfigManager {

	private static ConfigManager instance = null;
	private Logger log = null;
	private Map<String, Map<String,Config>> mapConfig = null;
	private org.openspcoop2.generic_project.utils.XSDValidator validator = null; 
	
	public static ConfigManager getinstance(Logger log) throws Exception {
		if(instance == null)
			init(log);
		
		return instance;
	}
	
	private static synchronized void init(Logger log) throws Exception{
		instance = new ConfigManager(log);
	}
	
	
	public ConfigManager(Logger log) throws Exception {
		this.log = log;
		this.mapConfig = new HashMap<String, Map<String,Config>>();
		
		try {
		this.validator = 
				new org.openspcoop2.generic_project.utils.XSDValidator(this.log,Config.class, 
				"/mvcProperties.xsd"
				// elencare in questa posizione altri schemi xsd che vengono inclusi/importati dallo schema /mvcProperties.xsd
			);
		}catch(Exception e) {
			this.log.error("Errore durante la init del ManagerConfigurazioni: " + e.getMessage(),e);
			throw e;
		}
	}
	
	
	public void leggiConfigurazioni(String systemPath, boolean update, boolean validazioneXSD) throws Exception{
		if(!this.mapConfig.containsKey(systemPath) || update) {
			if(this.mapConfig.containsKey(systemPath)) {
				this.mapConfig.remove(systemPath);
			}
			
			Map<String,Config> mapConfigFromDir = null;
			
			File dir = new File(systemPath);
			
			if(!dir.exists())
				throw new Exception("Il path indicato ["+systemPath+"] non esiste, impossibile leggere le configurazioni");
			
			if(!dir.isDirectory())
				throw new Exception("Il path indicato ["+systemPath+"] non e' una directory");
			
			String[] fileList = dir.list();
			
			if(fileList != null && fileList.length > 0) {
				mapConfigFromDir = new HashMap<String,Config>();
				JaxbDeserializer xmlReader = new JaxbDeserializer();
				for (String f : fileList) {
					File fileConfig = new File(dir.getPath() + File.separator + f); 
					if(!fileConfig.isDirectory()) {
						// validazione XSD se prevista
						if(validazioneXSD) {
							try {
								this.validator.getXsdValidator().valida(fileConfig);
							}catch(Exception e) {
								this.log.error("La configurazione ["+fileConfig.getName()+"] non e' valida: " + e.getMessage());
								throw e;
							}
						}
						
						Config configDaFile = xmlReader.readConfig(fileConfig);
						
						if(mapConfigFromDir.containsKey(configDaFile.getName()))
							throw new Exception("La configurazione ["+configDaFile.getName()+"] e' duplicata all'interno del path indicato ["+systemPath+"].");
							
						mapConfigFromDir.put(configDaFile.getName(),configDaFile);
					}
				}
				this.mapConfig.put(systemPath, mapConfigFromDir);
			}
		}
		
//		return this.mapConfig.get(systemPath);
	}
	
	public List<String> getNomiConfigurazioni(String systemPath) {
		List<String> list = new ArrayList<String>();
		
		list.addAll(this.mapConfig.keySet()); // ORDINARE IN QUALE ORDINE?
		
		return list;
	}
	
	public Config getConfigurazione(String systemPAth, String name){
		return this.mapConfig.get(systemPAth).get(name);
	}
	
	
	
}
