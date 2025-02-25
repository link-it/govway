package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.peer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.config.constants.ServiceBinding;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Classe di utilita per i test sugli headers peer
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PeerUtilities {

	public static final String APPLICATION_MESSAGE_ID_HEADER = "GovWay-Peer-Application-Message-ID";	
	public static final String RELATES_TO_HEADER = "GovWay-Peer-Relates-To";
	public static final String TRANSACTION_ID_HEADER = "GovWay-Peer-Transaction-ID";
	public static final String CONVERSATION_ID_HEADER = "GovWay-Peer-Conversation-ID";
	public static final String MESSAGE_ID_HEADER = "GovWay-Peer-Message-ID";
	public static final String CUSTOM_TRANSACTION_ID_HEADER = "GovWay-Peer-Transaction-ID-Custom";
	public static final String PEER_PRIORITY_HEADER = "GovWay-Peer-Priority";
	public static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Peer-Remaining";
	public static final String RATE_LIMIT_LIMIT_HEADER = "X-RateLimit-Peer-Limit";
	public static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Peer-Reset";

	private static final String TRANSACTION_ID_VALUE = "transactionId";
	private static final String APPLICATION_MESSAGE_ID_VALUE = "applicationMessageId";
	private static final String RELATES_TO_VALUE = "relatesTo";
	private static final String CONVERSATION_ID_VALUE = "conversationId";
	private static final String MESSAGE_ID_VALUE = "messageId";
	private static final String RATE_LIMIT_REMAINING_VALUE = "remaining";
	private static final String RATE_LIMIT_LIMIT_VALUE = "limit";
	private static final String RATE_LIMIT_RESET_VALUE = "reset";

	private static final Map<String, String> HEADER_MAP = Map.ofEntries(
			Map.entry(APPLICATION_MESSAGE_ID_HEADER, APPLICATION_MESSAGE_ID_VALUE),
			Map.entry(RELATES_TO_HEADER, RELATES_TO_VALUE),
			Map.entry(TRANSACTION_ID_HEADER, TRANSACTION_ID_VALUE),
			Map.entry(CONVERSATION_ID_HEADER, CONVERSATION_ID_VALUE),
			Map.entry(MESSAGE_ID_HEADER, MESSAGE_ID_VALUE),
			Map.entry(CUSTOM_TRANSACTION_ID_HEADER, TRANSACTION_ID_VALUE),
			Map.entry(PEER_PRIORITY_HEADER, TRANSACTION_ID_VALUE),
			Map.entry(RATE_LIMIT_REMAINING_HEADER, RATE_LIMIT_REMAINING_VALUE),
			Map.entry(RATE_LIMIT_LIMIT_HEADER, RATE_LIMIT_LIMIT_VALUE),
			Map.entry(RATE_LIMIT_RESET_HEADER, RATE_LIMIT_RESET_VALUE)
	);
	
	private static final Map<String, String> HEADER_OVERWRITE_MAP = Map.ofEntries(
			Map.entry(APPLICATION_MESSAGE_ID_HEADER, APPLICATION_MESSAGE_ID_VALUE),
			Map.entry(RELATES_TO_HEADER, RELATES_TO_VALUE),
			Map.entry(TRANSACTION_ID_HEADER, MESSAGE_ID_VALUE),
			Map.entry(CONVERSATION_ID_HEADER, CONVERSATION_ID_VALUE),
			Map.entry(MESSAGE_ID_HEADER, MESSAGE_ID_VALUE),
			Map.entry(CUSTOM_TRANSACTION_ID_HEADER, TRANSACTION_ID_VALUE),
			Map.entry(PEER_PRIORITY_HEADER, TRANSACTION_ID_VALUE),
			Map.entry(RATE_LIMIT_REMAINING_HEADER, RATE_LIMIT_REMAINING_VALUE),
			Map.entry(RATE_LIMIT_LIMIT_HEADER, RATE_LIMIT_LIMIT_VALUE),
			Map.entry(RATE_LIMIT_RESET_HEADER, RATE_LIMIT_RESET_VALUE)
	);
	
	private PeerUtilities() {
		
	}
	
	/**
	 * Test utilizzato per verificare la feature degli headers peer con i parametri di default.
	 * - verifica che gli headers standard di govway vanegono correttamente inviati
	 * - verifica che gli headers peer descritti da una regexp ritorni tutti e solo
	 *   gli headers matchati  
	 * @param tipoServizio (Erogazione | Fruizione)
	 * @param type (SOAP | REST)
	 * @throws UtilsException
	 */
	public static void testSimple(TipoServizio tipoServizio, ServiceBinding type) throws UtilsException {
		genericTest("/default", tipoServizio, type, HEADER_MAP, new String[]{
				PeerUtilities.TRANSACTION_ID_HEADER,
				PeerUtilities.APPLICATION_MESSAGE_ID_HEADER,
				PeerUtilities.RELATES_TO_HEADER,
				PeerUtilities.CONVERSATION_ID_HEADER,
				PeerUtilities.MESSAGE_ID_HEADER,
				PeerUtilities.RATE_LIMIT_LIMIT_HEADER,
				PeerUtilities.RATE_LIMIT_REMAINING_HEADER,
				PeerUtilities.RATE_LIMIT_RESET_HEADER
		});
	}
	
	/**
	 * Test utilizzato per verificare le proprieta custom di una erogazione/fruizione
	 * - verifica la proprieta che disabilita tutte le regole di default
	 * - verifica nel caso di headers peer definiti come una lista venga ritornato solo
	 *   il primo headers matchato
	 * - verifica che la cache funzioni
	 * @throws UtilsException
	 */
	public static void testCustom(TipoServizio tipoServizio, ServiceBinding type) throws UtilsException {
		
		// carico in le regole regexp in cache
		genericTest("/custom", tipoServizio, type, HEADER_MAP, new String[]{
				PeerUtilities.CUSTOM_TRANSACTION_ID_HEADER,
				PeerUtilities.PEER_PRIORITY_HEADER,
				PeerUtilities.RATE_LIMIT_LIMIT_HEADER,
				PeerUtilities.RATE_LIMIT_REMAINING_HEADER,
				PeerUtilities.RATE_LIMIT_RESET_HEADER
		});
		
		// la cache sara valorizzata le regexp sarnno prese dalla cache
		genericTest("/custom", tipoServizio, type, HEADER_MAP, new String[]{
				PeerUtilities.CUSTOM_TRANSACTION_ID_HEADER,
				PeerUtilities.PEER_PRIORITY_HEADER,
				PeerUtilities.RATE_LIMIT_LIMIT_HEADER,
				PeerUtilities.RATE_LIMIT_REMAINING_HEADER,
				PeerUtilities.RATE_LIMIT_RESET_HEADER
		});
	}
	
	/**
	 * Test utilizzato per verificare che le proprieta dell'erogazione/fruizione sovrascrivono
	 * le proprieta di default
	 * @param tipoServizio (Erogazione | Fruizione)
	 * @param type (SOAP | REST)
	 * @throws UtilsException
	 */
	public static void testOverwrite(TipoServizio tipoServizio, ServiceBinding type) throws UtilsException {
		genericTest("/overwrite", tipoServizio, type, HEADER_OVERWRITE_MAP, new String[]{
				PeerUtilities.TRANSACTION_ID_HEADER,
				PeerUtilities.APPLICATION_MESSAGE_ID_HEADER,
				PeerUtilities.RELATES_TO_HEADER,
				PeerUtilities.CONVERSATION_ID_HEADER,
				PeerUtilities.MESSAGE_ID_HEADER,
				PeerUtilities.RATE_LIMIT_LIMIT_HEADER,
				PeerUtilities.RATE_LIMIT_REMAINING_HEADER,
				PeerUtilities.RATE_LIMIT_RESET_HEADER
		});
	}
	
	
	private static void genericTest(String operazione, TipoServizio tipo, ServiceBinding type, Map<String, String> allHeaders, String[] expectedHeaders) throws UtilsException {
		HttpResponse res = sendRequest(
				type, 
				tipo, 
				operazione, 200);
		checkHeaders(res, allHeaders, expectedHeaders);
	}
	
	private static HttpResponse sendRequest(ServiceBinding type, TipoServizio tipoServizio, String operation, Integer acceptedCode) throws UtilsException {
		
		boolean isSoap = type.equals(ServiceBinding.SOAP);
		
		StringBuilder urlBuilder = new StringBuilder()
				.append(System.getProperty("govway_base_path"));
		if (tipoServizio == TipoServizio.FRUIZIONE)
			urlBuilder.append("/out/SoggettoInternoTestFruitore");
		urlBuilder.append("/SoggettoInternoTest")
			.append("/TestHeadersPeer")
			.append( isSoap ? "Soap" : "Rest")
			.append("/v1")
			.append(operation);
		
		
		HttpRequest request = new HttpRequest();
		request.setUrl(urlBuilder.toString());
		request.setMethod(HttpRequestMethod.POST);

		String contentType = null;
		byte[]content = null;
		
		if(isSoap) {
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, operation.substring(1));
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
			content = Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes();
		
		} else {
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		}
		
		request.setContent(content);
		request.setContentType(contentType);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		String idTransazione = getIdTransazione(response);
		
		ConfigLoader.getLoggerCore().debug("[{}] invio richiesta alla porta {}, code: {}", idTransazione, operation, response.getResultHTTPOperation());
		
		assertNotNull(idTransazione);
		if (acceptedCode != null)
			assertEquals(acceptedCode.intValue(), response.getResultHTTPOperation());
		
		return response;
	}
	
	private static String getIdTransazione(HttpResponse response) {
		return  response.getHeaderFirstValue("GovWay-Transaction-ID");
	}
	
	private static void checkHeaders(HttpResponse res, Map<String, String> allHeaders, String[] expectedHeaders) {
		Set<String> headers = new HashSet<>(allHeaders.keySet());
		
		for (String header : expectedHeaders) {
			assertNotNull("expected header '" + header + "' not present", res.getHeaderValues(header));
			String value = String.join(",", res.getHeaderValues(header));
			assertEquals("expected header '" + header + "'", allHeaders.get(header), value);
			headers.remove(header);
		}
		
		for (String header : headers) {
			assertNull("header '" + header + "' not expected but present", res.getHeaderValues(header));
		}
	}
}
