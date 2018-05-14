package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;

public class SelectItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String value;
	protected String label;

	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SelectItem(String value, String label){
		this.setLabel(label);
		this.setValue(value);
	}

	public SelectItem(){
		this.value = null;
		this.label = null;
	}

	@Override
	public String toString() {
		return this.label != null ? this.label.toString() :  "" ;
	}
	
	public String getLabel() {
		return this.label;
	}
}
