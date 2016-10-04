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

package org.openspcoop2.core.registry.ws.server.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI;
import org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB;
import org.openspcoop2.utils.UtilsException;

/**     
 * DriverRegistroServizi
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServizi {

	Object driver;
	String tipo;
	
	public Object getDriver() {
		return this.driver;
	}

	public String getTipo() {
		return this.tipo;
	}
	
	private static DriverRegistroServizi driverRegistroServizi = null;
	
	public static boolean initialize(Logger log){

		try {
			DriverRegistroServizi.driverRegistroServizi = new DriverRegistroServizi();	
			DriverRegistroServizi.driverRegistroServizi.init();
		    return true;
		}
		catch(Exception e) {
			log.error("Errore durante l'inizializzazione del DriverRegistroServizi: "+e.getMessage(),e);
			DriverRegistroServizi.driverRegistroServizi = null;
		    return false;
		}
	}
    
	public static DriverRegistroServizi getInstance() throws UtilsException{
		if(DriverRegistroServizi.driverRegistroServizi==null){
	    	throw new UtilsException("DriverRegistroServizi non inizializzato");
	    }
	    return DriverRegistroServizi.driverRegistroServizi;
	}
	
	public void init() throws RuntimeException {

		Logger logWS = LoggerProperties.getLoggerWS();
		Logger logDAO = LoggerProperties.getLoggerDAO();
		try {
			BackendProperties backendProperties = BackendProperties.getInstance();
			this.tipo = backendProperties.getTipoRegistro();
			
			
			// REGISTRO TYPE DB
			
			if(this.tipo.equalsIgnoreCase("db")){
				// Leggo le informazioni dal file properties
				String jndiName = backendProperties.getDbDataSource();
				String tipoDatabase = backendProperties.getDbTipoDatabase();
				Properties jndiProp = backendProperties.getDbDataSourceContext();
	
				this.driver = new DriverRegistroServiziDB(jndiName, jndiProp, logDAO, tipoDatabase);
				if(((DriverRegistroServiziDB)this.driver).create == false){
					throw new Exception("Driver ["+this.tipo+"] non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
				}
			}
			
			// REGISTRO TYPE UDDI
			
			else if(this.tipo.equalsIgnoreCase("uddi")){
				String inquiryURL = backendProperties.getUddiInquiryURL();
				String publishURL = backendProperties.getUddiPublishURL();
				String user = backendProperties.getUddiUser();
				String password = backendProperties.getUddiPassword();
				String urlPrefix = backendProperties.getUddiWebUrlPrefix();
				String pathPrefix = backendProperties.getUddiWebPathPrefix();
								
				this.driver = new DriverRegistroServiziUDDI(inquiryURL, publishURL, user, password, urlPrefix, pathPrefix, null);
				if(((DriverRegistroServiziUDDI)this.driver).create == false){
					throw new Exception("Driver ["+this.tipo+"] non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
				}
			}
			
			// REGISTRO TYPE WEB
					
			else if(this.tipo.equalsIgnoreCase("web")){
			
			    String urlPrefix = backendProperties.getWebUrlPrefix();
			    String pathPrefix = backendProperties.getWebPathPrefix();
			    
				this.driver = new DriverRegistroServiziWEB(urlPrefix, pathPrefix, null);
				if(((DriverRegistroServiziWEB)this.driver).create == false){
					throw new Exception("Driver ["+this.tipo+"] non inizializzato correttamente (Per ulteriori dettagli vedi log '*_sql')");
				}
			}
			
			else{
				throw new Exception("Tipo di backend ["+this.tipo+"] sconosciuto");
			}
			
			logWS.info("Initialized ManagementService. Registry type: [" + this.tipo + "]");
			
		} catch (Exception e) {
			logWS.error(e.getMessage(),e);
			logWS.error("ManagementService NON ATTIVO.");
			throw new RuntimeException("ManagementService Non Attivo.");
		}
	}
}

