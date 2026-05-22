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
package org.openspcoop2.pdd.core.token.aws;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Helper per il mascheramento delle credenziali AWS sensibili (SecretAccessKey, SessionToken)
 * quando vengono serializzate verso storage di audit (tabella transazioni) o stampate nei log.
 * <p>
 * La maschera è "soft": mantiene i primi e gli ultimi 3 caratteri così da poter verificare
 * a occhio quale credenziale è stata usata (es. {@code wJa...KEY}), senza esporre il segreto
 * intero. Per stringhe troppo brevi (≤ 12 char) viene usato un placeholder fisso
 * ({@code ***}) per evitare un rapporto segreto-visibile/segreto-totale troppo alto.
 * </p>
 * <p>
 * Il mascheramento avviene esclusivamente sul percorso di serializzazione esterna (Jackson
 * verso DB transazioni, {@code toString()} verso log). L'istanza Java in memoria — usata dal
 * signer SigV4 per firmare le request al backend e cacheata via Java serialization nelle
 * Token Policy — continua a contenere il valore reale.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class AwsSecretMasker {

	/** Numero di caratteri visibili in testa e in coda al segreto mascherato. */
	private static final int VISIBLE_HEAD = 3;
	private static final int VISIBLE_TAIL = 3;

	/**
	 * Soglia minima sopra cui il mascheramento parziale è applicabile in sicurezza:
	 * sotto questa soglia il rapporto caratteri-visibili / lunghezza totale sarebbe
	 * troppo alto, quindi si applica il placeholder fisso.
	 */
	private static final int MIN_LEN_FOR_PARTIAL_MASK = 12;

	/** Placeholder usato per stringhe brevi o input null/vuoti. */
	private static final String FULL_MASK = "***";

	/** Separator tra prefisso e suffisso del valore mascherato. */
	private static final String SEPARATOR = "...";

	/**
	 * Pattern per la maschera dei valori dentro tag XML STS sensibili. Cattura group 2 = content
	 * tra apertura e chiusura del tag. Coerente con la response standard {@code AssumeRoleResponse}.
	 */
	private static final Pattern STS_SECRET_TAGS = Pattern.compile(
			"(<(?:SecretAccessKey|SessionToken)>)([^<]*)(</(?:SecretAccessKey|SessionToken)>)");

	private AwsSecretMasker() {
		// utility class
	}

	/**
	 * Mascheramento attivo solo se la property
	 * {@code org.openspcoop2.pdd.retrieveToken.saveAsTokenInfo.awsV4.maskSecrets} è {@code true}
	 * (default). Letto via {@link OpenSPCoop2Properties}; in caso il singleton non sia ancora
	 * disponibile (es. boot precoce), si applica il default {@code true} (security-by-default).
	 */
	private static boolean isMaskingEnabled() {
		try {
			OpenSPCoop2Properties props = OpenSPCoop2Properties.getInstance();
			if (props != null) {
				return props.isGestioneRetrieveTokenSaveAsTokenInfoAwsV4MaskSecrets();
			}
		} catch (Exception ignore) {
			// singleton non ancora inizializzato: fallback secure-by-default
		}
		return true;
	}

	/**
	 * Applica il mascheramento "soft" a un singolo valore segreto: primi e ultimi
	 * {@value #VISIBLE_HEAD} caratteri visibili, in mezzo {@code "..."}. Valori più corti
	 * di {@value #MIN_LEN_FOR_PARTIAL_MASK} caratteri (e null/vuoti) sono ritornati come
	 * placeholder fisso {@code ***}. Se il mascheramento è disabilitato via property, il
	 * valore originale viene ritornato in chiaro.
	 */
	public static String mask(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		if (!isMaskingEnabled()) {
			return value;
		}
		int len = value.length();
		if (len <= MIN_LEN_FOR_PARTIAL_MASK) {
			return FULL_MASK;
		}
		return value.substring(0, VISIBLE_HEAD) + SEPARATOR + value.substring(len - VISIBLE_TAIL);
	}

	/**
	 * Maschera i contenuti dei tag XML {@code <SecretAccessKey>} e {@code <SessionToken>} dentro
	 * la response STS, lasciando intatti gli altri elementi ({@code AccessKeyId}, {@code Expiration},
	 * ecc.) per l'audit. Se il mascheramento è disabilitato via property, lo XML viene ritornato
	 * intatto.
	 */
	public static String maskStsXmlSecrets(String xml) {
		if (xml == null || xml.isEmpty()) {
			return xml;
		}
		if (!isMaskingEnabled()) {
			return xml;
		}
		Matcher m = STS_SECRET_TAGS.matcher(xml);
		StringBuffer sb = new StringBuffer(xml.length());
		while (m.find()) {
			String openTag = m.group(1);
			String content = m.group(2);
			String closeTag = m.group(3);
			// mask diretto (no recursive isMaskingEnabled check, già verificato sopra)
			String masked = maskAlways(content);
			m.appendReplacement(sb, Matcher.quoteReplacement(openTag + masked + closeTag));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String maskAlways(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		int len = value.length();
		if (len <= MIN_LEN_FOR_PARTIAL_MASK) {
			return FULL_MASK;
		}
		return value.substring(0, VISIBLE_HEAD) + SEPARATOR + value.substring(len - VISIBLE_TAIL);
	}


	/**
	 * Serializer Jackson da applicare ai campi sensibili di {@code AWSCredentialBag} via
	 * {@code @JsonSerialize(using = AwsSecretMasker.Serializer.class)}. Maschera al volo
	 * durante la conversione in JSON; l'istanza in memoria resta intatta per il signer.
	 */
	public static class Serializer extends JsonSerializer<String> {
		@Override
		public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			if (value == null) {
				gen.writeNull();
				return;
			}
			gen.writeString(mask(value));
		}
	}
}
