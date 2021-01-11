/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.PolicyFields;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;

public class Commons {

	public static void checkPostConditionsRichiesteSimultanee(String idPolicy) {
		
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
