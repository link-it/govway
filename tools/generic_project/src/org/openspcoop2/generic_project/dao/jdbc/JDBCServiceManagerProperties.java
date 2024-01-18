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

import org.openspcoop2.generic_project.utils.ServiceManagerProperties;

/**
 * JDBCServiceManagerProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCServiceManagerProperties extends ServiceManagerProperties {
	
	public JDBCServiceManagerProperties(){
	}
	public JDBCServiceManagerProperties(ServiceManagerProperties serviceManagerProperties){
		super();
		super.setAutomaticTransactionManagement(serviceManagerProperties.isAutomaticTransactionManagement());
		super.setDatabaseType(serviceManagerProperties.getDatabaseType());
		super.setGenerateDdl(serviceManagerProperties.isGenerateDdl());
		super.setShowSql(serviceManagerProperties.isShowSql());
		super.setSecondsToRefreshConnection(serviceManagerProperties.getSecondsToRefreshConnection());
	}
	
}
