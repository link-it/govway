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



package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.io.File;
import java.util.List;

import org.apache.axis.AxisFault;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.constants.Costanti;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.DynamicNamespaceContextFactory;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.message.XPathExpressionEngine;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.Repository;
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
		AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
		XPathExpressionEngine engine = new XPathExpressionEngine();
		Document local_env_document = xmlUtils.newDocument(local_env);
		String version_jbossas = engine.getStringMatchPattern(local_env_document, DynamicNamespaceContextFactory.getInstance().getNamespaceContext(local_env_document), "//property[@name='as']/@value");
		return version_jbossas;
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore) throws ProtocolException{
		return "OPENSPCOOP2_ORG_"+codiceErrore.getCodice();
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
		verificaFaultIntegrazione(error, CostantiPdD.OPENSPCOOP2, identificativoPortaAtteso, identificativoFunzioneAtteso, codiceEccezioneAtteso, descrizioneEccezioneAttesa, checkDescrizioneTramiteMatchEsatto);
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
			xml = XMLUtils.getInstance().toString(erroreApplicativo);
			Reporter.log("Errore Applicativo CNIPA ("+erroreApplicativo.getNamespaceURI()+"): "+xml);
			Assert.assertTrue("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(erroreApplicativo.getNamespaceURI()));
			
			Reporter.log("Validazione xsd");
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			xsdResourceResolver.addResource("Busta.xsd", Utilities.class.getResourceAsStream("/Busta.xsd"));
			xsdResourceResolver.addResource("soapEnvelope.xsd", Utilities.class.getResourceAsStream("/soapEnvelope.xsd"));
			xsdResourceResolver.addResource("wssecurityUtility.xsd", Utilities.class.getResourceAsStream("/wssecurityUtility.xsd"));
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(LoggerWrapperFactory.getLogger(Utilities.class),
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
					
					if(codiceEccezioneAtteso.startsWith("EGOV_IT_")){
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

	public static boolean existsOpenSPCoopDetails(AxisFault error){
		Element [] details = error.getFaultDetails();
		return existsOpenSPCoopDetails(details);
	}
	
	public static boolean existsOpenSPCoopDetails(Element [] details){
		// XML Applicativo
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("dettaglio-eccezione".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				boolean result = org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail);
				if(result==false){
					String debugDetail = null;
					try{
						debugDetail = org.openspcoop2.utils.xml.XMLUtils.getInstance().toString(detail);
					}catch(Exception e){}
					System.out.println("Detail["+detail.getLocalName()+"] non e' un DettagliEccezione ["+debugDetail+"]");
					Reporter.log("Detail["+detail.getLocalName()+"] non e' un DettagliEccezione ["+debugDetail+"]");
				}
				return result;
			}
		}
		
		return false;
	}
	
	public static boolean existsOpenSPCoopDetails(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("dettaglio-eccezione".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				return org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail);
			}
		}
		
		return false;
	}
	
	public static Node getOpenSPCoopDetails(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("dettaglio-eccezione".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				if(org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail)){
					return detail;
				}
			}
		}
		
		return null;
	}
	
	public static void verificaFaultOpenSPCoopDetail(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli) throws Exception{
		verificaFaultOpenSPCoopDetail(error, dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, eccezioni, dettagli, true);
	}
	
	public static void verificaFaultOpenSPCoopDetail(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli, boolean verificatutto) throws Exception{
		String[] identificativiFunzioneAttesi = new String[1];
		identificativiFunzioneAttesi[0] = identificativoModuloAtteso;
		verificaFaultOpenSPCoopDetail(getDettaglioOpenSPCoop(error), dominioAtteso, tipoPdDAtteso, identificativiFunzioneAttesi, 
				eccezioni, dettagli, verificatutto);
	}
	
	public static void verificaFaultOpenSPCoopDetail(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli, boolean verificatutto) throws Exception{
		verificaFaultOpenSPCoopDetail(getDettaglioOpenSPCoop(error), dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				eccezioni, dettagli, verificatutto);
	}
	
	private static Node getDettaglioOpenSPCoop(AxisFault error){
		// XML Applicativo
		Element [] details = error.getFaultDetails();
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Element dettaglioOpenSPCoop = null;
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("dettaglio-eccezione".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				if(org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail)){
					dettaglioOpenSPCoop = detail;
				}
				break;
			}
		}
		return dettaglioOpenSPCoop;
	}
	
	public static void verificaFaultOpenSPCoopDetail(Node dettaglioOpenSPCoop,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli, boolean verificatutto) throws Exception{
		
		String xml = null;
		
		try{

			Assert.assertTrue(dettaglioOpenSPCoop!=null);
			xml = XMLUtils.getInstance().toString(dettaglioOpenSPCoop);
			Reporter.log("Dettaglio OpenSPCoop ("+dettaglioOpenSPCoop.getNamespaceURI()+"): "+xml);
			Assert.assertTrue("http://www.openspcoop2.org/core/eccezione/details".equals(dettaglioOpenSPCoop.getNamespaceURI()));
			
			Reporter.log("Validazione xsd");
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(LoggerWrapperFactory.getLogger(Utilities.class),
					xsdResourceResolver,Utilities.class.getResourceAsStream("/openspcoopDetail.xsd"));
			validatoreXSD.valida(dettaglioOpenSPCoop);
			
			Reporter.log("Navigazione...");
			Assert.assertTrue(dettaglioOpenSPCoop.hasChildNodes());
			NodeList listDettaglioOpenSPCoop = dettaglioOpenSPCoop.getChildNodes();
			Assert.assertTrue(listDettaglioOpenSPCoop!=null);
			Assert.assertTrue(listDettaglioOpenSPCoop.getLength()>0);
			boolean dominioOk = false;
			boolean dominioSoggettoOk = false;
			boolean dominioSoggettoTipoOk = false;
			boolean dominioSoggettoNomeOk = false;
			boolean oraRegistrazioneOk = false;
			boolean identificativoPortaOk = false;
			boolean identificativoFunzioneOk = false;
			boolean identificativoModuloOk = false;
			boolean eccezioneOk = false;
			for(int i=0; i<listDettaglioOpenSPCoop.getLength(); i++){
				Node n = listDettaglioOpenSPCoop.item(i);
				Reporter.log("DettaglioOpenSPCoop, elemento ["+n.getLocalName()+"] ["+n.getNamespaceURI()+"]");
				if("ora-registrazione".equals(n.getLocalName())){
					if(oraRegistrazioneOk){
						throw new Exception("Elemento ora-registrazione presente piu' di una volta all'interno del dettaglio di OpenSPCoop");
					}
					oraRegistrazioneOk = true;
					Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(n.getNamespaceURI()) );
					String ora = n.getTextContent();
					Assert.assertTrue(ora!=null);
					Reporter.log("ora: "+ora);
				}else if("dominio".equals(n.getLocalName())){
					if(dominioOk){
						throw new Exception("Elemento dominio presente piu' di una volta all'interno del dettaglio di OpenSPCoop");
					}
					dominioOk = true;
					Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(n.getNamespaceURI()) );
					
					Assert.assertTrue(n.hasChildNodes());
					
					NamedNodeMap attributi = n.getAttributes();
					Assert.assertTrue(attributi!=null);
					Assert.assertTrue(attributi.getLength()==2);
					
					// modulo
					Attr modulo = (Attr) attributi.getNamedItem("modulo");
					Assert.assertTrue(modulo!=null);
					if(identificativoModuloOk){
						throw new Exception("Attributo modulo presente piu' di una volta all'interno del dettaglio.dominio di OpenSPCoop");
					}
					identificativoModuloOk = true;
					String idModuloValue = modulo.getTextContent();
					boolean match=false;
					for(int h=0;h<identificativoModuloAtteso.length;h++){
						Reporter.log("Controllo identificativoFunzione presente["+idModuloValue+"] atteso("+h+")["+identificativoModuloAtteso[h]+"]");
						if(idModuloValue.equals(identificativoModuloAtteso[h])){ 
							match = true;
							break;
						}
					}
					Assert.assertTrue(match);
					
					// funzione
					Attr funzione = (Attr) attributi.getNamedItem("funzione");
					Assert.assertTrue(funzione!=null);
					if(identificativoFunzioneOk){
						throw new Exception("Attributo funzione presente piu' di una volta all'interno del dettaglio.dominio di OpenSPCoop");
					}
					identificativoFunzioneOk = true;
					String idFunzioneValue = funzione.getTextContent();
					String identificativoFunzioneAtteso = null;
					if(tipoPdDAtteso.equals(TipoPdD.DELEGATA)){
						identificativoFunzioneAtteso = Costanti.TIPO_PDD_PORTA_DELEGATA;
					}
					else if(tipoPdDAtteso.equals(TipoPdD.APPLICATIVA)){
						identificativoFunzioneAtteso = Costanti.TIPO_PDD_PORTA_APPLICATIVA;
					}
					else if(tipoPdDAtteso.equals(TipoPdD.INTEGRATION_MANAGER)){
						identificativoFunzioneAtteso = Costanti.TIPO_PDD_INTEGRATION_MANAGER;
					}
					else if(tipoPdDAtteso.equals(TipoPdD.ROUTER)){
						identificativoFunzioneAtteso = Costanti.TIPO_PDD_ROUTER;
					}
					Reporter.log("Controllo identificativoFunzione presente["+idFunzioneValue+"] atteso["+identificativoFunzioneAtteso+"]");
					Assert.assertTrue(idFunzioneValue.equals(identificativoFunzioneAtteso));
					
					// child-node
					NodeList listDominio = n.getChildNodes();
					Assert.assertTrue(listDominio!=null);
					Assert.assertTrue(listDominio.getLength()==2);
					for(int j=0; j<listDominio.getLength(); j++){
						Node nDominio = listDominio.item(j);
						Reporter.log("DettaglioOpenSPCoop.dominio, elemento ["+nDominio.getLocalName()+"] ["+nDominio.getNamespaceURI()+"]");
						
						if("identificativo-porta".equals(nDominio.getLocalName())){
							if(identificativoPortaOk){
								throw new Exception("Elemento identificativo-porta presente piu' di una volta all'interno del dettaglio.dominio di OpenSPCoop");
							}
							identificativoPortaOk = true;
							Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(nDominio.getNamespaceURI()) );
							String idPortaValue = nDominio.getTextContent();
							String identificativoPortaAtteso = dominioAtteso.getCodicePorta();
							Reporter.log("Controllo identificativoPorta presente["+idPortaValue+"] atteso["+identificativoPortaAtteso+"]");
							Assert.assertTrue(idPortaValue.equals(identificativoPortaAtteso));
						}
						
						if("soggetto".equals(nDominio.getLocalName())){
							if(dominioSoggettoOk){
								throw new Exception("Elemento soggetto presente piu' di una volta all'interno del dettaglio.dominio di OpenSPCoop");
							}
							dominioSoggettoOk = true;
							Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(nDominio.getNamespaceURI()) );
							
							// tipo
							NamedNodeMap attributiSoggetto = nDominio.getAttributes();
							Assert.assertTrue(attributiSoggetto!=null);
							Assert.assertTrue(attributiSoggetto.getLength()==1);
							Attr tipoSoggetto = (Attr) attributiSoggetto.getNamedItem("tipo");
							Assert.assertTrue(tipoSoggetto!=null);
							if(dominioSoggettoTipoOk){
								throw new Exception("Attributo tipo presente piu' di una volta all'interno del dettaglio.dominio.soggetto di OpenSPCoop");
							}
							dominioSoggettoTipoOk = true;
							String tipoValue = tipoSoggetto.getTextContent();
							String tipoAtteso = dominioAtteso.getTipo();
							Reporter.log("Controllo tipo presente["+tipoValue+"] atteso["+tipoAtteso+"]");
							Assert.assertTrue(tipoValue.equals(tipoAtteso));
							
							// nome
							if(dominioSoggettoNomeOk){
								throw new Exception("Elemento soggetto presente piu' di una volta all'interno del dettaglio.dominio di OpenSPCoop");
							}
							dominioSoggettoNomeOk = true;
							Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(nDominio.getNamespaceURI()) );
							String nomeValue = nDominio.getTextContent();
							String nomeAtteso = dominioAtteso.getNome();
							Reporter.log("Controllo nome presente["+nomeValue+"] atteso["+nomeAtteso+"]");
							Assert.assertTrue(nomeValue.equals(nomeAtteso));
						}
					}
					
				}
				else if("eccezioni".equals(n.getLocalName())){
					eccezioneOk = true;
					Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(n.getNamespaceURI()) );
					Assert.assertTrue(n.hasChildNodes()==true);
					NodeList listTipoEccezione = n.getChildNodes();
					Assert.assertTrue(listTipoEccezione!=null);
					Assert.assertTrue(listTipoEccezione.getLength()>0);
					boolean findEccezione = false;
					for(int j=0; j<listTipoEccezione.getLength(); j++){
						Node tmp = listTipoEccezione.item(j);
						Reporter.log("Eccezioni, eccezione ["+tmp.getLocalName()+"]");
						if("eccezione".equals(tmp.getLocalName())){
							findEccezione = true;
							Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(tmp.getNamespaceURI()) );
						}
						
						// Controllo attributi				
						Assert.assertTrue(tmp.hasChildNodes()==false);
						NamedNodeMap attributi = tmp.getAttributes();
						Assert.assertTrue(attributi!=null);
						Assert.assertTrue(attributi.getLength()==3 || attributi.getLength()==5);
						
						Attr codiceEccezione = (Attr) attributi.getNamedItem("codice");
						Assert.assertTrue(codiceEccezione!=null);
						String valoreCodiceEccezione = codiceEccezione.getTextContent();
						
						Attr descrizioneEccezione = (Attr) attributi.getNamedItem("descrizione");
						Assert.assertTrue(descrizioneEccezione!=null);
						String valoreDescrizioneEccezione = descrizioneEccezione.getTextContent();
						
						Attr tipoEccezione = (Attr) attributi.getNamedItem("tipo");
						Assert.assertTrue(tipoEccezione!=null);
						String valoreTipoEccezione = tipoEccezione.getTextContent();
						
						// Check eccezione attesa
						boolean findEccezioneAttesa = false;
						
						Reporter.log("Verifico codiceEccezione["+valoreCodiceEccezione+"] descrizioneEccezione["+valoreDescrizioneEccezione+"] tipoEccezione["+valoreTipoEccezione+"]");
						
						for (int k = 0; k < eccezioni.size(); k++) {
														
							Reporter.log("Eccezioni, eccezione ["+tmp.getLocalName()+"] Controllo presenza codice["+eccezioni.get(k).getCodice()
									+"] descrizione["+eccezioni.get(k).getDescrizione()+"] matchEsatto["+eccezioni.get(k).isCheckDescrizioneTramiteMatchEsatto()+"] ...");
							
							// check codice
							if(eccezioni.get(k).getCodice().equals(valoreCodiceEccezione)){
								
								// check tipo
								boolean okTipo = false;
								if(valoreCodiceEccezione.startsWith("EGOV_IT_")){
									if(Costanti.TIPO_ECCEZIONE_PROTOCOLLO.equals(valoreTipoEccezione)){
										okTipo = true;
									}
								}
								else{
									if(Costanti.TIPO_ECCEZIONE_INTEGRAZIONE.equals(valoreTipoEccezione)){
										okTipo = true;
									}
								}
								
								if(okTipo){
								// check descrizione
									if(eccezioni.get(k).isCheckDescrizioneTramiteMatchEsatto()){
										if(eccezioni.get(k).getDescrizione().equals(valoreDescrizioneEccezione)){
											findEccezioneAttesa = true;
										}
									} else if(valoreDescrizioneEccezione.contains(eccezioni.get(k).getDescrizione())){
										findEccezioneAttesa = true;
									}
								}
								
							}
							
							if(findEccezioneAttesa){
								Reporter.log("Eccezioni, eccezione ["+tmp.getLocalName()+"] Controllo presenza codice["+eccezioni.get(k).getCodice()
										+"] descrizione["+eccezioni.get(k).getDescrizione()+"] matchEsatto["+eccezioni.get(k).isCheckDescrizioneTramiteMatchEsatto()+"] FIND");
								eccezioni.remove(k);
								break;
							}
							
						}
						if(!findEccezioneAttesa){
							Reporter.log("Eccezione ("+valoreTipoEccezione+")("+valoreCodiceEccezione+")("+valoreDescrizioneEccezione+") non trovato tra quelli attesi");
						}
						Assert.assertTrue(findEccezioneAttesa);
					}
					Assert.assertTrue(findEccezione);
				
					// Verifico che tutte le eccezioni attese siano state riscontrate
					if(verificatutto) Assert.assertTrue( eccezioni.size() == 0);
				}
				else if("dettagli".equals(n.getLocalName())){
					Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(n.getNamespaceURI()) );
					if(dettagli.size()>0){
						Assert.assertTrue(n.hasChildNodes()==true);
						NodeList listDettagli = n.getChildNodes();
						Assert.assertTrue(listDettagli!=null);
						Assert.assertTrue(listDettagli.getLength()>0);
						boolean findDettaglio = false;
						for(int j=0; j<listDettagli.getLength(); j++){
							Node tmp = listDettagli.item(j);
							Reporter.log("Dettagli, dettaglio ["+tmp.getLocalName()+"]");
							if("dettaglio".equals(tmp.getLocalName())){
								findDettaglio = true;
								Assert.assertTrue( "http://www.openspcoop2.org/core/eccezione/details".equals(tmp.getNamespaceURI()) );
							}
							
							// Controllo attributi				
							Assert.assertTrue(tmp.hasChildNodes());
							NamedNodeMap attributi = tmp.getAttributes();
							Assert.assertTrue(attributi!=null);
							Assert.assertTrue(attributi.getLength()==1);
							
							Attr tipoDettaglio = (Attr) attributi.getNamedItem("tipo");
							Assert.assertTrue(tipoDettaglio!=null);
							String valoreTipo = tipoDettaglio.getTextContent();
							
							String valoreDettaglio = tmp.getTextContent();
							Assert.assertTrue(valoreDettaglio!=null);
							
							// Check eccezione attesa
							boolean findDettaglioAtteso = false;
							for (int k = 0; k < dettagli.size(); k++) {
														
								if(dettagli.get(k).getCodice().equals(valoreTipo)){
									if(dettagli.get(k).isCheckDescrizioneTramiteMatchEsatto()){
										if(dettagli.get(k).getDescrizione().equals(valoreDettaglio)){
											findDettaglioAtteso = true;
										}else if(valoreDettaglio.contains(dettagli.get(k).getDescrizione())){
											findDettaglioAtteso = true;
										}
									}
								}
								
								if(findDettaglioAtteso){
									dettagli.remove(k);
									break;
								}
								
							}
							if(!findDettaglioAtteso){
								Reporter.log("Detail ("+valoreTipo+")("+valoreDettaglio+") non trovato tra quelli attesi");
							}
							Assert.assertTrue(findDettaglioAtteso);
						}
						Assert.assertTrue(findDettaglio);
					
						// Verifico che tutte le dettagli attese siano state riscontrate
						Assert.assertTrue( dettagli.size() == 0);
					}
				}
				else if(n.getLocalName()!=null){
					throw new Exception("Elemento ["+n.getLocalName()+"] presente all'interno del dettaglio di OpenSPCoop non atteso");
				}
			}
			Assert.assertTrue(dominioOk);
			Assert.assertTrue(dominioSoggettoOk);
			Assert.assertTrue(dominioSoggettoTipoOk);
			Assert.assertTrue(dominioSoggettoNomeOk);
			Assert.assertTrue(oraRegistrazioneOk);
			Assert.assertTrue(identificativoPortaOk);
			Assert.assertTrue(identificativoModuloOk);
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
			xml = XMLUtils.getInstance().toString(erroreIntegrationManagerException);
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
	
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS = true;
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS = false;
}


