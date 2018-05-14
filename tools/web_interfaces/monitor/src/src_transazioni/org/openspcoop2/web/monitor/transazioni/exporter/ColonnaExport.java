package org.openspcoop2.web.monitor.transazioni.exporter;

import java.io.Serializable;

import org.openspcoop2.web.monitor.core.bean.SelectItem;

public class ColonnaExport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean visibile = false;
	private String label = null;
	private String key= null;
	
	public ColonnaExport(){}
	
	public ColonnaExport(String key, String label, boolean visibile){
		this.key = key;
		this.label = label;
		this.visibile = visibile;
	}
	
	public boolean isVisibile() {
		return this.visibile;
	}
	public void setVisibile(boolean visibile) {
		this.visibile = visibile;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public SelectItem getSelectItem(){
		return new SelectItem(this.getKey(), this.getLabel());
	}
	
}
