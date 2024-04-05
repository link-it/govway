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
package org.openspcoop2.generic_project.utils;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.TipiDatabase;


/**
 * ServiceManagerProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceManagerProperties {

	private String databaseType;
	private boolean generateDdl;
	private boolean showSql;
	private boolean automaticTransactionManagement = true;
	private int secondsToRefreshConnection = -1;
	
	public String getDatabaseType() {
		return this.databaseType;
	}
	public TipiDatabase getDatabase() throws ServiceException {
		TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.databaseType);
		if(TipiDatabase.DEFAULT.equals(databaseType)){
			throw new ServiceException("Database ["+databaseType+"] not supported");
		}
		return databaseType;
	}
	public boolean isGenerateDdl() {
		return this.generateDdl;
	}
	public boolean isShowSql() {
		return this.showSql;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public void setGenerateDdl(boolean generateDdl) {
		this.generateDdl = generateDdl;
	}
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}
	public boolean isAutomaticTransactionManagement() {
		return this.automaticTransactionManagement;
	}
	public void setAutomaticTransactionManagement(
			boolean automaticTransactionManagement) {
		this.automaticTransactionManagement = automaticTransactionManagement;
	}
	public int getSecondsToRefreshConnection() {
		return this.secondsToRefreshConnection;
	}
	public void setSecondsToRefreshConnection(int secondsToRefreshConnection) {
		this.secondsToRefreshConnection = secondsToRefreshConnection;
	}
}
