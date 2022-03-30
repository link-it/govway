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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.tempo_complessivo_risposta;

import static org.junit.Assert.assertEquals;

import java.util.Map;

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

	static void checkPreConditionsTempoComplessivoRisposta(String idPolicy)  {
		
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
				
				assertEquals("0", policyValues.get(PolicyFields.RichiesteAttive));
				assertEquals("0", policyValues.get(PolicyFields.RichiesteConteggiate));
				assertEquals("0 secondi (0 ms)", policyValues.get(PolicyFields.Contatore));
				assertEquals("0 secondi (0 ms)", policyValues.get(PolicyFields.ValoreMedio));
				assertEquals("0", policyValues.get(PolicyFields.RichiesteBloccate));
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

	static void checkPostConditionsTempoComplessivoRisposta(String idPolicy)  {
		
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
				
				assertEquals("0", policyValues.get(PolicyFields.RichiesteAttive));
				assertEquals("1", policyValues.get(PolicyFields.RichiesteConteggiate));
				assertEquals("1", policyValues.get(PolicyFields.RichiesteBloccate));
				
				String secondiContatore = policyValues.get("Contatore").split(" ")[0];
				assertEquals("2", secondiContatore);
				
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
