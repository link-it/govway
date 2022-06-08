package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.Date;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;

import com.hazelcast.core.HazelcastInstance;

public class BuilderDatiCollezionatiDistributed {

	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati,DatiCollezionati dati, HazelcastInstance hazelcast, String uniqueMapId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case DISTRIBUTED:
			ret = new DatiCollezionatiDistributed(dati, hazelcast, uniqueMapId);
			break;
		case DISTRIBUTED_ASYNC:
			ret = new DatiCollezionatiDistributedAsync(dati, hazelcast, uniqueMapId);
			break;
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(dati, hazelcast, uniqueMapId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
	
	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati, Date updatePolicyDate, HazelcastInstance hazelcast, String uniqueMapId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case DISTRIBUTED:
			ret = new DatiCollezionatiDistributed(updatePolicyDate, hazelcast, uniqueMapId);
			break;
		case DISTRIBUTED_ASYNC:
			ret = new DatiCollezionatiDistributedAsync(updatePolicyDate, hazelcast, uniqueMapId);
			break;
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(updatePolicyDate, hazelcast, uniqueMapId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
}
