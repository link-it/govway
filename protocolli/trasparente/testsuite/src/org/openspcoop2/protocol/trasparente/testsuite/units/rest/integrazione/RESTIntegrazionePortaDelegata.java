/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RESTIntegrazionePortaDelegata
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTIntegrazionePortaDelegata {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RESTIntegrazionePortaDelegata";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=RESTIntegrazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=RESTIntegrazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	

	@DataProvider (name="contentTypesOk")
	public Object[][] contentTypesOk(){
		return IntegrazioneUtils.getContentTypesOk();
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".ContentTypesRequestOk"},dataProvider="contentTypesOk")
	public void testContentTypeRequestOk(String contentType) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "json";
		if(contentType.contains("xml")) {
			tipo = "xml";
		}
		restCore.invoke(tipo, 200, repository, true, true, contentType);
		restCore.postInvoke(repository);
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".ContentTypesResponseOk"},dataProvider="contentTypesOk")
	public void testContentTypeResponseOk(String contentType) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "json";
		if(contentType.contains("xml")) {
			tipo = "xml";
		}
		restCore.invoke(tipo, 200, repository, true, true, contentType);
		restCore.postInvoke(repository);
	}
	
	
	@DataProvider (name="contentTypesKo")
	public Object[][] contentTypesKo(){
		return IntegrazioneUtils.getContentTypesKo();
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".ContentTypesRequestKo"},dataProvider="contentTypesKo")
	public void testContentTypeRequestKo(String contentType,String erroreAtteso) throws TestSuiteException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "json";
		boolean authenticationError = true;
		boolean badRequest = authenticationError; // sfrutto questo if
		HttpResponse httpResponse = restCore.invoke(tipo, 400, repository, true, true, 
				false, contentType, false, badRequest,
				null,null);
		String errorType = httpResponse.getHeader(CostantiTestSuite.ERROR_TYPE);
		Reporter.log(CostantiTestSuite.ERROR_TYPE+"='"+errorType+"'");
		Assert.assertEquals(errorType, CostantiTestSuite.ERROR_TYPE_BAD_REQUEST);
		
		DatabaseMsgDiagnosticiComponent dataMsg = null;
		try{
			dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			String diag = "Richiesta non valida: Content-Type '"+contentType+"' presente nella richiesta non valido; "+erroreAtteso;
			Reporter.log("Cerco diagnostico "+diag+" nei log ...");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, diag));		
		}finally{
			dataMsg.close();
		}
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".ContentTypesResponseKo"},dataProvider="contentTypesKo")
	public void testContentTypeResponseKo(String contentType,String erroreAtteso) throws TestSuiteException, Exception{
		Date dataInizioTest = DateManager.getDate();
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "json";
		boolean authenticationError = true;
		boolean badRequest = authenticationError; // sfrutto questo if
		HttpResponse httpResponse = restCore.invoke(tipo, 502, repository, true, true, 
				false, contentType, false, badRequest,
				null,null);
		String errorType = httpResponse.getHeader(CostantiTestSuite.ERROR_TYPE);
		Reporter.log(CostantiTestSuite.ERROR_TYPE+"='"+errorType+"'");
		Assert.assertEquals(errorType, CostantiTestSuite.ERROR_TYPE_INVALID_RESPONSE);

		DatabaseMsgDiagnosticiComponent dataMsg = null;
		try{
			dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			
			String version_jbossas = org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.readApplicationServerVersion();
			if((version_jbossas.startsWith("tomcat")) && "text/problem+json;charsetUTF-8".equals(contentType)) {
				contentType = "text/problem+json; charsetutf-8=";
				erroreAtteso = "In parameter list <; charsetutf-8=>, expected parameter value, got \"null\"";
			}
			
			String diag = "Il contenuto applicativo della risposta ricevuta non è processabile: Non è stato possibile comprendere come trattare il messaggio ricevuto (Content-Type: "+contentType+"): "+erroreAtteso;
			Reporter.log("Cerco diagnostico "+diag+" nei log ...");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, diag));		
		}finally{
			dataMsg.close();
		}
	}
	
	
	
	
	// match solo risorsa POST /service/echo
	
	@DataProvider (name="testCorrelazioneApplicativaPuntuale")
	public Object[][] testCorrelazioneApplicativaPuntuale(){
		return new Object[][]{
			{"POST","/service/echo",true},
			{"PUT","/service/echo",false},
			{"DELETE","/service/echo",false},
		};	
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaPuntuale"},dataProvider="testCorrelazioneApplicativaPuntuale")
	public void testCorrelazioneApplicativaPuntuale(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_PUNTUALE, metodo, path, expectedIdCorrelazione, true);
		
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaPuntualeCaseInsensitive"},dataProvider="testCorrelazioneApplicativaPuntuale")
	public void testCorrelazioneApplicativaPuntualeCaseInsensitive(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_PUNTUALE_CASE_INSENSITIVE, metodo, path, expectedIdCorrelazione, true);
		
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaPuntualeRisposta"},dataProvider="testCorrelazioneApplicativaPuntuale")
	public void testCorrelazioneApplicativaPuntualeRisposta(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_PUNTUALE_RISPOSTA, metodo, path, expectedIdCorrelazione, true);
		
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaPuntualeRispostaCaseInsensitive"},dataProvider="testCorrelazioneApplicativaPuntuale")
	public void testCorrelazioneApplicativaPuntualeRispostaCaseInsensitive(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_PUNTUALE_RISPOSTA_CASE_INSENSITIVE, metodo, path, expectedIdCorrelazione, true);
		
	}
	
	
	// match solo risorsa POST /service/echo, PUT /service/echo e DELETE /service/echo
	
	@DataProvider (name="testCorrelazioneApplicativaQualsiasiMetodo")
	public Object[][] testCorrelazioneApplicativaQualsiasiMetodo(){
		return new Object[][]{
			{"POST","/service/echo",true},
			{"PUT","/service/echo",true},
			{"DELETE","/service/echo",true}, 
			{"GET","/service/echo/23", false},
			{"HEAD","/service/ping",false},
		};	
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaQualsiasiMetodo"},dataProvider="testCorrelazioneApplicativaQualsiasiMetodo")
	public void testCorrelazioneApplicativaQualsiasiMetodo(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		boolean invocaCasoOk = true;
		if("DELETE".equals(metodo)) {
			// non potrà andare a buon fine essendo la regola di correlazione sul contenuto e la DELETE non prevede un payload nella richiesta
			// La semplice verifica che la correlazione applicativa (fallita) è stata applicata è sufficente per il test
			invocaCasoOk = false;
			
			Reporter.log("Force invocaCasoOk=false");
		}
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_QUALSIASI_METODO, metodo, path, expectedIdCorrelazione, invocaCasoOk);
		
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaQualsiasiMetodoRisposta"},dataProvider="testCorrelazioneApplicativaQualsiasiMetodo")
	public void testCorrelazioneApplicativaQualsiasiMetodoRisposta(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_QUALSIASI_METODO_RISPOSTA, metodo, path, expectedIdCorrelazione, true);
		
	}
	
	
	
	// match solo risorsa GET /service/echo/{id}
	
	@DataProvider (name="testCorrelazioneApplicativaGet")
	public Object[][] testCorrelazioneApplicativaGet(){
		return new Object[][]{
			{"GET","/service/echo/23",true},
			{"GET","/service/echo/67altroId",true},
			{"GET","/service/echo/23/details",false},
			{"POST","/service/echo",false},
			{"PUT","/service/echo",false},
			{"DELETE","/service/echo",false},
		};	
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGet"},dataProvider="testCorrelazioneApplicativaGet")
	public void testCorrelazioneApplicativaGet(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET, metodo, path, expectedIdCorrelazione, true);
		
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetStar"},dataProvider="testCorrelazioneApplicativaGet")
	public void testCorrelazioneApplicativaGetStar(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
				
		if(!expectedIdCorrelazione && path.endsWith("details")){
			expectedIdCorrelazione=true; // La porta delegate in questione possiede un path * che matcha anche con ulteriori sotto path
		
			Reporter.log("Force expectedIdCorrelazione");
		}
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_STAR, metodo, path, expectedIdCorrelazione, true);
		
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetRisposta"},dataProvider="testCorrelazioneApplicativaGet")
	public void testCorrelazioneApplicativaGetRisposta(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_RISPOSTA, metodo, path, expectedIdCorrelazione, true);
		
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetStarRisposta"},dataProvider="testCorrelazioneApplicativaGet")
	public void testCorrelazioneApplicativaGetStarRisposta(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
				
		if(!expectedIdCorrelazione && path.endsWith("details")){
			expectedIdCorrelazione=true; // La porta delegate in questione possiede un path * che matcha anche con ulteriori sotto path
		
			Reporter.log("Force expectedIdCorrelazione");
		}
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_STAR_RISPOSTA, metodo, path, expectedIdCorrelazione, true);
		
	}
	
	
	// match solo risorsa GET /service/echo/{id} e GET /service/ping/{id}
	
	
	@DataProvider (name="testCorrelazioneApplicativaGetQualsiasi")
	public Object[][] testCorrelazioneApplicativaGetQualsiasi(){
		return new Object[][]{
			{"GET","/service/echo/23",true},
			{"GET","/service/echo/67altroId",true},
			{"GET","/service/echo/23/details",true},
			{"GET","/service/ping/23",true},
			{"GET","/service/ping/82",true},
			{"POST","/service/echo",false},
			{"PUT","/service/echo",false},
			{"DELETE","/service/echo",false},
		};	
	}

	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetQualsiasi"},dataProvider="testCorrelazioneApplicativaGetQualsiasi")
	public void testCorrelazioneApplicativaGetQualsiasi(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_QUALSIASI, metodo, path, expectedIdCorrelazione, true);
		
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetQualsiasiStar"},dataProvider="testCorrelazioneApplicativaGetQualsiasi")
	public void testCorrelazioneApplicativaGetQualsiasiStar(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		IntegrazioneUtils.testCorrelazioneApplicativaRichiesta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_QUALSIASI_STAR, metodo, path, expectedIdCorrelazione, true);
		
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetQualsiasiRisposta"},dataProvider="testCorrelazioneApplicativaGetQualsiasi")
	public void testCorrelazioneApplicativaGetQualsiasiRisposta(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		boolean invocaCasoOk = true;
		if("GET".equals(metodo) && path.contains("/ping/")) {
			// non potrà andare a buon fine essendo la regola di correlazione sul contenuto e l'operazione ping non prevede un payload nella risposta
			// La semplice verifica che la correlazione applicativa (fallita) è stata applicata è sufficente per il test
			invocaCasoOk = false;
			
			Reporter.log("Force invocaCasoOk=false");
		}
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_QUALSIASI_RISPOSTA, metodo, path, expectedIdCorrelazione, invocaCasoOk);
		
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, RESTIntegrazionePortaDelegata.ID_GRUPPO,RESTIntegrazionePortaDelegata.ID_GRUPPO+".testCorrelazioneApplicativaGetQualsiasiStarRisposta"},dataProvider="testCorrelazioneApplicativaGetQualsiasi")
	public void testCorrelazioneApplicativaGetQualsiasiStarRisposta(String metodo,String path,boolean expectedIdCorrelazione) throws TestSuiteException, Exception{
		
		boolean invocaCasoOk = true;
		if("GET".equals(metodo) && path.contains("/ping/")) {
			// non potrà andare a buon fine essendo la regola di correlazione sul contenuto e l'operazione ping non prevede un payload nella risposta
			// La semplice verifica che la correlazione applicativa (fallita) è stata applicata è sufficente per il test
			invocaCasoOk = false;
			
			Reporter.log("Force invocaCasoOk=false");
		}
		
		IntegrazioneUtils.testCorrelazioneApplicativaRisposta(RUOLO.PORTA_DELEGATA, CostantiTestSuite.PORTA_DELEGATA_REST_CORRELAZIONE_APPLICATIVA_GET_QUALSIASI_STAR_RISPOSTA, metodo, path, expectedIdCorrelazione, invocaCasoOk);
		
	}
}
