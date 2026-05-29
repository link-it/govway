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
package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.parametri;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Test sui costrutti introdotti da OpenAPI 3.1 sui parametri (libreria kappa).
 * Esercita l'API {@code OpenAPI31FeaturesParameterSerialization} con i 4 endpoint dedicati:
 * <ul>
 *   <li>{@code /query/nullable_string_new}  -&gt; {@code type: ["string","null"]} (nullable nuovo stile)</li>
 *   <li>{@code /query/const_value}          -&gt; {@code const}</li>
 *   <li>{@code /query/prefixItems_tuple}    -&gt; {@code prefixItems} (tuple validation)</li>
 *   <li>{@code /header/const_header}        -&gt; {@code const} su header</li>
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 */
public class OpenAPI31FeaturesParameterSerializationTest extends ConfigLoader {

	protected String getApiName() {
		return "OpenAPI31FeaturesParameterSerialization";
	}

	// ---- /query/nullable_string_new : type: ["string","null"] ---------------------------------

	@Test public void erogazione_nullable_string_new_ok_value() throws Exception {
		invokeOk(TipoServizio.EROGAZIONE, "GET", queryUrl("nullable_string_new") + "?param=qualsiasi", null);
	}
	@Test public void fruizione_nullable_string_new_ok_value() throws Exception {
		invokeOk(TipoServizio.FRUIZIONE, "GET", queryUrl("nullable_string_new") + "?param=qualsiasi", null);
	}
	@Test public void erogazione_nullable_string_new_ko_missing() throws Exception {
		invokeKo(TipoServizio.EROGAZIONE, "GET", queryUrl("nullable_string_new"), null);
	}
	@Test public void fruizione_nullable_string_new_ko_missing() throws Exception {
		invokeKo(TipoServizio.FRUIZIONE, "GET", queryUrl("nullable_string_new"), null);
	}

	// ---- /query/const_value : const: "VALORE_FISSO" -------------------------------------------

	@Test public void erogazione_const_value_ok() throws Exception {
		invokeOk(TipoServizio.EROGAZIONE, "GET", queryUrl("const_value") + "?param=VALORE_FISSO", null);
	}
	@Test public void fruizione_const_value_ok() throws Exception {
		invokeOk(TipoServizio.FRUIZIONE, "GET", queryUrl("const_value") + "?param=VALORE_FISSO", null);
	}
	@Test public void erogazione_const_value_ko_other() throws Exception {
		invokeKo(TipoServizio.EROGAZIONE, "GET", queryUrl("const_value") + "?param=altro", null);
	}
	@Test public void fruizione_const_value_ko_other() throws Exception {
		invokeKo(TipoServizio.FRUIZIONE, "GET", queryUrl("const_value") + "?param=altro", null);
	}
	@Test public void erogazione_const_value_ko_missing() throws Exception {
		invokeKo(TipoServizio.EROGAZIONE, "GET", queryUrl("const_value"), null);
	}

	// ---- /query/prefixItems_tuple : tuple [integer,string], style=form explode=false -----------

	@Test public void erogazione_prefixItems_tuple_ok() throws Exception {
		invokeOk(TipoServizio.EROGAZIONE, "GET", queryUrl("prefixItems_tuple") + "?param=5,ciao", null);
	}
	@Test public void fruizione_prefixItems_tuple_ok() throws Exception {
		invokeOk(TipoServizio.FRUIZIONE, "GET", queryUrl("prefixItems_tuple") + "?param=5,ciao", null);
	}
	@Test public void erogazione_prefixItems_tuple_ko_wrong_type() throws Exception {
		// primo elemento non integer
		invokeKo(TipoServizio.EROGAZIONE, "GET", queryUrl("prefixItems_tuple") + "?param=foo,bar", null);
	}
	@Test public void erogazione_prefixItems_tuple_ko_too_many() throws Exception {
		// 3 elementi, maxItems=2
		invokeKo(TipoServizio.EROGAZIONE, "GET", queryUrl("prefixItems_tuple") + "?param=5,ciao,extra", null);
	}
	@Test public void erogazione_prefixItems_tuple_ko_missing() throws Exception {
		invokeKo(TipoServizio.EROGAZIONE, "GET", queryUrl("prefixItems_tuple"), null);
	}

	// ---- /header/const_header : const: "HEADER_FISSO" su header X-Const -----------------------

	@Test public void erogazione_const_header_ok() throws Exception {
		invokeOk(TipoServizio.EROGAZIONE, "GET", headerUrl("const_header"), "HEADER_FISSO");
	}
	@Test public void fruizione_const_header_ok() throws Exception {
		invokeOk(TipoServizio.FRUIZIONE, "GET", headerUrl("const_header"), "HEADER_FISSO");
	}
	@Test public void erogazione_const_header_ko_other() throws Exception {
		invokeKo(TipoServizio.EROGAZIONE, "GET", headerUrl("const_header"), "altro");
	}
	@Test public void fruizione_const_header_ko_other() throws Exception {
		invokeKo(TipoServizio.FRUIZIONE, "GET", headerUrl("const_header"), "altro");
	}
	@Test public void erogazione_const_header_ko_missing() throws Exception {
		invokeKo(TipoServizio.EROGAZIONE, "GET", headerUrl("const_header"), null);
	}

	// ---- helpers --------------------------------------------------------------------------------

	private String basePath(TipoServizio tipo) {
		return tipo == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + getApiName() + "/v1"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/" + getApiName() + "/v1";
	}

	private String queryUrl(String path) {
		return "/query/" + path;
	}

	private String headerUrl(String path) {
		return "/header/" + path;
	}

	private void invokeOk(TipoServizio tipo, String method, String relativeUrl, String constHeaderValue) throws Exception {
		HttpResponse response = invoke(tipo, method, relativeUrl, constHeaderValue);
		assertEquals(200, response.getResultHTTPOperation());
	}

	private void invokeKo(TipoServizio tipo, String method, String relativeUrl, String constHeaderValue) throws Exception {
		HttpResponse response = invoke(tipo, method, relativeUrl, constHeaderValue);
		OpenAPI30_Utils.verifyKo(response);
	}

	private HttpResponse invoke(TipoServizio tipo, String method, String relativeUrl, String constHeaderValue) throws Exception {
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.valueOf(method));
		request.setUrl(basePath(tipo) + relativeUrl);
		if (constHeaderValue != null) {
			request.addHeader("X-Const", constHeaderValue);
		}
		return HttpUtilities.httpInvoke(request);
	}
}
