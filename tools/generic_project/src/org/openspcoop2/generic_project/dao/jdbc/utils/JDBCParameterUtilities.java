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


import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBCParameterUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCParameterUtilities {

	private TipiDatabase tipoDatabase = null;
	private IJDBCAdapter jdbcAdapter = null;
	
	public JDBCParameterUtilities(TipiDatabase tipoDatabaseOpenSPCoop2) throws SQLQueryObjectException, JDBCAdapterException{
		this.tipoDatabase = tipoDatabaseOpenSPCoop2;
		this.jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDatabase.getNome());
	}
	
	
	public void setParameters(PreparedStatement pstmt,JDBCObject ... params) throws SQLException, JDBCAdapterException, UtilsException{
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				setParameter(pstmt, (i+1), params[i]);
			}
		}
	}
	
	public void setParameter(PreparedStatement pstmt,int index,JDBCObject param) throws SQLException, JDBCAdapterException, UtilsException{
		
		Object value = param.getObject();
		Class<?> type = param.getTypeObject();
		
		//System.out.println("SET PARAMETER VALUE["+value+"] TYPE["+type.getName()+"]");
		
		if( (value!=null) && (value instanceof IEnumeration) ){
			IEnumeration enumObject = (IEnumeration) value;
			Object valueEnum = enumObject.getValue();
			Class<?> cValueEnum = null;
			if(valueEnum!=null){
				cValueEnum = valueEnum.getClass();
			}
			JDBCObject jdbcObject = new JDBCObject(valueEnum, cValueEnum);
			setParameter(pstmt,index, jdbcObject); // ricorsione sul tipo semplice
		}
		
		else if(type.getName().equals(String.class.getName())){
			String valueWrapped = null;
			if(value!=null && value instanceof String){
				valueWrapped = (String) value;
			}
			else if(value!=null && value instanceof Character){
				valueWrapped = ((Character)value).charValue()+"";
			}
			else if(value!=null){
				throw new UtilsException("Tipo["+type.getName()+"] non compatibile con l'oggetto fornito["+value.getClass().getName()+"]");
			}
			if(value==null){
				pstmt.setNull(index, java.sql.Types.VARCHAR);
			}else{
				pstmt.setString(index, valueWrapped);
			}
		}
		
		else if(type.getName().equals(Character.class.getName())){
			Character valueWrapped = null;
			String charValue = null;
			if(value!=null){
				valueWrapped = (Character) value;
				char charPrimitiveValue = valueWrapped.charValue();
				charValue = valueWrapped.charValue()+"";
				if(charPrimitiveValue==0){ // == ''
					// ERROR: invalid byte sequence for encoding "UTF8": 0x00
					// Postgresql non supporta il carattere 'vuoto'. Si deve usare un null value
					charValue = null;
				}
			}
			if(charValue!=null){
				pstmt.setString(index, charValue );
			}else{
				pstmt.setNull(index, java.sql.Types.VARCHAR);
			}
		}
		else if(type.getName().equals(char.class.getName())){
			Character valueWrapped = null;
			String charValue = null;
			if(value!=null){
				valueWrapped = (Character) value;
				char charPrimitiveValue = valueWrapped.charValue();
				charValue = valueWrapped.charValue()+"";
				if(charPrimitiveValue==0){ // == ''
					// ERROR: invalid byte sequence for encoding "UTF8": 0x00
					// Postgresql non supporta il carattere 'vuoto'. Si deve usare un null value
					charValue = null;
				}
			}
			if(charValue!=null){
				pstmt.setString(index, charValue );
			}else{
				pstmt.setNull(index, java.sql.Types.VARCHAR);
			}
		}
		
		else if(type.getName().equals(Boolean.class.getName())){
			if(value!=null){
				Boolean valueWrapped = (Boolean) value;
				pstmt.setBoolean(index, valueWrapped);
			}
			else{
				if(TipiDatabase.ORACLE.equals(this.tipoDatabase) ||
						TipiDatabase.DB2.equals(this.tipoDatabase) ){
					pstmt.setNull(index, java.sql.Types.INTEGER);
				}
				else{
					pstmt.setNull(index, java.sql.Types.BOOLEAN);
				}
			}
		}
		else if(type.getName().equals(boolean.class.getName())){
			if(value!=null){
				Boolean valueWrapped = (Boolean) value;
				pstmt.setBoolean(index, valueWrapped); 
			}
			else{
				if(TipiDatabase.ORACLE.equals(this.tipoDatabase) ||
						TipiDatabase.DB2.equals(this.tipoDatabase)){
					pstmt.setNull(index, java.sql.Types.INTEGER);
				}
				else{
					pstmt.setNull(index, java.sql.Types.BOOLEAN);
				}
			}
		}
		
		else if(type.getName().equals(Byte.class.getName())){
			if(value!=null){
				Byte valueWrapped = (Byte) value;
				//pstmt.setByte(index, valueWrapped); tradotto come INT nel database
				pstmt.setInt(index, valueWrapped.intValue());
			}
			else{
				//pstmt.setNull(index, java.sql.Types.BINARY);
				pstmt.setNull(index, java.sql.Types.INTEGER);
			}
		}
		else if(type.getName().equals(byte.class.getName())){
			if(value!=null){
				Byte valueWrapped = (Byte) value;
				//pstmt.setByte(index, valueWrapped); tradotto come INT nel database
				pstmt.setInt(index, valueWrapped.intValue());
			}
			else{
				//pstmt.setNull(index, java.sql.Types.BINARY);
				pstmt.setNull(index, java.sql.Types.INTEGER);
			}		
		}
		
		else if(type.getName().equals(Short.class.getName())){
			if(value!=null){
				Short valueWrapped = (Short) value;
				pstmt.setShort(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.INTEGER);
			}
		}
		else if(type.getName().equals(short.class.getName())){
			if(value!=null){
				Short valueWrapped = (Short) value;
				pstmt.setShort(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.INTEGER);
			}
		}
		
		else if(type.getName().equals(Integer.class.getName())){
			if(value!=null){
				Integer valueWrapped = (Integer) value;
				pstmt.setInt(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.INTEGER);
			}
		}
		else if(type.getName().equals(int.class.getName())){
			if(value!=null){
				Integer valueWrapped = (Integer) value;
				pstmt.setInt(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.INTEGER);
			}
		}
						
		else if(type.getName().equals(Long.class.getName())){
			if(value!=null){
				Long valueWrapped = (Long) value;
				pstmt.setLong(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.BIGINT);
			}
		}
		else if(type.getName().equals(long.class.getName())){
			if(value!=null){
				Long valueWrapped = (Long) value;
				pstmt.setLong(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.BIGINT);
			}
		}
		
		else if(type.getName().equals(Double.class.getName())){
			if(value!=null){
				Double valueWrapped = (Double) value;
				pstmt.setDouble(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.DOUBLE);
			}
		}
		else if(type.getName().equals(double.class.getName())){
			if(value!=null){
				Double valueWrapped = (Double) value;
				pstmt.setDouble(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.DOUBLE);
			}
		}
		
		else if(type.getName().equals(Float.class.getName())){
			if(value!=null){
				Float valueWrapped = (Float) value;
				pstmt.setFloat(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.FLOAT);
			}
		}
		else if(type.getName().equals(float.class.getName())){
			if(value!=null){
				Float valueWrapped = (Float) value;
				pstmt.setFloat(index, valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.FLOAT);
			}
		}
		
		else if(type.getName().equals(Date.class.getName())){
			Date valueWrapped = null;
			if(value!=null){
				valueWrapped = (Date) value;
			}
			if(value!=null){
				pstmt.setTimestamp(index, new Timestamp((valueWrapped).getTime()));
			}else{
				pstmt.setNull(index, java.sql.Types.TIMESTAMP);
			}
		}
		else if(type.getName().equals(java.sql.Date.class.getName())){
			java.sql.Date valueWrapped = null;
			if(value!=null){
				valueWrapped = (java.sql.Date) value;
			}
			if(value!=null){
				pstmt.setTimestamp(index, new Timestamp((valueWrapped).getTime()));
			}else{
				pstmt.setNull(index, java.sql.Types.TIMESTAMP);
			}
		}
		else if(type.getName().equals(Timestamp.class.getName())){
			if(value!=null){
				Timestamp valueWrapped = (Timestamp) value;
				pstmt.setTimestamp(index,valueWrapped);
			}
			else{
				pstmt.setNull(index, java.sql.Types.TIMESTAMP);
			}
		}
		else if(type.getName().equals(Calendar.class.getName())){
			Calendar valueWrapped = null;
			if(value!=null){
				valueWrapped = (Calendar) value;
			}
			if(value!=null){
				pstmt.setTimestamp(index, new Timestamp((valueWrapped).getTime().getTime()));
			}else{
				pstmt.setNull(index, java.sql.Types.TIMESTAMP);
			}
		}
		
		else if(type.getName().equals(byte[].class.getName())){
			byte[] valueWrapped = null;
			if(value!=null){
				valueWrapped = (byte[]) value;
			}
			this.jdbcAdapter.setBinaryData(pstmt, index, valueWrapped);
		}
		
		else if(type.getName().equals(URI.class.getName())){
			if(value!=null){
				URI valueWrapped = (URI) value;
				pstmt.setString(index,valueWrapped.toString());
			}
			else{
				pstmt.setNull(index, java.sql.Types.TIMESTAMP);
			}
		}
		
		else{
			throw new SQLException("Tipo di oggetto (posizione "+index+") non gestito: "+type.getClass().getName()+" - "+type);
		}
				
	}
	
	public String readStringParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (String) readParameter(rs, index, String.class);
	}
	public String readStringParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (String) readParameter(rs, name, String.class);
	}
	public Character readCharParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Character) readParameter(rs, index, Character.class);
	}
	public Character readCharParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Character) readParameter(rs, name, Character.class);
	}
	public Boolean readBooleanParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Boolean) readParameter(rs, index, Boolean.class);
	}
	public Boolean readBooleanParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Boolean) readParameter(rs, name, Boolean.class);
	}
	public Byte readByteParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Byte) readParameter(rs, index, Byte.class);
	}
	public Byte readByteParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Byte) readParameter(rs, name, Byte.class);
	}
	public Short readShortParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Short) readParameter(rs, index, Short.class);
	}
	public Short readShortParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Short) readParameter(rs, name, Short.class);
	}
	public Integer readIntegerParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Integer) readParameter(rs, index, Integer.class);
	}
	public Integer readIntegerParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Integer) readParameter(rs, name, Integer.class);
	}
	public Long readLongParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Long) readParameter(rs, index, Long.class);
	}
	public Long readLongParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Long) readParameter(rs, name, Long.class);
	}
	public Double readDoubleParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Double) readParameter(rs, index, Double.class);
	}
	public Double readDoubleParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Double) readParameter(rs, name, Double.class);
	}
	public Float readFloatParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Float) readParameter(rs, index, Float.class);
	}
	public Float readFloatParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Float) readParameter(rs, name, Float.class);
	}
	public Date readDateParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Date) readParameter(rs, index, Date.class);
	}
	public Date readDateParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Date) readParameter(rs, name, Date.class);
	}
	public java.sql.Date readSqlDateParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (java.sql.Date) readParameter(rs, index, java.sql.Date.class);
	}
	public java.sql.Date readSqlDateParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (java.sql.Date) readParameter(rs, name, java.sql.Date.class);
	}
	public Timestamp readTimestampParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Timestamp) readParameter(rs, index, Timestamp.class);
	}
	public Timestamp readTimestampParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Timestamp) readParameter(rs, name, Timestamp.class);
	}
	public Calendar readCalendarParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (Calendar) readParameter(rs, index, Calendar.class);
	}
	public Calendar readCalendarParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (Calendar) readParameter(rs, name, Calendar.class);
	}
	public URI readURIParameter(ResultSet rs,int index) throws SQLException, UtilsException{
		return (URI) readParameter(rs, index, URI.class);
	}
	public URI readURIParameter(ResultSet rs,String name) throws SQLException, UtilsException{
		return (URI) readParameter(rs, name, URI.class);
	}
	public Object readParameter(ResultSet rs,int index,Class<?> type) throws SQLException, UtilsException{
		return readParameter(rs, index, null, type, JDBCDefaultForXSDType.NONE);
	}
	public Object readParameter(ResultSet rs,String name,Class<?> type) throws SQLException, UtilsException{
		return readParameter(rs, -1, name, type, JDBCDefaultForXSDType.NONE);
	}
	public Object readParameter(ResultSet rs,String name,Class<?> type,JDBCDefaultForXSDType jdbcDefaultForXSDType) throws SQLException, UtilsException{
		return readParameter(rs, -1, name, type, jdbcDefaultForXSDType);
	}
	private Object readParameter(ResultSet rs,int index,String name,Class<?> type,JDBCDefaultForXSDType jdbcDefaultForXSDType) throws SQLException, UtilsException{
		
		if(type.toString().equals(String.class.toString())){
			if(name!=null){
				return rs.getString(name);
			}else{
				return rs.getString(index);
			}
		}
		else if(type.toString().equals(Character.class.toString()) || type.toString().equals(char.class.toString())){
			String s = null;
			if(name!=null){
				s = rs.getString(name);
			}else{
				s = rs.getString(index);
			}
			if(s!=null){
				return new Character(s.charAt(0));
			}else{
				if(type.toString().equals(char.class.toString())){
					// primitive default value
					return '\u0000';
				}
				else{
					return null;
				}
			}
		}
		else if(type.toString().equals(Boolean.class.toString()) || type.toString().equals(boolean.class.toString())){
			boolean booleanValue;
			if(name!=null){
				booleanValue = rs.getBoolean(name);
			}else{
				booleanValue = rs.getBoolean(index);
			}
			if(type.toString().equals(Boolean.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return booleanValue;
		}
		else if(type.toString().equals(Byte.class.toString()) || type.toString().equals(byte.class.toString())){
			byte byteValue;
			if(name!=null){
				//return rs.getByte(name); tradotto come INT nel database
				byteValue = (byte) rs.getInt(name);
			}else{
				//return rs.getByte(index); tradotto come INT nel database
				byteValue = (byte) rs.getInt(index);
			}
			if(byteValue==0 && 
				jdbcDefaultForXSDType!=null && 
				JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL.equals(jdbcDefaultForXSDType)){
				return null;
			}
			if(type.toString().equals(Byte.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return byteValue;
		}
		else if(type.toString().equals(Short.class.toString()) || type.toString().equals(short.class.toString())){
			short shortValue;
			if(name!=null){
				shortValue = rs.getShort(name);
			}else{
				shortValue = rs.getShort(index);
			}
			if(shortValue==0 && 
				jdbcDefaultForXSDType!=null && 
				JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL.equals(jdbcDefaultForXSDType)){
				return null;
			}
			if(type.toString().equals(Short.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return shortValue;
		}
		else if(type.toString().equals(Integer.class.toString()) || type.toString().equals(int.class.toString())){
			int intValue;
			if(name!=null){
				intValue = rs.getInt(name);
			}else{
				intValue = rs.getInt(index);
			}
			if(intValue==0 && 
				jdbcDefaultForXSDType!=null && 
				JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL.equals(jdbcDefaultForXSDType)){
				return null;
			}
			if(type.toString().equals(Integer.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return intValue;
		}
		else if(type.toString().equals(Long.class.toString()) || type.toString().equals(long.class.toString())){
			long longValue;
			if(name!=null){
				longValue = rs.getLong(name);
			}else{
				longValue = rs.getLong(index);
			}
			if(longValue==0L && 
				jdbcDefaultForXSDType!=null && 
				JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL.equals(jdbcDefaultForXSDType)){
				return null;
			}
			if(type.toString().equals(Long.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return longValue;
		}
		else if(type.toString().equals(Double.class.toString()) || type.toString().equals(double.class.toString())){
			double doubleValue;
			if(name!=null){
				doubleValue = rs.getDouble(name);
			}else{
				doubleValue = rs.getDouble(index);
			}
			if(doubleValue==0.0d && 
				jdbcDefaultForXSDType!=null && 
				JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL.equals(jdbcDefaultForXSDType)){
				return null;
			}
			if(type.toString().equals(Double.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return doubleValue;
		}
		else if(type.toString().equals(Float.class.toString()) || type.toString().equals(float.class.toString())){
			float floatValue;
			if(name!=null){
				floatValue = rs.getFloat(name);
			}else{
				floatValue = rs.getFloat(index);
			}
			if(floatValue==0.0f && 
				jdbcDefaultForXSDType!=null && 
				JDBCDefaultForXSDType.FORCE_ZERO_AS_NULL.equals(jdbcDefaultForXSDType)){
				return null;
			}
			if(type.toString().equals(Float.class.toString())){
				if(rs.wasNull()){
					return null;
				}
			}
			return floatValue;
		}
		else if(type.toString().equals(Date.class.toString())){
			Timestamp ts = null;
			if(name!=null){
				ts = rs.getTimestamp(name);
			}else{
				ts = rs.getTimestamp(index);
			}
			if(ts!=null){
				return new Date(ts.getTime());
			}
			else{
				return null;
			}
		}
		else if(type.toString().equals(java.sql.Date.class.toString())){
			Timestamp ts = null;
			if(name!=null){
				ts = rs.getTimestamp(name);
			}else{
				ts = rs.getTimestamp(index);
			}
			if(ts!=null){
				return new java.sql.Date(ts.getTime());
			}
			else{
				return null;
			}
		}
		else if(type.toString().equals(Timestamp.class.toString())){
			if(name!=null){
				return rs.getTimestamp(name);
			}else{
				return rs.getTimestamp(index);
			}
		}
		else if(type.toString().equals(Calendar.class.toString())){
			Timestamp ts = null;
			if(name!=null){
				ts = rs.getTimestamp(name);
			}else{
				ts = rs.getTimestamp(index);
			}
			if(ts!=null){
				Calendar c = Calendar.getInstance();
				c.setTime(new Date(ts.getTime()));
				return c;
			}
			else{
				return null;
			}
		}
		else if(type.getName().equals(byte[].class.getName())){
			if(name!=null){
				return this.jdbcAdapter.getBinaryData(rs, name);
			}
			else{
				return this.jdbcAdapter.getBinaryData(rs, index);
			}
		}
		else if(type.getName().equals(URI.class.getName())){
			String uri = null;
			if(name!=null){
				uri = rs.getString(name);
			}else{
				uri = rs.getString(index);
			}
			if(uri!=null){
				try{
					return new URI(uri);
				}catch(Exception e){
					throw new UtilsException(e.getMessage(),e);
				}
			}
			else{
				return null;
			}
		}
		else{
			if(name!=null){
				throw new SQLException("Tipo di oggetto (nome "+name+") non gestito: "+type.getClass().getName()+" - "+type);
			}else{
				throw new SQLException("Tipo di oggetto (posizione "+(index)+") non gestito: "+type.getClass().getName()+" - "+type);
			}
		}
	}
}
