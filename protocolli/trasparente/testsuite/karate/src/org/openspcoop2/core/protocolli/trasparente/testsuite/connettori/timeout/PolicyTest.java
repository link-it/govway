/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.timeout;

import java.util.Optional;

import org.junit.Test;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* PolicyTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PolicyTest extends ConfigLoader {


	/** Policy Negoziazione */
	
	// connectTimeout
	@Test
	public void erogazione_negoziazione_connectTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"negoziazione/connectionTimeout", "connectionTimeout",
				Optional.of("negoziazione.connectionTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaPolicyConnectionTimeout");
	}
	@Test
	public void fruizione_negoziazione_connectTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"negoziazione/connectionTimeout", "connectionTimeout",
				Optional.of("negoziazione.connectionTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaPolicyConnectionTimeout");
	}
	
	// readTimeout
	@Test
	public void erogazione_negoziazione_readTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"negoziazione/readTimeout", "readTimeout",
				Optional.of("negoziazione.readTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaPolicyReadTimeout");
	}
	@Test
	public void fruizione_negoziazione_readTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"negoziazione/readTimeout", "readTimeout",
				Optional.of("negoziazione.readTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaPolicyReadTimeout");
	}
	
	
	
	
	/** Policy Validazione Introspection */
	
	// connectTimeout
	@Test
	public void erogazione_validazione_introspection_connectTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/introspection/connectionTimeout", "connectionTimeout",
				Optional.of("validazione.introspection.connectionTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaPolicyIntrospectionConnectionTimeout");
	}
	@Test
	public void fruizione_validazione_introspection_connectTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/introspection/connectionTimeout", "connectionTimeout",
				Optional.of("validazione.introspection.connectionTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaPolicyIntrospectionConnectionTimeout");
	}
	
	// readTimeout
	@Test
	public void erogazione_validazione_introspection_readTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/introspection/readTimeout", "readTimeout",
				Optional.of("validazione.introspection.readTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaPolicyIntrospectionReadTimeout");
	}
	@Test
	public void fruizione_validazione_introspection_readTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/introspection/readTimeout", "readTimeout",
				Optional.of("validazione.introspection.readTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaPolicyIntrospectionReadTimeout");
	}
	
	
	
	
	/** Policy Validazione UserInfo */
	
	// connectTimeout
	@Test
	public void erogazione_validazione_userInfo_connectTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/userInfo/connectionTimeout", "connectionTimeout",
				Optional.of("validazione.userInfo.connectionTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaPolicyUserInfoConnectionTimeout");
	}
	@Test
	public void fruizione_validazione_userInfo_connectTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/userInfo/connectionTimeout", "connectionTimeout",
				Optional.of("validazione.userInfo.connectionTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaPolicyUserInfoConnectionTimeout");
	}
	
	// readTimeout
	@Test
	public void erogazione_validazione_userInfo_readTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/userInfo/readTimeout", "readTimeout",
				Optional.of("validazione.userInfo.readTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaPolicyUserInfoReadTimeout");
	}
	@Test
	public void fruizione_validazione_userInfo_readTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"validazione/userInfo/readTimeout", "readTimeout",
				Optional.of("validazione.userInfo.readTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaPolicyUserInfoReadTimeout");
	}
	
	
	
	
	
	
	/** AttributeAuthority */
	
	// connectTimeout
	@Test
	public void erogazione_attributeAuthority_connectTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"attributeAuthority/connectionTimeout", "connectionTimeout",
				Optional.of("attributeAuthority.connectionTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaAAConnectionTimeout");
	}
	@Test
	public void fruizione_attributeAuthority_connectTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"attributeAuthority/connectionTimeout", "connectionTimeout",
				Optional.of("attributeAuthority.connectionTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"),
				"TempiRispostaAAConnectionTimeout");
	}
	
	// readTimeout
	@Test
	public void erogazione_attributeAuthority_readTimeout() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"attributeAuthority/readTimeout", "readTimeout",
				Optional.of("attributeAuthority.readTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaAAReadTimeout");
	}
	@Test
	public void fruizione_attributeAuthority_readTimeout() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE).getBytes(),
				"attributeAuthority/readTimeout", "readTimeout",
				Optional.of("attributeAuthority.readTimeout"), // gruppo
				Optional.empty(), // connettore   
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				"TempiRispostaAAReadTimeout");
	}
	
	
	
	
	
	
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, Optional<String> gruppo, Optional<String> connettore, String msgErrore,
			TipoEvento tipoEvento, String descrizioneEvento,
			String policyName) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, gruppo, connettore, msgErrore,
				tipoEvento, descrizioneEvento,
				null, null, false, 
				null,
				policyName);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, Optional<String> gruppo, Optional<String> connettore, String msgErrore,
			TipoEvento tipoEvento, String descrizioneEvento,
			Integer throttlingByte, Integer throttlingMs, boolean throttlingSend,
			String applicativeId,
			String policyName) throws Exception {
		return RestTest._testRest(
				tipoServizio, contentType, content,
				operazione, tipoTest, gruppo, connettore, msgErrore,
				tipoEvento, descrizioneEvento,
				throttlingByte, throttlingMs, throttlingSend,
				applicativeId,
				policyName,
				logCore);
	}
}
