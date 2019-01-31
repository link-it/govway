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

package org.openspcoop2.web.ctrlstat.servlet.config;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneUtilities {

	public static boolean alreadyExists(TipoOperazione tipoOperazione, ConfigurazioneCore confCore, ConfigurazioneHelper confHelper, 
			AttivazionePolicy policy, InfoPolicy infoPolicy, RuoloPolicy ruoloPorta, String nomePorta,
			StringBuffer existsMessage, String newLine) throws DriverControlStationException, NotFoundException {
		if(infoPolicy!=null){
			AttivazionePolicy p = null;
			try {
				p = confCore.getGlobalPolicy(policy.getIdPolicy(),policy.getFiltro(), policy.getGroupBy());
			}catch(DriverControlStationNotFound e) {
				//ignore
			}
			if(p!=null){
				if(TipoOperazione.ADD.equals(tipoOperazione) ||	(p.getId()!=null &&	policy.getId()!=null &&	p.getId().longValue()!=policy.getId().longValue())){
					String messaggio = "Esiste già una attivazione per la policy con nome '"+
							policy.getIdPolicy()+"' "+newLine+"e"+newLine+"Collezionamento dei Dati: "+ 
							confHelper.toStringCompactGroupBy(policy.getGroupBy(),ruoloPorta,nomePorta)+newLine+"e"+newLine+	
							confHelper.toStringFilter(policy.getFiltro(),ruoloPorta,nomePorta);
					existsMessage.append(messaggio);
					return true; 
				}
			}
			
			AttivazionePolicy pAlias = null;
			if(policy.getAlias()!=null && !"".equals(policy.getAlias())) {
				try {
					pAlias = confCore.getGlobalPolicyByAlias(policy.getAlias());
				}catch(DriverControlStationNotFound e) {
					//ignore
				}
				if(pAlias!=null){
					if(TipoOperazione.ADD.equals(tipoOperazione) || (pAlias.getId()!=null && policy.getId()!=null && pAlias.getId().longValue()!=policy.getId().longValue())){
						String messaggio = "Esiste già una attivazione per la policy con "+ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS+" '"+policy.getAlias()+"'";
						existsMessage.append(messaggio);
						return true; 
					}
				}
			}
		}
		
		return false;
	}
	
}
