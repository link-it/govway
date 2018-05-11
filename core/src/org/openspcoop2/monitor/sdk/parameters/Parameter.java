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
