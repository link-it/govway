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
package org.openspcoop2.generic_project.expression.impl.sql;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.AbstractBaseExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.ConjunctionExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * ConjunctionExpressionSQL
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConjunctionExpressionSQL extends ConjunctionExpressionImpl implements ISQLExpression {

	@SuppressWarnings("unused")
	private ISQLFieldConverter sqlFieldConverter;
	
	public ConjunctionExpressionSQL(ISQLFieldConverter sqlFieldConverter,IObjectFormatter objectFormatter) {
		super(objectFormatter);
		this.sqlFieldConverter = sqlFieldConverter;
	}

	private String toSqlEngine(SQLMode mode,ISQLQueryObject sqlQueryObject, List<Object> oggettiPreparedStatement,Map<String, Object> oggettiJPA)throws ExpressionException{
		StringBuilder bf = new StringBuilder();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		int index = 0;
		for (Iterator<AbstractBaseExpressionImpl> iterator = this.getLista().iterator(); iterator.hasNext();) {
			AbstractBaseExpressionImpl exp = iterator.next();
			if(index>0){
				if(this.isAndConjunction())
					bf.append(" AND ");
				else
					bf.append(" OR ");
			}
			if(exp instanceof ISQLExpression){
				if(sqlQueryObject!=null){
					ISQLQueryObject sqlQ = null;
					try{
						sqlQ = sqlQueryObject.newSQLQueryObject();
					
						switch (mode) {
						case STANDARD:
							((ISQLExpression)exp).toSql(sqlQ);
							bf.append(sqlQ.createSQLConditions());
							break;
						case PREPARED_STATEMENT:
							((ISQLExpression)exp).toSqlPreparedStatement(sqlQ,oggettiPreparedStatement);
							bf.append(sqlQ.createSQLConditions());
							break;
						case JPA:
							((ISQLExpression)exp).toSqlJPA(sqlQ,oggettiJPA);
							bf.append(sqlQ.createSQLConditions());
							break;
						}
					}catch(Exception e){
						throw new ExpressionException("Expression["+index+"] (type:"+exp.getClass().getName()+")  sqlQueryObject error: "+e.getMessage(),e);
					}
				}
				else{
					switch (mode) {
					case STANDARD:
						bf.append(((ISQLExpression)exp).toSql());
						break;
					case PREPARED_STATEMENT:
						bf.append(((ISQLExpression)exp).toSqlPreparedStatement(oggettiPreparedStatement));
						break;
					case JPA:
						bf.append(((ISQLExpression)exp).toSqlJPA(oggettiJPA));
						break;
					}
				}
			}else{
				throw new ExpressionException("Expression["+index+"] (type:"+exp.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
			}
			index++;
		}
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
