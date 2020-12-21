/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.monitor.engine.alarm;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;


/**
 * AlarmConfigProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmConfigProperties {

	
	/** Copia Statica */
	private static AlarmConfigProperties alarmConfigProperties = null;
	private static AlarmEngineConfig alarmEngineConfig = null;

	public static AlarmEngineConfig getAlarmConfiguration(org.slf4j.Logger log) throws Exception{
		return getAlarmConfiguration(log, null);
	}
	public static AlarmEngineConfig getAlarmConfiguration(org.slf4j.Logger log,String filePath) throws Exception{

		if(AlarmConfigProperties.alarmConfigProperties==null){
			return _getAlarmConfiguration(log, filePath);
		}else{
			return AlarmConfigProperties.alarmEngineConfig;
		}
	}
	
	private static synchronized AlarmEngineConfig _getAlarmConfiguration(org.slf4j.Logger log,String filePath) throws Exception{

		if(AlarmConfigProperties.alarmConfigProperties==null){
			AlarmConfigProperties.alarmConfigProperties = new AlarmConfigProperties(log,filePath);
			AlarmConfigProperties.alarmEngineConfig = AlarmEngineConfig.readAlarmEngineConfig(log, alarmConfigProperties);
		}

		return AlarmConfigProperties.alarmEngineConfig;
		
	}
	
	
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'alarmConfig.properties' */
	private PropertiesReader reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public AlarmConfigProperties(org.slf4j.Logger log,String filePat) throws EngineException{

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties p = new Properties();
		java.io.InputStream properties = null;
		try{  
			File f = new File(filePat);
			if(f.exists()){
				properties = new FileInputStream(f);
			}
			else{
				properties = AlarmConfigProperties.class.getResourceAsStream(filePat);
				if(properties==null && !filePat.startsWith("/")){
					properties = AlarmConfigProperties.class.getResourceAsStream("/"+filePat);
				}
			}
			if(properties==null){
				throw new Exception("Properties "+filePat+" not found");
			}
			p.load(properties);
			properties.close();
		}catch(Exception e) {
			log.error("Riscontrato errore durante la lettura del file '"+filePat+"': "+e.getMessage(),e);
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new EngineException(e.getMessage(),e);
		}	
	
		try{
			this.reader = new PropertiesReader(p, true);
		}catch(Exception e){
			throw new EngineException(e.getMessage(),e);
		}
		
	}

	
	
	
	
	/* ********  P R O P E R T I E S  ******** */

	public String getProperty(String name,String defaultValue, boolean convertEnvProperty) throws UtilsException{
		String tmp = null;
		if(convertEnvProperty){
			tmp = this.reader.getValue_convertEnvProperties(name);
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
			tmp = this.reader.getValue_convertEnvProperties(name);
		}else{
			tmp = this.reader.getValue(name);
		}
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+name+"] not found");
			}
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}
	
}
