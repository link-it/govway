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

package org.openspcoop2.protocol.engine.registry;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReaderInUso;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.slf4j.Logger;

/**
 *  RegistryReaderInUso
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistryReaderInUso implements IRegistryReaderInUso {

	private IDriverRegistroServiziGet driverRegistroServiziGET;	
	@SuppressWarnings("unused")
	private Logger log;
	
	public RegistryReaderInUso() {}
	
	@Override
	public void init(IDriverRegistroServiziGet driverRegistroServizi, Logger log) {
		this.driverRegistroServiziGET = driverRegistroServizi;
		this.log = log;
	}
	
	
	
	// SOGGETTI
	
	@Override
	public boolean inUso(IDSoggetto idSoggetto) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("inUso(IDSoggetto)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				return DBOggettiInUsoUtils.isSoggettoRegistryInUso(connection, driverDB.getTipoDB(), idSoggetto, true, whereIsInUso, normalizeObjectIds);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	@Override
	public String getDettagliInUso(IDSoggetto idSoggetto) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("getDettagliInUso(IDSoggetto)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				boolean inUso = DBOggettiInUsoUtils.isSoggettoRegistryInUso(connection, driverDB.getTipoDB(), idSoggetto, true, whereIsInUso, normalizeObjectIds);
				if(inUso) {
					return DBOggettiInUsoUtils.toString(idSoggetto , whereIsInUso, false, "\n", normalizeObjectIds);
				}
				else {
					return null;
				}
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	
	
	// ACCORDI PARTE COMUNE
	
	@Override
	public boolean inUso(IDAccordo idAccordo) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("inUso(IDAccordo)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(connection, driverDB.getTipoDB(), idAccordo, whereIsInUso, normalizeObjectIds);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	@Override
	public String getDettagliInUso(IDAccordo idAccordo) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("getDettagliInUso(IDAccordo)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				boolean inUso = DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso(connection, driverDB.getTipoDB(), idAccordo, whereIsInUso, normalizeObjectIds);
				if(inUso) {
					return DBOggettiInUsoUtils.toString(idAccordo , whereIsInUso, false, "\n", normalizeObjectIds);
				}
				else {
					return null;
				}
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	
	
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE COMUNE
	
	@Override
	public boolean inUso(IDPortType id) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("inUso(IDPortType)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				return DBOggettiInUsoUtils.isPortTypeInUso(connection, driverDB.getTipoDB(), id, whereIsInUso, normalizeObjectIds);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	@Override
	public String getDettagliInUso(IDPortType id) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("getDettagliInUso(IDPortType)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				boolean inUso = DBOggettiInUsoUtils.isPortTypeInUso(connection, driverDB.getTipoDB(), id, whereIsInUso, normalizeObjectIds);
				if(inUso) {
					return DBOggettiInUsoUtils.toString(id , whereIsInUso, false, "\n", "");
				}
				else {
					return null;
				}
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	
	
	@Override
	public boolean inUso(IDPortTypeAzione id) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("inUso(IDPortTypeAzione)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				return DBOggettiInUsoUtils.isOperazioneInUso(connection, driverDB.getTipoDB(), id, whereIsInUso, normalizeObjectIds);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	@Override
	public String getDettagliInUso(IDPortTypeAzione id) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("getDettagliInUso(IDPortTypeAzione)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				boolean inUso = DBOggettiInUsoUtils.isOperazioneInUso(connection, driverDB.getTipoDB(), id, whereIsInUso, normalizeObjectIds);
				if(inUso) {
					return DBOggettiInUsoUtils.toString(id , whereIsInUso, false, "\n", "");
				}
				else {
					return null;
				}
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	
	
	@Override
	public boolean inUso(IDResource id) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("inUso(IDResource)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				return DBOggettiInUsoUtils.isRisorsaInUso(connection, driverDB.getTipoDB(), id, whereIsInUso, normalizeObjectIds);
				
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}
	@Override
	public String getDettagliInUso(IDResource id) throws RegistryException{
		if(this.driverRegistroServiziGET instanceof DriverRegistroServiziDB) {
			DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) this.driverRegistroServiziGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("getDettagliInUso(IDResource)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				boolean inUso = DBOggettiInUsoUtils.isRisorsaInUso(connection, driverDB.getTipoDB(), id, whereIsInUso, normalizeObjectIds);
				if(inUso) {
					
					String methodPath = null; // prefix non aggiunto
					
					return DBOggettiInUsoUtils.toString(id , methodPath, whereIsInUso, false, "\n", "");
				}
				else {
					return null;
				}
			}
			catch(Exception e) {
				throw new RegistryException(e.getMessage(),e);
			}
			finally {
				driverDB.releaseConnection(connection);
			}
		}
		else {
			throw new RuntimeException("Not Implemented");
		}
	}

}
