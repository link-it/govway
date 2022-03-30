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
package org.openspcoop2.generic_project.expression.impl.formatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateUtils;

/**
 * CalendarTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CalendarTypeFormatter implements ITypeFormatter<Calendar> {

	private SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm:ss.SSS");
	
	@Override
	public String toString(Calendar o) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		return this.dateformat.format(o.getTime());
	}
	
	@Override
	public String toSQLString(Calendar o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	
	@Override
	public String toSQLString(Calendar o,TipiDatabase databaseType) throws ExpressionException {
		switch (databaseType) {
		case POSTGRESQL:
			return "timestamp '"+this.toString(o)+"'";
		case ORACLE:
		case MYSQL:
		case HSQL:
		case SQLSERVER:
		case DEFAULT:
		default:
			return "'"+this.toString(o)+"'";
		}
	}

	@Override
	public Calendar toObject(String o, Class<?> c) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.dateformat.parse(o));
			return calendar;
		}catch(Exception e){
			throw new ExpressionException("Conversion failure: "+e.getMessage(),e);
		}
	}

	@Override
	public Class<Calendar> getTypeSupported() {
		return Calendar.class;
	}

}
