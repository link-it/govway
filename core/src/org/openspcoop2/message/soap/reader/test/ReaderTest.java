/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.soap.reader.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * TestReader
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReaderTest {

	private static final String HEADER_5K = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"    <!-- TEST -->\n"+
		"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>80300000000000000000000000000000000000</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>50000000000000000000000000000000000000</emulov></redro>\n"+
		"</xsd:skcotSyub>\n"+
		"</soapenv:Header>";
	private static final String HEADER_5K_UNICA_RIGA =  HEADER_5K.replaceAll("\n", "").replace("    <!-- TEST -->", "");
	
	private static final String HEADER_WSA = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n"+
		"    <wsa:MessageID>\n"+
		"      uuid:6B29FC40-CA47-1067-B31D-00DD010662DA\n"+
		"    </wsa:MessageID>\n"+
		"    <wsa:ReplyTo>\n"+
		"      <wsa:Address>http://business456.example/client1</wsa:Address>\n"+
		"    </wsa:ReplyTo>\n"+
		"    <wsa:To>http://fabrikam123.example/Purchasing</wsa:To>\n"+
		"    <wsa:Action>http://fabrikam123.example/SubmitPO</wsa:Action>\n"+
		"   </soapenv:Header>";

	private static final String HEADER_WSA_2 = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"    <wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n"+
		"      uuid:6B29FC40-CA47-1067-B31D-00DD010662DA\n"+
		"    </wsa:MessageID>\n"+
		"    <wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n"+
		"      <wsa:Address>http://business456.example/client1</wsa:Address>\n"+
		"    </wsa:ReplyTo>\n"+
		"    <wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://fabrikam123.example/Purchasing</wsa:To>\n"+
		"    <wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://fabrikam123.example/SubmitPO</wsa:Action>\n"+
		"   </soapenv:Header>";	
	
	private static final String HEADER_WSA_CDATA = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"    <wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n"+
		"      uuid:6B29FC40-CA47-1067-B31D-00DD010662DA\n"+
		"    </wsa:MessageID><![CDATA[\n"+
		"<xsd:skcotSyub>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"\n"+
		"</xsd:skcotSyub>]]>\n"+
		"    <wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">\n"+
		"      <wsa:Address>http://business456.example/client1</wsa:Address>\n"+
		"    </wsa:ReplyTo>\n"+
		"    <wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://fabrikam123.example/Purchasing</wsa:To>\n"+
		"    <wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://fabrikam123.example/SubmitPO</wsa:Action><![CDATA[\n"+
		"<xsd:skcotSyub>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"\n"+
		"</xsd:skcotSyub>]]>\n"+
		"   </soapenv:Header>";

	private static final String HEADER_MIME = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:prova=\"http://prova.openspcoop2.org\">\n"+
		"        <a:example1 xmlns:a=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\">prova</a:example1>\n"+
		"        <b:example2 xmlns:b=\"http://www.openspcoop2.org\" soapenv:actor=\"http://www.prova.it\" soapenv:mustUnderstand=\"0\">prova2</b:example2>\n"+
		"        </soapenv:Header>";
	
	private static final String HEADER_XMLENTITY = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"    <!-- TEST -->\n"+
		"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">\n"+
		"&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;IBM&lt;/lobmys&gt;&lt;DIreyub&gt;asankha&lt;/DIreyub&gt;&lt;ecirp&gt;140.34&lt;/ecirp&gt;&lt;emulov&gt;2000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;MSFT&lt;/lobmys&gt;&lt;DIreyub&gt;ruwan&lt;/DIreyub&gt;&lt;ecirp&gt;23.56&lt;/ecirp&gt;&lt;emulov&gt;8030&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;SUN&lt;/lobmys&gt;&lt;DIreyub&gt;indika&lt;/DIreyub&gt;&lt;ecirp&gt;14.56&lt;/ecirp&gt;&lt;emulov&gt;500&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"&lt;redro&gt;&lt;lobmys&gt;GOOG&lt;/lobmys&gt;&lt;DIreyub&gt;chathura&lt;/DIreyub&gt;&lt;ecirp&gt;60.24&lt;/ecirp&gt;&lt;emulov&gt;40000&lt;/emulov&gt;&lt;/redro&gt;\n"+
		"</xsd:skcotSyub>\n"+
		"</soapenv:Header>";
	
	private static final String HEADER_CARATTERI_ACAPO_TAB = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"AltroperTest\" xmlns:xsd=\"http://services.samples/xsd\" xmlns:xsd2=\"http://services.samples/xsd\">\n"+
		"   <xsd:skcotSyub>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"</xsd:skcotSyub>\n"+
		"</soapenv:Header>";
	
	private static final QName ROOT_ELEMENT_SKCOT = new QName("http://services.samples/xsd", "skcotSyub", "xsd");
	private static final QName ROOT_ELEMENT_SKCOT_EMPTY_PREFIX = new QName("http://services.samples/xsd", "skcotSyub");
	private static final QName ROOT_ELEMENT_SKCOT_NS2_PREFIX = new QName("http://services.samples/xsd", "skcotSyub", "ns2");
	private static final QName ROOT_ELEMENT_FAULT_11 = new QName("http://services.samples/xsd", "Fault", "SOAP-ENV-SIMILE");
	private static final QName ROOT_ELEMENT_FAULT_12 = new QName("http://services.samples/xsd", "Fault", "env-simile");
	private static final QName ROOT_ELEMENT_FAULT_EMPTY_PREFIX = new QName("http://services.samples/xsd", "Fault");
	private static final QName ROOT_ELEMENT_BODY_NOPOLICY = new QName("http://services.samples/xsd", "BodyNoPolicy", "ns2");
	private static final QName ROOT_ELEMENT_BODY_EXACT = new QName("http://services.samples/xsd", "Body", "ns2");
	
	private static final String HEADER_NOPOLICY = "<soap:Header xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"		        <ns2:HeaderNoPolicy xmlns:ns2=\"http://services.samples/xsd\">\n"+
		"		        	<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\"/>\n"+
		"                </ns2:HeaderNoPolicy>\n"+
		"		</soap:Header>";
	
	private static final String HEADER_EXACT = "<soap:Header xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
	    "		        <ns2:Header xmlns:ns2=\"http://services.samples/xsd\">\n"+
	    "		        	<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\"/>\n"+
	    "                </ns2:Header>\n"+
		"		</soap:Header>";
	
	private static final String HEADER_VARI_COMMENTI = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"   \n"+
		"   <!-- Commento Iniziale -->\n"+
		"\n"+
		"<!--\n"+
		"     <jejdkeke> Commento Iniziale \n"+
		"     \n"+
		"     dz<de>>>>>deded de\n"+
		"     de- de-de d\n"+
		"     -->\n"+
		"   \n"+
		"   \n"+
		"    <!-- TEST -->\n"+
		"<xsd:skcotSyub xmlns:xsd=\"http://services.samples/xsd\">\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"\n"+
		"<!-- Commento Iniziale -->\n"+
		"\n"+
		"<!--\n"+
		"     <jejdkeke> Commento Iniziale \n"+
		"     \n"+
		"     dz<de>>>>>deded de\n"+
		"     de- de-de d\n"+
		"     -->\n"+
		"\n"+
		"\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"\n"+
		"\n"+
		"<!-- Commento Iniziale -->\n"+
		"\n"+
		"<!--\n"+
		"     <jejdkeke> Commento Iniziale \n"+
		"     \n"+
		"     dz<de>>>>>deded de\n"+
		"     de- de-de d\n"+
		"     -->\n"+
		"\n"+
		"\n"+
		"</xsd:skcotSyub>\n"+
		"\n"+
		"<!-- Commento Iniziale -->\n"+
		"\n"+
		"<!--\n"+
		"     <jejdkeke> Commento Iniziale \n"+
		"     \n"+
		"     dz<de>>>>>deded de\n"+
		"     de- de-de d\n"+
		"     -->\n"+
		"\n"+
		"\n"+
		"</soapenv:Header>";
	
	private static final String HEADER_SOAP_VUOTO = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"</soapenv:Header>";

	private static final String HEADER_SOAP_VUOTO_CON_COMMENTI = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><!--  Qualche commento -->\n"+
		"<!--  ulteriore -->\n"+
		"</soapenv:Header>";
	
	private static final String HEADER_SOAP_VUOTO_CON_CDATA = "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
		"    <!-- TEST -->\n"+
		"<![CDATA[<xsd:skcotSyub>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"<redro><lobmys>GOOG</lobmys><DIreyub>chathura</DIreyub><ecirp>60.24</ecirp><emulov>40000</emulov></redro>\n"+
		"<redro><lobmys>IBM</lobmys><DIreyub>asankha</DIreyub><ecirp>140.34</ecirp><emulov>2000</emulov></redro>\n"+
		"<redro><lobmys>MSFT</lobmys><DIreyub>ruwan</DIreyub><ecirp>23.56</ecirp><emulov>8030</emulov></redro>\n"+
		"<redro><lobmys>SUN</lobmys><DIreyub>indika</DIreyub><ecirp>14.56</ecirp><emulov>500</emulov></redro>\n"+
		"\n"+
		"</xsd:skcotSyub>]]>\n"+
		"</soapenv:Header>";
	
	public static void main(String [] args) throws Exception{
		test();
	}
	public static void test() throws Exception{
		
		
		boolean expectedFullBuffer = false; // si esce prima.
		boolean isBodyEmptyAtteso = true;
		boolean isFaultAtteso = true;
		
		int buffer_dimensione_default = 10; // tutti i messaggi di test sono tarati su questa dimensione
		
		String HEADER_SOAP_NON_ATTESO = null;
		QName ROOT_ELEMENT_NON_ATTESO = null;
		
		String contentTypeSoap11 = HttpConstants.CONTENT_TYPE_SOAP_1_1+"; charset=UTF-8";
		String contentTypeSoap12 = HttpConstants.CONTENT_TYPE_SOAP_1_2+"; charset=UTF-8";
		String _prefixContentTypeWithAttachments = HttpConstants.CONTENT_TYPE_MULTIPART_RELATED+";   boundary=\"----=_Part_0_6330713.1171639717331\";   type=\"";
		String contentTypeSoap11WithAttachments = _prefixContentTypeWithAttachments+HttpConstants.CONTENT_TYPE_SOAP_1_1+"\"";
		String contentTypeSoap12WithAttachments = _prefixContentTypeWithAttachments+HttpConstants.CONTENT_TYPE_SOAP_1_2+"\"";
		
		
		System.out.println("\n\n*** TEST SOAP 11 da 5K (buffer 1k) ***");
		test("request5K_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 da 5K (buffer 1k) ***");
		test("request5K_soap12.xml", contentTypeSoap12, 1, false, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 da 5K (unica riga) (buffer 1k) ***");
		test("request5K_unicaRiga_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 da 5K (unica riga) (buffer 1k) ***");
		test("request5K_unicaRiga_soap12.xml", contentTypeSoap12, 1, false, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K (buffer 1k) ***");
		test("requestHeader5K_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K (buffer 1k) ***");
		test("requestHeader5K_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5K_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 5407,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5K_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 5405,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K (unica riga) (buffer 1k) ***");
		test("requestHeader5K_unicaRiga_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K (unica riga) (buffer 1k) ***");
		test("requestHeader5K_unicaRiga_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5K_unicaRiga_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 5120,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_5K_UNICA_RIGA);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5K_unicaRiga_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 5120,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_5K_UNICA_RIGA);

		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (buffer 1k) ***");
		test("requestHeader5KBody5K_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (buffer 1k) ***");
		test("requestHeader5KBody5K_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5KBody5K_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 6144,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5KBody5K_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 6144,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_5K);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (unica riga) (buffer 1k) ***");
		test("requestHeader5KBody5K_unicaRiga_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (unica riga) (buffer 1k) ***");
		test("requestHeader5KBody5K_unicaRiga_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con Header 5K e Body 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5KBody5K_unicaRiga_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 5120,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_5K_UNICA_RIGA);
		
		System.out.println("\n\n*** TEST SOAP 12 con Header 5K e Body 5K (unica riga) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeader5KBody5K_unicaRiga_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 5120,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_5K_UNICA_RIGA);
		
		
		System.out.println("\n\n*** TEST SOAP 11 con prefisso SOAP vuoto (buffer 1k) ***");
		test("requestSoapPrefixEmpty_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con prefisso SOAP vuoto (buffer 1k) ***");
		test("requestSoapPrefixEmpty_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con prefisso SOAP vuoto e Header (buffer 1k) ***");
		test("requestSoapPrefixEmptyWithHeader_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA.replaceAll("soapenv:", "").replaceAll(":soapenv", ""));
		
		System.out.println("\n\n*** TEST SOAP 12 con prefisso SOAP vuoto e Header (buffer 1k) ***");
		test("requestSoapPrefixEmptyWithHeader_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA.replaceAll("soapenv:", "").replaceAll(":soapenv", ""));
		
		
		System.out.println("\n\n*** TEST SOAP 11 con prefissi SOAP vari con Header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefix_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_2.replaceAll("soapenv", "hdr"));
		
		System.out.println("\n\n*** TEST SOAP 12 con prefissi SOAP vari con Header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefix_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_2.replaceAll("soapenv:", "").replaceAll(":soapenv", ""));
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 tutto su una riga con soap body vuoto (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSuUnaRigaSoapBodyVuoto_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 5086,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_5K_UNICA_RIGA);
		
		System.out.println("\n\n*** TEST SOAP 12 tutto su una riga con soap body vuoto (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSuUnaRigaSoapBodyVuoto_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 5084,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_5K_UNICA_RIGA);
		
		System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty1_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 151,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty1_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 149,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty2_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 125,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty2_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 123,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soap body vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty3_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 164,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soap body vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestEmpty3_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 162,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		
		System.out.println("\n\n*** TEST SOAP 11 con request short senza header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShort_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 301,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short senza header (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShort_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 299,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty1_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 337,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_VUOTO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso1) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty1_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 335,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_VUOTO);
		
		System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty2_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 318,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso2) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty2_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 316,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 con request short con header vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty3_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 383,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_VUOTO_CON_COMMENTI);
		
		System.out.println("\n\n*** TEST SOAP 12 con request short con header vuoto (caso3) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestShortHeaderEmpty3_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 381,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_VUOTO_CON_COMMENTI);
		
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 soapFault (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFault_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 812,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soapFault (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFault_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soapFault con soap prefix empty (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFaultSoapPrefixEmpty_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 776,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soapFault con soap prefix empty (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFaultSoapPrefixEmpty_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 950,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 soapFault con stack trace (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFaultLong_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 soapFault con stack trace (buffer "+buffer_dimensione_default+"k) ***");
		test("soapFaultLong_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 struttura soapFault simile (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 878,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT_11,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 struttura soapFault simile (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT_12,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 struttura soapFault simile (empty prefix) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault2_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 830,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT_EMPTY_PREFIX,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 struttura soapFault simile (empty prefix) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSimileSoapFault2_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_FAULT_EMPTY_PREFIX,	HEADER_SOAP_NON_ATTESO);
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestAllCDATAHeaderBodyEmpty_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 7633,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_VUOTO_CON_CDATA);
		
		System.out.println("\n\n*** TEST SOAP 12 CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestAllCDATAHeaderBodyEmpty_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 7631,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_VUOTO_CON_CDATA);
		
		System.out.println("\n\n*** TEST SOAP 11 con prefissi SOAP vari con Header e CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefixAndCDATA_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_CDATA.replaceAll("soapenv", "hdr"));
		
		System.out.println("\n\n*** TEST SOAP 12 con prefissi SOAP vari con Header e CDATA (buffer "+buffer_dimensione_default+"k) ***");
		test("requestMixedPrefixAndCDATA_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_WSA_CDATA.replaceAll("soapenv:", "").replaceAll(":soapenv", ""));

		
		
		System.out.println("\n\n*** TEST SOAP 11 XmlEntity (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntity_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 12 XmlEntity (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntity_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 11 XmlEntity OpenOnly (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntityOpenOnly_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 12 XmlEntity OpenOnly (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyXmlEntityOpenOnly_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		System.out.println("\n\n*** TEST SOAP 11 XmlEntity CloseOnly (buffer "+buffer_dimensione_default+"k) ***");
		try {
			test("requestHeaderBodyXmlEntityCloseOnly_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 3072,
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
		test("requestHeaderBodyXmlEntityCloseOnly_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 3072,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_XMLENTITY);
		
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 con attachments (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIME_soap11.bin", contentTypeSoap11WithAttachments, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_EMPTY_PREFIX, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIME_soap12.bin", contentTypeSoap12WithAttachments, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_EMPTY_PREFIX, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 11 con attachments (pdf) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEpdf_soap11.bin", contentTypeSoap11WithAttachments, buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_EMPTY_PREFIX, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (pdf) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEpdf_soap12.bin", contentTypeSoap12WithAttachments, buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_EMPTY_PREFIX, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 11 con attachments (bodyEmpty) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEbodyEmpty_soap11.bin", contentTypeSoap11WithAttachments, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (bodyEmpty) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSOAPMultipartRelatedMIMEbodyEmpty_soap12.bin", contentTypeSoap12WithAttachments, buffer_dimensione_default, expectedFullBuffer, 1935,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_NON_ATTESO, HEADER_MIME);

		

		
		System.out.println("\n\n*** TEST SOAP 11 '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestACapo_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 972,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 12 '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestACapo_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 972,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);

		System.out.println("\n\n*** TEST SOAP 11 '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestTab_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 973,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 12 '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestTab_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 954,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 11 rootElement '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestRootElementACapo_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 666,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 rootElement '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestRootElementACapo_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 664,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 rootElement '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestRootElementTab_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 671,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 rootElement '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestRootElementTab_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 669,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 header '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderACapo_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 953,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 12 header '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderACapo_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 951,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 11 header '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderTab_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 957,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 12 header '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderTab_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 954,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_CARATTERI_ACAPO_TAB);
		
		System.out.println("\n\n*** TEST SOAP 11 fault '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSoapFaultACapo_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 986,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 fault '\\n' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSoapFaultACapo_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 fault '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSoapFaultTab_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 987,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 fault '\\t' (buffer "+buffer_dimensione_default+"k) ***");
		test("requestSoapFaultTab_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		
		// Questo tipo di errore avviene solamente se il carattere > e' iniziale prima di qualsiasi <
		// I test con attachments servono appunto a non gestire questo aspetto poiche' poiche' il carattere > potrebbe essere nel mime
		// se cmq si attiva la validazione, verrà individuato l'errore
		System.out.println("\n\n*** TEST SOAP 11 elemento > senza < (buffer "+buffer_dimensione_default+"k) ***");
		try {
			test("requestMalformed_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 316,
					Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
					ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
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
			test("requestMalformed_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 316,
					Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
					ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
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
		test("contentIdMalformedSOAPMultipartRelatedMIME_soap11.bin", contentTypeSoap11WithAttachments, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_EMPTY_PREFIX, HEADER_MIME);
		
		System.out.println("\n\n*** TEST SOAP 12 con attachments (carattere > prima di <) (buffer "+buffer_dimensione_default+"k) ***");
		test("contentIdMalformedSOAPMultipartRelatedMIME_soap12.bin", contentTypeSoap12WithAttachments, buffer_dimensione_default, expectedFullBuffer, 1024,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_EMPTY_PREFIX, HEADER_MIME);
		
		
		System.out.println("\n\n*** TEST SOAP 11 elemento body non corretto (buffer "+buffer_dimensione_default+"k) ***");
		try {
			test("requestBodyMalformed_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 262,
					Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
					ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
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
			test("requestBodyMalformed_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 262,
					Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
					ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
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
		test("requestBodyMalformed2_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 262,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);

		System.out.println("\n\n*** TEST SOAP 12 elemento body non corretto (test2: chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestBodyMalformed2_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 220,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);

		System.out.println("\n\n*** TEST SOAP 11 elemento body non corretto (test3: /chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestBodyMalformed3_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 263,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 elemento body non corretto (test3: /chiusura senza apertura ammessa in xml) (buffer "+buffer_dimensione_default+"k) ***");
		test("requestBodyMalformed3_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 221,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso,
				ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 11 elemento header non corretto (buffer "+buffer_dimensione_default+"k) ***");
		try {
			test("requestHeaderMalformed_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 282,
					Costanti.SOAP_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
					ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
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
			test("requestHeaderMalformed_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 240,
					Costanti.SOAP12_ENVELOPE_NAMESPACE, isBodyEmptyAtteso, !isFaultAtteso,
					ROOT_ELEMENT_NON_ATTESO, HEADER_SOAP_NON_ATTESO);
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
		test("requestFirstChildNameBody_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 240,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_BODY_NOPOLICY, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con BodyChildName (buffer 1k) ***");
		test("requestFirstChildNameBody_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 238,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_BODY_NOPOLICY, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con BodyChildNameExact (buffer 1k) ***");
		test("requestFirstChildNameExactBody_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 224,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_BODY_EXACT, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con BodyChildNameExact (buffer 1k) ***");
		test("requestFirstChildNameExactBody_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 222,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_BODY_EXACT, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con HeaderChildName (buffer 1k) ***");
		test("requestFirstChildNameHeader_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 444,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_NOPOLICY);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con HeaderChildName (buffer 1k) ***");
		test("requestFirstChildNameHeader_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 442,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_NOPOLICY);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con HeaderChildNameExact (buffer 1k) ***");
		test("requestFirstChildNameExactHeader_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 428,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_EXACT);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con HeaderChildNameExact (buffer 1k) ***");
		test("requestFirstChildNameExactHeader_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 426,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_EXACT);
		
		
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 11 con XmlDeclaration a capo (buffer 1k) ***");
		test("requestXmlDeclarationACapo_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 485,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_EXACT);
		
		System.out.println("\n\n*** TEST SPECIAL CASE SOAP 12 con XmlDeclaration a capo (buffer 1k) ***");
		test("requestXmlDeclarationACapo_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 483,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_EXACT);
		
		
		
		System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP12 , e messaggio SOAP 11 (buffer 1k) ***");
		test("requestSoap12Commentata_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 319,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP11 , e messaggio SOAP 12 (buffer 1k) ***");
		test("requestSoap11Commentata_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 314,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP12 (ACapo) , e messaggio SOAP 11 (buffer 1k) ***");
		test("requestSoap12CommentataACapo_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 331,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST DichiarazioneCommentata SOAP11 (ACapo) , e messaggio SOAP 12 (buffer 1k) ***");
		test("requestSoap11CommentataACapo_soap12.xml", contentTypeSoap12, 1, expectedFullBuffer, 327,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT_NS2_PREFIX, HEADER_SOAP_NON_ATTESO);
	
		
		
		System.out.println("\n\n*** TEST SOAP11 con vari commenti (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyVariCommenti_soap11.xml", contentTypeSoap11, buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_VARI_COMMENTI);
		
		System.out.println("\n\n*** TEST SOAP12 con vari commenti (buffer "+buffer_dimensione_default+"k) ***");
		test("requestHeaderBodyVariCommenti_soap12.xml", contentTypeSoap12, buffer_dimensione_default, expectedFullBuffer, 2048,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT, HEADER_VARI_COMMENTI);
				
		
		
		
		System.out.println("\n\n*** TEST SOAP 11 con returnLine (buffer 1k) ***");
		test("requestEnvelope_returnLine_soap11.xml", contentTypeSoap11, 1, expectedFullBuffer, 463,
				Costanti.SOAP_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		System.out.println("\n\n*** TEST SOAP 12 con returnLine (buffer 1k) ***");
		test("requestEnvelope_returnLine_soap12.xml", contentTypeSoap11, 1, expectedFullBuffer, 461,
				Costanti.SOAP12_ENVELOPE_NAMESPACE, !isBodyEmptyAtteso, !isFaultAtteso, 
				ROOT_ELEMENT_SKCOT,	HEADER_SOAP_NON_ATTESO);
		
		
		System.out.println("\n\nTestsuite completata con successo");
	}
	
	
	private static void test(String fileName, String contentType, int bufferThresholdKb, boolean expectedFullBuffer, int expectedBufferSize, 
			String namespaceAtteso, boolean isBodyEmptyAtteso, boolean isFaultAtteso, 
			QName expectedRootElement, String expectedHeader) throws Exception {
		
		OpenSPCoop2MessageSoapStreamReader streamReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),
				contentType, 
				ReaderTest.class.getResourceAsStream("/org/openspcoop2/message/soap/reader/test/"+fileName), 
				bufferThresholdKb);
		InputStream is = null;
		try {
			streamReader.read();
			streamReader.checkException();
		}finally {
			// anche in caso di eccezione devo cmq aggiornare is
			is = streamReader.getBufferedInputStream();
		}
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
		String sAtteso = Utilities.getAsString(ReaderTest.class.getResourceAsStream("/org/openspcoop2/message/soap/reader/test/"+fileName), Charset.UTF_8.getValue());
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
			String rootElementPrefix = expectedRootElement.getPrefix();
			if(!rootElementPrefix.equals(streamReader.getRootElementPrefix())) {
				throw new Exception("RootElement Prefix ottenuto dal reader '"+streamReader.getRootElementPrefix()+"' differente da quello atteso '"+rootElementPrefix+"'");
			}
			System.out.println("RootElement (prefix:"+rootElementPrefix+"): {"+rootElementNamespace+"}"+rootElementLocalName);
		}
		else {
			if(streamReader.getRootElementNamespace()!=null) {
				throw new Exception("RootElement Namespace presente '"+streamReader.getRootElementNamespace()+"' ma non atteso");
			}
			if(streamReader.getRootElementLocalName()!=null) {
				throw new Exception("RootElement LocalName presente '"+streamReader.getRootElementLocalName()+"' ma non atteso");
			}
			if(streamReader.getRootElementPrefix()!=null) {
				throw new Exception("RootElement Prefix presente '"+streamReader.getRootElementPrefix()+"' ma non atteso");
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
