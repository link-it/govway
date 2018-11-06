/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.IntegrationErrorProblemType;
import java.io.Serializable;


/** <p>Java class for IntegrationErrorCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationErrorCollection">
 * 		&lt;sequence>
 * 			&lt;element name="rfc7807" type="{http://www.openspcoop2.org/protocol/manifest}RFC7807" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="authentication" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="authorization" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="notFound" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="badRequest" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tooManyRequests" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="internalError" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="serviceUnavailable" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationError" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/protocol/manifest}DefaultIntegrationError" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="problemType" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorProblemType" use="required"/>
 * 		&lt;attribute name="useInternalFault" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationErrorCollection", 
  propOrder = {
  	"rfc7807",
  	"authentication",
  	"authorization",
  	"notFound",
  	"badRequest",
  	"tooManyRequests",
  	"internalError",
  	"serviceUnavailable",
  	"_default"
  }
)

@XmlRootElement(name = "IntegrationErrorCollection")

public class IntegrationErrorCollection extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationErrorCollection() {
  }

  public RFC7807 getRfc7807() {
    return this.rfc7807;
  }

  public void setRfc7807(RFC7807 rfc7807) {
    this.rfc7807 = rfc7807;
  }

  public IntegrationError getAuthentication() {
    return this.authentication;
  }

  public void setAuthentication(IntegrationError authentication) {
    this.authentication = authentication;
  }

  public IntegrationError getAuthorization() {
    return this.authorization;
  }

  public void setAuthorization(IntegrationError authorization) {
    this.authorization = authorization;
  }

  public IntegrationError getNotFound() {
    return this.notFound;
  }

  public void setNotFound(IntegrationError notFound) {
    this.notFound = notFound;
  }

  public IntegrationError getBadRequest() {
    return this.badRequest;
  }

  public void setBadRequest(IntegrationError badRequest) {
    this.badRequest = badRequest;
  }

  public IntegrationError getTooManyRequests() {
    return this.tooManyRequests;
  }

  public void setTooManyRequests(IntegrationError tooManyRequests) {
    this.tooManyRequests = tooManyRequests;
  }

  public IntegrationError getInternalError() {
    return this.internalError;
  }

  public void setInternalError(IntegrationError internalError) {
    this.internalError = internalError;
  }

  public IntegrationError getServiceUnavailable() {
    return this.serviceUnavailable;
  }

  public void setServiceUnavailable(IntegrationError serviceUnavailable) {
    this.serviceUnavailable = serviceUnavailable;
  }

  public DefaultIntegrationError getDefault() {
    return this._default;
  }

  public void setDefault(DefaultIntegrationError _default) {
    this._default = _default;
  }

  public void set_value_problemType(String value) {
    this.problemType = (IntegrationErrorProblemType) IntegrationErrorProblemType.toEnumConstantFromString(value);
  }

  public String get_value_problemType() {
    if(this.problemType == null){
    	return null;
    }else{
    	return this.problemType.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.IntegrationErrorProblemType getProblemType() {
    return this.problemType;
  }

  public void setProblemType(org.openspcoop2.protocol.manifest.constants.IntegrationErrorProblemType problemType) {
    this.problemType = problemType;
  }

  public boolean isUseInternalFault() {
    return this.useInternalFault;
  }

  public boolean getUseInternalFault() {
    return this.useInternalFault;
  }

  public void setUseInternalFault(boolean useInternalFault) {
    this.useInternalFault = useInternalFault;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="rfc7807",required=false,nillable=false)
  protected RFC7807 rfc7807;

  @XmlElement(name="authentication",required=false,nillable=false)
  protected IntegrationError authentication;

  @XmlElement(name="authorization",required=false,nillable=false)
  protected IntegrationError authorization;

  @XmlElement(name="notFound",required=false,nillable=false)
  protected IntegrationError notFound;

  @XmlElement(name="badRequest",required=false,nillable=false)
  protected IntegrationError badRequest;

  @XmlElement(name="tooManyRequests",required=false,nillable=false)
  protected IntegrationError tooManyRequests;

  @XmlElement(name="internalError",required=false,nillable=false)
  protected IntegrationError internalError;

  @XmlElement(name="serviceUnavailable",required=false,nillable=false)
  protected IntegrationError serviceUnavailable;

  @XmlElement(name="default",required=true,nillable=false)
  protected DefaultIntegrationError _default;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_problemType;

  @XmlAttribute(name="problemType",required=true)
  protected IntegrationErrorProblemType problemType;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="useInternalFault",required=false)
  protected boolean useInternalFault = false;

}
