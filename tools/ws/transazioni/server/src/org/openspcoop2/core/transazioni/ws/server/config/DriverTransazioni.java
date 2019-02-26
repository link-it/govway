/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.transazioni.ws.server.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.UtilsException;

/**
* DriverTransazioni
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class DriverTransazioni {

	org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager driver;
	
	public org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager getDriver() {
		return this.driver;
	}

	private static DriverTransazioni driverTransazioni = null;
	
	public static boolean initialize(Logger log){

		try {
			DriverTransazioni.driverTransazioni = new DriverTransazioni();	
			DriverTransazioni.driverTransazioni.init();
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del Driver: "+e.getMessage(),e);
			DriverTransazioni.driverTransazioni = null;
		    return false;
		}
	}
    
	public static DriverTransazioni getInstance() throws UtilsException{
		if(DriverTransazioni.driverTransazioni==null){
	    	throw new UtilsException("Driver non inizializzato");
	    }
	    return DriverTransazioni.driverTransazioni;
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
				ServiceManagerProperties smProperties = new ServiceManagerProperties();
				smProperties.setDatabaseType(tipoDatabase);
				smProperties.setShowSql(true); // regolo poi il livello di severita' di log4j
				this.driver = new org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager(jndiName, jndiProp, smProperties, logDAO);
			}catch(Exception e){
				logDAO.error(e.getMessage(),e);
				throw new Exception("Driver non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
			}
			
			logWS.info("Initialized ManagementService. Transazioni type: [" + this.driver.getClass().getName() + "]");
			
		} catch (Exception e) {
			logWS.error(e.getMessage(),e);
			logWS.error("ManagementService NON ATTIVO.");
			throw new RuntimeException("ManagementService Non Attivo.");
		}
	}
}

