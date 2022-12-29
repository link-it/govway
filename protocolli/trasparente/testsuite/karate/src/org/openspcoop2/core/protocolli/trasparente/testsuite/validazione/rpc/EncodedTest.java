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
package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.rpc;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;

/**
* EncodedTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class EncodedTest extends ConfigLoader {

	private static final String API_SENZA_VALIDAZIONE = "ServiceRPCEncoded";
	private static final String API_CON_VALIDAZIONE = "ServiceRPCEncodedlValidazioneContenuti";
	
	
	
	
	// ***** RPC TYPE ****
	
	private static String content_rpc_type_soapUI = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:type=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/type-example\">\n"+
			"   <soapenv:Header/>\n"+
			"  <soapenv:Body>\n"+
			"      <type:RPCE-type soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
			"         <nominativo xsi:type=\"typ:nominativoType\" ruolo=\"test\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">test</nominativo>\n"+
			"         <indirizzo xsi:type=\"xsd:string\">test</indirizzo>\n"+
			"         <ora-registrazione xsi:type=\"xsd:dateTime\">2022-12-27T15:22:41.220+01:00</ora-registrazione>\n"+
			"         <idstring xsi:type=\"xsd:string\">test</idstring>\n"+
			"         <idint xsi:type=\"xsd:int\">1</idint>\n"+
			"      </type:RPCE-type>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
	
	@Test
	public void erogazione_rpc_type_soapUI() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_type_soapUI, true, API_SENZA_VALIDAZIONE, "RPCE-type",
				null);
	}
	@Test
	public void fruizione_rpc_type_soapUI() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_type_soapUI, true, API_SENZA_VALIDAZIONE, "RPCE-type",
				null);
	}
	
	@Test
	public void erogazione_rpc_type_soapUI_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_type_soapUI, true, API_CON_VALIDAZIONE, "RPCE-type",
				"Cannot find the declaration of element 'nominativo'");
	}
	@Test
	public void fruizione_rpc_type_soapUI_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_type_soapUI, true, API_CON_VALIDAZIONE, "RPCE-type",
				"Cannot find the declaration of element 'nominativo'");
	}
	
	
	
	
	
	// ***** RPC ELEMENT ****
	
	private static String content_rpc_element_soapUI = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:elem=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/element-example\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <elem:RPCE-element soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
			"         <typ:nominativo xsi:type=\"typ:nominativoType\" ruolo=\"test\">test</typ:nominativo>\n"+
			"         <typ:indirizzo xsi:type=\"typ:indirizzoType\">test</typ:indirizzo>\n"+
			"         <typ:ora-registrazione xsi:type=\"xsd:dateTime\">2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"+
			"      </elem:RPCE-element>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
	
	@Test
	public void erogazione_rpc_element_soapUI() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_element_soapUI, true, API_SENZA_VALIDAZIONE, "RPCE-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_soapUI() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_element_soapUI, true, API_SENZA_VALIDAZIONE, "RPCE-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_soapUI_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_element_soapUI, true, API_CON_VALIDAZIONE, "RPCE-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_soapUI_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_element_soapUI, true, API_CON_VALIDAZIONE, "RPCE-element",
				null);
	}
	
	
	
	// ***** RPC ELEMENT (TEST ROOT ELEMENT) ****
	
	private static String content_rpc_element_rootElementUnqualfied = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <RPCE-element soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
			"         <typ:nominativo xsi:type=\"typ:nominativoType\" ruolo=\"test\">test</typ:nominativo>\n"+
			"         <typ:indirizzo xsi:type=\"typ:indirizzoType\">test</typ:indirizzo>\n"+
			"         <typ:ora-registrazione xsi:type=\"xsd:dateTime\">2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"+
			"      </RPCE-element>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>";
	
	@Test
	public void erogazione_rpc_element_rootElementUnqualfied() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_element_rootElementUnqualfied, true, API_SENZA_VALIDAZIONE, "RPCE-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_rootElementUnqualfied() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_element_rootElementUnqualfied, true, API_SENZA_VALIDAZIONE, "RPCE-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_rootElementUnqualfied_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_element_rootElementUnqualfied, true, API_CON_VALIDAZIONE, "RPCE-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_rootElementUnqualfied_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_element_rootElementUnqualfied, true, API_CON_VALIDAZIONE, "RPCE-element",
				null);
	}
	
	private static String content_rpc_element_rootElementNamespaceDifferente = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:elemDIFFERENTE=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/element-exampleDIFFERENTE\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <elemDIFFERENTE:RPCE-element soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
			"         <typ:nominativo xsi:type=\"typ:nominativoType\" ruolo=\"test\">test</typ:nominativo>\n"+
			"         <typ:indirizzo xsi:type=\"typ:indirizzoType\">test</typ:indirizzo>\n"+
			"         <typ:ora-registrazione xsi:type=\"xsd:dateTime\">2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"+
			"      </elemDIFFERENTE:RPCE-element>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>";
	
	@Test
	public void erogazione_rpc_element_rootElementNamespaceDifferente() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_element_rootElementNamespaceDifferente, true, API_SENZA_VALIDAZIONE, "RPCE-element",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_element_rootElementNamespaceDifferente() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_element_rootElementNamespaceDifferente, true, API_SENZA_VALIDAZIONE, "RPCE-element",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_element_rootElementNamespaceDifferente_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_element_rootElementNamespaceDifferente, true, API_CON_VALIDAZIONE, "RPCE-element",
				"RPCE-element", // riconosco l'azione tramite la url 
				"Invalid rpc request element 'RPCE-element' by WSDL specification 'gw/ENTE:RPCEncodedTest:1' (port-type:ServiceRPCEncoded, operation:RPCE-element): expected namespace 'http://openspcoop2.org/ValidazioneContenutiWS/Service/element-example'; found namespace 'http://openspcoop2.org/ValidazioneContenutiWS/Service/element-exampleDIFFERENTE'");
	}
	@Test
	public void fruizione_rpc_element_rootElementNamespaceDifferente_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_element_rootElementNamespaceDifferente, true, API_CON_VALIDAZIONE, "RPCE-element",
				"RPCE-element", // riconosco l'azione tramite la url 
				"Invalid rpc request element 'RPCE-element' by WSDL specification 'gw/ENTE:RPCEncodedTest:1' (port-type:ServiceRPCEncoded, operation:RPCE-element): expected namespace 'http://openspcoop2.org/ValidazioneContenutiWS/Service/element-example'; found namespace 'http://openspcoop2.org/ValidazioneContenutiWS/Service/element-exampleDIFFERENTE'");
	}
	
	
	
	
	
	
	// ***** RPC XSI TYPE ****
		
	private static String content_rpc_xsitype_soapUI = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:type=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/type-example\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <type:RPCE-xsitype soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"+
			"         <richiestaEsempioXSI xsi:type=\"typ:FixedMessaggioType\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"            <dati>test</dati>\n"+
			"         </richiestaEsempioXSI>\n"+
			"      </type:RPCE-xsitype>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
		
	@Test
	public void erogazione_rpc_xsitype_soapUI() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_xsitype_soapUI, true, API_SENZA_VALIDAZIONE, "RPCE-xsitype",
				null);
	}
	@Test
	public void fruizione_rpc_xsitype_soapUI() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_xsitype_soapUI, true, API_SENZA_VALIDAZIONE, "RPCE-xsitype",
				null);
	}
	
	@Test
	public void erogazione_rpc_xsitype_soapUI_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.EROGAZIONE, content_rpc_xsitype_soapUI, true, API_CON_VALIDAZIONE, "RPCE-xsitype",
				"Cannot find the declaration of element 'richiestaEsempioXSI'.");
	}
	@Test
	public void fruizione_rpc_xsitype_soapUI_validazioneGovWay() throws Exception {
		LiteralTest._testNoWaitServer(TipoServizio.FRUIZIONE, content_rpc_xsitype_soapUI, true, API_CON_VALIDAZIONE, "RPCE-xsitype",
				"Cannot find the declaration of element 'richiestaEsempioXSI'.");
	}
	
	
	
}
