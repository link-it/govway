/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.dao.jdbc;


import java.sql.Connection;

import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * JDBCLimitedServiceManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCLimitedServiceManager extends JDBCServiceManagerBase {

	public JDBCLimitedServiceManager(JDBCServiceManagerBase jdbcServiceManager){
		this.datasource = jdbcServiceManager.datasource;
		this.connection = jdbcServiceManager.connection;
		this.log = jdbcServiceManager.log;
		this.jdbcProperties = jdbcServiceManager.jdbcProperties;
	}
	
	
	@Override
	protected Connection getConnection() throws ServiceException {
		throw new ServiceException("Connection managed from framework");
	}
	@Override
	protected void closeConnection(Connection connection) throws ServiceException {
		throw new ServiceException("Connection managed from framework");
	}
	
}
