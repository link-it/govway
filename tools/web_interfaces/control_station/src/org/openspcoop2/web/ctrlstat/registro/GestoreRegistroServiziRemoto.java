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


package org.openspcoop2.web.ctrlstat.registro;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI;
import org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB;
import org.openspcoop2.core.registry.driver.ws.DriverRegistroServiziWS;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.web.ctrlstat.config.RegistroServiziRemotoProperties;


/**
 * GestoreRegistroServiziRemoto
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestoreRegistroServiziRemoto {

	
	public static IDriverRegistroServiziGet getDriverRegistroServizi(Logger log) throws Exception{
		if(gestore==null){
			init(log);
		}
		return gestore.driverRegistroServizi;
	} 
	public static String getTipoRegistroServizi(Logger log) throws Exception{
		if(gestore==null){
			init(log);
		}
		return gestore.tipoRegistro;
	} 
	
	private static GestoreRegistroServiziRemoto gestore = null;
	private static synchronized void init(Logger log) throws Exception{
		if(gestore==null){
			gestore = new GestoreRegistroServiziRemoto(log);
		}
	}
	
	
	
	private String tipoRegistro;
	private IDriverRegistroServiziGet driverRegistroServizi = null;
	
	public GestoreRegistroServiziRemoto(Logger log) throws Exception{
		
		RegistroServiziRemotoProperties registroServiziRemotoProperties = RegistroServiziRemotoProperties.getInstance();
		
		
		/* CONFIGURAZIONE GENERALE */
		this.tipoRegistro = registroServiziRemotoProperties.getTipoRegistroServiziRemoto();
		
		
		/* REGISTRO XML */
		if(CostantiConfigurazione.REGISTRO_XML.equals(this.tipoRegistro)){
			
			String location = registroServiziRemotoProperties.getRegistroServiziXML_Location();
			
			
			this.driverRegistroServizi = new DriverRegistroServiziXML(location, log);
			if(((DriverRegistroServiziXML)this.driverRegistroServizi).create==false){
				throw new Exception("[RegistroServiziRemoto] DriverRegistroServiziXML non correttamente inizializzato");
			}
		}
		
		/* REGISTRO WS */
		else if(CostantiConfigurazione.REGISTRO_WS.equals(this.tipoRegistro)){
			
			String location = registroServiziRemotoProperties.getRegistroServiziWS_Location();
			
			String username = registroServiziRemotoProperties.getRegistroServiziWS_Username();
			if(username!=null){
				username = username.trim();
			}
			String password = registroServiziRemotoProperties.getRegistroServiziWS_Password();
			if(password!=null){
				password = password.trim();
			}
			
			if(username!=null && password!=null){
				this.driverRegistroServizi = new DriverRegistroServiziWS(location,username,password, log);
			}else{
				this.driverRegistroServizi = new DriverRegistroServiziWS(location, log);
			}
			if(((DriverRegistroServiziWS)this.driverRegistroServizi).create==false){
				throw new Exception("[RegistroServiziRemoto] DriverRegistroServiziWS non correttamente inizializzato");
			}
		}
		
		/* REGISTRO DB */
		else if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoRegistro)){
			
			String tipoDatabase = registroServiziRemotoProperties.getRegistroServiziDB_TipoDatabase();
			
			String datasource = registroServiziRemotoProperties.getRegistroServiziDB_DataSource();
		
			Properties ctxProperties = registroServiziRemotoProperties.getRegistroServiziDB_DataSourceContext();
			
			this.driverRegistroServizi = new DriverRegistroServiziDB(datasource,ctxProperties, log,tipoDatabase);
			if(((DriverRegistroServiziDB)this.driverRegistroServizi).create==false){
				throw new Exception("[RegistroServiziRemoto] DriverRegistroServiziDB non correttamente inizializzato");
			}
			
		}
		
		/* REGISTRO UDDI */
		else if(CostantiConfigurazione.REGISTRO_UDDI.equals(this.tipoRegistro)){
			
			String location = registroServiziRemotoProperties.getRegistroServiziUDDI_InquiryURL();
			
			String username = registroServiziRemotoProperties.getRegistroServiziUDDI_Username();
			if(username!=null){
				username = username.trim();
			}
			String password = registroServiziRemotoProperties.getRegistroServiziUDDI_Password();
			if(password!=null){
				password = password.trim();
			}
			
			if(username!=null && password!=null){
				this.driverRegistroServizi = new DriverRegistroServiziUDDI(location,username,password, log);
			}else{
				this.driverRegistroServizi = new DriverRegistroServiziUDDI(location, log);
			}
			if(((DriverRegistroServiziUDDI)this.driverRegistroServizi).create==false){
				throw new Exception("[RegistroServiziRemoto] DriverRegistroServiziUDDI non correttamente inizializzato");
			}
	
		} 
		
		/* REGISTRO WEB */
		else if(CostantiConfigurazione.REGISTRO_WEB.equals(this.tipoRegistro)){
			
			String location = registroServiziRemotoProperties.getRegistroServiziWEB_URLPrefix();
			
			this.driverRegistroServizi = new DriverRegistroServiziWEB(location, log);
			if(((DriverRegistroServiziWEB)this.driverRegistroServizi).create==false){
				throw new Exception("[RegistroServiziRemoto] DriverRegistroServiziWEB non correttamente inizializzato");
			}
			
			
		} 
		
		else{
			throw new Exception("Tipo di registro non gestito: "+this.tipoRegistro);
		}
		
		log.info("Accesso al registro dei servizi remoto di tipo ["+this.tipoRegistro+"] correttamente effettuato."); 
			
		
	}
	
}
