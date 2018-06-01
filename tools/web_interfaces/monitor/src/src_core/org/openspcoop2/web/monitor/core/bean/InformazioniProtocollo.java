package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;

/***
 * 
 * 
 * 
 * @author pintori
 *
 */
public class InformazioniProtocollo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String descrizioneProtocollo = null;
	private String webSiteProtocollo = null;
	private String labelProtocollo = null;
	
	public String getDescrizioneProtocollo() {
		return this.descrizioneProtocollo;
	}
	public void setDescrizioneProtocollo(String descrizioneProtocollo) {
		this.descrizioneProtocollo = descrizioneProtocollo;
	}
	public String getWebSiteProtocollo() {
		return this.webSiteProtocollo;
	}
	public void setWebSiteProtocollo(String webSiteProtocollo) {
		this.webSiteProtocollo = webSiteProtocollo;
	}
	public String getLabelProtocollo() {
		return this.labelProtocollo;
	}
	public void setLabelProtocollo(String labelProtocollo) {
		this.labelProtocollo = labelProtocollo;
	} 

}
