/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import org.openspcoop2.generic_project.expression.impl.DateTimePartExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.DateTimePartEnum;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;

/**
 * DateTimePartExpressionSQL
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateTimePartExpressionSQL extends DateTimePartExpressionImpl implements ISQLExpression {

	private ISQLFieldConverter sqlFieldConverter;
	
	public DateTimePartExpressionSQL(ISQLFieldConverter sqlFieldConverter,IObjectFormatter objectFormatter, IField field,
			String value, DateTimePartEnum dateTimePartEnum) {
		super(objectFormatter, field, value, dateTimePartEnum);
		this.sqlFieldConverter = sqlFieldConverter;
	}

	private SQLQueryObjectCore getSQLQueryObjectCore(ISQLQueryObject sqlQueryObject) throws ExpressionException {
		if(sqlQueryObject==null) {
			try {
				// uso uno di default
				if(this.sqlFieldConverter!=null && this.sqlFieldConverter.getDatabaseType()!=null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.sqlFieldConverter.getDatabaseType());
				}
				else {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(TipiDatabase.POSTGRESQL); // uso come default
				}
			}catch(Exception e) {
				throw new ExpressionException(e.getMessage(),e);
			}
		}
		return (SQLQueryObjectCore) sqlQueryObject;
	}
	
	private String toSqlEngine(SQLMode mode,ISQLQueryObject sqlQueryObject,List<?> oggettiPreparedStatement,Map<String, ?> oggettiJPA)throws ExpressionException{
		
		if(mode!=null) {
			// nop
		}
		if(oggettiPreparedStatement!=null) {
			// nop
		}
		if(oggettiJPA!=null) {
			// nop
		}
		
		StringBuilder bf = new StringBuilder();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		
		SQLQueryObjectCore sqlObjectCore = getSQLQueryObjectCore(sqlQueryObject);
		
		try {
			String prefix = sqlObjectCore.getExtractDateTimePartFromTimestampFieldPrefix(this.dateTimePartEnum);
			bf.append(prefix);
		}catch(Exception e) {
			throw new ExpressionException(e.getMessage(),e);
		}
		
		if(this.sqlFieldConverter!=null) {
			bf.append(this.sqlFieldConverter.toColumn(this.getField(),true));
		}
		else {
			bf.append(this.getField().getFieldName());
		}

		try {
			String suffix = sqlObjectCore.getExtractDateTimePartFromTimestampFieldSuffix(this.dateTimePartEnum);
			bf.append(" ");
			bf.append(suffix);
		}catch(Exception e) {
			throw new ExpressionException(e.getMessage(),e);
		}
		
		bf.append(" = '");
		String sqlValue = null;
		try{
			sqlValue = super.getObjectFormatter().toString(this.getValue());
		}catch(Exception e){
			return "ERROR: "+e.getMessage();
		}
		bf.append(sqlValue);
		bf.append("'");
		
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	
	private void toSqlEngine(ISQLQueryObject sqlQueryObject,SQLMode mode,List<Object> oggettiPreparedStatement,Map<String, Object> oggettiJPA)throws ExpressionException{
		try{
			String s = toSqlEngine(mode, sqlQueryObject, oggettiPreparedStatement, oggettiJPA);
			s = s.substring(1,s.length()-2);
			sqlQueryObject.addWhereCondition(s);
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	@Override
	public String toSql() throws ExpressionException {
		return toSqlEngine(SQLMode.STANDARD, null, null, null);
	}

	@Override
	public String toSqlPreparedStatement(List<Object> oggetti)
			throws ExpressionException {
		return toSqlEngine(SQLMode.PREPARED_STATEMENT, null, oggetti, null);
	}

	@Override
	public String toSqlJPA(Map<String, Object> oggetti)
			throws ExpressionException {
		return toSqlEngine(SQLMode.JPA, null, null, oggetti);
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
