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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.numero_richieste;

import static org.junit.Assert.assertEquals;

import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.PolicyFields;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;

/**
* Commons
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Commons {

	public static void checkPostConditionsRichiesteSimultanee(String idPolicy) {
		checkPostConditionsRichiesteSimultanee(idPolicy, 
				null);	//PolicyGroupByActiveThreadsType.LOCAL); FIX: Devo controllarlo solamente se non e' null. Quando si fa il reset dell'erogazione/fruizione e non passa ancora una richiesta, rimane il motore precedente
	}
	public static void checkPostConditionsRichiesteSimultanee(String idPolicy, PolicyGroupByActiveThreadsType policyType) {
		
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));
		
		while(true) {
			try {
				String jmxPolicyInfo = Utils.getPolicy(idPolicy);
				
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}
				
				Utils.logRateLimiting.info(jmxPolicyInfo);
				RichiesteSimultaneePolicyInfo polInfo = new RichiesteSimultaneePolicyInfo(jmxPolicyInfo);
				assertEquals(Integer.valueOf(0), polInfo.richiesteAttive);
				
				if(policyType!=null) {
					// Devo controllarlo somanete se non e' null. Quando si fa il reset dell'erogazione/fruizione e non passa ancora una richiesta, rimane il motore precedente
					assertEquals(policyType.toLabel(), polInfo.sincronizzazione);
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

	public static void checkPreConditionsRichiesteSimultanee(String idPolicy) {
		checkPreConditionsRichiesteSimultanee(idPolicy, PolicyGroupByActiveThreadsType.LOCAL);
	}
	public static void checkPreConditionsRichiesteSimultanee(String idPolicy, PolicyGroupByActiveThreadsType policyType) {
		
		int remainingChecks = Integer.valueOf(System.getProperty("rl_check_policy_conditions_retry"));
		int delay = Integer.valueOf(System.getProperty("rl_check_policy_conditions_delay"));
	
		while(true) {
			try {
				String jmxPolicyInfo = Utils.getPolicy(idPolicy);
				if (jmxPolicyInfo.equals(PolicyFields.NOPOLICYINFO)) {
					break;
				}
				
				Utils.logRateLimiting.info(jmxPolicyInfo);
				RichiesteSimultaneePolicyInfo polInfo = new RichiesteSimultaneePolicyInfo(jmxPolicyInfo);
				assertEquals(Integer.valueOf(0), polInfo.richiesteAttive);
				
				if(policyType!=null) {
					// Devo controllarlo somanete se non e' null. Quando si fa il reset dell'erogazione/fruizione e non passa ancora una richiesta, rimane il motore precedente
					assertEquals(policyType.toLabel(), polInfo.sincronizzazione);
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
