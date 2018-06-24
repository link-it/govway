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

import backend.ecodex.org._1_1.GetStatusRequest;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GetStatusRequest 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GetStatusRequestModel extends AbstractModel<GetStatusRequest> {

	public GetStatusRequestModel(){
	
		super();
	
		this.MESSAGE_ID = new Field("messageID",java.lang.String.class,"getStatusRequest",GetStatusRequest.class);
	
	}
	
	public GetStatusRequestModel(IField father){
	
		super(father);
	
		this.MESSAGE_ID = new ComplexField(father,"messageID",java.lang.String.class,"getStatusRequest",GetStatusRequest.class);
	
	}
	
	

	public IField MESSAGE_ID = null;
	 

	@Override
	public Class<GetStatusRequest> getModeledClass(){
		return GetStatusRequest.class;
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