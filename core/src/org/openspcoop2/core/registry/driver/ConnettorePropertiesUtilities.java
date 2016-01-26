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

package org.openspcoop2.core.registry.driver;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * ConnettorePropertiesUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettorePropertiesUtilities {

	public static ArrayList<Property> fromPropertiesToCollection(Properties props) {
		ArrayList<Property> lista = new ArrayList<Property>();

		Property tmp = null;
		Enumeration<?> en = props.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = (String) props.get(key);
			// log.info("loading property "+key+" - "+value);
			tmp = new Property();
			tmp.setNome(key);
			tmp.setValore(value);

			lista.add(tmp);
		}

		return lista;
	}
	
	public static Property[] getPropertiesConnettore(String nome_connettore,Connection con, String tipoDB) throws CoreException {

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String path = null;
		Properties prop = new Properties();
		Property[] res = null;

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_PROPERTIES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_connettore = ?");
			String queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome_connettore);

			risultato = stmt.executeQuery();

			if (risultato.next())
				path = risultato.getString("path");

			// controllo il path
			if (path == null || path.equals(""))
				throw new CoreException("getPropertiesConnettore : nessun path impostato per il connettore : " + nome_connettore);

			// Accedo al file
			InputStream ins = DBUtils.class.getResourceAsStream(path);

			prop.load(ins);

			// log.info("caricato il file :"+path+" con "+prop.size()+"
			// properties.");

			Collection<Property> collection = ConnettorePropertiesUtilities.fromPropertiesToCollection(prop);

			res =collection.toArray(new Property[collection.size()]);

			return res;

		} catch (FileNotFoundException fe) {
			throw new CoreException("Impossibile aprire il file :" + path + " Errore: " + fe.getMessage(),fe);
		} catch (Exception e) {
			throw new CoreException("Errore durante la lettura delle properties dal file [" + path + "]: " + e.getMessage(),e);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
}
