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
package org.openspcoop2.utils.oauth2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.JWTParser;
import org.slf4j.Logger;

/**
 * Validatore semplificato per claim JWT.
 *
 * Verifica solamente:
 * 1. Presenza dei claim specificati
 * 2. Valore del claim deve essere uguale a uno di quelli nella lista valori ammessi
 *
 * Esempio di utilizzo con mappa:
 * <pre>
 * Map&lt;String, List&lt;String&gt;&gt; claimsToValidate = new HashMap&lt;&gt;();
 * claimsToValidate.put("role", Arrays.asList("admin", "operator"));
 * claimsToValidate.put("tenant", Arrays.asList("tenant-a", "tenant-b"));
 *
 * Oauth2ClaimValidator validator = new Oauth2ClaimValidator(log, claimsToValidate);
 * ValidationResult result = validator.validate(token);
 *
 * if (result.isValid()) {
 *     // Token valido
 * } else {
 *     // Token non valido: result.getErrors()
 * }
 * </pre>
 *
 * Esempio di utilizzo con Properties (integrazione GovWay):
 * <pre>
 * // In console.properties o monitor.properties:
 * // oauth2.claims.role=admin,operator,viewer
 * // oauth2.claims.tenant=tenant-a,tenant-b
 * // oauth2.claims.environment=production,staging
 *
 * Properties loginProperties = ConsoleProperties.getInstance().getLoginProperties();
 * SimpleJWTClaimValidator validator = new SimpleJWTClaimValidator(log, loginProperties);
 * ValidationResult result = validator.validate(accessToken);
 * </pre>
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Oauth2ClaimValidator {

	private static final String MATCH_MODE_PREFIX_OR_SHORT = "[]";
	private static final String MATCH_MODE_PREFIX_OR = "[or]";
	private static final String MATCH_MODE_PREFIX_AND = "[and]";

	private Logger log;
	private Map<String, List<String>> claimsToValidate;
	private Map<String, ClaimMatchMode> claimsMatchMode;

	/**
	 * Costruttore che accetta Properties e costruisce automaticamente la mappa dei claim.
	 * Legge le properties nel formato: oauth2.claims.NOME_CLAIM=valore1,valore2,valore3
	 *
	 * Esempio in console.properties:
	 * <pre>
	 * oauth2.claims.role=admin,operator,viewer
	 * oauth2.claims.tenant=tenant-a,tenant-b
	 * oauth2.claims.environment=production,staging,development
	 * </pre>
	 *
	 * @param log Logger per messaggi di debug/errore
	 * @param loginProperties Properties ottenute da ConsoleProperties.getInstance().getLoginProperties()
	 *                        o PddMonitorProperties.getInstance().getLoginProperties()
	 */
	public Oauth2ClaimValidator(Logger log, Properties loginProperties) {
		this.log = log;
		this.claimsMatchMode = new HashMap<>();
		this.claimsToValidate = parseClaimsFromProperties(loginProperties);
	}

	/**
	 * Estrae i claim da validare dalle Properties.
	 * Cerca tutte le chiavi che iniziano con "oauth2.claims." e costruisce la mappa.
	 *
	 * @param properties Properties contenenti la configurazione
	 * @return Mappa dei claim da validare
	 */
	private Map<String, List<String>> parseClaimsFromProperties(Properties properties) {
		Map<String, List<String>> claims = new HashMap<>();

		if (properties == null || properties.isEmpty()) {
			logDebug("Properties vuote, nessun claim da validare configurato");
			return claims;
		}

		// Itera su tutte le properties
		for (String key : properties.stringPropertyNames()) {
			if (isClaimValidationProperty(key)) {
				processClaimProperty(key, properties, claims);
			}
		}

		logClaimsSummary(claims);
		return claims;
	}

	/**
	 * Verifica se una property key è relativa alla validazione dei claim
	 */
	private boolean isClaimValidationProperty(String key) {
		return key != null && key.startsWith(OAuth2Costanti.PROP_OAUTH2_CLAIMS_VALIDATION);
	}

	/**
	 * Processa una singola property di validazione claim
	 */
	private void processClaimProperty(String key, Properties properties, Map<String, List<String>> claims) {
		String claimName = extractClaimName(key);

		if (claimName.isEmpty()) {
			logWarn("Property '{}' non valida: nome claim vuoto", key);
			return;
		}

		String valuesStr = properties.getProperty(key);
		if (StringUtils.isNotEmpty(valuesStr)) {
			// Rileva prefisso match mode
			ClaimMatchMode matchMode = ClaimMatchMode.EXACT;
			String valuesStrWithoutPrefix = valuesStr;
			if (valuesStr.startsWith(MATCH_MODE_PREFIX_OR)) {
				matchMode = ClaimMatchMode.OR;
				valuesStrWithoutPrefix = valuesStr.substring(MATCH_MODE_PREFIX_OR.length());
			} else if (valuesStr.startsWith(MATCH_MODE_PREFIX_AND)) {
				matchMode = ClaimMatchMode.AND;
				valuesStrWithoutPrefix = valuesStr.substring(MATCH_MODE_PREFIX_AND.length());
			} else if (valuesStr.startsWith(MATCH_MODE_PREFIX_OR_SHORT)) {
				matchMode = ClaimMatchMode.OR;
				valuesStrWithoutPrefix = valuesStr.substring(MATCH_MODE_PREFIX_OR_SHORT.length());
			}

			List<String> allowedValues = parseAllowedValues(valuesStrWithoutPrefix);

			if (matchMode != ClaimMatchMode.EXACT) {
				this.claimsMatchMode.put(claimName, matchMode);
			}

			claims.put(claimName, allowedValues);
			logClaimConfiguration(claimName, allowedValues);
		}
	}

	/**
	 * Estrae il nome del claim dalla property key
	 */
	private String extractClaimName(String key) {
		return key.substring(OAuth2Costanti.PROP_OAUTH2_CLAIMS_VALIDATION.length());
	}

	/**
	 * Parsifica i valori ammessi da una stringa CSV
	 */
	private List<String> parseAllowedValues(String valuesStr) {
		List<String> allowedValues = new ArrayList<>();
		String[] values = valuesStr.split(",");

		for (String value : values) {
			String trimmedValue = value.trim();
			if (!trimmedValue.isEmpty()) {
				allowedValues.add(trimmedValue);
			}
		}

		return allowedValues.isEmpty() ? null : allowedValues;
	}

	/**
	 * Log della configurazione di un singolo claim
	 */
	private void logClaimConfiguration(String claimName, List<String> allowedValues) {
		if (allowedValues == null) {
			logDebug("Claim '{}' configurato per verifica solo presenza", claimName);
		} else {
			logDebug("Claim '{}' configurato con valori ammessi: {}", claimName, allowedValues);
		}
	}

	/**
	 * Log del riepilogo claim configurati
	 */
	private void logClaimsSummary(Map<String, List<String>> claims) {
		if (this.log == null) {
			return;
		}

		if (claims.isEmpty()) {
			this.log.info("Nessun claim da validare configurato nelle properties");
		} else {
			this.log.info("Configurati {} claim da validare", claims.size());
		}
	}

	/**
	 * Metodo helper per log debug
	 */
	private void logDebug(String message, Object... args) {
		if (this.log != null) {
			this.log.debug(message, args);
		}
	}

	/**
	 * Metodo helper per log warn
	 */
	private void logWarn(String message, Object... args) {
		if (this.log != null) {
			this.log.warn(message, args);
		}
	}

	/**
	 * Valida i claim specificati nel token JWT.
	 *
	 * Per ogni coppia (claimName, allowedValues) configurata nel costruttore:
	 * - Verifica che il claim esista nel token
	 * - Verifica che il valore del claim sia presente nella lista dei valori ammessi
	 *
	 * @param token Token JWT da validare
	 * @return Risultato della validazione con lista errori
	 */
	public ValidationResult validate(String token) {
		ValidationResult result = new ValidationResult();
		result.setValid(true);

		if (this.claimsToValidate == null || this.claimsToValidate.isEmpty()) {
			if (this.log != null) {
				this.log.debug("Nessun claim da validare");
			}
			return result;
		}

		if (token == null || token.isEmpty()) {
			result.setValid(false);
			result.addError("Token JWT non fornito");
			return result;
		}

		try {
			// Crea il parser JWT per il token fornito
			JWTParser jwtParser = new JWTParser(token);

			// Legge tutti i claim dal payload del token
			Map<String, String> tokenClaims = jwtParser.getPayloadClaims();

			if (tokenClaims == null || tokenClaims.isEmpty()) {
				result.setValid(false);
				result.addError("Token JWT non contiene claim nel payload");
				return result;
			}

			// Valida ogni claim specificato
			for (Map.Entry<String, List<String>> entry : this.claimsToValidate.entrySet()) {
				String claimName = entry.getKey();
				List<String> allowedValues = entry.getValue();

				validateSingleClaim(claimName, allowedValues, tokenClaims, result);
			}

		} catch (UtilsException e) {
			result.setValid(false);
			result.addError("Errore durante la lettura dei claim dal token: " + e.getMessage());
			if (this.log != null) {
				this.log.error("Errore lettura claim dal token", e);
			}
		}

		return result;
	}

	/**
	 * Valida un singolo claim
	 */
	private void validateSingleClaim(String claimName, List<String> allowedValues,
			Map<String, String> tokenClaims, ValidationResult result) {

		// Verifica presenza del claim nel token
		String claimValue = tokenClaims.get(claimName);
		if (claimValue == null) {
			result.setValid(false);
			result.addError("Claim '" + claimName + "' non presente nel token");
			return;
		}

		// Se non ci sono valori ammessi specificati, verifica solo la presenza
		if (allowedValues == null || allowedValues.isEmpty()) {
			if (this.log != null) {
				this.log.debug("Claim '{}' presente con valore: {}", claimName, claimValue);
			}
			return;
		}

		// Determina la modalità di confronto
		ClaimMatchMode matchMode = this.claimsMatchMode.get(claimName);
		if (matchMode == null) {
			matchMode = ClaimMatchMode.EXACT;
		}

		boolean valid;
		if (matchMode == ClaimMatchMode.EXACT) {
			// Verifica che il valore sia nella lista dei valori ammessi
			valid = false;
			for (String allowedValue : allowedValues) {
				if (claimValue.equals(allowedValue)) {
					valid = true;
					break;
				}
			}
		} else {
			// Modalità OR/AND: splitta il valore del claim per virgola
			List<String> claimTokens = new ArrayList<>();
			for (String t : claimValue.split(",")) {
				String trimmed = t.trim();
				if (!trimmed.isEmpty()) {
					claimTokens.add(trimmed);
				}
			}

			if (matchMode == ClaimMatchMode.OR) {
				// Almeno uno dei valori configurati deve essere presente nel claim
				valid = false;
				for (String allowedValue : allowedValues) {
					if (claimTokens.contains(allowedValue)) {
						valid = true;
						break;
					}
				}
			} else {
				// AND: tutti i valori configurati devono essere presenti nel claim
				valid = true;
				for (String allowedValue : allowedValues) {
					if (!claimTokens.contains(allowedValue)) {
						valid = false;
						break;
					}
				}
			}
		}

		if (!valid) {
			result.setValid(false);
			String modeLabel = (matchMode == ClaimMatchMode.EXACT) ? "" : " (mode: " + matchMode.name().toLowerCase() + ")";
			result.addError("Claim '" + claimName + "' ha valore '" + claimValue +
					"' che non è tra i valori ammessi: " + allowedValues + modeLabel);
			if (this.log != null) {
				this.log.warn("Claim '{}' ha valore '{}' non ammesso. Valori ammessi: {} (mode: {})",
						claimName, claimValue, allowedValues, matchMode);
			}
		} else {
			if (this.log != null) {
				this.log.debug("Claim '{}' validato con successo. Valore: {} (mode: {})", claimName, claimValue, matchMode);
			}
		}
	}

	/**
	 * Classe che rappresenta il risultato della validazione
	 */
	public static class ValidationResult {
		private boolean valid;
		private List<String> errors;

		public ValidationResult() {
			this.valid = true;
			this.errors = new ArrayList<>();
		}

		/**
		 * @return true se la validazione è stata superata
		 */
		public boolean isValid() {
			return this.valid;
		}

		/**
		 * Imposta lo stato di validità
		 *
		 * @param valid true se valido
		 */
		public void setValid(boolean valid) {
			this.valid = valid;
		}

		/**
		 * @return Lista degli errori riscontrati
		 */
		public List<String> getErrors() {
			return this.errors;
		}

		/**
		 * Aggiunge un errore alla lista
		 *
		 * @param error Messaggio di errore
		 */
		public void addError(String error) {
			this.errors.add(error);
		}

		/**
		 * @return true se ci sono errori
		 */
		public boolean hasErrors() {
			return !this.errors.isEmpty();
		}

		/**
		 * @return Stringa con tutti gli errori separati da punto e virgola
		 */
		public String getErrorsAsString() {
			if (this.errors.isEmpty()) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < this.errors.size(); i++) {
				if (i > 0) {
					sb.append("; ");
				}
				sb.append(this.errors.get(i));
			}
			return sb.toString();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ValidationResult [valid=").append(this.valid);
			if (!this.valid && !this.errors.isEmpty()) {
				sb.append(", errors=").append(getErrorsAsString());
			}
			sb.append("]");
			return sb.toString();
		}
	}

	/**
	 * Modalità di confronto per i valori dei claim
	 */
	public enum ClaimMatchMode {
		/** Confronto esatto: il valore del claim deve corrispondere a uno dei valori ammessi */
		EXACT,
		/** OR: almeno uno dei valori configurati deve essere presente tra i valori del claim (separati da virgola) */
		OR,
		/** AND: tutti i valori configurati devono essere presenti tra i valori del claim (separati da virgola) */
		AND
	}
}
