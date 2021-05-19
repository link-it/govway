package org.openspcoop2.message.soap.reader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;

public class TestReader {

	private static final String HEADER_5K = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\"><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro><redro><lobmys/><DIreyub/><ecirp/><emulov/></redro></xsd:skcotSyub></soapenv:Header>";
	private static final String HEADER_WSA = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"><wsa:MessageID/><wsa:ReplyTo><wsa:Address/></wsa:ReplyTo><wsa:To/><wsa:Action/></soapenv:Header>";
	private static final String HEADER_WSA_2 = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"/><wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"><wsa:Address/></wsa:ReplyTo><wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"/><wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"/></soapenv:Header>";
	private static final String HEADER_MIME = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:prova=\"http://prova.openspcoop2.org\"><a:example1 xmlns:a=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\"/><b:example2 xmlns:b=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\"/></soapenv:Header>";
	private static final String HEADER_XMLENTITY = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">\n"+
		"&lt;redro&gt;lobmys&gt;IBM&lt;/lobmys&gt;DIreyub&gt;asankha&lt;/DIreyub&gt;ecirp&gt;140.34&lt;/ecirp&gt;emulov&gt;2000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;MSFT&lt;/lobmys&gt;DIreyub&gt;ruwan&lt;/DIreyub&gt;ecirp&gt;23.56&lt;/ecirp&gt;emulov&gt;8030&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;SUN&lt;/lobmys&gt;DIreyub&gt;indika&lt;/DIreyub&gt;ecirp&gt;14.56&lt;/ecirp&gt;emulov&gt;500&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;GOOG&lt;/lobmys&gt;DIreyub&gt;chathura&lt;/DIreyub&gt;ecirp&gt;60.24&lt;/ecirp&gt;emulov&gt;40000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;IBM&lt;/lobmys&gt;DIreyub&gt;asankha&lt;/DIreyub&gt;ecirp&gt;140.34&lt;/ecirp&gt;emulov&gt;2000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;MSFT&lt;/lobmys&gt;DIreyub&gt;ruwan&lt;/DIreyub&gt;ecirp&gt;23.56&lt;/ecirp&gt;emulov&gt;8030&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;SUN&lt;/lobmys&gt;DIreyub&gt;indika&lt;/DIreyub&gt;ecirp&gt;14.56&lt;/ecirp&gt;emulov&gt;500&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;GOOG&lt;/lobmys&gt;DIreyub&gt;chathura&lt;/DIreyub&gt;ecirp&gt;60.24&lt;/ecirp&gt;emulov&gt;40000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;IBM&lt;/lobmys&gt;DIreyub&gt;asankha&lt;/DIreyub&gt;ecirp&gt;140.34&lt;/ecirp&gt;emulov&gt;2000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;MSFT&lt;/lobmys&gt;DIreyub&gt;ruwan&lt;/DIreyub&gt;ecirp&gt;23.56&lt;/ecirp&gt;emulov&gt;8030&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;SUN&lt;/lobmys&gt;DIreyub&gt;indika&lt;/DIreyub&gt;ecirp&gt;14.56&lt;/ecirp&gt;emulov&gt;500&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;GOOG&lt;/lobmys&gt;DIreyub&gt;chathura&lt;/DIreyub&gt;ecirp&gt;60.24&lt;/ecirp&gt;emulov&gt;40000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;IBM&lt;/lobmys&gt;DIreyub&gt;asankha&lt;/DIreyub&gt;ecirp&gt;140.34&lt;/ecirp&gt;emulov&gt;2000&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;MSFT&lt;/lobmys&gt;DIreyub&gt;ruwan&lt;/DIreyub&gt;ecirp&gt;23.56&lt;/ecirp&gt;emulov&gt;8030&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;SUN&lt;/lobmys&gt;DIreyub&gt;indika&lt;/DIreyub&gt;ecirp&gt;14.56&lt;/ecirp&gt;emulov&gt;500&lt;/emulov&gt;/redro&gt;\n"+
		"&lt;redro&gt;lobmys&gt;GOOG&lt;/lobmys&gt;DIreyub&gt;chathura&lt;/DIreyub&gt;ecirp&gt;60.24&lt;/ecirp&gt;emulov&gt;40000&lt;/emulov&gt;/redro&gt;</xsd:skcotSyub></soapenv:Header>";
	
	private static final QName ROOT_ELEMENT_SKCOT = new QName("http://services.samples/xsd", "skcotSyub");
	private static final QName ROOT_ELEMENT_FAULT = new QName("http://services.samples/xsd", "Fault");
	
