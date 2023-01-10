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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest.auth;

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PosizioneCredenziale;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.WWWAuthenticateUtils;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * AutenticazionePortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTAutenticazionePortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RESTAutenticazionePortaApplicativa";
	public static final String ID_GRUPPO_NO_PRINCIPAL = "RESTAutenticazionePortaApplicativaNoPrincipal";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups= {RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL })
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL })
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	

//	1) Invocazione dove il client fornisce le credenziali corrette che non vengono "bruciate" dalla PdD (autenticazione:none) e vengono inoltrate al servizio. 
//	Si attende 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithBasicAuth_OK"})
	public void testServiceWithBasicAuth_ok() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}
	
	
//	2) Invocazione dove il client fornisce le credenziali errate che non vengono "bruciate" dalla PdD (autenticazione:none) e vengono inoltrate al servizio. 
//	Si attende 401 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiErrate"})
	public void testServiceWithBasicAuth_ko_credentialErrate() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","ERRATA");
		restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		restCore.postInvoke(repository);
	}
	
//	3) Invocazione dove il client non fornisce le credenziali (autenticazione:none). 
//	Si attende 401 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiNonFornite"})
	public void testServiceWithBasicAuth_ko_noCredential() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		restCore.postInvoke(repository);
	}
	
	
//	4) Invocazione come 2 o 3 dove però il servizio finale ritorna anche l'header WWW-Authenticate
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiErrate_Domain"})
	public void testServiceWithBasicAuth_ko_credentialErrate_domain() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH_DOMAIN);
		restCore.setCredenziali("testsuiteOp2","ERRATA");
		HttpResponse response = restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		String domain = response.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		if(domain==null || "".equals(domain)) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' non trovato nella risposta");
		}
		String atteso = HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX+"TestSuiteOpenSPCoop2"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX; 
		if(atteso.equals(domain)==false) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' con un valore ("+domain+") differente da quello atteso("+atteso+")");
		}
		restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiNonFornite_Domain"})
	public void testServiceWithBasicAuth_ko_noCredential_domain() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_SERVICE_WITH_BASIC_AUTH_DOMAIN);
		restCore.setCredenziali("testsuiteOp2","ERRATA");
		HttpResponse response = restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		String domain = response.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		if(domain==null || "".equals(domain)) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' non trovato nella risposta");
		}
		String atteso = HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX+"TestSuiteOpenSPCoop2"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX; 
		if(atteso.equals(domain)==false) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' con un valore ("+domain+") differente da quello atteso("+atteso+")");
		}
		restCore.postInvoke(repository);
	}
	
	
	
//	5) Invocazione dove il client fornisce le credenziali corrette che però devono essere "bruciate" dall'autenticazione 'basic'. Queste non vengono inoltrate quindi al servizio.
//	Si attende un 401 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthBasic_ServiceWithBasicAuth_KO_CredenzialiBruciate"})
	public void testAuthBasicServiceWithBasicAuth_ko_credentialBruciate() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA_TEST_CREDENZIALI_BRUCIATE);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		restCore.postInvoke(repository);
	}
	
	
//	6) Invocazione dove il client fornisce le credenziali corrette che vengono sia utilizzate dalla PdD per autenticazione basic
	// sia vengono forwardate (non sono "bruciate") all'applicativo finale che che può quindi effettuare anche lui a sua volta l'autenticazione.
//	Si attende un 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthBasic_ServiceWithBasicAuth_OK_CredenzialiInoltrate"})
	public void testAuthBasicServiceWithBasicAuth_ko_credentialForwarded() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA_TEST_CREDENZIALI_BRUCIATE);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH_FORWARD_CREDENTIALS);
		restCore.setCredenziali("testsuiteOp2","12345678");
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}
	
//	7) Invocazione dove il client non fornisce le credenziali richieste dall'autenticazione 'basic'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthBasic_WWWAuthenticate_credenzialiNonFornite"})
	public void testAuthBasicWWWAuthenticate_credenzialiNonFornite() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				HttpConstants.AUTHENTICATION_BASIC, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_BASIC_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_BASIC_ERROR_DESCRIPTION_NOTFOUND);
	}
	
//	8) Invocazione dove il client fornisce credenziali non valide richieste dall'autenticazione 'basic'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthBasic_WWWAuthenticate_credenzialiNonValide"})
	public void testAuthBasicWWWAuthenticate_credenzialiNonValide() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("utenzaInventataNonEsistente","12345678");
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				HttpConstants.AUTHENTICATION_BASIC, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_BASIC_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_BASIC_ERROR_DESCRIPTION_INVALID);
	}
	
	
	
	
	
	
	
