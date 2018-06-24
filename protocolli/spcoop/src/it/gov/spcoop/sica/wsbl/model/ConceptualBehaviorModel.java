/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import it.gov.spcoop.sica.wsbl.ConceptualBehavior;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConceptualBehavior 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConceptualBehaviorModel extends AbstractModel<ConceptualBehavior> {

	public ConceptualBehaviorModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"ConceptualBehavior",ConceptualBehavior.class);
		this.STATES = new it.gov.spcoop.sica.wsbl.model.StatesTypeModel(new Field("states",it.gov.spcoop.sica.wsbl.StatesType.class,"ConceptualBehavior",ConceptualBehavior.class));
		this.TRANSITIONS = new it.gov.spcoop.sica.wsbl.model.TransitionsTypeModel(new Field("transitions",it.gov.spcoop.sica.wsbl.TransitionsType.class,"ConceptualBehavior",ConceptualBehavior.class));
	
	}
	
	public ConceptualBehaviorModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"ConceptualBehavior",ConceptualBehavior.class);
		this.STATES = new it.gov.spcoop.sica.wsbl.model.StatesTypeModel(new ComplexField(father,"states",it.gov.spcoop.sica.wsbl.StatesType.class,"ConceptualBehavior",ConceptualBehavior.class));
		this.TRANSITIONS = new it.gov.spcoop.sica.wsbl.model.TransitionsTypeModel(new ComplexField(father,"transitions",it.gov.spcoop.sica.wsbl.TransitionsType.class,"ConceptualBehavior",ConceptualBehavior.class));
	
	}
	
	

	public IField NAME = null;
	 
	public it.gov.spcoop.sica.wsbl.model.StatesTypeModel STATES = null;
	 
	public it.gov.spcoop.sica.wsbl.model.TransitionsTypeModel TRANSITIONS = null;
	 

	@Override
	public Class<ConceptualBehavior> getModeledClass(){
		return ConceptualBehavior.class;
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