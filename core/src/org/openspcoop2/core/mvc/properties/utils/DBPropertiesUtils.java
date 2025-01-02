/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.core.mvc.properties.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB_genericPropertiesDriver;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**     
 * DBPropertiesUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBPropertiesUtils {

	private DBPropertiesUtils() {}
	
	public static Map<String, String> readProperties(Connection con, String tipoDB, String nomeTabella, String nomeColonnaKey, String nomeColonnaValue, String nomeColonnaEncValue, 
			String nomeColonnaParent, Long idParent, IDriverBYOK driverBYOK) throws CoreException{
		String methodName = "readProperties Tabella["+nomeTabella+"], NomeColonnaKey["+nomeColonnaKey+"], NomeColonnaValue["+nomeColonnaValue+"], nomeColonnaEncValue["+nomeColonnaEncValue+"], NomeColonnaParent["+nomeColonnaParent+"], IDParent["+idParent+"]";
		Map<String, String> map = new HashMap<>();

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField(nomeTabella +"." + nomeColonnaKey);
			sqlQueryObject.addSelectField(nomeTabella +"." + nomeColonnaValue);
			sqlQueryObject.addSelectField(nomeTabella +"." + nomeColonnaEncValue);
			sqlQueryObject.addWhereCondition(nomeTabella +"." + nomeColonnaParent +" = ?");
			sqlQueryObject.addOrderBy(nomeTabella +"." + nomeColonnaKey);
			sqlQueryObject.setSortType(true);

			String queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);

			int index = 1;
			stmt.setLong(index++, idParent);

			risultato = stmt.executeQuery();

			while (risultato.next()) {
				String key = risultato.getString(nomeColonnaKey);
				
				String value = null;
				String plainValue = risultato.getString(nomeColonnaValue);
				String encValue = risultato.getString(nomeColonnaEncValue);
				if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
					if(driverBYOK!=null) {
						value = driverBYOK.unwrapAsString(encValue);
					}
					else {
						value = encValue;
					}
				}
				else {
					value = plainValue;
				}

				map.put(key, value);
			}

			return map;

		}catch(Exception e){
			throw new CoreException(methodName + " error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	public static void writeProperties(Connection con, String tipoDB, Map<String, String> map, String nomeTabella, String nomeColonnaKey, String nomeColonnaValue, String nomeColonnaEncValue, 
			String nomeColonnaParent, Long idParent, IDriverBYOK driverBYOK, String tipoParent) throws CoreException{
		String methodName = "writeProperties Tabella["+nomeTabella+"], NomeColonnaKey["+nomeColonnaKey+"], NomeColonnaValue["+nomeColonnaValue+"], nomeColonnaEncValue["+nomeColonnaEncValue+"], NomeColonnaParent["+nomeColonnaParent+"], IDParent["+idParent+"]";

		PreparedStatement stmt = null;

		if(idParent <=0){
			throw new CoreException("IdParent non fornito");
		}

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addInsertTable(nomeTabella);
			sqlQueryObject.addInsertField(nomeColonnaKey, "?");
			sqlQueryObject.addInsertField(nomeColonnaValue, "?");
			sqlQueryObject.addInsertField(nomeColonnaEncValue, "?");
			sqlQueryObject.addInsertField(nomeColonnaParent, "?");
			String queryString = sqlQueryObject.createSQLInsert();

			for (String key : map.keySet()) {
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setString(index++, key);
				
				String plainValue = map.get(key);
				String encValue = null;
				if(driverBYOK!=null && DriverConfigurazioneDB_genericPropertiesDriver.isConfidentialProperty(tipoParent, key)) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainValue);
					if(byokValue!=null) {
						encValue = byokValue.getWrappedValue();
						plainValue = byokValue.getWrappedPlainValue();
					}
				}
				
				stmt.setString(index++, plainValue);
				stmt.setString(index++, encValue);
				
				stmt.setLong(index++, idParent);
				stmt.executeUpdate();
				stmt.close();
			}

		}catch(Exception e){
			throw new CoreException(methodName + " error",e);
		} finally {

			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	public static void deleteProperties(Connection con, String tipoDB, String nomeTabella, String nomeColonnaParent, Long idParent) throws CoreException{
		String methodName = "deleteProperties Tabella["+nomeTabella+"], NomeColonnaParent["+nomeColonnaParent+"], IDParent["+idParent+"]";

		PreparedStatement stmt = null;

		if(idParent <=0){
			throw new CoreException("IdParent non fornito");
		}

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addDeleteTable(nomeTabella);
			sqlQueryObject.addWhereCondition(nomeTabella +"." + nomeColonnaParent +" = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLDelete();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idParent);
			stmt.executeUpdate();
			stmt.close();

		}catch(Exception e){
			throw new CoreException(methodName + " error",e);
		} finally {

			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}


	public static Map<String, String> toMap(Map<String, Properties> propertiesMap) throws Exception {
		Map<String, String> map = null;
		if(propertiesMap == null)
			return map;

		map = new HashMap<>();

		for (String nomeProperties : propertiesMap.keySet()) {
			Properties properties = propertiesMap.get(nomeProperties);
			for (Object key : properties.keySet()) {
				String keyAsString = (String) key;
				String val = properties.getProperty(keyAsString);

				if(!nomeProperties.equals(Costanti.NOME_MAPPA_PROPERTIES_DEFAULT)) {
					keyAsString = nomeProperties + Costanti.KEY_PROPERTIES_CUSTOM_SEPARATOR + keyAsString;
				} 

				map.put(keyAsString, val);
			}
		}

		return map;
	}

	public static Map<String, Properties> toMultiMap(Map<String, String> dbMap) throws Exception {
		List<String> nomiProperties = new ArrayList<>();
		
		for (String key : dbMap.keySet()) {
			String keyToAdd = key;
			if(key.contains(Costanti.KEY_PROPERTIES_CUSTOM_SEPARATOR)) {
				int idx = key.indexOf(Costanti.KEY_PROPERTIES_CUSTOM_SEPARATOR);
				keyToAdd = key.substring(0,idx);
			}
			
			if(!nomiProperties.contains(keyToAdd))
				nomiProperties.add(keyToAdd);
		}
		
		return toMultiMap(dbMap, nomiProperties);
	}
	
	
	public static Map<String, Properties> toMultiMap(Map<String, String> dbMap, List<String> nomiProperties) {
		Map<String, Properties> map = null;
		if(dbMap == null)
			return map;

		map = new HashMap<>();

		for (String key : dbMap.keySet()) {
			String value = dbMap.get(key);
			String nomeProperties = Costanti.NOME_MAPPA_PROPERTIES_DEFAULT;
			String startsWith =  startsWith(nomiProperties, key);
			// ho trovato una property da raggruppare
			if(startsWith != null) {
				nomeProperties = startsWith;
				key = normalizePropertyName(nomeProperties, key);
			}

			Properties properties = map.remove(nomeProperties);
			if(properties == null) {
				properties = new Properties();
			}

			properties.put(key, value);

			map.put(nomeProperties, properties);
		}
		return map;
	}

	public static String startsWith(List<String> nomiProperties, String key) {
		if(nomiProperties != null && !nomiProperties.isEmpty()) {
			for (String string : nomiProperties) {
				if(key.startsWith(string+Costanti.KEY_PROPERTIES_CUSTOM_SEPARATOR))
					return string;
			}
		}

		return null;
	}

	public static String normalizePropertyName(String nomiProperties, String key) {
		int prefixLength = nomiProperties.length() + Costanti.KEY_PROPERTIES_CUSTOM_SEPARATOR.length();
		return key.substring(prefixLength);
	}

}
