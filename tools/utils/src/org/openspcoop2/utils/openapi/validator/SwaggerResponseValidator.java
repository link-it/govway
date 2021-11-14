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

package org.openspcoop2.utils.openapi.validator;

import static com.atlassian.oai.validator.util.ContentTypeUtils.findMostSpecificMatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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
import io.swagger.v3.oas.models.media.BinarySchema;
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
	
	public SwaggerResponseValidator(OpenAPI openApi, OpenapiApi4jValidatorConfig config) {
		
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
	                            "Required Content-Type is missing" 	// TODO: Migliora messaggio, dire se siamo in respons eo in request
	                    ));
			}
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
		
		final MediaType schemaMediaType = responseSchema.getContent().get(mostSpecificMatch.get());
		final Body responseBody = response.getResponseBody().orElse(null);
		
		if (responseBody != null && schemaMediaType.getSchema() instanceof BinarySchema) {
			BinarySchema schema = (BinarySchema) schemaMediaType.getSchema();
			ValidationReport report = ValidationReport.empty();
			
			if ("string".equals(schema.getType()) && "binary".equals(schema.getFormat())) {
				if (ContentTypeUtils.isJsonContentType(response)) {
					report = report.merge(validateJsonFormat(responseBody,this.normalValidatorMessages));															
				}
			}
			else if ("string".equals(schema.getType()) && "base64".equals(schema.getFormat())) {
				report = report.merge(validateBase64Body(responseBody,this.normalValidatorMessages));
			}
			
			return report.merge(this.fileValidator.validateResponse(response, apiOperation));
			
		} else {
			// Se il content-type è un json o il subtype è *, lo schema del content deve essere validato.
			// Se però è un json già lo farà il validatore di atlassian, per cui controlliamo solo il caso del subtype
			
            com.google.common.net.MediaType responseMediaType = com.google.common.net.MediaType.parse(mostSpecificMatch.get());
            if (responseMediaType.subtype().equals("*")) {
            	return this.normalSchemaValidator
	                    .validate(() -> response.getResponseBody().get().toJsonNode(),schemaMediaType.getSchema(), "response.body");
            }	
		}

		return this.normalValidator.validateResponse(response, apiOperation);
	}
	
	public static LevelResolver.Builder getLevelResolverBuilder(OpenapiApi4jValidatorConfig config) {
		var errorLevelResolver = LevelResolver.create();
		
		// Il LevelResolver serve a gestire il livello di serietà dei messaggi						
		// Di default il LevelResolver porta segnala ogni errore di validazione come 
		// un ERROR, quindi dobbiamo disattivarli selettivamente.
		// Le chiavi da usare per il LevelResolver sono nel progetto swagger-validator 
		// sotto src/main/resources/messages.properties
							
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
			var error = SwaggerRequestValidator.validateBase64String(body.toString(null));
			if (error.isPresent()) {
				return ValidationReport.singleton(
						messages.create("validation.response.body.schema", error.get()));
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
