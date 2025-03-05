package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

public class HttpLibraryTest extends ConfigLoader {
	
	private static final String HTTPCORE_LIBRARY = "core";
	private static final String HTTPCONNECTION_LIBRARY = "UrlConn";
	
	private static final String HTTP_SCHEME = "http";
	private static final String HTTPS_SCHEME = "https";
	
	private static final String API = "TestHttpLibrary";
	
	@Test
	public void testHttpCoreNIO() throws Throwable {
		String id = test(HTTP_SCHEME, HTTPCORE_LIBRARY, true);
		checkLibrary(id, HTTP_SCHEME, HTTPCORE_LIBRARY, true);
	}
	
	@Test
	public void testHttpsCoreNIO() throws Throwable {
		String id = test(HTTPS_SCHEME, HTTPCORE_LIBRARY, true);
		checkLibrary(id, HTTPS_SCHEME, HTTPCORE_LIBRARY, true);
	}
	
	@Test
	public void testHttpCoreBIO() throws Throwable {
		String id = test(HTTP_SCHEME, HTTPCORE_LIBRARY, false);
		checkLibrary(id, HTTP_SCHEME, HTTPCORE_LIBRARY, false);
	}
	
	@Test
	public void testHttpsCoreBIO() throws Throwable {
		String id = test(HTTPS_SCHEME, HTTPCORE_LIBRARY, false);
		checkLibrary(id, HTTPS_SCHEME, HTTPCORE_LIBRARY, false);
	}
	
	@Test
	public void testHttpUrlConnBIO() throws Throwable {
		String id = test(HTTP_SCHEME, HTTPCONNECTION_LIBRARY, false);
		checkLibrary(id, HTTP_SCHEME, HTTPCONNECTION_LIBRARY, false);
	}
	
	@Test
	public void testHttpsUrlConnBIO() throws Throwable {
		String id = test(HTTPS_SCHEME, HTTPCONNECTION_LIBRARY, false);
		checkLibrary(id, HTTPS_SCHEME, HTTPCONNECTION_LIBRARY, false);
	}
	
	@Test
	public void testHttpUrlConnNIO() throws Throwable {
		String id = test(HTTP_SCHEME, HTTPCONNECTION_LIBRARY, true);
		checkLibrary(id, HTTP_SCHEME, HTTPCORE_LIBRARY, true);
	}
	
	@Test
	public void testHttpsUrlConnNIO() throws Throwable {
		String id = test(HTTPS_SCHEME, HTTPCONNECTION_LIBRARY, true);
		checkLibrary(id, HTTPS_SCHEME, HTTPCORE_LIBRARY, true);
	}
	
	private String test(String scheme, String library, boolean isNIO) throws Throwable {
		String operazione = scheme + library;
		String url = new StringBuilder()
				.append(System.getProperty("govway_base_path"))
				.append(isNIO ? "/in/async" : "")
				.append("/").append("SoggettoInternoTest")
				.append("/").append(API)
				.append("/").append("v1")
				.append("/").append(operazione)
				.toString();

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
				
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
				
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(200, response.getResultHTTPOperation());
		
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
				
		return idTransazione;
	}
	
	private void checkLibrary(String idTransazione, String scheme, String library, boolean isNIO) throws SQLQueryObjectException {
		String msgLibrary = scheme + library + (isNIO ? "-nio" : "");
		checkMsgDiag(idTransazione, "Messaggio applicativo con ID \\[[^\\]]+\\] consegnato al servizio applicativo \\[[^\\]]+\\] mediante connettore \\[" + msgLibrary + "\\] \\([^\\)]+\\) con codice di trasporto: 200");
	}
	
	public static void checkMsgDiag(String idTransazione, String msgRegex) throws SQLQueryObjectException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		query.addSelectField(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
		query.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE + " = ?");
		query.setANDLogicOperator(true);
		
		List<Map<String, Object>> msgs = null;
		int[] timeouts = {100, 250, 500, 2000, 5000};
		int index = 0;
		
		// se non trovo nessun messaggio aspetto che magari govway non ha ancora scritto il messaggio sul db
		while (msgs == null && index < timeouts.length) {
			
			Utilities.sleep(timeouts[index++]); 
			
			msgs = ConfigLoader.getDbUtils().readRows(query.createSQLQuery(), idTransazione);
		}
		assertNotNull(msgs);
		
		ConfigLoader.getLoggerCore().debug("messaggio attesto: {}", msgRegex);
		Pattern pattern = Pattern.compile(msgRegex);
		for (Map<String, Object> msg : msgs) {
			assertTrue(msg.containsKey(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO));
			String message = (String) msg.get(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
			if (pattern.matcher(message).matches())
				return;
		}
		
		assertTrue("nessun messaggio diagnostico corrisponde a quelli cercati", false);
	}
}
