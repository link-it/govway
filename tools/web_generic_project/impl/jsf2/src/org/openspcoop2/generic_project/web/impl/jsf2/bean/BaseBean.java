/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf2.bean;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.generic_project.web.bean.IBean;
import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactory;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;
import org.openspcoop2.generic_project.web.impl.jsf2.CostantiJsf2Impl;
import org.openspcoop2.generic_project.web.output.OutputField;

/**
 * Classe Base per i bean dell'interfaccia grafica.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *
 *	@param <DTOType> Tipo di dato da visualizzare.
 *	@param <KeyType> Tipo della chiave del dato da visualizzare.
 */
public abstract class BaseBean<DTOType, KeyType>  implements IBean<DTOType, KeyType> {

	private Map<String, OutputField<?>> fields = null;

	protected DTOType dto  ;

	protected KeyType id ;

	private WebGenericProjectFactory factory = null;

	@Override
	public abstract KeyType getId();

	@Override
	public void setId(KeyType id) {
		this.id = id;
	}

	@Override
	public void setDTO(DTOType dto) {
		this.dto = dto;
	}

	public BaseBean(){
		this.fields = new HashMap<String, OutputField<?>>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DTOType getDTO(){
		if(this.dto==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.dto = ((Class<DTOType>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {

			}
		}
		return this.dto;
	}

	@Override
	public Map<String, OutputField<?>> getFields() {
		return this.fields;
	}

	@Override
	public void setFields(Map<String, OutputField<?>> fields) {
		this.fields = fields;
	}

	@Override
	public void setField(String fieldName, OutputField<?> field){
		this.fields.put(fieldName, field);
	}

	@Override
	public WebGenericProjectFactory getWebGenericProjectFactory()
			throws FactoryException {
		if(this.factory == null)
			this.factory = WebGenericProjectFactoryManager.getInstance().getWebGenericProjectFactoryByName(CostantiJsf2Impl.FACTORY_NAME);

		return this.factory;
	}

	@Override
	public void setWebGenericProjectFactory(WebGenericProjectFactory factory)
			throws FactoryException {
		this.factory  = factory;

	}
}
