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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

import com.atlassian.oai.validator.model.Body;
import com.atlassian.oai.validator.report.MessageResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.schema.SchemaValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CharMatcher;

import io.swagger.v3.oas.models.media.Schema;

/**
 * SwaggerValidatorUtils
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SwaggerValidatorUtils {

	public static boolean isBase64SchemaFile(Schema<?> schema) {
		return "string".equals(schema.getType()) && "base64".equals(schema.getFormat());
	}

	public static boolean isBinarySchemaFile(Schema<?> schema) {
		return "string".equals(schema.getType()) && "binary".equals(schema.getFormat());
	}

	// Adattata dalla com.github.fgce...jsonschema
	private static final Optional<String> validateBase64String(String input) {
		
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
			return Optional.of("err.format.base64.badLength, should be multiple of 4: " + input.length());
		}
	
		final int index	= NOT_BASE64.indexIn(PATTERN.matcher(input).replaceFirst(""));
	
		if (index == -1)
			return Optional.empty();
	
		return Optional.of(
				"err.format.base64.illegalChars: character '" + Character.toString(input.charAt(index)) + "' index " + index);	        
	}
	public static final ValidationReport validateBase64Body(Body body, MessageResolver messages, boolean request) {
		try {		
			var error = SwaggerValidatorUtils.validateBase64String(body.toString(StandardCharsets.UTF_8));
			if (error.isPresent()) {
				return ValidationReport.singleton(
						messages.create(request ? "validation.request.body.schema": "validation.response.body.schema", 
								(request ? "[REQUEST] ": "[RESPONSE] ")+
										error.get()));
			} else {
				return ValidationReport.empty();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final ValidationReport validateJsonFormat(Body body, MessageResolver messages, boolean request) {
		try {
			body.toJsonNode();
		} catch (final IOException e) {
            return ValidationReport.singleton(
            		messages.create(
            				request ? "validation.request.body.schema.invalidJson" : "validation.response.body.schema.invalidJson",
                            messages.get(SchemaValidator.INVALID_JSON_KEY, e.getMessage()).getMessage()
                    )
            );
        }
		return ValidationReport.empty();		
	}

	public static final String getSchemaVersion(JsonNode node) {
	    if (node == null) {
	        return null;
	    }
	
	    JsonNode version = node.get("openapi");
	    if (version != null) {
	        return version.toString();
	    }
	
	    version = node.get("swagger");
	    if (version != null) {
	        return version.toString();
	    }
	    version = node.get("swaggerVersion");
	    if (version != null) {
	        return version.toString();
	    }
	
	    return null;
	}
	
	public static final boolean isSchemaV1(String version) {
		return version != null && (version.startsWith("\"1") || version.startsWith("1"));
	}
	
	public static final boolean isSchemaV2(String version) {
		return version != null && (version.startsWith("\"2") || version.startsWith("2"));		
	}
	
	public static final boolean isSchemaV3(String version) {
		return version != null && (version.startsWith("\"3") || version.startsWith("3"));
		
	}

}
