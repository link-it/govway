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
package org.openspcoop2.generic_project.expression.impl;

import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;


/**
 * PaginatedExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PaginatedExpressionImpl extends ExpressionImpl implements IPaginatedExpression {

	public PaginatedExpressionImpl() throws ExpressionException {
		super();
	}
	public PaginatedExpressionImpl(IObjectFormatter objectFormatter) throws ExpressionException{
		super(objectFormatter);
	}
	public PaginatedExpressionImpl(ExpressionImpl expr) throws ExpressionException{
		super(expr);
	}
	
	private Integer limit = null;
	private Integer offset = null;
	
	/**
	 * Sets the maximum number of results
	 * 
	 * @param limit maximum number of results 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IPaginatedExpression limit(int limit) throws ExpressionNotImplementedException,
			ExpressionException {
		this.limit = limit;
		return this;
	}

	/**
	 * Set the offset from which to search
	 * 
	 * @param offset offset
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IPaginatedExpression offset(int offset) throws ExpressionNotImplementedException,
			ExpressionException {
		this.offset = offset;
		return this;
	}

	public Integer getLimit() {
		return this.limit;
	}

	public Integer getOffset() {
		return this.offset;
	}



	
	/* ToString */
	@Override
	public String toString(){
		String s = super.toString();
		if(s==null){
			return s;
		}
		else{
			StringBuffer bf = new StringBuffer();
			bf.append(s);
			
			if(this.limit!=null && this.limit>=0){
				bf.append(" LIMIT "+this.limit);
			}
			
			if(this.offset!=null && this.offset>=0){
				bf.append(" OFFSET "+this.offset);
			}
			
			return bf.toString();
		}
	}
}
