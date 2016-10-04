/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Message;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Message 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageModel extends AbstractModel<Message> {

	public MessageModel(){
	
		super();
	
		this.PART = new org.openspcoop2.core.registry.model.MessagePartModel(new Field("part",org.openspcoop2.core.registry.MessagePart.class,"message",Message.class));
		this.USE = new Field("use",java.lang.String.class,"message",Message.class);
		this.SOAP_NAMESPACE = new Field("soap-namespace",java.lang.String.class,"message",Message.class);
	
	}
	
	public MessageModel(IField father){
	
		super(father);
	
		this.PART = new org.openspcoop2.core.registry.model.MessagePartModel(new ComplexField(father,"part",org.openspcoop2.core.registry.MessagePart.class,"message",Message.class));
		this.USE = new ComplexField(father,"use",java.lang.String.class,"message",Message.class);
		this.SOAP_NAMESPACE = new ComplexField(father,"soap-namespace",java.lang.String.class,"message",Message.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.MessagePartModel PART = null;
	 
	public IField USE = null;
	 
	public IField SOAP_NAMESPACE = null;
	 

	@Override
	public Class<Message> getModeledClass(){
		return Message.class;
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