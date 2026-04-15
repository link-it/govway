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

package org.openspcoop2.protocol.modipa.controllo_traffico;

import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.PluginsException;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.slf4j.Logger;

/**
 * RateLimitingAuthorizationClientId - Estrae il claim 'client_id' dal payload del token ModI authorization
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RateLimitingAuthorizationClientId extends AbstractModIRateLimiting {

	private static final String CLAIM_CLIENT_ID = Claims.JSON_WEB_TOKEN_FOR_OAUTH2_ACCESS_TOKENS_RFC_9068_CLIENT_ID;

	@Override
	protected String estraiValore(Logger log, SecurityToken securityToken, Dati datiRichiesta) throws PluginsException {
		try {
			RestMessageSecurityToken authorization = getAuthorizationToken(securityToken);
			String clientId = authorization.getPayloadClaim(CLAIM_CLIENT_ID);
			if(clientId == null) {
				throw new PluginsException("Claim '" + CLAIM_CLIENT_ID + "' non presente nel token authorization ModI");
			}
			return clientId;
		} catch(PluginsException e) {
			throw e;
		} catch(Exception e) {
			throw new PluginsException("Errore durante l'estrazione del claim '" + CLAIM_CLIENT_ID + "': " + e.getMessage(), e);
		}
	}

}
