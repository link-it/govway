/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.LargePayloadType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model LargePayloadType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LargePayloadTypeModel extends AbstractModel<LargePayloadType> {

	public LargePayloadTypeModel(){
	
		super();
	
		this.VALUE = new Field("value",byte[].class,"LargePayloadType",LargePayloadType.class);
		this.PAYLOAD_ID = new Field("payloadId",java.lang.String.class,"LargePayloadType",LargePayloadType.class);
		this.CONTENT_TYPE = new Field("contentType",java.lang.String.class,"LargePayloadType",LargePayloadType.class);
	
	}
	
	public LargePayloadTypeModel(IField father){
	
		super(father);
	
		this.VALUE = new ComplexField(father,"value",byte[].class,"LargePayloadType",LargePayloadType.class);
		this.PAYLOAD_ID = new ComplexField(father,"payloadId",java.lang.String.class,"LargePayloadType",LargePayloadType.class);
		this.CONTENT_TYPE = new ComplexField(father,"contentType",java.lang.String.class,"LargePayloadType",LargePayloadType.class);
	
	}
	
	

	public IField VALUE = null;
	 
	public IField PAYLOAD_ID = null;
	 
	public IField CONTENT_TYPE = null;
	 

	@Override
	public Class<LargePayloadType> getModeledClass(){
		return LargePayloadType.class;
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