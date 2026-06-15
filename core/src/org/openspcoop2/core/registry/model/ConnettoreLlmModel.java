/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import org.openspcoop2.core.registry.ConnettoreLlm;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConnettoreLlm 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreLlmModel extends AbstractModel<ConnettoreLlm> {

	public ConnettoreLlmModel(){
	
		super();
	
		this.PROVIDER = new org.openspcoop2.core.registry.model.ConnettoreLlmProviderRefModel(new Field("provider",org.openspcoop2.core.registry.ConnettoreLlmProviderRef.class,"connettore-llm",ConnettoreLlm.class));
	
	}
	
	public ConnettoreLlmModel(IField father){
	
		super(father);
	
		this.PROVIDER = new org.openspcoop2.core.registry.model.ConnettoreLlmProviderRefModel(new ComplexField(father,"provider",org.openspcoop2.core.registry.ConnettoreLlmProviderRef.class,"connettore-llm",ConnettoreLlm.class));
	
	}
	
	

	public org.openspcoop2.core.registry.model.ConnettoreLlmProviderRefModel PROVIDER = null;
	 

	@Override
	public Class<ConnettoreLlm> getModeledClass(){
		return ConnettoreLlm.class;
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