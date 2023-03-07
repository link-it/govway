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

package org.openspcoop2.pdd.core.response_caching.test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.response_caching.HashGenerator;
import org.openspcoop2.pdd.core.response_caching.ResponseCached;
import org.openspcoop2.protocol.engine.URLProtocolContextImpl;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**     
 * Test
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponseCachingTest {

	private static final String SOAP_ENVELOPE_RISPOSTA = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
			"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header></soapenv:Header>";
	private static final String SOAP_ENVELOPE_RISPOSTA_END = 
		"<soapenv:Body><prova>test</prova></soapenv:Body></soapenv:Envelope>";
	
	public static void main(String [] args) throws Exception{
		test();
	}
	public static void test() throws Exception{
				
		Logger log = LoggerWrapperFactory.getLogger(ResponseCachingTest.class);
		
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromValues("gw", "serv", "gw", "RegioneToscana", 1));
		requestInfo.getIdServizio().setAzione("az");
		
		URLProtocolContext protocolContext = new URLProtocolContextImpl(log);
		requestInfo.setProtocolContext(protocolContext);
		protocolContext.setInterfaceName("nomePortaDelegataXXXX");
		protocolContext.setFunction("PD");
		protocolContext.setRequestURI("http://govway/in/GW_serv/RGT");
		
		protocolContext.setParameters(new HashMap<String, List<String>>());
		TransportUtils.addParameter(protocolContext.getParameters(),"p1", "v1");
		TransportUtils.addParameter(protocolContext.getParameters(),"p2", "v2a");
		TransportUtils.addParameter(protocolContext.getParameters(),"p2", "v2b");
		
		protocolContext.setHeaders(new HashMap<String, List<String>>());
		TransportUtils.addHeader(protocolContext.getHeaders(),"h1", "v1");
		TransportUtils.addHeader(protocolContext.getHeaders(),"h2", "v2a");
		TransportUtils.addHeader(protocolContext.getHeaders(),"h2", "v2b");
		TransportUtils.addHeader(protocolContext.getHeaders(),"h3", "v3");
		TransportUtils.addHeader(protocolContext.getHeaders(),"Content-Type", "text/xml");
		
		String messaggio = SOAP_ENVELOPE_RISPOSTA + SOAP_ENVELOPE_RISPOSTA_END;
		byte [] messaggioArray = messaggio.getBytes();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(messaggioArray);
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		OpenSPCoop2MessageParseResult pr = messageFactory.createMessage(MessageType.SOAP_11,protocolContext,
				bin,null);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		MapKey<String> CONT1 = org.openspcoop2.utils.Map.newMapKey("CONT1");
		MapKey<String> CONT2 = org.openspcoop2.utils.Map.newMapKey("CONT2");
		msg.addContextProperty(CONT1, "V1");
		msg.addContextProperty(CONT2, "V2");
		
		HashGenerator generator = new HashGenerator("MD5");
		//generator = new HashGenerator("SHA-256");
		
		ResponseCachingConfigurazione responseCachingConfig = new ResponseCachingConfigurazione();
		responseCachingConfig.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
		List<String> headers = new ArrayList<String>();
		headers.addAll(protocolContext.getHeaders().keySet());
		responseCachingConfig.getHashGenerator().setHeaders(StatoFunzionalita.ABILITATO);
		responseCachingConfig.getHashGenerator().setHeaderList(headers);
		
		
		System.out.println("TEST1: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		System.out.println("TEST2: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		protocolContext.getParameters().remove("p1");
		System.out.println("TEST3 ko: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		TransportUtils.addParameter(protocolContext.getParameters(),"p1", "v1");
		System.out.println("TEST3 ok: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		protocolContext.getHeaders().remove("h1");
		System.out.println("TEST4 ko: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		TransportUtils.addHeader(protocolContext.getHeaders(),"h1", "v1");
		System.out.println("TEST4 ok: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		Node n = msg.castAsSoap().getSOAPBody().addChildElement("empty");
		System.out.println("TEST5 ko: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		msg.castAsSoap().getSOAPBody().removeChild(n);
		System.out.println("TEST5 ok: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		requestInfo.getIdServizio().setAzione("BOBO");
		System.out.println("TEST6 ko: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		requestInfo.getIdServizio().setAzione("az");
		System.out.println("TEST6 ok: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		
		
		
		int seconds = 300;
		ResponseCached rCached = ResponseCached.toResponseCached(msg,seconds);
		AttachmentsProcessingMode attachmentProcessingMode = AttachmentsProcessingMode.getMemoryCacheProcessingMode();
		OpenSPCoop2Message msgRebuild = rCached.toOpenSPCoop2Message(messageFactory, attachmentProcessingMode, "GovWay-CacheKey");
		System.out.println("TESTRebuild ok: "+generator.buildKeyCache(msgRebuild, requestInfo, responseCachingConfig));
		
		System.out.println("TEST print: "+rCached.print());
	}

}
