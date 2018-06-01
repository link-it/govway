package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;

/***
 * 
 * 
 * 
 * 
 * @author pintori
 *
 */
public class MenuModalitaItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String value = null;
	private String label = null;
	private String icon = null;
	
	public MenuModalitaItem(String value, String label, String icon) {
		this.setIcon(icon);
		this.setLabel(label);
		this.setValue(value);
	}

	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIcon() {
		return this.icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
}

