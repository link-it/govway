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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest.jose;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.testng.Assert;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JOSEUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSEUtils {

	
//	private static final String JSON_MESSAGE = "{"+
//	        "\"name\":\"value1\","+
//	        "\"name2\":\"value2\","+
//	        "\"nameINT\":3,"+
//	        "\"nameDOUBLE\":4.5"+
//			"}";
	private static String JSON_MESSAGE = null;
	static {
		try {
			ObjectNode node = JSONUtils.getInstance(true).newObjectNode();
			node.put("name", "value1");
			node.put("name2", "value2");
			node.put("nameINT", 3);
			node.put("nameDOUBLE", 4.6);
			JSON_MESSAGE = JSONUtils.getInstance().toString(node);
		}catch(Exception e) {
			e.printStackTrace(System.err);
		}
	}

	
	public int returnCodeAtteso = 200;
	public boolean delegata = true;
	public Properties queryParameters = new Properties();
	
	public boolean signature = true;
	public boolean payloadEncoding = true;
	
	public boolean jwt = false;
	public boolean json = false;
	public Properties jwtHeaders;
	
	public HttpResponse invoke(Repository repository, String azione) throws UtilsException {
	
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContent(JSON_MESSAGE.getBytes());
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		
		StringBuffer bf = new StringBuffer();
		if(this.delegata)
			bf.append(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		else
			bf.append(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
		String porta = CostantiTestSuite.PORTA_DELEGATA_JOSE_PREFIX+
				azione+"/"+
				azione+ // duplico l'azione per farla passare avanti
				"/service/echo";
		bf.append(porta);
		String urlDaUtilizzare = TransportUtils.buildLocationWithURLBasedParameter(this.queryParameters, bf.toString());
		Reporter.log("URL: "+urlDaUtilizzare);
		request.setUrl(urlDaUtilizzare);

		// invocazione
		HttpResponse httpResponse = HttpUtilities.httpInvoke(request);
		
		Reporter.log("Atteso ["+this.returnCodeAtteso+"] ritornato ["+httpResponse.getResultHTTPOperation()+"], raccolgo id ...");

		// Raccolgo identificativo per verifica traccia
		String idMessaggio = httpResponse.getHeader(org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
		Assert.assertTrue(idMessaggio!=null);
		Reporter.log("Ricevuto id ["+idMessaggio+"]");
		repository.add(idMessaggio);
		
		Reporter.log("["+idMessaggio+"] Atteso ["+this.returnCodeAtteso+"] ritornato ["+httpResponse.getResultHTTPOperation()+"]");
		if(this.returnCodeAtteso != httpResponse.getResultHTTPOperation()) {
			if(httpResponse.getContent()!=null) {
				Reporter.log("["+idMessaggio+"] body ["+new String(httpResponse.getContent())+"]");
			}
			else {
				Reporter.log("["+idMessaggio+"] body empty");
			}
		}
		Assert.assertTrue(this.returnCodeAtteso == httpResponse.getResultHTTPOperation());
		
		String contentTypeAttesoRisposta = null;
		if(this.returnCodeAtteso == 200) {
			contentTypeAttesoRisposta = HttpConstants.CONTENT_TYPE_JSON;
		}
		else {
			contentTypeAttesoRisposta = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
		}
		String contentTypeRisposta = httpResponse.getContentType();
		Assert.assertEquals(contentTypeRisposta, contentTypeAttesoRisposta, "Content-Type del file di risposta ["+contentTypeRisposta+"] diverso da quello atteso ["+contentTypeAttesoRisposta+"]");
		
		if(this.returnCodeAtteso == 200) {
			
			String contentRisposta = new String(httpResponse.getContent());
			
			if(this.jwt) {
				Reporter.log("["+idMessaggio+"] JWT: "+contentRisposta);
				String [] tmp = contentRisposta.split("\\.");
				if(tmp!=null)
					Reporter.log("["+idMessaggio+"] split.length: "+tmp.length);
				Assert.assertTrue(tmp!=null && tmp.length>2);
				
				String header = null;
				if(this.signature) {
					Assert.assertTrue(tmp.length==3);
					header = tmp[0];
					String contenutoRispostaEstratto = tmp[1];
					contenutoRispostaEstratto = new String(Base64Utilities.decode(contenutoRispostaEstratto));
					
					Assert.assertEquals(contenutoRispostaEstratto,JSON_MESSAGE, "File di risposta ["+contenutoRispostaEstratto+"] diverso da quello atteso ["+JSON_MESSAGE+"]");
				}
				else {
					Assert.assertTrue(tmp.length==5);
					header = tmp[0];
				}
				
				checkHeader(header);
			}
			else if(this.json) {
				Reporter.log("["+idMessaggio+"] JSON: "+contentRisposta);
				JSONUtils jsonUtils = JSONUtils.getInstance();
				JsonNode node = null;
				if(this.payloadEncoding) {
					node = jsonUtils.getAsNode(contentRisposta.getBytes());
				}
				else {
					node = jsonUtils.getAsNode(contentRisposta.replace(JSON_MESSAGE, "{}").getBytes());
				}
				
				if(this.signature) {
					
					JsonNode payLoadNode = node.get("payload");
					Assert.assertNotNull(payLoadNode, "element 'payload' non presente nella struttura JSON ["+contentRisposta+"]");
					String contenuto = payLoadNode.asText();
					String contenutoRispostaEstratto = contenuto;
					if(this.payloadEncoding) {
						contenutoRispostaEstratto = new String(Base64Utilities.decode(contenuto));
					}
					else {
						// replace precedente
						Assert.assertEquals(contenutoRispostaEstratto,"{}","Effettuato replace precedente, atteso vuoto");
						contenutoRispostaEstratto = JSON_MESSAGE;
					}
					
					Assert.assertEquals(contenutoRispostaEstratto,JSON_MESSAGE, "File di risposta ["+contenutoRispostaEstratto+"] diverso da quello atteso ["+JSON_MESSAGE+"]");
					
					JsonNode signatureNode = node.get("signatures");
					Assert.assertNotNull(signatureNode, "element 'signatures' non presente nella struttura JSON ["+contentRisposta+"]");
					Assert.assertTrue((signatureNode instanceof ArrayNode), "element 'signatures' presente nella struttura JSON ["+contentRisposta+"] ma non è un array");
					ArrayNode signatureNodeArray = (ArrayNode) signatureNode;
					JsonNode signatureNodePositionOne = signatureNodeArray.get(0);
					Assert.assertNotNull(signatureNodePositionOne, "element 'signatures[0]' non presente nella struttura JSON ["+contentRisposta+"]");
					
					JsonNode protectedNode = signatureNodePositionOne.get("protected");
					Assert.assertNotNull(protectedNode, "element 'protected' non presente nella struttura JSON ["+contentRisposta+"]");
					
					String header = protectedNode.asText();
					checkHeader(header);
					
				}
				else {
					
					JsonNode recipientsNode = node.get("recipients");
					Assert.assertNotNull(recipientsNode, "element 'recipients' non presente nella struttura JSON ["+contentRisposta+"]");
					
					
					JsonNode unprotectedNode = node.get("unprotected");
					Assert.assertNotNull(unprotectedNode, "element 'unprotected' non presente nella struttura JSON ["+contentRisposta+"]");
					String unprotectedHeader = unprotectedNode.asText();
					JsonNode alg = unprotectedNode.get("alg");
					Assert.assertNotNull(alg, "claims 'alg' non presente tra gli header non protetti ["+unprotectedHeader+"] struttura JSON ["+contentRisposta+"]");
					String algValue = alg.asText();
					if(this.jwtHeaders!=null) {
						String checkValue = this.jwtHeaders.getProperty("alg");
						Assert.assertEquals(algValue, checkValue, "claims 'alg' presente tra gli header non protetti ["+unprotectedHeader+"] differente dal valore atteso '"+checkValue+"' struttura JSON ["+contentRisposta+"]");
					}
					
					JsonNode protectedNode = node.get("protected");
					Assert.assertNotNull(protectedNode, "element 'protected' non presente nella struttura JSON ["+contentRisposta+"]");
					String header = protectedNode.asText();
					checkHeader(header);

				}
				
			}
			else {
				Assert.assertEquals(contentRisposta,JSON_MESSAGE, "File di risposta ["+new String(contentRisposta)+"] diverso da quello atteso ["+JSON_MESSAGE+"]");
			}
		}
		
		return httpResponse;
	}
	
	public void checkHeader(String header) {
		header = new String(Base64Utilities.decode(header));
		Reporter.log("header: "+header);
		Assert.assertTrue(header.startsWith("{"),"Header ["+header+"] non inizia con {");
		Assert.assertTrue(header.endsWith("}"),"Header ["+header+"] non finisce con }");
		Properties readP = new Properties();
		if(header.length()>2) {
			header = header.substring(1, header.length()-1);
			String [] hdr = header.split(",");
			if(hdr!=null) {
				for (int i = 0; i < hdr.length; i++) {
					String hdrItem = hdr[i];
					String [] tmpP = hdrItem.split(":");
					if(tmpP.length==2) {
						String valore = tmpP[1];
						if(valore.startsWith("[")) {
							valore = valore.substring(1, valore.length());
						}
						else if(valore.startsWith("\"[")) {
							valore = valore.substring(2, valore.length());
						}
						readP.setProperty(tmpP[0], valore);
					}
				}
			}
		}
		Enumeration<?> en  = readP.propertyNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = readP.getProperty(key);
			Reporter.log("Trovato hdr ["+key+"]=["+value+"]");
		}
		if(this.jwtHeaders!=null && !this.jwtHeaders.isEmpty()) {
			Enumeration<?> enCheck  = this.jwtHeaders.propertyNames();
			while (enCheck.hasMoreElements()) {
				String keyCheck = (String) enCheck.nextElement();
				String valueCheck = this.jwtHeaders.getProperty(keyCheck);
				
				if(!this.signature && "alg".equals(keyCheck)){
					continue; // viene definito negli header unprotected
				}
				
				keyCheck = "\"" + keyCheck + "\"";
				valueCheck = "\"" + valueCheck + "\"";
				
				Assert.assertTrue(readP.containsKey(keyCheck),"Claim "+keyCheck+" non trovato nell'header");
				String valueTrovato = readP.getProperty(keyCheck);
				Assert.assertTrue(valueTrovato!=null,"Claim "+keyCheck+" con un valore null nell'header");
				Assert.assertEquals(valueTrovato,valueCheck,"Claim "+keyCheck+" trovato nell'header con un valore ("+valueTrovato+") differente da quello atteso ("+valueCheck+")");
			}
		}
	}
	
	public void postInvokeDelegata(Repository repository,
			String azione) throws TestSuiteException, Exception{
		this.postInvoke(repository, azione, true, false);
	}
	public void postInvokeDelegata(Repository repository,
			String azione, boolean checkNotIsArrived) throws TestSuiteException, Exception{
		this.postInvoke(repository, azione, true, checkNotIsArrived);
	}
	
	public void postInvokeApplicativa(Repository repository,
			String azione) throws TestSuiteException, Exception{
		this.postInvoke(repository, azione, false, true);
	}
	public void postInvokeApplicativa(Repository repository,
			String azione, boolean checkNotIsArrived) throws TestSuiteException, Exception{
		this.postInvoke(repository, azione, false, checkNotIsArrived);
	}
	
	private void postInvoke(Repository repository,
			String azione, boolean checkDelegata, boolean checkNotIsArrived) throws TestSuiteException, Exception{
	
		String tipoServizio = CostantiTestSuite.REST_TIPO_SERVIZIO;
		String nomeServizio = CostantiTestSuite.NOME_SERVIZIO_JOSE ;
		
		String id=repository.getNext();
		if(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		DatabaseComponent data = null;
		if(org.openspcoop2.protocol.trasparente.testsuite.units.utils.CooperazioneTrasparenteBase.protocolloEmetteTracce) {
			if(checkDelegata) {
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
				
				CooperazioneBaseInformazioni info = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						 CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
						false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);
				CooperazioneBase collaborazioneTrasparenteBase = 
						new CooperazioneBase(false,MessageType.SOAP_11,  info, 
								org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
								DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance(), checkDelegata);
				collaborazioneTrasparenteBase.testSincrono(data,id, tipoServizio, nomeServizio, azione, checkServizioApplicativo,null); 
				
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
	
}
