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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.MtomProcessorFlow;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model MtomProcessorFlow 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MtomProcessorFlowModel extends AbstractModel<MtomProcessorFlow> {

	public MtomProcessorFlowModel(){
	
		super();
	
		this.PARAMETER = new org.openspcoop2.core.config.model.MtomProcessorFlowParameterModel(new Field("parameter",org.openspcoop2.core.config.MtomProcessorFlowParameter.class,"mtom-processor-flow",MtomProcessorFlow.class));
		this.MODE = new Field("mode",java.lang.String.class,"mtom-processor-flow",MtomProcessorFlow.class);
	
	}
	
	public MtomProcessorFlowModel(IField father){
	
		super(father);
	
		this.PARAMETER = new org.openspcoop2.core.config.model.MtomProcessorFlowParameterModel(new ComplexField(father,"parameter",org.openspcoop2.core.config.MtomProcessorFlowParameter.class,"mtom-processor-flow",MtomProcessorFlow.class));
		this.MODE = new ComplexField(father,"mode",java.lang.String.class,"mtom-processor-flow",MtomProcessorFlow.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.MtomProcessorFlowParameterModel PARAMETER = null;
	 
	public IField MODE = null;
	 

	@Override
	public Class<MtomProcessorFlow> getModeledClass(){
		return MtomProcessorFlow.class;
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