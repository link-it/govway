package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.Properties;

public class TokenForward implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Properties trasporto = new Properties();
	private Properties url = new Properties();
	
	public Properties getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Properties trasporto) {
		this.trasporto = trasporto;
	}
	public Properties getUrl() {
		return this.url;
	}
	public void setUrl(Properties url) {
		this.url = url;
	}
}
