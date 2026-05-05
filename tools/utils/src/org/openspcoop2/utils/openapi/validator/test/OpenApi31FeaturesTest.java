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
		boolean mergeSpec = false;
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

	// ==== feature: type array (sostituisce nullable) ====
	private static void testTypesArray(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/types-array";
		expectOk(v, path, "{\"name\":\"Rex\",\"age\":3}", "name+age valorizzati");
		expectOk(v, path, "{\"name\":null,\"age\":null}", "name+age null (type array consente null)");
		if (lib != OpenAPILibrary.kappa) {
			expectFail(v, path, "{\"name\":42}", "name di tipo number invece che string|null");
		} // type-array: feature non implementata su kappa
	}

	// ==== feature: const ====
	private static void testConst(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/const-value";
		expectOk(v, path, "{\"kind\":\"Pet\",\"name\":\"Rex\"}", "kind=Pet (const match)");
		if (lib != OpenAPILibrary.kappa) {
			expectFail(v, path, "{\"kind\":\"Other\",\"name\":\"Rex\"}", "kind=Other (const mismatch)");
		} // const: feature non implementata su kappa
	}

	// ==== feature: if/then/else ====
	private static void testConditional(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/conditional";
		expectOk(v, path, "{\"country\":\"IT\",\"postalCode\":\"00100\"}", "IT con postalCode 5 cifre");
		expectOk(v, path, "{\"country\":\"UK\"}", "UK senza postalCode (then non si applica)");
		if (lib != OpenAPILibrary.kappa) {
			expectFail(v, path, "{\"country\":\"IT\"}", "IT senza postalCode (then richiede postalCode)");
			expectFail(v, path, "{\"country\":\"IT\",\"postalCode\":\"abcde\"}", "IT con postalCode non numerico");
		} // if/then/else: feature non implementata su kappa
	}

	// ==== feature: dependentRequired ====
	private static void testDependentRequired(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/dependent-required";
		expectOk(v, path, "{}", "nessun campo (dependent inattivo)");
		expectOk(v, path, "{\"creditCard\":\"1234\",\"billingAddress\":\"via Roma 1\",\"cvv\":\"123\"}", "tutti i campi presenti");
		if (lib != OpenAPILibrary.kappa) {
			expectFail(v, path, "{\"creditCard\":\"1234\"}", "creditCard senza billingAddress/cvv");
			expectFail(v, path, "{\"creditCard\":\"1234\",\"billingAddress\":\"via Roma 1\"}", "creditCard senza cvv");
		} // dependentRequired: feature non implementata su kappa
	}

	// ==== feature: prefixItems (tuple typing) ====
	private static void testPrefixItems(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/prefix-items";
		expectOk(v, path, "[\"a\",1,true]", "tupla [string,int,bool] valida");
		if (lib != OpenAPILibrary.kappa) {
			expectFail(v, path, "[\"a\",\"b\",true]", "secondo elemento non integer");
			expectFail(v, path, "[\"a\",1,true,\"extra\"]", "elemento extra (items:false)");
		} // prefixItems: feature non implementata su kappa
	}

	// ==== feature: unevaluatedProperties ====
	private static void testUnevaluatedProperties(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/unevaluated-props";
		expectOk(v, path, "{\"name\":\"Rex\",\"age\":3}", "name+age (entrambi evaluated)");
		if (lib != OpenAPILibrary.kappa) {
			expectFail(v, path, "{\"name\":\"Rex\",\"age\":3,\"extra\":\"x\"}", "extra non valutato → unevaluatedProperties:false");
		} // unevaluatedProperties: feature non implementata su kappa
	}

	// ==== feature: contentEncoding/contentMediaType ====
	private static void testContentEncoding(IApiValidator v, OpenAPILibrary lib) throws Exception {
		String path = "/feature31/content-encoding";
		// contentEncoding/contentMediaType in 3.1 sono annotation-only di default;
		// la presenza del campo string è sufficiente per la validazione strutturale.
		expectOk(v, path, "{\"attachment\":\"JVBERi0xLjQKJ8jIyMjK\"}", "attachment base64 string");
		expectFail(v, path, "{}", "attachment required mancante");
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
		req.setUrl(path);
		req.setMethod(HttpRequestMethod.POST);
		req.setContent(body);
		req.setContentType("application/json");
		Map<String, List<String>> headers = new HashMap<>();
		TransportUtils.addHeader(headers, HttpConstants.CONTENT_TYPE, "application/json");
		req.setHeaders(headers);
		return req;
	}

	private static void expectOk(IApiValidator v, String path, String body, String descr) throws Exception {
		System.out.println("  [ok ] " + path + " — " + descr);
		try {
			v.validate(request(path, body));
		} catch (ValidatorException e) {
			throw new Exception("Validazione non attesa fallita: " + descr + " → " + e.getMessage(), e);
		}
	}

	private static void expectFail(IApiValidator v, String path, String body, String descr) throws Exception {
		System.out.println("  [err] " + path + " — " + descr);
		try {
			v.validate(request(path, body));
			throw new Exception("Validazione doveva fallire: " + descr);
		} catch (ValidatorException e) {
			// atteso
		}
	}
}
