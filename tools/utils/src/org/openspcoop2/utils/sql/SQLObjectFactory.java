/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.utils.sql;

import org.openspcoop2.utils.TipiDatabase;


/**
 * Factory degli oggetti SQLQueryObject
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SQLObjectFactory {

	public static ISQLQueryObject createSQLQueryObject(String tipoDatabase) throws SQLQueryObjectException {
		return SQLObjectFactory.toQueryObject(tipoDatabase);
	}

	public static ISQLQueryObject createSQLQueryObject(TipiDatabase tipoDatabase) throws SQLQueryObjectException {
		return SQLObjectFactory.createSQLQueryObject(tipoDatabase.toString());
	}
	
	public static ISQLQueryObject toQueryObject(String tipoDatabase) throws SQLQueryObjectException{
		if (TipiDatabase.POSTGRESQL.equals(tipoDatabase)) {
			return new PostgreSQLQueryObject(TipiDatabase.POSTGRESQL);
		} else if (TipiDatabase.MYSQL.equals(tipoDatabase)) {
			return new MySQLQueryObject(TipiDatabase.MYSQL);
		} else if (TipiDatabase.ORACLE.equals(tipoDatabase)) {
			return new OracleQueryObject(TipiDatabase.ORACLE);
		}else if(TipiDatabase.HSQL.toString().equals(tipoDatabase)){
			return new HyperSQLQueryObject(TipiDatabase.HSQL);
		} else if(TipiDatabase.SQLSERVER.toString().equals(tipoDatabase)){
			return new SQLServerQueryObject(TipiDatabase.SQLSERVER);			
		} else if(TipiDatabase.DB2.toString().equals(tipoDatabase)){
			return new DB2QueryObject(TipiDatabase.DB2);			
		} else {
			throw new SQLQueryObjectException("Tipo database non gestito ["+tipoDatabase+"]");
		}

	}
}
