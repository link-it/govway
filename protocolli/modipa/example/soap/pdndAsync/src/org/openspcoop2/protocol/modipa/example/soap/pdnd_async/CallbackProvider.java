/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.example.soap.pdnd_async;

import jakarta.annotation.Resource;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.Provider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.ServiceMode;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.WebServiceProvider;
import jakarta.xml.ws.soap.SOAPBinding;

import org.w3c.dom.Element;

/**
 * Implementazione dell'API di callback SOAP di esempio (versioni compatta ed estesa)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceProvider(serviceName = "PDNDAsyncCallbackService", portName = "PDNDAsyncCallbackPort", targetNamespace = SoapUtils.NS_CALLBACK)
@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP11HTTP_BINDING)
public class CallbackProvider implements Provider<SOAPMessage> {

	@Resource
	private WebServiceContext wsContext;
	
	private final String headerConversationId;
	
	public CallbackProvider(String headerConversationId) {
		this.headerConversationId = headerConversationId;
	}
	
	@Override
	public SOAPMessage invoke(SOAPMessage request) {
		try {
			Element operation = SoapUtils.getOperation(request);
			SOAPMessage response = SoapUtils.newResponse();
			switch (operation.getLocalName()) {
			case "callbackInvocation":
				String id = SoapUtils.getHttpHeader(this.wsContext.getMessageContext(), this.headerConversationId);
				System.out.println("[callback_invocation] Notifica ricevuta per l'interazione '"+id+"' (status: "+SoapUtils.getChildValue(operation, "status")+
						", entità: "+SoapUtils.getChildValue(operation, "entities")+")");
				System.out.println("[callback_invocation] I dati possono essere ottenuti con: ant runClient -Doperation=getResource -DconversationId="+id);
				SoapUtils.addChild(SoapUtils.addResponseElement(response, SoapUtils.NS_CALLBACK, "callbackInvocationResponse"), SoapUtils.NS_CALLBACK, "outcome", "ACK");
				return response;
			case "notifyError":
				System.out.println("[errors] Segnalazione ricevuta: "+SoapUtils.getChildValue(operation, "message"));
				SoapUtils.addChild(SoapUtils.addResponseElement(response, SoapUtils.NS_CALLBACK, "notifyErrorResponse"), SoapUtils.NS_CALLBACK, "outcome", "ACK");
				return response;
			case "health":
				SoapUtils.addChild(SoapUtils.addResponseElement(response, SoapUtils.NS_CALLBACK, "healthResponse"), SoapUtils.NS_CALLBACK, "status", "UP");
				return response;
			default:
				return SoapUtils.newFault("Client", "Operation '"+operation.getLocalName()+"' unknown");
			}
		}catch(Exception e) {
			return SoapUtils.newFault("Server", e.getMessage());
		}
	}
}
