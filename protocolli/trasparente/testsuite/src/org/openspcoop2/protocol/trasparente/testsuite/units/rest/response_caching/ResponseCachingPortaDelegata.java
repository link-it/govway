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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest.response_caching;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.utils.Utilities;
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
 * ResponseCachingPortaDelegata
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingPortaDelegata {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ResponseCachingPortaDelegata.REST";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups={ResponseCachingPortaDelegata.ID_GRUPPO})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={ResponseCachingPortaDelegata.ID_GRUPPO})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	

	
	
	/**
	 * Verifica funzionamento response caching
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".verifyCache"})
	public void testResponseCaching() throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		boolean checkNotIsArrived = true; 
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// terza invocazione (viene ritornata la stessa risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_due);
		
	}
	
	/**
	 * Verifica funzionamento response caching, algoritmo di generazione digest di default
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".digestDefault"})
	public void testResponseCachingDigestDefault() throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		boolean checkNotIsArrived = true; 
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		
		// *** modificando la url il cache id varia ***
		queryParametersRichiesta.put("parametroCasuale", "test");
		
		// prima invocazione (non viene cachata la risposta)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached con id differente a prima)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_parametroUrl = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_parametroUrl+"]");
		
		// cache id deve essere differente
		Assert.assertNotEquals(cacheKeyId, cacheKeyId_parametroUrl);
		
		
		// *** ripristinando la url il cache id torna ad essere uguale ***
		queryParametersRichiesta.clear();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_tre = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_tre+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_tre);
		
		
		// *** modificando il body il cache id varia ***
		
		// prima invocazione (non viene cachata la risposta)
		httpResponse = restCore.invoke("xml", 200, repository, true, true, HttpConstants.CONTENT_TYPE_XML, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached con id differente a prima)
		httpResponse = restCore.invoke("xml", 200, repository, true, true, HttpConstants.CONTENT_TYPE_XML, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_contenuto = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_contenuto+"]");
		
		// cache id deve essere differente
		Assert.assertNotEquals(cacheKeyId, cacheKeyId_contenuto);
		
		
		// *** ripristinando il contenuto il cache id torna ad essere uguale ***
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_quattro = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_quattro+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_quattro);
		
		
		// *** modificando gli header il cache id non varia ***
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE+"DIVERSO");
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE+"DIVERSO");
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_header = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_header+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_header);
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_header2 = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_header2+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_header2);
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put("X-ALTRO","VALORE");
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_header3 = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_header3+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_header3);
	}
	
	/**
	 * Verifica utilizzo no-cache in Cache Control che fa si che la risposta non cachata non sia ritornata
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".noCache")
	public Object[][] noCacheProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_NO_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".noCache"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".noCache")
	public void testResponseCachingNoCache(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (non viene comunque cachata la risposta)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// terza invocazione (non viene comunque cachata la risposta)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// rimuovo direttiva
		headersRichiesta.remove(headerName);
		
		boolean checkNotIsArrived = true; 
		boolean cached = false;
		String cacheKeyId = null;
		if(HttpConstants.CACHE_STATUS_HTTP_1_1.equals(headerName) && !headerValue.contains(HttpConstants.CACHE_STATUS_DIRECTIVE_NO_STORE)) {
			cached = true;
		}
		
		if(!cached) {
		
			// con la sola direttiva no-cache, cmq il messaggio viene salvato in http-1.1
			// altrimenti serve una ulteriore invocazione senza la direttiva
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
						
		}
			
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// terza invocazione (viene ritornata la stessa risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_due);
		
	}
	
	
	
	
	/**
	 * Verifica utilizzo no-store in Cache Control che fa si che la risposta non sia cachata
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".noStore")
	public Object[][] noStoreProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_NO_STORE},
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".noStore"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".noStore")
	public void testResponseCachingNoStore(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (non viene comunque cachata la risposta)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
				
		// rimuovo direttiva
		headersRichiesta.remove(headerName);
		
		// non essendo stata cachata la risposta mi aspetto di nuovo che non sia presente in cache
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
		// adesso viene ritornata la risposta cached
		boolean checkNotIsArrived = true; 
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// terza invocazione (viene ritornata la stessa risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_due);
		
	}
	
	
	
	
	/**
	 * Verifica utilizzo max-age in Cache Control di 10 secondi
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".maxAge")
	public Object[][] maxAgeProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_MAX_AGE+"=10"},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_DIRECTIVE_MAX_AGE+"=10"}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".maxAge"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".maxAge")
	public void testResponseCachingMaxAge(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
		// adesso viene ritornata la risposta cached
		boolean checkNotIsArrived = true; 
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// attendo 10+1 secondi
		Utilities.sleep((10+1)*1000);
		
		if(HttpConstants.CACHE_STATUS_HTTP_1_1.equals(headerName)) {
			
			// terza invocazione (resettata)
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			// adesso viene ritornata la risposta cached
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_after = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId_after+"]");
			
			// cache id non deve essere uguale
			Assert.assertNotEquals(cacheKeyId, cacheKeyId_after);
		}
		else {
			// terza invocazione (viene ritornata la stessa risposta cached)
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
			
		}
		
	}
	
	
	
	/**
	 * Verifica timeout differente dal default (10 secondi) impostato nella configurazione della regola
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".timeout"})
	public void testResponseCachingTimeout() throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_TIMEOUT_SECONDS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
		// adesso viene ritornata la risposta cached
		boolean checkNotIsArrived = true; 
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// attendo 10+1 secondi
		Utilities.sleep((10+1)*1000);
			
		// terza invocazione (resettata)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// adesso viene ritornata la risposta cached
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_after = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_after+"]");
		
		// cache id non deve essere uguale
		Assert.assertNotEquals(cacheKeyId, cacheKeyId_after);
		
	}
	
	
	
	/*
	 * Verifica funzionamento response caching, algoritmo di generazione digest che include due header http
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".digestHeaders"})
	public void testResponseCachingDigestHeaders() throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_DIGEST_CON_HEADERS);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		boolean checkNotIsArrived = true; 
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		

		// *** modificando gli header il cache id varia ***
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE+"DIVERSO");
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		// risposta not cached
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_header = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_header+"]");
		
		// cache id deve essere differente alla prima invocazione
		Assert.assertNotEquals(cacheKeyId, cacheKeyId_header);
		
		
		// *** ripristinando gli header ottengo lo stesso cache id precedente ***
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		// stesso cache id della prima invocazione
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_ripristino_1 = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_ripristino_1+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_ripristino_1);
		
		
		// *** modificando il secondo header il cache id varia ***
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE+"DIVERSO");
		
		// risposta not cached
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_header2 = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_header2+"]");
		
		// cache id deve essere differente alla prima invocazione
		Assert.assertNotEquals(cacheKeyId, cacheKeyId_header2);
		Assert.assertNotEquals(cacheKeyId_header, cacheKeyId_header2);
		
		
		// *** ripristinando gli header ottengo lo stesso cache id precedente ***
		
		headersRichiesta.clear();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		// stesso cache id della prima invocazione
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_ripristino_2 = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_ripristino_2+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_ripristino_2);
		
		
		// *** aggiungere un terzo header non registrato per il digest non fa cambiare il cache id ***
		
		headersRichiesta.put("X-ALTRO-HEADER", "VALORE");
		
		// stesso cache id della prima invocazione
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_header_3 = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_header_3+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_header_3);
		
	}
	
	
	
	

	/*
	 * Verifica funzionamento response caching, algoritmo di generazione digest che non include ne header, ne body ne url
	 * 
	 */
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".digestUriBodyDisabilitato"})
	public void testResponseCachingDigestUriBodyDisabilitato() throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_DIGEST_URI_BODY_DISABILITATO);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, pId);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, "2-"+pId);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		boolean checkNotIsArrived = true; 
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		
		// *** modificando la url il cache id non varia ***
		queryParametersRichiesta.put("parametroCasuale", "test");
		
		// prima invocazione (viene ritornata la risposta cached con id uguale a prima)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_parametroUrl = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_parametroUrl+"]");
		
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_parametroUrl);
		
		
		// *** ripristino la url ***
		queryParametersRichiesta.clear();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_tre = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_tre+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_tre);
		
		
		// *** modifico il body il cache id non varia ***
		
		// prima invocazione (viene ritornata la risposta cached con id uguale a prima)
		httpResponse = restCore.invoke("xml", 200, repository, true, false, HttpConstants.CONTENT_TYPE_XML, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_contenuto = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_contenuto+"]");
		
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_contenuto);
		
		
		// *** ripristino il contenuto ***
		
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_quattro = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId_quattro+"]");
		
		// cache id deve essere uguale alla prima invocazione
		Assert.assertEquals(cacheKeyId, cacheKeyId_quattro);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Verifica utilizzo no-cache in Cache Control non viene interpretato poichè disabilitato in configurazione
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".noCacheDisabled")
	public Object[][] noCacheDisabledProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_NO_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".noCacheDisabled"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".noCacheDisabled")
	public void testResponseCachingNoCacheDisabled(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_NO_CACHE_DISABLED);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		boolean cached = false;
		String cacheKeyId = null;
		if(HttpConstants.CACHE_STATUS_HTTP_1_1.equals(headerName) && !headerValue.contains(HttpConstants.CACHE_STATUS_DIRECTIVE_NO_STORE)) {
			
			// con la sola direttiva no-cache ignorata, cmq il messaggio viene salvato in http-1.1 se non e' presente la direttiva no_store.
			// e comunque non viene salvato nel caso di http 1.0
			
			cached = true;
		}
		
		if(!cached) {
			
			// continua a non essere salvata
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			// continua a non essere salvata
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
						
		}
		else {
		
			// seconda invocazione (viene ritornata la risposta cached poichè viene ignorata la direttiva)
			boolean checkNotIsArrived = true; 
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId+"]");
			
			// terza invocazione  (viene ritornata la risposta cached poichè viene ignorata la direttiva)
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
			
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
			
		}
		
	}
	
	
	
	
	/**
	 * Verifica utilizzo no-store in Cache Control non viene interpretato poichè disabilitato in configurazione
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".noStoreDisabled")
	public Object[][] noStoreDisabledProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_NO_STORE},
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".noStoreDisabled"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".noStoreDisabled")
	public void testResponseCachingNoStoreDisabled(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_NO_STORE_DISABLED);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		boolean returnCached = false;
		String cacheKeyId = null;
		if(HttpConstants.CACHE_STATUS_HTTP_1_1.equals(headerName) && !headerValue.contains(HttpConstants.CACHE_STATUS_DIRECTIVE_NO_CACHE)) {
			
			// con la sola direttiva no-store ignorata, cmq il messaggio cachato non viene ritornato se in http-1.1 e' presente la direttiva no_cache.
			// e comunque non viene ritornato nel caso di http 1.0
			
			returnCached = true;
		}
		
		if(returnCached) {
			
			boolean checkNotIsArrived = true; 
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId+"]");
			
			// terza invocazione  (viene ritornata la risposta cached poichè viene ignorata la direttiva)
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
			
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
			
		}
		else {
			
			// continua a non essere ritornata la risposta cached
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			// continua a non essere ritornata la risposta cached
			httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
		}
		
		
	}
	
	
	
	/**
	 * Verifica utilizzo no-store e no-cache in Cache Control non viene interpretato poichè disabilitato in configurazione
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".noCacheNoStoreDisabled")
	public Object[][] noCacheNoStoreDisabledProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_NO_STORE},
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_NO_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_HTTP_1_1_DISABLE_CACHE},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_HTTP_1_0_DISABLE_CACHE}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".noCacheNoStoreDisabled"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".noCacheNoStoreDisabled")
	public void testResponseCachingNoCacheNoStoreDisabled(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_NO_CACHE_NO_STORE_DISABLED);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		// seconda invocazione (viene ritornata la risposta cachata)
		boolean checkNotIsArrived = true; 
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// terza invocazione  (viene ritornata la risposta cached poichè viene ignorata la direttiva)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_due);

		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Verifica utilizzo max-age in Cache Control di 10 secondi non viene interpretato poichè disabilitato in configurazione
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".maxAgeDisabled")
	public Object[][] maxAgeDisabledProvider(){
		return new Object[][]{
			{HttpConstants.CACHE_STATUS_HTTP_1_1, HttpConstants.CACHE_STATUS_DIRECTIVE_MAX_AGE+"=10"},
			{HttpConstants.CACHE_STATUS_HTTP_1_0, HttpConstants.CACHE_STATUS_DIRECTIVE_MAX_AGE+"=10"}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".maxAgeDisabled"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".maxAgeDisabled")
	public void testResponseCachingMaxAgeDisabled(String headerName, String headerValue) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_MAX_AGE_DISABLED);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(headerName, headerValue);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
		// adesso viene ritornata la risposta cached
		boolean checkNotIsArrived = true; 
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
		// attendo 10+1 secondi
		Utilities.sleep((10+1)*1000);
		
		// terza invocazione (viene ritornata la stessa risposta cached)
		httpResponse = restCore.invoke("json", 200, repository, true, true, HttpConstants.CONTENT_TYPE_JSON, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, checkNotIsArrived);
		String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
	
		// cache id deve essere uguale
		Assert.assertEquals(cacheKeyId, cacheKeyId_due);
		
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Verifica che solo le risposte più grandi di 1 k vengono salvate
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".maxSize")
	public Object[][] maxSizeProvider(){
		return new Object[][]{
			{"zip", HttpConstants.CONTENT_TYPE_ZIP, true},
			{"json", HttpConstants.CONTENT_TYPE_JSON, false}
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".maxSize"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".maxSize")
	public void testResponseCachingMaxSize(String tipo, String contentType, boolean maggioreUnKb) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_MAX_MESSAGE_SIZE);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke(tipo, 200, repository, true, true, contentType, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		if(maggioreUnKb) {
			
			// seconda invocazione (sempre non cachata)
			httpResponse = restCore.invoke(tipo, 200, repository, true, true, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			// terza invocazione (sempre non cachata)
			httpResponse = restCore.invoke(tipo, 200, repository, true, true, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
		}
		else {
		
			// adesso viene ritornata la risposta cached
			boolean checkNotIsArrived = true; 
			httpResponse = restCore.invoke(tipo, 200, repository, true, true, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
			// terza invocazione (viene ritornata la stessa risposta cached)
			httpResponse = restCore.invoke(tipo, 200, repository, true, true, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
		
		}
		
	}
	
	
	/**
	 * Verifica regole specifiche con return code
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".regole")
	public Object[][] regoleProvider(){
		return new Object[][]{
			{410, false, null, true , -1},
			{410, true, "json", true , -1},
			{410, true, "xml", true , -1},
			{411, false, null, true , -1},
			{411, true, "json", false , -1},
			{411, true, "xml", false , -1},
			{413, false, null,  true , -1},
			{413, true, "json", false , -1},
			{413, true, "xml", false , -1},
			{415, false, null,  true , -1},
			{415, true, "json", false , -1},
			{415, true, "xml", false , -1},
			{416, false, null,  true , 11},
			{416, true, "json", false , -1},
			{416, true, "xml", false , -1},
			{200, false, null, false , -1},
			{200, true, "json", false , -1},
			{200, true, "xml", false , -1},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".regole"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".regole")
	public void testResponseCachingRegole(int returnCode, boolean fault, String serializationType, boolean expectedCache, long sleep) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();

		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_REGOLE);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		if(fault) {
			queryParametersRichiesta.put("problem", true+"");
			queryParametersRichiesta.put("problemSerializationType", serializationType);
			queryParametersRichiesta.put("problemStatus", returnCode+"");
		}
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		if(fault) {
			if("xml".equals(serializationType)) {
				contentType = HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
			}
			else {
				contentType = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
			}
		}
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		if(expectedCache) {
			
			// adesso viene ritornata la risposta cached
			boolean checkNotIsArrived = true; 
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
			if(sleep>0) {
				Utilities.sleep(sleep);
			}
			
			// terza invocazione (viene ritornata la stessa risposta cached)
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
			
		}
		else {
			// seconda invocazione (sempre non cachata)
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			if(sleep>0) {
				Utilities.sleep(sleep);
			}
			
			// terza invocazione (sempre non cachata)
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		}
		
		
	}
	
	
	
	
	
	/**
	 * Verifica regole con return code qualsiasi
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaDelegata.ID_GRUPPO+".regolaDefault")
	public Object[][] regolaDefaultProvider(){
		return new Object[][]{
			{400, false, null, true , -1},
			{400, true, "json", false , -1},
			{400, true, "xml", false , -1},
			{500, false, null, true , -1},
			{500, true, "json", false , -1},
			{500, true, "xml", false , -1},
			{200, false, null, true , -1},
			{200, true, "json", false , -1},
			{200, true, "xml", false , -1},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_DELEGATA, ResponseCachingPortaDelegata.ID_GRUPPO,ResponseCachingPortaDelegata.ID_GRUPPO+".regolaDefault"},
			dataProvider=ResponseCachingPortaDelegata.ID_GRUPPO+".regolaDefault")
	public void testResponseCachingRegolaDefault(int returnCode, boolean fault, String serializationType, boolean expectedCache, long sleep) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();

		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_RESPONSE_CACHING_REGOLA_DEFAULT);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		if(fault) {
			queryParametersRichiesta.put("problem", true+"");
			queryParametersRichiesta.put("problemSerializationType", serializationType);
			queryParametersRichiesta.put("problemStatus", returnCode+"");
		}
		
		String contentType = HttpConstants.CONTENT_TYPE_JSON;
		if(fault) {
			if("xml".equals(serializationType)) {
				contentType = HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807;
			}
			else {
				contentType = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
			}
		}
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		if(expectedCache) {
			
			// adesso viene ritornata la risposta cached
			boolean checkNotIsArrived = true; 
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
			if(sleep>0) {
				Utilities.sleep(sleep);
			}
			
			// terza invocazione (viene ritornata la stessa risposta cached)
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
			
		}
		else {
			// seconda invocazione (sempre non cachata)
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			if(sleep>0) {
				Utilities.sleep(sleep);
			}
			
			// terza invocazione (sempre non cachata)
			httpResponse = restCore.invoke("json", returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		}
		
		
	}
}
