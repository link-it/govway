package org.openspcoop2.utils.openapi.validator;

import static com.atlassian.oai.validator.util.ContentTypeUtils.findMostSpecificMatch;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.openspcoop2.utils.rest.ProcessingException;

import com.atlassian.oai.validator.interaction.request.CustomRequestValidator;
import com.atlassian.oai.validator.interaction.request.RequestValidator;
import com.atlassian.oai.validator.model.ApiOperation;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.report.MessageResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.schema.SchemaValidator;
import com.google.common.base.CharMatcher;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;

public class SwaggerRequestValidator extends RequestValidator {

	// Se lo schema del request body è: type: string, format: binary, ovvero un
	// BinarySchema,
	// allora disattiva la validazione del body e controlla solo che questo sia
	// presente.

	public SwaggerRequestValidator(SchemaValidator schemaValidator, MessageResolver messages, OpenAPI api,
			List<CustomRequestValidator> customRequestValidators) {
		super(schemaValidator, messages, api, customRequestValidators);
	}

	@Override
	public ValidationReport validateRequest(Request request, ApiOperation apiOperation) {

		// Se lo schema del request body è: type: string, format: binary, ovvero un
		// BinarySchema,
		// allora disattiva la validazione del body e controlla solo che questo sia
		// presente se è required.
		// Se invece il format è base64 controlla che sia in base64
		
		var requestBody = apiOperation.getOperation().getRequestBody();
		var maybeMediaType = findApiMediaTypeForRequest(request, requestBody);
		if (!maybeMediaType.isEmpty()) {
			MediaType type = maybeMediaType.get().getRight();
			if (type.getSchema() instanceof BinarySchema) {
				
				if (Boolean.TRUE.equals(requestBody.getRequired()) && request.getRequestBody().isEmpty()) {
					// TODO: sollevaError required
				}
				
				BinarySchema schema = (BinarySchema) type.getSchema();
				if ("string".equals(schema.getType()) && "binary".equals(schema.getFormat())) {
					return ValidationReport.empty();
				}
				else if ("string".equals(schema.getType()) && "base64".equals(schema.getFormat())) {
					try {
						return validateBase64String(request.getRequestBody().get().toString(null));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}				
			}
		}


		return super.validateRequest(request, apiOperation);
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
