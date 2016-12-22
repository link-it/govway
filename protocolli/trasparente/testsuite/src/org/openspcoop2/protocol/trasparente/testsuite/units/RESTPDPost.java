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

import org.openspcoop2.protocol.trasparente.testsuite.units.utils.DataProviderUtils;
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
 * RESTPDPost
 * 
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTPDPost {

	private final static String ID_GRUPPO = "REST.PD.POST";
	private HttpRequestMethod method = HttpRequestMethod.POST;

	private RESTCore restCore;
	
	public RESTPDPost() {
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
	 * responseCodeConCon
	 */
	@DataProvider (name="responseCodeConCon")
	public Object[][] responseCodeConCon(){
		return DataProviderUtils.responseCodeConCon();
	}

	/**
	 * responseCodeConSenza
	 */
	@DataProvider (name="responseCodeConSenza")
	public Object[][] responseCodeConSenza(){
		return DataProviderUtils.responseCodeConSenza();
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

	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaJSON"},dataProvider="contentTypeJSONConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_SenzaContenutoRispostaJSON"},dataProvider="contentTypeJSONConSenza")
	public void test_SenzaContenutoRichiesta_SenzaContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, false, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaJSON"},dataProvider="contentTypeJSONConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaJSON"},dataProvider="contentTypeJSONConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaJSON(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("json", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}

	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaBinary"},dataProvider="contentTypeBinaryConCon")
	public void test_ConContenutoRichiesta_ConContenutoBinary(String tipoTest, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke(tipoTest, responseCodeAtteso, repository, true, true, null);
		this.restCore.postInvoke(repository);
	}

	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_SenzaContenutoRispostaBinary"},dataProvider="contentTypeBinaryConSenza")
	public void test_SenzaContenutoRichiesta_SenzaContenutoBinary(String tipoTest, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke(tipoTest, responseCodeAtteso, repository, false, false, null);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaBinary"},dataProvider="contentTypeBinaryConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaBinary(String tipoTest, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke(tipoTest, responseCodeAtteso, repository, true, false, null);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaBinary"},dataProvider="contentTypeBinaryConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaBinary(String tipoTest, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke(tipoTest, responseCodeAtteso, repository, false, true, null);
		this.restCore.postInvoke(repository);
	}

	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaXML"},dataProvider="contentTypeXMLConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, true, true, contentType);
		this.restCore.postInvoke(repository);
	}

	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_SenzaContenutoRispostaXML"},dataProvider="contentTypeXMLConSenza")
	public void test_SenzaContenutoRichiesta_SenzaContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, false, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaXML"},dataProvider="contentTypeXMLConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, true, false, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaXML"},dataProvider="contentTypeXMLConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaXML(String contentType, int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("xml", responseCodeAtteso, repository, false, true, contentType);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_ConContenutoRispostaMulti"},dataProvider="responseCodeConCon")
	public void test_ConContenutoRichiesta_ConContenutoRispostaMulti(int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("multi", responseCodeAtteso, repository, true, true, null);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_SenzaContenutoRispostaMulti"},dataProvider="responseCodeConSenza")
	public void test_SenzaContenutoRichiesta_SenzaContenutoRispostaMulti(int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("multi", responseCodeAtteso, repository, false, false, null);
		this.restCore.postInvoke(repository);
	}

	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".ConContenutoRichiesta_SenzaContenutoRispostaMulti"},dataProvider="responseCodeConSenza")
	public void test_ConContenutoRichiesta_SenzaContenutoRispostaMulti(int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("multi", responseCodeAtteso, repository, true, false, null);
		this.restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTCore.REST,RESTCore.REST_PD,RESTPDPost.ID_GRUPPO,RESTPDPost.ID_GRUPPO+".SenzaContenutoRichiesta_ConContenutoRispostaMulti"},dataProvider="responseCodeConCon")
	public void test_SenzaContenutoRichiesta_ConContenutoRispostaMulti(int responseCodeAtteso) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		this.restCore.invoke("multi", responseCodeAtteso, repository, false, true, null);
		this.restCore.postInvoke(repository);
	}


}
