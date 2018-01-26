/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageProperties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessageProperties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessagePropertiesModel extends AbstractModel<MessageProperties> {

	public MessagePropertiesModel(){
	
		super();
	
		this.PROPERTY = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PropertyModel(new Field("Property",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property.class,"MessageProperties",MessageProperties.class));
	
	}
	
	public MessagePropertiesModel(IField father){
	
		super(father);
	
		this.PROPERTY = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PropertyModel(new ComplexField(father,"Property",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property.class,"MessageProperties",MessageProperties.class));
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PropertyModel PROPERTY = null;
	 

	@Override
	public Class<MessageProperties> getModeledClass(){
		return MessageProperties.class;
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