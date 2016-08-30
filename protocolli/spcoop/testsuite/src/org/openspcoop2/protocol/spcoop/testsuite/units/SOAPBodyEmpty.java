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

import javax.xml.soap.SOAPException;

import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPBodyEmpty {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "SOAPBodyEmpty";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());



	
	
	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 
	
	
	

	
	
	
	
	



	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoSoapBodyVuoto=new Repository();
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_SOAP_BODY_VUOTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapBodyVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoSoapBodyVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapSenzaBodyFileName(), false,false);
		client.run();

		// Test uguaglianza Body is empty
		Assert.assertTrue(client.sentMessage.getSOAPBody()!=null);
		Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPBody().hasChildNodes());
		//client.sentMessage.writeTo(System.out);
		Assert.assertTrue(!client.sentMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(client.receivedMessage.getSOAPBody()!=null);
		Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(!client.receivedMessage.getSOAPBody().hasChildNodes());
	}
	@DataProvider (name="SincronoSoapBodyVuoto")
	public Object[][]testSincronoSoapBodyVuoto()throws Exception{
		String id=this.repositorySincronoSoapBodyVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_SOAP_BODY_VUOTO"},
			dataProvider="SincronoSoapBodyVuoto",dependsOnMethods={"sincronoSoapBodyVuoto"})
	public void testSincronoSoapBodyVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoStatefulSoapBodyVuoto=new Repository();
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_BODY_VUOTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoStatefulSoapBodyVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoStatefulSoapBodyVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapSenzaBodyFileName(), false,false);
		client.run();

		// Test uguaglianza Body is empty
		Assert.assertTrue(client.sentMessage.getSOAPBody()!=null);
		Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPBody().hasChildNodes());
		//client.sentMessage.writeTo(System.out);
		Assert.assertTrue(!client.sentMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(client.receivedMessage.getSOAPBody()!=null);
		Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(!client.receivedMessage.getSOAPBody().hasChildNodes());
	}
	@DataProvider (name="SincronoStatefulSoapBodyVuoto")
	public Object[][]testSincronoStatefulSoapBodyVuoto()throws Exception{
		String id=this.repositorySincronoStatefulSoapBodyVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_BODY_VUOTO"},
			dataProvider="SincronoStatefulSoapBodyVuoto",dependsOnMethods={"sincronoStatefulSoapBodyVuoto"})
	public void testSincronoStatefulSoapBodyVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoSoapWithAttachments_BodyVuoto=new Repository();
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_SOAP_WITH_ATTACHMENTS_BODY_VUOTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapWithAttachments_BodyVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoSoapWithAttachments_BodyVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachmentsSenzaBodyFileName(), false,true);
		client.run();

		// Test uguaglianza Body is empty
		Assert.assertTrue(client.sentMessage.getSOAPBody()!=null);
		Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPBody().hasChildNodes());
		//client.sentMessage.writeTo(System.out);
		Assert.assertTrue(!client.sentMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(client.receivedMessage.getSOAPBody()!=null);
		Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(!client.receivedMessage.getSOAPBody().hasChildNodes());
		
		// Test uguaglianza attachments
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoSoapWithAttachments_BodyVuoto")
	public Object[][]testSincronoSoapWithAttachments_BodyVuoto()throws Exception{
		String id=this.repositorySincronoSoapWithAttachments_BodyVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_SOAP_WITH_ATTACHMENTS_BODY_VUOTO"},
			dataProvider="SincronoSoapWithAttachments_BodyVuoto",dependsOnMethods={"sincronoSoapWithAttachments_BodyVuoto"})
	public void testSincronoSoapWithAttachments_BodyVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoStatefulSoapWithAttachments_BodyVuoto=new Repository();
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_WITH_ATTACHMENTS_BODY_VUOTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoStatefulSoapWithAttachments_BodyVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoStatefulSoapWithAttachments_BodyVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachmentsSenzaBodyFileName(), false,true);
		client.run();

		// Test uguaglianza Body is empty
		Assert.assertTrue(client.sentMessage.getSOAPBody()!=null);
		Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPBody().hasChildNodes());
		//client.sentMessage.writeTo(System.out);
		Assert.assertTrue(!client.sentMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(client.receivedMessage.getSOAPBody()!=null);
		Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPBody().hasChildNodes());
		Assert.assertTrue(!client.receivedMessage.getSOAPBody().hasChildNodes());
		
		// Test uguaglianza attachments
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoStatefulSoapWithAttachments_BodyVuoto")
	public Object[][]testSincronoStatefulSoapWithAttachments_BodyVuoto()throws Exception{
		String id=this.repositorySincronoStatefulSoapWithAttachments_BodyVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPBodyEmpty.ID_GRUPPO,SOAPBodyEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_WITH_ATTACHMENTS_BODY_VUOTO"},
			dataProvider="SincronoStatefulSoapWithAttachments_BodyVuoto",dependsOnMethods={"sincronoStatefulSoapWithAttachments_BodyVuoto"})
	public void testSincronoStatefulSoapWithAttachments_BodyVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
}
