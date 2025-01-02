/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.timeout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.xml.soap.SOAPFault;

import org.junit.Test;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ProblemUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDati;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.w3c.dom.Node;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	
	// connectTimeout globale
	@Test
	public void erogazione_connectTimeout_globale() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"connectionTimeoutGlobale", "connectionTimeoutGlobale",
				Optional.of("Predefinito"), // gruppo
				Optional.of("connectionTimeoutGlobale"), // connettore 
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "9000"));
	}
	@Test
	public void fruizione_connectTimeout_globale() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"connectionTimeoutGlobale", "connectionTimeoutGlobale",
				Optional.of("connectionTimeoutGlobale"), // gruppo
				Optional.empty(), // connettore 
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "8000"));
	}
	
	// readTimeout globale
	@Test
	public void erogazione_readTimeout_globale() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"readTimeoutGlobale", "readTimeoutGlobale",
				Optional.of("Predefinito"), // gruppo
				Optional.of("readTimeoutGlobale"), // connettore      
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "16000"));
	}
	@Test
	public void fruizione_readTimeout_globale() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"readTimeoutGlobale", "readTimeoutGlobale",
				Optional.of("readTimeoutGlobale"), // gruppo
				Optional.empty(), // connettore       
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "15000"));
	}
	
	
	
	
	
	
	// connectTimeout registrazioneAbilitata
	@Test
	public void erogazione_connectTimeout_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata", "connectionTimeout",
				Optional.of("sendRegistrazioneAbilitata"), // gruppo
				Optional.of("connectionTimeout"), // connettore
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"));
	}
	@Test
	public void fruizione_connectTimeout_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneAbilitata", "connectionTimeout",
				Optional.of("sendRegistrazioneAbilitata.connectionTimeout"), // gruppo
				Optional.empty(), // connettore  
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"));
	}
	
	// connectTimeout registrazioneDisabilitata
	@Test
	public void erogazione_connectTimeout_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "connectionTimeout",
				Optional.of("Predefinito"), // gruppo
				Optional.of("connectionTimeout"), // connettore   
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"));
	}
	@Test
	public void fruizione_connectTimeout_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE).getBytes(),
				"sendRegistrazioneDisabilitata", "connectionTimeout",
				Optional.of("sendRegistrazioneDisabilitata.connectionTimeout"), // gruppo
				Optional.empty(), // connettore 
				RestTest.DIAGNOSTICO_CONNECTION_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
				RestTest.MESSAGGIO_CONNECTION_TIMEOUT.replace(RestTest.SOGLIA, "10"));
	}
	
	
	// echoReceiveRequestSlow registrazioneAbilitata
	@Test
	public void erogazione_echoReceiveRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoReceiveRequestSlow",
				Optional.of("sendRegistrazioneAbilitata"), // gruppo
				Optional.of("echoReceiveRequestSlow"), // connettore     
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoReceiveRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoReceiveRequestSlow",
				Optional.of("sendRegistrazioneAbilitata.echoReceiveRequestSlow"), // gruppo
				Optional.empty(), // connettore     
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	
	// echoReceiveRequestSlow registrazioneDisabilitata
	@Test
	public void erogazione_echoReceiveRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoReceiveRequestSlow",
				Optional.of("Predefinito"), // gruppo
				Optional.of("echoReceiveRequestSlow"), // connettore      
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoReceiveRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoReceiveRequestSlow",
				Optional.of("sendRegistrazioneDisabilitata.echoReceiveRequestSlow"), // gruppo
				Optional.empty(), // connettore      
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	
	// echoSleepBeforeResponse registrazioneAbilitata
	@Test
	public void erogazione_echoSleepBeforeResponse_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoSleepBeforeResponse",
				Optional.of("sendRegistrazioneAbilitata"), // gruppo
				Optional.of("echoSleepBeforeResponse"), // connettore     
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoSleepBeforeResponse_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoSleepBeforeResponse",
				Optional.of("sendRegistrazioneAbilitata.echoSleepBeforeResponse"), // gruppo
				Optional.empty(), // connettore      
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	// echoSleepBeforeResponse registrazioneDisabilitata
	@Test
	public void erogazione_echoSleepBeforeResponse_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoSleepBeforeResponse", 
				Optional.of("Predefinito"), // gruppo
				Optional.of("echoSleepBeforeResponse"), // connettore 
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoSleepBeforeResponse_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoSleepBeforeResponse",
				Optional.of("sendRegistrazioneDisabilitata.echoSleepBeforeResponse"), // gruppo
				Optional.empty(), // connettore       
				RestTest.DIAGNOSTICO_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	
	
	// echoSendResponseSlow registrazioneAbilitata
	@Test
	public void erogazione_echoSendResponseSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneAbilitata", "echoSendResponseSlow",
				Optional.of("sendRegistrazioneAbilitata"), // gruppo
				Optional.of("echoSendResponseSlow"), // connettore    
				RestTest.DIAGNOSTICO_RESPONSE_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoSendResponseSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
			"sendRegistrazioneAbilitata", "echoSendResponseSlow",
			Optional.of("sendRegistrazioneAbilitata.echoSendResponseSlow"), // gruppo
			Optional.empty(), // connettore         
			RestTest.DIAGNOSTICO_RESPONSE_READ_TIMEOUT,
			TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
			RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	
	
	// echoSendResponseSlow registrazioneDisabilitata
	@Test
	public void erogazione_echoSendResponseSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendRegistrazioneDisabilitata", "echoSendResponseSlow",
				Optional.of("Predefinito"), // gruppo
				Optional.of("echoSendResponseSlow"), // connettore    
				RestTest.DIAGNOSTICO_RESPONSE_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoSendResponseSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
			"sendRegistrazioneDisabilitata", "echoSendResponseSlow",
			Optional.of("sendRegistrazioneDisabilitata.echoSendResponseSlow"), // gruppo
			Optional.empty(), // connettore          
			RestTest.DIAGNOSTICO_RESPONSE_READ_TIMEOUT,
			TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
			RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	
	
	// echoSendResponseSlow correlazioneApplicativa
	@Test
	public void erogazione_echoSendResponseSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
				"sendCorrelazioneApplicativa", "EchoSendResponseSlow",
				Optional.of("sendCorrelazioneApplicativa"), // gruppo
				Optional.of("EchoSendResponseSlow"), // connettore      
				RestTest.DIAGNOSTICO_RESPONSE_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
				RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	@Test
	public void fruizione_echoSendResponseSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_500K).getBytes(),
			"sendCorrelazioneApplicativa", "EchoSendResponseSlow",
			Optional.of("sendCorrelazioneApplicativa.EchoSendResponseSlow"), // gruppo
			Optional.empty(), // connettore           
			RestTest.DIAGNOSTICO_RESPONSE_READ_TIMEOUT,
			TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
			RestTest.MESSAGGIO_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"));
	}
	
	
	
	// clientSendRequestSlow registrazioneAbilitata
	@Test
	public void erogazione_clientSendRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitataClientSendSlow", "default",
				Optional.of("sendRegistrazioneAbilitataClientSendSlow"), // gruppo
				Optional.empty(), // connettore      
				RestTest.DIAGNOSTICO_REQUEST_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
				RestTest.MESSAGGIO_REQUEST_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				1000, 100, true);
	}
	@Test
	public void fruizione_clientSendRequestSlow_registrazioneAbilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneAbilitataClientSendSlow", "default",
				Optional.of("sendRegistrazioneAbilitataClientSendSlow"), // gruppo
				Optional.empty(), // connettore            
				RestTest.DIAGNOSTICO_REQUEST_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
				RestTest.MESSAGGIO_REQUEST_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				1000, 100, true);
	}
	
	
	// clientSendRequestSlow registrazioneDisabilitata
	@Test
	public void erogazione_clientSendRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitataClientSendSlow", "default",
				Optional.of("sendRegistrazioneDisabilitataClientSendSlow"), // gruppo
				Optional.empty(), // connettore       
				RestTest.DIAGNOSTICO_REQUEST_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
				RestTest.MESSAGGIO_REQUEST_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				1000, 100, true);
	}
	@Test
	public void fruizione_clientSendRequestSlow_registrazioneDisabilitata() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendRegistrazioneDisabilitataClientSendSlow", "default",
				Optional.of("sendRegistrazioneDisabilitataClientSendSlow"), // gruppo
				Optional.empty(), // connettore       
				RestTest.DIAGNOSTICO_REQUEST_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
				RestTest.MESSAGGIO_REQUEST_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				1000, 100, true);
	}
	
	
	
	// clientSendRequestSlow correlazioneApplicativa
	@Test
	public void erogazione_clientSendRequestSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendCorrelazioneApplicativa", "ClientSendRequestSlow",
				Optional.of("sendCorrelazioneApplicativa"), // gruppo
				Optional.empty(), // connettore        
				RestTest.DIAGNOSTICO_REQUEST_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
				RestTest.MESSAGGIO_REQUEST_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				1000, 100, true);
	}
	@Test
	public void fruizione_clientSendRequestSlow_correlazioneApplicativa() throws Exception {
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, Bodies.getSOAPEnvelope11(Bodies.SIZE_50K).getBytes(),
				"sendCorrelazioneApplicativa", "ClientSendRequestSlow",
				Optional.of("sendCorrelazioneApplicativa.ClientSendRequestSlow"), // gruppo
				Optional.empty(), // connettore              
				RestTest.DIAGNOSTICO_REQUEST_READ_TIMEOUT,
				TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
				RestTest.MESSAGGIO_REQUEST_READ_TIMEOUT.replace(RestTest.SOGLIA, "2000"),
				1000, 100, true);
	}
	
	
	
	
	
	
	
	// connessioneCLientInterrotta
	@Test
	public void erogazione_connessioneCLientInterrotta() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.EROGAZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, 
				Bodies.getSOAPEnvelope11(
						// Bodies.SMALL_SIZE, rimane da capire come disabilitare il buffer, in tomcat9 non vale piu' socketBuffer=-1 
						// Per adesso si usa un messaggio maggiore della dimensione di 8k in modo da andare "fuori" buffer
						Bodies.SIZE_50K, 
						idApplicativoClaim).getBytes(),
				"connessioneClientInterrotta", "connessioneClientInterrotta",
				null, // gruppo
				null, // connettore                
				"Broken pipe",
				null ,null,
				idApplicativo);
	}
	@Test
	public void fruizione_connessioneCLientInterrotta() throws Exception {
		String idApplicativo = System.currentTimeMillis()+"-"+IDUtilities.getUniqueSerialNumber();
		String idApplicativoClaim = "<identificativoApplicativo>"+idApplicativo+"</identificativoApplicativo>";
		_test(TipoServizio.FRUIZIONE, HttpConstants.CONTENT_TYPE_SOAP_1_1, 
				Bodies.getSOAPEnvelope11(
						// Bodies.SMALL_SIZE, rimane da capire come disabilitare il buffer, in tomcat9 non vale piu' socketBuffer=-1 
						// Per adesso si usa un messaggio maggiore della dimensione di 8k in modo da andare "fuori" buffer
						Bodies.SIZE_50K, 
						idApplicativoClaim).getBytes(),
				"connessioneClientInterrotta", "connessioneClientInterrotta",
				null, // gruppo
				null, // connettore                
				"Broken pipe",
				null ,null,
				idApplicativo);
	}
	
	
	
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, Optional<String> gruppo, Optional<String> connettore, String msgErrore,
			TipoEvento tipoEvento, String descrizioneEvento) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, gruppo, connettore, msgErrore,
				tipoEvento, descrizioneEvento,
				null, null, false, 
				null);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, Optional<String> gruppo, Optional<String> connettore, String msgErrore,
			TipoEvento tipoEvento, String descrizioneEvento,
			String applicativeId) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, gruppo, connettore, msgErrore,
				tipoEvento, descrizioneEvento,
				null, null, false, 
				applicativeId);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, Optional<String> gruppo, Optional<String> connettore, String msgErrore,
			TipoEvento tipoEvento, String descrizioneEvento,
			Integer throttlingByte, Integer throttlingMs, boolean throttlingSend) throws Exception {
		return _test(
				tipoServizio, contentType, content,
				operazione, tipoTest, gruppo, connettore, msgErrore,
				tipoEvento, descrizioneEvento,
				throttlingByte, throttlingMs, throttlingSend,
				null);
	}
	private static HttpResponse _test(
			TipoServizio tipoServizio, String contentType, byte[]content,
			String operazione, String tipoTest, Optional<String> gruppo, Optional<String> connettore, String msgErrore,
			TipoEvento tipoEvento, String descrizioneEvento,
			Integer throttlingByte, Integer throttlingMs, boolean throttlingSend,
			String applicativeId) throws Exception {
		
		LocalDateTime dataSpedizione = LocalDateTime.now();

		IDSoggetto idFruitore = null;
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idFruitore = new IDSoggetto("gw", "SoggettoInternoTestFruitore");
		}
		
		IDSoggetto idErogatore = new IDSoggetto("gw", "SoggettoInternoTest");
		IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromValues("gw", "TempiRispostaSOAP", idErogatore, 1);
		
		String idServizio = "SoggettoInternoTest/TempiRispostaSOAP/v1";
		
		String idEventoServizio = idServizio;
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			idEventoServizio = "SoggettoInternoTestFruitore/"+idServizio;
		}

		String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/"+idServizio+"/"+operazione
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/"+idServizio+"/"+operazione;
		if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
			if(!"connectionTimeoutGlobale".equals(operazione) && 
					!"readTimeoutGlobale".equals(operazione) && 
					!"connessioneClientInterrotta".equals(operazione) && 
					!"sendRegistrazioneAbilitataClientSendSlow".equals(operazione) && 
					!"sendRegistrazioneDisabilitataClientSendSlow".equals(operazione)) {
				url=url+"."+tipoTest;
			}
		}
		url=url+"?test="+tipoTest;
		
		HttpRequest request = new HttpRequest();
		request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "\"test\"");
		
		if(throttlingByte!=null && throttlingMs!=null) {
			if(throttlingSend) {
				request.setThrottlingSendByte(throttlingByte);
				request.setThrottlingSendMs(throttlingMs);
			}
		}
		
		if("connessioneClientInterrotta".equals(operazione)) {
			request.setReadTimeout(2000);
		}
		else {
			request.setReadTimeout(20000);
		}
				
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(contentType);
		
		request.setContent(content);
		
		request.setUrl(url);
		
		HttpResponse response = null;
		try {
			response = HttpUtilities.httpInvoke(request);
			if("connessioneClientInterrotta".equals(operazione)) {
				throw new Exception("Expected exception timeout client");
			}
		}catch(Throwable t) {
			if("connessioneClientInterrotta".equals(operazione)) {
				// costruisco io la risposta
				response = new HttpResponse();
				response.setResultHTTPOperation(200);
				response.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
				Utilities.sleep(5000); // aspetto che termina il server
				response.addHeader("GovWay-Transaction-ID", DBVerifier.getIdTransazioneByIdApplicativoRichiesta(applicativeId));
			}
			else {
				throw t;
			}
		}

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
				
		EsitoTransazioneName esitoTransazioneName = null;
		String soapPrefix = null;
		String errorKo = null;
		String errorKoMessage = null;
		int returnCodeKo = -1;
		if(tipoEvento!=null) {
			switch (tipoEvento) {
			case CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT:
				esitoTransazioneName = EsitoTransazioneName.ERRORE_CONNECTION_TIMEOUT;
				errorKo = API_UNAVAILABLE;
				errorKoMessage = API_UNAVAILABLE_MESSAGE;
				returnCodeKo = 503;
				soapPrefix = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER;
				break;
			case CONTROLLO_TRAFFICO_READ_TIMEOUT:
				esitoTransazioneName = EsitoTransazioneName.ERRORE_RESPONSE_TIMEOUT;
				if("sendRegistrazioneDisabilitata".equals(operazione) && "echoSendResponseSlow".equals(tipoTest)) {
					verifyOk(response, 200); // il codice http e' gia' stato impostato e si ottiene l'errore in streaming durante la consegna al client
				}
				else {
					errorKo = ENDPOINT_READ_TIMEOUT;
					errorKoMessage = ENDPOINT_READ_TIMEOUT_MESSAGE;
					returnCodeKo = 504;
					soapPrefix = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER;
				}
				break;
			case CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT:
				esitoTransazioneName = EsitoTransazioneName.ERRORE_REQUEST_TIMEOUT;
				errorKo = REQUEST_TIMED_OUT;
				errorKoMessage = REQUEST_TIMED_OUT_MESSAGE;
				returnCodeKo = 400;
				soapPrefix = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT;
				break;
			default:
				esitoTransazioneName = EsitoTransazioneName.ERRORE_INVOCAZIONE;
				errorKo = API_UNAVAILABLE;
				errorKoMessage = API_UNAVAILABLE_MESSAGE;
				returnCodeKo = 503;
				soapPrefix = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER;
				break;
			}
		}	
		else {
			if("connessioneClientInterrotta".equals(operazione)) {
				esitoTransazioneName = EsitoTransazioneName.ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE;
				verifyOk(response, 200); // il codice http e' gia' stato impostato
			}
			else {
				errorKo = API_UNAVAILABLE;
				errorKoMessage = API_UNAVAILABLE_MESSAGE;
				returnCodeKo = 503;
				soapPrefix = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER;
			}
		}
		
		if(errorKo!=null) {
			logCore.info("Verifico risposta ko con codice '"+errorKo+"' ["+returnCodeKo+"/"+errorKoMessage+"] (soapPrefix:"+soapPrefix+") ...");
			verifyKo(response, soapPrefix, errorKo, returnCodeKo, errorKoMessage);
			logCore.info("Verifico risposta ko con codice '"+errorKo+"' ["+returnCodeKo+"/"+errorKoMessage+"] (soapPrefix:"+soapPrefix+") ok");
		}
		
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(esitoTransazioneName);
		logCore.info("Verifico transazione con stato '"+esitoExpected+"' ("+esitoTransazioneName.toString()+") [msgErrore:"+msgErrore+"] ...");
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore);
		logCore.info("Verifico transazione con stato '"+esitoExpected+"' ("+esitoTransazioneName.toString()+") [msgErrore:"+msgErrore+"] ok");
		
		if(tipoEvento!=null) {
			
			PolicyDati dati = new PolicyDati();
			dati.setProfilo(CostantiLabel.TRASPARENTE_PROTOCOL_NAME);
			if(TipoServizio.FRUIZIONE.equals(tipoServizio)) {
				dati.setRuoloPorta(RuoloPolicy.DELEGATA);
			}
			else {
				dati.setRuoloPorta(RuoloPolicy.APPLICATIVA);
			}
			
			dati.setIdServizio(idServizioObject);
			dati.setIdFruitore(idFruitore);
			
			if(gruppo.isPresent()) {
				dati.setGruppo(gruppo.get());
			}
			if(connettore.isPresent()) {
				dati.setConnettore(connettore.get());
			}
			
			DBVerifier.checkEventiConViolazioneTimeout(idEventoServizio, tipoEvento, gruppo, connettore, null,
					dati,
					descrizioneEvento, dataSpedizione,
					response, logCore);
		}
		
		return response;
		
	}
	
	public static final String API_UNAVAILABLE = "APIUnavailable";
	public static final String API_UNAVAILABLE_MESSAGE = "The API Implementation is temporary unavailable";
	
	public static final String ENDPOINT_READ_TIMEOUT = "EndpointReadTimeout";
	public static final String ENDPOINT_READ_TIMEOUT_MESSAGE = "Read Timeout calling the API implementation";
	
	public static final String REQUEST_TIMED_OUT = "RequestReadTimeout";
	public static final String REQUEST_TIMED_OUT_MESSAGE = "Timeout reading the request payload";

	public static void verifyKo(HttpResponse response, String soapPrefixError, String error, int code, String errorMsg) throws Exception {
		
		assertEquals(500, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
		
		try {
			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2MessageParseResult parse = factory.createMessage(MessageType.SOAP_11, MessageRole.NONE, response.getContentType(), response.getContent());
			OpenSPCoop2Message msg = parse.getMessage_throwParseThrowable();
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			assertEquals(true ,soapMsg.isFault());
			SOAPFault soapFault = soapMsg.getSOAPBody().getFault();
			assertNotNull(soapFault);
			
			assertNotNull(soapFault.getFaultCodeAsQName());
			String faultCode = soapFault.getFaultCodeAsQName().getLocalPart();
			assertNotNull(faultCode);
			assertEquals(soapPrefixError+"."+error, faultCode);
			
			String faultString = soapFault.getFaultString();
			assertNotNull(faultString);
			assertEquals(errorMsg, faultString);
			
			String faultActor = soapFault.getFaultActor();
			assertNotNull(faultActor);
			assertEquals("http://govway.org/integration", faultActor);
						
			assertEquals(true ,ProblemUtilities.existsProblem(soapFault.getDetail(), getLoggerCore()));
			Node problemNode = ProblemUtilities.getProblem(soapFault.getDetail(), getLoggerCore());
			
			ProblemUtilities.verificaProblem(problemNode, 
					code, error, errorMsg, true, 
					getLoggerCore());
			
			assertEquals(error, response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			
			if(code==504) {
				assertNotNull(response.getHeaderFirstValue(HttpConstants.RETRY_AFTER));
			}

		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void verifyOk(HttpResponse response, int code) {
		
		assertEquals(code, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_SOAP_1_1, response.getContentType());
		
	}
}
