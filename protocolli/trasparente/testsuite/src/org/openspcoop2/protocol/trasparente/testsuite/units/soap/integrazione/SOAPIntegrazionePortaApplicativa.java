/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.integrazione;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * SOAPIntegrazionePortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPIntegrazionePortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "SOAPIntegrazionePortaApplicativa";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=SOAPIntegrazionePortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	@AfterGroups (alwaysRun=true , groups=SOAPIntegrazionePortaApplicativa.ID_GRUPPO)
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
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, SOAPIntegrazionePortaApplicativa.ID_GRUPPO,SOAPIntegrazionePortaApplicativa.ID_GRUPPO+".ContentTypesOk"},dataProvider="contentTypesOk")
	public void testContentTypeOk(String contentType) throws TestSuiteException, Exception{
		
		String version_jbossas = org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.readApplicationServerVersion();
		if((version_jbossas.startsWith("tomcat")) && contentType.endsWith(";")) {
			System.out.println("Test con content-type '"+contentType+"' non eseguito, essendo su tomcat"); // Su tomcat arriva al servizio un content-type Content-Type: text/xml; =
			return;
		}
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_NO_INTEGRAZIONE_SOAP);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "soap11";
		if(contentType.contains("application")) {
			tipo = "soap12";
		}
		HashMap<String, String> headersRequest = new HashMap<String, String>();
		if("soap11".equals(tipo)) {
			headersRequest.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
		}
		restCore.invoke(tipo, 200, repository, true, true, contentType, headersRequest);
		restCore.postInvoke(repository);
	}
	
	@DataProvider (name="contentTypesKo")
	public Object[][] contentTypesKo(){
		return IntegrazioneUtils.getContentTypesKo();
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, SOAPIntegrazionePortaApplicativa.ID_GRUPPO,SOAPIntegrazionePortaApplicativa.ID_GRUPPO+".ContentTypesKo"},dataProvider="contentTypesKo")
	public void testContentTypeKo(String contentType,String erroreAtteso) throws TestSuiteException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_NO_INTEGRAZIONE_SOAP);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "json";
		boolean authenticationError = true;
		boolean badRequest = authenticationError; // sfrutto questo if
		HttpResponse httpResponse = null;
		try {
			httpResponse = restCore.invoke(tipo, 500, repository, true, true, 
					false, contentType, false, badRequest,
					null,null);
			String errorType = httpResponse.getHeaderFirstValue(CostantiTestSuite.ERROR_TYPE);
			Reporter.log(CostantiTestSuite.ERROR_TYPE+"='"+errorType+"'");
			Assert.assertEquals(errorType, CostantiTestSuite.ERROR_TYPE_BAD_REQUEST);
		}catch(Exception e) {
			if(!e.getMessage().contains("Server returned HTTP response code: 500")) {
				throw e;
			}
		}
		
		DatabaseMsgDiagnosticiComponent dataMsg = null;
		try{
			dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			String diag = "Richiesta non valida: Content-Type '"+contentType+"' presente nella richiesta non valido; "+erroreAtteso;
			Reporter.log("Cerco diagnostico '"+diag+"' nei log ...");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, diag));		
		}finally{
			dataMsg.close();
		}
	}
	
	
	
	
	
	@DataProvider (name="alternativeSoap12ContentTypesSupported")
	public Object[][] alternativeSoap12ContentTypesSupported(){
		return IntegrazioneUtils.getAlternativeSoap12ContentTypesSupported();
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, SOAPIntegrazionePortaApplicativa.ID_GRUPPO,SOAPIntegrazionePortaApplicativa.ID_GRUPPO+".AlternativeSoap12ContentTypes"},dataProvider="alternativeSoap12ContentTypesSupported")
	public void testAlternativeSoap12ContentTypesSupported(String contentType) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_NO_INTEGRAZIONE_SOAP);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "soap12";
		HashMap<String, String> headersRequest = new HashMap<String, String>();
		restCore.invoke(tipo, 200, repository, true, true, contentType, headersRequest);
		restCore.postInvoke(repository);
	}
	
	@DataProvider (name="alternativeSoap12ContentTypesUnsupported")
	public Object[][] alternativeSoap12ContentTypesUnsupported(){
		return IntegrazioneUtils.getAlternativeSoap12ContentTypesUnsupported();
	}
	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_INTEGRAZIONE, SOAPIntegrazionePortaApplicativa.ID_GRUPPO,SOAPIntegrazionePortaApplicativa.ID_GRUPPO+".AlternativeSoap12ContentTypesUnsupported"},dataProvider="alternativeSoap12ContentTypesUnsupported")
	public void testAlternativeSoap12ContentTypesUnsupported(String contentType) throws TestSuiteException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_NO_INTEGRAZIONE_SOAP);
		restCore.setCredenziali("testsuiteOp2","12345678");
		String tipo = "json";
		boolean authenticationError = true;
		boolean badRequest = authenticationError; // sfrutto questo if
		HttpResponse httpResponse = null;
		try {
			httpResponse = restCore.invoke(tipo, 500, repository, true, true, 
					false, contentType, false, badRequest,
					null,null);
			String errorType = httpResponse.getHeaderFirstValue(CostantiTestSuite.ERROR_TYPE);
			Reporter.log(CostantiTestSuite.ERROR_TYPE+"='"+errorType+"'");
			Assert.assertEquals(errorType, CostantiTestSuite.ERROR_TYPE_UNPROCESSABLE_REQUEST_CONTENT);
		}catch(Exception e) {
			if(!e.getMessage().contains("Server returned HTTP response code: 500")) {
				throw e;
			}
		}
		
		DatabaseMsgDiagnosticiComponent dataMsg = null;
		try{
			String ctAtteso = contentType;
			if(ctAtteso.contains(";")) {
				ctAtteso = ctAtteso.substring(0, ctAtteso.indexOf(";"));
			}
			String erroreAtteso = "Invalid Content-Type:"+ctAtteso+". Is this an error message instead of a SOAP response?";
			dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
			String diag = "Il contenuto applicativo della richiesta ricevuta non è processabile: "+erroreAtteso;
			Reporter.log("Cerco diagnostico '"+diag+"' nei log ...");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, diag));		
		}finally{
			dataMsg.close();
		}
	}
	
}
