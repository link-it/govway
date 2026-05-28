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

package org.openspcoop2.utils.openapi.validator.swagger;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.IApiSpecConfig;
import org.openspcoop2.utils.rest.IApiSpecValidator;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;
import org.slf4j.Logger;

import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.converter.SwaggerConverter;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

/**
 * Validatore di specifica basato su swagger-parser (io.swagger.v3.parser).
 *
 * Riparsa il documento raw e raccoglie i {@code messages} prodotti dal
 * parser. Se il modello {@code OpenAPI} non viene costruito, gli stessi
 * messaggi vengono propagati come {@link ProcessingException}; se invece
 * il modello esiste ma ci sono messaggi, vengono emessi come
 * {@link ParseWarningException}.
 *
 * Supporta sia OpenAPI 3.x che Swagger 2 (a seconda del formato dell'Api).
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SwaggerSpecValidator implements IApiSpecValidator {

	@SuppressWarnings("unused")
	private SwaggerSpecConfig config;

	@Override
	public void init(Logger log, IApiSpecConfig config) throws ProcessingException {
		if (config == null)
			throw new ProcessingException("Config is null");
		if (!(config instanceof SwaggerSpecConfig))
			throw new ProcessingException("Config must be a SwaggerSpecConfig (got " + config.getClass().getName() + ")");
		this.config = (SwaggerSpecConfig) config;
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
			throw new ProcessingException("Swagger spec validator supports only OpenapiApi class");
		if (OpenapiApi.isOpenApi31(api))
			throw new ProcessingException("La libreria 'swagger_request_validator' non supporta OpenAPI 3.1");

		OpenapiApi openapiApi = (OpenapiApi) api;
		String apiRaw = openapiApi.getApiRaw();
		String parseWarningResult = openapiApi.getParseWarningResult();
		ApiFormats format = openapiApi.getFormat();

		try {

			ParseOptions parseOptions = new ParseOptions();
			SwaggerParseResult pr = null;
			if (ApiFormats.SWAGGER_2.equals(format)) {
				pr = new SwaggerConverter().readContents(apiRaw, null, parseOptions);
			} else {
				pr = new OpenAPIV3Parser().readContents(apiRaw, null, parseOptions);
			}

			if (pr == null) {
				throw new ProcessingException("Parse result undefined");
			}

			StringBuilder bfMessage = new StringBuilder();
			if (pr.getMessages() != null && !pr.getMessages().isEmpty()) {
				for (String msg : pr.getMessages()) {
					if (bfMessage.length() > 0) {
						bfMessage.append("\n");
					}
					bfMessage.append("- ").append(msg);
				}
			}

			if (pr.getOpenAPI() == null) {
				if (bfMessage.length() > 0) {
					throw new ProcessingException("Parse failed: " + bfMessage.toString());
				} else {
					throw new ProcessingException("Parse failed");
				}
			}

			if (bfMessage.length() > 0) {
				throw new ParseWarningException(bfMessage.toString());
			}
		} catch (ParseWarningException pwe) {
			throw pwe;
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
