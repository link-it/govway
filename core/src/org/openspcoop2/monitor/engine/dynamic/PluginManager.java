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

package org.openspcoop2.monitor.engine.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * PluginManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginManager {

	private IRegistroPluginsReader registroPluginsReader;
	
	private Date expireDate;
	private int expireSeconds;
	private final org.openspcoop2.utils.Semaphore lockExpire = new org.openspcoop2.utils.Semaphore("PluginManager-expire");
	
	private PluginsImage pluginsImage = new PluginsImage();
	private PluginsImage pluginsImageSwitchOld = null;
	private final org.openspcoop2.utils.Semaphore lockImage = new org.openspcoop2.utils.Semaphore("PluginManager-image");

	protected PluginManager(IRegistroPluginsReader registroPluginsReader, int expireSeconds) {
		this.registroPluginsReader = registroPluginsReader;
		this.expireSeconds = expireSeconds;
	}

	private void checkUpdate(Logger log) {
		try {
			Date nowDate = DateManager.getDate();
			if(this.expireDate==null || nowDate.after(this.expireDate)) {
				this.update(log, nowDate);
			}
		}catch(Exception t) {
			log.error("Update plugin image failed: "+t.getMessage(),t);
		}
	}
	public void updateFromConsoleConfig(Logger log) {
		try {
			if(this.expireDate!=null) {
				Date expired = new Date(this.expireDate.getTime()+1);
				this.update(log, expired);
			}
		}catch(Exception t) {
			log.error("Update plugin image failed: "+t.getMessage(),t);
		}
	}
	private void update(Logger log, Date nowDate) throws UtilsException, CoreException {
		
		boolean update = false;
		
		this.lockExpire.acquire("updateExpireDate");
		try {
			if(this.expireDate==null || nowDate.after(this.expireDate)) {
				this.expireDate = new Date(nowDate.getTime()+(this.expireSeconds*1000));
				update = true;
				// l'aggiornamento effettivo lo faccio fuori dal synchronized per non bloccare le chiamate a _findClass, intanto che l'immagine viene aggiornata
				// gli altri thread che entrano in questo metodo trovano la lor nowDate inferiore ad expireDate
			}
		}finally {
			this.lockExpire.release("updateExpireDate");
		}
		
		if(update) {
			RegistroPlugins registro = null;
			try {
				registro = this.registroPluginsReader.getRegistroPlugins();
			}catch (NotFoundException notFound) {
				log.debug(notFound.getMessage(),notFound);
			}
			this.update(log, registro);
		}
	}
	
	private void update(Logger log, RegistroPlugins pluginsParam) throws UtilsException {
		
		this.lockImage.acquire("update");
		try {
			
			RegistroPlugins plugins = null;
			if(pluginsParam!=null) {
				plugins = pluginsParam;
			}
			else {
				plugins = new RegistroPlugins();
			}
	
			// verifico prima quelli da eliminare
			List<String> pluginDaEliminare = new ArrayList<>();
			if(this.pluginsImage!=null && !this.pluginsImage.plugins.isEmpty()) {
				for (String pluginName : this.pluginsImage.plugins.keySet()) {
					boolean found = false;
					if(plugins.sizePluginList()>0) {
						for (RegistroPlugin pluginNew : plugins.getPluginList()) {
							if(pluginNew.getNome().equals(pluginName)) {
								found = true;
								break;
							}
						}
					}
					if(!found) {
						pluginDaEliminare.add(pluginName);
					}
				}
			}
			
			// Nuova immagine
			PluginsImage newImage = null;
			if(plugins.sizePluginList()>0) {
				newImage = new PluginsImage();
				HashMap<String, String> mapPosizioniToNomi = new HashMap<>();
				for (RegistroPlugin pluginNew : plugins.getPluginList()) {
					
					if(!StatoFunzionalita.ABILITATO.equals(pluginNew.getStato())) {
						continue;
					}
					if(pluginNew.sizeArchivioList()<=0) {
						continue; // vuoto
					}
					
					String posPad = StringUtils.leftPad(pluginNew.getPosizione()+"", 10);
					mapPosizioniToNomi.put(posPad, pluginNew.getNome());
					
					// check se esiste
					if(this.pluginsImage!=null && this.pluginsImage.plugins.containsKey(pluginNew.getNome())) {
						
						// check se e' stato aggiornato
						Plugin active = this.pluginsImage.plugins.get(pluginNew.getNome());
						if(pluginNew.getData().after(active.getDate())) {
							
							// Da aggiornare
							
							pluginDaEliminare.add(pluginNew.getNome()); // elimino nella vecchia immagine
							
							Plugin pluginNewInstance = null;
							try {
								pluginNewInstance = new Plugin(pluginNew);
							}catch(Exception e) {
								log.error("Errore durante l'istanziazione del plugin '"+pluginNew.getNome()+"': "+e.getMessage(),e);
							}
							if(pluginNewInstance!=null) {
								newImage.plugins.put(pluginNew.getNome(), pluginNewInstance);
							}
						
						}
						else {
							
							// non modificato
							newImage.plugins.put(pluginNew.getNome(), active);
							
						}
						
					}
					else {
						Plugin pluginNewInstance = null;
						try {
							pluginNewInstance = new Plugin(pluginNew);
						}catch(Exception e) {
							log.error("Errore durante l'istanziazione del plugin '"+pluginNew.getNome()+"': "+e.getMessage(),e);
						}
						if(pluginNewInstance!=null) {
							newImage.plugins.put(pluginNew.getNome(), pluginNewInstance);
						}
					}
					
				}
				
	
				// ordino per posizione
				if(!mapPosizioniToNomi.isEmpty()) {
					List<String> posizioni = new ArrayList<>();
					posizioni.addAll(mapPosizioniToNomi.keySet());
					Collections.sort(posizioni);
					for (String pos : posizioni) {
						newImage.pluginsActiveOrdered.add(mapPosizioniToNomi.get(pos));
					}
				}
			}
			
			// effettuo switch
			this.pluginsImageSwitchOld = this.pluginsImage;
			this.pluginsImage = newImage;
			
			// Effettuo la chiusura di quelli da eliminare
			if(!pluginDaEliminare.isEmpty()) {
				for (String pluginName : pluginDaEliminare) {
					try {
						this.pluginsImageSwitchOld.plugins.get(pluginName).close();
					}catch(Exception t) {
						// ignore
					}
				}
			}
			this.pluginsImageSwitchOld = null;
					
		}finally {
			this.lockImage.release("update");
		}
		
	}
	
	public void close() {
		
		// Chiusura di tutti
		this.lockImage.acquireThrowRuntime("close");
		try {
			
			if(this.pluginsImage!=null && this.pluginsImage.plugins.size()>0) {
				for (Plugin plugin : this.pluginsImage.plugins.values()) {
					try {
						plugin.close();
					}catch(Exception t) {
						// close
					}
				}
			}
			
			if(this.pluginsImageSwitchOld!=null &&
				this.pluginsImageSwitchOld.plugins.size()>0) {
				for (Plugin plugin : this.pluginsImageSwitchOld.plugins.values()) {
					try {
						plugin.close();
					}catch(Exception t) {
						// close
					}
				}
			}
			
		}finally {
			this.lockImage.release("close");
		}
	}
	

	public Class<?> findClass(Logger log, TipoPlugin tipoClasseDaRicercare, String className) throws ClassNotFoundException {
		return this.findClassEngine(log, tipoClasseDaRicercare, null, className, true);
	}
	public Class<?> findClass(Logger log, TipoPlugin tipoClasseDaRicercare, String className, boolean searchDefaultClassLoader) throws ClassNotFoundException {
		return this.findClassEngine(log, tipoClasseDaRicercare, null, className, searchDefaultClassLoader);
	}
	
	public Class<?> findClass(Logger log, String tipoClasseDaRicercare, String className) throws ClassNotFoundException {
		return this.findClassEngine(log, null, tipoClasseDaRicercare, className, true);
	}
	public Class<?> findClass(Logger log, String tipoClasseDaRicercare, String className, boolean searchDefaultClassLoader) throws ClassNotFoundException {
		return this.findClassEngine(log, null, tipoClasseDaRicercare, className, searchDefaultClassLoader);
	}
	
	
	
	private Class<?> findClassEngine(Logger log, TipoPlugin tipoClasseDaRicercare,String tipoClasseCustomDaRicercare, String className, boolean searchDefaultClassLoader) throws ClassNotFoundException {

		checkUpdate(log);
		
		// Se server gestire tramite una cache

		// Se abilitato prima cerco sempre nel classloader attuale.
		// Il classloader dinamico verrà utilizzato SOLAMENTE se il tipo non viene risolto prima tramite i meccanismi standard. 
		// Questo behaviour permette di avere poi un synchronized sul semaforo del classloader dinamico solamente quando serve davvero, 
		// visto che il caricamento dinamico delle classi è molto diffuso all'interno dell'architettura di GovWay.
		ClassNotFoundException notFound = null;
		if(searchDefaultClassLoader) {
			try {
				return Class.forName(className);
			}catch(ClassNotFoundException e) {
				notFound = e;
			}
		}
		
		if(this.pluginsImage!=null) { // potrebbe essere disabilitato
			PluginsImage image = this.pluginsImage; // lo assegno, in modo che se avviene un update, cambia il riferimento
			
			if(!image.pluginsActiveOrdered.isEmpty()) {
				Class<?> c = findClassEngine(image, tipoClasseDaRicercare, tipoClasseCustomDaRicercare, className);
				if(c!=null) {
					return c;
				}
			}
		}
		
		if(notFound!=null) {
			throw notFound;
		}
		return null;
	}
	private Class<?> findClassEngine(PluginsImage image, TipoPlugin tipoClasseDaRicercare, String tipoClasseCustomDaRicercare, String className){
		List<String> listPluginsActiveOrdered = image.pluginsActiveOrdered;
		for (String pluginName : listPluginsActiveOrdered) {
			
			Plugin plugin = image.plugins.get(pluginName);
			if(plugin!=null) {
				Class<?> c = findClassEngineByPlugin(plugin, tipoClasseDaRicercare, tipoClasseCustomDaRicercare, className);
				if(c!=null) {
					return c;
				}
			}
			
		}
		return null;
	}
	private Class<?> findClassEngineByPlugin(Plugin plugin, TipoPlugin tipoClasseDaRicercare, String tipoClasseCustomDaRicercare, String className){
		ClassLoader classLoader = null;
		if(tipoClasseDaRicercare!=null) {
			classLoader = plugin.getClassLoader(tipoClasseDaRicercare);
		}else {
			classLoader = plugin.getClassLoader(tipoClasseCustomDaRicercare);
		}
		if(classLoader!=null) {
			Class<?> c = null;
			try {
				c = classLoader.loadClass(className);
			}catch(ClassNotFoundException cNotFound) {
				// ignore
			}
			if(c!=null) {
				return c;
			}
		}
		return null;
	}

}

class PluginsImage {
	
	HashMap<String, Plugin> plugins = new HashMap<>();
	List<String> pluginsActiveOrdered = new ArrayList<>();
	
}
