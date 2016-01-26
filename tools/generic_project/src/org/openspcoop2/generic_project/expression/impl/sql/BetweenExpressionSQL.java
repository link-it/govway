/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.generic_project.expression.impl.sql;

import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.BetweenExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.generic_project.utils.IDGenerator;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * BetweenExpressionSQL
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BetweenExpressionSQL extends BetweenExpressionImpl implements ISQLExpression {

	private ISQLFieldConverter sqlFieldConverter;
	
	public BetweenExpressionSQL(ISQLFieldConverter sqlFieldConverter,IObjectFormatter objectFormatter, IField field,
			Object lower, Object high) {
		super(objectFormatter, field, lower, high);
		this.sqlFieldConverter = sqlFieldConverter;
	}

	public String toSql_engine(SQLMode mode,List<Object> oggettiPreparedStatement,Hashtable<String, Object> oggettiJPA)throws ExpressionException{
		StringBuffer bf = new StringBuffer();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		bf.append(this.sqlFieldConverter.toColumn(this.getField(),true));
		bf.append(" BETWEEN ");
		bf.append(" ");
		switch (mode) {
		case STANDARD:
			try{
				bf.append(super.getObjectFormatter().toSQLString(this.getLower()));
			}catch(Exception e){
				return "ERROR-LOWER: "+e.getMessage();
			}
			break;
		case PREPARED_STATEMENT:
			bf.append("?");
			oggettiPreparedStatement.add(this.getLower());
			break;
		case JPA:
			String id = "Lower_"+IDGenerator.getUniqueID(this.getField());
			bf.append(":"+id);
			oggettiJPA.put(id, this.getLower());
			break;
		}
		bf.append(" AND ");
		switch (mode) {
		case STANDARD:
			try{
				bf.append(super.getObjectFormatter().toSQLString(this.getHigh()));
			}catch(Exception e){
				return "ERROR-HIGH: "+e.getMessage();
			}
			break;
		case PREPARED_STATEMENT:
			bf.append("?");
			oggettiPreparedStatement.add(this.getHigh());
			break;
		case JPA:
			String id = "High_"+IDGenerator.getUniqueID(this.getField());
			bf.append(":"+id);
			oggettiJPA.put(id, this.getHigh());
			break;
		}
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	
	public void toSql_engine(ISQLQueryObject sqlQueryObject,SQLMode mode,List<Object> oggettiPreparedStatement,Hashtable<String, Object> oggettiJPA)throws ExpressionException{
		try{
			String s = toSql_engine(mode, oggettiPreparedStatement, oggettiJPA);
			s = s.substring(1,s.length()-2);
			sqlQueryObject.addWhereCondition(s);
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	@Override
	public String toSql() throws ExpressionException {
		return toSql_engine(SQLMode.STANDARD, null, null);
	}

	@Override
	public String toSqlPreparedStatement(List<Object> oggetti)
			throws ExpressionException {
		return toSql_engine(SQLMode.PREPARED_STATEMENT, oggetti, null);
	}

	@Override
	public String toSqlJPA(Hashtable<String, Object> oggetti)
			throws ExpressionException {
		return toSql_engine(SQLMode.JPA, null, oggetti);
	}
	
	@Override
	public void toSql(ISQLQueryObject sqlQueryObject) throws ExpressionException {
		toSql_engine(sqlQueryObject,SQLMode.STANDARD, null, null);
	}

	@Override
	public void toSqlPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti)
			throws ExpressionException {
		toSql_engine(sqlQueryObject,SQLMode.PREPARED_STATEMENT, oggetti, null);
	}

	@Override
	public void toSqlJPA(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti)
			throws ExpressionException {
		toSql_engine(sqlQueryObject,SQLMode.JPA, null, oggetti);
	}
}
