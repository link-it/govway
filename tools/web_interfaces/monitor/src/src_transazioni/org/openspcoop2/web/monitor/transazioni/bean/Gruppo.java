package org.openspcoop2.web.monitor.transazioni.bean;

import java.io.Serializable;

public class Gruppo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private String label = null;
	private String value = null;
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}