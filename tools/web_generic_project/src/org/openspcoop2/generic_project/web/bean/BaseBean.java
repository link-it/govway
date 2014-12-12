/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved.
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
package org.openspcoop2.generic_project.web.bean;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.generic_project.web.presentation.field.OutputField;

/**
 * BaseBean Fornisce il supporto per i dati da visualizzare nella pagina.
 * Incapsula un bean DTO che viene fornito dal livello Service e/o BD.
 * Aggiunge informazioni necessarie alla presentazione nella pagina.
 * 
 * @param <T> Tipo Oggetto
 * @param <K> Tipo Chiave Oggetto
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseBean<T,K> {

	private Map<String, OutputField<?>> fields = null;
	
	protected T dto  ;
	
	public void setDTO(T dto) {
		this.dto = dto;
	}
	
	public BaseBean(){
		this.fields = new HashMap<String, OutputField<?>>();
	}
	
	@SuppressWarnings("unchecked")
	public T getDTO(){
		if(this.dto==null){
			try{
				ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
				this.dto = ((Class<T>)parameterizedType.getActualTypeArguments()[0]).newInstance();
			}catch (Exception e) {
				 
			}
		}
		return this.dto;
	}

	public Map<String, OutputField<?>> getFields() {
		return this.fields;
	}

	public void setFields(Map<String, OutputField<?>> fields) {
		this.fields = fields;
	}
	
	public void setField(String fieldName, OutputField<?> field){
		this.fields.put(fieldName, field);
	}
	
}
