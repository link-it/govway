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


package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.occupazione_banda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.PolicyFields;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
* Commons
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Commons {

	public static void checkPreConditionsOccupazioneBanda(String idPolicy) {
		checkPreConditionsOccupazioneBanda(idPolicy, PolicyGroupByActiveThreadsType.LOCAL);
	}
	public static void checkPreConditionsOccupazioneBanda(String idPolicy, PolicyGroupByActiveThreadsType policyType) {
		Logger logRateLimiting = LoggerWrapperFactory.getLogger("testsuite.rate_limiting");
	
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));

		while (true) {
			try {
				String jmxPolicyInfo = Utils.getPolicy(idPolicy);
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}
				logRateLimiting.info(jmxPolicyInfo);
				Map<String, String> policyValues = Utils.parsePolicy(jmxPolicyInfo);
	
				if(!PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(policyType) && 
						!PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(policyType)) {
					assertEquals("0", policyValues.get(PolicyFields.RichiesteAttive));
				}
				
				assertEquals("0", policyValues.get(PolicyFields.RichiesteConteggiate));
				assertEquals("0 kb (0 bytes)", policyValues.get(PolicyFields.Contatore));
				assertEquals("0 kb (0 bytes)", policyValues.get(PolicyFields.ValoreMedio));
				assertEquals("0", policyValues.get(PolicyFields.RichiesteBloccate));
				
				if(policyType!=null) {
					// Devo controllarlo somanete se non e' null. Quando si fa il reset dell'erogazione/fruizione e non passa ancora una richiesta, rimane il motore precedente
					assertEquals(policyType.toLabel(), policyValues.get(PolicyFields.Sincronizzazione));
				}
				
				break;
			} catch (AssertionError e) {
				if (remainingChecks == 0) {
					throw e;
				}
				remainingChecks--;
				org.openspcoop2.utils.Utilities.sleep(delay);
			}
		}
	
	}

	public static void checkPostConditionsOccupazioneBanda(String idPolicy) {
		checkPostConditionsOccupazioneBanda(idPolicy, PolicyGroupByActiveThreadsType.LOCAL);
	}
	public static void checkPostConditionsOccupazioneBanda(String idPolicy, PolicyGroupByActiveThreadsType policyType) {
		Logger logRateLimiting = LoggerWrapperFactory.getLogger("testsuite.rate_limiting");
		
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));

		while(true) {
			try {
				String jmxPolicyInfo = Utils.getPolicy(idPolicy);
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}	
				logRateLimiting.info(jmxPolicyInfo);
				Map<String, String> policyValues = Utils.parsePolicy(jmxPolicyInfo);
				
				if(!PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(policyType) && 
						!PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(policyType)) {
					assertEquals("0", policyValues.get(PolicyFields.RichiesteAttive));
				}
				
				if(PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP.equals(policyType) ||
						PolicyGroupByActiveThreadsType.HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP.equals(policyType)) {
					int conteggiate = Integer.valueOf(policyValues.get(PolicyFields.RichiesteConteggiate));
					assertTrue(conteggiate<=3);
					
					int bloccate = Integer.valueOf(policyValues.get(PolicyFields.RichiesteBloccate));
					assertTrue(bloccate<=1);
				}
				else {
					assertEquals("3", policyValues.get(PolicyFields.RichiesteConteggiate));
					assertEquals("1", policyValues.get(PolicyFields.RichiesteBloccate));
				}
				
				String kbContatore = policyValues.get("Contatore").split(" ")[0];
				assertTrue(Double.valueOf(kbContatore) > 5);				
				
				if(policyType!=null) {
					// Devo controllarlo somanete se non e' null. Quando si fa il reset dell'erogazione/fruizione e non passa ancora una richiesta, rimane il motore precedente
					assertEquals(policyType.toLabel(), policyValues.get(PolicyFields.Sincronizzazione));
				}
				
				break;
			} catch (AssertionError e) {
				if(remainingChecks == 0) {
					throw e;
				}
				remainingChecks--;
				org.openspcoop2.utils.Utilities.sleep(delay);
			}
		} 
	}

}
