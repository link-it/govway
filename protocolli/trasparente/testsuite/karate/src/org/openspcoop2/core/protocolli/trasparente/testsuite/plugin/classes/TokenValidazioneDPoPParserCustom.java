/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import java.util.Map;

import org.openspcoop2.pdd.core.token.parser.BasicDPoPParser;
import org.openspcoop2.pdd.core.token.parser.TipologiaClaimsDPoP;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.JWTParser;

/**
* TokenValidazioneDPoPParserCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TokenValidazioneDPoPParserCustom extends BasicDPoPParser {

	public TokenValidazioneDPoPParserCustom() {
		super(TipologiaClaimsDPoP.RFC9449);
	}

	@Override
	public void init(String raw) throws UtilsException {

		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "token-validazione-dpop";

			// Parse del DPoP token per leggere i claims custom dal payload
			JWTParser jwtParser = new JWTParser(raw);
			Map<String, String> claims = jwtParser.getPayloadClaims();

			String operazione = claims.get("operazione");
			if(operazione != null) {
				tipoTest = tipoTest + "-" + operazione;
			}
			if(claims.containsKey("pa")) {
				tipoTest = tipoTest + "-pa";
			}
			else if(claims.containsKey("pd")) {
				tipoTest = tipoTest + "-pd";
			}

			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}

		super.init(raw);
	}

}
