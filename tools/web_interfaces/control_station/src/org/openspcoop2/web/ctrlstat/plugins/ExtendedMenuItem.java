package org.openspcoop2.web.ctrlstat.plugins;

public class ExtendedMenuItem {

	public ExtendedMenuItem(String label, String url){
		this.label = label;
		this.url = url;
	}
	
	private String label;
	private String url;
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
