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
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificatePrincipal;
import org.slf4j.Logger;

/**
 * RateLimitingIntegrityCertificateSubject
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RateLimitingIntegrityCertificateSubject extends AbstractModIRateLimiting {

	@Override
	protected String estraiValore(Logger log, SecurityToken securityToken, Dati datiRichiesta) throws PluginsException {
		CertificateInfo cert = getCertificate(securityToken, datiRichiesta, true);
		CertificatePrincipal subject = cert.getSubject();
		if(subject == null) {
			throw new PluginsException("Subject non presente nel certificato di firma ModI");
		}
		return subject.toString();
	}

}
