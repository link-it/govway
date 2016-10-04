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
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * Utiity che consente di effettuare una INSERT e recuperare l'id generato
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InsertAndGeneratedKey {

	public static long insertAndReturnGeneratedKey(Connection con,TipiDatabase tipoDatabase,IKeyGeneratorObject object,
			InsertAndGeneratedKeyObject ... objects) throws KeyGeneratorException{

		PreparedStatement stmt = null;
		try{

			// KeyGenerator
			IKeyGenerator keyGenerator = KeyGeneratorFactory.createKeyGeneratorFactory(tipoDatabase.getNome(), con, object);

			
			// Query di insert
			ISQLQueryObject sqlQueryObject = org.openspcoop2.utils.sql.SQLObjectFactory.createSQLQueryObject(tipoDatabase.getNome());
			sqlQueryObject.addInsertTable(object.getTable());
			if(objects==null){
				throw new Exception("Objects for insert is null");
			}
			if(objects.length<=0){
				throw new Exception("Objects for insert is empty");
			}
			for (int i = 0; i < objects.length; i++) {
				sqlQueryObject.addInsertField(objects[i].getName(), "?");
			}
			if(keyGenerator.isReturnGeneratedKeySupported()==false){
				sqlQueryObject.addInsertField(keyGenerator.getColunmKeyName(), "?");
			}
			String insertString = sqlQueryObject.createSQLInsert();

			
			// Eseguo Prepared Statement
			if(keyGenerator.isReturnGeneratedKeySupported()){
				stmt = con.prepareStatement(insertString,Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = con.prepareStatement(insertString);
			}
			int  i = 0;
			int indexJDBC = 1;
			for ( ; i < objects.length; i++, indexJDBC++) {
				switch (objects[i].getJdbcType()) {
				case TIMESTAMP:
					Timestamp t = null;
					if(objects[i].getValue()!=null){
						if(objects[i].getValue() instanceof Timestamp){
							t = (Timestamp) objects[i].getValue();
						}else if(objects[i].getValue() instanceof Date){
							t = new Timestamp( ((Date) objects[i].getValue()).getTime());
						}else if(objects[i].getValue() instanceof java.sql.Date){
							t = new Timestamp( ((Date) objects[i].getValue()).getTime());
						}else if(objects[i].getValue() instanceof Calendar){
							t = new Timestamp( ((Calendar) objects[i].getValue()).getTime().getTime());
						}
					}
					stmt.setTimestamp(indexJDBC, t);
					break;
				case INT:
					Integer num = null;
					if(objects[i].getValue()!=null){
						if(objects[i].getValue() instanceof Integer){
							num = (Integer) objects[i].getValue();
						}else if(objects[i].getValue() instanceof Long){
							num = ((Long) objects[i].getValue()).intValue();
						}else if(objects[i].getValue() instanceof Float){
							num = ((Float) objects[i].getValue()).intValue();
						}else if(objects[i].getValue() instanceof Double){
							num = ((Double) objects[i].getValue()).intValue();
						}
					}
					stmt.setInt(indexJDBC, num);
					break;
				case LONG:
					Long numLong = null;
					if(objects[i].getValue()!=null){
						if(objects[i].getValue() instanceof Long){
							numLong = (Long) objects[i].getValue();
						}else if(objects[i].getValue() instanceof Integer){
							numLong = ((Integer) objects[i].getValue()).longValue();
						}else if(objects[i].getValue() instanceof Float){
							numLong = ((Float) objects[i].getValue()).longValue();
						}else if(objects[i].getValue() instanceof Double){
							numLong = ((Double) objects[i].getValue()).longValue();
						}
					}
					stmt.setLong(indexJDBC, numLong);
					break;
				case STRING:
					String s = null;
					if(objects[i].getValue()!=null){
						s = (String) objects[i].getValue();
					}
					JDBCUtilities.setSQLStringValue(stmt,indexJDBC,s);
					break;
				default:
					throw new Exception("JDBC Type non supportato: "+objects[i].getJdbcType());
				}
			}
			if(keyGenerator.isReturnGeneratedKeySupported()==false){
				stmt.setLong(indexJDBC, keyGenerator.generateKey());
			}
			stmt.executeUpdate();

			
			// Ritorno id generato
			return keyGenerator.getReturnGeneratedKey(stmt);

		}catch(Exception e){
			throw new KeyGeneratorException ("insertAndReturnGeneratedKey failed: "+e.getMessage(),e);
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception eClose){}
		}
	}
}
