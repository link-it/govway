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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.autenticazione;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* IpRichiedenteTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class IpRichiedenteTest extends ConfigLoader {


	@Test
	public void testBaseSoap11() throws UtilsException, ProtocolException {
		testEngine(MessageType.SOAP_11, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void testBaseSoap12() throws UtilsException, ProtocolException {
		testEngine(MessageType.SOAP_12, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2", "124.2.3.2");
	}
	
	// ====================================================================
	// X-Forwarded-For
	// ====================================================================

	@Test
	public void xForwardedFor_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void xForwardedFor_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void xForwardedFor_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void xForwardedFor_ipv6WithBrackets() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "[2001:db8::1]", "2001:db8::1");
	}
	@Test
	public void xForwardedFor_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void xForwardedFor_multipleIpv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2, 10.0.0.1", "124.2.3.2, 10.0.0.1");
	}
	@Test
	public void xForwardedFor_multipleIpv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2:8080, 10.0.0.1:9090", "124.2.3.2, 10.0.0.1");
	}
	@Test
	public void xForwardedFor_multipleIpv4MixedPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2:8080, 10.0.0.1", "124.2.3.2, 10.0.0.1");
	}
	@Test
	public void xForwardedFor_multipleIpv4Ipv6Mixed() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED_FOR, "124.2.3.2:8080, [2001:db8::1]:4711", "124.2.3.2, 2001:db8::1");
	}

	// ====================================================================
	// Forwarded-For
	// ====================================================================

	@Test
	public void forwardedFor_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED_FOR, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void forwardedFor_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED_FOR, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void forwardedFor_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED_FOR, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void forwardedFor_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED_FOR, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void forwardedFor_multipleIpv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED_FOR, "124.2.3.2:8080, 10.0.0.1:9090", "124.2.3.2, 10.0.0.1");
	}
	@Test
	public void forwardedFor_multipleIpv4Ipv6Mixed() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED_FOR, "124.2.3.2:8080, [2001:db8::1]:4711", "124.2.3.2, 2001:db8::1");
	}

	// ====================================================================
	// X-Forwarded
	// ====================================================================

	@Test
	public void xForwarded_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void xForwarded_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void xForwarded_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void xForwarded_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void xForwarded_multipleIpv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED, "124.2.3.2:8080, 10.0.0.1:9090", "124.2.3.2, 10.0.0.1");
	}
	@Test
	public void xForwarded_multipleIpv4Ipv6Mixed() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_FORWARDED, "124.2.3.2:8080, [2001:db8::1]:4711", "124.2.3.2, 2001:db8::1");
	}

	// ====================================================================
	// Forwarded (RFC 7239)
	// ====================================================================

	@Test
	public void forwarded_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.43", "192.0.2.43");
	}
	@Test
	public void forwarded_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.43:47011", "192.0.2.43");
	}
	@Test
	public void forwarded_ipv6QuotedBracketed() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=\"[2001:db8:cafe::17]\"", "2001:db8:cafe::17");
	}
	@Test
	public void forwarded_ipv6QuotedBracketedWithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=\"[2001:db8:cafe::17]:4711\"", "2001:db8:cafe::17");
	}
	@Test
	public void forwarded_withProtoAndBy() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.60;proto=http;by=203.0.113.43", "192.0.2.60");
	}
	@Test
	public void forwarded_withProtoAndByAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.60:8080;proto=http;by=203.0.113.43", "192.0.2.60");
	}
	@Test
	public void forwarded_multipleEntries() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.43, for=198.51.100.17", "192.0.2.43, 198.51.100.17");
	}
	@Test
	public void forwarded_multipleEntriesWithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.43:8080, for=198.51.100.17:9090", "192.0.2.43, 198.51.100.17");
	}
	@Test
	public void forwarded_multipleEntriesMixedIpv4Ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "for=192.0.2.43:8080, for=\"[2001:db8:cafe::17]:4711\"", "192.0.2.43, 2001:db8:cafe::17");
	}
	@Test
	public void forwarded_caseInsensitive() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.FORWARDED, "For=192.0.2.43:8080", "192.0.2.43");
	}

	// ====================================================================
	// X-Client-IP
	// ====================================================================

	@Test
	public void xClientIp_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLIENT_IP, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void xClientIp_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLIENT_IP, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void xClientIp_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLIENT_IP, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void xClientIp_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLIENT_IP, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void xClientIp_multipleIpv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLIENT_IP, "124.2.3.2:8080, 10.0.0.1:9090", "124.2.3.2, 10.0.0.1");
	}

	// ====================================================================
	// Client-IP
	// ====================================================================

	@Test
	public void clientIp_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLIENT_IP, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void clientIp_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLIENT_IP, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void clientIp_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLIENT_IP, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void clientIp_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLIENT_IP, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void clientIp_multipleIpv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLIENT_IP, "124.2.3.2:8080, 10.0.0.1:9090", "124.2.3.2, 10.0.0.1");
	}

	// ====================================================================
	// X-Cluster-Client-IP
	// ====================================================================

	@Test
	public void xClusterClientIp_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLUSTER_CLIENT_IP, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void xClusterClientIp_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLUSTER_CLIENT_IP, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void xClusterClientIp_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLUSTER_CLIENT_IP, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void xClusterClientIp_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLUSTER_CLIENT_IP, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void xClusterClientIp_multipleIpv4Ipv6Mixed() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.X_CLUSTER_CLIENT_IP, "124.2.3.2:8080, [2001:db8::1]:4711", "124.2.3.2, 2001:db8::1");
	}

	// ====================================================================
	// Cluster-Client-IP
	// ====================================================================

	@Test
	public void clusterClientIp_ipv4() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLUSTER_CLIENT_IP, "124.2.3.2", "124.2.3.2");
	}
	@Test
	public void clusterClientIp_ipv4WithPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLUSTER_CLIENT_IP, "124.2.3.2:8080", "124.2.3.2");
	}
	@Test
	public void clusterClientIp_ipv6() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLUSTER_CLIENT_IP, "2001:db8:85a3:8d3:1319:8a2e:370:7348", "2001:db8:85a3:8d3:1319:8a2e:370:7348");
	}
	@Test
	public void clusterClientIp_ipv6WithBracketsAndPort() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLUSTER_CLIENT_IP, "[2001:db8::1]:8080", "2001:db8::1");
	}
	@Test
	public void clusterClientIp_multipleIpv4Ipv6Mixed() throws UtilsException, ProtocolException {
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				HttpConstants.CLUSTER_CLIENT_IP, "124.2.3.2:8080, [2001:db8::1]:4711", "124.2.3.2, 2001:db8::1");
	}


	// ====================================================================
	// Ordine di priorità degli header
	// ====================================================================

	@Test
	public void priorita_xForwardedFor_over_forwardedFor() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.X_FORWARDED_FOR, "10.1.1.1");
		headers.put(HttpConstants.FORWARDED_FOR, "10.1.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.1.1.1", "10.1.1.1");
	}
	@Test
	public void priorita_forwardedFor_over_xForwarded() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.FORWARDED_FOR, "10.2.1.1");
		headers.put(HttpConstants.X_FORWARDED, "10.2.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.2.1.1", "10.2.1.1");
	}
	@Test
	public void priorita_xForwarded_over_forwarded() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.X_FORWARDED, "10.3.1.1");
		headers.put(HttpConstants.FORWARDED, "for=10.3.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.3.1.1", "10.3.1.1");
	}
	@Test
	public void priorita_forwarded_over_xClientIp() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.FORWARDED, "for=10.4.1.1");
		headers.put(HttpConstants.X_CLIENT_IP, "10.4.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "for=10.4.1.1", "10.4.1.1");
	}
	@Test
	public void priorita_xClientIp_over_clientIp() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.X_CLIENT_IP, "10.5.1.1");
		headers.put(HttpConstants.CLIENT_IP, "10.5.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.5.1.1", "10.5.1.1");
	}
	@Test
	public void priorita_clientIp_over_xClusterClientIp() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.CLIENT_IP, "10.6.1.1");
		headers.put(HttpConstants.X_CLUSTER_CLIENT_IP, "10.6.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.6.1.1", "10.6.1.1");
	}
	@Test
	public void priorita_xClusterClientIp_over_clusterClientIp() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.X_CLUSTER_CLIENT_IP, "10.7.1.1");
		headers.put(HttpConstants.CLUSTER_CLIENT_IP, "10.7.1.2");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.7.1.1", "10.7.1.1");
	}
	@Test
	public void priorita_xForwardedFor_over_all() throws UtilsException, ProtocolException {
		Map<String, String> headers = new HashMap<>();
		headers.put(HttpConstants.X_FORWARDED_FOR, "10.8.1.1");
		headers.put(HttpConstants.FORWARDED_FOR, "10.8.1.2");
		headers.put(HttpConstants.X_FORWARDED, "10.8.1.3");
		headers.put(HttpConstants.FORWARDED, "for=10.8.1.4");
		headers.put(HttpConstants.X_CLIENT_IP, "10.8.1.5");
		headers.put(HttpConstants.CLIENT_IP, "10.8.1.6");
		headers.put(HttpConstants.X_CLUSTER_CLIENT_IP, "10.8.1.7");
		headers.put(HttpConstants.CLUSTER_CLIENT_IP, "10.8.1.8");
		testEngine(MessageType.JSON, TipoServizio.EROGAZIONE, "secret",
				headers, "10.8.1.1", "10.8.1.1");
	}


	// ====================================================================
	// Test Engine
	// ====================================================================

	public static HttpResponse testEngine(MessageType messageType,
			TipoServizio tipoServizio,
			String operazione,
			String nomeHeader, String valoreHeader, String valoreAttesoIndicizzato) throws UtilsException, ProtocolException {

		Map<String, String> clientAddressHeaders = new HashMap<>();
		clientAddressHeaders.put(nomeHeader, valoreHeader);
		return testEngine(messageType, tipoServizio, operazione,
				clientAddressHeaders, valoreHeader, valoreAttesoIndicizzato);
	}

	public static HttpResponse testEngine(MessageType messageType,
			TipoServizio tipoServizio,
			String operazione,
			Map<String, String> clientAddressHeaders,
			String valoreAttesoReale, String valoreAttesoIndicizzato) throws UtilsException, ProtocolException {

		Map<String, String> headers = new HashMap<>(clientAddressHeaders);

		String api = "TestAutenticazioneGateway";

		String contentType=null;
		byte[]content=null;
		if(MessageType.JSON.equals(messageType)) {
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			content=Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
			api = api + "REST";
		}
		else if(MessageType.SOAP_11.equals(messageType)) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			content=Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
			api = api + "SOAP";
			headers.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "test");
		}
		else if(MessageType.SOAP_12.equals(messageType)) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
			content=Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE).getBytes();
			api = api + "SOAP";
		}
		else {
			throw new UtilsException("Tipo ["+messageType+"] non gestito");
		}

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/"+operazione;

		HttpRequest request = new HttpRequest();

		request.setReadTimeout(20000);


		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);

		request.setContent(content);

		request.setUrl(url);

		for (Map.Entry<String,String> entry : headers.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = HttpUtilities.httpInvoke(request);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);

		assertEquals(200, response.getResultHTTPOperation());
		assertEquals(contentType, response.getContentType());

		DBVerifier.verify(idTransazione, esitoExpected);

		DBVerifier.verifyCredenzialeIp(idTransazione,
				valoreAttesoReale, valoreAttesoIndicizzato);

		return response;
	}
}
