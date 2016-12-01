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

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RESTPost
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class RESTPost {

	private final static String ID_GRUPPO = "REST.POST";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
			false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneTrasparenteBase = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.info, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance());
	
	
	
	
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
	
	
	
	
	
	
	Repository repository_RestPost_200_SenzaContenutoRichiesta_ConContenutoRisposta=new Repository();
	@Test(groups={RESTPost.ID_GRUPPO,RESTPost.ID_GRUPPO+".200_SenzaContenutoRichiesta_ConContenutoRisposta"})
	public void invoke_RestPost_200_SenzaContenutoRichiesta_ConContenutoRisposta() throws TestSuiteException, Exception{
		
		try{
		
			// File (Può essere utilizzate per la richiesta e/o per la risposta) 
			String pathAssolutoFile = Utilities.testSuiteProperties.getJSONFileName();
			
			// 1. Tipo di richiesta da inviare: XML, JSON, DOC, PDF, ZIP e anche vedere di inviare un Multipart (il multipart va aggiunto.... chiedermi come fare)
			byte[] richiesta = FileSystemUtilities.readBytesFromFile(pathAssolutoFile);
			// Definiire un content type corretto rispetto al tipo di file che si invia
			// Per conoscere il tipo di file corretto è possibile utilizzare l'utility sottostante
			String contentTypeRichiesta = MimeTypes.getInstance().getMimeType("json"); // fornire l'estensione del file in minuscolo
			
			// 2. Variabile sul tipo di HttpRequestMethod
			// Per sapere se il metodo prevede un input od un output come contenuto è possibile usare la seguente utility
			HttpRequestMethod method = HttpRequestMethod.POST;
			HttpBodyParameters httpBody = new HttpBodyParameters(method, contentTypeRichiesta);
			boolean contenutoRichiesta = httpBody.isDoOutput();
			boolean contenutoRisposta =  httpBody.isDoInput();

			HttpRequest request = new HttpRequest();
			request.setMethod(method);
			if(contenutoRichiesta){
				request.setContent(richiesta);
				request.setContentType(contentTypeRichiesta);
			}
			
			// Se richiesto dalla porta
			//request.setUsername(username);
			//request.setPassword(password);
			
			// Header HTTP da inviare
			String nomeHeaderHttpInviato = "ProvaHeaderRequest";
			String valoreHeaderRichiesto = "TEST_"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
			request.addHeader(nomeHeaderHttpInviato, valoreHeaderRichiesto);
 			
			// Header HTTP da ricevere
			String nomeHeaderHttpDaRicevere = "ProvaHeaderResponse";
			String valoreHeaderHttpDaRicevere = "TEST_RESPONSE_"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
			
			// String url
			// 3. Costruzione url
			// NOTA: il file che si invia e quello che si riceve in alcuni test è differente
			StringBuffer bf = new StringBuffer();
			bf.append(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()); // oppure usare RicezioneBuste per la PA
			bf.append(CostantiTestSuite.PORTA_DELEGATA_REST_API);  // oppure usare PORTA_APPLICATIVA_REST_API per la PA
			bf.append("/service/echo?").
				append("checkEqualsHttpMethod=").append(method.name()).
				append("&").
				append("checkEqualsHttpHeader=").append(nomeHeaderHttpInviato).append(":").append(valoreHeaderRichiesto).
				append("&").
				append("destFile=").append(pathAssolutoFile).
				append("&").
				append("destFileContentType=").append(contentTypeRichiesta).
				append("&").
				append("returnHttpHeader=").append(nomeHeaderHttpDaRicevere).append(":").append(valoreHeaderHttpDaRicevere);
			request.setUrl(bf.toString());
			
			// invocazione
			HttpResponse httpResponse = HttpUtilities.httpInvoke(request);
			
			// Raccolgo identificativo per verifica traccia
			String idMessaggio = httpResponse.getHeader(TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
			Assert.assertTrue(idMessaggio!=null);
			Reporter.log("Ricevuto id ["+idMessaggio+"]");
			this.repository_RestPost_200_SenzaContenutoRichiesta_ConContenutoRisposta.add(idMessaggio);
			
			// Controllo return code atteso dal test
			int returnCodeAtteso = 200;
			Reporter.log("["+idMessaggio+"] Atteso ["+returnCodeAtteso+"] ritornato ["+httpResponse.getResultHTTPOperation()+"]");
			Assert.assertTrue(returnCodeAtteso == httpResponse.getResultHTTPOperation());
			
			// Controllo header di risposta atteso
			String headerRispostaRitornatoValore = httpResponse.getHeader(nomeHeaderHttpDaRicevere);
			Reporter.log("["+idMessaggio+"] Atteso Header ["+nomeHeaderHttpDaRicevere+"] con valore atteso ["+valoreHeaderHttpDaRicevere+"] e valore ritornato ["+headerRispostaRitornatoValore+"]");
			Assert.assertTrue(headerRispostaRitornatoValore!=null);
			Assert.assertTrue(headerRispostaRitornatoValore.equals(valoreHeaderHttpDaRicevere));
			
			// Controllo risposta
			if(contenutoRisposta){
				
				// TODO: controllare uguaglianza file da ricevere
				
				// TODO: controllare uguaglianza content-type da ricevere
				
			}
	
			
		}catch(Exception e){
			throw e;
		}
		
		
	}
	@DataProvider (name="provider_200_SenzaContenutoRichiesta_ConContenutoRisposta")
	public Object[][]testOneWay() throws Exception{
		String id=this.repository_RestPost_200_SenzaContenutoRichiesta_ConContenutoRisposta.getNext();
		if(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={RESTPost.ID_GRUPPO,RESTPost.ID_GRUPPO+".200_SenzaContenutoRichiesta_ConContenutoRisposta"},
			dataProvider="provider_200_SenzaContenutoRichiesta_ConContenutoRisposta",
			dependsOnMethods={"invoke_RestPost_200_SenzaContenutoRichiesta_ConContenutoRisposta"})
	public void testOneWay(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBase.testSincrono(data,id, CostantiTestSuite.REST_TIPO_SERVIZIO,
					CostantiTestSuite.SOAP_NOME_SERVIZIO_API, null, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
}
