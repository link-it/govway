/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.message;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.context.MessageContext;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.w3c.dom.Node;

/**
 * Interfaccia del messaggio OpenSPCoop
 * 
 *                        OpenSPCoop2Message    (interface)
 *                          |                         |
 *                          |                  OpenSPCoop2SoapMessage (interface)
 *	  	                    |                                |                |
 *    	      AbstractBaseOpenSPCoop2Message                 |         AbstractBaseOpenSPCoop2SoapMessage (viene implementata anche da Axiom)
 *                          |                                |                            |            
 *     AbstractBaseOpenSPCoop2MessageDynamicContent<T>       |               AbstractOpenSPCoop2Message_saaj_impl
 *        |                                        |         |                                    |
 * AbstractBaseOpenSPCoop2RestMessage<T>     AbstractOpenSPCoop2Message_soap_impl        OpenSPCoop2Message_saaj_1[1,2]_impl
 *        |                                                  |                              |
 * ...implREST xml,json,bin...                OpenSPCoop2Message_soap1[1,2]_impl -- wrap -- |
 * 
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface OpenSPCoop2Message {
	
	/* Message Factory */
	
	public OpenSPCoop2MessageFactory getFactory();
	
	
	/* Normalize to SAAJ */
	
	public OpenSPCoop2Message normalizeToSaajImpl() throws MessageException;
	
	
	/* Copy Resources to another instance */
	
	public void copyResourcesTo(OpenSPCoop2Message newInstance) throws MessageException;
	public void copyResourcesTo(OpenSPCoop2Message newInstance, boolean skipTransportInfo) throws MessageException;
	public MessageContext serializeResourcesTo() throws MessageException;
	public void serializeResourcesTo(OutputStream os) throws MessageException;
	public void readResourcesFrom(MessageContext messageContext) throws MessageException;
	public void readResourcesFrom(InputStream is) throws MessageException;
	
	
	/* Cast */
	
	public OpenSPCoop2SoapMessage castAsSoap() throws MessageException;	
	public OpenSPCoop2RestMessage<?> castAsRest() throws MessageException;	
	public OpenSPCoop2RestXmlMessage castAsRestXml() throws MessageException;	
	public OpenSPCoop2RestJsonMessage castAsRestJson() throws MessageException;	
	public OpenSPCoop2RestBinaryMessage castAsRestBinary() throws MessageException;	
	public OpenSPCoop2RestMimeMultipartMessage castAsRestMimeMultipart() throws MessageException;	
	
	
	/* Trasporto */
	
	public void setTransportRequestContext(TransportRequestContext transportRequestContext);
	public TransportRequestContext getTransportRequestContext();
	public void setTransportResponseContext(TransportResponseContext transportResponseContext);
	public TransportResponseContext getTransportResponseContext();
	public void forceTransportHeader(String name, String value);
	public void forceTransportHeader(String name, List<String> value);
	public void forceUrlProperty(String name, String value);
	public void forceUrlProperty(String name, List<String> value);
	public OpenSPCoop2MessageProperties getForwardTransportHeader(ForwardConfig forwardConfig) throws MessageException;
	public OpenSPCoop2MessageProperties getForwardUrlProperties(ForwardConfig forwardConfig) throws MessageException;
	
		
	/* Forced Response */
	
	public void setForcedResponseCode(String code);
	public String getForcedResponseCode();
	public void forceEmptyResponse();
	public boolean isForcedEmptyResponse();
	public void forceResponse(ForcedResponseMessage msg);
	public ForcedResponseMessage getForcedResponse();
	
	
	/* Context */
	
	public void addContextProperty(String property, Object value);
	public Iterator<String> keysContextProperty();
	public Object getContextProperty(String property);
	public Object removeContextProperty(String property);
	
	public void setTransactionId(String transactionId);
	public String getTransactionId();
		
		
	/* ContentType */
	
	public void addContentTypeParameter(String name,String value) throws MessageException;	
	public void removeContentTypeParameter(String name) throws MessageException;	
	public void updateContentType() throws MessageException;	
	public void setContentType(String type) throws MessageException;
	public String getContentType() throws MessageException;

	
	/* MessageType */
	
	public MessageType getMessageType();
	public void setMessageType(MessageType messageType);
	
	
	/* MessageRole */
	
	public MessageRole getMessageRole();
	public void setMessageRole(MessageRole messageRole);
	public boolean isFault() throws MessageException;
	
	
	/* ServiceBinding */
	
	public ServiceBinding getServiceBinding();
		
	
	/* WriteTo e Save */
	
	public boolean isContentBuilded();
	
	public void writeTo(OutputStream os, boolean consume) throws MessageException;
	
	public void saveChanges() throws MessageException;
	public boolean saveRequired();
	
		
	/* Content Length */
	
	public void updateIncomingMessageContentLength();
	public void updateIncomingMessageContentLength(long incomingsize);
	public long getIncomingMessageContentLength();	
	public void updateOutgoingMessageContentLength(long outgoingsize);
	public long getOutgoingMessageContentLength();

		
	/* Node Utilities */
	
	public String getAsString(Node ele, boolean consume);
	public byte[] getAsByte(Node ele, boolean consume);
	public void addNamespaceXSITypeIfNotExists(Node element, Node root) throws MessageException;
	

	/* Errors */
	
	public ParseException getParseException();
	public void setParseException(Throwable eParsing);
	public void setParseException(ParseException eParsing);
	
	
	/* Protocol Plugin */
	
	public void setProtocolName(String protocolName);
	public String getProtocolName();
	

	/* Stream */
	
	public void setNotifierInputStream(NotifierInputStream is);	
	public NotifierInputStream getNotifierInputStream();

	
}
