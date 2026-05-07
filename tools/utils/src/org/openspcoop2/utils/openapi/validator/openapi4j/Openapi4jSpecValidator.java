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

package org.openspcoop2.utils.openapi.validator.openapi4j;

import java.net.URI;
import java.util.List;

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
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.rest.IApiSpecConfig;
import org.openspcoop2.utils.rest.IApiSpecValidator;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Validatore di specifica OpenAPI 3.0 basato su openapi4j.
 *
 * Replica il comportamento storico di
 * {@code OpenapiApi._validateOpenAPI3()}: deserializza il documento
 * raw nel modello {@link OpenApi3} e lo passa a
 * {@link OpenApi3Validator}. Riferimenti esterni sono ignorati (verrà
 * emesso solo l'eventuale warning di parsing già accumulato a monte).
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public class Openapi4jSpecValidator implements IApiSpecValidator {

	@SuppressWarnings("unused")
	private Openapi4jSpecConfig config;

	@Override
	public void init(Logger log, IApiSpecConfig config) throws ProcessingException {
		if (config == null)
			throw new ProcessingException("Config is null");
		if (!(config instanceof Openapi4jSpecConfig))
			throw new ProcessingException("Config must be an Openapi4jSpecConfig (got " + config.getClass().getName() + ")");
		this.config = (Openapi4jSpecConfig) config;
	}

	@Override
	public void close(Logger log) throws ProcessingException {
		// no-op
	}

	@Override
	public void validate(Logger log, Api api) throws ProcessingException, ParseWarningException {

		if (api == null)
			throw new ProcessingException("Api is null");
		if (!(api instanceof OpenapiApi))
			throw new ProcessingException("Openapi4j spec validator supports only OpenapiApi class");

		OpenapiApi openapiApi = (OpenapiApi) api;
		String apiRaw = openapiApi.getApiRaw();
		String parseWarningResult = openapiApi.getParseWarningResult();

		try {

			YAMLUtils yamlUtils = YAMLUtils.getInstance();
			JSONUtils jsonUtils = JSONUtils.getInstance();

			boolean apiRawIsYaml = yamlUtils.isYaml(apiRaw);
			JsonNode schemaNodeRoot = null;
			if (apiRawIsYaml) {
				schemaNodeRoot = yamlUtils.getAsNode(apiRaw);
			} else {
				schemaNodeRoot = jsonUtils.getAsNode(apiRaw);
			}

			JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
			List<String> refPath = null;
			try {
				refPath = engine.getStringMatchPattern(schemaNodeRoot, "$..$ref");
			} catch (JsonPathNotFoundException notFound) {
				// nessun $ref nel documento
			}
			boolean refNonRisolvibili = false;
			if (refPath != null && !refPath.isEmpty()) {
				for (String refP : refPath) {
					if (refP != null) {
						String ref = refP.trim();
						if (ref.startsWith("#")) {
							continue; // relativo al file stesso
						} else {
							refNonRisolvibili = true; // ref verso altri file (emette solo warning, se presente)
							break;
						}
					}
				}
			}
			if (!refNonRisolvibili) {
				OAI3Context context = null;
				OpenApi3 openApi4j = null;
				try {
					context = new OAI3Context(new URI("file:/").toURL(), schemaNodeRoot, null);
					openApi4j = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
					openApi4j.setContext(context);
				} catch (Throwable e) {
					throw new ProcessingException(e.getMessage(), e);
				}
				try {
					ValidationResults results = OpenApi3Validator.instance().validate(openApi4j);
					if (!results.isValid()) {
						throw new ProcessingException(results.toString());
					}
				} catch (org.openapi4j.core.validation.ValidationException valExc) {
					if (valExc.results() != null) {
						throw new ProcessingException(valExc.results().toString());
					} else {
						throw new ProcessingException(valExc.getMessage());
					}
				} catch (ProcessingException pe) {
					throw pe;
				} catch (Throwable e) {
					e.printStackTrace(System.out);
					Throwable eAnalyze = Utilities.getInnerNotEmptyMessageException(e);
					throw new ProcessingException(eAnalyze != null ? eAnalyze.getMessage() : e.getMessage(), e);
				}
			}
		} catch (ProcessingException pe) {
			if (parseWarningResult != null && StringUtils.isNotEmpty(parseWarningResult)) {
				throw new ProcessingException("\n" + parseWarningResult + "\n" + pe.getMessage());
			} else {
				throw new ProcessingException(pe.getMessage());
			}
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		}
	}

}
