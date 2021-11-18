package org.openspcoop2.utils.openapi.validator.swagger;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CharMatcher;

import io.swagger.v3.oas.models.media.Schema;

public class SwaggerValidatorUtils {

	public static boolean isBase64SchemaFile(Schema schema) {
		return "string".equals(schema.getType()) && "base64".equals(schema.getFormat());
	}

	public static boolean isBinarySchemaFile(Schema schema) {
		return "string".equals(schema.getType()) && "binary".equals(schema.getFormat());
	}

	// Adattata dalla com.github.fgce...jsonschema
	public static final Optional<String> validateBase64String(String input) {
		
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
	
	        final int index
	            = NOT_BASE64.indexIn(PATTERN.matcher(input).replaceFirst(""));
	
	        if (index == -1)
	            return Optional.empty();
	
	        return Optional.of(
	        		"err.format.base64.illegalChars: character '" + Character.toString(input.charAt(index)) + "' index " + index);	        
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
