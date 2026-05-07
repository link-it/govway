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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.openapi.OpenapiApi;
import org.openspcoop2.utils.openapi.OpenapiApiValidatorStructure;
import org.openspcoop2.utils.openapi.UniqueInterfaceGenerator;
import org.openspcoop2.utils.openapi.UniqueInterfaceGeneratorConfig;
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
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.erosb.kappa.core.model.v3.OAI3Context;
import com.github.erosb.kappa.core.util.TreeUtil;
import com.github.erosb.kappa.core.validation.OpenApiValidationFailure;
import com.github.erosb.kappa.operation.validator.model.Request;
import com.github.erosb.kappa.operation.validator.model.Response;
import com.github.erosb.kappa.operation.validator.model.impl.Body;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultRequest;
import com.github.erosb.kappa.operation.validator.model.impl.DefaultResponse;
import com.github.erosb.kappa.operation.validator.validation.OperationValidator;
import com.github.erosb.kappa.parser.model.v3.OpenApi3;
import com.github.erosb.kappa.parser.model.v3.Operation;
import com.github.erosb.kappa.parser.model.v3.Path;
import com.github.erosb.kappa.schema.validator.ValidationData;

/**
 * Kappa (com.github.erosb) api validator — supporta OpenAPI 3.0 e 3.1.
 *
 * Specchio dell'engine openapi4j, basato sulla libreria kappa (fork mantenuto)
 * con le patch GovWay applicate.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KappaRequestValidator extends AbstractApiValidator implements IApiValidator {

	private static class KappaValidatorCache extends OpenapiApiValidatorStructure {
		private static final long serialVersionUID = 1L;
		private transient OpenApi3 openApi = null;
	}

	private OpenApi3 openApi;
	private ValidatorConfig config;
	private final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("KappaValidator");

	@Override
	public void init(Logger log, Api api, ApiValidatorConfig rawConfig) throws ProcessingException {

		if (!(rawConfig instanceof ValidatorConfig))
			throw new ProcessingException("Config must be a kappa config class");
		this.config = (ValidatorConfig) rawConfig;

		if (api == null)
			throw new ProcessingException("Api is null");
		if (!(api instanceof OpenapiApi))
			throw new ProcessingException("Kappa validator supports only OpenapiApi class");
		this.api = api;
		this.log = log;

		OpenapiApi openapiApi = (OpenapiApi) api;

		SemaphoreLock lock = this.semaphore.acquireThrowRuntime("init");
		try {

			JsonNode schemaNodeRoot = null;
			URL uriSchemaNodeRoot = null;
			Map<URL, JsonNode> schemaMap = null;
			String root = "file:/";

			KappaValidatorCache validationStructure;

			if (openapiApi.getValidationStructure() != null
					&& openapiApi.getValidationStructure().getNodeValidatorePrincipale() != null
					&& !openapiApi.getValidationStructure().getNodeValidatorePrincipale().isEmpty()
					&& openapiApi.getValidationStructure() instanceof KappaValidatorCache validationCache) {

				this.openApi = validationCache.openApi;
				if (this.openApi != null)
					return;

				for (String nome : validationCache.getNodeValidatorePrincipale().keySet()) {
					if (root.equals(nome)) {
						schemaNodeRoot = validationCache.getNodeValidatorePrincipale().get(nome);
						uriSchemaNodeRoot = new URI(root).toURL();
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
							readApiSchemas = true; // torno al metodo tradizionale
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
				uriSchemaNodeRoot = new URI(root).toURL();

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

				validationStructure = new KappaValidatorCache();
				Map<String, JsonNode> nodeValidatorePrincipale = new HashMap<>();
				nodeValidatorePrincipale.put(root, schemaNodeRoot);
				if (schemaMap != null && !schemaMap.isEmpty()) {
					for (Map.Entry<URL, JsonNode> entry : schemaMap.entrySet()) {
						nodeValidatorePrincipale.put(entry.getKey().toString(), entry.getValue());
					}
				}
				validationStructure.setNodeValidatorePrincipale(nodeValidatorePrincipale);
				openapiApi.setValidationStructure(validationStructure);
			}

			// Costruisco OpenAPI3 di kappa
			OAI3Context context = new OAI3Context(uriSchemaNodeRoot, schemaNodeRoot, schemaMap);
			context.setMultipartOptimization(this.config.isValidateMultipartOptimization());
			this.openApi = TreeUtil.json.convertValue(context.getBaseDocument(), OpenApi3.class);
			this.openApi.setContext(context);

			// La libreria kappa non espone un OpenApi3Validator standalone come openapi4j;
			// la validazione strutturale avviene durante il parsing/conversione del documento.

			validationStructure.openApi = this.openApi;

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

	@Override
	public void close(Logger log, Api api, ApiValidatorConfig config) throws ProcessingException {
		// no-op
	}

	@Override
	public void validatePreConformanceCheck(HttpBaseEntity<?> httpEntity, ApiOperation operation, Object... args)
			throws ProcessingException, ValidatorException {

		List<ApiBodyParameter> bodyParameters = this.getBodyParameters(httpEntity, operation);

		validateWithKappa(httpEntity, operation);

		// Controllo poi i campi required come controllo aggiuntivo
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

	private void validateWithKappa(HttpBaseEntity<?> httpEntity, ApiOperation apiOperation)
			throws ProcessingException, ValidatorException {

		Operation operation = null;
		Path path = null;
		boolean found = false;

		for (Map.Entry<String, Path> pathEntry : this.openApi.getPaths().entrySet()) {
			path = pathEntry.getValue();
			for (String method : pathEntry.getValue().getOperations().keySet()) {
				operation = pathEntry.getValue().getOperation(method);
				String normalizePath = ApiOperation.normalizePath(pathEntry.getKey());
				if (apiOperation.getHttpMethod().toString().equalsIgnoreCase(method)
						&& apiOperation.getPath().equals(normalizePath)) {
					found = true;
					break;
				}
			}
			if (found)
				break;
		}
		if (!found) {
			throw new ProcessingException("Resource " + apiOperation.getHttpMethod() + " " + apiOperation.getPath()
					+ " not found in OpenAPI 3");
		}

		try {

			ValidationData<Void> vData = new ValidationData<>();
			this.openApi.setServers(null); // se lascio i server, validatePath verifica anche la base url
			OperationValidator val = new OperationValidator(this.openApi, path, operation);

			if (httpEntity instanceof HttpBaseRequestEntity<?> httpRequest) {

				Request requestKappa = buildRequestKappa(httpRequest);

				if (this.config.isValidateRequestPath()) {
					val.validatePath(requestKappa, vData);
				}
				if (this.config.isValidateRequestQuery()) {
					val.validateQuery(requestKappa, vData);
				}
				if (this.config.isValidateRequestHeaders()) {
					val.validateHeaders(requestKappa, vData);
				}
				if (this.config.isValidateRequestCookie()) {
					val.validateCookies(requestKappa, vData);
				}
				if (this.config.isValidateRequestBody()) {
					val.validateBody(requestKappa, vData);
				}
			} else if (httpEntity instanceof HttpBaseResponseEntity<?> response) {

				Response responseKappa = buildResponseKappa(response.getStatus(), response.getHeaders(),
						response.getContent());
				if (this.config.isValidateResponseHeaders()) {
					val.validateHeaders(responseKappa, vData);
				}
				if (this.config.isValidateResponseBody()) {
					val.validateBody(responseKappa, vData);
				}
			}

			if (!vData.isValid()) {
				List<OpenApiValidationFailure> failures = vData.results();
				if (failures != null && !failures.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (OpenApiValidationFailure f : failures) {
						if (sb.length() > 0) {
							sb.append('\n');
						}
						sb.append(f.getMessage());
					}
					throw new ValidatorException(sb.toString());
				} else {
					throw new ValidatorException("Validation failed");
				}
			}

		} catch (ValidatorException e) {
			throw e;
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		}
	}

	private Request buildRequestKappa(HttpBaseRequestEntity<?> httpRequest) throws ProcessingException {

		try {

			final DefaultRequest.Builder builder = new DefaultRequest.Builder(httpRequest.getUrl(),
					Request.Method.getMethod(httpRequest.getMethod().name()));

			String queryString = null;
			if (httpRequest.getParameters() != null && !httpRequest.getParameters().isEmpty()) {
				StringBuilder sb = new StringBuilder();
				Iterator<String> keys = httpRequest.getParameters().keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					List<String> values = httpRequest.getParameters().get(key);
					try {
						key = TransportUtils.urlEncodeParam(key,
								org.openspcoop2.utils.resources.Charset.UTF_8.getValue());
					} catch (Exception e) {
						if (this.log != null) {
							this.log.error("URLEncode key[" + key + "] error: " + e.getMessage(), e);
						} else {
							e.printStackTrace(System.out);
						}
					}

					for (String value : values) {
						if (sb.length() > 0) {
							sb.append("&");
						}
						try {
							value = TransportUtils.urlEncodeParam(value,
									org.openspcoop2.utils.resources.Charset.UTF_8.getValue());
						} catch (Exception e) {
							if (this.log != null) {
								this.log.error("URLEncode value[" + value + "] error: " + e.getMessage(), e);
							} else {
								e.printStackTrace(System.out);
							}
						}
						sb.append(key);
						sb.append("=");
						sb.append(value);
					}
				}
				queryString = sb.toString();
			}

			if (HttpRequestMethod.GET.toString().equalsIgnoreCase(httpRequest.getMethod().name())) {
				builder.query(queryString);
			} else {
				if (queryString != null) {
					builder.query(queryString);
				}
				if (httpRequest.getContent() != null) {
					if (httpRequest.getContent() instanceof String str) {
						builder.body(Body.from(str));
					} else if (httpRequest.getContent() instanceof byte[] b) {
						try (ByteArrayInputStream bin = new ByteArrayInputStream(b)) {
							builder.body(Body.from(bin));
						}
					} else if (httpRequest.getContent() instanceof InputStream is) {
						builder.body(Body.from(is));
					} else {
						throw new ProcessingException(
								"Type '" + httpRequest.getContent().getClass().getName() + "' unsupported");
					}
				}
			}

			if (httpRequest.getCookies() != null) {
				for (Cookie cookie : httpRequest.getCookies()) {
					builder.cookie(cookie.getName(), cookie.getValue());
				}
			}

			if (httpRequest.getHeaders() != null) {
				for (Map.Entry<String, List<String>> e : httpRequest.getHeaders().entrySet()) {
					builder.header(e.getKey(), e.getValue());
				}
			}

			return builder.build();

		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		}
	}

	private Response buildResponseKappa(int status, Map<String, List<String>> headers, Object content)
			throws ProcessingException {

		try {

			final DefaultResponse.Builder builder = new DefaultResponse.Builder(status);

			if (content != null) {
				switch (content) {
				case String s -> builder.body(Body.from(s));
				case InputStream is -> builder.body(Body.from(is));
				case byte[] b -> {
					try (ByteArrayInputStream bin = new ByteArrayInputStream(b)) {
						builder.body(Body.from(bin));
					}
				}
				case Document d -> {
					byte[] b = XMLUtils.getInstance().toByteArray(d);
					try (ByteArrayInputStream bin = new ByteArrayInputStream(b)) {
						builder.body(Body.from(bin));
					}
				}
				case Element e -> {
					byte[] b = XMLUtils.getInstance().toByteArray(e);
					try (ByteArrayInputStream bin = new ByteArrayInputStream(b)) {
						builder.body(Body.from(bin));
					}
				}
				default -> throw new ProcessingException("Type '" + content.getClass().getName() + "' unsupported");
				}
			}

			if (headers != null) {
				for (Map.Entry<String, List<String>> e : headers.entrySet()) {
					builder.header(e.getKey(), e.getValue());
				}
			}

			return builder.build();

		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		}
	}
}
