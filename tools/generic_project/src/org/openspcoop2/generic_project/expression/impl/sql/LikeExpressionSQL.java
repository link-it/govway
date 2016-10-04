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
package org.openspcoop2.generic_project.expression.impl.sql;

import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.impl.LikeExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.EscapeSQLPattern;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * LikeExpressionSQL
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LikeExpressionSQL extends LikeExpressionImpl implements ISQLExpression {

	private ISQLFieldConverter sqlFieldConverter;
	
	public LikeExpressionSQL(ISQLFieldConverter sqlFieldConverter,IObjectFormatter objectFormatter, IField field,
			String value, LikeMode mode, boolean caseInsensitive) {
		super(objectFormatter, field, value, mode, caseInsensitive);
		this.sqlFieldConverter = sqlFieldConverter;
	}

	public String toSql_engine(SQLMode mode,ISQLQueryObject sqlQueryObject,List<?> oggettiPreparedStatement,Hashtable<String, ?> oggettiJPA)throws ExpressionException{
		StringBuffer bf = new StringBuffer();
		if(isNot()){
			bf.append("( NOT ");
		}
		bf.append("( ");
		if(this.isCaseInsensitive()){
			bf.append("lower(");
		}
		bf.append(this.sqlFieldConverter.toColumn(this.getField(),true));
		if(this.isCaseInsensitive()){
			bf.append(")");
		}
		bf.append(" like '");
		String likeParam = null;
		String sqlValue = null;
		try{
			sqlValue = super.getObjectFormatter().toString(this.getValue());
		}catch(Exception e){
			return "ERROR: "+e.getMessage();
		}
			
		// escape
		String escapeClausole = "";
		try{
			ISQLQueryObject sqlQueryObjectForEscape = null;
			if(sqlQueryObject!=null){
				sqlQueryObjectForEscape = sqlQueryObject.newSQLQueryObject();
			}
			else{
				sqlQueryObjectForEscape = SQLObjectFactory.createSQLQueryObject(TipiDatabase.POSTGRESQL); // lo uso come default per produrre sql
			}
			//System.out.println("DATABASE: "+sqlQueryObjectForEscape.getClass().getName());
			EscapeSQLPattern escapePattern = sqlQueryObjectForEscape.escapePatternValue(sqlValue);
			//System.out.println("ESCAPE PATTERN: "+escapePattern.isUseEscapeClausole());
			if(escapePattern.isUseEscapeClausole()){
				escapeClausole = " ESCAPE '"+escapePattern.getEscapeClausole()+"'";
			}
			sqlValue = escapePattern.getEscapeValue();
		}catch(Exception e){
			throw new ExpressionException(e);
		}
		
		// mode
		switch (this.getMode()) {
		case EXACT:
			if(this.isCaseInsensitive()){
				likeParam = sqlValue.toLowerCase();
			}else{
				likeParam = sqlValue;
			}
			break;
		case ANYWHERE:
			if(this.isCaseInsensitive()){
				likeParam = "%"+sqlValue.toLowerCase()+"%";
			}else{
				likeParam = "%"+sqlValue+"%";
			}
			break;
		case END:
			if(this.isCaseInsensitive()){
				likeParam = "%"+sqlValue.toLowerCase();
			}else{
				likeParam = "%"+sqlValue;
			}
			break;
		case START:
			if(this.isCaseInsensitive()){
				likeParam = sqlValue.toLowerCase()+"%";
			}else{
				likeParam = sqlValue+"%";
			}
			break;
		default:
			break;
		}
		
		bf.append(likeParam);
		bf.append("'");
		bf.append(escapeClausole);
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
