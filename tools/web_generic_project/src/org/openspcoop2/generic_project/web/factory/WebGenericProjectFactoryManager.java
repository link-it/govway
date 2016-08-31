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
package org.openspcoop2.generic_project.web.factory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.web.core.Utils;
import org.openspcoop2.generic_project.web.logging.LoggerManager;
import org.openspcoop2.utils.resources.MapReader;

/***
 * 
 *  Manager per gestire le implementazioni della libreria utilizzabili nelle webapp.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public class WebGenericProjectFactoryManager {

	private static WebGenericProjectFactoryManager instance;
	private Logger log = null;
	private MapReader<String, WebGenericProjectFactory> factories = null;
	private MapReader<String, URL> propertiesURLs = null;


	private static synchronized void init(Logger log) throws FactoryException{
		WebGenericProjectFactoryManager.instance = new WebGenericProjectFactoryManager(log);
	}

	public static WebGenericProjectFactoryManager getInstance(Logger log) throws FactoryException{
		if(WebGenericProjectFactoryManager.instance == null)
			init(log);

		return WebGenericProjectFactoryManager.instance;
	}

	public static WebGenericProjectFactoryManager getInstance() throws FactoryException{
		try{
			Logger log = LoggerManager.getWebGenericProjectLogger();

			if(WebGenericProjectFactoryManager.instance == null)
				init(log);
		}catch(FactoryException e){
			throw e;
		}catch(Exception e){
			throw new FactoryException(e);
		}

		return WebGenericProjectFactoryManager.instance;
	}

	public WebGenericProjectFactoryManager(Logger log) throws FactoryException{
		this.log = log;

		try{
			this.log.info("Inizializzazione Web GenericProject Factory in corso...");

			Hashtable<String, WebGenericProjectFactory> tmp_factories = new Hashtable<String, WebGenericProjectFactory>();
			Hashtable<String, URL> tmp_propertiesURLs = new Hashtable<String, URL>();
			// 1. Cerco nel classloader (funziona per jboss5.x)
			Enumeration<URL> en = WebGenericProjectFactoryManager.class.getClassLoader().getResources("/"+Costanti.WEB_GENERIC_PROJECT_FACTORY_IMPL_PROPERTIES);
			while(en.hasMoreElements()){
				URL propertiesURL = en.nextElement();
				loadProperties(propertiesURL,false,tmp_factories,tmp_propertiesURLs);
			}

			if(tmp_factories.size()<=0){
				// 2. (funziona per jboss4.x) ma vengono forniti jar duplicati, quelli dentro ear e quelli dentro tmp.
				en = WebGenericProjectFactoryManager.class.getClassLoader().getResources(Costanti.WEB_GENERIC_PROJECT_FACTORY_IMPL_PROPERTIES);
				while(en.hasMoreElements()){
					URL propertiesURL = en.nextElement();
					loadProperties(propertiesURL,true,tmp_factories,tmp_propertiesURLs);
				}
			}

			// Se non ho trovato il file di properties non posso utilizzare la libreria
			if(tmp_factories.size()<=0){
				throw new Exception("Impossibile trovare un implementazione valida della libreria.");
			}

			this.factories = new MapReader<String, WebGenericProjectFactory>(tmp_factories,true);
			this.propertiesURLs = new MapReader<String, URL>(tmp_propertiesURLs,true);
			this.log.info("Inizializzazione Web GenericProject Factory completata.");
		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante l'inizializzazione della factory: " + e.getMessage(), e);
			throw new FactoryException("Si e' verificato un errore durante l'inizializzazione della factory: " + e.getMessage(), e);
		}
	}

	private void loadProperties(URL propertiesUrl, boolean filtraSenzaErroreFactoryGiaCaricato, 
			Hashtable<String, WebGenericProjectFactory> tmp_factories,
			Hashtable<String, URL> tmp_propertiesURLs) throws Exception{

		this.log.debug("Analyze Properties ["+propertiesUrl.toString()+"] ...");

		InputStream propertiesStream = null;
		Properties propertiesReader = new Properties();
		try{
			propertiesStream = propertiesUrl.openStream();
			propertiesReader.load(propertiesStream);

		}finally{
			try{
				propertiesStream.close();
			}catch(Exception e){}
		}

		// Leggo le informazioni relative alla classe da instanziare
		String factoryClassName = Utils.getProperty(propertiesReader, Costanti.IMPL_FACTORY_CLASS, true);

		this.log.debug("Caricamento classe ["+factoryClassName+"] in corso...");
		WebGenericProjectFactory factory = loadFactory(factoryClassName);

		String factoryName = factory.getFactoryName();
		this.log.debug("Caricamento classe ["+factoryClassName+"] completato, trovata Factory ["+factoryName+"].");

		if(tmp_factories.containsKey(factoryName)){

			URL urlGiaPresente = tmp_propertiesURLs.get(factoryName);
			if(filtraSenzaErroreFactoryGiaCaricato){
				this.log.warn("Factory ["+factoryName+"] e' la stessa per piu' implementazioni ["+propertiesUrl.toString()
						+"] and ["+urlGiaPresente.toURI()+"]");
			}
			else{
				throw new Exception("Factory ["+factoryName+"] e' la stessa per piu' implementazioni ["+propertiesUrl.toString()+
						"] and ["+urlGiaPresente.toURI()+"]");
			}

		}
		tmp_factories.put(factoryName, factory);
		tmp_propertiesURLs.put(factoryName, propertiesUrl);
		this.log.debug("Analyze Properties ["+propertiesUrl.toString()+"] with success");

	}

	public WebGenericProjectFactory loadFactory(String factoryClass) throws Exception {

		try{
			Class<?> c = Class.forName(factoryClass);
			Constructor<?> constructor = c.getConstructor(Logger.class);
			WebGenericProjectFactory p = (WebGenericProjectFactory) constructor.newInstance(this.log);
			return  p;
		} catch (Exception e) {
			throw new FactoryException("Impossibile caricare la factory indicata ["+factoryClass+"] " + e, e);
		}
	}

	public WebGenericProjectFactory getWebGenericProjectFactoryByName(String factoryName) throws FactoryException {
		if(this.factories.containsKey(factoryName)){
			return this.factories.get(factoryName);
		}
		else{
			throw new FactoryException("WebGenericProjectFactory with name ["+factoryName+"] not found");
		}
	}

	public Properties getFactoryProperties(String factoryName) throws Exception {

		this.log.debug("Caricamento delle properties per la factory ["+factoryName+"] in corso...");

		URL propertiesUrl = this.propertiesURLs.get(factoryName);

		InputStream propertiesStream = null;
		Properties propertiesReader = new Properties();
		try{
			propertiesStream = propertiesUrl.openStream();
			propertiesReader.load(propertiesStream);

		}finally{
			try{
				propertiesStream.close();
			}catch(Exception e){}
		}

		this.log.debug("Caricamento delle properties per la factory ["+factoryName+"] completato.");

		return propertiesReader;
	}
	
	public WebGenericProjectFactory getDefaultFactory() throws FactoryException {
		if(this.factories.size() > 0 ){
			Enumeration<String> keys = this.factories.keys();
			String factoryName = keys.nextElement();
			return this.factories.get(factoryName);
		}
		else{
			throw new FactoryException("Default WebGenericProjectFactory not found");
		}
	}
}
