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
package eu.domibus.configuration.model;

import eu.domibus.configuration.PropertyValue;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PropertyValue 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertyValueModel extends AbstractModel<PropertyValue> {

	public PropertyValueModel(){
	
		super();
	
		this.URL = new eu.domibus.configuration.model.PropertyValueUrlModel(new Field("url",eu.domibus.configuration.PropertyValueUrl.class,"PropertyValue",PropertyValue.class));
		this.HEADER = new eu.domibus.configuration.model.PropertyValueHeaderModel(new Field("header",eu.domibus.configuration.PropertyValueHeader.class,"PropertyValue",PropertyValue.class));
	
	}
	
	public PropertyValueModel(IField father){
	
		super(father);
	
		this.URL = new eu.domibus.configuration.model.PropertyValueUrlModel(new ComplexField(father,"url",eu.domibus.configuration.PropertyValueUrl.class,"PropertyValue",PropertyValue.class));
		this.HEADER = new eu.domibus.configuration.model.PropertyValueHeaderModel(new ComplexField(father,"header",eu.domibus.configuration.PropertyValueHeader.class,"PropertyValue",PropertyValue.class));
	
	}
	
	

	public eu.domibus.configuration.model.PropertyValueUrlModel URL = null;
	 
	public eu.domibus.configuration.model.PropertyValueHeaderModel HEADER = null;
	 

	@Override
	public Class<PropertyValue> getModeledClass(){
		return PropertyValue.class;
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