/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PartProperties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartPropertiesModel extends AbstractModel<PartProperties> {

	public PartPropertiesModel(){
	
		super();
	
		this.PROPERTY = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PropertyModel(new Field("Property",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property.class,"PartProperties",PartProperties.class));
	
	}
	
	public PartPropertiesModel(IField father){
	
		super(father);
	
		this.PROPERTY = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PropertyModel(new ComplexField(father,"Property",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property.class,"PartProperties",PartProperties.class));
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PropertyModel PROPERTY = null;
	 

	@Override
	public Class<PartProperties> getModeledClass(){
		return PartProperties.class;
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