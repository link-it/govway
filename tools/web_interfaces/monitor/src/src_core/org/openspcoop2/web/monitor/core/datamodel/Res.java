package org.openspcoop2.web.monitor.core.datamodel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Res extends ResBase implements Serializable{
	
	private static final long serialVersionUID = 4601647394984864541L;
	private Date risultato;
	private Long id;
	
	public Res() {
		super();
	}
	
	public Res(Map<String, Object> map) { 
		this.risultato = (Date)map.get("risultato");
		this.setSomma((Long)map.get("somma"));
		this.id = this.risultato.getTime();
	}

	public Date getRisultato() {
		return this.risultato;
	}

	public void setRisultato(Date risultato) {
		this.risultato = risultato;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer(super.toString());
		bf.append("\n");
		if(this.risultato!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			bf.append("\tRisultato-Date: ["+sdf.format(this.risultato)+"]");
		}
		else{
			bf.append("\tRisultatoDate: [undefined]");
		}
		return bf.toString();
	}
	
	
}