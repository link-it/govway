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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.protocollo;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* Soap2RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class Soap2RestTest extends ConfigLoader {

	@Test
	public void ok_soap11_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "ok_vardatori.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	@Test
	public void ok_soap12_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "ok_vardatori.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	
	
	@Test
	public void ko_soap11_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "ko_vardatori.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ko, Utilities.descr_ko_xml, response);
	}
	@Test
	public void ko_soap12_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "ko_vardatori.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ko, Utilities.descr_ko_xml, response);
	}
	
	
	
	@Test
	public void emailError_soap11_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "email_error_vardatori.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ko, Utilities.descr_emailError_xml, response);
	}
	@Test
	public void emailError_soap12_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "email_error_vardatori.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ko, Utilities.descr_emailError_xml, response);
	}
	
	
	
	@Test
	public void serverError_soap11_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "server_error.json", HttpConstants.CONTENT_TYPE_JSON, 500,
				"Ricevuto un SOAPFault in seguito all'invio della busta di cooperazione: %<faultcode>soapenv:"+Utilities.fault_code_server+"</faultcode>%<faultstring>"+Utilities.fault_string_server+"</faultstring>%");
		Utilities.verificaFault(true, Utilities.fault_code_server_soap, Utilities.fault_string_server, response);
	}
	@Test
	public void serverError_soap12_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "server_error.json", HttpConstants.CONTENT_TYPE_JSON, 500,
				"Ricevuto un SOAPFault in seguito all'invio della busta di cooperazione: %<env:Value xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">soapenv:"+Utilities.fault_code_server+"</env:Value>%<env:Text xml:lang=\"en-US\">"+Utilities.fault_string_server+"</env:Text>%");
		Utilities.verificaFault(false, Utilities.fault_code_server_soap, Utilities.fault_string_server, response);
	}
	
	
	
	@Test
	public void clientError_soap11_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "client_error.json", HttpConstants.CONTENT_TYPE_JSON, 500,
				"Ricevuto un SOAPFault in seguito all'invio della busta di cooperazione: %<faultcode>soapenv:"+Utilities.fault_code_client+"</faultcode>%<faultstring>"+Utilities.fault_string_client+"</faultstring>%");
		Utilities.verificaFault(true, Utilities.fault_code_client_soap, Utilities.fault_string_client, response);
	}
	@Test
	public void clientError_soap12_vardatori() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/varDatori_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Vardatori", "client_error.json", HttpConstants.CONTENT_TYPE_JSON, 500,
				"Ricevuto un SOAPFault in seguito all'invio della busta di cooperazione: %<env:Value xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">soapenv:"+Utilities.fault_code_client+"</env:Value>%<env:Text xml:lang=\"en-US\">"+Utilities.fault_string_client+"</env:Text>%");
		Utilities.verificaFault(false, Utilities.fault_code_client_soap, Utilities.fault_string_client, response);
	}
	
	
	
	
	@Test
	public void ok_soap11_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/uniLav.xml"),
				"TrasformazioneSoap2Rest","", "Unilav", "ok_unilav.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	@Test
	public void ok_soap12_unilav() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/uniLav_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Unilav", "ok_unilav.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	
	
	
	@Test
	public void ok_soap11_uniurg() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/uniUrg.xml"),
				"TrasformazioneSoap2Rest","", "Uniurg", "ok_uniurg.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	@Test
	public void ok_soap12_uniurg() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/uniUrg_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Uniurg", "ok_uniurg.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	
	
	
	
	@Test
	public void ok_soap11_unisomm() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_1, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/uniSomm.xml"),
				"TrasformazioneSoap2Rest","", "Unisomm", "ok_unisomm.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
	@Test
	public void ok_soap12_unisomm() throws Exception {
		HttpResponse response = Utilities._test(TipoServizio.FRUIZIONE,HttpConstants.CONTENT_TYPE_SOAP_1_2, FileSystemUtilities.readBytesFromFile("/etc/govway/test_trasformazioni/richiesteSoap/uniSomm_soap12.xml"),
				"TrasformazioneSoap2Rest","", "Unisomm", "ok_unisomm.json", HttpConstants.CONTENT_TYPE_JSON, 200,
				null);
		Utilities.verificaXmlOk(Utilities.tipo_ok, Utilities.descr_ok, response);
	}
}
