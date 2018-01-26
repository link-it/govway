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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Payload;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Payload 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PayloadModel extends AbstractModel<Payload> {

	public PayloadModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"payload",Payload.class);
		this.CID = new Field("cid",java.lang.String.class,"payload",Payload.class);
		this.MIME_TYPE = new Field("mimeType",java.lang.String.class,"payload",Payload.class);
		this.IN_BODY = new Field("inBody",java.lang.String.class,"payload",Payload.class);
		this.SCHEMA_FILE = new Field("schemaFile",java.net.URI.class,"payload",Payload.class);
		this.MAX_SIZE = new Field("maxSize",java.lang.Integer.class,"payload",Payload.class);
		this.REQUIRED = new Field("required",boolean.class,"payload",Payload.class);
	
	}
	
	public PayloadModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"payload",Payload.class);
		this.CID = new ComplexField(father,"cid",java.lang.String.class,"payload",Payload.class);
		this.MIME_TYPE = new ComplexField(father,"mimeType",java.lang.String.class,"payload",Payload.class);
		this.IN_BODY = new ComplexField(father,"inBody",java.lang.String.class,"payload",Payload.class);
		this.SCHEMA_FILE = new ComplexField(father,"schemaFile",java.net.URI.class,"payload",Payload.class);
		this.MAX_SIZE = new ComplexField(father,"maxSize",java.lang.Integer.class,"payload",Payload.class);
		this.REQUIRED = new ComplexField(father,"required",boolean.class,"payload",Payload.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField CID = null;
	 
	public IField MIME_TYPE = null;
	 
	public IField IN_BODY = null;
	 
	public IField SCHEMA_FILE = null;
	 
	public IField MAX_SIZE = null;
	 
	public IField REQUIRED = null;
	 

	@Override
	public Class<Payload> getModeledClass(){
		return Payload.class;
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