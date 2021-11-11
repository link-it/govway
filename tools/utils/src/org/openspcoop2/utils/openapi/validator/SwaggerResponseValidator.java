package org.openspcoop2.utils.openapi.validator;

import static com.atlassian.oai.validator.util.ContentTypeUtils.findMostSpecificMatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.atlassian.oai.validator.interaction.response.ResponseValidator;
import com.atlassian.oai.validator.model.ApiOperation;
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
		
		
		// E' solo la validazione sul body che non va fatta in caso di schema che descrive un file.
		// Tutta la validazione sulla risposta, eccome se va fatta.
		//repo.getMessages().

		final ApiResponse apiResponse = getApiResponse(response, apiOperation);
		if (apiResponse.getContent() == null) {
			return this.normalValidator.validateResponse(response, apiOperation);
		}
		
		//	VALIDAZIONE CUSTOM 1:
		//	Se la risposta ha un body ma non content-type, solleva errore.
		
		//  Anche se la risposta ha un body definito ma non un content-type deve sollevare errore
		
		Content contentSchema = apiResponse.getContent();
		if (contentSchema != null && !contentSchema.isEmpty()) {
			if ( /*response.getResponseBody().isPresent() &&*/ response.getContentType().isEmpty() /*&& response.getContentType().get().isEmpty()*/) {
				return ValidationReport.singleton(
	                    this.normalValidatorMessages.create(
	                            "validation.response.body.schema.invalidJson",
	                            "sese manca il content-type!"
	                            
	                    ));
			}
		}
		
		final Optional<String> mostSpecificMatch = findMostSpecificMatch(response, apiResponse.getContent().keySet());
		
		if (!mostSpecificMatch.isPresent()) {
			// Se non matcho content-type, lascio fare al normal validator
			return this.normalValidator.validateResponse(response, apiOperation);
		}

		final MediaType type = apiResponse.getContent().get(mostSpecificMatch.get());
		
		if (type.getSchema() == null) {
			return this.normalValidator.validateResponse(response, apiOperation);
		}
		
		// Se il content type è json, voglio comunque validarlo e verificare che sia un json corretto
		if (type.getSchema() instanceof BinarySchema) {
			BinarySchema schema = (BinarySchema) type.getSchema();

			if ("string".equals(schema.getType()) && "binary".equals(schema.getFormat())) {
				if (ContentTypeUtils.isJsonContentType(response)) {
					try {
						response.getResponseBody().get().toJsonNode();
					} catch (final IOException e) {
		                return ValidationReport.singleton(
		                        this.normalValidatorMessages.create(
		                                "validation.response.body.schema.invalidJson",
		                                this.normalValidatorMessages.get(SchemaValidator.INVALID_JSON_KEY, e.getMessage()).getMessage()
		                        )
		                );
		            }
					
				}
				return this.fileValidator.validateResponse(response, apiOperation);
			}
			else if ("string".equals(schema.getType()) && "base64".equals(schema.getFormat())) {
				/*try {
				//	return validateBase64String(request.getRequestBody().get().toString(null));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}*/
				// TODO: Valida base64
				return this.fileValidator.validateResponse(response, apiOperation);
			}						
		} else {
			
			if (!ContentTypeUtils.isJsonContentType(response)) {
				// Se lo schema non è un Binary Schema, voglio validarlo se il content-type non è a json.
				// E' comunque uno schema da rispettare.
				// Questo perchè il bodyValidator valida solo se il content-type è json.
				// TODO:Migliorare questa parte.
				return this.normalSchemaValidator
	                    .validate(() -> response.getResponseBody().get().toJsonNode(),
	                            type.getSchema(), "response.body");
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

	private ApiResponse getApiResponse(final Response response, final ApiOperation apiOperation) {
		final ApiResponse apiResponse = apiOperation.getOperation().getResponses()
				.get(Integer.toString(response.getStatus()));
		if (apiResponse == null) {
			return apiOperation.getOperation().getResponses().get("default"); // try the default response
		}
		return apiResponse;
	}

}
