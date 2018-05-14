package org.openspcoop2.web.monitor.core.status;

public class PddStatus extends BaseStatus{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String url;
	
	public PddStatus(){
		super();
	}
	
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
