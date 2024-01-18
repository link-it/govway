/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package it.cnipa.collprofiles.model;

import it.cnipa.collprofiles.OperationListType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model OperationListType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperationListTypeModel extends AbstractModel<OperationListType> {

	public OperationListTypeModel(){
	
		super();
	
		this.OPERATION = new it.cnipa.collprofiles.model.OperationTypeModel(new Field("operation",it.cnipa.collprofiles.OperationType.class,"operationListType",OperationListType.class));
	
	}
	
	public OperationListTypeModel(IField father){
	
		super(father);
	
		this.OPERATION = new it.cnipa.collprofiles.model.OperationTypeModel(new ComplexField(father,"operation",it.cnipa.collprofiles.OperationType.class,"operationListType",OperationListType.class));
	
	}
	
	

	public it.cnipa.collprofiles.model.OperationTypeModel OPERATION = null;
	 

	@Override
	public Class<OperationListType> getModeledClass(){
		return OperationListType.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}