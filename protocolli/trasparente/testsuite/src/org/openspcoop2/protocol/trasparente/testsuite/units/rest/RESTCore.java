/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.mail.internet.ContentType;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.FileCache;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PosizioneCredenziale;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.TestFileEntry;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTCore {

	public static final String REST = "REST";
	public static final String REST_PD = "REST.PD";
	public static final String REST_PD_LOCAL_FORWARD = "REST.PD.LOCAL_FORWARD";
	public static final String REST_PA = "REST.PA";

	private static final String MSG_LOCAL_FORWARD = "Modalità 'local-forward' attiva, messaggio ruotato direttamente verso la porta applicativa";

	private HttpRequestMethod method;
	private String servizioRichiesto;
	private String portaApplicativaDelegata;
	private String basicUsername;
	private String basicPassword;
	
	private String apiKey;
	private String appId;
	private boolean appIdEnabled;
	private PosizioneCredenziale posizioneApiKey = PosizioneCredenziale.HEADER;
	private PosizioneCredenziale posizioneAppId = PosizioneCredenziale.HEADER;
	private String nomePosizioneApiKey = null;
	private String nomePosizioneAppId = null;

	private RUOLO ruolo;
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBase collaborazioneTrasparenteBase;

	public enum RUOLO {PORTA_DELEGATA, PORTA_APPLICATIVA, PORTA_DELEGATA_LOCAL_FORWARD, PORTA_APPLICATIVA_TEST_CREDENZIALI_BRUCIATE}

	public RESTCore(HttpRequestMethod method, RUOLO ruolo)  {
		this.method = method;
		this.ruolo = ruolo;
		IDSoggetto idSoggettoMittente = null;
		IDSoggetto idSoggettoDestinatario = null;
		switch(this.ruolo) {
		case PORTA_APPLICATIVA:
			this.servizioRichiesto = Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore();
			this.portaApplicativaDelegata = CostantiTestSuite.PORTA_APPLICATIVA_REST_API;
			idSoggettoMittente = CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_ANONIMO;
			idSoggettoDestinatario = CostantiTestSuite.PROXY_SOGGETTO_EROGATORE;
			break;
		case PORTA_DELEGATA:
			this.servizioRichiesto = Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore();
			this.portaApplicativaDelegata = CostantiTestSuite.PORTA_DELEGATA_REST_API;
			idSoggettoMittente = CostantiTestSuite.PROXY_SOGGETTO_FRUITORE;
			idSoggettoDestinatario = CostantiTestSuite.PROXY_SOGGETTO_EROGATORE;
			break;
		case PORTA_DELEGATA_LOCAL_FORWARD:
			this.servizioRichiesto = Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore();
			this.portaApplicativaDelegata = CostantiTestSuite.PORTA_DELEGATA_REST_API_LOCAL_FORWARD;
			idSoggettoMittente = CostantiTestSuite.PROXY_SOGGETTO_FRUITORE;
			idSoggettoDestinatario = CostantiTestSuite.PROXY_SOGGETTO_EROGATORE;
			break;
		case PORTA_APPLICATIVA_TEST_CREDENZIALI_BRUCIATE:
			this.servizioRichiesto = Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore();
			this.portaApplicativaDelegata = CostantiTestSuite.PORTA_APPLICATIVA_REST_API;
			idSoggettoMittente = CostantiTestSuite.PROXY_SOGGETTO_TEST_CREDENZIALI_BRUCIATE;
			idSoggettoDestinatario = CostantiTestSuite.PROXY_SOGGETTO_EROGATORE;
			break;
		default:
			break;
		
		}
		
		CooperazioneBaseInformazioni info = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(idSoggettoMittente,
				idSoggettoDestinatario,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
		this.collaborazioneTrasparenteBase = 
			new CooperazioneBase(false,MessageType.SOAP_11,  info, 
					org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
					DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance(), this.ruolo.equals(RUOLO.PORTA_DELEGATA));

	}
	public void setPortaApplicativaDelegata(String portaApplicativaDelegata) {
		this.portaApplicativaDelegata = portaApplicativaDelegata;
	}
	public void setCredenziali(String username,String password) {
		this.basicUsername = username;
		this.basicPassword = password;
	}
	
	public void setCredenzialiApiKey(String username, String password, PosizioneCredenziale posizioneApiKey) throws Exception {
		setCredenzialiApiKey(username, password, posizioneApiKey, null);
	}
	public void setCredenzialiApiKey(String username, String password, PosizioneCredenziale posizioneApiKey, String nomePosizioneApiKey) throws Exception {
		this.setApiKey(ApiKeyUtilities.encodeApiKey(username, password), posizioneApiKey, nomePosizioneApiKey);
	}
	
	public void setCredenzialiMultipleApiKey(String username, String password, PosizioneCredenziale posizioneApiKey, PosizioneCredenziale posizioneAppId) throws Exception {
		setCredenzialiMultipleApiKey(username, password, posizioneApiKey, null, posizioneAppId, null);
	}
	public void setCredenzialiMultipleApiKey(String username, String password, 
			PosizioneCredenziale posizioneApiKey, String nomePosizioneApiKey,
			PosizioneCredenziale posizioneAppId, String nomePosizioneAppId) throws Exception {
		this.setApiKey(ApiKeyUtilities.encodeMultipleApiKey(password), posizioneApiKey, nomePosizioneApiKey);
		this.setAppId(username, posizioneAppId, nomePosizioneAppId);
	}
	
	
	public void setApiKey(String apiKey, PosizioneCredenziale posizioneApiKey) {
		this.setApiKey(apiKey, posizioneApiKey, null);
	}
	public void setApiKey(String apiKey, PosizioneCredenziale posizioneApiKey, String nomePosizioneApiKey) {
		this.apiKey = apiKey;
		this.posizioneApiKey = posizioneApiKey;
		if(this.posizioneApiKey==null) {
			this.posizioneApiKey = PosizioneCredenziale.HEADER;
		}
		this.nomePosizioneApiKey = nomePosizioneApiKey;
		if(this.nomePosizioneApiKey==null) {
			switch (this.posizioneApiKey) {
			case QUERY:
				this.nomePosizioneApiKey= ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_API_KEY;
				break;
			case HEADER:
				this.nomePosizioneApiKey= ParametriAutenticazioneApiKey.DEFAULT_HEADER_API_KEY;
				break;
			case COOKIE:
				this.nomePosizioneApiKey= ParametriAutenticazioneApiKey.DEFAULT_COOKIE_API_KEY;
				break;
			}
		}
	}
	public void setAppId(String appId, PosizioneCredenziale posizioneAppId) {
		this.setAppId(appId, posizioneAppId, null);
	}
	public void setAppId(String appId, PosizioneCredenziale posizioneAppId, String nomePosizioneAppId) {
		this.appId = appId;
		this.posizioneAppId = posizioneAppId;
		if(this.posizioneAppId==null) {
			this.posizioneAppId = PosizioneCredenziale.HEADER;
		}
		this.nomePosizioneAppId = nomePosizioneAppId;
		if(this.nomePosizioneAppId==null) {
			switch (this.posizioneAppId) {
			case QUERY:
				this.nomePosizioneAppId= ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_APP_ID;
				break;
			case HEADER:
				this.nomePosizioneAppId= ParametriAutenticazioneApiKey.DEFAULT_HEADER_APP_ID;
				break;
			case COOKIE:
				this.nomePosizioneAppId= ParametriAutenticazioneApiKey.DEFAULT_COOKIE_APP_ID;
				break;
			}
		}
		this.appIdEnabled = true;
	}
	
	public void postInvokeCheckHeader(HttpResponse httpResponse, HashMap<String, String> headers) throws TestSuiteException, Exception{
		
		Iterator<String> itKeys = headers.keySet().iterator();
		while (itKeys.hasNext()) {
			String hdrName = (String) itKeys.next();
			String hdrValue = headers.get(hdrName);
			
			String trovato = httpResponse.getHeaderFirstValue(hdrName);
			
			if(hdrValue!=null) {
				Reporter.log("Atteso header '"+hdrName+"' ["+hdrValue+"] trovato ["+trovato+"]");
				Assert.assertTrue(trovato!=null);
				Assert.assertTrue(trovato.equals(hdrValue));
			}
			else {
				Reporter.log("Non atteso header ["+hdrName+"] trovato ["+trovato+"]");
				Assert.assertTrue(trovato==null);
			}
		}
		
	}
	
	public String postInvokeCheckExistsHeader(HttpResponse httpResponse, String hdrName) throws TestSuiteException, Exception{
		String trovato = httpResponse.getHeaderFirstValue(hdrName);
		if(trovato!=null) {
			Reporter.log("Atteso header '"+hdrName+"', trovato con valore: ["+trovato+"]");
		}
		else {
			Reporter.log("Atteso header '"+hdrName+"', non trovato");
		}
		Assert.assertTrue(trovato!=null);
		return trovato;
	}
	public String postInvokeCheckNotExistsHeader(HttpResponse httpResponse, String hdrName) throws TestSuiteException, Exception{
		String trovato = httpResponse.getHeaderFirstValue(hdrName);
		if(trovato!=null) {
			Reporter.log("Non atteso header '"+hdrName+"', trovato con valore: ["+trovato+"]");
		}
		else {
			Reporter.log("Non atteso header '"+hdrName+"', non trovato");
		}
		Assert.assertTrue(trovato==null);
		return trovato;
	}
	
	public void postInvoke(Repository repository) throws TestSuiteException, Exception{
		this.postInvoke(repository, false);
	}
	public void postInvoke(Repository repository, boolean checkNotIsArrived) throws TestSuiteException, Exception{
		this.postInvoke(repository, false,
				CostantiTestSuite.REST_TIPO_SERVIZIO,
				CostantiTestSuite.SOAP_NOME_SERVIZIO_API, null);
	}
	public void postInvoke(Repository repository,
			String tipoServizio, String nomeServizio, String azione) throws TestSuiteException, Exception{
		this.postInvoke(repository, false,
				tipoServizio, nomeServizio, azione);
	}
	public void postInvoke(Repository repository, boolean checkNotIsArrived,
			String tipoServizio, String nomeServizio, String azione) throws TestSuiteException, Exception{
	
		
		String id=repository.getNext();
		if(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		DatabaseComponent data = null;
		boolean isDelegata = this.ruolo.equals(RUOLO.PORTA_DELEGATA);
		if(org.openspcoop2.protocol.trasparente.testsuite.units.utils.CooperazioneTrasparenteBase.protocolloEmetteTracce) {
			if(isDelegata) {
				data = DatabaseProperties.getDatabaseComponentFruitore();
			} else {
				data = DatabaseProperties.getDatabaseComponentErogatore();
			}
		}
		
		try {
			//Thread.sleep(2000); // in modo da dare il tempo al servizio di Testsuite di fare l'update delle tracce
			// provo a ridurre il tempo di sleep, per far terminare prima la testsuite
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(org.openspcoop2.protocol.trasparente.testsuite.units.utils.CooperazioneTrasparenteBase.protocolloEmetteTracce) {
			try{
				//boolean checkServizioApplicativo = !isDelegata;
				boolean checkServizioApplicativo = true; // lo voglio controllare sempre
				if(checkNotIsArrived) {
					checkServizioApplicativo = false;
				}
				this.collaborazioneTrasparenteBase.testSincrono(data,id, tipoServizio, nomeServizio, azione, checkServizioApplicativo,null); 
				
				if(checkNotIsArrived) {
					Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
				}
				
			}catch(Exception e){
				throw e;
			}finally{
				data.close();
			}
		}
	}

	public void postInvokeLocalForward(Repository repository, boolean isErrore) throws TestSuiteException, Exception{

		try {
			Thread.sleep(1000); // in modo da dare il tempo all'ultimo diagnostico di essere scritto su database
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String id=repository.getNext();
		if(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		DatabaseMsgDiagnosticiComponent msgDiag = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
		DatabaseComponent data = DatabaseProperties.getDatabaseComponentFruitore();
		
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

//			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==isErrore);

			if(isErrore) {
				Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007013","001006"));
			} else {
				Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}

	}
	
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			String contentType) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				false, contentType, false, false, 
				null, null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			String contentType,
			HashMap<String, String> headersRequest) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				false, contentType, false, false,
				headersRequest, null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			String contentType,
			HashMap<String, String> headersRequest, HashMap<String, String> propertiesRequest) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				false, contentType, false, false,
				headersRequest, propertiesRequest);
	}
	
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, false, false,
				null, null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType,
			HashMap<String, String> headersRequest) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, false, false,
				headersRequest, null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType,
			HashMap<String, String> headersRequest, HashMap<String, String> propertiesRequest) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, false, false,
				headersRequest, propertiesRequest);
	}
	
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType, boolean authorizationError) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, authorizationError, false,
				null, null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType, boolean authorizationError,
			HashMap<String, String> headersRequest) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, authorizationError, false,
				headersRequest, null);
	}
	public HttpResponse invokeAuthenticationError(String tipoTest, int returnCodeAtteso, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType, HashMap<String, String> headersRequest) throws TestSuiteException, Exception{
		return this.invoke(tipoTest, returnCodeAtteso, null, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, false, true,
				headersRequest, null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType, boolean authorizationError, boolean authenticationError,
			HashMap<String, String> headersRequest, HashMap<String, String> propertiesRequest) throws TestSuiteException, Exception{
		return invoke(tipoTest, returnCodeAtteso, repository, isRichiesta, isRisposta, 
				isHttpMethodOverride, contentType, authorizationError, authenticationError,
				headersRequest, propertiesRequest,
				null);
	}
	public HttpResponse invoke(String tipoTest, int returnCodeAtteso, Repository repository, boolean isRichiesta, boolean isRisposta, 
			boolean isHttpMethodOverride, String contentType, boolean authorizationError, boolean authenticationError,
			HashMap<String, String> headersRequest, HashMap<String, String> propertiesRequest,
			String restContext) throws TestSuiteException, Exception{
		
		TestFileEntry fileEntry = null;
		if(!"preflight".equals(tipoTest) && !"empty-payload".equals(tipoTest)) {
			fileEntry = FileCache.get(tipoTest);
		}

		try{
		
			// 1. Tipo di richiesta da inviare: XML, JSON, DOC, PDF, ZIP e Multipart
			byte[] richiesta = null;
			String extRichiesta = null;
			if(fileEntry!=null) {
				richiesta = fileEntry.getBytesRichiesta();
				extRichiesta = fileEntry.getExtRichiesta();
			}
			// Definiire un content type corretto rispetto al tipo di file che si invia
			// Per conoscere il tipo di file corretto è possibile utilizzare l'utility sottostante (spostata in TestFileEntry)
			String contentTypeRichiesta = contentType != null ? contentType : extRichiesta;
			
			// 2. Variabile sul tipo di HttpRequestMethod
			// Per sapere se il metodo prevede un input od un output come contenuto è possibile usare la seguente utility
			HttpBodyParameters httpBody = new HttpBodyParameters(this.method, contentTypeRichiesta);
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
			
			if(this.basicUsername!=null && this.basicPassword!=null) {
				request.setUsername(this.basicUsername);
				request.setPassword(this.basicPassword);
			}
			
			// Se richiesto dalla porta
			//request.setUsername(username);
			//request.setPassword(password);
			
			// Header HTTP da inviare
			String nomeHeaderHttpInviato = null;
			String valoreHeaderRichiesto = null;
			if(headersRequest!=null) {
				Iterator<String> itKeys = headersRequest.keySet().iterator();
				while (itKeys.hasNext()) {
					String hdrName = (String) itKeys.next();
					String hdrValue = headersRequest.get(hdrName);
					request.addHeader(hdrName, hdrValue);
				}
			}
			else {
				if(isHttpMethodOverride) {
					nomeHeaderHttpInviato = "X-HTTP-Method-Override";
					valoreHeaderRichiesto = "PUT";
				} else {
					nomeHeaderHttpInviato = "ProvaHeaderRequest";
					valoreHeaderRichiesto = "TEST_"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
				}
				request.addHeader(nomeHeaderHttpInviato, valoreHeaderRichiesto);
			}
			if(this.apiKey!=null && PosizioneCredenziale.HEADER.equals(this.posizioneApiKey)){
				request.addHeader(this.nomePosizioneApiKey, this.apiKey);
			}
			if(this.appIdEnabled && this.appId!=null && PosizioneCredenziale.HEADER.equals(this.posizioneAppId)){
				request.addHeader(this.nomePosizioneAppId, this.appId);
			}

			// Header HTTP da ricevere
			String nomeHeaderHttpDaRicevere = "ProvaHeaderResponse";
			String valoreHeaderHttpDaRicevere = "TEST_RESPONSE_"+org.openspcoop2.utils.id.IDUtilities.getUniqueSerialNumber();
			if(propertiesRequest!=null && !propertiesRequest.isEmpty()) {
				valoreHeaderHttpDaRicevere = "TEST_RESPONSE"; // non dinamico, usato in response caching
			}
			
			// Cookie
			StringBuilder sbCookie = new StringBuilder();
			if(this.apiKey!=null && (this.portaApplicativaDelegata!=null && 
					(this.portaApplicativaDelegata.toLowerCase().contains("apikey") || this.portaApplicativaDelegata.toLowerCase().contains("appid")))) {
				if(sbCookie.length()>0) {
					sbCookie.append(HttpConstants.COOKIE_SEPARATOR);
				}
				sbCookie.append(CostantiTestSuite.COOKIE_CUSTOM1_NAME);
				sbCookie.append(HttpConstants.COOKIE_NAME_VALUE_SEPARATOR);
				sbCookie.append(CostantiTestSuite.COOKIE_CUSTOM1_VALUE);
			}
			if(this.apiKey!=null && PosizioneCredenziale.COOKIE.equals(this.posizioneApiKey)){
				if(sbCookie.length()>0) {
					sbCookie.append(HttpConstants.COOKIE_SEPARATOR);
				}
				sbCookie.append(this.nomePosizioneApiKey);
				sbCookie.append(HttpConstants.COOKIE_NAME_VALUE_SEPARATOR);
				sbCookie.append(URLEncoder.encode(this.apiKey, Charset.UTF_8.getValue()));
			}
			if(this.appIdEnabled && this.appId!=null && PosizioneCredenziale.COOKIE.equals(this.posizioneAppId)){
				if(sbCookie.length()>0) {
					sbCookie.append(HttpConstants.COOKIE_SEPARATOR);
				}
				sbCookie.append(this.nomePosizioneAppId);
				sbCookie.append(HttpConstants.COOKIE_NAME_VALUE_SEPARATOR);
				sbCookie.append(URLEncoder.encode(this.appId, Charset.UTF_8.getValue()));
			}
			if(this.apiKey!=null && (this.portaApplicativaDelegata!=null && 
					(this.portaApplicativaDelegata.toLowerCase().contains("apikey") || this.portaApplicativaDelegata.toLowerCase().contains("appid")))) {
				if(sbCookie.length()>0) {
					sbCookie.append(HttpConstants.COOKIE_SEPARATOR);
				}
				sbCookie.append(CostantiTestSuite.COOKIE_CUSTOM2_NAME);
				sbCookie.append(HttpConstants.COOKIE_NAME_VALUE_SEPARATOR);
				sbCookie.append(CostantiTestSuite.COOKIE_CUSTOM2_VALUE);
			}
			if(sbCookie.length()>0) {
				request.addHeader(HttpConstants.COOKIE, sbCookie.toString());
			}
			
			String action = null;
			if(contenutoRisposta) {
				action = "echo";
			} else {
				action = "ping";
			}
			
			// String url
			// 3. Costruzione url
			StringBuilder bf = new StringBuilder();
			bf.append(this.servizioRichiesto);
			bf.append(this.portaApplicativaDelegata);
			if(!"soap11".equals(tipoTest) && !"soap12".equals(tipoTest)) {
				if(restContext!=null) {
					bf.append(restContext);
				}
				else {
					bf.append("/service/").append(action);
				}
			}
			Map<String,String> propertiesURLBased = new HashMap<>();
			if(propertiesRequest!=null && !propertiesRequest.isEmpty()) {
				propertiesURLBased.putAll(propertiesRequest);
			}

			if(this.apiKey!=null && PosizioneCredenziale.QUERY.equals(this.posizioneApiKey)){
				propertiesURLBased.put(this.nomePosizioneApiKey, this.apiKey);
			}
			if(this.appIdEnabled && this.appId!=null && PosizioneCredenziale.QUERY.equals(this.posizioneAppId)){
				propertiesURLBased.put(this.nomePosizioneAppId, this.appId);
			}
			
			propertiesURLBased.put("checkEqualsHttpMethod", this.method.name());
			if(nomeHeaderHttpInviato!=null) {
				propertiesURLBased.put("checkEqualsHttpHeader", nomeHeaderHttpInviato + ":" + valoreHeaderRichiesto);
			}
							
			boolean redirect = false;
			String headerLocation = HttpConstants.REDIRECT_LOCATION;
			if(returnCodeAtteso != 200) {
				if(returnCodeAtteso==301 || returnCodeAtteso==303  || returnCodeAtteso==304|| returnCodeAtteso==307 ||
						returnCodeAtteso==201 || // aggiunto per verificare che anche in questo caso sia corretto header Location (proxy pass revers)
						returnCodeAtteso==204  // aggiunto per verificare che anche in caso sia corretto header Content-Location (proxy pass revers)
				) {
					// gli altri return code (302) li gestisco normali per vedere cmq di essere trasparente
					redirect = true;
					propertiesURLBased.put("redirect", "true");
					propertiesURLBased.put("redirectReturnCode", returnCodeAtteso+"");
					if(returnCodeAtteso==303) {
						propertiesURLBased.put("redirectContext", "altroContestoApplicativo/test");
					}
					else if(returnCodeAtteso==304) {
						propertiesURLBased.put("redirectContext", "/");
					}
					else if(returnCodeAtteso==307) {
						propertiesURLBased.put("redirectAbsoluteUrl", "false");
					}
					
					if(returnCodeAtteso==204) {
						headerLocation = HttpConstants.CONTENT_LOCATION;
						propertiesURLBased.put("redirectHeaderLocation", headerLocation);
					}
					//else { viene tornato HttpConstants.REDIRECT_LOCATION }
				}
				else {
					if(!authorizationError && !authenticationError) {
						propertiesURLBased.put("returnCode", returnCodeAtteso + "");
					}
				}
			}

			if(!redirect && !authorizationError && !authenticationError) {
				propertiesURLBased.put("returnHttpHeader", nomeHeaderHttpDaRicevere +":" + valoreHeaderHttpDaRicevere);
			}
			
			if(!redirect && contenutoRisposta) {
				if(rispostaOk) {
					if( (!tipoTest.equals("multi")) && (!tipoTest.equals("multi-mixed")) && (!tipoTest.equals("empty-payload")) ) {
						propertiesURLBased.put("destFileContentType", contentType != null ? contentType : fileEntry.getExtRisposta());
						propertiesURLBased.put("destFile", fileEntry.getFilenameRichiesta());
					}
					else {
						if(!isRichiesta) {
							// Altrimenti indietro non saprei cosa tornare
							propertiesURLBased.put("destFile", fileEntry.getFilenameRichiesta());
							if(tipoTest.equals("multi")) {
								propertiesURLBased.put("destFileContentTypeMultipartSubType", "related");
							}
							else {
								propertiesURLBased.put("destFileContentTypeMultipartSubType", "mixed");
							}
							propertiesURLBased.put("destFileContentTypeMultipartParameterType", "text/xml");
						}
					}
				} else {
					if(!propertiesURLBased.containsKey("fault") && !propertiesURLBased.containsKey("problem")) {
						propertiesURLBased.put("destFileContentType", contentType != null ? contentType : fileEntry.getExtRispostaKo());
						propertiesURLBased.put("destFile", fileEntry.getFilenameRispostaKo());
					}
				}
			}
			
			String urlBase = bf.toString();
			if(returnCodeAtteso==307) {
				// Aggiungo ulteriori path per verificare proxyPass
				if(urlBase.endsWith("/")==false) {
					urlBase = urlBase + "/ALTRO/PARAMETRO";
				}
			}
			String urlDaUtilizzare = buildUrl(propertiesURLBased, urlBase);

			Reporter.log("URL: "+urlDaUtilizzare);
			request.setUrl(urlDaUtilizzare);
			
			// invocazione
			HttpResponse httpResponse = HttpUtilities.httpInvoke(request);
			
			Reporter.log("Atteso ["+returnCodeAtteso+"] ritornato ["+httpResponse.getResultHTTPOperation()+"], raccolgo id ...");
			
			// Raccolgo identificativo per verifica traccia
			String idMessaggio = null;
			if(!"preflight".equals(tipoTest)) {
				if(httpResponse.getHeadersValues()!=null) {
					Reporter.log("Headers: ("+httpResponse.getHeadersValues().keySet()+")");
				}
				if(!authenticationError) {
					Reporter.log("Leggo id da header ["+TestSuiteProperties.getInstance().getIdMessaggioTrasporto()+"]");
					idMessaggio = httpResponse.getHeaderFirstValue(TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
					Assert.assertTrue(idMessaggio!=null);
					Reporter.log("Ricevuto id ["+idMessaggio+"]");
					repository.add(idMessaggio);
				}
				else {
					Reporter.log("Leggo id transazione da header ["+TestSuiteProperties.getInstance().getIDTransazioneTrasporto()+"]");
					idMessaggio = "AuthnError-"+httpResponse.getHeaderFirstValue(TestSuiteProperties.getInstance().getIDTransazioneTrasporto());
				}
			}
			else {
				idMessaggio = "Preflight-"+httpResponse.getHeaderFirstValue(TestSuiteProperties.getInstance().getIDTransazioneTrasporto());
			}
			
			Reporter.log("["+idMessaggio+"] Atteso ["+returnCodeAtteso+"] ritornato ["+httpResponse.getResultHTTPOperation()+"]");
			if(returnCodeAtteso != httpResponse.getResultHTTPOperation()) {
				if(httpResponse.getContent()!=null) {
					Reporter.log("["+idMessaggio+"] body ["+new String(httpResponse.getContent())+"]");
				}
				else {
					Reporter.log("["+idMessaggio+"] body empty");
				}
			}
			Assert.assertTrue(returnCodeAtteso == httpResponse.getResultHTTPOperation());
			
			// Controllo risposta redirect
			if(redirect) {
				String location = httpResponse.getHeaderFirstValue(headerLocation);
				Reporter.log("Location ricevuta ["+location+"]");
				Assert.assertTrue(location!=null);
				String urlBaseSenzaHostPort = urlBase.substring(urlBase.indexOf("/govway/"), urlBase.length());
				String urlAttesa = null;
				
				if(returnCodeAtteso==303) {
					urlAttesa = "/altroContestoApplicativo/test";
				}
				else if(returnCodeAtteso==304) {
					urlAttesa = "/";
				}
				else if(returnCodeAtteso==307) {
					urlAttesa = buildUrl(new HashMap<>(), urlBaseSenzaHostPort);
				}
				else {
					urlAttesa = buildUrl(new HashMap<>(), urlBase);
				}
				if(urlAttesa.contains("govway/api/in/")) {
					//urlAttesa = urlAttesa.replace("govway/api/in/", "govway/in/"); // la url ritornata e' normalizzata
					urlAttesa = urlAttesa.replace("govway/api/in/", "govway/"); // la url ritornata e' normalizzata
				}
				if(urlAttesa.contains("govway/api/out/")) {
					urlAttesa = urlAttesa.replace("govway/api/out/", "govway/out/"); // la url ritornata e' normalizzata
				}
				
				if(returnCodeAtteso!=303 && returnCodeAtteso!=304) {
					Reporter.log("Location ["+location+"] atteso ["+urlAttesa+"] verifico uguaglianza");
					boolean check = location.equals(urlAttesa);
					if(!check) {
						System.out.println("====");
						System.out.println("Location ["+location+"] ");
						System.out.println("!equals urlAttesa ["+urlAttesa+"] ");
					}
					Assert.assertTrue(check); // verifico proxyPassReverse
				}
				else {
					// verifico proxyPassReverse non abbia fatto modifiche essendo un contesto diverso
					Reporter.log("Location ["+location+"] atteso ["+urlAttesa+"] verifico termina come atteso");
					Assert.assertTrue(!location.equals(urlAttesa)); 
					Assert.assertTrue(location.endsWith(urlAttesa)); 
				}
			}
			
			// Controllo header di risposta atteso
			if(!redirect && !authorizationError && !authenticationError &&
					!"soap11".equals(tipoTest) && !"soap12".equals(tipoTest) && !"preflight".equals(tipoTest)) {
				String headerRispostaRitornatoValore = httpResponse.getHeaderFirstValue(nomeHeaderHttpDaRicevere);
				Reporter.log("["+idMessaggio+"] Atteso Header ["+nomeHeaderHttpDaRicevere+"] con valore atteso ["+valoreHeaderHttpDaRicevere+"] e valore ritornato ["+headerRispostaRitornatoValore+"]");
				Assert.assertTrue(headerRispostaRitornatoValore!=null);
				Assert.assertTrue(headerRispostaRitornatoValore.equals(valoreHeaderHttpDaRicevere));
			}
			
			// Controllo risposta
			if("empty-payload".equals(tipoTest)) {
				contenutoRisposta = false;
			}
			if(!redirect && contenutoRisposta && !authorizationError && !authenticationError){
				
				byte[] contentAttesoRisposta = rispostaOk ? fileEntry.getBytesRichiesta(): fileEntry.getBytesRispostaKo();
				String contentTypeAttesoRisposta = contentType != null ? contentType : (rispostaOk) ? fileEntry.getExtRisposta(): fileEntry.getExtRispostaKo();
				String contentTypeRisposta = httpResponse.getContentType();

				
				if((tipoTest.equals("multi") || tipoTest.equals("multi-mixed")) && rispostaOk) {
					ContentType ctRisp = new ContentType(contentTypeRisposta);
					ContentType ctAttesoRisp = new ContentType(contentTypeAttesoRisposta);
					Assert.assertEquals(ctRisp.getBaseType(), ctAttesoRisp.getBaseType(), "Content-Type del file di risposta ["+ctRisp.getBaseType()+"] diverso da quello atteso ["+ctAttesoRisp.getBaseType()+"]");
					Assert.assertEquals(ctRisp.getParameter("type"), ctAttesoRisp.getParameter("type"), "Content-Type del file di risposta ["+ctRisp.getParameter("type")+"] diverso da quello atteso ["+ctAttesoRisp.getParameter("type")+"]");
						
				} else {
					// viene appiattito, e' uguale
					if(contentTypeAttesoRisposta.contains("; charset=")) {
						contentTypeAttesoRisposta = contentTypeAttesoRisposta.replace("; charset=", ";charset=");
					}
					if(contentTypeAttesoRisposta.contains("\"UTF-8\"")) {
						contentTypeAttesoRisposta = contentTypeAttesoRisposta.replace("\"UTF-8\"", "UTF-8");
					}
					if(contentTypeAttesoRisposta.equals("text/xml;") || contentTypeAttesoRisposta.equals("text/xml ;")) {
						contentTypeAttesoRisposta = "text/xml";
					}
					String contentTypeAttesoRisposta2 = contentTypeAttesoRisposta.replace(" ",""); // elimino gli spazi che vengono appiattiti in tomcat 9.0.91
					boolean check1=contentTypeRisposta.equals(contentTypeAttesoRisposta);
					boolean check2=contentTypeRisposta.equals(contentTypeAttesoRisposta2);
					boolean esito = check1 || check2;
					Assert.assertTrue(esito, "Content-Type del file di risposta ["+contentTypeRisposta+"] diverso da quello atteso ["+contentTypeAttesoRisposta+"] o atteso2["+contentTypeAttesoRisposta2+"]");
				}

				byte[] contentRisposta = httpResponse.getContent();

				if (!propertiesURLBased.containsKey("fault") && !propertiesURLBased.containsKey("problem")) {
				
					if(tipoTest.equals("xml") || tipoTest.equals("soap11") || tipoTest.equals("soap12")) {
						XMLDiff xmlDiffEngine = new XMLDiff();
						XMLDiffOptions xmlDiffOptions = new XMLDiffOptions();
						xmlDiffEngine.initialize(XMLDiffImplType.XML_UNIT, xmlDiffOptions);
						Assert.assertTrue(xmlDiffEngine.diff(xmlDiffEngine.getXMLUtils().newDocument(contentRisposta),xmlDiffEngine.getXMLUtils().newDocument(contentAttesoRisposta))
								, "File di risposta ["+new String(contentRisposta)+"]diverso da quello atteso ["+new String(contentAttesoRisposta)+"]: " + xmlDiffEngine.getDifferenceDetails());
					} else if(tipoTest.equals("multi") || tipoTest.equals("multi-mixed")) {
						if(!isRichiesta && rispostaOk) {
							MimeMultipart mm = new MimeMultipart(new ByteArrayInputStream(contentRisposta), contentTypeRisposta);
							MimeMultipart mmAtteso = 
									new MimeMultipart(new ByteArrayInputStream(FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getMultipartFileName())),
											contentTypeRisposta);
										// questo e' prodotto male	contentTypeAttesoRisposta);
							Reporter.log("BodyParts ["+mm.countBodyParts()+"] == ["+mmAtteso.countBodyParts()+"]");
							Assert.assertEquals(mm.countBodyParts(),mmAtteso.countBodyParts());
	
							for(int i = 0; i < mm.countBodyParts(); i++) {
								Reporter.log("BodyParts["+i+"] ContentType ["+mm.getBodyPart(i).getContentType()+"] == ["+mmAtteso.getBodyPart(i).getContentType()+"]");
								Assert.assertEquals(mm.getBodyPart(i).getContentType(),mmAtteso.getBodyPart(i).getContentType());
								Reporter.log("BodyParts["+i+"] Size ["+mm.getBodyPart(i).getSize()+"] == ["+mmAtteso.getBodyPart(i).getSize()+"]");
								Assert.assertEquals(mm.getBodyPart(i).getSize(),mmAtteso.getBodyPart(i).getSize());
	//							Assert.assertEquals(mm.getBodyPart(i).getContent(),mmAtteso.getBodyPart(i).getContent());
							}
						} else {
							Assert.assertEquals(contentRisposta,contentAttesoRisposta, "File di risposta ["+new String(contentRisposta)+"]diverso da quello atteso ["+new String(contentAttesoRisposta)+"]");
						}
						
					} else {
						if(!HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equals(contentTypeRisposta) &&
								!HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807.equals(contentTypeRisposta)) {
							Assert.assertEquals(contentRisposta,contentAttesoRisposta, "File di risposta ["+new String(contentRisposta)+"]diverso da quello atteso ["+new String(contentAttesoRisposta)+"]");
						}
					}
					
				}
				
			}
	
			return httpResponse;
			
		}catch(Exception e){
			throw e;
		}
		
		
	}
	
	@SuppressWarnings("deprecation")
	private String buildUrl(Map<String,String> propertiesURLBased, String urlBase) {
		return TransportUtils.buildLocationWithURLBasedParameter(propertiesURLBased, urlBase);
	}
	
}
