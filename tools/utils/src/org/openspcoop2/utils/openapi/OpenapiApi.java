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


package org.openspcoop2.utils.openapi;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openapi4j.core.model.v3.OAI3Context;
import org.openapi4j.core.util.TreeUtil;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.openapi4j.parser.validation.v3.OpenApi3Validator;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

/**
 * SwaggerApi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class OpenapiApi extends Api {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Flag statico che seleziona l'implementazione utilizzata per rilevare i riferimenti
	 * '$ref' esterni nel documento OpenAPI durante la validazione.
	 * <ul>
	 *   <li>false (default): scansione diretta dell'albero JsonNode; considera solo i
	 *       '$ref' con valore stringa (i veri JSON Reference) e ignora le proprieta'
	 *       che hanno '$ref' come nome.</li>
	 *   <li>true: vecchia (deprecata) implementazione basata sulla query JsonPath
	 *       '$..$ref' invocata in modalita' lenient, che salta i match con valore non
	 *       stringa (es. proprieta' '$ref' di uno schema). Funzionalmente equivalente
	 *       ma indiretta e mantenuta solo per retro-compatibilita'.</li>
	 * </ul>
	 */
	private static volatile boolean useLegacyDollarRefValidation = false;
	public static void setUseLegacyDollarRefValidation(boolean v) {
		useLegacyDollarRefValidation = v;
	}
	public static boolean isUseLegacyDollarRefValidation() {
		return useLegacyDollarRefValidation;
	}

	private transient OpenAPI api;
	private transient Map<String, Schema<?>> definitions;
	private String apiRaw;
	private String parseWarningResult;
	private ApiFormats format;

	/** Pattern precompilato per la detection 3.1 sull'apiRaw (evita di ricompilare la regex ad ogni chiamata). */
	private static final java.util.regex.Pattern OPENAPI_31_PATTERN =
			java.util.regex.Pattern.compile("(?s).*[\"']?openapi[\"']?\\s*:\\s*[\"']?3\\.1.*");

	/**
	 * Esito memoizzato della detection OpenAPI 3.0 vs 3.1. La detection è deterministica
	 * (dipende da {@code format} e {@code apiRaw}, entrambi immutabili dopo la costruzione),
	 * quindi viene calcolata una sola volta. Il campo è volutamente NON {@code transient}:
	 * sopravvive a eventuale serializzazione/deserializzazione (a differenza del modello
	 * swagger-parser {@code api}), evitando di rieseguire la regex sull'intero spec.
	 */
	private volatile Boolean openApi31;

	/**
	 * Cache delle strutture di validazione costruite da {@code init()} degli engine, indicizzata per
	 * una chiave derivata dalla libreria + flag di config che impattano l'inizializzazione (es.
	 * {@code mergeAPISpec}, {@code validateAPISpec}). Più porte sullo stesso accordo possono
	 * configurare engine/parametri diversi: tenendo entry distinte qui evitiamo che la cache popolata
	 * da una porta venga (mis)riusata da un'altra che si attende un tipo/payload differente.
	 */
	private final java.util.concurrent.ConcurrentMap<String, OpenapiApiValidatorStructure> validationStructures = new java.util.concurrent.ConcurrentHashMap<>();

	public OpenapiApi(ApiFormats format, OpenAPI swagger, String apiRaw, String parseWarningResult) {
		this.format = format;
		this.api = swagger;
		this.apiRaw = apiRaw;
		this.parseWarningResult = parseWarningResult;
		this.definitions = new HashMap<>();
	}
	
	public ApiFormats getFormat() {
		return this.format;
	}
	
	public OpenAPI getApi() {
		return this.api;
	}

	public String getApiRaw() {
		return this.apiRaw;
	}

	public String getParseWarningResult() {
		return this.parseWarningResult;
	}

	/**
	 * Verifica se l'Api passato è un OpenAPI 3.1.x. La detection privilegia il
	 * modello swagger-parser (transient: può essere {@code null} dopo
	 * serializzazione/deserializzazione) e ricade sull'apiRaw via regex.
	 *
	 * @return {@code true} se l'api è OpenAPI 3.1, {@code false} altrimenti
	 *         (incluso il caso in cui {@code api} non sia un {@code OpenapiApi}
	 *         oppure il formato non sia {@code OPEN_API_3}).
	 */
	public static boolean isOpenApi31(Api api) {
		if (!(api instanceof OpenapiApi)) {
			return false;
		}
		return ((OpenapiApi) api).isOpenApi31();
	}

	/**
	 * Variante d'istanza con memoizzazione dell'esito: la regex sull'apiRaw viene eseguita
	 * al massimo una volta per {@link OpenapiApi}, evitando di scandire l'intero spec ad ogni
	 * richiesta (il chiamante {@code ValidatoreMessaggiApplicativiRest} la invoca per ogni messaggio).
	 *
	 * @return {@code true} se l'api è OpenAPI 3.1, {@code false} altrimenti (incluso il caso
	 *         in cui il formato non sia {@code OPEN_API_3}).
	 */
	public boolean isOpenApi31() {
		Boolean cached = this.openApi31;
		if (cached != null) {
			return cached.booleanValue();
		}
		// Race benigna: la detection è deterministica, eventuali calcoli concorrenti danno lo stesso esito.
		boolean result = computeOpenApi31();
		this.openApi31 = result;
		return result;
	}

	private boolean computeOpenApi31() {
		if (!ApiFormats.OPEN_API_3.equals(this.format)) {
			return false;
		}
		// 1) modello swagger-parser, se ancora disponibile
		try {
			if (this.api != null && this.api.getOpenapi() != null
					&& this.api.getOpenapi().startsWith("3.1")) {
				return true;
			}
		} catch (Exception e) {
			// fallthrough
		}
		// 2) fallback: scan dell'apiRaw per la dichiarazione di versione
		// pattern: openapi: "3.1...", openapi: 3.1..., "openapi": "3.1..."
		String raw = this.apiRaw;
		if (raw == null) {
			return false;
		}
		return OPENAPI_31_PATTERN.matcher(raw).matches();
	}
	
	public Map<String, Schema<?>> getDefinitions() {
		return this.definitions;
	}

	public Map<String, Schema<?>> getAllDefinitions() {
		Map<String, Schema<?>> map = new HashMap<>();
		map.putAll(this.getDefinitions());
		if(this.api.getComponents() != null && this.api.getComponents().getSchemas() != null)
			for(String k: this.api.getComponents().getSchemas().keySet()) {
				map.put(k, this.api.getComponents().getSchemas().get(k));
			}
		return map;
	}

	public void setDefinitions(Map<String, Schema<?>> definitions) {
		this.definitions = definitions;
	}

	/**
	 * Recupera la validation structure sotto la chiave indicata. La chiave deve essere costruita
	 * dall'engine dalle property di config che impattano la cache (libreria + mergeAPISpec +
	 * validateAPISpec + ...), in modo che porte diverse con configurazioni diverse mantengano cache
	 * separate.
	 */
	public OpenapiApiValidatorStructure getValidationStructure(String cacheKey) {
		return this.validationStructures.get(cacheKey);
	}

	/** Memorizza la validation structure sotto la chiave indicata (vedi {@link #getValidationStructure(String)}). */
	public void setValidationStructure(String cacheKey, OpenapiApiValidatorStructure validationStructure) {
		this.validationStructures.put(cacheKey, validationStructure);
	}
	
	@Override
	public void validate() throws ProcessingException,ParseWarningException {
		this.validate(false);
	}
	@Override
	public void validate(boolean validateBodyParameterElement) throws ProcessingException, ParseWarningException {
		this.validate(false, false);
	}
	@Override
	public void validate(boolean usingFromSetProtocolInfo, boolean validateBodyParameterElement) throws ProcessingException, ParseWarningException {
		
		// Primo valido l'openapi
		if(!usingFromSetProtocolInfo) {
			// Se valido poi non riesco a caricare OpenAPI che comunque non sono validi
			if(ApiFormats.OPEN_API_3.equals(this.format)) {
				this.validateOpenAPI3Engine();
			}
		}
				
		// Valido la struttura
		super.validate(usingFromSetProtocolInfo, validateBodyParameterElement);
		
		// Se ho trovato dei warning li emetto
		if(this.parseWarningResult!=null && StringUtils.isNotEmpty(this.parseWarningResult)) {
			throw new ParseWarningException("\n"+this.parseWarningResult);
		}
	}
	
	private void validateOpenAPI3Engine() throws ProcessingException {
		
		try {
		
			YAMLUtils yamlUtils = YAMLUtils.getInstance();
			JSONUtils jsonUtils = JSONUtils.getInstance();
									
			boolean apiRawIsYaml = yamlUtils.isYaml(this.apiRaw);
			JsonNode schemaNodeRoot = null;
			if(apiRawIsYaml) {
				schemaNodeRoot = yamlUtils.getAsNode(this.apiRaw);
			}
			else {
				schemaNodeRoot = jsonUtils.getAsNode(this.apiRaw);
			}
		
			boolean refNonRisolvibili;
			if(useLegacyDollarRefValidation) {
				refNonRisolvibili = checkExternalRefLegacy(schemaNodeRoot);
			}
			else {
				refNonRisolvibili = hasExternalRef(schemaNodeRoot);
			}
			if(!refNonRisolvibili) {
				// Costruisco OpenAPI3					
				OAI3Context context = null;
				OpenApi3 openApi4j = null;
				try {
					context = new OAI3Context(new URI("file:/").toURL(), schemaNodeRoot, null);
					openApi4j = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
					openApi4j.setContext(context);
				}catch(Throwable e) {
					throw new ProcessingException(e.getMessage(), e);
				}
				try {
					ValidationResults results = OpenApi3Validator.instance().validate(openApi4j);
					if(!results.isValid()) {
						throw new ProcessingException(results.toString());
					}
				}catch(org.openapi4j.core.validation.ValidationException valExc) {
					if(valExc.results()!=null) {
						throw new ProcessingException(valExc.results().toString());
					}
					else {
						throw new ProcessingException(valExc.getMessage());
					}
				}catch(ProcessingException pe) {
					throw pe;
				}catch(Throwable e) {
					e.printStackTrace(System.out);
					Throwable eAnalyze = Utilities.getInnerNotEmptyMessageException(e);
					throw new ProcessingException(eAnalyze!=null ? eAnalyze.getMessage(): e.getMessage(), e);
				}
			}
		}
		catch(ProcessingException pe) {
			if(this.parseWarningResult!=null && StringUtils.isNotEmpty(this.parseWarningResult)) {
				throw new ProcessingException("\n"+this.parseWarningResult+"\n"+pe.getMessage());
			}
			else {
				throw new ProcessingException(pe.getMessage());
			}
		}
		catch(Exception e) {
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	/**
	 * Rileva la presenza di JSON Reference esterni (valore '$ref' che non inizia con '#')
	 * scandendo l'albero JsonNode. Vengono considerati solo i '$ref' con valore stringa:
	 * un eventuale '$ref' che compare come NOME di proprieta' di uno schema (con valore
	 * oggetto) non e' un JSON Reference e viene ignorato.
	 */
	private boolean hasExternalRef(JsonNode node) {
		if(node == null) {
			return false;
		}
		if(node.isObject()) {
			return objectHasExternalRef(node);
		}
		if(node.isArray()) {
			return arrayHasExternalRef(node);
		}
		return false;
	}
	private boolean objectHasExternalRef(JsonNode node) {
		if(isExternalRef(node.get("$ref"))) {
			return true;
		}
		for(Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {
			if(hasExternalRef(it.next())) {
				return true;
			}
		}
		return false;
	}
	private boolean arrayHasExternalRef(JsonNode node) {
		for(JsonNode el : node) {
			if(hasExternalRef(el)) {
				return true;
			}
		}
		return false;
	}
	private boolean isExternalRef(JsonNode refNode) {
		if(refNode == null || !refNode.isTextual()) {
			return false;
		}
		String ref = refNode.asText().trim();
		return !ref.isEmpty() && !ref.startsWith("#");
	}

	/**
	 * Vecchia implementazione di rilevamento dei '$ref' esterni basata sulla query
	 * JsonPath '$..$ref' eseguita in modalita' lenient: i match con valore non stringa
	 * (es. proprieta' di uno schema chiamata letteralmente '$ref') vengono saltati,
	 * quindi il comportamento e' funzionalmente equivalente a {@link #hasExternalRef(JsonNode)}.
	 *
	 * Mantenuta per retro-compatibilita' ed attivabile globalmente tramite
	 * {@link #setUseLegacyDollarRefValidation(boolean)}.
	 *
	 * Preferire {@link #hasExternalRef(JsonNode)}: scansione diretta
	 *             dell'albero JsonNode, piu' efficiente e senza il passaggio per JsonPath.
	 */
	private boolean checkExternalRefLegacy(JsonNode schemaNodeRoot) throws Exception {
		JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
		List<String> refPath = null;
		try {
			refPath = engine.getStringMatchPattern(schemaNodeRoot, "$..$ref", true);
		}catch(JsonPathNotFoundException notFound){
			/**System.out.println("NOT FOUND: "+notFound.getMessage());*/
		}
		if(refPath!=null && !refPath.isEmpty()) {
			for (String refP : refPath) {
				if(refP!=null) {
					String ref = refP.trim();
					if(ref.startsWith("#")) {
						/**continue;*/ // relativo verso il file stesso
					}
					else {
						return true; // ref verso altri file
					}
				}
			}
		}
		return false;
	}
}
