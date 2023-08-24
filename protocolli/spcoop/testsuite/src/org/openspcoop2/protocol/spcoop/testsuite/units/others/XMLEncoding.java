/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.others;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.axis14.Axis14DynamicNamespaceContextFactory;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Test sulla validazione dei contenuti applicativi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLEncoding {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "XMLEncoding";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	private static boolean addIDUnivoco = true;

	
	
	
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
	 * Test per il profilo di collaborazione xmlEncoding
	 */
	Repository repositoryXMLEncoding=new Repository();
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncoding"},description="Test di tipo xmlEncoding, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void xmlEncoding() throws TestSuiteException, IOException, SOAPException, TransformerException, SAXException, XPathException, XPathNotFoundException, XPathNotValidException, XMLException, ParserConfigurationException{
		// Creazione client xmlEncoding
		ClientSincrono client=new ClientSincrono(this.repositoryXMLEncodingStateful);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_XML_ENCODING);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getXMLEncodingSoapFileName(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		
		//System.out.println(XMLUtils.toString(client.getResponseMessage().getSOAPBody()));
		
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		Axis14DynamicNamespaceContextFactory dncUtils = new Axis14DynamicNamespaceContextFactory();
		DynamicNamespaceContext dnc = dncUtils.getNamespaceContextFromSoapEnvelope11(client.getResponseMessage().getSOAPEnvelope());
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine(messageFactory);
		String msg = null;
		try {
			msg = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).toString(client.getResponseMessage().getSOAPBody());
		}catch(Throwable t) {
			// normalize per conflito di librerie axis - saaj
			org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
			Node n = d.importNode(client.getResponseMessage().getSOAPBody(), true);
			msg = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toString(n,true);
		}
		String value = xpathEngine.getStringMatchPattern(msg, dnc, "//prova2/text()");
		//System.out.println("VALUE = "+value);
		Assert.assertEquals(value, "AMÉLIE");
		
	}
	@DataProvider (name="xmlEncoding")
	public Object[][]testxmlEncoding()throws Exception{
		String id=this.repositoryXMLEncodingStateful.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncoding"},dataProvider="xmlEncoding",dependsOnMethods={"xmlEncoding"})
	public void testxmlEncoding(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_XML_ENCODING, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione xmlEncoding
	 */
	Repository repositoryXMLEncodingSoapWithAttachments=new Repository();
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncodingSoapWithAttachments"},description="Test di tipo xmlEncodingSoapWithAttachments, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void xmlEncodingSoapWithAttachments() throws TestSuiteException, IOException, SOAPException, TransformerException, SAXException, XPathException, XPathNotFoundException, XPathNotValidException, XMLException, ParserConfigurationException{
		// Creazione client xmlEncodingSoapWithAttachments
		ClientSincrono client=new ClientSincrono(this.repositoryXMLEncodingSoapWithAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_XML_ENCODING);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getXMLEncodingSoapWithAttachmentsFileName(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
		
		//System.out.println(XMLUtils.toString(client.getResponseMessage().getSOAPBody()));
		
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		Axis14DynamicNamespaceContextFactory dncUtils = new Axis14DynamicNamespaceContextFactory();
		DynamicNamespaceContext dncBody = dncUtils.getNamespaceContextFromSoapEnvelope11(client.getResponseMessage().getSOAPEnvelope());
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine(messageFactory);
		String msg = null;
		try {
			msg = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).toString(client.getResponseMessage().getSOAPBody());
		}catch(Throwable t) {
			// normalize per conflito di librerie axis - saaj
			org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
			Node n = d.importNode(client.getResponseMessage().getSOAPBody(), true);
			msg = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toString(n,true);
		}
		String valueBody = xpathEngine.getStringMatchPattern(msg, dncBody, "//prova2/text()");
		
			//System.out.println("VALUE = "+value);
		Assert.assertEquals(valueBody, "AMÉLIE");
		
		Iterator<?> it = client.getResponseMessage().getAttachments();
		while(it.hasNext()){
			AttachmentPart ap = (AttachmentPart) it.next();
			String value = (String) ap.getContent();
			//System.out.println("VALUE = ["+value+"]");
			Assert.assertTrue(value.contains("HELLO AMÉLIE WORLD"));
		}
		
		
		
		
	}
	@DataProvider (name="xmlEncodingSoapWithAttachments")
	public Object[][]testxmlEncodingSoapWithAttachments()throws Exception{
		String id=this.repositoryXMLEncodingSoapWithAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncodingSoapWithAttachments"},dataProvider="xmlEncodingSoapWithAttachments",dependsOnMethods={"xmlEncodingSoapWithAttachments"})
	public void testxmlEncodingSoapWithAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_XML_ENCODING, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione xmlEncodingStateful
	 */
	Repository repositoryXMLEncodingStateful=new Repository();
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncodingStateful"},description="Test di tipo xmlEncodingStateful, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void xmlEncodingStateful() throws TestSuiteException, IOException, SOAPException, TransformerException, SAXException, XPathException, XPathNotFoundException, XPathNotValidException, XMLException, ParserConfigurationException{
		// Creazione client xmlEncodingStateful
		ClientSincrono client=new ClientSincrono(this.repositoryXMLEncodingStateful);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_XML_ENCODING_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getXMLEncodingSoapFileName(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		
		//System.out.println(XMLUtils.toString(client.getResponseMessage().getSOAPBody()));
		
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		Axis14DynamicNamespaceContextFactory dncUtils = new Axis14DynamicNamespaceContextFactory();
		DynamicNamespaceContext dnc = dncUtils.getNamespaceContextFromSoapEnvelope11(client.getResponseMessage().getSOAPEnvelope());
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine(messageFactory);
		String msg = null;
		try {
			msg = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).toString(client.getResponseMessage().getSOAPBody());
		}catch(Throwable t) {
			// normalize per conflito di librerie axis - saaj
			org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
			Node n = d.importNode(client.getResponseMessage().getSOAPBody(), true);
			msg = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toString(n,true);
		}
		String value = xpathEngine.getStringMatchPattern(msg, dnc, "//prova2/text()");
		
		//System.out.println("VALUE = "+value);
		Assert.assertEquals(value, "AMÉLIE");
		
	}
	@DataProvider (name="xmlEncodingStateful")
	public Object[][]testxmlEncodingStateful()throws Exception{
		String id=this.repositoryXMLEncodingStateful.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncodingStateful"},dataProvider="xmlEncodingStateful",dependsOnMethods={"xmlEncodingStateful"})
	public void testxmlEncodingStateful(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_XML_ENCODING_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione xmlEncodingStateful
	 */
	Repository repositoryXMLEncodingStatefulSoapWithAttachments=new Repository();
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncodingStatefulSoapWithAttachments"},description="Test di tipo xmlEncodingStatefulSoapWithAttachments, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void xmlEncodingStatefulSoapWithAttachments() throws TestSuiteException, IOException, SOAPException, TransformerException, SAXException, XPathException, XPathNotFoundException, XPathNotValidException, XMLException, ParserConfigurationException{
		// Creazione client xmlEncodingStatefulSoapWithAttachments
		ClientSincrono client=new ClientSincrono(this.repositoryXMLEncodingStatefulSoapWithAttachments);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_XML_ENCODING_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getXMLEncodingSoapWithAttachmentsFileName(), false,addIDUnivoco);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
		
		//System.out.println(XMLUtils.toString(client.getResponseMessage().getSOAPBody()));
		
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		Axis14DynamicNamespaceContextFactory dncUtils = new Axis14DynamicNamespaceContextFactory();
		DynamicNamespaceContext dncBody = dncUtils.getNamespaceContextFromSoapEnvelope11(client.getResponseMessage().getSOAPEnvelope());
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine(messageFactory);
		String msg = null;
		try {
			msg = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(messageFactory).toString(client.getResponseMessage().getSOAPBody());
		}catch(Throwable t) {
			// normalize per conflito di librerie axis - saaj
			org.w3c.dom.Document d = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.newDocument();
			Node n = d.importNode(client.getResponseMessage().getSOAPBody(), true);
			msg = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toString(n,true);
		}
		String valueBody = xpathEngine.getStringMatchPattern(msg, dncBody, "//prova2/text()");
		
		//System.out.println("VALUE = "+value);
		Assert.assertEquals(valueBody, "AMÉLIE");
		
		Iterator<?> it = client.getResponseMessage().getAttachments();
		while(it.hasNext()){
			AttachmentPart ap = (AttachmentPart) it.next();
			String value = (String) ap.getContent();
			//System.out.println("VALUE = ["+value+"]");
			Assert.assertTrue(value.contains("HELLO AMÉLIE WORLD"));
		}
		
		
		
		
	}
	@DataProvider (name="xmlEncodingStatefulSoapWithAttachments")
	public Object[][]testxmlEncodingStatefulSoapWithAttachments()throws Exception{
		String id=this.repositoryXMLEncodingStatefulSoapWithAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiOthers.ID_GRUPPO_OTHERS, XMLEncoding.ID_GRUPPO,XMLEncoding.ID_GRUPPO+".xmlEncodingStatefulSoapWithAttachments"},dataProvider="xmlEncodingStatefulSoapWithAttachments",dependsOnMethods={"xmlEncodingStatefulSoapWithAttachments"})
	public void testxmlEncodingStatefulSoapWithAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_XML_ENCODING_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

}
