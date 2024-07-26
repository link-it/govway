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

package org.openspcoop2.protocol.spcoop.testsuite.units.sicurezza;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPException;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.mime.MimeTypeConstants;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Vengono provate le seguenti funzionalità aggiuntive di WSSecurity:
 *  postBase64EncodingAttachment [value: boolean, default false]: Produce l'encoding in base64 dell'attachment (dopo aver applicato la sicurezza)
 *  preBase64EncodingAttachment [value: boolean, default false]: Produce l'encoding in base64 dell'attachment (prima di applicare la sicurezza)
 *  postBase64DecodingAttachment [value: boolean, default false]: Decodifica la rappresentazione base64 dell'attachment (dopo la validazione della sicurezza)
 *  explicitAttachmentInclusiveNamespace [value: boolean, default false]: Inserisce gli elementi "InclusiveNamespace" anche se la lista dei prefissi nella trasformazione degli attachment e' vuota
 *  omitAttachmentKeyInfo [value: boolean, default false]: Non inserisce all'interno della EncryptedData dell'attachment l'elemento "KeyInfo"
 *  omitCanonicalizationInclusiveNamespace [value: boolean, default false]: Non inserisce l'elemento "InclusiveNamespace" all'interno dell'elemento "CanonicalizationMethod"
 *  addAttachmentIdBrackets [value: boolean, default false]: Aggiunge all'id di riferimento degli allegati delle parentesi unicinate
 *  encryptAttachmentHeaders [value: boolean, default false]: Aggiunge gli header dell'Attachment all'interno del messaggio cifrato
 *  
 *  
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSecurityWSS4Jext {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "WSSecurityWSS4Jext";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(
			CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_WSS4JEXT,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_WSS4JEXT,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = new CooperazioneBase(
			false, 
			MessageType.SOAP_11, 
			this.info, 
			org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
			DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	private static boolean addIDUnivoco = false;
	

	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun = true , groups = ID_GRUPPO)
	public void testOpenspcoopCoreLogRaccoltaTempoAvvioTest() {
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 
	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	@AfterGroups (alwaysRun = true , groups = ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception {
		if (!this.erroriAttesiOpenSPCoopCore.isEmpty()) {
			FileSystemUtilities.verificaOpenspcoopCore(
					this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		} else {
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt e Signature
	 */
	Repository repositorySincronoWSSWSS4JextEncryptSignature = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO+".WSS4JEXT_SINCRONO_ENCRYPT_SIGNATURE"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt e Signature")
	public void sincronoWSSWSS4JextEncryptSignature() throws TestSuiteException, IOException {
		
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextEncryptSignature);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_ENCRYPT_SIGNATURE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	
	@DataProvider (name="SincronoWSSWSS4JextEncryptSignature")
	public Object[][] testSincronoWSSWSS4JextEncryptSignature() {
		String id = this.repositorySincronoWSSWSS4JextEncryptSignature.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT_SIGNATURE"}, 
			dataProvider = "SincronoWSSWSS4JextEncryptSignature", dependsOnMethods = {"sincronoWSSWSS4JextEncryptSignature"})
	public void testSincronoWSSWSS4JextEncryptSignature(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_ENCRYPT_SIGNATURE,
				checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4J Signature e Encrypt
 	 */
	Repository repositorySincronoWSSWSS4JextSignatureEncrypt = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE_ENCRYPT"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature e Encrypt")
	public void sincronoWSSWSS4JextSignatureEncrypt() throws TestSuiteException, IOException {
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextSignatureEncrypt);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_SIGNATURE_ENCRYPT);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name = "SincronoWSSWSS4JextSignatureEncrypt")
	public Object[][] testSincronoWSSWSS4JextSignatureEncrypt() {
		String id = this.repositorySincronoWSSWSS4JextSignatureEncrypt.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE_ENCRYPT"},
			dataProvider = "SincronoWSSWSS4JextSignatureEncrypt", dependsOnMethods = {"sincronoWSSWSS4JextSignatureEncrypt"})
	public void testSincronoWSSWSS4JextSignatureEncrypt(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException{
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_SIGNATURE_ENCRYPT, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt
	 */
	Repository repositorySincronoWSSWSS4JextEncrypt = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt")
	public void sincronoWSSWSS4JextEncrypt() throws TestSuiteException, IOException {
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextEncrypt);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_ENCRYPT);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name = "SincronoWSSWSS4JextEncrypt")
	public Object[][] testSincronoWSSWSS4JextEncrypt() {
		String id = this.repositorySincronoWSSWSS4JextEncrypt.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT"},
			dataProvider = "SincronoWSSWSS4JextEncrypt", dependsOnMethods = {"sincronoWSSWSS4JextEncrypt"})
	public void testSincronoWSSWSS4JextEncrypt(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException{
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_ENCRYPT, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature
	 */
	Repository repositorySincronoWSSWSS4JextSignature=new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature")
	public void sincronoWSSWSS4JextSignature() throws TestSuiteException, IOException {
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextSignature);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_SIGNATURE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name = "SincronoWSSWSS4JextSignature")
	public Object[][] testSincronoWSSWSS4JextSignature() {
		String id = this.repositorySincronoWSSWSS4JextSignature.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups={CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE"},
			dataProvider = "SincronoWSSWSS4JextSignature", dependsOnMethods = {"sincronoWSSWSS4JextSignature"})
	public void testSincronoWSSWSS4JextSignature(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_SIGNATURE, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt e Signature Con Attachments
	 */
	Repository repositorySincronoWSSWSS4JextEncryptSignatureAttachments = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT_SIGNATURE_ATTACHMENTS"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt e Signature Con Attachments")
	public void sincronoWSSWSS4JextEncryptSignatureAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextEncryptSignatureAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_ENCRYPT_SIGNATURE_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoBinario(), MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoXml(), MimeTypeConstants.MEDIA_TYPE_SOAP_1_1);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());		
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name = "SincronoWSSWSS4JextEncryptSignatureAttachments")
	public Object[][] testSincronoWSSWSS4JextEncryptSignatureAttachments() {
		String id = this.repositorySincronoWSSWSS4JextEncryptSignatureAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT_SIGNATURE_ATTACHMENTS"},
			dataProvider = "SincronoWSSWSS4JextEncryptSignatureAttachments", dependsOnMethods = {"sincronoWSSWSS4JextEncryptSignatureAttachments"})
	public void testSincronoWSSWSS4JextEncryptSignatureAttachments(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_ENCRYPT_SIGNATURE_ATTACHMENTS, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature e Encrypt  Con Attachments
 	 */
	Repository repositorySincronoWSSWSS4JextSignatureEncryptAttachments = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE_ENCRYPT_ATTACHMENTS"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature e Encrypt Con Attachments")
	public void sincronoWSSWSS4JextSignatureEncryptAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextSignatureEncryptAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_SIGNATURE_ENCRYPT_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoBinario(), MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoXml(), MimeTypeConstants.MEDIA_TYPE_SOAP_1_1);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());		
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name = "SincronoWSSWSS4JextSignatureEncryptAttachments")
	public Object[][] testSincronoWSSWSS4JextSignatureEncryptAttachments() {
		String id = this.repositorySincronoWSSWSS4JextSignatureEncryptAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE_ENCRYPT_ATTACHMENTS"},
			dataProvider = "SincronoWSSWSS4JextSignatureEncryptAttachments", dependsOnMethods = {"sincronoWSSWSS4JextSignatureEncryptAttachments"})
	public void testSincronoWSSWSS4JextSignatureEncryptAttachments(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_SIGNATURE_ENCRYPT_ATTACHMENTS, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt Con Attachments
	 */
	Repository repositorySincronoWSSWSS4JextEncryptAttachments = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT_ATTACHMENTS"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Encrypt Con Attachments")
	public void sincronoWSSWSS4JextEncryptAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextEncryptAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_ENCRYPT_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoBinario(), MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoXml(), MimeTypeConstants.MEDIA_TYPE_SOAP_1_1);
		client.run();
		
		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());		
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name = "SincronoWSSWSS4JextEncryptAttachments")
	public Object[][] testSincronoWSSWSS4JextEncryptAttachments() {
		String id = this.repositorySincronoWSSWSS4JextEncryptAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_ENCRYPT_ATTACHMENTS"},
		dataProvider = "SincronoWSSWSS4JextEncryptAttachments", dependsOnMethods = {"sincronoWSSWSS4JextEncryptAttachments"})
	public void testSincronoWSSWSS4JextEncryptAttachments(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_ENCRYPT_ATTACHMENTS, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature Con Attachments
	 */
	Repository repositorySincronoWSSWSS4JextSignatureAttachments = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4J_SINCRONO_SIGNATURE_ATTACHMENTS"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature Con Attachments")
	public void sincronoWSSWSS4JextSignatureAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextSignatureAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_SIGNATURE_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoBinario(), MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoXml(), MimeTypeConstants.MEDIA_TYPE_SOAP_1_1);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());	
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name = "SincronoWSSWSS4JextSignatureAttachments")
	public Object[][] testSincronoWSSWSS4JextSignatureAttachments() {
		String id = this.repositorySincronoWSSWSS4JextSignatureAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE_ATTACHMENTS"},
			dataProvider = "SincronoWSSWSS4JextSignatureAttachments", dependsOnMethods = {"sincronoWSSWSS4JextSignatureAttachments"})
	public void testSincronoWSSWSS4JextSignatureAttachments(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_SIGNATURE_ATTACHMENTS, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature Con Attachments (Engine 'xmlSec')
	 */
	Repository repositorySincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec = new Repository();
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4J_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC"},
			description = "Test per il profilo di collaborazione Sincrono con WSSecurity WSS4Jext Signature Con Attachments (Engine 'xmlSec')")
	public void sincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client = new ClientSincrono(this.repositorySincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_WSS4JEXT_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4Jext(), false, addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoBinario(), MimeTypeConstants.MEDIA_TYPE_APPLICATION_OCTET_STREAM);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecurityWSS4JextAllegatoXml(), MimeTypeConstants.MEDIA_TYPE_SOAP_1_1);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());	
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec")
	public Object[][] testSincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec() {
		String id = this.repositorySincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), id, false},	
				{DatabaseProperties.getDatabaseComponentErogatore(), id, true}	
		};
	}
	@Test(groups = {CostantiSicurezza.ID_GRUPPO_SICUREZZA, WSSecurityWSS4Jext.ID_GRUPPO, WSSecurityWSS4Jext.ID_GRUPPO + ".WSS4JEXT_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC"},
			dataProvider = "SincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec", dependsOnMethods = {"sincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec"})
	public void testSincronoWSSWSS4JextSignatureAttachmentsEngineXmlSec(DatabaseComponent data, String id, boolean checkServizioApplicativo) throws TestSuiteException {
		try {
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_WSS4JEXT_SIGNATURE_ATTACHMENTS_SIGNATURE_ENGINE_XMLSEC, checkServizioApplicativo, null);
		} finally {
			data.close();
		}
	}
}
