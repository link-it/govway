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



package org.openspcoop2.web.ctrlstat.config;


import java.util.ArrayList;
import java.util.List;
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


public class DatasourceProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'console.datasource.properties' */
	private DatasourceInstanceProperties reader;

	/** Copia Statica */
	private static DatasourceProperties datasourceProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private DatasourceProperties(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(DatasourceProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DatasourceProperties.class.getResourceAsStream("/console.datasource.properties");
			if(properties==null){
				throw new Exception("File '/console.datasource.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'console.datasource.properties': \n\n"+e.getMessage());
		    throw new Exception("ConsoleProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){
		    	// close
		    }
		}

		this.reader = new DatasourceInstanceProperties(propertiesReader, this.log, confDir, confPropertyName, confLocalPathPrefix);
	}
	private DatasourceProperties(Properties properties) throws Exception {
		this.reader = new DatasourceInstanceProperties(properties, this.log, "undefined", "undefined", "undefined");
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized boolean initialize(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log){

		try {
			if(DatasourceProperties.datasourceProperties==null){
				DatasourceProperties.datasourceProperties = new DatasourceProperties(confDir,confPropertyName,confLocalPathPrefix,log);
			}
		    return true;
		}
		catch(Exception e) {
			log.error("Inizializzazione fallita: "+e.getMessage(),e);
		    return false;
		}
	}
	public static synchronized boolean initialize(Properties properties,Logger log){
		try {
			if(DatasourceProperties.datasourceProperties==null){
				DatasourceProperties.datasourceProperties = new DatasourceProperties(properties);
			}
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
	public static DatasourceProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(DatasourceProperties.datasourceProperties==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (DatasourceProperties.class) {
				throw new OpenSPCoop2ConfigurationException("DatasourceProperties non inizializzato");
			}
	    }
	    return DatasourceProperties.datasourceProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		DatasourceProperties.datasourceProperties.reader.setLocalObjectImplementation(prop);
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
	private Boolean readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp);
	}

	

	
	/* ----- Database della Console -------- */
	
	public String getDataSource() throws UtilsException{
		return this.readProperty(true, "dataSource");
	}
	
	public Properties getDataSourceContext() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("dataSource.property.");
	}
	
	public String getTipoDatabase() throws UtilsException{
		return this.readProperty(true, "tipoDatabase");
	}
	
	
	/* ----- Database di Monitoraggio ------- */
	
	public List<String> getSinglePddMonitorSorgentiDati() throws UtilsException{
		List<String> l = new ArrayList<>();
		String p = this.readProperty(false, "singlePdD.monitor.sorgentiDati");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
				l.add(tmp[i]);
			}
		}
		return l;
	}
	
	public String getSinglePddMonitorLabel(String source) throws UtilsException{
		return this.readProperty(true, "singlePdD.monitor."+source+".label");
	}
	
	public String getSinglePddMonitorDataSource(String source) throws UtilsException{
		return this.readProperty(true, "singlePdD.monitor."+source+".dataSource");
	}
	
	public Properties getSinglePddMonitorDataSourceContext(String source) throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("singlePdD.monitor."+source+".dataSource.property.");
	}
	
	public String getSinglePddMonitorTipoDatabase(String source) throws UtilsException{
		return this.readProperty(true, "singlePdD.monitor."+source+".tipoDatabase");
	}
	
	
	/* ----- Database di Tracciamento ------- */
		
	public Boolean isSinglePddTracceStessoDBConsole() throws UtilsException{
		return this.readBooleanProperty(true, "singlePdD.tracce.sameDBWebUI");
	}
	
	public String getSinglePddTracceDataSource() throws UtilsException{
		return this.readProperty(true, "singlePdD.tracce.dataSource");
	}
	
	public Properties getSinglePddTracceDataSourceContext() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("singlePdD.tracce.dataSource.property.");
	}
	
	public String getSinglePddTracceTipoDatabase() throws UtilsException{
		return this.readProperty(true, "singlePdD.tracce.tipoDatabase");
	}
	
	
	/* ----- Database dei Messaggi Diagnostici ------- */
	
	public Boolean isSinglePddMessaggiDiagnosticiStessoDBConsole() throws UtilsException{
		return this.readBooleanProperty(true, "singlePdD.msgDiagnostici.sameDBWebUI");
	}
	
	public String getSinglePddMessaggiDiagnosticiDataSource() throws UtilsException{
		return this.readProperty(true, "singlePdD.msgDiagnostici.dataSource");
	}
	
	public Properties getSinglePddMessaggiDiagnosticiDataSourceContext() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("singlePdD.msgDiagnostici.dataSource.property.");
	}
	
	public String getSinglePddMessaggiDiagnosticiTipoDatabase() throws UtilsException{
		return this.readProperty(true, "singlePdD.msgDiagnostici.tipoDatabase");
	}
	
	

}
