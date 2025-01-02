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


package org.openspcoop2.core.diagnostica.ws.server.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.logger.DriverMsgDiagnostici;
import org.openspcoop2.utils.UtilsException;

/**
* DriverDiagnostica
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class DriverDiagnostica {

	DriverMsgDiagnostici driver;
	
	public DriverMsgDiagnostici getDriver() {
		return this.driver;
	}

	private static DriverDiagnostica driverDiagnostici = null;
	
	public static boolean initialize(Logger log){

		try {
			DriverDiagnostica.driverDiagnostici = new DriverDiagnostica();	
			DriverDiagnostica.driverDiagnostici.init();
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del Driver: "+e.getMessage(),e);
			DriverDiagnostica.driverDiagnostici = null;
		    return false;
		}
	}
    
	public static DriverDiagnostica getInstance() throws UtilsException{
		if(DriverDiagnostica.driverDiagnostici==null){
	    	throw new UtilsException("Driver non inizializzato");
	    }
	    return DriverDiagnostica.driverDiagnostici;
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
				this.driver = new DriverMsgDiagnostici(jndiName, tipoDatabase, jndiProp, logDAO);
			}catch(Exception e){
				logDAO.error(e.getMessage(),e);
				throw new Exception("Driver non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
			}
			
			logWS.info("Initialized ManagementService. Diagnostica type: [" + this.driver.getClass().getName() + "]");
			
		} catch (Exception e) {
			logWS.error(e.getMessage(),e);
			logWS.error("ManagementService NON ATTIVO.");
			throw new RuntimeException("ManagementService Non Attivo.");
		}
	}
}

