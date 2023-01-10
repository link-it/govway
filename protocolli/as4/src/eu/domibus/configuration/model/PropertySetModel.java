/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import eu.domibus.configuration.PropertySet;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PropertySet 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertySetModel extends AbstractModel<PropertySet> {

	public PropertySetModel(){
	
		super();
	
		this.PROPERTY_REF = new eu.domibus.configuration.model.PropertyRefModel(new Field("propertyRef",eu.domibus.configuration.PropertyRef.class,"propertySet",PropertySet.class));
		this.NAME = new Field("name",java.lang.String.class,"propertySet",PropertySet.class);
	
	}
	
	public PropertySetModel(IField father){
	
		super(father);
	
		this.PROPERTY_REF = new eu.domibus.configuration.model.PropertyRefModel(new ComplexField(father,"propertyRef",eu.domibus.configuration.PropertyRef.class,"propertySet",PropertySet.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"propertySet",PropertySet.class);
	
	}
	
	

	public eu.domibus.configuration.model.PropertyRefModel PROPERTY_REF = null;
	 
	public IField NAME = null;
	 

	@Override
	public Class<PropertySet> getModeledClass(){
		return PropertySet.class;
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