	public static void main(String[] args) throws Exception {
		
		
		boolean expectedFullBuffer = false; // si esce prima.
		boolean isBodyEmptyAtteso = true;
		boolean isFaultAtteso = true;
		
		int buffer_dimensione_default = 10; // tutti i messaggi di test sono tarati su questa dimensione
		
		String HEADER_SOAP_NON_ATTESO = null;
		QName ROOT_ELEMENT_NON_ATTESO = null;
		
		
		System.out.println("\n\n*** TEST SOAP 11 da 5K (buffer 1k) ***");
		test("request5K_soap11.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 da 5K (buffer 1k) ***");
		test("request5K_soap12.xml", 1, false, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K (buffer 1k) ***");
		test("requestHeader5K_soap11.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K (buffer 1k) ***");
		test("requestHeader5K_soap12.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5K_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 5407,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5K_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 5405,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_5K);

		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (buffer 1k) ***");
		test("requestHeader5KBody5K_soap11.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (buffer 1k) ***");
		test("requestHeader5KBody5K_soap12.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5KBody5K_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 6144,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5KBody5K_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 6144,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_5K);
		
		
		System.out.println("\n\n*** TEST SOAP 11 con prefisso SOAP vuoto (buffer 1k) ***");
		test("requestSoapPrefixEmpty_soap11.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con prefisso SOAP vuoto (buffer 1k) ***");
		test("requestSoapPrefixEmpty_soap12.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con prefisso SOAP vuoto e Header (buffer 1k) ***");
		test("requestSoapPrefixEmptyWithHeader_soap11.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA.replaceAll("soapenv", "SOAP-ENV"));
		
		System.out.println("\n\n*** TEST SOAP 12 con prefisso SOAP vuoto e Header (buffer 1k) ***");
		test("requestSoapPrefixEmptyWithHeader_soap12.xml", 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA.replaceAll("soapenv", "env"));
		
		
		System.out.println("\n\n*** TEST SOAP 11 con prefissi SOAP vari con Header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefix_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_2.replaceAll("soapenv", "hdr"));
		
		System.out.println("\n\n*** TEST SOAP 12 con prefissi SOAP vari con Header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefix_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_2.replaceAll("soapenv", "env"));
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 tutto su una riga con soap body vuoto (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSuUnaRigaSoapBodyVuoto_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 4115,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 12 tutto su una riga con soap body vuoto (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSuUnaRigaSoapBodyVuoto_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 4113,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty1_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 151,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty1_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 149,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty2_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 125,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty2_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 123,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty3_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 164,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty3_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 162,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		
		System.out.println("\n\n*** TEST SOAP 11 con request short senza header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShort_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 301,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short senza header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShort_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 299,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty1_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 337,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty1_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 335,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty2_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 318,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty2_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 316,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty3_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 383,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty3_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 381,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 soapFault (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFault_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 812,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soapFault (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFault_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soapFault con soap prefix empty (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFault_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 812,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soapFault con soap prefix empty (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFault_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soapFault con stack trace (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFaultLong_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soapFault con stack trace (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFaultLong_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 struttura soapFault simile (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 878,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 struttura soapFault simile (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 struttura soapFault simile (empty prefix) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault2_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 830,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 struttura soapFault simile (empty prefix) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault2_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT,	HEADER_SOAP_NON_ATTESO);
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestAllCDATAHeaderBodyEmpty_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 7633,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestAllCDATAHeaderBodyEmpty_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 7631,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con prefissi SOAP vari con Header e CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefixAndCDATA_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_2.replaceAll("soapenv", "hdr"));
		
		System.out.println("\n\n*** TEST SOAP 12 con prefissi SOAP vari con Header e CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefixAndCDATA_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_2.replaceAll("soapenv", "env"));

		
		
		System.out.println("\n\n*** TEST SOAP 11 XmlEntity (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntity_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 12 XmlEntity (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntity_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 11 XmlEntity OpenOnly (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntityOpenOnly_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 12 XmlEntity OpenOnly (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntityOpenOnly_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 11 XmlEntity CloseOnly (buffer "+buffer_dimensione_default+"k) ***");
		try {
			test("requestHeaderBodyXmlEntityCloseOnly_soap11.xml", buffer_dimensione_default, expectedFullBuffer, 3072,
					Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
					ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
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
		test("requestHeaderBodyXmlEntityCloseOnly_soap12.xml", buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 con attachments (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIME_soap11.bin", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIME_soap12.bin", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 11 con attachments (pdf) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEpdf_soap11.bin", buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (pdf) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEpdf_soap12.bin", buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 11 con attachments (bodyEmpty) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEbodyEmpty_soap11.bin", buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (bodyEmpty) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEbodyEmpty_soap12.bin", buffer_dimensione_default, expectedFullBuffer, 1923,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_MIME);

	}
	
	
	private static void test(String fileName, int bufferThresholdKb, boolean expectedFullBuffer, int expectedBufferSize, 
			String namespaceAtteso, boolean isBodyEmptyAtteso, boolean isFaultAtteso, 
			QName expectedRootElement, String expectedHeader) throws Exception {
		
		OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
				HttpConstants.CONTENT_TYPE_SOAP_1_1, 
				TestReader.class.getResourceAsStream("/org/openspcoop2/message/soap/reader/"+fileName), 
				bufferThresholdKb);
		InputStream is = streamReader.read();
		if(!expectedFullBuffer) {
			if(! (is instanceof SequenceInputStream)) {
				throw new Exception("Atteso InputStream 'SequenceInputStream' (partial buffer)");
			}
			int bufferSize = streamReader.getBufferSize();
			if(bufferSize != expectedBufferSize) {
				throw new Exception("Utilizzo del buffer da parte del reader '"+bufferSize+"' differente da quello atteso '"+expectedBufferSize+"'");
			}
		}
		else {
			if(! (is instanceof ByteArrayInputStream)) {
				throw new Exception("Atteso InputStream 'ByteArrayInputStream' (full buffer)");
			}
		}
		System.out.println("BufferSize: "+streamReader.getBufferSize());
		boolean parsingCompleto = streamReader.isParsingComplete();
		System.out.println("ParsingCompleto: "+parsingCompleto);
		String sLetto = Utilities.getAsString(is, Charset.UTF_8.getValue());
		String sAtteso = Utilities.getAsString(TestReader.class.getResourceAsStream("/org/openspcoop2/message/soap/reader/"+fileName), Charset.UTF_8.getValue());
		if(!sLetto.equals(sAtteso)) {
			throw new Exception("Messaggio ottenuto dal reader differente da quello atteso");
		}
		String envelope = streamReader.getEnvelope();
		System.out.println("Envelope: "+envelope);
		String namespace = streamReader.getNamespace();
		System.out.println("Namespace ("+streamReader.getMessageType()+"): "+namespace);
		if(!namespaceAtteso.equals(namespace)) {
			throw new Exception("Namespace ottenuto dal reader '"+namespace+"' differente da quello atteso '"+namespaceAtteso+"'");
		}
		if(isBodyEmptyAtteso || isFaultAtteso || expectedRootElement!=null) {
			if(!parsingCompleto) {
				throw new Exception("Atteso parsing completo");
			}
		}
		boolean isEmpty = streamReader.isEmpty();
		System.out.println("BodyEmpty: "+isEmpty);
		if(isEmpty!=isBodyEmptyAtteso) {
			throw new Exception("Indicazione SOAPBodyEmpty ottenuto dal reader '"+isEmpty+"' differente da quello atteso '"+isBodyEmptyAtteso+"'");
		}
		
		boolean isFault = streamReader.isFault();
		System.out.println("Fault: "+isFault);
		if(isFault!=isFaultAtteso) {
			throw new Exception("Indicazione SOAPFault ottenuto dal reader '"+isFault+"' differente da quello atteso '"+isFaultAtteso+"'");
		}
		
		if(expectedRootElement!=null) {
			String rootElementNamespace = expectedRootElement.getNamespaceURI();
			if(!rootElementNamespace.equals(streamReader.getRootElementNamespace())) {
				throw new Exception("RootElement Namespace ottenuto dal reader '"+streamReader.getRootElementNamespace()+"' differente da quello atteso '"+rootElementNamespace+"'");
			}
			String rootElementLocalName = expectedRootElement.getLocalPart();
			if(!rootElementLocalName.equals(streamReader.getRootElementLocalName())) {
				throw new Exception("RootElement LocalName ottenuto dal reader '"+streamReader.getRootElementLocalName()+"' differente da quello atteso '"+rootElementLocalName+"'");
			}
			System.out.println("RootElement: {"+rootElementNamespace+"}"+rootElementLocalName);
		}
		else {
			if(streamReader.getRootElementNamespace()!=null) {
				throw new Exception("RootElement Namespace presente '"+streamReader.getRootElementNamespace()+"' ma non atteso");
			}
			if(streamReader.getRootElementLocalName()!=null) {
				throw new Exception("RootElement LocalName presente '"+streamReader.getRootElementLocalName()+"' ma non atteso");
			}
		}
		
		SOAPHeader header = streamReader.getHeader();
		if(expectedHeader!=null) {
			if(header==null) {
				throw new Exception("SOAPHeader atteso non trovato");
			}
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory(); 
			String headerString = OpenSPCoop2MessageFactory.getAsString(factory, header, false);
			expectedHeader = MessageType.SOAP_11.equals(streamReader.getMessageType()) ? expectedHeader : expectedHeader.replace(Costanti.SOAP_ENVELOPE_NAMESPACE, Costanti.SOAP12_ENVELOPE_NAMESPACE);
			System.out.println("Ricevuto header: '"+headerString+"'");
			if(!expectedHeader.equals(headerString)) {
				throw new Exception("Header SOAP ricevuto differente da quello atteso: '"+expectedHeader+"'");
			}
			
		}
		else {
			if(header!=null) {
				OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory(); 
				String headerString = OpenSPCoop2MessageFactory.getAsString(factory, header, false);
				System.out.println("Ricevuto header: '"+headerString+"'");
			
				throw new Exception("SOAPHeader non atteso ma trovato");
			}
		}
	}

}
