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
package org.openspcoop2.generic_project.expression.impl.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * TimestampTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimestampTypeFormatter implements ITypeFormatter<Timestamp> {

	private SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Override
	public String toString(Timestamp o) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		return this.dateformat.format(o);
	}
	
	@Override
	public String toSQLString(Timestamp o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	
	@Override
	public String toSQLString(Timestamp o, TipiDatabase databaseType) throws ExpressionException {
		return TimestampTypeFormatter.toSQLString(o, o, databaseType, this);
	}
	public static <T> String toSQLString(T rawDate, Date date, TipiDatabase databaseType, ITypeFormatter<T> formatter) throws ExpressionException {
		
		if(databaseType!=null) {
			switch (databaseType) {
			case POSTGRESQL:
			case ORACLE:
			case MYSQL:
			case HSQL:
			case SQLSERVER:
			case DB2:
			case DERBY:
				try {
					ISQLQueryObject sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(databaseType);
					return sqlQueryObjectCore.getSelectTimestampConstantField(date);
				}catch(Exception e) {
					throw new ExpressionException(e.getMessage(),e);
				}
			case DEFAULT:
				return "'"+formatter.toString(rawDate)+"'";
			}
		}
		
		return "'"+formatter.toString(rawDate)+"'";
	}


	@Override
	public Timestamp toObject(String o, Class<?> c) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		try{
			return new Timestamp(this.dateformat.parse(o).getTime());
		}catch(Exception e){
			throw new ExpressionException("Conversion failure: "+e.getMessage(),e);
		}
	}

	@Override
	public Class<Timestamp> getTypeSupported() {
		return Timestamp.class;
	}

}
