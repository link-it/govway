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
package org.openspcoop2.message.context;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for message-context complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-context"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="transport-request-context" type="{http://www.openspcoop2.org/message/context}transport-request-context" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="transport-response-context" type="{http://www.openspcoop2.org/message/context}transport-response-context" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="forced-response" type="{http://www.openspcoop2.org/message/context}forced-response" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="serialized-context" type="{http://www.openspcoop2.org/message/context}serialized-context" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-type-parameters" type="{http://www.openspcoop2.org/message/context}content-type-parameters" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="content-length" type="{http://www.openspcoop2.org/message/context}content-length" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="soap" type="{http://www.openspcoop2.org/message/context}soap" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="message-type" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="message-role" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="protocol" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message-context", 
  propOrder = {
  	"transportRequestContext",
  	"transportResponseContext",
  	"forcedResponse",
  	"serializedContext",
  	"contentTypeParameters",
  	"contentLength",
  	"soap"
  }
)

@XmlRootElement(name = "message-context")

public class MessageContext extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public MessageContext() {
    super();
  }

  public TransportRequestContext getTransportRequestContext() {
    return this.transportRequestContext;
  }

  public void setTransportRequestContext(TransportRequestContext transportRequestContext) {
    this.transportRequestContext = transportRequestContext;
  }

  public TransportResponseContext getTransportResponseContext() {
    return this.transportResponseContext;
  }

  public void setTransportResponseContext(TransportResponseContext transportResponseContext) {
    this.transportResponseContext = transportResponseContext;
  }

  public ForcedResponse getForcedResponse() {
    return this.forcedResponse;
  }

  public void setForcedResponse(ForcedResponse forcedResponse) {
    this.forcedResponse = forcedResponse;
  }

  public SerializedContext getSerializedContext() {
    return this.serializedContext;
  }

  public void setSerializedContext(SerializedContext serializedContext) {
    this.serializedContext = serializedContext;
  }

  public ContentTypeParameters getContentTypeParameters() {
    return this.contentTypeParameters;
  }

  public void setContentTypeParameters(ContentTypeParameters contentTypeParameters) {
    this.contentTypeParameters = contentTypeParameters;
  }

  public ContentLength getContentLength() {
    return this.contentLength;
  }

  public void setContentLength(ContentLength contentLength) {
    this.contentLength = contentLength;
  }

  public Soap getSoap() {
    return this.soap;
  }

  public void setSoap(Soap soap) {
    this.soap = soap;
  }

  public java.lang.String getMessageType() {
    return this.messageType;
  }

  public void setMessageType(java.lang.String messageType) {
    this.messageType = messageType;
  }

  public java.lang.String getMessageRole() {
    return this.messageRole;
  }

  public void setMessageRole(java.lang.String messageRole) {
    this.messageRole = messageRole;
  }

  public java.lang.String getProtocol() {
    return this.protocol;
  }

  public void setProtocol(java.lang.String protocol) {
    this.protocol = protocol;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.message.context.model.MessageContextModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.message.context.MessageContext.modelStaticInstance==null){
  			org.openspcoop2.message.context.MessageContext.modelStaticInstance = new org.openspcoop2.message.context.model.MessageContextModel();
	  }
  }
  public static org.openspcoop2.message.context.model.MessageContextModel model(){
	  if(org.openspcoop2.message.context.MessageContext.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.message.context.MessageContext.modelStaticInstance;
  }


  @XmlElement(name="transport-request-context",required=false,nillable=false)
  protected TransportRequestContext transportRequestContext;

  @XmlElement(name="transport-response-context",required=false,nillable=false)
  protected TransportResponseContext transportResponseContext;

  @XmlElement(name="forced-response",required=false,nillable=false)
  protected ForcedResponse forcedResponse;

  @XmlElement(name="serialized-context",required=false,nillable=false)
  protected SerializedContext serializedContext;

  @XmlElement(name="content-type-parameters",required=false,nillable=false)
  protected ContentTypeParameters contentTypeParameters;

  @XmlElement(name="content-length",required=false,nillable=false)
  protected ContentLength contentLength;

  @XmlElement(name="soap",required=false,nillable=false)
  protected Soap soap;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="message-type",required=true)
  protected java.lang.String messageType;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="message-role",required=true)
  protected java.lang.String messageRole;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="protocol",required=true)
  protected java.lang.String protocol;

}
