/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.mvc.properties.Compatibility;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.Tags;
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
	private Map<String, Map<String,Config>> mapConfigBuildIn = null;
	private Map<String, Map<String,Config>> mapConfigFileSystem = null;
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
		this.mapConfigBuildIn = new HashMap<String, Map<String,Config>>();
		this.mapConfigFileSystem = new HashMap<String, Map<String,Config>>();
		this.mapConfig = new HashMap<String, Map<String,Config>>();
		
		try {
			this.validator = XSDValidator.getXSDValidator(log);
		}catch(Exception e) {
			this.log.error("Errore durante la init del ManagerConfigurazioni: " + e.getMessage(),e);
			throw e;
		}
	}
	
	
	public void leggiConfigurazioni(PropertiesSourceConfiguration propertiesSourceConfiguration, boolean validazioneXSD) throws Exception{
		// Configurazioni builtIn
		if(!this.mapConfigBuildIn.containsKey(propertiesSourceConfiguration.getId()) || propertiesSourceConfiguration.isUpdateBuiltIn()) {
			if(this.mapConfigBuildIn.containsKey(propertiesSourceConfiguration.getId())) {
				this.mapConfigBuildIn.remove(propertiesSourceConfiguration.getId());
			}
			
			Map<String,Config> mapConfigFromDir = null;
			
			List<byte[]> builtIn = propertiesSourceConfiguration.getBuiltIn();
			
			if(builtIn != null && builtIn.size() > 0) {
				mapConfigFromDir = new HashMap<String,Config>();
				JaxbDeserializer xmlReader = new JaxbDeserializer();
				for (int i = 0 ; i < builtIn.size(); i++ ) {
					byte[] f = builtIn.get(i);
					// validazione XSD se prevista
					if(validazioneXSD) {
						try {
							this.validator.valida(new ByteArrayInputStream(f));
						}catch(Exception e) {
							this.log.error("La configurazione builtIn numero ["+(i+1)+"] non e' valida: " + e.getMessage());
							throw e;
						}
					}
					
					Config configDaFile = xmlReader.readConfig(f);
					String id = configDaFile.getId();
					if(mapConfigFromDir.containsKey(id))
						throw new Exception("La configurazione builtIn con id '"+id+"' risulta duplicata all'interno di quelle precaricate nel sistema.");
						
					mapConfigFromDir.put(id,configDaFile);
				}
				this.mapConfigBuildIn.put(propertiesSourceConfiguration.getId(), mapConfigFromDir);
			}
		}
		
		// configurazioni da file system
		if(propertiesSourceConfiguration.getDirectory() != null && (!this.mapConfigFileSystem.containsKey(propertiesSourceConfiguration.getId()) || propertiesSourceConfiguration.isUpdate())) {
			if(this.mapConfigFileSystem.containsKey(propertiesSourceConfiguration.getId())) {
				this.mapConfigFileSystem.remove(propertiesSourceConfiguration.getId());
			}
			
			Map<String,Config> mapConfigFromDir = null;
			
			File dir = new File(propertiesSourceConfiguration.getDirectory());
			
			if(!dir.exists())
				throw new Exception("Il path indicato ["+propertiesSourceConfiguration.getDirectory()+"] non esiste, impossibile leggere le configurazioni");
			
			if(!dir.isDirectory())
				throw new Exception("Il path indicato ["+propertiesSourceConfiguration.getDirectory()+"] non e' una directory");
			
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
							throw new Exception("La configurazione con id '"+id+"' risulta duplicata all'interno del path indicato ["+propertiesSourceConfiguration.getDirectory()+"].");
							
						mapConfigFromDir.put(id,configDaFile);
					}
				}
				this.mapConfigFileSystem.put(propertiesSourceConfiguration.getId(), mapConfigFromDir);
			}
		}
		
		// merge configurazioni
		if(!this.mapConfig.containsKey(propertiesSourceConfiguration.getId()) || propertiesSourceConfiguration.isUpdateBuiltIn() || propertiesSourceConfiguration.isUpdate()) {
			if(this.mapConfig.containsKey(propertiesSourceConfiguration.getId())) {
				this.mapConfig.remove(propertiesSourceConfiguration.getId());
			}
			
			Map<String, Config> mapBuiltIn = this.mapConfigBuildIn.get(propertiesSourceConfiguration.getId());
			Map<String, Config> mapFileSystem = this.mapConfigFileSystem.get(propertiesSourceConfiguration.getId());
			
			if(mapBuiltIn != null && mapBuiltIn.size() > 0 && mapFileSystem != null && mapFileSystem.size() > 0 ) {
				// controllo duplicati 
				for (String keyBuiltIn : mapBuiltIn.keySet()) {
					if(mapFileSystem.keySet().contains(keyBuiltIn))
						throw new Exception("La configurazione con id '"+keyBuiltIn+"' risulta duplicata, e' presente sia come configurazione builtIn che come configurazione esterna, eliminare una delle due.");
				}
			}
			
			Map<String, Config> map = new HashMap<String,Config>();
			if(mapBuiltIn != null && mapBuiltIn.size() > 0)
				map.putAll(mapBuiltIn);
			if(mapFileSystem != null && mapFileSystem.size() > 0)
				map.putAll(mapFileSystem);
			
			this.mapConfig.put(propertiesSourceConfiguration.getId(), map);
		}
	}
	
	public List<String> getNomiConfigurazioni(PropertiesSourceConfiguration propertiesSourceConfiguration, String ... tags) {
		List<String> mapSortedKeys = new ArrayList<>();
		Map<String, List<String>> mapSorted = new HashMap<>();
				
		Map<String, Config> map = this.mapConfig.get(propertiesSourceConfiguration.getId());

		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			
			Config config = map.get(id);
			if(tags!=null && tags.length>0) {
				Compatibility compatibility = config.getCompatibility();
				// se non e' compatibile con i tag richiesti non lo aggiungo alla lista
				if(!checkCompatibility(compatibility, Arrays.asList(tags))) 
					continue;
			}
			
			String labelId = config.getLabel();
			if(config.getSortLabel()!=null) {
				labelId = config.getSortLabel();
			}
			
			List<String> l = null;
			if(mapSorted.containsKey(labelId)) {
				l = mapSorted.get(labelId);
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
	
	public List<String> convertToLabel(PropertiesSourceConfiguration propertiesSourceConfiguration, List<String> idList){
	
		Map<String, Config> map = this.mapConfig.get(propertiesSourceConfiguration.getId());
		List<String> listLabels = new ArrayList<>();
		for (String id : idList) {
			Config config = map.get(id);
			String labelId = config.getLabel();
//			if(config.getSortLabel()!=null) {
//				labelId = config.getSortLabel();
//			}
			listLabels.add(labelId);
		}
		return listLabels;
	}
	
	public Config getConfigurazione(PropertiesSourceConfiguration propertiesSourceConfiguration, String name){
		return this.mapConfig.get(propertiesSourceConfiguration.getId()).get(name);
	}
	
	public boolean checkCompatibility(Compatibility compatibility, List<String> tags) {
		if(compatibility == null)
			return true;
		
		boolean isAnd = compatibility.getAnd(); 
		boolean isNot = compatibility.getNot();
		
		// Valore di partenza dell'esito totale e'
		// TRUE se devo controllare l'and dei tag
		// FALSE se devo controllare l'or dei tag
		boolean esito = isAnd ? true : false;

		for (Tags tag : compatibility.getTagsList()) {
			boolean resCondition = checkTags(tag,tags);

			// aggiorno l'esito in base all'operazione da aggregare AND o OR
			esito = isAnd ? (esito && resCondition) : (esito || resCondition);
		}

		// eventuale NOT della condizione
		return isNot ? !esito : esito;
	}

	private boolean checkTags(Tags tag, List<String> tags) {
		boolean isAnd = tag.getAnd(); 
		boolean isNot = tag.getNot();
		
		// Valore di partenza dell'esito totale e'
		// TRUE se devo controllare l'and dei tag
		// FALSE se devo controllare l'or dei tag
		boolean esito = isAnd ? true : false;
		
		for (String stringTag : tag.getTagList()) {
			boolean resCondition = tags.contains(stringTag);
			// aggiorno l'esito in base all'operazione da aggregare AND o OR
			esito = isAnd ? (esito && resCondition) : (esito || resCondition);
		}
		
		// eventuale NOT della condizione
		return isNot ? !esito : esito;
	}
	
}
