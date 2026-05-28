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

package org.openspcoop2.utils.openapi.validator.test;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.openapi.validator.OpenAPILibrary;
import org.openspcoop2.utils.openapi.validator.OpenapiApiValidatorConfig;
import org.openspcoop2.utils.openapi.validator.OpenapiLibraryValidatorConfig;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.ApiValidatorConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.rest.entity.TextHttpRequestEntity;
import org.openspcoop2.utils.rest.entity.TextHttpResponseEntity;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * Test mirato delle feature OpenAPI 3.1.
 *
 * Riusa lo stesso file testOpenAPI_3.1.yaml/json già usato da OpenApi31Test.
 * Ogni feature è esercitata da un endpoint dedicato sotto /feature31/...
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenApi31FeaturesTest {

	public static void main(String[] args) throws Exception {

		String tipo = "yaml";
		if (args != null && args.length > 0) {
			tipo = args[0];
		}
		OpenAPILibrary openAPILibrary = OpenAPILibrary.kappa;
		if (args != null && args.length > 1) {
			openAPILibrary = OpenAPILibrary.valueOf(args[1]);
		}
		boolean mergeSpec = true;
		if (args != null && args.length > 2) {
			mergeSpec = Boolean.valueOf(args[2]);
		}

		IApiValidator validator = buildValidator(tipo, openAPILibrary, mergeSpec);

		System.out.println("=== OpenAPI 3.1 features (engine=" + openAPILibrary + " tipo=" + tipo + " merge=" + mergeSpec + ") ===\n");

		testInfoSummaryAndLicenseIdentifier();
		testWebhooks();
		testTypesArray(validator, openAPILibrary);
		testConst(validator, openAPILibrary);
		testConditional(validator, openAPILibrary);
		testDependentRequired(validator, openAPILibrary);
		testPrefixItems(validator, openAPILibrary);
		testUnevaluatedProperties(validator, openAPILibrary);
		testContentEncoding(validator, openAPILibrary);
		testDependentSchemas(validator, openAPILibrary);
		testUnevaluatedItems(validator, openAPILibrary);
		testPropertyNames(validator, openAPILibrary);
		testNullType(validator, openAPILibrary);
		testMultiType(validator, openAPILibrary);
		testRequiredEmpty(validator, openAPILibrary);
		testDefs(validator, openAPILibrary);
		testContentMediaType(validator, openAPILibrary);
		testMinMaxContains(validator, openAPILibrary);
		testPatternProperties(validator, openAPILibrary);
		testExamplesArray(validator, openAPILibrary);
		testReusablePathItem(validator, openAPILibrary);
		testEnumMixed(validator, openAPILibrary);
		testNotKeyword(validator, openAPILibrary);
		testFormatExtensions(validator, openAPILibrary);
		testComponentsRefs(validator, openAPILibrary);
		testCrossFileRefInProperty(tipo, openAPILibrary, mergeSpec);

		System.out.println("\n=== ALL OpenAPI 3.1 feature tests completed ===");
	}

	// ==== feature: info.summary + license.identifier ====
	// Già verificate dal solo fatto che il caricamento dello spec non fallisca.
	private static void testInfoSummaryAndLicenseIdentifier() {
		System.out.println("[feature] info.summary + license.identifier ... ok (spec parsato senza errori)");
	}

	// ==== feature: webhooks (top-level) ====
	private static void testWebhooks() {
		// idem: il parsing dello spec con la sezione webhooks non deve fallire
		System.out.println("[feature] webhooks (top-level) ... ok (spec parsato senza errori)");
	}

	// Per ogni feature: testiamo SIA la variante inline ('/feature31/<name>-inline') SIA quella via $ref a components ('/feature31/<name>-ref').
	// Per ognuna, validiamo il body sia in request che in response (json-sKema fa la stessa logica ma kappa istanzia
	// validator separati, quindi controllare entrambi aiuta a intercettare regressioni nel dispatching).
	private static final String[] FORMS = new String[]{"-inline", "-ref"};

	// ==== feature: type array (sostituisce nullable) ====
	private static void testTypesArray(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/types-array" + f;
			expectOkBoth(v, path, "{\"name\":\"Rex\",\"age\":3}", "name+age valorizzati");
			expectOkBoth(v, path, "{\"name\":null,\"age\":null}", "name+age null (type array consente null)");
			expectFailBoth(v, path, "{\"name\":42}", "name di tipo number invece che string|null");
		}
	}

	// ==== feature: const ====
	private static void testConst(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/const-value" + f;
			expectOkBoth(v, path, "{\"kind\":\"Pet\",\"name\":\"Rex\"}", "kind=Pet (const match)");
			expectFailBoth(v, path, "{\"kind\":\"Other\",\"name\":\"Rex\"}", "kind=Other (const mismatch)");
		}
	}

	// ==== feature: if/then/else ====
	private static void testConditional(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/conditional" + f;
			expectOkBoth(v, path, "{\"country\":\"IT\",\"postalCode\":\"00100\"}", "IT con postalCode 5 cifre");
			expectOkBoth(v, path, "{\"country\":\"UK\"}", "UK senza postalCode (then non si applica)");
			expectFailBoth(v, path, "{\"country\":\"IT\"}", "IT senza postalCode (then richiede postalCode)");
			expectFailBoth(v, path, "{\"country\":\"IT\",\"postalCode\":\"abcde\"}", "IT con postalCode non numerico");
		}
	}

	// ==== feature: dependentRequired ====
	private static void testDependentRequired(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/dependent-required" + f;
			expectOkBoth(v, path, "{}", "nessun campo (dependent inattivo)");
			expectOkBoth(v, path, "{\"creditCard\":\"1234\",\"billingAddress\":\"via Roma 1\",\"cvv\":\"123\"}", "tutti i campi presenti");
			expectFailBoth(v, path, "{\"creditCard\":\"1234\"}", "creditCard senza billingAddress/cvv");
			expectFailBoth(v, path, "{\"creditCard\":\"1234\",\"billingAddress\":\"via Roma 1\"}", "creditCard senza cvv");
		}
	}

	// ==== feature: prefixItems (tuple typing) ====
	private static void testPrefixItems(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/prefix-items" + f;
			expectOkBoth(v, path, "[\"a\",1,true]", "tupla [string,int,bool] valida");
			expectFailBoth(v, path, "[\"a\",\"b\",true]", "secondo elemento non integer");
			expectFailBoth(v, path, "[\"a\",1,true,\"extra\"]", "elemento extra (items:false)");
		}
	}

	// ==== feature: unevaluatedProperties ====
	private static void testUnevaluatedProperties(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/unevaluated-props" + f;
			expectOkBoth(v, path, "{\"name\":\"Rex\",\"age\":3}", "name+age (entrambi evaluated)");
			expectFailBoth(v, path, "{\"name\":\"Rex\",\"age\":3,\"extra\":\"x\"}", "extra non valutato → unevaluatedProperties:false");
		}
	}

	// ==== feature: contentEncoding/contentMediaType ====
	private static void testContentEncoding(IApiValidator v, OpenAPILibrary lib) throws Exception {
		// contentEncoding/contentMediaType in 3.1 sono annotation-only di default;
		// la presenza del campo string e' sufficiente per la validazione strutturale.
		for (String f : FORMS) {
			String path = "/feature31/content-encoding" + f;
			expectOkBoth(v, path, "{\"attachment\":\"JVBERi0xLjQKJ8jIyMjK\"}", "attachment base64 string");
			expectFailBoth(v, path, "{}", "attachment required mancante");
		}
	}

	// ==== feature: dependentSchemas ====
	private static void testDependentSchemas(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/dependent-schemas" + f;
			expectOkBoth(v, path, "{}", "nessun campo");
			expectOkBoth(v, path, "{\"creditCard\":\"1234\",\"billingAddress\":\"via Roma 1\"}", "credit+billing valido (billing minLength 3)");
			expectFailBoth(v, path, "{\"creditCard\":\"1234\"}", "creditCard senza billingAddress");
			expectFailBoth(v, path, "{\"creditCard\":\"1234\",\"billingAddress\":\"vR\"}", "billingAddress troppo corto (minLength 3)");
		}
	}

	// ==== feature: unevaluatedItems ====
	private static void testUnevaluatedItems(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/unevaluated-items" + f;
			expectOkBoth(v, path, "[\"x\",42]", "tupla [string,int] valida");
			expectFailBoth(v, path, "[\"x\",42,\"extra\"]", "elemento extra non valutato (unevaluatedItems:false)");
		}
	}

	// ==== feature: propertyNames ====
	private static void testPropertyNames(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/property-names" + f;
			expectOkBoth(v, path, "{\"abc_def\":1,\"foo123\":2}", "tutti i property name validi");
			expectFailBoth(v, path, "{\"X-bad\":1}", "property name con maiuscola e '-' non valido");
		}
	}

	// ==== feature: type null esplicito ====
	private static void testNullType(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/null-type" + f;
			expectOkBoth(v, path, "{\"onlyNull\":null}", "onlyNull=null OK");
			expectFailBoth(v, path, "{\"onlyNull\":\"x\"}", "onlyNull stringa, atteso null");
		}
	}

	// ==== feature: type multi-value ====
	private static void testMultiType(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/multi-type" + f;
			expectOkBoth(v, path, "{\"anyKind\":1}", "anyKind=integer");
			expectOkBoth(v, path, "{\"anyKind\":\"text\"}", "anyKind=string");
			expectOkBoth(v, path, "{\"anyKind\":null}", "anyKind=null");
			expectFailBoth(v, path, "{\"anyKind\":true}", "anyKind=boolean non ammesso");
		}
	}

	// ==== feature: required vuoto ====
	private static void testRequiredEmpty(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/required-empty" + f;
			expectOkBoth(v, path, "{}", "body vuoto valido (required: [])");
			expectOkBoth(v, path, "{\"anything\":\"x\"}", "property opzionale valorizzata");
		}
	}

	// ==== feature: $defs ====
	private static void testDefs(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/defs" + f;
			expectOkBoth(v, path, "{\"id\":\"AB1234\"}", "id matcha pattern $defs.Identifier");
			expectFailBoth(v, path, "{\"id\":\"abc\"}", "id non matcha pattern");
		}
	}

	// ==== feature: contentMediaType + contentSchema ====
	// In JSON Schema 2020-12 sono annotation-only di default: validiamo la cornice (presenza/tipo
	// del campo string) e ci accertiamo che il parsing dello spec non fallisca.
	private static void testContentMediaType(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/content-media-type" + f;
			expectOkBoth(v, path, "{\"payload\":\"eyJpZCI6MX0=\"}", "payload base64 string presente");
			expectFailBoth(v, path, "{}", "payload required mancante");
			expectFailBoth(v, path, "{\"payload\":42}", "payload non string");
		}
	}

	// ==== feature: minContains / maxContains ====
	private static void testMinMaxContains(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/min-max-contains" + f;
			expectOkBoth(v, path, "[2,4]", "2 elementi pari (minContains=2)");
			expectOkBoth(v, path, "[1,2,4,6]", "3 elementi pari (entro maxContains=3)");
			expectFailBoth(v, path, "[2,1,1]", "solo 1 elemento pari (sotto minContains)");
			expectFailBoth(v, path, "[2,4,6,8]", "4 elementi pari (oltre maxContains)");
		}
	}

	// ==== feature: patternProperties ====
	private static void testPatternProperties(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/pattern-properties" + f;
			expectOkBoth(v, path, "{\"s_name\":\"alice\",\"i_age\":42}", "due pattern ok");
			expectOkBoth(v, path, "{}", "oggetto vuoto");
			expectFailBoth(v, path, "{\"s_name\":42}", "s_* deve essere string");
			expectFailBoth(v, path, "{\"i_age\":\"x\"}", "i_* deve essere integer");
			expectFailBoth(v, path, "{\"other\":\"x\"}", "key non match e additionalProperties:false");
		}
	}

	// ==== feature: examples (array, 3.1) ====
	// `examples` non viene mai validato a runtime: il check è che lo spec carichi e la
	// validazione del body funzioni comunque correttamente.
	private static void testExamplesArray(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/examples-array" + f;
			expectOkBoth(v, path, "{\"name\":\"alice\"}", "body valido (examples ignorati a runtime)");
			expectFailBoth(v, path, "{}", "name required mancante");
		}
	}

	// ==== feature: enum con tipi misti (3.1) ====
	private static void testEnumMixed(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/enum-mixed" + f;
			expectOkBoth(v, path, "{\"value\":1}", "integer 1 in enum");
			expectOkBoth(v, path, "{\"value\":\"x\"}", "string 'x' in enum");
			expectOkBoth(v, path, "{\"value\":null}", "null in enum");
			expectOkBoth(v, path, "{\"value\":true}", "true in enum");
			expectFailBoth(v, path, "{\"value\":2}", "integer 2 non in enum");
			expectFailBoth(v, path, "{\"value\":\"y\"}", "string 'y' non in enum");
			expectFailBoth(v, path, "{\"value\":false}", "false non in enum");
		}
	}

	// ==== feature: 'not' keyword ====
	private static void testNotKeyword(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/not-keyword" + f;
			expectOkBoth(v, path, "{\"value\":\"x\"}", "value=string ammesso (not integer)");
			expectOkBoth(v, path, "{\"value\":null}", "value=null ammesso (not integer)");
			expectOkBoth(v, path, "{\"value\":1.5}", "value=number(non-integer) ammesso");
			expectFailBoth(v, path, "{\"value\":42}", "value=integer rifiutato (not)");
		}
	}

	// ==== feature: format keywords (uuid, duration: rivisitati/nuovi in 3.1) ====
	// idn-email/iri/json-pointer non sono implementati nativamente da json-sKema 0.30.0,
	// per cui non sono testati qui.
	private static void testFormatExtensions(IApiValidator v, OpenAPILibrary lib) throws Exception {
		for (String f : FORMS) {
			String path = "/feature31/format-extensions" + f;
			expectOkBoth(v, path, "{\"id\":\"12345678-1234-1234-1234-123456789012\",\"dur\":\"PT1H30M\"}", "uuid + duration validi");
			expectFailBoth(v, path, "{\"id\":\"not-a-uuid\",\"dur\":\"PT1H\"}", "uuid invalido");
			expectFailBoth(v, path, "{\"id\":\"12345678-1234-1234-1234-123456789012\",\"dur\":\"not-a-duration\"}", "duration invalida");
		}
	}

	// ==== feature: $ref a components.requestBodies / responses / parameters ====
	// Verifica che la risoluzione $ref funzioni anche sui non-schema components.
	private static void testComponentsRefs(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/components-refs";
		// query parameter 'tag' required (minLength:2) via $ref a components.parameters
		// body via $ref a components.requestBodies, response via $ref a components.responses
		expectOk(v, path + "?tag=ab", "{\"name\":\"alice\"}", "request: tag valido + body valido");
		expectFail(v, path, "{\"name\":\"alice\"}", "request: tag mancante");
		expectFail(v, path + "?tag=a", "{\"name\":\"alice\"}", "request: tag troppo corto");
		expectFail(v, path + "?tag=ab", "{}", "request: body name mancante");
		expectFail(v, path + "?tag=ab", "{\"name\":\"x\"}", "request: body name troppo corto");
		expectOkResponse(v, path, "{\"resultId\":42}", "response: resultId integer");
		expectFailResponse(v, path, "{}", "response: resultId mancante");
		expectFailResponse(v, path, "{\"resultId\":\"x\"}", "response: resultId non integer");
	}

	// ==== feature: components.pathItems riusabili (3.1) ====
	private static void testReusablePathItem(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/reusable-pathitem";
		expectOk(v, path, "{\"token\":\"abcd\"}", "request: token ok");
		expectFail(v, path, "{\"token\":\"x\"}", "request: token troppo corto");
		expectFail(v, path, "{}", "request: token required mancante");
		expectOkResponse(v, path, "{\"ok\":true}", "response: ok=true");
		expectFailResponse(v, path, "{\"ok\":\"yes\"}", "response: ok non boolean");
		expectFailResponse(v, path, "{}", "response: ok required mancante");
	}

	// === helpers ===

	private static IApiValidator buildValidator(String tipo, OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception {
		String resource = "yaml".equalsIgnoreCase(tipo)
				? "/org/openspcoop2/utils/openapi/test/testOpenAPI_3.1.yaml"
				: "/org/openspcoop2/utils/openapi/test/testOpenAPI_3.1.json";

		URL url = OpenApi31FeaturesTest.class.getResource(resource);
		if (url == null) {
			throw new IllegalStateException("Resource not found: " + resource);
		}

		ApiSchemaType schemaType = "yaml".equalsIgnoreCase(tipo) ? ApiSchemaType.YAML : ApiSchemaType.JSON;
		String ext = "yaml".equalsIgnoreCase(tipo) ? "yaml" : "json";
		ApiSchema schema1 = new ApiSchema("test_import." + ext,
				Utilities.getAsByteArray(OpenApi31FeaturesTest.class.getResourceAsStream(
						"/org/openspcoop2/utils/openapi/test/test_import." + ext)),
				schemaType);
		ApiSchema schema2 = new ApiSchema("test_import2." + ext,
				Utilities.getAsByteArray(OpenApi31FeaturesTest.class.getResourceAsStream(
						"/org/openspcoop2/utils/openapi/test/test_import2." + ext)),
				schemaType);

		IApiReader reader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		reader.init(LoggerWrapperFactory.getLogger(OpenApi31FeaturesTest.class), new File(url.toURI()), new ApiReaderConfig(), schema1, schema2);
		Api api = reader.read();

		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(false);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);

		ApiValidatorConfig config = ApiFactory.newApiValidatorConfig(openAPILibrary.name());
		config.readProperties(configO.getOpenApiValidatorConfig()::getProperty);

		IApiValidator validator = ApiFactory.newApiValidator(openAPILibrary.name());
		validator.init(LoggerWrapperFactory.getLogger(OpenApi31FeaturesTest.class), api, config);
		return validator;
	}

	private static TextHttpRequestEntity request(String path, String body) {
		TextHttpRequestEntity req = new TextHttpRequestEntity();
		String urlOnly = path;
		Map<String, List<String>> qParams = null;
		int qi = path.indexOf('?');
		if (qi >= 0) {
			urlOnly = path.substring(0, qi);
			if (qi < path.length() - 1) {
				qParams = new HashMap<>();
				for (String kv : path.substring(qi + 1).split("&")) {
					if (kv.isEmpty()) {
						continue;
					}
					int eq = kv.indexOf('=');
					String name = eq > 0 ? kv.substring(0, eq) : kv;
					String value = eq > 0 ? kv.substring(eq + 1) : "";
					qParams.computeIfAbsent(name, k -> new java.util.ArrayList<>()).add(value);
				}
			}
		}
		req.setUrl(urlOnly);
		req.setMethod(HttpRequestMethod.POST);
		req.setContent(body);
		req.setContentType("application/json");
		Map<String, List<String>> headers = new HashMap<>();
		TransportUtils.addHeader(headers, HttpConstants.CONTENT_TYPE, "application/json");
		req.setHeaders(headers);
		if (qParams != null) {
			req.setParameters(qParams);
		}
		return req;
	}

	private static TextHttpResponseEntity response(String path, String body) {
		TextHttpResponseEntity resp = new TextHttpResponseEntity();
		resp.setUrl(path);
		resp.setMethod(HttpRequestMethod.POST);
		resp.setStatus(200);
		resp.setContent(body);
		resp.setContentType("application/json");
		Map<String, List<String>> headers = new HashMap<>();
		TransportUtils.addHeader(headers, HttpConstants.CONTENT_TYPE, "application/json");
		resp.setHeaders(headers);
		return resp;
	}

	// ==== regressione: $ref cross-file usato come VALORE DI PROPRIETÀ di uno schema oggetto ====
	// Bug originario: UniqueInterfaceGenerator, durante il replace della passata 'senza virgolette'
	// applicabile a Yaml31.mapper, produceva '$ref: #/components/...' (senza quotes). In YAML,
	// '#' preceduto da spazio avvia un commento, quindi il parser leggeva '$ref:' come null e kappa
	// emetteva "$ref: expected string, found null". Caso non coperto dagli altri test perché qui i
	// $ref cross-file sono usati come schema top-level (es. requestBody.schema), non come valore di
	// una properties di uno schema object.
	private static void testCrossFileRefInProperty(String tipo, OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception {
		System.out.println("\n[regression] $ref cross-file in properties (engine=" + openAPILibrary
				+ " merge=" + mergeSpec + ") ...");
		IApiValidator v = buildValidatorExternalRef(tipo, openAPILibrary, mergeSpec);
		expectOkBoth(v, "/echo",
				"{\"code\":\"AB123\",\"details\":{\"category\":\"warning\",\"priority\":3}}",
				"body valido (code conforme al pattern, details popolato da schema cross-file)");
		expectFailBoth(v, "/echo",
				"{\"code\":\"xx\",\"details\":{\"category\":\"warning\",\"priority\":3}}",
				"code viola il pattern ^[A-Z]{2}\\d{3}$");
	}

	private static IApiValidator buildValidatorExternalRef(String tipo, OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception {
		String mainResource = "yaml".equalsIgnoreCase(tipo)
				? "/org/openspcoop2/utils/openapi/test/testOpenAPI_3.1_externalRef.yaml"
				: "/org/openspcoop2/utils/openapi/test/testOpenAPI_3.1_externalRef.json";
		URL url = OpenApi31FeaturesTest.class.getResource(mainResource);
		if (url == null) {
			throw new IllegalStateException("Resource not found: " + mainResource);
		}

		ApiSchemaType schemaType = "yaml".equalsIgnoreCase(tipo) ? ApiSchemaType.YAML : ApiSchemaType.JSON;
		String ext = "yaml".equalsIgnoreCase(tipo) ? "yaml" : "json";
		ApiSchema importSchema = new ApiSchema("test_externalRef_import." + ext,
				Utilities.getAsByteArray(OpenApi31FeaturesTest.class.getResourceAsStream(
						"/org/openspcoop2/utils/openapi/test/test_externalRef_import." + ext)),
				schemaType);

		IApiReader reader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
		reader.init(LoggerWrapperFactory.getLogger(OpenApi31FeaturesTest.class), new File(url.toURI()),
				new ApiReaderConfig(), importSchema);
		Api api = reader.read();

		OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
		configO.setEmitLogError(false);
		configO.setOpenApiValidatorConfig(new OpenapiLibraryValidatorConfig());
		configO.getOpenApiValidatorConfig().setOpenApiLibrary(openAPILibrary);
		configO.getOpenApiValidatorConfig().setValidateAPISpec(true);
		configO.getOpenApiValidatorConfig().setMergeAPISpec(mergeSpec);

		ApiValidatorConfig config = ApiFactory.newApiValidatorConfig(openAPILibrary.name());
		config.readProperties(configO.getOpenApiValidatorConfig()::getProperty);

		IApiValidator validator = ApiFactory.newApiValidator(openAPILibrary.name());
		validator.init(LoggerWrapperFactory.getLogger(OpenApi31FeaturesTest.class), api, config);
		return validator;
	}

	private static void expectOk(IApiValidator v, String path, String body, String descr) throws Exception {
		System.out.println("  [ok req ] " + path + " — " + descr);
		try {
			v.validate(request(path, body));
		} catch (ValidatorException e) {
			throw new Exception("Validazione request non attesa fallita: " + descr + " → " + e.getMessage(), e);
		}
	}

	private static void expectFail(IApiValidator v, String path, String body, String descr) throws Exception {
		System.out.println("  [err req] " + path + " — " + descr);
		try {
			v.validate(request(path, body));
			throw new Exception("Validazione request doveva fallire: " + descr);
		} catch (ValidatorException e) {
			// atteso
		}
	}

	private static void expectOkResponse(IApiValidator v, String path, String body, String descr) throws Exception {
		System.out.println("  [ok rsp ] " + path + " — " + descr);
		try {
			v.validate(response(path, body));
		} catch (ValidatorException e) {
			throw new Exception("Validazione response non attesa fallita: " + descr + " → " + e.getMessage(), e);
		}
	}

	private static void expectFailResponse(IApiValidator v, String path, String body, String descr) throws Exception {
		System.out.println("  [err rsp] " + path + " — " + descr);
		try {
			v.validate(response(path, body));
			throw new Exception("Validazione response doveva fallire: " + descr);
		} catch (ValidatorException e) {
			// atteso
		}
	}

	private static void expectOkBoth(IApiValidator v, String path, String body, String descr) throws Exception {
		expectOk(v, path, body, descr);
		expectOkResponse(v, path, body, descr);
	}

	private static void expectFailBoth(IApiValidator v, String path, String body, String descr) throws Exception {
		expectFail(v, path, body, descr);
		expectFailResponse(v, path, body, descr);
	}
}
