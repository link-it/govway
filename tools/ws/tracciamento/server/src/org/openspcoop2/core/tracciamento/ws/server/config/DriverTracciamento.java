/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.core.tracciamento.ws.server.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;

/**
* DriverTracciamento
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class DriverTracciamento {

	org.openspcoop2.pdd.logger.DriverTracciamento driver;
	
	public org.openspcoop2.pdd.logger.DriverTracciamento getDriver() {
		return this.driver;
	}

	private static DriverTracciamento driverTracciamento = null;
	
	public static boolean initialize(Logger log){

		try {
			DriverTracciamento.driverTracciamento = new DriverTracciamento();	
			DriverTracciamento.driverTracciamento.init();
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del Driver: "+e.getMessage(),e);
			DriverTracciamento.driverTracciamento = null;
		    return false;
		}
	}
    
	public static DriverTracciamento getInstance() throws UtilsException{
		if(DriverTracciamento.driverTracciamento==null){
	    	throw new UtilsException("Driver non inizializzato");
	    }
	    return DriverTracciamento.driverTracciamento;
	}
	
	public void init() throws RuntimeException {

		Logger logWS = LoggerProperties.getLoggerWS();
		Logger logDAO = LoggerProperties.getLoggerDAO();
		try {
			DatasourceProperties datasourceProperties = DatasourceProperties.getInstance();
			
			// Leggo le informazioni dal file properties
			String jndiName = datasourceProperties.getDbDataSource();
			String tipoDatabase = datasourceProperties.getDbTipoDatabase();
			Properties jndiProp = datasourceProperties.getDbDataSourceContext();
	
			try{
				this.driver = new org.openspcoop2.pdd.logger.DriverTracciamento(jndiName, tipoDatabase, jndiProp, logDAO);
			}catch(Exception e){
				logDAO.error(e.getMessage(),e);
				throw new Exception("Driver non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
			}
			
			logWS.info("Initialized ManagementService. Tracciamento type: [" + this.driver.getClass().getName() + "]");
			
		} catch (Exception e) {
			logWS.error(e.getMessage(),e);
			logWS.error("ManagementService NON ATTIVO.");
			throw new RuntimeException("ManagementService Non Attivo.");
		}
	}
}

