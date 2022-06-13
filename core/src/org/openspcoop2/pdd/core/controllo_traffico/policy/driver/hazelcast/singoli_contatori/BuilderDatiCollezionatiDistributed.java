package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori;

import java.util.Date;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.TipoDatiCollezionati;

import com.hazelcast.core.HazelcastInstance;

public class BuilderDatiCollezionatiDistributed {

	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati,DatiCollezionati dati, HazelcastInstance hazelcast, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case DISTRIBUTED:
			ret = new DatiCollezionatiDistributed(dati, hazelcast, uniquePrefixId);
			break;
		case DISTRIBUTED_ASYNC:
			throw new RuntimeException("Policy Ancora da implementare, forse non serve, sarebbe inconsistente come quella per i PNCounter ma andrebbe più lenta");
			//ret = new DatiCollezionatiDistributedAsync(dati, hazelcast, uniquePrefixId);
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(dati, hazelcast, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
	
	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati, Date updatePolicyDate, HazelcastInstance hazelcast, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case DISTRIBUTED:
			ret = new DatiCollezionatiDistributed(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		case DISTRIBUTED_ASYNC:
			throw new RuntimeException("Policy Ancora da implementare, forse non serve, sarebbe inconsistente come quella per i PNCounter ma andrebbe più lenta");
			//ret = new DatiCollezionatiDistributedAsync(updatePolicyDate, hazelcast, uniquePrefixId);
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
}
