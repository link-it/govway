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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.Message;

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
	
		this.NAME = new Field("name",java.lang.String.class,"message",Message.class);
		this.TYPE = new Field("type",java.lang.String.class,"message",Message.class);
		this.SOURCE = new Field("source",java.lang.String.class,"message",Message.class);
		this.TARGET = new Field("target",java.lang.String.class,"message",Message.class);
	
	}
	
	public MessageModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"message",Message.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"message",Message.class);
		this.SOURCE = new ComplexField(father,"source",java.lang.String.class,"message",Message.class);
		this.TARGET = new ComplexField(father,"target",java.lang.String.class,"message",Message.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField TYPE = null;
	 
	public IField SOURCE = null;
	 
	public IField TARGET = null;
	 

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