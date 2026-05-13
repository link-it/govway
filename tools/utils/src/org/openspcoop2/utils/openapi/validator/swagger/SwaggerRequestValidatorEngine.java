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

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.openapi.OpenapiApiValidatorStructure;
import org.openspcoop2.utils.openapi.UniqueInterfaceGenerator;
import org.openspcoop2.utils.openapi.UniqueInterfaceGeneratorConfig;
import org.openspcoop2.utils.openapi.validator.OpenAPILibrary;
import org.openspcoop2.utils.openapi.validator.OpenapiLibraryValidatorConfig;
import org.openspcoop2.utils.openapi.validator.SwaggerRequestValidatorOpenAPI;
import org.openspcoop2.utils.rest.AbstractApiValidator;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.Cookie;
import org.openspcoop2.utils.rest.entity.HttpBaseEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseRequestEntity;
import org.openspcoop2.utils.rest.entity.HttpBaseResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.atlassian.oai.validator.model.ApiPath;
import com.atlassian.oai.validator.model.ApiPathImpl;
import com.atlassian.oai.validator.model.NormalisedPath;
import com.atlassian.oai.validator.model.NormalisedPathImpl;
import com.atlassian.oai.validator.model.Request.Method;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.SimpleValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;

/**
 * Swagger-request-validator (Atlassian) api validator.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SwaggerRequestValidatorEngine extends AbstractApiValidator implements IApiValidator {

	private static class SwaggerValidatorCache extends OpenapiApiValidatorStructure {
		private static final long serialVersionUID = 1L;
		private transient SwaggerRequestValidatorOpenAPI swaggerRequestValidatorOpenAPI = null;
	}

	private OpenAPI openApiSwagger;
	private SwaggerRequestValidator swaggerRequestValidator;
	private SwaggerResponseValidator swaggerResponseValidator;
	private ValidatorConfig config;
	private final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("SwaggerRequestValidator");

	@Override
	public void init(Logger log, Api api, ApiValidatorConfig rawConfig) throws ProcessingException {

		if (!(rawConfig instanceof ValidatorConfig))
			throw new ProcessingException("Config must be a swagger-request-validator config class");
		this.config = (ValidatorConfig) rawConfig;

		if (api == null)
			throw new ProcessingException("Api is null");
		if (!(api instanceof OpenapiApi))
			throw new ProcessingException("Swagger-request-validator supports only OpenapiApi class");
		if (OpenapiApi.isOpenApi31(api))
			throw new ProcessingException("La libreria 'swagger_request_validator' non supporta OpenAPI 3.1: " +
					"configurare una libreria compatibile (es. 'kappa') per le specifiche 3.1");
		this.api = api;
		this.log = log;

		OpenapiApi openapiApi = (OpenapiApi) api;

		if (ApiFormats.SWAGGER_2.equals(openapiApi.getFormat()) && this.config.isMergeAPISpec()) {
			// la funzionalità di merge non è supportata per Swagger 2: forzo a false
			this.config.setMergeAPISpec(false);
		}

		SemaphoreLock lock = this.semaphore.acquireThrowRuntime("init");
		try {

			OpenapiLibraryValidatorConfig libConfig = toLibraryConfig();

			JsonNode schemaNodeRoot = null;
			Map<java.net.URL, JsonNode> schemaMap = null;
			String root = "file:/";
			boolean validateSchema = true;

			SwaggerValidatorCache validationStructure;
			SwaggerRequestValidatorOpenAPI swaggerRequestValidatorOpenAPI = null;

			if (openapiApi.getValidationStructure() != null
					&& openapiApi.getValidationStructure().getNodeValidatorePrincipale() != null
					&& !openapiApi.getValidationStructure().getNodeValidatorePrincipale().isEmpty()
					&& openapiApi.getValidationStructure() instanceof SwaggerValidatorCache validationCache) {

				validateSchema = false;
				swaggerRequestValidatorOpenAPI = validationCache.swaggerRequestValidatorOpenAPI;

				for (String nome : validationCache.getNodeValidatorePrincipale().keySet()) {
					if (root.equals(nome)) {
						schemaNodeRoot = validationCache.getNodeValidatorePrincipale().get(nome);
					} else {
						if (schemaMap == null) {
							schemaMap = new HashMap<>();
						}
						schemaMap.put(new URI(nome).toURL(),
								validationCache.getNodeValidatorePrincipale().get(nome));
					}
				}

				validationStructure = validationCache;

			} else {

				YAMLUtils yamlUtils = YAMLUtils.getInstance();
				JSONUtils jsonUtils = JSONUtils.getInstance();

				String apiRaw = openapiApi.getApiRaw();
				boolean apiRawIsYaml = yamlUtils.isYaml(apiRaw);
				boolean readApiSchemas = true;
				if (this.config.isMergeAPISpec()) {

					readApiSchemas = false;

					Map<String, String> attachments = new HashMap<>();
					if (api.getSchemas() != null && !api.getSchemas().isEmpty()) {

						for (ApiSchema apiSchema : api.getSchemas()) {
							if (!ApiSchemaType.JSON.equals(apiSchema.getType())
									&& !ApiSchemaType.YAML.equals(apiSchema.getType())) {
								continue;
							}
							byte[] schema = apiSchema.getContent();
							if (ApiSchemaType.JSON.equals(apiSchema.getType())) {
								if (jsonUtils.isJson(schema)) {
									attachments.put(apiSchema.getName(), new String(apiSchema.getContent()));
								}
							} else {
								if (yamlUtils.isYaml(schema)) {
									attachments.put(apiSchema.getName(), new String(apiSchema.getContent()));
								}
							}
						}
					}

					if (!attachments.isEmpty()) {
						UniqueInterfaceGeneratorConfig configUniqueInterfaceGeneratorConfig = new UniqueInterfaceGeneratorConfig();
						configUniqueInterfaceGeneratorConfig.setFormat(ApiFormats.OPEN_API_3);
						configUniqueInterfaceGeneratorConfig.setYaml(apiRawIsYaml);
						configUniqueInterfaceGeneratorConfig.setMaster(apiRaw);
						configUniqueInterfaceGeneratorConfig.setAttachments(attachments);
						try {
							String apiMerged = UniqueInterfaceGenerator.generate(configUniqueInterfaceGeneratorConfig,
									null, null, true, log);
							if (apiMerged == null) {
								throw new Exception("empty ApiSpec");
							}
							apiRaw = apiMerged;
						} catch (Throwable t) {
							log.error("Merge API Spec failed: " + t.getMessage(), t);
							readApiSchemas = true;
						}
					}
				}

				if (apiRawIsYaml) {
					if (YAMLUtils.containsKeyAnchor(apiRaw)) {
						String jsonRepresentation = YAMLUtils.resolveKeyAnchorAndConvertToJson(apiRaw);
						schemaNodeRoot = jsonUtils.getAsNode(jsonRepresentation);
					} else {
						schemaNodeRoot = yamlUtils.getAsNode(apiRaw);
					}
				} else {
					schemaNodeRoot = jsonUtils.getAsNode(apiRaw);
				}
				normalizeRefs(schemaNodeRoot);

				if (readApiSchemas && api.getSchemas() != null && !api.getSchemas().isEmpty()) {

					for (ApiSchema apiSchema : api.getSchemas()) {
						if (!ApiSchemaType.JSON.equals(apiSchema.getType())
								&& !ApiSchemaType.YAML.equals(apiSchema.getType())) {
							continue;
						}
						byte[] schema = apiSchema.getContent();
						JsonNode schemaNodeInternal = null;
						if (ApiSchemaType.JSON.equals(apiSchema.getType())) {
							if (jsonUtils.isJson(schema)) {
								schemaNodeInternal = jsonUtils.getAsNode(schema);
							}
						} else {
							if (yamlUtils.isYaml(schema)) {
								String sSchema = new String(schema);
								if (YAMLUtils.containsKeyAnchor(sSchema)) {
									String jsonRepresentation = YAMLUtils.resolveKeyAnchorAndConvertToJson(sSchema);
									schemaNodeInternal = jsonUtils.getAsNode(jsonRepresentation);
								} else {
									schemaNodeInternal = yamlUtils.getAsNode(schema);
								}
							}
						}
						if (schemaNodeInternal == null) {
							continue;
						}
						normalizeRefs(schemaNodeInternal);
						if (schemaMap == null) {
							schemaMap = new HashMap<>();
						}
						schemaMap.put(new URI(root + apiSchema.getName()).toURL(), schemaNodeInternal);
					}
				}

				validationStructure = new SwaggerValidatorCache();
				Map<String, JsonNode> nodeValidatorePrincipale = new HashMap<>();
				nodeValidatorePrincipale.put(root, schemaNodeRoot);
				if (schemaMap != null && !schemaMap.isEmpty()) {
					for (Map.Entry<java.net.URL, JsonNode> entry : schemaMap.entrySet()) {
						nodeValidatorePrincipale.put(entry.getKey().toString(), entry.getValue());
					}
				}
				validationStructure.setNodeValidatorePrincipale(nodeValidatorePrincipale);
				openapiApi.setValidationStructure(validationStructure);
			}

			if (validateSchema && this.config.isValidateAPISpec()) {
				var validationResult = new SwaggerOpenApiValidator().validate(schemaNodeRoot);
				if (validationResult.isPresent()) {
					throw new ProcessingException("OpenAPI3 not valid: " + validationResult.get());
				}
			}

			if (swaggerRequestValidatorOpenAPI != null) {
				this.openApiSwagger = swaggerRequestValidatorOpenAPI.getOpenApiSwagger();
			} else {
				SwaggerRequestValidatorOpenAPI newInstance = new SwaggerRequestValidatorOpenAPI(schemaNodeRoot,
						libConfig, api);
				this.openApiSwagger = newInstance.getOpenApiSwagger();
				validationStructure.swaggerRequestValidatorOpenAPI = newInstance;
			}

			this.swaggerRequestValidator = new SwaggerRequestValidator(this.openApiSwagger, libConfig);
			this.swaggerResponseValidator = new SwaggerResponseValidator(this.openApiSwagger, libConfig);

		} catch (Throwable e) {
			try {
				this.close(log, api, this.config);
			} catch (Throwable t) {
				// ignore
			}
			throw new ProcessingException(e);
		} finally {
			this.semaphore.release(lock, "init");
		}
	}

	/**
	 * Adatta la ValidatorConfig flat di questo package ad un OpenapiLibraryValidatorConfig,
	 * richiesto dai wrapper (SwaggerRequestValidatorOpenAPI, SwaggerRequestValidator,
	 * SwaggerResponseValidator) che sono condivisi con la Validator legacy.
	 */
	private OpenapiLibraryValidatorConfig toLibraryConfig() {
		OpenapiLibraryValidatorConfig lvc = new OpenapiLibraryValidatorConfig();
		lvc.setOpenApiLibrary(OpenAPILibrary.swagger_request_validator);
		lvc.setMergeAPISpec(this.config.isMergeAPISpec());
		lvc.setValidateAPISpec(this.config.isValidateAPISpec());
		lvc.setValidateRequestPath(this.config.isValidateRequestPath());
		lvc.setValidateRequestQuery(this.config.isValidateRequestQuery());
		lvc.setValidateRequestUnexpectedQueryParam(this.config.isValidateRequestUnexpectedQueryParam());
		lvc.setValidateRequestHeaders(this.config.isValidateRequestHeaders());
		lvc.setValidateRequestCookie(this.config.isValidateRequestCookie());
		lvc.setValidateRequestBody(this.config.isValidateRequestBody());
		lvc.setValidateResponseHeaders(this.config.isValidateResponseHeaders());
		lvc.setValidateResponseBody(this.config.isValidateResponseBody());
		lvc.setValidateWildcardSubtypeAsJson(this.config.isValidateWildcardSubtypeAsJson());
		lvc.setSwaggerRequestValidator_InjectingAdditionalPropertiesFalse(
				this.config.isSwaggerRequestValidatorInjectingAdditionalPropertiesFalse());
		lvc.setSwaggerRequestValidator_ResolveFullyApiSpec(
				this.config.isSwaggerRequestValidatorResolveFullyApiSpec());
		return lvc;
	}

	@Override
	public void close(Logger log, Api api, ApiValidatorConfig config) throws ProcessingException {
		// no-op
	}

	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity, ApiOperation operation, Object... args)
			throws ProcessingException, ValidatorException {

		List<ApiBodyParameter> bodyParameters = this.getBodyParameters(httpEntity, operation);

		validateWithSwaggerRequestValidator(httpEntity, operation);

		boolean required = false;
		if (bodyParameters != null && !bodyParameters.isEmpty()) {
			for (ApiBodyParameter body : bodyParameters) {
				if (body.isRequired())
					required = true;
			}
		}
		if (required && httpEntity.getContent() == null) {
			throw new ValidatorException("Required body undefined");
		}
	}

	private void validateWithSwaggerRequestValidator(HttpBaseEntity<?> httpEntity, ApiOperation gwOperation)
			throws ProcessingException, ValidatorException {

		Optional<Entry<String, PathItem>> item = this.openApiSwagger.getPaths().entrySet().stream()
				.filter(pathItem -> ApiOperation.normalizePath(pathItem.getKey()).equals(gwOperation.getPath()))
				.findFirst();

		if (item.isEmpty()) {
			throw new ProcessingException(
					"Resource " + gwOperation.getHttpMethod() + " " + gwOperation.getPath() + " not found in OpenAPI 3");
		}

		HttpMethod method = HttpMethod.valueOf(gwOperation.getHttpMethod().toString());

		io.swagger.v3.oas.models.Operation swaggerOperation = item.get().getValue().readOperationsMap().get(method);

		ApiPath apiPath = new ApiPathImpl(httpEntity.getUrl(), null);
		NormalisedPath requestPath = new NormalisedPathImpl(httpEntity.getUrl(), null);

		com.atlassian.oai.validator.model.ApiOperation swaggerValidatorOperation =
				new com.atlassian.oai.validator.model.ApiOperation(apiPath, requestPath, method, swaggerOperation);

		ValidationReport report;

		if (httpEntity instanceof HttpBaseRequestEntity<?> request) {
			var swaggerRequest = buildSwaggerRequest(request);
			report = this.swaggerRequestValidator.validateRequest(swaggerRequest, swaggerValidatorOperation);
		} else if (httpEntity instanceof HttpBaseResponseEntity<?> response) {
			var swaggerResponse = buildSwaggerResponse(response);
			report = this.swaggerResponseValidator.validateResponse(swaggerResponse, swaggerValidatorOperation);
		} else {
			throw new ProcessingException("Unknown type for HttpBaseEntity: " + httpEntity.getClass().toString());
		}

		if (report.hasErrors()) {
			String msgReport = SimpleValidationReportFormat.getInstance().apply(report);
			throw new ValidatorException(msgReport);
		}
	}

	private static Method fromHttpMethod(HttpRequestMethod method) {
		return Method.valueOf(method.toString());
	}

	private static com.atlassian.oai.validator.model.Response buildSwaggerResponse(HttpBaseResponseEntity<?> response)
			throws ProcessingException {

		final SimpleResponse.Builder builder = new SimpleResponse.Builder(response.getStatus());

		if (response.getHeaders() != null && !response.getHeaders().isEmpty()) {
			response.getHeaders().forEach(builder::withHeader);
		}

		Object content = response.getContent();
		if (content instanceof String s) {
			builder.withBody(s);
		} else if (content instanceof byte[] b) {
			builder.withBody(b);
		} else if (content instanceof InputStream is) {
			builder.withBody(is);
		} else if (content instanceof Document d) {
			try {
				builder.withBody(XMLUtils.getInstance().toByteArray(d));
			} catch (Exception e) {
				throw new ProcessingException(e.getMessage(), e);
			}
		} else if (content instanceof Element e) {
			try {
				builder.withBody(XMLUtils.getInstance().toByteArray(e));
			} catch (Exception ex) {
				throw new ProcessingException(ex.getMessage(), ex);
			}
		} else if (content == null) {
			// nop
		} else {
			throw new ProcessingException("Type '" + content.getClass().getName() + "' unsupported");
		}

		return builder.build();
	}

	private static com.atlassian.oai.validator.model.Request buildSwaggerRequest(HttpBaseRequestEntity<?> request)
			throws ProcessingException {
		Object content = request.getContent();
		final SimpleRequest.Builder builder = new SimpleRequest.Builder(fromHttpMethod(request.getMethod()),
				request.getUrl());

		Map<String, List<String>> hdr = request.getHeaders();
		if (hdr != null && !hdr.isEmpty()) {
			List<String> originalValues = TransportUtils.getValues(hdr, HttpConstants.ACCEPT);
			TransportUtils.removeRawObject(hdr, HttpConstants.ACCEPT);

			if (!hdr.isEmpty()) {
				hdr.forEach(builder::withHeader);
			}

			if (originalValues != null) {
				hdr.put(HttpConstants.ACCEPT, originalValues);
			}
		}

		if (request.getParameters() != null && !request.getParameters().isEmpty()) {
			request.getParameters().forEach(builder::withQueryParam);
		}
		if (request.getCookies() != null && !request.getCookies().isEmpty()) {
			Cookie cookie = request.getCookies().get(0);
			StringBuilder cookiesValue = new StringBuilder(cookie.getName() + HttpConstants.COOKIE_NAME_VALUE_SEPARATOR + cookie.getValue());

			for (int i = 1; i < request.getCookies().size(); i++) {
				cookie = request.getCookies().get(i);
				cookiesValue.append(cookiesValue + HttpConstants.COOKIE_SEPARATOR + " " + cookie.getName()
						+ HttpConstants.COOKIE_NAME_VALUE_SEPARATOR + cookie.getValue());
			}
			builder.withHeader(HttpConstants.COOKIE, cookiesValue.toString());
		}

		if (content instanceof String s) {
			builder.withBody(s);
		} else if (content instanceof byte[] b) {
			builder.withBody(b);
		} else if (content instanceof InputStream is) {
			builder.withBody(is);
		} else if (content != null) {
			throw new ProcessingException("Type '" + content.getClass().getName() + "' unsupported");
		}

		return builder.build();
	}
}
