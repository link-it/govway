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

package org.openspcoop2.utils.rest;

import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.slf4j.Logger;

/**
 * Fallback {@link IApiSpecValidator}: replica la validazione strutturale
 * dichiarata nella classe base {@link Api#validate(boolean, boolean)},
 * senza coinvolgere alcun engine OpenAPI.
 *
 * Viene usato di default da {@link ApiFactory#newApiSpecValidator(String)}
 * quando l'engine richiesto è {@code null} o non è istanziabile.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public class BaseSpecValidator implements IApiSpecValidator {

	private BaseSpecConfig config;

	@Override
	public void init(Logger log, IApiSpecConfig config) throws ProcessingException {
		if (config == null)
			throw new ProcessingException("Config is null");
		if (!(config instanceof BaseSpecConfig))
			throw new ProcessingException("Config must be a BaseSpecConfig (got " + config.getClass().getName() + ")");
		this.config = (BaseSpecConfig) config;
	}

	@Override
	public void close(Logger log) throws ProcessingException {
		// no-op
	}

	@Override
	public void validate(Logger log, Api api) throws ProcessingException, ParseWarningException {

		if (api == null)
			throw new ProcessingException("Api is null");
		if (api.getOperations() == null || api.getOperations().isEmpty()) {
			throw new ProcessingException("Paths and Operations undefined");
		}

		boolean validateBodyParameterElement = this.config != null && this.config.isValidateBodyParameterElement();

		for (int i = 0; i < api.getOperations().size(); i++) {

			ApiOperation op = api.getOperations().get(i);

			String prefix = "Operation[" + i + "] ";

			if (op.getHttpMethod() == null) {
				throw new ProcessingException("HttpMethod is null");
			}
			if (op.getPath() == null) {
				throw new ProcessingException("Path is null");
			}
			prefix = "Operation[" + op.getHttpMethod().name() + " " + op.getPath() + "] ";

			if (op.getRequest() != null) {
				String pRequest = prefix + "[request] ";

				for (int j = 0; j < op.getRequest().sizeBodyParameters(); j++) {
					ApiBodyParameter bodyParm = op.getRequest().getBodyParameter(j);
					if (bodyParm.getMediaType() == null) {
						throw new ProcessingException(pRequest + "MediaType undefined in body parameter (position '" + j + "')");
					}
					if (validateBodyParameterElement && bodyParm.getElement() == null) {
						throw new ProcessingException(pRequest + "Element undefined in body parameter '" + bodyParm.getMediaType() + "'");
					}
				}

				for (int j = 0; j < op.getRequest().sizeCookieParameters(); j++) {
					ApiCookieParameter par = op.getRequest().getCookieParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pRequest + "Name undefined in cookie parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pRequest + "Type undefined in cookie parameter '" + par.getName() + "'");
					}
				}

				for (int j = 0; j < op.getRequest().sizeDynamicPathParameters(); j++) {
					ApiRequestDynamicPathParameter par = op.getRequest().getDynamicPathParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pRequest + "Name undefined in dynamic path parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pRequest + "Type undefined in dynamic path parameter '" + par.getName() + "'");
					}
				}

				for (int j = 0; j < op.getRequest().sizeFormParameters(); j++) {
					ApiRequestFormParameter par = op.getRequest().getFormParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pRequest + "Name undefined in form parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pRequest + "Type undefined in form parameter '" + par.getName() + "'");
					}
				}

				for (int j = 0; j < op.getRequest().sizeHeaderParameters(); j++) {
					ApiHeaderParameter par = op.getRequest().getHeaderParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pRequest + "Name undefined in header parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pRequest + "Type undefined in header parameter '" + par.getName() + "'");
					}
				}

				for (int j = 0; j < op.getRequest().sizeQueryParameters(); j++) {
					ApiRequestQueryParameter par = op.getRequest().getQueryParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pRequest + "Name undefined in query parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pRequest + "Type undefined in query parameter '" + par.getName() + "'");
					}
				}
			}

			boolean defaultResponse = false;

			for (int k = 0; k < op.sizeResponses(); k++) {

				String pResponse = prefix + "[response '" + k + "'] ";

				ApiResponse response = op.getResponse(k);

				if (response.isDefaultHttpReturnCode()) {
					if (defaultResponse) {
						throw new ProcessingException(pResponse + "Http Return Code Default already defined");
					} else {
						defaultResponse = true;
					}
				}
				if (response.getHttpReturnCode() <= 0 && !response.isDefaultHttpReturnCode()) {
					throw new ProcessingException(pResponse + "Http Return Code undefined");
				}

				if (response.isDefaultHttpReturnCode()) {
					pResponse = prefix + "[response status 'default'] ";
				} else {
					pResponse = prefix + "[response status '" + response.getHttpReturnCode() + "'] ";
				}

				for (int j = 0; j < response.sizeBodyParameters(); j++) {
					ApiBodyParameter bodyParm = response.getBodyParameter(j);
					if (bodyParm.getMediaType() == null) {
						throw new ProcessingException(pResponse + "MediaType undefined in body parameter (position '" + j + "')");
					}
					if (validateBodyParameterElement && bodyParm.getElement() == null) {
						throw new ProcessingException(pResponse + "Element undefined in body parameter '" + bodyParm.getMediaType() + "'");
					}
				}

				for (int j = 0; j < response.sizeCookieParameters(); j++) {
					ApiCookieParameter par = response.getCookieParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pResponse + "Name undefined in cookie parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pResponse + "Type undefined in cookie parameter '" + par.getName() + "'");
					}
				}

				for (int j = 0; j < response.sizeHeaderParameters(); j++) {
					ApiHeaderParameter par = response.getHeaderParameter(j);
					if (par.getName() == null) {
						throw new ProcessingException(pResponse + "Name undefined in header parameter (position '" + j + "')");
					}
					if (par.getApiParameterSchema() == null || !par.getApiParameterSchema().isDefined()) {
						throw new ProcessingException(pResponse + "Type undefined in header parameter '" + par.getName() + "'");
					}
				}
			}
		}
	}

}
