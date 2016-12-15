/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.trasparente.testsuite.units;

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RESTPut
 * 
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author: bussu $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class RESTPAPut {

	private final static String ID_GRUPPO = "REST.PA.PUT";
	private HttpRequestMethod method = HttpRequestMethod.PUT;

	private RESTCore restCore;
	
	public RESTPAPut() {
		this.restCore = new RESTCore(this.method, true);
	}
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	}

	/**
	 * contentTypeJSON Con Con
	 */
	@DataProvider (name="contentTypeJSONConCon")
	public Object[][] contentTypeJSONConCon(){
		return DataProviderUtils.contentTypeJSONConCon();
	}

	/**
	 * contentTypeJSON Con Senza
	 */
	@DataProvider (name="contentTypeJSONConSenza")
	public Object[][] contentTypeJSONConSenza(){
		return DataProviderUtils.contentTypeJSONConSenza();
	}

	/**
	 * contentTypeXML Con Con
	 */
	@DataProvider (name="contentTypeBinaryConCon")
	public Object[][] contentTypeBinaryConCon(){
		return DataProviderUtils.contentTypeBinaryConCon();
	}

	/**
	 * contentTypeXML Con Senza
	 */
	@DataProvider (name="contentTypeBinaryConSenza")
	public Object[][] contentTypeBinaryConSenza(){
		return DataProviderUtils.contentTypeBinaryConSenza();
	}


	/**
	 * contentTypeXML Con Con
	 */
	@DataProvider (name="contentTypeXMLConCon")
	public Object[][] contentTypeXMLConCon(){
		return DataProviderUtils.contentTypeXMLConCon();
	}

	/**
	 * contentTypeXML Con Senza
	 */
	@DataProvider (name="contentTypeXMLConSenza")
	public Object[][] contentTypeXMLConSenza(){
		return DataProviderUtils.contentTypeXMLConSenza();
	}

	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaJSON"},dataProvider="contentTypeJSONConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaJSON"},dataProvider="contentTypeJSONConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaJSON"},dataProvider="contentTypeJSONConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}


	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaPDF"},dataProvider="contentTypeBinaryConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaPDF(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("pdf", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaPDF"},dataProvider="contentTypeBinaryConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaPDF(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("pdf", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaPDF"},dataProvider="contentTypeBinaryConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaPDF(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("pdf", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}


	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaXML"},dataProvider="contentTypeXMLConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaXML"},dataProvider="contentTypeXMLConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaXML"},dataProvider="contentTypeXMLConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}


	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaZIP"},dataProvider="contentTypeBinaryConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaZIP(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("zip", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaZIP"},dataProvider="contentTypeBinaryConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaZIP(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("zip", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaZIP"},dataProvider="contentTypeBinaryConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaZIP(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("zip", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}


	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaDOC"},dataProvider="contentTypeBinaryConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaDOC(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("doc", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaDOC"},dataProvider="contentTypeBinaryConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaDOC(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("doc", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST_CORE,RESTPAPut.ID_GRUPPO,RESTPAPut.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaDOC"},dataProvider="contentTypeBinaryConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaDOC(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("doc", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}


}
