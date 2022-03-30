/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * JOSESignature
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSESignature {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "JOSESignature";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups={JOSESignature.ID_GRUPPO})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		
//		System.out.println("JSONUtils: configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)");
//		org.openspcoop2.utils.json.JSONUtils.getObjectMapper().configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={JOSESignature.ID_GRUPPO})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	

	
	
	
	
	// *** COMPACT ***

	/**
	 * Tests una configurazione funzionante in tutto il flusso di andata e ritorno
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".compact.azioniFlussiOk")
	public Object[][] compactAzioniFlussiOkProvider(){
		return new Object[][]{
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC}, //  Compact; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_SYMMETRIC}, //  Compact; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_ASYMMETRIC}, //  Compact Detach Header; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_SYMMETRIC}, //  Compact Detach Header; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_URL_ASYMMETRIC}, //  Compact Detach Url; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_URL_SYMMETRIC}, //  Compact Detach Url; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_BINARY_CERTIFICATE_IN_HEADERS},
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_URL_CERTIFICATE_IN_HEADERS},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".compact.flussiOk"},dataProvider=JOSESignature.ID_GRUPPO+".compact.azioniFlussiOk")
	public void compactTestFlussiOk(String azione) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	
	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".compact.azioniFlussiEsaminaJWT")
	public Object[][] compactAzioniFlussiEsaminaJWTProvider(){
		return new Object[][]{
			// Per tutte si verifica che torni indietro un payload JWT e che il body nel payload sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_ASYMMETRIC_JWT_HEADERS_ONLY_SENDER_REQUEST, 
				new NameValue("alg", "RS256"),
				new NameValue("kid", "openspcoop"),
				new NameValue("typ", "JWT"),
				new NameValue("cty", HttpConstants.CONTENT_TYPE_JSON),
				new NameValue("crit", "prova1"),
				new NameValue("hdr1", "value1"),
				new NameValue("hdr2", "value2"),
				new NameValue("hdr3", "value3"),
				}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender,  verifica che negli header ci sia l'algoritmo RS256
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".compact.esaminaJWT"},dataProvider=JOSESignature.ID_GRUPPO+".compact.azioniFlussiEsaminaJWT")
	public void compactTestEsaminaJWT(String azione, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		utils.jwt=true;
		if(nameValues!=null && nameValues.length>0) {
			utils.jwtHeaders = new Properties();
			for (int i = 0; i < nameValues.length; i++) {
				utils.jwtHeaders.put(nameValues[i].getName(), nameValues[i].getValue());
			}
		}
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	
	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * La gestione avviene in modalita DETACH via header HTTP
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".compact.azioniFlussiEsaminaJWTinHeader")
	public Object[][] compactAzioniFlussiEsaminaJWTinHeaderProvider(){
		return new Object[][]{
			// Per tutte si verifica che in un header http torni indietro un payload JWT e che il body nel payload http sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_ASYMMETRIC_ONLY_SENDER_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_ASYMMETRIC_ONLY_SENDER_RESPONSE, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_SYMMETRIC_ONLY_SENDER_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_SYMMETRIC_ONLY_SENDER_RESPONSE, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER,  new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER,  new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_HEADER_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_URL_ASYMMETRIC_ONLY_SENDER_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_URL_SYMMETRIC_ONLY_SENDER_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_URL_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL,  new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_COMPACT_DETACH_URL_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL,  new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".compact.esaminaJWTinHeader"},dataProvider=JOSESignature.ID_GRUPPO+".compact.azioniFlussiEsaminaJWTinHeader")
	public void compactTestEsaminaJWTinHeader(String azione, boolean request, String header, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		String prefix = "";
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		if(request) {
			prefix = "PREFIX-";
			
			boolean url = CostantiTestSuite.JOSE_DETACH_REQUEST_URL.equals(header);	
			if(url) {
				utils.queryParameters.put("replyQueryParameter", header);
				utils.queryParameters.put("replyPrefixQueryParameter", prefix);	
			}
			else {
				utils.queryParameters.put("replyHttpHeader", header);
				utils.queryParameters.put("replyPrefixHttpHeader", prefix);
			}
		}
		utils.jwt=false;
		if(nameValues!=null && nameValues.length>0) {
			utils.jwtHeaders = new Properties();
			for (int i = 0; i < nameValues.length; i++) {
				utils.jwtHeaders.put(nameValues[i].getName(), nameValues[i].getValue());
			}
		}
		HttpResponse response = utils.invoke(repository, azione);
		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getHeadersValues());
		
		Iterator<String> it = response.getHeadersValues().keySet().iterator();
		while (it.hasNext()) {
			String hdrName = (String) it.next();
			String hdrValue = response.getHeaderFirstValue(hdrName);
			Reporter.log("Hdr response ["+hdrName+"] ["+hdrValue+"]");
		}
		
		String searchHdr = prefix + header;
		String hdr = response.getHeaderFirstValue(searchHdr);
		Assert.assertNotNull(hdr,"Header '"+searchHdr+"' atteso non presente");
		
		String [] tmp = hdr.split("\\.");
		if(tmp!=null)
			Reporter.log("split.length: "+tmp.length);
		Assert.assertTrue(tmp!=null && tmp.length>2);
		Assert.assertTrue(tmp.length==3);
		String headerJWT = tmp[0];
		
		utils.checkHeader(headerJWT);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	
	
	
	
	
	

	// *** JSON ***

	/**
	 * Tests una configurazione funzionante in tutto il flusso di andata e ritorno
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".json.azioniFlussiOk")
	public Object[][] jsonAzioniFlussiOkProvider(){
		return new Object[][]{
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_ASYMMETRIC}, //  Json Payload Encoding; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_SYMMETRIC}, //  Json Payload Encoding; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_ASYMMETRIC}, //  Compact Detach Header; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_SYMMETRIC}, //  Compact Detach Header; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_URL_ASYMMETRIC}, //  Compact Detach Url; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_URL_SYMMETRIC}, //  Compact Detach Url; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_ASYMMETRIC}, //  Compact Detach Header; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_SYMMETRIC}, //  Compact Detach Header; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_URL_ASYMMETRIC}, //  Compact Detach Url; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_URL_SYMMETRIC}, //  Compact Detach Url; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_ASYMMETRIC_BINARY_CERTIFICATE_IN_HEADERS},
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_ASYMMETRIC_URL_CERTIFICATE_IN_HEADERS},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".json.flussiOk"},dataProvider=JOSESignature.ID_GRUPPO+".json.azioniFlussiOk")
	public void jsonTestFlussiOk(String azione) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".json.azioniFlussiEsaminaJSONPayloadEncoding")
	public Object[][] jsonAzioniFlussiEsaminaJSONPayloadEncodingProvider(){
		return new Object[][]{
			// Per tutte si verifica che torni indietro un payload JWT e che il body nel payload sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RS256")}, //  Json; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RS256")}, //  Json; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "HS256")}, //  Json; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "HS256")}, //  Json; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RS256")}, //  Json; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RS256")}, //  Json; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "HS256")}, //  Json; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_ENCODING_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "HS256")}, //  Json; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_ASYMMETRIC_JWT_HEADERS_ONLY_SENDER_REQUEST, 
				new NameValue("alg", "RS256"),
				new NameValue("kid", "openspcoop"),
				new NameValue("typ", "JWT"),
				new NameValue("cty", HttpConstants.CONTENT_TYPE_JSON),
				new NameValue("crit", "prova1"),
				new NameValue("hdr1", "value1"),
				new NameValue("hdr2", "value2"),
				new NameValue("hdr3", "value3"),
				}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender,  verifica che negli header ci sia l'algoritmo RS256
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".json.esaminaJSONPayloadEncoding"},dataProvider=JOSESignature.ID_GRUPPO+".json.azioniFlussiEsaminaJSONPayloadEncoding")
	public void jsonTestEsaminaJsonPayloadEncoding(String azione, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		utils.json=true;
		utils.payloadEncoding = true;
		if(nameValues!=null && nameValues.length>0) {
			utils.jwtHeaders = new Properties();
			for (int i = 0; i < nameValues.length; i++) {
				utils.jwtHeaders.put(nameValues[i].getName(), nameValues[i].getValue());
			}
		}
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".json.azioniFlussiEsaminaJSONPayloadNotEncoding")
	public Object[][] jsonAzioniFlussiEsaminaJSONPayloadNotEncodingProvider(){
		return new Object[][]{
			// Per tutte si verifica che torni indietro un payload JWT e che il body nel payload sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RS256")}, //  Json; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RS256")}, //  Json; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "HS256")}, //  Json; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "HS256")}, //  Json; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RS256")}, //  Json; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RS256")}, //  Json; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "HS256")}, //  Json; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_PAYLOAD_NOT_ENCODING_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "HS256")}, //  Json; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".json.esaminaJSONPayloadNotEncoding"},dataProvider=JOSESignature.ID_GRUPPO+".json.azioniFlussiEsaminaJSONPayloadNotEncoding")
	public void jsonTestEsaminaJsonPayloadNotEncoding(String azione, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		utils.json=true;
		utils.payloadEncoding = false;
		if(nameValues!=null && nameValues.length>0) {
			utils.jwtHeaders = new Properties();
			for (int i = 0; i < nameValues.length; i++) {
				utils.jwtHeaders.put(nameValues[i].getName(), nameValues[i].getValue());
			}
		}
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	

	

	
	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * La gestione avviene in modalita DETACH via header HTTP
	 * 
	 */
	@DataProvider(name=JOSESignature.ID_GRUPPO+".json.azioniFlussiEsaminaJsonInHeader")
	public Object[][] jsonAzioniFlussiEsaminaJsonInHeaderProvider(){
		return new Object[][]{
			// Per tutte si verifica che in un header http torni indietro un payload JWT e che il body nel payload http sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_ASYMMETRIC_ONLY_SENDER_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_ASYMMETRIC_ONLY_SENDER_RESPONSE, false, true, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_SYMMETRIC_ONLY_SENDER_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_SYMMETRIC_ONLY_SENDER_RESPONSE, false, true, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER,  new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, false, true, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER,  new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_HEADER_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, false, true, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_URL_ASYMMETRIC_ONLY_SENDER_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_URL_SYMMETRIC_ONLY_SENDER_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_URL_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL,  new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_BASE64_URL_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, true, CostantiTestSuite.JOSE_DETACH_REQUEST_URL,  new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_ASYMMETRIC_ONLY_SENDER_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_ASYMMETRIC_ONLY_SENDER_RESPONSE, false, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_SYMMETRIC_ONLY_SENDER_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_SYMMETRIC_ONLY_SENDER_RESPONSE, false, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER,  new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, false, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "RS256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_HEADER,  new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_HEADER_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, false, false, CostantiTestSuite.JOSE_DETACH_RESPONSE_HEADER, new NameValue("alg", "HS256")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_URL_ASYMMETRIC_ONLY_SENDER_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_URL, new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_URL_SYMMETRIC_ONLY_SENDER_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_URL, new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_URL_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_URL,  new NameValue("alg", "RS256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWS_JSON_DETACH_NOT_BASE64_URL_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, true, false, CostantiTestSuite.JOSE_DETACH_REQUEST_URL,  new NameValue("alg", "HS256")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSESignature.ID_GRUPPO,JOSESignature.ID_GRUPPO+".json.esaminaJsonInHeader"},dataProvider=JOSESignature.ID_GRUPPO+".json.azioniFlussiEsaminaJsonInHeader")
	public void jsonTestEsaminaJsonInHeader(String azione, boolean request, boolean base64Encoded, String header, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		String prefix = "";
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = true;
		if(request) {
			prefix = "PREFIX-";
			
			boolean url = CostantiTestSuite.JOSE_DETACH_REQUEST_URL.equals(header);	
			if(url) {
				utils.queryParameters.put("replyQueryParameter", header);
				utils.queryParameters.put("replyPrefixQueryParameter", prefix);	
			}
			else {
				utils.queryParameters.put("replyHttpHeader", header);
				utils.queryParameters.put("replyPrefixHttpHeader", prefix);
			}
		}
		utils.jwt=false;
		if(nameValues!=null && nameValues.length>0) {
			utils.jwtHeaders = new Properties();
			for (int i = 0; i < nameValues.length; i++) {
				utils.jwtHeaders.put(nameValues[i].getName(), nameValues[i].getValue());
			}
		}
		HttpResponse response = utils.invoke(repository, azione);
		
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getHeadersValues());
		
		Iterator<String> it = response.getHeadersValues().keySet().iterator();
		while (it.hasNext()) {
			String hdrName = (String) it.next();
			String hdrValue = response.getHeaderFirstValue(hdrName);
			Reporter.log("Hdr response ["+hdrName+"] ["+hdrValue+"]");
		}
		
		String searchHdr = prefix + header;
		String hdr = response.getHeaderFirstValue(searchHdr);
		if(base64Encoded) {
			hdr = new String(Base64Utilities.decode(hdr));
		}
		Assert.assertNotNull(hdr,"Header '"+searchHdr+"' atteso non presente");
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		JsonNode node = jsonUtils.getAsNode(hdr);
		
		JsonNode payLoadNode = node.get("payload");
		Assert.assertNull(payLoadNode, "element 'payload' non atteso nella struttura JSON detached ["+hdr+"]");
		
		JsonNode signatureNode = node.get("signatures");
		Assert.assertNotNull(signatureNode, "element 'signatures' non presente nella struttura JSON detached ["+hdr+"]");
		Assert.assertTrue((signatureNode instanceof ArrayNode), "element 'signatures' presente nella struttura JSON detached ["+hdr+"] ma non è un array");
		ArrayNode signatureNodeArray = (ArrayNode) signatureNode;
		JsonNode signatureNodePositionOne = signatureNodeArray.get(0);
		Assert.assertNotNull(signatureNodePositionOne, "element 'signatures[0]' non presente nella struttura JSON detached ["+hdr+"]");
		
		JsonNode protectedNode = signatureNodePositionOne.get("protected");
		Assert.assertNotNull(protectedNode, "element 'protected' non presente nella struttura JSON detached ["+hdr+"]");
		
		String headerJWT = protectedNode.asText();
		
		utils.checkHeader(headerJWT);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
}
