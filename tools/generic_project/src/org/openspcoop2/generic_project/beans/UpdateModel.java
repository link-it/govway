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
package org.openspcoop2.generic_project.beans;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;

/**
 * UpdateModel
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UpdateModel {

	private IModel<?> model = null;
	private List<UpdateField> updateFiels = new ArrayList<UpdateField>();
	private IExpression condition = null;

	public UpdateModel(IModel<?> model) throws ServiceException{
		this(model, null, null);
	}
	public UpdateModel(IModel<?> model, List<UpdateField> updateFiels) throws ServiceException{
		this(model, null, updateFiels);
	}
	public UpdateModel(IModel<?> model, IExpression condition,List<UpdateField> updateFiels) throws ServiceException{
		if(model==null){
			throw new ServiceException("Parameter model is null");
		}
		this.model = model;
		if(updateFiels!=null && updateFiels.size()>0){
			for (UpdateField updateField : updateFiels) {
				this.addUpdateField(updateField);
			}
		}
		this.condition = condition;
	}
	
	public IModel<?> getModel() {
		return this.model;
	}
	public void setModel(IModel<?> model) throws ServiceException {
		if(model==null){
			throw new ServiceException("Parameter model is null");
		}
		this.model = model;
	}
	
	public int sizeUpdateFields(){
		return this.updateFiels.size();
	}
	public void addUpdateField(UpdateField updateField){
		this.updateFiels.add(updateField);
	}
	public UpdateField getUpdateField(int index){
		return this.updateFiels.get(index);
	}
	public UpdateField removeUpdateField(int index){
		return this.updateFiels.get(index);
	}	
	public List<UpdateField> getUpdateFiels() {
		return this.updateFiels;
	}
	public void setUpdateFiels(List<UpdateField> updateFiels) {
		this.updateFiels = updateFiels;
	}
	
	public IExpression getCondition() {
		return this.condition;
	}
	public void setCondition(IExpression condition) {
		this.condition = condition;
	}
}
