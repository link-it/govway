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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_con_notifiche;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale.Common;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla.CommonConsegnaMultipla;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
 * RestNotificheRisposteTest
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RestNotificheRisposteTest extends ConfigLoader{

	private static final String erogazione = "TestConsegnaConNotificheRispostaRest";

	private static final String contentRichiestaTrasformata = "{\n"+
			"  \"claim-26\" : \"TEST ESEMPIO 26\",\n"+
			"  \"claim-25\" : \"TEST ESEMPIO 25\",\n"+
			"  \"claim-28\" : \"TEST ESEMPIO 28\",\n"+
			"  \"claim-27\" : \"TEST ESEMPIO 27\",\n"+
			"  \"claim-22\" : \"TEST ESEMPIO 22\",\n"+
			"  \"claim-21\" : \"TEST ESEMPIO 21\",\n"+
			"  \"claim-24\" : \"TEST ESEMPIO 24\",\n"+
			"  \"claim-23\" : \"TEST ESEMPIO 23\",\n"+
			"  \"claim-29\" : \"TEST ESEMPIO 29\",\n"+
			"  \"claim-20\" : \"TEST ESEMPIO 20\",\n"+
			"  \"claim-15\" : \"TEST ESEMPIO 15\",\n"+
			"  \"claim-14\" : \"TEST ESEMPIO 14\",\n"+
			"  \"claim-17\" : \"TEST ESEMPIO 17\",\n"+
			"  \"claim-16\" : \"TEST ESEMPIO 16\",\n"+
			"  \"claim-11\" : \"TEST ESEMPIO 11\",\n"+
			"  \"claim-10\" : \"TEST ESEMPIO 10\",\n"+
			"  \"claim-32\" : \"TEST ESEMPIO 32\",\n"+
			"  \"claim-13\" : \"TEST ESEMPIO 13\",\n"+
			"  \"claim-12\" : \"TEST ESEMPIO 12\",\n"+
			"  \"claim-19\" : \"TEST ESEMPIO 19\",\n"+
			"  \"claim-18\" : \"TEST ESEMPIO 18\",\n"+
			"  \"claim-1nuovo\" : \"vecchioValore_TEST ESEMPIO 1\",\n"+
			"  \"claim-9\" : \"TEST ESEMPIO 9\",\n"+
			"  \"claim-5\" : \"TEST ESEMPIO 5\",\n"+
			"  \"claim-6\" : \"TEST ESEMPIO 6\",\n"+
			"  \"claim-7\" : \"TEST ESEMPIO 7\",\n"+
			"  \"claim-31\" : \"TEST ESEMPIO 31\",\n"+
			"  \"claim-8\" : \"TEST ESEMPIO 8\",\n"+
			"  \"claim-30\" : \"TEST ESEMPIO 30\",\n"+
			"  \"claim-2\" : \"TEST ESEMPIO 2\",\n"+
			"  \"claim-3\" : \"TEST ESEMPIO 3\",\n"+
			"  \"claim-4\" : \"TEST ESEMPIO 4\"\n"+
			"}";
	
	private static final String contentRispostaTrasformata = "<ns2:Test xmlns:ns2=\"http://govway.org/example\">\n"+
		"\n"+
		"\n"+
		"<xmlFragment3>TEST ESEMPIO 3</xmlFragment3>\n"+
		"<xmlFragment4>TEST ESEMPIO 4</xmlFragment4>\n"+
		"<xmlFragment5>TEST ESEMPIO 5</xmlFragment5>\n"+
		"<xmlFragment6>TEST ESEMPIO 6</xmlFragment6>\n"+
		"<xmlFragment7>TEST ESEMPIO 7</xmlFragment7>\n"+
		"<xmlFragment8>TEST ESEMPIO 8</xmlFragment8>\n"+
		"<xmlFragment9>TEST ESEMPIO 9</xmlFragment9>\n"+
		"<xmlFragment10>TEST ESEMPIO 10</xmlFragment10>\n"+
		"<xmlFragment11>TEST ESEMPIO 11</xmlFragment11>\n"+
		"<xmlFragment12>TEST ESEMPIO 12</xmlFragment12>\n"+
		"<xmlFragment13>TEST ESEMPIO 13</xmlFragment13>\n"+
		"<xmlFragment14>TEST ESEMPIO 14</xmlFragment14>\n"+
		"<xmlFragment15>TEST ESEMPIO 15</xmlFragment15>\n"+
		"<xmlFragment16>TEST ESEMPIO 16</xmlFragment16>\n"+
		"<xmlFragment17>TEST ESEMPIO 17</xmlFragment17>\n"+
		"<xmlFragment18>TEST ESEMPIO 18</xmlFragment18>\n"+
		"<xmlFragment19>TEST ESEMPIO 19</xmlFragment19>\n"+
		"<xmlFragment20>TEST ESEMPIO 20</xmlFragment20>\n"+
		"<xmlFragment21>TEST ESEMPIO 21</xmlFragment21>\n"+
		"<xmlFragment22>TEST ESEMPIO 22</xmlFragment22>\n"+
		"<xmlFragment23>TEST ESEMPIO 23</xmlFragment23>\n"+
		"\n"+
		"<xmlFragment1-nuovo>vecchioValore_TEST ESEMPIO 1</xmlFragment1-nuovo><xmlFragmentHeaderRequest>ResponseValoreEsempioPerHeader</xmlFragmentHeaderRequest><xmlFragment2-nuovo>vecchioValore_TEST ESEMPIO 2</xmlFragment2-nuovo><xmlFragmentHeaderResponse>ResponseValoreEsempioPerHeader</xmlFragmentHeaderResponse></ns2:Test>";
	
	private static final String contentRichiestaRispostaTrasformata = "{\n"+
		"	\"request-claim\": \"TEST ESEMPIO 1\",\n"+
		"	\"response-claim\": \"TEST ESEMPIO 2\"\n"+
		"}\n";
	
	private static byte[] responseXml = null;
	private static File responseXmlFile = null;
	
	private static File _cartellaFiles = null;
	
	@BeforeClass
	public static void Before() {
		Common.fermaRiconsegne(dbUtils);
		_cartellaFiles = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(!_cartellaFiles.exists()) {
			_cartellaFiles.mkdir();
		}
		if (!_cartellaFiles.isDirectory()|| !_cartellaFiles.canWrite()) {
			throw new RuntimeException("E' necessario creare la cartella per scrivere le richieste dei connettori, indicata dalla poprietà: <connettori.consegna_multipla.connettore_file.path> ");
		}
		
		try {
			responseXml = Bodies.getXML(Bodies.SMALL_SIZE).getBytes();
			responseXmlFile = File.createTempFile("response_", "xml", _cartellaFiles);
			FileSystemUtilities.writeFile(responseXmlFile, responseXml);
		}catch(Exception e) {
			throw new RuntimeException("Creazione risposta xml non riuscita");
		}
	}
	
	@AfterClass
	public static void After() {
		Common.fermaRiconsegne(dbUtils);
		File cartellaRisposte = CommonConsegnaMultipla.connettoriFilePath.toFile();
		if(cartellaRisposte.exists() && cartellaRisposte.isDirectory() && cartellaRisposte.canWrite()) {
			FileSystemUtilities.emptyDir(cartellaRisposte);
		}
		
		try {
			responseXmlFile.delete();
		}catch(Exception e) {
			throw new RuntimeException("Eliminazione risposta xml non riuscita");
		}
	}

	@org.junit.After
	public void AfterEach() {
		Common.fermaRiconsegne(dbUtils);
	}
	
	
	private static String buildCondition(String nomeColonna, boolean isNull) {
		TipiDatabase db = ConfigLoader.getDbUtils().tipoDatabase;
		if(db!=null && TipiDatabase.ORACLE.equals(db)) {
			if(isNull) {
				return "("+nomeColonna+" IS NULL)";
			}
			else {
				return "("+nomeColonna+" IS NOT NULL)";
			}
		}
		else {
			if(isNull) {
				return "("+nomeColonna+" IS NULL OR "+nomeColonna+" = '')";
			}
			else {
				return "("+nomeColonna+" IS NOT NULL AND "+nomeColonna+" != '')";
			}
		}
	}
	
	public static void checkRuntime(String msgId, boolean expectedRequest, String expectedRequestContentType, 
			boolean expectedResponse, String expectedResponseContentType) throws Exception {
		checkRuntime(msgId, expectedRequest, expectedRequestContentType, 
				expectedResponse, expectedResponseContentType,
				false);
	}
	public static void checkRuntime(String msgId, boolean expectedRequest, String expectedRequestContentType, 
			boolean expectedResponse, String expectedResponseContentType,
			boolean expectedTransactionContext) throws Exception {
		
		String query = "select CONTENT_TYPE, RESPONSE_CONTENT_TYPE from DEFINIZIONE_MESSAGGI where ID_MESSAGGIO='"+msgId+"' AND TIPO='INBOX'";
		if(expectedRequest) {
			query+=" AND "+buildCondition("MSG_BYTES", false)+" AND "+buildCondition("MSG_CONTEXT", false)+"";
		}
		else {
			query+=" AND "+buildCondition("MSG_BYTES", true)+"";
		}
		if(expectedResponse) {
			query+=" AND "+buildCondition("RESPONSE_MSG_BYTES", false)+" AND "+buildCondition("RESPONSE_MSG_CONTEXT", false)+"";
		}
		else {
			query+=" AND "+buildCondition("RESPONSE_MSG_BYTES", true)+" AND "+buildCondition("RESPONSE_MSG_CONTEXT", true)+"";
		}
		if(expectedTransactionContext) {
			query+=" AND "+buildCondition("TRANSACTION_CONTEXT", false)+"";
		}
		else {
			query+=" AND "+buildCondition("TRANSACTION_CONTEXT", true)+"";
		}
		ConfigLoader.getLoggerCore().info("Checking stato messaggi runtime:  " + msgId);
		ConfigLoader.getLoggerCore().info("Query: " + query);
		Map<String, Object> map = null;
		try {		
			List<Map<String, Object>> readRows = ConfigLoader.getDbUtils().readRows(query);
			if(readRows==null || readRows.isEmpty()) {
				
				if(expectedTransactionContext) {
					for (int i = 0; i < 50; i++) {
						Utilities.sleep(200); // Aspetto PostOutResponseHandler
						readRows = ConfigLoader.getDbUtils().readRows(query);
						if(readRows!=null && !readRows.isEmpty()) {
							break;
						}
					}
					if(readRows==null || readRows.isEmpty()) {
						throw new Exception("Attesa riga con id '"+msgId+"'");
					}
				}
				else {
					throw new Exception("Attesa riga con id '"+msgId+"'");
				}
			}
			if(readRows.size()!=1) {
				throw new Exception("Attesa 1 riga con id '"+msgId+"' (trovate: "+readRows.size()+")");
			}
			
			map = readRows.get(0);
			if(map==null || map.isEmpty()) {
				throw new Exception("Attesa riga con id '"+msgId+"'");
			}
			if(map.size()!=2) {
				throw new Exception("Attese 2 info per l'id '"+msgId+"' (trovate: "+map.size()+")");
			}
		}catch(Exception e) {
			
			String queryDebug = "select CONTENT_TYPE, RESPONSE_CONTENT_TYPE, MSG_BYTES, RESPONSE_MSG_BYTES , TRANSACTION_CONTEXT from DEFINIZIONE_MESSAGGI where ID_MESSAGGIO='"+msgId+"' AND TIPO='INBOX'";
			ConfigLoader.getLoggerCore().info("Checking DEBUG prima di sollevare errore per stato messaggi runtime:  " + msgId);
			ConfigLoader.getLoggerCore().info("Query DEBUG: " + queryDebug);
			List<Map<String, Object>> readRows = ConfigLoader.getDbUtils().readRows(queryDebug);
			if(readRows==null || readRows.isEmpty()) {
				ConfigLoader.getLoggerCore().info("Query DEBUG: no results");
			}
			else {
				for (int i = 0; i < readRows.size(); i++) {
					map = readRows.get(i);
					StringBuilder sb = new StringBuilder("[Raw-"+i+"] ");
					sb.append("CONTENT_TYPE='").append(map.get("CONTENT_TYPE")).append("' ");
					sb.append("RESPONSE_CONTENT_TYPE='").append(map.get("RESPONSE_CONTENT_TYPE")).append("' ");
					sb.append("MSG_BYTES='").append(map.get("MSG_BYTES")!=null).append("' ");
					sb.append("RESPONSE_MSG_BYTES='").append(map.get("RESPONSE_MSG_BYTES")!=null).append("' ");
					sb.append("TRANSACTION_CONTEXT='").append(map.get("TRANSACTION_CONTEXT")!=null).append("' ");
					ConfigLoader.getLoggerCore().info(sb.toString());
				}
			}
			
			throw e;
		}
		
		String ctRequest = null;
		String ctResponse = null;
		for (String s : map.keySet()) {
			if(s.equalsIgnoreCase("content_type")) {
				ctRequest = (String) map.get(s);
			}
			else if(s.equalsIgnoreCase("response_content_type")) {
				ctResponse = (String) map.get(s);
			}
		}
		
		assertEquals(expectedRequest ? expectedRequestContentType : "____EMPTY____", ctRequest);
		
		assertEquals(expectedResponse ? expectedResponseContentType : null, ctResponse);
		
	}

	
	private static HttpRequest buildRestRequest(String erogazione, HttpRequestMethod method, String tipoTest) {
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		
		HttpRequest request = new HttpRequest();
		request.setMethod(method);
		request.setContent(content);
		request.setContentType(contentType);
		request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/" + erogazione + "/v1/test"
				+ "?tipoTest="+tipoTest+"&destFile="+responseXmlFile.getAbsolutePath()+"&destFileContentType="+HttpConstants.CONTENT_TYPE_XML);
		
		return request;
	}
	
	public static void checkFile(File cartellaFiles, String govwayId, String nomeFileAtteso, String contentAtteso, String contentTypeAtteso) throws Exception {
		// Check file attesi
		File fDir = new File(cartellaFiles,govwayId);
		if(!fDir.exists()) {
			throw new IOException("Expected dir '"+fDir.getAbsolutePath()+"'");
		}
		if(!fDir.isDirectory()) {
			throw new IOException("Expected as dir '"+fDir.getAbsolutePath()+"'");
		}
		
		File fContent = new File(fDir,nomeFileAtteso+".bin");
		if(!fContent.exists()) {
			throw new IOException("Expected content '"+fContent.getAbsolutePath()+"'");
		}
		String c = FileSystemUtilities.readFile(fContent);
		if(!c.equals(contentAtteso)) {
			System.out.println("Contenuto diverso da quello atteso");
			System.out.println("Atteso: ["+contentAtteso+"]");
			System.out.println("Ricevuto: ["+c+"]");
		}
		assertEquals(contentAtteso, c);
		
		File fHdr = new File(fDir,nomeFileAtteso+".hdr");
		if(!fHdr.exists()) {
			throw new IOException("Expected header '"+fHdr.getAbsolutePath()+"'");
		}
		String hdr = FileSystemUtilities.readFile(fHdr);

		String ctAtteso = HttpConstants.CONTENT_TYPE+"="+contentTypeAtteso;
		if(!hdr.contains(ctAtteso) && !hdr.contains(ctAtteso.toLowerCase())) {
			throw new IOException("Expected header "+ctAtteso+" in '"+fHdr.getAbsolutePath()+"', found ["+hdr+"]");
		}
		
	}
	
	public static void checkFileZip(File cartellaFiles, String govwayId, String nomeFileAtteso, 
			String contentRequestAtteso, String contentTypeRequestAtteso,
			String contentResponseAtteso, String contentTypeResponseAtteso) throws Exception {
		// Check file attesi
		File fDir = new File(cartellaFiles,govwayId);
		if(!fDir.exists()) {
			throw new IOException("Expected dir '"+fDir.getAbsolutePath()+"'");
		}
		if(!fDir.isDirectory()) {
			throw new IOException("Expected as dir '"+fDir.getAbsolutePath()+"'");
		}
		
		File fContent = new File(fDir,nomeFileAtteso+".bin");
		if(!fContent.exists()) {
			throw new IOException("Expected content '"+fContent.getAbsolutePath()+"'");
		}
		
		File fDirDestZip = new File(fDir,nomeFileAtteso+".zip");
		ZipUtilities.unzipFile(fContent.getAbsolutePath(), fDirDestZip.getAbsolutePath());
		File [] f = fDirDestZip.listFiles();
		if(f==null) {
			throw new IOException("Expected zip content '"+fContent.getAbsolutePath()+"' with 4 files");
		}
		if(f.length!=4) {
			throw new IOException("Expected zip content '"+fContent.getAbsolutePath()+"' with 4 files (founded: "+f.length+")");
		}
		boolean findRequest = false;
		boolean findRequestHdr = false;
		boolean findResponse = false;
		boolean findResponseHdr = false;
		for (File file : f) {
			if("request.bin".equals(file.getName())) {
				findRequest = true;
				String c = FileSystemUtilities.readFile(file);
				if(!c.equals(contentRequestAtteso)) {
					System.out.println("Contenuto richiesta diverso da quello atteso");
					System.out.println("Atteso: ["+contentRequestAtteso+"]");
					System.out.println("Ricevuto: ["+c+"]");
				}
				assertEquals(contentRequestAtteso, c);
			}
			else if("requestHeaders.bin".equals(file.getName())) {
				findRequestHdr=true;
				String hdr = FileSystemUtilities.readFile(file);
		
				String ctAtteso = HttpConstants.CONTENT_TYPE+": "+contentTypeRequestAtteso;
				if(!hdr.contains(ctAtteso) && !hdr.contains(ctAtteso.toLowerCase())) {
					throw new IOException("Expected header "+ctAtteso+" in '"+file.getAbsolutePath()+"', found ["+hdr+"]");
				}
			}
			else if("response.bin".equals(file.getName())) {
				findResponse = true;
				String c = FileSystemUtilities.readFile(file);
				if(!c.equals(contentResponseAtteso)) {
					System.out.println("Contenuto risposta diverso da quello atteso");
					System.out.println("Atteso: ["+contentResponseAtteso+"]");
					System.out.println("Ricevuto: ["+c+"]");
				}
				assertEquals(contentResponseAtteso, c);
			}
			else if("responseHeaders.bin".equals(file.getName())) {
				findResponseHdr=true;
				String hdr = FileSystemUtilities.readFile(file);
		
				String ctAtteso = HttpConstants.CONTENT_TYPE+": "+contentTypeResponseAtteso;
				if(!hdr.contains(ctAtteso) && !hdr.contains(ctAtteso.toLowerCase())) {
					throw new IOException("Expected header "+ctAtteso+" in '"+file.getAbsolutePath()+"', found ["+hdr+"]");
				}
			}
			else {
				throw new IOException("Zip content '"+fContent.getAbsolutePath()+"' with unknown file '"+file.getName()+"'");
			}
		}
		if(!findRequest) {
			throw new IOException("Expected zip content '"+fContent.getAbsolutePath()+"' with 'request.bin' file");
		}
		if(!findRequestHdr) {
			throw new IOException("Expected zip content '"+fContent.getAbsolutePath()+"' with 'requestHeaders.bin' file");
		}
		if(!findResponse) {
			throw new IOException("Expected zip content '"+fContent.getAbsolutePath()+"' with 'response.bin' file");
		}
		if(!findResponseHdr) {
			throw new IOException("Expected zip content '"+fContent.getAbsolutePath()+"' with 'responseHeaders.bin' file");
		}
		
	}
		
	
	@Test
	public void notificaRichiesta() throws Exception {
		 _notificaRichiesta("Richiesta","RichiestaDefault");
	}
	@Test
	public void notificaRichiestaTrasformata() throws Exception {
		 _notificaRichiesta("RichiestaTrasformata", "RichiestaTrasformata");
	}
	public void _notificaRichiesta(String nomeConnettore, String tag) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, tag);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				true, request1.getContentType(), 
				false, null);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(request1.getContent());
		String nomeFile = "richiesta";
		if("RichiestaTrasformata".equals(nomeConnettore)) {
			nomeFile = "richiestaTrasformata";
			contentAtteso = contentRichiestaTrasformata;
		}
		String contentTypeAtteso = request1.getContentType();
		checkFile(_cartellaFiles,govwayId, nomeFile, contentAtteso, contentTypeAtteso);
		
	}
	
	@Test
	public void notificaRichiestaCheckMetodoHTTPDefault() throws Exception {
		notificaRichiestaVerificaHttpMethod(null, "RichiestaCheckMetodoHttpDefault");
	}
	
	@Test
	public void notificaRichiestaMetodoPUT() throws Exception {
		notificaRichiestaVerificaHttpMethod(HttpRequestMethod.PUT, "RichiestaMetodoPUT");
	}
	
	private void notificaRichiestaVerificaHttpMethod(HttpRequestMethod httpRequestMethodExpected, String nomeConnettore) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		
		List<HttpRequestMethod> metodi = new ArrayList<HttpRequestMethod>();
		metodi.add(HttpRequestMethod.POST);
		metodi.add(HttpRequestMethod.PUT);
		for (HttpRequestMethod httpRequestMethod : metodi) {
			
			HttpRequest request1 = buildRestRequest(erogazione, httpRequestMethod, nomeConnettore);
	
			var responses = Common.makeParallelRequests(request1, 1);
			HttpResponse res = responses.get(0);
			String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
			assertNotNull(govwayId);
			String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
			assertNotNull(messageId);
			
			// CheckRuntime
			String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
			checkRuntime(msgId, 
					true, request1.getContentType(), 
					false, null);
			
			// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
			for (var r : responses) {
				assertEquals(200, r.getResultHTTPOperation());
				
				CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
			}
	
			// Attendo la consegna
			List<String> connettoriCheck = new ArrayList<>();
			connettoriCheck.addAll(connettori);
			CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);
	
			// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
			for (var response : responses) {
				CommonConsegnaMultipla.checkConsegnaCompletata(response);
				CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
			}
				
		}
	}
	
	
	
	
	@Test
	public void notificaRichieste() throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add("Richiesta");
		connettori.add("RichiestaCheckMetodoHttpDefault");
		connettori.add("RichiestaMetodoPUT");
		connettori.add("RichiestaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "Richiesta");

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				true, request1.getContentType(), 
				false, null);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(request1.getContent());
		String contentTypeAtteso = request1.getContentType();
		checkFile(_cartellaFiles,govwayId, "richiesta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = contentRichiestaTrasformata;
		checkFile(_cartellaFiles,govwayId, "richiestaTrasformata", contentAtteso, contentTypeAtteso);
		
	}
	
	
	
	
	
	@Test
	public void notificaRisposta() throws Exception {
		_notificaRisposta("Risposta","RispostaDefault");
	}
	@Test
	public void notificaRispostaTrasformata() throws Exception {
		_notificaRisposta("RispostaTrasformata", "RispostaTrasformata");
	}
	public void _notificaRisposta(String nomeConnettore, String tag) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, tag);
		
		request1.addHeader("GovWay-TestSuite-ReqID", "RequestValoreEsempioPerHeader");

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				false, null, 
				true, HttpConstants.CONTENT_TYPE_XML);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(responseXml);
		String nomeFile = "risposta";
		if("RispostaTrasformata".equals(nomeConnettore)) {
			nomeFile = "rispostaTrasformata";
			contentAtteso = contentRispostaTrasformata;
		}
		String contentTypeAtteso = HttpConstants.CONTENT_TYPE_XML;
		checkFile(_cartellaFiles,govwayId, nomeFile, contentAtteso, contentTypeAtteso);
		
	}
	

	
	@Test
	public void notificaRispostaCheckMetodoHTTPDefault() throws Exception {
		notificaRispostaVerificaHttpMethod(null, "RispostaCheckMetodoHttpDefault");
	}
	
	@Test
	public void notificaRispostaMetodoPUT() throws Exception {
		notificaRispostaVerificaHttpMethod(HttpRequestMethod.PUT, "RispostaMetodoPUT");
	}
	
	private void notificaRispostaVerificaHttpMethod(HttpRequestMethod httpRequestMethodExpected, String nomeConnettore) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		
		List<HttpRequestMethod> metodi = new ArrayList<HttpRequestMethod>();
		metodi.add(HttpRequestMethod.POST);
		metodi.add(HttpRequestMethod.PUT);
		for (HttpRequestMethod httpRequestMethod : metodi) { // la risposta è indipendente dalla richiesta
			
			HttpRequest request1 = buildRestRequest(erogazione, httpRequestMethod, nomeConnettore);
	
			var responses = Common.makeParallelRequests(request1, 1);
			HttpResponse res = responses.get(0);
			String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
			assertNotNull(govwayId);
			String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
			assertNotNull(messageId);
			
			// CheckRuntime
			String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
			checkRuntime(msgId, 
					false, null, 
					true, HttpConstants.CONTENT_TYPE_XML);
			
			// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
			for (var r : responses) {
				assertEquals(200, r.getResultHTTPOperation());
				
				CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
			}
	
			// Attendo la consegna
			List<String> connettoriCheck = new ArrayList<>();
			connettoriCheck.addAll(connettori);
			CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);
	
			// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
			for (var response : responses) {
				CommonConsegnaMultipla.checkConsegnaCompletata(response);
				CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
			}
				
		}
	}
	
	
	
	
	
	@Test
	public void notificaRisposte() throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add("Risposta");
		connettori.add("RispostaCheckMetodoHttpDefault");
		connettori.add("RispostaMetodoPUT");
		connettori.add("RispostaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "Risposta");

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				false, null, 
				true, HttpConstants.CONTENT_TYPE_XML);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String contentAtteso = new String(responseXml);
		String contentTypeAtteso = HttpConstants.CONTENT_TYPE_XML;
		checkFile(_cartellaFiles,govwayId, "risposta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = contentRispostaTrasformata;
		checkFile(_cartellaFiles,govwayId, "rispostaTrasformata", contentAtteso, contentTypeAtteso);
		
	}
	
	
	
	@Test
	public void notificaRichiestaRisposta() throws Exception {
		_notificaRichiestaRisposta("RichiestaRisposta","RichiestaRispostaDefault");
	}
	@Test
	public void notificaRichiestaRispostaTrasformata() throws Exception {
		_notificaRichiestaRisposta("RichiestaRispostaTrasformata", "RichiestaRispostaTrasformata");
	}
	public void _notificaRichiestaRisposta(String nomeConnettore, String tag) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
				
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, tag);

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				true, request1.getContentType(), 
				true, HttpConstants.CONTENT_TYPE_XML);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		if("RichiestaRispostaTrasformata".equals(nomeConnettore)) {	
			String nomeFile = "richiestaRispostaTrasformata";
			String contentAtteso = contentRichiestaRispostaTrasformata;
			String contentTypeAtteso = "application/json; charset=\"UTF-8\"";
			checkFile(_cartellaFiles,govwayId, nomeFile, contentAtteso, contentTypeAtteso);
		}
		else {
			String nomeFile = "richiestaRisposta";
			String contentRichiestaAtteso = new String(request1.getContent());
			String contentRispostaAtteso = new String(responseXml);
			String contentRispostaTypeAtteso = HttpConstants.CONTENT_TYPE_XML;
			checkFileZip(_cartellaFiles,govwayId, nomeFile, 
					contentRichiestaAtteso,request1.getContentType(),
					contentRispostaAtteso, contentRispostaTypeAtteso
					);
		}
		
	}
	
	
	
	
	@Test
	public void notificaRichiestaRispostaCheckMetodoHTTPDefault() throws Exception {
		notificaRichiestaRispostaVerificaHttpMethod(null, "RichiestaRispostaCheckMetodoHttpDefault");
	}
	
	@Test
	public void notificaRichiestaRispostaMetodoPUT() throws Exception {
		notificaRichiestaRispostaVerificaHttpMethod(HttpRequestMethod.PUT, "RichiestaRispostaMetodoPUT");
	}
	
	private void notificaRichiestaRispostaVerificaHttpMethod(HttpRequestMethod httpRequestMethodExpected, String nomeConnettore) throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add(nomeConnettore);
		
		List<HttpRequestMethod> metodi = new ArrayList<HttpRequestMethod>();
		metodi.add(HttpRequestMethod.POST);
		metodi.add(HttpRequestMethod.PUT);
		for (HttpRequestMethod httpRequestMethod : metodi) {
			
			HttpRequest request1 = buildRestRequest(erogazione, httpRequestMethod, nomeConnettore);
	
			var responses = Common.makeParallelRequests(request1, 1);
			HttpResponse res = responses.get(0);
			String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
			assertNotNull(govwayId);
			String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
			assertNotNull(messageId);
			
			// CheckRuntime
			String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
			checkRuntime(msgId, 
					true, request1.getContentType(), 
					true, HttpConstants.CONTENT_TYPE_XML);
			
			// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
			for (var r : responses) {
				assertEquals(200, r.getResultHTTPOperation());
				
				CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, 1));
				CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
			}
	
			// Attendo la consegna
			List<String> connettoriCheck = new ArrayList<>();
			connettoriCheck.addAll(connettori);
			CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);
	
			// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
			for (var response : responses) {
				CommonConsegnaMultipla.checkConsegnaCompletata(response);
				CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
			}
				
		}
	}
	
	
	
	
	
	
	@Test
	public void notificaRichiesteRisposte() throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add("RichiestaRisposta");
		connettori.add("RichiestaRispostaCheckMetodoHttpDefault");
		connettori.add("RichiestaRispostaMetodoPUT");
		connettori.add("RichiestaRispostaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "RichiestaRisposta");

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				true, request1.getContentType(), 
				true, HttpConstants.CONTENT_TYPE_XML);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		// Check file attesi
		String nomeFile = "richiestaRispostaTrasformata";
		String contentAtteso = contentRichiestaRispostaTrasformata;
		String contentTypeAtteso = "application/json; charset=\"UTF-8\"";
		checkFile(_cartellaFiles,govwayId, nomeFile, contentAtteso, contentTypeAtteso);

		nomeFile = "richiestaRisposta";
		String contentRichiestaAtteso = new String(request1.getContent());
		String contentRispostaAtteso = new String(responseXml);
		String contentRispostaTypeAtteso = HttpConstants.CONTENT_TYPE_XML;
		checkFileZip(_cartellaFiles,govwayId, nomeFile, 
				contentRichiestaAtteso,request1.getContentType(),
				contentRispostaAtteso, contentRispostaTypeAtteso
				);
		
	}
	
	
	
	
	@Test
	public void all() throws Exception {
		/**
		 * Si controlla lo stato sul db prima e dopo la consegna delle notifiche.
		 */
		
		Set<String> connettori = new HashSet<String>();
		connettori.add("Richiesta");
		connettori.add("RichiestaCheckMetodoHttpDefault");
		connettori.add("RichiestaMetodoPUT");
		connettori.add("RichiestaTrasformata");
		connettori.add("Risposta");
		connettori.add("RispostaCheckMetodoHttpDefault");
		connettori.add("RispostaMetodoPUT");
		connettori.add("RispostaTrasformata");
		connettori.add("RichiestaRisposta");
		connettori.add("RichiestaRispostaCheckMetodoHttpDefault");
		connettori.add("RichiestaRispostaMetodoPUT");
		connettori.add("RichiestaRispostaTrasformata");
		HttpRequest request1 = buildRestRequest(erogazione, HttpRequestMethod.POST, "All");

		var responses = Common.makeParallelRequests(request1, 1);
		HttpResponse res = responses.get(0);
		String govwayId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Transaction-ID");
		assertNotNull(govwayId);
		String messageId = TransportUtils.getFirstValue(res.getHeadersValues(), "GovWay-Message-ID");
		assertNotNull(messageId);
		
		// CheckRuntime
		String msgId = CostantiPdD.PREFIX_MESSAGGIO_CONNETTORE_MULTIPLO+0+CostantiPdD.SEPARATOR_MESSAGGIO_CONNETTORE_MULTIPLO+messageId;
		checkRuntime(msgId, 
				true, request1.getContentType(), 
				true, HttpConstants.CONTENT_TYPE_XML);
		
		// Devono essere state create le tracce sul db ma non ancora fatta nessuna consegna
		for (var r : responses) {
			assertEquals(200, r.getResultHTTPOperation());
			
			CommonConsegnaMultipla.withBackoff( () -> CommonConsegnaMultipla.checkPresaInConsegna(r, connettori.size()));
			CommonConsegnaMultipla.checkSchedulingConnettoreIniziato(r, connettori);	
		}

		// Attendo la consegna
		List<String> connettoriCheck = new ArrayList<>();
		connettoriCheck.addAll(connettori);
		CommonConsegnaMultipla.waitConsegna(responses, connettoriCheck);

		// Per i connettori di tipo file, controllo anche che la scrittura della richiesta di consegna multipla sia avvenuta.
		for (var response : responses) {
			CommonConsegnaMultipla.checkConsegnaCompletata(response);
			CommonConsegnaMultipla.checkSchedulingConnettoriCompletato(response,  connettori, CommonConsegnaMultipla.ESITO_OK, 200, "", "");
		}

		
		// Check file attesi per Richiesta
		String contentAtteso = new String(request1.getContent());
		String contentTypeAtteso = request1.getContentType();
		checkFile(_cartellaFiles,govwayId, "richiesta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = contentRichiestaTrasformata;
		checkFile(_cartellaFiles,govwayId, "richiestaTrasformata", contentAtteso, contentTypeAtteso);
		
		
		// Check file attesi per Risposta
		contentAtteso = new String(responseXml);
		contentTypeAtteso = HttpConstants.CONTENT_TYPE_XML;
		checkFile(_cartellaFiles,govwayId, "risposta", contentAtteso, contentTypeAtteso);
		
		contentAtteso = contentRispostaTrasformata;
		checkFile(_cartellaFiles,govwayId, "rispostaTrasformata", contentAtteso, contentTypeAtteso);
		
		
		// Check file attesi per RichiestaRisposta
		String nomeFile = "richiestaRispostaTrasformata";
		contentAtteso = contentRichiestaRispostaTrasformata;
		contentTypeAtteso = "application/json; charset=\"UTF-8\"";
		checkFile(_cartellaFiles,govwayId, nomeFile, contentAtteso, contentTypeAtteso);

		nomeFile = "richiestaRisposta";
		String contentRichiestaAtteso = new String(request1.getContent());
		String contentRispostaAtteso = new String(responseXml);
		String contentRispostaTypeAtteso = HttpConstants.CONTENT_TYPE_XML;
		checkFileZip(_cartellaFiles,govwayId, nomeFile, 
				contentRichiestaAtteso,request1.getContentType(),
				contentRispostaAtteso, contentRispostaTypeAtteso
				);
		
	}
}
