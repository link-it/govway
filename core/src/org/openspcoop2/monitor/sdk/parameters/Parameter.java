/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.parameters;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;

/**
 * Parameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class Parameter<T> {

	private String id;
	private List<String> refreshParamIds;
	private ParameterRendering<T> rendering;
	private ParameterType type;
	private T value;
	
	protected Parameter(String id, ParameterType type){
		this.id = id;
		this.type = type;
		this.refreshParamIds = new ArrayList<String>();
		this.rendering = new ParameterRendering<T>();
	}
	protected Parameter(Parameter<T> p){
		this.id = p.id;
		this.type = p.type;
		this.refreshParamIds = p.refreshParamIds;
		this.rendering = p.rendering;
	}
	
	public ParameterType getType() {
		return this.type;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getRefreshParamIds() {
		return this.refreshParamIds;
	}
	public void setRefreshParamIds(List<String> refreshParamIds) {
		this.refreshParamIds = refreshParamIds;
	}
	public ParameterRendering<T> getRendering() {
		return this.rendering;
	}
	public void setRendering(ParameterRendering<T> rendering) {
		this.rendering = rendering;
	}
	@SuppressWarnings("unchecked")
	public T getValue() {
		if(this.value!=null){
			// Per le stringhe effettuo il trim
			if(this.value instanceof String){
				String s = (String) this.value;
				return (T) s.trim();
			}
		}
		return this.value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public void resetValue(){
		this.value = null;
	}
	public abstract void setValueAsString(String value) throws ParameterException;
	public abstract String getValueAsString() throws ParameterException;
	
}
