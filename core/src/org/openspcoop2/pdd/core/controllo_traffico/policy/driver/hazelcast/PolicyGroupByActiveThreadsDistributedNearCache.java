/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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


package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.Date;
import java.util.Map.Entry;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.utils.Map;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * Gestore contatori policy che lavora sulla copia distribuita ed esegue computazioni locali con quello 
 * che trova nella cache.
 * 
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreadsDistributedNearCache extends AbstractPolicyGroupByActiveThreadsDistributed  {


	public PolicyGroupByActiveThreadsDistributedNearCache(ActivePolicy policy, String uniqueIdMap, HazelcastInstance hazelcast) throws PolicyException {
		super(policy, uniqueIdMap, PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE, hazelcast);
	}

	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx)
			throws PolicyException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);
		
		// Lavoro in asincrono sui contatori della copia remota
		this.distributedMap.submitToKey(datiGroupBy, new StartRequestProcessor(this.activePolicy,ctx));
		
		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);
		boolean newDati = false;
		if (datiCollezionati == null) {
			Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
			datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
			newDati = true;
		}
		else {
			if(datiCollezionati.getUpdatePolicyDate()!=null) {
				if(!datiCollezionati.getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
					// data aggiornata
					datiCollezionati.resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
				}
			}
		}
		DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.clone(); // i valori utilizzati dal policy verifier verranno impostati con il valore impostato nell'operazione chiamata
		if(newDati) {
			datiCollezionatiPerPolicyVerifier.initDatiIniziali(this.activePolicy);
			datiCollezionatiPerPolicyVerifier.checkDate(log, this.activePolicy); // inizializza le date se ci sono
		}
		
		// Lavoro anche sulla copia locale
		datiCollezionati.registerStartRequest(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);
		
		return datiCollezionatiPerPolicyVerifier;
	}
	
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione,
			IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx) throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		// Lavoro sulla copia remota
		this.distributedMap.submitToKey(datiGroupBy, new UpdateDatiRequestProcessor(this.activePolicy, ctx));

		// Opero sulla copia locale
		// per adesso se la policy non è ancora arrivata nella near cache, agisco in locale come se fosse la prima richiesta.
		DatiCollezionati datiCollezionati = this.distributedMap.get(datiGroupBy);			
		if(datiCollezionati == null) {
			log.debug("(idTransazione:"+idTransazione+") Policy non ancora in Near Cache. Conto come fosse la prima richiesta; dati identificativi ["+datiGroupBy.toString()+"]");
			
			Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
			datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
			datiCollezionati.registerStartRequest(log, this.activePolicy, null);
			return datiCollezionati;
		} else {
			DatiCollezionati datiCollezionatiPerPolicyVerifier = (DatiCollezionati) datiCollezionati.clone(); // i valori utilizzati dal policy verifier verranno impostati con il valore impostato nell'operazione chiamata
			
			boolean update = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx, datiCollezionatiPerPolicyVerifier);
			if(update) {
				return datiCollezionatiPerPolicyVerifier;
			}
			
			return null;
		}
	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata)
			throws PolicyException, PolicyNotFoundException {
		
		datiGroupBy = augmentIDUnivoco(datiGroupBy);

		// Lavoro sulla copia remota
		this.distributedMap.submitToKey(datiGroupBy, new EndRequestProcessor(this.activePolicy, ctx, dati, isApplicabile, isViolata));			
		
		// Non ho nulla da restituire e non gestisco direttamente la near cache quindi l'ultima richiesta la faccio solo in remoto
	}
	
	@Override
	public void resetCounters() {
		
		this.distributedMap.executeOnEntries( (Entry<IDUnivocoGroupByPolicy, DatiCollezionati> entry) -> {
			entry.getValue().resetCounters();
			entry.setValue(entry.getValue());
			return true;
		});			
	}
	
}
