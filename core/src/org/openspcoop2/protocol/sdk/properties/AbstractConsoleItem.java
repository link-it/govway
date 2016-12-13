package org.openspcoop2.protocol.sdk.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;

public abstract class AbstractConsoleItem<T> extends BaseConsoleItem {

	private T defaultValue;
	private boolean reloadOnChange;
	private boolean required;
	private String regexpr;
	private TreeMap<String,T> mapLabelValues;

	protected AbstractConsoleItem(String id, String label, ConsoleItemType type) throws ProtocolException{
		super(id, label, type);
		this.mapLabelValues = new TreeMap<String,T>();
	}

	public T getDefaultValue() {
		return this.defaultValue;
	}
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isReloadOnChange() {
		return this.reloadOnChange;
	}
	public void setReloadOnChange(boolean reloadOnChange) {
		this.reloadOnChange = reloadOnChange;
	}

	public boolean isRequired() {
		return this.required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getRegexpr() {
		return this.regexpr;
	}
	public void setRegexpr(String regexpr) {
		this.regexpr = regexpr;
	}
	
	public TreeMap<String, T> getMapLabelValues() {
		return this.mapLabelValues;
	}
	public List<String> getLabels(){
		if(this.mapLabelValues!=null && this.mapLabelValues.size()>0){	
			List<String> labeles = new ArrayList<String>();
			labeles.addAll(this.mapLabelValues.keySet());
			return labeles;
		}
		return null;
	}
	public List<T> getValues(){
		if(this.mapLabelValues!=null && this.mapLabelValues.size()>0){	
			List<T> values = new ArrayList<T>();
			values.addAll(this.mapLabelValues.values());
			return values;
		}
		return null;
	}
	public void clearMapLabelValues(){
		this.mapLabelValues.clear();
	}
	public void addLabelValue(String key, T value){
		this.mapLabelValues.put(key, value);
	}
	
}
