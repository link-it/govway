/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.generic_project.exception.NotFoundException;

/**
 * DettaglioRateLimiting
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DettaglioRateLimiting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<String> policyName = new ArrayList<>();
	private Map<String, AttivazionePolicy> policyMap = new HashMap<String, AttivazionePolicy>();
	private Map<String, ConfigurazionePolicy> configPolicyMap = new HashMap<String, ConfigurazionePolicy>();
	
	public void addDetail(AttivazionePolicy ap, ConfigurazionePolicy cp) {
		this.policyName.add(ap.getIdActivePolicy());
		this.policyMap.put(ap.getIdActivePolicy(), ap);
		this.configPolicyMap.put(ap.getIdActivePolicy(), cp);
	}
	
	public String getAsCSVRecord() throws NotFoundException {
		StringBuilder sb = new StringBuilder("");
		if(this.policyName!=null && !this.policyName.isEmpty()) {
			for (String name : this.policyName) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(this.toString(name));
			}
		}
		return sb.toString();
	}
	
	public String toString(String policyName) throws NotFoundException {
		StringBuilder sb = new StringBuilder();
		AttivazionePolicy ap = this.policyMap.get(policyName);
		ConfigurazionePolicy cp = this.configPolicyMap.get(policyName);
		if(StringUtils.isNotEmpty(ap.getAlias())) {
			sb.append(ap.getAlias());
		}
		else {
			sb.append(policyName);
		}
		sb.append(" ");
		if(ap.getEnabled()) {
			if(ap.isWarningOnly()) {
				sb.append("warningOnly");
			}
			else {
				sb.append("abilitato");	
			}
		}
		else {
			sb.append("disabilitato");
		}
		sb.append(" ");
				
		TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.getTipo(cp.getRisorsa(), cp.isSimultanee());
		sb.append(tipoRisorsaPolicyAttiva.name());
		sb.append(" ");
		
		if(TipoRisorsaPolicyAttiva.DIMENSIONE_MASSIMA_MESSAGGIO.equals(tipoRisorsaPolicyAttiva)) {
			// Dimensione Richiesta Richiesta 
			if(ap.isRidefinisci()) {
				sb.append(ap.getValore2());
			}
			else {
				sb.append(cp.getValore2());
			}
			sb.append(" ");
		}
		
		if(ap.isRidefinisci()) {
			sb.append(ap.getValore());
		}
		else {
			sb.append(cp.getValore());
		}
		
		return sb.toString();
	}
}
