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

package org.openspcoop2.generic_project.dao.jdbc.utils;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBC_SQLObjectFactory
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBC_SQLObjectFactory {

	private boolean selectForUpdate = false;
	
	public boolean isSelectForUpdate() {
		return this.selectForUpdate;
	}

	public void setSelectForUpdate(boolean selectForUpdate) {
		this.selectForUpdate = selectForUpdate;
	}

	public ISQLQueryObject createSQLQueryObject(TipiDatabase tipoDatabase) throws SQLQueryObjectException{
		ISQLQueryObject sqlQueryObject = null;
		switch (tipoDatabase) {
		case POSTGRESQL:
			sqlQueryObject = new JDBC_PostgreSQLQueryObject(this);
			break;
		case MYSQL:
			sqlQueryObject = new JDBC_MySQLQueryObject(this);
			break;
		case ORACLE:
			sqlQueryObject = new JDBC_OracleQueryObject(this);
			break;
		case HSQL:
			sqlQueryObject = new JDBC_HyperQueryObject(this);
			break;
		case DERBY:
			sqlQueryObject = new JDBC_DerbyQueryObject(this);
			break;
		case SQLSERVER:
			sqlQueryObject = new JDBC_SQLServerQueryObject(this);
			break;
		case DB2:
			sqlQueryObject = new JDBC_DB2SQLQueryObject(this);
			break;
		case DEFAULT:
			throw new SQLQueryObjectException("Tipo database non gestito ["+tipoDatabase+"]");
		}
		
		sqlQueryObject.setSelectForUpdate(this.selectForUpdate);
		((SQLQueryObjectCore)sqlQueryObject).setForceSelectForUpdateDisabledForNotQueryMethod(true);
		return sqlQueryObject;
	}
}
	

