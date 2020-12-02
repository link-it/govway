/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest.integrazione;

import java.util.HashMap;

import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * IntegrazioneUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrazioneUtils {


	public static Object[][] getContentTypesOk(){
		return new Object[][]{
			{"application/json"},
			{"text/json; charset=UTF-8"},
			{"text/problem+json;charset=UTF-8"},
			{"text/xml; altro=AAA; charset=UTF-8"},
			{"text/json;charset=\"UTF-8\""},
		};	
	}
	
	public static Object[][] getContentTypesKo(){
		return new Object[][]{
			{"application","In Content-Type string <application>, expected '/', got null"},
			{"/","In Content-Type string </>, expected MIME type, got /"},
			{"application/","In Content-Type string <application/>, expected MIME subtype, got null"},
			{"/json","In Content-Type string </json>, expected MIME type, got /"},
			{"text/json, charset=UTF-8","In parameter list <, charset=UTF-8>, expected ';', got \",\""},
			{"text/problem+json;charsetUTF-8","In parameter list <;charsetUTF-8>, expected '=', got \"null\""}
		};	
	}
	

	public static void testCorrelazioneApplicativaRichiesta(RUOLO ruolo, String nomePorta, String metodo,String path,boolean expectedIdCorrelazione, boolean checkCasoOk) throws TestSuiteException, Exception{
		testCorrelazioneApplicativa(ruolo, nomePorta, metodo, path, expectedIdCorrelazione, false, checkCasoOk);
	}
	public static void testCorrelazioneApplicativaRisposta(RUOLO ruolo, String nomePorta, String metodo,String path,boolean expectedIdCorrelazione, boolean checkCasoOk) throws TestSuiteException, Exception{
		testCorrelazioneApplicativa(ruolo, nomePorta, metodo, path, false, expectedIdCorrelazione, checkCasoOk);
	}
	private static void testCorrelazioneApplicativa(RUOLO ruolo, String nomePorta, String metodo,String path,boolean expectedIdCorrelazioneRichiesta,boolean expectedIdCorrelazioneRisposta,
			boolean checkCasoOk) throws TestSuiteException, Exception{
		
		if(expectedIdCorrelazioneRichiesta && expectedIdCorrelazioneRisposta) {
			throw new Exception("Configurazione non supportata");
		}
		
		String ID_TRANSAZIONE = org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getIDTransazioneTrasporto();
		EsitiProperties esitiProperties = EsitiProperties.getInstance(TrasparenteTestsuiteLogger.getInstance(), CostantiTestSuite.PROTOCOL_NAME);
		
		HttpRequestMethod httpMethod = HttpRequestMethod.valueOf(metodo);
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(httpMethod, ruolo);
		restCore.setPortaApplicativaDelegata(nomePorta);
		
		if(expectedIdCorrelazioneRichiesta || expectedIdCorrelazioneRisposta) {
		
			String tipo = "jsonNoPrefix";
			boolean authenticationError = true;
			boolean badRequest = authenticationError; // sfrutto questo if
			int code = expectedIdCorrelazioneRichiesta ? 400 : 502;
			HttpResponse httpResponse = restCore.invoke(tipo, code, repository, true, path.contains("echo"), 
					false, HttpConstants.CONTENT_TYPE_JSON, false, badRequest,
					null,null,
					path);
			String errorType = httpResponse.getHeader(CostantiTestSuite.ERROR_TYPE);
			Reporter.log(CostantiTestSuite.ERROR_TYPE+"='"+errorType+"'");
			Assert.assertEquals(errorType, 
					expectedIdCorrelazioneRichiesta ? CostantiTestSuite.ERROR_TYPE_APPLICATION_CORRELATION_IDENTIFICATION_REQUEST_FAILED :
						CostantiTestSuite.ERROR_TYPE_INVALID_RESPONSE);
			
			String idTransazione = httpResponse.getHeader(ID_TRANSAZIONE);
			Reporter.log(ID_TRANSAZIONE+"='"+idTransazione+"'");
			Assert.assertNotNull(idTransazione);
			
			DatabaseComponent data = null;
			try{
				if(RUOLO.PORTA_DELEGATA.equals(ruolo)) {
					data = DatabaseProperties.getDatabaseComponentFruitore();
				}
				else {
					data = DatabaseProperties.getDatabaseComponentErogatore();
				}
				
				boolean isTrace = data.getVerificatoreTransazioni().isTraced(idTransazione);
				int intEsito = esitiProperties.convertoToCode(expectedIdCorrelazioneRichiesta ? 
						EsitoTransazioneName.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA :
							EsitoTransazioneName.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA);
				boolean isTraceEsito = data.getVerificatoreTransazioni().isTracedEsito(idTransazione, intEsito);
				Reporter.log("Database transazioni, isTrace"+isTrace+": isTraceEsito("+intEsito+"):"+isTraceEsito+" ...");
				Assert.assertTrue(isTrace);
				Assert.assertTrue(isTraceEsito);
			}finally{
				data.close();
			}
			
		}
		
		if(checkCasoOk) {
			String tipo = "json";
			HashMap<String, String> propertiesRequest = new HashMap<>();
			propertiesRequest.put("correlazioneApplicativa",CostantiTestSuite.ID_CORRELAZIONE_MESSAGGI_JSON);
			HttpResponse httpResponse = restCore.invoke(tipo, 200, repository, true, path.contains("echo"), 
					false, HttpConstants.CONTENT_TYPE_JSON, false, false, 
					null, propertiesRequest,
					path);
			
			String errorType = httpResponse.getHeader(CostantiTestSuite.ERROR_TYPE);
			Reporter.log(CostantiTestSuite.ERROR_TYPE+"='"+errorType+"'");
			Assert.assertNull(errorType);
			
			String idTransazione = httpResponse.getHeader(ID_TRANSAZIONE);
			Reporter.log(ID_TRANSAZIONE+"='"+idTransazione+"'");
			Assert.assertNotNull(idTransazione);
					
			DatabaseComponent data = null;
			try{
				if(RUOLO.PORTA_DELEGATA.equals(ruolo)) {
					data = DatabaseProperties.getDatabaseComponentFruitore();
				}
				else {
					data = DatabaseProperties.getDatabaseComponentErogatore();
				}
				
				boolean isTrace = data.getVerificatoreTransazioni().isTraced(idTransazione);
				int intEsito = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
				boolean isTraceEsito = data.getVerificatoreTransazioni().isTracedEsito(idTransazione, intEsito);
				Reporter.log("Database transazioni id="+idTransazione+", isTrace:"+isTrace+", isTraceEsito("+intEsito+"):"+isTraceEsito+" ...");
				Assert.assertTrue(isTrace);
				Assert.assertTrue(isTraceEsito);
				
				boolean existsCorrelazioneApplicativaRichiesta = data.getVerificatoreTransazioni().existsTracedCorrelazioneApplicativaRichiesta(idTransazione);
				boolean isExpectedCorrelazioneApplicativaRichiesta = data.getVerificatoreTransazioni().isTracedCorrelazioneApplicativaRichiesta(idTransazione, CostantiTestSuite.ID_CORRELAZIONE_MESSAGGI_JSON);
				boolean existsCorrelazioneApplicativaRisposta = data.getVerificatoreTransazioni().existsTracedCorrelazioneApplicativaRisposta(idTransazione);
				boolean isExpectedCorrelazioneApplicativaRisposta = data.getVerificatoreTransazioni().isTracedCorrelazioneApplicativaRisposta(idTransazione, CostantiTestSuite.ID_CORRELAZIONE_MESSAGGI_JSON);
				
				Reporter.log("Database transazioni id="+idTransazione+", existsCorrelazioneApplicativaRichiesta:"+existsCorrelazioneApplicativaRichiesta+", isExpectedCorrelazioneApplicativaRichiesta:"+isExpectedCorrelazioneApplicativaRichiesta+" ...");
				Reporter.log("Database transazioni id="+idTransazione+", existsCorrelazioneApplicativaRisposta:"+existsCorrelazioneApplicativaRisposta+", isExpectedCorrelazioneApplicativaRisposta:"+isExpectedCorrelazioneApplicativaRisposta+" ...");
				if(expectedIdCorrelazioneRichiesta) {
					Assert.assertTrue(existsCorrelazioneApplicativaRichiesta);
					Assert.assertTrue(isExpectedCorrelazioneApplicativaRichiesta);
					Assert.assertFalse(existsCorrelazioneApplicativaRisposta);
					Assert.assertFalse(isExpectedCorrelazioneApplicativaRisposta);
				}
				else if(expectedIdCorrelazioneRisposta) {
					Assert.assertFalse(existsCorrelazioneApplicativaRichiesta);
					Assert.assertFalse(isExpectedCorrelazioneApplicativaRichiesta);
					Assert.assertTrue(existsCorrelazioneApplicativaRisposta);
					Assert.assertTrue(isExpectedCorrelazioneApplicativaRisposta);
				}
				else {
					Assert.assertFalse(existsCorrelazioneApplicativaRichiesta);
					Assert.assertFalse(isExpectedCorrelazioneApplicativaRichiesta);
					Assert.assertFalse(existsCorrelazioneApplicativaRisposta);
					Assert.assertFalse(isExpectedCorrelazioneApplicativaRisposta);
				}
				
			}finally{
				data.close();
			}
		}

	}
}
