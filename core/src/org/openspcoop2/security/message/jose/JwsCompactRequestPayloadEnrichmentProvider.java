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

package org.openspcoop2.security.message.jose;

import java.util.List;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;

/**
 * JwsCompactRequestPayloadEnrichmentProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JwsCompactRequestPayloadEnrichmentProvider extends SignatureSenderProvider {

	private static final String ID_ISSUER = "issuer";
	private static final String ID_AUDIENCE_MANUAL = "audienceManual";
	private static final String ID_TTL = "ttl";

	public JwsCompactRequestPayloadEnrichmentProvider() {
		super();
	}

	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException {
		if(ID_ISSUER.equals(id) || ID_AUDIENCE_MANUAL.equals(id) || ID_TTL.equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			List<String> listBody = DynamicHelperCostanti.getLABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI(false, false, false);
			pInfo.setListBody(listBody);
			return pInfo;
		}
		return super.getProviderInfo(id);
	}

}
