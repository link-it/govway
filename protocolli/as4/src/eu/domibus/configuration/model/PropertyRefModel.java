/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import eu.domibus.configuration.PropertyRef;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PropertyRef 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertyRefModel extends AbstractModel<PropertyRef> {

	public PropertyRefModel(){
	
		super();
	
		this.PROPERTY = new Field("property",java.lang.String.class,"propertyRef",PropertyRef.class);
	
	}
	
	public PropertyRefModel(IField father){
	
		super(father);
	
		this.PROPERTY = new ComplexField(father,"property",java.lang.String.class,"propertyRef",PropertyRef.class);
	
	}
	
	

	public IField PROPERTY = null;
	 

	@Override
	public Class<PropertyRef> getModeledClass(){
		return PropertyRef.class;
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