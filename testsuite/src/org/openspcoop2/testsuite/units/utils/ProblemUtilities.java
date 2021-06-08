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



package org.openspcoop2.testsuite.units.utils;

import org.apache.axis.AxisFault;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.rest.problem.ProblemConstants;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Contiene utility per i test effettuati.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ProblemUtilities {

	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS = OpenSPCoopDetailsUtilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS = OpenSPCoopDetailsUtilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
	

	
	public static boolean existsProblem(AxisFault error){
		Element [] details = error.getFaultDetails();
		return existsProblem(details);
	}
	
	public static boolean existsProblem(Element [] details){
		// XML Applicativo
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("problem".equals(detail.getLocalName())){
				XmlDeserializer deserializer = new XmlDeserializer();
				Reporter.log("Detail["+detail.getLocalName()+"] isProblemRFC7807["+deserializer.isProblemRFC7807(detail)+"]");
				return deserializer.isProblemRFC7807(detail);
			}
		}
		
		return false;
	}
	
	public static boolean existsProblem(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("problem".equals(detail.getLocalName())){
				XmlDeserializer deserializer = new XmlDeserializer();
				return deserializer.isProblemRFC7807(detail);
			}
		}
		
		return false;
	}
	
	public static Node getProblem(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("problem".equals(detail.getLocalName())){
				XmlDeserializer deserializer = new XmlDeserializer();
				// Details, contiene l'xml che forma ill'errore applicativo
				Reporter.log("Detail["+detail.getLocalName()+"] isProblemRFC7807["+deserializer.isProblemRFC7807(detail)+"]");
				if(deserializer.isProblemRFC7807(detail)){
					return detail;
				}
			}
		}
		
		return null;
	}
	
	public static void verificaProblem(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			ExceptionCodeExpected exceptionCodeExpected, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		String[] identificativiFunzioneAttesi = new String[1];
		identificativiFunzioneAttesi[0] = identificativoModuloAtteso;
		verificaProblem(getProblem(error), dominioAtteso, tipoPdDAtteso, identificativiFunzioneAttesi, 
				exceptionCodeExpected, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	
	public static void verificaProblem(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			ExceptionCodeExpected exceptionCodeExpected, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		verificaProblem(getProblem(error), dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				exceptionCodeExpected, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	
	private static Node getProblem(AxisFault error){
		// XML Applicativo
		Element [] details = error.getFaultDetails();
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Element dettaglioOpenSPCoop = null;
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("problem".equals(detail.getLocalName())){
				XmlDeserializer deserializer = new XmlDeserializer();
				// Details, contiene l'xml che forma ill'errore applicativo
				Reporter.log("Detail["+detail.getLocalName()+"] isProblemRFC7807["+deserializer.isProblemRFC7807(detail)+"]");
				if(deserializer.isProblemRFC7807(detail)){
					dettaglioOpenSPCoop = detail;
				}
				break;
			}
		}
		return dettaglioOpenSPCoop;
	}
	
	public static void verificaProblem(Node problemNode,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			ExceptionCodeExpected exceptionCodeExpected, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{

		String xml = null;
		
		try{

			ErroriProperties erroriProperties = ErroriProperties.getInstance(LoggerWrapperFactory.getLogger(ProblemUtilities.class));
			
			Assert.assertTrue(problemNode!=null);
			xml = XMLUtils.DEFAULT.toString(problemNode);
			Reporter.log("Namespace Problem ("+problemNode.getNamespaceURI()+"): "+xml);
			Assert.assertTrue(ProblemConstants.XML_PROBLEM_DETAILS_RFC_7807_NAMESPACE.equals(problemNode.getNamespaceURI()));
			
			XmlDeserializer deserializer = new XmlDeserializer();
			ProblemRFC7807 problemRFC7807 = deserializer.fromNode(problemNode);
			
			int httpStatus = exceptionCodeExpected.getGovWayReturnCode();
			Reporter.log("Controllo stato presente["+problemRFC7807.getStatus()+"] atteso["+httpStatus+"]");
			Assert.assertTrue(problemRFC7807.getStatus()!=null && problemRFC7807.getStatus().intValue() == httpStatus);
			
			//String typeAtteso = "https://httpstatuses.com/"+httpStatus;
			String typeAtteso = erroriProperties.getWebSite_noWrap(exceptionCodeExpected.getIntegrationFunctionError());
			Reporter.log("Controllo type presente["+problemRFC7807.getType()+"] atteso["+typeAtteso+"]");
			Assert.assertTrue(problemRFC7807.getType()!=null && problemRFC7807.getType().equals(typeAtteso));
			
			//String titleAtteso = HttpUtilities.getHttpReason(httpStatus);
			String titleAtteso = erroriProperties.getErrorType_noWrap(exceptionCodeExpected.getIntegrationFunctionError());
			Reporter.log("Controllo title presente["+problemRFC7807.getTitle()+"] atteso["+titleAtteso+"]");
			Assert.assertTrue(problemRFC7807.getTitle()!=null && problemRFC7807.getTitle().equals(titleAtteso));
						
			String value = problemRFC7807.getDetail();
			String atteso = descrizione;
			Reporter.log("Controllo description presente["+value+"] atteso["+atteso+"] checkDescrizioneTramiteMatchEsatto["+checkDescrizioneTramiteMatchEsatto+"]");
			if(checkDescrizioneTramiteMatchEsatto) {
				Assert.assertTrue(value.equals(atteso));
			}
			else {
				Assert.assertTrue(value.contains(atteso));
			}
			
			Reporter.log("Controllo esistenza govway id");
			String govwayTransactionId = null;
			if(problemRFC7807.getCustom()!=null) {
				govwayTransactionId = (String) problemRFC7807.getCustom().get(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID());
			}
			Assert.assertTrue(govwayTransactionId!=null);
			
			if(exceptionCodeExpected.isGenericCode()==false) {
				Reporter.log("Controllo esistenza govway status");
				String govwayStatus = null;
				if(problemRFC7807.getCustom()!=null) {
					govwayStatus = (String) problemRFC7807.getCustom().get(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_CODE());
				}
				Assert.assertTrue(govwayStatus!=null);
				
				String codiceGovWayStatusAtteso = null;
				String code = null;
				if(exceptionCodeExpected.isProtocolException()) {
					codiceGovWayStatusAtteso = org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL;
					code = exceptionCodeExpected.getCodiceErroreSpecifico();
				}
				else {
					codiceGovWayStatusAtteso = org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_INTEGRATION;
					code = exceptionCodeExpected.getCodiceErroreSpecifico();
				}
				codiceGovWayStatusAtteso = codiceGovWayStatusAtteso + code;
				Reporter.log("Controllo govwayStatus presente["+govwayStatus+"] atteso["+codiceGovWayStatusAtteso+"]");
				Assert.assertTrue(govwayStatus!=null && govwayStatus.equals(codiceGovWayStatusAtteso));
			}
			
		}catch(Exception e){
			if(xml!=null){
				System.out.println("----------------------");
				System.out.println(xml);
				System.out.println(e.getMessage());
			}
			throw e;
		}
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+codiceErrore.getCodice();
	}
	public static String toString(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_PREFIX_CODE+codiceErrore.getCodice();
	}
}


