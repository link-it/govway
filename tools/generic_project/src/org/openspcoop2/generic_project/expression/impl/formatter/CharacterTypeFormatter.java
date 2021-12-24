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
 * CharacterTypeFormatter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CharacterTypeFormatter implements ITypeFormatter<Character> {

	@Override
	public String toString(Character o) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		return o.charValue()+"";
	}
	
	@Override
	public String toSQLString(Character o) throws ExpressionException {
		return this.toSQLString(o, TipiDatabase.DEFAULT);
	}
	
	@Override
	public String toSQLString(Character o, TipiDatabase databaseType) throws ExpressionException {
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
	public Character toObject(String o, Class<?> c) throws ExpressionException {
		if(o == null){
			throw new ExpressionException("Object parameter is null");
		}
		if(o.length()!=1){
			throw new ExpressionException("Object parameter has wrong length ("+o.length()+") (required 1 for character object)");
		}
		try{
			return Character.valueOf(o.charAt(0));
		}catch(Exception e){
			throw new ExpressionException("Conversion failure: "+e.getMessage(),e);
		}
	}

	@Override
	public Class<Character> getTypeSupported() {
		return Character.class;
	}

}
