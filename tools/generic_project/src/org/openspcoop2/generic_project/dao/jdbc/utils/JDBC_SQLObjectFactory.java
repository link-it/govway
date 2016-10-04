/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.utils.sql.SQLObjectFactory;
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
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.setSelectForUpdate(this.selectForUpdate);
		((SQLQueryObjectCore)sqlQueryObject).setForceSelectForUpdateDisabledForNotQueryMethod(true);
		return sqlQueryObject;
	}
}
	

