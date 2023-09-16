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
package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* LiteralTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class LiteralTest extends ConfigLoader {

	private static final String API_SENZA_VALIDAZIONE = "ServiceRPCLiteral";
	private static final String API_CON_VALIDAZIONE = "ServiceRPCLiteralValidazioneContenuti";
	private static final String API_SOAP_NAMESPACE_RIDEFINITO = "ServiceRPCLiteralSoapNamespaceRidefinito";
	
	private static boolean CLIENT_VALIDATION = true;
	private static boolean SERVER_VALIDATION = true;
	
	private static RPCServerThread serverValidazioneAbilitata;
	private static RPCServerThread serverValidazioneDisabilitata;
	private static RPCServerThread serverValidazioneSoapNamespaceRidefinito;
	
	@BeforeClass
	public static void startServer()throws Exception {
		serverValidazioneAbilitata = new RPCServerThread(8888, true, false);
		serverValidazioneAbilitata.start();
		serverValidazioneDisabilitata = new RPCServerThread(8889, false, false);
		serverValidazioneDisabilitata.start();	
		serverValidazioneSoapNamespaceRidefinito = new RPCServerThread(8887, true, true);
		serverValidazioneSoapNamespaceRidefinito.start();	
	}
	
	private static void waitStartServer() {
		int index = 0;
		while(index<60) {
			boolean ok = true;
			if(serverValidazioneAbilitata==null) {
				ok = false;
			}
			if(!serverValidazioneAbilitata.isInitialized()) {
				ok = false;
			}
			if(serverValidazioneDisabilitata==null) {
				ok = false;
			}
			if(!serverValidazioneDisabilitata.isInitialized()) {
				ok = false;
			}
			if(serverValidazioneSoapNamespaceRidefinito==null) {
				ok = false;
			}
			if(!serverValidazioneSoapNamespaceRidefinito.isInitialized()) {
				ok = false;
			}
			if(ok) {
				break;
			}
			else {
				Utilities.sleep(1000);
				index++;
			}
		}
	}
	
	@AfterClass
	public static void stopServer()throws Exception {
		if(serverValidazioneAbilitata!=null){
			serverValidazioneAbilitata.setStop(true);
			while(serverValidazioneAbilitata.isFinished()==false){
				try{
					Thread.sleep(1000);		
				}catch(Exception e){}
			}
		}
		if(serverValidazioneDisabilitata!=null){
			serverValidazioneDisabilitata.setStop(true);
			while(serverValidazioneDisabilitata.isFinished()==false){
				try{
					Thread.sleep(1000);		
				}catch(Exception e){}
			}
		}
	}
	
	
	// ***** RPC TYPE ****
	
	@Test
	public void erogazione_rpc_type_messaggiCorrettoPerViaValidazioneSchemiAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				null);
	}
	@Test
	public void fruizione_rpc_type_messaggiCorrettoPerViaValidazioneSchemiAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				null);
	}
	
	@Test
	public void erogazione_rpc_type_messaggiNonCorrettiPerViaValidazioneSchemiDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, !CLIENT_VALIDATION, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				null);
	}
	@Test
	public void fruizione_rpc_type_messaggiNonCorrettiPerViaValidazioneSchemiDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, !CLIENT_VALIDATION, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				null);
	}
	
	private static String content_rpc_type_soapUI = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://openspcoop2.org/ValidazioneContenutiWS/Service\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <ser:RPCL-type>\n"+
			"         <nominativo ruolo=\"test\">test</nominativo>\n"+
			"         <indirizzo>test</indirizzo>\n"+
			"         <ora-registrazione>2022-12-27T15:22:41.220+01:00</ora-registrazione>\n"+
			"         <idstring>3</idstring>\n"+
			"         <idint>3</idint>\n"+
			"      </ser:RPCL-type>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
	
	@Test
	public void erogazione_rpc_type_soapUI() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_type_soapUI, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				null);
	}
	@Test
	public void fruizione_rpc_type_soapUI() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_type_soapUI, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				null);
	}
	
	@Test
	public void erogazione_rpc_type_messaggiCorrettoPerViaValidazioneSchemiAbilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				null);
	}
	@Test
	public void fruizione_rpc_type_messaggiCorrettoPerViaValidazioneSchemiAbilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				null);
	}
	
	@Test
	public void erogazione_rpc_type_messaggioRichiestaNonCorrettoPerViaValidazioneSchemiClientDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, !CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				"Required input parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}int]idint' undefined in 5 body root-element(s) founded: {null}nominativo,{null}indirizzo,{null}ora-registrazione,{null}idstring,{null}idint");
	}
	@Test
	public void fruizione_rpc_type_messaggioRichiestaNonCorrettoPerViaValidazioneSchemiClientDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, !CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				"Required input parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}int]idint' undefined in 5 body root-element(s) founded: {null}nominativo,{null}indirizzo,{null}ora-registrazione,{null}idstring,{null}idint");
	}
	
	@Test
	public void erogazione_rpc_type_messaggioRispostaNonCorrettoPerViaValidazioneSchemiServerDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, !SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				null); // Funziona perchè vi è un solo nodo, e allora su GovWay per questo caso speciale viene attivata la validazione descritta nella proprieta' 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione'
	}
	@Test
	public void fruizione_rpc_type_messaggioRispostaNonCorrettoPerViaValidazioneSchemiServerDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, !SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				null); // Funziona perchè vi è un solo nodo, e allora su GovWay per questo caso speciale viene attivata la validazione descritta nella proprieta' 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione'
	}
	
	@Test
	public void erogazione_rpc_type_soapUI_validazioneGovWay() throws Exception {

		_test(TipoServizio.EROGAZIONE, content_rpc_type_soapUI, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				"Required input parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}int]idint' undefined in 5 body root-element(s) founded: {null}nominativo,{null}indirizzo,{null}ora-registrazione,{null}idstring,{null}idint");
	}
	@Test
	public void fruizione_rpc_type_soapUI_validazioneGovWay() throws Exception {

		_test(TipoServizio.FRUIZIONE, content_rpc_type_soapUI, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				"Required input parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}int]idint' undefined in 5 body root-element(s) founded: {null}nominativo,{null}indirizzo,{null}ora-registrazione,{null}idstring,{null}idint");
	}
	
	@Test
	public void erogazione_rpc_type_govwayApiNamespaceRidefinito() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-type",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_type_govwayApiNamespaceRidefinito() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-type",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_type_stubNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_type_stubNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_type_messaggiCorrettoPerViaValidazioneSchemiAbilitata_soapNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-type",
				null);
	}
	@Test
	public void fruizione_rpc_type_messaggiCorrettoPerViaValidazioneSchemiAbilitata_soapNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-type",
				null);
	}
	
	
	
	// ***** RPC TYPE (TEST ROOT ELEMENT) ****
	
	// NOTA: test migliori per questo caso sono fatti in rpc_element_*
	
	private static String content_rpc_type_rootElementUnqualfied = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <RPCL-type>\n"+
			"         <nominativo ruolo=\"test\">test</nominativo>\n"+
			"         <indirizzo>test</indirizzo>\n"+
			"         <ora-registrazione>2022-12-27T15:22:41.220+01:00</ora-registrazione>\n"+
			"         <idstring>3</idstring>\n"+
			"         <idint>3</idint>\n"+
			"      </RPCL-type>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>";
		
	@Test
	public void erogazione_rpc_type_rootElementUnqualfied() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_type_rootElementUnqualfied, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				"Unmarshalling Error: cvc-elt.4.2: Cannot resolve 'nominativoType' to a type definition for element 'nominativo'."); // e' il backend CXF che non riconosce
	}
	@Test
	public void fruizione_rpc_type_rootElementUnqualfied() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_type_rootElementUnqualfied, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				"Unmarshalling Error: cvc-elt.4.2: Cannot resolve 'nominativoType' to a type definition for element 'nominativo'."); // e' il backend CXF che non riconosce
	}
	
	@Test
	public void erogazione_rpc_type_rootElementUnqualfied_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_type_rootElementUnqualfied, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				"Required input parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}int]idint' undefined in 5 body root-element(s) founded: {null}nominativo,{null}indirizzo,{null}ora-registrazione,{null}idstring,{null}idint");
	}
	@Test
	public void fruizione_rpc_type_rootElementUnqualfied_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_type_rootElementUnqualfied, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-type",
				"Required input parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}int]idint' undefined in 5 body root-element(s) founded: {null}nominativo,{null}indirizzo,{null}ora-registrazione,{null}idstring,{null}idint");
	}
	
	@Test
	public void erogazione_rpc_type_rootElementUnqualfied_configPropertiesNotAccepted() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_type_rootElementUnqualfied, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-type",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_type_rootElementUnqualfied_configPropertiesNotAccepted() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_type_rootElementUnqualfied, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-type",
				"Operation undefined in the API specification");
	}
		
	private static String content_rpc_type_rootElementNamespaceDifferente = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:serDIFFERENTE=\"http://openspcoop2.org/ValidazioneContenutiWS/ServiceDIFFERENTE\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <serDIFFERENTE:RPCL-type>\n"+
			"         <nominativo ruolo=\"test\">test</nominativo>\n"+
			"         <indirizzo>test</indirizzo>\n"+
			"         <ora-registrazione>2022-12-27T15:22:41.220+01:00</ora-registrazione>\n"+
			"         <idstring>3</idstring>\n"+
			"         <idint>3</idint>\n"+
			"      </serDIFFERENTE:RPCL-type>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>";
	
	@Test
	public void erogazione_rpc_type_rootElementNamespaceDifferente() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_type_rootElementNamespaceDifferente, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_type_rootElementNamespaceDifferente() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_type_rootElementNamespaceDifferente, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-type",
				"Operation undefined in the API specification");
	}
	
	
	
	// ***** RPC ELEMENT ****
	
	@Test
	public void erogazione_rpc_element_messaggiCorrettoPerViaValidazioneSchemiAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_messaggiCorrettoPerViaValidazioneSchemiAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_messaggiNonCorrettiPerViaValidazioneSchemiDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, !CLIENT_VALIDATION, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_messaggiNonCorrettiPerViaValidazioneSchemiDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, !CLIENT_VALIDATION, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	private static String content_rpc_element_soapUI = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://openspcoop2.org/ValidazioneContenutiWS/Service\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <ser:RPCL-element>\n"+
			"         <typ:nominativo ruolo=\"test\">test</typ:nominativo>\n"+
			"         <typ:indirizzo>test</typ:indirizzo>\n"+
			"         <typ:ora-registrazione>2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"+
			"      </ser:RPCL-element>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
	
	@Test
	public void erogazione_rpc_element_soapUI() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_soapUI, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_soapUI() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_soapUI, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_messaggiCorrettoPerViaValidazioneSchemiAbilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_messaggiCorrettoPerViaValidazioneSchemiAbilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_messaggioRichiestaCorrettoAncheSeValidazioneSchemiClientDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, !CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_messaggioRichiestaCorrettoAncheSeValidazioneSchemiClientDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, !CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_messaggioRispostaCorrettoAncheSeValidazioneSchemiServerDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, !SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null); 
	}
	@Test
	public void fruizione_rpc_element_messaggioRispostaCorrettoAncheSeValidazioneSchemiServerDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, !SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null); 
	}
	
	@Test
	public void erogazione_rpc_element_soapUI_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_soapUI, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_soapUI_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_soapUI, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_govwayApiNamespaceRidefinito() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_element_govwayApiNamespaceRidefinito() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_element_stubNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_element_stubNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_element_messaggiCorrettoPerViaValidazioneSchemiAbilitata_soapNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_messaggiCorrettoPerViaValidazioneSchemiAbilitata_soapNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				null);
	}
	
	
	
	// ***** RPC ELEMENT (TEST ROOT ELEMENT) ****
	
	private static String content_rpc_element_rootElementUnqualfied = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <RPCL-element>\n"+
			"         <typ:nominativo ruolo=\"test\">test</typ:nominativo>\n"+
			"         <typ:indirizzo>test</typ:indirizzo>\n"+
			"         <typ:ora-registrazione>2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"+
			"      </RPCL-element>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>";
	
	@Test
	public void erogazione_rpc_element_rootElementUnqualfied() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_rootElementUnqualfied() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_rootElementUnqualfied_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	@Test
	public void fruizione_rpc_element_rootElementUnqualfied_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				null);
	}
	
	@Test
	public void erogazione_rpc_element_rootElementUnqualfied_configPropertiesNotAccepted() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_element_rootElementUnqualfied_configPropertiesNotAccepted() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_element_rootElementUnqualfied_configPropertiesNotAccepted_validazioneGovWayFallita() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				"RPCL-element", // riconosco l'azione tramite la url 
				"Unqualified rpc request element 'RPCL-element' by WSDL specification");
	}
	@Test
	public void fruizione_rpc_element_rootElementUnqualfied_configPropertiesNotAccepted_validazioneGovWayFallita() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_rootElementUnqualfied, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-element",
				"RPCL-element", // riconosco l'azione tramite la url 
				"Unqualified rpc request element 'RPCL-element' by WSDL specification");
	}
	
	private static String content_rpc_element_rootElementNamespaceDifferente = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:serDIFFERENTE=\"http://openspcoop2.org/ValidazioneContenutiWS/ServiceDIFFERENTE\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <serDIFFERENTE:RPCL-element>\n"+
			"         <typ:nominativo ruolo=\"test\">test</typ:nominativo>\n"+
			"         <typ:indirizzo>test</typ:indirizzo>\n"+
			"         <typ:ora-registrazione>2022-12-27T15:22:41.220+01:00</typ:ora-registrazione>\n"+
			"      </serDIFFERENTE:RPCL-element>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>";
	
	@Test
	public void erogazione_rpc_element_rootElementNamespaceDifferente() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_rootElementNamespaceDifferente, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_element_rootElementNamespaceDifferente() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_rootElementNamespaceDifferente, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-element",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_element_rootElementNamespaceDifferente_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_element_rootElementNamespaceDifferente, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				"RPCL-element", // riconosco l'azione tramite la url 
				"expected namespace 'http://openspcoop2.org/ValidazioneContenutiWS/Service'; found namespace 'http://openspcoop2.org/ValidazioneContenutiWS/ServiceDIFFERENTE'");
	}
	@Test
	public void fruizione_rpc_element_rootElementNamespaceDifferente_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_element_rootElementNamespaceDifferente, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-element",
				"RPCL-element", // riconosco l'azione tramite la url 
				"expected namespace 'http://openspcoop2.org/ValidazioneContenutiWS/Service'; found namespace 'http://openspcoop2.org/ValidazioneContenutiWS/ServiceDIFFERENTE'");
	}
	
	
	
	// ***** RPC XSI TYPE ****
	
	@Test
	public void erogazione_rpc_xsitype_messaggiCorrettoPerViaValidazioneSchemiAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	@Test
	public void fruizione_rpc_xsitype_messaggiCorrettoPerViaValidazioneSchemiAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	
	@Test
	public void erogazione_rpc_xsitype_messaggiNonCorrettiPerViaValidazioneSchemiDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, !CLIENT_VALIDATION, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	@Test
	public void fruizione_rpc_xsitype_messaggiNonCorrettiPerViaValidazioneSchemiDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, !CLIENT_VALIDATION, !SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	
	private static String content_rpc_xsitype_soapUI = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://openspcoop2.org/ValidazioneContenutiWS/Service\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <ser:RPCL-xsitype>\n"+
			"         <richiestaEsempioXSI>\n"+
			"            <typ:dati>test</typ:dati>\n"+
			"         </richiestaEsempioXSI>\n"+
			"      </ser:RPCL-xsitype>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
	
	private static String content_rpc_xsitype_soapUI_fixedValueErrato = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://openspcoop2.org/ValidazioneContenutiWS/Service\" xmlns:typ=\"http://openspcoop2.org/ValidazioneContenutiWS/Service/types\">\n"+
			"   <soapenv:Header/>\n"+
			"   <soapenv:Body>\n"+
			"      <ser:RPCL-xsitype>\n"+
			"         <richiestaEsempioXSI>\n"+
			"            <typ:dati>test2</typ:dati>\n"+
			"         </richiestaEsempioXSI>\n"+
			"      </ser:RPCL-xsitype>\n"+
			"   </soapenv:Body>\n"+
			"</soapenv:Envelope>"; // generato con soapui
	
	@Test
	public void erogazione_rpc_xsitype_soapUI() throws Exception {
		_test(TipoServizio.EROGAZIONE, content_rpc_xsitype_soapUI, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	@Test
	public void fruizione_rpc_xsitype_soapUI() throws Exception {
		_test(TipoServizio.FRUIZIONE, content_rpc_xsitype_soapUI, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	
	@Test
	public void erogazione_rpc_xsitype_messaggiCorrettoPerViaValidazioneSchemiAbilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	@Test
	public void fruizione_rpc_xsitype_messaggiCorrettoPerViaValidazioneSchemiAbilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				null);
	}
	
	@Test
	public void erogazione_rpc_xsitype_messaggioRichiestaNonCorrettoPerViaValidazioneSchemiClientDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, !CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				null); // Funziona perchè vi è un solo nodo, e allora su GovWay per questo caso speciale viene attivata la validazione descritta nella proprieta' 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione'
	}
	@Test
	public void fruizione_rpc_xsitype_messaggioRichiestaNonCorrettoPerViaValidazioneSchemiClientDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, !CLIENT_VALIDATION, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				null); // Funziona perchè vi è un solo nodo, e allora su GovWay per questo caso speciale viene attivata la validazione descritta nella proprieta' 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione'
	}
	
	@Test
	public void erogazione_rpc_xsitype_messaggioRispostaNonCorrettoPerViaValidazioneSchemiServerDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, !SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				"Invalid response received from the API Implementation");
				// Solo nei diagnostici. "Required output parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}string]esito' undefined in 2 body root-element(s) founded: {null}rispostaEsempioXSI,{null}esito"); 
	}
	@Test
	public void fruizione_rpc_xsitype_messaggioRispostaNonCorrettoPerViaValidazioneSchemiServerDisabilitata_validazioneGovWay() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, !SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				"Invalid response received from the API Implementation");
				// Solo nei diagnostici. "Required output parameter '[xsi:type=\"{http://www.w3.org/2001/XMLSchema\"}string]esito' undefined in 2 body root-element(s) founded: {null}rispostaEsempioXSI,{null}esito"); 
	}
	
	@Test
	public void erogazione_rpc_xsitype_soapUI_validazioneGovWay() throws Exception {

		_test(TipoServizio.EROGAZIONE, content_rpc_xsitype_soapUI, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				null); // Funziona perchè vi è un solo nodo, e allora su GovWay per questo caso speciale viene attivata la validazione descritta nella proprieta' 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione'
	}
	@Test
	public void fruizione_rpc_xsitype_soapUI_validazioneGovWay() throws Exception {

		_test(TipoServizio.FRUIZIONE, content_rpc_xsitype_soapUI, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				null); // Funziona perchè vi è un solo nodo, e allora su GovWay per questo caso speciale viene attivata la validazione descritta nella proprieta' 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione'
	}
	
	@Test
	public void erogazione_rpc_xsitype_soapUI_fixedValueErrato_validazioneGovWay() throws Exception {

		_test(TipoServizio.EROGAZIONE, content_rpc_xsitype_soapUI_fixedValueErrato, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				"(element {http://openspcoop2.org/ValidazioneContenutiWS/Service}RPCL-xsitype) cvc-elt.5.2.2.2.2: The value 'test2' of element 'typ:dati' does not match the {value constraint} value 'test'."); 
	}
	@Test
	public void fruizione_rpc_xsitype_soapUI_fixedValueErrato_validazioneGovWay() throws Exception {

		_test(TipoServizio.FRUIZIONE, content_rpc_xsitype_soapUI_fixedValueErrato, SERVER_VALIDATION, API_CON_VALIDAZIONE, "RPCL-xsitype",
				"(element {http://openspcoop2.org/ValidazioneContenutiWS/Service}RPCL-xsitype) cvc-elt.5.2.2.2.2: The value 'test2' of element 'typ:dati' does not match the {value constraint} value 'test'."); 
	}
	
	@Test
	public void erogazione_rpc_xsitype_govwayApiNamespaceRidefinito() throws Exception {
		_test(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-xsitype",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_xsitype_govwayApiNamespaceRidefinito() throws Exception {
		_test(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-xsitype",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_xsitype_stubNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				"Operation undefined in the API specification");
	}
	@Test
	public void fruizione_rpc_xsitype_stubNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SENZA_VALIDAZIONE, "RPCL-xsitype",
				"Operation undefined in the API specification");
	}
	
	@Test
	public void erogazione_rpc_xsitype_messaggiCorrettoPerViaValidazioneSchemiAbilitata_soapNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.EROGAZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-xsitype",
				null);
	}
	@Test
	public void fruizione_rpc_xsitype_messaggiCorrettoPerViaValidazioneSchemiAbilitata_soapNamespaceRidefinito() throws Exception {
		_testSoapNamespaceRidefinito(TipoServizio.FRUIZIONE, CLIENT_VALIDATION, SERVER_VALIDATION, API_SOAP_NAMESPACE_RIDEFINITO, "RPCL-xsitype",
				null);
	}
	
	private static String correctUrl(TipoServizio tipoServizio,boolean serverValidationEnabled,String api, String url) {
		if(tipoServizio == TipoServizio.EROGAZIONE) {
			if(serverValidationEnabled) {
				url = url +"?tipo=ValidazioneAbilitata";
			}
			else {
				url = url +"?tipo=ValidazioneDisabilitata";
			}
		}
		else {
			if(api.equals(API_SENZA_VALIDAZIONE)) {
				if(serverValidationEnabled) {
					url = url.replace(API_SENZA_VALIDAZIONE, API_SENZA_VALIDAZIONE+"ValidazioneAbilitata");
				}
				else {
					url = url.replace(API_SENZA_VALIDAZIONE, API_SENZA_VALIDAZIONE+"ValidazioneDisabilitata");
				}
			}
			else if(api.equals(API_CON_VALIDAZIONE)) {
				if(serverValidationEnabled) {
					url = url.replace(API_CON_VALIDAZIONE, API_CON_VALIDAZIONE+"ValidazioneAbilitata");
				}
				else {
					url = url.replace(API_CON_VALIDAZIONE, API_CON_VALIDAZIONE+"ValidazioneDisabilitata");
				}
			}
		}
		return url;
	}
	
	private static void _test(
			TipoServizio tipoServizio, boolean clientValidationEnabled, boolean serverValidationEnabled, String api, String operazione,
			String msgErrore) throws Exception {
		
		waitStartServer();

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/";
		url = correctUrl(tipoServizio, serverValidationEnabled, api, url);
		
		URL wsdlURL = org.openspcoop2.example.server.rpc.literal.stub.ServiceRPCLiteral_Service.WSDL_LOCATION;
		final QName SERVICE_NAME = new QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "ServiceRPCLiteral");
		org.openspcoop2.example.server.rpc.literal.stub.ServiceRPCLiteral_Service ss = new org.openspcoop2.example.server.rpc.literal.stub.ServiceRPCLiteral_Service(wsdlURL, SERVICE_NAME);
		org.openspcoop2.example.server.rpc.literal.stub.ServiceRPCLiteral port = ss.getServiceRPCLiteral();
		((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
		if(clientValidationEnabled) {
			((BindingProvider)port).getRequestContext().put("schema-validation-enabled", true);
		}
		
		if("RPCL-type".equals(operazione)) {
			//System.out.println("Invoking rpclType...");
	        org.openspcoop2.example.server.rpc.literal.stub.NominativoType _rpclType_nominativo = new org.openspcoop2.example.server.rpc.literal.stub.NominativoType();
	        _rpclType_nominativo.setRuolo("richiesta");
	        _rpclType_nominativo.setValue("Andrea");
	        java.lang.String _rpclType_indirizzo = "via di Test";
	        GregorianCalendar c = new GregorianCalendar();
	        c.setTime(new Date());
	        javax.xml.datatype.XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	        javax.xml.datatype.XMLGregorianCalendar _rpclType_oraRegistrazione = date2;
	        java.lang.String _rpclType_idstring = "Id-Req-1";
	        int _rpclType_idint = 123;
	        org.openspcoop2.example.server.rpc.literal.stub.EsitoType _rpclType__return = null;
	        String errore = null;
	        try {
	        	_rpclType__return = port.rpclType(_rpclType_nominativo, _rpclType_indirizzo, _rpclType_oraRegistrazione, _rpclType_idstring, _rpclType_idint);

	        	//System.out.println("rpclType._rpclType_esito=" + _rpclType__return.getCode());
	        	//System.out.println("rpclType._rpclType_esito.descr=" + _rpclType__return.getReason());
	        }catch(Throwable t) {
	        	//System.out.println("ERRORE: "+t.getMessage());
	        	errore = t.getMessage();
	        }
	        if(msgErrore!=null) {
	        	Assert.assertNotNull(errore);
	        	if(!errore.contains(msgErrore)) {
	        		System.out.println("Errore ricevuto: "+errore);
	        		System.out.println("Errore atteso: "+msgErrore);
	        	}
	        	Assert.assertTrue(errore.contains(msgErrore));
	        }
	        else {
	        	Assert.assertEquals(_rpclType__return.getCode(), "OK");
	        }
		}
		
		else if("RPCL-element".equals(operazione)) {
			//System.out.println("Invoking rpclElement...");
			org.openspcoop2.example.server.rpc.literal.stub.ObjectFactory of = new org.openspcoop2.example.server.rpc.literal.stub.ObjectFactory();
			org.openspcoop2.example.server.rpc.literal.stub.NominativoType _rpclElement_nominativoUtente_type = new org.openspcoop2.example.server.rpc.literal.stub.NominativoType();
			_rpclElement_nominativoUtente_type.setRuolo("richiesta");
			_rpclElement_nominativoUtente_type.setValue("Andrea");
			JAXBElement<org.openspcoop2.example.server.rpc.literal.stub.NominativoType> _rpclElement_nominativoUtente = of.createNominativo(_rpclElement_nominativoUtente_type);
			JAXBElement<String> _rpclElement_indirizzoUtente = of.createIndirizzo("via di Test");
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(new Date());
			javax.xml.datatype.XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			JAXBElement<javax.xml.datatype.XMLGregorianCalendar> _rpclElement_oraRegistrazioneUtente = of.createOraRegistrazione(date2);
			
			org.openspcoop2.example.server.rpc.literal.stub.EsitoType _rpclElement__return = null;
			String errore = null;
	        try {
	        	_rpclElement__return = port.rpclElement(_rpclElement_nominativoUtente, _rpclElement_indirizzoUtente, _rpclElement_oraRegistrazioneUtente);
	        
	        	//System.out.println("rpclElement._rpclElement_esitoRegistrazione=" + _rpclElement__return.getCode());
	        	//System.out.println("rpclElement._rpclElement_esitoRegistrazione.descr=" + _rpclElement__return.getReason());
	        }catch(Throwable t) {
	        	//System.out.println("ERRORE: "+t.getMessage());
	        	errore = t.getMessage();
	        }
	        if(msgErrore!=null) {
	        	Assert.assertNotNull(errore);
	        	if(!errore.contains(msgErrore)) {
	        		System.out.println("Errore ricevuto: "+errore);
	        		System.out.println("Errore atteso: "+msgErrore);
	        	}
	        	Assert.assertTrue(errore.contains(msgErrore));
	        }
	        else {
	        	Assert.assertEquals(_rpclElement__return.getCode(), "OK");
	        }
		}
		
		else if("RPCL-xsitype".equals(operazione)) {
			//System.out.println("Invoking rpclXsitype...");
			org.openspcoop2.example.server.rpc.literal.stub.FixedMessaggioType _rpclXsitype_richiestaEsempioXSI = new org.openspcoop2.example.server.rpc.literal.stub.FixedMessaggioType();
			_rpclXsitype_richiestaEsempioXSI.setDati("test");
			jakarta.xml.ws.Holder<org.openspcoop2.example.server.rpc.literal.stub.FixedMessaggioType> _rpclXsitype_rispostaEsempioXSI = new jakarta.xml.ws.Holder<org.openspcoop2.example.server.rpc.literal.stub.FixedMessaggioType>();
			jakarta.xml.ws.Holder<java.lang.String> _rpclXsitype_esito = new jakarta.xml.ws.Holder<java.lang.String>();
			String errore = null;
			try {
				port.rpclXsitype(_rpclXsitype_richiestaEsempioXSI, _rpclXsitype_rispostaEsempioXSI, _rpclXsitype_esito);
			
				//System.out.println("rpclXsitype._rpclXsitype_rispostaEsempioXSI=" + _rpclXsitype_rispostaEsempioXSI.value.getDati());
				//System.out.println("rpclXsitype._rpclXsitype_esito=" + _rpclXsitype_esito.value);
			}catch(Throwable t) {
	        	//System.out.println("ERRORE: "+t.getMessage());
	        	errore = t.getMessage();
	        }
	        if(msgErrore!=null) {
	        	Assert.assertNotNull(errore);
	        	if(!errore.contains(msgErrore)) {
	        		System.out.println("Errore ricevuto: "+errore);
	        		System.out.println("Errore atteso: "+msgErrore);
	        	}
	        	Assert.assertTrue(errore.contains(msgErrore));
	        }
	        else {
	        	Assert.assertEquals(_rpclXsitype_esito.value, "OK");
	        }
		}
	}
	
	
	private static void _testSoapNamespaceRidefinito(
			TipoServizio tipoServizio, boolean clientValidationEnabled, boolean serverValidationEnabled, String api, String operazione,
			String msgErrore) throws Exception {
		
		waitStartServer();

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/";
		url = correctUrl(tipoServizio, serverValidationEnabled, api, url);
		
		URL wsdlURL = org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.ServiceRPCLiteral_Service.WSDL_LOCATION;
		final QName SERVICE_NAME = new QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "ServiceRPCLiteral");
		org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.ServiceRPCLiteral_Service ss = new org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.ServiceRPCLiteral_Service(wsdlURL, SERVICE_NAME);
		org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.ServiceRPCLiteral port = ss.getServiceRPCLiteral();
		((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
		if(clientValidationEnabled) {
			((BindingProvider)port).getRequestContext().put("schema-validation-enabled", true);
		}
		
		if("RPCL-type".equals(operazione)) {
			//System.out.println("Invoking rpclType...");
	        org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.NominativoType _rpclType_nominativo = new org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.NominativoType();
	        _rpclType_nominativo.setRuolo("richiesta");
	        _rpclType_nominativo.setValue("Andrea");
	        java.lang.String _rpclType_indirizzo = "via di Test";
	        GregorianCalendar c = new GregorianCalendar();
	        c.setTime(new Date());
	        javax.xml.datatype.XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	        javax.xml.datatype.XMLGregorianCalendar _rpclType_oraRegistrazione = date2;
	        java.lang.String _rpclType_idstring = "Id-Req-1";
	        int _rpclType_idint = 123;
	        org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.EsitoType _rpclType__return = null;
	        String errore = null;
	        try {
	        	_rpclType__return = port.rpclType(_rpclType_nominativo, _rpclType_indirizzo, _rpclType_oraRegistrazione, _rpclType_idstring, _rpclType_idint);

	        	//System.out.println("rpclType._rpclType_esito=" + _rpclType__return.getCode());
	        	//System.out.println("rpclType._rpclType_esito.descr=" + _rpclType__return.getReason());
	        }catch(Throwable t) {
	        	//System.out.println("ERRORE: "+t.getMessage());
	        	errore = t.getMessage();
	        }
	        if(msgErrore!=null) {
	        	Assert.assertNotNull(errore);
	        	if(!errore.contains(msgErrore)) {
	        		System.out.println("Errore ricevuto: "+errore);
	        		System.out.println("Errore atteso: "+msgErrore);
	        	}
	        	Assert.assertTrue(errore.contains(msgErrore));
	        }
	        else {
	        	Assert.assertEquals(_rpclType__return.getCode(), "OK");
	        }
		}
		
		else if("RPCL-element".equals(operazione)) {
			//System.out.println("Invoking rpclElement...");
			org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.ObjectFactory of = new org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.ObjectFactory();
			org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.NominativoType _rpclElement_nominativoUtente_type = new org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.NominativoType();
			_rpclElement_nominativoUtente_type.setRuolo("richiesta");
			_rpclElement_nominativoUtente_type.setValue("Andrea");
			JAXBElement<org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.NominativoType> _rpclElement_nominativoUtente = of.createNominativo(_rpclElement_nominativoUtente_type);
			JAXBElement<String> _rpclElement_indirizzoUtente = of.createIndirizzo("via di Test");
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(new Date());
			javax.xml.datatype.XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			JAXBElement<javax.xml.datatype.XMLGregorianCalendar> _rpclElement_oraRegistrazioneUtente = of.createOraRegistrazione(date2);
			
			org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.EsitoType _rpclElement__return = null;
			String errore = null;
	        try {
	        	_rpclElement__return = port.rpclElement(_rpclElement_nominativoUtente, _rpclElement_indirizzoUtente, _rpclElement_oraRegistrazioneUtente);
	        
	        	//System.out.println("rpclElement._rpclElement_esitoRegistrazione=" + _rpclElement__return.getCode());
	        	//System.out.println("rpclElement._rpclElement_esitoRegistrazione.descr=" + _rpclElement__return.getReason());
	        }catch(Throwable t) {
	        	//System.out.println("ERRORE: "+t.getMessage());
	        	errore = t.getMessage();
	        }
	        if(msgErrore!=null) {
	        	Assert.assertNotNull(errore);
	        	if(!errore.contains(msgErrore)) {
	        		System.out.println("Errore ricevuto: "+errore);
	        		System.out.println("Errore atteso: "+msgErrore);
	        	}
	        	Assert.assertTrue(errore.contains(msgErrore));
	        }
	        else {
	        	Assert.assertEquals(_rpclElement__return.getCode(), "OK");
	        }
		}
		
		else if("RPCL-xsitype".equals(operazione)) {
			//System.out.println("Invoking rpclXsitype...");
			org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.FixedMessaggioType _rpclXsitype_richiestaEsempioXSI = new org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.FixedMessaggioType();
			_rpclXsitype_richiestaEsempioXSI.setDati("test");
			jakarta.xml.ws.Holder<org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.FixedMessaggioType> _rpclXsitype_rispostaEsempioXSI = new jakarta.xml.ws.Holder<org.openspcoop2.example.server.rpc.literal.stub_namespace_ridefinito.FixedMessaggioType>();
			jakarta.xml.ws.Holder<java.lang.String> _rpclXsitype_esito = new jakarta.xml.ws.Holder<java.lang.String>();
			String errore = null;
			try {
				port.rpclXsitype(_rpclXsitype_richiestaEsempioXSI, _rpclXsitype_rispostaEsempioXSI, _rpclXsitype_esito);
			
				//System.out.println("rpclXsitype._rpclXsitype_rispostaEsempioXSI=" + _rpclXsitype_rispostaEsempioXSI.value.getDati());
				//System.out.println("rpclXsitype._rpclXsitype_esito=" + _rpclXsitype_esito.value);
			}catch(Throwable t) {
	        	//System.out.println("ERRORE: "+t.getMessage());
	        	errore = t.getMessage();
	        }
	        if(msgErrore!=null) {
	        	Assert.assertNotNull(errore);
	        	if(!errore.contains(msgErrore)) {
	        		System.out.println("Errore ricevuto: "+errore);
	        		System.out.println("Errore atteso: "+msgErrore);
	        	}
	        	Assert.assertTrue(errore.contains(msgErrore));
	        }
	        else {
	        	Assert.assertEquals(_rpclXsitype_esito.value, "OK");
	        }
		}
	}
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String content,
			boolean serverValidationEnabled,
			String api, String operazione,
			String msgErrore) throws Exception {
		return _test(
				tipoServizio, content,
				serverValidationEnabled,
				api, operazione, null,
				msgErrore);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String content,
			boolean serverValidationEnabled,
			String api, String operazione, String operazioneUrl,
			String msgErrore) throws Exception {
		
		waitStartServer();
		
		return _testNoWaitServer(tipoServizio, content, 
				serverValidationEnabled, 
				api, operazione, operazioneUrl,
				msgErrore);
		
	}
	protected static HttpResponse _testNoWaitServer(
			TipoServizio tipoServizio, String content,
			boolean serverValidationEnabled,
			String api, String operazione,
			String msgErrore) throws Exception {
		return _testNoWaitServer(
				tipoServizio, content,
				serverValidationEnabled,
				api, operazione, null,
				msgErrore);
	}
	protected static HttpResponse _testNoWaitServer(
			TipoServizio tipoServizio, String content,
			boolean serverValidationEnabled,
			String api, String operazione, String operazioneUrl,
			String msgErrore) throws Exception {

		
		
		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+api+"/v1/"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/"+api+"/v1/";
		if(operazioneUrl!=null) {
			url = url + operazioneUrl;
		}
		url = correctUrl(tipoServizio, serverValidationEnabled, api, url);


		HttpRequest request = new HttpRequest();
		
		String contentTypeRequest = HttpConstants.CONTENT_TYPE_SOAP_1_1+";charset=utf-8";
		String contentTypeResponse = contentTypeRequest;
		if(msgErrore!=null && !msgErrore.contains("Unmarshalling Error: cvc-elt.4.2")) {
			contentTypeResponse = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		}
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\""+operazione+"\"");
		
				
		request.setReadTimeout(20000);
						
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentTypeRequest);
		
		request.setContent(content.getBytes());
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
		}catch(Throwable t) {
			throw t;
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		
		int returCodeAtteso = msgErrore!=null ? 500 : 200;
		assertEquals(returCodeAtteso, response.getResultHTTPOperation());
		//System.out.println("ATTESO ["+contentTypeResponse+"]");
		//System.out.println("RICEVUTO ["+response.getContentType()+"]");
		assertEquals(contentTypeResponse, response.getContentType());
		
//		if(operazione.contains("11")) {
//			String soapAction = response.getHeaderFirstValue("GovWay-TestSuite-SOAPAction");
//			assertNotNull("verifica soap action ricevuta '"+soapAction+"'", soapAction);
//			assertEquals(soapActionAttesaRisposta, soapAction);
//		}
//		else {
//			String soapAction = response.getHeaderFirstValue("GovWay-TestSuite-Content-Type");
//			assertNotNull("verifica soap action ricevuta '"+soapAction+"'", soapAction);
//			assertEquals(contentTypeResponse, soapAction);
//		}
				
		if(msgErrore!=null) {
			String errore = new String(response.getContent());
			if(!errore.contains(msgErrore)) {
        		System.out.println("Errore ricevuto: "+errore);
        		System.out.println("Errore atteso: "+msgErrore);
        	}
        	Assert.assertTrue(errore.contains(msgErrore));
		}
		
		if(msgErrore==null || msgErrore.contains("Unmarshalling Error: cvc-elt.4.2")) {
			String action = response.getHeaderFirstValue("GovWay-Action");
			assertNotNull(action);
			assertEquals(action, operazione);
		}
		
		return response;
		
	}
	
}
