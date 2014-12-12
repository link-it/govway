/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.dao.jpa;


import java.util.Hashtable;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.sql.ExpressionSQL;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * JPAExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JPAExpression extends ExpressionSQL  {

	public JPAExpression(ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		super(sqlFieldConverter);
	}
	public JPAExpression(JPAPaginatedExpression expression) throws ExpressionException{
		super(expression);
	}
	
	public String toSqlForJPA(Hashtable<String, Object> oggetti) throws ExpressionException{
		return this.toSqlJPA(oggetti);
	}
	
	public void toSqlForJPA(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti) throws ExpressionException{
		this.toSqlJPA(sqlQueryObject,oggetti);
	}
	
	public void toSqlForJPAWithFromCondition(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		this.toSqlJPAWithFromCondition(sqlQueryObject,oggetti,tableNamePrincipale);
	}
}
