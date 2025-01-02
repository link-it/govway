/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.web.lib.queue.config;


import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
* ConsoleProperties
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/


public class QueueProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'queue.properties' */
	private QueueInstanceProperties reader;

	/** Copia Statica */
	private static QueueProperties queueProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private QueueProperties(String confDir,Logger log) throws UtilsException {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(QueueProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = QueueProperties.class.getResourceAsStream("/queue.properties");
			if(properties==null){
				throw new UtilsException("File '/queue.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			doError(e);
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){
		    	// close
		    }
		}

		this.reader = new QueueInstanceProperties(propertiesReader, this.log, confDir);
	}

	private void doError(Exception e) throws UtilsException {
		String msg = "Riscontrato errore durante la lettura del file 'queue.properties': "+e.getMessage();
		this.log.error(msg,e);
	    throw new UtilsException("ConsoleProperties initialize error: "+e.getMessage());
	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    QueueProperties.queueProperties = new QueueProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Inizializzazione fallita: "+e.getMessage(),e);
			return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static QueueProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(QueueProperties.queueProperties==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (QueueProperties.class) {
				throw new OpenSPCoop2ConfigurationException("QueueProperties non inizializzato");
			}
	    }
	    return QueueProperties.queueProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		QueueProperties.queueProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValueConvertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}
	private Integer readIntegerProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}

	

	
	public String getConnectionFactory() throws UtilsException{
		return this.readProperty(true, "ConnectionFactory");
	}
	
	public Properties getConnectionFactoryContext() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("ConnectionFactory.property.");
	}
	
	public Integer getWaitTime() throws UtilsException{
		return this.readIntegerProperty(true, "WAIT_TIME");
	}
	
	
	

}
