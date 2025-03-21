/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.openspcoop2.pdd.core.token.parser.BasicTokenParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaims;

/**
* TokenValidazioneParserCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TokenValidazioneParserCustom extends BasicTokenParser {

	public TokenValidazioneParserCustom() {
		super(TipologiaClaims.JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068);
	}
	
	@Override
	public void init(String raw, Map<String, Serializable> claims) {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "token-validazione";
			String operazione = (String) claims.get("operazione");
			tipoTest = tipoTest + "-"+operazione;
			if(claims.containsKey("pa")) {
				tipoTest = tipoTest + "-pa";
			}
			else {
				tipoTest = tipoTest + "-pd";
			}
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		super.init(raw, claims);
	}

}
