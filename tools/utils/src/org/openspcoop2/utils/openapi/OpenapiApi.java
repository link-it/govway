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
	private static boolean useLegacyDollarRefValidation = false;
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

	// struttura una volta che l'api è stata inizializzata per la validazione (e' serializzabile e cachabile)
	private OpenapiApiValidatorStructure validationStructure;

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

	public OpenapiApiValidatorStructure getValidationStructure() {
		return this.validationStructure;
	}

	public void setValidationStructure(OpenapiApiValidatorStructure validationStructure) {
		this.validationStructure = validationStructure;
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
