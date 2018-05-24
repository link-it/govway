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
package org.openspcoop2.core.mvc.properties.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.serializer.JaxbDeserializer;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
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
	private AbstractValidatoreXSD validator = null; 
	
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
			this.validator = XSDValidator.getXSDValidator(log);
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
								this.validator.valida(fileConfig);
							}catch(Exception e) {
								this.log.error("La configurazione ["+fileConfig.getName()+"] non e' valida: " + e.getMessage());
								throw e;
							}
						}
						
						Config configDaFile = xmlReader.readConfig(fileConfig);
						String id = configDaFile.getId();
						if(mapConfigFromDir.containsKey(id))
							throw new Exception("La configurazione con id '"+id+"' risulta duplicata all'interno del path indicato ["+systemPath+"].");
							
						mapConfigFromDir.put(id,configDaFile);
					}
				}
				this.mapConfig.put(systemPath, mapConfigFromDir);
			}
		}
		
//		return this.mapConfig.get(systemPath);
	}
	
	public List<String> getNomiConfigurazioni(String systemPath, String ... tags) {
		List<String> mapSortedKeys = new ArrayList<>();
		Map<String, List<String>> mapSorted = new HashMap<>();
				
		Map<String, Config> map = this.mapConfig.get(systemPath);

		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			
			Config config = map.get(id);
			if(tags!=null && tags.length>0) {
				// TODO FILTRARE IN BASE AI TAGS 
				continue;
			}
			
			String labelId = config.getLabel();
			if(config.getSortLabel()!=null) {
				labelId = config.getSortLabel();
			}
			
			List<String> l = null;
			if(mapSorted.containsKey(labelId)) {
				l = mapSorted.get(id);
			}
			else {
				l = new ArrayList<>();
				mapSorted.put(labelId, l);
				mapSortedKeys.add(labelId);
			}
			l.add(id);
			
		}
		
		Collections.sort(mapSortedKeys);
		List<String> list = new ArrayList<>();
		for (String labelId : mapSortedKeys) {
			List<String> l = mapSorted.get(labelId);
			list.addAll(l);
		}
		return list; // lista contenente gli id
	}
	
	public List<String> convertToLabel(String systemPath, List<String> idList){
	
		Map<String, Config> map = this.mapConfig.get(systemPath);
		List<String> listLabels = new ArrayList<>();
		for (String id : idList) {
			Config config = map.get(id);
			String labelId = config.getLabel();
			if(config.getSortLabel()!=null) {
				labelId = config.getSortLabel();
			}
			listLabels.add(labelId);
		}
		return listLabels;
	}
	
	public Config getConfigurazione(String systemPath, String name){
		return this.mapConfig.get(systemPath).get(name);
	}
	
	
	
}
