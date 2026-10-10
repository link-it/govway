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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Resource;
import jakarta.xml.soap.SOAPElement;
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
 * Implementazione dell'e-service SOAP di esempio (versioni compatta ed estesa: la versione compatta utilizza un sottoinsieme delle operazioni)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceProvider(serviceName = "PDNDAsyncEServiceService", portName = "PDNDAsyncEServicePort", targetNamespace = SoapUtils.NS_ESERVICE)
@ServiceMode(value = Service.Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP11HTTP_BINDING)
public class EServiceProvider implements Provider<SOAPMessage> {

	private static final String OUTCOME = "outcome";
	private static final String CLIENT = "Client";
	
	@Resource
	private WebServiceContext wsContext;
	
	private final String headerConversationId;
	private final String headerUrlCallback;
	private final CallbackNotifier callbackNotifier;
	private final Map<String, String> catalog = new ConcurrentHashMap<>();
	
	public EServiceProvider(String headerConversationId, String headerUrlCallback, CallbackNotifier callbackNotifier) {
		this.headerConversationId = headerConversationId;
		this.headerUrlCallback = headerUrlCallback;
		this.callbackNotifier = callbackNotifier;
		this.catalog.put("A01", "Elemento di esempio A01");
		this.catalog.put("B02", "Elemento di esempio B02");
	}
	
	@Override
	public SOAPMessage invoke(SOAPMessage request) {
		try {
			Element operation = SoapUtils.getOperation(request);
			switch (operation.getLocalName()) {
			case "startInteraction":
				return startInteraction(operation);
			case "getResource":
				return getResource(operation);
			case "confirmation":
				return confirmation();
			case "getCatalogItem":
				return getCatalogItem(operation);
			case "updateCatalogItem":
				this.catalog.put(SoapUtils.getChildValue(operation, "code"), String.valueOf(SoapUtils.getChildValue(operation, "description")));
				return outcome("updateCatalogItemResponse", "UPDATED");
			case "health":
				SOAPMessage response = SoapUtils.newResponse();
				SoapUtils.addChild(SoapUtils.addResponseElement(response, SoapUtils.NS_ESERVICE, "healthResponse"), SoapUtils.NS_ESERVICE, "status", "UP");
				return response;
			default:
				return SoapUtils.newFault(CLIENT, "Operation '"+operation.getLocalName()+"' unknown");
			}
		}catch(IllegalArgumentException e) {
			return SoapUtils.newFault(CLIENT, e.getMessage());
		}catch(Exception e) {
			return SoapUtils.newFault("Server", e.getMessage());
		}
	}
	
	private SOAPMessage startInteraction(Element operation) throws Exception {
		String id = getConversationId("start_interaction");
		String urlCallback = SoapUtils.getHttpHeader(this.wsContext.getMessageContext(), this.headerUrlCallback);
		String subject = SoapUtils.getChildValue(operation, "subject");
		if(subject==null) {
			throw new IllegalArgumentException("Element 'subject' is required");
		}
		int entities = SoapUtils.getChildIntValue(operation, "entities", 10);
		InteractionStore.Interaction interaction = new InteractionStore.Interaction(id, subject, entities, urlCallback);
		InteractionStore.put(interaction);
		System.out.println("[start_interaction] Interazione '"+id+"' avviata (subject: '"+subject+"', entità: "+entities+", urlCallback: '"+urlCallback+"')");
		if(this.callbackNotifier!=null) {
			this.callbackNotifier.schedule(interaction);
		}
		return outcome("startInteractionResponse", "ACCEPTED");
	}
	
	private SOAPMessage getResource(Element operation) throws Exception {
		InteractionStore.Interaction interaction = getInteraction("get_resource");
		int offset = SoapUtils.getChildIntValue(operation, "offset", 0);
		int limit = SoapUtils.getChildIntValue(operation, "limit", 10);
		SOAPMessage response = SoapUtils.newResponse();
		SOAPElement result = SoapUtils.addResponseElement(response, SoapUtils.NS_ESERVICE, "getResourceResponse");
		SoapUtils.addChild(result, SoapUtils.NS_ESERVICE, "total", String.valueOf(interaction.getEntities()));
		SoapUtils.addChild(result, SoapUtils.NS_ESERVICE, "offset", String.valueOf(offset));
		int count = 0;
		for (int i = offset; i < Math.min(offset+limit, interaction.getEntities()); i++) {
			SOAPElement entity = result.addChildElement(new javax.xml.namespace.QName(SoapUtils.NS_ESERVICE, "entity", "tns"));
			SoapUtils.addChild(entity, SoapUtils.NS_ESERVICE, "id", String.valueOf(i+1));
			SoapUtils.addChild(entity, SoapUtils.NS_ESERVICE, "value", interaction.getSubject()+" #"+(i+1));
			count++;
		}
		System.out.println("[get_resource] Interazione '"+interaction.getId()+"': restituite "+count+" entità (offset "+offset+")");
		return response;
	}
	
	private SOAPMessage confirmation() throws Exception {
		InteractionStore.Interaction interaction = getInteraction("confirmation");
		interaction.setConfirmed(true);
		System.out.println("[confirmation] Interazione '"+interaction.getId()+"' confermata");
		return outcome("confirmationResponse", "CONFIRMED");
	}
	
	private SOAPMessage getCatalogItem(Element operation) throws Exception {
		String code = SoapUtils.getChildValue(operation, "code");
		String description = code!=null ? this.catalog.get(code) : null;
		if(description==null) {
			throw new IllegalArgumentException("Catalog item '"+code+"' not found");
		}
		SOAPMessage response = SoapUtils.newResponse();
		SOAPElement item = SoapUtils.addResponseElement(response, SoapUtils.NS_ESERVICE, "getCatalogItemResponse");
		SoapUtils.addChild(item, SoapUtils.NS_ESERVICE, "code", code);
		SoapUtils.addChild(item, SoapUtils.NS_ESERVICE, "description", description);
		return response;
	}
	
	private static SOAPMessage outcome(String element, String value) throws Exception {
		SOAPMessage response = SoapUtils.newResponse();
		SoapUtils.addChild(SoapUtils.addResponseElement(response, SoapUtils.NS_ESERVICE, element), SoapUtils.NS_ESERVICE, OUTCOME, value);
		return response;
	}
	
	private String getConversationId(String fase) {
		String id = SoapUtils.getHttpHeader(this.wsContext.getMessageContext(), this.headerConversationId);
		if(id==null || "".equals(id)) {
			throw new IllegalArgumentException("Header '"+this.headerConversationId+"' (interactionId) not provided for phase '"+fase+"'");
		}
		return id;
	}
	private InteractionStore.Interaction getInteraction(String fase) {
		String id = getConversationId(fase);
		InteractionStore.Interaction interaction = InteractionStore.get(id);
		if(interaction==null) {
			throw new IllegalArgumentException("Interaction '"+id+"' not found");
		}
		return interaction;
	}
}
