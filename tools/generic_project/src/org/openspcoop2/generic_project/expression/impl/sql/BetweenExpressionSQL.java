/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.expression.impl.sql;

import java.util.List;
import java.util.Map;

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

	private String toSqlEngine(SQLMode mode,List<Object> oggettiPreparedStatement,Map<String, Object> oggettiJPA)throws ExpressionException{
		StringBuilder bf = new StringBuilder();
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
			if(oggettiPreparedStatement!=null) {
				oggettiPreparedStatement.add(this.getLower());
			}
			break;
		case JPA:
			String id = "Lower_"+IDGenerator.getUniqueID(this.getField());
			bf.append(":"+id);
			if(oggettiJPA!=null) {
				oggettiJPA.put(id, this.getLower());
			}
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
			if(oggettiPreparedStatement!=null) {
				oggettiPreparedStatement.add(this.getHigh());
			}
			break;
		case JPA:
			String id = "High_"+IDGenerator.getUniqueID(this.getField());
			bf.append(":"+id);
			if(oggettiJPA!=null) {
				oggettiJPA.put(id, this.getHigh());
			}
			break;
		}
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	
	private void toSqlEngine(ISQLQueryObject sqlQueryObject,SQLMode mode,List<Object> oggettiPreparedStatement,Map<String, Object> oggettiJPA)throws ExpressionException{
		try{
			String s = toSqlEngine(mode, oggettiPreparedStatement, oggettiJPA);
			s = s.substring(1,s.length()-2);
			sqlQueryObject.addWhereCondition(s);
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	@Override
	public String toSql() throws ExpressionException {
		return toSqlEngine(SQLMode.STANDARD, null, null);
	}

	@Override
	public String toSqlPreparedStatement(List<Object> oggetti)
			throws ExpressionException {
		return toSqlEngine(SQLMode.PREPARED_STATEMENT, oggetti, null);
	}

	@Override
	public String toSqlJPA(Map<String, Object> oggetti)
			throws ExpressionException {
		return toSqlEngine(SQLMode.JPA, null, oggetti);
	}
	
	@Override
	public void toSql(ISQLQueryObject sqlQueryObject) throws ExpressionException {
		toSqlEngine(sqlQueryObject,SQLMode.STANDARD, null, null);
	}

	@Override
	public void toSqlPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti)
			throws ExpressionException {
		toSqlEngine(sqlQueryObject,SQLMode.PREPARED_STATEMENT, oggetti, null);
	}

	@Override
	public void toSqlJPA(ISQLQueryObject sqlQueryObject,Map<String, Object> oggetti)
			throws ExpressionException {
		toSqlEngine(sqlQueryObject,SQLMode.JPA, null, oggetti);
	}
}
