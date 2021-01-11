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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.MessagePart;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MessagePart 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessagePartModel extends AbstractModel<MessagePart> {

	public MessagePartModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"message-part",MessagePart.class);
		this.ELEMENT_NAME = new Field("element-name",java.lang.String.class,"message-part",MessagePart.class);
		this.ELEMENT_NAMESPACE = new Field("element-namespace",java.lang.String.class,"message-part",MessagePart.class);
		this.TYPE_NAME = new Field("type-name",java.lang.String.class,"message-part",MessagePart.class);
		this.TYPE_NAMESPACE = new Field("type-namespace",java.lang.String.class,"message-part",MessagePart.class);
	
	}
	
	public MessagePartModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"message-part",MessagePart.class);
		this.ELEMENT_NAME = new ComplexField(father,"element-name",java.lang.String.class,"message-part",MessagePart.class);
		this.ELEMENT_NAMESPACE = new ComplexField(father,"element-namespace",java.lang.String.class,"message-part",MessagePart.class);
		this.TYPE_NAME = new ComplexField(father,"type-name",java.lang.String.class,"message-part",MessagePart.class);
		this.TYPE_NAMESPACE = new ComplexField(father,"type-namespace",java.lang.String.class,"message-part",MessagePart.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField ELEMENT_NAME = null;
	 
	public IField ELEMENT_NAMESPACE = null;
	 
	public IField TYPE_NAME = null;
	 
	public IField TYPE_NAMESPACE = null;
	 

	@Override
	public Class<MessagePart> getModeledClass(){
		return MessagePart.class;
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