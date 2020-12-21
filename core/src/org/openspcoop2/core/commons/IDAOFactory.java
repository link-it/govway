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
package org.openspcoop2.core.commons;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.slf4j.Logger;

/**
 * IDAOFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDAOFactory {


	public Object getServiceManager(IProjectInfo dao) throws CoreException;
		
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit) throws CoreException;
	

	public Object getServiceManager(IProjectInfo dao, ServiceManagerProperties smProperties) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit, ServiceManagerProperties smProperties) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds, ServiceManagerProperties smProperties) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit, ServiceManagerProperties smProperties) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection, ServiceManagerProperties smProperties) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit, ServiceManagerProperties smProperties) throws CoreException;
	

	public Object getServiceManager(IProjectInfo dao, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit, Logger log) throws CoreException;
	

	public Object getServiceManager(IProjectInfo dao, ServiceManagerProperties smProperties, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, boolean autoCommit, ServiceManagerProperties smProperties, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds, ServiceManagerProperties smProperties, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, DataSource ds,boolean autoCommit, ServiceManagerProperties smProperties, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection, ServiceManagerProperties smProperties, Logger log) throws CoreException;
	
	public Object getServiceManager(IProjectInfo dao, Connection connection,boolean autoCommit, ServiceManagerProperties smProperties, Logger log) throws CoreException;

}
