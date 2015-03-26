package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.lib.mvc.Parameter;

public class UrlParameters {

	private String url;
	private List<Parameter> parameter = new ArrayList<Parameter>();
	
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int sizeParameter() {
		return this.parameter.size();
	}
	public boolean addParameter(Parameter e) {
		return this.parameter.add(e);
	}
	public Parameter getParameter(int index) {
		return this.parameter.get(index);
	}
	public Parameter removeParameter(int index) {
		return this.parameter.remove(index);
	}
	public List<Parameter> getParameter() {
		return this.parameter;
	}
	
}
