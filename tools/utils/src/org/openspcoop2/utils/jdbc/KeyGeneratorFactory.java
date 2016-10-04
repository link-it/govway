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

package org.openspcoop2.utils.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Factory per un KeyGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyGeneratorFactory {

	public static IKeyGenerator createKeyGeneratorFactory(String tipoDatabase,Connection connection,IKeyGeneratorObject object) throws KeyGeneratorException {
		return KeyGeneratorFactory.toKeyGenerator(tipoDatabase, connection, object);
	}

	public static IKeyGenerator toKeyGenerator(String tipoDatabase,Connection connection,IKeyGeneratorObject object) throws KeyGeneratorException{
		
		if (TipiDatabase.POSTGRESQL.equals(tipoDatabase)) {
			return new PostgreSQLKeyGenerator(connection, object);
		} else if (TipiDatabase.MYSQL.equals(tipoDatabase)) {
			return new MySQLKeyGenerator(connection, object);
		} else if (TipiDatabase.ORACLE.equals(tipoDatabase)) {
			return new OracleKeyGenerator(connection, object);
		} else if (TipiDatabase.HSQL.equals(tipoDatabase)) {
			return new HyperSQLKeyGenerator(connection, object);
		} else if(TipiDatabase.SQLSERVER.toString().equals(tipoDatabase)){			
			return new SQLServerKeyGenerator(connection, object);
		} else if(TipiDatabase.DB2.toString().equals(tipoDatabase)){			
			return new DB2KeyGenerator(connection, object);
		} else {
			throw new KeyGeneratorException("Tipo database non gestito ["+tipoDatabase+"]");
		}

	}
}
