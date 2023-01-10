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

package org.openspcoop2.utils.logger.config;

/**
 * DatabaseConfigConnection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatabaseConfigConnection {

	private String jdbcConnectionUrl;
	private String jdbcDriver;
	private String jdbcUsername;
	private String jdbcPassword;
	
	public String getJdbcConnectionUrl() {
		return this.jdbcConnectionUrl;
	}
	public void setJdbcConnectionUrl(String jdbcConnectionUrl) {
		this.jdbcConnectionUrl = jdbcConnectionUrl;
	}
	public String getJdbcDriver() {
		return this.jdbcDriver;
	}
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	public String getJdbcUsername() {
		return this.jdbcUsername;
	}
	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}
	public String getJdbcPassword() {
		return this.jdbcPassword;
	}
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	
}
