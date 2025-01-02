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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Actions;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Actions 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ActionsModel extends AbstractModel<Actions> {

	public ActionsModel(){
	
		super();
	
		this.ACTION = new eu.domibus.configuration.model.ActionModel(new Field("action",eu.domibus.configuration.Action.class,"actions",Actions.class));
	
	}
	
	public ActionsModel(IField father){
	
		super(father);
	
		this.ACTION = new eu.domibus.configuration.model.ActionModel(new ComplexField(father,"action",eu.domibus.configuration.Action.class,"actions",Actions.class));
	
	}
	
	

	public eu.domibus.configuration.model.ActionModel ACTION = null;
	 

	@Override
	public Class<Actions> getModeledClass(){
		return Actions.class;
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