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
package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.classes.AbstractTracciamentoHandler;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.slf4j.Logger;

import net.minidev.json.JSONObject;

/**
* TracciamentoUtils
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TracciamentoUtils {
	
	public static final String OK = AbstractTracciamentoHandler.OK;
	public static final String FINE = AbstractTracciamentoHandler.FINE;
	
	/**public static final String CREDENZIALE = "GovWay-TestSuite-Credenziale";*/
	
	public static final String REQ_FILE = AbstractTracciamentoHandler.REQ_FILE;
	public static final String RES_FILE = AbstractTracciamentoHandler.RES_FILE;
	public static final String POST_RES_FILE = AbstractTracciamentoHandler.POST_RES_FILE;
	
	public static HttpResponse test(
			Logger logCore,
			TipoServizio tipoServizio,
			String api, String operazione, 
			TracciamentoVerifica tracciamentoVerifica,
			boolean expectedOk,
			String diagnosticoErrore, boolean error, String ... detail) throws Exception {
		
		File request = null;
		File response = null;
		File postResponse = null;
		try {
			request = File.createTempFile("requestCheckTracciamento", ".check");
			response = File.createTempFile("responseCheckTracciamento", ".check");
			postResponse = File.createTempFile("postResponseCheckTracciamento", ".check");
			
			return test(
					request, response, postResponse,
					logCore,
					tipoServizio,
					api, operazione, 
					tracciamentoVerifica,
					expectedOk,
					diagnosticoErrore, error, detail);
			
		}finally {
			FileSystemUtilities.deleteFile(request);
			FileSystemUtilities.deleteFile(response);
			/**FileSystemUtilities.deleteFile(postResponse); LO DEVE ELIMINARE L'HANDLER*/
		}
		
	}
	private static HttpResponse test(
			File requestFile, File responseFile, File postResponseFile,
			Logger logCore,
			TipoServizio tipoServizio,
			String api, String operazione, 
			TracciamentoVerifica tracciamentoVerifica,
			boolean expectedOk,
			String diagnosticoErrore, boolean error, String ... detail) throws Exception {
		
		String apiInvoke = "SoggettoInternoTest/"+api+"/v1";

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+apiInvoke+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+apiInvoke+"/"+operazione;
		url=url+"?test="+operazione;
		if(!tracciamentoVerifica.queryParameters.isEmpty()) {
			for (Map.Entry<String,String> entry : tracciamentoVerifica.queryParameters.entrySet()) {
				url=url+"&"+entry.getKey()+"="+entry.getValue();
			}
		}
		
		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		byte[]content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		request.setContent(content);
		
		request.setUrl(url);
		
		if(tracciamentoVerifica.faseTracciamentoErrore!=null) {
			if(tracciamentoVerifica.faseTracciamentoErroreDB) {
				request.addHeader("GovWay-TestSuite-GenerateFault-TracciamentoDB-Commit", tracciamentoVerifica.faseTracciamentoErrore.name());
			}
			else {
				request.addHeader("GovWay-TestSuite-GenerateFault-TracciamentoFileTrace-Log", tracciamentoVerifica.faseTracciamentoErrore.name());
			}
		}
		
		if(!tracciamentoVerifica.headers.isEmpty()) {
			for (Map.Entry<String,String> entry : tracciamentoVerifica.headers.entrySet()) {
				request.addHeader(entry.getKey(),entry.getValue());
			}
		}
		
		String credenzialeUnivoca = UUID.randomUUID().toString();
		/**request.addHeader(CREDENZIALE,credenzialeUnivoca);*/
		request.setUsername(credenzialeUnivoca);
		request.setPassword("ininfluente");
		
		request.addHeader(REQ_FILE,requestFile.getAbsolutePath());
		request.addHeader(RES_FILE,responseFile.getAbsolutePath());
		request.addHeader(POST_RES_FILE,postResponseFile.getAbsolutePath());
		
		TracciamentoInvoker invoker = new TracciamentoInvoker(request);
		invoker.start();
				
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		
		File checkUno = requestFile;
		boolean skipToPost = false;
		if(tracciamentoVerifica.verificaOutRequest==null || tracciamentoVerifica.verificaOutRequest.getValue()==null) {
			checkUno = postResponseFile;
			skipToPost = true;
		}
		String fileReq = null;
		int limit = 10;
		int offset = 0;
		while(!OK.equals(fileReq) && offset<limit) {
			Utilities.sleep(50+(100*offset));
			logInfo(logCore,"Verifica ok nel file '"+checkUno.getAbsolutePath()+"' (tentativo:"+offset+") ...");
			fileReq = FileSystemUtilities.readFile(checkUno);
			offset++;
		}
		if(offset==limit) {
			throw new Exception("Limit raggiunto per verifica ok nel file '"+checkUno.getAbsolutePath()+"'");
		}

		if(tracciamentoVerifica.verificaInRequest!=null && tracciamentoVerifica.verificaInRequest.getValue()!=null) {
			logInfo(logCore,"Verifica tracciamento "+tracciamentoVerifica.getTipoVerifica()+" su fase '"+FaseTracciamento.IN_REQUEST+"'; expected:"+tracciamentoVerifica.verificaInRequest.getValue());
			if(tracciamentoVerifica.verificaDB) {
				if(tracciamentoVerifica.verificaInRequest.getValue().booleanValue()) {
					DBVerifier.verify(api, operazione, credenzialeUnivoca, esitoExpected, 
							FaseTracciamento.IN_REQUEST, 
							tracciamentoVerifica.checkDiagnostico(FaseTracciamento.IN_REQUEST) ? diagnosticoErrore : null, 
							tracciamentoVerifica.checkLogDetail(FaseTracciamento.IN_REQUEST) ? error : false, 
							tracciamentoVerifica.checkLogDetail(FaseTracciamento.IN_REQUEST) ? detail : null);
				}
				else {
					DBVerifier.verifyNotExpected(api, operazione, credenzialeUnivoca,
							FaseTracciamento.IN_REQUEST);
				}
			}
		}
		
		if(!skipToPost) {
			if(tracciamentoVerifica.sleepAfterInRequest>0) {
				logInfo(logCore,"Serializzo nel file '"+requestFile.getAbsolutePath()+"' (sleep:"+tracciamentoVerifica.sleepAfterInRequest+") ...");
				FileSystemUtilities.writeFile(requestFile, (tracciamentoVerifica.sleepAfterInRequest+"").getBytes());
			}
			else {
				logInfo(logCore,"Serializzo nel file '"+requestFile.getAbsolutePath()+"' ("+FINE+") ...");
				FileSystemUtilities.writeFile(requestFile, FINE.getBytes());
			}
			
			if(tracciamentoVerifica.sleepAfterInRequest>0) {
				Utilities.sleep(tracciamentoVerifica.sleepAfterInRequest);
			}
			
			String fileRes = null;
			offset = 0;
			while(!OK.equals(fileRes) && offset<limit) {
				Utilities.sleep(50+(100*offset));
				logInfo(logCore,"Verifica ok nel file '"+responseFile.getAbsolutePath()+"' (tentativo:"+offset+") ...");
				fileRes = FileSystemUtilities.readFile(responseFile);
				offset++;
			}
			if(offset==limit) {
				throw new Exception("Limit raggiunto per verifica ok nel file '"+responseFile.getAbsolutePath()+"'");
			}
			
			if(tracciamentoVerifica.verificaOutRequest!=null && tracciamentoVerifica.verificaOutRequest.getValue()!=null) {
				logInfo(logCore,"Verifica tracciamento "+tracciamentoVerifica.getTipoVerifica()+" su fase '"+FaseTracciamento.OUT_REQUEST+"'; expected:"+tracciamentoVerifica.verificaOutRequest.getValue());
				if(tracciamentoVerifica.verificaDB) {
					if(tracciamentoVerifica.verificaOutRequest.getValue().booleanValue()) {
						DBVerifier.verify(api, operazione, credenzialeUnivoca, esitoExpected, 
								FaseTracciamento.OUT_REQUEST, 
								tracciamentoVerifica.checkDiagnostico(FaseTracciamento.OUT_REQUEST) ? diagnosticoErrore : null, 
								tracciamentoVerifica.checkLogDetail(FaseTracciamento.OUT_REQUEST) ? error : false, 
								tracciamentoVerifica.checkLogDetail(FaseTracciamento.OUT_REQUEST) ? detail : null);
					}
					else {
						DBVerifier.verifyNotExpected(api, operazione, credenzialeUnivoca,
								FaseTracciamento.OUT_REQUEST);
					}
				}
			}
			
			if(tracciamentoVerifica.sleepAfterOutRequest>0) {
				logInfo(logCore,"Serializzo nel file '"+responseFile.getAbsolutePath()+"' (sleep:"+tracciamentoVerifica.sleepAfterOutRequest+") ...");
				FileSystemUtilities.writeFile(responseFile, (tracciamentoVerifica.sleepAfterOutRequest+"").getBytes());
			}
			else {
				logInfo(logCore,"Serializzo nel file '"+responseFile.getAbsolutePath()+"' ("+FINE+") ...");
				FileSystemUtilities.writeFile(responseFile, FINE.getBytes());
			}
			
			if(tracciamentoVerifica.sleepAfterOutRequest>0) {
				Utilities.sleep(tracciamentoVerifica.sleepAfterOutRequest);
			}
							
			String filePostRes = null;
			offset = 0;
			while(!OK.equals(filePostRes) && offset<limit) {
				Utilities.sleep(50+(100*offset));
				logInfo(logCore,"Verifica ok nel file '"+postResponseFile.getAbsolutePath()+"' (tentativo:"+offset+") ...");
				filePostRes = FileSystemUtilities.readFile(postResponseFile);
				offset++;
			}
			if(offset==limit) {
				throw new Exception("Limit raggiunto per verifica ok nel file '"+postResponseFile.getAbsolutePath()+"'");
			}
			
		}
		
		if(diagnosticoErrore!=null) {
			if(expectedOk) {
				if(tracciamentoVerifica.queryParameters.containsKey("problem")) {
					esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
				}
				else {
					esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK_PRESENZA_ANOMALIE);
				}
			}
			else {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_TRACCIAMENTO);
			}
		}
		else {
			if(tracciamentoVerifica.queryParameters.containsKey("problem")) {
				esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO);
			}
		}
		
		if(tracciamentoVerifica.verificaOutResponse!=null && tracciamentoVerifica.verificaOutResponse.getValue()!=null) {
			logInfo(logCore,"Verifica tracciamento "+tracciamentoVerifica.getTipoVerifica()+" su fase '"+FaseTracciamento.OUT_RESPONSE+"'; expected:"+tracciamentoVerifica.verificaOutResponse.getValue());
			if(tracciamentoVerifica.verificaDB) {
				if(tracciamentoVerifica.verificaOutResponse.getValue().booleanValue()) {
					DBVerifier.verify(api, operazione, credenzialeUnivoca, esitoExpected, 
							FaseTracciamento.OUT_RESPONSE, 
							tracciamentoVerifica.checkDiagnostico(FaseTracciamento.OUT_RESPONSE) ? diagnosticoErrore : null, 
							tracciamentoVerifica.checkLogDetail(FaseTracciamento.OUT_RESPONSE) ? error : false, 
							tracciamentoVerifica.checkLogDetail(FaseTracciamento.OUT_RESPONSE) ? detail : null);
				}
				else {
					DBVerifier.verifyNotExpected(api, operazione, credenzialeUnivoca,
							FaseTracciamento.OUT_RESPONSE);
				}
			}
		}
		
		if(tracciamentoVerifica.sleepAfterOutResponse>0) {
			logInfo(logCore,"Serializzo nel file '"+postResponseFile.getAbsolutePath()+"' (sleep:"+tracciamentoVerifica.sleepAfterOutResponse+") ...");
			FileSystemUtilities.writeFile(postResponseFile, (tracciamentoVerifica.sleepAfterOutResponse+"").getBytes());
		}
		else {
			logInfo(logCore,"Serializzo nel file '"+postResponseFile.getAbsolutePath()+"' ("+FINE+") ...");
			FileSystemUtilities.writeFile(postResponseFile, FINE.getBytes());
		}
				
		invoker.waitShutdown();
		
		HttpResponse response = invoker.getResponse();
		Throwable t = invoker.getT();
		if(t!=null) {
			throw new Exception(t.getMessage(),t);
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
				
		if(diagnosticoErrore!=null && !expectedOk) {
			verifyKo(response, API_UNAVAILABLE, 503, API_UNAVAILABLE_MESSAGE);
		}
		else {
			if(tracciamentoVerifica.queryParameters.containsKey("problem")) {
				verifyOk(response, 500, HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
			}
			else {
				verifyOk(response, 200, contentType);
			}
		}
		
		if(tracciamentoVerifica.faseTracciamentoErrore!=null && tracciamentoVerifica.faseTracciamentoErroreDB) {
			Utilities.sleep(TestTracciamentoCostanti.SLEEP_BEFORE_CHECK_RESOURCES);
			checkRepo(true);
		}
				
		if(tracciamentoVerifica.verificaPostOutResponse!=null && tracciamentoVerifica.verificaPostOutResponse.getValue()!=null) {
			logInfo(logCore,"Verifica tracciamento "+tracciamentoVerifica.getTipoVerifica()+" su fase '"+FaseTracciamento.POST_OUT_RESPONSE+"'; expected:"+tracciamentoVerifica.verificaPostOutResponse.getValue());
			if(tracciamentoVerifica.verificaDB) {
				if(tracciamentoVerifica.verificaPostOutResponse.getValue().booleanValue()) {
					DBVerifier.verify(idTransazione, esitoExpected, 
							FaseTracciamento.POST_OUT_RESPONSE, 
							tracciamentoVerifica.checkDiagnostico(FaseTracciamento.POST_OUT_RESPONSE) ? diagnosticoErrore : null, 
							tracciamentoVerifica.checkLogDetail(FaseTracciamento.POST_OUT_RESPONSE) ? error : false, 
							tracciamentoVerifica.checkLogDetail(FaseTracciamento.POST_OUT_RESPONSE) ? detail : null);
					
					if(tracciamentoVerifica.checkInfo) {
						logInfo(logCore,"Verifica tracciamento info "+tracciamentoVerifica.getTipoVerifica()+" su fase '"+FaseTracciamento.POST_OUT_RESPONSE+"'");
						DBVerifier.verifyInfo(idTransazione, tracciamentoVerifica.mapExpectedTokenInfo, tracciamentoVerifica.tempiElaborazioneExpected);
					}
				}
				else {
					DBVerifier.verifyNotExpected(idTransazione,
							FaseTracciamento.POST_OUT_RESPONSE);
				}
			}
		}
		
		if(tracciamentoVerifica.faseTracciamentoErrore!=null && tracciamentoVerifica.faseTracciamentoErroreDB) {
			Utilities.sleep(TestTracciamentoCostanti.SLEEP_AFTER_CHECK_RESOURCES);
			checkRepo(false);
		}
		
		return response;
		
	}
	
	private static void checkRepo(boolean expectedFiles) throws UtilsException {
		String dir = System.getProperty("fileSystemRecovery.repository");
		File d = new File(dir);
		if(!d.exists()) {
			throw new UtilsException("Repository non trovato");
		}
		
		checkRepo(d, expectedFiles, org.openspcoop2.monitor.engine.constants.Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE);
		checkRepo(d, expectedFiles, org.openspcoop2.monitor.engine.constants.Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DIAGNOSTICO);
		checkRepo(d, expectedFiles, org.openspcoop2.monitor.engine.constants.Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DUMP);
		
	}
	private static void checkRepo(File d, boolean expectedFiles, String nome) throws UtilsException {
		File dirTransazioni = new File(d, nome);
		if(!dirTransazioni.exists()) {
			throw new UtilsException("Repository '"+dirTransazioni+"' non trovato");
		}
		File [] f = dirTransazioni.listFiles();
		if(f==null || f.length<=0) {
			throw new UtilsException("Repository '"+dirTransazioni+"' vuoto? (no dlq?)");
		}
		List<String> files = new ArrayList<>();
		for (File file : f) {
			if(file.isFile()) {
				files.add(file.getName());
			}
		}
		if(expectedFiles) {
			if(files.isEmpty()) {
				throw new UtilsException("Repository '"+dirTransazioni+"' vuoto, attesi file");
			}
		}
		else {
			if(!files.isEmpty()) {
				throw new UtilsException("Repository '"+dirTransazioni+"' atteso vuoto, trovati: "+files);
			}
		}
	}
	
	private static void logInfo(Logger logCore, String msg) {
		logCore.info(msg);
	}
	
	public static void verifyOk(HttpResponse response, int code, String expectedContentType) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(expectedContentType, response.getContentType());
		
	}
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static void verifyKo(HttpResponse response, String error, int code, String errorMsg) throws Exception {
		
		assertEquals(code, response.getResultHTTPOperation());
		
		if(error!=null) {
			assertEquals(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807, response.getContentType());
			
			try {
				JsonPathExpressionEngine jsonPath = new JsonPathExpressionEngine();
				JSONObject jsonResp = JsonPathExpressionEngine.getJSONObject(new String(response.getContent()));
				
				assertEquals("https://govway.org/handling-errors/"+code+"/"+error+".html", jsonPath.getStringMatchPattern(jsonResp, "$.type").get(0));
				assertEquals(error, jsonPath.getStringMatchPattern(jsonResp, "$.title").get(0));
				assertEquals(code, jsonPath.getNumberMatchPattern(jsonResp, "$.status").get(0));
				assertNotEquals(null, jsonPath.getStringMatchPattern(jsonResp, "$.govway_id").get(0));	
				assertEquals(true, jsonPath.getStringMatchPattern(jsonResp, "$.detail").get(0).equals(errorMsg));
				
				assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
	
				if(code==504) {
					assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
				}
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
