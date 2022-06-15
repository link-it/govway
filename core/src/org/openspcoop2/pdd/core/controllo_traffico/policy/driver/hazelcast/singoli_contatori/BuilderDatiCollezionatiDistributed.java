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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori;

import java.util.Date;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.TipoDatiCollezionati;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @author Francesco Scarlato
 *
 */
public class BuilderDatiCollezionatiDistributed {

	public static DatiCollezionati build(TipoDatiCollezionati tipoDatiCollezionati,DatiCollezionati dati, HazelcastInstance hazelcast, String uniquePrefixId) {
		DatiCollezionati ret;
		
		switch (tipoDatiCollezionati) {
		case ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(dati, hazelcast, uniquePrefixId);
			break;
		case ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(dati, hazelcast, uniquePrefixId);
			break;
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
		case ATOMIC_LONG:
			ret = new DatiCollezionatiDistributedAtomicLong(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		case ATOMIC_LONG_ASYNC:
			ret = new DatiCollezionatiDistributedAtomicLongAsync(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		case PNCOUNTER:
			ret = new DatiCollezionatiDistributedPNCounter(updatePolicyDate, hazelcast, uniquePrefixId);
			break;
		default:
			throw new RuntimeException("TipoDatiCollezionati sconosciuto: " + tipoDatiCollezionati);
		}
		
		return ret;
	}
}
