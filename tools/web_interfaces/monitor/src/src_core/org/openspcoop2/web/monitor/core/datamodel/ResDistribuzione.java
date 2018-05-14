package org.openspcoop2.web.monitor.core.datamodel;

import java.io.Serializable;
import java.util.Map;

public class ResDistribuzione extends ResBase implements Serializable{
	
	private static final long serialVersionUID = 4601647394984864541L;
	private String risultato;
	
		
	public ResDistribuzione() {
		super();
	}
	
	public ResDistribuzione(String risultato, Number somma) {
		this.risultato=risultato;
		this.setSomma(somma);
	}
	
	public ResDistribuzione(Map<String, Object> map) {
		this.risultato = (String)map.get("risultato");
		this.setSomma((Number)map.get("somma"));
	}

	public String getRisultato() {
		return this.risultato;
	}

	public void setRisultato(String risultato) {
		this.risultato = risultato;
	}	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer(super.toString());
		bf.append("\n");
		if(this.risultato!=null){
			bf.append("\tRisultato: ["+this.risultato+"]");
		}
		else{
			bf.append("\tRisultato: [undefined]");
		}
		return bf.toString();
	}
}