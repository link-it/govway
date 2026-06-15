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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ConnettoreLlmProviderRef;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConnettoreLlmProviderRef 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreLlmProviderRefModel extends AbstractModel<ConnettoreLlmProviderRef> {

	public ConnettoreLlmProviderRefModel(){
	
		super();
	
		this.PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new Field("property",org.openspcoop2.core.config.Property.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class));
		this.BINDING = new org.openspcoop2.core.config.model.ConnettoreLlmBindingModel(new Field("binding",org.openspcoop2.core.config.ConnettoreLlmBinding.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class));
		this.TIPO = new Field("tipo",java.lang.String.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class);
		this.NOME = new Field("nome",java.lang.String.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class);
	
	}
	
	public ConnettoreLlmProviderRefModel(IField father){
	
		super(father);
	
		this.PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new ComplexField(father,"property",org.openspcoop2.core.config.Property.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class));
		this.BINDING = new org.openspcoop2.core.config.model.ConnettoreLlmBindingModel(new ComplexField(father,"binding",org.openspcoop2.core.config.ConnettoreLlmBinding.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class));
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"connettore-llm-provider-ref",ConnettoreLlmProviderRef.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PropertyModel PROPERTY = null;
	 
	public org.openspcoop2.core.config.model.ConnettoreLlmBindingModel BINDING = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<ConnettoreLlmProviderRef> getModeledClass(){
		return ConnettoreLlmProviderRef.class;
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