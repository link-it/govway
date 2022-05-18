package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.util.Map.Entry;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;

import com.hazelcast.core.Offloadable;
import com.hazelcast.map.EntryProcessor;

public class ResetCountersProcessor implements EntryProcessor<IDUnivocoGroupByPolicy, DatiCollezionati, Boolean>, Offloadable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String getExecutorName() {
		return "hz:offloadable";
	}

	@Override
	public Boolean process(Entry<IDUnivocoGroupByPolicy, DatiCollezionati> entry) {
		entry.getValue().resetCounters();
		entry.setValue(entry.getValue());
		return true;
	}
	
}

