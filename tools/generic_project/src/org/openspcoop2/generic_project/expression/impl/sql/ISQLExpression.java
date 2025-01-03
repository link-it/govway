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

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.sql.ISQLQueryObject;


/**
 * ISQLExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISQLExpression {

	public String toSql()throws ExpressionException;
	
	public String toSqlPreparedStatement(List<Object> oggetti)throws ExpressionException;
	
	public String toSqlJPA(Map<String, Object> oggetti)throws ExpressionException;
	
	public void toSql(ISQLQueryObject sqlQueryObject)throws ExpressionException;
	
	public void toSqlPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti)throws ExpressionException;
	
	public void toSqlJPA(ISQLQueryObject sqlQueryObject,Map<String, Object> oggetti)throws ExpressionException;
	
}
