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
package org.openspcoop2.core.mvc.properties.model;

import org.openspcoop2.core.mvc.properties.Property;

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
	
		this.NAME = new Field("name",java.lang.String.class,"property",Property.class);
		this.SELECTED_VALUE = new Field("selectedValue",java.lang.String.class,"property",Property.class);
		this.UNSELECTED_VALUE = new Field("unselectedValue",java.lang.String.class,"property",Property.class);
		this.APPEND = new Field("append",boolean.class,"property",Property.class);
		this.APPEND_SEPARATOR = new Field("appendSeparator",java.lang.String.class,"property",Property.class);
		this.PROPERTIES = new Field("properties",java.lang.String.class,"property",Property.class);
		this.FORCE = new Field("force",boolean.class,"property",Property.class);
	
	}
	
	public PropertyModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"property",Property.class);
		this.SELECTED_VALUE = new ComplexField(father,"selectedValue",java.lang.String.class,"property",Property.class);
		this.UNSELECTED_VALUE = new ComplexField(father,"unselectedValue",java.lang.String.class,"property",Property.class);
		this.APPEND = new ComplexField(father,"append",boolean.class,"property",Property.class);
		this.APPEND_SEPARATOR = new ComplexField(father,"appendSeparator",java.lang.String.class,"property",Property.class);
		this.PROPERTIES = new ComplexField(father,"properties",java.lang.String.class,"property",Property.class);
		this.FORCE = new ComplexField(father,"force",boolean.class,"property",Property.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField SELECTED_VALUE = null;
	 
	public IField UNSELECTED_VALUE = null;
	 
	public IField APPEND = null;
	 
	public IField APPEND_SEPARATOR = null;
	 
	public IField PROPERTIES = null;
	 
	public IField FORCE = null;
	 

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