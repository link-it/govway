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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Legs;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Legs 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LegsModel extends AbstractModel<Legs> {

	public LegsModel(){
	
		super();
	
		this.LEG = new eu.domibus.configuration.model.LegModel(new Field("leg",eu.domibus.configuration.Leg.class,"legs",Legs.class));
	
	}
	
	public LegsModel(IField father){
	
		super(father);
	
		this.LEG = new eu.domibus.configuration.model.LegModel(new ComplexField(father,"leg",eu.domibus.configuration.Leg.class,"legs",Legs.class));
	
	}
	
	

	public eu.domibus.configuration.model.LegModel LEG = null;
	 

	@Override
	public Class<Legs> getModeledClass(){
		return Legs.class;
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