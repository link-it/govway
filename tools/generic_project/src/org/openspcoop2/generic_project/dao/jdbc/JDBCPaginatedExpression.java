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
package org.openspcoop2.generic_project.dao.jdbc;


import java.util.List;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.expression.impl.sql.PaginatedExpressionSQL;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * JDBCPaginatedExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPaginatedExpression extends PaginatedExpressionSQL  {

	public JDBCPaginatedExpression(ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		super(sqlFieldConverter);
	}
	public JDBCPaginatedExpression(JDBCExpression expression) throws ExpressionException{
		super(expression);
	}
	
	public String toSqlForPreparedStatement(List<Object> oggetti) throws ExpressionException{
		return this.toSqlPreparedStatement(oggetti);
	}
	
	public void toSqlForPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti) throws ExpressionException{
		this.toSqlPreparedStatement(sqlQueryObject,oggetti);
	}
	
	public void toSqlForPreparedStatementWithFromCondition(ISQLQueryObject sqlQueryObject,List<Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		this.toSqlPreparedStatementWithFromCondition(sqlQueryObject,oggetti,tableNamePrincipale);
	}
}
