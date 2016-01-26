/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.SystemProperties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SystemProperties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SystemPropertiesModel extends AbstractModel<SystemProperties> {

	public SystemPropertiesModel(){
	
		super();
	
		this.SYSTEM_PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new Field("system-property",org.openspcoop2.core.config.Property.class,"system-properties",SystemProperties.class));
	
	}
	
	public SystemPropertiesModel(IField father){
	
		super(father);
	
		this.SYSTEM_PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new ComplexField(father,"system-property",org.openspcoop2.core.config.Property.class,"system-properties",SystemProperties.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.PropertyModel SYSTEM_PROPERTY = null;
	 

	@Override
	public Class<SystemProperties> getModeledClass(){
		return SystemProperties.class;
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