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

import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.PolicyFields;

public class NumeroRichiestePolicyInfo {

	public Integer richiesteAttive = null;
	public Integer richiesteConteggiate = null;
	public Integer richiesteBloccate = null;
	
	public NumeroRichiestePolicyInfo(String jmxPolicyInfo) {		
		String[] lines = jmxPolicyInfo.split(System.lineSeparator());
		for (String l : lines) {
			l = l.strip();
			String[] keyValue = l.split(":");
			if(keyValue.length == 2) {
				if (keyValue[0].equals(PolicyFields.RichiesteAttive)) {
					this.richiesteAttive = Integer.valueOf(keyValue[1].strip());
				}
				if (keyValue[0].equals(PolicyFields.RichiesteConteggiate)) {
					this.richiesteConteggiate = Integer.valueOf(keyValue[1].strip());
				}
				if (keyValue[0].equals(PolicyFields.RichiesteBloccate)) {
					this.richiesteBloccate = Integer.valueOf(keyValue[1].strip());
				}
			}
		}
		if (this.richiesteAttive == null || this.richiesteConteggiate == null || this.richiesteBloccate == null) {
			throw new RuntimeException("Errore durante la lettura della policy, informazioni mancanti.");
		}		
	}
}
