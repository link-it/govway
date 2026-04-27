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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* SSETest
* 
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SSETest extends ConfigLoader {

	private static final String API = "TestSSE";
	private static final String OPERAZIONE_SSE_DEV = "sse.dev";

	private static final int EXPECTED_DURATION_SECONDS = 10;   // ascolta per 20 secondi (più un tempo di tolleranza)
	private static final int EXPECTED_DURATION_SECONDS_TOLERANCE = 4;
    private static final int EXPECTED_EVENTS = 5; // ci aspettiamo in media 2 eventi/sec

	/* Header letto dal connettore di GovWay (Endpoint: https://${header:test-sse-remote-endpoint})
	 * per indirizzare la chiamata o all'endpoint pubblico sse.dev oppure al mock locale.
	 * L'endpoint del connettore impone il prefisso 'https://', quindi nell'header viene
	 * passato solo 'host[:port]/path'. */
	private static final String HEADER_REMOTE_ENDPOINT = "test-sse-remote-endpoint";
	private static final String SSE_DEV_REMOTE_ENDPOINT = "sse.dev/test";

	/* Keystore self-signed gia' usato dagli altri test (HttpProxyThread, plugin/RestTest, ...). */
	private static final String MOCK_KEYSTORE_FILE = "/erogatore.jks";
	private static final String MOCK_KEYSTORE_PASSWORD = "openspcoop";

	private static final String PROP_SERVER = "connettori.opzioni_avanzate.sse.server";
	private static final String PROP_MOCK_PORT = "connettori.opzioni_avanzate.sse.mock.port";
	private static final String SERVER_MOCK = "mock";

	private static SSEDevMockServer sseMockServer;

	private static boolean useMock() {
		/* In jenkins viene sempre usato il mock; altrimenti decide la property
		 * 'connettori.opzioni_avanzate.sse.server' (default 'mock'). */
		if (Utils.isJenkins()) {
			return true;
		}
		return SERVER_MOCK.equalsIgnoreCase(System.getProperty(PROP_SERVER, SERVER_MOCK));
	}

	@BeforeClass
	public static void startSseMockIfNeeded() throws IOException, java.security.GeneralSecurityException {
		if (useMock()) {
			int port = Integer.parseInt(System.getProperty(PROP_MOCK_PORT, "8092"));
			String keystorePath = ConfigLoader.getGovwayCfgKeys() + MOCK_KEYSTORE_FILE;
			sseMockServer = new SSEDevMockServer(port, keystorePath, MOCK_KEYSTORE_PASSWORD);
			sseMockServer.start();
		}
	}

	@AfterClass
	public static void stopSseMockIfNeeded() {
		if (sseMockServer != null) {
			sseMockServer.stop();
			sseMockServer = null;
		}
	}

	private static String getRemoteEndpoint() {
		if (sseMockServer != null) {
			return "127.0.0.1:" + sseMockServer.getPort() + "/test";
		}
		return SSE_DEV_REMOTE_ENDPOINT;
	}

	@Test
	public void testErogazioneSSEdev() throws URISyntaxException, IOException, AssertionError, SQLQueryObjectException  {
		test(TipoServizio.EROGAZIONE,OPERAZIONE_SSE_DEV);
	}

	@Test
	public void testFruizioneSSEdev() throws URISyntaxException, IOException, AssertionError, SQLQueryObjectException  {
		test(TipoServizio.FRUIZIONE,OPERAZIONE_SSE_DEV);
	}
	
	private void test(TipoServizio tipoServizio, String operazione) throws URISyntaxException, IOException, AssertionError, SQLQueryObjectException  {
		
		String urlI = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/in/async/SoggettoInternoTest/"+API+"/v1/"+operazione
				: System.getProperty("govway_base_path") + "/out/async/SoggettoInternoTestFruitore/SoggettoInternoTest/"+API+"/v1/"+operazione;
		
		URL url = new URI(urlI).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Per SSE la connessione deve restare aperta
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", HttpConstants.CONTENT_TYPE_EVENT_STREAM);
        connection.setRequestProperty(HEADER_REMOTE_ENDPOINT, getRemoteEndpoint());
        connection.setReadTimeout(0); // NO timeout (connessione long-lived)
        connection.setConnectTimeout(5000);

        int responseCode = connection.getResponseCode();
        assertEquals("SSE endpoint should return 200 OK", 200, responseCode);

        String idTransazioneHeader = "GovWay-Transaction-ID";
        String idTransazione = connection.getHeaderField(idTransazioneHeader);
        if(idTransazione==null) {
        	idTransazione = connection.getHeaderField(idTransazioneHeader.toLowerCase());
        }
        
        try(InputStreamReader isr = new InputStreamReader(connection.getInputStream());){

        	BufferedReader reader = new BufferedReader(isr);
        	
	        long start = System.currentTimeMillis();
	        int receivedEvents = 0;
	
	        String line;
	        while ((line = reader.readLine()) != null) {
	            // Gli eventi SSE arrivano come "data: <messaggio>"
	            if (line.startsWith("data:")) {
	                receivedEvents++;
	                System.out.println("Received event #" + receivedEvents + ": " + line.substring(5).trim());
	            }
	
	            // Se abbiamo ricevuto tutti gli eventi attesi, esci
	            if (receivedEvents >= EXPECTED_EVENTS) {
	                break;
	            }
	
	            // Se è passato troppo tempo, esci per non bloccare il test
	            long elapsed = (System.currentTimeMillis() - start) / 1000;
	            if (elapsed > EXPECTED_DURATION_SECONDS + EXPECTED_DURATION_SECONDS_TOLERANCE) { // piccolo margine
	                System.err.println("Timeout: troppi pochi eventi ricevuti in tempo utile");
	                break;
	            }
	        }
	
	        // Chiudi la connessione SSE
	        reader.close();
	        connection.disconnect();
	
	        // Verifica che abbiamo ricevuto il numero di eventi atteso
	        System.err.println("Ricevuti "+receivedEvents+" eventi");
	        assertEquals("Numero di eventi SSE ricevuti non corretto", EXPECTED_EVENTS, receivedEvents);
			
        }
        
        int esitoExpected = 0;
        String msgDiagnostico = "Modalità Server-Sent Events (SSE) attivata per la consegna della risposta";
        DBVerifier.verify(idTransazione, esitoExpected, Pattern.compile( Pattern.quote(msgDiagnostico), Pattern.DOTALL), null);

	}
	
}
