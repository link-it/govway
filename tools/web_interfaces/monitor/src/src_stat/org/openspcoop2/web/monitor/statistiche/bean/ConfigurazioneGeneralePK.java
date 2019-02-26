/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.core.transazioni.constants.PddRuolo;

/**
 * ConfigurazioneGeneralePK
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
