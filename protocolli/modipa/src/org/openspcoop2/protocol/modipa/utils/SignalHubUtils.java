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

public class SignalHubUtils {
	
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
