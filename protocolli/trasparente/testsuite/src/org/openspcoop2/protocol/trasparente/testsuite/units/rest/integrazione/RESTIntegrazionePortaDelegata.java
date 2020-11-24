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
			String diag = "Il contenuto applicativo della risposta ricevuta non è processabile: Non è stato possibile comprendere come trattare il messaggio ricevuto (Content-Type: "+contentType+"): "+erroreAtteso;
			Reporter.log("Cerco diagnostico "+diag+" nei log ...");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, diag));		
		}finally{
			dataMsg.close();
		}
	}
}
