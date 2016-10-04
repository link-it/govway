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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.generic_project.beans.IEnumeration;
import org.openspcoop2.utils.Utilities;

/**
 * JDBCSqlLogger
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCSqlLogger {

	private org.slf4j.Logger log = null;
	private SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe

	/**
	 * Costruttore della classe di utilita' per gestire i log sia di debug che di errore delle query effettuate su database
	 * 
	 * @param log logger
	 */
	public JDBCSqlLogger(org.slf4j.Logger log){
		this.log = log;
	}

	/**
	 * Messaggio di debug
	 * 
	 * @param msg messaggio di debug
	 */
	public void debug( String msg ) {
		this.log.debug(msg);
	}

	/**
	 * Registrazione del comando sql eseguita con livello DEBUG
	 * 
	 * @param sql comando sql
	 */
	public void debugSql( String sql ) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( sqlLog( sql ));
		}
	}

	/**
	 * Registrazione del comando sql eseguita con livello DEBUG
	 * 
	 * @param sql comando sql
	 * @param param parametro richiesto dal comando sql
	 */
	public void debugSql( String sql, JDBCObject ... param ) {
		if (this.log.isDebugEnabled()) {
			this.log.debug( sqlLog( sql , param ) );
		}
	}
	
	
	/**
	 * Messaggio di info
	 * 
	 * @param msg messaggio di info
	 */
	public void info( String msg ) {
		this.log.info(msg);
	}

	/**
	 * Registrazione del comando sql eseguita con livello INFO
	 * 
	 * @param sql comando sql
	 */
	public void infoSql( String sql ) {
		if (this.log.isInfoEnabled()) {
			this.log.info( sqlLog( sql ));
		}
	}

	/**
	 * Registrazione del comando sql eseguita con livello INFO
	 * 
	 * @param sql comando sql
	 * @param param parametro richiesto dal comando sql
	 */
	public void infoSql( String sql, JDBCObject ... param ) {
		if (this.log.isInfoEnabled()) {
			this.log.info( sqlLog( sql , param ) );
		}
	}

	/**
	 * Messaggio di errore
	 * 
	 * @param msg messaggio di errore
	 */
	public void error( String msg ) {
		this.log.error(msg );
	}

	/**
	 * Messaggio di errore con corrispettiva eccezione
	 * 
	 * @param msg messaggio di errore 
	 * @param t eccezione
	 */
	public void error( String msg, Throwable t ) {
		this.log.error(msg, t );
	}

	/**
	 * Registrazione del comando sql eseguita con livello ERROR
	 * 
	 * @param t eccezione
	 * @param sql comando sql
	 */
	public void errorSql( Throwable t, String sql ) {
		this.log.error( sqlLog( sql ), t );
	}

	/**
	 * Registrazione del comando sql eseguita con livello ERROR
	 * 
	 * @param t eccezione
	 * @param sql comando sql 
	 * @param param parametro richiesto dal comando
	 */
	public void errorSql( Throwable t, String sql, JDBCObject ... param ) {
		this.log.error( sqlLog( sql, param ), t );
	}



	/**
	 * Trasformazione in string 'human readable' del comando sql con i corrispettivi parametri
	 * 
	 * @param sql comando sql
	 * @param params parametri richiesti dal comando
	 * @return SQL
	 */
	public String sqlLog( String sql, JDBCObject ... params) {
		String ret = "SQL: " + sql;

		if (params == null || params.length == 0) {
			return ret;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				if (sb.length() != 0) {
					sb.append( ",\n" );
				}
								
				Object object = params[i].getObject();
				Class<?> type = params[i].getTypeObject();
				sb.append( "\t- param("+(i+1)+") type("+type+") value(" );
				
				logParam(object, type, sb);
				
				sb.append(")");
			}
		}

		return  ret + "; PARAMS: \n" + sb.toString();

	}

	private void logParam(Object object,Class<?> type,StringBuffer sb){
		
		if (object == null){
			sb.append("null");
		}

		else if(type.getName().equals(String.class.getName())){
			sb.append('\'').append( object ).append( '\'' );
		}

		else if(type.getName().equals(Character.class.getName())){
			Character valueWrapped = (Character) object;
			char charPrimitiveValue = valueWrapped.charValue();
			String charValue = valueWrapped.charValue()+"";
			if(charPrimitiveValue==0){ // == ''
				// ERROR: invalid byte sequence for encoding "UTF8": 0x00
				// Postgresql non supporta il carattere 'vuoto'. Si deve usare un null value
				charValue = null;
			}
			if(charValue!=null)
				sb.append('\'').append( charValue ).append( '\'' );
			else
				sb.append("null");
		}
		else if(type.getName().equals(char.class.getName())){
			Character valueWrapped = (Character) object;
			char charPrimitiveValue = valueWrapped.charValue();
			String charValue = valueWrapped.charValue()+"";
			if(charPrimitiveValue==0){ // == ''
				// ERROR: invalid byte sequence for encoding "UTF8": 0x00
				// Postgresql non supporta il carattere 'vuoto'. Si deve usare un null value
				charValue = null;
			}
			if(charValue!=null)
				sb.append('\'').append( charValue ).append( '\'' );
			else
				sb.append("null");
		}
		
		else if(type.getName().equals(Boolean.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(boolean.class.getName())){
			sb.append( object );
		}
		
		else if(type.getName().equals(Byte.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(byte.class.getName())){
			sb.append( object );
		}
		
		else if(type.getName().equals(Short.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(short.class.getName())){
			sb.append( object );
		}
		
		else if(type.getName().equals(Integer.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(int.class.getName())){
			sb.append( object );
		}
						
		else if(type.getName().equals(Long.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(long.class.getName())){
			sb.append( object );
		}
		
		else if(type.getName().equals(Double.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(double.class.getName())){
			sb.append( object );
		}
		
		else if(type.getName().equals(Float.class.getName())){
			sb.append( object.toString() );
		}
		else if(type.getName().equals(float.class.getName())){
			sb.append( object );
		}
		
		else if(type.getName().equals(Date.class.getName())){
			sb.append('\'').append( this.dateformat.format((Date)object) ).append( '\'' );
		}
		else if(type.getName().equals(java.sql.Date.class.getName())){
			sb.append('\'').append( this.dateformat.format((java.sql.Date)object) ).append( '\'' );
		}
		else if(type.getName().equals(Timestamp.class.getName())){
			sb.append('\'').append( this.dateformat.format( new Date( ((Timestamp)object).getTime() ) )).append( '\'' );
		}
		else if(type.getName().equals(Calendar.class.getName())){
			sb.append('\'').append( this.dateformat.format( new Date( ((Calendar)object).getTime().getTime() ) )).append( '\'' );
		}
		
		else if(type.getName().equals(byte[].class.getName())){
			 // 1024 = 1K
			 // Visualizzo al massimo 5K
			 int max = 5 * 1024;
			 try{
				 sb.append('\'').append(Utilities.convertToPrintableText((byte[])object, max)).append('\'');
			 }catch(Exception e){
				 sb.append( "bytes[] not printable ("+e.getMessage()+")" );
			 }
		}
		
		else if(type.getName().equals(URI.class.getName())){
			sb.append('\'').append( object ).append( '\'' );
		}
		
		else if(object!=null && object instanceof IEnumeration){
			IEnumeration enumObject = (IEnumeration) object;
			Object value = enumObject.getValue();
			Class<?> cValue = null;
			if(value!=null){
				cValue = value.getClass();
			}
			sb.append("IEnumeration ");
			this.logParam(value, cValue, sb);
		}
		
		else {
			sb.append( "!!ERROR Print not supported for this type" );
		}
	}
}
