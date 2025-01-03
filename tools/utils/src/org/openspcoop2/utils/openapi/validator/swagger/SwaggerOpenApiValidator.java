/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.swagger.util.Json;

/**
 * SwaggerOpenApiValidator
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SwaggerOpenApiValidator {

	// vedi io.swagger.handler.ValidatorController
	
	private static final String SCHEMA_OPENAPI3_FILE = "schema-openapi3.json";
	private static final String SCHEMA_SWAGGER2_FILE = "schema-swagger-v2.json";
	private static final String INVALID_VERSION = "Unsupported Swagger version";
	static ObjectMapper JsonMapper = Json.mapper();

	private JsonSchema schemaV2;
	private JsonSchema schemaV3;

	public SwaggerOpenApiValidator() {
		String prefix = "/org/openspcoop2/utils/openapi/validator/swagger/";
		
		/* 
		 * Schemi presi da:
		 * - https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/schemas/v3.0/schema.json
		 * - https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/schemas/v2.0/schema.json
		 * 
		 * NOTA: lo schema v3 è stato modificato per sostituire tutte le occorrenze di 
		 * 			"format": "uri-reference" 
		 * 		 con 
		 * 			"format": "uri"
		 * 		 Il validatore non supportava uri-reference
		 */		
		this.schemaV2 = resolveJsonSchema(getResourceFileAsString(prefix + SCHEMA_SWAGGER2_FILE), true);
		this.schemaV3 = resolveJsonSchema(getResourceFileAsString(prefix + SCHEMA_OPENAPI3_FILE), true);
	}

	public Optional<String> validate(JsonNode spec) throws ProcessingException {
		
		// Gli swaggerV2 vengono convertiti in OpenAPI, per cui la validazione devo farla prima 
		// di aver convertito lo schema del json node nell'oggetto OpenAPI altrimenti si perdono le info
		
		boolean isVersion2 = false;
		
		String version = SwaggerValidatorUtils.getSchemaVersion(spec);
        if (SwaggerValidatorUtils.isSchemaV1(version)) {
        	return Optional.of(INVALID_VERSION);        	
        } else if (SwaggerValidatorUtils.isSchemaV2(version)) {
            isVersion2 = true;
        } else if (version == null || SwaggerValidatorUtils.isSchemaV3(version)) {
        	// siamo in v3
        }
        
        JsonSchema schema = getSchema(isVersion2);
        ProcessingReport report = schema.validateUnchecked(spec);
        ListProcessingReport lp = new ListProcessingReport();
        lp.mergeWith(report);

        StringBuilder result = new StringBuilder("");
        java.util.Iterator<ProcessingMessage> it = lp.iterator();
        while (it.hasNext()) {
            ProcessingMessage pm = it.next();
            result.append(pm.toString());
            result.append("\n");
        }		

        return result.length() == 0 ? Optional.empty() : Optional.of(result.toString());
	}
	
	
	private JsonSchema getSchema(boolean isVersion2) {
		if (isVersion2) {
			return this.schemaV2;
		} else {
			return this.schemaV3;
		}
	}

	
	private JsonSchema resolveJsonSchema(String schemaAsString, boolean removeId) {
		try {
			JsonNode schemaObject = JsonMapper.readTree(schemaAsString);
			if (removeId) {
				ObjectNode oNode = (ObjectNode) schemaObject;
				if (oNode.get("id") != null) {
					oNode.remove("id");
				}
				if (oNode.get("$schema") != null) {
					oNode.remove("$schema");
				}
				if (oNode.get("description") != null) {
					oNode.remove("description");
				}
			}
			
			// Come schema factory utilizzo quella di atlassian che estende il jsonSchema
			// con altri tipi
			//JsonSchemaFactory factory = SwaggerV20Library.schemaFactory();//JsonSchemaFactory.byDefault();
			JsonSchemaFactory factory = JsonSchemaFactory
					.newBuilder()
			           .setReportProvider(
		                        // Only emit ERROR and above from the JSON schema validation
		                        new ListReportProvider(LogLevel.WARNING, LogLevel.FATAL))
					.freeze();
			
			return factory.getJsonSchema(schemaObject);
		} catch (Exception e) {
			throw new RuntimeException("Errore inatteso durante il parsing JSON dello schema: " + e.getMessage());
		}
	}

	
	public String getResourceFileAsString(String fileName) {
		
		try {
			InputStream is = SwaggerOpenApiValidator.class.getResourceAsStream(fileName);
			if (is != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				var ret = reader.lines().collect(Collectors.joining(System.lineSeparator()));
				reader.close();
				return ret;
			}
		} catch (Exception e) {
			throw new RuntimeException("Errore inatteso durante la lettura del file dello schema: " + e.getMessage());
		}
		return null;
	}

}
