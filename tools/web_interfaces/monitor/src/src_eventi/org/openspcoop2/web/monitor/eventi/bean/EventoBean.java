package org.openspcoop2.web.monitor.eventi.bean;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.List;

public class EventoBean extends Evento {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EventoBean() {
		super();
	}
	
	public EventoBean(Evento evento){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
//		metodiEsclusi.add(new BlackListElement("setLatenzaTotale",
//				Long.class));
		
		BeanUtils.copy(this, evento, metodiEsclusi);
	}
	
	public String getSeveritaReadable(){
		try{
			TipoSeverita tipo = SeveritaConverter.toSeverita(this.getSeverita());
			return tipo.name();
		}catch(Exception e){
			return this.getSeverita()+"";
		}
	}
	
	public String getIdConfigurazioneCompact(){
		String idconfigurazione = this.getIdConfigurazione();
		if(idconfigurazione!=null && idconfigurazione.length()>100){
			return idconfigurazione.substring(0,97)+"...";
		}
		else{
			return idconfigurazione;
		}
	}
}
