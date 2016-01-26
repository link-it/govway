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
package org.openspcoop2.generic_project.expression;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;

/**
 * IPaginatedExpression
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IPaginatedExpression extends IExpression {

	/**
	 * Sets the maximum number of results
	 * 
	 * @param limit maximum number of results 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IPaginatedExpression limit(int limit) throws ExpressionNotImplementedException,ExpressionException;
	
	/**
	 * Set the offset from which to search
	 * 
	 * @param offset offset
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	public IPaginatedExpression offset(int offset) throws ExpressionNotImplementedException,ExpressionException;
	
}