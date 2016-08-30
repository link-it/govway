/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.openspcoop2.message.SOAPVersion;
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
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulle funzionalita' di WS-Security
 *  
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSecuritySoapBox {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "WSSecuritySoapBox";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_SOAPBOX,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_SOAPBOX,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	private static boolean addIDUnivoco = false;
	

	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	

	

	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt e Signature
	 */
	Repository repositorySincronoWSSSoapBoxEncryptSignature=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT_SIGNATURE"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt e Signature")
	public void sincronoWSSSoapBoxEncryptSignature() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxEncryptSignature);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT_SIGNATURE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name="SincronoWSSSoapBoxEncryptSignature")
	public Object[][]testSincronoWSSSoapBoxEncryptSignature()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxEncryptSignature.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT_SIGNATURE"},dataProvider="SincronoWSSSoapBoxEncryptSignature",dependsOnMethods={"sincronoWSSSoapBoxEncryptSignature"})
	public void testSincronoWSSSoapBoxEncryptSignature(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT_SIGNATURE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature e Encrypt
 	 */
	Repository repositorySincronoWSSSoapBoxSignatureEncrypt=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ENCRYPT"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature e Encrypt")
	public void sincronoWSSSoapBoxSignatureEncrypt() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxSignatureEncrypt);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ENCRYPT);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name="SincronoWSSSoapBoxSignatureEncrypt")
	public Object[][]testSincronoWSSSoapBoxSignatureEncrypt()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxSignatureEncrypt.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ENCRYPT"},dataProvider="SincronoWSSSoapBoxSignatureEncrypt",dependsOnMethods={"sincronoWSSSoapBoxSignatureEncrypt"})
	public void testSincronoWSSSoapBoxSignatureEncrypt(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ENCRYPT, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt
	 */
	Repository repositorySincronoWSSSoapBoxEncrypt=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt")
	public void sincronoWSSSoapBoxEncrypt() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxEncrypt);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name="SincronoWSSSoapBoxEncrypt")
	public Object[][]testSincronoWSSSoapBoxEncrypt()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxEncrypt.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT"},dataProvider="SincronoWSSSoapBoxEncrypt",dependsOnMethods={"sincronoWSSSoapBoxEncrypt"})
	public void testSincronoWSSSoapBoxEncrypt(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature
	 */
	Repository repositorySincronoWSSSoapBoxSignature=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature")
	public void sincronoWSSSoapBoxSignature() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxSignature);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());			
	}
	@DataProvider (name="SincronoWSSSoapBoxSignature")
	public Object[][]testSincronoWSSSoapBoxSignature()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxSignature.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE"},dataProvider="SincronoWSSSoapBoxSignature",dependsOnMethods={"sincronoWSSSoapBoxSignature"})
	public void testSincronoWSSSoapBoxSignature(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt e Signature Con Attachments
	 */
	Repository repositorySincronoWSSSoapBoxEncryptSignatureAttachments=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT_SIGNATURE_ATTACHMENTS"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt e Signature Con Attachments")
	public void sincronoWSSSoapBoxEncryptSignatureAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxEncryptSignatureAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT_SIGNATURE_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoBinario(),"application/octet-stream");
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoXml(),"text/xml");
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());		
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoWSSSoapBoxEncryptSignatureAttachments")
	public Object[][]testSincronoWSSSoapBoxEncryptSignatureAttachments()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxEncryptSignatureAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT_SIGNATURE_ATTACHMENTS"},dataProvider="SincronoWSSSoapBoxEncryptSignatureAttachments",dependsOnMethods={"sincronoWSSSoapBoxEncryptSignatureAttachments"})
	public void testSincronoWSSSoapBoxEncryptSignatureAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT_SIGNATURE_ATTACHMENTS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature e Encrypt  Con Attachments
 	 */
	Repository repositorySincronoWSSSoapBoxSignatureEncryptAttachments=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ENCRYPT_ATTACHMENTS"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature e Encrypt Con Attachments")
	public void sincronoWSSSoapBoxSignatureEncryptAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxSignatureEncryptAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ENCRYPT_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoBinario(),"application/octet-stream");
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoXml(),"text/xml");
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());		
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoWSSSoapBoxSignatureEncryptAttachments")
	public Object[][]testSincronoWSSSoapBoxSignatureEncryptAttachments()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxSignatureEncryptAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ENCRYPT_ATTACHMENTS"},dataProvider="SincronoWSSSoapBoxSignatureEncryptAttachments",dependsOnMethods={"sincronoWSSSoapBoxSignatureEncryptAttachments"})
	public void testSincronoWSSSoapBoxSignatureEncryptAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ENCRYPT_ATTACHMENTS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt Con Attachments
	 */
	Repository repositorySincronoWSSSoapBoxEncryptAttachments=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT_ATTACHMENTS"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Encrypt Con Attachments")
	public void sincronoWSSSoapBoxEncryptAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxEncryptAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_ENCRYPT_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoBinario(),"application/octet-stream");
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoXml(),"text/xml");
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());		
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoWSSSoapBoxEncryptAttachments")
	public Object[][]testSincronoWSSSoapBoxEncryptAttachments()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxEncryptAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_ENCRYPT_ATTACHMENTS"},dataProvider="SincronoWSSSoapBoxEncryptAttachments",dependsOnMethods={"sincronoWSSSoapBoxEncryptAttachments"})
	public void testSincronoWSSSoapBoxEncryptAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_ENCRYPT_ATTACHMENTS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature Con Attachments
	 */
	Repository repositorySincronoWSSSoapBoxSignatureAttachments=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature Con Attachments")
	public void sincronoWSSSoapBoxSignatureAttachments() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxSignatureAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoBinario(),"application/octet-stream");
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoXml(),"text/xml");
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());	
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoWSSSoapBoxSignatureAttachments")
	public Object[][]testSincronoWSSSoapBoxSignatureAttachments()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxSignatureAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS"},dataProvider="SincronoWSSSoapBoxSignatureAttachments",dependsOnMethods={"sincronoWSSSoapBoxSignatureAttachments"})
	public void testSincronoWSSSoapBoxSignatureAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature Con Attachments (Engine 'sun')
	 */
	Repository repositorySincronoWSSSoapBoxSignatureAttachments_engineSun=new Repository();
	boolean repositorySincronoWSSSoapBoxSignatureAttachments_engineSun_checkDB = true;
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_SUN"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature Con Attachments (Engine 'sun')")
	public void sincronoWSSSoapBoxSignatureAttachments_engineSun() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxSignatureAttachments_engineSun);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_ENGINE_SUN);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoBinario(),"application/octet-stream");
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoXml(),"text/xml");
		String javaVersion = System.getProperty("java.version");
		System.out.println("JavaVersion: "+ javaVersion);
		String errorJava7 = "com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusive cannot be cast to com.sun.org.apache.xml.internal.security.transforms.TransformSpi";
		try{
			client.run();
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());	
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
		}catch(Exception e){
			if(e.getMessage().contains(errorJava7)){
				System.out.println("Warning: SunEngine for sign different in Java7, error occurs for test 'SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_SUN'");
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Error processing signature for element : {http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/}Intestazione with Id");
				this.erroriAttesiOpenSPCoopCore.add(err);
				
				this.repositorySincronoWSSSoapBoxSignatureAttachments_engineSun_checkDB = false;
			}
			else{
				throw e;
			}
		}
	}
	@DataProvider (name="SincronoWSSSoapBoxSignatureAttachments_engineSun")
	public Object[][]testSincronoWSSSoapBoxSignatureAttachments_engineSun()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxSignatureAttachments_engineSun.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_SUN"},dataProvider="SincronoWSSSoapBoxSignatureAttachments_engineSun",dependsOnMethods={"sincronoWSSSoapBoxSignatureAttachments_engineSun"})
	public void testSincronoWSSSoapBoxSignatureAttachments_engineSun(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			if(this.repositorySincronoWSSSoapBoxSignatureAttachments_engineSun_checkDB){
				this.collaborazioneSPCoopBase.testSincrono(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_SIGNATURE_ENGINE_SUN, checkServizioApplicativo,null);
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature Con Attachments (Engine 'xmlSec')
	 */
	Repository repositorySincronoWSSSoapBoxSignatureAttachments_engineXmlSec=new Repository();
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC"},
			description="Test per il profilo di collaborazione Sincrono con WSSecurity SoapBox Signature Con Attachments (Engine 'xmlSec')")
	public void sincronoWSSSoapBoxSignatureAttachments_engineXmlSec() throws TestSuiteException, IOException, SOAPException{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoWSSSoapBoxSignatureAttachments_engineXmlSec);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBox(), false,addIDUnivoco);
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoBinario(),"application/octet-stream");
		client.addAttachment(Utilities.testSuiteProperties.getSoapTestWSSecuritySoapBoxAllegatoXml(),"text/xml");
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());	
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoWSSSoapBoxSignatureAttachments_engineXmlSec")
	public Object[][]testSincronoWSSSoapBoxSignatureAttachments_engineXmlSec()throws Exception{
		String id=this.repositorySincronoWSSSoapBoxSignatureAttachments_engineXmlSec.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecuritySoapBox.ID_GRUPPO,WSSecuritySoapBox.ID_GRUPPO+".SOAPBOX_SINCRONO_SIGNATURE_ATTACHMENTS_ENGINE_XMLSEC"},dataProvider="SincronoWSSSoapBoxSignatureAttachments_engineXmlSec",dependsOnMethods={"sincronoWSSSoapBoxSignatureAttachments_engineXmlSec"})
	public void testSincronoWSSSoapBoxSignatureAttachments_engineXmlSec(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SOAPBOX_SIGNATURE_ATTACHMENTS_SIGNATURE_ENGINE_XMLSEC, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}
