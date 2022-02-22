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

package org.openspcoop2.message.soap.reader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.openspcoop2.message.AbstractBaseOpenSPCoop2MessageDynamicContent;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.AbstractOpenSPCoop2Message_soap_impl;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.XMLDiff;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TestOptimizedHeader
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestOptimizedHeader {

	public static void main(String[] args) throws Exception {
	
		Integer testDimensioneKb = null;
		
		if(args!=null && args.length>0) {
			testHeap = Boolean.valueOf(args[0]);
			if(testHeap) {
				runtime = Runtime.getRuntime();
			}
			
			if(args.length>1) {
				maxIterations = Integer.valueOf(args[1]);
			}
			
			if(args.length>2) {
				testDimensioneKb = Integer.valueOf(args[2]);
			}
			
			if(args.length>3) {
				HEAP_MEMORY_SIZE = Long.valueOf(args[3]) * 1024l * 1024l; // 100 mega
			}
			else {
				HEAP_MEMORY_SIZE = 100l * 1024l * 1024l; // 100 mega
			}
		}
		else {
			HEAP_MEMORY_SIZE = 100l * 1024l * 1024l; // 100 mega
		}
		
		int buffer_dimensione_default = 10; // tutti i messaggi di test sono tarati su questa dimensione
		
		String contentTypeSoap11 = HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=UTF-8";
		String contentTypeSoap12 = HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=UTF-8";
		String _prefixContentTypeWithAttachments = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+";   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\"";
		String contentTypeSoap11WithAttachments = _prefixContentTypeWithAttachments+HttpConstants.CONTENT_TYPE_SOAP_1_1+"\"";
		String contentTypeSoap12WithAttachments = _prefixContentTypeWithAttachments+HttpConstants.CONTENT_TYPE_SOAP_1_2+"\"";
		
		
		if(testDimensioneKb==null || testDimensioneKb<0) {
		
			System.out.println("\n\n*** TEST SOAP 11 da 5K (buffer 1k) ***");
			test("request5K_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SOAP 12 da 5K (buffer 1k) ***");
			test("request5K_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			System.out.println("\n\n*** TEST SOAP 11 da 5K (unica riga) (buffer 1k) ***");
			test("request5K_unicaRiga_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SOAP 12 da 5K (unica riga) (buffer 1k) ***");
			test("request5K_unicaRiga_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K (buffer 1k) ***");
			test("requestHeader5K_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K (buffer 1k) ***");
			test("requestHeader5K_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5K_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5K_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K (unica riga) (buffer 1k) ***");
			test("requestHeader5K_unicaRiga_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K (unica riga) (buffer 1k) ***");
			test("requestHeader5K_unicaRiga_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5K_unicaRiga_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5K_unicaRiga_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (buffer 1k) ***");
			test("requestHeader5KBody5K_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (buffer 1k) ***");
			test("requestHeader5KBody5K_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5KBody5K_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5KBody5K_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (unica riga) (buffer 1k) ***");
			test("requestHeader5KBody5K_unicaRiga_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (unica riga) (buffer 1k) ***");
			test("requestHeader5KBody5K_unicaRiga_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, false);
			
			System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5KBody5K_unicaRiga_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeader5KBody5K_unicaRiga_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			System.out.println("\n\n*** TEST SOAP 11 con prefisso SOAP vuoto (buffer 1k) ***");
			test("requestSoapPrefixEmpty_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con prefisso SOAP vuoto (buffer 1k) ***");
			test("requestSoapPrefixEmpty_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con prefisso SOAP vuoto e Header (buffer 1k) ***");
			test("requestSoapPrefixEmptyWithHeader_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con prefisso SOAP vuoto e Header (buffer 1k) ***");
			test("requestSoapPrefixEmptyWithHeader_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			
			System.out.println("\n\n*** TEST SOAP 11 con prefissi SOAP vari con Header (buffer "+buffer_dimensione_default+"k) ***");
			test("requestMixedPrefix_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con prefissi SOAP vari con Header (buffer "+buffer_dimensione_default+"k) ***");
			test("requestMixedPrefix_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			System.out.println("\n\n*** TEST SOAP 11 tutto su una riga con soap body vuoto (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSuUnaRigaSoapBodyVuoto_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 tutto su una riga con soap body vuoto (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSuUnaRigaSoapBodyVuoto_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestEmpty1_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestEmpty1_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestEmpty2_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestEmpty2_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestEmpty3_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestEmpty3_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			System.out.println("\n\n*** TEST SOAP 11 con request short senza header (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShort_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con request short senza header (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShort_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShortHeaderEmpty1_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShortHeaderEmpty1_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShortHeaderEmpty2_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShortHeaderEmpty2_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShortHeaderEmpty3_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestShortHeaderEmpty3_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			System.out.println("\n\n*** TEST SOAP 11 soapFault (buffer "+buffer_dimensione_default+"k) ***");
			test("soapFault_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 soapFault (buffer "+buffer_dimensione_default+"k) ***");
			test("soapFault_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 soapFault con soap prefix empty (buffer "+buffer_dimensione_default+"k) ***");
			test("soapFaultSoapPrefixEmpty_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 soapFault con soap prefix empty (buffer "+buffer_dimensione_default+"k) ***");
			test("soapFaultSoapPrefixEmpty_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 soapFault con stack trace (buffer "+buffer_dimensione_default+"k) ***");
			test("soapFaultLong_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 soapFault con stack trace (buffer "+buffer_dimensione_default+"k) ***");
			test("soapFaultLong_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 struttura soapFault simile (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSimileSoapFault_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 struttura soapFault simile (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSimileSoapFault_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 struttura soapFault simile (empty prefix) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSimileSoapFault2_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 struttura soapFault simile (empty prefix) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSimileSoapFault2_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			System.out.println("\n\n*** TEST SOAP 11 CDATA (buffer "+buffer_dimensione_default+"k) ***");
			test("requestAllCDATAHeaderBodyEmpty_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 CDATA (buffer "+buffer_dimensione_default+"k) ***");
			test("requestAllCDATAHeaderBodyEmpty_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con prefissi SOAP vari con Header e CDATA (buffer "+buffer_dimensione_default+"k) ***");
			test("requestMixedPrefixAndCDATA_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con prefissi SOAP vari con Header e CDATA (buffer "+buffer_dimensione_default+"k) ***");
			test("requestMixedPrefixAndCDATA_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			System.out.println("\n\n*** TEST SOAP 11 XmlEntity (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyXmlEntity_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 XmlEntity (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyXmlEntity_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 XmlEntity OpenOnly (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyXmlEntityOpenOnly_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 XmlEntity OpenOnly (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyXmlEntityOpenOnly_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 XmlEntity CloseOnly (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestHeaderBodyXmlEntityCloseOnly_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Element type \"redro\" must be followed by either attribute specifications, \">\" or \"/>\".")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			
			System.out.println("\n\n*** TEST SOAP 12 XmlEntity CloseOnly (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyXmlEntityCloseOnly_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			
			System.out.println("\n\n*** TEST SOAP 11 con attachments (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSOAPMultipartRelatedMIME_soap11.bin", contentTypeSoap11WithAttachments, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con attachments (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSOAPMultipartRelatedMIME_soap12.bin", contentTypeSoap12WithAttachments, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con attachments (pdf) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSOAPMultipartRelatedMIMEpdf_soap11.bin", contentTypeSoap11WithAttachments, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con attachments (pdf) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSOAPMultipartRelatedMIMEpdf_soap12.bin", contentTypeSoap12WithAttachments, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 con attachments (bodyEmpty) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSOAPMultipartRelatedMIMEbodyEmpty_soap11.bin", contentTypeSoap11WithAttachments, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con attachments (bodyEmpty) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSOAPMultipartRelatedMIMEbodyEmpty_soap12.bin", contentTypeSoap12WithAttachments, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			System.out.println("\n\n*** TEST SOAP 11 '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestACapo_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestACapo_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
	
			System.out.println("\n\n*** TEST SOAP 11 '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestTab_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestTab_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 rootElement '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestRootElementACapo_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true); 
			
			System.out.println("\n\n*** TEST SOAP 12 rootElement '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestRootElementACapo_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 rootElement '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestRootElementTab_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 rootElement '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestRootElementTab_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 header '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderACapo_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 header '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderACapo_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 header '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderTab_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 header '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderTab_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 fault '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSoapFaultACapo_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 fault '\\n' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSoapFaultACapo_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 fault '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSoapFaultTab_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 fault '\\t' (buffer "+buffer_dimensione_default+"k) ***");
			test("requestSoapFaultTab_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			// Questo tipo di errore avviene solamente se il carattere > e' iniziale prima di qualsiasi <
			// I test con attachments servono appunto a non gestire questo aspetto poiche' poiche' il carattere > potrebbe essere nel mime
			// se cmq si attiva la validazione, verrà individuato l'errore
			System.out.println("\n\n*** TEST SOAP 11 elemento > senza < (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestMalformed_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Invalid content; found premature '>' character")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			System.out.println("\n\n*** TEST SOAP 12 elemento > senza < (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestMalformed_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Invalid content; found premature '>' character")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			System.out.println("\n\n*** TEST SOAP 11 con attachments (carattere > prima di <) (buffer "+buffer_dimensione_default+"k) ***");
			test("contentIdMalformedSOAPMultipartRelatedMIME_soap11.bin", contentTypeSoap11WithAttachments, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 con attachments (carattere > prima di <) (buffer "+buffer_dimensione_default+"k) ***");
			test("contentIdMalformedSOAPMultipartRelatedMIME_soap12.bin", contentTypeSoap12WithAttachments, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			
			System.out.println("\n\n*** TEST SOAP 11 elemento body non corretto (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestBodyMalformed_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Element type \"CONTENUTO_ERRATO\" must be followed by either attribute specifications, \">\" or \"/>\".")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			
			System.out.println("\n\n*** TEST SOAP 12 elemento body non corretto (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestBodyMalformed_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Element type \"CONTENUTO_ERRATO\" must be followed by either attribute specifications, \">\" or \"/>\".")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			
			System.out.println("\n\n*** TEST SOAP 11 elemento body corretto (test2: chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestBodyMalformed2_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);

			System.out.println("\n\n*** TEST SOAP 12 elemento body non corretto (test2: chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestBodyMalformed2_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);

			System.out.println("\n\n*** TEST SOAP 11 elemento body non corretto (test3: /chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestBodyMalformed3_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 12 elemento body non corretto (test3: /chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
			test("requestBodyMalformed3_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP 11 elemento header non corretto (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestHeaderMalformed_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Element type \"CONTENUTO_ERRATO\" must be followed by either attribute specifications, \">\" or \"/>\".")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			
			System.out.println("\n\n*** TEST SOAP 12 elemento header non corretto (buffer "+buffer_dimensione_default+"k) ***");
			try {
				test("requestHeaderMalformed_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
				throw new Exception("Atteso errore");
			}catch(Throwable e) {
				if(e.getMessage().contains("Element type \"CONTENUTO_ERRATO\" must be followed by either attribute specifications, \">\" or \"/>\".")) {
					System.out.println("Rilevato errore atteso: "+e.getMessage());
				}
				else {
					throw e;
				}
			}
			
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con BodyChildName (buffer 1k) ***");
			test("requestFirstChildNameBody_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con BodyChildName (buffer 1k) ***");
			test("requestFirstChildNameBody_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con BodyChildNameExact (buffer 1k) ***");
			test("requestFirstChildNameExactBody_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con BodyChildNameExact (buffer 1k) ***");
			test("requestFirstChildNameExactBody_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con HeaderChildName (buffer 1k) ***");
			test("requestFirstChildNameHeader_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con HeaderChildName (buffer 1k) ***");
			test("requestFirstChildNameHeader_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con HeaderChildNameExact (buffer 1k) ***");
			test("requestFirstChildNameExactHeader_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con HeaderChildNameExact (buffer 1k) ***");
			test("requestFirstChildNameExactHeader_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
	
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con XmlDeclaration a capo (buffer 1k) ***");
			test("requestXmlDeclarationACapo_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con XmlDeclaration a capo (buffer 1k) ***");
			test("requestXmlDeclarationACapo_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			
			
			System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP12 , e messaggio SOAP 11 (buffer 1k) ***");
			test("requestSoap12Commentata_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP11 , e messaggio SOAP 12 (buffer 1k) ***");
			test("requestSoap11Commentata_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
			
			System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP12 (ACapo) , e messaggio SOAP 11 (buffer 1k) ***");
			test("requestSoap12CommentataACapo_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, 1, true);
			
			System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP11 (ACapo) , e messaggio SOAP 12 (buffer 1k) ***");
			test("requestSoap11CommentataACapo_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, 1, true);
		
			
			
			System.out.println("\n\n*** TEST SOAP11 con vari commenti (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyVariCommenti_soap11.xml", contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
			
			System.out.println("\n\n*** TEST SOAP12 con vari commenti (buffer "+buffer_dimensione_default+"k) ***");
			test("requestHeaderBodyVariCommenti_soap12.xml", contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
			
			
			System.out.println("\n\nTestsuite completata con successo");
			
		}
		
		
		if(testDimensioneKb==null || testDimensioneKb>0) {
			
			System.out.println("\n\nTestsuite per dimensioni maggiori ...");
			
			List<Integer> dimensioni = new ArrayList<Integer>();
			if(testDimensioneKb!=null && testDimensioneKb>0) {
				dimensioni.add(testDimensioneKb);
			}
			else {
				dimensioni.add(50);
				dimensioni.add(500);
				// su jenkins va in out of memory per via delle risorse limitate
				//dimensioni.add(5000);
			}
			
			for (Integer dimensione : dimensioni) {
				
				System.out.println("\n\n*** TEST SOAP 11 da "+dimensione+"K (buffer 1k) ***");
				test("request5K_soap11.xml", dimensione, contentTypeSoap11, MessageType.SOAP_11, 1, true);
				
				System.out.println("\n\n*** TEST SOAP 12 da "+dimensione+"K (buffer 1k) ***");
				test("request5K_soap12.xml", dimensione, contentTypeSoap12, MessageType.SOAP_12, 1, true);
				
				System.out.println("\n\n*** TEST SOAP 11 da "+dimensione+"K (unica riga) (buffer 1k) ***");
				test("request5K_unicaRiga_soap11.xml", dimensione, contentTypeSoap11, MessageType.SOAP_11, 1, true);
				
				System.out.println("\n\n*** TEST SOAP 12 da "+dimensione+"K (unica riga) (buffer 1k) ***");
				test("request5K_unicaRiga_soap12.xml", dimensione, contentTypeSoap12, MessageType.SOAP_12, 1, true);
				
				
				System.out.println("\n\n*** TEST SOAP 11 con Header 5K da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5K_soap11.xml", dimensione, contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				
				System.out.println("\n\n*** TEST SOAP 12 con Header 5K da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5K_soap12.xml", dimensione, contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
								
				System.out.println("\n\n*** TEST SOAP 11 con Header 5K (unica riga) da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5K_unicaRiga_soap11.xml", dimensione, contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				
				System.out.println("\n\n*** TEST SOAP 12 con Header 5K (unica riga) da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5K_unicaRiga_soap12.xml", dimensione, contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
				
								
				System.out.println("\n\n*** TEST SOAP 11 con Header 5K da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5KBody5K_soap11.xml", dimensione, contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				
				System.out.println("\n\n*** TEST SOAP 12 con Header 5K da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5KBody5K_soap12.xml", dimensione, contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
								
				System.out.println("\n\n*** TEST SOAP 11 con Header 5K da "+dimensione+"K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5KBody5K_unicaRiga_soap11.xml", dimensione, contentTypeSoap11, MessageType.SOAP_11, buffer_dimensione_default, true);
				
				System.out.println("\n\n*** TEST SOAP 12 con Header 5K da "+dimensione+"K (buffer "+buffer_dimensione_default+"k) ***");
				test("requestHeader5KBody5K_unicaRiga_soap12.xml", dimensione, contentTypeSoap12, MessageType.SOAP_12, buffer_dimensione_default, true);
				
			}
			
			System.out.println("\n\nTestsuite per dimensioni maggiori completata con successo");
			
		}
	}
	
	private static long HEAP_MEMORY_SIZE = -1;
	private static boolean print_message = false;
		
	private static int maxIterations = 100;
	
	private static String ID_SOAP_ENVELOPE_MULTIPART = "<56D2051AED8F9598BB61721D8C95BA6F>";
	
	private static void test(String fileName, String contentType, MessageType messageType, int bufferThresholdKb, boolean attesoComeOttimizzabile) throws Exception {
		test(fileName, -1, contentType, messageType, bufferThresholdKb, attesoComeOttimizzabile);
	}
	private static void test(String fileName, int kbBody, String contentType, MessageType messageType, int bufferThresholdKb, boolean attesoComeOttimizzabile) throws Exception {
		
		long rapporto_min_saaj = 30;
		long rapporto_max_saaj = 36;
		
		long rapporto_min_dom = 5;
		long rapporto_max_dom = 15;
		
		long rapporto_min = 1;
		long rapporto_stream_reader = 3;
		
		long incremento_minimo_memoria = 1;
		
		byte [] resource = Utilities.getAsByteArray(TestReader.class.getResourceAsStream("/org/openspcoop2/message/soap/reader/"+fileName));
		
		OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		if(kbBody>0) {
			System.out.println("Build new message ...");
			long bytes = ((long)kbBody) * 1024l;
			while(resource.length<bytes) {
				
				XMLUtils xmlUtils = XMLUtils.getInstance(factory);
				
				Element envelope = xmlUtils.newElement(resource);
				NodeList l = envelope.getElementsByTagNameNS(envelope.getNamespaceURI(), "Body");
				Node body = l.item(0);
				
				Node node = XMLUtils.getInstance(factory).getFirstNotEmptyChildNode(body);

				List<Node> children = xmlUtils.getNotEmptyChildNodes(node, false);
				
				for (int j = 0; j < children.size(); j++) {
					//for (int i = 0; i < 5; i++) {
					Node d = children.get(j).cloneNode(true);
					node.appendChild(d);
					//}
				}
				
				resource = xmlUtils.toByteArray(envelope);
					
			}
			System.out.println("Builded: "+Utilities.convertBytesToFormatString(resource.length, true, " "));
		}
		
		String resourceAsString = new String(resource);
		long initial = print(0, "RisorsaRaw", resource, null, null, null);
		
		String defaultDomPrefix = MessageType.SOAP_11.equals(messageType) ? "SOAP-ENV" : "env";
		
		XMLDiffOptions xmlDiffOptions = new XMLDiffOptions();
		xmlDiffOptions.setIgnoreComments(true);
		xmlDiffOptions.setIgnoreDiffBetweenTextAndCDATA(true);
		xmlDiffOptions.setIgnoreWhitespace(true);
		xmlDiffOptions.setNormalize(false);
		XMLDiff xmlDiffEngine = new XMLDiff();
		xmlDiffEngine.initialize(XMLDiffImplType.XML_UNIT, xmlDiffOptions);
		
		TransportRequestContext req = new TransportRequestContext();
		Map<String, List<String>> parametriTrasporto = new HashMap<String, List<String>>();
		List<String> list = new ArrayList<String>();
		if(fileName.startsWith("requestSOAPMultipartRelatedMIMEpdf")) {
			String ctMultipartRefreshed = null;
			String ct = MessageType.SOAP_11.equals(messageType) ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			ctMultipartRefreshed = ContentTypeUtilities.buildMultipartContentType(resource, ct, ID_SOAP_ENVELOPE_MULTIPART);
			list.add(ctMultipartRefreshed);
		}
		else {
			list.add(contentType);
		}
		parametriTrasporto.put(HttpConstants.CONTENT_TYPE, list);
		req.setHeaders(parametriTrasporto);
		
		Element envelope = null;
		if(!contentType.startsWith(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED) && !fileName.startsWith("requestMalformed_soap")) {
			envelope = XMLUtils.getInstance(factory).newElement(resource);
		}
		
		if(fileName.startsWith("requestMixedPrefixAndCDATA")) {
			rapporto_max_dom = rapporto_max_dom*3;
			rapporto_min = 3;
			rapporto_min_saaj = 15;
		}
		else if(fileName.startsWith("requestSoapPrefixEmpty") || fileName.startsWith("requestMixedPrefix")) {
			rapporto_max_dom = rapporto_max_dom*4; 
			rapporto_min = 3;
			rapporto_min_saaj = 28;
			rapporto_max_saaj = 46;
		}
		else if(fileName.startsWith("requestEmpty") || fileName.startsWith("requestShort")) {
			// essendo vuoto il messaggio, la sovrastruttura amplifica notevolmente come rapporto
			rapporto_max_dom = rapporto_max_dom*35; 
			rapporto_min = 6;
			rapporto_stream_reader = 45;
			rapporto_max_saaj = 70;
			incremento_minimo_memoria = 10;
		}
		else if (fileName.startsWith("soapFault_soap") || fileName.startsWith("soapFaultSoapPrefixEmpty_") || fileName.startsWith("requestSimileSoapFault")) {
			// essendo piccolo il messaggio, la sovrastruttura amplifica notevolmente come rapporto
			rapporto_max_dom = rapporto_max_dom*6; 
			incremento_minimo_memoria = 10;
			rapporto_stream_reader = 10;
			rapporto_min_saaj = 20;
		}
		else if (fileName.startsWith("soapFaultLong") || fileName.startsWith("requestAllCDATAHeaderBodyEmpty") ) {
			// come soap fault è praticamente vuoto, è solo testo.
			rapporto_max_dom = rapporto_max_dom*6; 
			rapporto_min_saaj = 2;
		}
		else if (fileName.startsWith("requestHeaderBodyXmlEntity")) {
			// è molto testo come xml entity
			rapporto_max_dom = rapporto_max_dom*6;
			rapporto_min_saaj = 3;
		}
		else if(fileName.startsWith("requestSOAPMultipartRelatedMIME") || fileName.startsWith("contentIdMalformedSOAPMultipartRelatedMIME")){
			rapporto_stream_reader = 10;
			rapporto_min_saaj = 20;
		}
		else if(fileName.startsWith("requestACapo_")) {
			// sono messaggi piccoli, pieni di \n o \t
			rapporto_max_dom = rapporto_max_dom*6; 
			rapporto_stream_reader = 20;
			rapporto_min_saaj = 20;
			rapporto_max_saaj = 50;
		}
		else if(fileName.contains("FirstChild")) {
			// essendo piccolo il messaggio, la sovrastruttura amplifica notevolmente come rapporto
			rapporto_max_dom = rapporto_max_dom*30; 
			rapporto_min = 15;
			rapporto_min_saaj = 20;
			rapporto_max_saaj = 55;
		}
		else if(fileName.contains("requestXmlDeclarationACapo_")) {
			// essendo piccolo il messaggio, la sovrastruttura amplifica notevolmente come rapporto
			rapporto_max_dom = rapporto_max_dom*30; 
			rapporto_min = 15;
			rapporto_min_saaj = 20;
			rapporto_max_saaj = 45;
		}
		else if(fileName.contains("requestSoap") && fileName.contains("Commentata")){
			// essendo piccolo il messaggio, la sovrastruttura amplifica notevolmente come rapporto
			rapporto_max_dom = rapporto_max_dom*20; 
			rapporto_min = 10;
			rapporto_min_saaj = 20;
			rapporto_max_saaj = 45;
		}
		else if(fileName.contains("requestHeaderBodyVariCommenti")) {
			rapporto_max_dom = rapporto_max_dom * 4;
			rapporto_stream_reader = 15;
			rapporto_min_saaj = 15;
		}
		else if(fileName.startsWith("requestTab")) {
			rapporto_max_dom = rapporto_max_dom * 6;
			rapporto_stream_reader = 15;
			rapporto_min_saaj = 25;
			rapporto_max_saaj = 45;
		}
		else if(fileName.startsWith("requestRootElementACapo") || fileName.startsWith("requestRootElementTab") ||
				fileName.startsWith("requestHeaderACapo") || fileName.startsWith("requestHeaderTab")) {
			rapporto_max_dom = rapporto_max_dom * 8;
			rapporto_stream_reader = 15;
			rapporto_min_saaj = 25;
			rapporto_max_saaj = 45;
		}
		else if(fileName.startsWith("requestSoapFaultACapo") || fileName.startsWith("requestSoapFaultTab")) {
			rapporto_max_dom = rapporto_max_dom * 8;
			rapporto_stream_reader = 15;
			rapporto_min_saaj = 20;
			rapporto_max_saaj = 45;
		}
		
		if(kbBody>50) {
			rapporto_min_dom = 3;
		}
		
		if(envelope!=null) {
			print(0, "DOM", envelope, initial, rapporto_min_dom, rapporto_max_dom);
		}
						
		
		List<Object> contenitor = new ArrayList<>();
		
		boolean wsa = "requestSoapPrefixEmptyWithHeader_soap11.xml".equals(fileName) || "requestSoapPrefixEmptyWithHeader_soap12.xml".equals(fileName);
		boolean mixed = "requestMixedPrefix_soap11.xml".equals(fileName) || "requestMixedPrefix_soap12.xml".equals(fileName);
		boolean shortHeader = fileName.startsWith("requestShortHeaderEmpty");
		boolean cdata = fileName.startsWith("requestAllCDATA");
		
		
		System.out.println("== TRASPARENTE ==");
		
		long rapporto = 1;
		//System.out.println("HEAP: "+HEAP_MEMORY_SIZE);
		//System.out.println("initial: "+initial);
		//System.out.println("rapporto: "+rapporto);
		long iterazioniPossibili = (HEAP_MEMORY_SIZE / initial) / rapporto;
		//System.out.println("iterazioniPossibili: "+iterazioniPossibili);
		if(!testHeap) {
			iterazioniPossibili = 1;
		}
		else {
			if(iterazioniPossibili>maxIterations) {
				iterazioniPossibili = maxIterations;
			}
		}
		
		for (int i = 0; i < iterazioniPossibili; i++) {
			
			printMemory(i, iterazioniPossibili);
			
			OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					contentType, 
					new ByteArrayInputStream(resource), 
					bufferThresholdKb);
			InputStream is = null;
			try {
				streamReader.read();
				streamReader.checkException();
			}finally {
				// anche in caso di eccezione devo cmq aggiornare is
				is = streamReader.getBufferedInputStream();
			}
			print(i, "StreamReader", streamReader, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, is, null, streamReader);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			print(i, "OpenSPCoop2Message-init", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			if(!streamReader.isSoapHeaderOptimizable()) {
				if(attesoComeOttimizzabile) {
					throw new Exception("Atteso come ottimizzabile");
				}
			}
			//OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
						
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			StringBuilder sbDebug = new StringBuilder();
			((AbstractBaseOpenSPCoop2MessageDynamicContent<?>)msg).writeTo(bout, true, false, null, sbDebug);
			if(!Costanti.WRITE_MODE_SERIALIZE_STREAM.equals(sbDebug.toString())) {
				throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_STREAM+"'; riscontrata: "+sbDebug.toString());
			}
			bout.flush();
			bout.close();
			
			if(i==0 && print_message) {
				System.out.println("WRITE: "+bout.toString());
			}
			
			long dimensione = print(i, "OpenSPCoop2Message-after-write", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			if(i==0) {
				saveDimensioneOggetto(dimensione);
			}
			contenitor.add(msg); // per GC
			
			// verifico consistenza messaggio scritto
			verificaConsistenza(contentType, factory, bout, messageType, fileName);
			
			// verifico uguaglianza
			if(!resourceAsString.equals(bout.toString())) {
				throw new Exception("Contenuto serializzato differente: \nATTESO:\n["+resourceAsString+"]\n GENERATO:\n["+bout.toString()+"]");
			}
			
			print(i, "finalResourceRaw", bout.toByteArray(), initial, 1l, rapporto_min);
		}
		
		checkMemoryError();
		
		
		

		
		
		System.out.println("== HEADER ==");
		
		rapporto = 2;
		iterazioniPossibili = (HEAP_MEMORY_SIZE / initial) / rapporto;
		if(!testHeap) {
			iterazioniPossibili = 1;
		}
		else {
			if(iterazioniPossibili>maxIterations) {
				iterazioniPossibili = maxIterations;
			}
		}
		
		for (int i = 0; i < iterazioniPossibili; i++) {
			
			printMemory(i, iterazioniPossibili);
			
			OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					contentType, 
					new ByteArrayInputStream(resource), 
					bufferThresholdKb);
			InputStream is = null;
			try {
				streamReader.read();
				streamReader.checkException();
			}finally {
				// anche in caso di eccezione devo cmq aggiornare is
				is = streamReader.getBufferedInputStream();
			}
			print(i, "StreamReader", streamReader, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, is, null, streamReader);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			print(i, "OpenSPCoop2Message-init", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			boolean isOptimizable = streamReader.isSoapHeaderOptimizable();
			if(!isOptimizable) {
				if(attesoComeOttimizzabile) {
					throw new Exception("Atteso come ottimizzabile");
				}
			}			
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			SOAPHeader hdr = soapMsg.getSOAPHeader();
			if(hdr==null) {
				hdr = soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			SOAPElement soapElement = hdr.addChildElement("PROVA","test","http://prova");
			
			if(isOptimizable) {
				if(fileName.contains("Header5K") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
					print(i, "OpenSPCoop2Message-after-header", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 2l : rapporto_max_saaj); // viene costruito saaj in reader
				}
				else if(fileName.startsWith("requestSoapPrefixEmptyWithHeader") || fileName.startsWith("requestMixedPrefix")) {
					print(i, "OpenSPCoop2Message-after-header", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 4l : rapporto_min * 4l); // viene costruito saaj in reader
				}
				else {
					print(i, "OpenSPCoop2Message-after-header", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 2l : rapporto_stream_reader+1);
				}
			}
			else {
				print(i, "OpenSPCoop2Message-after-header", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			}
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			StringBuilder sbDebug = new StringBuilder();
			((AbstractBaseOpenSPCoop2MessageDynamicContent<?>)msg).writeTo(bout, true, false, null, sbDebug);
			if(isOptimizable) {
				if(!Costanti.WRITE_MODE_SERIALIZE_STREAM_WITH_HEADER.equals(sbDebug.toString())) {
					throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_STREAM_WITH_HEADER+"'; riscontrata: "+sbDebug.toString());
				}
			}
			else {
				if(!Costanti.WRITE_MODE_SERIALIZE_CONTENT.equals(sbDebug.toString())) {
					throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_CONTENT+"'; riscontrata: "+sbDebug.toString());
				}
			}
			bout.flush();
			bout.close();
			
			if(i==0 && print_message) {
				System.out.println("WRITE AFTER HEADER: "+bout.toString());
			}
			
			long dimensione = -1;
			if(isOptimizable) {
				// dopo la write la memoria occupata dal saaj object presente nel reader viene rilasciata
				dimensione = print(i, "OpenSPCoop2Message-after-header-write", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 2l : rapporto_stream_reader+1);
			}
			else {
				dimensione = print(i, "OpenSPCoop2Message-after-header-write", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			}
			if(i==0) {
				if(incremento_minimo_memoria>1) {
					dimensione = dimensione * incremento_minimo_memoria;
				}
				saveDimensioneOggetto(dimensione);
			}
			contenitor.add(msg); // per GC
			
			// verifico consistenza messaggio scritto
			verificaConsistenza(contentType, factory, bout, messageType,fileName);
			
			// verifico uguaglianza
			String newHeader = OpenSPCoop2MessageFactory.getAsString(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), soapElement, true);
			String newString = null; 
			if(isOptimizable) {
				if(fileName.startsWith("requestHeaderBodyXmlEntityOpenOnly")) {
					newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
					newString = newString.replace("<soapenv:Header>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">");
					newString = newString.replaceFirst("<xsd:skcotSyub>", "<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">");
					int indexOf = newString.indexOf("<soapenv:Body>");
					
					List<String> listSrc = new ArrayList<String>();
					listSrc.add("&lt;redro>&lt;lobmys>IBM&lt;/lobmys>&lt;DIreyub>asankha&lt;/DIreyub>&lt;ecirp>140.34&lt;/ecirp>&lt;emulov>2000&lt;/emulov>&lt;/redro>");
					listSrc.add("&lt;redro>&lt;lobmys>MSFT&lt;/lobmys>&lt;DIreyub>ruwan&lt;/DIreyub>&lt;ecirp>23.56&lt;/ecirp>&lt;emulov>8030&lt;/emulov>&lt;/redro>");
					listSrc.add("&lt;redro>&lt;lobmys>SUN&lt;/lobmys>&lt;DIreyub>indika&lt;/DIreyub>&lt;ecirp>14.56&lt;/ecirp>&lt;emulov>500&lt;/emulov>&lt;/redro>");
					listSrc.add("&lt;redro>&lt;lobmys>GOOG&lt;/lobmys>&lt;DIreyub>chathura&lt;/DIreyub>&lt;ecirp>60.24&lt;/ecirp>&lt;emulov>40000&lt;/emulov>&lt;/redro>");
					List<String> listDest = new ArrayList<String>();
					listDest.add("&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;");
					listDest.add("&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;");
					listDest.add("&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;");
					listDest.add("&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;");
					for (int j = 0; j < listSrc.size(); j++) {
						String src = listSrc.get(j);
						String dest = listDest.get(j);
						int ind = newString.indexOf(src);
						indexOf = newString.indexOf("<soapenv:Body>"); // va ricalcolato
						
						//System.out.println("["+j+"] 0<ind["+ind+"]<body["+indexOf+"]");
						while(ind >0 && ind < indexOf) {
							newString = newString.replaceFirst(src, dest);
							ind = newString.indexOf(src);
							indexOf = newString.indexOf("<soapenv:Body>"); // va ricalcolato
							//System.out.println("-----["+j+"] 0<ind["+ind+"]<body["+indexOf+"]");
						}
					}
				}
				else if(fileName.startsWith("requestHeaderACapo")) {
					newString = resourceAsString.replace("</soapenv:Header\n"+
							">", newHeader+"</soapenv:Header>");				
					newString = newString.replace("<soapenv:Header\n"+
							"xmlns:xsd=\"http://services.samples/xsd\"\n"+
							"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
							"xmlns=\"AltroperTest\"   \n"+
							"   >",
							"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
				}
				else if(fileName.startsWith("requestHeaderTab_")) {
					newString = resourceAsString.replace("</soapenv:Header	>", newHeader+"</soapenv:Header>");
					newString = newString.replace("<soapenv:Header		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
							"	xmlns=\"AltroperTest\"	\n"+	
							">", 
							"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
						newString = newString.replaceFirst("<xsd:skcotSyub	>","<xsd:skcotSyub>");
						newString = newString.replaceFirst("<redro	>","<redro>");
				}
				else if(fileName.startsWith("requestFirstChildNameHeader") || fileName.startsWith("requestFirstChildNameExactHeader")) {
					newString = resourceAsString.replace("</soap:Header>", newHeader+"</soap:Header>");
					newString = newString.replace("<soap:Header>", "<soap:Header xmlns:soap=\""+streamReader.getNamespace()+"\">");
				}
				else if(fileName.contains("Header") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
					newString = resourceAsString.replace("<soapenv:Header>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">");
					if(shortHeader) {
						newString = newString.replace("<soapenv:Header/>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\"></soapenv:Header>");
					}
					if(!wsa && !shortHeader && !cdata) {
						newString = newString.replaceFirst("<xsd:skcotSyub>", "<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">");
					}
					newString = newString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
					newString = newString.replace("</Header>", newHeader+"</Header>");
					if(wsa) {
						newString = newString.replace("<Header xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">",
								"<Header xmlns=\""+streamReader.getNamespace()+"\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					}
				}
				else if(mixed) {
					newString = resourceAsString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
					newString = newString.replace("</Header>", newHeader+"</Header>");
					newString = newString.replace("<hdr:Header>",
							"<hdr:Header xmlns:hdr=\""+streamReader.getNamespace()+"\">");
					newString = newString.replace("<Header>",
							"<Header xmlns=\""+streamReader.getNamespace()+"\">");
					newString = newString.replace("<wsa:MessageID>","<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					newString = newString.replace("<wsa:ReplyTo>","<wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					newString = newString.replace("<wsa:To>","<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					newString = newString.replace("<wsa:Action>","<wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				}
				else if(fileName.startsWith("requestEmpty1")) {
					newString = resourceAsString.replace("<soapenv:Body/>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">"+newHeader+"</soapenv:Header><soapenv:Body/>");
				}
				else if(fileName.startsWith("requestEmpty2")) {
					newString = resourceAsString.replace("<Body>", "<Header xmlns=\""+streamReader.getNamespace()+"\">"+newHeader+"</Header><Body>");
				}
				else if(fileName.startsWith("soapFault_soap")) {
					newString = resourceAsString.replace("<"+defaultDomPrefix+":Body>", "<"+defaultDomPrefix+":Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
					newString = newString.replace("xmlns:soapenv","xmlns:"+defaultDomPrefix+"");
				}
				else if(fileName.startsWith("soapFaultLong_soap11")) {
					newString = resourceAsString.replace("<env:Header />\n    <env:Body>", "<env:Header xmlns:env=\""+streamReader.getNamespace()+"\">"+newHeader+"</env:Header><env:Body>");
				}
				else if(fileName.startsWith("soapFaultLong_soap12")) {
					newString = resourceAsString.replace("<env:Body>", "<env:Header xmlns:env=\""+streamReader.getNamespace()+"\">"+newHeader+"</env:Header><env:Body>");
				}
				else if(fileName.startsWith("requestSimileSoapFault")){
					newString = resourceAsString.replace("<"+defaultDomPrefix+":Body>", "<"+defaultDomPrefix+":Header xmlns:"+defaultDomPrefix+"=\""+streamReader.getNamespace()+"\">"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
				}
				else if(fileName.startsWith("requestMixedPrefixAndCDATA")){
					if(fileName.contains("soap11")) {
						newString = resourceAsString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
						newString = newString.replace("<hdr:Header>", "<hdr:Header xmlns:hdr=\""+streamReader.getNamespace()+"\">");
					}
					else {
						newString = resourceAsString.replace("</Header>", newHeader+"</Header>");
						newString = newString.replace("<Header>", "<Header xmlns=\""+streamReader.getNamespace()+"\">");
					}
					newString = newString.replace("<wsa:MessageID>","<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					newString = newString.replace("<wsa:ReplyTo>","<wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					newString = newString.replace("<wsa:To>","<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					newString = newString.replace("<wsa:Action>","<wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				}
				else if(fileName.startsWith("requestSOAPMultipartRelatedMIME") || fileName.startsWith("contentIdMalformedSOAPMultipartRelatedMIME")) {
					newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
					newString = newString.replace("<soapenv:Header ", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" ");
					newString = newString.replace("soapenv:mustUnderstand=\"0\" >","soapenv:mustUnderstand=\"0\">");
				}
				else if(fileName.startsWith("requestACapo")) {
					newString = resourceAsString.replace("</soapenv:Header\n"+
								">", newHeader+"</soapenv:Header>");
					newString = newString.replace("</soapenv:Header\n\n"+
							">", newHeader+"</soapenv:Header>");
					
					newString = newString.replace("<soapenv:Header\n"+
							"xmlns:xsd=\"http://services.samples/xsd\"\n"+
							"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
							"xmlns=\"AltroperTest\"   \n"+
							"   >",
							"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
					
					newString = newString.replace("<xsd:skcotSyub\n"+
								">","<xsd:skcotSyub>");
					newString = newString.replace("<xsd:skcotSyub\n"+
								"   >","<xsd:skcotSyub>");
					
					if(fileName.contains("11")) {
						newString = newString.replaceFirst("<redro\n"+
								">","<redro>");
					}
					
					newString = newString.replaceFirst("</xsd:skcotSyub\n"+
							">","</xsd:skcotSyub>");
					if(fileName.contains("12")) {
						newString = newString.replaceFirst("</xsd:skcotSyub\n\n"+
								">","</xsd:skcotSyub>");
					}
				}
				else if(fileName.startsWith("requestTab")) {
					newString = resourceAsString.replace("</soapenv:Header	>", newHeader+"</soapenv:Header>");
					newString = newString.replace("<soapenv:Header		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
						"	xmlns=\"AltroperTest\"	\n"+	
						">", 
						"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
					newString = newString.replaceFirst("<xsd:skcotSyub	>","<xsd:skcotSyub>");
					newString = newString.replaceFirst("<redro	>","<redro>");
				}
				else if(fileName.startsWith("requestFirstChildNameBody") || fileName.startsWith("requestFirstChildNameExactBody") || 
						fileName.startsWith("requestSoap12Commentata") || fileName.startsWith("requestSoap11Commentata")) {
					newString = resourceAsString.replace("<soap:Body>", "<soap:Header xmlns:soap=\""+streamReader.getNamespace()+"\">"+newHeader+"</soap:Header><soap:Body>");	
				}
				else if(fileName.startsWith("requestXmlDeclarationACapo")) {
					newString = resourceAsString.replace("</soap:Header>", newHeader+"</soap:Header>");
					newString = newString.replace("<soap:Header>", "<soap:Header xmlns:soap=\""+streamReader.getNamespace()+"\">");
				}
				else {
					newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">"+newHeader+"</soapenv:Header><soapenv:Body>");
					newString = newString.replace("<Body>", "<Header xmlns=\""+streamReader.getNamespace()+"\">"+newHeader+"</Header><Body>");
				}
			}
			else {
				if(fileName.contains("Header")) {
					newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				}
				else {
					newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
					newString = newString.replace("<Body>", "<Header>"+newHeader+"</Header><Body>");
				}
				newString = newString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n   ", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				newString = newString.replace("</soapenv:Envelope>\n", "</soapenv:Envelope>");
			}
			String check = null;
			if(newString!=null) {
				newString = normalizeReturnLine(newString);
				check = normalizeReturnLine(bout.toString());
			}
			if(newString!=null && !newString.equals(check)) {
				throw new Exception("Contenuto serializzato differente: \nATTESO:\n["+newString+"]\n GENERATO:\n["+check+"]");
			}
			
			print(i, "finalResourceRaw", bout.toByteArray(), initial, 1l, rapporto_min);
		}
		
		checkMemoryError();
		
		

		
		
		
		System.out.println("== BODY ==");
		
		rapporto = rapporto_max_saaj;
		iterazioniPossibili = (HEAP_MEMORY_SIZE / initial) / rapporto;
		if(!testHeap) {
			iterazioniPossibili = 1;
		}
		else {
			if(iterazioniPossibili>maxIterations) {
				iterazioniPossibili = maxIterations;
			}
		}
		
		contenitor.clear();
		
		for (int i = 0; i < iterazioniPossibili; i++) {
			
			printMemory(i, iterazioniPossibili);
		
			OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					contentType, 
					new ByteArrayInputStream(resource), 
					bufferThresholdKb);
			InputStream is = null;
			try {
				streamReader.read();
				streamReader.checkException();
			}finally {
				// anche in caso di eccezione devo cmq aggiornare is
				is = streamReader.getBufferedInputStream();
			}
			print(i, "StreamReader", streamReader, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, is, null, streamReader);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			print(i, "OpenSPCoop2Message-init", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			boolean isOptimizable = streamReader.isSoapHeaderOptimizable();
			if(!isOptimizable) {
				if(attesoComeOttimizzabile) {
					throw new Exception("Atteso come ottimizzabile");
				}
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			SOAPHeader hdr = soapMsg.getSOAPHeader();
			if(hdr==null) {
				hdr = soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			SOAPElement soapElement = hdr.addChildElement("PROVA","test","http://prova");
			
			if(isOptimizable) {
				if(fileName.contains("Header5K") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
					print(i, "OpenSPCoop2Message-after-header", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 2l : rapporto_max_saaj); // viene costruito saaj in reader
				}
				else if(fileName.startsWith("requestSoapPrefixEmptyWithHeader") || fileName.startsWith("requestMixedPrefix")) {
					print(i, "OpenSPCoop2Message-after-header", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 4l : rapporto_min * 4l); // viene costruito saaj in reader
				}
				else {
					print(i, "OpenSPCoop2Message-after-header", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min * 2l : rapporto_stream_reader+1);
				}
			}
			else {
				print(i, "OpenSPCoop2Message-after-header", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			}
			
			soapMsg.getSOAPBody();
			
			if(fileName.contains("Header5K") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
				print(i, "OpenSPCoop2Message-after-body", msg, initial, rapporto_min_saaj, rapporto_max_saaj*2); // spazio con rapporto raddoppiato: saaj incredibilmente esoso
			}
			else {
				print(i, "OpenSPCoop2Message-after-body", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			}
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			StringBuilder sbDebug = new StringBuilder();
			((AbstractBaseOpenSPCoop2MessageDynamicContent<?>)msg).writeTo(bout, true, false, null, sbDebug);
			if(!Costanti.WRITE_MODE_SERIALIZE_CONTENT.equals(sbDebug.toString())) {
				throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_CONTENT+"'; riscontrata: "+sbDebug.toString());
			}
			bout.flush();
			bout.close();
			
			if(i==0 && print_message) {
				System.out.println("WRITE AFTER BODY: "+bout.toString());
			}
			
			long dimensione = -1;
			if(fileName.contains("Header5K") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
				dimensione = print(i, "OpenSPCoop2Message-after-body-write", msg, initial, rapporto_min_saaj, rapporto_max_saaj*2); // spazio con rapporto raddoppiato: saaj incredibilmente esoso
			}
			else {
				dimensione = print(i, "OpenSPCoop2Message-after-body-write", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			}
			if(i==0) {
				if(incremento_minimo_memoria>1) {
					dimensione = dimensione * incremento_minimo_memoria;
				}
				saveDimensioneOggetto(dimensione);
			}
			contenitor.add(msg); // per GC
			
			// verifico consistenza messaggio scritto
			verificaConsistenza(contentType, factory, bout, messageType, fileName);
			
			// verifico uguaglianza
			String newHeader = OpenSPCoop2MessageFactory.getAsString(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), soapElement, true);
			String newString = null;
			if(fileName.startsWith("requestHeaderBodyXmlEntityOpenOnly")) {
				newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				
				List<String> listSrc = new ArrayList<String>();
				listSrc.add("&lt;redro>&lt;lobmys>IBM&lt;/lobmys>&lt;DIreyub>asankha&lt;/DIreyub>&lt;ecirp>140.34&lt;/ecirp>&lt;emulov>2000&lt;/emulov>&lt;/redro>");
				listSrc.add("&lt;redro>&lt;lobmys>MSFT&lt;/lobmys>&lt;DIreyub>ruwan&lt;/DIreyub>&lt;ecirp>23.56&lt;/ecirp>&lt;emulov>8030&lt;/emulov>&lt;/redro>");
				listSrc.add("&lt;redro>&lt;lobmys>SUN&lt;/lobmys>&lt;DIreyub>indika&lt;/DIreyub>&lt;ecirp>14.56&lt;/ecirp>&lt;emulov>500&lt;/emulov>&lt;/redro>");
				listSrc.add("&lt;redro>&lt;lobmys>GOOG&lt;/lobmys>&lt;DIreyub>chathura&lt;/DIreyub>&lt;ecirp>60.24&lt;/ecirp>&lt;emulov>40000&lt;/emulov>&lt;/redro>");
				List<String> listDest = new ArrayList<String>();
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;");
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;");
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;");
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;");
				for (int j = 0; j < listSrc.size(); j++) {
					String src = listSrc.get(j);
					String dest = listDest.get(j);
					int ind = newString.indexOf(src);
					
					//System.out.println("["+j+"] 0<ind["+ind+"]<body["+indexOf+"]");
					while(ind >0) {
						newString = newString.replaceFirst(src, dest);
						ind = newString.indexOf(src);
						//System.out.println("-----["+j+"] 0<ind["+ind+"]<body["+indexOf+"]");
					}
				}
			}
			else if(fileName.startsWith("requestHeaderACapo")) {
				newString = resourceAsString.replace("</soapenv:Header\n"+
						">", newHeader+"</soapenv:Header>");				
				newString = newString.replace("<soapenv:Header\n"+
						"xmlns:xsd=\"http://services.samples/xsd\"\n"+
						"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
						"xmlns=\"AltroperTest\"   \n"+
						"   >",
						"<soapenv:Header xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
				newString = newString.replace("<xsd:skcotSyub\n"+
						"xmlns:xsd=\"http://services.samples/xsd\"\n"+
						"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
						"xmlns=\"AltroperTest\"\n"+
						">",
						"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\" xmlns=\"AltroperTest\" xmlns:xsd2=\"http://services.samples/xsd\">");
			}
			else if(fileName.startsWith("requestHeaderTab_")) {
				newString = resourceAsString.replace("</soapenv:Header	>", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
						"	xmlns=\"AltroperTest\"	\n"+	
						">", 
						"<soapenv:Header xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
					newString = newString.replaceFirst("<xsd:skcotSyub	>","<xsd:skcotSyub>");
					newString = newString.replaceFirst("<redro	>","<redro>");
					newString = newString.replace("<xsd:skcotSyub		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
							"	xmlns=\"AltroperTest\"	\n"+	
							">",
							"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\" xmlns=\"AltroperTest\" xmlns:xsd2=\"http://services.samples/xsd\">");
			}
			else if(fileName.startsWith("requestFirstChildNameHeader") || fileName.startsWith("requestFirstChildNameExactHeader")) {
				newString = resourceAsString.replace("</soap:Header>", newHeader+"</soap:Header>");
				newString = newString.replace("</soap:Envelope>\n", "</soap:Envelope>");
			}
			else if(fileName.contains("Header") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
				newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				if(shortHeader) {
					newString = newString.replace("<soapenv:Header/>", "<soapenv:Header>"+newHeader+"</soapenv:Header>");
				}
				
				// i prefissi vuoti, nel dom vengono rimpiazzati con SOAP-ENV in soap11 ed env in soap12
				newString = newString.replace("<Header ", "<"+defaultDomPrefix+":Header ");
				newString = newString.replace("</Header>", newHeader+"</"+defaultDomPrefix+":Header>");
				
				if(fileName.startsWith("requestHeaderBodyVariCommenti")) {
					newString = newString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
						"\n"+
						"<!-- Commento Iniziale -->\n"+
						"\n"+
						"<!--","<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- Commento Iniziale --><!--");
					newString = newString.replace("-->\n"+
						"\n"+
						"\n"+
						"   <soapenv:Envelope","--><soapenv:Envelope");
				}
			}
			else if(mixed) {
				newString = resourceAsString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
				newString = newString.replace("</Header>", newHeader+"</"+defaultDomPrefix+":Header>");
				newString = newString.replace("<Header>", "<"+defaultDomPrefix+":Header>");
				newString = newString.replace("<Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n"+ 
					"   xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\" \n"+ 
					"   xmlns:hdr=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"+ 
					"   xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" \n"+ 
					"   xmlns:xsd=\"http://services.samples/xsd\">",
					
					"<"+defaultDomPrefix+":Envelope xmlns:"+defaultDomPrefix+"=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hdr=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:xsd=\"http://services.samples/xsd\">");
				newString = newString.replace("<env:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" \n"+ 
						"   xmlns=\"http://www.w3.org/2003/05/soap-envelope\" \n"+ 
						"   xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"\n"+ 
						"   xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" \n"+ 
						"   xmlns:xsd=\"http://services.samples/xsd\">",
						
						"<"+defaultDomPrefix+":Envelope xmlns:"+defaultDomPrefix+"=\"http://www.w3.org/2003/05/soap-envelope\" xmlns=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:xsd=\"http://services.samples/xsd\">");
				newString = newString.replace("</Envelope>", "</"+defaultDomPrefix+":Envelope>");
			}
			else if(fileName.startsWith("requestEmpty1")) {
				newString = resourceAsString.replace("<soapenv:Body/>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body/>");
			}
			else if(fileName.startsWith("requestEmpty2")) {
				newString = resourceAsString.replace("<Body></Body>", "<"+defaultDomPrefix+":Header>"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body/>");
			}
			else if(fileName.startsWith("requestEmpty3")) {
				newString = resourceAsString.replace(("<Body>"), ("<"+defaultDomPrefix+":Header>"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>"));
			}
			else if(fileName.startsWith("soapFault_soap")) {
				newString = resourceAsString.replace("<"+defaultDomPrefix+":Body>", "<"+defaultDomPrefix+":Header>"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
				newString = newString.replace("xmlns:soapenv","xmlns:"+defaultDomPrefix+"");
			}
			else if(fileName.startsWith("soapFaultSoapPrefixEmpty")){
				newString = resourceAsString.replace(("<Body>"), ("<"+defaultDomPrefix+":Header>"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>"));
				newString = newString.replace("<Fault>","<"+defaultDomPrefix+":Fault>");
				newString = newString.replace("</Fault>","</"+defaultDomPrefix+":Fault>");
				if(fileName.contains("soap12")) {
					newString = newString.replace("<Detail>","<"+defaultDomPrefix+":Detail>");
					newString = newString.replace("</Detail>","</"+defaultDomPrefix+":Detail>");
				}
			}
			else if(fileName.startsWith("soapFaultLong_soap11")) {
				newString = resourceAsString.replace("<env:Header />", "<env:Header>"+newHeader+"</env:Header>");
			}
			else if(fileName.startsWith("soapFaultLong_soap12")) {
				newString = resourceAsString.replace("<env:Body>", "<env:Header>"+newHeader+"</env:Header><env:Body>");
			}
			else if(fileName.startsWith("requestSimileSoapFault")){
				newString = resourceAsString.replace("<"+defaultDomPrefix+":Body>", "<"+defaultDomPrefix+":Header>"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
			}
			else if(fileName.startsWith("requestMixedPrefixAndCDATA")){
				if(fileName.contains("soap11")) {
					newString = resourceAsString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
					newString = newString.replace("<Envelope xmlns:soapenv=\""+streamReader.getNamespace()+"\" \n"+ 
							"   xmlns=\""+streamReader.getNamespace()+"\" \n"+ 
							"   xmlns:hdr=\""+streamReader.getNamespace()+"\"\n"+ 
							"   xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" \n"+ 
							"   xmlns:xsd=\"http://services.samples/xsd\">",
							
							"<"+defaultDomPrefix+":Envelope xmlns:"+defaultDomPrefix+"=\""+streamReader.getNamespace()+"\" xmlns=\""+streamReader.getNamespace()+"\" xmlns:hdr=\""+streamReader.getNamespace()+"\" xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:xsd=\"http://services.samples/xsd\">");
				}
				else {
					newString = resourceAsString.replace("</Header>", newHeader+"</env:Header>");
					newString = newString.replace("<Header>", "<env:Header>");
					newString = newString.replace("<env:Envelope xmlns:soapenv=\""+streamReader.getNamespace()+"\" \n"+ 
							"   xmlns=\""+streamReader.getNamespace()+"\" \n"+ 
							"   xmlns:env=\""+streamReader.getNamespace()+"\"\n"+ 
							"   xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" \n"+ 
							"   xmlns:xsd=\"http://services.samples/xsd\">",
							
							"<"+defaultDomPrefix+":Envelope xmlns:"+defaultDomPrefix+"=\""+streamReader.getNamespace()+"\" xmlns=\""+streamReader.getNamespace()+"\" xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:xsd=\"http://services.samples/xsd\">");
				}
			}
			else if(fileName.startsWith("requestSOAPMultipartRelatedMIME") || fileName.startsWith("contentIdMalformedSOAPMultipartRelatedMIME")) {
				newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				newString = newString.replace("soapenv:mustUnderstand=\"0\" >","soapenv:mustUnderstand=\"0\">");
				String mime = ContentTypeUtilities.readMultipartBoundaryFromContentType(soapMsg.getContentType());
				newString = newString.replace("------=_Part_0_6330713.1171639717331--","--"+mime+"--");
				newString = newString.replace("------=_Part_0_6330713.1171639717331","--"+mime);
				newString = newString.replaceFirst("charset=UTF-8","charset=utf-8");
				if(!fileName.startsWith("requestSOAPMultipartRelatedMIMEbodyEmpty")) {
					newString = newString.replaceFirst("&quot;mario&quot;","\"mario\"");
					newString = newString.replaceFirst("&quot;rossi&quot;","\"rossi\"");
					newString = newString.replaceFirst("<attachInZIP1 href=\"cid:D559E7E9E29638A15AD37B90FCAEAD53\" xmlns=\"\"/><attachInZIP2 href=\"cid:FF5ED4B1298A2E36CF986C32638C5257\" xmlns=\"\"/>",
							"<attachInZIP1 xmlns=\"\" href=\"cid:D559E7E9E29638A15AD37B90FCAEAD53\"/><attachInZIP2 xmlns=\"\" href=\"cid:FF5ED4B1298A2E36CF986C32638C5257\"/>");
				}
				newString = newString.substring(2);
				newString = newString.substring(0, newString.length()-2);
			}
			else if(fileName.startsWith("requestACapo")) {
				newString = resourceAsString.replace("</soapenv:Header\n"+
							">", newHeader+"</soapenv:Header>");
				newString = newString.replace("</soapenv:Header\n\n"+
						">", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header\n"+
						"xmlns:xsd=\"http://services.samples/xsd\"\n"+
						"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
						"xmlns=\"AltroperTest\"   \n"+
						"   >",
						"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
				
				newString = newString.replace("<xsd:skcotSyub\n"+
								">","<xsd:skcotSyub>");
				newString = newString.replace("<xsd:skcotSyub\n"+
						"   >","<xsd:skcotSyub>");
				
				newString = newString.replace("<redro\n"+
						">","<redro>");
				newString = newString.replace("</xsd:skcotSyub\n"+
						">","</xsd:skcotSyub>");
				newString = newString.replace("?>\n"+
						"\n"+
						"   <soapenv:Envelope\n"+
						(fileName.contains("11") ? "\n" : "")+
						"xmlns:soapenv=\""+streamReader.getNamespace()+"\"\n"+
						"\n"+
						">",
						"?><soapenv:Envelope xmlns:soapenv=\""+streamReader.getNamespace()+"\">");
				newString = newString.replace("<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\"","<soapenv:Header"); 
				newString = newString.replace("<soapenv:Body\n"+
						">","<soapenv:Body>");
				newString = newString.replace("<soapenv:Body\n\n"+
						">","<soapenv:Body>");
				newString = newString.replace("<xsd:skcotSyub\n"+
					(fileName.contains("12") ? "\n" : "")+
					"xmlns:xsd=\"http://services.samples/xsd\"\n"+
					"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
					"xmlns=\"AltroperTest\"\n"+
					">",
					"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\" xmlns=\"AltroperTest\" xmlns:xsd2=\"http://services.samples/xsd\">");
				newString = newString.replace("</soapenv:Body\n\n"+
						">","</soapenv:Body>");
				newString = newString.replace("</lobmys\n"+
						">","</lobmys>");
				newString = newString.replace("</redro\n"+
						">","</redro>");
				newString = newString.replace("</soapenv:Envelope\n\n"+
						">","</soapenv:Envelope>");
				newString = newString.replace("</soapenv:Envelope\n"+
						">","</soapenv:Envelope>");
				newString = newString.replace("</xsd:skcotSyub\n\n"+
						">","</xsd:skcotSyub>");
			}
			else if(fileName.startsWith("requestTab")) {
				newString = resourceAsString.replace("</soapenv:Header	>", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
					"	xmlns=\"AltroperTest\"	\n"+	
					">", 
					"<soapenv:Header xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
				newString = newString.replaceFirst("<xsd:skcotSyub	>","<xsd:skcotSyub>");
				newString = newString.replaceFirst("<redro	>","<redro>");
				newString = newString.replace("<?xml	 version=\"1.0\"	 encoding=\"UTF-8\"	?>\n"+
						"\n"+
						"   <soapenv:Envelope	xmlns:soapenv=\""+streamReader.getNamespace()+"\"		>",
						"<?xml	 version=\"1.0\"	 encoding=\"UTF-8\"	?><soapenv:Envelope xmlns:soapenv=\""+streamReader.getNamespace()+"\">");
				newString = newString.replace("<soapenv:Body	>","<soapenv:Body>");
				newString = newString.replace("</xsd:skcotSyub		>","</xsd:skcotSyub>");
				newString = newString.replace("</soapenv:Body		>","</soapenv:Body>");
				newString = newString.replace("</soapenv:Envelope		>","</soapenv:Envelope>");
				newString = newString.replace("<redro		>","<redro>");
				newString = newString.replace("<xsd:skcotSyub		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
					"	xmlns=\"AltroperTest\"	\n"+	
					">",
					"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\" xmlns=\"AltroperTest\" xmlns:xsd2=\"http://services.samples/xsd\">");
			}
			else if(fileName.startsWith("requestRootElementACapo")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<xsd:skcotSyub\n"+
						"xmlns:xsd=\"http://services.samples/xsd\"\n"+
						"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
						"xmlns=\"AltroperTest\"\n"+
						">",
						"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\" xmlns=\"AltroperTest\" xmlns:xsd2=\"http://services.samples/xsd\">");
				newString = newString.replace("</xsd:skcotSyub\n"+
						">","</xsd:skcotSyub>");
			}
			else if(fileName.startsWith("requestRootElementTab")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<xsd:skcotSyub		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
						"	xmlns=\"AltroperTest\"	\n"+	
						">",
						"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\" xmlns=\"AltroperTest\" xmlns:xsd2=\"http://services.samples/xsd\">");
				newString = newString.replace("</xsd:skcotSyub	>","</xsd:skcotSyub>");
			}
			else if(fileName.startsWith("requestSoapFaultACapo_soap11")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<soapenv:Fault\n"+
					"xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"+
					"xmlns:problem=\"urn:ietf:rfc:7807\"\n"+
					">",
					"<soapenv:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:problem=\"urn:ietf:rfc:7807\">");
				newString = newString.replace("</soapenv:Fault\n>","</soapenv:Fault>");
			}
			else if(fileName.startsWith("requestSoapFaultACapo_soap12")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<env:Fault\n"+
					"xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"\n"+
					"xmlns:problem=\"urn:ietf:rfc:7807\"\n"+
					"\n"+
					"    >",
					"<env:Fault xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:problem=\"urn:ietf:rfc:7807\">");
				newString = newString.replace("</env:Fault\n\n>","</env:Fault>");
			}
			else if(fileName.startsWith("requestSoapFaultTab_soap11")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<soapenv:Fault	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"	xmlns:problem=\"urn:ietf:rfc:7807\"	>",
						"<soapenv:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:problem=\"urn:ietf:rfc:7807\">");
				newString = newString.replace("</soapenv:Fault		>","</soapenv:Fault>");
			}
			else if(fileName.startsWith("requestSoapFaultTab_soap12")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<env:Fault		xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\"	xmlns:problem=\"urn:ietf:rfc:7807\"		>",
						"<env:Fault xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:problem=\"urn:ietf:rfc:7807\">");
				newString = newString.replace("</env:Fault		>","</env:Fault>");
			}
			else if(fileName.startsWith("requestFirstChildNameBody") || fileName.startsWith("requestFirstChildNameExactBody") || 
					fileName.startsWith("requestSoap12Commentata") || fileName.startsWith("requestSoap11Commentata")) {
				newString = resourceAsString.replace("<soap:Body>", "<soap:Header>"+newHeader+"</soap:Header><soap:Body>");	
				newString = newString.replace("</soap:Envelope>\n", "</soap:Envelope>");
				if(fileName.startsWith("requestSoap12Commentata")) {
					newString = newString.replace("\n  <!--<soap:Envelope","<!--<soap:Envelope");
					newString = newString.replace("-->     \n\n<soap:Envelope","--><soap:Envelope");
				}
				if(fileName.startsWith("requestSoap11Commentata")) {
					newString = newString.replace("\n  <!--<soap:Envelope","<!--<soap:Envelope");
					newString = newString.replace("-->\n\n<soap:Envelope","--><soap:Envelope");
				}
				if(fileName.startsWith("requestSoap12CommentataACapo")) {
					newString = newString.replace("\n<!--","<!--");
					newString = newString.replace("-->      \n\n","-->");
				}
				if(fileName.startsWith("requestSoap11CommentataACapo")) {
					newString = newString.replace("\n  <!--","<!--");
					newString = newString.replace("-->      \n\n","-->");
					newString = newString.replace("</ns2:skcotSyub>y>","</ns2:skcotSyub>y&gt;");
				}
			}
			else if(fileName.startsWith("requestXmlDeclarationACapo")) {
				newString = resourceAsString.replace("</soap:Header>", newHeader+"</soap:Header>");
				newString = newString.replace("</soap:Envelope>\n", "</soap:Envelope>");
				newString = newString.replace("?>\n"+
					"\n"+
					"<soap:Envelope","?><soap:Envelope");
			}
			else if(fileName.startsWith("requestBodyMalformed2")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("CONTENUTO_ERRATO>","CONTENUTO_ERRATO&gt;");
			}
			else if(fileName.startsWith("requestBodyMalformed3")) {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("CONTENUTO_ERRATO/>","CONTENUTO_ERRATO/&gt;");
			}
			else {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header>"+newHeader+"</soapenv:Header><soapenv:Body>");
				
				// i prefissi vuoti, nel dom vengono rimpiazzati con SOAP-ENV in soap11 ed env in soap12
				newString = newString.replace("<Body>", "<"+defaultDomPrefix+":Header>"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
			}
			// i prefissi vuoti, nel dom vengono rimpiazzati con SOAP-ENV in soap11 ed env in soap12
			if(!mixed) {
				newString = newString.replace("<Envelope xmlns=\""+streamReader.getNamespace()+"\"", "<"+defaultDomPrefix+":Envelope xmlns:"+defaultDomPrefix+"=\""+streamReader.getNamespace()+"\" xmlns=\""+streamReader.getNamespace()+"\"");
				newString = newString.replace("</Envelope>", "</"+defaultDomPrefix+":Envelope>");
				newString = newString.replace("<Body>", "<"+defaultDomPrefix+":Body>");
				newString = newString.replace("</Body>", "</"+defaultDomPrefix+":Body>");
			}
			
			newString = newString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n   ", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			newString = newString.replace("</soapenv:Envelope>\n", "</soapenv:Envelope>");
			newString = newString.replace("</"+defaultDomPrefix+":Envelope>\n", "</"+defaultDomPrefix+":Envelope>");
			String check = null;
			if(newString!=null) {
				newString = normalizeReturnLine(newString);
				check = normalizeReturnLine(bout.toString());
			}
			if(newString!=null && !newString.equals(check)) {
				if(fileName.startsWith("requestSOAPMultipartRelatedMIMEpdf")) {
					if(i==0) {
						System.out.println("Verifica disabilitata; richiesta binaria con PDF");
					}
				}
				else {
					throw new Exception("Contenuto serializzato differente: \nATTESO:\n["+newString+"]\n GENERATO:\n["+check+"]");
				}
			}
			
			print(i, "finalResourceRaw", bout.toByteArray(), initial, 1l, rapporto_min);
			
		}
		
		checkMemoryError();
		
		
		
		

		
		System.out.println("== TRASPARENTE, ACCESSO CONTENUTO READ ONLY ==");
		
		rapporto = rapporto_max_saaj;
		iterazioniPossibili = (HEAP_MEMORY_SIZE / initial) / rapporto;
		if(!testHeap) {
			iterazioniPossibili = 1;
		}
		else {
			if(iterazioniPossibili>maxIterations) {
				iterazioniPossibili = maxIterations;
			}
		}
		
		for (int i = 0; i < iterazioniPossibili; i++) {
			
			printMemory(i, iterazioniPossibili);
			
			OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					contentType, 
					new ByteArrayInputStream(resource), 
					bufferThresholdKb);
			InputStream is = null;
			try {
				streamReader.read();
				streamReader.checkException();
			}finally {
				// anche in caso di eccezione devo cmq aggiornare is
				is = streamReader.getBufferedInputStream();
			}
			print(i, "StreamReader", streamReader, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, is, null, streamReader);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			print(i, "OpenSPCoop2Message-init", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			boolean isOptimizable = streamReader.isSoapHeaderOptimizable();
			if(!isOptimizable) {
				if(attesoComeOttimizzabile) {
					throw new Exception("Atteso come ottimizzabile");
				}
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			AbstractOpenSPCoop2Message_soap_impl<?> soap = null;
			if(soapMsg instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
				soap = (AbstractOpenSPCoop2Message_soap_impl<?>)soapMsg;
				soap.getContent(true, "idTransazione-xxx").getSOAPMessage();
			}
						
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			StringBuilder sbDebug = new StringBuilder();
			((AbstractBaseOpenSPCoop2MessageDynamicContent<?>)msg).writeTo(bout, true, false, null, sbDebug);
			if(!Costanti.WRITE_MODE_SERIALIZE_BUFFER.equals(sbDebug.toString())) {
				throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_BUFFER+"'; riscontrata: "+sbDebug.toString());
			}
			bout.flush();
			bout.close();
			
			if(i==0 && print_message) {
				System.out.println("WRITE: "+bout.toString());
			}
			
			long dimensione = print(i, "OpenSPCoop2Message-after-write", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			if(i==0) {
				if(incremento_minimo_memoria>1) {
					dimensione = dimensione * incremento_minimo_memoria;
				}
				saveDimensioneOggetto(dimensione);
			}
			contenitor.add(msg); // per GC
			
			// verifico consistenza messaggio scritto
			verificaConsistenza(contentType, factory, bout, messageType, fileName);
			
			// verifico uguaglianza
			if(!resourceAsString.equals(bout.toString())) {
				throw new Exception("Contenuto serializzato differente: \nATTESO:\n["+resourceAsString+"]\n GENERATO:\n["+bout.toString()+"]");
			}
			
			print(i, "finalResourceRaw", bout.toByteArray(), initial, 1l, rapporto_min);
		}
		
		checkMemoryError();
		
		
		

		
		
		System.out.println("== HEADER e ACCESSO CONTENUTO READ ONLY ==");
		
		rapporto = rapporto_max_saaj;
		iterazioniPossibili = (HEAP_MEMORY_SIZE / initial) / rapporto;
		if(!testHeap) {
			iterazioniPossibili = 1;
		}
		else {
			if(iterazioniPossibili>maxIterations) {
				iterazioniPossibili = maxIterations;
			}
		}
		
		for (int i = 0; i < iterazioniPossibili; i++) {
			
			printMemory(i, iterazioniPossibili);
			
			OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
					contentType, 
					new ByteArrayInputStream(resource), 
					bufferThresholdKb);
			InputStream is = null;
			try {
				streamReader.read();
				streamReader.checkException();
			}finally {
				// anche in caso di eccezione devo cmq aggiornare is
				is = streamReader.getBufferedInputStream();
			}
			print(i, "StreamReader", streamReader, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, is, null, streamReader);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			print(i, "OpenSPCoop2Message-init", msg, initial, 1l, bufferThresholdKb==1 ? rapporto_min : rapporto_stream_reader);
			
			boolean isOptimizable = streamReader.isSoapHeaderOptimizable();
			if(!isOptimizable) {
				if(attesoComeOttimizzabile) {
					throw new Exception("Atteso come ottimizzabile");
				}
			}			
			OpenSPCoop2SoapMessage _soapMsg = msg.castAsSoap();
			
			AbstractOpenSPCoop2Message_soap_impl<?> soap = null;
			if(_soapMsg instanceof AbstractOpenSPCoop2Message_soap_impl<?>) {
				soap = (AbstractOpenSPCoop2Message_soap_impl<?>)_soapMsg;
				soap.getContent(true, "idTransazione-xxx").getSOAPMessage();
			}
			
			SOAPHeader hdr = soap.getSOAPHeader();
			if(hdr==null) {
				hdr = soap.getSOAPPart().getEnvelope().addHeader();
			}
			SOAPElement soapElement = hdr.addChildElement("PROVA","test","http://prova");
			
			if(fileName.contains("Header5K") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
				print(i, "OpenSPCoop2Message-after-header", msg, initial, rapporto_min_saaj, rapporto_max_saaj*2); // viene mantenuto l'oggetto header saaj presente nel reader + l'oggetto dom per le operazioni readonly
			}
			else {
				print(i, "OpenSPCoop2Message-after-header", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			}
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			StringBuilder sbDebug = new StringBuilder();
			((AbstractBaseOpenSPCoop2MessageDynamicContent<?>)msg).writeTo(bout, true, false, null, sbDebug);
			if(isOptimizable) {
				if(!Costanti.WRITE_MODE_SERIALIZE_BUFFER_WITH_HEADER.equals(sbDebug.toString())) {
					throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_BUFFER_WITH_HEADER+"'; riscontrata: "+sbDebug.toString());
				}
			}
			else {
				if(!Costanti.WRITE_MODE_SERIALIZE_CONTENT.equals(sbDebug.toString())) {
					throw new Exception("Attesa modalita di serializzazione '"+Costanti.WRITE_MODE_SERIALIZE_CONTENT+"'; riscontrata: "+sbDebug.toString());
				}
			}
			bout.flush();
			bout.close();
			
			if(i==0 && print_message) {
				System.out.println("WRITE AFTER HEADER: "+bout.toString());
			}
			
			// la write rilascia le risorse presenti nell'oggetto header saaj presente nel reader
			long dimensione = print(i, "OpenSPCoop2Message-after-header-write", msg, initial, rapporto_min_saaj, rapporto_max_saaj);
			if(i==0) {
				if(incremento_minimo_memoria>1) {
					dimensione = dimensione * incremento_minimo_memoria;
				}
				saveDimensioneOggetto(dimensione);
			}
			contenitor.add(msg); // per GC
			
			// verifico consistenza messaggio scritto
			verificaConsistenza(contentType, factory, bout, messageType, fileName);
			
			// verifico uguaglianza
			String newHeader = OpenSPCoop2MessageFactory.getAsString(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), soapElement, true);
			String newString = null;
			if(fileName.startsWith("requestHeaderBodyXmlEntityOpenOnly")) {
				newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">");
				newString = newString.replaceFirst("<xsd:skcotSyub>", "<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">");
				int indexOf = newString.indexOf("<soapenv:Body>");
				
				List<String> listSrc = new ArrayList<String>();
				listSrc.add("&lt;redro>&lt;lobmys>IBM&lt;/lobmys>&lt;DIreyub>asankha&lt;/DIreyub>&lt;ecirp>140.34&lt;/ecirp>&lt;emulov>2000&lt;/emulov>&lt;/redro>");
				listSrc.add("&lt;redro>&lt;lobmys>MSFT&lt;/lobmys>&lt;DIreyub>ruwan&lt;/DIreyub>&lt;ecirp>23.56&lt;/ecirp>&lt;emulov>8030&lt;/emulov>&lt;/redro>");
				listSrc.add("&lt;redro>&lt;lobmys>SUN&lt;/lobmys>&lt;DIreyub>indika&lt;/DIreyub>&lt;ecirp>14.56&lt;/ecirp>&lt;emulov>500&lt;/emulov>&lt;/redro>");
				listSrc.add("&lt;redro>&lt;lobmys>GOOG&lt;/lobmys>&lt;DIreyub>chathura&lt;/DIreyub>&lt;ecirp>60.24&lt;/ecirp>&lt;emulov>40000&lt;/emulov>&lt;/redro>");
				List<String> listDest = new ArrayList<String>();
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;");
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;");
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;");
				listDest.add("&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;");
				for (int j = 0; j < listSrc.size(); j++) {
					String src = listSrc.get(j);
					String dest = listDest.get(j);
					int ind = newString.indexOf(src);
					indexOf = newString.indexOf("<soapenv:Body>"); // va ricalcolato
					
					//System.out.println("["+j+"] 0<ind["+ind+"]<body["+indexOf+"]");
					while(ind >0 && ind < indexOf) {
						newString = newString.replaceFirst(src, dest);
						ind = newString.indexOf(src);
						indexOf = newString.indexOf("<soapenv:Body>"); // va ricalcolato
						//System.out.println("-----["+j+"] 0<ind["+ind+"]<body["+indexOf+"]");
					}
				}
			}
			else if(fileName.startsWith("requestHeaderACapo")) {
				newString = resourceAsString.replace("</soapenv:Header\n"+
						">", newHeader+"</soapenv:Header>");				
				newString = newString.replace("<soapenv:Header\n"+
						"xmlns:xsd=\"http://services.samples/xsd\"\n"+
						"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
						"xmlns=\"AltroperTest\"   \n"+
						"   >",
						"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
			}
			else if(fileName.startsWith("requestHeaderTab_")) {
				newString = resourceAsString.replace("</soapenv:Header	>", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
						"	xmlns=\"AltroperTest\"	\n"+	
						">", 
						"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
					newString = newString.replaceFirst("<xsd:skcotSyub	>","<xsd:skcotSyub>");
					newString = newString.replaceFirst("<redro	>","<redro>");
			}
			else if(fileName.startsWith("requestFirstChildNameHeader") || fileName.startsWith("requestFirstChildNameExactHeader")) {
				newString = resourceAsString.replace("</soap:Header>", newHeader+"</soap:Header>");
				newString = newString.replace("<soap:Header>", "<soap:Header xmlns:soap=\""+streamReader.getNamespace()+"\">");
			}
			else if(fileName.contains("Header") || fileName.startsWith("requestSuUnaRigaSoapBodyVuoto")) {
				if(isOptimizable) {
					newString = resourceAsString.replace("<soapenv:Header>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">");
					if(shortHeader) {
						newString = newString.replace("<soapenv:Header/>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\"></soapenv:Header>");
					}
					if(!wsa && !shortHeader && !cdata) {
						newString = newString.replaceFirst("<xsd:skcotSyub>", "<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">");
					}
					newString = newString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
					newString = newString.replace("</Header>", newHeader+"</Header>");
					newString = newString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
					if(wsa) {
						newString = newString.replace("<Header xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">",
								"<Header xmlns=\""+streamReader.getNamespace()+"\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
					}
				}
				else {
					newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				}
			}
			else if(mixed) {
				newString = resourceAsString.replace("<hdr:Header>",
						"<hdr:Header xmlns:hdr=\""+streamReader.getNamespace()+"\">");
				newString = newString.replace("</Header>", newHeader+"</Header>");
				newString = newString.replace("<hdr:Header>",
						"<hdr:Header xmlns:hdr=\""+streamReader.getNamespace()+"\">");
				newString = newString.replace("<Header>",
						"<Header xmlns=\""+streamReader.getNamespace()+"\">");
				newString = newString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
				newString = newString.replace("<wsa:MessageID>","<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				newString = newString.replace("<wsa:ReplyTo>","<wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				newString = newString.replace("<wsa:To>","<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				newString = newString.replace("<wsa:Action>","<wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
			}
			else if(fileName.startsWith("requestEmpty1")) {
				newString = resourceAsString.replace("<soapenv:Body/>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">"+newHeader+"</soapenv:Header><soapenv:Body/>");
			}
			else if(fileName.startsWith("requestEmpty2")) {
				newString = resourceAsString.replace("<Body>", "<Header xmlns=\""+streamReader.getNamespace()+"\">"+newHeader+"</Header><Body>");
			}
			else if(fileName.startsWith("soapFault_soap")) {
				newString = resourceAsString.replace("<"+defaultDomPrefix+":Body>", "<"+defaultDomPrefix+":Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
				newString = newString.replace("xmlns:soapenv","xmlns:"+defaultDomPrefix+"");
			}
			else if(fileName.startsWith("soapFaultLong_soap11")) {
				newString = resourceAsString.replace("<env:Header />\n    <env:Body>", "<env:Header xmlns:env=\""+streamReader.getNamespace()+"\">"+newHeader+"</env:Header><env:Body>");
			}
			else if(fileName.startsWith("soapFaultLong_soap12")) {
				newString = resourceAsString.replace("<env:Body>", "<env:Header xmlns:env=\""+streamReader.getNamespace()+"\">"+newHeader+"</env:Header><env:Body>");
			}
			else if(fileName.startsWith("requestSimileSoapFault")){
				newString = resourceAsString.replace("<"+defaultDomPrefix+":Body>", "<"+defaultDomPrefix+":Header xmlns:"+defaultDomPrefix+"=\""+streamReader.getNamespace()+"\">"+newHeader+"</"+defaultDomPrefix+":Header><"+defaultDomPrefix+":Body>");
			}
			else if(fileName.startsWith("requestMixedPrefixAndCDATA")){
				if(fileName.contains("soap11")) {
					newString = resourceAsString.replace("</hdr:Header>", newHeader+"</hdr:Header>");
					newString = newString.replace("<hdr:Header>", "<hdr:Header xmlns:hdr=\""+streamReader.getNamespace()+"\">");
				}
				else {
					newString = resourceAsString.replace("</Header>", newHeader+"</Header>");
					newString = newString.replace("<Header>", "<Header xmlns=\""+streamReader.getNamespace()+"\">");
				}
				newString = newString.replace("<wsa:MessageID>","<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				newString = newString.replace("<wsa:ReplyTo>","<wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				newString = newString.replace("<wsa:To>","<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
				newString = newString.replace("<wsa:Action>","<wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">");
			}
			else if(fileName.startsWith("requestSOAPMultipartRelatedMIME") || fileName.startsWith("contentIdMalformedSOAPMultipartRelatedMIME")) {
				newString = resourceAsString.replace("</soapenv:Header>", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header ", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" ");
				newString = newString.replace("soapenv:mustUnderstand=\"0\" >","soapenv:mustUnderstand=\"0\">");
			}
			else if(fileName.startsWith("requestACapo")) {
				newString = resourceAsString.replace("</soapenv:Header\n"+
							">", newHeader+"</soapenv:Header>");
				newString = newString.replace("</soapenv:Header\n\n"+
						">", newHeader+"</soapenv:Header>");
				
				newString = newString.replace("<soapenv:Header\n"+
						"xmlns:xsd=\"http://services.samples/xsd\"\n"+
						"xmlns:xsd2=\"http://services.samples/xsd\"\n"+
						"xmlns=\"AltroperTest\"   \n"+
						"   >",
						"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
				
				newString = newString.replace("<xsd:skcotSyub\n"+
							">","<xsd:skcotSyub>");
				newString = newString.replace("<xsd:skcotSyub\n"+
							"   >","<xsd:skcotSyub>");
				
				if(fileName.contains("11")) {
					newString = newString.replaceFirst("<redro\n"+
							">","<redro>");
				}
				
				newString = newString.replaceFirst("</xsd:skcotSyub\n"+
						">","</xsd:skcotSyub>");
				if(fileName.contains("12")) {
					newString = newString.replaceFirst("</xsd:skcotSyub\n\n"+
							">","</xsd:skcotSyub>");
				}
			}
			else if(fileName.startsWith("requestTab")) {
				newString = resourceAsString.replace("</soapenv:Header	>", newHeader+"</soapenv:Header>");
				newString = newString.replace("<soapenv:Header		xmlns:xsd=\"http://services.samples/xsd\"		xmlns:xsd2=\"http://services.samples/xsd\"	\n"+	
					"	xmlns=\"AltroperTest\"	\n"+	
					">", 
					"<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">");
				newString = newString.replaceFirst("<xsd:skcotSyub	>","<xsd:skcotSyub>");
				newString = newString.replaceFirst("<redro	>","<redro>");
			}
			else if(fileName.startsWith("requestFirstChildNameBody") || fileName.startsWith("requestFirstChildNameExactBody") || 
					fileName.startsWith("requestSoap12Commentata") || fileName.startsWith("requestSoap11Commentata")) {
				newString = resourceAsString.replace("<soap:Body>", "<soap:Header xmlns:soap=\""+streamReader.getNamespace()+"\">"+newHeader+"</soap:Header><soap:Body>");	
			}
			else if(fileName.startsWith("requestXmlDeclarationACapo")) {
				newString = resourceAsString.replace("</soap:Header>", newHeader+"</soap:Header>");
				newString = newString.replace("<soap:Header>", "<soap:Header xmlns:soap=\""+streamReader.getNamespace()+"\">");
			}
			else {
				newString = resourceAsString.replace("<soapenv:Body>", "<soapenv:Header xmlns:soapenv=\""+streamReader.getNamespace()+"\">"+newHeader+"</soapenv:Header><soapenv:Body>");
				newString = newString.replace("<Body>", "<Header xmlns=\""+streamReader.getNamespace()+"\">"+newHeader+"</Header><Body>");
			}
			if(!isOptimizable) {
				newString = newString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n   ", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				newString = newString.replace("</soapenv:Envelope>\n", "</soapenv:Envelope>");
			}
			String check = null;
			if(newString!=null) {
				newString = normalizeReturnLine(newString);
				check = normalizeReturnLine(bout.toString());
			}
			if(newString!=null && !newString.equals(check)) {
				throw new Exception("Contenuto serializzato differente: \nATTESO:\n["+newString+"]\n GENERATO:\n["+check+"]");
			}
			
			print(i, "finalResourceRaw", bout.toByteArray(), initial, 1l, rapporto_min);
		}
		
		checkMemoryError();
		
		
		
		
	}
	

	private static boolean testHeap = false;
	private static long print(int iterazione, String name, Object resource, Long initial, Long rapportoAttesoMin, Long rapportoAttesoMax) throws Exception {
		if(testHeap && iterazione==0) {
			long l = org.openspcoop2.utils.instrument.InstrumentationUtils.deepUsage(resource);//,  org.openspcoop2.utils.instrument.InstrumentationUtils.VisibilityFilter.NON_PUBLIC);
			String rapporto = "";
			long rapportoOttenuto = -1;
			if(initial!=null) {
				rapportoOttenuto = l/initial.longValue();
				rapporto = " (1:"+rapportoOttenuto+")";
			}
			System.out.println("Dimensione "+name+rapporto+": "+Utilities.convertBytesToFormatString(l, true, " "));
			if(initial!=null) {
				if(rapportoOttenuto<rapportoAttesoMin || rapportoOttenuto>rapportoAttesoMax) {
					
					Map<String, List<Long>> map = new HashMap<String, List<Long>>();
					org.openspcoop2.utils.instrument.InstrumentationUtils.deepUsage(resource, map, 6);
					org.openspcoop2.utils.instrument.InstrumentationUtils.printToSystemOut(map, false);
					
					throw new Exception("Atteso un rapporto 1:["+rapportoAttesoMin+"-"+rapportoAttesoMax+"] mentre è stato riscontrato un rapporto 1:"+rapportoOttenuto);
				}
			}
			return l;
		}
		return -1;
    }
	private static long dimensioneOggetto = -1;
	private static void saveDimensioneOggetto(long dimensione) {
		if(testHeap) {
			dimensioneOggetto = dimensione;
			skip=0;
			error.clear();
		}
	}
	private static Runtime runtime = null;
	private static long precedenteCalcolo = -1;
	private static long skip = 0;
	private static long tolleranza_bytes = 1024; // 5kb
	private static List<String> error = new ArrayList<String>();
	private static boolean printAllIterations = false;
	private static void printMemory(int iterazione, long interazioniPossibili) throws Exception {
		if(testHeap) {
			System.gc();
			long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
			
			String increment = "";
			if(precedenteCalcolo>0 && skip>3) {
				if(precedenteCalcolo>0) {
					long diff = memoryBefore - precedenteCalcolo;
					long abs = java.lang.Math.abs(diff);
					increment = " "+((diff<0)?"gcclean:-":"increment:+")+Utilities.convertBytesToFormatString(abs,true," ");
				}
			}
			
			if(printAllIterations || (iterazione%10==0)) {
				System.out.println("memory-after "+iterazione+"/"+interazioniPossibili+": "+Utilities.convertBytesToFormatString(memoryBefore,true," ")+increment);
			}
			if(dimensioneOggetto>0) {
				skip++;
				if(precedenteCalcolo>0 && skip>3) {
					long diff = memoryBefore - precedenteCalcolo;
					
					if(diff<0) {
						precedenteCalcolo=-1; // ha girato il GC
					}
					else {
						if(diff>dimensioneOggetto) {
							long actual = diff - dimensioneOggetto;
							if(actual>tolleranza_bytes) {
								error.add(""+diff+"b");
							}
						}
						precedenteCalcolo = memoryBefore;
					}
				}
				else {
					precedenteCalcolo = memoryBefore;
				}
			}
		}
	}
	private static void checkMemoryError() throws Exception {
		if(error.size()>5) { // accetto 5 errori
			throw new Exception("Atteso un incremento di memoria di "+dimensioneOggetto+"b, riscontrato: "+error.toString());
		}
	}
	
	
	private static void verificaConsistenza(String contentType, OpenSPCoop2MessageFactory factory, ByteArrayOutputStream bout, MessageType messageType, String fileName) throws Exception {
		if(!contentType.startsWith(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED)) {
			Element check = XMLUtils.getInstance(factory).newElement(bout.toByteArray());
			check.toString();
			
			TransportRequestContext req = new TransportRequestContext();
			Map<String, List<String>> parametriTrasporto = new HashMap<String, List<String>>();
			List<String> list = new ArrayList<String>();
			list.add(contentType);
			parametriTrasporto.put(HttpConstants.CONTENT_TYPE, list);
			req.setHeaders(parametriTrasporto);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, new ByteArrayInputStream(bout.toByteArray()), null);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			msg.castAsSoap().getSOAPHeader();
			msg.castAsSoap().getSOAPBody();
			msg.castAsSoap().getSOAPPart();
		}
		else {
			byte[] b = bout.toByteArray();
			//File f = File.createTempFile("sss", "bin");
			//FileSystemUtilities.writeFile(f, bout.toByteArray());
			String ctMultipartRefreshed = null;
			String ct = MessageType.SOAP_11.equals(messageType) ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			if(fileName.startsWith("requestSOAPMultipartRelatedMIMEpdf")) {
				ctMultipartRefreshed = ContentTypeUtilities.buildMultipartContentType(b, ct, ID_SOAP_ENVELOPE_MULTIPART);
			}
			else {
				ctMultipartRefreshed = ContentTypeUtilities.buildMultipartContentType(b, ct);
			}
			//System.out.println("CT ["+ct+"] ["+f.getAbsolutePath()+"]");
			//System.out.println("CT ["+ctMultipartRefreshed+"]");
			
			TransportRequestContext req = new TransportRequestContext();
			Map<String, List<String>> parametriTrasporto = new HashMap<String, List<String>>();
			List<String> list = new ArrayList<String>();
			list.add(ctMultipartRefreshed);
			parametriTrasporto.put(HttpConstants.CONTENT_TYPE, list);
			req.setHeaders(parametriTrasporto);
			
			OpenSPCoop2MessageParseResult parser = factory.createMessage(messageType, req, new ByteArrayInputStream(b), null);
			OpenSPCoop2Message msg = parser.getMessage_throwParseException();
			msg.castAsSoap().getSOAPHeader();
			msg.castAsSoap().getSOAPBody();
			msg.castAsSoap().getSOAPPart();
			if(msg.castAsSoap().countAttachments()<=0) {
				throw new Exception("Attesi attachments");
			}
		}
	}
	
	private static String normalizeReturnLine(String newString) {
		return newString.replaceAll("\r\n", "\n");
	}
}
