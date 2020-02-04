/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.response_caching;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.w3c.dom.Node;

/**     
 * Test
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	private static final String SOAP_ENVELOPE_RISPOSTA = 
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
			"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header></soapenv:Header>";
	private static final String SOAP_ENVELOPE_RISPOSTA_END = 
		"<soapenv:Body><prova>test</prova></soapenv:Body></soapenv:Envelope>";
	
	public static void main(String[] args) throws Exception {
				
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromValues("gw", "serv", "gw", "RegioneToscana", 1));
		requestInfo.getIdServizio().setAzione("az");
		
		URLProtocolContext protocolContext = new URLProtocolContext();
		requestInfo.setProtocolContext(protocolContext);
		protocolContext.setInterfaceName("nomePortaDelegataXXXX");
		protocolContext.setFunction("PD");
		protocolContext.setRequestURI("http://govway/in/GW_serv/RGT");
		protocolContext.setParametersFormBased(new HashMap<String, String>());
		protocolContext.getParametersFormBased().put("p1", "v1");
		protocolContext.getParametersFormBased().put("p2", "v2");
		
		protocolContext.setParametersTrasporto(new HashMap<String, String>());
		protocolContext.getParametersTrasporto().put("h1", "v1");
		protocolContext.getParametersTrasporto().put("h2", "v2");
		protocolContext.getParametersTrasporto().put("h3", "v3");
		protocolContext.getParametersTrasporto().put("Content-Type", "text/xml");
		
		String messaggio = SOAP_ENVELOPE_RISPOSTA + SOAP_ENVELOPE_RISPOSTA_END;
		byte [] messaggioArray = messaggio.getBytes();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(messaggioArray);
		OpenSPCoop2MessageFactory messageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		OpenSPCoop2MessageParseResult pr = messageFactory.createMessage(MessageType.SOAP_11,protocolContext,
				bin,null);
		OpenSPCoop2Message msg = pr.getMessage_throwParseException();
		msg.addContextProperty("CONT1", "V1");
		msg.addContextProperty("CONT2", "V2");
		
		HashGenerator generator = new HashGenerator("MD5");
		//generator = new HashGenerator("SHA-256");
		
		ResponseCachingConfigurazione responseCachingConfig = new ResponseCachingConfigurazione();
		responseCachingConfig.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
		
		
		System.out.println("TEST1: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		System.out.println("TEST2: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		protocolContext.getParametersFormBased().remove("p1");
		System.out.println("TEST3 ko: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		protocolContext.getParametersFormBased().put("p1", "v1");
		System.out.println("TEST3 ok: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		protocolContext.getParametersTrasporto().remove("h1");
		System.out.println("TEST4 ko: "+generator.buildKeyCache(msg, requestInfo, responseCachingConfig));
		
		protocolContext.getParametersTrasporto().put("h1", "v1");
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
