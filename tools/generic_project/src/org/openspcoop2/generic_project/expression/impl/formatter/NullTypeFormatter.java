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


import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * NullTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NullTypeFormatter implements ITypeFormatter<org.apache.commons.lang.ObjectUtils.Null> {

	
	@Override
	public String toString(org.apache.commons.lang.ObjectUtils.Null o) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		return "null";
	}
	
	@Override
	public String toSQLString(org.apache.commons.lang.ObjectUtils.Null o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	
	@Override
	public String toSQLString(org.apache.commons.lang.ObjectUtils.Null o,TipiDatabase databaseType) throws ExpressionException {
		switch (databaseType) {
		case POSTGRESQL:
			return "null::text";
		case ORACLE:
		case MYSQL:
		case HSQL:
		case SQLSERVER:
		case DEFAULT:
		default:
			return "null";
		}
	}

	@Override
	public org.apache.commons.lang.ObjectUtils.Null toObject(String o, Class<?> c) throws ExpressionException {
		if(o == null){
			return org.apache.commons.lang.ObjectUtils.NULL;
		}
		try{
			if(o.equals("null")){
				return org.apache.commons.lang.ObjectUtils.NULL;
			}
			else{
				throw new Exception("Object is not null, value: ["+o+"]");
			}
		}catch(Exception e){
			throw new ExpressionException("Conversion failure: "+e.getMessage(),e);
		}
	}

	@Override
	public Class<org.apache.commons.lang.ObjectUtils.Null> getTypeSupported() {
		return org.apache.commons.lang.ObjectUtils.Null.class;
	}

}
