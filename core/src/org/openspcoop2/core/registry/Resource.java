/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.MessageType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for resource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resource">
 * 		&lt;sequence>
 * 			&lt;element name="request" type="{http://www.openspcoop2.org/core/registry}resource-request" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="response" type="{http://www.openspcoop2.org/core/registry}resource-response" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/registry}protocol-property" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id-accordo" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="method" type="{http://www.openspcoop2.org/core/registry}HttpMethod" use="optional"/>
 * 		&lt;attribute name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/>
 * 		&lt;attribute name="request-message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/>
 * 		&lt;attribute name="response-message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resource", 
  propOrder = {
  	"request",
  	"response",
  	"protocolProperty"
  }
)

@XmlRootElement(name = "resource")

public class Resource extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Resource() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public ResourceRequest getRequest() {
    return this.request;
  }

  public void setRequest(ResourceRequest request) {
    this.request = request;
  }

  public void addResponse(ResourceResponse response) {
    this.response.add(response);
  }

  public ResourceResponse getResponse(int index) {
    return this.response.get( index );
  }

  public ResourceResponse removeResponse(int index) {
    return this.response.remove( index );
  }

  public List<ResourceResponse> getResponseList() {
    return this.response;
  }

  public void setResponseList(List<ResourceResponse> response) {
    this.response=response;
  }

  public int sizeResponseList() {
    return this.response.size();
  }

  public void addProtocolProperty(ProtocolProperty protocolProperty) {
    this.protocolProperty.add(protocolProperty);
  }

  public ProtocolProperty getProtocolProperty(int index) {
    return this.protocolProperty.get( index );
  }

  public ProtocolProperty removeProtocolProperty(int index) {
    return this.protocolProperty.remove( index );
  }

  public List<ProtocolProperty> getProtocolPropertyList() {
    return this.protocolProperty;
  }

  public void setProtocolPropertyList(List<ProtocolProperty> protocolProperty) {
    this.protocolProperty=protocolProperty;
  }

  public int sizeProtocolPropertyList() {
    return this.protocolProperty.size();
  }

  public java.lang.Long getIdAccordo() {
    return this.idAccordo;
  }

  public void setIdAccordo(java.lang.Long idAccordo) {
    this.idAccordo = idAccordo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getPath() {
    return this.path;
  }

  public void setPath(java.lang.String path) {
    this.path = path;
  }

  public void set_value_method(String value) {
    this.method = (HttpMethod) HttpMethod.toEnumConstantFromString(value);
  }

  public String get_value_method() {
    if(this.method == null){
    	return null;
    }else{
    	return this.method.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.HttpMethod getMethod() {
    return this.method;
  }

  public void setMethod(org.openspcoop2.core.registry.constants.HttpMethod method) {
    this.method = method;
  }

  public void set_value_messageType(String value) {
    this.messageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String get_value_messageType() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.core.registry.constants.MessageType messageType) {
    this.messageType = messageType;
  }

  public void set_value_requestMessageType(String value) {
    this.requestMessageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String get_value_requestMessageType() {
    if(this.requestMessageType == null){
    	return null;
    }else{
    	return this.requestMessageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getRequestMessageType() {
    return this.requestMessageType;
  }

  public void setRequestMessageType(org.openspcoop2.core.registry.constants.MessageType requestMessageType) {
    this.requestMessageType = requestMessageType;
  }

  public void set_value_responseMessageType(String value) {
    this.responseMessageType = (MessageType) MessageType.toEnumConstantFromString(value);
  }

  public String get_value_responseMessageType() {
    if(this.responseMessageType == null){
    	return null;
    }else{
    	return this.responseMessageType.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.MessageType getResponseMessageType() {
    return this.responseMessageType;
  }

  public void setResponseMessageType(org.openspcoop2.core.registry.constants.MessageType responseMessageType) {
    this.responseMessageType = responseMessageType;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="request",required=false,nillable=false)
  protected ResourceRequest request;

  @XmlElement(name="response",required=true,nillable=false)
  protected List<ResourceResponse> response = new ArrayList<ResourceResponse>();

  /**
   * @deprecated Use method getResponseList
   * @return List<ResourceResponse>
  */
  @Deprecated
  public List<ResourceResponse> getResponse() {
  	return this.response;
  }

  /**
   * @deprecated Use method setResponseList
   * @param response List<ResourceResponse>
  */
  @Deprecated
  public void setResponse(List<ResourceResponse> response) {
  	this.response=response;
  }

  /**
   * @deprecated Use method sizeResponseList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeResponse() {
  	return this.response.size();
  }

  @XmlElement(name="protocol-property",required=true,nillable=false)
  protected List<ProtocolProperty> protocolProperty = new ArrayList<ProtocolProperty>();

  /**
   * @deprecated Use method getProtocolPropertyList
   * @return List<ProtocolProperty>
  */
  @Deprecated
  public List<ProtocolProperty> getProtocolProperty() {
  	return this.protocolProperty;
  }

  /**
   * @deprecated Use method setProtocolPropertyList
   * @param protocolProperty List<ProtocolProperty>
  */
  @Deprecated
  public void setProtocolProperty(List<ProtocolProperty> protocolProperty) {
  	this.protocolProperty=protocolProperty;
  }

  /**
   * @deprecated Use method sizeProtocolPropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProtocolProperty() {
  	return this.protocolProperty.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idAccordo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="path",required=false)
  protected java.lang.String path;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_method;

  @XmlAttribute(name="method",required=false)
  protected HttpMethod method;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_messageType;

  @XmlAttribute(name="message-type",required=false)
  protected MessageType messageType;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_requestMessageType;

  @XmlAttribute(name="request-message-type",required=false)
  protected MessageType requestMessageType;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_responseMessageType;

  @XmlAttribute(name="response-message-type",required=false)
  protected MessageType responseMessageType;

}
