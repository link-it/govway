package org.openspcoop2.web.monitor.transazioni.mbean;

import java.io.Serializable;

public class DiagnosticiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String idTransazione;
	private String idEgov;
	private String identificativoPorta;
	
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	
	public String getIdentificativoPorta() {
		return this.identificativoPorta;
	}
	
	public void setIdentificativoPorta(String identificativoPorta) {
		this.identificativoPorta = identificativoPorta;
	}
	
	public String getIdEgov() {
		return this.idEgov;
	}
	
	public void setIdEgov(String idEgov) {
		this.idEgov = idEgov;
	}
	
	public String submit(){
		return "success";
	}
	
}
