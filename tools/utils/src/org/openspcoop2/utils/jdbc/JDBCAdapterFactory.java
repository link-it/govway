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


package org.openspcoop2.utils.jdbc;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Factory degli oggetti SQLQueryObject
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JDBCAdapterFactory {

	public static IJDBCAdapter createJDBCAdapter(TipiDatabase tipoDatabase) throws JDBCAdapterException {
		IJDBCAdapter jdbcAdapter = null;

		if (TipiDatabase.POSTGRESQL.equals(tipoDatabase)) {
			jdbcAdapter = new BytesJDBCAdapter(tipoDatabase);
		} else if (TipiDatabase.MYSQL.equals(tipoDatabase)) {
			jdbcAdapter = new BlobJDBCAdapter(tipoDatabase);
		} else if (TipiDatabase.ORACLE.equals(tipoDatabase)) {
			jdbcAdapter = new BlobJDBCAdapter(tipoDatabase);
		} else if (TipiDatabase.HSQL.equals(tipoDatabase)) {
			jdbcAdapter = new BytesJDBCAdapter(tipoDatabase);
		} else if ( TipiDatabase.SQLSERVER.equals(tipoDatabase) ) {
			jdbcAdapter = new StreamJDBCAdapter(tipoDatabase);
		} else if ( TipiDatabase.DB2.equals(tipoDatabase) ) {
			jdbcAdapter = new BlobJDBCAdapter(tipoDatabase);
		} else{
			throw new JDBCAdapterException("Tipo database non gestito ["+tipoDatabase+"]");
		}

		return jdbcAdapter;
	}
	
	public static IJDBCAdapter createJDBCAdapter(String tipoDatabase) throws JDBCAdapterException {
		return createJDBCAdapter(TipiDatabase.toEnumConstant(tipoDatabase));
	}

}
