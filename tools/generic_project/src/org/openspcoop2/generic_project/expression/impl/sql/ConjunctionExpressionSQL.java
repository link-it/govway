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
import java.util.Iterator;
import java.util.List;

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

	public String toSql_engine(SQLMode mode,ISQLQueryObject sqlQueryObject, List<Object> oggettiPreparedStatement,Hashtable<String, Object> oggettiJPA)throws ExpressionException{
		StringBuffer bf = new StringBuffer();
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
	
	public void toSql_engine(ISQLQueryObject sqlQueryObject,SQLMode mode,List<Object> oggettiPreparedStatement,Hashtable<String, Object> oggettiJPA)throws ExpressionException{
		try{
			String s = toSql_engine(mode, sqlQueryObject, oggettiPreparedStatement, oggettiJPA);
			s = s.substring(1,s.length()-2);
			sqlQueryObject.addWhereCondition(s);
		}catch(Exception e){
			throw new ExpressionException(e);
		}	
	}
	
	@Override
	public String toSql() throws ExpressionException {
		return toSql_engine(SQLMode.STANDARD, null, null, null);
	}

	@Override
	public String toSqlPreparedStatement(List<Object> oggetti)
			throws ExpressionException {
		return toSql_engine(SQLMode.PREPARED_STATEMENT, null, oggetti, null);
	}

	@Override
	public String toSqlJPA(Hashtable<String, Object> oggetti)
			throws ExpressionException {
		return toSql_engine(SQLMode.JPA, null, null, oggetti);
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
