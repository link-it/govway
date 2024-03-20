/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import java.io.Serializable;
import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.BasicDynamicDiscoveryParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;

/**
* TokenDynamicDiscoveryParserCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TokenDynamicDiscoveryParserCustom extends BasicDynamicDiscoveryParser {

	public TokenDynamicDiscoveryParserCustom() {
		super(TipologiaClaims.OIDC_ID_TOKEN);
	}
	
	@Override
	public void init(String raw, Map<String, Serializable> claims) {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "token-dynamic-discovery";
			Serializable v = claims.get("altro");
			if(v instanceof String) {
				if("pa".equals(v)) {
					tipoTest = tipoTest + "-pa";
				}
				else {
					tipoTest = tipoTest + "-pd";
				}
			}
			else {
				throw new Exception("claim 'altro' non trovato");
			}
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		super.init(raw, claims);
	}

}
