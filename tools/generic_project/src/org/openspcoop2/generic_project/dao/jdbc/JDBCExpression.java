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
package org.openspcoop2.generic_project.dao.jdbc;


import java.util.List;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.sql.ExpressionSQL;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * JDBCExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCExpression extends ExpressionSQL  {

	public JDBCExpression(ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		super(sqlFieldConverter);
	}
	public JDBCExpression(JDBCPaginatedExpression expression) throws ExpressionException{
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
