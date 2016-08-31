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

package org.openspcoop2.core.config.ws.server.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.utils.UtilsException;

/**     
 * DriverConfigurazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazione {

	Object driver;
	String tipo;
	
	public Object getDriver() {
		return this.driver;
	}

	public String getTipo() {
		return this.tipo;
	}
	
	private static DriverConfigurazione driverConfigurazione = null;
	
	public static boolean initialize(Logger log){

		try {
			DriverConfigurazione.driverConfigurazione = new DriverConfigurazione();	
			DriverConfigurazione.driverConfigurazione.init();
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del DriverConfigurazione: "+e.getMessage(),e);
			DriverConfigurazione.driverConfigurazione = null;
		    return false;
		}
	}
    
	public static DriverConfigurazione getInstance() throws UtilsException{
		if(DriverConfigurazione.driverConfigurazione==null){
	    	throw new UtilsException("DriverConfigurazione non inizializzato");
	    }
	    return DriverConfigurazione.driverConfigurazione;
	}
	
	public void init() throws RuntimeException {

		Logger logWS = LoggerProperties.getLoggerWS();
		Logger logDAO = LoggerProperties.getLoggerDAO();
		try {
			BackendProperties backendProperties = BackendProperties.getInstance();
			this.tipo = backendProperties.getTipoConfigurazione();
			
			
			// CONFIGURAZIONE TYPE DB
			
			if(this.tipo.equalsIgnoreCase("db")){
				// Leggo le informazioni dal file properties
				String jndiName = backendProperties.getDbDataSource();
				String tipoDatabase = backendProperties.getDbTipoDatabase();
				Properties jndiProp = backendProperties.getDbDataSourceContext();
	
				this.driver = new DriverConfigurazioneDB(jndiName, jndiProp, logDAO, tipoDatabase);
				if(((DriverConfigurazioneDB)this.driver).create == false){
					throw new Exception("Driver ["+this.tipo+"] non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
				}
			}
			
			else{
				throw new Exception("Tipo di backend ["+this.tipo+"] sconosciuto");
			}
			
			logWS.info("Initialized ManagementService. Config type: [" + this.tipo + "]");
			
		} catch (Exception e) {
			logWS.error(e.getMessage(),e);
			logWS.error("ManagementService NON ATTIVO.");
			throw new RuntimeException("ManagementService Non Attivo.");
		}
	}
}

