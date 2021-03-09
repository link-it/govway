package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

import net.minidev.json.JSONObject;

public class OpenAPI30_Utils {
	
	public static final String INVALID_REQUEST_CONTENT = "InvalidRequestContent";

	public static void verifyKo(HttpResponse response) {
		
		assertEquals(400, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
		
		try {
			JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
			JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
			
			assertEquals("https://govway.org/handling-errors/400/InvalidRequestContent.html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
			assertEquals(INVALID_REQUEST_CONTENT, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
			assertEquals(400, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
			assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
			assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).contains("Request content not conform to API specification"));
			
			assertEquals(INVALID_REQUEST_CONTENT, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
