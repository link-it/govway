/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.monitor.rs.server.config;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.slf4j.Logger;


/**
 * SoggettiConfig
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettiConfig {

	private static Boolean semaphore = true;
	
	public static boolean existsIdentificativoPorta(String tipoSoggetto, String nomeSoggetto) {
		
		if(Utility.existsIdentificativoPorta(tipoSoggetto, nomeSoggetto)) {
			return true;
		}
		
		// Verifico se il soggetto non fosse stato aggiunto in seguito al restart dell'API
		
		DBManager dbManager = DBManager.getInstance();
		Connection connection = null;
		Logger logSql = LoggerProperties.getLoggerDAO();
		try {
			connection = dbManager.getConnectionConfig();
			
			DriverRegistroServiziDB driverDB = new DriverRegistroServiziDB(connection, logSql, DatasourceProperties.getInstance().getConfigTipoDatabase());
			
			IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto);
			
			if(driverDB.existsSoggetto(idSoggetto)==false) {
				return false;
			}
			
			Soggetto soggetto = driverDB.getSoggetto(idSoggetto);
			if(soggetto.getPortaDominio()==null || StringUtils.isEmpty(soggetto.getPortaDominio())) {
				return false; // esterno
			}
			
			FiltroRicerca filtroRicercaPdd = new FiltroRicerca();
			filtroRicercaPdd.setTipo(PddTipologia.OPERATIVO.toString());
			List<String> idsPdd = null;
			try {
				idsPdd = driverDB.getAllIdPorteDominio(filtroRicercaPdd);
			}catch(DriverRegistroServiziNotFound notFound) {		
			}
			if(idsPdd==null || idsPdd.isEmpty() || !idsPdd.contains(soggetto.getPortaDominio())) {
				return false;
			}
			
			synchronized (semaphore) {
				
				// controllo se non fosse gia' stato riaggiunto da un altro thread
				
				if(Utility.existsIdentificativoPorta(tipoSoggetto, nomeSoggetto)) {
					return true;
				}
				
				Utility.putIdentificativoPorta(tipoSoggetto, nomeSoggetto, soggetto.getIdentificativoPorta());
				
				return true;
			}
			
		} 
		catch(DriverRegistroServiziNotFound notFound) {
			logSql.debug("Soggetto "+tipoSoggetto+"/"+nomeSoggetto+" non esistente: "+notFound.getMessage(),notFound);
			return false;
		}
		catch(Exception e) {
			logSql.error("Errore durante il controllo di esistenza del soggetto "+tipoSoggetto+"/"+nomeSoggetto+": "+e.getMessage(),e);
			return false;
		}
		finally {
			dbManager.releaseConnectionConfig(connection);
		}
	}
	
}
