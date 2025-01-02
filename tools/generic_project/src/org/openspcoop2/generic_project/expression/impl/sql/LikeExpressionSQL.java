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

import java.util.List;
import java.util.Map;

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
		if(this.isCaseInsensitive()){
			bf.append("lower(");
		}
		bf.append(this.sqlFieldConverter.toColumn(this.getField(),true));
		if(this.isCaseInsensitive()){
			bf.append(")");
		}
		bf.append(" like '");
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
			/**System.out.println("DATABASE: "+sqlQueryObjectForEscape.getClass().getName());*/
			EscapeSQLPattern escapePattern = sqlQueryObjectForEscape.escapePatternValue(sqlValue);
			/**System.out.println("ESCAPE PATTERN: "+escapePattern.isUseEscapeClausole());*/
			if(escapePattern.isUseEscapeClausole()){
				escapeClausole = " ESCAPE '"+escapePattern.getEscapeClausole()+"'";
			}
			sqlValue = escapePattern.getEscapeValue();
		}catch(Exception e){
			throw new ExpressionException(e);
		}
		
		// mode
		String likeParam = getLikeParamSql(sqlValue);
		bf.append(likeParam);
		bf.append("'");
		bf.append(escapeClausole);
		bf.append(" )");
		if(isNot()){
			bf.append(" )");
		}
		return bf.toString();
	}
	private String getLikeParamSql(String sqlValue) {
		String likeParam = null;
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
		return likeParam;
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
