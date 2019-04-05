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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.response_caching;

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
 * ResponseCachingPortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingPortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ResponseCachingPortaApplicativa.SOAP";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups={ResponseCachingPortaApplicativa.ID_GRUPPO})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={ResponseCachingPortaApplicativa.ID_GRUPPO})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	

	
	/**
	 * Verifica regole con return code qualsiasi
	 * 
	 */
	@DataProvider(name=ResponseCachingPortaApplicativa.ID_GRUPPO+".soapFault")
	public Object[][] soapFaultProvider(){
		return new Object[][]{
			{500, CostantiTestSuite.PORTA_APPLICATIVA_RESPONSE_CACHING_INCLUDE_SOAP_FAULT_11, 
				CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_SOAPFAULT_11_500, true , "soap11"},
			
			{500, CostantiTestSuite.PORTA_APPLICATIVA_RESPONSE_CACHING_EXCLUDE_SOAP_FAULT_11, 
				CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_SOAPFAULT_11_500, false , "soap11"},
			
			{500, CostantiTestSuite.PORTA_APPLICATIVA_RESPONSE_CACHING_INCLUDE_SOAP_FAULT_12, 
				CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_SOAPFAULT_12_500, true , "soap12"},
			
			{500, CostantiTestSuite.PORTA_APPLICATIVA_RESPONSE_CACHING_EXCLUDE_SOAP_FAULT_12, 
				CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_SOAPFAULT_12_500, false , "soap12"},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING, CostantiTestSuite.ID_GRUPPO_TEST_RESPONSE_CACHING_PORTA_APPLICATIVA, ResponseCachingPortaApplicativa.ID_GRUPPO,ResponseCachingPortaApplicativa.ID_GRUPPO+".soapFault"},
			dataProvider=ResponseCachingPortaApplicativa.ID_GRUPPO+".soapFault")
	public void testResponseCachingRegolaSoapFault(int returnCode, String porta, String azione, boolean expectedCache, String tipo) throws TestSuiteException, Exception{

		// serve per non incorrere in casi in cui già la prima chiamata e' in cache
		String pId = "rc-"+DateManager.getTimeMillis()+"-"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();

		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.POST, RUOLO.PORTA_APPLICATIVA);
		restCore.setPortaApplicativaDelegata(porta);
		
		HashMap<String, String> headersRichiesta = new HashMap<>();
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_1_VALUE);
		headersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2, CostantiTestSuite.TEST_RESPONSE_CACHING_HEADER_2_VALUE);
		headersRichiesta.put(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"TEST-CORS\"");
		
		HashMap<String, String> queryParametersRichiesta = new HashMap<>();
		queryParametersRichiesta.put(CostantiTestSuite.TEST_RESPONSE_CACHING_URL_OPERATION_ID, pId);
		queryParametersRichiesta.put("fault", "true"); // serve solo per evitare il controllo sull'uguaglianza della risposta
		
		String contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1+";charset=utf-8";
		if("soap12".equals(tipo)) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2+";charset=utf-8";
		}
		
		// prima invocazione (non viene cachata la risposta)
		HttpResponse httpResponse = restCore.invoke(tipo, returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
		restCore.postInvoke(repository, CostantiTestSuite.SOAP_TIPO_SERVIZIO,
				CostantiTestSuite.SOAP_NOME_SERVIZIO_SINCRONO, azione);
		restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		
		if(expectedCache) {
			
			// adesso viene ritornata la risposta cached
			boolean checkNotIsArrived = true; 
			httpResponse = restCore.invoke(tipo, returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Trovato cache key ["+cacheKeyId+"]");
		
			// terza invocazione (viene ritornata la stessa risposta cached)
			httpResponse = restCore.invoke(tipo, returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository, checkNotIsArrived);
			String cacheKeyId_due = restCore.postInvokeCheckExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			Reporter.log("Terza invocazione, trovato cache key ["+cacheKeyId_due+"]");
		
			// cache id deve essere uguale
			Assert.assertEquals(cacheKeyId, cacheKeyId_due);
			
		}
		else {
			// seconda invocazione (sempre non cachata)
			httpResponse = restCore.invoke(tipo, returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
			
			// terza invocazione (sempre non cachata)
			httpResponse = restCore.invoke(tipo, returnCode, repository, true, false, contentType, headersRichiesta, queryParametersRichiesta);
			restCore.postInvoke(repository);
			restCore.postInvokeCheckNotExistsHeader(httpResponse, TestSuiteProperties.getInstance().getCacheKeyTrasporto());
		}
		
		
	}
}
