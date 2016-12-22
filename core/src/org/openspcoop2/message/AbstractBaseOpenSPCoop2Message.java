/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.w3c.dom.Node;

/**
 * AbstractBaseOpenSPCoop2Message
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseOpenSPCoop2Message implements org.openspcoop2.message.OpenSPCoop2Message {

	/* Trasporto */	
	public TransportRequestContext transportRequestContext;
	public TransportResponseContext transportResponseContext;	
	public String forcedResponseCode;
	public OpenSPCoop2MessageProperties forwardTransportHeader = new OpenSPCoop2MessageProperties();
	public OpenSPCoop2MessageProperties forwardUrlProperties = new OpenSPCoop2MessageProperties();
		
	/* Context */	
	public Map<String, Object> context = new Hashtable<String, Object>();
	
	/* ContentType */
	public Map<String, String> contentTypeParamaters = new Hashtable<String, String>();
	
	/* MessageType */
	public MessageType messageType;
	
	/* MessageRole */
	public MessageRole messageRole;
	
	/* Content Length */
	public long outgoingsize = -1;
	public long incomingsize = -1;
	public Long incomingSizeForced = null;
	
	/* Errors */
	public ParseException parseException;
	
	/* Protocol Plugin */
	public String protocolName;
	
	/* Stream */
	public NotifierInputStream notifierInputStream;
	

	
	/* Normalize to SAAJ */
	
	@Override
	public OpenSPCoop2Message normalizeToSaajImpl() throws MessageException{
		return this;
	}
	
	
	/* Copy Resources to another instance */
	
	@Override
	public void copyResourcesTo(OpenSPCoop2Message newInstance) throws MessageException{
		
		// Le seguenti risorse non vengono ricopiate poichè varieranno nella nuova versione
		// - contentTypeParamaters
		// - messageType
		// - outgoingsize
		// - notifierInputStream
		
		if(this.parseException!=null){
			throw new MessageException(this.getParseException().getSourceException());
		}
		
		if(newInstance instanceof AbstractBaseOpenSPCoop2Message){
			AbstractBaseOpenSPCoop2Message base = (AbstractBaseOpenSPCoop2Message) newInstance; 
			base.transportRequestContext = this.transportRequestContext;
			base.transportResponseContext = this.transportResponseContext;
			base.forcedResponseCode = this.forcedResponseCode;
			base.forwardTransportHeader = this.forwardTransportHeader;
			base.forwardUrlProperties = this.forwardUrlProperties;
			base.context = this.context;
			base.messageRole = this.messageRole;
			base.incomingsize = this.incomingsize;
			base.incomingSizeForced = this.incomingSizeForced;
			base.protocolName = this.protocolName;
		}
		else{
			// Viene riversato solo quello che è possibile riversare
			newInstance.setTransportRequestContext(this.transportRequestContext);
			newInstance.setTransportResponseContext(this.transportResponseContext);
			newInstance.setForcedResponseCode(this.forcedResponseCode);
			if(this.context.size()>0){
				Iterator<String> it = this.context.keySet().iterator();
				while (it.hasNext()) {
					String contextKey = (String) it.next();
					newInstance.addContextProperty(contextKey, this.context.get(contextKey));
				}
			}	
			newInstance.setMessageRole(this.messageRole);
			newInstance.setProtocolName(this.protocolName);
		}
	}
	
	
	
	/* Cast */
	
	@Override
	public OpenSPCoop2SoapMessage castAsSoap() throws MessageException{
		if(this instanceof OpenSPCoop2SoapMessage){
			return (OpenSPCoop2SoapMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in SOAP Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestMessage<?> castAsRest() throws MessageException{
		if(this instanceof OpenSPCoop2RestMessage){
			return (OpenSPCoop2RestMessage<?>) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestXmlMessage castAsRestXml() throws MessageException{
		if(this instanceof OpenSPCoop2RestXmlMessage){
			return (OpenSPCoop2RestXmlMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-Xml Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestJsonMessage castAsRestJson() throws MessageException{
		if(this instanceof OpenSPCoop2RestJsonMessage){
			return (OpenSPCoop2RestJsonMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-Json Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestBinaryMessage castAsRestBinary() throws MessageException{
		if(this instanceof OpenSPCoop2RestBinaryMessage){
			return (OpenSPCoop2RestBinaryMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-Binary Context");
		}
	}
	
	@Override
	public OpenSPCoop2RestMimeMultipartMessage castAsRestMimeMultipart() throws MessageException{
		if(this instanceof OpenSPCoop2RestMimeMultipartMessage){
			return (OpenSPCoop2RestMimeMultipartMessage) this;
		}
		else{
			throw new MessageException("Instance (type:"+this.getMessageType()+" class:"+this.getClass().getName()+") not usable in REST-MimeMultipart Context");
		}
	}
	
	
	

	/* Trasporto */
	
	@Override
	public void setTransportRequestContext(TransportRequestContext transportRequestContext){
		this.transportRequestContext = transportRequestContext;
	}
	@Override
	public TransportRequestContext getTransportRequestContext(){
		return this.transportRequestContext;
	}
	@Override
	public void setTransportResponseContext(TransportResponseContext transportResponseContext){
		this.transportResponseContext = transportResponseContext;
	}
	@Override
	public TransportResponseContext getTransportResponseContext(){
		return this.transportResponseContext;
	}
	@Override
	public void setForcedResponseCode(String code){
		this.forcedResponseCode = code;
	}
	@Override
	public String getForcedResponseCode(){
		return this.forcedResponseCode;
	}
	@Override
	public OpenSPCoop2MessageProperties getForwardTransportHeader(List<String> whiteListHeader) throws MessageException{
		return this.forwardTransportHeader;
	}
	@Override
	public OpenSPCoop2MessageProperties getForwardUrlProperties() throws MessageException{
		return this.forwardUrlProperties;
	}
	

	/* Context */
	
	@Override
	public void addContextProperty(String property, Object value){
		this.context.put(property, value);
	}
	@Override
	public Iterator<String> keysContextProperty(){
		return this.context.keySet().iterator();
	}
	@Override
	public Object getContextProperty(String property){
		return this.context.get(property);
	}
	@Override
	public Object removeContextProperty(String property){
		return this.context.remove(property);
	}
	
	
	/* ContentType */
	
	@Override
	public void addContentTypeParameter(String name,String value) throws MessageException{
		this.contentTypeParamaters.put(name, value);
	}
	
	@Override
	public void removeContentTypeParameter(String name) throws MessageException{
		this.contentTypeParamaters.remove(name);
	}
	
	
	/* MessageType */
	
	@Override
	public MessageType getMessageType(){
		return this.messageType;
	}
	
	@Override
	public void setMessageType(MessageType messageType){
		this.messageType = messageType;
	}
	
	
	/* MessageRole */
	
	@Override
	public MessageRole getMessageRole() {
		return this.messageRole;
	}
	
	@Override
	public void setMessageRole(MessageRole messageRole) {
		this.messageRole = messageRole;
	}
	
	
	/* ServiceBinding */
	
	@Override
	public ServiceBinding getServiceBinding(){
		if(this instanceof OpenSPCoop2SoapMessage){
			return ServiceBinding.SOAP;
		}
		else{
			return ServiceBinding.REST;
		}
	}
	
	
	
	/* Content Length */
	
	@Override
	public void updateIncomingMessageContentLength(){
		// NOP (Viene letta tutta nel costruttore)
	}
	
	@Override
	public void updateIncomingMessageContentLength(long incomingsize){
		this.incomingSizeForced = incomingsize;
	}
	
	@Override
	public long getIncomingMessageContentLength() {
		if(this.incomingSizeForced!=null){
			return this.incomingSizeForced;
		}else{
			return this.incomingsize;
		}
	}

	@Override
	public void updateOutgoingMessageContentLength(long outgoingsize){
		this.outgoingsize = outgoingsize;
	}
	
	@Override
	public long getOutgoingMessageContentLength() {
		return this.outgoingsize;
	}
	
	
	
	/* Node Utilities */
	
	@Override
	public String getAsString(Node ele, boolean consume){
		try {
			return XMLUtils.getInstance().toString(ele,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public byte[] getAsByte(Node ele, boolean consume){
		try {
			return XMLUtils.getInstance().toByteArray(ele,true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	
	
	/* Errors */
	
	@Override
	public ParseException getParseException(){
		return this.parseException;
	}

	@Override
	public void setParseException(ParseException e){
		this.parseException = e;
	}
	
	@Override
	public void setParseException(Throwable e){
		this.parseException = ParseExceptionUtils.buildParseException(e);
	}
	
	
	/* Protocol Plugin */
	
	@Override
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	@Override
	public String getProtocolName() {
		return this.protocolName;
	}
	
	
	/* Stream */
	
	@Override
	public void setNotifierInputStream(NotifierInputStream is) {
		this.notifierInputStream = is;
	}

	@Override
	public NotifierInputStream getNotifierInputStream() {
		return this.notifierInputStream;
	}
	
}
