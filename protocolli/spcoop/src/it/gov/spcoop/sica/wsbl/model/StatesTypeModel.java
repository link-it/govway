/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.StatesType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatesType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatesTypeModel extends AbstractModel<StatesType> {

	public StatesTypeModel(){
	
		super();
	
		this.STATE_INITIAL = new it.gov.spcoop.sica.wsbl.model.StateTypeInitialModel(new Field("state-initial",it.gov.spcoop.sica.wsbl.StateTypeInitial.class,"statesType",StatesType.class));
		this.STATE_FINAL = new it.gov.spcoop.sica.wsbl.model.StateTypeFinalModel(new Field("state-final",it.gov.spcoop.sica.wsbl.StateTypeFinal.class,"statesType",StatesType.class));
		this.STATE = new it.gov.spcoop.sica.wsbl.model.StateTypeNormalModel(new Field("state",it.gov.spcoop.sica.wsbl.StateTypeNormal.class,"statesType",StatesType.class));
	
	}
	
	public StatesTypeModel(IField father){
	
		super(father);
	
		this.STATE_INITIAL = new it.gov.spcoop.sica.wsbl.model.StateTypeInitialModel(new ComplexField(father,"state-initial",it.gov.spcoop.sica.wsbl.StateTypeInitial.class,"statesType",StatesType.class));
		this.STATE_FINAL = new it.gov.spcoop.sica.wsbl.model.StateTypeFinalModel(new ComplexField(father,"state-final",it.gov.spcoop.sica.wsbl.StateTypeFinal.class,"statesType",StatesType.class));
		this.STATE = new it.gov.spcoop.sica.wsbl.model.StateTypeNormalModel(new ComplexField(father,"state",it.gov.spcoop.sica.wsbl.StateTypeNormal.class,"statesType",StatesType.class));
	
	}
	
	

	public it.gov.spcoop.sica.wsbl.model.StateTypeInitialModel STATE_INITIAL = null;
	 
	public it.gov.spcoop.sica.wsbl.model.StateTypeFinalModel STATE_FINAL = null;
	 
	public it.gov.spcoop.sica.wsbl.model.StateTypeNormalModel STATE = null;
	 

	@Override
	public Class<StatesType> getModeledClass(){
		return StatesType.class;
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