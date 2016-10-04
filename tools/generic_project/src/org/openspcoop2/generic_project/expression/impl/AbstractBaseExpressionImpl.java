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
package org.openspcoop2.generic_project.expression.impl;

import java.util.List;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;

/**
 * AbstractBaseExpressionImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseExpressionImpl {

	private IObjectFormatter objectFormatter;
	
	public AbstractBaseExpressionImpl(IObjectFormatter objectFormatter){
		this.objectFormatter = objectFormatter;
	}
	
	private boolean not = false;

	public boolean isNot() {
		return this.not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}

	public IObjectFormatter getObjectFormatter() {
		return this.objectFormatter;
	}

	public void setObjectFormatter(IObjectFormatter objectFormatter) {
		this.objectFormatter = objectFormatter;
	}

	public abstract boolean inUseField(IField field) throws ExpressionNotImplementedException, ExpressionException;
	
	public abstract List<Object> getFieldValues(IField field) throws ExpressionNotImplementedException, ExpressionException;
		
	public abstract boolean inUseModel(IModel<?> model) throws ExpressionNotImplementedException, ExpressionException;
	
	
	public abstract List<IField> getFields() throws ExpressionNotImplementedException, ExpressionException;

}
