/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import java.util.Properties;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * JOSEEncrypt
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JOSEEncrypt {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "JOSEEncrypt";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups={JOSEEncrypt.ID_GRUPPO})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		
//		System.out.println("JSONUtils: configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)");
//		org.openspcoop2.utils.json.JSONUtils.getObjectMapper().configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={JOSEEncrypt.ID_GRUPPO})
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
	@DataProvider(name=JOSEEncrypt.ID_GRUPPO+".compact.azioniFlussiOk")
	public Object[][] compactAzioniFlussiOkProvider(){
		return new Object[][]{
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC}, //  Compact; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_SYMMETRIC}, //  Compact; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_ASYMMETRIC}, //  Compact; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_SYMMETRIC}, //  Compact; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
					
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_BINARY_CERTIFICATE_IN_HEADERS},
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_URL_CERTIFICATE_IN_HEADERS},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSEEncrypt.ID_GRUPPO,JOSEEncrypt.ID_GRUPPO+".compact.flussiOk"},dataProvider=JOSEEncrypt.ID_GRUPPO+".compact.azioniFlussiOk")
	public void compactTestFlussiOk(String azione) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = false;
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	

	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * 
	 */
	@DataProvider(name=JOSEEncrypt.ID_GRUPPO+".compact.azioniFlussiEsaminaJWT")
	public Object[][] compactAzioniFlussiEsaminaJWTProvider(){
		return new Object[][]{
			// Per tutte si verifica che torni indietro un payload JWT e che il body nel payload sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_DEFLATE_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_COMPACT_ASYMMETRIC_JWT_HEADERS_ONLY_SENDER_REQUEST, 
				new NameValue("alg", "RSA-OAEP-256"),
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
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSEEncrypt.ID_GRUPPO,JOSEEncrypt.ID_GRUPPO+".compact.esaminaJWT"},dataProvider=JOSEEncrypt.ID_GRUPPO+".compact.azioniFlussiEsaminaJWT")
	public void compactTestEsaminaJWT(String azione, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = false;
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
	
	
	
	

	// *** JSON ***

	/**
	 * Tests una configurazione funzionante in tutto il flusso di andata e ritorno
	 * 
	 */
	@DataProvider(name=JOSEEncrypt.ID_GRUPPO+".json.azioniFlussiOk")
	public Object[][] jsonAzioniFlussiOkProvider(){
		return new Object[][]{
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC}, //  Compact; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_SYMMETRIC}, //  Compact; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_ASYMMETRIC}, //  Compact; flusso di richieste con chiavi asimmetriche in JSK, risposta con chiavi asimmetriche in JWK
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_SYMMETRIC}, //  Compact; flusso di richieste con chiavi simmetriche in JCEKS, risposta con chiavi simmetriche in JWK
					
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_BINARY_CERTIFICATE_IN_HEADERS},
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_URL_CERTIFICATE_IN_HEADERS},
		};
	}
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSEEncrypt.ID_GRUPPO,JOSEEncrypt.ID_GRUPPO+".json.flussiOk"},dataProvider=JOSEEncrypt.ID_GRUPPO+".json.azioniFlussiOk")
	public void jsonTestFlussiOk(String azione) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = false;
		
		utils.invoke(repository, azione);
		
		utils.postInvokeDelegata(repository, azione);
		utils.postInvokeApplicativa(repository, azione);
					
	}
	
	
	
	/**
	 * Tests una configurazione funzionante dove non viene effettuato il processamento in fase di ricezione, o non viene effettuato il detach della sicurezza.
	 * 
	 */
	@DataProvider(name=JOSEEncrypt.ID_GRUPPO+".json.azioniFlussiEsaminaJWT")
	public Object[][] jsonAzioniFlussiEsaminaJWTProvider(){
		return new Object[][]{
			// Per tutte si verifica che torni indietro un payload JWT e che il body nel payload sia uguale a quello spedito.
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_ASYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_ASYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche solo nel sender, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_SYMMETRIC_ONLY_SENDER_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_SYMMETRIC_ONLY_SENDER_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche solo nel sender, verifica che negli header ci sia l'algoritmo HS256
		
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di richiesta con chiave asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_ASYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "RSA-OAEP-256")}, //  Compact; flusso di risposta con chiavi asimmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo RS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_SYMMETRIC_DETACH_SEC_INFO_DISABLED_REQUEST, new NameValue("alg", "dir")}, //  Compact; flusso di richiesta con chiave simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_DEFLATE_SYMMETRIC_DETACH_SEC_INFO_DISABLED_RESPONSE, new NameValue("alg", "dir")}, //  Compact; flusso di risposta con chiavi simmetriche, nel receiver non viene ripulito il sec, verifica che negli header ci sia l'algoritmo HS256
		
			
			{CostantiTestSuite.NOME_SERVIZIO_JOSE_AZIONE_JWE_JSON_ASYMMETRIC_JWT_HEADERS_ONLY_SENDER_REQUEST, 
				new NameValue("alg", "RSA-OAEP-256"),
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
	@Test(groups={CostantiTestSuite.ID_GRUPPO_TEST_JOSE, JOSEEncrypt.ID_GRUPPO,JOSEEncrypt.ID_GRUPPO+".json.esaminaJWT"},dataProvider=JOSEEncrypt.ID_GRUPPO+".json.azioniFlussiEsaminaJWT")
	public void jsonTestEsaminaJWT(String azione, NameValue ... nameValues ) throws TestSuiteException, Exception{
		Repository repository=new Repository();
		
		JOSEUtils utils = new JOSEUtils();
		utils.delegata = true;
		utils.signature = false;
		utils.json=true;
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
}
