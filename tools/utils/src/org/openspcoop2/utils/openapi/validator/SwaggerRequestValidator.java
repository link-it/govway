package org.openspcoop2.utils.openapi.validator;

import static com.atlassian.oai.validator.util.ContentTypeUtils.findMostSpecificMatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.atlassian.oai.validator.interaction.request.RequestValidator;
import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.Body;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.MessageResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Level;
import com.atlassian.oai.validator.schema.SchemaValidator;
import com.atlassian.oai.validator.schema.transform.AdditionalPropertiesInjectionTransformer;
import com.atlassian.oai.validator.util.ContentTypeUtils;
import com.google.common.base.CharMatcher;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;

public class SwaggerRequestValidator {

	private final MessageResolver normalValidatorMessages;
	private final MessageResolver fileValidatorMessages;
	private final RequestValidator normalValidator;
	private final RequestValidator fileValidator;
	
	
	public SwaggerRequestValidator(OpenAPI openApi, OpenapiApi4jValidatorConfig config) {
	
		var errorLevelResolverBuilder = SwaggerResponseValidator.getLevelResolverBuilder(config);
		
		this.normalValidatorMessages = new MessageResolver(errorLevelResolverBuilder.build());
		
		SchemaValidator schemaValidator = new SchemaValidator(openApi, this.normalValidatorMessages);		
		if (!config.isInjectingAdditionalProperties()) {
			var transformers = schemaValidator.transformers;
			schemaValidator.transformers = transformers.stream()
					.filter( t -> t != AdditionalPropertiesInjectionTransformer.getInstance())
					.collect(Collectors.toList());
		}
		
		this.normalValidator = new RequestValidator(schemaValidator, this.normalValidatorMessages, openApi, Arrays.asList());
		
		errorLevelResolverBuilder.withLevel("validation.request.body", Level.IGNORE);
		errorLevelResolverBuilder.withLevel("validation.request.body.missing", Level.ERROR);

		this.fileValidatorMessages = new MessageResolver(errorLevelResolverBuilder.build());
		schemaValidator = new SchemaValidator(openApi, this.fileValidatorMessages);
		this.fileValidator = new RequestValidator(schemaValidator, this.fileValidatorMessages, openApi, Arrays.asList());
	}

	public ValidationReport validateRequest(Request request, ApiOperation apiOperation) {

		// Se lo schema del request body è: type: string, format: binary, ovvero un
		// BinarySchema,
		// allora disattiva la validazione del body e controlla solo che questo sia
		// presente se è required.
		// Se invece il format è base64 controlla che sia in base64
		
		var requestBody = apiOperation.getOperation().getRequestBody();
		if (requestBody == null) {
			return this.normalValidator.validateRequest(request, apiOperation);
		}		
		var maybeMediaType = findApiMediaTypeForRequest(request, requestBody);
		
		if (!maybeMediaType.isEmpty()) {
			MediaType type = maybeMediaType.get().getRight();
			ValidationReport report = ValidationReport.empty();

			if (type.getSchema() instanceof BinarySchema) {
				BinarySchema schema = (BinarySchema) type.getSchema();

				if ("string".equals(schema.getType()) && "binary".equals(schema.getFormat())) {					
					if (ContentTypeUtils.isJsonContentType(request)) {
						// Se il content type è json, voglio comunque validarlo e verificare che sia un json corretto
						report.merge(validateJsonFormat(request.getRequestBody().get()));						
					}						
				}				
				else if ("string".equals(schema.getType()) && "base64".equals(schema.getFormat())) {
					/*try {
					//	return validateBase64String(request.getRequestBody().get().toString(null));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}*/
					// TODO: Valida base64 e eventualmente json
				}
				return report.merge(this.fileValidator.validateRequest(request, apiOperation));
			}
			
		}
		
		return this.normalValidator.validateRequest(request, apiOperation);
	}
	
	
	private ValidationReport validateJsonFormat(Body body) {
		try {
			body.toJsonNode();
		} catch (final IOException e) {
            return ValidationReport.singleton(
                    this.normalValidatorMessages.create(
                            "validation.response.body.schema.invalidJson",
                            this.normalValidatorMessages.get(SchemaValidator.INVALID_JSON_KEY, e.getMessage()).getMessage()
                    )
            );
        }
		return ValidationReport.empty();		
	}

	private Optional<Pair<String, MediaType>> findApiMediaTypeForRequest(Request request, RequestBody apiRequestBodyDefinition) {
		return Optional.ofNullable(apiRequestBodyDefinition).map(RequestBody::getContent)
				.flatMap(content -> findMostSpecificMatch(request, content.keySet())
						.map(mostSpecificMatch -> Pair.of(mostSpecificMatch, content.get(mostSpecificMatch))));
	}
	
	private ValidationReport validateBase64String(String input) {
		
		/*
		 * Regex to accurately remove _at most two_ '=' characters from the end of the
		 * input.
		 */
		final Pattern PATTERN = Pattern.compile("==?$");

		/*
		 * Negation of the Base64 alphabet. We try and find one character, if any,
		 * matching this "negated" character matcher.
		 *
		 * FIXME: use .precomputed()?
		 */
		final CharMatcher NOT_BASE64 = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.inRange('0', '9')).or(CharMatcher.anyOf("+/")).negate();
	 		
		 if (input.length() % 4 != 0) {
	    //        report.error(newMsg(data, bundle, "err.format.base64.badLength")
	    //            .putArgument("length", input.length()));
		// TODO: Error
	            return null;
	        }

	        final int index
	            = NOT_BASE64.indexIn(PATTERN.matcher(input).replaceFirst(""));

	        if (index == -1)
	            return ValidationReport.empty();

	        return null;
	        // TODO: Error
	        /*report.error(newMsg(data, bundle, "err.format.base64.illegalChars")
	            .putArgument("character", Character.toString(input.charAt(index)))
	            .putArgument("index", index));*/
	}
	

}
