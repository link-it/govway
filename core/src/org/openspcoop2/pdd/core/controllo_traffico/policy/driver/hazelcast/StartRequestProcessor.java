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
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Map;
import org.slf4j.Logger;

import com.hazelcast.core.Offloadable;
import com.hazelcast.map.EntryProcessor;

/**
 * 

You can use the following system properties to configure the Near Cache invalidation:

hazelcast.map.invalidation.batch.enabled: Enable or disable batching. Its default value is true. When it is set to false, all invalidations are sent immediately.

hazelcast.map.invalidation.batch.size: Maximum number of invalidations in a batch. Its default value is 100.

hazelcast.map.invalidation.batchfrequency.seconds: If the collected invalidations do not reach the configured batch size, a background process sends them periodically. Its default value is 10 seconds.

If there are a lot of clients or many mutating operations, batching should remain enabled and the batch size should be configured with the hazelcast.map.invalidation.batch.size system property to a suitable value.

 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StartRequestProcessor implements EntryProcessor<IDUnivocoGroupByPolicy, DatiCollezionati, DatiCollezionati>, Offloadable {
	
	private static final long serialVersionUID = 1L;
	private final ActivePolicy activePolicy;
	private final Map<Object> ctx;

	public StartRequestProcessor(ActivePolicy policy, Map<Object> ctx) {
		this.activePolicy = policy;
		this.ctx = ctx;
	}
	
	@Override
	public DatiCollezionati  process(Entry<IDUnivocoGroupByPolicy, DatiCollezionati> entry) {
		//System.out.println("<"+idTransazione+"> registerStartRequest distribuita");
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());

		if (entry.getValue() == null) {
			Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(this.ctx);
			entry.setValue(new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate));
		}
        else {
        	if(entry.getValue().getUpdatePolicyDate()!=null) {
            	if(!entry.getValue().getUpdatePolicyDate().equals(this.activePolicy.getInstanceConfiguration().getUpdateTime())) {
            		// data aggiornata
            		entry.getValue().resetCounters(this.activePolicy.getInstanceConfiguration().getUpdateTime());
            	}
            }
        }

		entry.getValue().registerStartRequest(log, this.activePolicy, this.ctx);
		
		// mi salvo l'attuale stato
		DatiCollezionati datiCollezionatiReaded = (DatiCollezionati) entry.getValue().clone(); 
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		entry.setValue(entry.getValue());
		
		return datiCollezionatiReaded;
	}

	@Override
	public String getExecutorName() {
		return "hz:offloadable";
	}
}