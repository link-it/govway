/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourPropertiesUtils;
import org.slf4j.Logger;

/**
 * HealthCheckUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HealthCheckUtils  {
		
	public static HealthCheckConfigurazione read(PortaApplicativa pa, Logger log) throws BehaviourException {
		HealthCheckConfigurazione config = new HealthCheckConfigurazione();
		if(pa.getBehaviour()==null || pa.getBehaviour().sizeProprietaList()<=0) {
			throw new BehaviourException("Configurazione sticky non disponibile");
		}
		
		for (Proprieta p : pa.getBehaviour().getProprietaList()) {
			
			String nome = p.getNome();
			String valore = p.getValore().trim();
			
			try {
				if(HealthCheckCostanti.PASSIVE_HEALTH_CHECK.equals(nome)) {
					config.setPassiveCheckEnabled("true".equals(valore));
				}
				else if(HealthCheckCostanti.PASSIVE_HEALTH_CHECK_SECONDS.equals(nome)) {
					config.setPassiveHealthCheck_excludeForSeconds(Integer.valueOf(valore));
				}
			}catch(Exception e) {
				throw new BehaviourException("Configurazione health check non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
			}
			
		}

		return config;
	}
	

	public static void save(PortaApplicativa pa, HealthCheckConfigurazione configurazione) throws BehaviourException {
		
		if(pa.getBehaviour()==null) {
			throw new BehaviourException("Configurazione behaviour non abilitata");
		}
		if(configurazione==null) {
			throw new BehaviourException("Configurazione health check non fornita");
		}
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),HealthCheckCostanti.PASSIVE_HEALTH_CHECK, configurazione.isPassiveCheckEnabled()+"");
		
		BehaviourPropertiesUtils.removeProprieta(pa.getBehaviour(),HealthCheckCostanti.PASSIVE_HEALTH_CHECK_SECONDS);
		
		if(configurazione.isPassiveCheckEnabled()) {
			if(configurazione.getPassiveHealthCheck_excludeForSeconds()!=null && configurazione.getPassiveHealthCheck_excludeForSeconds().intValue()>0) {
				BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),HealthCheckCostanti.PASSIVE_HEALTH_CHECK_SECONDS, configurazione.getPassiveHealthCheck_excludeForSeconds().intValue()+"");
			}
		}
		
	}
	
}
