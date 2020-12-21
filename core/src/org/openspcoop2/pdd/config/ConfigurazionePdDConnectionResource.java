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

/**     
 * ConfigurazionePdDConnectionResource
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdDConnectionResource {

	protected Connection connectionDB = null;
	protected boolean connectionPdD = false;
	
	public Connection getConnectionDB() {
		return this.connectionDB;
	}
	public void setConnectionDB(Connection connectionDB) {
		this.connectionDB = connectionDB;
	}
	public boolean isConnectionPdD() {
		return this.connectionPdD;
	}
	public void setConnectionPdD(boolean connectionPdD) {
		this.connectionPdD = connectionPdD;
	}
}
