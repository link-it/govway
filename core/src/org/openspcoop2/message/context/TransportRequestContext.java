/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for transport-request-context complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transport-request-context">
 * 		&lt;sequence>
 * 			&lt;element name="url-parameters" type="{http://www.openspcoop2.org/message/context}url-parameters" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="header-parameters" type="{http://www.openspcoop2.org/message/context}header-parameters" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="credentials" type="{http://www.openspcoop2.org/message/context}credentials" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="web-context" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="request-uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="request-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocol-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocol-web-context" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="function" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="function-parameters" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="interface-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transport-request-context", 
  propOrder = {
  	"urlParameters",
  	"headerParameters",
  	"credentials",
  	"webContext",
  	"requestUri",
  	"requestType",
  	"source",
  	"protocolName",
  	"protocolWebContext",
  	"function",
  	"functionParameters",
  	"interfaceName"
  }
)

@XmlRootElement(name = "transport-request-context")

public class TransportRequestContext extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TransportRequestContext() {
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

  public UrlParameters getUrlParameters() {
    return this.urlParameters;
  }

  public void setUrlParameters(UrlParameters urlParameters) {
    this.urlParameters = urlParameters;
  }

  public HeaderParameters getHeaderParameters() {
    return this.headerParameters;
  }

  public void setHeaderParameters(HeaderParameters headerParameters) {
    this.headerParameters = headerParameters;
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  public java.lang.String getWebContext() {
    return this.webContext;
  }

  public void setWebContext(java.lang.String webContext) {
    this.webContext = webContext;
  }

  public java.lang.String getRequestUri() {
    return this.requestUri;
  }

  public void setRequestUri(java.lang.String requestUri) {
    this.requestUri = requestUri;
  }

  public java.lang.String getRequestType() {
    return this.requestType;
  }

  public void setRequestType(java.lang.String requestType) {
    this.requestType = requestType;
  }

  public java.lang.String getSource() {
    return this.source;
  }

  public void setSource(java.lang.String source) {
    this.source = source;
  }

  public java.lang.String getProtocolName() {
    return this.protocolName;
  }

  public void setProtocolName(java.lang.String protocolName) {
    this.protocolName = protocolName;
  }

  public java.lang.String getProtocolWebContext() {
    return this.protocolWebContext;
  }

  public void setProtocolWebContext(java.lang.String protocolWebContext) {
    this.protocolWebContext = protocolWebContext;
  }

  public java.lang.String getFunction() {
    return this.function;
  }

  public void setFunction(java.lang.String function) {
    this.function = function;
  }

  public java.lang.String getFunctionParameters() {
    return this.functionParameters;
  }

  public void setFunctionParameters(java.lang.String functionParameters) {
    this.functionParameters = functionParameters;
  }

  public java.lang.String getInterfaceName() {
    return this.interfaceName;
  }

  public void setInterfaceName(java.lang.String interfaceName) {
    this.interfaceName = interfaceName;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="url-parameters",required=false,nillable=false)
  protected UrlParameters urlParameters;

  @XmlElement(name="header-parameters",required=false,nillable=false)
  protected HeaderParameters headerParameters;

  @XmlElement(name="credentials",required=false,nillable=false)
  protected Credentials credentials;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="web-context",required=false,nillable=false)
  protected java.lang.String webContext;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="request-uri",required=false,nillable=false)
  protected java.lang.String requestUri;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="request-type",required=false,nillable=false)
  protected java.lang.String requestType;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="source",required=false,nillable=false)
  protected java.lang.String source;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocol-name",required=false,nillable=false)
  protected java.lang.String protocolName;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="protocol-web-context",required=false,nillable=false)
  protected java.lang.String protocolWebContext;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="function",required=false,nillable=false)
  protected java.lang.String function;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="function-parameters",required=false,nillable=false)
  protected java.lang.String functionParameters;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="interface-name",required=false,nillable=false)
  protected java.lang.String interfaceName;

}
