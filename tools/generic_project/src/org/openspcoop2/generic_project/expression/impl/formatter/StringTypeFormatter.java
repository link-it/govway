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
 * StringTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StringTypeFormatter implements ITypeFormatter<String> {

	@Override
	public String toString(String o) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		return o;
	}
	
	@Override
	public String toSQLString(String o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	
	@Override
	public String toSQLString(String o, TipiDatabase databaseType) throws ExpressionException {
		switch (databaseType) {
		case POSTGRESQL:
			return "'"+StringTypeFormatter.escapeStringValue(toString(o))+"'::text";
		case ORACLE:
		case MYSQL:
		case HSQL:
		case SQLSERVER:
		case DEFAULT:
		default:
			return "'"+StringTypeFormatter.escapeStringValue(toString(o))+"'";
		}
	}

	@Override
	public String toObject(String o, Class<?> c) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		return o;
	}

	@Override
	public Class<String> getTypeSupported() {
		return String.class;
	}

	public static String escapeStringValue(String value) throws ExpressionException{
        
        if(value==null){
                throw new ExpressionException("Value is null");
        }
        // convert ' in ''
        int index = value.indexOf('\'');
        if(index>=0){
                StringBuilder str =  new StringBuilder();
                char[] v = value.toCharArray();
                for(int i=0; i<v.length; i++){
                        if(v[i]=='\''){
                                str.append('\'');
                        }
                        str.append(v[i]);
                }
                return str.toString();
        }
        
        return value;
	}

}
