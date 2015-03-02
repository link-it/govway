package org.openspcoop2.core.tracciamento.ws.server.config;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openspcoop2.utils.UtilsException;

/**     
 * ServerProperties
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;
	
	/** Reader delle proprieta' impostate nel file 'wstracciamento.properties' */
	private ServerInstanceProperties reader;
	
	/** Copia Statica */
	private static ServerProperties serverProperties = null;
	
	
	public ServerProperties(String confDir,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = Logger.getLogger(ServerProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = DatasourceProperties.class.getResourceAsStream("/wstracciamento.properties");
			if(properties==null){
				throw new Exception("File '/wstracciamento.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'wstracciamento.properties': \n\n"+e.getMessage());
		    throw new Exception("WSMonitorProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}

		this.reader = new ServerInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    ServerProperties.serverProperties = new ServerProperties(confDir,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del ServerProperties: "+e.getMessage(),e);		   
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di Properties
	 * 
	 */
	public static ServerProperties getInstance() throws UtilsException{
		if(ServerProperties.serverProperties==null){
	    	throw new UtilsException("ServerProperties non inizializzato");
	    }
	    return ServerProperties.serverProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		ServerProperties.serverProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	public String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValue_convertEnvProperties(property);
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
	
	public Properties getProperties() throws UtilsException{
		Properties p = new Properties();
		Enumeration<?> names = this.reader.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			p.put(name, this.reader.getValue_convertEnvProperties(name));
		}
		return p;
	}

}