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
package org.openspcoop2.core.protocolli.trasparente.testsuite.token.negoziazione;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* NegoziazioneCustomTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class NegoziazioneCustomTest extends ConfigLoader {

	public static final String api_negoziazione = "TestNegoziazioneTokenCustom";
	public static final String api_negoziazione_payload_invertito = "TestNegoziazioneTokenCustomPayloadInvertito";
	
	
	@Test
	public void azure() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		String host = "127.0.0.1";
		headers.put("test_hostname", host);
		headers.put("test_azure_param_2", "vAzureValue2");
		headers.put("test_azure_header", "vTest2");
		long now = DateManager.getTimeMillis();
		now = now + ((1000l)*(120l)); // aggiungo 120 secondi
		now = now / 1000;
		headers.put("test_expires_on", now+"");
		
		String suffix = "/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/azure?resource=https://vault.azure.net&api-version=2019-08-01&azure_param_1=vAzureValue1&azure_param_2=vAzureValue2";
		String endpoint = "http://"+host+":8080"+suffix;
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "azure", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"client_id\":\"5E29463D-71DA-4FE0-8E69-999B57DB23B0\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":"+now+"000",
				"\"expires_on\":\""+now+"\"",
				"\"policy\":\"TestNegoziazioneCustomAzure\"",
				"\"azure_header_1\":\"vTest1\"",
			    "\"azure_header_2\":\"vTest2\"",
			    "\"test_expires_on\":\""+now+"\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_hostname", "hostname-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "azure", headers,
				true,
				"(Errore di Connessione) [EndpointNegoziazioneToken: http://hostname-ERRATO:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/azure?resource=https://vault.azure.net&api-version=2019-08-01&azure_param_1=vAzureValue1&azure_param_2=vAzureValue2] unknown host 'hostname-ERRATO'");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_hostname", host);
		headers.put("test_azure_param_2", "vAzureValue2-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "azure", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_azure_param_2", "vAzureValue2");
		headers.put("test_azure_header", "vTest2-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "azure", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_azure_header", "vTest2");
		NegoziazioneTest._test(logCore, api_negoziazione, "azure", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"client_id\":\"5E29463D-71DA-4FE0-8E69-999B57DB23B0\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":"+now+"000",
				"\"expires_on\":\""+now+"\"",
				"\"policy\":\"TestNegoziazioneCustomAzure\"",
				"\"azure_header_1\":\"vTest1\"",
			    "\"azure_header_2\":\"vTest2\"",
			    "\"test_expires_on\":\""+now+"\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
		// modifico l'header expires_on che finisce anche nella chiave, inserisco un token scaduto per verificare che venga segnalato.
		now = DateManager.getTimeMillis();
		now = now - ((1000l)*(120l)); // levo 120 secondi
		now = now / 1000;
		headers.put("test_expires_on", now+"");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "azure", headers,
				true,
				"Errore avvenuto durante la consegna HTTP: AccessToken expired",
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"client_id\":\"5E29463D-71DA-4FE0-8E69-999B57DB23B0\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":"+now+"000",
				"\"expires_on\":\""+now+"\"",
				"\"policy\":\"TestNegoziazioneCustomAzure\"",
				"\"azure_header_1\":\"vTest1\"",
			    "\"azure_header_2\":\"vTest2\"",
			    "\"test_expires_on\":\""+now+"\"");
	}
	
	
	
	@Test
	public void get() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_get_param", "vGetValue");
		headers.put("test_get_header", "vTestGet");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/get?get_param=vGetValue";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "get", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomGET\"",
				"\"get_header\":\"vTestGet\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_get_param", "vGetValue-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "get", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_get_param", "vGetValue");
		headers.put("test_get_header", "vTestGet-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "get", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_get_header", "vTestGet");
		NegoziazioneTest._test(logCore, api_negoziazione, "get", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomGET\"",
				"\"get_header\":\"vTestGet\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}
	
	
	
	@Test
	public void put() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_put_param", "vPutValue");
		headers.put("test_put_header", "vTestPut");
		headers.put("test_elem", "vCustom2");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/put?put_param=vPutValue";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "put", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPUT\"",
				"\"put_header\":\"vTestPut\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_param", "vPutValue-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "put", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_param", "vPutValue");
		headers.put("test_put_header", "vTestPut-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "put", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_header", "vTestPut");
		headers.put("test_elem", "vCustom2-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "put", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_elem", "vCustom2");
		NegoziazioneTest._test(logCore, api_negoziazione, "put", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPUT\"",
				"\"put_header\":\"vTestPut\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}
	
	
	
	@Test
	public void post_freemarker() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_post_param", "vPostValueFreemarker");
		headers.put("test_post_header", "vTestPostFreemarker");
		headers.put("test_elem", "vCustom2Freemarker");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/post_freemarker?post_param=vPostValueFreemarker&post_freemarker=true";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "post_freemarker", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPOSTFreemarker\"",
				"\"post_header\":\"vTestPostFreemarker\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_post_param", "vPostValueFreemarker-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "post_freemarker", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_post_param", "vPostValueFreemarker");
		headers.put("test_post_header", "vTestPostFreemarker-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "post_freemarker", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_post_header", "vTestPostFreemarker");
		headers.put("test_elem", "vCustom2Freemarker-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "post_freemarker", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_elem", "vCustom2Freemarker");
		NegoziazioneTest._test(logCore, api_negoziazione, "post_freemarker", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPOSTFreemarker\"",
				"\"post_header\":\"vTestPostFreemarker\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}
	
	
	
	
	@Test
	public void put_velocity() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_put_param", "vPutValueVelocity");
		headers.put("test_put_header", "vTestPutVelocity");
		headers.put("test_elem", "vCustom2Velocity");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/put_velocity?put_param=vPutValueVelocity&put_velocity=true";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "put_velocity", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPUTVelocity\"",
				"\"put_header\":\"vTestPutVelocity\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_param", "vPutValueVelocity-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "put_velocity", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_param", "vPutValueVelocity");
		headers.put("test_put_header", "vTestPutVelocity-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "put_velocity", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_header", "vTestPutVelocity");
		headers.put("test_elem", "vCustom2Velocity-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "put_velocity", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_elem", "vCustom2Velocity");
		NegoziazioneTest._test(logCore, api_negoziazione, "put_velocity", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPUTVelocity\"",
				"\"put_header\":\"vTestPutVelocity\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}

	
	
	
	
	
	
	
	@Test
	public void mapping() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_put_param", "vPutValue");
		headers.put("test_put_header", "vTestPut");
		headers.put("test_elem", "vCustom2");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/mapping?put_param=vPutValue";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "mapping", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"refreshToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomMappingResponse\"",
				"\"put_header\":\"vTestPut\"",
				"\"tokenType\":\"example\"",
				"\"scopes\":[\"scope\",\"scope2\"]",
				"\"TESTtoken_type\":\"example\"",
				"\"TESTexpires_in\":\"3600\"",
				"\"TESTaccess_token\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"TESTscope\":\"scope scope2\"",
				"\"TESTrefresh_expires_in\":\"7200\"",
				"\"TESTrefresh_token\":\"2YotnFZFEjr1zCsicMWpAA\"");

		
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_param", "vPutValue-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_param", "vPutValue");
		headers.put("test_put_header", "vTestPut-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_put_header", "vTestPut");
		headers.put("test_elem", "vCustom2-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_elem", "vCustom2");
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"refreshToken\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomMappingResponse\"",
				"\"put_header\":\"vTestPut\"",
				"\"tokenType\":\"example\"",
				"\"scopes\":[\"scope\",\"scope2\"]",
				"\"TESTtoken_type\":\"example\"",
				"\"TESTexpires_in\":\"3600\"",
				"\"TESTaccess_token\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"TESTscope\":\"scope scope2\"",
				"\"TESTrefresh_expires_in\":\"7200\"",
				"\"TESTrefresh_token\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}
	
	
	
	
	@Test
	public void mapping_azure() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		String host = "127.0.0.1";
		headers.put("test_hostname", host);
		headers.put("test_azure_param_2", "vAzureValue2");
		headers.put("test_azure_header", "vTest2");
		long now = DateManager.getTimeMillis();
		now = now + ((1000l)*(120l)); // aggiungo 120 secondi
		now = now / 1000;
		headers.put("test_expires_on", now+"");
		
		String suffix = "/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/mapping_azure?resource=https://vault.azure.net&api-version=2019-08-01&azure_param_1=vAzureValue1&azure_param_2=vAzureValue2";
		String endpoint = "http://"+host+":8080"+suffix;
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione, "mapping_azure", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":"+now+"000",
				"\"policy\":\"TestNegoziazioneCustomMappingAzure\"",
				"\"azure_header_1\":\"vTest1\"",
			    "\"azure_header_2\":\"vTest2\"",
			    "\"test_expires_on\":\""+now+"\"",
			    "\"tokenType\":\"exampleAzure\"",
				"\"scopes\":[\"scope\",\"scopeAzure\"]",
				"\"TESTtoken_type\":\"exampleAzure\"",
				"\"TESTexpires_on\":\""+now+"\"",
				"\"TESTaccess_token\":\"eyJ0eXAAAAAAAAA\"",
				"\"TESTscope\":\"scope scopeAzure\"",
				"\"TESTrefresh_expires_in\":\""+now+"\"",
				"\"TESTrefresh_token\":\"2YotnFZFEjr1zCsicMWpAA\"");
		
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_hostname", "hostname-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping_azure", headers,
				true,
				"(Errore di Connessione) [EndpointNegoziazioneToken: http://hostname-ERRATO:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummy/v1/mapping_azure?resource=https://vault.azure.net&api-version=2019-08-01&azure_param_1=vAzureValue1&azure_param_2=vAzureValue2] unknown host 'hostname-ERRATO'");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_hostname", host);
		headers.put("test_azure_param_2", "vAzureValue2-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping_azure", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_azure_param_2", "vAzureValue2");
		headers.put("test_azure_header", "vTest2-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping_azure", headers,
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("test_azure_header", "vTest2");
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping_azure", headers,
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":"+now+"000",
				"\"policy\":\"TestNegoziazioneCustomMappingAzure\"",
				"\"azure_header_1\":\"vTest1\"",
			    "\"azure_header_2\":\"vTest2\"",
			    "\"test_expires_on\":\""+now+"\"",
			    "\"tokenType\":\"exampleAzure\"",
				"\"scopes\":[\"scope\",\"scopeAzure\"]",
				"\"TESTtoken_type\":\"exampleAzure\"",
				"\"TESTexpires_on\":\""+now+"\"",
				"\"TESTaccess_token\":\"eyJ0eXAAAAAAAAA\"",
				"\"TESTscope\":\"scope scopeAzure\"",
				"\"TESTrefresh_expires_in\":\""+now+"\"",
				"\"TESTrefresh_token\":\"2YotnFZFEjr1zCsicMWpAA\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
		// modifico l'header expires_on che finisce anche nella chiave, inserisco un token scaduto per verificare che venga segnalato.
		now = DateManager.getTimeMillis();
		now = now - ((1000l)*(120l)); // levo 120 secondi
		now = now / 1000;
		headers.put("test_expires_on", now+"");
		
		NegoziazioneTest._test(logCore, api_negoziazione, "mapping_azure", headers,
				true,
				"Errore avvenuto durante la consegna HTTP: AccessToken expired",
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":"+now+"000",
				"\"policy\":\"TestNegoziazioneCustomMappingAzure\"",
				"\"azure_header_1\":\"vTest1\"",
			    "\"azure_header_2\":\"vTest2\"",
			    "\"test_expires_on\":\""+now+"\"",
			    "\"tokenType\":\"exampleAzure\"",
				"\"scopes\":[\"scope\",\"scopeAzure\"]",
				"\"TESTtoken_type\":\"exampleAzure\"",
				"\"TESTexpires_on\":\""+now+"\"",
				"\"TESTaccess_token\":\"eyJ0eXAAAAAAAAA\"",
				"\"TESTscope\":\"scope scopeAzure\"",
				"\"TESTrefresh_expires_in\":\""+now+"\"",
				"\"TESTrefresh_token\":\"2YotnFZFEjr1zCsicMWpAA\"");
	}
	
	
	
	
	
	
	
	
	
	@Test
	public void post_no_payload() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_post_param", "vPostValue");
		headers.put("test_post_header", "vTestPost");
		headers.put("post_bearer_token", "ApplicativoVerificaNegoziazioneTokenCustomBearer");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummyPayloadInvertito/v1/get?post_param=vPostValue";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "get", headers, // l'azione invocata rimane 'get'
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPOSTwithoutPayload\"",
				"\"post_header\":\"vTestPost\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_post_param", "vPostValue-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "get", headers, // l'azione invocata rimane 'get'
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_post_param", "vPostValue");
		headers.put("test_post_header", "vTestPost-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "get", headers, // l'azione invocata rimane 'get'
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_post_header", "vTestPost");
		headers.put("post_bearer_token", "ApplicativoVerificaNegoziazioneTokenCustomBearer-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "get", headers, // l'azione invocata rimane 'get'
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationDeny");
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("post_bearer_token", "ApplicativoVerificaNegoziazioneTokenCustomBearer");
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "get", headers, // l'azione invocata rimane 'get'
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomPOSTwithoutPayload\"",
				"\"post_header\":\"vTestPost\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}
	
	
	
	@Test
	public void delete_freemarker() throws Exception {
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
	
		Map<String, String> headers = new HashMap<>();
		headers.put("test_delete_param", "vDeleteValueFreemarker");
		headers.put("test_delete_header", "vTestDeleteFreemarker");
		headers.put("test_elem", "vCustom2Freemarker");
		headers.put("delete_username", "ApplicativoVerificaNegoziazioneTokenCustomBasic");
		headers.put("delete_password", "123456");
		
		String endpoint = "http://localhost:8080/govway/SoggettoInternoTest/AuthorizationServerCustomCredentialsDummyPayloadInvertito/v1/post_freemarker?delete_param=vDeleteValueFreemarker&delete_freemarker=true";
		
		HttpResponse response = NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomDELETEFreemarker\"",
				"\"delete_header\":\"vTestDeleteFreemarker\"");
		String idRichiestaOriginale = response.getHeaderFirstValue("GovWay-Transaction-ID");

		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_delete_param", "vPostValueFreemarker-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_delete_param", "vDeleteValueFreemarker");
		headers.put("test_delete_header", "vTestDeleteFreemarker-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_delete_header", "vTestDeleteFreemarker");
		headers.put("test_elem", "vCustom2Freemarker-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				true,
				"Connessione terminata con errore (codice trasporto: 403)%AuthorizationContentDeny");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("test_elem", "vCustom2Freemarker");
		headers.put("delete_username", "ApplicativoVerificaNegoziazioneTokenCustomBasic-ERRATO");
		
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				true,
				"Connessione terminata con errore (codice trasporto: 401)%AuthenticationFailed");
		
		// provo a modificare una informazione dinamica, il precedente token salvato in cache non deve essere riutilizzato
		headers.put("delete_username", "ApplicativoVerificaNegoziazioneTokenCustomBasic");
		headers.put("delete_password", "123456-ERRATO");
		
		// NOTA: la password non viene utilizzata come chiave di cache e quindi continua ad utilizzare la precedente invocazione completata con successo
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomDELETEFreemarker\"",
				"\"delete_header\":\"vTestDeleteFreemarker\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
		// provo a modificare una informazione dinamica, ripristinando la configurazione corretta
		headers.put("delete_password", "123456");
		NegoziazioneTest._test(logCore, api_negoziazione_payload_invertito, "post_freemarker", headers, // l'azione invocata rimane 'post_freemarker'
				false,
				null,
				"\"type\":\"retrieved_token\"",
				"\"grantType\":\"custom\"",
				"\"endpoint\":\""+endpoint+"\"",
				"\"accessToken\":\"",
				"\"expiresIn\":",
				"\"policy\":\"TestNegoziazioneCustomDELETEFreemarker\"",
				"\"delete_header\":\"vTestDeleteFreemarker\"",
				"\"transactionId\":\""+idRichiestaOriginale+"\""
				);
		
	}
}