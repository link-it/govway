/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.eventi.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;

/**
 * EventoBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
	
	public String getConfigurazioneHTML(){
		String tmp = this.getConfigurazione();
		if(tmp!=null){
			tmp = tmp.trim();
			if(tmp.contains("\n")){
				return formatHTML(tmp);
			}
			else{
				return tmp;
			}
		}
		else{
			return null;
		}
	}
	private String formatHTML(String tmp) {
		String [] split = tmp.split("\n");
		if(split!=null && split.length>0){
			StringBuilder bf = new StringBuilder();
			for (int i = 0; i < split.length; i++) {
				if(bf.length()>0){
					bf.append("<BR/>");
				}
				bf.append(split[i].trim());
			}
			return bf.toString();
		}
		else{
			return tmp;
		}
	}
	
	public boolean isEventoTimeout() {
		if(this.getTipo()!=null &&
				(
					TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT.equals(this.getTipo())
					||
					TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT.equals(this.getTipo())
					||
					TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT.equals(this.getTipo())
				)
			) {
			return true;
		}
		return false;
	}
}
