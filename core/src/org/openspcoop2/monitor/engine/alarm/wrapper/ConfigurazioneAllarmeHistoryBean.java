/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.monitor.engine.alarm.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.monitor.engine.alarm.utils.AllarmiUtils;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.beans.BlackListElement;

/**
 * ConfigurazioneAllarmeHistoryBean 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneAllarmeHistoryBean extends AllarmeHistory{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ConfigurazioneAllarmeHistoryBean() {
		super();
	}
	
	public ConfigurazioneAllarmeHistoryBean(AllarmeHistory allarmeHistory){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
//		metodiEsclusi.add(new BlackListElement("setLatenzaTotale",
//				Long.class));
		
		BeanUtils.copy(this, allarmeHistory, metodiEsclusi);
	}
	
	private static final int LIMIT = 100;
	
	public String getDettaglioStatoAbbr(){
	String tmp = this.getDettaglioStato();
		
		if(tmp != null){
			if(tmp.length() >= LIMIT)
				return tmp.substring(0, (LIMIT-3))+"...";
		}
		
		return tmp;
	}
	
	public String getDettaglioStatoHtmlEscaped(){
		String tmp = this.getDettaglioStato();
		
		if(tmp != null){
			while(tmp.contains("\n")) {
				tmp = tmp.replace("\n", "<BR/>");
			}
		}
		
		return tmp;
	}
	
	public String getLabelStato(){
		StatoAllarme stato = AllarmiConverterUtils.toStatoAllarme(this.getStato());
		return AllarmiUtils.getLabelStato(stato);
	}

}
