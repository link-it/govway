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


package org.openspcoop2.web.ctrlstat.registro;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
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
				
		else{
			throw new Exception("Tipo di registro non gestito: "+this.tipoRegistro);
		}
		
		log.info("Accesso al registro dei servizi remoto di tipo ["+this.tipoRegistro+"] correttamente effettuato."); 
			
		
	}
	
}
