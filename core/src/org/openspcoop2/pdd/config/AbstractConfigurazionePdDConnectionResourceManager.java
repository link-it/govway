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

package org.openspcoop2.pdd.config;

import java.sql.Connection;

import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.slf4j.Logger;

/**     
 * AbstractConfigurazionePdDConnectionResourceManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AbstractConfigurazionePdDConnectionResourceManager {

	protected OpenSPCoop2Properties openspcoopProperties;
	protected boolean configurazioneDinamica = false;
	protected boolean useConnectionPdD = false;
	protected DriverConfigurazioneDB driver;
	protected Logger log;
	
	
	public AbstractConfigurazionePdDConnectionResourceManager(OpenSPCoop2Properties openspcoopProperties, DriverConfigurazioneDB driver, boolean useConnectionPdD, Logger log) {
		this.openspcoopProperties = openspcoopProperties;
		this.configurazioneDinamica = this.openspcoopProperties.isConfigurazioneDinamica();
		this.useConnectionPdD = useConnectionPdD;
		this.driver = driver;
		this.log = log;

	}
	
	
	
	
	
	// IMPL
	
	protected ConfigurazionePdDConnectionResource getConnection(Connection connectionPdD, String methodName) throws Exception{
		ConfigurazionePdDConnectionResource cr = new ConfigurazionePdDConnectionResource();
		if(connectionPdD!=null && this.useConnectionPdD){
			cr.connectionDB = connectionPdD;
			cr.connectionPdD = true;
		}
		else{
			cr.connectionDB = this.driver.getConnection(methodName);
			cr.connectionPdD = false;
		}
		return cr;
	}
	protected void releaseConnection(ConfigurazionePdDConnectionResource cr) {
		if(cr!=null && cr.connectionDB!=null && !cr.connectionPdD) {
			this.driver.releaseConnection(cr.connectionDB);
		}
	}
	
}
