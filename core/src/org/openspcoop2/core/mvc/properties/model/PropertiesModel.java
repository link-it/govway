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

import org.openspcoop2.core.mvc.properties.Properties;

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
	
		this.COLLECTION = new org.openspcoop2.core.mvc.properties.model.CollectionModel(new Field("collection",org.openspcoop2.core.mvc.properties.Collection.class,"properties",Properties.class));
	
	}
	
	public PropertiesModel(IField father){
	
		super(father);
	
		this.COLLECTION = new org.openspcoop2.core.mvc.properties.model.CollectionModel(new ComplexField(father,"collection",org.openspcoop2.core.mvc.properties.Collection.class,"properties",Properties.class));
	
	}
	
	

	public org.openspcoop2.core.mvc.properties.model.CollectionModel COLLECTION = null;
	 

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