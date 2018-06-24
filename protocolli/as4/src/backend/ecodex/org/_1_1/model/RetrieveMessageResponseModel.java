/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.RetrieveMessageResponse;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RetrieveMessageResponse 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RetrieveMessageResponseModel extends AbstractModel<RetrieveMessageResponse> {

	public RetrieveMessageResponseModel(){
	
		super();
	
		this.BODYLOAD = new backend.ecodex.org._1_1.model.LargePayloadTypeModel(new Field("bodyload",backend.ecodex.org._1_1.LargePayloadType.class,"retrieveMessageResponse",RetrieveMessageResponse.class));
		this.PAYLOAD = new backend.ecodex.org._1_1.model.LargePayloadTypeModel(new Field("payload",backend.ecodex.org._1_1.LargePayloadType.class,"retrieveMessageResponse",RetrieveMessageResponse.class));
	
	}
	
	public RetrieveMessageResponseModel(IField father){
	
		super(father);
	
		this.BODYLOAD = new backend.ecodex.org._1_1.model.LargePayloadTypeModel(new ComplexField(father,"bodyload",backend.ecodex.org._1_1.LargePayloadType.class,"retrieveMessageResponse",RetrieveMessageResponse.class));
		this.PAYLOAD = new backend.ecodex.org._1_1.model.LargePayloadTypeModel(new ComplexField(father,"payload",backend.ecodex.org._1_1.LargePayloadType.class,"retrieveMessageResponse",RetrieveMessageResponse.class));
	
	}
	
	

	public backend.ecodex.org._1_1.model.LargePayloadTypeModel BODYLOAD = null;
	 
	public backend.ecodex.org._1_1.model.LargePayloadTypeModel PAYLOAD = null;
	 

	@Override
	public Class<RetrieveMessageResponse> getModeledClass(){
		return RetrieveMessageResponse.class;
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