//	ApiKey 1) Invocazione dove il client fornisce le credenziali corrette che vengono "bruciate" dalla PdD, al servizio vengono inoltrati solamente i cookies rimasti. 
//	Si attende 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithApiKeyAuth"})
	public void testServiceWithApiKeyAuth() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APIKEY);
		restCore.setCredenzialiApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456", PosizioneCredenziale.COOKIE);
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}

//	ApiKey 2) Invocazione dove il client fornisce le credenziali corrette che non vengono "bruciate" dalla PdD, al servizio vengono inoltrati tutti i cookies, anche quelli contenente l'api key. 
//	Si attende 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithApiKeyAuthForward"})
	public void testServiceWithApiKeyAuthForward() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APIKEY_FORWARD);
		restCore.setCredenzialiApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456", PosizioneCredenziale.COOKIE);
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}
	

//	ApiKey 3) Invocazione dove il client non fornisce le credenziali richieste dall'autenticazione 'api-key'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthApiKey_WWWAuthenticate_credenzialiNonFornite"})
	public void testAuthApiKeyWWWAuthenticate_credenzialiNonFornite() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APIKEY);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_AUTHTYPE, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_ERROR_DESCRIPTION_NOTFOUND);
	}
	
//	ApiKey 4) Invocazione dove il client fornisce credenziali non valide richieste dall'autenticazione 'api-key'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthApiKey_WWWAuthenticate_credenzialiNonValide"})
	public void testAuthApiKeyWWWAuthenticate_credenzialiNonValide() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APIKEY);
		restCore.setCredenzialiApiKey("EsempioUtenzaInesistenzaApiKey@MinisteroFruitore.gw", "123456", PosizioneCredenziale.COOKIE);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_AUTHTYPE, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_ERROR_DESCRIPTION_INVALID);
	}
	

	
	
//	ApiKey + AppId 1) Invocazione dove il client fornisce le credenziali corrette che vengono "bruciate" dalla PdD, al servizio vengono inoltrati solamente i cookies rimasti. 
//	Si attende 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithApiKeyAppIdAuth"})
	public void testServiceWithApiKeyAppIdAuth() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APPID);
		restCore.setCredenzialiMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456", PosizioneCredenziale.COOKIE, PosizioneCredenziale.COOKIE);
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}

//	ApiKey + AppId 2) Invocazione dove il client fornisce le credenziali corrette che non vengono "bruciate" dalla PdD, al servizio vengono inoltrati tutti i cookies, anche quelli contenente l'api key. 
//	Si attende 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".ServiceWithApiKeyAppIdAuthForward"})
	public void testServiceWithApiKeyAppIdAuthForward() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APPID_FORWARD);
		restCore.setCredenzialiMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456", PosizioneCredenziale.COOKIE, PosizioneCredenziale.COOKIE);
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}
	

//	ApiKey + AppId 3) Invocazione dove il client non fornisce le credenziali richieste dall'autenticazione 'api-key + AppId'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthApiKeyAppId_WWWAuthenticate_credenzialiNonFornite"})
	public void testAuthApiKeyAppIdWWWAuthenticate_credenzialiNonFornite() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APPID);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_AUTHTYPE, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_ERROR_DESCRIPTION_NOTFOUND);
	}
	
//	ApiKey + AppId 4) Invocazione dove il client fornisce credenziali non valide richieste dall'autenticazione 'api-key + AppId'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthApiKeyAppId_WWWAuthenticate_credenzialiNonValide"})
	public void testAuthApiKeyAppIdWWWAuthenticate_credenzialiNonValide() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_APPID);
		restCore.setCredenzialiMultipleApiKey("EsempioUtenzaInesistenzaApiKey@MinisteroFruitore.gw", "123456", PosizioneCredenziale.COOKIE, PosizioneCredenziale.COOKIE);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_AUTHTYPE, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_APIKEY_ERROR_DESCRIPTION_INVALID);
	}
		
	
	
	
	
//	https 1) Invocazione dove il client non fornisce le credenziali richieste dall'autenticazione 'https'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@Test(groups={RESTAutenticazionePortaApplicativa.ID_GRUPPO,RESTAutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,RESTAutenticazionePortaApplicativa.ID_GRUPPO+".AuthHttps_WWWAuthenticate_credenzialiNonFornite"})
	public void testAuthHttpsWWWAuthenticate_credenzialiNonFornite() throws TestSuiteException, Exception{
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_APPLICATIVA_REST_HTTPS);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_HTTPS_AUTHTYPE, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_HTTPS_REALM, 
				CostantiTestSuite.TEST_WWWAUTHENTICATE_HTTPS_ERROR_DESCRIPTION_NOTFOUND);
	}
	
	
}
