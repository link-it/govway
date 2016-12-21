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

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.mail.internet.ContentType;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.FileCache;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.TestFileEntry;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.XMLDiff;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * RESTPost
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class RESTCore {

	public static final String REST_CORE = "REST";
	public static final String REST_PD = "REST.PD";
	public static final String REST_PA = "REST.PA";

	private HttpRequestMethod method;
	private String servizioRichiesto;
	private String portaApplicativaDelegata;
	private boolean isDelegata;
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBase collaborazioneTrasparenteBase;

	public RESTCore(HttpRequestMethod method, boolean delegata) {
		this.method = method;
		this.isDelegata = delegata;
		IDSoggetto idSoggettoMittente = null;
		IDSoggetto idSoggettoDestinatario = null;
		if(this.isDelegata) {
			this.servizioRichiesto = Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore();
			this.portaApplicativaDelegata = CostantiTestSuite.PORTA_DELEGATA_REST_API;
			idSoggettoMittente = CostantiTestSuite.PROXY_SOGGETTO_FRUITORE;
			idSoggettoDestinatario = CostantiTestSuite.PROXY_SOGGETTO_EROGATORE;
		} else {
			this.servizioRichiesto = Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore();
			this.portaApplicativaDelegata = CostantiTestSuite.PORTA_APPLICATIVA_REST_API;
			idSoggettoMittente = CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_ANONIMO;
			idSoggettoDestinatario = CostantiTestSuite.PROXY_SOGGETTO_EROGATORE;
		}
		
		CooperazioneBaseInformazioni info = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(idSoggettoMittente,
				idSoggettoDestinatario,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
		this.collaborazioneTrasparenteBase = 
			new CooperazioneBase(false,MessageType.SOAP_11,  info, 
					org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
					DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance(), this.isDelegata);

	}
	
	
	public void postInvoke(Repository repository) throws TestSuiteException, Exception{

		
		String id=repository.getNext();
		if(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		DatabaseComponent data = null;
		if(this.isDelegata) {
			data = DatabaseProperties.getDatabaseComponentFruitore();
		} else {
			data = DatabaseProperties.getDatabaseComponentErogatore();
		}
		
		if(!this.isDelegata) {
			try {
				Thread.sleep(2000); // in modo da dare il tempo al servizio di Testsuite di fare l'update delle tracce
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try{
			this.collaborazioneTrasparenteBase.testSincrono(data,id, CostantiTestSuite.REST_TIPO_SERVIZIO,
					CostantiTestSuite.SOAP_NOME_SERVIZIO_API, null, !this.isDelegata,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	public void invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, String contentType) throws TestSuiteException, Exception{
		this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, false, contentType);
	}
	
	public void invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, boolean isHttpMethodOverride, String contentType) throws TestSuiteException, Exception{
		
		TestFileEntry fileEntry = FileCache.get(tipoTest);

		try{
		
			// 1. Tipo di richiesta da inviare: XML, JSON, DOC, PDF, ZIP e Multipart
			byte[] richiesta = fileEntry.getBytesRichiesta();
			// Definiire un content type corretto rispetto al tipo di file che si invia
			// Per conoscere il tipo di file corretto è possibile utilizzare l'utility sottostante (spostata in TestFileEntry)
			String contentTypeRichiesta = contentType != null ? contentType : fileEntry.getExtRichiesta();
			
			// 2. Variabile sul tipo di HttpRequestMethod
			// Per sapere se il metodo prevede un input od un output come contenuto è possibile usare la seguente utility
			HttpBodyParameters httpBody = new HttpBodyParameters(this.method, fileEntry.getExtRichiesta());
			boolean contenutoRichiesta = httpBody.isDoOutput();
			
			
			
			boolean rispostaOk = returnCodeAtteso < 299;
			boolean contenutoRisposta =  httpBody.isDoInput() && (isRisposta || !rispostaOk);

			HttpRequest request = new HttpRequest();
			request.setMethod(this.method);
			if(contenutoRichiesta) {
				if(isRichiesta){
					request.setContent(richiesta);
					request.setContentType(contentTypeRichiesta);
//				} else {
//					request.setContent("".getBytes());
//					request.setContentType(MimeTypes.getInstance().getMimeType("txt"));
				}
			}
			
			// Se richiesto dalla porta
			//request.setUsername(username);
			//request.setPassword(password);
			
			// Header HTTP da inviare
			String nomeHeaderHttpInviato = null;
			String valoreHeaderRichiesto = null;
			if(isHttpMethodOverride) {
				nomeHeaderHttpInviato = "X-HTTP-Method-Override";
				valoreHeaderRichiesto = "PUT";
			} else {
				nomeHeaderHttpInviato = "ProvaHeaderRequest";
				valoreHeaderRichiesto = "TEST_"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
			}

			request.addHeader(nomeHeaderHttpInviato, valoreHeaderRichiesto);

			// Header HTTP da ricevere
			String nomeHeaderHttpDaRicevere = "ProvaHeaderResponse";
			String valoreHeaderHttpDaRicevere = "TEST_RESPONSE_"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
			
			String action = null;
			if(contenutoRisposta) {
				action = "echo";
			} else {
				action = "ping";
			}
			
			// String url
			// 3. Costruzione url
			StringBuffer bf = new StringBuffer();
			bf.append(this.servizioRichiesto);
			bf.append(this.portaApplicativaDelegata);
			bf.append("/service/").append(action);
			Properties propertiesURLBased = new Properties();
			propertiesURLBased.put("checkEqualsHttpMethod", this.method.name());
			propertiesURLBased.put("checkEqualsHttpHeader", nomeHeaderHttpInviato + ":" + valoreHeaderRichiesto);
			propertiesURLBased.put("returnHttpHeader", nomeHeaderHttpDaRicevere +":" + valoreHeaderHttpDaRicevere);
			
			if(returnCodeAtteso != 200) {
				propertiesURLBased.put("returnCode", returnCodeAtteso + "");
			}

			if(contenutoRisposta) {
				if(rispostaOk) {
					if(!tipoTest.equals("multi") || !isRichiesta) {
						propertiesURLBased.put("destFileContentType", contentType != null ? contentType : fileEntry.getExtRisposta());
						propertiesURLBased.put("destFile", fileEntry.getFilenameRichiesta());
					}
				} else {
					propertiesURLBased.put("destFileContentType", contentType != null ? contentType : fileEntry.getExtRispostaKo());
					propertiesURLBased.put("destFile", fileEntry.getFilenameRispostaKo());
				}
			}
			
			String urlDaUtilizzare = TransportUtils.buildLocationWithURLBasedParameter(propertiesURLBased, bf.toString());

			request.setUrl(urlDaUtilizzare);
			
			// invocazione
			HttpResponse httpResponse = HttpUtilities.httpInvoke(request);
			
			// Raccolgo identificativo per verifica traccia
			String idMessaggio = httpResponse.getHeader(TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
			Assert.assertTrue(idMessaggio!=null);
			Reporter.log("Ricevuto id ["+idMessaggio+"]");
			repository.add(idMessaggio);
			
			Reporter.log("["+idMessaggio+"] Atteso ["+returnCodeAtteso+"] ritornato ["+httpResponse.getResultHTTPOperation()+"]");
			Assert.assertTrue(returnCodeAtteso == httpResponse.getResultHTTPOperation());
			
			// Controllo header di risposta atteso
			String headerRispostaRitornatoValore = httpResponse.getHeader(nomeHeaderHttpDaRicevere);
			Reporter.log("["+idMessaggio+"] Atteso Header ["+nomeHeaderHttpDaRicevere+"] con valore atteso ["+valoreHeaderHttpDaRicevere+"] e valore ritornato ["+headerRispostaRitornatoValore+"]");
			Assert.assertTrue(headerRispostaRitornatoValore!=null);
			Assert.assertTrue(headerRispostaRitornatoValore.equals(valoreHeaderHttpDaRicevere));
			
			// Controllo risposta
			if(contenutoRisposta){
				
				byte[] contentAttesoRisposta = rispostaOk ? fileEntry.getBytesRichiesta(): fileEntry.getBytesRispostaKo();
				String contentTypeAttesoRisposta = contentType != null ? contentType : (rispostaOk) ? fileEntry.getExtRisposta(): fileEntry.getExtRispostaKo();
				String contentTypeRisposta = httpResponse.getContentType();

				
				if(tipoTest.equals("multi") && rispostaOk) {
					ContentType ctRisp = new ContentType(contentTypeRisposta);
					ContentType ctAttesoRisp = new ContentType(contentTypeAttesoRisposta);
					Assert.assertEquals(ctRisp.getBaseType(), ctAttesoRisp.getBaseType(), "Content-Type del file di risposta ["+ctRisp.getBaseType()+"] diverso da quello atteso ["+ctAttesoRisp.getBaseType()+"]");
					Assert.assertEquals(ctRisp.getParameter("type"), ctAttesoRisp.getParameter("type"), "Content-Type del file di risposta ["+ctRisp.getParameter("type")+"] diverso da quello atteso ["+ctAttesoRisp.getParameter("type")+"]");
						
				} else {
					Assert.assertEquals(contentTypeRisposta, contentTypeAttesoRisposta, "Content-Type del file di risposta ["+contentTypeRisposta+"] diverso da quello atteso ["+contentTypeAttesoRisposta+"]");
				}

				byte[] contentRisposta = httpResponse.getContent();

				if(tipoTest.equals("xml")) {
					XMLDiff xmlDiffEngine = new XMLDiff();
					XMLDiffOptions xmlDiffOptions = new XMLDiffOptions();
					xmlDiffEngine.initialize(XMLDiffImplType.XML_UNIT, xmlDiffOptions);
					Assert.assertTrue(xmlDiffEngine.diff(xmlDiffEngine.getXMLUtils().newDocument(contentRisposta),xmlDiffEngine.getXMLUtils().newDocument(contentAttesoRisposta))
							, "File di risposta ["+new String(contentRisposta)+"]diverso da quello atteso ["+new String(contentAttesoRisposta)+"]: " + xmlDiffEngine.getDifferenceDetails());
				} else if(tipoTest.equals("multi")) {
					if(!isRichiesta && rispostaOk) {
						MimeMultipart mm = new MimeMultipart(new ByteArrayInputStream(contentRisposta), contentTypeRisposta);
						MimeMultipart mmAtteso = new MimeMultipart(new ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getMultipartFileName())), contentTypeAttesoRisposta);
						Assert.assertEquals(mm.countBodyParts(),mmAtteso.countBodyParts());

						for(int i = 0; i < mm.countBodyParts(); i++) {
							Assert.assertEquals(mm.getBodyPart(i).getContentType(),mmAtteso.getBodyPart(i).getContentType());
							Assert.assertEquals(mm.getBodyPart(i).getSize(),mmAtteso.getBodyPart(i).getSize());
//							Assert.assertEquals(mm.getBodyPart(i).getContent(),mmAtteso.getBodyPart(i).getContent());
						}
					} else {
						Assert.assertEquals(contentRisposta,contentAttesoRisposta, "File di risposta ["+new String(contentRisposta)+"]diverso da quello atteso ["+new String(contentAttesoRisposta)+"]");
					}
					
				} else {
					Assert.assertEquals(contentRisposta,contentAttesoRisposta, "File di risposta ["+new String(contentRisposta)+"]diverso da quello atteso ["+new String(contentAttesoRisposta)+"]");
				}
				
			}
	
			
		}catch(Exception e){
			throw e;
		}
		
		
	}
	
}
