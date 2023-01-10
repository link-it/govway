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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.ContentLength;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ContentLength 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentLengthModel extends AbstractModel<ContentLength> {

	public ContentLengthModel(){
	
		super();
	
		this.OUTGOING_SIZE = new Field("outgoing-size",java.lang.Long.class,"content-length",ContentLength.class);
		this.INCOMING_SIZE = new Field("incoming-size",java.lang.Long.class,"content-length",ContentLength.class);
		this.INCOMING_SIZE_FORCED = new Field("incoming-size-forced",java.lang.Long.class,"content-length",ContentLength.class);
	
	}
	
	public ContentLengthModel(IField father){
	
		super(father);
	
		this.OUTGOING_SIZE = new ComplexField(father,"outgoing-size",java.lang.Long.class,"content-length",ContentLength.class);
		this.INCOMING_SIZE = new ComplexField(father,"incoming-size",java.lang.Long.class,"content-length",ContentLength.class);
		this.INCOMING_SIZE_FORCED = new ComplexField(father,"incoming-size-forced",java.lang.Long.class,"content-length",ContentLength.class);
	
	}
	
	

	public IField OUTGOING_SIZE = null;
	 
	public IField INCOMING_SIZE = null;
	 
	public IField INCOMING_SIZE_FORCED = null;
	 

	@Override
	public Class<ContentLength> getModeledClass(){
		return ContentLength.class;
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