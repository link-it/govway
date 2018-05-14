package org.openspcoop2.web.monitor.core.datamodel;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ResLive extends Res implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ESITO_OK = "esitoOk";
	public static final String ESITO_FAULT = "esitoFault";
	public static final String ESITO_KO = "esitoKo";
	
	
//	private Long esitoKo;
//	private Long esitoOk;
//	private Date risultato;
	
	public ResLive() {
		super();
	}
	
	public ResLive(Long esitoOk, Long esitoFault, Long esitoKo){
		super();
		this.getObjectsMap().put(ESITO_OK, esitoOk);
		this.getObjectsMap().put(ESITO_FAULT, esitoFault);
		this.getObjectsMap().put(ESITO_KO, esitoKo);
		this.setRisultato(new Date(System.currentTimeMillis()));
		
//		this.esitoOk = esitoOk;
//		this.esitoKo = esitoKo;
//		this.risultato = new Date(System.currentTimeMillis());
	}

	public ResLive(Long esitoOk, Long esitoFault, Long esitoKo, Date ts){
		super();
		this.getObjectsMap().put(ESITO_OK, esitoOk);
		this.getObjectsMap().put(ESITO_FAULT, esitoFault);
		this.getObjectsMap().put(ESITO_KO, esitoKo);
		this.setRisultato(ts);
		
//		this.esitoOk = esitoOk;
//		this.esitoKo = esitoKo;
//		this.risultato = ts;
	}
	
	public ResLive(Map<String, Object> map) { 
		super();
		this.getObjectsMap().put(ESITO_OK, (Number)map.get(ESITO_OK));
		this.getObjectsMap().put(ESITO_FAULT, (Number)map.get(ESITO_FAULT));
		this.getObjectsMap().put(ESITO_KO, (Number)map.get(ESITO_KO));
		this.setRisultato((Date)map.get("risultato"));
	}
	
	public Long getEsitoOk() {
		return (Long) this.getObjectsMap().get(ESITO_OK); //this.esitoOk;
	}

	public void setEsitoOk(Long esitoOk) {
		
		this.getObjectsMap().put(ESITO_OK, esitoOk);
//		this.esitoOk = esitoOk;
	}

	public Long getEsitoFault() {
		return (Long) this.getObjectsMap().get(ESITO_FAULT);
//		return this.esitoKo;
	}

	public void setEsitoFault(Long esitoFault) {
		this.getObjectsMap().put(ESITO_FAULT, esitoFault);
//		this.esitoKo = esitoKo;
	}
	
	public Long getEsitoKo() {
		return (Long) this.getObjectsMap().get(ESITO_KO);
//		return this.esitoKo;
	}

	public void setEsitoKo(Long esitoKo) {
		this.getObjectsMap().put(ESITO_KO, esitoKo);
//		this.esitoKo = esitoKo;
	}

//	public Date getRisultato() {
//		return this.risultato;
//	}
//
//	public void setRisultato(Date timePick) {
//		this.risultato = timePick;
//	}
	
	
}
