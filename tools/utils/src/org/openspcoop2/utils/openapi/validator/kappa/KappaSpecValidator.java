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

package org.openspcoop2.utils.openapi.validator.kappa;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.github.erosb.kappa.core.model.v3.OAI3Context;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;

/**
 * Validatore di specifica OpenAPI basato su kappa (com.github.erosb).
 *
 * Kappa non espone un OpenApi3Validator separato come openapi4j: la
 * verifica strutturale avviene nella fase di {@code TreeUtil.json.convertValue(...)},
 * che fallisce se il documento non è ben formato secondo il modello
 * OpenAPI 3.0/3.1. Riferimenti esterni sono ignorati (verrà emesso solo
 * l'eventuale warning di parsing già accumulato a monte).
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public class KappaSpecValidator implements IApiSpecValidator {

	@SuppressWarnings("unused")
	private KappaSpecConfig config;

	@Override
	public void init(Logger log, IApiSpecConfig config) throws ProcessingException {
		if (config == null)
			throw new ProcessingException("Config is null");
		if (!(config instanceof KappaSpecConfig))
			throw new ProcessingException("Config must be a KappaSpecConfig (got " + config.getClass().getName() + ")");
		this.config = (KappaSpecConfig) config;
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
			throw new ProcessingException("Kappa spec validator supports only OpenapiApi class");

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
							refNonRisolvibili = true;
							break;
						}
					}
				}
			}
			if (!refNonRisolvibili) {
				try {
					OAI3Context context = new OAI3Context(new URI("file:/").toURL(), schemaNodeRoot, null);
					OpenApi3 openApi = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
					openApi.setContext(context);
				} catch (Throwable e) {
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
