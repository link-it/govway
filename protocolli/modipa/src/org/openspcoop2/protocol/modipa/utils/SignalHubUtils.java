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
package org.openspcoop2.protocol.modipa.utils;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.pdd.config.DigestServiceParams;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.digest.DigestType;

/**
 * SignalHubUtils classe con metodi di utilita signal hub
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignalHubUtils {
	
	private SignalHubUtils() {}
	
	/**
	 * Funzioone per fare il parsing delle protocolProperties di un erogazione impostata per supportare singnal hub
	 * per generare nuove informazioni crittografiche per la creazione del digest
	 * @param idServizio
	 * @param eServiceProperties
	 * @param serial
	 * @return
	 * @throws ProtocolException
	 */
	public static DigestServiceParams generateDigestServiceParams(IDServizio idServizio, List<ProtocolProperty> eServiceProperties, Long serial) throws ProtocolException {
		Long durata = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_LIFETIME_ID, true);
		String algorithm = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ALGORITHM_ID);
		Long seedSize = ProtocolPropertiesUtils.getRequiredNumberValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_SEED_SIZE_ID, true);
		
		DigestServiceParams param = new DigestServiceParams();
		param.setIdServizio(idServizio);
		param.setDataRegistrazione(Instant.now());
		param.setDurata(durata.intValue());
		param.setDigestAlgorithm(DigestType.fromAlgorithmName(algorithm));
		param.setSeed(Base64.getEncoder().encode(new SecureRandom().generateSeed(seedSize.intValue())));
		param.setSerialNumber(serial);
		
		return param;
	}
	
	public static List<ProtocolProperty> obtainSignalHubProtocolProperty(Context context) {
		return ((List<?>) context.get(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_PROPERTIES))
		.stream()
		.map(e -> (ProtocolProperty)e)
		.collect(Collectors.toList());
	}
}
