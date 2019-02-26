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

import eu.domibus.configuration.Properties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Properties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertiesModel extends AbstractModel<Properties> {

	public PropertiesModel(){
	
		super();
	
		this.PROPERTY = new eu.domibus.configuration.model.PropertyModel(new Field("property",eu.domibus.configuration.Property.class,"properties",Properties.class));
		this.PROPERTY_SET = new eu.domibus.configuration.model.PropertySetModel(new Field("propertySet",eu.domibus.configuration.PropertySet.class,"properties",Properties.class));
	
	}
	
	public PropertiesModel(IField father){
	
		super(father);
	
		this.PROPERTY = new eu.domibus.configuration.model.PropertyModel(new ComplexField(father,"property",eu.domibus.configuration.Property.class,"properties",Properties.class));
		this.PROPERTY_SET = new eu.domibus.configuration.model.PropertySetModel(new ComplexField(father,"propertySet",eu.domibus.configuration.PropertySet.class,"properties",Properties.class));
	
	}
	
	

	public eu.domibus.configuration.model.PropertyModel PROPERTY = null;
	 
	public eu.domibus.configuration.model.PropertySetModel PROPERTY_SET = null;
	 

	@Override
	public Class<Properties> getModeledClass(){
		return Properties.class;
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