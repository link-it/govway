/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.BlackListElement;

import java.util.ArrayList;
import java.util.List;

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
}
