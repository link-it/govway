/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.Map.Entry;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;

import com.hazelcast.core.Offloadable;
import com.hazelcast.map.EntryProcessor;

/**
 * 

You can use the following system properties to configure the Near Cache invalidation:

hazelcast.map.invalidation.batch.enabled: Enable or disable batching. Its default value is true. When it is set to false, all invalidations are sent immediately.

hazelcast.map.invalidation.batch.size: Maximum number of invalidations in a batch. Its default value is 100.

hazelcast.map.invalidation.batchfrequency.seconds: If the collected invalidations do not reach the configured batch size, a background process sends them periodically. Its default value is 10 seconds.

If there are a lot of clients or many mutating operations, batching should remain enabled and the batch size should be configured with the hazelcast.map.invalidation.batch.size system property to a suitable value.

 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InitProcessor implements EntryProcessor<IDUnivocoGroupByPolicy, DatiCollezionati, DatiCollezionati>, Offloadable {
	
	private static final long serialVersionUID = 1L;
	
	private DatiCollezionati dati = null;
	
	public InitProcessor(DatiCollezionati dati) {
		this.dati = dati;
	}
	
	@Override
	public DatiCollezionati process(Entry<IDUnivocoGroupByPolicy, DatiCollezionati> entry) {
		if(entry.getValue()==null) {
			// devo inizializarla solo allo startup del primo nodo. Gli altri troveranno il valore gia' presente
			entry.setValue(this.dati);
		}
		return entry.getValue();
	}

	@Override
	public String getExecutorName() {
		return "hz:offloadable";
	}
}