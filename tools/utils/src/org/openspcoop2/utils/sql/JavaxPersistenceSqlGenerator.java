/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.sql;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;

/**
 * JavaxPersistenceSqlGenerator
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JavaxPersistenceSqlGenerator {

	public static void generate(String persistenceId, String folder) throws IOException {
		
		TipiDatabase[] values = {TipiDatabase.POSTGRESQL,TipiDatabase.MYSQL,TipiDatabase.ORACLE, TipiDatabase.DERBY};
		for(TipiDatabase tipoDatabase: values) {
			generate(persistenceId, folder, tipoDatabase);
		}
	}
	
	private static void generate(String persistenceId, String folder, TipiDatabase tipoDatabase) throws IOException {

		String create= folder + tipoDatabase.toString().toLowerCase() +  "/"+persistenceId+".sql";
		String drop = folder + tipoDatabase.toString().toLowerCase()+ "/"+persistenceId+"_drop.sql";

		Files.deleteIfExists(Paths.get(create));
        Files.deleteIfExists(Paths.get(drop));

        Map<String, String> map = getMap(persistenceId, create, drop, tipoDatabase);
		
		// Persistence.generateSchema(persistenceId, map);
		try {
			Class<?> cPersistence = Class.forName("javax.persistence.Persistence");
			Method m = cPersistence.getMethod("generateSchema", persistenceId.getClass(), java.util.Map.class);
			m.invoke(null, persistenceId, map);
		}catch(Throwable t) {
			throw new IOException(t);
		}
      
	}

	private static Map<String, String> getMap(String persistenceId, String create, String drop, TipiDatabase tipoDatabase) throws IOException {
		Map<String, String> map = new HashMap<>();

        map.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        map.put("javax.persistence.schema-generation.scripts.create-target", create);
        map.put("javax.persistence.schema-generation.scripts.drop-target", drop);
        map.put("hibernate.hbm2ddl.delimiter", ";");
        map.put("hibernate.format_sql", "true");
		
        switch(tipoDatabase) {
		case DERBY:
	        map.put("javax.persistence.database-product-name", "Derby");
	        map.put("hibernate.dialect","org.hibernate.dialect.DerbyTenSevenDialect");
			break;
		case MYSQL:
	        map.put("javax.persistence.database-product-name", "Mysql");
	        map.put("hibernate.dialect","org.hibernate.dialect.MySQL5InnoDBDialect");
			break;
		case ORACLE:
	        map.put("javax.persistence.database-product-name", "Oracle");
	        map.put("hibernate.dialect","org.hibernate.dialect.Oracle10gDialect");
			break;
		case POSTGRESQL:
	        map.put("javax.persistence.database-product-name", "Postgresql");
	        map.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
	        map.put("javax.persistence.database-major-version", "9");
	        map.put("javax.persistence.database-minor-version", "1");
			break;
		default:
			break;
		}

		return map;
	}

}
