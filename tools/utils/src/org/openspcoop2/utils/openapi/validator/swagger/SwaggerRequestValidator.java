/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.openspcoop2.utils.openapi.validator.OpenapiLibraryValidatorConfig;

import com.atlassian.oai.validator.interaction.request.RequestValidator;
import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.Body;
import com.atlassian.oai.validator.model.Request;
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
import io.swagger.v3.oas.models.parameters.RequestBody;

/**
 * SwaggerRequestValidator
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SwaggerRequestValidator {

	private final MessageResolver normalValidatorMessages;
	private final SchemaValidator normalSchemaValidator;
	private final RequestValidator normalValidator;
	
	private final MessageResolver fileValidatorMessages;
	private final SchemaValidator fileSchemaValidator;
	private final RequestValidator fileValidator;
	
	private final boolean validateWildcardSubtypeAsJson;
	
	public SwaggerRequestValidator(OpenAPI openApi, OpenapiLibraryValidatorConfig config) {
	
		this.validateWildcardSubtypeAsJson = config.isValidateWildcardSubtypeAsJson();
		
		var errorLevelResolverBuilder = SwaggerRequestValidator.getLevelResolverBuilder(config);
		this.normalValidatorMessages = new MessageResolver(errorLevelResolverBuilder.build());	
		this.normalSchemaValidator = new SchemaValidator(openApi, this.normalValidatorMessages);		
		if (!config.isSwaggerRequestValidator_InjectingAdditionalPropertiesFalse()) {
			var transformers = this.normalSchemaValidator.transformers;
			this.normalSchemaValidator.transformers = transformers.stream()
					.filter( t -> t != AdditionalPropertiesInjectionTransformer.getInstance())
					.collect(Collectors.toList());
		}
		
		this.normalValidator = new RequestValidator(this.normalSchemaValidator, this.normalValidatorMessages, openApi, Arrays.asList());
		
		// file validator, non entra nel merito del body.
		errorLevelResolverBuilder.withLevel("validation.request.body", Level.IGNORE);
		errorLevelResolverBuilder.withLevel("validation.request.body.missing", Level.ERROR);
		this.fileValidatorMessages = new MessageResolver(errorLevelResolverBuilder.build());
		this.fileSchemaValidator = new SchemaValidator(openApi, this.fileValidatorMessages);
		
		if (!config.isSwaggerRequestValidator_InjectingAdditionalPropertiesFalse()) {
			var transformers = this.fileSchemaValidator.transformers;
			this.fileSchemaValidator.transformers = transformers.stream()
					.filter( t -> t != AdditionalPropertiesInjectionTransformer.getInstance())
					.collect(Collectors.toList());
		}
		this.fileValidator = new RequestValidator(this.fileSchemaValidator, this.fileValidatorMessages, openApi, Arrays.asList());
	}

	public ValidationReport validateRequest(Request request, ApiOperation apiOperation) {

		var requestBodySchema = apiOperation.getOperation().getRequestBody();		
		if (requestBodySchema == null) {
			return this.normalValidator.validateRequest(request, apiOperation);
		}
		
		//	VALIDAZIONE CUSTOM 1:
		//  Controllo che il content-type nullo non sia ammesso quando è richiesto un content
		//	Gli altri casi vengono controllati da atlassian
		//	Questo completa la richiesta che il content-type passato deve stare fra quelli elencati.
		
		Content contentSchema = requestBodySchema.getContent();
		boolean isContentRequired = requestBodySchema.getRequired() == null ? false : requestBodySchema.getRequired();
		
		if (isContentRequired && contentSchema != null && !contentSchema.isEmpty()) {	
			if (request.getContentType().isEmpty()) {
				return ValidationReport.singleton(
	                    this.normalValidatorMessages.create(
	                            "validation.request.contentType.notAllowed",
	                            "[REQUEST] Required Content-Type is missing"
	                    ));
			}
		}
		
		if (request.getRequestBody().isPresent() && request.getContentType().isEmpty()) {
			return ValidationReport.singleton(
                    this.normalValidatorMessages.create(
                            "validation.request.contentType.notAllowed",
                            "[REQUEST] Empty Content-Type not allowed if body is present"
                    ));			
		}

		// VALIDAZIONE CUSTOM PER BODY 2
		// Se lo schema del request body è: type: string, format: binary, ovvero un BinarySchema,
		// allora al più valida che il body sia un json e valida tutto il resto della richiesta
		// Se invece il format è base64 controlla che sia in base64
		// Se il subtype è /* valida lo schema della richiesta
		
		var maybeMediaType = findApiMediaTypeForRequest(request, requestBodySchema);
		if (maybeMediaType.isEmpty()) {
			return this.normalValidator.validateRequest(request, apiOperation);
		}

		MediaType type = maybeMediaType.get().getRight();
        com.google.common.net.MediaType requestMediaType = com.google.common.net.MediaType.parse(maybeMediaType.get().getLeft());
		ValidationReport report = ValidationReport.empty();
		Body requestBody = request.getRequestBody().orElse(null);
		
		// Validazione schema string format binary	
		if (SwaggerValidatorUtils.isBinarySchemaFile(type.getSchema()) && requestBody != null) {
			if (ContentTypeUtils.isJsonContentType(request)) {
				report = report.merge(SwaggerValidatorUtils.validateJsonFormat(requestBody,this.normalValidatorMessages,true))
							.merge(this.fileValidator.validateRequest(request, apiOperation));
			}
		} 
		
		// validazione schema string format base64	
		else if (SwaggerValidatorUtils.isBase64SchemaFile(type.getSchema()) && requestBody != null) {
			report = report.merge(SwaggerValidatorUtils.validateBase64Body(requestBody,this.normalValidatorMessages,true))
					.merge(this.fileValidator.validateRequest(request, apiOperation));
			
		} 
		
		// validazione schema subtype *
		else if (this.validateWildcardSubtypeAsJson && requestMediaType.subtype().equals("*")) {
        	report = this.normalSchemaValidator
                    .validate( () -> request.getRequestBody().get().toJsonNode(), type.getSchema(), "request.body")
                    .merge(this.normalValidator.validateRequest(request, apiOperation));
		} 
		
		else {
			report = this.normalValidator.validateRequest(request, apiOperation);
		}
		
		return report;
	}
	
	private static LevelResolver.Builder getLevelResolverBuilder(OpenapiLibraryValidatorConfig config) {
		var errorLevelResolver = LevelResolver.create();
		
		// Il LevelResolver serve a gestire il livello di serietà dei messaggi						
		// Di default il LevelResolver porta segnala ogni errore di validazione come 
		// un ERROR, quindi dobbiamo disattivarli selettivamente.
		// Le chiavi da usare per il LevelResolver sono nel progetto swagger-validator 
		// sotto src/main/resources/messages.properties
		
		// Config Request
		if (!config.isValidateRequestPath()) {
			errorLevelResolver.withLevel("validation.request.path.missing", Level.IGNORE);							
		}
		if (!config.isValidateRequestQuery()) {
			errorLevelResolver.withLevel("validation.request.parameter.query", Level.IGNORE);							
		}
		if (!config.isValidateRequestUnexpectedQueryParam()) {
			errorLevelResolver.withLevel("validation.request.parameter.query.unexpected", Level.IGNORE);
		}
		if (!config.isValidateRequestHeaders()) {
			errorLevelResolver.withLevel("validation.request.parameter.header", Level.IGNORE);
		}
		if (!config.isValidateRequestCookie()) {
			errorLevelResolver.withLevel("validation.request.parameter.cookie", Level.IGNORE);				
		}
		if (!config.isValidateRequestBody()) {
			errorLevelResolver.withLevel("validation.request.body", Level.IGNORE);
		}

		return errorLevelResolver;
		
	}
	
	
	private Optional<Pair<String, MediaType>> findApiMediaTypeForRequest(Request request, RequestBody apiRequestBodyDefinition) {
		return Optional.ofNullable(apiRequestBodyDefinition).map(RequestBody::getContent)
				.flatMap(content -> findMostSpecificMatch(request, content.keySet())
						.map(mostSpecificMatch -> Pair.of(mostSpecificMatch, content.get(mostSpecificMatch))));
	}

}
