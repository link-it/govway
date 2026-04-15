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

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.PluginsException;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.slf4j.Logger;

/**
 * AbstractModIRateLimiting
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractModIRateLimiting implements IRateLimiting {

	protected abstract String estraiValore(Logger log, SecurityToken securityToken, Dati datiRichiesta) throws PluginsException;

	@Override
	public String estraiValoreFiltro(Logger log, Dati datiRichiesta) throws PluginsException {
		return estraiValore(log, datiRichiesta);
	}

	@Override
	public String estraiValoreCollezionamentoDati(Logger log, Dati datiRichiesta) throws PluginsException {
		return estraiValore(log, datiRichiesta);
	}

	private String estraiValore(Logger log, Dati datiRichiesta) throws PluginsException {
		SecurityToken securityToken = getSecurityToken(datiRichiesta);
		return estraiValore(log, securityToken, datiRichiesta);
	}
	
	protected SecurityToken getSecurityToken(Dati datiRichiesta) throws PluginsException {
		if(datiRichiesta.getPddContext() == null) {
			throw new PluginsException("PdDContext non disponibile");
		}
		SecurityToken securityToken = SecurityTokenUtilities.readSecurityToken(datiRichiesta.getPddContext());
		if(securityToken == null) {
			throw new PluginsException("SecurityToken non disponibile nel contesto");
		}
		return securityToken;
	}

	protected RestMessageSecurityToken getAuthorizationToken(SecurityToken securityToken) throws PluginsException {
		RestMessageSecurityToken authorization = securityToken.getAuthorization();
		if(authorization == null) {
			throw new PluginsException("Token ModI authorization non disponibile");
		}
		return authorization;
	}
	
	protected RestMessageSecurityToken getIntegrityToken(SecurityToken securityToken) throws PluginsException {
		RestMessageSecurityToken authorization = securityToken.getIntegrity();
		if(authorization == null) {
			throw new PluginsException("Token ModI integrity non disponibile");
		}
		return authorization;
	}

	protected CertificateInfo getCertificateFromToken(RestMessageSecurityToken token, String tokenType) throws PluginsException {
		CertificateInfo cert = token.getCertificate();
		if(cert == null) {
			throw new PluginsException("Certificato (x5c) non presente nel token " + tokenType);
		}
		return cert;
	}

	protected CertificateInfo getCertificate(SecurityToken securityToken, Dati datiRichiesta, boolean integrity) throws PluginsException {
		
		boolean soap = MessageType.SOAP_11.equals(datiRichiesta.getMessaggio().getMessageType()) || MessageType.SOAP_12.equals(datiRichiesta.getMessaggio().getMessageType());
		if(soap) {
			if(securityToken.getEnvelope() == null) {
				throw new PluginsException("Token ModI envelope non disponibile");
			}
			CertificateInfo cert = securityToken.getEnvelope().getCertificate();
			if(cert == null) {
				throw new PluginsException("Certificato non presente nel token envelope");
			}
			return cert;
		}else {
			if(integrity) {
				if(securityToken.getIntegrity() == null) {
					throw new PluginsException("Token ModI integrity non disponibile");
				}
				return getCertificateFromToken(securityToken.getIntegrity(), "integrity");
			} else {
				if(securityToken.getAuthorization() == null) {
					throw new PluginsException("Token ModI authorization non disponibile");
				}
				return getCertificateFromToken(securityToken.getAuthorization(), "authorization");
			}
		}
	}

}
