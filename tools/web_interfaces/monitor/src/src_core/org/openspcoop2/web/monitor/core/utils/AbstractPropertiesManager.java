package org.openspcoop2.web.monitor.core.utils;

import org.openspcoop2.core.commons.dao.DAOFactoryInstanceProperties;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.openspcoop2.utils.properties.CollectionProperties;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.slf4j.Logger;

public class AbstractPropertiesManager {
	
	//private  HashMap<String, String> map = null;
	Properties map = null;
		
	/**
	 * Inizializza la mappa delle property
	 * legge il default resource bundle 'bundleName'
	 * dopodiche effettua l'ovverride delle properties eventualmente definite all'esterno dell'applicazione
	 * utilizzando lo stesso algoritmo di lookup di openspcoop
	 */
	protected AbstractPropertiesManager(String bundleName,Logger log,String variabile,String nomeFile){
		this(bundleName, log, variabile, nomeFile, null, Locale.getDefault());
	}
	protected AbstractPropertiesManager(String bundleName,Logger log,String variabile,String nomeFile,Locale locale){
		this(bundleName, log, variabile, nomeFile, null, locale);
	}
	protected AbstractPropertiesManager(String bundleName,Logger log,String variabile,String nomeFile,String confDir){
		this(bundleName, log, variabile, nomeFile, confDir, Locale.getDefault());
	}
	protected AbstractPropertiesManager(String bundleName,Logger log,String variabile,String nomeFile,String confDir,Locale locale){
		ResourceBundle bundle = null;
		try {
			this.map = new Properties();
			
			//per prima cosa leggo il bundle per leggere i valori di default
			bundle = ResourceBundle.getBundle(bundleName, locale, AbstractPropertiesManager.getCurrentLoader(bundleName));
			if(confDir==null){
				try {
					confDir = bundle.getString("confDirectory");
				} catch (MissingResourceException e) {
					// bundle with this name not found;
					log.warn("Impossibile trovare il bundle for base name ["+bundleName+"] Locale["+locale+"]",e);
				}		
			}
			
			//popolo la map
			Enumeration<String> en = bundle.getKeys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				this.map.put(key, bundle.getString(key));
			}
			
			//leggo le properties eventualmente ridefinite
			CollectionProperties prop = PropertiesUtilities.searchLocalImplementation(DAOFactoryInstanceProperties.OPENSPCOOP2_LOCAL_HOME,log, variabile, nomeFile, confDir);
			if(prop!=null){
				Enumeration<?> proEnums = prop.keys();
				while (proEnums.hasMoreElements()) {
					String key = (String) proEnums.nextElement();
					//effettuo l'ovverride
					this.map.put(key, prop.getProperty(key));
				}
			}
		} catch (MissingResourceException e) {
			// bundle with this name not found;
			log.error("Impossibile trovare il bundle for base name ["+bundleName+"] Locale["+locale+"]",e);
		}
	}
	
	private static ClassLoader getCurrentLoader(Object fallbackClass) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = fallbackClass.getClass().getClassLoader();
		return loader;
	}
	
	public String getMessageFromResourceBundle(String key) {
		return (String)this.map.get(key);

	}
	
	public String getProperty(String key){
		return (String)this.map.get(key);
	}
	public String getProperty(String key,String defaultValue){
		return this.map.getProperty(key, defaultValue);
	}
	public Properties getProperties(){
		return this.map;
	}
}
