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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.MtomProcessor;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MtomProcessor 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MtomProcessorModel extends AbstractModel<MtomProcessor> {

	public MtomProcessorModel(){
	
		super();
	
		this.REQUEST_FLOW = new org.openspcoop2.core.config.model.MtomProcessorFlowModel(new Field("request-flow",org.openspcoop2.core.config.MtomProcessorFlow.class,"mtom-processor",MtomProcessor.class));
		this.RESPONSE_FLOW = new org.openspcoop2.core.config.model.MtomProcessorFlowModel(new Field("response-flow",org.openspcoop2.core.config.MtomProcessorFlow.class,"mtom-processor",MtomProcessor.class));
	
	}
	
	public MtomProcessorModel(IField father){
	
		super(father);
	
		this.REQUEST_FLOW = new org.openspcoop2.core.config.model.MtomProcessorFlowModel(new ComplexField(father,"request-flow",org.openspcoop2.core.config.MtomProcessorFlow.class,"mtom-processor",MtomProcessor.class));
		this.RESPONSE_FLOW = new org.openspcoop2.core.config.model.MtomProcessorFlowModel(new ComplexField(father,"response-flow",org.openspcoop2.core.config.MtomProcessorFlow.class,"mtom-processor",MtomProcessor.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.MtomProcessorFlowModel REQUEST_FLOW = null;
	 
	public org.openspcoop2.core.config.model.MtomProcessorFlowModel RESPONSE_FLOW = null;
	 

	@Override
	public Class<MtomProcessor> getModeledClass(){
		return MtomProcessor.class;
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