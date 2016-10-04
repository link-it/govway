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
package org.openspcoop2.generic_project.expression.impl.formatter;

import java.text.SimpleDateFormat;
import java.sql.Timestamp;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * TimestampTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimestampTypeFormatter implements ITypeFormatter<Timestamp> {

	private SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
	
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
