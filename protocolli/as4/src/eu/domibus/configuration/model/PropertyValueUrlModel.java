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

import eu.domibus.configuration.PropertyValueUrl;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PropertyValueUrl 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertyValueUrlModel extends AbstractModel<PropertyValueUrl> {

	public PropertyValueUrlModel(){
	
		super();
	
		this.PATTERN = new Field("pattern",java.lang.String.class,"PropertyValueUrl",PropertyValueUrl.class);
	
	}
	
	public PropertyValueUrlModel(IField father){
	
		super(father);
	
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"PropertyValueUrl",PropertyValueUrl.class);
	
	}
	
	

	public IField PATTERN = null;
	 

	@Override
	public Class<PropertyValueUrl> getModeledClass(){
		return PropertyValueUrl.class;
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