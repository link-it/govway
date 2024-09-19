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

package org.openspcoop2.pdd.logger;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.transazioni.dao.IDumpMessaggioServiceSearch;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.slf4j.Logger;

import org.openspcoop2.pdd.logger.dump.TransactionDumpMessaggioServiceSearch;

/**     
 * TransactionJDBCServiceManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionJDBCServiceManager extends org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager{

	public TransactionJDBCServiceManager() {
		super();
	}

	public TransactionJDBCServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties,
			Logger alog) throws ServiceException {
		super(connection, jdbcProperties, alog);
	}

	public TransactionJDBCServiceManager(Connection connection, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(connection, jdbcProperties);
	}

	public TransactionJDBCServiceManager(Connection connection, ServiceManagerProperties smProperties, Logger alog)
			throws ServiceException {
		super(connection, smProperties, alog);
	}

	public TransactionJDBCServiceManager(Connection connection, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(connection, smProperties);
	}

	public TransactionJDBCServiceManager(DataSource ds, JDBCServiceManagerProperties jdbcProperties, Logger alog)
			throws ServiceException {
		super(ds, jdbcProperties, alog);
	}

	public TransactionJDBCServiceManager(DataSource ds, JDBCServiceManagerProperties jdbcProperties)
			throws ServiceException {
		super(ds, jdbcProperties);
	}

	public TransactionJDBCServiceManager(DataSource ds, ServiceManagerProperties smProperties, Logger alog)
			throws ServiceException {
		super(ds, smProperties, alog);
	}

	public TransactionJDBCServiceManager(DataSource ds, ServiceManagerProperties smProperties) throws ServiceException {
		super(ds, smProperties);
	}

	public TransactionJDBCServiceManager(String jndiName, Properties contextJNDI,
			JDBCServiceManagerProperties jdbcProperties, Logger alog) throws ServiceException {
		super(jndiName, contextJNDI, jdbcProperties, alog);
	}

	public TransactionJDBCServiceManager(String jndiName, Properties contextJNDI,
			JDBCServiceManagerProperties jdbcProperties) throws ServiceException {
		super(jndiName, contextJNDI, jdbcProperties);
	}

	public TransactionJDBCServiceManager(String jndiName, Properties contextJNDI, ServiceManagerProperties smProperties,
			Logger alog) throws ServiceException {
		super(jndiName, contextJNDI, smProperties, alog);
	}

	public TransactionJDBCServiceManager(String jndiName, Properties contextJNDI, ServiceManagerProperties smProperties)
			throws ServiceException {
		super(jndiName, contextJNDI, smProperties);
	}

	public TransactionJDBCServiceManager(String connectionUrl, String driverJDBC, String username, String password,
			JDBCServiceManagerProperties jdbcProperties, Logger alog) throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, jdbcProperties, alog);
	}

	public TransactionJDBCServiceManager(String connectionUrl, String driverJDBC, String username, String password,
			JDBCServiceManagerProperties jdbcProperties) throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, jdbcProperties);
	}

	public TransactionJDBCServiceManager(String connectionUrl, String driverJDBC, String username, String password,
			ServiceManagerProperties smProperties, Logger alog) throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, smProperties, alog);
	}

	public TransactionJDBCServiceManager(String connectionUrl, String driverJDBC, String username, String password,
			ServiceManagerProperties smProperties) throws ServiceException {
		super(connectionUrl, driverJDBC, username, password, smProperties);
	}

	
	
	@Override
	public IDumpMessaggioServiceSearch getDumpMessaggioServiceSearch() throws ServiceException,NotImplementedException{
		return new TransactionDumpMessaggioServiceSearch(this);
	}
	
}
