/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.io.File;

import org.apache.axis.AxisFault;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.units.utils.OpenSPCoopDetailsUtilities;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDResourceResolver;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contiene utility per i test effettuati.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Utilities {

	/** 
	 * Counter utilizzato per la creazione dell'identificativo eGov
	 */
	public static int counterIDSeriale = 1;
	
	/** TestSuite Properties */
	public static TestSuiteProperties testSuiteProperties = TestSuiteProperties.getInstance();
	
	/** Database Properties */
	public static org.openspcoop2.testsuite.units.utils.DatabaseProperties databaseProperties = DatabaseProperties.getInstance();
	
	public static String readApplicationServerVersion() throws Exception{
		byte[] local_env = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(new File("local_env.xml"));
		AbstractXMLUtils xmlUtils = XMLUtils.DEFAULT;
		XPathExpressionEngine engine = new XPathExpressionEngine(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
		Document local_env_document = xmlUtils.newDocument(local_env);
		String version_jbossas = engine.getStringMatchPattern(local_env_document, DynamicNamespaceContextFactory.getInstance(OpenSPCoop2MessageFactory.getDefaultMessageFactory()).getNamespaceContext(local_env_document), "//property[@name='as']/@value");
		return version_jbossas;
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+codiceErrore.getCodice();
	}
	
	public static String toString(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.testsuite.core.Utilities.toString(codiceErrore, CostantiTestSuite.PROTOCOL_NAME);
	}
	
	public static String toString(MessaggiFaultErroreCooperazione msgErrore) throws ProtocolException{
		return org.openspcoop2.testsuite.core.Utilities.toString(msgErrore, CostantiTestSuite.PROTOCOL_NAME);
	}
	
	public static String toString(ErroreCooperazione errore) throws ProtocolException{
		return org.openspcoop2.testsuite.core.Utilities.toString(errore, CostantiTestSuite.PROTOCOL_NAME);
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore,String prefix,boolean isGeneric) throws ProtocolException{
		return org.openspcoop2.testsuite.core.Utilities.toString(codiceErrore, prefix,isGeneric, CostantiTestSuite.PROTOCOL_NAME);
	}
	
	
	public static String convertSystemTimeIntoString(long beforeRunTest,long afterRunTest){
		return convertSystemTimeIntoString_millisecondi(beforeRunTest,afterRunTest,false);
	}
	public static String convertSystemTimeIntoString_millisecondi(long beforeRunTest,long afterRunTest,boolean millisecondiCheck){
		long millisecondi = (afterRunTest - beforeRunTest)%1000;
		long diff = (afterRunTest - beforeRunTest)/1000;
		long ore = diff/3600;
		long minuti = (diff%3600) / 60;
		long secondi = (diff%3600) % 60;
		StringBuffer bf = new StringBuffer();
		if(ore==1)
			bf.append(ore+" ora ");
		else if(ore>0)
			bf.append(ore+" ore ");
		if(minuti==1)
			bf.append(minuti+" minuto ");
		else if(minuti>0)
			bf.append(minuti+" minuti ");
		if(secondi==1)
			bf.append(secondi+" secondo ");
		else if(secondi>0)
			bf.append(secondi+" secondi ");
		if(millisecondiCheck){
			if(millisecondi==1)
				bf.append(millisecondi+" millisecondo ");
			else if(millisecondi>0)
				bf.append(millisecondi+" millisecondi");
		}
		if(bf.length()==0){
			bf.append("test non riuscito");
		}
		return bf.toString();
	}
	
	
	public static void sendMsg_CacheStartupTime(String urlServizio,String pd,String fileName) throws Exception{
		// invioUnMessaggio per cache
		ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
		client.setUrlPortaDiDominio(urlServizio);
		client.setPortaDelegata(pd);
		client.connectToSoapEngine();
		client.setMessageFromFile(fileName, false);
		client.setRispostaDaGestire(true);
		client.run();
	}
	
	public static void verificaFaultIntegrazione(AxisFault error, String identificativoPortaAtteso,String identificativoFunzioneAtteso,
			String codiceEccezioneAtteso, String descrizioneEccezioneAttesa, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		verificaFaultIntegrazione(error, org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR, identificativoPortaAtteso, identificativoFunzioneAtteso, codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);
	}
	public static void verificaFaultIntegrazione(AxisFault error,String actor, String identificativoPortaAtteso,String identificativoFunzioneAtteso,
			String codiceEccezioneAtteso, String descrizioneEccezioneAttesa, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
		Reporter.log("Controllo actor ["+actor+"]");
		Assert.assertTrue(actor.equals(error.getFaultActor()));
		Reporter.log("Controllo fault code ["+codiceEccezioneAtteso+"]");
		Assert.assertTrue(codiceEccezioneAtteso.equals(error.getFaultCode().getLocalPart()));
		Reporter.log("Controllo fault string (match esatto:"+checkDescrizioneTramiteMatchEsatto+") ["+descrizioneEccezioneAttesa+"] ("+error.getFaultString()+")");
		if(checkDescrizioneTramiteMatchEsatto)
			Assert.assertTrue(error.getFaultString().equals(descrizioneEccezioneAttesa));
		else
			Assert.assertTrue(error.getFaultString().contains(descrizioneEccezioneAttesa));
		Reporter.log("Controllo xml errore applicativo cnipa definito nei details");
		Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,identificativoPortaAtteso,identificativoFunzioneAtteso,
				codiceEccezioneAtteso,descrizioneEccezioneAttesa,checkDescrizioneTramiteMatchEsatto);	
	}
	
	public static void verificaFaultDetailsRispettoErroreApplicativoCnipa(AxisFault error,String identificativoPortaAtteso,String identificativoFunzioneAtteso,
			String codiceEccezioneAtteso, String descrizioneEccezioneAttesa, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		
		// XML Applicativo
		Element [] details = error.getFaultDetails();
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Element erroreApplicativo = null;
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("MessaggioDiErroreApplicativo".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il messaggio di errore applicativo 
				// come definito nella specifica CNIPA 
				// (  Sistema Pubblico di cooperazione: Porta di Dominio v1.0   a pg 41)
				erroreApplicativo = detail;
				break;
			}
		}

		verificaErroreApplicativoCnipa(erroreApplicativo, identificativoPortaAtteso, identificativoFunzioneAtteso, 
				codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);
	}
	
	public static void verificaErroreApplicativoCnipa(Node erroreApplicativo,String identificativoPortaAtteso,String identificativoFunzioneAtteso,
			String codiceEccezioneAtteso, String descrizioneEccezioneAttesa, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		
		String xml = null;
		
		try{

			Assert.assertTrue(erroreApplicativo!=null);
			xml = XMLUtils.DEFAULT.toString(erroreApplicativo);
			Reporter.log("Errore Applicativo CNIPA ("+erroreApplicativo.getNamespaceURI()+"): "+xml);
			Assert.assertTrue("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(erroreApplicativo.getNamespaceURI()));
			
			Reporter.log("Validazione xsd");
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			xsdResourceResolver.addResource("Busta.xsd", Utilities.class.getResourceAsStream("/Busta.xsd"));
			xsdResourceResolver.addResource("soapEnvelope.xsd", Utilities.class.getResourceAsStream("/soapEnvelope.xsd"));
			xsdResourceResolver.addResource("wssecurityUtility.xsd", Utilities.class.getResourceAsStream("/wssecurityUtility.xsd"));
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),LoggerWrapperFactory.getLogger(Utilities.class),
					xsdResourceResolver,Utilities.class.getResourceAsStream("/EccezioneCNIPA.xsd"));
			validatoreXSD.valida(erroreApplicativo);
			
			Reporter.log("Navigazione...");
			Assert.assertTrue(erroreApplicativo.hasChildNodes());
			NodeList listErroreApplicativo = erroreApplicativo.getChildNodes();
			Assert.assertTrue(listErroreApplicativo!=null);
			Assert.assertTrue(listErroreApplicativo.getLength()>0);
			boolean oraRegistrazioneOk = false;
			boolean identificativoPortaOk = false;
			boolean identificativoFunzioneOk = false;
			boolean eccezioneOk = false;
			for(int i=0; i<listErroreApplicativo.getLength(); i++){
				Node n = listErroreApplicativo.item(i);
				Reporter.log("Errore Applicativo CNIPA, elemento ["+n.getLocalName()+"] ["+n.getNamespaceURI()+"]");
				if("OraRegistrazione".equals(n.getLocalName())){
					if(oraRegistrazioneOk){
						throw new Exception("Elemento OraRegistrazione presente piu' di una volta all'interno dell'errore applicativo");
					}
					oraRegistrazioneOk = true;
					Assert.assertTrue( "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(n.getNamespaceURI()) );
					String ora = n.getTextContent();
					Assert.assertTrue(ora!=null);
					Reporter.log("ora: "+ora);
				}else if("IdentificativoPorta".equals(n.getLocalName())){
					if(identificativoPortaOk){
						throw new Exception("Elemento IdentificativoPorta presente piu' di una volta all'interno dell'errore applicativo");
					}
					identificativoPortaOk = true;
					Assert.assertTrue( "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(n.getNamespaceURI()) );
					String idPortaValue = n.getTextContent();
					Reporter.log("Controllo identificativoPorta presente["+idPortaValue+"] atteso["+identificativoPortaAtteso+"]");
					Assert.assertTrue(idPortaValue.equals(identificativoPortaAtteso));
				}
				else if("IdentificativoFunzione".equals(n.getLocalName())){
					if(identificativoFunzioneOk){
						throw new Exception("Elemento IdentificativoFunzione presente piu' di una volta all'interno dell'errore applicativo");
					}
					identificativoFunzioneOk = true;
					Assert.assertTrue( "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(n.getNamespaceURI()) );
					String idFunzioneValue = n.getTextContent();
					Reporter.log("Controllo identificativoFunzione presente["+idFunzioneValue+"] atteso["+identificativoFunzioneAtteso+"]");
					Assert.assertTrue(idFunzioneValue.equals(identificativoFunzioneAtteso));
				}
				else if("Eccezione".equals(n.getLocalName())){
					if(eccezioneOk){
						throw new Exception("Elemento Eccezione presente piu' di una volta all'interno dell'errore applicativo");
					}
					eccezioneOk = true;
					Assert.assertTrue( "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(n.getNamespaceURI()) );
					Assert.assertTrue(n.hasChildNodes()==true);
					NodeList listTipoEccezione = n.getChildNodes();
					Assert.assertTrue(listTipoEccezione!=null);
					Assert.assertTrue(listTipoEccezione.getLength()>0);
					boolean findTipoEccezioneBusta = false;
					boolean findTipoEccezioneProcessamento = false;
					Node tipoEccezione = null;
					for(int j=0; j<listTipoEccezione.getLength(); j++){
						Node tmp = listTipoEccezione.item(j);
						Reporter.log("Errore Applicativo CNIPA, tipo eccezione ["+tmp.getLocalName()+"]");
						if("EccezioneBusta".equals(tmp.getLocalName())){
							if(findTipoEccezioneBusta){
								throw new Exception("Elemento EccezioneBusta presente piu' di una volta all'interno dell'errore applicativo, nell'eccezione");
							}
							findTipoEccezioneBusta = true;
							Assert.assertTrue( "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(tmp.getNamespaceURI()) );
							Assert.assertTrue(tipoEccezione==null); // altrimenti ho gia' precedentemente trovato un tipo di eccezione processamento 
							tipoEccezione = tmp;
						}
						else if("EccezioneProcessamento".equals(tmp.getLocalName())){
							if(findTipoEccezioneProcessamento){
								throw new Exception("Elemento EccezioneBusta presente piu' di una volta all'interno dell'errore applicativo, nell'eccezione");
							}
							findTipoEccezioneProcessamento = true;
							Assert.assertTrue( "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(tmp.getNamespaceURI()) );
							Assert.assertTrue(tipoEccezione==null); // altrimenti ho gia' precedentemente trovato un tipo di eccezione busta 
							tipoEccezione = tmp;
						}
					}
					if(findTipoEccezioneBusta && findTipoEccezioneProcessamento){
						throw new Exception("Trovato piu' di un tipo di eccezione all'interno dell'errore applicativo");
					}
					if(!findTipoEccezioneBusta && !findTipoEccezioneProcessamento){
						throw new Exception("Non e' stato trovato un tipo di eccezione all'interno dell'errore applicativo");
					}
					
					if(codiceEccezioneAtteso.startsWith(SPCoopCostanti.ECCEZIONE_PREFIX_CODE)){
						Assert.assertTrue(findTipoEccezioneBusta);
					}else{
						Assert.assertTrue(findTipoEccezioneProcessamento);
					}
					
					// Controllo attributi				
					Assert.assertTrue(tipoEccezione.hasChildNodes()==false);
					NamedNodeMap attributi = tipoEccezione.getAttributes();
					Assert.assertTrue(attributi!=null);
					Assert.assertTrue(attributi.getLength()>=2);
					
					Attr codiceEccezione = (Attr) attributi.getNamedItem("codiceEccezione");
					Assert.assertTrue(codiceEccezione!=null);
					String valueEccezione = codiceEccezione.getTextContent();
					Reporter.log("Controllo codiceEccezione presente["+valueEccezione+"] atteso["+codiceEccezioneAtteso+"]");
					Assert.assertTrue(valueEccezione.equals(codiceEccezioneAtteso));
					
					Attr descrizioneEccezione = (Attr) attributi.getNamedItem("descrizioneEccezione");
					Assert.assertTrue(descrizioneEccezione!=null);
					valueEccezione = descrizioneEccezione.getTextContent();
					Reporter.log("Controllo descrizioneEccezione presente["+valueEccezione+"] atteso["+descrizioneEccezioneAttesa+"]");
					Assert.assertTrue(valueEccezione.contains(descrizioneEccezioneAttesa));
				}
				else if(n.getLocalName()!=null){
					throw new Exception("Elemento ["+n.getLocalName()+"] presente all'interno dell'errore applicativo non atteso");
				}
			}
			Assert.assertTrue(oraRegistrazioneOk);
			Assert.assertTrue(identificativoPortaOk);
			Assert.assertTrue(identificativoFunzioneOk);
			Assert.assertTrue(eccezioneOk);
		}catch(Exception e){
			if(xml!=null){
				System.out.println("----------------------");
				System.out.println(xml);
				System.out.println(e.getMessage());
			}
			throw e;
		}
	}

	

	public static void verificaDetailsOpenSPCoopExample(Node erroreDetailsOpenSPCoop){
		Assert.assertTrue("http://www.openspcoop.org/example".equals(erroreDetailsOpenSPCoop.getNamespaceURI()));
		NodeList nodeList = erroreDetailsOpenSPCoop.getChildNodes();
		Assert.assertTrue(nodeList!=null);
		Reporter.log("Child OpenSPCoop ["+nodeList.getLength()+"]");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			Reporter.log("Child OpenSPCoop ["+i+"]="+n.getLocalName());
		}
		Assert.assertTrue(nodeList.getLength()>=2);
		boolean errore = false;
		boolean errore2 = false;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if("errore".equals(n.getLocalName())){
				errore = true;
				Assert.assertTrue("http://www.openspcoop.org/example".equals(n.getNamespaceURI()));
				Assert.assertTrue(n.getAttributes()!=null);
				Assert.assertTrue(n.getAttributes().getLength()==1 || n.getAttributes().getLength()==2);
				Assert.assertTrue(n.getAttributes().getNamedItem("tipo")!=null);
				Assert.assertTrue(n.getAttributes().getNamedItem("tipo").getNodeValue()!=null);
				Assert.assertTrue(n.getAttributes().getNamedItem("tipo").getNodeValue().equals("openspcoopTest"));
				Assert.assertTrue(n.getTextContent()!=null);
				Assert.assertTrue(n.getTextContent().equals("TEST"));
			}
			else if("errore2".equals(n.getLocalName())){
				errore2 = true;
				Assert.assertTrue("http://www.openspcoop.org/example".equals(n.getNamespaceURI()));
				Assert.assertTrue(n.getAttributes()!=null);
				Assert.assertTrue(n.getAttributes().getLength()==1 || n.getAttributes().getLength()==2);
				Assert.assertTrue(n.getAttributes().getNamedItem("tipo")!=null);
				Assert.assertTrue(n.getAttributes().getNamedItem("tipo").getNodeValue()!=null);
				Assert.assertTrue(n.getAttributes().getNamedItem("tipo").getNodeValue().equals("openspcoopTestAnnidato"));
				
				NodeList nodeInternoList = n.getChildNodes();
				Assert.assertTrue(nodeInternoList!=null);
				Assert.assertTrue(nodeInternoList.getLength()>=1);
				boolean trovatoInterno = false;
				for (int j = 0; j < nodeInternoList.getLength(); j++) {
					Node nInterno = nodeInternoList.item(j);
					if("errore2Annidato".equals(nInterno.getLocalName())){
						trovatoInterno = true;
						Assert.assertTrue("http://www.openspcoop.org/example".equals(nInterno.getNamespaceURI()));
						Assert.assertTrue(nInterno.getAttributes()!=null);
						Assert.assertTrue(nInterno.getAttributes().getLength()==1 || n.getAttributes().getLength()==2);
						Assert.assertTrue(nInterno.getAttributes().getNamedItem("tipo")!=null);
						Assert.assertTrue(nInterno.getAttributes().getNamedItem("tipo").getNodeValue()!=null);
						Assert.assertTrue(nInterno.getAttributes().getNamedItem("tipo").getNodeValue().equals("openspcoopInterno"));
						Assert.assertTrue(nInterno.getTextContent()!=null);
						Assert.assertTrue(nInterno.getTextContent().equals("TESTANNIDATO"));
					}
				}
				Assert.assertTrue(trovatoInterno);
			}
		}
		Assert.assertTrue(errore);
		Assert.assertTrue(errore2);
	}
	
	public static void verificaIntegrationManagerException(AxisFault error,String identificativoPortaAtteso,String identificativoFunzioneAtteso,
			String codiceEccezioneAtteso, String descrizioneEccezioneAttesa, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		
		// XML Applicativo
		Element [] details = error.getFaultDetails();
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Element erroreIntegrationManagerException = null;
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("IntegrationManagerException".equals(detail.getLocalName())){
				erroreIntegrationManagerException = detail;
				break;
			}
		}

		verificaIntegrationManagerException(erroreIntegrationManagerException, identificativoPortaAtteso, identificativoFunzioneAtteso, 
				codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);
	}
	
	public static void verificaIntegrationManagerException(Node erroreIntegrationManagerException,String identificativoPortaAtteso,String identificativoFunzioneAtteso,
			String codiceEccezioneAtteso, String descrizioneEccezioneAttesa, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		
		String xml = null;
		
		try{

			Assert.assertTrue(erroreIntegrationManagerException!=null);
			xml = XMLUtils.DEFAULT.toString(erroreIntegrationManagerException);
			Reporter.log("Errore IntegrationManagerException ("+erroreIntegrationManagerException.getNamespaceURI()+"): "+xml);
			Assert.assertTrue("http://services.pdd.openspcoop2.org".equals(erroreIntegrationManagerException.getNamespaceURI()));
			
			Reporter.log("Navigazione...");
			Assert.assertTrue(erroreIntegrationManagerException.hasChildNodes());
			NodeList listErrore = erroreIntegrationManagerException.getChildNodes();
			Assert.assertTrue(listErrore!=null);
			Assert.assertTrue(listErrore.getLength()>0);
			boolean oraRegistrazioneOk = false;
			boolean identificativoPortaOk = false;
			boolean identificativoFunzioneOk = false;
			boolean tipoEccezioneOk = false;
			boolean codiceEccezioneOk = false;
			boolean descrizioneEccezioneOk = false;
			for(int i=0; i<listErrore.getLength(); i++){
				Node n = listErrore.item(i);
				Reporter.log("Errore IntegrationManagerException, elemento ["+n.getLocalName()+"] ["+n.getNamespaceURI()+"]");
				if("oraRegistrazione".equals(n.getLocalName())){
					if(oraRegistrazioneOk){
						throw new Exception("Elemento oraRegistrazione presente piu' di una volta all'interno dell'errore IntegrationManagerException");
					}
					oraRegistrazioneOk = true;
					Assert.assertTrue( "http://services.pdd.openspcoop2.org".equals(n.getNamespaceURI()) );
					String ora = n.getTextContent();
					Assert.assertTrue(ora!=null);
					Reporter.log("ora: "+ora);
				}else if("identificativoPorta".equals(n.getLocalName())){
					if(identificativoPortaOk){
						throw new Exception("Elemento identificativoPorta presente piu' di una volta all'interno dell'errore applicativo");
					}
					identificativoPortaOk = true;
					Assert.assertTrue( "http://services.pdd.openspcoop2.org".equals(n.getNamespaceURI()) );
					String idPortaValue = n.getTextContent();
					Reporter.log("Controllo identificativoPorta presente["+idPortaValue+"] atteso["+identificativoPortaAtteso+"]");
					Assert.assertTrue(idPortaValue.equals(identificativoPortaAtteso));
				}
				else if("identificativoFunzione".equals(n.getLocalName())){
					if(identificativoFunzioneOk){
						throw new Exception("Elemento identificativoFunzione presente piu' di una volta all'interno dell'errore applicativo");
					}
					identificativoFunzioneOk = true;
					Assert.assertTrue( "http://services.pdd.openspcoop2.org".equals(n.getNamespaceURI()) );
					String idFunzioneValue = n.getTextContent();
					Reporter.log("Controllo identificativoFunzione presente["+idFunzioneValue+"] atteso["+identificativoFunzioneAtteso+"]");
					Assert.assertTrue(idFunzioneValue.equals(identificativoFunzioneAtteso));
				}
				else if("tipoEccezione".equals(n.getLocalName())){
					if(tipoEccezioneOk){
						throw new Exception("Elemento tipoEccezione presente piu' di una volta all'interno dell'errore applicativo");
					}
					tipoEccezioneOk = true;
					Assert.assertTrue( "http://services.pdd.openspcoop2.org".equals(n.getNamespaceURI()) );
					String tipoEccezioneValue = n.getTextContent();
					Reporter.log("Controllo tipoEccezione presente["+tipoEccezioneValue+"] atteso["+SPCoopCostanti.ECCEZIONE_PROCESSAMENTO_SPCOOP+"/"+SPCoopCostanti.ECCEZIONE_INTEGRAZIONE+"]");
					Assert.assertTrue(tipoEccezioneValue.equals(SPCoopCostanti.ECCEZIONE_PROCESSAMENTO_SPCOOP) || tipoEccezioneValue.equals(SPCoopCostanti.ECCEZIONE_INTEGRAZIONE));
				}
				else if("codiceEccezione".equals(n.getLocalName())){
					if(codiceEccezioneOk){
						throw new Exception("Elemento codiceEccezione presente piu' di una volta all'interno dell'errore applicativo");
					}
					codiceEccezioneOk = true;
					Assert.assertTrue( "http://services.pdd.openspcoop2.org".equals(n.getNamespaceURI()) );
					String codiceEccezioneValue = n.getTextContent();
					Reporter.log("Controllo codiceEccezione presente["+codiceEccezioneValue+"] atteso["+codiceEccezioneAtteso+"]");
					Assert.assertTrue(codiceEccezioneValue.equals(codiceEccezioneAtteso));
				}
				else if("descrizioneEccezione".equals(n.getLocalName())){
					if(descrizioneEccezioneOk){
						throw new Exception("Elemento descrizioneEccezione presente piu' di una volta all'interno dell'errore applicativo");
					}
					descrizioneEccezioneOk = true;
					Assert.assertTrue( "http://services.pdd.openspcoop2.org".equals(n.getNamespaceURI()) );
					String descrizioneEccezioneValue = n.getTextContent();
					Reporter.log("Controllo descrizioneEccezione presente["+descrizioneEccezioneValue+"] atteso["+descrizioneEccezioneAttesa+"]");
					Assert.assertTrue(descrizioneEccezioneValue.equals(descrizioneEccezioneAttesa));
				}
				else if(n.getLocalName()!=null){
					throw new Exception("Elemento ["+n.getLocalName()+"] presente all'interno dell'errore applicativo non atteso");
				}
			}
			Assert.assertTrue(oraRegistrazioneOk);
			Assert.assertTrue(identificativoPortaOk);
			Assert.assertTrue(identificativoFunzioneOk);
			Assert.assertTrue(tipoEccezioneOk);
			Assert.assertTrue(codiceEccezioneOk);
			Assert.assertTrue(descrizioneEccezioneOk);
		}catch(Exception e){
			if(xml!=null){
				System.out.println("----------------------");
				System.out.println(xml);
				System.out.println(e.getMessage());
			}
			throw e;
		}
	}
	
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS = OpenSPCoopDetailsUtilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS = OpenSPCoopDetailsUtilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
}


