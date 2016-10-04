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
public class SOAPHeaderEmpty {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "SOAPHeaderEmpty";
	
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
	 * Test con messaggio SOAP con header senza child
	 */
	Repository repositorySincronoSoapHeaderVuoto=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_HEADER_VUOTO"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoSoapHeaderVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoSoapHeaderVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapHeaderEmptyFileName(), false,false);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
	}
	@DataProvider (name="SincronoSoapHeaderVuoto")
	public Object[][]testSincronoSoapHeaderVuoto()throws Exception{
		String id=this.repositorySincronoSoapHeaderVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_HEADER_VUOTO"},
			dataProvider="SincronoSoapHeaderVuoto",dependsOnMethods={"sincronoSoapHeaderVuoto"})
	public void testSincronoSoapHeaderVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP con header senza child stateful
	 */
	Repository repositorySincronoStatefulSoapHeaderVuoto=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_HEADER_VUOTO"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoStatefulSoapHeaderVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoStatefulSoapHeaderVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapHeaderEmptyFileName(), false,false);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
	}
	@DataProvider (name="SincronoStatefulSoapHeaderVuoto")
	public Object[][]testSincronoStatefulSoapHeaderVuoto()throws Exception{
		String id=this.repositorySincronoStatefulSoapHeaderVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_HEADER_VUOTO"},
			dataProvider="SincronoStatefulSoapHeaderVuoto",dependsOnMethods={"sincronoStatefulSoapHeaderVuoto"})
	public void testSincronoStatefulSoapHeaderVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP senza header
	 */
	Repository repositorySincronoSoapHeaderNonPresente=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_HEADER_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoSoapHeaderNonPresente() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoSoapHeaderNonPresente);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapSenzaHeaderFileName(), false,false);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
	}
	@DataProvider (name="SincronoSoapHeaderNonPresente")
	public Object[][]testSincronoSoapHeaderNonPresente()throws Exception{
		String id=this.repositorySincronoSoapHeaderNonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_HEADER_NON_PRESENTE"},
			dataProvider="SincronoSoapHeaderNonPresente",dependsOnMethods={"sincronoSoapHeaderNonPresente"})
	public void testSincronoSoapHeaderNonPresente(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP con header senza header element stateful
	 */
	Repository repositorySincronoStatefulSoapHeaderNonPresente=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_HEADER_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoStatefulSoapHeaderNonPresente() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoStatefulSoapHeaderNonPresente);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoapSenzaHeaderFileName(), false,false);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
	}
	@DataProvider (name="SincronoStatefulSoapHeaderNonPresente")
	public Object[][]testSincronoStatefulSoapHeaderNonPresente()throws Exception{
		String id=this.repositorySincronoStatefulSoapHeaderNonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_HEADER_NON_PRESENTE"},
			dataProvider="SincronoStatefulSoapHeaderNonPresente",dependsOnMethods={"sincronoStatefulSoapHeaderNonPresente"})
	public void testSincronoStatefulSoapHeaderNonPresente(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP con header senza child
	 */
	Repository repositorySincronoSoapWithAttachmentsHeaderVuoto=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_WITH_ATTACHMENTS_HEADER_VUOTO"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoSoapWithAttachmentsHeaderVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoSoapWithAttachmentsHeaderVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachmentsHeaderEmptyFileName(), false,true);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
		
		// Test uguaglianza attachments
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoSoapWithAttachmentsHeaderVuoto")
	public Object[][]testSincronoSoapWithAttachmentsHeaderVuoto()throws Exception{
		String id=this.repositorySincronoSoapWithAttachmentsHeaderVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_WITH_ATTACHMENTS_HEADER_VUOTO"},
			dataProvider="SincronoSoapWithAttachmentsHeaderVuoto",dependsOnMethods={"sincronoSoapWithAttachmentsHeaderVuoto"})
	public void testSincronoSoapWithAttachmentsHeaderVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP con header senza child stateful
	 */
	Repository repositorySincronoStatefulSoapWithAttachmentsHeaderVuoto=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_WITH_ATTACHMENTS_HEADER_VUOTO"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoStatefulSoapWithAttachmentsHeaderVuoto() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoStatefulSoapWithAttachmentsHeaderVuoto);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachmentsHeaderEmptyFileName(), false,true);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
		
		// Test uguaglianza attachments
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoStatefulSoapWithAttachmentsHeaderVuoto")
	public Object[][]testSincronoStatefulSoapWithAttachmentsHeaderVuoto()throws Exception{
		String id=this.repositorySincronoStatefulSoapWithAttachmentsHeaderVuoto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_WITH_ATTACHMENTS_HEADER_VUOTO"},
			dataProvider="SincronoStatefulSoapWithAttachmentsHeaderVuoto",dependsOnMethods={"sincronoStatefulSoapWithAttachmentsHeaderVuoto"})
	public void testSincronoStatefulSoapWithAttachmentsHeaderVuoto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP senza header
	 */
	Repository repositorySincronoSoapWithAttachmentsHeaderNonPresente=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_WITH_ATTACHMENTS_HEADER_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoSoapWithAttachmentsHeaderNonPresente() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoSoapWithAttachmentsHeaderNonPresente);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachmentsSenzaHeaderFileName(), false,true);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
		
		// Test uguaglianza attachments
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoSoapWithAttachmentsHeaderNonPresente")
	public Object[][]testSincronoSoapWithAttachmentsHeaderNonPresente()throws Exception{
		String id=this.repositorySincronoSoapWithAttachmentsHeaderNonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_SOAP_WITH_ATTACHMENTS_HEADER_NON_PRESENTE"},
			dataProvider="SincronoSoapWithAttachmentsHeaderNonPresente",dependsOnMethods={"sincronoSoapWithAttachmentsHeaderNonPresente"})
	public void testSincronoSoapWithAttachmentsHeaderNonPresente(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test con messaggio SOAP con header senza header element stateful
	 */
	Repository repositorySincronoStatefulSoapWithAttachmentsHeaderNonPresente=new Repository();
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_HEADER_HEADER_NON_PRESENTE"},description="Test di tipo sincrono, Viene controllato se i Header sono uguali e se gli attachment sono uguali")
	public void sincronoStatefulSoapWithAttachmentsHeaderNonPresente() throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositorySincronoStatefulSoapWithAttachmentsHeaderNonPresente);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_SOAP_HEADER_EMPTY_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachmentsSenzaHeaderFileName(), false,true);
		client.run();

		// Test uguaglianza Header is empty
		if(client.sentMessage.getSOAPHeader()!=null){
			Reporter.log("Request has child nodes: "+client.sentMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.sentMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Request senza header SOAP");
		}
		if(client.receivedMessage.getSOAPHeader()!=null){
			Reporter.log("Response has child nodes: "+client.receivedMessage.getSOAPHeader().hasChildNodes());
			Assert.assertTrue(!client.receivedMessage.getSOAPHeader().hasChildNodes());
		}
		else{
			Reporter.log("Response senza header SOAP");
		}
		
		// Test uguaglianza attachments
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoStatefulSoapWithAttachmentsHeaderNonPresente")
	public Object[][]testSincronoStatefulSoapWithAttachmentsHeaderNonPresente()throws Exception{
		String id=this.repositorySincronoStatefulSoapWithAttachmentsHeaderNonPresente.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={SOAPHeaderEmpty.ID_GRUPPO,SOAPHeaderEmpty.ID_GRUPPO+".SINCRONO_STATEFUL_SOAP_HEADER_HEADER_NON_PRESENTE"},
			dataProvider="SincronoStatefulSoapWithAttachmentsHeaderNonPresente",dependsOnMethods={"sincronoStatefulSoapWithAttachmentsHeaderNonPresente"})
	public void testSincronoStatefulSoapWithAttachmentsHeaderNonPresente(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_HEADER_EMTPY_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
}
