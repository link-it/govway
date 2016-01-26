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
package org.openspcoop2.core.api.model;

import org.openspcoop2.core.api.Resource;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Resource 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceModel extends AbstractModel<Resource> {

	public ResourceModel(){
	
		super();
	
		this.PATH = new Field("path",java.lang.String.class,"resource",Resource.class);
		this.METHOD = new Field("method",java.lang.String.class,"resource",Resource.class);
		this.TYPE = new Field("type",java.lang.String.class,"resource",Resource.class);
		this.MEDIA_TYPE = new Field("media-type",java.lang.String.class,"resource",Resource.class);
		this.RESPONSE_STATUS = new Field("response-status",java.lang.Integer.class,"resource",Resource.class);
		this.RESPONSE_MESSAGE = new Field("response-message",java.lang.String.class,"resource",Resource.class);
	
	}
	
	public ResourceModel(IField father){
	
		super(father);
	
		this.PATH = new ComplexField(father,"path",java.lang.String.class,"resource",Resource.class);
		this.METHOD = new ComplexField(father,"method",java.lang.String.class,"resource",Resource.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"resource",Resource.class);
		this.MEDIA_TYPE = new ComplexField(father,"media-type",java.lang.String.class,"resource",Resource.class);
		this.RESPONSE_STATUS = new ComplexField(father,"response-status",java.lang.Integer.class,"resource",Resource.class);
		this.RESPONSE_MESSAGE = new ComplexField(father,"response-message",java.lang.String.class,"resource",Resource.class);
	
	}
	
	

	public IField PATH = null;
	 
	public IField METHOD = null;
	 
	public IField TYPE = null;
	 
	public IField MEDIA_TYPE = null;
	 
	public IField RESPONSE_STATUS = null;
	 
	public IField RESPONSE_MESSAGE = null;
	 

	@Override
	public Class<Resource> getModeledClass(){
		return Resource.class;
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