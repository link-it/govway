/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import static com.atlassian.oai.validator.util.ContentTypeUtils.findMostSpecificMatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openspcoop2.utils.openapi.validator.OpenapiApi4jValidatorConfig;

import com.atlassian.oai.validator.interaction.response.ResponseValidator;
import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.Body;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.MessageResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Level;
import com.atlassian.oai.validator.schema.SchemaValidator;
import com.atlassian.oai.validator.schema.transform.AdditionalPropertiesInjectionTransformer;
import com.atlassian.oai.validator.util.ContentTypeUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

public class SwaggerResponseValidator {
	
	private final MessageResolver normalValidatorMessages;
	private final SchemaValidator normalSchemaValidator;
	private final ResponseValidator normalValidator;
	
	private final MessageResolver fileValidatorMessages;
	private final SchemaValidator fileSchemaValidator;
	private final ResponseValidator fileValidator;
	
	private final boolean validateWildcardSubtypeAsJson; 
	
	public SwaggerResponseValidator(OpenAPI openApi, OpenapiApi4jValidatorConfig config) {
		
		this.validateWildcardSubtypeAsJson = config.isValidateWildcardSubtypeAsJson();
		
		var errorLevelResolverBuilder = getLevelResolverBuilder(config);
		this.normalValidatorMessages = new MessageResolver(errorLevelResolverBuilder.build());
		this.normalSchemaValidator = new SchemaValidator(openApi, this.normalValidatorMessages);
		
		if (!config.isInjectingAdditionalProperties()) {
			var transformers = this.normalSchemaValidator.transformers;
			this.normalSchemaValidator.transformers = transformers.stream()
					.filter( t -> t != AdditionalPropertiesInjectionTransformer.getInstance())
					.collect(Collectors.toList());
		}
		
		this.normalValidator = new ResponseValidator(this.normalSchemaValidator, this.normalValidatorMessages, openApi, Arrays.asList());
		
		errorLevelResolverBuilder.withLevel("validation.response.body", Level.IGNORE);
		errorLevelResolverBuilder.withLevel("validation.response.body.missing", Level.ERROR);

		this.fileValidatorMessages = new MessageResolver(errorLevelResolverBuilder.build());
		this.fileSchemaValidator = new SchemaValidator(openApi, this.fileValidatorMessages);
		
		if (!config.isInjectingAdditionalProperties()) {
			var transformers = this.fileSchemaValidator.transformers;
			this.fileSchemaValidator.transformers = transformers.stream()
					.filter( t -> t != AdditionalPropertiesInjectionTransformer.getInstance())
					.collect(Collectors.toList());
		}
		
		this.fileValidator = new ResponseValidator(this.fileSchemaValidator, this.fileValidatorMessages, openApi, Arrays.asList());
	}

	
	public ValidationReport validateResponse(Response response, ApiOperation apiOperation) {
		
		final ApiResponse responseSchema = getApiResponse(response, apiOperation);
		if (responseSchema.getContent() == null) {
			return this.normalValidator.validateResponse(response, apiOperation);
		}
		
		//	VALIDAZIONE CUSTOM 1:
		//	Controllo che il content-type nullo non sia ammesso quando è richiesto un content
		//	Le risposte vuote sono quelle senza content, se il content c'è, ci deve essere
		//	anche il mediaType
		
		Content contentSchema = responseSchema.getContent();
		if (contentSchema != null && !contentSchema.isEmpty()) {
			if (response.getContentType().isEmpty()) {
				return ValidationReport.singleton(
	                    this.normalValidatorMessages.create(
	                    		"validation.response.contentType.notAllowed",
	                            "[RESPONSE] Required Content-Type is missing"
	                    ));
			}
		}
		
		if (response.getResponseBody().isPresent() && response.getContentType().isEmpty()) {
			return ValidationReport.singleton(
                    this.normalValidatorMessages.create(
                            "validation.response.contentType.notAllowed",
                            "[RESPONSE] Empty Content-Type not allowed if body is present"
                    ));			
		}
		
		// VALIDAZIONE CUSTOM 2
		// Se lo schema del request body è: type: string, format: binary, ovvero un BinarySchema,
		// allora al più valida che il body sia un json e valida tutto il resto della richiesta
		// Se invece il format è base64 controlla che sia in base64
		
		final Optional<String> mostSpecificMatch = findMostSpecificMatch(response, responseSchema.getContent().keySet());		
		if (!mostSpecificMatch.isPresent()) {
			// Se non matcho il content-type, lascio fare al normal validator, 
			return this.normalValidator.validateResponse(response, apiOperation);
		}
		
		final MediaType mediaType = responseSchema.getContent().get(mostSpecificMatch.get());
        com.google.common.net.MediaType responseMediaType = com.google.common.net.MediaType.parse(mostSpecificMatch.get());
		final Body responseBody = response.getResponseBody().orElse(null);
		ValidationReport report = ValidationReport.empty();
		
		// Validazione schema binario	
		if (SwaggerValidatorUtils.isBinarySchemaFile(mediaType.getSchema()) && responseBody != null) {
			if (ContentTypeUtils.isJsonContentType(response)) {
				report = report.merge(validateJsonFormat(responseBody,this.normalValidatorMessages))
							.merge(this.fileValidator.validateResponse(response, apiOperation));
			}		
			
		} else if (SwaggerValidatorUtils.isBase64SchemaFile(mediaType.getSchema()) && responseBody != null) {
			report = report.merge(validateBase64Body(responseBody,this.normalValidatorMessages))
					.merge(this.fileValidator.validateResponse(response, apiOperation));
			
		} else if (this.validateWildcardSubtypeAsJson && responseMediaType.subtype().equals("*")) {
        	report = this.normalSchemaValidator
                    .validate( () -> response.getResponseBody().get().toJsonNode(), mediaType.getSchema(), "response.body")
                    .merge(this.normalValidator.validateResponse(response, apiOperation));
        	
		} else {
			report = this.normalValidator.validateResponse(response, apiOperation);
		}
		
		return report;
	
	}
	
