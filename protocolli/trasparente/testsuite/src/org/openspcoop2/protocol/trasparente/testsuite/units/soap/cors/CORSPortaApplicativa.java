/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.cors;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CORSPortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CORSPortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "CORSPortaApplicativa.SOAP";
	

	
	private Date dataAvvioGruppoTest = null;
	private boolean enabledTestCORSTrasparente = true;
	@BeforeGroups (alwaysRun=true , groups={CORSPortaApplicativa.ID_GRUPPO})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		
		// Abilito generazione headers 'Origin' come quelli del CORS
		HttpUtilities.enableHttpUrlConnectionForwardRestrictedHeaders();
					
		this.enabledTestCORSTrasparente = TestSuiteProperties.getInstance().isEnabledTestCORSTrasparente();
		if(!this.enabledTestCORSTrasparente) {
			System.out.println("WARN: Tests per CORS Trasparente disabilitati");
		}
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={CORSPortaApplicativa.ID_GRUPPO})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	// *** SIMPLE/ACTUAL ***

	/**
	 * Tests if a POST request is treated as actual request.
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".actualRequestPOST"})
	public void testDoFilterActualPOST() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);	
	}
	
	
	/**
	 * Tests the presence of the origin (and not '*') in the response, when
	 * supports credentials is enabled along with any origin, '*'.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".actualAnyOriginAndSupportsCredentials"})
	public void testDoFilterActualAnyOriginAndSupportsCredentials() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS_CREDENTIALS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		restCore.setCredenziali("testsuiteOp2","12345678");
		
		HttpResponse httpResponse = restCore.invoke("soap12", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_2, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
	 * Tests the presence of '*' in the response, when
	 * supports credentials is disabled along with any origin, '*'.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".actualAnyOriginAndSupportsCredentialsDisabled"})
	public void testDoFilterActualAnyOriginAndSupportsCredentialsDisabled() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		restCore.setCredenziali("testsuiteOp2","12345678");
		
		HttpResponse httpResponse = restCore.invoke("soap12", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_2, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Tests the presence of exposed headers in response, if configured.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".actualRequestWithExposedHeaders"})
	public void testDoFilterActualWithExposedHeaders() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_EXPOSE);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_EXPOSE_HEADERS, CostantiTestSuite.TEST_CORS_EXPOSE_HEADERS);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	
	
	
	// *** PREFLIGHT ***
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflight"})
	public void testDoFilterPreflight() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
	 * Checks if an OPTIONS request is processed as pre-flight with more headers.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightMoreHeaders"})
	public void testDoFilterPreflightMoreHeaders() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE+","+ 
				HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+" ,"+ 
				HttpConstants.AUTHORIZATION+" , "+
				HttpConstants.CACHE_STATUS_HTTP_1_1);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
	 * Checks if an OPTIONS request is processed as pre-flight where any origin
	 * is enabled.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightAnyOrigin"})
	public void testDoFilterPreflightAnyOrigin() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight, with invalid origin.
	 */
	@DataProvider(name=CORSPortaApplicativa.ID_GRUPPO+".wrongOriginProvider")
	public Object[][] wrongOriginProvider(){
		return new Object[][]{
			{CostantiTestSuite.TEST_HTTP_ORIGIN},
			{CostantiTestSuite.TEST_HTTPS_ORIGIN_UPPER_CASE},
			{CostantiTestSuite.TEST_HTTPS_ORIGIN_2},
			{CostantiTestSuite.TEST_HTTPS_ORIGIN_PORT_DIFFERENT},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightInvalidOrigin"},
			dataProvider=CORSPortaApplicativa.ID_GRUPPO+".wrongOriginProvider")
	public void testDoFilterPreflightInvalidOrigin(String origin) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, origin);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, null); // // il browser in questo contesto non fa passare la richiesta poichè non ha trovato l'origin che combacia
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight, with max age -1
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightNegativeMaxAge"})
	public void testDoFilterPreflightNegativeMaxAge() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_MAXAGE_DISABLED);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_MAX_AGE, HttpConstants.ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight, with max age
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightMaxAge"})
	public void testDoFilterPreflightMaxAge() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_MAXAGE);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_MAX_AGE, 345+"");
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight.
	 * Tests the presence of the origin (and not '*') in the response, when
	 * supports credentials is enabled along with any origin, '*'.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightAnyOriginAndSupportsCredentials"})
	public void testDoFilterPreflightAnyOriginAndSupportsCredentials() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS_CREDENTIALS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		restCore.setCredenziali("testsuiteOp2","12345678");
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
	 * Checks if an OPTIONS request is processed as pre-flight.
	 * Tests the presence of '*' in the response, when
	 * supports credentials is disabled along with any origin, '*'.
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightAnyOriginAndSupportsCredentialsDisabled"})
	public void testDoFilterPreflightAnyOriginAndSupportsCredentialsDisabled() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		restCore.setCredenziali("testsuiteOp2","12345678");
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}

	
	
	
	
	// *** INVALID ***
	
	/**
	 * Checks a request in presence of null origin
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".nullOrigin"})
	public void testDoFilterNullOrigin() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		//headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Checks a request in presence of empty origin
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".emptyOrigin"})
	public void testDoFilterEmptyOrigin() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, "");
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Checks a request in presence of origin not allowed
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".originNotAllowed"},
			dataProvider=CORSPortaApplicativa.ID_GRUPPO+".wrongOriginProvider")
	public void testDoFilterOriginNotAllowed(String origin) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, origin);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	 /**
     * when a valid CORS Pre-flight request arrives, with no
     * Access-Control-Request-Method: vengono cmq ritornati i metodi poiche in GovWay la preflight viene gestita, non essendo stata riconosciuta una azione
     */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightNoAccessControlRequestMethod"})
	public void testDoFilterPreflightNoACRM() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		//headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
     * when a valid CORS Pre-flight request arrives, with empty
     * Access-Control-Request-Method: vengono cmq ritornati i metodi poiche in GovWay la preflight viene gestita, non essendo stata riconosciuta una azione
     */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightEmptyAccessControlRequestMethod"})
	public void testDoFilterPreflightEmptyACRM() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,"");
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
     * when a valid CORS Pre-flight request arrives, with no
     * Access-Control-Request-Headers: vengono cmq ritornati i metodi poiche in GovWay la preflight viene gestita, non essendo stata riconosciuta una azione
     */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightNoAccessControlRequestHeaders"})
	public void testDoFilterPreflightNoACRH() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		//headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
     * when a valid CORS Pre-flight request arrives, with empty
     * Access-Control-Request-Headers: vengono cmq ritornati i metodi poiche in GovWay la preflight viene gestita, non essendo stata riconosciuta una azione
     */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightEmptyAccessControlRequestHeaders"})
	public void testDoFilterPreflightEmptyACRH() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,"");
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
     * when a valid CORS Pre-flight request arrives, with unsupported
     * Access-Control-Request-Headers
     */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightUnsupportedAccessControlRequestHeaders"})
	public void testDoFilterPreflightUnsupportedACRH() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE+", X-CUSTOM_NON_SUPPORTATO");
		
		HttpResponse httpResponse = restCore.invoke("preflight", 200, repository, false, false, null, headersRichiesta);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, CostantiTestSuite.TEST_CORS_ALLOW_METHOD_DEFAULT_AS_STRING);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, CostantiTestSuite.TEST_CORS_ALLOW_HEADERS_DEFAULT_AS_STRING); // il nuovo header non viene ritornato
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	/**
	 * Tests with different origin
	 */
	@DataProvider(name=CORSPortaApplicativa.ID_GRUPPO+".originProvider")
	public Object[][] originProvider(){
		return new Object[][]{
			{CostantiTestSuite.TEST_HTTPS_ORIGIN},
			{CostantiTestSuite.TEST_HTTP_ORIGIN},
			{CostantiTestSuite.TEST_HTTPS_ORIGIN_UPPER_CASE},
			{CostantiTestSuite.TEST_HTTPS_ORIGIN_2},
			{CostantiTestSuite.TEST_HTTPS_ORIGIN_PORT_DIFFERENT},
			{CostantiTestSuite.TEST_FILE_ORIGIN},
			{CostantiTestSuite.TEST_NULL_ORIGIN},
		};
	}	
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".differentOrigin"},
			dataProvider=CORSPortaApplicativa.ID_GRUPPO+".originProvider")
	public void testDoFilterDifferentOrigin(String origin) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, origin);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
	 * Tests with invalid origin
	 */
	@DataProvider(name=CORSPortaApplicativa.ID_GRUPPO+".invalidOriginProvider")
	public Object[][] invalidOriginProvider(){
		return new Object[][]{
			{CostantiTestSuite.TEST_INVALID_ORIGIN_1},
			{CostantiTestSuite.TEST_INVALID_ORIGIN_2},
			{CostantiTestSuite.TEST_INVALID_ORIGIN_3},
			{CostantiTestSuite.TEST_INVALID_ORIGIN_4}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".invalidOrigin"},
			dataProvider=CORSPortaApplicativa.ID_GRUPPO+".invalidOriginProvider")
	public void testDoFilterInvalidOrigin(String origin) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_ALLORIGINS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, origin);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	
	
	
	
	
	
	
	// *** TRASPARENTE ***
	
	/**
	 * Tests configurazione trasparente as actual request.
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".actualRequestTrasparente"})
	public void testDoFilterActualRequestTrasparente() throws TestSuiteException, Exception{

		if(!this.enabledTestCORSTrasparente) {
			Reporter.log("Tests per CORS Trasparente disabilitati");
			return;
		}
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_TRASPARENTE);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_TRASPARENTE, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, null);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, null);
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
	/**
	 * Tests configurazione trasparente as preflight request.
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_CORS, CostantiTestSuite.ID_GRUPPO_TEST_CORS_PORTA_APPLICATIVA, CORSPortaApplicativa.ID_GRUPPO,CORSPortaApplicativa.ID_GRUPPO+".preflightTrasparente"})
	public void testDoFilterPreflightTrasparente() throws TestSuiteException, Exception{

		if(!this.enabledTestCORSTrasparente) {
			Reporter.log("Tests per CORS Trasparente disabilitati");
			return;
		}
		
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.OPTIONS, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_SOAP_CORS_TRASPARENTE);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.POST.name());
		headersRichiesta.put(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE+", "+HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+", HeaderProva");
		
		HttpResponse httpResponse = restCore.invoke("soap11", 200, repository, true, true, HttpConstants.CONTENT_TYPE_SOAP_1_1, headersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_TRASPARENTE, CostantiTestSuite.NOME_SERVIZIO_CORS_SOAP_AZIONE_INVOKER);
		
		HashMap<String, String> headersAttesiRisposta = new HashMap<>();
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN, CostantiTestSuite.TEST_HTTPS_ORIGIN);
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS, HttpRequestMethod.POST.name());
		headersAttesiRisposta.put(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS, HttpConstants.CONTENT_TYPE+", "+HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+", HeaderProva");
		restCore.postInvokeCheckHeader(httpResponse, headersAttesiRisposta);		
	}
}
