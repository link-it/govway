/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.core.config.utils;

import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.config.Property;

/**     
 * OpenSPCoopAppenderUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopAppenderUtilities {

	public static void addParameters(Logger log,List<Property> appenderProperties,
			String dsJndiName,
			String connectionUrl, String connectionDriver, String connectionUsername, String connectionPassword,
			String tipoDatabase,
			boolean usePdDConnection, boolean debug
			) throws Exception{
		
		
		if(dsJndiName!=null){
			Property prop_ds = new Property();
			prop_ds.setNome("datasource");
			prop_ds.setValore(dsJndiName);
			appenderProperties.add(prop_ds);
		}
		else if(connectionUrl!=null){
			Property prop_connectionUrl = new Property();
			prop_connectionUrl.setNome("connectionUrl");
			prop_connectionUrl.setValore(connectionUrl);
			appenderProperties.add(prop_connectionUrl);
			
			Property prop_driver = new Property();
			prop_driver.setNome("connectionDriver");
			prop_driver.setValore(connectionDriver);
			appenderProperties.add(prop_driver);
			
			Property prop_username = new Property();
			prop_username.setNome("connectionUsername");
			prop_username.setValore(connectionUsername);
			appenderProperties.add(prop_username);
			
			Property prop_password = new Property();
			prop_password.setNome("connectionPassword");
			prop_password.setValore(connectionPassword);
			appenderProperties.add(prop_password);
		}
			
		Property prop_tipoDatabase = new Property();
		prop_tipoDatabase.setNome("tipoDatabase");
		prop_tipoDatabase.setValore(tipoDatabase);
		appenderProperties.add(prop_tipoDatabase);
		
		if(usePdDConnection) {
			Property prop_usePdDConnection = new Property();
			prop_usePdDConnection.setNome("usePdDConnection");
			prop_usePdDConnection.setValore(usePdDConnection+"");
			appenderProperties.add(prop_usePdDConnection);
		}
		
		if(debug) {
			Property prop_debug = new Property();
			prop_debug.setNome("debug");
			prop_debug.setValore(debug+"");
			appenderProperties.add(prop_debug);
		}

	}
	
	public static void addCheckProperties(List<Property> appenderProperties, boolean checkProperties) {
		Property prop_checkProperties = new Property();
		prop_checkProperties.setNome("checkProperties");
		prop_checkProperties.setValore(checkProperties+"");
		appenderProperties.add(prop_checkProperties);
	}
	
}
