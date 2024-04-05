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

package org.openspcoop2.generic_project.dao.jdbc.utils;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.DerbyQueryObject;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBC_DerbyQueryObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBC_DerbyQueryObject extends DerbyQueryObject {

	private JDBC_SQLObjectFactory factory;

	public JDBC_DerbyQueryObject(JDBC_SQLObjectFactory factory) {
		super(TipiDatabase.DERBY);	
		this.factory = factory;
	}
	
	@Override
	public ISQLQueryObject newSQLQueryObject() throws SQLQueryObjectException {		
		return this.factory.createSQLQueryObject(this.getTipoDatabaseOpenSPCoop2());
	}
	
	
}
