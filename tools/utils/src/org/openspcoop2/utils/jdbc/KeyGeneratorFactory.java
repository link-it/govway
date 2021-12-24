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

package org.openspcoop2.utils.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Factory per un KeyGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyGeneratorFactory {

	private static boolean useAutoIncrementPostgresql = false;
	public static boolean isUseAutoIncrementPostgresql() {
		return KeyGeneratorFactory.useAutoIncrementPostgresql;
	}
	public static void setUseAutoIncrementPostgresql(boolean useAutoIncrementPostgresql) {
		KeyGeneratorFactory.useAutoIncrementPostgresql = useAutoIncrementPostgresql;
	}

	static {
		try (InputStream is = KeyGeneratorFactory.class.getResourceAsStream("/org/openspcoop2/utils/jdbc/jdbc.properties")){
			if(is!=null) {
				Properties p = new Properties();
				p.load(is);
				String tmp = p.getProperty("postgresql.autoIncrement");
				if(tmp!=null) {
					if("true".equalsIgnoreCase(tmp.trim())) {
						KeyGeneratorFactory.useAutoIncrementPostgresql = true;
					}
				}
			}
		}catch(Throwable t) {}
	}
	
	public static IKeyGenerator createKeyGeneratorFactory(String tipoDatabase,Connection connection,IKeyGeneratorObject object) throws KeyGeneratorException {
		return KeyGeneratorFactory.toKeyGenerator(tipoDatabase, connection, object);
	}

	public static IKeyGenerator toKeyGenerator(String tipoDatabase,Connection connection,IKeyGeneratorObject object) throws KeyGeneratorException{
		
		if (TipiDatabase.POSTGRESQL.equals(tipoDatabase)) {
			if(KeyGeneratorFactory.useAutoIncrementPostgresql) {
				return new PostgreSQLAutoKeyGenerator(connection, object);
			}
			else {
				return new PostgreSQLKeyGenerator(connection, object);
			}
		} else if (TipiDatabase.MYSQL.equals(tipoDatabase)) {
			return new MySQLKeyGenerator(connection, object);
		} else if (TipiDatabase.ORACLE.equals(tipoDatabase)) {
			return new OracleKeyGenerator(connection, object);
		} else if (TipiDatabase.HSQL.equals(tipoDatabase)) {
			return new HyperSQLKeyGenerator(connection, object);
		} else if (TipiDatabase.DERBY.equals(tipoDatabase)) {
			return new DerbyKeyGenerator(connection, object);
		} else if(TipiDatabase.SQLSERVER.toString().equals(tipoDatabase)){			
			return new SQLServerKeyGenerator(connection, object);
		} else if(TipiDatabase.DB2.toString().equals(tipoDatabase)){			
			return new DB2KeyGenerator(connection, object);
		} else {
			throw new KeyGeneratorException("Tipo database non gestito ["+tipoDatabase+"]");
		}

	}
}
