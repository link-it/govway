package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.core.transazioni.constants.PddRuolo;


public class ConfigurazioneGeneralePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private Long id = null;
	private PddRuolo ruolo = null;
	
	public ConfigurazioneGeneralePK(){
		this.id = -1L;
		this.ruolo = PddRuolo.DELEGATA;
	}
	
	public ConfigurazioneGeneralePK(Long id,PddRuolo ruolo){
		this.id = id;
		this.ruolo = ruolo;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PddRuolo getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(PddRuolo ruolo) {
		this.ruolo = ruolo;
	}
	
	public ConfigurazioneGeneralePK(String identificatore){
		String[] split = identificatore.split("-");
		this.setId(Long.parseLong(split[0]));
		this.setRuolo(PddRuolo.toEnumConstant(split[1]));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getId());
		sb.append("-");
		sb.append(this.getRuolo().getValue());
		
		return sb.toString();
	}
	
}
