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
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReaderInUso;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.slf4j.Logger;

/**
 *  ConfigIntegrationReaderInUso
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigIntegrationReaderInUso implements IConfigIntegrationReaderInUso {
	
	private IDriverConfigurazioneGet driverConfigurazioneGET;
	@SuppressWarnings("unused")
	private Logger log;
	
	public ConfigIntegrationReaderInUso() {
	}


	@Override
	public void init(IDriverConfigurazioneGet driverConfigurazione,Logger log) {
		this.driverConfigurazioneGET = driverConfigurazione;
		this.log = log;
	}
	
	
	// SERVIZI APPLICATIVI
	
	@Override
	public boolean inUso(IDServizioApplicativo idServizioApplicativo) throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("inUso(IDServizioApplicativo)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				return DBOggettiInUsoUtils.isServizioApplicativoInUso(connection, driverDB.getTipoDB(), idServizioApplicativo, whereIsInUso, true, normalizeObjectIds);
				
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
	public String getDettagliInUso(IDServizioApplicativo idServizioApplicativo) throws RegistryException{
		if(this.driverConfigurazioneGET instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) this.driverConfigurazioneGET;
			Connection connection = null;
			try {
				connection = driverDB.getConnection("getDettagliInUso(IDServizioApplicativo)");
				
				Map<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				
				boolean normalizeObjectIds = true;
				boolean inUso = DBOggettiInUsoUtils.isServizioApplicativoInUso(connection, driverDB.getTipoDB(), idServizioApplicativo, whereIsInUso, true, normalizeObjectIds);
				if(inUso) {
					return DBOggettiInUsoUtils.toString(idServizioApplicativo , whereIsInUso, false, "\n", normalizeObjectIds);
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
