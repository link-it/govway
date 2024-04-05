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

import eu.domibus.configuration.Property;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Property 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertyModel extends AbstractModel<Property> {

	public PropertyModel(){
	
		super();
	
		this.VALUE = new eu.domibus.configuration.model.PropertyValueModel(new Field("value",eu.domibus.configuration.PropertyValue.class,"property",Property.class));
		this.NAME = new Field("name",java.lang.String.class,"property",Property.class);
		this.KEY = new Field("key",java.lang.String.class,"property",Property.class);
		this.DATATYPE = new Field("datatype",java.lang.String.class,"property",Property.class);
		this.REQUIRED = new Field("required",boolean.class,"property",Property.class);
	
	}
	
	public PropertyModel(IField father){
	
		super(father);
	
		this.VALUE = new eu.domibus.configuration.model.PropertyValueModel(new ComplexField(father,"value",eu.domibus.configuration.PropertyValue.class,"property",Property.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"property",Property.class);
		this.KEY = new ComplexField(father,"key",java.lang.String.class,"property",Property.class);
		this.DATATYPE = new ComplexField(father,"datatype",java.lang.String.class,"property",Property.class);
		this.REQUIRED = new ComplexField(father,"required",boolean.class,"property",Property.class);
	
	}
	
	

	public eu.domibus.configuration.model.PropertyValueModel VALUE = null;
	 
	public IField NAME = null;
	 
	public IField KEY = null;
	 
	public IField DATATYPE = null;
	 
	public IField REQUIRED = null;
	 

	@Override
	public Class<Property> getModeledClass(){
		return Property.class;
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