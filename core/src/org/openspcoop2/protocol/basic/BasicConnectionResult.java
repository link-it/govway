/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.basic;

import java.sql.Connection;

/**
 * BasicConnectionResult
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicConnectionResult {

	private Connection connection;
	private boolean releaseConnection;
	
	public Connection getConnection() {
		return this.connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public boolean isReleaseConnection() {
		return this.releaseConnection;
	}
	public void setReleaseConnection(boolean releaseConnection) {
		this.releaseConnection = releaseConnection;
	}
	
}