	public static LevelResolver.Builder getLevelResolverBuilder(OpenapiApi4jValidatorConfig config) {
		var errorLevelResolver = LevelResolver.create();
		
		// Il LevelResolver serve a gestire il livello di serietà dei messaggi						
		// Di default il LevelResolver porta segnala ogni errore di validazione come 
		// un ERROR, quindi dobbiamo disattivarli selettivamente.
		// Le chiavi da usare per il LevelResolver sono nel progetto swagger-validator 
		// sotto src/main/resources/messages.properties
		// TODO: Effettivamente potrei spezzare questa funzione e mettere le cose che riguardano la risposta
		// solo qui e quelle che riguardano la richiesta solo nello SwaggerRequestValidator
		if (!config.isValidateUnexpectedQueryParam()) {
			errorLevelResolver.withLevel("validation.request.parameter.query.unexpected", Level.IGNORE);
		}				
		
		// Config Request
		if (!config.isValidateRequestBody()) {
			errorLevelResolver.withLevel("validation.request.body", Level.IGNORE);
		}
		if (!config.isValidateRequestHeaders()) {
			errorLevelResolver.withLevel("validation.request.parameter.header", Level.IGNORE);
		}
		if (!config.isValidateRequestQuery()) {
			errorLevelResolver.withLevel("validation.request.parameter.query", Level.IGNORE);							
		}
		if (!config.isValidateRequestCookie()) {
			// TODO e qua?
		}
		
		// Config Response 
		if(!config.isValidateResponseHeaders()) {
			errorLevelResolver.withLevel("validation.response.parameter.header", Level.IGNORE);
		}
		if(!config.isValidateResponseBody()) {
			errorLevelResolver.withLevel("validation.response.body", Level.IGNORE);
		}
		
		return errorLevelResolver;
		
	}
	
	private static final ValidationReport validateJsonFormat(Body body, MessageResolver messages) {
		try {
			body.toJsonNode();
		} catch (final IOException e) {
            return ValidationReport.singleton(
            		messages.create(
                            "validation.request.body.schema.invalidJson",
                            messages.get(SchemaValidator.INVALID_JSON_KEY, e.getMessage()).getMessage()
                    )
            );
        }
		return ValidationReport.empty();		
	}
	
	private static final ValidationReport validateBase64Body(Body body, MessageResolver messages) {
		try {		
			var error = SwaggerValidatorUtils.validateBase64String(body.toString(StandardCharsets.UTF_8));
			if (error.isPresent()) {
				return ValidationReport.singleton(
						messages.create("validation.response.body.schema", "[RESPONSE] "+error.get()));
			} else {
				return ValidationReport.empty();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ApiResponse getApiResponse(final Response response, final ApiOperation apiOperation) {
		final ApiResponse apiResponse = apiOperation.getOperation().getResponses()
				.get(Integer.toString(response.getStatus()));
		if (apiResponse == null) {
			return apiOperation.getOperation().getResponses().get("default"); // try the default response
		}
		return apiResponse;
	}

}
