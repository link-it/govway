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
package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.monitor.engine.constants.CostantiConfigurazione;
import org.openspcoop2.monitor.engine.exceptions.EngineException;

import java.util.Properties;

import org.openspcoop2.utils.UtilsException;


/**
 * MonitorProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitorProperties {

	
	/** Copia Statica */
	private static MonitorProperties monitorPropertiesStaticInstance = null;

	private static synchronized void initialize(org.slf4j.Logger log) throws EngineException {

		if(MonitorProperties.monitorPropertiesStaticInstance==null)
			MonitorProperties.monitorPropertiesStaticInstance = new MonitorProperties(log);	

	}

	public static MonitorProperties getInstance(org.slf4j.Logger log) throws EngineException{

		if(MonitorProperties.monitorPropertiesStaticInstance==null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (MonitorProperties.class) {
				MonitorProperties.initialize(log);
			}
		}

		return MonitorProperties.monitorPropertiesStaticInstance;
	}
	
	
	
	
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'server.properties' */
	private MonitorInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private MonitorProperties(org.slf4j.Logger log) throws EngineException{

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = MonitorProperties.class.getResourceAsStream("/"+CostantiConfigurazione.CONFIG_FILENAME);
			if(properties==null){
				throw new EngineException("Properties "+CostantiConfigurazione.CONFIG_FILENAME+" not found");
			}
			propertiesReader.load(properties);
			properties.close();
		}catch(Exception e) {
			doError(log, properties, e);
		}	
	
		try{
			this.reader = new MonitorInstanceProperties(propertiesReader, log);
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
		
	}
	private void doError(org.slf4j.Logger log, java.io.InputStream properties, Exception e) throws EngineException {
		log.error("Riscontrato errore durante la lettura del file '"+CostantiConfigurazione.CONFIG_FILENAME+"': "+e.getMessage(),e);
		try{
			if(properties!=null)
				properties.close();
		}catch(Exception er){
			// close
		}
		throw new EngineException(e.getMessage(),e);
	}
	
	
	
	
	/* ********  P R O P E R T I E S  ******** */

	public String getProperty(String name,String defaultValue, boolean convertEnvProperty) throws UtilsException{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValueConvertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null){
			return defaultValue;
		}
		else{
			return tmp.trim();
		}
	}
	public String getProperty(String name,boolean required, boolean convertEnvProperty) throws UtilsException{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValueConvertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null &&
				required){
			throw new UtilsException("Property ["+name+"] not found");
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}
	
}
