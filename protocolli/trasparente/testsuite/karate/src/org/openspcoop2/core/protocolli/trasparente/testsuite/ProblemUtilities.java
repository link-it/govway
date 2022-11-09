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


package org.openspcoop2.core.protocolli.trasparente.testsuite;

import static org.junit.Assert.assertTrue;

import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.utils.rest.problem.ProblemConstants;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Contiene utility per i test effettuati.
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemUtilities {

	public static boolean existsProblem(Element xml, Logger log){
		// XML Applicativo
		assertTrue(xml!=null);
		assertTrue(xml.getChildNodes()!=null);
		assertTrue(xml.getChildNodes().getLength()>0);
		log.debug("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			log.debug("Detail["+detail.getLocalName()+"]");
			if("problem".equals(detail.getLocalName())){
				XmlDeserializer deserializer = new XmlDeserializer();
				return deserializer.isProblemRFC7807(detail);
			}
		}
		
		return false;
	}
	
	public static Node getProblem(Element xml, Logger log){
		// XML Applicativo
		assertTrue(xml!=null);
		assertTrue(xml.getChildNodes()!=null);
		assertTrue(xml.getChildNodes().getLength()>0);
		log.debug("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			log.debug("Detail["+detail.getLocalName()+"]");
			if("problem".equals(detail.getLocalName())){
				XmlDeserializer deserializer = new XmlDeserializer();
				// Details, contiene l'xml che forma ill'errore applicativo
				log.debug("Detail["+detail.getLocalName()+"] isProblemRFC7807["+deserializer.isProblemRFC7807(detail)+"]");
				if(deserializer.isProblemRFC7807(detail)){
					return detail;
				}
			}
		}
		
		return null;
	}
	
	public static void verificaProblem(Node problemNode,
			int exceptionCodeExpected, String integrationFunctionError, String descrizione, boolean checkDescrizioneTramiteMatchEsatto, 
			Logger log) throws Exception{

		String xml = null;
		
		try{

			assertTrue(problemNode!=null);
			xml = MessageXMLUtils.DEFAULT.toString(problemNode);
			log.debug("Namespace Problem ("+problemNode.getNamespaceURI()+"): "+xml);
			assertTrue(ProblemConstants.XML_PROBLEM_DETAILS_RFC_7807_NAMESPACE.equals(problemNode.getNamespaceURI()));
			
			XmlDeserializer deserializer = new XmlDeserializer();
			ProblemRFC7807 problemRFC7807 = deserializer.fromNode(problemNode);
			
			int httpStatus = exceptionCodeExpected;
			log.debug("Controllo stato presente["+problemRFC7807.getStatus()+"] atteso["+httpStatus+"]");
			assertTrue(problemRFC7807.getStatus()!=null && problemRFC7807.getStatus().intValue() == httpStatus);
			
			//String typeAtteso = "https://httpstatuses.com/"+httpStatus;
			String typeAtteso = "https://govway.org/handling-errors/"+exceptionCodeExpected+"/"+integrationFunctionError+".html";
			log.debug("Controllo type presente["+problemRFC7807.getType()+"] atteso["+typeAtteso+"]");
			assertTrue(problemRFC7807.getType()!=null && problemRFC7807.getType().equals(typeAtteso));
			
			//String titleAtteso = HttpUtilities.getHttpReason(httpStatus);
			String titleAtteso = integrationFunctionError;
			log.debug("Controllo title presente["+problemRFC7807.getTitle()+"] atteso["+titleAtteso+"]");
			assertTrue(problemRFC7807.getTitle()!=null && problemRFC7807.getTitle().equals(titleAtteso));
						
			String value = problemRFC7807.getDetail();
			String atteso = descrizione;
			log.debug("Controllo description presente["+value+"] atteso["+atteso+"] checkDescrizioneTramiteMatchEsatto["+checkDescrizioneTramiteMatchEsatto+"]");
			if(checkDescrizioneTramiteMatchEsatto) {
				assertTrue(value.equals(atteso));
			}
			else {
				assertTrue(value.contains(atteso));
			}
			
			log.debug("Controllo esistenza govway id");
			String govwayTransactionId = null;
			if(problemRFC7807.getCustom()!=null) {
				govwayTransactionId = (String) problemRFC7807.getCustom().get(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID());
			}
			assertTrue(govwayTransactionId!=null);
			
		}catch(Exception e){
			if(xml!=null){
				System.out.println("----------------------");
				System.out.println(xml);
				System.out.println(e.getMessage());
			}
			throw e;
		}
	}
	
}
